package constants;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DailyQuests {
   private static final List<Integer> VanishingJourney = new ArrayList<>(
      Arrays.asList(
         34130,
         34131,
         34132,
         34133,
         34134,
         34135,
         34136,
         34137,
         34138,
         34139,
         34140,
         34141,
         34142,
         34143,
         34144,
         34145,
         34146,
         34147,
         34148,
         34149,
         34150,
         39055,
         39056,
         39057,
         39058,
         39059,
         39060,
         39061,
         39062,
         39063
      )
   );
   private static final List<Integer> ChewChew = new ArrayList<>(
      Arrays.asList(
         39017,
         39018,
         39019,
         39020,
         39021,
         39022,
         39023,
         39024,
         39025,
         39026,
         39027,
         39028,
         39029,
         39030,
         39031,
         39032,
         39033,
         39064,
         39065,
         39066,
         39067,
         39068,
         39069,
         39070
      )
   );
   private static final List<Integer> Lacheln = new ArrayList<>(
      Arrays.asList(34381, 34382, 34383, 34384, 34385, 34386, 34387, 34388, 34389, 34390, 34391, 34392, 34393, 34394)
   );
   private static final List<Integer> Arcana = new ArrayList<>(
      Arrays.asList(39038, 39039, 39040, 39041, 39042, 39043, 39044, 39045, 39046, 39047, 39048, 39049, 39050)
   );
   private static final List<Integer> Morass = new ArrayList<>(
      Arrays.asList(
         34276, 34277, 34278, 34279, 34280, 34281, 34282, 34283, 34284, 34285, 34286, 34287, 34288, 34289, 34290, 34291, 34292, 34293, 34294, 34295, 34296
      )
   );
   private static final List<Integer> Esfera = new ArrayList<>(
      Arrays.asList(34780, 34781, 34782, 34783, 34784, 34785, 34786, 34787, 34788, 34789, 34790, 34791, 34792, 34793, 34794, 34795, 34796, 34797, 34798, 34799)
   );
   private static final List<Integer> Tenebris = new ArrayList<>(
      Arrays.asList(
         35560,
         35561,
         35562,
         35563,
         35564,
         35565,
         35570,
         35571,
         35572,
         35573,
         35574,
         35575,
         35576,
         35577,
         35578,
         35579,
         35580,
         35581,
         35582,
         35583,
         35590,
         35591,
         35592,
         35593,
         35594
      )
   );
   private static final List<Integer> Sernium1 = new ArrayList<>(Arrays.asList(39820, 39821, 39822, 39823, 39824, 39825));
   private static final List<Integer> Sernium2 = new ArrayList<>(Arrays.asList(39923, 39924, 39925, 39926, 39927, 39928));
   private static final List<Integer> HotelArcs = new ArrayList<>(Arrays.asList(38151, 38152, 38153, 38154, 38155, 38156));
   public static final ImmutableMap<Integer, List<Integer>> dailyQuests = ImmutableMap.<Integer, List<Integer>>builder()
      .put(34129, VanishingJourney)
      .put(39014, ChewChew)
      .put(34378, Lacheln)
      .put(39035, Arcana)
      .put(34275, Morass)
      .put(34773, Esfera)
      .put(35550, Tenebris)
      .put(39819, Sernium1)
      .put(39922, Sernium2)
      .put(38150, HotelArcs)
      .build();
}
