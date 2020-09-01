package dev.crystall.playernpclib.api.base;

import lombok.Getter;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public abstract class BasePlayerNPC {

  private String name;

  /**
   * Defines if the entity should be registered at the server
   */
  private boolean registerServerEntity;

  /**
   * The id the entity will be registered with at the server
   */
  private int entityId;

  protected BasePlayerNPC(String name, boolean registerServerEntity) {
    this.name = name;
    this.registerServerEntity = registerServerEntity;
    this.entityId = entityId;
  }

  public void setName(String name) {
    // TODO send packet to update the name
  }
}
