package net.glasslauncher.hmifabric;

import net.glasslauncher.hmifabric.tabs.TabCrafting;
import net.glasslauncher.hmifabric.tabs.TabRegistry;
import net.glasslauncher.hmifabric.tabs.TabSmelting;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.registry.Identifier;

import java.util.*;

public class TabUtils {

    private static final Map<Class<? extends ContainerBase>, ItemInstance> guiToBlock = new HashMap<>();

    public static ItemInstance getItemFromGui(ContainerBase screen) {
        return guiToBlock.get(screen.getClass());
    }

    public static void putItemGui(Class<? extends ContainerBase> gui, ItemInstance item) {
        guiToBlock.put(gui, item);
    }

    public static void addWorkBenchGui(Class<? extends ContainerBase> gui) {
        TabCrafting workbenchTab = (TabCrafting) TabRegistry.INSTANCE.get(Identifier.of(HowManyItems.MODID, "crafting"));
        //noinspection ConstantConditions If this is null, we have bigger issues.
        workbenchTab.guiCraftingStations.add(gui);
    }

    public static void addEquivalentWorkbench(ItemInstance item) {
        TabCrafting workbenchTab = (TabCrafting) TabRegistry.INSTANCE.get(Identifier.of(HowManyItems.MODID, "crafting"));
        //noinspection ConstantConditions
        workbenchTab.equivalentCraftingStations.add(item);
    }

    public static void addEquivalentFurnace(ItemInstance item) {
        TabSmelting furnaceTab = (TabSmelting) TabRegistry.INSTANCE.get(Identifier.of(HowManyItems.MODID, "smelting"));
        //noinspection ConstantConditions
        furnaceTab.equivalentCraftingStations.add(item);
    }

    public static void addHiddenModItems(ArrayList<ItemInstance> itemList) {
    }
}
