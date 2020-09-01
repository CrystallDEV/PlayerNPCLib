package dev.crystall.playernpclib;

import dev.crystall.playernpclib.api.utility.Utils;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PlayerNPCLib {

  @Getter
  private static PlayerNPCLib instance;

  private final JavaPlugin plugin;

  public PlayerNPCLib(JavaPlugin plugin) {
    Utils.verify(instance == null, "Only one instance of " + getClass().getCanonicalName() + " is allowed");
    PlayerNPCLib.instance = this;
    this.plugin = plugin;

    String versionName = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];


    plugin.getLogger().info("Enabled for Server Version " + versionName);
  }

}
