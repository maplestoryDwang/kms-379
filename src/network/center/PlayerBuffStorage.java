package network.center;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import objects.users.MapleCoolDownValueHolder;
import objects.users.MapleDiseaseValueHolder;

public class PlayerBuffStorage implements Serializable {
   private static final Map<Integer, List<PlayerBuffValueHolder>> buffs = new ConcurrentHashMap<>();
   private static final Map<Integer, List<MapleCoolDownValueHolder>> coolDowns = new ConcurrentHashMap<>();
   private static final Map<Integer, List<MapleDiseaseValueHolder>> diseases = new ConcurrentHashMap<>();

   public static final void addBuffsToStorage(int chrid, List<PlayerBuffValueHolder> toStore) {
      buffs.put(chrid, toStore);
   }

   public static final void addCooldownsToStorage(int chrid, List<MapleCoolDownValueHolder> toStore) {
      coolDowns.put(chrid, toStore);
   }

   public static final void addDiseaseToStorage(int chrid, List<MapleDiseaseValueHolder> toStore) {
      diseases.put(chrid, toStore);
   }

   public static final List<PlayerBuffValueHolder> getBuffsFromStorage(int chrid) {
      return buffs.remove(chrid);
   }

   public static final List<MapleCoolDownValueHolder> getCooldownsFromStorage(int chrid) {
      return coolDowns.remove(chrid);
   }

   public static final List<MapleDiseaseValueHolder> getDiseaseFromStorage(int chrid) {
      return diseases.remove(chrid);
   }
}
