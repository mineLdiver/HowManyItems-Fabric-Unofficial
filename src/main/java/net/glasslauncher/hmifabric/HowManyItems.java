package net.glasslauncher.hmifabric;

import net.fabricmc.api.ClientModInitializer;
import net.glasslauncher.hmifabric.mixin.DrawableHelperAccessor;
import net.glasslauncher.hmifabric.tabs.Tab;
import net.minecraft.block.BlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegister;
import net.modificationstation.stationapi.api.common.event.EventListener;
import net.modificationstation.stationapi.api.common.event.packet.MessageListenerRegister;
import net.modificationstation.stationapi.api.common.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.common.packet.Message;
import net.modificationstation.stationapi.api.common.registry.Identifier;
import net.modificationstation.stationapi.api.common.registry.ModID;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.logging.Logger;

import static net.glasslauncher.hmifabric.Utils.hiddenItems;

public class HowManyItems implements ClientModInitializer {

    public static Logger logger = Logger.getLogger(HowManyItems.class.getName());

    @Entrypoint.ModID
    private static ModID modID;

    public GuiOverlay overlay;

    @Entrypoint.Instance
    public static HowManyItems thisMod;

    @EventListener
    public void registerKeyBindings(KeyBindingRegister event) {
        event.keyBindings.add(Config.toggleOverlay);
    }

    //Use this if you are a making a mod that adds a tab
    public static void addTab(Tab tab) {
        if(tab != null) {
            modTabs.add(tab);
        }
    }

    public static void addGuiToBlock(Class<? extends ContainerBase> gui, ItemInstance item) {
        TabUtils.putItemGui(gui, item);
    }

    public static void addWorkBenchGui(Class<? extends ContainerBase> gui) {
        TabUtils.addWorkBenchGui(gui);
    }

    public static void addEquivalentWorkbench(ItemInstance item) {
        TabUtils.addEquivalentWorkbench(item);
    }

    public static void addEquivalentFurnace(ItemInstance item) {
        TabUtils.addEquivalentFurnace(item);
    }

    public static void onSettingChanged() {
        if(thisMod.overlay != null) thisMod.overlay.init();
        Config.writeConfig();
    }

    public void onTickInGUI(Minecraft mc, ScreenBase guiscreen) {
        if(guiscreen instanceof ContainerBase) {
            ContainerBase screen = (ContainerBase)guiscreen;
            if(Config.overlayEnabled) {
                if(GuiOverlay.screen != screen || overlay == null || screen.width != overlay.width || screen.height != overlay.height) {
                    overlay = new GuiOverlay(screen);
                }
                overlay.onTick();
            }
            Utils.drawStoredToolTip();
            if(Utils.isKeyDown(Config.pushRecipe) || Utils.isKeyDown(Config.pushUses)) {
                if(!keyHeldLastTick) {
                    boolean getUses = Utils.isKeyDown(Config.pushUses);
                    ItemInstance newFilter = null;

                    ScreenScaler scaledresolution = new ScreenScaler(mc.options, mc.actualWidth, mc.actualHeight);
                    int i = scaledresolution.getScaledWidth();
                    int j = scaledresolution.getScaledHeight();
                    int posX = (Mouse.getEventX() * i) / mc.actualWidth;
                    int posY = j - (Mouse.getEventY() * j) / mc.actualHeight - 1;
                    newFilter = Utils.hoveredItem((ContainerBase)guiscreen, posX, posY);
                    if (newFilter == null) {
                        newFilter = GuiOverlay.hoverItem;
                    }
                    if(newFilter == null) {
                        if(guiscreen instanceof GuiRecipeViewer)
                            newFilter = ((GuiRecipeViewer)guiscreen).getHoverItem();
                    }
                    if(newFilter != null) {
                        pushRecipe(guiscreen, newFilter, getUses);
                    }
                    else {
                        if(Config.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && Config.fastSearch) {
                            GuiOverlay.focusSearchBox();
                        }
                    }
                }
            }
            else if(Utils.isKeyDown(Config.prevRecipe)) {
                if(!keyHeldLastTick) {
                    if (guiscreen instanceof GuiRecipeViewer && !GuiOverlay.searchBoxFocused()) {
                        ((GuiRecipeViewer) guiscreen).pop();
                    }
                    else {
                        if(Config.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && Config.fastSearch)
                            if(!GuiOverlay.emptySearchBox()) GuiOverlay.focusSearchBox();
                    }
                }
            }
            else if(Config.clearSearchBox.key == Config.focusSearchBox.key
                    && Utils.isKeyDown(Config.clearSearchBox)) {

                if(System.currentTimeMillis() > focusCooldown) {
                    focusCooldown = System.currentTimeMillis() + 800L;
                    if(!GuiOverlay.searchBoxFocused())
                        GuiOverlay.clearSearchBox();
                    GuiOverlay.focusSearchBox();
                }
            }
            else if(Utils.isKeyDown(Config.clearSearchBox)) {
                GuiOverlay.clearSearchBox();
            }
            else if(Utils.isKeyDown(Config.focusSearchBox)) {
                if(System.currentTimeMillis() > focusCooldown) {
                    focusCooldown = System.currentTimeMillis() + 800L;
                    GuiOverlay.focusSearchBox();
                }
            }
            else if(Utils.isKeyDown(Config.allRecipes)) {
                pushRecipe(guiscreen, null, false);
            }
            else {
                keyHeldLastTick = false;
            }
            if(Utils.isKeyDown(Config.pushRecipe) || Utils.isKeyDown(Config.pushUses) || Utils.isKeyDown(Config.prevRecipe)) {
                keyHeldLastTick = true;
            }

        }
    }

    public void onTickInGame(Minecraft minecraft)
    {
        if(minecraft.currentScreen == null && Utils.isKeyDown(Config.allRecipes) && !keyHeldLastTick) {
            keyHeldLastTick = true;
            pushRecipe(null, null, false);
        }
    }

    public static boolean keyHeldLastTick = false;
    private static long focusCooldown = 0L;

    public static void pushRecipe(ScreenBase gui, ItemInstance item, boolean getUses) {
        if(Utils.getMC().player.inventory.getCursorItem() == null) {
            if (gui instanceof GuiRecipeViewer) {
                ((GuiRecipeViewer) gui).push(item, getUses);
            }
            else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
                GuiRecipeViewer newgui = new GuiRecipeViewer(item, getUses, gui);
                Utils.getMC().currentScreen = newgui;
                ScreenScaler scaledresolution = new ScreenScaler(Utils.getMC().options, Utils.getMC().actualWidth, Utils.getMC().actualHeight);
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                newgui.init(Utils.getMC(), i, j);
                Utils.getMC().skipGameRender = false;
            }
        }
    }

    public static void pushTabBlock(ScreenBase gui, ItemInstance item) {
        if (gui instanceof GuiRecipeViewer) {
            ((GuiRecipeViewer) gui).pushTabBlock(item);
        }
        else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
            Utils.getMC().lockCursor();
            GuiRecipeViewer newgui = new GuiRecipeViewer(item, gui);
            Utils.getMC().currentScreen = newgui;
            ScreenScaler scaledresolution = new ScreenScaler(Utils.getMC().options, Utils.getMC().actualWidth, Utils.getMC().actualHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            newgui.init(Utils.getMC(), i, j);
            Utils.getMC().skipGameRender = false;
        }
    }

    public static void drawRect(int i, int j, int k, int l, int i1) {
        // This is not that slow. Its only getting the method with reflection that is slow.
        try {
            ((DrawableHelperAccessor) Utils.gui).invokeFill(i, j, k, l, i1);
        } catch (Exception e) {
            logger.severe("Something went very wrong rendering a GUI!");
            e.printStackTrace();
        }
    }

    public static ArrayList<Tab> getTabs() {
        if(tabs == null) {
            allTabs = new ArrayList<>();

            TabUtils.loadTabs(allTabs, modID);

            allTabs.addAll(modTabs);
            Config.readConfig();
            tabs = Config.orderTabs();
        }
        return tabs;
    }

    public static void tabOrderChanged(boolean[] tabEnabled, Tab[] tabOrder) {
        Config.tabOrderChanged(tabEnabled, tabOrder);
        tabs = Config.orderTabs();
    }

    private static ArrayList<Tab> tabs;
    public static ArrayList<Tab> allTabs;
    private static final ArrayList<Tab> modTabs = new ArrayList<>();

    public static void handleHandshake(PlayerBase playerBase, Message customData) {
        Config.isHMIServer = customData.booleans()[0];
    }

    @EventListener
    public void registerMessageListeners(MessageListenerRegister messageListenerRegistry) {
        messageListenerRegistry.registry.registerValue(Identifier.of("hmifabric:handshake"), HowManyItems::handleHandshake);
    }

    @Override
    public void onInitializeClient() {
        Config.init();
        hiddenItems.add(new ItemInstance(BlockBase.STILL_WATER));
        hiddenItems.add(new ItemInstance(BlockBase.STILL_LAVA));
        hiddenItems.add(new ItemInstance(BlockBase.BED));
        hiddenItems.add(new ItemInstance(BlockBase.TALLGRASS));
        hiddenItems.add(new ItemInstance(BlockBase.DEADBUSH));
        hiddenItems.add(new ItemInstance(BlockBase.PISTON_HEAD));
        hiddenItems.add(new ItemInstance(BlockBase.MOVING_PISTON));
        hiddenItems.add(new ItemInstance(BlockBase.DOUBLE_STONE_SLAB));
        hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_DUST));
        hiddenItems.add(new ItemInstance(BlockBase.CROPS));
        hiddenItems.add(new ItemInstance(BlockBase.FARMLAND));
        hiddenItems.add(new ItemInstance(BlockBase.FURNACE_LIT));
        hiddenItems.add(new ItemInstance(BlockBase.STANDING_SIGN));
        hiddenItems.add(new ItemInstance(BlockBase.WOOD_DOOR));
        hiddenItems.add(new ItemInstance(BlockBase.WALL_SIGN));
        hiddenItems.add(new ItemInstance(BlockBase.IRON_DOOR));
        hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_ORE_LIT));
        hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_TORCH));
        hiddenItems.add(new ItemInstance(BlockBase.SUGAR_CANES));
        hiddenItems.add(new ItemInstance(BlockBase.CAKE));
        hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_REPEATER));
        hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_REPEATER_LIT));
        hiddenItems.add(new ItemInstance(BlockBase.LOCKED_CHEST));
    }
}
