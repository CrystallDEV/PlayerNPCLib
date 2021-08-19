package dev.crystall.playernpclib.wrapper;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Created by CrystallDEV on 18/08/2021
 */
public interface BaseWrapperPlayServerAnimation extends IBaseWrapper{

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
   * Retrieve Animation.
   * <p>
   * Notes: animation ID
   *
   * @return The current Animation
   */
  int getAnimation();

  /**
   * Set Animation.
   *
   * @param value - new value.
   */
  void setAnimation(int value);

}
