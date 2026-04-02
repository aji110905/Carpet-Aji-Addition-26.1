package aji.carpetajiaddition.mixin.rules.removeEnderPearlDamage;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderPearlMixin {
    @ModifyArg(
            method = "onHit",
            at = @At(
                    value = "INVOKE",

                target = "Lnet/minecraft/server/level/ServerPlayer;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
                        ),
                index = 2
    )
    private float removeEnderPearlDamage(float damage) {
        if (CarpetAjiAdditionSettings.removeEnderPearlDamage) {
            return 0;
        } else {
            return damage;
        }
    }
}
