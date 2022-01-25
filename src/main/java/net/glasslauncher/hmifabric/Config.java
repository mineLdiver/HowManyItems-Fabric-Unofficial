package net.glasslauncher.hmifabric;

import blue.endless.jankson.Comment;
import net.glasslauncher.hmifabric.tabs.Tab;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.api.MultiplayerSynced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.util.ArrayList;

public class Config {
    
    public static ArrayList<Tab> orderTabs() {
    	ArrayList<Tab> orderedTabs = new ArrayList<>();
    	for(int i = 0; i < HowManyItems.allTabs.size(); i++) {
    		Tab tab = HowManyItems.allTabs.get(i);
    		while(orderedTabs.size() < tab.index + 1)
				orderedTabs.add(null);
    		if(tab.index >= 0) {
    			orderedTabs.set(tab.index, tab);
    		}
		}
    	while(orderedTabs.remove(null)) {}
    	for(int i = 0; i < orderedTabs.size(); i++) {
    		orderedTabs.get(i).index = i;
		}
    	for(int i = 0; i < HowManyItems.allTabs.size(); i++) {
    		Tab tab = HowManyItems.allTabs.get(i);
    		if(tab.index == -2) {
    			tab.index = orderedTabs.size();
    			orderedTabs.add(tab);
    		}
    		else if(tab.index < 0) {
    			tab.index = -1;
    		}
		}
    	//writeConfig();
    	return orderedTabs;
	}
    
    public static void tabOrderChanged(boolean[] tabEnabled, Tab[] tabOrder) {
    	for(int i = 0; i < HowManyItems.allTabs.size(); i++) {
    		Tab tab = HowManyItems.allTabs.get(i);
    		for(int j = 0; j < tabOrder.length; j++) {
    			if(tab.equals(tabOrder[j])) {
    				tab.index = j;
    				if(!tabEnabled[j]) tab.index = -1;
    			}
    		}
    	}
    	//writeConfig();
    }

    @GConfig(value = "config", visibleName = "HMI Config")
	public static ConfigFields config = new ConfigFields();

	public static boolean isHMIServer = true;

	public static KeyBinding pushRecipe = new KeyBinding("Get Recipes", Keyboard.KEY_R);
	public static KeyBinding pushUses = new KeyBinding("Get Uses", Keyboard.KEY_U);
	public static KeyBinding prevRecipe = new KeyBinding("Previous Recipe", Keyboard.KEY_BACK);
	public static KeyBinding allRecipes = new KeyBinding("Show All Recipes", Keyboard.KEY_NONE);

	public static KeyBinding toggleOverlay = new KeyBinding("Toggle HMI", Keyboard.KEY_O);
	public static KeyBinding clearSearchBox = new KeyBinding("Clear Search", Keyboard.KEY_DELETE);
	public static KeyBinding focusSearchBox = new KeyBinding("Focus Search", Keyboard.KEY_RETURN);

	public static class ConfigFields {
		@ConfigName("Overlay Enabled")
		public Boolean overlayEnabled = true;
		@ConfigName("Cheats Enabled")
		public Boolean cheatsEnabled = false;
		@ConfigName("Show Item IDs")
		public Boolean showItemIDs = false;
		@ConfigName("Center Search Bar")
		public Boolean centredSearchBar = false;
		@ConfigName("Fast Search")
		public Boolean fastSearch = false;
		@ConfigName("Inverted Scrolling")
		public Boolean scrollInverted = false;

		@MultiplayerSynced
		@ConfigName("Multiplayer Give Command")
		public String mpGiveCommand = "/give {0} {1} {2}";
		@MultiplayerSynced
		@ConfigName("Multiplayer Heal Command")
		public String mpHealCommand = "";
		@MultiplayerSynced
		@ConfigName("Multiplayer Time Day Command")
		public String mpTimeDayCommand = "/time set 0";
		@MultiplayerSynced
		@ConfigName("Multiplayer Time Night Command")
		public String mpTimeNightCommand = "/time set 13000";
		@MultiplayerSynced
		@ConfigName("Multiplayer Rain On Command")
		public String mpRainONCommand = "";
		@MultiplayerSynced
		@ConfigName("Multiplayer Rain Off Command")
		public String mpRainOFFCommand = "";

		@ConfigName("Draggable Recipe Viewer")
		public Boolean recipeViewerDraggableGui = false;
		@ConfigName("Developer Mode")
		@Comment("Shows null items. Can cause crashes with poorly made mods.")
		public Boolean devMode = false;

		@ConfigName("Recipe Viewer GUI Width")
		public Integer recipeViewerGuiWidth = 251;
		@ConfigName("Recipe Viewer GUI Height")
		public Integer recipeViewerGuiHeight = 134;
	}
}
