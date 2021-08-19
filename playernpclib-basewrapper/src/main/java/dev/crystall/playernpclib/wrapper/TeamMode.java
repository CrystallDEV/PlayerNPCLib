package dev.crystall.playernpclib.wrapper;

import com.comphenix.protocol.reflect.IntEnum;

/**
 * Enum containing all known modes.
 *
 * @author dmulloy2
 */
public class TeamMode extends IntEnum {

  public static final int TEAM_CREATED = 0;
  public static final int TEAM_REMOVED = 1;
  public static final int TEAM_UPDATED = 2;
  public static final int PLAYERS_ADDED = 3;
  public static final int PLAYERS_REMOVED = 4;

  private static final TeamMode INSTANCE = new TeamMode();

  public static TeamMode getInstance() {
    return INSTANCE;
  }
}