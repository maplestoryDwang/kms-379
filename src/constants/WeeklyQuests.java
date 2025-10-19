package constants;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeeklyQuests {
   private static final List<Integer> Haven = new ArrayList<>(
      Arrays.asList(
         39101,
         39102,
         39103,
         39104,
         39105,
         39106,
         39107,
         39108,
         39111,
         39112,
         39113,
         39114,
         39115,
         39116,
         39117,
         39118,
         39119,
         39121,
         39122,
         39123,
         39124,
         39125,
         39126,
         39127,
         39131,
         39132,
         39133,
         39134,
         39135,
         39136,
         39141,
         39142,
         39143,
         39144,
         39145,
         39146,
         39147,
         39148,
         39149,
         39150,
         39151,
         39152,
         39153,
         39154,
         39155,
         39160,
         39161,
         39163,
         39164
      )
   );
   private static final List<Integer> DarkWorldTree = new ArrayList<>(
      Arrays.asList(39003, 39004, 39005, 39006, 39007, 39008, 39009, 39010, 39011, 39012, 15708)
   );
   public static final ImmutableMap<Integer, List<Integer>> dailyQuests = ImmutableMap.<Integer, List<Integer>>builder().put(39165, Haven).put(39002, DarkWorldTree).build();
}
