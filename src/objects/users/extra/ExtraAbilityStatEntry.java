package objects.users.extra;

public class ExtraAbilityStatEntry {
   private ExtraAbilityOption option;
   private int value;

   public ExtraAbilityStatEntry(ExtraAbilityOption option, int value) {
      this.setOption(option);
      this.setValue(value);
   }

   public ExtraAbilityOption getOption() {
      return this.option;
   }

   public void setOption(ExtraAbilityOption option) {
      this.option = option;
   }

   public int getValue() {
      return this.value;
   }

   public void setValue(int value) {
      this.value = value;
   }
}
