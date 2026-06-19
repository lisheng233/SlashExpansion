package com.lisheng.slashexpansion.entity;

import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.entity.Projectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;

public class SenbonzakuraSwordEntity extends EntityAbstractSummonedSword {

    public SenbonzakuraSwordEntity(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setPierce((byte) 3);
        this.setNoGravity(true);
    }

    public static SenbonzakuraSwordEntity createInstance(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new SenbonzakuraSwordEntity(SlashExpansionEntities.SENBONZAKURA_SWORD.get(), worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        // 樱花粒子效果
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                this.getX(), this.getY(), this.getZ(),
                2, 0.2, 0.2, 0.2, 0.1);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // 樱花特效：命中时爆开樱花粒子
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                result.getEntity().getX(), result.getEntity().getY() + 1, result.getEntity().getZ(),
                30, 1.5, 1.5, 1.5, 0.2);
        }
        super.onHitEntity(result);
    }
}