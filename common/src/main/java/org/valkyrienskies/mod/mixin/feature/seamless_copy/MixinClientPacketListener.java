package org.valkyrienskies.mod.mixin.feature.seamless_copy;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.assembly.SeamlessChunksManager;
import org.valkyrienskies.mod.mixin.accessors.network.protocol.game.ClientboundSectionBlocksUpdatePacketAccessor;
import org.valkyrienskies.mod.mixinducks.feature.seamless_copy.SeamlessCopyClientPacketListenerDuck;

/**
 * This mixin enables us to prevent the client from processing chunk/block update packets. This serves two purposes:
 *
 * <ol>
 *     <li>
 *         We can force ship assembly to happen all at once, by deferring the packets from being processed until the
 *         ship is fully assembled - players will no longer see flickering (provided the chunks are rerendered
 *         synchronously), and it will be impossible to fall through them
 *     </li>
 *     <li>
 *         We can prevent chunks without a ship from loading until their ship loads in. This allows us to ensure that
 *         any chunk in the shipyard on the client has an associated ship.
 *     </li>
 * </ol>
 * <p>
 * We keep an instance of SeamlessChunksManager in this class because it may get reused across worlds.
 *
 * @see SeamlessChunksManager
 */
@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener implements SeamlessCopyClientPacketListenerDuck {

    @Unique
    private final SeamlessChunksManager chunks = new SeamlessChunksManager(ClientPacketListener.class.cast(this));

    @Inject(
        at = @At("HEAD"),
        method = "cleanup"
    )
    private void beforeCleanup(final CallbackInfo ci) {
        chunks.cleanup();
    }

    @Inject(
        at = @At("HEAD"),
        method = "handleLevelChunk",
        cancellable = true
    )
    private void beforeHandleLevelChunk(final ClientboundLevelChunkPacket packet, final CallbackInfo ci) {
        if (chunks.queue(packet.getX(), packet.getZ(), packet)) {
            ci.cancel();
        }
    }

    @Inject(
        at = @At("HEAD"),
        method = "handleChunkBlocksUpdate",
        cancellable = true
    )
    private void beforeHandleChunkBlocksUpdate(final ClientboundSectionBlocksUpdatePacket packet,
        final CallbackInfo ci) {
        final SectionPos pos = ((ClientboundSectionBlocksUpdatePacketAccessor) packet).getSectionPos();
        if (chunks.queue(pos.x(), pos.z(), packet)) {
            ci.cancel();
        }
    }

    @Inject(
        at = @At("HEAD"),
        method = "handleBlockUpdate",
        cancellable = true
    )
    private void beforeHandleBlockUpdate(final ClientboundBlockUpdatePacket packet, final CallbackInfo ci) {
        if (chunks.queue(packet.getPos().getX() >> 4, packet.getPos().getZ() >> 4, packet)) {
            ci.cancel();
        }
    }

    @Inject(
        at = @At("HEAD"),
        method = "handleLightUpdatePacked",
        cancellable = true
    )
    private void beforeHandleLightUpdatePacked(final ClientboundLightUpdatePacket packet, final CallbackInfo ci) {
        if (chunks.queue(packet.getX(), packet.getZ(), packet)) {
            ci.cancel();
        }
    }

    @NotNull
    @Override
    public SeamlessChunksManager getChunks() {
        return chunks;
    }
}
