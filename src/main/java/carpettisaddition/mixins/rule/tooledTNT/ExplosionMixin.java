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

package carpettisaddition.mixins.rule.tooledTNT;

import carpettisaddition.helpers.rule.tooledTNT.TooledTNTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public abstract class ExplosionMixin
{
	/**
	 * See {@link OptimizedExplosionMixin} for mixin at carpet in for this rule
	 */
	@Redirect(
			method = "affectWorld",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/item/ItemStack;EMPTY:Lnet/minecraft/item/ItemStack;"
			),
			//#if MC >= 11900
			//$$ allow = 2
			//#else
			allow = 1
			//#endif
	)
	private ItemStack useTheToolInYourHand()
	{
		return TooledTNTHelper.getMainHandItemOfCausingEntity((Explosion)(Object)this);
	}
}
