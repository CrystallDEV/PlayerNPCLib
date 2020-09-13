package dev.crystall.playernpclib.manager;

import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.event.NPCHideEvent;
import dev.crystall.playernpclib.api.event.NPCShowEvent;
import dev.crystall.playernpclib.api.event.NPCSpawnEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
      playerNPCList.add(npc);
    }
  }

  public boolean removeEntity(BasePlayerNPC npc) {
    if (!playerNPCList.contains(npc)) {
      return false;
    }

    playerNPCList.remove(npc);
    npc.onDespawn();
    return true;
  }

  public void handleRealPlayerMove(Player player) {
    for (BasePlayerNPC npc : getPlayerNPCList()) {
      if (!canSee(player, npc)) {
        // NPC is hidden from the player
        continue;
      }

      if (!canSee(player, npc) && inRangeOf(player, npc) && inViewOf(player, npc)) {
        // The player is in range and can see the NPC, auto-show it.
        showNPC(player, npc);
      } else if (canSee(player, npc) && !inRangeOf(player, npc)) {
        // The player is not in range of the NPC anymore, auto-hide it.
        hideNPC(player, npc);
      }
    }
  }

  public void showNPC(Player player, BasePlayerNPC npc) {
    if (!new NPCShowEvent(player, npc).callEvent()) {
      return;
    }
    npc.show(player);
  }

  public void hideNPC(Player player, BasePlayerNPC npc) {
    if (!new NPCHideEvent(player, npc).callEvent()) {
      return;
    }
    npc.hide(player);
  }

  public boolean canSee(Player player, BasePlayerNPC npc) {
    if (npc.getBukkitLivingEntity() == null) {
      return false;
    }
    return PlayerNPCLib.getInstance().getEntityHider().canSee(player, npc.getBukkitLivingEntity());
  }

  public boolean inRangeOf(Player player, BasePlayerNPC npc) {

    return true;
  }

  public boolean inViewOf(Player player, BasePlayerNPC npc) {

    return true;
  }

}
