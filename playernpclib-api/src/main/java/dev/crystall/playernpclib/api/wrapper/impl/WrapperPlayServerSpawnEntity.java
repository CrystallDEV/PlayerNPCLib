package dev.crystall.playernpclib.api.wrapper.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import java.util.UUID;
import org.bukkit.entity.EntityType;

/**
 * Wrapper for SPAWN_ENTITY packet (MC 1.20.2+).
 * Replaces deprecated NAMED_ENTITY_SPAWN packet.
 *
 * Source: dmulloy2/PacketWrapper - WrapperPlayServerSpawnEntity.java
 */
public class WrapperPlayServerSpawnEntity extends AbstractPacket {

  public WrapperPlayServerSpawnEntity() {
    super(new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY), PacketType.Play.Server.SPAWN_ENTITY);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerSpawnEntity(PacketContainer packet) {
    super(packet, PacketType.Play.Server.SPAWN_ENTITY);
  }

  public int getEntityID() {
    return handle.getIntegers().read(0);
  }

  public void setEntityID(int value) {
    handle.getIntegers().write(0, value);
  }

  public UUID getUniqueId() {
    return handle.getUUIDs().read(0);
  }

  public void setUniqueId(UUID value) {
    handle.getUUIDs().write(0, value);
  }

  public EntityType getType() {
    return handle.getEntityTypeModifier().read(0);
  }

  public void setType(EntityType value) {
    handle.getEntityTypeModifier().write(0, value);
  }

  public double getX() {
    return handle.getDoubles().read(0);
  }

  public void setX(double value) {
    handle.getDoubles().write(0, value);
  }

  public double getY() {
    return handle.getDoubles().read(1);
  }

  public void setY(double value) {
    handle.getDoubles().write(1, value);
  }

  public double getZ() {
    return handle.getDoubles().read(2);
  }

  public void setZ(double value) {
    handle.getDoubles().write(2, value);
  }

  public float getYaw() {
    return (handle.getBytes().read(1) * 360.F) / 256.0F;
  }

  public void setYaw(float value) {
    handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
  }

  public float getPitch() {
    return (handle.getBytes().read(0) * 360.F) / 256.0F;
  }

  public void setPitch(float value) {
    handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
  }

  public int getObjectData() {
    return handle.getIntegers().read(1);
  }

  public void setObjectData(int value) {
    handle.getIntegers().write(1, value);
  }

}
