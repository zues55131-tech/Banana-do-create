package com.example.bananacreate.menu;

import com.example.bananacreate.cyber.CyberNet;
import com.example.bananacreate.cyber.Cyberware;
import com.example.bananacreate.cyber.CyberwareData;
import com.example.bananacreate.item.ChromeScalpelItem;
import com.example.bananacreate.registry.ModAttachments;
import com.example.bananacreate.registry.ModBlocks;
import com.example.bananacreate.registry.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

/** Interface da Mesa de Remocao: sem slots; cada botao (id = ordem do implante) remove aquele implante. */
public class ImplantRemovalMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public ImplantRemovalMenu(int id, Inventory inv) {
        this(id, inv, ContainerLevelAccess.NULL);
    }

    public ImplantRemovalMenu(int id, Inventory inv, ContainerLevelAccess access) {
        super(ModMenus.IMPLANT_REMOVAL.get(), id);
        this.access = access;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        Cyberware[] all = Cyberware.values();
        if (buttonId < 0 || buttonId >= all.length || !(player instanceof ServerPlayer sp)) return false;
        Cyberware c = all[buttonId];
        CyberwareData data = CyberwareData.of(sp);
        if (!data.has(c)) return false;

        sp.setData(ModAttachments.CYBERWARE, data.without(c));
        CyberNet.syncToAll(sp);
        ItemStack back = new ItemStack(ChromeScalpelItem.itemFor(c));
        if (!sp.getInventory().add(back)) sp.drop(back, false);
        if (!sp.getAbilities().instabuild) {
            sp.hurt(sp.level().damageSources().magic(), 2.0f);
        }
        sp.level().playSound(null, sp.getX(), sp.getY(), sp.getZ(),
                SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 0.8f, 1.4f);
        sp.displayClientMessage(Component.translatable("message.bananacreate.removed",
                Component.translatable("item.bananacreate." + c.key())), true);
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.IMPLANT_REMOVAL_TABLE.get());
    }
}
