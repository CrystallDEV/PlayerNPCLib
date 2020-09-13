package dev.crystall.playernpclib.api.base;

import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.packet.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class MoveablePlayerNPC extends BasePlayerNPC {

  public MoveablePlayerNPC(String name, Location location) {
    super(name, location);
  }

  @Override
  public void onSpawn() {
    this.bukkitLivingEntity = (Zombie) getLocation().getWorld().spawnEntity(getLocation(), EntityType.ZOMBIE);
    this.bukkitLivingEntity.setAdult();
    this.bukkitLivingEntity.setShouldBurnInDay(false);
    this.bukkitLivingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.31F);

    for (Player player1 : Bukkit.getOnlinePlayers()) {
      show(player1);
    }

    Bukkit.getScheduler().runTaskTimer(PlayerNPCLib.getInstance().getPlugin(), () -> {
      this.location = this.bukkitLivingEntity.getLocation();
      for (Player player : Bukkit.getOnlinePlayers()) {
        PacketManager.sendMovePacket(player, this);
      }
    }, 0L, 1L);
  }

  @Override
  public void onDespawn() {
    getBukkitLivingEntity().remove();
    for (Player player : Bukkit.getOnlinePlayers()) {
      hide(player);
    }
  }

  @Override
  public void show(Player player) {
    super.show(player);
    PlayerNPCLib.getInstance().getEntityHider().hideEntity(player, getBukkitLivingEntity());
  }

  @Override
  public void hide(Player player) {
    super.hide(player);
  }
}
