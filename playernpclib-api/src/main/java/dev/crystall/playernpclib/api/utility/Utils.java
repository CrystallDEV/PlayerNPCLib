package dev.crystall.playernpclib.api.utility;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.manager.EntityManager;
import java.util.Arrays;
import java.util.IllegalFormatException;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Slf4j
public class Utils {

  private Utils() {
  }

  /**
   * Verify a condition is true. If false, it will throw a GeneralException.
   *
   * @param condition The condition to verify.
   * @param error The error description.
   * @param args String format variables.
   */
  public static void verify(boolean condition, String error, Object... args) {
    if (condition) {
      return; // Check passed, don't error.
    }

    try {
      throw new RuntimeException(error + " - " + Arrays.toString(args));
    } catch (IllegalFormatException e) { // In-case there was an error formatting the error message, still throw the exception.
      log.error("[Utils#verify] Can't format message");
      log.error(e.getMessage());
    }
  }

  /**
   * Gets the {@link EquipmentSlot} for a given {@link ItemSlot}.
   *
   * @param itemSlot the item slot that should be checked
   * @return the {@link EquipmentSlot} for the given {@link ItemSlot}
   */
  public static EquipmentSlot getEquipmentSlotFor(ItemSlot itemSlot) {
    return switch (itemSlot) {
      case MAINHAND -> EquipmentSlot.HAND;
      case OFFHAND -> EquipmentSlot.OFF_HAND;
      case FEET -> EquipmentSlot.FEET;
      case LEGS -> EquipmentSlot.LEGS;
      case CHEST -> EquipmentSlot.CHEST;
      case HEAD -> EquipmentSlot.HEAD;
    };
  }

  /**
   * Gets the {@link BasePlayerNPC} from a given {@link Entity}.
   *
   * @param entity the entity that should be checked
   * @return the {@link BasePlayerNPC} if the entity is a {@link BasePlayerNPC}, otherwise null
   */
  public static BasePlayerNPC getNPCFromEntity(Entity entity) {
    for (BasePlayerNPC npc : EntityManager.getPlayerNPCList()) {
      if (npc instanceof MovablePlayerNPC movablePlayerNPC) {
        if (!entity.equals(movablePlayerNPC.getBukkitLivingEntity())) {
          continue;
        }
        return movablePlayerNPC;
      }
    }
    return null;
  }

  /**
   * Changes a given location {@param loc} to face the location {@param lookAt}. This will be used for NPCs and the world UI.
   *
   * @param loc the original location that should be changed
   * @param lookAt the location that the original location {@param lookAt} should be facing
   * @return the new location that is facing the location {@param lookAt}
   */
  public static Location lookAt(Location loc, Location lookAt) {
    Location directionLocation = loc.clone();
    Vector dirBetweenLocations = lookAt.toVector().subtract(directionLocation.toVector()).normalize();
    directionLocation.setDirection(dirBetweenLocations);
    return directionLocation;
  }
}
