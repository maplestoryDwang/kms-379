package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.fields.fieldset.FieldSetInstance;
import objects.users.MapleCharacter;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Party {
   private int partyMemberCountMin;
   private int partyMemberCountMax;
   private int enterFieldsetPartyMemberCountMin;
   private int enterFieldsetPartyMemberCountMax;
   private Party.PartyMember partyMember;

   public Party(MapleData root) {
      MapleData partyMemberCount = root.getChildByPath("party_member_count");
      this.setPartyMemberCountMin(MapleDataTool.getInt("min", partyMemberCount, 0));
      this.setPartyMemberCountMax(MapleDataTool.getInt("max", partyMemberCount, 0));
      MapleData enterFieldsetPartyMemberCount = root.getChildByPath("enter_fieldset_party_member_count");
      this.setEnterFieldsetPartyMemberCountMin(MapleDataTool.getInt("min", enterFieldsetPartyMemberCount, 0));
      this.setEnterFieldsetPartyMemberCountMax(MapleDataTool.getInt("max", enterFieldsetPartyMemberCount, 0));
      MapleData d = root.getChildByPath("party_member");
      if (d != null) {
         this.setPartyMember(new Party.PartyMember(d));
      }
   }

   public boolean check(List<MapleCharacter> partyMembers, FieldSetInstance fieldSet) {
      if (partyMembers != null && !partyMembers.isEmpty()) {
         if (this.partyMemberCountMin <= 0 || this.partyMemberCountMin <= partyMembers.size() && this.partyMemberCountMax >= partyMembers.size()) {
            if (this.enterFieldsetPartyMemberCountMin > 0) {
               if (fieldSet == null) {
                  return false;
               }

               if (this.enterFieldsetPartyMemberCountMin > fieldSet.userList.size() || this.enterFieldsetPartyMemberCountMax < fieldSet.userList.size()) {
                  return false;
               }
            }

            return this.partyMember != null ? this.partyMember.check(partyMembers) : true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int getPartyMemberCountMin() {
      return this.partyMemberCountMin;
   }

   public void setPartyMemberCountMin(int partyMemberCountMin) {
      this.partyMemberCountMin = partyMemberCountMin;
   }

   public int getPartyMemberCountMax() {
      return this.partyMemberCountMax;
   }

   public void setPartyMemberCountMax(int partyMemberCountMax) {
      this.partyMemberCountMax = partyMemberCountMax;
   }

   public int getEnterFieldsetPartyMemberCountMin() {
      return this.enterFieldsetPartyMemberCountMin;
   }

   public void setEnterFieldsetPartyMemberCountMin(int enterFieldsetPartyMemberCountMin) {
      this.enterFieldsetPartyMemberCountMin = enterFieldsetPartyMemberCountMin;
   }

   public int getEnterFieldsetPartyMemberCountMax() {
      return this.enterFieldsetPartyMemberCountMax;
   }

   public void setEnterFieldsetPartyMemberCountMax(int enterFieldsetPartyMemberCountMax) {
      this.enterFieldsetPartyMemberCountMax = enterFieldsetPartyMemberCountMax;
   }

   public Party.PartyMember getPartyMember() {
      return this.partyMember;
   }

   public void setPartyMember(Party.PartyMember partyMember) {
      this.partyMember = partyMember;
   }

   private class PartyMember {
      private AchievementConditionType condition;
      private List<Party.PartyMember.Values> valueList;

      public PartyMember(MapleData root) {
         this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "and"));
         this.valueList = new ArrayList<>();
         MapleData values = root.getChildByPath("values");
         if (values != null) {
            int index = 0;

            while (true) {
               MapleData data = values.getChildByPath(String.valueOf(index++));
               if (data == null) {
                  break;
               }

               this.valueList.add(new Party.PartyMember.Values(data));
            }
         }
      }

      public boolean check(List<MapleCharacter> partyMembers) {
         if (partyMembers != null && !partyMembers.isEmpty()) {
            switch (this.condition) {
               case and:
                  int index = 0;

                  for (Party.PartyMember.Values valuesx : this.valueList) {
                     if (!valuesx.check(partyMembers.get(index++))) {
                        return false;
                     }
                  }
               default:
                  return true;
               case or:
                  boolean check = false;
                  index = 0;

                  for (Party.PartyMember.Values values : this.valueList) {
                     if (values.check(partyMembers.get(index++))) {
                        check = true;
                     }
                  }

                  return check;
            }
         } else {
            return false;
         }
      }

      private class Values {
         private AchievementConditionType condition;
         private List<Integer> characterJobCodeList;
         private Party.PartyMember.Values.FieldsetIndividualDeathCount fieldsetIndividualDeathCountList;

         public Values(MapleData root) {
            this.setCondition(AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or")));
            this.setCharacterJobCodeList(new ArrayList<>());
            MapleData fieldsetIndividualDeathCount = root.getChildByPath("fieldset_individual_death_count");
            if (fieldsetIndividualDeathCount != null) {
               this.setFieldsetIndividualDeathCountList(new Party.PartyMember.Values.FieldsetIndividualDeathCount(fieldsetIndividualDeathCount));
            }

            MapleData values = root.getChildByPath("values");
            if (values != null) {
               int index = 0;

               while (true) {
                  MapleData data = values.getChildByPath(String.valueOf(index++));
                  if (data == null) {
                     break;
                  }

                  this.getCharacterJobCodeList().add(MapleDataTool.getInt("character_jobcode", data, 0));
               }
            }
         }

         public boolean check(MapleCharacter player) {
            switch (this.condition) {
               case and:
                  if (this.characterJobCodeList != null && !this.characterJobCodeList.isEmpty()) {
                     Integer job = Integer.valueOf(player.getJob());
                     if (!this.characterJobCodeList.contains(job)) {
                        return false;
                     }
                  }

                  if (this.fieldsetIndividualDeathCountList != null && !this.fieldsetIndividualDeathCountList.check(player.getDecrementDeathCount())) {
                     return false;
                  }
               default:
                  return true;
               case or:
                  boolean check = false;
                  if (this.characterJobCodeList != null && !this.characterJobCodeList.isEmpty()) {
                     Integer job = Integer.valueOf(player.getJob());
                     if (this.characterJobCodeList.contains(job)) {
                        check = true;
                     }
                  }

                  if (this.fieldsetIndividualDeathCountList != null && this.fieldsetIndividualDeathCountList.check(player.getDecrementDeathCount())) {
                     check = true;
                  }

                  return check;
            }
         }

         public AchievementConditionType getCondition() {
            return this.condition;
         }

         public void setCondition(AchievementConditionType condition) {
            this.condition = condition;
         }

         public List<Integer> getCharacterJobCodeList() {
            return this.characterJobCodeList;
         }

         public void setCharacterJobCodeList(List<Integer> characterJobCodeList) {
            this.characterJobCodeList = characterJobCodeList;
         }

         public Party.PartyMember.Values.FieldsetIndividualDeathCount getFieldsetIndividualDeathCountList() {
            return this.fieldsetIndividualDeathCountList;
         }

         public void setFieldsetIndividualDeathCountList(Party.PartyMember.Values.FieldsetIndividualDeathCount fieldsetIndividualDeathCountList) {
            this.fieldsetIndividualDeathCountList = fieldsetIndividualDeathCountList;
         }

         private class FieldsetIndividualDeathCount {
            private int min;
            private int max;

            public FieldsetIndividualDeathCount(MapleData root) {
               this.setMin(MapleDataTool.getInt("min", root, 0));
               this.setMax(MapleDataTool.getInt("max", root, 0));
            }

            public boolean check(int decrementDeathCount) {
               return decrementDeathCount >= this.min && decrementDeathCount <= this.max;
            }

            public int getMin() {
               return this.min;
            }

            public void setMin(int min) {
               this.min = min;
            }

            public int getMax() {
               return this.max;
            }

            public void setMax(int max) {
               this.max = max;
            }
         }
      }
   }
}
