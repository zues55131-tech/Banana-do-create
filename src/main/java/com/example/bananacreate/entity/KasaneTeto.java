package com.example.bananacreate.entity;

import com.example.bananacreate.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Kasane Teto (homenagem de fa, versao UTAU).
 *
 * Interacoes:
 *  - Segue quem segura pao na mao.
 *  - Shift + botao direito com pao: alimenta (cura, coracoes, aumenta a amizade e as vezes da presentes).
 *  - Botao direito: abre a troca (pao, note block, cookies, disco...). Com amizade 6+ libera a Jukebox.
 *  - Canta melodias de vez em quando (notas coloridas, olhos fechados e boca aberta).
 *  - Dança quando alguem joga pao no chao OU quando toca um disco numa Jukebox perto.
 *  - Vai ate um Note Block proximo e toca ele (sem mudar a nota do bloco).
 *  - Acena para jogadores que chegam perto.
 *  - Quando atacada, se defende cantando: rajadas sonoras no agressor (SonicDefenseGoal).
 */
public class KasaneTeto extends AbstractVillager {
    private static final EntityDataAccessor<Boolean> SINGING =
            SynchedEntityData.defineId(KasaneTeto.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DANCING =
            SynchedEntityData.defineId(KasaneTeto.class, EntityDataSerializers.BOOLEAN);

    /** Amizade: sobe a cada pao dado com shift + botao direito. */
    private int affection;
    private int greetCooldown;
    /** Quem atacou por ultimo: alvo das rajadas sonoras (ate {@link #DEFENSE_TICKS} sem apanhar de novo). */
    private LivingEntity defendTarget;
    private int defendTicks;
    private static final int DEFENSE_TICKS = 200;

    public KasaneTeto(EntityType<? extends KasaneTeto> type, Level level) {
        super(type, level);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BREAD));
        setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SINGING, false);
        builder.define(DANCING, false);
    }

    public boolean isSinging() {
        return entityData.get(SINGING);
    }

    public void setSinging(boolean value) {
        entityData.set(SINGING, value);
    }

    public boolean isDancing() {
        return entityData.get(DANCING);
    }

    public void setDancing(boolean value) {
        entityData.set(DANCING, value);
    }

    public int getAffection() {
        return affection;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Affection", affection);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        affection = tag.getInt("Affection");
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(0, new SonicDefenseGoal(this));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        goalSelector.addGoal(1, new DanceGoal(this));
        goalSelector.addGoal(2, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(3, new TemptGoal(this, 1.0, stack -> stack.is(Items.BREAD), false));
        goalSelector.addGoal(3, new PlayNoteBlockGoal(this));
        goalSelector.addGoal(3, new SingGoal(this));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.4));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.VILLAGER_SPAWN_EGG) || !isAlive() || isTrading() || isBaby()) {
            return super.mobInteract(player, hand);
        }

        // Shift + direito com pao: alimentar
        if (held.is(Items.BREAD) && player.isSecondaryUseActive()) {
            if (!level().isClientSide) {
                feed(player, held);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        // getOffers() so pode ser chamado no servidor (no cliente lanca excecao e derruba o jogo)
        if (!level().isClientSide && !getOffers().isEmpty()) {
            setTradingPlayer(player);
            openTradingScreen(player, getDisplayName(), 1);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    private void feed(Player player, ItemStack bread) {
        if (!player.getAbilities().instabuild) {
            bread.shrink(1);
        }
        heal(4.0f);
        affection++;
        playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.3f);
        if (level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.HEART, getX(), getY() + 1.9, getZ(), 4, 0.4, 0.3, 0.4, 0.0);
        }
        if (affection % 3 == 0) {
            giveGift();
        }
        if (affection == 6) {
            // amiga o bastante: libera uma troca nova
            getOffers().add(new MerchantOffer(new ItemCost(Items.EMERALD, 8),
                    new ItemStack(Items.JUKEBOX, 1), 2, 15, 0.05f));
            if (level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.NOTE, getX(), getY() + 2.1, getZ(), 6, 0.5, 0.3, 0.5, 1.0);
            }
        }
    }

    /** Presente de agradecimento: cai no chao perto dela. */
    private void giveGift() {
        ItemStack gift = switch (random.nextInt(6)) {
            case 0 -> new ItemStack(Items.BREAD, 3);
            case 1 -> new ItemStack(Items.COOKIE, 4);
            case 2 -> new ItemStack(ModItems.BANANA.get(), 3);
            case 3 -> new ItemStack(Items.EMERALD, 2);
            case 4 -> new ItemStack(Items.NOTE_BLOCK, 1);
            default -> new ItemStack(Items.AMETHYST_SHARD, 2);
        };
        spawnAtLocation(gift);
        playSound(SoundEvents.VILLAGER_YES, 1.0f, 1.4f);
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = getOffers();
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 1),
                new ItemStack(Items.BREAD, 6), 16, 2, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.BREAD, 10),
                new ItemStack(Items.EMERALD, 1), 12, 4, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5),
                new ItemStack(Items.NOTE_BLOCK, 1), 8, 6, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(ModItems.BANANA.get(), 6),
                new ItemStack(Items.COOKIE, 8), 10, 4, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 12),
                new ItemStack(Items.MUSIC_DISC_CAT, 1), 2, 12, 0.05f));
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int xp = 3 + random.nextInt(4);
            level().addFreshEntity(new ExperienceOrb(level(), getX(), getY() + 0.5, getZ(), xp));
        }
    }

    /** Acena para jogadores que chegam perto. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide || !isAlive()) {
            return;
        }
        if (greetCooldown > 0) {
            greetCooldown--;
        } else if (tickCount % 20 == 0 && !isTrading() && !isSinging() && !isDancing()) {
            Player nearby = level().getNearestPlayer(this, 5.0);
            if (nearby != null && !nearby.isSpectator() && hasLineOfSight(nearby)) {
                getLookControl().setLookAt(nearby);
                swing(InteractionHand.MAIN_HAND);
                playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 1.6f);
                greetCooldown = 1200;
            }
        }
    }


    /** Quando apanha de outro ser vivo, ela passa a cantar e disparar rajadas sonoras nele. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide && source.getEntity() instanceof LivingEntity attacker
                && attacker != this && attacker.isAlive()) {
            defendTarget = attacker;
            defendTicks = DEFENSE_TICKS;
        }
        return hurt;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean getYesSound) {
        return getYesSound ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    // ------------------------------------------------------------------ goals

    /** Danca com pao no chao (ate comer) ou enquanto houver Jukebox tocando por perto. */
    static class DanceGoal extends Goal {
        private final KasaneTeto teto;
        private int danceTicks;
        private boolean viaJukebox;
        private int scanDelay;
        private boolean cachedJukebox;

        DanceGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        private List<ItemEntity> breadNearby() {
            return teto.level().getEntitiesOfClass(ItemEntity.class,
                    teto.getBoundingBox().inflate(8.0, 3.0, 8.0),
                    item -> item.getItem().is(Items.BREAD));
        }

        private boolean jukeboxPlayingNearby() {
            if (--scanDelay > 0) {
                return cachedJukebox;
            }
            scanDelay = 20;
            cachedJukebox = false;
            BlockPos c = teto.blockPosition();
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-9, -3, -9), c.offset(9, 3, 9))) {
                BlockState js = teto.level().getBlockState(p);
                if (js.is(Blocks.JUKEBOX) && js.getValue(JukeboxBlock.HAS_RECORD)) {
                    cachedJukebox = true;
                    break;
                }
            }
            return cachedJukebox;
        }

        @Override
        public boolean canUse() {
            return !teto.isTrading() && (jukeboxPlayingNearby() || !breadNearby().isEmpty());
        }

        @Override
        public boolean canContinueToUse() {
            if (teto.isTrading()) {
                return false;
            }
            return viaJukebox ? jukeboxPlayingNearby() : (danceTicks < 200 && !breadNearby().isEmpty());
        }

        @Override
        public void start() {
            danceTicks = 0;
            viaJukebox = jukeboxPlayingNearby();
            teto.getNavigation().stop();
            teto.setDancing(true);
        }

        @Override
        public void tick() {
            danceTicks++;
            teto.getNavigation().stop();

            float spin = teto.getYRot() + (viaJukebox ? 12.0f : 25.0f);
            teto.setYRot(spin);
            teto.yBodyRot = spin;
            teto.yHeadRot = spin;

            if (danceTicks % 10 == 0) {
                teto.getJumpControl().jump();
            }
            if (danceTicks % 8 == 0 && teto.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.NOTE, teto.getX(), teto.getY() + 2.1, teto.getZ(),
                        1, 0.3, 0.2, 0.3, 1.0);
            }
        }

        @Override
        public void stop() {
            teto.setDancing(false);
            if (!viaJukebox && danceTicks >= 200) {
                List<ItemEntity> bread = breadNearby();
                if (!bread.isEmpty()) {
                    bread.get(0).getItem().shrink(1); // comeu um pao
                    teto.playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.2f);
                }
            }
            danceTicks = 0;
        }
    }

    /**
     * Autodefesa: cantando, dispara rajadas sonoras (tipo Warden, mas musicais) em quem a atacou.
     * Cada rajada tem um instante de carga (nota subindo), depois acerta em linha reta ate 20 blocos,
     * ferindo, empurrando e deixando o alvo tonto. Dura 10 s depois da ultima pancada recebida.
     */
    static class SonicDefenseGoal extends Goal {
        private static final double RANGE = 20.0;
        private static final int WINDUP = 14;
        private static final int PERIOD = 34;
        private final KasaneTeto teto;
        private int tick;

        SonicDefenseGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = teto.defendTarget;
            return t != null && t.isAlive() && teto.defendTicks > 0 && !teto.isTrading()
                    && teto.distanceToSqr(t) <= (RANGE + 6) * (RANGE + 6);
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            tick = 0;
            teto.getNavigation().stop();
            teto.setDancing(false);
            teto.setSinging(true);
        }

        @Override
        public void tick() {
            LivingEntity target = teto.defendTarget;
            if (target == null) return;
            teto.defendTicks--;
            teto.getNavigation().stop();
            teto.getLookControl().setLookAt(target, 60.0f, 60.0f);
            tick++;

            int phase = tick % PERIOD;
            if (phase > 0 && phase < WINDUP && phase % 4 == 0 && teto.level() instanceof ServerLevel server) {
                // carga: notas subindo de tom
                float pitch = 0.8f + phase / (float) WINDUP * 1.2f;
                teto.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.6f, pitch);
                server.sendParticles(ParticleTypes.NOTE, teto.getX(), teto.getY() + 2.1, teto.getZ(),
                        0, phase / (double) WINDUP, 0.0, 0.0, 1.0);
            } else if (phase == WINDUP) {
                fire(target);
            }
        }

        private void fire(LivingEntity target) {
            if (!(teto.level() instanceof ServerLevel server)) return;
            Vec3 from = teto.getEyePosition().add(0, -0.1, 0);
            Vec3 to = target.getBoundingBox().getCenter();
            Vec3 path = to.subtract(from);
            double dist = path.length();
            if (dist < 0.5 || dist > RANGE + 4) return;
            Vec3 dir = path.scale(1.0 / dist);

            // rajada visivel: ondas sonicas e notas coloridas ao longo da linha
            for (double d = 1.0; d < dist; d += 1.0) {
                Vec3 pt = from.add(dir.scale(d));
                server.sendParticles(ParticleTypes.SONIC_BOOM, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0);
                server.sendParticles(ParticleTypes.NOTE, pt.x, pt.y, pt.z, 0, (d / dist), 0.0, 0.0, 1.0);
            }
            teto.playSound(SoundEvents.WARDEN_SONIC_BOOM, 2.5f, 1.4f);
            teto.swing(InteractionHand.MAIN_HAND);

            if (target.hurt(teto.damageSources().sonicBoom(teto), 6.0f)) {
                target.knockback(1.5, -dir.x, -dir.z);
                target.setDeltaMovement(target.getDeltaMovement().add(dir.x * 1.2, 0.35, dir.z * 1.2));
                target.hurtMarked = true;
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.CONFUSION, 60, 0));
            }
        }

        @Override
        public void stop() {
            teto.setSinging(false);
            teto.defendTarget = null;
            teto.defendTicks = 0;
        }
    }

    /** Canta uma melodia curta (escala pentatonica) com notas coloridas subindo. */
    static class SingGoal extends Goal {
        private static final int[] SCALE = {0, 2, 4, 7, 9, 12, 14};
        private final KasaneTeto teto;
        private int[] melody;
        private int index;
        private int tick;

        SingGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !teto.isTrading() && !teto.isInWaterOrBubble() && teto.getRandom().nextInt(500) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return !teto.isTrading() && index < melody.length;
        }

        @Override
        public void start() {
            melody = new int[8 + teto.getRandom().nextInt(5)];
            int step = 2;
            for (int i = 0; i < melody.length; i++) {
                step = Math.max(0, Math.min(SCALE.length - 1, step + teto.getRandom().nextInt(3) - 1));
                melody[i] = SCALE[step];
            }
            index = 0;
            tick = 0;
            teto.getNavigation().stop();
            teto.setSinging(true);
        }

        @Override
        public void tick() {
            teto.getNavigation().stop();
            Player listener = teto.level().getNearestPlayer(teto, 8.0);
            if (listener != null) {
                teto.getLookControl().setLookAt(listener, 30.0f, 30.0f);
            }
            if (++tick % 7 != 0 || index >= melody.length) {
                return;
            }
            int semitone = melody[index++];
            float pitch = 0.7f * (float) Math.pow(2.0, semitone / 12.0);
            teto.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, pitch);
            if (teto.level() instanceof ServerLevel server) {
                // count = 0: o "deltaX" vira a cor da nota (0..1)
                server.sendParticles(ParticleTypes.NOTE, teto.getX(), teto.getY() + 2.1, teto.getZ(),
                        0, semitone / 24.0, 0.0, 0.0, 1.0);
            }
        }

        @Override
        public void stop() {
            teto.setSinging(false);
            index = 0;
        }
    }

    /** Anda ate um Note Block proximo e toca varias vezes (nao muda a nota do bloco). */
    static class PlayNoteBlockGoal extends Goal {
        private final KasaneTeto teto;
        private BlockPos target;
        private int plays;
        private int cooldown;
        private int timeout;

        PlayNoteBlockGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private BlockPos findNoteBlock() {
            BlockPos c = teto.blockPosition();
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-8, -2, -8), c.offset(8, 2, 8))) {
                if (teto.level().getBlockState(p).is(Blocks.NOTE_BLOCK)
                        && teto.level().getBlockState(p.above()).isAir()) {
                    double d = p.distSqr(c);
                    if (d < bestDist) {
                        bestDist = d;
                        best = p.immutable();
                    }
                }
            }
            return best;
        }

        @Override
        public boolean canUse() {
            if (teto.isTrading() || teto.getRandom().nextInt(250) != 0) {
                return false;
            }
            target = findNoteBlock();
            return target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && plays < 8 && timeout < 300 && !teto.isTrading()
                    && teto.level().getBlockState(target).is(Blocks.NOTE_BLOCK);
        }

        @Override
        public void start() {
            plays = 0;
            cooldown = 0;
            timeout = 0;
        }

        @Override
        public void tick() {
            timeout++;
            double tx = target.getX() + 0.5;
            double ty = target.getY() + 0.5;
            double tz = target.getZ() + 0.5;
            teto.getLookControl().setLookAt(tx, ty, tz);
            if (teto.distanceToSqr(tx, ty, tz) > 6.25) {
                teto.getNavigation().moveTo(tx, target.getY(), tz, 0.9);
                return;
            }
            teto.getNavigation().stop();
            if (--cooldown <= 0) {
                teto.swing(InteractionHand.MAIN_HAND);
                teto.level().blockEvent(target, Blocks.NOTE_BLOCK, 0, 0);
                plays++;
                cooldown = 12;
            }
        }

        @Override
        public void stop() {
            target = null;
            plays = 0;
        }
    }
}
