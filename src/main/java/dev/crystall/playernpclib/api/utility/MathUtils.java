package dev.crystall.playernpclib.api.utility;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Created by CrystallDEV on 10/09/2020
 */
public class MathUtils {

  private MathUtils() {
  }

  public static float getLookAtYaw(Entity loc, Entity lookat) {
    return getLookAtYaw(loc.getLocation(), lookat.getLocation());
  }

  public static float getLookAtYaw(Block loc, Block lookat) {
    return getLookAtYaw(loc.getLocation(), lookat.getLocation());
  }

  public static float getLookAtYaw(Location loc, Location lookat) {
    return getLookAtYaw(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
  }

  public static float getLookAtYaw(Vector motion) {
    return getLookAtYaw(motion.getX(), motion.getZ());
  }

  /**
   * Gets the horizontal look-at angle in degrees to look into the
   * 2D-direction specified
   *
   * @param dx axis of the direction
   * @param dz axis of the direction
   * @return the angle in degrees
   */
  public static float getLookAtYaw(double dx, double dz) {
    return atan2(dz, dx) - 180f;
  }

  /**
   * Gets the pitch angle in degrees to look into the direction specified
   *
   * @param dX axis of the direction
   * @param dY axis of the direction
   * @param dZ axis of the direction
   * @return look-at angle in degrees
   */
  public static float getLookAtPitch(double dX, double dY, double dZ) {
    return getLookAtPitch(dY, length(dX, dZ));
  }

  /**
   * Gets the pitch angle in degrees to look into the direction specified
   *
   * @param dY axis of the direction
   * @param dXZ axis of the direction (length of x and z)
   * @return look-at angle in degrees
   */
  public static float getLookAtPitch(double dY, double dXZ) {
    return -atan(dY / dXZ);
  }

  public static float getLookAtPitch(Location loc, Location lookat) {
    return getLookAtPitch(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
  }

  /**
   * Gets the inverse tangent of the value in degrees
   *
   * @param value
   * @return inverse tangent angle in degrees
   */
  public static float atan(double value) {
    return (float) Math.toDegrees(CommonTrigMath.atan(value));
  }

  /**
   * Gets the inverse tangent angle in degrees of the rectangle vector
   *
   * @param y axis
   * @param x axis
   * @return inverse tangent 2 angle in degrees
   */
  public static float atan2(double y, double x) {
    return (float) Math.toDegrees(CommonTrigMath.atan2(y, x));
  }

  public static double length(double... values) {
    return Math.sqrt(lengthSquared(values));
  }

  public static double lengthSquared(double... values) {
    double rval = 0;
    for (double value : values) {
      rval += value * value;
    }
    return rval;
  }

  /**
   * Credits for this class goes to user aioobe on stackoverflow.com
   * Source: http://stackoverflow.com/questions/4454630/j2me-calculate-the-the-distance-between-2-latitude-and-longitude
   * <br>
   * Copied from org.bukkit.craftbukkit.TrigMath to get around package versioning issues without losing efficiency.
   * Please guys, stop hiding the good stuff when this should definitely be part of the API!
   */
  private static class CommonTrigMath {

    static final double sq2p1 = 2.414213562373095048802e0;
    static final double sq2m1 = .414213562373095048802e0;
    static final double p4 = .161536412982230228262e2;
    static final double p3 = .26842548195503973794141e3;
    static final double p2 = .11530293515404850115428136e4;
    static final double p1 = .178040631643319697105464587e4;
    static final double p0 = .89678597403663861959987488e3;
    static final double q4 = .5895697050844462222791e2;
    static final double q3 = .536265374031215315104235e3;
    static final double q2 = .16667838148816337184521798e4;
    static final double q1 = .207933497444540981287275926e4;
    static final double q0 = .89678597403663861962481162e3;
    static final double PIO2 = 1.5707963267948966135E0;

    private static double mxatan(double arg) {
      double argsq = arg * arg, value;

      value = ((((p4 * argsq + p3) * argsq + p2) * argsq + p1) * argsq + p0);
      value = value / (((((argsq + q4) * argsq + q3) * argsq + q2) * argsq + q1) * argsq + q0);
      return value * arg;
    }

    private static double msatan(double arg) {
      return arg < sq2m1 ? mxatan(arg)
        : arg > sq2p1 ? PIO2 - mxatan(1 / arg)
          : PIO2 / 2 + mxatan((arg - 1) / (arg + 1));
    }

    public static double atan(double arg) {
      return arg > 0 ? msatan(arg) : -msatan(-arg);
    }

    public static double atan2(double arg1, double arg2) {
      if (arg1 + arg2 == arg1) {
        return arg1 >= 0 ? PIO2 : -PIO2;
      }
      arg1 = atan(arg1 / arg2);
      return arg2 < 0 ? arg1 <= 0 ? arg1 + Math.PI : arg1 - Math.PI : arg1;
    }
  }
}

