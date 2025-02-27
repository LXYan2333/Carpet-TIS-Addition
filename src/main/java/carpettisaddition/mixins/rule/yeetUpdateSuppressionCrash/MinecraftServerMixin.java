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

package carpettisaddition.mixins.rule.yeetUpdateSuppressionCrash;

import carpettisaddition.CarpetTISAdditionSettings;
import carpettisaddition.helpers.rule.yeetUpdateSuppressionCrash.UpdateSuppressionException;
import carpettisaddition.utils.ModIds;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Restriction(
		require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.17"),
		conflict = @Condition(value = ModIds.carpet_extra, versionPredicates = ">=1.4.14 <=1.4.43")
)
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
	@Redirect(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V"
			)
	)
	private void yeetUpdateSuppressionCrash_implOnTickWorlds(ServerWorld serverWorld, BooleanSupplier shouldKeepTicking)
	{
		if (CarpetTISAdditionSettings.yeetUpdateSuppressionCrash)
		{
			try
			{
				serverWorld.tick(shouldKeepTicking);
			}
			catch (CrashException e)
			{
				if (!(e.getCause() instanceof UpdateSuppressionException))
				{
					throw e;
				}
				UpdateSuppressionException.report((UpdateSuppressionException)e.getCause());
			}
			catch (UpdateSuppressionException e)
			{
				UpdateSuppressionException.report(e);
			}
		}
		else
		{
			// vanilla
			serverWorld.tick(shouldKeepTicking);
		}
	}
}
