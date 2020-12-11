package net.glasslauncher.hmifabric;

import java.util.ArrayList;

import net.modificationstation.stationloader.api.common.mod.StationMod;
import net.minecraft.item.ItemInstance;

public abstract class TabHandler {

	public abstract void loadTabs(StationMod basemod);
	
	public void registerItems(ArrayList<ItemInstance> itemList) { }
}
