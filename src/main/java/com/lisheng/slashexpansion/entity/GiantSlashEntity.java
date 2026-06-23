package com.lisheng.slashexpansion.entity;

import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
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

public class GiantSlashEntity extends Entity implements IShootable {

    private LivingEntity owner;
    private double damage;
    private KnockBacks knockback;
    private boolean isVertical;
    private int maxDistance = 64;
    private double distanceTraveled = 0;
    private int ticks = 0;
    private Vec3 directionVec;
    private static final int MAX_TICKS = 36;
    private static final double SPEED_PER_TICK = 2.0;
    private static final int DAMAGE_COOLDOWN = 3;

    private final Map<Entity, Integer> cooldownMap = new HashMap<>();

    private static final double ATTACK_RANGE_VERTICAL = 6.0;
    private static final double ATTACK_RANGE_HORIZONTAL = 8.0;

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

        Vec3 currentPos = this.position();
        Vec3 newPos = currentPos.add(directionVec.scale(SPEED_PER_TICK));
        this.setPos(newPos.x, newPos.y, newPos.z);
        distanceTraveled += SPEED_PER_TICK;

        dealDamage();

        cooldownMap.entrySet().removeIf(entry -> {
            int newCooldown = entry.getValue() - 1;
            if (newCooldown <= 0) {
                return true;
            }
            entry.setValue(newCooldown);
            return false;
        });

        if (distanceTraveled >= maxDistance) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        if (ticks > MAX_TICKS) {
            this.remove(RemovalReason.DISCARDED);
        }

        spawnServerParticles();
    }

    private void dealDamage() {
        if (owner == null) return;

        double reach = isVertical ? ATTACK_RANGE_VERTICAL : ATTACK_RANGE_HORIZONTAL;

        AABB aabb = this.getBoundingBox().inflate(reach);
        
        // ★★★ 使用 TargetSelector.test 过滤目标 ★★★
        List<LivingEntity> targets = this.level().getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            e -> e != owner && e.isAlive() && !e.isSpectator()
                && e.distanceTo(this) <= reach
                && TargetSelector.test.test(owner, e) // ★ 关键：只攻击可攻击目标
        );

        if (targets.isEmpty()) return;

        var damageSource = new net.minecraft.world.damagesource.DamageSource(
            this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.GENERIC),
            owner
        );

        for (LivingEntity target : targets) {
            if (cooldownMap.containsKey(target)) {
                continue;
            }

            target.invulnerableTime = 0;
            target.hurt(damageSource, (float) this.damage);
            knockback.action.accept(target);

            cooldownMap.put(target, DAMAGE_COOLDOWN);
        }
    }

    private void spawnServerParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        if (isVertical) {
            for (double dy = -6; dy <= 6; dy += 0.5) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + dy, this.getZ(),
                    1, 0.05, 0.05, 0.05, 0.0);
            }
            if (ticks % 3 == 0) {
                serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    this.getX(), this.getY(), this.getZ(),
                    5, 0.5, 0.5, 0.5, 0.1);
            }
        } else {
            Vec3 right = new Vec3(0, 1, 0).cross(directionVec).normalize();
            if (right.lengthSqr() < 0.001) {
                right = new Vec3(1, 0, 0);
            }
            for (double dw = -8; dw <= 8; dw += 0.5) {
                Vec3 spreadPos = this.position().add(right.scale(dw));
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    spreadPos.x, spreadPos.y + 0.5, spreadPos.z,
                    1, 0.05, 0.05, 0.05, 0.0);
            }
            if (ticks % 3 == 0) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    5, 1.5, 0.5, 1.5, 0.1);
            }
        }
    }

    // IShootable 接口实现
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shoot'");
    }
}