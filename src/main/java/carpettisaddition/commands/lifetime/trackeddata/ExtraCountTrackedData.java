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

package carpettisaddition.commands.lifetime.trackeddata;

import carpettisaddition.commands.lifetime.removal.RemovalReason;
import carpettisaddition.commands.lifetime.spawning.SpawningReason;
import carpettisaddition.commands.lifetime.utils.LifeTimeTrackerContext;
import carpettisaddition.utils.CommandUtil;
import carpettisaddition.utils.CounterUtil;
import carpettisaddition.utils.Messenger;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.text.BaseText;

import java.util.Map;

public abstract class ExtraCountTrackedData extends BasicTrackedData
{
	public final Map<SpawningReason, Long> spawningExtraCountMap = Maps.newHashMap();
	public final Map<RemovalReason, Long> removalExtraCountMap = Maps.newHashMap();

	protected abstract long getExtraCount(Entity entity);

	@Override
	public void updateSpawning(Entity entity, SpawningReason reason)
	{
		super.updateSpawning(entity, reason);
		this.spawningExtraCountMap.put(reason, this.spawningExtraCountMap.getOrDefault(reason, 0L) + this.getExtraCount(entity));
	}

	@Override
	public void updateRemoval(Entity entity, RemovalReason reason)
	{
		super.updateRemoval(entity, reason);
		this.removalExtraCountMap.put(reason, this.removalExtraCountMap.getOrDefault(reason, 0L) + this.getExtraCount(entity));
	}

	protected abstract BaseText getCountDisplayText();

	private BaseText attachExtraCountHoverText(BaseText text, long extraCount, long ticks)
	{
		BaseText extra = Messenger.c(
				this.getCountDisplayText(),
				"g : ",
				CounterUtil.ratePerHourText(extraCount, ticks, "wgg")
		);
		Messenger.hover(text, extra);

		// console cannot display hover text, so we append the extra count text to the end of the line
		if (CommandUtil.isConsoleCommandSource(LifeTimeTrackerContext.commandSource.get()))
		{
			text = Messenger.c(text, "g  [", extra, "g ]");
		}
		return text;
	}

	@Override
	public BaseText getSpawningCountText(long ticks)
	{
		return this.attachExtraCountHoverText(super.getSpawningCountText(ticks), getLongMapSum(this.spawningExtraCountMap), ticks);
	}

	@Override
	public BaseText getRemovalCountText(long ticks)
	{
		return this.attachExtraCountHoverText(super.getRemovalCountText(ticks), getLongMapSum(this.removalExtraCountMap), ticks);
	}

	@Override
	protected BaseText getSpawningReasonWithRate(SpawningReason reason, long ticks, long count, long total)
	{
		return this.attachExtraCountHoverText(super.getSpawningReasonWithRate(reason, ticks, count, total), this.spawningExtraCountMap.getOrDefault(reason, 0L), ticks);
	}

	@Override
	protected BaseText getRemovalReasonWithRate(RemovalReason reason, long ticks, long count, long total)
	{
		return this.attachExtraCountHoverText(super.getRemovalReasonWithRate(reason, ticks, count, total), this.removalExtraCountMap.getOrDefault(reason, 0L), ticks);
	}
}
