package dev.crystall.playernpclib;

import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.event.NPCSpawnEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class EntityManager {

  @Getter
  private static final List<BasePlayerNPC> playerNPCList = new CopyOnWriteArrayList<>();


  public EntityManager() {
  }


  public void spawnEntity(BasePlayerNPC npc) {
    if (new NPCSpawnEvent(npc).callEvent()) {
      npc.onSpawn();
    }
  }

  public boolean removeEntity(BasePlayerNPC npc) {
    return false;
  }

}
