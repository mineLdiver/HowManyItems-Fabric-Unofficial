package net.glasslauncher.hmifabric.mixin;

import net.glasslauncher.hmifabric.Config;
import net.glasslauncher.hmifabric.event.HMITabRegistryEvent;
import net.minecraft.recipe.RecipeRegistry;
import net.modificationstation.stationapi.api.StationAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeRegistry.class)
public class MixinRecipeRegistry {

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collections;sort(Ljava/util/List;Ljava/util/Comparator;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterRecipeRegister(CallbackInfo ci) {
        StationAPI.EVENT_BUS.post(new HMITabRegistryEvent());
        Config.orderTabs();
    }
}
