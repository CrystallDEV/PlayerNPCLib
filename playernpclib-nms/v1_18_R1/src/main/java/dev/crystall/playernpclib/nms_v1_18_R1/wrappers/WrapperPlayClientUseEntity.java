package dev.crystall.playernpclib.nms_v1_18_R1.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import dev.crystall.playernpclib.wrapper.AbstractPacket;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayClientUseEntity;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class WrapperPlayClientUseEntity extends AbstractPacket implements BaseWrapperPlayClientUseEntity {

  public WrapperPlayClientUseEntity() {
    super(new PacketContainer(PacketType.Play.Client.USE_ENTITY), PacketType.Play.Client.USE_ENTITY);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayClientUseEntity(PacketContainer packet) {
    super(packet, PacketType.Play.Client.USE_ENTITY);
  }

  @Override
  public int getTargetID() {
    return handle.getIntegers().read(0);
  }

  @Override
  public Entity getTarget(World world) {
    return handle.getEntityModifier(world).read(0);
  }

  @Override
  public Entity getTarget(PacketEvent event) {
    return getTarget(event.getPlayer().getWorld());
  }

  @Override
  public void setTargetID(int value) {
    handle.getIntegers().write(0, value);
  }

  @Override
  public EntityUseAction getType() {
    return handle.getEnumEntityUseActions().read(0).getAction();
  }

  @Override
  public void setType(EntityUseAction value) {
    handle.getEnumEntityUseActions().write(0, WrappedEnumEntityUseAction.fromHandle(value));
  }

  @Override
  public Vector getTargetVector() {
    return handle.getVectors().read(0);
  }

  @Override
  public void setTargetVector(Vector value) {
    handle.getVectors().write(0, value);
  }

}
