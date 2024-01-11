package dev.crystall.playernpclib.nms_v1_20_R2.manager;


import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ANIMATION;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO_REMOVE;
import static dev.crystall.playernpclib.api.wrapper.WrapperFactory.BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerAnimation;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityDestroy;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityEquipment;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityHeadRotation;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityTeleport;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerNamedEntitySpawn;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerPlayerInfo;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerPlayerInfoRemove;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerScoreboardTeam;
import dev.crystall.playernpclib.api.wrapper.TeamMode;
import dev.crystall.playernpclib.api.wrapper.WrapperGenerator;
import dev.crystall.playernpclib.manager.PacketManager;
import java.util.Arrays;
import java.util.Collections;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.OptionStatus;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@NoArgsConstructor
public class PacketManagerImpl implements PacketManager {

  @Override
  public void sendScoreBoardTeamPacket(Player player, BasePlayerNPC npc) {
    BaseWrapperPlayServerScoreboardTeam wrapperTeam = WrapperGenerator.map(
      BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM);
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.PLAYERS_ADDED);
    wrapperTeam.setPlayers(Collections.singletonList(npc.getInternalName()));
    sendPacket(player, wrapperTeam.getHandle(), false);
  }

  @Override
  public void sendScoreBoardTeamCreatePacket(Player player) {
    BaseWrapperPlayServerScoreboardTeam wrapperTeam = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM);
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.TEAM_CREATED);
    wrapperTeam.setNameTagVisibility(OptionStatus.ALWAYS.toString());
    wrapperTeam.setPlayers(Collections.emptyList());
    sendPacket(player, wrapperTeam.getHandle(), false);
  }


  @Override
  public void sendNPCCreatePackets(Player player, BasePlayerNPC npc) {
    // Add entity to player list
    sendPlayerInfoPacket(player, npc, PlayerInfoAction.ADD_PLAYER);

    // Spawn entity
    BaseWrapperPlayServerNamedEntitySpawn spawnWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN);
    spawnWrapper.setEntityID(npc.getEntityId());
    spawnWrapper.setPlayerUUID(npc.getUuid());
    spawnWrapper.setPosition(npc.getLocation().toVector());
    spawnWrapper.setPitch(npc.getLocation().getPitch());
    spawnWrapper.setYaw(npc.getLocation().getYaw());
    sendPacket(player, spawnWrapper.getHandle(), false);

    sendHeadRotationPacket(player, npc);

    Bukkit.getScheduler().runTaskLater(PlayerNPCLib.getPlugin(), () -> sendPlayerInfoPacketRemove(player, npc), 20L);
  }


  @Override
  public void sendMovePacket(Player player, BasePlayerNPC npc) {
    // Location update
    BaseWrapperPlayServerEntityTeleport moveWrapper = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT);
    moveWrapper.setEntityID(npc.getEntityId());
    moveWrapper.setX(npc.getLocation().getX());
    moveWrapper.setY(npc.getLocation().getY());
    moveWrapper.setZ(npc.getLocation().getZ());
    moveWrapper.setYaw(npc.getLocation().getYaw());
    moveWrapper.setPitch(npc.getLocation().getPitch());
    sendPacket(player, moveWrapper.getHandle(), false);

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
    // TODO fix the datawatcher
//    BaseWrapperPlayServerEntityMetadata wrapperEntityMeta = WrapperGenerator.map(BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA);
//    wrapperEntityMeta.setEntityID(npc.getEntityId());
//
//    // Create the data watcher for this entity
//    var watcher = WrappedDataWatcher.getEntityWatcher(player).deepClone();
//    var obj = new WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()));
//    watcher.setObject(obj, EntityPose.DYING.toNms());
//
//    wrapperEntityMeta.setMetadata(watcher.getWatchableObjects());
//    sendPacket(player, wrapperEntityMeta.getHandle(), false);
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
