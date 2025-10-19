package objects.fields.fieldset.childs;

import database.DBConfig;
import java.util.ArrayList;
import java.util.Arrays;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstanceMap;

public class EasyKalosEnter extends FieldSet {
   public EasyKalosEnter(int channel) {
      super("EasyKalosEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(410006200, 410006220, 410006240, 410006260, 410006280))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 265;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "kalos_clear";
      this.bossName = "칼로스";
      this.difficulty = "이지";
      this.dailyLimit = DBConfig.isGanglim ? 3 : 6;
   }

   @Override
   public void updateFieldSetPS() {
   }

   @Override
   public int enter(int CharacterID, int tier) {
      return 0;
   }

   @Override
   public String getQuestTime(int CharacterID) {
      return null;
   }

   @Override
   public void resetQuestTime() {
   }

   @Override
   public int startManually() {
      return 0;
   }
}
