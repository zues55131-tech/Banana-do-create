package com.example.bananacreate.block;

import com.example.bananacreate.block.entity.ImplantTableBE;
import com.example.bananacreate.item.ImplantItem;
import com.example.bananacreate.menu.ImplantEquipMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Mesa de Implantes. Botao direito com a mesa vazia (ou agachado) abre a interface para colocar o implante.
 * Com um implante em cima, botao direito INSTALA no seu corpo (consome o implante).
 */
public class ImplantTableBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 12, 0, 16, 16, 16),
            Block.box(1, 0, 1, 15, 12, 15));

    public ImplantTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ImplantTableBE(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ImplantTableBE be)) return InteractionResult.PASS;

        ItemStack stack = be.getItem();
        if (stack.isEmpty() || player.isSecondaryUseActive()) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new ImplantEquipMenu(id, inv, be.inv, ContainerLevelAccess.create(level, pos)),
                    Component.translatable("block.bananacreate.implant_table")));
            return InteractionResult.CONSUME;
        }
        if (stack.getItem() instanceof ImplantItem implant
                && ImplantItem.tryInstall(player, implant.type(), stack)) {
            if (!player.getAbilities().instabuild) be.inv.setItem(0, ItemStack.EMPTY);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ImplantTableBE be) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, be.getItem());
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
