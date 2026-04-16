package aji.carpetajiaddition.mixin.rules.toughTurtleEgg;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.TurtleEggBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin {
    @Inject(method = "canDestroyEgg", at = @At("HEAD"), cancellable = true)
    private void canDestroyEgg(ServerLevel level, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetAjiAdditionSettings.toughTurtleEgg) {
            cir.setReturnValue(false);
        }
    }
}
