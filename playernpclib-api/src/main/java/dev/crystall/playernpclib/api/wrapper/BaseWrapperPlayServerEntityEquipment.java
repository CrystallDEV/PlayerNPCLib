package dev.crystall.playernpclib.api.wrapper;

import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CrystallDEV on 18/08/2021
 */
public interface BaseWrapperPlayServerEntityEquipment extends IBaseWrapper{

  /**
   * Retrieve Entity ID.
   * <p>
   * Notes: entity's ID
   *
   * @return The current Entity ID
   */
  int getEntityID();

  /**
   * Set Entity ID.
   *
   * @param value - new value.
   */
  void setEntityID(int value);

  /**
   * Retrieve the entity of the painting that will be spawned.
   *
   * @param world - the current world of the entity.
   * @return The spawned entity.
   */
  Entity getEntity(World world);

  /**
   * Retrieve the entity of the painting that will be spawned.
   *
   * @param event - the packet event.
   * @return The spawned entity.
   */
  Entity getEntity(PacketEvent event);

  List<Pair<ItemSlot, ItemStack>> getSlotStackPairLists();

  void SetSlotStackPairLists(List<Pair<ItemSlot, ItemStack>> values);

}
