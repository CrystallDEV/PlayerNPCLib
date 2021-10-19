package dev.crystall.playernpclib.api.event;


import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a NPC attacks another entity
 * <p>
 * Created by CrystallDEV on 16/09/2020
 */
@Getter
public class NPCAttackEvent extends Event implements Cancellable {

  private final BasePlayerNPC npc;
  private final LivingEntity target;
  private boolean isCancelled = false;

  public NPCAttackEvent(final BasePlayerNPC npc, final LivingEntity target) {
    this.npc = npc;
    this.target = target;
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