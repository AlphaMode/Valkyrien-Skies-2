package org.valkyrienskies.mod.fabric.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class ExampleFabricMixin {

    @Shadow
    @Final
    private static Logger field_23775;

    @Inject(
        at = @At("HEAD"),
        method = "init"
    )
    public void inject(final CallbackInfo info) {
        field_23775.info("Hello from ExampleFabricMixin");
    }

}
