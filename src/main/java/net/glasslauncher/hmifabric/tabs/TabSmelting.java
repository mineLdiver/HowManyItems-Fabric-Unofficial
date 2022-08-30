package net.glasslauncher.hmifabric.tabs;

import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.vanillafix.block.Blocks;
import net.modificationstation.stationapi.api.vanillafix.item.Items;

import java.util.*;
import java.util.concurrent.*;

public class TabSmelting extends TabWithTexture {

    private static final Random RANDOM = new Random();
    protected Map recipesComplete;
    protected ArrayList<Object[]> recipes = new ArrayList<>();
    private ArrayList<ItemInstance> fuels;
    private BlockBase tabBlock;
    private int metadata;
    private boolean damagedFurnaceInput = false;

    public TabSmelting(ModID tabCreator) {
        this(tabCreator, new HashMap(), new ArrayList<>(), "/gui/furnace.png", BlockBase.FURNACE);

        recipesComplete = SmeltingRecipeRegistry.getInstance().getRecipes();

        fuels.add(new ItemInstance(ItemBase.stick));
        fuels.add(new ItemInstance(Items.COAL));
        fuels.add(new ItemInstance(Items.CHARCOAL));
        fuels.add(new ItemInstance(ItemBase.lavaBucket));
        fuels.add(new ItemInstance(Blocks.OAK_SAPLING));
        fuels.add(new ItemInstance(Blocks.BIRCH_SAPLING));
        fuels.add(new ItemInstance(Blocks.SPRUCE_SAPLING));
        for (BlockBase block : BlockBase.BY_ID) {
            if (block != null && (block.material == Material.WOOD /*|| ModLoader.AddAllFuel(block.id) > 0 Not sure how to reimplement in SL*/)
                    //ignore signs, doors and locked chest
                    && block.id != 63 && block.id != 64 && block.id != 68 && block.id != 95)
                fuels.add(new ItemInstance(block));

        }

		/*
		for(ItemBase item: ItemBase.byId) {
			if(item != null && ModLoader.AddAllFuel(item.id) > 0)
				fuels.add(new ItemInstance(item));
		}*/

        try {
            //ModLoader.getPrivateValue(net.minecraft.src.TileEntityFurnace.class, new TileEntityFurnace(), "furnaceHacks");
            damagedFurnaceInput = true;
        } catch (Exception exception) {
            damagedFurnaceInput = false;
        }
    }

    public TabSmelting(ModID tabCreator, Map recipes, ArrayList<ItemInstance> fuels, String texturePath, BlockBase tabBlock) {
        this(tabCreator, recipes, fuels, texturePath, tabBlock, 0);
    }

    public TabSmelting(ModID tabCreator, Map recipes, ArrayList<ItemInstance> fuels, String texturePath, BlockBase tabBlock, int metadata) {
        this(tabCreator, 3, recipes, fuels, texturePath, 84, 56, 54, 15, tabBlock, metadata);
    }

    public TabSmelting(ModID tabCreator, int slotsPerRecipe, Map recipes, ArrayList<ItemInstance> fuels, String texturePath, int width, int height, int textureX, int textureY, BlockBase tabBlock, int metadata) {
        this(tabCreator, slotsPerRecipe, texturePath, width, height, textureX, textureY, tabBlock, metadata);

        this.recipesComplete = recipes;
        this.fuels = fuels;
    }

    public TabSmelting(ModID tabCreator, int slotsPerRecipe, String texturePath, int width, int height, int textureX, int textureY, BlockBase tabBlock, int metadata) {
        super(tabCreator, slotsPerRecipe, texturePath, width, height, 3, 3, textureX, textureY);

        this.tabBlock = tabBlock;
        this.metadata = metadata;

        slots[0] = new Integer[]{62, 23};
        slots[1] = new Integer[]{2, 5};
        if (slotsPerRecipe > 2)
            slots[2] = new Integer[]{2, 41};
        equivalentCraftingStations.add(getTabItem());
    }

    @Override
    public ItemInstance[][] getItems(int index, ItemInstance filter) {
        ItemInstance[][] items = new ItemInstance[recipesPerPage][];
        for (int j = 0; j < recipesPerPage; j++) {
            items[j] = new ItemInstance[slots.length];
            int k = index + j;
            if (k < recipes.size()) {
                Object[] recipeObj = recipes.get(k);
                if (recipeObj[1] instanceof ItemInstance[]) {
                    ItemInstance[] recipe = (ItemInstance[]) recipeObj[1];
                    for (int i = 0; i < recipe.length; i++) {
                        int offset = i+1;
                        items[j][offset] = recipe[i];
                        if (recipe[i] != null && recipe[i].getDamage() == -1) {
                            if (recipe[i].usesMeta()) {
                                if (filter != null && recipe[i].itemId == filter.itemId) {
                                    items[j][offset] = new ItemInstance(recipe[i].getType(), 0, filter.getDamage());
                                } else {
                                    items[j][offset] = new ItemInstance(recipe[i].getType());
                                }
                            } else if (filter != null && recipe[i].itemId == filter.itemId) {
                                items[j][offset] = new ItemInstance(recipe[i].getType(), 0, filter.getDamage());
                            }
                        }
                    }
                } else if (recipeObj[1] instanceof TagKey<?>[]) {
                    //noinspection unchecked shut
                    TagKey<ItemBase>[] recipe = (TagKey<ItemBase>[]) recipeObj[1];
                    for (int i = 0; i < recipe.length; i++) {
                        int offset = i+1;
                        ItemBase theHolyOneLiner = ItemRegistry.INSTANCE.getOrCreateEntryList(recipe[i]).getRandom(ThreadLocalRandom.current()).orElseThrow(() -> new RuntimeException("HMI: Error: Tag \"" + recipe[offset-1 /* effectively final, shut it, java */].toString() + "\" does not exist in the registry!")).value();
                        ItemInstance displayItem = new ItemInstance(theHolyOneLiner);
                        items[j][offset] = displayItem;
                        if (recipe[i] != null && displayItem.getDamage() == -1) {
                            if (displayItem.usesMeta()) {
                                if (filter != null && displayItem.itemId == filter.itemId) {
                                    items[j][offset] = new ItemInstance(displayItem.getType(), 0, filter.getDamage());
                                } else {
                                    items[j][offset] = new ItemInstance(displayItem.getType());
                                }
                            } else if (filter != null && displayItem.itemId == filter.itemId) {
                                items[j][offset] = new ItemInstance(displayItem.getType(), 0, filter.getDamage());
                            }
                        }
                    }
                }
                items[j][0] = (ItemInstance) recipeObj[0];
                if (fuels != null) {
                    items[j][2] = fuels.get(RANDOM.nextInt(fuels.size()));
                }
            }

            if (items[j][0] == null && recipesOnThisPage > j) {
                recipesOnThisPage = j;
                redrawSlots = true;
                break;
            } else if (items[j][0] != null && recipesOnThisPage == j) {
                recipesOnThisPage = j + 1;
                redrawSlots = true;
            }
        }

        return items;
    }

    @Override
    public void updateRecipes(ItemInstance filter, Boolean getUses) {
        recipes.clear();
        updateRecipesWithoutClear(filter, getUses);
    }

    public void updateRecipesWithoutClear(ItemInstance filter, Boolean getUses) {
        lastIndex = 0;
        for (Object obj : recipesComplete.keySet()) {
            int dmg = 0;
            if (filter != null) dmg = filter.getDamage();

            ItemInstance output = (ItemInstance) (recipesComplete.get(obj));
            Object input = null;
            if (obj != null) {
                if (obj instanceof ItemInstance) {
                    obj = new ItemInstance[]{((ItemInstance) obj).copy()};
                } else if (obj instanceof TagKey<?>) {
                    //noinspection unchecked
                    TagKey<ItemBase> finalObj = (TagKey<ItemBase>) obj;
                    ItemBase theHolyOneLiner = ItemRegistry.INSTANCE.getOrCreateEntryList(finalObj).getRandom(ThreadLocalRandom.current()).orElseThrow(() -> new RuntimeException("HMI: Error: Tag \"" + finalObj.toString() + "\" does not exist in the registry!")).value();

                    obj = new ItemInstance[]{new ItemInstance(theHolyOneLiner)};
                } else if (obj instanceof TagKey<?>[]) {
                    ArrayList<ItemInstance> coolStuff = new ArrayList<>();
                    //noinspection unchecked
                    for (TagKey<ItemBase> entry : (TagKey<ItemBase>[]) obj) {
                        ItemBase theHolyOneLiner = ItemRegistry.INSTANCE.getOrCreateEntryList(entry).getRandom(ThreadLocalRandom.current()).orElseThrow(() -> new RuntimeException("HMI: Error: Tag \"" + entry.toString() + "\" does not exist in the registry!")).value();
                        coolStuff.add(new ItemInstance(theHolyOneLiner));
                    }
                    obj = coolStuff.toArray(new ItemInstance[]{});
                }
                if (obj instanceof Integer) {
                    if ((Integer) obj < BlockBase.BY_ID.length) {
                        if (BlockBase.BY_ID[(Integer) obj] == null) continue;
                        input = new ItemInstance[]{new ItemInstance(BlockBase.BY_ID[(Integer) obj], 1, dmg)};
                    } else {
                        if ((Integer) obj < ItemBase.byId.length) {
                            input = new ItemInstance[]{new ItemInstance(ItemBase.byId[(Integer) obj], 1, dmg)};
                        } else if (damagedFurnaceInput && (Integer) obj - (output.getDamage() << 16) < BlockBase.BY_ID.length) {
                            if (BlockBase.BY_ID[(Integer) obj - (output.getDamage() << 16)] == null) continue;
                            input = new ItemInstance[]{new ItemInstance(BlockBase.BY_ID[(Integer) obj - (output.getDamage() << 16)], 1, output.getDamage())};
                        } else continue;
                    }
                } else if (obj instanceof ItemInstance[]) {
                    input = obj;
                } else throw new ClassCastException("Invalid recipe item type " + obj.getClass().getName() + "!");
            }
            if (input != null && (filter == null || (getUses && Arrays.stream(((ItemInstance[]) input)).allMatch((inp) -> inp.itemId == filter.itemId)) || (!getUses && output.itemId == filter.itemId && (output.getDamage() == filter.getDamage() || output.getDamage() < 0 || !output.usesMeta())))) {
                recipes.add(new Object[]{output, input});
            } else if (filter == null) throw new ClassCastException("Invalid recipe item type " + input.getClass().getName() + "!");
        }
        size = recipes.size();
        super.updateRecipes(filter, getUses);
        size = recipes.size();
    }

    @Override
    public ItemInstance getTabItem() {
        return new ItemInstance(tabBlock, 1, metadata);
    }
}
