package objects.users.skills;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class VMatrixOptionInfo {
   public int slotMax;
   public int equipSlotMin;
   public int equipSlotMax;
   public int specialSlotMax;
   public int extendLevel;
   public int extendAF;
   public int gradeMax;
   public int totalGradeMax;
   public int craftSkillCoreCost;
   public int craftEnchantCoreCost;
   public int craftSpecialCoreCost;
   public int craftGemstoneCost;
   public int matrixPointResetMeso;
   public int equipSlotEnhanceMax;

   public VMatrixOptionInfo(MapleData data) {
      this.slotMax = MapleDataTool.getInt("slotMax", data, 0);
      this.equipSlotMin = MapleDataTool.getInt("equipSlotMin", data, 0);
      this.equipSlotMax = MapleDataTool.getInt("equipSlotMax", data, 0);
      this.specialSlotMax = MapleDataTool.getInt("specialSlotMax", data, 0);
      this.extendLevel = MapleDataTool.getInt("extendLevel", data, 0);
      this.extendAF = MapleDataTool.getInt("extendAF", data, 0);
      this.gradeMax = MapleDataTool.getInt("gradeMax", data, 0);
      this.totalGradeMax = MapleDataTool.getInt("totalGradeMax", data, 0);
      this.craftSkillCoreCost = MapleDataTool.getInt("craftSkillCoreCost", data, 0);
      this.craftEnchantCoreCost = MapleDataTool.getInt("craftEnchantCoreCost", data, 0);
      this.craftSpecialCoreCost = MapleDataTool.getInt("craftSpecialCoreCost", data, 0);
      this.craftGemstoneCost = MapleDataTool.getInt("craftGemstoneCost", data, 0);
      this.matrixPointResetMeso = MapleDataTool.getInt("matrixPointResetMeso", data, 0);
      this.equipSlotEnhanceMax = MapleDataTool.getInt("equipSlotEnhanceMax", data, 0);
   }
}
