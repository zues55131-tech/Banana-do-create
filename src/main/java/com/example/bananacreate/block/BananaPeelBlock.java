package com.example.bananacreate.block;

import com.example.bananacreate.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Casca de banana: bloco 3D fino no chao. Quem pisa nela (andando ou correndo)
 * escorrega, e e arremessado para frente. Agachar evita. Some depois de UM escorregao.
 */
public class BananaPeelBlock extends Block {
    public static final IntegerProperty USES = IntegerProperty.create("uses", 0, 2);
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 3, 15);
    private static final String LAST_SLIP = "bananacreate_last_slip";

    public BananaPeelBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(USES, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(USES);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !entity.onGround()) return;
        if (entity instanceof Player player && player.isShiftKeyDown()) return;
        if (living.isFallFlying() || living.isPassenger()) return;

        long last = entity.getPersistentData().getLong(LAST_SLIP);
        if (entity.tickCount - last < 10 && entity.tickCount >= last) return;

        Vec3 mv = entity.getDeltaMovement();
        Vec3 dir = new Vec3(mv.x, 0, mv.z);
        if (dir.lengthSqr() < 0.0025) {
            Vec3 look = entity.getLookAngle();
            dir = new Vec3(look.x, 0, look.z);
        }
        if (dir.lengthSqr() < 1.0E-4) return;

        entity.getPersistentData().putLong(LAST_SLIP, entity.tickCount);
        dir = dir.normalize().scale(1.05);
        entity.setDeltaMovement(dir.x, 0.38, dir.z);
        entity.hasImpulse = true;
        entity.hurtMarked = true;

        if (level instanceof ServerLevel server) {
            level.playSound(null, pos, SoundEvents.SLIME_BLOCK_FALL, SoundSource.BLOCKS, 0.9f, 1.5f);
            server.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getY() + 0.1, entity.getZ(), 6, 0.2, 0.05, 0.2, 0.02);
            // a casca some assim que alguem escorrega nela
            level.destroyBlock(pos, false);
        }
    }

    /** Tenta colocar uma casca perto de {@code start}, descendo ate achar chao firme (max 4 blocos). */
    public static boolean tryPlace(Level level, BlockPos start) {
        BlockPos.MutableBlockPos p = start.mutable();
        BlockState peel = ModBlocks.BANANA_PEEL.get().defaultBlockState();
        for (int i = 0; i < 4; i++) {
            BlockState here = level.getBlockState(p);
            if (here.canBeReplaced() && here.getFluidState().isEmpty()) {
                if (peel.canSurvive(level, p)) {
                    level.setBlockAndUpdate(p.immutable(), peel);
                    return true;
                }
            } else {
                return false;
            }
            p.move(Direction.DOWN);
        }
        return false;
    }
}
