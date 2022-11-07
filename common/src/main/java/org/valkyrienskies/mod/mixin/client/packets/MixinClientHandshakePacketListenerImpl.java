package org.valkyrienskies.mod.mixin.client.packets;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class MixinClientHandshakePacketListenerImpl {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void preSetCurrentServer(final Connection connection, final Minecraft minecraft, final ServerData serverData, final Screen screen,
        final Consumer consumer, final CallbackInfo ci) {
        ValkyrienSkiesMod.getVsCore().getNetworking().setClientUsesUDP(false);
    }
}
