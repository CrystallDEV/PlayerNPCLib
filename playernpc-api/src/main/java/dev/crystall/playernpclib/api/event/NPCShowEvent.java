package dev.crystall.playernpclib.api.event;

import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public class NPCShowEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();

  private final BasePlayerNPC npc;
  private final Player player;
  private boolean isCancelled = false;

  public NPCShowEvent(final Player player, final BasePlayerNPC npc) {
    this.player = player;
    this.npc = npc;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.isCancelled = cancel;
  }
}