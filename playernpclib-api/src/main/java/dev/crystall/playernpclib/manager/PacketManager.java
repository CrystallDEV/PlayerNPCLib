package dev.crystall.playernpclib.manager;

import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ANIMATION;
import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY;
import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT;
import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION;
import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA;
import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT;
import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO;
import static dev.crystall.playernpclib.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerAnimation;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerEntityDestroy;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerEntityEquipment;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerEntityHeadRotation;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerEntityMetadata;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerEntityTeleport;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerNamedEntitySpawn;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerPlayerInfo;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerScoreboardTeam;
import dev.crystall.playernpclib.wrapper.TeamMode;
import dev.crystall.playernpclib.wrapper.WrapperGenerator;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class PacketManager {

  private PacketManager() {
  }

  public static void sendScoreBoardTeamPacket(Player player, BasePlayerNPC npc) {
    BaseWrapperPlayServerScoreboardTeam wrapperTeam = new WrapperGenerator<BaseWrapperPlayServerScoreboardTeam>(
      BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM).result;
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.PLAYERS_ADDED);
    wrapperTeam.setPlayers(Collections.singletonList(npc.getInternalName()));
    sendPacket(player, wrapperTeam.getHandle(), false);
  }

  public static void sendScoreBoardTeamCreatePacket(Player player) {
    BaseWrapperPlayServerScoreboardTeam wrapperTeam = new WrapperGenerator<BaseWrapperPlayServerScoreboardTeam>(
      BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM).result;
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.TEAM_CREATED);
    wrapperTeam.setNameTagVisibility("always");
    wrapperTeam.setPlayers(Collections.emptyList());
    sendPacket(player, wrapperTeam.getHandle(), false);
  }

  /**
   * Sends packets to the given player to create a custom npc
   *
   * @param player
   * @param npc
   */
  public static void sendNPCCreatePackets(Player player, BasePlayerNPC npc) {
    // Add entity to player list
    sendPlayerInfoPacket(player, npc, PlayerInfoAction.ADD_PLAYER);

    // Spawn entity
    BaseWrapperPlayServerNamedEntitySpawn spawnWrapper = new WrapperGenerator<BaseWrapperPlayServerNamedEntitySpawn>(
      BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM).result;
    spawnWrapper.setEntityID(npc.getEntityId());
    spawnWrapper.setPlayerUUID(npc.getUuid());
    spawnWrapper.setPosition(npc.getLocation().toVector());
    spawnWrapper.setPitch(npc.getLocation().getPitch());
    spawnWrapper.setYaw(npc.getLocation().getYaw());
    sendPacket(player, spawnWrapper.getHandle(), false);

    sendHeadRotationPacket(player, npc);

    Bukkit.getScheduler().runTaskLater(PlayerNPCLib.getPlugin(), () -> sendPlayerInfoPacket(player, npc, PlayerInfoAction.REMOVE_PLAYER), 20L);
  }

  /**
   * Sends position update packets to the given player
   *
   * @param player
   * @param npc
   */
  public static void sendMovePacket(Player player, BasePlayerNPC npc) {
    // Location update
    BaseWrapperPlayServerEntityTeleport moveWrapper = new WrapperGenerator<BaseWrapperPlayServerEntityTeleport>(
      BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT).result;
    moveWrapper.setEntityID(npc.getEntityId());
    moveWrapper.setX(npc.getLocation().getX());
    moveWrapper.setY(npc.getLocation().getY());
    moveWrapper.setZ(npc.getLocation().getZ());
    moveWrapper.setYaw(npc.getLocation().getYaw());
    moveWrapper.setPitch(npc.getLocation().getPitch());
    sendPacket(player, moveWrapper.getHandle(), false);

    sendHeadRotationPacket(player, npc);
  }

  /**
   * Sends packets to hide the npc from the player
   *
   * @param player
   * @param npc
   */
  public static void sendHidePackets(Player player, BasePlayerNPC npc) {
    // Remove entity
    BaseWrapperPlayServerEntityDestroy spawnWrapper = new WrapperGenerator<BaseWrapperPlayServerEntityDestroy>(
      BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY).result;
    spawnWrapper.setEntityIds(new int[]{npc.getEntityId()});
    sendPacket(player, spawnWrapper.getHandle(), false);

    // Remove player from tab list if its still on there
    sendPlayerInfoPacket(player, npc, PlayerInfoAction.REMOVE_PLAYER);
  }

  /**
   * Sends packets to rotate an entities head
   *
   * @param player
   * @param npc
   */
  public static void sendHeadRotationPacket(Player player, BasePlayerNPC npc) {
    // Head rotation
    if (npc.getEyeLocation() != null) {
      BaseWrapperPlayServerEntityHeadRotation headWrapper = new WrapperGenerator<BaseWrapperPlayServerEntityHeadRotation>(
        BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION).result;
      headWrapper.setEntityID(npc.getEntityId());
      headWrapper.setHeadYaw((byte) ((npc.getEyeLocation().getYaw() % 360.0F) * 256.0F / 360.0F));
      sendPacket(player, headWrapper.getHandle(), false);
    }
  }

  /**
   * Sends packets to either add or remove the given npc for the given player from the tablist
   *
   * @param player
   * @param npc
   * @param action
   */
  public static void sendPlayerInfoPacket(Player player, BasePlayerNPC npc, PlayerInfoAction action) {
    BaseWrapperPlayServerPlayerInfo infoWrapper = new WrapperGenerator<BaseWrapperPlayServerPlayerInfo>(
      BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO).result;

    PlayerInfoData data = new PlayerInfoData(npc.getGameProfile(), 1, NativeGameMode.NOT_SET, WrappedChatComponent.fromText(npc.getDisplayName()));
    infoWrapper.setData(Collections.singletonList(data));
    infoWrapper.setAction(action);
    sendPacket(player, infoWrapper.getHandle(), false);
  }

  public static void sendEquipmentPackets(Player player, BasePlayerNPC npc) {
    BaseWrapperPlayServerEntityEquipment wrapper = new WrapperGenerator<BaseWrapperPlayServerEntityEquipment>(
      BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT).result;
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

  public static void sendAnimationPacket(Player player, BasePlayerNPC npc, int animationID) {
    BaseWrapperPlayServerAnimation animationWrapper = new WrapperGenerator<BaseWrapperPlayServerAnimation>(
      BASE_WRAPPER_PLAY_SERVER_ANIMATION).result;
    animationWrapper.setEntityID(npc.getEntityId());
    animationWrapper.setAnimation(animationID);
    sendPacket(player, animationWrapper.getHandle(), false);

  }

  public static void sendDeathMetaData(Player player, BasePlayerNPC npc) {
    BaseWrapperPlayServerEntityMetadata wrapperEntityMeta = new WrapperGenerator<BaseWrapperPlayServerEntityMetadata>(
      BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA).result;
    wrapperEntityMeta.setEntityID(npc.getEntityId());

    // Create the data watcher for this entity
    WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(player).deepClone();
    watcher.setObject(8, 0F);

    //    Optional<?> opt = Optional.of(WrappedChatComponent.fromText("dead").getHandle());
    //    watcher.setObject(new WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt);
    //    watcher.setObject(new WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true); //custom name visible

    wrapperEntityMeta.setMetadata(watcher.getWatchableObjects());
    sendPacket(player, wrapperEntityMeta.getHandle(), false);
  }

  /**
   * Sends the given packet to the given player
   *
   * @param player
   * @param packetContainer
   */
  private static void sendPacket(Player player, PacketContainer packetContainer, boolean debug) {
    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);

      if (debug) {
        PlayerNPCLib.getPlugin().getServer().getConsoleSender().sendMessage(
          "Sent packet " + packetContainer.getType().name() + " to " + player.getDisplayName()
        );
      }
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Cannot send packet " + packetContainer, e);
    }
  }

}
