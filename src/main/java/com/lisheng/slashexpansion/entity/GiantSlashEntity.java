package com.lisheng.slashexpansion.entity;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import com.lisheng.slashexpansion.vfx.Vfx;
import mods.flammpfeil.slashblade.entity.IShootable;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巨型剑气实体（斩天拔剑术 / 剑荡八荒），数值来自 {@code [entities.giant_slash]}。
 */
public class GiantSlashEntity extends Entity implements IShootable {

    private LivingEntity owner;
    private double damage;
    private KnockBacks knockback;
    private boolean isVertical;
    private double distanceTraveled = 0;
    private int ticks = 0;
    private Vec3 directionVec;

    private final Map<Entity, Integer> cooldownMap = new HashMap<>();

    public GiantSlashEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    public GiantSlashEntity(Level world, LivingEntity owner, double damage, KnockBacks knockback, boolean isVertical, Vec3 direction) {
        this(SlashExpansionEntities.GIANT_SLASH.get(), world);
        this.owner = owner;
        this.damage = damage;
        this.knockback = knockback;
        this.isVertical = isVertical;
        this.directionVec = direction.normalize();
        Vec3 pos = owner.getEyePosition().add(directionVec.scale(2));
        this.setPos(pos.x, pos.y, pos.z);
        this.setYRot(owner.getYRot());
        this.setXRot(isVertical ? -90 : 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        ticks++;

        double speedPerTick = SlashExpansionConfig.Entities.GiantSlash.speedPerTick();
        Vec3 currentPos = this.position();
        Vec3 newPos = currentPos.add(directionVec.scale(speedPerTick));
        this.setPos(newPos.x, newPos.y, newPos.z);
        distanceTraveled += speedPerTick;

        dealDamage();

        cooldownMap.entrySet().removeIf(entry -> {
            int newCooldown = entry.getValue() - 1;
            if (newCooldown <= 0) {
                return true;
            }
            entry.setValue(newCooldown);
            return false;
        });

        if (distanceTraveled >= SlashExpansionConfig.Entities.GiantSlash.maxDistance()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        if (ticks > SlashExpansionConfig.Entities.GiantSlash.maxTicks()) {
            this.remove(RemovalReason.DISCARDED);
        }

        spawnServerParticles();
    }

    private void dealDamage() {
        if (owner == null) return;

        double reach = isVertical
                ? SlashExpansionConfig.Entities.GiantSlash.verticalHitRange()
                : SlashExpansionConfig.Entities.GiantSlash.horizontalHitRange();

        AABB aabb = this.getBoundingBox().inflate(reach);

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            e -> e != owner && e.isAlive() && !e.isSpectator()
                && e.distanceTo(this) <= reach
                && TargetSelector.test.test(owner, e)
        );

        if (targets.isEmpty()) return;

        var damageSource = new net.minecraft.world.damagesource.DamageSource(
            this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.GENERIC),
            owner
        );

        int cooldown = SlashExpansionConfig.Entities.GiantSlash.damageCooldown();

        for (LivingEntity target : targets) {
            if (cooldownMap.containsKey(target)) {
                continue;
            }

            target.invulnerableTime = 0;
            target.hurt(damageSource, (float) this.damage);
            knockback.action.accept(target);

            cooldownMap.put(target, cooldown);
        }
    }

    private void spawnServerParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        Vec3 pos = this.position();

        if (isVertical) {
            // 竖直刃光：一条细白芯 + 一圈外发光
            Vfx.line(serverLevel, pos.add(0.0, -6.0, 0.0), pos.add(0.0, 6.0, 0.0),
                    Vfx.GOLD, 1.1F, 4, 1.3D);
            Vfx.line(serverLevel, pos.add(0.0, -6.0, 0.0), pos.add(0.0, 6.0, 0.0),
                    Vfx.WHITE, 0.35F, 5, 0.75D);
            if (ticks % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                        pos.x, pos.y, pos.z, 5, 0.5, 0.5, 0.5, 0.1);
            }
            if (ticks % 3 == 0) {
                Vfx.play(serverLevel, Vfx.halo(pos, directionVec, 0.3D, 2.4D, Vfx.GOLD, 9, 4, 0.3F, 6));
            }
        } else {
            // 水平刃光
            Vec3 right = new Vec3(0, 1, 0).cross(directionVec).normalize();
            if (right.lengthSqr() < 0.001) {
                right = new Vec3(1, 0, 0);
            }
            Vfx.line(serverLevel, pos.subtract(right.scale(8.0D)), pos.add(right.scale(8.0D)),
                    Vfx.GOLD, 1.1F, 4, 1.3D);
            Vfx.line(serverLevel, pos.subtract(right.scale(8.0D)), pos.add(right.scale(8.0D)),
                    Vfx.WHITE, 0.35F, 5, 0.75D);
            if (ticks % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        pos.x, pos.y + 0.5, pos.z, 3, 1.2, 0.4, 1.2, 0.05);
            }
        }
    }

    @Override
    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public Entity getShooter() {
        return this.owner;
    }

    @Override
    public void setShooter(Entity shooter) {
        if (shooter instanceof LivingEntity) {
            this.owner = (LivingEntity) shooter;
        }
    }

    public Vec3 getDirectionVec() {
        return directionVec;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void shoot(double arg0, double arg1, double arg2, float arg3, float arg4) {
        throw new UnsupportedOperationException("Unimplemented method 'shoot'");
    }
}
