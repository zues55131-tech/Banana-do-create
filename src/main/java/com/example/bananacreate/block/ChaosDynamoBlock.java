package com.example.bananacreate.block;

import com.example.bananacreate.block.entity.ChaosDynamoBE;
import com.example.bananacreate.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Dinamo do Caos (Energia): gerador cinetico instavel, a rotacao muda sozinha. */
public class ChaosDynamoBlock extends RotatedPillarKineticBlock implements IBE<ChaosDynamoBE> {
    public ChaosDynamoBlock(Properties properties) {
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
    public Class<ChaosDynamoBE> getBlockEntityClass() {
        return ChaosDynamoBE.class;
    }

    @Override
    public BlockEntityType<? extends ChaosDynamoBE> getBlockEntityType() {
        return ModBlockEntities.CHAOS_DYNAMO.get();
    }
}
