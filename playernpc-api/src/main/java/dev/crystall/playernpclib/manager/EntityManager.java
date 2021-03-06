package dev.crystall.playernpclib.manager;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.api.event.ClickType;
import dev.crystall.playernpclib.api.event.NPCHideEvent;
import dev.crystall.playernpclib.api.event.NPCInteractEvent;
import dev.crystall.playernpclib.api.event.NPCShowEvent;
import dev.crystall.playernpclib.api.event.NPCSpawnEvent;
import dev.crystall.playernpclib.api.utility.Utils;
import dev.crystall.nms.wrappers.WrapperPlayClientUseEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class EntityManager {

  private static int ENTITY_ID_COUNTER = Integer.MAX_VALUE;

  @Getter
  private static final List<BasePlayerNPC> playerNPCList = new CopyOnWriteArrayList<>();

  // Prevent players from clicking at very high speeds.
  private final Set<UUID> interactableDelay = new HashSet<>();

  public EntityManager() {
    ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PlayerNPCLib.getPlugin(), Client.USE_ENTITY) {
      @Override
      public void onPacketReceiving(PacketEvent event) {
        handleInteractPacket(event.getPlayer(), event);
      }
    });

    Bukkit.getScheduler().runTaskTimer(PlayerNPCLib.getPlugin(), () -> {
      for (BasePlayerNPC npc : playerNPCList) {
        if (!(npc instanceof MovablePlayerNPC)) {
          if (npc.isLookAtClosestPlayer()) {
            handleLookClosest(npc);
          }
          continue;
        }
        handleNPCMoving((MovablePlayerNPC) npc);
      }
    }, 0L, 1L);
  }

  /**
   * @param npc
   */
  public void spawnEntity(BasePlayerNPC npc, boolean showByDefault) {
    if (new NPCSpawnEvent(npc).callEvent()) {
      if (showByDefault) {
        npc.spawn(new ArrayList<>(PlayerNPCLib.getPlugin().getServer().getOnlinePlayers()));
      } else {
        npc.spawn();
      }
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
      if (canSee(player, npc) && inRangeOf(player, npc) && inViewOf(player, npc)) {
        // The player is in range and can see the NPC, auto-show it.
        showNPC(player, npc);
      } else if (!canSee(player, npc) && !inRangeOf(player, npc)) {
        // The player is not in range of the NPC anymore, auto-hide it.
        hideNPC(player, npc);
      }
    }
  }

  public void handleNPCMoving(MovablePlayerNPC npc) {
    npc.setLocation(npc.getBukkitLivingEntity().getLocation(), false);
    npc.setEyeLocation(npc.getBukkitLivingEntity().getEyeLocation(), false);
    if (npc.isSpawned() && npc.getHologram() != null && !npc.getHologram().isDeleted()) {
      npc.getHologram().teleport(npc.getLocation().clone().add(0, 2.5, 0));
    }
    for (Player player : npc.getLocation().getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE)) {
      PacketManager.sendMovePacket(player, npc);
    }
  }

  /**
   * Async gets the closest player and makes the given npc look at it
   *
   * @param npc that will look at the closest player
   */
  private void handleLookClosest(BasePlayerNPC npc) {
    Player closestPlayer = null;
    double shortestDistance = Double.MAX_VALUE;
    for (Player player : npc.getLocation().getNearbyPlayers(Constants.NPC_LOOK_AT_RADIUS)) {
      if (!canSee(player, npc)) {
        continue;
      }
      double distance = player.getLocation().distance(npc.getLocation());
      if (distance < shortestDistance) {
        closestPlayer = player;
        shortestDistance = distance;
      }
    }

    if (closestPlayer != null) {
      Location lookLocation = Utils.lookAt(npc.getLocation(), closestPlayer.getLocation());
      npc.setLocation(lookLocation, false);
      npc.setEyeLocation(lookLocation, true);
    }
  }

  /**
   * @param player
   * @param event
   */
  private void handleInteractPacket(Player player, PacketEvent event) {
    PacketContainer packet = event.getPacket();
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

    // Cancel the event since we handle stuff ourself
    event.setCancelled(true);

    // Active delay
    if (interactableDelay.contains(player.getUniqueId())) {
      return;
    }

    ClickType clickType = packetWrapper.getType() == EntityUseAction.ATTACK ? ClickType.LEFT_CLICK : ClickType.RIGHT_CLICK;

    // Add the player to the delay set
    interactableDelay.add(player.getUniqueId());

    NPCInteractEvent interactEvent = new NPCInteractEvent(player, npc, clickType);
    Bukkit.getScheduler().runTask(PlayerNPCLib.getPlugin(), () -> {
      interactEvent.callEvent();
      interactableDelay.remove(player.getUniqueId());
    });

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

  public boolean canSee(Player player, BasePlayerNPC npc) {
    return npc.getShownTo().contains(player.getUniqueId());
  }

  public boolean canSee(Player player, int entityId) {
    BasePlayerNPC npc = playerNPCList.stream().filter(basePlayerNPC -> basePlayerNPC.getEntityId() == entityId).findFirst().orElse(null);
    return npc != null && npc.getShownTo().contains(player.getUniqueId());
  }

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

    return distanceSquared <= Math.pow(hideDistance, 2) && distanceSquared <= Math.pow(bukkitRange, 2);
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
