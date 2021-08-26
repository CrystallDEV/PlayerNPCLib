package dev.crystall.playernpclib.wrapper;

import java.lang.reflect.InvocationTargetException;
import lombok.NoArgsConstructor;

/**
 * Created by CrystallDEV on 19/08/2021
 */
@NoArgsConstructor
public class WrapperGenerator<T> {

  public T map(Class<? extends T> aClass, Object... initargs) {
    try {
      Class<?>[] types = new Class[initargs.length];
      int i = 0;
      for (Object o : initargs) {
        types[i] = o.getClass();
        i++;
      }
      return aClass.getDeclaredConstructor(types).newInstance(initargs);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
}
