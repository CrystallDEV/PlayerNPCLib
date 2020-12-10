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

@Getter
public class NPCInteractEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();

  private final BasePlayerNPC npc;
  private final Player player;
  private boolean isCancelled = false;
  private final ClickType clickType;

  public NPCInteractEvent(final Player player, final BasePlayerNPC npc, ClickType clickType) {
    this.player = player;
    this.npc = npc;
    this.clickType = clickType;
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

  public enum ClickType {
    LEFT_CLICK, RIGHT_CLICK
  }
}