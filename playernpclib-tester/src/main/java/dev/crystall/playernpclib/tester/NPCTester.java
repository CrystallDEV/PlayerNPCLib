package dev.crystall.playernpclib.tester;

import static org.bukkit.Bukkit.getPluginManager;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.api.base.StaticPlayerNPC;
import dev.crystall.playernpclib.api.event.ClickType;
import dev.crystall.playernpclib.api.event.NPCInteractEvent;
import dev.crystall.playernpclib.api.skin.SkinFetcher;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by CrystallDEV on 09/09/2020
 */
public class NPCTester extends JavaPlugin implements Listener {

  private static final Random random = new Random();

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
    // This can be null in case we have an error on startup
    if (playerNPCLib != null) {
      playerNPCLib.onDisable();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent event) {
    if (event.getItem() == null || event.getItem().getType() != Material.STICK) {
      return;
    }

    BasePlayerNPC npc = null;
    if (event.getAction().toString().startsWith("RIGHT")) {
      npc = new StaticPlayerNPC("P" + System.currentTimeMillis(), event.getPlayer().getLocation());
    }
    if (event.getAction().toString().startsWith("LEFT")) {
      npc = new MovablePlayerNPC(ChatColor.RED + "Barbarian", event.getPlayer().getLocation(), EntityType.ZOMBIE);
      ((MovablePlayerNPC) npc).setAggressive(true);
    }

    if (npc == null) {
      return;
    }

    setDefaultValues(npc);
    if (event.getPlayer().isSneaking()) {
      PlayerNPCLib.getEntityManager().spawnEntity(npc, false);
    } else {
      PlayerNPCLib.getEntityManager().spawnEntity(npc, true);
    }
  }

  private void setDefaultValues(BasePlayerNPC npc) {
    var subNames = new ArrayList<String>();
    for (int i = 0; i < Math.random() * 10; i++) {
      subNames.add("Sub name " + i);
    }
    npc.setSubNames(subNames);
    SkinFetcher.asyncFetchSkin(random.nextInt(5000), playerSkin -> {
      // Set the skin in a synced call
      Bukkit.getScheduler().runTask(this, () -> npc.setPlayerSkin(playerSkin));
    });
    npc.setItem(ItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
    npc.setItem(ItemSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
    npc.setItem(ItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
    npc.setItem(ItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
    npc.setItem(ItemSlot.MAINHAND, new ItemStack(Material.DIAMOND_AXE));
    npc.setItem(ItemSlot.OFFHAND, new ItemStack(Material.DIAMOND_AXE));
  }

  @EventHandler
  public void onEntityInteract(NPCInteractEvent event) {
    if (event.getClickType() == ClickType.LEFT_CLICK) {
      PlayerNPCLib.getEntityManager().removeEntity(event.getNpc());
    }
  }

}
