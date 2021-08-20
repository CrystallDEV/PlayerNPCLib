package dev.crystall.playernpclib.api.skin;

/**
 * Created by CrystallDEV on 14/09/2020
 */
public interface Callback<T> {

  void call(T skinData);

  default void failed() {
  }
}
