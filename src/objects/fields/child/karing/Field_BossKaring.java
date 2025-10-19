package objects.fields.child.karing;

import java.util.ArrayList;
import java.util.List;
import network.decode.PacketDecoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.child.karing.Gauge.KaringGauge;
import objects.fields.child.karing.Gauge.KaringGaugeEntry;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;

public class Field_BossKaring extends Field {
   public KaringGaugeEntry entry;
   public List<MapleCharacter> goongiparty = new ArrayList<>();
   public List<MapleCharacter> doolparty = new ArrayList<>();
   public List<MapleCharacter> hondonparty = new ArrayList<>();
   public List<MapleCharacter> selectedParty = null;
   long nextShieldTime = 0L;

   public Field_BossKaring(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
      this.entry = new KaringGaugeEntry(1000, 500, 500, 500, false, false, false);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.nextShieldTime <= System.currentTimeMillis()) {
         this.nextShieldTime = System.currentTimeMillis() + 45000L;
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.getDoolParty().clear();
      this.getGoongiParty().clear();
      this.getHondonParty().clear();
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.UIPacket.endInGameDirectionMode(0));
      KaringGauge.InitPacket.PerilsGauge p = new KaringGauge.InitPacket.PerilsGauge(this.entry);
      p.broadcastPacket(this);
      KaringGauge.InitPacket.MentalityGauge m = new KaringGauge.InitPacket.MentalityGauge(this.entry);
      m.broadcastPacket(this);
   }

   @Override
   public void onUserHit(MapleCharacter player, int mobTemplateID, int skillID, int skillLevel, int attackIndex) {
      super.onUserHit(player, mobTemplateID, skillID, skillLevel, attackIndex);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
      int[] decreaseAmounts = new int[]{0, 120, 80, 70, 60, 50};
      int decreaseAmount = 200;
      int size = player.getPartyMemberSize();
      if (size >= 2 && size <= 6) {
         decreaseAmount = decreaseAmounts[size];
      }
   }

   @Override
   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      super.onMobSkill(mob, skillID, skillLevel);
      switch (skillID) {
         case 267:
            if (skillLevel == 3) {
               MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skillID, skillLevel);
               MobSkill mobSkill = MapleLifeFactory.getRealMobSkill(skillID, skillLevel);

               for (MapleCharacter player : this.getCharactersThreadsafe()) {
                  mobSkillInfo.applyEffect(player, mob, mobSkill, true, true, mob.getTruePosition());
               }
            }
         default:
            if (skillID == 277 && skillLevel == 1) {
            }
      }
   }

   public MapleMonster findBoss() {
      return this.getMonsterById(8880838);
   }

   public void doAction(PacketDecoder packet, MapleCharacter player) {
   }

   public KaringGaugeEntry getKaringGaugeEntry() {
      return this.entry;
   }

   public void setKaringGaugeEntry(KaringGaugeEntry entry) {
      this.entry = entry;
   }

   public List<MapleCharacter> getGoongiParty() {
      return this.goongiparty;
   }

   public void addGoongiParty(MapleCharacter player) {
      this.goongiparty.add(player);
   }

   public void removeGoongiParty(MapleCharacter player) {
      this.goongiparty.remove(player);
   }

   public List<MapleCharacter> getDoolParty() {
      return this.doolparty;
   }

   public void addDoolParty(MapleCharacter player) {
      this.doolparty.add(player);
   }

   public void removeDoolParty(MapleCharacter player) {
      this.doolparty.remove(player);
   }

   public List<MapleCharacter> getHondonParty() {
      return this.hondonparty;
   }

   public void addHondonParty(MapleCharacter player) {
      this.hondonparty.add(player);
   }

   public void removeHondonParty(MapleCharacter player) {
      this.hondonparty.remove(player);
   }

   public List<MapleCharacter> getAllParty() {
      return this.selectedParty;
   }

   public void setAllParty(List<MapleCharacter> selectedParty) {
      this.selectedParty = selectedParty;
   }

   public void isClear(int boss) {
      if (boss != 0 && boss != 1 && boss == 2) {
      }
   }

   public void sendMentality() {
      KaringGauge.InitPacket.MentalityGauge m = new KaringGauge.InitPacket.MentalityGauge(this.entry);
      m.broadcastPacket(this);
   }

   public void sendPerils() {
      KaringGauge.InitPacket.PerilsGauge m = new KaringGauge.InitPacket.PerilsGauge(this.entry);
      m.broadcastPacket(this);
   }
}
