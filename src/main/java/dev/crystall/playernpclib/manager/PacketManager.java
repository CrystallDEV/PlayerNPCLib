package dev.crystall.playernpclib.manager;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import dev.crystall.nms.wrappers.WrapperPlayServerEntityDestroy;
import dev.crystall.nms.wrappers.WrapperPlayServerEntityTeleport;
import dev.crystall.nms.wrappers.WrapperPlayServerNamedEntitySpawn;
import dev.crystall.nms.wrappers.WrapperPlayServerPlayerInfo;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class PacketManager {

  private PacketManager() {
  }

  public static void sendScoreBoardTeamPacket(Player player, BasePlayerNPC npc) {

  }

  public static void sendMovePacket(Player player, BasePlayerNPC npc) {
    WrapperPlayServerEntityTeleport moveWrapper = new WrapperPlayServerEntityTeleport();
    moveWrapper.setEntityID(npc.getEntityId());
    moveWrapper.setX(npc.getLocation().getX());
    moveWrapper.setY(npc.getLocation().getY());
    moveWrapper.setZ(npc.getLocation().getZ());
    moveWrapper.setYaw(npc.getLocation().getYaw());
    moveWrapper.setPitch(npc.getLocation().getPitch());
    sendPacket(player, moveWrapper.getHandle(), false);

    //    // Head rotation
    //    WrapperPlayServerEntityHeadRotation headWrapper = new WrapperPlayServerEntityHeadRotation();
    //    headWrapper.setEntityID(npc.getEntityId());
    //
    //    Optional<Player> nearbyPlayer = npc.getLocation().getNearbyPlayers(10).stream()
    //      .min(Comparator.comparingDouble(o -> o.getLocation().distance(npc.getLocation())));
    //    nearbyPlayer.ifPresent(value ->
    //      headWrapper.setHeadYaw((byte) (MathUtils.getLookAtYaw(npc.getLocation(), nearbyPlayer.get().getLocation()) * 256.0F / 360.0F + 75)));
    //    sendPacket(player, headWrapper.getHandle(), false);
  }

  public static void sendNPCCreatePackets(Player player, BasePlayerNPC npc) {
    // Add entity to player list
    sendPlayerInfoPacket(player, npc, PlayerInfoAction.ADD_PLAYER);

    // Spawn entity
    WrapperPlayServerNamedEntitySpawn spawnWrapper = new WrapperPlayServerNamedEntitySpawn();
    spawnWrapper.setEntityID(npc.getEntityId());
    spawnWrapper.setPlayerUUID(npc.getUuid());
    spawnWrapper.setPosition(npc.getLocation().toVector());
    sendPacket(player, spawnWrapper.getHandle(), false);

    Bukkit.getScheduler().runTaskLater(PlayerNPCLib.getInstance().getPlugin(), () -> sendPlayerInfoPacket(player, npc, PlayerInfoAction.REMOVE_PLAYER), 1L);
  }

  public static void sendAnimationPacket(Player player, BasePlayerNPC npc) {

  }

  public static void sendPlayerInfoPacket(Player player, BasePlayerNPC npc, PlayerInfoAction action) {
    WrapperPlayServerPlayerInfo infoWrapper = new WrapperPlayServerPlayerInfo();

    infoWrapper.setAction(action);
    PlayerInfoData data = new PlayerInfoData(new WrappedGameProfile(npc.getUuid(), npc.getName()), 1, EnumWrappers.NativeGameMode.CREATIVE,
      WrappedChatComponent.fromText(npc.getName()));

    List<PlayerInfoData> dataList = new ArrayList<>();
    dataList.add(data);
    infoWrapper.setData(dataList);
    sendPacket(player, infoWrapper.getHandle(), false);
  }

  public static void sendEquipmentPackets(Player player, EquipmentSlot slot, boolean auto) {
    //    for (NPCSlot slot : NPCSlot.values()) {
    //      sendEquipmentPacket(player, slot, true);
    //    }
  }

  public static void sendEquipmentPacket(Player player, EquipmentSlot slot, boolean auto) {

  }

  public static void sendShowPackets(Player player, BasePlayerNPC npc) {

  }

  public static void sendHidePackets(Player player, BasePlayerNPC npc) {
    // Remove entity
    WrapperPlayServerEntityDestroy spawnWrapper = new WrapperPlayServerEntityDestroy();
    spawnWrapper.setEntityIds(new int[]{npc.getEntityId()});
    sendPacket(player, spawnWrapper.getHandle(), false);

    // Remove player from tab list if its still on there
    sendPlayerInfoPacket(player, npc, PlayerInfoAction.REMOVE_PLAYER);
  }

  public static void sendMetadataPacket(Player player, BasePlayerNPC npc) {

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
        PlayerNPCLib.getInstance().getPlugin().getServer().getConsoleSender()
          .sendMessage("Sent packet " + packetContainer.getType().name() + " to " + player.getDisplayName());
      }
    } catch (InvocationTargetException e) {
      throw new RuntimeException(
        "Cannot send packet " + packetContainer, e);
    }
  }

}
