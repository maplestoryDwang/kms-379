package objects.fields.gameobject.lifes;

public enum ElitePrefix {
   Prefix_0("힘 센", 100, 110),
   Prefix_1("튼튼한", 102, 112),
   Prefix_2("마법저항의", 103, 113),
   Prefix_3("재생하는", 114),
   Prefix_4("재빠른", 115),
   Prefix_5("봉인의", 120),
   Prefix_6("회피하는", 121),
   Prefix_7("허약의", 122),
   Prefix_8("기절시키는", 123),
   Prefix_9("저주의", 124),
   Prefix_10("맹독의", 125),
   Prefix_11("끈끈한", 126),
   Prefix_12("매혹의", 128),
   Prefix_13("독을 뿌리는", 131),
   Prefix_14("혼란의", 132),
   Prefix_15("언데드", 133),
   Prefix_16("포션을 싫어하는", 134),
   Prefix_17("멈추지 않는", 135),
   Prefix_18("암흑의", 136),
   Prefix_19("단단한", 142),
   Prefix_20("반사의", 145),
   Prefix_21("무적의", 146),
   Prefix_22("변신술사", 172),
   Prefix_23("석화의", 174),
   Prefix_24("자석의", 181),
   Prefix_25("역병의", 211),
   Prefix_26("지휘관", 212),
   Prefix_27("검은 사슬의", 213);

   private String prefix;
   private int[] skills;

   private ElitePrefix(String prefix, int... skills) {
      this.prefix = prefix;
      this.skills = skills;
   }

   public static String getElitePrefixBySkills(int skill) {
      for (ElitePrefix pre : values()) {
         for (int skills : pre.skills) {
            if (skill == skills) {
               return pre.prefix;
            }
         }
      }

      return null;
   }
}
