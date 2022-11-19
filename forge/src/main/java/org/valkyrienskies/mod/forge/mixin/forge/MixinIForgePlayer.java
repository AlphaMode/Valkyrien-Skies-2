package org.valkyrienskies.mod.forge.mixin.forge;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;

@Mixin(IForgePlayer.class)
public class MixinIForgePlayer {

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(DDD)D"
        ),
        method = "canInteractWith(Lnet/minecraft/core/BlockPos;D)Z"
    )
    private double redirectCanInteractWithDistance(final double x, final double y, final double z) {
        if (VSGameConfig.SERVER.getEnableInteractDistanceChecks()) {
            return VSGameUtilsKt.squaredDistanceToInclShips(Entity.class.cast(this), x, y, z);
        } else {
            return 0;
        }
    }
}
