package dev.crystall;

import static org.bukkit.Bukkit.getPluginManager;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.api.base.StaticPlayerNPC;
import dev.crystall.playernpclib.api.event.NPCInteractEvent;
import dev.crystall.playernpclib.api.event.NPCInteractEvent.ClickType;
import dev.crystall.playernpclib.api.skin.SkinFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.RandomUtils;
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
      SkinFetcher.asyncFetchSkin(RandomUtils.nextInt(0, 5000), skin -> {
        StaticPlayerNPC npc = new StaticPlayerNPC("P" + System.currentTimeMillis(), event.getPlayer().getLocation());
        npc.setName(String.valueOf(npc.getEntityId()));
        npc.setSkin(skin);

        npc.setItem(ItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
        npc.setItem(ItemSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
        npc.setItem(ItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
        npc.setItem(ItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
        npc.setItem(ItemSlot.MAINHAND, new ItemStack(Material.DIAMOND_AXE));
        npc.setItem(ItemSlot.OFFHAND, new ItemStack(Material.DIAMOND_AXE));
        
        Bukkit.getScheduler().runTask(PlayerNPCLib.getInstance().getPlugin(), () -> playerNPCLib.getEntityManager().spawnEntity(npc));
      });
    }

    if (event.getAction().toString().startsWith("LEFT") && event.getPlayer().isSneaking()) {
      SkinFetcher.asyncFetchSkin(RandomUtils.nextInt(0, 5000), skin -> {
        MovablePlayerNPC npc = new MovablePlayerNPC("P" + System.currentTimeMillis(), event.getPlayer().getLocation(), EntityType.ZOMBIE);
        npc.setName(String.valueOf(npc.getEntityId()));
        npc.setSkin(skin);

        npc.setItem(ItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
        npc.setItem(ItemSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
        npc.setItem(ItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
        npc.setItem(ItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
        npc.setItem(ItemSlot.MAINHAND, new ItemStack(Material.DIAMOND_AXE));
        npc.setItem(ItemSlot.OFFHAND, new ItemStack(Material.DIAMOND_AXE));

        Bukkit.getScheduler().runTask(PlayerNPCLib.getInstance().getPlugin(), () -> playerNPCLib.getEntityManager().spawnEntity(npc));
      });

    }
  }

  @EventHandler
  public void onEntityInteract(NPCInteractEvent event) {
    if (event.getClickType() == ClickType.LEFT_CLICK) {
      event.getNpc().onDespawn();
    }
  }
}
