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

package carpettisaddition.helpers.rule.spawnAlgorithmIgnorePlayer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class AlgorithmIgnorePlayer {
    public static boolean isPlayerInRange(World world, double x, double y, double z, double range) {
        for (PlayerEntity playerEntity : world.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) || !EntityPredicates.VALID_ENTITY_LIVING.test(playerEntity)) {
                continue;
            }

            if (shouldIgnore(playerEntity)) {
                continue;
            }

            double d = playerEntity.squaredDistanceTo(x, y, z);
            if (!(range < 0.0) && !(d < range * range)) continue;
            return true;
        }
        return false;
    }

    public static PlayerEntity getHorizontallyClosestPlayer(ServerWorld world, double x, double z) {
        PlayerEntity closestValidPlayer = null;
        double closestPlayerDistance = Double.MAX_VALUE;
        for (PlayerEntity player : world.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(player)) {
                continue;
            }
            if (shouldIgnore(player)) {
                continue;
            }
            if (closestValidPlayer == null) {
                closestValidPlayer = player;
                closestPlayerDistance = player.squaredDistanceTo(x,
                        //#if MC<11500
                        //$$ player.y,
                        //#else
                        player.getY(),
                        //#endif
                        z);
                continue;
            }
            double currentDistance = player.squaredDistanceTo(x,
                    //#if MC<11500
                    //$$ player.y,
                    //#else
                    player.getY(),
                    //#endif
                    z);
            if (currentDistance < closestPlayerDistance) {
                closestValidPlayer = player;
                closestPlayerDistance = currentDistance;
            }
        }
        return closestValidPlayer;
    }

    public static PlayerEntity getClosestPlayer(ServerWorld world, double x, double y, double z) {
        PlayerEntity closestValidPlayer = null;
        double closestPlayerDistance = Double.MAX_VALUE;
        for (PlayerEntity player : world.getPlayers()) {
            if (player.isSpectator()) {
                continue;
            }
            if (shouldIgnore(player)) {
                continue;
            }
            if (closestValidPlayer == null) {
                closestValidPlayer = player;
                closestPlayerDistance = player.squaredDistanceTo(x, y, z);
                continue;
            }
            double currentDistance = player.squaredDistanceTo(x, y, z);
            if (currentDistance < closestPlayerDistance) {
                closestValidPlayer = player;
                closestPlayerDistance = currentDistance;
            }
        }
        return closestValidPlayer;
    }

    public static ServerPlayerEntity getRandomAlivePlayer(net.minecraft.server.world.ServerWorld serverWorld) {
        List<ServerPlayerEntity> list = serverWorld.getPlayers(LivingEntity::isAlive);
        list.removeIf(AlgorithmIgnorePlayer::shouldIgnore);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(serverWorld.random.nextInt(list.size()));
    }

    public static Stream<ServerPlayerEntity> shouldIgnorePlayers(ServerWorld world) {
        return world.getPlayers()
                .stream()
                .filter(AlgorithmIgnorePlayer::shouldIgnore);
    }

    public static boolean shouldIgnore(Entity player) {
        Team ignoreTeam = player.getServer().getScoreboard().getTeam("spawnAlgIgnore");

        if (ignoreTeam != null) {
            return player.isTeamPlayer(ignoreTeam);
        } else {
            player.getServer().getScoreboard().addTeam("spawnAlgIgnore");
        }
        return false;
    }

    public static boolean shouldNotIgnore(Entity player) {
        if (IsSpaceEmptyHelper.isCalledFromSpawnEntitiesInChunk.get() &&shouldIgnore(player)){
            System.out.println("Ignore: " + player);
        }
        return !shouldIgnore(player);
    }

    public static boolean ignoreList(Set<Entity> set, List<Entity> list) {
        return list.addAll(set);
    }
}
