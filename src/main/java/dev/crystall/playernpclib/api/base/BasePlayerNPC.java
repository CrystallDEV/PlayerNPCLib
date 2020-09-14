package dev.crystall.playernpclib.api.base;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.manager.PacketManager;
import dev.crystall.playernpclib.manager.EntityManager;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public abstract class BasePlayerNPC {

  private String name;

  private final UUID uuid = UUID.randomUUID();

  private boolean isSpawned = false;
  /**
   * The id the entity will be registered with at the server
   */
  protected int entityId = Integer.MAX_VALUE - EntityManager.getPlayerNPCList().size();

  protected Location location;


  protected BasePlayerNPC(String name, Location location) {
    this.name = name;
    this.location = location;
  }

  public void onSpawn() {
    for (Player player : PlayerNPCLib.getInstance().getPlugin().getServer().getOnlinePlayers()) {
      show(player);
    }
    isSpawned = true;
  }

  public void onDespawn() {
    for (Player player : PlayerNPCLib.getInstance().getPlugin().getServer().getOnlinePlayers()) {
      hide(player);
    }
    isSpawned = false;
  }

  public void setName(String name) {
    // TODO send packet to update the name
    this.name = name;
  }

  public Hologram generateHologram() {
    return null;
  }

  public void show(Player player) {
    PacketManager.sendNPCCreatePackets(player, this);
  }

  public void hide(Player player) {
    PacketManager.sendHidePackets(player, this);
  }

}
