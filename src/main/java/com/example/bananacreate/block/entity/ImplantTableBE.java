package com.example.bananacreate.block.entity;

import com.example.bananacreate.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Mesa de Implantes: guarda 1 implante (colocado pela interface) que fica girando em 3D em cima dela. */
public class ImplantTableBE extends BlockEntity {
    public final SimpleContainer inv = new SimpleContainer(1);
    private boolean loading;

    public ImplantTableBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IMPLANT_TABLE.get(), pos, state);
        inv.addListener(c -> sync());
    }

    public ItemStack getItem() {
        return inv.getItem(0);
    }

    private void sync() {
        if (loading) return;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ItemStack s = getItem();
        if (!s.isEmpty()) tag.put("Item", s.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loading = true;
        ItemStack s = tag.contains("Item")
                ? ItemStack.parse(registries, tag.getCompound("Item")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        inv.setItem(0, s);
        loading = false;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
