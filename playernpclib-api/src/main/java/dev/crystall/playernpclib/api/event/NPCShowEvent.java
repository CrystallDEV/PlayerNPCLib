package dev.crystall.playernpclib.api.event;

import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public class NPCShowEvent extends Event implements Cancellable {

  private final BasePlayerNPC npc;
  private final Player player;
  private boolean isCancelled = false;

  public NPCShowEvent(final Player player, final BasePlayerNPC npc) {
    this.player = player;
    this.npc = npc;
  }

  @Getter
  private static final HandlerList handlerList = new HandlerList();

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return getHandlerList();
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