package com.example.bananacreate.menu;

import com.example.bananacreate.item.ImplantItem;
import com.example.bananacreate.registry.ModBlocks;
import com.example.bananacreate.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Interface da Mesa de Implantes: 1 slot (so aceita implantes) + inventario do jogador. */
public class ImplantEquipMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    /** Lado do cliente. */
    public ImplantEquipMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(1), ContainerLevelAccess.NULL);
    }

    public ImplantEquipMenu(int id, Inventory inv, Container container, ContainerLevelAccess access) {
        super(ModMenus.IMPLANT_EQUIP.get(), id);
        this.access = access;
        addSlot(new Slot(container, 0, 80, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ImplantItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 66 + r * 18));
            }
        }
        for (int c = 0; c < 9; c++) {
            addSlot(new Slot(inv, c, 8 + c * 18, 124));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            moved = stack.copy();
            if (index == 0) {
                if (!moveItemStackTo(stack, 1, slots.size(), true)) return ItemStack.EMPTY;
            } else if (stack.getItem() instanceof ImplantItem) {
                if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return moved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.IMPLANT_TABLE.get());
    }
}
