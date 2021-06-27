package dev.crystall.playernpclib.api.utility;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.api.base.MovablePlayerNPC;
import dev.crystall.playernpclib.manager.EntityManager;
import java.util.Arrays;
import java.util.IllegalFormatException;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

/**
 * Created by CrystallDEV on 01/09/2020
 */
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
      PlayerNPCLib.getPlugin().getLogger().severe("[Utils#verify] Can't format message");
      PlayerNPCLib.getPlugin().getLogger().severe(e.getMessage());
    }
  }

  public static EquipmentSlot getEquipmentSlotFor(ItemSlot itemSlot) {
    switch (itemSlot) {
      case MAINHAND:
        return EquipmentSlot.HAND;
      case OFFHAND:
        return EquipmentSlot.OFF_HAND;
      case FEET:
        return EquipmentSlot.FEET;
      case LEGS:
        return EquipmentSlot.LEGS;
      case CHEST:
        return EquipmentSlot.CHEST;
      case HEAD:
        return EquipmentSlot.HEAD;
      default:
        return EquipmentSlot.valueOf(itemSlot.name());
    }
  }

  public static BasePlayerNPC getNPCFromEntity(Entity entity) {
    for (BasePlayerNPC npc : EntityManager.getPlayerNPCList()) {
      if (npc instanceof MovablePlayerNPC) {
        MovablePlayerNPC movablePlayerNPC = (MovablePlayerNPC) npc;
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
   * @return
   */
  public static Location lookAt(Location loc, Location lookAt) {
    Location directionLocation = loc.clone();
    Vector dirBetweenLocations = lookAt.toVector().subtract(directionLocation.toVector()).normalize();
    directionLocation.setDirection(dirBetweenLocations);
    return directionLocation;
  }
}
