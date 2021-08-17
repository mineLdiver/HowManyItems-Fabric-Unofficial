package net.glasslauncher.hmifabric;

import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.registry.ModID;

import java.util.ArrayList;

public abstract class TabHandler {

	public abstract void loadTabs(ModID basemod);
	
	public void registerItems(ArrayList<ItemInstance> itemList) { }
}
