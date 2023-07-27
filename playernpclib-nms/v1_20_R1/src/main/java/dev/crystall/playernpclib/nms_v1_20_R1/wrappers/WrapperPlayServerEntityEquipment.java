package dev.crystall.playernpclib.nms_v1_20_R1.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityEquipment;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayServerEntityEquipment extends AbstractPacket implements BaseWrapperPlayServerEntityEquipment {

  public WrapperPlayServerEntityEquipment() {
    super(new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT), PacketType.Play.Server.ENTITY_EQUIPMENT);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerEntityEquipment(PacketContainer packet) {
    super(packet, PacketType.Play.Server.ENTITY_EQUIPMENT);
  }

  @Override
  public int getEntityID() {
    return handle.getIntegers().read(0);
  }

  @Override
  public void setEntityID(int value) {
    handle.getIntegers().write(0, value);
  }

  @Override
  public Entity getEntity(World world) {
    return handle.getEntityModifier(world).read(0);
  }

  @Override
  public Entity getEntity(PacketEvent event) {
    return getEntity(event.getPlayer().getWorld());
  }

  @Override
  public List<Pair<ItemSlot, ItemStack>> getSlotStackPairLists() {
    return handle.getSlotStackPairLists().read(0);
  }

  @Override
  public void SetSlotStackPairLists(List<Pair<ItemSlot, ItemStack>> values) {
    handle.getSlotStackPairLists().write(0, values);
  }

}
