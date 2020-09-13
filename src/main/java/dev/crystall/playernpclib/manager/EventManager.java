package dev.crystall.playernpclib.manager;

import dev.crystall.playernpclib.PlayerNPCLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    EntityManager.getPlayerNPCList().forEach(npc -> npc.show(event.getPlayer()));
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

}
