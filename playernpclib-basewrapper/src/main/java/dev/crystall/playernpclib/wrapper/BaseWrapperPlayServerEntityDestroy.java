package dev.crystall.playernpclib.wrapper;

/**
 * Created by CrystallDEV on 18/08/2021
 */
public interface BaseWrapperPlayServerEntityDestroy extends IBaseWrapper{

  /**
   * Retrieve Count.
   * <p>
   * Notes: length of following array
   *
   * @return The current Count
   */
  int getCount();

  /**
   * Retrieve Entity IDs.
   * <p>
   * Notes: the list of entities of destroy
   *
   * @return The current Entity IDs
   */
  int[] getEntityIDs();

  /**
   * Set Entity IDs.
   *
   * @param value - new value.
   */
  void setEntityIds(int[] value);
}
