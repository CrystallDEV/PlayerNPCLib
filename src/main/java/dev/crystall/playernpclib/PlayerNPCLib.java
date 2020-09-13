package dev.crystall.playernpclib;

import static org.bukkit.Bukkit.getServer;

import dev.crystall.playernpclib.api.utility.Utils;
import dev.crystall.playernpclib.manager.EntityHider;
import dev.crystall.playernpclib.manager.EntityHider.Policy;
import dev.crystall.playernpclib.manager.EntityManager;
import dev.crystall.playernpclib.manager.EventManager;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PlayerNPCLib {

  @Getter
  private static PlayerNPCLib instance;

  /**
   * The plugin that uses this library
   */
  private final JavaPlugin plugin;

  /**
   * Manages all custom player npcs (static and non static ones)
   */
  private EntityManager entityManager;

  /**
   * Manages all events happening in this library
   */
  private EventManager eventManager;

  /**
   * Handles hiding and showing of entities
   */
  private EntityHider entityHider;


  public PlayerNPCLib(JavaPlugin plugin) {
    Utils.verify(instance == null, "Only one instance of " + getClass().getCanonicalName() + " is allowed");
    PlayerNPCLib.instance = this;
    this.plugin = plugin;

    String versionName = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
    if (!createManager(versionName)) {
      // Disable the plugin if we encounter an error. Its most likely that the plugin depends on this library
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }

    plugin.getLogger().info("Enabled for Server Version " + versionName);
  }

  private boolean createManager(String versionName) {
    try {
      MinecraftVersions.valueOf(versionName);
      this.entityManager = new EntityManager();
      this.eventManager = new EventManager();
      this.entityHider = new EntityHider(plugin, Policy.BLACKLIST);
      return true;
    } catch (IllegalArgumentException exception) {
      plugin.getLogger().severe("[PlayerNPCLib] Your server's version (" + versionName + ") is not supported. PlayerNPCLib will not be enabled");
      return false;
    }
  }

  public static boolean isNPC(LivingEntity livingEntity) {
    return EntityManager.getPlayerNPCList().stream().anyMatch(npc -> npc.getEntityId() == livingEntity.getEntityId());
  }

}
