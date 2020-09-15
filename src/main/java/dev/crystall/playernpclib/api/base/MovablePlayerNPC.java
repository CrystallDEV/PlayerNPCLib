package dev.crystall.playernpclib.api.base;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.utility.Utils;
import dev.crystall.playernpclib.manager.PacketManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public class MovablePlayerNPC extends BasePlayerNPC {

  /**
   * This will be the bukkit entity, which is being spawned on the server but hidden to all players
   */
  protected Creature bukkitLivingEntity;
  private final EntityType entityType;

  /**
   * Defines if this npc should target and attack players / entities nearby
   */
  @Setter
  private boolean isAggressive = false;


  public MovablePlayerNPC(String name, Location location, EntityType entityType) {
    super(name, location);
    this.entityType = entityType;
  }

  @Override
  public void onSpawn() {
    this.bukkitLivingEntity = (Creature) getLocation().getWorld().spawnEntity(getLocation(), entityType);
    if (this.bukkitLivingEntity instanceof Ageable) {
      ((Ageable) this.bukkitLivingEntity).setAdult();
      ((Ageable) this.bukkitLivingEntity).setAdult();
    }
    if (this.bukkitLivingEntity instanceof Zombie) {
      ((Zombie) this.bukkitLivingEntity).setShouldBurnInDay(false);
    }
    this.bukkitLivingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35F);
    // Prevent sounds from this entity
    this.bukkitLivingEntity.setSilent(true);
    super.onSpawn();

    Bukkit.getScheduler().runTaskTimer(PlayerNPCLib.getInstance().getPlugin(), () -> {
      this.location = this.bukkitLivingEntity.getLocation();
      for (Player player : Bukkit.getOnlinePlayers()) {
        PacketManager.sendMovePacket(player, this);
      }
    }, 0L, 1L);
  }

  @Override
  public void onDespawn() {
    super.onDespawn();
    if (this.bukkitLivingEntity != null) {
      this.bukkitLivingEntity.remove();
    }
  }

  @Override
  public void show(Player player) {
    super.show(player);
    if (this.bukkitLivingEntity != null) {
      PlayerNPCLib.getInstance().getEntityHider().hideEntity(player, this.bukkitLivingEntity);
    }
  }

  @Override
  public void hide(Player player) {
    super.hide(player);
  }
}
