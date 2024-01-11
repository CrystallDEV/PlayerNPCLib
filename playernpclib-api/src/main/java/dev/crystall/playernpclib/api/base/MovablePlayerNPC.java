package dev.crystall.playernpclib.api.base;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.utility.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Spawns a movable npc, which moves around and can be attacked by players. This npc will be spawned as a creature and will be invisible to all players by
 * default
 * <p>
 * It spawns a bukkit entity to make it move around and attack players to prevent recalculation of the pathfinding and to guarantee vanilla like behaviour
 * <p>
 * Created by CrystallDEV on 01/09/2020
 */
@Slf4j
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
  public void spawn() {
    if (getLocation().getWorld().getDifficulty().equals(org.bukkit.Difficulty.PEACEFUL) && entityType.getEntityClass() == Creature.class) {
      log.warn("Tried spawning a movable entity with creature entity type in peaceful mode.");
      return;
    }
    this.bukkitLivingEntity = (Creature) getLocation().getWorld().spawnEntity(getLocation(), entityType);
    if (this.bukkitLivingEntity instanceof Ageable ageable) {
      ageable.setAdult();
    }
    if (this.bukkitLivingEntity instanceof Zombie zombie) {
      zombie.setShouldBurnInDay(false);
    }
    // Prevent sounds from this entity
    this.bukkitLivingEntity.setSilent(true);
    this.bukkitLivingEntity.setCanPickupItems(false);
    this.bukkitLivingEntity.setInvisible(true);
    this.bukkitLivingEntity.setInvulnerable(true);
    this.bukkitLivingEntity.setRemoveWhenFarAway(false);
    super.spawn();
  }

  @Override
  public void remove() {
    super.remove();
    if (this.bukkitLivingEntity != null && !this.bukkitLivingEntity.isDead()) {
      this.bukkitLivingEntity.remove();
    }
  }

  @Override
  public void update(Player player) {
    super.update(player);
  }

  public void updateInventory() {
    if (isSpawned()) {
      // Update items on here with the ones on
      for (ItemSlot itemSlot : ItemSlot.values()) {
        EquipmentSlot slot = Utils.getEquipmentSlotFor(itemSlot);
        getItemSlots().put(itemSlot, bukkitLivingEntity.getEquipment().getItem(slot));
      }
      for (Player player : location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE)) {
        PlayerNPCLib.getPacketManager().sendEquipmentPackets(player, this);
      }
    }
  }

}
