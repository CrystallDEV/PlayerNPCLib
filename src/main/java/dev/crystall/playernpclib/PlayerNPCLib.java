package dev.crystall.playernpclib;

import static org.bukkit.Bukkit.getServer;

import dev.crystall.playernpclib.api.utility.Utils;
import dev.crystall.playernpclib.manager.EntityHidePolicy;
import dev.crystall.playernpclib.manager.EntityHider;
import dev.crystall.playernpclib.manager.EntityManager;
import dev.crystall.playernpclib.manager.EventManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

@Getter
public class PlayerNPCLib {

  @Getter
  private static PlayerNPCLib instance;

  /**
   * The plugin that uses this library
   */
  @Getter
  private static JavaPlugin plugin;

  /**
   * Manages all custom player npcs (static and non static ones)
   */
  @Getter
  private static EntityManager entityManager;

  /**
   * Manages all events happening in this library
   */
  @Getter
  private static EventManager eventManager;

  /**
   * Handles hiding and showing of entities
   */
  @Getter
  private static EntityHider entityHider;


  public PlayerNPCLib(JavaPlugin plugin) {
    Utils.verify(instance == null, "Only one instance of " + getClass().getCanonicalName() + " is allowed");
    PlayerNPCLib.instance = this;
    PlayerNPCLib.plugin = plugin;

    String versionName = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
    if (!createManager(versionName)) {
      // Disable the plugin if we encounter an error. Its most likely that the plugin depends on this library
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }
    if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
      plugin.getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
      plugin.getLogger().severe("*** This plugin will be disabled. ***");
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }

    // Create the scoreboard for the npcs to be in
    Team npcTeam = null;
    for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
      if (team.getName().equals(Constants.NPC_TEAM_NAME)) {
        npcTeam = team;
        break;
      }
    }

    if (npcTeam == null) {
      npcTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(Constants.NPC_TEAM_NAME);
      npcTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
      npcTeam.setOption(Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.NEVER);
      npcTeam.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
    }

    plugin.getLogger().info("Enabled for Server Version " + versionName);
  }

  public void onDisable() {

  }

  private boolean createManager(String versionName) {
    try {
      MinecraftVersions.valueOf(versionName);
      PlayerNPCLib.entityManager = new EntityManager();
      PlayerNPCLib.eventManager = new EventManager();
      PlayerNPCLib.entityHider = new EntityHider(plugin, EntityHidePolicy.BLACKLIST);
      return true;
    } catch (IllegalArgumentException exception) {
      plugin.getLogger().severe("[PlayerNPCLib] Your server's version (" + versionName + ") is not supported. PlayerNPCLib will not be enabled");
      return false;
    }
  }

  public static boolean isNPC(Entity e) {
    return EntityManager.getPlayerNPCList().stream().anyMatch(npc -> npc.getEntityId() == e.getEntityId());
  }

}
