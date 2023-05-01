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
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 11600
//$$ import net.minecraft.world.gen.StructureAccessor;
//$$ import net.minecraft.world.gen.chunk.ChunkGenerator;
//$$ import net.minecraft.world.biome.SpawnSettings;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#elseif MC < 11500
//$$ import net.minecraft.world.World;
//#endif


@Mixin(value = SpawnHelper.class)
public abstract class SpawnHelperMixin {
    @Redirect(
            //#if MC < 11500
            //$$ method = "spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
            //#elseif MC >= 11600
            //$$ method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
            //#else
            method = "spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 11600
                    //$$ target = "Lnet/minecraft/server/world/ServerWorld;getClosestPlayer(DDDDZ)Lnet/minecraft/entity/player/PlayerEntity;"
                    //#elseif MC < 11500
                    //$$ target = "Lnet/minecraft/world/World;getClosestPlayer(DDD)Lnet/minecraft/entity/player/PlayerEntity;"
                    //#else
                    target = "Lnet/minecraft/server/world/ServerWorld;getClosestPlayer(DDD)Lnet/minecraft/entity/player/PlayerEntity;"
                    //#endif
            )
    )
    private static PlayerEntity getClosestPlayer(
            //#if MC >= 11600
            //$$ ServerWorld world, double x, double y, double z, double maxDistance, boolean ignoreCreative
            //#elseif MC < 11500
            //$$ World world, double x, double z, double maxDistance
            //#else
            ServerWorld world, double x, double z, double maxDistance
            //#endif
    ) {
        if (CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer) {
            //#if MC >= 11600
            //$$ return AlgorithmIgnorePlayer.getClosestPlayer(world, x, y, z);
            //#elseif MC < 11500
            //$$ return AlgorithmIgnorePlayer.getHorizontallyClosestPlayer((ServerWorld)world, x, z);
            //#else
            return AlgorithmIgnorePlayer.getHorizontallyClosestPlayer(world, x, z);
            //#endif
        }
        //#if MC >= 11600
        //$$ return world.getClosestPlayer(x, y, z, maxDistance, ignoreCreative);
        //#else
        return world.getClosestPlayer(x, z, -1);
        //#endif
    }




    @Inject(
            //#if MC >= 11600
            //$$ method = "canSpawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/SpawnSettings$SpawnEntry;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z",
            //#elseif MC < 11500
            //$$ method = "spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
            //#else
            method = "spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC < 11500
                    //$$ target = "Lnet/minecraft/world/World;doesNotCollide(Lnet/minecraft/util/math/Box;)Z"
                    //#else
                    target = "Lnet/minecraft/server/world/ServerWorld;doesNotCollide(Lnet/minecraft/util/math/Box;)Z"
                    //#endif
            )
    )
    private static void changeState(
            //#if MC >= 11600
            //$$ ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir
            //#elseif MC < 11500
            //$$ EntityCategory category, World world, WorldChunk chunk, BlockPos spawnPos, CallbackInfo ci
            //#else
            EntityCategory category, ServerWorld serverWorld, WorldChunk chunk, BlockPos spawnPos, CallbackInfo ci
            //#endif
    ) {
        IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.set(true);
    }

//    /**
//     * @author LXYan
//     * @reason Copy from IWorldMixin
//     */
//    @Overwrite
//    public static void spawnEntitiesInChunk(EntityCategory category, ServerWorld serverWorld, WorldChunk chunk, BlockPos spawnPos){
//        try {
//            if (CarpetTISAdditionSettings.spawnAlgorithmIgnorePlayer){
//                IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.set(true);
//            }
//            SpawnHelper.spawnEntitiesInChunk(category, serverWorld, chunk, spawnPos);
//        }finally {
//            IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.set(false);
//        }
//    }

    @Inject(
            //#if MC >= 11600
            //$$ method = "canSpawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/SpawnSettings$SpawnEntry;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z",
            //#elseif MC < 11500
            //$$ method = "spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
            //#else
            method = "spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
            //#endif
            at = @At(
                    value = "RETURN"
            )
    )
    private static void changeStateBack(
            //#if MC >= 11600
            //$$ ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir
            //#elseif MC < 11500
            //$$ EntityCategory category, World world, WorldChunk chunk, BlockPos spawnPos, CallbackInfo ci
            //#else
            EntityCategory category, ServerWorld serverWorld, WorldChunk chunk, BlockPos spawnPos, CallbackInfo ci
            //#endif
    ) {

        IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.set(false);
    }

//    @Redirect(
//            method = "spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/entity/mob/MobEntity;canImmediatelyDespawn(D)Z"
//            )
//    )
//    private static boolean beforeCreateEntity(MobEntity mobEntity, double distanceSquared) {
//        System.out.println("##################  new try   ################");
//        System.out.println("isLagFreeSpawning: " + CarpetSettings.lagFreeSpawning);
//        System.out.println("tack_spawn         "+SpawnReporter.track_spawns);
//        System.out.println("local_spawn        "+SpawnReporter.local_spawns != null);
//        System.out.println("mock_spawn         "+SpawnReporter.mock_spawns);
//        System.out.println("d=                 "+distanceSquared);
//        System.out.println("mobEntity.canImmediatelyDespawn     "+mobEntity.canImmediatelyDespawn(distanceSquared));
////        System.out.println("Try to create a Mob!");
//        return true;
//    }
//
//    @Redirect(
//            method ="spawnEntitiesInChunk(Lnet/minecraft/entity/EntityCategory;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/util/math/BlockPos;)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/entity/mob/MobEntity;canSpawn(Lnet/minecraft/world/IWorld;Lnet/minecraft/entity/SpawnType;)Z"
//            )
//    )
//    private static boolean before2(MobEntity mobEntity, IWorld serverWorld, SpawnType spawnType){
//        System.out.println("!mobEntity.canSpawn                 "+!mobEntity.canSpawn(serverWorld, SpawnType.NATURAL));
//        System.out.println("!mobEntity.canSpawn                 "+!mobEntity.canSpawn(serverWorld));
//        return  true;
//    }
}
