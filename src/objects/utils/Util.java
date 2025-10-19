package objects.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Util {
   private static Map<Class, Class> boxedToPrimClasses = new HashMap<>();

   public static Class<?> convertBoxedToPrimitiveClass(Class<?> clazz) {
      return boxedToPrimClasses.getOrDefault(clazz, clazz);
   }

   public static boolean succeedProp(int chance, int max) {
      Random random = new Random();
      return random.nextInt(max) < chance;
   }

   public static boolean succeedProp(int chance) {
      return succeedProp(chance, 100);
   }

   static {
      boxedToPrimClasses.put(Boolean.class, boolean.class);
      boxedToPrimClasses.put(Byte.class, byte.class);
      boxedToPrimClasses.put(Short.class, short.class);
      boxedToPrimClasses.put(Character.class, char.class);
      boxedToPrimClasses.put(Integer.class, int.class);
      boxedToPrimClasses.put(Long.class, long.class);
      boxedToPrimClasses.put(Float.class, float.class);
      boxedToPrimClasses.put(Double.class, double.class);
   }
}
