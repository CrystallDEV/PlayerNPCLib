package dev.crystall.playernpclib.wrapper;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by CrystallDEV on 19/08/2021
 */
public class WrapperGenerator<T> {

  public T result;

  public WrapperGenerator(Class<?> aClass) {
    try {
      result = (T) aClass.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
