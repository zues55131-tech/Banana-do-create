package com.example.bananacreate.entity;

import com.example.bananacreate.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

/**
 * Rei Banana: boss gigante e desengonçado. Usa bananas como arma, escorrega
 * os jogadores, faz festa de dança e, na segunda fase, chove banana.
 * Derrotado, solta UMA peca da Armadura do Rei Banana por vez (em ordem, por jogador).
 */
public class BananaKing extends Monster {
    private static final String KILL_KEY = "bananacreate_banana_king_kills";
    private static final int LINES = 8;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.bananacreate.banana_king"),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_10);

    private int abilityCooldown = 100;
    private int spawnShield = 80;
    private boolean enraged;

    public BananaKing(EntityType<? extends BananaKing> type, Level level) {
        super(type, level);
        setPersistenceRequired();
        bossEvent.setDarkenScreen(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 800.0)
                .add(Attributes.ATTACK_DAMAGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.STEP_HEIGHT, 1.6)
                .add(Attributes.SCALE, 3.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new AbilityGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 24.0f));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ---------------------------------------------------------------- barra de boss
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!level().isClientSide && tickCount == 0) {
            say(0);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (abilityCooldown > 0) abilityCooldown--;
        if (spawnShield > 0) spawnShield--;

        if (!enraged && getHealth() < getMaxHealth() * 0.5f) {
            enraged = true;
            bossEvent.setColor(BossEvent.BossBarColor.RED);
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.34);
            say(1);
            playSound(SoundEvents.RAVAGER_ROAR, 2.0f, 0.6f);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (spawnShield > 0 && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false; // animacao de entrada: ainda nao da para bater
        }
        return super.hurt(source, Math.min(amount, 60.0f)); // nada de matar de um golpe so
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void checkDespawn() {
        // nunca some sozinho
    }

    // ---------------------------------------------------------------- falas
    /** Fala engracada (sempre em frases originais), enviada para quem esta por perto. */
    private void say(int index) {
        if (!(level() instanceof ServerLevel server)) return;
        Component line = Component.translatable("message.bananacreate.banana_king.line" + index);
        Component msg = Component.translatable("message.bananacreate.banana_king.say",
                Component.translatable("entity.bananacreate.banana_king"), line);
        for (Player p : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(60.0))) {
            p.sendSystemMessage(msg);
        }
    }

    private void sayRandomTaunt() {
        say(2 + random.nextInt(LINES - 2));
    }

    // ---------------------------------------------------------------- poderes
    private List<Player> playersNear(double radius) {
        return level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(radius),
                p -> !p.isSpectator() && !p.isCreative());
    }

    private void throwBanana(Vec3 from, Vec3 to, float speed, float inaccuracy) {
        BananaProjectile banana = new BananaProjectile(level(), this);
        banana.setPos(from.x, from.y, from.z);
        Vec3 d = to.subtract(from);
        banana.shoot(d.x, d.y, d.z, speed, inaccuracy);
        level().addFreshEntity(banana);
    }

    private void useAbility(LivingEntity target) {
        if (!(level() instanceof ServerLevel server)) return;
        int pool = enraged ? 5 : 4;
        int pick = random.nextInt(pool);
        sayRandomTaunt();
        switch (pick) {
            case 0 -> { // rajada de bananas
                Vec3 eye = new Vec3(getX(), getEyeY() - 0.5, getZ());
                Vec3 aim = target.getEyePosition();
                for (int i = 0; i < (enraged ? 9 : 6); i++) {
                    throwBanana(eye, aim, 1.5f, 9.0f);
                }
                playSound(SoundEvents.SNOWBALL_THROW, 2.0f, 0.5f);
            }
            case 1 -> { // casca no chao: escorrega todo mundo
                for (Player p : playersNear(16.0)) {
                    double ang = random.nextDouble() * Math.PI * 2;
                    p.push(Math.cos(ang) * 1.4, 0.9, Math.sin(ang) * 1.4);
                    p.hurtMarked = true;
                    p.hurt(damageSources().mobAttack(this), 4.0f);
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                }
                for (int i = 0; i < 5; i++) {
                    ItemEntity peel = new ItemEntity(level(), getX() + random.nextGaussian() * 3, getY() + 1,
                            getZ() + random.nextGaussian() * 3, new ItemStack(ModItems.BANANA_PEEL.get()));
                    level().addFreshEntity(peel);
                }
                playSound(SoundEvents.SLIME_SQUISH, 2.0f, 0.5f);
            }
            case 2 -> { // festa da danca: tontura geral e o boss se cura
                for (Player p : playersNear(18.0)) {
                    p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 140));
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
                }
                heal(enraged ? 25.0f : 15.0f);
                server.sendParticles(ParticleTypes.NOTE, getX(), getY() + getBbHeight() * 0.6, getZ(),
                        40, 3.0, 2.0, 3.0, 1.0);
                playSound(SoundEvents.NOTE_BLOCK_BANJO.value(), 3.0f, 0.8f);
                playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 3.0f, 1.2f);
            }
            case 3 -> { // pisao: onda de choque em volta
                for (Player p : playersNear(9.0)) {
                    Vec3 away = p.position().subtract(position()).normalize();
                    p.push(away.x * 1.6, 0.6, away.z * 1.6);
                    p.hurtMarked = true;
                    p.hurt(damageSources().mobAttack(this), 12.0f);
                }
                server.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.5, getZ(), 6, 3.0, 0.2, 3.0, 0.0);
                playSound(SoundEvents.GENERIC_EXPLODE.value(), 2.0f, 0.6f);
            }
            default -> { // fase 2: chuva de bananas em cima do alvo
                for (int i = 0; i < 12; i++) {
                    BananaProjectile banana = new BananaProjectile(level(), this);
                    banana.setPos(target.getX() + random.nextGaussian() * 3, target.getY() + 10 + random.nextDouble() * 3,
                            target.getZ() + random.nextGaussian() * 3);
                    banana.setDeltaMovement(0, -0.9, 0);
                    level().addFreshEntity(banana);
                }
                playSound(SoundEvents.AMBIENT_CAVE.value(), 2.0f, 0.5f);
            }
        }
        abilityCooldown = enraged ? 55 : 90 + random.nextInt(40);
    }

    private static class AbilityGoal extends Goal {
        private final BananaKing boss;

        AbilityGoal(BananaKing boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            LivingEntity t = boss.getTarget();
            return t != null && t.isAlive() && boss.abilityCooldown <= 0 && boss.spawnShield <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity t = boss.getTarget();
            if (t != null) boss.useAbility(t);
        }
    }

    // ---------------------------------------------------------------- drop: uma peca por vez
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        int index;
        if (source.getEntity() instanceof Player player) {
            CompoundTag data = player.getPersistentData();
            int kills = data.getInt(KILL_KEY);
            index = kills % 4;                  // capacete, peitoral, calcas, botas... e de novo
            data.putInt(KILL_KEY, kills + 1);
        } else {
            index = random.nextInt(4);
        }
        Supplier<? extends Item>[] pieces = pieces();
        spawnAtLocation(new ItemStack(pieces[index].get()));
        spawnAtLocation(new ItemStack(ModItems.GOLDEN_BANANA.get(), 4 + random.nextInt(6)));
    }

    @SuppressWarnings("unchecked")
    private static Supplier<? extends Item>[] pieces() {
        return new Supplier[]{ModItems.BANANA_LORD_HELMET, ModItems.BANANA_LORD_CHESTPLATE,
                ModItems.BANANA_LORD_LEGGINGS, ModItems.BANANA_LORD_BOOTS};
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide) say(7);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 500;
    }

    // ---------------------------------------------------------------- sons
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 2.0f;
    }

    @Override
    public boolean canChangeDimensions(Level from, Level to) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", enraged);
        tag.putInt("SpawnShield", spawnShield);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        enraged = tag.getBoolean("Enraged");
        spawnShield = tag.getInt("SpawnShield");
        if (enraged) bossEvent.setColor(BossEvent.BossBarColor.RED);
    }
}
