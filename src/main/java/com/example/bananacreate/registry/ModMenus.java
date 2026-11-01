package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.menu.ImplantEquipMenu;
import com.example.bananacreate.menu.ImplantRemovalMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, BananaCreate.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ImplantEquipMenu>> IMPLANT_EQUIP =
            MENUS.register("implant_equip", () -> new MenuType<>(ImplantEquipMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<ImplantRemovalMenu>> IMPLANT_REMOVAL =
            MENUS.register("implant_removal", () -> new MenuType<>(ImplantRemovalMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
