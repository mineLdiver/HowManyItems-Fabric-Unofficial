package net.glasslauncher.hmifabric.tabs;

import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.modificationstation.stationapi.api.recipe.StationRecipe;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.tags.TagEntry;
import net.modificationstation.stationapi.api.tags.TagRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
		fuels.add(new ItemInstance(ItemBase.coal));
		fuels.add(new ItemInstance(ItemBase.lavaBucket));
		fuels.add(new ItemInstance(BlockBase.SAPLING));
		for(BlockBase block: BlockBase.BY_ID) {
			if(block != null && (block.material == Material.WOOD /*|| ModLoader.AddAllFuel(block.id) > 0 Not sure how to reimplement in SL*/)
					//ignore signs, doors and locked chest
					&& block.id != 63 && block.id != 64 && block.id != 68 && block.id != 95) 
				fuels.add(new ItemInstance(block));
				
		}

		/*
		for(ItemBase item: ItemBase.byId) {
			if(item != null && ModLoader.AddAllFuel(item.id) > 0)
				fuels.add(new ItemInstance(item));
		}*/
		
		try
        {
            //ModLoader.getPrivateValue(net.minecraft.src.TileEntityFurnace.class, new TileEntityFurnace(), "furnaceHacks");
            damagedFurnaceInput = true;
        }
        catch(Exception exception)
        {
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
		if(slotsPerRecipe > 2)
		slots[2] = new Integer[]{2, 41};
		equivalentCraftingStations.add(getTabItem());
	}

	@Override
	public ItemInstance[][] getItems(int index, ItemInstance filter) {
		ItemInstance[][] items = new ItemInstance[recipesPerPage][];
		for(int j = 0; j < recipesPerPage; j++)
        {
            items[j] = new ItemInstance[slots.length];
            int k = index + j;
            if(k < recipes.size())
            {
            	Object[] recipeObj = recipes.get(k);
            	if (recipeObj instanceof ItemInstance[]) {
            		ItemInstance[] recipe = (ItemInstance[]) recipeObj;
					for (int i = 0; i < recipe.length; i++) {
						items[j][i] = recipe[i];
						if (recipe[i] != null && recipe[i].getDamage() == -1) {
							if (recipe[i].usesMeta()) {
								if (filter != null && recipe[i].itemId == filter.itemId) {
									items[j][i] = new ItemInstance(recipe[i].getType(), 0, filter.getDamage());
								} else {
									items[j][i] = new ItemInstance(recipe[i].getType());
								}
							} else if (filter != null && recipe[i].itemId == filter.itemId) {
								items[j][i] = new ItemInstance(recipe[i].getType(), 0, filter.getDamage());
							}
						}
					}
				}
            	else if (recipeObj instanceof TagEntry[]) {
					TagEntry[] recipe = (TagEntry[]) recipeObj;
					for (int i = 0; i < recipe.length; i++) {
						items[j][i] = recipe[i].displayItem;
						if (recipe[i] != null && recipe[i].displayItem.getDamage() == -1) {
							if (recipe[i].displayItem.usesMeta()) {
								if (filter != null && recipe[i].displayItem.itemId == filter.itemId) {
									items[j][i] = new ItemInstance(recipe[i].displayItem.getType(), 0, filter.getDamage());
								} else {
									items[j][i] = new ItemInstance(recipe[i].displayItem.getType());
								}
							} else if (filter != null && recipe[i].displayItem.itemId == filter.itemId) {
								items[j][i] = new ItemInstance(recipe[i].displayItem.getType(), 0, filter.getDamage());
							}
						}
					}
				}
				if (fuels != null) {
					items[j][2] = fuels.get(RANDOM.nextInt(fuels.size()));
				}
             }

            if(items[j][0] == null && recipesOnThisPage > j) {
            	recipesOnThisPage = j;
                redrawSlots = true;
                break;
            }
            else if(items[j][0] != null && recipesOnThisPage == j) {
            	recipesOnThisPage = j+1;
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
			
			ItemInstance output = (ItemInstance)(recipesComplete.get(obj));
			Object input = null;
			if(obj != null) {
				if (obj instanceof ItemInstance) {
					ItemInstance itemInstance = (ItemInstance) obj;
					obj = itemInstance.itemId;
					dmg = itemInstance.getDamage();
				}
				else if (obj instanceof Identifier) {
					Optional<List<TagEntry>> tagEntries = TagRegistry.INSTANCE.get((Identifier) obj);
					if (tagEntries.isPresent()) {
						obj = tagEntries.get().toArray(new TagEntry[]{});
					}
				}
				if (obj instanceof Integer) {
					if ((Integer) obj < BlockBase.BY_ID.length) {
						if (BlockBase.BY_ID[(Integer) obj] == null) continue;
						input = new ItemInstance(BlockBase.BY_ID[(Integer) obj], 1, dmg);
					} else {
						if ((Integer) obj < ItemBase.byId.length) {
							input = new ItemInstance(ItemBase.byId[(Integer) obj], 1, dmg);
						} else if (damagedFurnaceInput && (Integer) obj - (output.getDamage() << 16) < BlockBase.BY_ID.length) {
							if (BlockBase.BY_ID[(Integer) obj - (output.getDamage() << 16)] == null) continue;
							input = new ItemInstance(BlockBase.BY_ID[(Integer) obj - (output.getDamage() << 16)], 1, output.getDamage());
						} else continue;
					}
				}
				else if (obj instanceof TagEntry[]) {
					input = obj;
				}
				else throw new ClassCastException("Invalid recipe item type " + obj.getClass().getName() + "!");
			}
			if(input instanceof ItemInstance && (filter == null || (getUses && ((ItemInstance) input).itemId == filter.itemId ) || (!getUses && output.itemId == filter.itemId && (output.getDamage() == filter.getDamage() || output.getDamage() < 0 || !output.usesMeta()))))
			{
				recipes.add(new Object[]{output, input});
            }
			else if(input instanceof TagEntry[] && (filter == null || (getUses && ((TagEntry[]) input)[RANDOM.nextInt(((TagEntry[]) input).length)].displayItem.itemId == filter.itemId ) || (!getUses && output.itemId == filter.itemId && (output.getDamage() == filter.getDamage() || output.getDamage() < 0 || !output.usesMeta()))))
			{
				recipes.add(new Object[]{output, input});
			}
			
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
