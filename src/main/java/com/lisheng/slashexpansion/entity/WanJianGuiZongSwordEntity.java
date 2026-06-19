package com.lisheng.slashexpansion.entity;

import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.entity.Projectile;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import org.joml.Vector3f;

public class WanJianGuiZongSwordEntity extends EntityAbstractSummonedSword {

    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(
            WanJianGuiZongSwordEntity.class, EntityDataSerializers.FLOAT
    );

    // ★ 修正：使用 VECTOR3（不是 VEC3）
    private static final EntityDataAccessor<Vector3f> TARGET_OFFSET = SynchedEntityData.defineId(
            WanJianGuiZongSwordEntity.class, EntityDataSerializers.VECTOR3
    );

    public WanJianGuiZongSwordEntity(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setPierce((byte) 0);
        this.setNoGravity(true);
    }

    public static WanJianGuiZongSwordEntity createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new WanJianGuiZongSwordEntity(SlashExpansionEntities.WAN_JIAN_SWORD.get(), worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPEED, 1.5f);
        this.entityData.define(TARGET_OFFSET, new Vector3f(0, 0, 0));
    }

    public void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    public void setTargetOffset(Vec3 offset) {
        this.entityData.set(TARGET_OFFSET, new Vector3f((float) offset.x, (float) offset.y, (float) offset.z));
    }

    public Vec3 getTargetOffset() {
        Vector3f v = this.entityData.get(TARGET_OFFSET);
        return new Vec3(v.x, v.y, v.z);
    }

    public void fireAt(LivingEntity target) {
        Vec3 targetPos = target.getEyePosition();
        Vec3 startPos = this.position();
        Vec3 direction = targetPos.subtract(startPos).normalize();
        this.shoot(direction.x, direction.y, direction.z, getSpeed(), 0.5f);
        this.setIsCritical(true);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (target instanceof LivingEntity) {
            KnockBacks.cancel.action.accept((LivingEntity) target);
        }
        super.onHitEntity(result);
    }
}