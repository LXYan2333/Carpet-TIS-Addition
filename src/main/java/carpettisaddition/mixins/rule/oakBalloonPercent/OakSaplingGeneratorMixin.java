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

package carpettisaddition.mixins.rule.oakBalloonPercent;

import carpettisaddition.CarpetTISAdditionSettings;
import net.minecraft.block.sapling.OakSaplingGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC >= 11900
//$$ import net.minecraft.util.math.random.Random;
//#else
import java.util.Random;
//#endif

@Mixin(OakSaplingGenerator.class)
public abstract class OakSaplingGeneratorMixin
{
	@Redirect(
			//#if MC >= 11700
			//$$ method = "getTreeFeature",
			//#else
			method = "createTreeFeature",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11900
					//$$ target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"
					//#else
					target = "Ljava/util/Random;nextInt(I)I"
					//#endif
			)
	)
	private int oakBalloonPercent_modifyRandomResult(Random random, int bound)
	{
		if (CarpetTISAdditionSettings.oakBalloonPercent > 0)
		{
			boolean balloon = random.nextInt(100) < CarpetTISAdditionSettings.oakBalloonPercent;
			return balloon ? 0 : 1;
		}
		else
		{
			// vanilla
			return random.nextInt(bound);
		}
	}
}
