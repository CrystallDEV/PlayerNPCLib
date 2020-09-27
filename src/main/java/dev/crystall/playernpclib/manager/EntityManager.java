package dev.crystall.playernpclib.manager;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import dev.crystall.nms.wrappers.WrapperPlayClientUseEntity;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.event.NPCHideEvent;
import dev.crystall.playernpclib.api.event.NPCInteractEvent;
import dev.crystall.playernpclib.api.event.NPCShowEvent;
import dev.crystall.playernpclib.api.event.NPCSpawnEvent;
import dev.crystall.playernpclib.api.utility.MathUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class EntityManager {

  private static int ENTITY_ID_COUNTER = Integer.MAX_VALUE;

  @Getter
  private static final List<BasePlayerNPC> playerNPCList = new CopyOnWriteArrayList<>();

  public EntityManager() {
    ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PlayerNPCLib.getPlugin(), Client.USE_ENTITY) {
      @Override
      public void onPacketReceiving(PacketEvent event) {
        handleInteractPacket(event.getPlayer(), event.getPacket());
      }
    });
  }

  /**
   * @param npc
   */
  public void spawnEntity(BasePlayerNPC npc) {
    if (new NPCSpawnEvent(npc).callEvent()) {
      npc.spawn();
      playerNPCList.add(npc);
    }
  }

  /**
   * @param npc
   * @return
   */
  public boolean removeEntity(BasePlayerNPC npc) {
    if (!playerNPCList.contains(npc)) {
      return false;
    }

    playerNPCList.remove(npc);
    npc.remove();
    return true;
  }

  /**
   * @param player
   */
  public void handleRealPlayerMove(Player player) {
    for (BasePlayerNPC npc : getPlayerNPCList()) {
      if (!canSee(player, npc)) {
        // NPC is hidden from the player
        continue;
      }

      if (!canSee(player, npc) && inRangeOf(player, npc) && inViewOf(player, npc)) {
        // The player is in range and can see the NPC, auto-show it.
        showNPC(player, npc);
      } else if (canSee(player, npc) && !inRangeOf(player, npc)) {
        // The player is not in range of the NPC anymore, auto-hide it.
        hideNPC(player, npc);
      }
    }
  }

  /**
   * @param player
   * @param packet
   */
  private void handleInteractPacket(Player player, PacketContainer packet) {
    WrapperPlayClientUseEntity packetWrapper = new WrapperPlayClientUseEntity(packet);

    BasePlayerNPC npc = null;
    for (BasePlayerNPC playerNPC : EntityManager.getPlayerNPCList()) {
      if (playerNPC.isSpawned() && playerNPC.getEntityId() == packetWrapper.getTargetID()) {
        npc = playerNPC;
        break;
      }
    }

    if (npc == null) {
      // Default player, not doing magic with the packet.
      return;
    }

    NPCInteractEvent.ClickType clickType = packetWrapper.getType() == EntityUseAction.ATTACK
      ? NPCInteractEvent.ClickType.LEFT_CLICK : NPCInteractEvent.ClickType.RIGHT_CLICK;

    NPCInteractEvent interactEvent = new NPCInteractEvent(player, npc, clickType);
    Bukkit.getScheduler().runTask(PlayerNPCLib.getPlugin(), interactEvent::callEvent);

    // Since this is a packet sent by the client, we need to make some checks
    if (!player.getWorld().equals(npc.getLocation().getWorld()) || player.getLocation().distanceSquared(npc.getLocation()) > 64 || player.isDead()) {
      interactEvent.setCancelled(true);
    }
  }

  /**
   * @param player
   * @param npc
   */
  public void showNPC(Player player, BasePlayerNPC npc) {
    if (!new NPCShowEvent(player, npc).callEvent()) {
      return;
    }
    npc.show(player);
  }

  /**
   * @param player
   * @param npc
   */
  public void hideNPC(Player player, BasePlayerNPC npc) {
    if (!new NPCHideEvent(player, npc).callEvent()) {
      return;
    }
    npc.hide(player);
  }

  /**
   * @param player
   * @param npc
   * @return
   */
  public boolean canSee(Player player, BasePlayerNPC npc) {
    return PlayerNPCLib.getEntityHider().canSee(player, npc.getEntityId());
  }

  /**
   * @param player
   * @param npc
   * @return
   */
  public boolean inRangeOf(Player player, BasePlayerNPC npc) {
    if (player == null) {
      return false;
    }
    if (!player.getWorld().equals(npc.getLocation().getWorld())) {
      // No need to continue our checks, they are in different worlds.
      return false;
    }

    // If Bukkit doesn't track the NPC entity anymore, bypass the hiding distance variable.
    // This will cause issues otherwise (e.g. custom skin disappearing).
    double hideDistance = 50;
    double distanceSquared = player.getLocation().distanceSquared(npc.getLocation());
    double bukkitRange = Bukkit.getViewDistance() << 4;

    return distanceSquared <= MathUtils.lengthSquared(hideDistance) && distanceSquared <= MathUtils.lengthSquared(bukkitRange);
  }

  /**
   * @param player
   * @param npc
   * @return
   */
  public boolean inViewOf(Player player, BasePlayerNPC npc) {
    Vector dir = npc.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
    double cosFOV = Math.cos(Math.toRadians(60));
    return dir.dot(player.getEyeLocation().getDirection()) >= cosFOV;
  }

  public static int tickAndGetCounter() {
    return --ENTITY_ID_COUNTER;
  }

}
