package dev.crystall.playernpclib.manager;

/**
 * The current entity visibility policy.
 * <p>
 * Created by CrystallDEV on 03/12/2020
 *
 * @author Kristian
 */
public enum EntityHidePolicy {
  /**
   * All entities are invisible by default. Only entities specifically made visible may be seen.
   */
  WHITELIST,

  /**
   * All entities are visible by default. An entity can only be hidden explicitly.
   */
  BLACKLIST,
}
