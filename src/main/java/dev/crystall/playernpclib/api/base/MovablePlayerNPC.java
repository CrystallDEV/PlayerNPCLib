package dev.crystall.playernpclib.api.base;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.utility.Utils;
import dev.crystall.playernpclib.manager.PacketManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
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
  public void spawn() {
    this.bukkitLivingEntity = (Creature) getLocation().getWorld().spawnEntity(getLocation(), entityType);
    if (this.bukkitLivingEntity instanceof Ageable) {
      ((Ageable) this.bukkitLivingEntity).setAdult();
      ((Ageable) this.bukkitLivingEntity).setAdult();
    }
    if (this.bukkitLivingEntity instanceof Zombie) {
      ((Zombie) this.bukkitLivingEntity).setShouldBurnInDay(false);
    }
    // Prevent sounds from this entity
    this.bukkitLivingEntity.setSilent(true);
    super.spawn();

    Bukkit.getScheduler().runTaskTimer(PlayerNPCLib.getPlugin(), () -> {
      this.location = this.bukkitLivingEntity.getLocation();
      for (Player player : Bukkit.getOnlinePlayers()) {
        PacketManager.sendMovePacket(player, this);
      }
    }, 0L, 1L);
  }

  @Override
  public void remove() {
    super.remove();
    if (this.bukkitLivingEntity != null) {
      this.bukkitLivingEntity.remove();
    }
  }

  @Override
  public void show(Player player) {
    super.show(player);
    if (this.bukkitLivingEntity != null) {
      PlayerNPCLib.getEntityHider().hideEntity(player, this.bukkitLivingEntity);
    }
  }

  @Override
  public void hide(Player player) {
    super.hide(player);
  }

  @Override
  public void setItem(ItemSlot slot, ItemStack itemStack) {
    super.setItem(slot, itemStack);
    if (this.bukkitLivingEntity != null) {
      this.bukkitLivingEntity.getEquipment().setItem(Utils.getEquipmentSlotFor(slot), itemStack);
    }
  }

  public void updateInventory() {
    if (isSpawned()) {
      // Update items on here with the ones on
      for (ItemSlot itemSlot : ItemSlot.values()) {
        EquipmentSlot slot = Utils.getEquipmentSlotFor(itemSlot);
        getItemSlots().put(itemSlot, bukkitLivingEntity.getEquipment().getItem(slot));
      }
      // TODO get only nearby players
      for (Player player : Bukkit.getServer().getOnlinePlayers()) {
        PacketManager.sendEquipmentPackets(player, this);
      }
    }
  }
}
