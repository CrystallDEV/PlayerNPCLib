package dev.crystall.playernpclib.api.event;

/**
 * Created by CrystallDEV on 14/09/2020
 */

import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCInteractEvent extends Event implements Cancellable {

  private final BasePlayerNPC npc;
  private final Player player;
  private boolean isCancelled = false;
  private final ClickType clickType;

  public NPCInteractEvent(final Player player, final BasePlayerNPC npc, ClickType clickType) {
    this.player = player;
    this.npc = npc;
    this.clickType = clickType;
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