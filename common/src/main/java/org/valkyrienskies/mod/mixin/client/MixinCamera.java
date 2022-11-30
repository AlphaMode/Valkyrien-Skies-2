package org.valkyrienskies.mod.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.primitives.AABBi;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.client.IVSCamera;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(Camera.class)
public abstract class MixinCamera implements IVSCamera {
    // region Shadow
    @Shadow
    private boolean initialized;
    @Shadow
    private BlockGetter level;
    @Shadow
    private Entity entity;
    @Shadow
    @Final
    private Vector3f forwards;
    @Shadow
    @Final
    private Vector3f up;
    @Shadow
    @Final
    private Vector3f left;
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;
    @Shadow
    @Final
    private Quaternionf rotation;
    @Shadow
    private boolean detached;
    @Shadow
    private float eyeHeight;
    @Shadow
    private float eyeHeightOld;

    @Shadow
    protected abstract double getMaxZoom(double startingDistance);

    @Shadow
    protected abstract void move(double distanceOffset, double verticalOffset, double horizontalOffset);

    @Shadow
    protected abstract void setPosition(double x, double y, double z);
    // endregion

    @Override
    public void setupWithShipMounted(final @NotNull BlockGetter level, final @NotNull Entity renderViewEntity,
        final boolean thirdPerson, final boolean thirdPersonReverse, final float partialTicks,
        final @NotNull ClientShip shipMountedTo, final @NotNull Vector3dc inShipPlayerPosition) {
        final ShipTransform renderTransform = shipMountedTo.getRenderTransform();
        final Vector3dc playerBasePos =
            renderTransform.getShipToWorldMatrix().transformPosition(inShipPlayerPosition, new Vector3d());
        final Vector3dc playerEyePos = renderTransform.getShipCoordinatesToWorldCoordinatesRotation()
            .transform(new Vector3d(0.0, Mth.lerp(partialTicks, this.eyeHeightOld, this.eyeHeight), 0.0))
            .add(playerBasePos);

        this.initialized = true;
        this.level = level;
        this.entity = renderViewEntity;
        this.detached = thirdPerson;
        this.setRotationWithShipTransform(renderViewEntity.getViewYRot(partialTicks),
            renderViewEntity.getViewXRot(partialTicks), renderTransform);
        this.setPosition(playerEyePos.x(), playerEyePos.y(), playerEyePos.z());
        if (thirdPerson) {
            if (thirdPersonReverse) {
                this.setRotationWithShipTransform(this.yRot + 180.0F, -this.xRot, renderTransform);
            }

            final AABBi boundingBox = (AABBi) shipMountedTo.getShipVoxelAABB();

            double dist = ((boundingBox.lengthX() + boundingBox.lengthY() + boundingBox.lengthZ()) / 3.0) * 1.5;

            dist = dist > 4 ? dist : 4;

            this.move(-this.getMaxZoom(4.0 * (dist / 4.0)), 0.0, 0.0);
        }
    }

    @Unique
    private void setRotationWithShipTransform(final float yaw, final float pitch, final ShipTransform renderTransform) {
        final Quaterniondc originalRotation =
            new Quaterniond().rotateY(Math.toRadians(-yaw)).rotateX(Math.toRadians(pitch)).normalize();
        final Quaterniondc newRotation =
            renderTransform.getShipCoordinatesToWorldCoordinatesRotation().mul(originalRotation, new Quaterniond());
        this.xRot = pitch;
        this.yRot = yaw;
        VectorConversionsMCKt.set(this.rotation, newRotation);
        this.forwards.set(0.0F, 0.0F, 1.0F);
        this.forwards.rotate(this.rotation);
        this.up.set(0.0F, 1.0F, 0.0F);
        this.up.rotate(this.rotation);
        this.left.set(1.0F, 0.0F, 0.0F);
        this.left.rotate(this.rotation);
    }
}
