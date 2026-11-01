package com.example.bananacreate.block.entity;

import com.example.bananacreate.item.TitanSyringeItem;
import com.example.bananacreate.item.TitanTransformation;
import com.example.bananacreate.registry.ModBlockEntities;
import com.example.bananacreate.registry.ModBlocks;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Esteira de Tita: gerador cinetico do Create.
 * Prende UM tita por vez (o mais proximo, num raio de {@link #CAPTURE_RANGE} blocos). O tita preso
 * fica sem IA e e conduzido em circulo em cima da esteira, empurrando-a: 32 RPM fixos.
 * Matar o tita ou quebrar a esteira solta/encerra a geracao.
 */
public class TitanTreadmillBE extends GeneratingKineticBlockEntity {
    public static final String KEY_BOUND = "bananacreate_treadmill_pos";
    private static final double CAPTURE_RANGE = 6.0;
    private static final double ORBIT_RADIUS = 1.4;
    private static final float ANGLE_STEP = 0.11f;
    private static final float SPEED_RPM = 32.0f;
    private static final float CAPACITY_PER_RPM = 32.0f;
    private static final int MISSING_LIMIT = 200;

    private UUID titanId;
    private boolean active;
    private float angle;
    private int missingTicks;

    public TitanTreadmillBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TITAN_TREADMILL.get(), pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        return active ? SPEED_RPM : 0;
    }

    @Override
    public float calculateAddedStressCapacity() {
        this.lastCapacityProvided = CAPACITY_PER_RPM;
        return CAPACITY_PER_RPM;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level instanceof ServerLevel server)) return;

        Mob titan = null;
        if (titanId != null) {
            Entity e = server.getEntity(titanId);
            if (e instanceof Mob m && m.isAlive() && m.getTags().contains(TitanSyringeItem.TITAN_TAG)) {
                titan = m;
                missingTicks = 0;
            } else if (e != null || ++missingTicks > MISSING_LIMIT) {
                // morreu/sumiu de vez (ou ficou tempo demais fora do mundo): libera o lugar
                titanId = null;
                missingTicks = 0;
                setChanged();
                sendData();
            }
        }

        if (titanId == null && server.getGameTime() % 10 == 0) {
            titan = captureNearest(server);
        }

        if (titan != null) walk(titan);

        boolean nowActive = titan != null;
        if (nowActive != active) {
            active = nowActive;
            updateGeneratedRotation();
        }
    }

    private Mob captureNearest(ServerLevel server) {
        AABB box = new AABB(worldPosition).inflate(CAPTURE_RANGE, 5.0, CAPTURE_RANGE);
        double cx = worldPosition.getX() + 0.5, cz = worldPosition.getZ() + 0.5;
        List<Mob> candidates = server.getEntitiesOfClass(Mob.class, box, m ->
                m.isAlive()
                        && m.getTags().contains(TitanSyringeItem.TITAN_TAG)
                        && !TitanTransformation.isTransforming(m)
                        && !ownedByOther(m));
        Mob best = candidates.stream()
                .min(Comparator.comparingDouble(m -> m.distanceToSqr(cx, m.getY(), cz)))
                .orElse(null);
        if (best == null) return null;

        titanId = best.getUUID();
        best.setNoAi(true);
        best.setTarget(null);
        best.setPersistenceRequired();
        best.getPersistentData().putLong(KEY_BOUND, worldPosition.asLong());
        angle = (float) Math.atan2(best.getZ() - cz, best.getX() - cx);
        server.playSound(null, worldPosition, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 2.0f, 0.5f);
        server.sendParticles(ParticleTypes.CLOUD, best.getX(), best.getY() + 0.3, best.getZ(),
                20, 1.0, 0.2, 1.0, 0.05);
        setChanged();
        sendData();
        return best;
    }

    /** Titas presos em outra esteira (que ainda existe e segura esse mesmo tita) nao podem ser roubados. */
    private boolean ownedByOther(Mob m) {
        CompoundTag data = m.getPersistentData();
        if (!data.contains(KEY_BOUND)) return false;
        BlockPos other = BlockPos.of(data.getLong(KEY_BOUND));
        if (other.equals(worldPosition)) return false;
        return level.getBlockEntity(other) instanceof TitanTreadmillBE o && m.getUUID().equals(o.titanId);
    }

    /** Conduz o tita em circulo em cima da esteira (preso: nao sai dali). */
    private void walk(Mob titan) {
        if (!titan.isNoAi()) titan.setNoAi(true);
        angle = (angle + ANGLE_STEP) % (float) (Math.PI * 2);
        double x = worldPosition.getX() + 0.5 + Math.cos(angle) * ORBIT_RADIUS;
        double z = worldPosition.getZ() + 0.5 + Math.sin(angle) * ORBIT_RADIUS;
        float yaw = (float) Math.toDegrees(angle); // olha na direcao em que anda (tangente do circulo)
        titan.setPos(x, worldPosition.getY() + 1.0, z);
        titan.setYRot(yaw);
        titan.setYHeadRot(yaw);
        titan.yBodyRot = yaw;
        titan.setDeltaMovement(0, 0, 0);
        titan.fallDistance = 0;
        titan.setTarget(null);
    }

    private void release() {
        if (titanId == null || !(level instanceof ServerLevel server)) return;
        Entity e = server.getEntity(titanId);
        if (e instanceof Mob m) {
            m.setNoAi(false);
            m.getPersistentData().remove(KEY_BOUND);
        }
        titanId = null;
    }

    @Override
    public void remove() {
        // remove() roda ao quebrar o bloco ou tirar da estrutura, nao ao descarregar o chunk:
        // so solta o tita se o bloco realmente sumiu
        if (level != null && !level.isClientSide
                && !level.getBlockState(worldPosition).is(ModBlocks.TITAN_TREADMILL.get())) {
            release();
        }
        super.remove();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (titanId != null) compound.putUUID("Titan", titanId);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        titanId = compound.hasUUID("Titan") ? compound.getUUID("Titan") : null;
    }
}
