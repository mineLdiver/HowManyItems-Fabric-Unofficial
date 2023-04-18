package net.glasslauncher.hmifabric.tabs;

import com.mojang.serialization.Lifecycle;
import net.glasslauncher.hmifabric.HowManyItems;
import net.glasslauncher.hmifabric.TabUtils;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.registry.RegistryKey;
import net.modificationstation.stationapi.api.registry.SimpleRegistry;

import java.util.*;

public class TabRegistry extends SimpleRegistry<Tab> {
    public static final RegistryKey<Registry<Tab>> KEY = RegistryKey.ofRegistry(HowManyItems.MODID.id("tabs"));
    public static final TabRegistry INSTANCE = Registry.create(KEY, new TabRegistry(), Lifecycle.experimental());

    public List<Tab> tabOrder = new ArrayList<>();

    public TabRegistry() {
        super(KEY, Lifecycle.experimental(), null);
    }

    /**
     * Use this over Regsitry.register, otherwise you'll have errors and crashes.
     */
    public void register(Identifier identifier, Tab tab, ItemInstance displayItem) {
        Registry.register(this, identifier, tab);
        TabUtils.putItemGui(tab.getGuiClass(), displayItem);
        tabOrder.add(tab);
    }

    public void addEquivalentCraftingStation(Identifier identifier, ItemInstance displayitem) {
        //noinspection ConstantConditions
        INSTANCE.get(identifier).equivalentCraftingStations.add(displayitem);
    }

}
