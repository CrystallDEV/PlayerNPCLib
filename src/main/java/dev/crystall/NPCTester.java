package dev.crystall;

import static org.bukkit.Bukkit.getPluginManager;

import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.api.base.StaticPlayerNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by CrystallDEV on 09/09/2020
 */
public class NPCTester extends JavaPlugin implements Listener {

  private static PlayerNPCLib playerNPCLib;

  @Override
  public void onEnable() {
    super.onEnable();
    getPluginManager().registerEvents(this, this);
    playerNPCLib = new PlayerNPCLib(this);
  }

  @Override
  public void onDisable() {
    super.onDisable();
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent event) {
    if (event.getAction().toString().startsWith("RIGHT") && event.getPlayer().isSneaking()) {
      StaticPlayerNPC npc = new StaticPlayerNPC("P" + System.currentTimeMillis(), event.getPlayer().getLocation());
      npc.setName(String.valueOf(npc.getEntityId()));
      playerNPCLib.getEntityManager().spawnEntity(npc);
    }

    if (event.getAction().toString().startsWith("LEFT") && event.getPlayer().isSneaking()) {
      MovablePlayerNPC npc = new MovablePlayerNPC("P" + System.currentTimeMillis(), event.getPlayer().getLocation(), EntityType.ZOMBIE);
      npc.setName(String.valueOf(npc.getEntityId()));
      playerNPCLib.getEntityManager().spawnEntity(npc);
    }
  }

  @EventHandler
  public void onEntityInteract(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player && ((Player) event.getDamager()).isSneaking()) {
      if (PlayerNPCLib.isNPC((LivingEntity) event.getEntity())) {
        //        playerNPCLib.getEntityManager().removeEntity(event.getEntity());
      }
    }
  }
}
