//package org.valkyrienskies.mod.forge.mixin.compat.flywheel.client;
//
//import com.jozufozu.flywheel.backend.instancing.IInstance;
//import com.jozufozu.flywheel.backend.instancing.InstanceManager;
//import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
//import com.jozufozu.flywheel.backend.instancing.tile.TileInstanceManager;
//import com.jozufozu.flywheel.backend.material.MaterialManager;
//import com.jozufozu.flywheel.core.Contexts;
//import com.jozufozu.flywheel.core.shader.WorldProgram;
//import java.util.WeakHashMap;
//import javax.annotation.ParametersAreNonnullByDefault;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import org.joml.Vector3i;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import org.valkyrienskies.core.api.ships.ClientShip;
//import org.valkyrienskies.mod.common.VSGameUtilsKt;
//import org.valkyrienskies.mod.forge.mixinducks.MixinTileInstanceManagerDuck;
//
//@Mixin(TileInstanceManager.class)
//@ParametersAreNonnullByDefault
//public abstract class MixinTileInstanceManager extends InstanceManager<BlockEntity> implements
//    MixinTileInstanceManagerDuck {
//
//    public WeakHashMap<ShipObjectClient, MaterialManager<WorldProgram>> getShipMaterialManagers() {
//        return shipMaterialManagers;
//    }
//
//    @Unique
//    private final WeakHashMap<ShipObjectClient, MaterialManager<WorldProgram>> shipMaterialManagers =
//        new WeakHashMap<>();
//
//    public MixinTileInstanceManager(final MaterialManager<?> materialManager) {
//        super(materialManager);
//    }
//
//    @Inject(
//        method = "createRaw(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lcom/jozufozu/flywheel/backend/instancing/IInstance;",
//        at = @At("HEAD"),
//        cancellable = true
//    )
//    void preCreateRaw(final BlockEntity blockEntity, final CallbackInfoReturnable<IInstance> cir) {
//        final Level nullableLevel = blockEntity.getLevel();
//        if (nullableLevel instanceof ClientLevel) {
//            final ClientLevel level = (ClientLevel) nullableLevel;
//            final ClientShip ship = VSGameUtilsKt.getShipObjectManagingPos(
//                level, blockEntity.getBlockPos());
//            if (ship != null) {
//                final MaterialManager<WorldProgram> manager = shipMaterialManagers.computeIfAbsent(ship,
//                    k -> MaterialManager.builder(Contexts.WORLD).setIgnoreOriginCoordinate(true).build());
//                final Vector3i c = ship.getChunkClaim().getCenterBlockCoordinates(new Vector3i());
//                ((MaterialManagerAccessor) manager).setOriginCoordinate(new BlockPos(c.x, c.y, c.z));
//
//                cir.setReturnValue(InstancedRenderRegistry.getInstance().create(manager, blockEntity));
//            }
//        }
//    }
//}
