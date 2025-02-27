/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Carpet TIS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet TIS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet TIS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package carpettisaddition.mixins.command.lifetime.removal.conversion;

import carpettisaddition.commands.lifetime.interfaces.LifetimeTrackerTarget;
import carpettisaddition.commands.lifetime.removal.MobConversionRemovalReason;
import carpettisaddition.utils.ModIds;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.16"))
@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity
{
	protected ZombieEntityMixin(EntityType<? extends HostileEntity> type, World world)
	{
		super(type, world);
	}

	@ModifyArg(
			method = "convertTo",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
			)
	)
	private Entity recordSelfRemoval$LifeTimeTracker(Entity zombieVariant)
	{
		((LifetimeTrackerTarget)this).recordRemoval(new MobConversionRemovalReason(zombieVariant.getType()));
		return zombieVariant;
	}

	@Inject(
			method = "onKilledOther",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/passive/VillagerEntity;remove()V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void recordVillagerRemoval$LifeTimeTracker(LivingEntity other, CallbackInfo ci, VillagerEntity villagerEntity, ZombieVillagerEntity zombieVillagerEntity)
	{
		((LifetimeTrackerTarget)villagerEntity).recordRemoval(new MobConversionRemovalReason(zombieVillagerEntity.getType()));
	}
}
