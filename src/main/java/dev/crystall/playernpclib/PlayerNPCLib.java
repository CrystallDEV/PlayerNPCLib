package dev.crystall.playernpclib;

import com.comphenix.protocol.ProtocolManager;
import dev.crystall.playernpclib.api.utility.Utils;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PlayerNPCLib {

  @Getter
  private static PlayerNPCLib instance;

  private final JavaPlugin plugin;
  private Class<?> npcClass;
  private EntityManager entityManager;

  public PlayerNPCLib(JavaPlugin plugin) {
    Utils.verify(instance == null, "Only one instance of " + getClass().getCanonicalName() + " is allowed");
    PlayerNPCLib.instance = this;
    this.plugin = plugin;

    String versionName = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
    try {
      this.entityManager = new EntityManager();
      this.npcClass = Class.forName("dev.crystall.playernpclib.nms." + versionName + ".NPC_" + versionName);
    } catch (ClassNotFoundException exception) {
      plugin.getLogger().severe("[PlayerNPCLib] Your server's version (" + versionName + ") is not supported. PlayerNPCLib will not be enabled");
      return;
    }

    plugin.getLogger().info("Enabled for Server Version " + versionName);
  }

  public static boolean isNPC(LivingEntity livingEntity) {
    return EntityManager.getPlayerNPCList().contains(livingEntity.getEntityId());
  }

}
