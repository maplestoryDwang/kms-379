package objects.fields.fieldset.childs;

import database.DBConfig;
import java.util.ArrayList;
import java.util.Arrays;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstanceMap;

public class ChaosKalosEnter extends FieldSet {
   public ChaosKalosEnter(int channel) {
      super("ChaosKalosEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(410006300, 410006320, 410006340, 410006360, 410006380))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 265;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "kalos_clear";
      this.bossName = "칼로스";
      this.difficulty = "카오스";
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
