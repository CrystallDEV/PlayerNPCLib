package dev.crystall.playernpclib.nms_v1_18_R1.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityTeleport;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerEntityTeleport extends AbstractPacket implements BaseWrapperPlayServerEntityTeleport {

  public WrapperPlayServerEntityTeleport() {
    super(new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT), PacketType.Play.Server.ENTITY_TELEPORT);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerEntityTeleport(PacketContainer packet) {
    super(packet, PacketType.Play.Server.ENTITY_TELEPORT);
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
  public double getX() {
    return handle.getDoubles().read(0);
  }

  @Override
  public void setX(double value) {
    handle.getDoubles().write(0, value);
  }

  @Override
  public double getY() {
    return handle.getDoubles().read(1);
  }

  @Override
  public void setY(double value) {
    handle.getDoubles().write(1, value);
  }

  @Override
  public double getZ() {
    return handle.getDoubles().read(2);
  }

  @Override
  public void setZ(double value) {
    handle.getDoubles().write(2, value);
  }

  @Override
  public float getYaw() {
    return (handle.getBytes().read(0) * 360.F) / 256.0F;
  }

  @Override
  public void setYaw(float value) {
    handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
  }

  @Override
  public float getPitch() {
    return (handle.getBytes().read(1) * 360.F) / 256.0F;
  }

  @Override
  public void setPitch(float value) {
    handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
  }

  @Override
  public boolean getOnGround() {
    return handle.getBooleans().read(0);
  }

  public void setOnGround(boolean value) {
    handle.getBooleans().write(0, value);
  }
}
