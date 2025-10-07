package dev.crystall.playernpclib.nms_v1_20_R2.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerAnimation;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerAnimation extends AbstractPacket implements BaseWrapperPlayServerAnimation {

  public WrapperPlayServerAnimation() {
    super(new PacketContainer(PacketType.Play.Server.ANIMATION), PacketType.Play.Server.ANIMATION);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerAnimation(PacketContainer packet) {
    super(packet, PacketType.Play.Server.ANIMATION);
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
  public int getAnimation() {
    return handle.getIntegers().read(1);
  }

  @Override
  public void setAnimation(int value) {
    handle.getIntegers().write(1, value);
  }

}
