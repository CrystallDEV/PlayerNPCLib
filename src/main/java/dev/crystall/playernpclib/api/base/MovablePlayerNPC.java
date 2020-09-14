package dev.crystall.playernpclib.api.base;

import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.manager.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class MovablePlayerNPC extends BasePlayerNPC {

  /**
   * This will be the bukkit entity, which is being spawned on the server but hidden to all players
   */
  protected LivingEntity bukkitLivingEntity;
  private final EntityType entityType;


  public MovablePlayerNPC(String name, Location location, EntityType entityType) {
    super(name, location);
    this.entityType = entityType;
  }

  @Override
  public void onSpawn() {
    this.bukkitLivingEntity = (LivingEntity) getLocation().getWorld().spawnEntity(getLocation(), entityType);
    if (this.bukkitLivingEntity instanceof Ageable) {
      ((Ageable) this.bukkitLivingEntity).setAdult();
      ((Ageable) this.bukkitLivingEntity).setAdult();
    }
    if (this.bukkitLivingEntity instanceof Zombie) {
      ((Zombie) this.bukkitLivingEntity).setShouldBurnInDay(false);
    }
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
    this.bukkitLivingEntity.remove();
    for (Player player : Bukkit.getOnlinePlayers()) {
      hide(player);
    }
  }

  @Override
  public void show(Player player) {
    super.show(player);
    PlayerNPCLib.getInstance().getEntityHider().hideEntity(player, this.bukkitLivingEntity);
  }

  @Override
  public void hide(Player player) {
    super.hide(player);
  }
}
