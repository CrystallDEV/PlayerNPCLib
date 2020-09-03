package dev.crystall.playernpclib.api.base;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import dev.crystall.playernpclib.EntityManager;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.packet.PacketManager;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Location;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public abstract class BasePlayerNPC {

  private String name;

  private UUID uuid = UUID.randomUUID();

  /**
   * The id the entity will be registered with at the server
   */
  private final int entityId = Integer.MAX_VALUE - EntityManager.getPlayerNPCList().size();

  private final Location location;

  protected BasePlayerNPC(String name, Location location) {
    this.name = name;
    this.location = location;
  }

  public void setName(String name) {
    // TODO send packet to update the name
  }


  public Hologram generateHologram() {
    return null;
  }

  public void onSpawn() {
    PlayerNPCLib.getInstance().getPlugin().getServer().getOnlinePlayers().forEach(player -> {
      PacketManager.sendNPCCreatePackets(player, this);
    });
  }

}
