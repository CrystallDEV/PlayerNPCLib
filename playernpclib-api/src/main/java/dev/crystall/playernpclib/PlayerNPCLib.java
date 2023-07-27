package dev.crystall.playernpclib;

import static org.bukkit.Bukkit.getServer;

import dev.crystall.playernpclib.api.utility.Utils;
import dev.crystall.playernpclib.api.wrapper.MinecraftVersions;
import dev.crystall.playernpclib.api.wrapper.WrapperFactory;
import dev.crystall.playernpclib.manager.EntityHidePolicy;
import dev.crystall.playernpclib.manager.EntityHider;
import dev.crystall.playernpclib.manager.EntityManager;
import dev.crystall.playernpclib.manager.EventManager;
import dev.crystall.playernpclib.manager.PacketManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

@Slf4j
public class PlayerNPCLib {

  @Getter
  @Setter
  private static PlayerNPCLib instance;

  @Getter
  private static MinecraftVersions detectedNMSVersion;

  /**
   * The plugin that uses this library
   */
  @Getter
  @Setter
  private static JavaPlugin plugin;

  /**
   * Manages all custom player npcs (static and non-static ones)
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

  /**
   * Manages all packets sent to the player
   */
  @Getter
  private static PacketManager packetManager;

  public PlayerNPCLib(JavaPlugin plugin) {
    Utils.verify(instance == null, "Only one instance of " + getClass().getCanonicalName() + " is allowed");
    setInstance(this);
    setPlugin(plugin);

    String versionName = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
    checkServerVersion(versionName);

    if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
      log.error("*** HolographicDisplays is not installed or not enabled. ***");
      log.error("*** This plugin will be disabled. ***");
      getServer().getPluginManager().disablePlugin(plugin);
      return;
    }
    createManager();
    // Create the scoreboard for the npcs to be in
    createNPCScoreboards();

    log.info("Enabled for Server Version {}", versionName);
  }

  public void onDisable() {
    log.info("Disabling PlayerNPCLib....");
  }

  private void createNPCScoreboards() {
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
  }

  private void checkServerVersion(String versionName) {
    try {
      detectedNMSVersion = MinecraftVersions.valueOf(versionName);
    } catch (IllegalArgumentException ignored) {
      logUnsupportedServerVersion(versionName);
      return;
    }

    if (!WrapperFactory.init()) {
      logUnsupportedServerVersion(versionName);
      return;
    }
    log.info("Your server's version ({}) is supported", versionName);
  }

  private void logUnsupportedServerVersion(String versionName) {
    log.error("Your server's version ({}) is not supported. PlayerNPCLib will not be enabled", versionName);
    getServer().getPluginManager().disablePlugin(plugin);
  }

  private static void createManager() {
    PlayerNPCLib.packetManager = WrapperFactory.createPacketManager();
    PlayerNPCLib.entityManager = new EntityManager();
    PlayerNPCLib.eventManager = new EventManager();
    PlayerNPCLib.entityHider = new EntityHider(plugin, EntityHidePolicy.WHITELIST);
  }

  public static boolean isNPC(Entity e) {
    return EntityManager.getPlayerNPCList().stream().anyMatch(npc -> npc.getEntityId() == e.getEntityId());
  }

  public static boolean isNPC(int entityId) {
    return EntityManager.getPlayerNPCList().stream().anyMatch(npc -> npc.getEntityId() == entityId);
  }

}
