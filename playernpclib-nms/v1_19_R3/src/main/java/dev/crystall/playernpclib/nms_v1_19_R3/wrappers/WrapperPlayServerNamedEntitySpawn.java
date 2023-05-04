package dev.crystall.playernpclib.nms_v1_19_R3.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.crystall.playernpclib.wrapper.AbstractPacket;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerNamedEntitySpawn;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class WrapperPlayServerNamedEntitySpawn extends AbstractPacket implements BaseWrapperPlayServerNamedEntitySpawn {

  public WrapperPlayServerNamedEntitySpawn() {
    super(new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN), PacketType.Play.Server.NAMED_ENTITY_SPAWN);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerNamedEntitySpawn(PacketContainer packet) {
    super(packet, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
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
  public UUID getPlayerUUID() {
    return handle.getUUIDs().read(0);
  }

  @Override
  public void setPlayerUUID(UUID value) {
    handle.getUUIDs().write(0, value);
  }

  @Override
  public Vector getPosition() {
    return new Vector(getX(), getY(), getZ());
  }

  @Override
  public void setPosition(Vector position) {
    setX(position.getX());
    setY(position.getY());
    setZ(position.getZ());
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

}
