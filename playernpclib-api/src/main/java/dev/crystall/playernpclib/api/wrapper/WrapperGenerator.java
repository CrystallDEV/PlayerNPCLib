package dev.crystall.playernpclib.api.wrapper;

import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by CrystallDEV on 19/08/2021
 */
@Slf4j
public class WrapperGenerator {

  public static <T> T map(Class<? extends T> aClass, Object... initargs) {
    try {
      Class<?>[] types = new Class[initargs.length];
      int i = 0;
      for (Object o : initargs) {
        types[i] = o.getClass();
        i++;
      }
      return aClass.getDeclaredConstructor(types).newInstance(initargs);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      log.error("Unable to map class " + aClass.getSimpleName(), e);
    }
    return null;
  }
}
