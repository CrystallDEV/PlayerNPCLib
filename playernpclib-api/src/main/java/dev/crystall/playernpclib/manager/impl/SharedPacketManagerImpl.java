package dev.crystall.playernpclib.manager.impl;


import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ANIMATION;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO_REMOVE;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.util.Vector;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerAnimation;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityDestroy;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityEquipment;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityHeadRotation;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityMetadata;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityTeleport;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerNamedEntitySpawn;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerPlayerInfo;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerPlayerInfoRemove;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerScoreboardTeam;
import dev.crystall.playernpclib.api.wrapper.MinecraftVersions;
import dev.crystall.playernpclib.api.wrapper.TeamMode;
import dev.crystall.playernpclib.api.wrapper.WrapperGenerator;
import dev.crystall.playernpclib.api.wrapper.impl.WrapperPlayServerSpawnEntity;
import dev.crystall.playernpclib.manager.PacketManager;
import java.util.Arrays;
import java.util.Collections;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.OptionStatus;

/**
 * Shared PacketManager implementation for all Minecraft versions.
 * Uses version-aware logic for protocol differences.
 *
 * Created by CrystallDEV on 01/09/2020
 * Consolidated for multi-version support on 2025-10-07
 */
@Slf4j
@NoArgsConstructor
public class SharedPacketManagerImpl implements PacketManager {

  @Override
  public void sendScoreBoardTeamPacket(Player player, BasePlayerNPC npc) {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null || !version.hasCompatibleScoreboardTeam()) {
      // SCOREBOARD_TEAM incompatible with MC 1.21.7
      // Packet structure changed: Strings array only has 1 element (team name)
      // Nametag visibility/collision rule moved to Parameters object
      // ProtocolLib 5.4.0 cannot access Parameters (Issues #3053, #3147, #2973)
      return;
    }

    BaseWrapperPlayServerScoreboardTeam wrapperTeam = WrapperGenerator.map(
      BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM);
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.PLAYERS_ADDED);
    wrapperTeam.setPlayers(Collections.singletonList(npc.getInternalName()));
    sendPacket(player, wrapperTeam.getHandle(), false);
  }

  @Override
  public void sendScoreBoardTeamCreatePacket(Player player) {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null || !version.hasCompatibleScoreboardTeam()) {
      // SCOREBOARD_TEAM incompatible with MC 1.21.7
      // Packet structure changed: Strings array only has 1 element (team name)
      // Nametag visibility/collision rule moved to Parameters object
      // ProtocolLib 5.4.0 cannot access Parameters (Issues #3053, #3147, #2973)
      return;
    }

    BaseWrapperPlayServerScoreboardTeam wrapperTeam = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM);
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.TEAM_CREATED);
    wrapperTeam.setNameTagVisibility(OptionStatus.ALWAYS.toString());
    wrapperTeam.setPlayers(Collections.emptyList());
    sendPacket(player, wrapperTeam.getHandle(), false);
  }


  @Override
  public void sendNPCCreatePackets(Player player, BasePlayerNPC npc) {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null) {
      log.error("Cannot send NPC create packets: NMS version not detected");
      return;
    }

    // Add entity to player list
    sendPlayerInfoPacket(player, npc, PlayerInfoAction.ADD_PLAYER);

    // Spawn entity - version-aware
    if (version.hasNamedEntitySpawn()) {
      // Legacy (MC ≤1.20.1): Use NAMED_ENTITY_SPAWN
      BaseWrapperPlayServerNamedEntitySpawn spawnWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN);
      spawnWrapper.setEntityID(npc.getEntityId());
      spawnWrapper.setPlayerUUID(npc.getUuid());
      spawnWrapper.setPosition(npc.getLocation().toVector());
      spawnWrapper.setPitch(npc.getLocation().getPitch());
      spawnWrapper.setYaw(npc.getLocation().getYaw());
      sendPacket(player, spawnWrapper.getHandle(), false);
    } else {
      // Modern (MC 1.20.2+): Use SPAWN_ENTITY
      WrapperPlayServerSpawnEntity spawnWrapper = new WrapperPlayServerSpawnEntity();
      spawnWrapper.setEntityID(npc.getEntityId());
      spawnWrapper.setUniqueId(npc.getUuid());
      spawnWrapper.setType(EntityType.PLAYER);
      spawnWrapper.setX(npc.getLocation().getX());
      spawnWrapper.setY(npc.getLocation().getY());
      spawnWrapper.setZ(npc.getLocation().getZ());
      spawnWrapper.setPitch(npc.getLocation().getPitch());
      spawnWrapper.setYaw(npc.getLocation().getYaw());
      spawnWrapper.setObjectData(0);  // 0 for player entities
      sendPacket(player, spawnWrapper.getHandle(), false);
    }

    sendHeadRotationPacket(player, npc);

    Bukkit.getScheduler().runTaskLater(PlayerNPCLib.getPlugin(), () -> sendPlayerInfoPacketRemove(player, npc), 20L);
  }


  @Override
  public void sendMovePacket(Player player, BasePlayerNPC npc) {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null) {
      log.error("Cannot send move packet: NMS version not detected");
      return;
    }

    PacketContainer packet;

    if (version.hasModernEntityTeleport()) {
      // Modern structure (MC 1.21.3+) - ProtocolLib Issue #3341
      packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);

      // Entity ID
      packet.getIntegers().write(0, npc.getEntityId());

      // Access InternalStructure (PositionMoveRotation)
      var structures = packet.getStructures().getValues();
      if (!structures.isEmpty()) {
        var positionMoveRotation = structures.get(0);

        // Position vector (X, Y, Z)
        positionMoveRotation.getVectors().write(0, new Vector(
            npc.getLocation().getX(),
            npc.getLocation().getY(),
            npc.getLocation().getZ()
        ));

        // Delta vector (0, 0, 0 for teleportation)
        positionMoveRotation.getVectors().write(1, new Vector(0, 0, 0));

        // Rotation as Floats (direct degrees, no conversion)
        positionMoveRotation.getFloat().write(0, npc.getLocation().getYaw());
        positionMoveRotation.getFloat().write(1, npc.getLocation().getPitch());
      }

      // OnGround (outside InternalStructure)
      packet.getBooleans().write(0, true);

    } else {
      // Legacy structure (MC ≤1.21.2)
      BaseWrapperPlayServerEntityTeleport moveWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT);
      moveWrapper.setEntityID(npc.getEntityId());
      moveWrapper.setX(npc.getLocation().getX());
      moveWrapper.setY(npc.getLocation().getY());
      moveWrapper.setZ(npc.getLocation().getZ());
      moveWrapper.setYaw(npc.getLocation().getYaw());
      moveWrapper.setPitch(npc.getLocation().getPitch());
      packet = moveWrapper.getHandle();
    }

    sendPacket(player, packet, false);
    sendHeadRotationPacket(player, npc);
  }


  @Override
  public void sendHidePackets(Player player, BasePlayerNPC npc) {
    // Remove entity
    BaseWrapperPlayServerEntityDestroy spawnWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY);
    spawnWrapper.setEntityIds(new int[]{npc.getEntityId()});
    sendPacket(player, spawnWrapper.getHandle(), false);

    // Remove player from tab list if it's still on there
    sendPlayerInfoPacketRemove(player, npc);
  }


  @Override
  public void sendHeadRotationPacket(Player player, BasePlayerNPC npc) {
    // Head rotation
    if (npc.getEyeLocation() != null) {
      BaseWrapperPlayServerEntityHeadRotation headWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION);
      headWrapper.setEntityID(npc.getEntityId());
      headWrapper.setHeadYaw((byte) ((npc.getEyeLocation().getYaw() % 360.0F) * 256.0F / 360.0F));
      sendPacket(player, headWrapper.getHandle(), false);
    }
  }

  @Override
  public void sendPlayerInfoPacket(Player player, BasePlayerNPC npc, PlayerInfoAction action) {
    BaseWrapperPlayServerPlayerInfo infoWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO);
    PlayerInfoData data = new PlayerInfoData(npc.getGameProfile().getUUID(), 1, false, NativeGameMode.SURVIVAL, npc.getGameProfile(),
      WrappedChatComponent.fromText(npc.getDisplayName()));
    infoWrapper.setData(Collections.singletonList(data));
    infoWrapper.setActions(Collections.singleton(action));
    sendPacket(player, infoWrapper.getHandle(), false);
  }

  @Override
  public void sendPlayerInfoPacketRemove(Player player, BasePlayerNPC npc) {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null) {
      log.error("Cannot send player info remove packet: NMS version not detected");
      return;
    }

    // VERSION-AWARE: PLAYER_INFO_REMOVE only exists in 1.20+
    if (!version.hasPlayerInfoRemove()) {
      // Legacy versions: Use PLAYER_INFO with REMOVE_PLAYER action
      sendPlayerInfoPacket(player, npc, PlayerInfoAction.REMOVE_PLAYER);
      return;
    }

    // Modern implementation (v1_20+)
    BaseWrapperPlayServerPlayerInfoRemove wrapperPlayServerPlayerInfoRemove = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO_REMOVE);
    wrapperPlayServerPlayerInfoRemove.addPlayerId(npc.getGameProfile().getUUID());
    sendPacket(player, wrapperPlayServerPlayerInfoRemove.getHandle(), false);
  }

  @Override
  public void sendEquipmentPackets(Player player, BasePlayerNPC npc) {
    BaseWrapperPlayServerEntityEquipment wrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT);
    wrapper.setEntityID(npc.getEntityId());
    wrapper.SetSlotStackPairLists(Arrays.asList(
      new Pair<>(ItemSlot.MAINHAND, npc.getItemSlots().get(ItemSlot.MAINHAND)),
      new Pair<>(ItemSlot.OFFHAND, npc.getItemSlots().get(ItemSlot.OFFHAND)),
      new Pair<>(ItemSlot.FEET, npc.getItemSlots().get(ItemSlot.FEET)),
      new Pair<>(ItemSlot.LEGS, npc.getItemSlots().get(ItemSlot.LEGS)),
      new Pair<>(ItemSlot.CHEST, npc.getItemSlots().get(ItemSlot.CHEST)),
      new Pair<>(ItemSlot.HEAD, npc.getItemSlots().get(ItemSlot.HEAD))
    ));
    sendPacket(player, wrapper.getHandle(), false);
  }

  @Override
  public void sendAnimationPacket(Player player, BasePlayerNPC npc, int animationID) {
    BaseWrapperPlayServerAnimation animationWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ANIMATION);
    animationWrapper.setEntityID(npc.getEntityId());
    animationWrapper.setAnimation(animationID);
    sendPacket(player, animationWrapper.getHandle(), false);

  }

  @Override
  public void sendDeathMetaData(Player player, BasePlayerNPC npc) {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null) {
      log.error("Cannot send death metadata: NMS version not detected");
      return;
    }

    // VERSION-AWARE: DataWatcher API changed in 1.20+
    // Only works for legacy versions (1.16-1.19)
    if (version.isModern()) {
      // Modern versions: DataWatcher API incompatible
      // Feature disabled until ProtocolLib provides compatible API
      return;
    }

    // Legacy implementation (v1_16-v1_19)
    BaseWrapperPlayServerEntityMetadata wrapperEntityMeta = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA);
    wrapperEntityMeta.setEntityID(npc.getEntityId());

    // Create the data watcher for this entity
    var watcher = WrappedDataWatcher.getEntityWatcher(player).deepClone();
    var obj = new WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()));
    watcher.setObject(obj, EntityPose.DYING.toNms());

    wrapperEntityMeta.setMetadata(watcher.getWatchableObjects());
    sendPacket(player, wrapperEntityMeta.getHandle(), false);
  }

  @Override
  public void sendPacket(Player player, PacketContainer packetContainer, boolean debug) {
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);

    if (debug) {
      PlayerNPCLib.getPlugin().getServer().getConsoleSender().sendMessage(
        "Sent packet " + packetContainer.getType().name() + " to " + player.getDisplayName()
      );
    }
  }

}
