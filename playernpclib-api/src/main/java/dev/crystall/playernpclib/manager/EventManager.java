package dev.crystall.playernpclib.manager;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.api.event.NPCAttackEvent;
import dev.crystall.playernpclib.api.utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Created by CrystallDEV on 13/09/2020
 */
public class EventManager implements Listener {

  public EventManager() {
    Bukkit.getPluginManager().registerEvents(this, PlayerNPCLib.getPlugin());
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    PacketManager.sendScoreBoardTeamCreatePacket(event.getPlayer());
    for (BasePlayerNPC npc : EntityManager.getPlayerNPCList()) {
      if (PlayerNPCLib.getEntityManager().canSee(event.getPlayer(), npc)) {
        npc.show(event.getPlayer());
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {

  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Location from = event.getFrom();
    Location to = event.getTo();
    // Only check movement when the player moves from one block to another.
    if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
      PlayerNPCLib.getEntityManager().handleRealPlayerMove(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityAttack(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity)) {
      return; // We only handle damage between two living entities
    }

    for (BasePlayerNPC npc : EntityManager.getPlayerNPCList()) {
      if (npc instanceof MovablePlayerNPC movablePlayerNPC) {
        if (!event.getDamager().equals(movablePlayerNPC.getBukkitLivingEntity())) {
          continue;
        }

        // The monster is supposed to attack
        if (movablePlayerNPC.isAggressive()) {
          new NPCAttackEvent(npc, (LivingEntity) event.getEntity()).callEvent();
          npc.playAnimation(0);
        }

        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTargetAggro(EntityTargetLivingEntityEvent event) {
    for (BasePlayerNPC npc : EntityManager.getPlayerNPCList()) {
      if (npc instanceof MovablePlayerNPC movablePlayerNPC) {
        if (!event.getEntity().equals(movablePlayerNPC.getBukkitLivingEntity())) {
          continue;
        }

        // The monster is supposed to attack
        if (movablePlayerNPC.isAggressive()) {
          continue;
        }

        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onRemove(EntityRemoveFromWorldEvent event) {
    MovablePlayerNPC playerNPC = (MovablePlayerNPC) Utils.getNPCFromEntity(event.getEntity());
    if (playerNPC == null) {
      return; // Not an entity related to our library
    }
    playerNPC.remove();
    Bukkit.getScheduler().runTaskLater(PlayerNPCLib.getPlugin(), () -> {
      PlayerNPCLib.getEntityManager().removeEntity(playerNPC);
    }, 20L);
  }

  @EventHandler
  public void onDeath(EntityDeathEvent event) {
    MovablePlayerNPC playerNPC = (MovablePlayerNPC) Utils.getNPCFromEntity(event.getEntity());
    if (playerNPC == null) {
      return; // Not an entity related to our library
    }
    playerNPC.remove();
    Bukkit.getScheduler().runTaskLater(PlayerNPCLib.getPlugin(), () -> {
      PlayerNPCLib.getEntityManager().removeEntity(playerNPC);
    }, 20L);
  }

  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent event) {
    Chunk chunk = event.getChunk();

  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent event) {
    Chunk chunk = event.getChunk();
  }


}
