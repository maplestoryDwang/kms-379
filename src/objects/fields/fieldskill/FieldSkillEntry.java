package objects.fields.fieldskill;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import network.encode.PacketEncoder;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class FieldSkillEntry {
   private Map<Integer, AttackInfo> attackInfos = new HashMap<>();
   private MuiltRayInfo muiltRayInfo = null;
   private final MapleData data;
   private Integer type;
   private Integer delay;
   private Point lt;
   private Point rb;
   private Map<Integer, List<ParadeInfo>> paradeInfoMap = new HashMap<>();
   private Map<Integer, List<PresetInfo>> presetInfo = new HashMap<>();

   public FieldSkillEntry(MapleData data) {
      this.data = data;
      MapleData multiRay = data.getChildByPath("MultiRay");
      if (multiRay != null) {
         this.muiltRayInfo = new MuiltRayInfo(multiRay);
      }

      MapleData areaWarninParadeInfo;
      if ((areaWarninParadeInfo = data.getChildByPath("areaWarninParadeInfo/preset")) != null) {
         for (MapleData mapleData3 : areaWarninParadeInfo.getChildren()) {
            int preset = Integer.parseInt(mapleData3.getName());
            ArrayList<ParadeInfo> presetInfos = new ArrayList<>();

            for (MapleData mapleData4 : mapleData3.getChildren()) {
               ParadeInfo info = new ParadeInfo(preset, mapleData4);
               presetInfos.add(info);
            }

            this.paradeInfoMap.put(preset, presetInfos);
         }
      }

      MapleData pointPreset;
      if ((pointPreset = data.getChildByPath("pointPreset")) != null) {
         for (MapleData presets : pointPreset.getChildren()) {
            int preset = Integer.parseInt(presets.getName());
            ArrayList<PresetInfo> presetInfos = new ArrayList<>();

            for (MapleData index : presets.getChildren()) {
               PresetInfo info = new PresetInfo(preset, index);
               presetInfos.add(info);
            }

            this.presetInfo.put(preset, presetInfos);
         }
      }

      MapleData mapleData2;
      if ((mapleData2 = data.getChildByPath("attackInfo")) != null) {
         for (MapleData index : mapleData2.getChildren()) {
            int attackIndex = Integer.parseInt(index.getName());
            AttackInfo attackInfo = new AttackInfo(attackIndex, index);
            this.getAttackInfos().put(attackIndex, attackInfo);
         }
      }

      MapleData lt;
      if ((lt = data.getChildByPath("lt")) != null) {
         this.lt = MapleDataTool.getPoint(lt);
      }

      MapleData rb;
      if ((rb = data.getChildByPath("rb")) != null) {
         this.rb = MapleDataTool.getPoint(rb);
      }

      MapleData delay;
      if ((delay = data.getChildByPath("delay")) != null) {
         this.delay = MapleDataTool.getInt(delay);
      }

      MapleData mapleData;
      if ((mapleData = data.getChildByPath("type")) != null) {
         this.type = MapleDataTool.getInt(mapleData);
      }
   }

   public void encode(PacketEncoder packet) {
      this.getAttackInfos().entrySet().stream().forEach(info -> {
         packet.write(1);
         info.getValue().encode(packet);
      });
      packet.write(0);
   }

   public Map<Integer, AttackInfo> getAttackInfos() {
      return this.attackInfos;
   }

   public List<ParadeInfo> getParadeInfosForPreset(int preset) {
      return this.paradeInfoMap.get(preset);
   }

   public List<PresetInfo> getPresetInfo(int preset) {
      return this.presetInfo.get(preset);
   }

   public int getRandPreset() {
      return ThreadLocalRandom.current().nextInt(0, this.presetInfo.size());
   }

   public MuiltRayInfo getMuiltRayInfo() {
      return this.muiltRayInfo;
   }

   public MapleData getData() {
      return this.data;
   }

   public Integer getType() {
      return this.type;
   }

   public Integer getDelay() {
      return this.delay;
   }

   public Point getLt() {
      return this.lt;
   }

   public Point getRb() {
      return this.rb;
   }
}
