package dev.crystall.playernpclib.api.base;

import org.bukkit.Location;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class StaticPlayerNPC extends BasePlayerNPC {

  public StaticPlayerNPC(String name, Location location) {
    super(name, location);
  }

  @Override
  public void onSpawn() {
    super.onSpawn();
  }
}
