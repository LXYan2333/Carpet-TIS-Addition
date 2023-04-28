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

package carpettisaddition.mixins.rule.spawnAlgorithmIgnorePlayer;

import carpettisaddition.CarpetTISAdditionSettings;
import carpettisaddition.helpers.rule.spawnAlgorithmIgnorePlayer.AlgorithmIgnorePlayer;
import carpettisaddition.helpers.rule.spawnAlgorithmIgnorePlayer.IsSpaceEmptyHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin {
    @Redirect(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;isPlayerInRange(DDDD)Z"
            )
    )
    private boolean isPlayerInRange(World world, double x, double y, double z, double range) {
        if (CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
            return AlgorithmIgnorePlayer.isPlayerInRange(world, x, y, z, range);
        }
        return world.isPlayerInRange(x, y, z, range);
    }

    @Inject(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;doesNotCollide(Lnet/minecraft/entity/Entity;)Z"
            )
    )
    private void changeState(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.set(true);
    }

    @Inject(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;doesNotCollide(Lnet/minecraft/entity/Entity;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void changeStateBack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.set(false);
    }
}
