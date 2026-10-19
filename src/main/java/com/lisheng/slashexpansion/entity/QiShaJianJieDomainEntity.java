package com.lisheng.slashexpansion.entity;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Holder;

import java.util.List;

/**
 * 七杀剑界领域实体，数值来自 {@code [sa.qi_sha_jian_jie]}。
 */
public class QiShaJianJieDomainEntity extends Entity {

    private LivingEntity owner;
    private double damage;
    private int radius;
    private int tickCounter = 0;
    private int hitCount = 0;

    public QiShaJianJieDomainEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    public QiShaJianJieDomainEntity(Level world, LivingEntity owner, double damage, int radius) {
        this(SlashExpansionEntities.QI_SHA_JIAN_JIE_DOMAIN.get(), world);
        this.owner = owner;
        this.damage = damage;
        this.radius = radius;
        int spawnDistance = SlashExpansionConfig.Sa.QiShaJianJie.spawnDistance();
        setPos(owner.getX() + owner.getLookAngle().x * spawnDistance,
               owner.getY() + 1,
               owner.getZ() + owner.getLookAngle().z * spawnDistance);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            for (int i = 0; i < 3; i++) {
                double angle = this.tickCount * 0.1 + i * 2.0;
                double x = this.getX() + Math.cos(angle) * radius;
                double z = this.getZ() + Math.sin(angle) * radius;
                this.level().addParticle(ParticleTypes.END_ROD, x, this.getY() + 0.5, z, 0, 0.1, 0);
                this.level().addParticle(ParticleTypes.ENCHANT, x, this.getY() + 1, z, 0, 0.2, 0);
            }
            return;
        }

        int maxHits = SlashExpansionConfig.Sa.QiShaJianJie.maxHits();
        int interval = Math.max(1, SlashExpansionConfig.Sa.QiShaJianJie.hitInterval());

        tickCounter++;
        if (tickCounter % interval == 0) {
            if (hitCount >= maxHits) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            AABB aabb = new AABB(
                this.getX() - radius, this.getY() - radius / 2, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius / 2, this.getZ() + radius
            );

            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != owner && e.isAlive() && !e.isSpectator()
                    && e.distanceTo(this) <= radius
                    && TargetSelector.test.test(owner, e)
            );

            if (!targets.isEmpty()) {
                @SuppressWarnings("unchecked")
                Holder<DamageType>[] damageTypeHolders = new Holder[] {
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.GENERIC),
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.FALL),
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.LIGHTNING_BOLT),
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.IN_FIRE),
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.WITHER),
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.MAGIC),
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.ARROW)
                };

                Holder<DamageType> type = damageTypeHolders[hitCount % damageTypeHolders.length];
                DamageSource source = new DamageSource(type, owner);

                for (LivingEntity target : targets) {
                    target.invulnerableTime = 0;
                    target.hurt(source, (float) damage);
                }

                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        1, 0, 0, 0, 0);
                }
                hitCount++;
            }
        }

        int leash = SlashExpansionConfig.Sa.QiShaJianJie.ownerLeashDistance();
        if (owner == null || !owner.isAlive() || owner.distanceTo(this) > leash) {
            this.remove(RemovalReason.DISCARDED);
        }
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
}
