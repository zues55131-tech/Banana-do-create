package com.example.bananacreate.block;

import com.example.bananacreate.block.entity.DeathSiphonBE;
import com.example.bananacreate.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Sifao da Morte (Morte): consome rotacao, puxa e drena monstros e condensa Essencia de Morte. */
public class DeathSiphonBlock extends RotatedPillarKineticBlock implements IBE<DeathSiphonBE> {
    public DeathSiphonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Class<DeathSiphonBE> getBlockEntityClass() {
        return DeathSiphonBE.class;
    }

    @Override
    public BlockEntityType<? extends DeathSiphonBE> getBlockEntityType() {
        return ModBlockEntities.DEATH_SIPHON.get();
    }
}
