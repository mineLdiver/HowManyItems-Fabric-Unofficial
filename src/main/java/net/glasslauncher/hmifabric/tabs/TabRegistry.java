package net.glasslauncher.hmifabric.tabs;

import com.mojang.serialization.Lifecycle;
import net.glasslauncher.hmifabric.HowManyItems;
import net.glasslauncher.hmifabric.TabUtils;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.registry.*;

import java.util.*;

public class TabRegistry extends SimpleRegistry<Tab> {
    private static final Tab EMPTY = new Tab(ModID.MINECRAFT, 0, 0, 0, 0, 0) {
        @Override
        public ItemInstance getTabItem() {
            return null;
        }

        @Override
        public ItemInstance[][] getItems(int index, ItemInstance filter) {
            return new ItemInstance[0][];
        }

        @Override
        public void draw(int x, int y, int recipeOnThisPageIndex, int cursorX, int cursorY) {}

        @Override
        public Class<? extends ContainerBase> getGuiClass() {
            return null;
        }
    };
    public static final RegistryKey<Registry<Tab>> KEY = RegistryKey.ofRegistry(HowManyItems.MODID.id("tabs"));
    public static final TabRegistry INSTANCE = Registries.create(KEY, new TabRegistry(), registry -> EMPTY, Lifecycle.experimental());

    public List<Tab> tabOrder = new ArrayList<>();

    public TabRegistry() {
        super(KEY, Lifecycle.experimental(), false);
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
