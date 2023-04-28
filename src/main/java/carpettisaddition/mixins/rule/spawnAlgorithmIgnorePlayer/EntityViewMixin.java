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


import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.EntityView;
import org.spongepowered.asm.mixin.Mixin;
import carpettisaddition.helpers.rule.spawnAlgorithmIgnorePlayer.AlgorithmIgnorePlayer;
import carpettisaddition.helpers.rule.spawnAlgorithmIgnorePlayer.IsSpaceEmptyHelper;
import carpettisaddition.CarpetTISAdditionSettings;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC >= 11600
//$$ import java.util.function.Predicate;
//#else
import java.util.Set;
//#endif


@Mixin(EntityView.class)
public interface EntityViewMixin {
    @ModifyVariable(
            //#if MC >= 11800
            //$$    method = "getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;",
            //#elseif MC >= 11600
            //$$    method = "getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/stream/Stream;",
            //#elseif MC < 11500
            //$$    method = "Lnet/minecraft/world/EntityView;method_20743(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/Set;)Ljava/util/stream/Stream;",
            //#else
            method = "getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/Set;)Ljava/util/stream/Stream;",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 11600
                    //$$ target = "Lnet/minecraft/world/EntityView;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"
                    //#else
                    target = "Lnet/minecraft/world/EntityView;getEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
                    //#endif
            )
    )
    //#if MC >= 11600
    //$$ default Predicate<Entity> predicateIgnorePlayer(Predicate<Entity> predicate) {
    //$$     if (IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.get() && CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
    //$$         return predicate.or(AlgorithmIgnorePlayer::shouldIgnore);
    //$$     }
    //$$     return predicate;
    //$$ }
    //#else
    default Set<Entity> predicateIgnorePlayer(Set<Entity> entities) {
        if (IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.get() && CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
            entities.addAll(AlgorithmIgnorePlayer.shouldIgnorePlayers((ServerWorld) this));
        }
        return entities;
    }
    //#endif
}
