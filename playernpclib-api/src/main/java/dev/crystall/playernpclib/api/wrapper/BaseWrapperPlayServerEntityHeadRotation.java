package dev.crystall.playernpclib.api.wrapper;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Created by CrystallDEV on 18/08/2021
 */
public interface BaseWrapperPlayServerEntityHeadRotation extends IBaseWrapper{
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

  /**
   * Retrieve Head Yaw.
   * <p>
   * Notes: head yaw in steps of 2p/256
   *
   * @return The current Head Yaw
   */
  byte getHeadYaw();

  /**
   * Set Head Yaw.
   *
   * @param value - new value.
   */
  void setHeadYaw(byte value);

}
