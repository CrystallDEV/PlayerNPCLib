package dev.crystall.playernpclib.nms_v1_19_R2.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.crystall.playernpclib.wrapper.AbstractPacket;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerEntityHeadRotation;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerEntityHeadRotation extends AbstractPacket implements BaseWrapperPlayServerEntityHeadRotation {

  public WrapperPlayServerEntityHeadRotation() {
    super(new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION), PacketType.Play.Server.ENTITY_HEAD_ROTATION);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerEntityHeadRotation(PacketContainer packet) {
    super(packet, PacketType.Play.Server.ENTITY_HEAD_ROTATION);
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
  public byte getHeadYaw() {
    return handle.getBytes().read(0);
  }

  @Override
  public void setHeadYaw(byte value) {
    handle.getBytes().write(0, value);
  }

}
