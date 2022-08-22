package net.glasslauncher.hmifabric.mixin;

import net.glasslauncher.hmifabric.Config;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.client.gui.CustomTooltipProvider;
import net.modificationstation.stationapi.api.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;

import java.util.*;

@Mixin(ItemBase.class)
public class MixinItemBase implements CustomTooltipProvider {

    @Override
    public String[] getTooltip(ItemInstance itemInstance, String originalTooltip) {
        ArrayList<String> tooltip = new ArrayList<>();
        tooltip.add(originalTooltip);
        if (Config.config.devMode) {
            for (TagKey<?> key : itemInstance.getRegistryEntry().streamTags().toList())
                tooltip.add(key.id().toString());
        }
        return tooltip.toArray(new String[]{});
    }
}
