package dev.crystall.playernpclib.api.base;

import com.destroystokyo.paper.entity.Pathfinder;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.packet.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class MoveablePlayerNPC extends BasePlayerNPC {

  public MoveablePlayerNPC(String name, Location location) {
    super(name, location);
  }

  @Override
  public void onSpawn() {
    super.onSpawn();

    Bukkit.getScheduler().runTaskTimer(PlayerNPCLib.getInstance().getPlugin(), () -> {
      Bukkit.getOnlinePlayers().forEach(player -> {
        PacketManager.sendMovePacket(player, this);
      });
    }, 0L, 1L);
  }

  @Override
  public void onDespawn() {

  }
}
