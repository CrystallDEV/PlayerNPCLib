package dev.crystall.playernpclib.manager;

import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.api.event.NPCAttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by CrystallDEV on 13/09/2020
 */
public class EventManager implements Listener {

  public EventManager() {
    Bukkit.getPluginManager().registerEvents(this, PlayerNPCLib.getInstance().getPlugin());
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    for (BasePlayerNPC npc : EntityManager.getPlayerNPCList()) {
      npc.show(event.getPlayer());
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
      PlayerNPCLib.getInstance().getEntityManager().handleRealPlayerMove(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityAttack(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity)) {
      return; // We only handle damage between two living entities
    }

    for (BasePlayerNPC npc : EntityManager.getPlayerNPCList()) {
      if (npc instanceof MovablePlayerNPC) {
        MovablePlayerNPC movablePlayerNPC = (MovablePlayerNPC) npc;
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
      if (npc instanceof MovablePlayerNPC) {
        MovablePlayerNPC movablePlayerNPC = (MovablePlayerNPC) npc;
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
}
