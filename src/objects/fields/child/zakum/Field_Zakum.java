package objects.fields.child.zakum;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.models.MobPacket;
import objects.fields.Field;
import objects.fields.MapleDynamicFoothold;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Field_Zakum extends Field {
   private static final int[] zakumTemplates = new int[]{8800002, 8800102};
   private static final int[] zakumArmsTemplates = new int[]{
      8800003,
      8800004,
      8800005,
      8800006,
      8800007,
      8800008,
      8800009,
      8800010,
      8800023,
      8800024,
      8800025,
      8800026,
      8800027,
      8800028,
      8800029,
      8800030,
      8800103,
      8800104,
      8800105,
      8800106,
      8800107,
      8800108,
      8800109,
      8800110,
      8800141,
      8800142,
      8800143,
      8800144,
      8800145,
      8800146,
      8800147,
      8800148
   };
   private static final int[] reviveArmsTemplates = new int[]{8800130, 8800131, 8800132, 8800133, 8800134, 8800135, 8800136, 8800137};
   private boolean started = false;
   private int phase = 1;
   private int dynamicObjectState = 0;
   private long flameTime = 0L;
   private long nextClapTime = 0L;
   private long nextPhase3AttackTime = 0L;

   public Field_Zakum(int mapid, int channel, int returnMapId, float monsterRate, MapleData root) {
      super(mapid, channel, returnMapId, monsterRate);
      this.loadAdditional(root);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
      this.started = false;
      this.phase = 1;
      this.dynamicObjectState = 0;
      this.flameTime = 0L;
      this.nextClapTime = 0L;
      this.nextPhase3AttackTime = 0L;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         this.onSendMobInitData(mob, player);
      }
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.isZakum(mob.getId())) {
         this.changePhase(1);
         this.changeZakumMode(1);
         int skillLevel = 0;
         switch (mob.getId()) {
            case 8800002:
               skillLevel = 2;
               break;
            case 8800022:
               skillLevel = 3;
               break;
            case 8800102:
               skillLevel = 1;
         }

         if (skillLevel > 0) {
            MobSkillInfo msi = MobSkillFactory.getMobSkill(235, skillLevel);
            if (msi != null) {
               msi.applyEffect(null, mob, null, false);
            } else {
               System.out.println("msi is null");
            }
         }
      }

      if (mob.getStats().isSkeleton()) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               this.onSendMobInitData(mob, player);
            }
         }
      }

      if (this.getZakumArmsCount() > 0 && !this.started) {
         this.started = true;
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      if (mob.getId() == 8800002 || mob.getId() == 8800102) {
         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            this.removeMonster(m, 1);
         }

         if (!DBConfig.isGanglim) {
            for (MapleCharacter p : this.getCharactersThreadsafe()) {
               if (p.getQuestStatus(2000002) == 1 && p.getOneInfo(2000002, "zakum") == null) {
                  p.updateOneInfo(2000002, "zakum", "1");
               }
            }
         }
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      long now = System.currentTimeMillis();
      super.fieldUpdatePerSeconds();
      MapleMonster zakum = this.getZakum();
      if (this.started) {
         if (this.dynamicObjectState == 0 && this.flameTime != 0L && this.flameTime <= now && this.phase != 4) {
            this.beginFlame();
            this.flameTime = System.currentTimeMillis() + 10000L;
            this.nextClapTime = System.currentTimeMillis() + 5000L;
         }

         if (this.dynamicObjectState == 1 && this.getMobsSize(8800120) > 0
            || this.phase == 3 && this.flameTime != 0L && this.flameTime <= now
            || this.phase == 4) {
            this.endFlame();
         }

         if (this.phase <= 2 && this.getZakumArmsCount() == 0) {
            this.changePhase(3);
            this.changeZakumMode(3);
         }

         if (this.phase <= 3 && zakum != null && zakum.getHPPercent() < 20) {
            this.changePhase(4);
            this.changeZakumMode(4);
         }

         if (this.phase == 2) {
            this.onProcessPhase2(now);
         }

         if (this.phase >= 3) {
            for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
               if ((this.isZakumArm(mob.getId()) || isReviveZakumArm(mob.getId()) || mob.getId() == 8800117)
                  && mob.getId() != 8800105
                  && mob.getId() != 8800109) {
                  this.removeMonster(mob, 1);
               }
            }
         }
      }
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
      this.flameTime = System.currentTimeMillis() + 5000L;
   }

   public void onProcessPhase2(long now) {
      MapleMonster[][] mobArray = new MapleMonster[3][2];

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (this.isZakumArm(mob.getId())) {
            int zakumArmIndex = getZakumArmIndex(mob.getId());
            if (zakumArmIndex >= 0 && zakumArmIndex <= 2) {
               mobArray[zakumArmIndex][0] = mob;
            }

            if (zakumArmIndex >= 4 && zakumArmIndex <= 6) {
               mobArray[zakumArmIndex - 4][1] = mob;
            }
         }
      }

      List<Pair<MapleMonster, MapleMonster>> list = new ArrayList<>();

      for (int index = 0; index < 3; index++) {
         if (mobArray[index][0] != null && mobArray[index][1] != null) {
            list.add(new Pair<>(mobArray[index][0], mobArray[index][1]));
         }
      }

      if (list.size() > 0) {
         Collections.shuffle(list);
         if (this.nextClapTime <= now) {
            this.nextClapTime = now + 3000L;
            Pair<MapleMonster, MapleMonster> pick = list.stream().findAny().orElse(null);
            if (pick != null) {
               this.doClap(pick.left);
               this.doClap(pick.right);
            }
         }
      }
   }

   public void doClap(MapleMonster mob) {
      int skillIdx = 2;
      this.broadcastMessage(MobPacket.mobForcedSkillAction(mob.getObjectId(), skillIdx, false));
      MobSkill ms = mob.getSkills().get(skillIdx);
      if (ms != null) {
         MobSkillInfo msi = MobSkillFactory.getMobSkill(ms.getMobSkillID(), ms.getLevel());
         if (msi != null) {
            this.broadcastMessage(MobPacket.mobSkillDelay(mob.getObjectId(), 2400, ms.getMobSkillID(), ms.getLevel(), 0, Collections.EMPTY_LIST));
         }
      }
   }

   public void loadAdditional(MapleData data) {
      MapleDynamicFoothold dynamicFoothold = new MapleDynamicFoothold(System.currentTimeMillis() + 2147483647L);

      for (int i = 0; i <= 7; i++) {
         MapleData layer = data.getChildByPath(String.valueOf(i));
         if (layer != null) {
            MapleData obj = layer.getChildByPath("obj");
            if (obj != null) {
               for (MapleData o : obj) {
                  int dynamic = MapleDataTool.getInt("dynamic", o, 0);
                  if (dynamic != 0) {
                     String tags = MapleDataTool.getString("tags", o, null);
                     String name = MapleDataTool.getString("name", o, null);
                     int x = MapleDataTool.getInt("x", o, 0);
                     int y = MapleDataTool.getInt("y", o, 0);
                     dynamicFoothold.putDynamicFootholdRealName(name, 0, new Point(x, y));
                  }
               }
            }
         }
      }

      this.spawnDynamicFoothold(dynamicFoothold);
   }

   private void onSendMobInitData(MapleMonster mob, MapleCharacter player) {
      if (mob.getStats().isBoss()) {
         if (!this.isZakumArm(mob.getId()) && !isReviveZakumArm(mob.getId())) {
            mob.sendAttackPriority(player);
         }

         if (mob.getStats().isSkeleton()) {
            mob.sendMobPhaseChange(player);
         }
      }
   }

   public void changePhase(int phase) {
      this.phase = phase;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         mob.setPhase(phase);
         mob.broadcastMobPhaseChange();
      }

      if (this.phase != 3) {
         this.nextPhase3AttackTime = 0L;
      } else {
         this.nextPhase3AttackTime = System.currentTimeMillis() + 3000L;
         this.flameTime = System.currentTimeMillis() + 15000L;
      }
   }

   public void changeZakumMode(int mode) {
      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (this.isZakum(mob.getId())) {
            switch (mode) {
               case 1:
                  mob.removeAttackPriority(0, 0);
                  mob.removeAttackPriority(0, 1);
                  if (this.phase <= 2) {
                     mob.addAllowedFsmSkill(0);
                  }
                  break;
               case 2:
                  mob.addAttackPriority(0, 0);
                  mob.addAttackPriority(0, 1);
                  mob.removeAllowedFsmSkill(0);
                  break;
               case 3:
                  this.resetFieldCommand();
                  mob.clearAttackPriority();
                  mob.addAttackPriority(0, 4);
                  mob.addAttackPriority(1, 2);
                  mob.addAttackPriority(1, 3);
                  mob.removeAllowedFsmSkill(0);
                  break;
               case 4:
                  this.resetFieldCommand();
                  mob.clearAttackPriority();
                  mob.addAttackPriority(0, 8);
                  mob.addAttackPriority(1, 6);
                  mob.addAttackPriority(1, 7);
                  mob.removeAllowedFsmSkill(0);
            }

            mob.broadcastAttackPriority();
         }

         if (this.isZakumArm(mob.getId())) {
            if (mode == 1) {
               mob.removeAttackPriority(0, 0);
            } else if (mode == 2) {
               mob.addAttackPriority(0, 0);
            }

            mob.broadcastAttackPriority();
         }
      }
   }

   private void beginFlame() {
      this.dynamicObjectState = 1;
      if (this.getNumMonsters() > 0) {
         this.setDynamicObjectVisible(null, true);
      }

      if (this.phase == 1) {
         this.changePhase(2);
      }

      this.changeZakumMode(2);
   }

   private void endFlame() {
      this.dynamicObjectState = 0;
      if (this.phase == 2) {
         this.changePhase(1);
      }

      this.changeZakumMode(1);

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (mob.getId() == 8800117) {
            this.removeMonster(mob, 1);
         }
      }

      this.setDynamicObjectVisible(null, false);
      if (this.phase != 3) {
         this.flameTime = 0L;
      } else {
         this.flameTime = System.currentTimeMillis() + 15000L;
      }
   }

   public boolean isZakumArm(int mobTemplateID) {
      for (int id : zakumArmsTemplates) {
         if (mobTemplateID == id) {
            return true;
         }
      }

      return false;
   }

   public static boolean isReviveZakumArm(int mobTemplateID) {
      for (int id : reviveArmsTemplates) {
         if (mobTemplateID == id) {
            return true;
         }
      }

      return false;
   }

   public boolean isZakum(int mobTemplateID) {
      for (int id : zakumTemplates) {
         if (mobTemplateID == id) {
            return true;
         }
      }

      return false;
   }

   public MapleMonster getZakum() {
      for (int id : zakumTemplates) {
         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            if (mob.getId() == id) {
               return mob;
            }
         }
      }

      return null;
   }

   public static int getZakumArmIndex(int templateID) {
      int t = --templateID % 10;
      int num = templateID / 10;
      if (num > 880002) {
         if (num == 880010) {
            return t - 2;
         }

         if (num == 880014) {
            return t;
         }
      } else if (num == 880000 || num == 880002) {
         return t - 2;
      }

      return t - 2;
   }

   public static Point getZakumHandClapDamagePos(int level) {
      switch (level) {
         case 0:
            return new Point(-10, -197);
         case 1:
            return new Point(-10, -112);
         case 2:
            return new Point(-10, -18);
         default:
            return new Point(0, 0);
      }
   }

   public static Point getZakumHandThrowingDamagePos(int index) {
      switch (index) {
         case 0:
            return new Point(-98, 86);
         case 1:
            return new Point(-221, 86);
         case 2:
            return new Point(-348, 86);
         case 3:
            return new Point(-472, 86);
         case 4:
            return new Point(54, 86);
         case 5:
            return new Point(194, 86);
         case 6:
            return new Point(341, 86);
         case 7:
            return new Point(484, 86);
         default:
            return new Point(0, 0);
      }
   }

   private int getZakumArmsCount() {
      return (int)this.getAllMonstersThreadsafe().stream().filter(m -> this.isZakumArm(m.getId())).count();
   }

   public long getNextPhase3AttackTime() {
      return this.nextPhase3AttackTime;
   }

   public void setNextPhase3AttackTime(long nextPhase3AttackTime) {
      this.nextPhase3AttackTime = nextPhase3AttackTime;
   }
}
