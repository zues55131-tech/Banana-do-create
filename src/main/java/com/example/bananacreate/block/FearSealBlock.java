package com.example.bananacreate.block;

import com.example.bananacreate.block.entity.FearSealBE;
import com.example.bananacreate.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Selo do Medo (Medo): consome rotacao e aterroriza monstros por perto. */
public class FearSealBlock extends RotatedPillarKineticBlock implements IBE<FearSealBE> {
    public FearSealBlock(Properties properties) {
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
    public Class<FearSealBE> getBlockEntityClass() {
        return FearSealBE.class;
    }

    @Override
    public BlockEntityType<? extends FearSealBE> getBlockEntityType() {
        return ModBlockEntities.FEAR_SEAL.get();
    }
}
