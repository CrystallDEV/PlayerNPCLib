package dev.crystall.playernpclib.manager;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import org.bukkit.entity.Player;

/**
 * Created by CrystallDEV on 27/07/2023
 */
public interface PacketManager {

  /**
   * Sends packets to the given player to update the scoreboard
   *
   * @param player The player to send the packets to
   * @param npc The npc to spawn
   */
  void sendScoreBoardTeamPacket(Player player, BasePlayerNPC npc);

  /**
   * Sends packets to the given player to create a scoreboard team
   *
   * @param player The player to send the packets to
   */
  void sendScoreBoardTeamCreatePacket(Player player);

  /**
   * Sends packets to the given player to create a custom npc
   *
   * @param player The player to send the packets to
   * @param npc The npc to create
   */
  void sendNPCCreatePackets(Player player, BasePlayerNPC npc);

  /**
   * Sends position update packets to the given player
   *
   * @param player The player to send the packets to
   * @param npc The npc to update
   */
  void sendMovePacket(Player player, BasePlayerNPC npc);

  /**
   * Sends packets to hide the npc from the player
   *
   * @param player The player to hide the npc from
   * @param npc The npc to hide
   */
  void sendHidePackets(Player player, BasePlayerNPC npc);

  /**
   * Sends packets to rotate an entities head
   *
   * @param player The player to send the packets to
   * @param npc The npc to rotate the head for
   */
  void sendHeadRotationPacket(Player player, BasePlayerNPC npc);

  /**
   * Sends packets execute a player info action for the given npc for the given player
   *
   * @param player The player to send the packets to
   * @param npc The npc to execute the action for
   * @param action The action to perform
   */
  void sendPlayerInfoPacket(Player player, BasePlayerNPC npc, PlayerInfoAction action);

  void sendPlayerInfoPacketRemove(Player player, BasePlayerNPC npc);

  void sendEquipmentPackets(Player player, BasePlayerNPC npc);

  void sendAnimationPacket(Player player, BasePlayerNPC npc, int animationID);

  void sendDeathMetaData(Player player, BasePlayerNPC npc);

  /**
   * Sends the given packet to the given player
   *
   * @param player The player to send the packet to
   * @param packetContainer The packet to send
   */
  void sendPacket(Player player, PacketContainer packetContainer, boolean debug);

}
