package dev.crystall.playernpclib.api.event;

import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public class NPCRemoveEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final BasePlayerNPC npc;

  public NPCRemoveEvent(final BasePlayerNPC npc) {
    this.npc = npc;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

}