package dev.crystall.playernpclib.api.utility;

import dev.crystall.playernpclib.PlayerNPCLib;
import java.util.Arrays;
import java.util.IllegalFormatException;

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
      PlayerNPCLib.getInstance().getPlugin().getLogger().severe("[Utils#verify] Can't format message");
      PlayerNPCLib.getInstance().getPlugin().getLogger().severe(e.getMessage());
    }
  }

}
