package objects.users.extra;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import objects.utils.Randomizer;

public class ExtraAbilityEntry {
   private ExtraAbilityOption option;
   private int weight;
   private ExtraAbilityEntry.ValueAndWeight[] value;

   public ExtraAbilityEntry(ExtraAbilityOption option, int weight, ExtraAbilityEntry.ValueAndWeight... value) {
      this.setOption(option);
      this.setWeight(weight);
      this.value = value;
   }

   public ExtraAbilityOption getOption() {
      return this.option;
   }

   public void setOption(ExtraAbilityOption option) {
      this.option = option;
   }

   public boolean isMaxValue(int value) {
      int index = 0;

      for (ExtraAbilityEntry.ValueAndWeight vaw : this.value) {
         if (index == this.value.length - 1 && vaw.value <= value) {
            return true;
         }

         index++;
      }

      return false;
   }

   public int pickValue(boolean lucky) {
      if (lucky) {
         return this.value[this.value.length - 1].value;
      } else {
         ExtraAbilityEntry.ValueAndWeight[] arr = Arrays.copyOf(this.value, this.value.length);
         int[] values = new int[arr.length];
         int[] weights = new int[arr.length];

         for (int i = 0; i < values.length; i++) {
            values[i] = arr[i].value;
         }

         for (int i = 0; i < weights.length; i++) {
            weights[i] = arr[i].weight;
         }

         List<ExtraAbilityEntry.ValueAndWeight> list = Arrays.asList(arr);
         Collections.shuffle(list);
         int maxWeight = 0;

         for (int weight : weights) {
            maxWeight += weight;
         }

         int rand = Randomizer.rand(1, maxWeight);
         int v = 0;

         for (ExtraAbilityEntry.ValueAndWeight vaw : list) {
            v += vaw.weight;
            if (rand <= v) {
               return vaw.value;
            }
         }

         return -1;
      }
   }

   public static void main(String[] args) {
      ExtraAbilityEntry entry = new ExtraAbilityEntry(
         ExtraAbilityOption.AllStatR,
         1,
         new ExtraAbilityEntry.ValueAndWeight(1, 10),
         new ExtraAbilityEntry.ValueAndWeight(2, 7),
         new ExtraAbilityEntry.ValueAndWeight(3, 6),
         new ExtraAbilityEntry.ValueAndWeight(4, 4),
         new ExtraAbilityEntry.ValueAndWeight(5, 1)
      );
      int[] pickCount = new int[5];

      for (int i = 0; i < 280; i++) {
         int pick = entry.pickValue(false);
         pickCount[pick - 1]++;
      }

      int index = 0;

      for (int count : pickCount) {
         System.out.println(index++ + " " + count);
      }
   }

   public int getWeight() {
      return this.weight;
   }

   public void setWeight(int weight) {
      this.weight = weight;
   }

   public static class ValueAndWeight {
      private int value;
      private int weight;

      public ValueAndWeight(int value, int weight) {
         this.setValue(value);
         this.setWeight(weight);
      }

      public int getValue() {
         return this.value;
      }

      public void setValue(int value) {
         this.value = value;
      }

      public int getWeight() {
         return this.weight;
      }

      public void setWeight(int weight) {
         this.weight = weight;
      }
   }
}
