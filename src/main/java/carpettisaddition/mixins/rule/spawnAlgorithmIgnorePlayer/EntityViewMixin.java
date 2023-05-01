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
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Stream;

//#if MC >= 11600
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import net.minecraft.util.math.Box;
//$$import java.util.function.Predicate;
//$$ import net.minecraft.predicate.entity.EntityPredicates;
//#else
import java.util.Set;
//#endif


@Mixin(EntityView.class)
public interface EntityViewMixin {

    @Redirect(
            //#if MC >= 11800
            //$$ method = "getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;",
            //#elseif MC >= 11600
            //$$ method = "getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/stream/Stream;",
            //#elseif MC < 11500
            //$$ method = "Lnet/minecraft/world/EntityView;method_20743(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/Set;)Ljava/util/stream/Stream;",
            //#else
            method = "getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/Set;)Ljava/util/stream/Stream;",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 11800
                    //$$ target = "Lnet/minecraft/world/EntityView;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"
                    //#else
                    target = "Ljava/util/List;stream()Ljava/util/stream/Stream;"
                    //#endif

            )
    )
    //#if MC >= 11800
    //$$ default List listIgnorePlayer(EntityView instance, Entity entity, Box box, Predicate<Entity> predicate) {
    //$$     if (IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.get() && CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
    //$$         return instance.getOtherEntities(entity,box, predicate.and(AlgorithmIgnorePlayer::shouldNotIgnore));
    //$$     }
    //$$     return instance.getOtherEntities(entity,box,predicate);
    //$$ }
    //#endif
    default Stream<Entity> streamIgnorePlayer(List<Entity> list) {
        if (IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.get() && CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
            return list.stream().filter(AlgorithmIgnorePlayer::shouldNotIgnore);
        }
        return list.stream();
    }


    //#if MC >= 11600
    //$$ @Shadow
    //$$ List<Entity> getOtherEntities(Entity par1, Box par2);
    //$$
    //$$ @Shadow
    //$$ List<Entity> getOtherEntities(Entity par1, Box par2, Predicate<? super Entity> par3);
    //$$ @Redirect(
    //$$         method = "intersectsEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/shape/VoxelShape;)Z",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/world/EntityView;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
    //$$         )
    //$$ )
    //$$ default List streamIgnorePlayer1(EntityView instance, Entity entity, Box box) {
    //$$     if (IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.get() && CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
    //$$         return this.getOtherEntities(entity,box, EntityPredicates.EXCEPT_SPECTATOR.and(AlgorithmIgnorePlayer::shouldNotIgnore));
    //$$     }
    //$$
    //$$     return getOtherEntities(entity, box);
    //$$ }
    //#else
    @Redirect(
            method = "intersectsEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/shape/VoxelShape;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;stream()Ljava/util/stream/Stream;"
            )
    )
    default Stream<Entity> streamIgnorePlayer1(List<Entity> list) {
        if (IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.get() && CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
            return list.stream().filter(AlgorithmIgnorePlayer::shouldNotIgnore);
        }
        return list.stream();
    }
    //#endif
}
