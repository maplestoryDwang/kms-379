package objects.fields.child.lucid;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.EliteState;
import objects.fields.Field;
import objects.fields.fieldset.instance.HellLucidBoss;
import objects.fields.gameobject.lifes.BossLucid;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Randomizer;

public abstract class Field_LucidBattle extends Field {
   public static List<String> stainedGlasses;
   public static Map<String, Long> stainedGlasses_removed = new HashMap<>();
   protected long lastSubSummon;
   private long lastCheckStatueStack;
   public long lastCreateButterfly;
   public long lastCreateGolem;
   public int lastCreateGolemIndex;
   protected int phase;
   private int statueStack;
   private int lastHPCount = -1;
   private List<Integer> noticed = new ArrayList<>();
   private List<Rectangle> contagionFieldList = new ArrayList<>();
   protected List<Field_LucidBattle.Butterfly> butterflies;
   protected boolean illusionBarrage;
   private long lastCreateDragon = 0L;
   private boolean hellMode = false;

   public Field_LucidBattle(int mapid, int channel, int returnMapId, float monsterRate, int phase) {
      super(mapid, channel, returnMapId, monsterRate);
      this.phase = phase;
      this.butterflies = new ArrayList<>();
      this.statueStack = 0;
      stainedGlasses = new ArrayList<>();
      stainedGlasses.add("Bblue1");
      stainedGlasses.add("Bblue2");
      stainedGlasses.add("Bblue3");
      stainedGlasses.add("Bred1");
      stainedGlasses.add("Bred2");
      stainedGlasses.add("Bred3");
      stainedGlasses.add("Mred2");
      stainedGlasses.add("Mred3");
      stainedGlasses.add("Myellow1");
      stainedGlasses.add("Myellow2");
      stainedGlasses.add("Myellow3");
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.butterflies.clear();
      this.noticed.clear();
      this.illusionBarrage = false;
      this.lastHPCount = -1;
      this.statueStack = 0;
      this.lastCreateButterfly = 0L;
      this.lastCheckStatueStack = 0L;
      this.lastSubSummon = 0L;
      this.phase = 1;
      this.hellMode = false;
   }

   @Override
   public boolean isCanSummonSubMob(int skillID, int skillLevel) {
      return skillID == 201 && skillLevel >= 213 && skillLevel <= 221 ? System.currentTimeMillis() >= this.lastSubSummon + 10000L : true;
   }

   @Override
   public void setSummonSubMob(long time) {
      this.lastSubSummon = time;
   }

   public abstract MapleMonster getBoss();

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      this.updateButterfly();
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.sendLucidEnterFieldInfo(player);
      player.setCanAttackLucidRewardMob(false);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
         this.setHellMode(true);
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
         packet.writeInt(EliteState.EliteBoss.getType());
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeMapleAsciiString("Bgm46/WierldForestIntheGirlsdream");
         packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterEffect2");
         packet.writeMapleAsciiString("Effect/EventEffect.img/gloryWmission/screenEff");
         player.send(packet.getPacket());
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      MapleMonster boss = this.getBoss();
      if (boss != null && this.getBoss().getId() == mob.getId()) {
         if (boss.getStats().getHp() <= 0L) {
            this.butterflies.clear();
            this.sendLucidRemoveButterfly();

            for (MapleMonster m : this.getAllMonstersThreadsafe()) {
               this.removeMonster(m, 1);
            }

            return;
         }

         if (System.currentTimeMillis() >= this.lastCheckStatueStack + 500L) {
            int hpPercent = boss.getHPPercent();
            int hpCount = hpPercent / 10;
            if (this.lastHPCount != hpCount) {
               if (this.isHellMode()) {
                  if (hpCount % 2 == 1) {
                     if (hpCount < 8) {
                        this.setStatueStack(Math.min(3, this.statueStack + 1), false);
                        if (this.lastHPCount == -1) {
                           this.sendLucidNotice("나팔동상 근처에서 '채집'키를 눌러  사용하면 루시드의 힘을 억제할 수 있습니다!", 0);
                        }
                     }

                     this.lastHPCount = hpCount;
                     this.lastCheckStatueStack = System.currentTimeMillis();
                  }
               } else {
                  if (hpCount < 9) {
                     this.setStatueStack(Math.min(3, this.statueStack + 1), false);
                     if (this.lastHPCount == -1) {
                        this.sendLucidNotice("나팔동상 근처에서 '채집'키를 눌러  사용하면 루시드의 힘을 억제할 수 있습니다!", 0);
                     }
                  }

                  this.lastHPCount = hpCount;
                  this.lastCheckStatueStack = System.currentTimeMillis();
               }
            }
         }
      }
   }

   public void sendLucidEnterFieldInfo(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_BUTTERFLY_CREATE.getValue());
      packet.writeInt(this.phase);
      packet.writeInt(this.butterflies.size());

      for (Field_LucidBattle.Butterfly butterfly : this.butterflies) {
         packet.writeInt(butterfly.template);
         packet.writeInt(butterfly.position.x);
         packet.writeInt(butterfly.position.y);
      }

      player.send(packet.getPacket());
      this.sendLucidStatueDisplay(player, true);
      this.sendLucidStatueStack(player, this.statueStack, false);
   }

   public void sendLucidStatueDisplay(MapleCharacter player, boolean show) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_STATUE_STATE_CHANGE.getValue());
      packet.writeInt(1);
      packet.write(show);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidStatueStack(MapleCharacter player, int stack, boolean hornEffect) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_STATUE_STATE_CHANGE.getValue());
      packet.writeInt(0);
      packet.writeInt(stack);
      packet.write(hornEffect);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidAddButterfly(Field_LucidBattle.Butterfly butterfly) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_BUTTERFLY_ACTION.getValue());
      packet.writeInt(Field_LucidBattle.ButterflyMode.Add.getType());
      packet.writeInt(this.phase);
      packet.writeInt(butterfly.template);
      packet.writeInt(butterfly.position.x);
      packet.writeInt(butterfly.position.y);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidAttackButterfly(int count, int delay) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_BUTTERFLY_ACTION.getValue());
      packet.writeInt(Field_LucidBattle.ButterflyMode.Attack.getType());
      packet.writeInt(count);
      packet.writeInt(delay);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidMoveButterfly(Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_BUTTERFLY_ACTION.getValue());
      packet.writeInt(Field_LucidBattle.ButterflyMode.Move.getType());
      packet.writeInt(pos.x);
      packet.writeInt(pos.y);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidRemoveButterfly() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_BUTTERFLY_ACTION.getValue());
      packet.writeInt(Field_LucidBattle.ButterflyMode.Remove.getType());
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidShootAction(int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_WELCOME_BARRAGE.getValue());
      packet.writeInt(type);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidSkill_FairyDust(int level, List<Field_LucidBattle.FairyDust> fairyDusts) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_DO_SKILL.getValue());
      packet.writeInt(MobSkillID.LUCID_SKILL.getVal());
      packet.writeInt(level);
      packet.writeInt(fairyDusts.size());

      for (Field_LucidBattle.FairyDust fairyDust : fairyDusts) {
         packet.writeInt(fairyDust.scale);
         packet.writeInt(fairyDust.createDelay);
         packet.writeInt(fairyDust.moveSpeed);
         packet.writeInt(fairyDust.angle);
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidSkill_FlowerTrap(int level, int pattern, Point pt, boolean flip) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_DO_SKILL.getValue());
      packet.writeInt(MobSkillID.LUCID_SKILL.getVal());
      packet.writeInt(level);
      packet.writeInt(pattern);
      packet.writeInt(pt.x);
      packet.writeInt(pt.y);
      packet.write(flip);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidSkill_ForcedTeleport(MapleCharacter target, int splitID) {
      if (this.phase != 1 || this.lastCreateDragon == 0L || this.lastCreateDragon - System.currentTimeMillis() > 10000L) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.LUCID_DO_SKILL.getValue());
         packet.writeInt(MobSkillID.LUCID_SKILL.getVal());
         packet.writeInt(6);
         packet.writeInt(splitID);
         this.broadcastMessage(packet.getPacket());
      }
   }

   public void sendLucidSkill_LaserRain(int delay, List<Integer> intervals) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_DO_SKILL.getValue());
      packet.writeInt(MobSkillID.LUCID_SKILL.getVal());
      packet.writeInt(5);
      packet.writeInt(delay);
      packet.writeInt(intervals.size());

      for (Integer i : intervals) {
         packet.writeInt(i);
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidSkill_Rush(int path) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_DO_SKILL.getValue());
      packet.writeInt(MobSkillID.LUCID_SKILL.getVal());
      packet.writeInt(8);
      packet.writeInt(path);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidSpiralButterfly(
      int angle, int angleRate, int angleDiff, int speed, int interval, int shotCount, int bulletAngleRate, int bulletSpeedRate
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_WELCOME_BARRAGE.getValue());
      packet.writeInt(4);
      packet.writeInt(angle);
      packet.writeInt(angleRate);
      packet.writeInt(angleDiff);
      packet.writeInt(speed);
      packet.writeInt(interval);
      packet.writeInt(shotCount);
      packet.writeInt(bulletAngleRate);
      packet.writeInt(bulletSpeedRate);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidSpiralShoot(int angle, int angleRate, int angleDiff, int speed, int interval, int shotCount, int bulletAngleRate, int bulletSpeedRate) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_WELCOME_BARRAGE.getValue());
      packet.writeInt(4);
      packet.writeInt(angle);
      packet.writeInt(angleRate);
      packet.writeInt(angleDiff);
      packet.writeInt(speed);
      packet.writeInt(interval);
      packet.writeInt(shotCount);
      packet.writeInt(bulletAngleRate);
      packet.writeInt(bulletSpeedRate);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidBiDirectionShoot(int angleRate, int speed, int interval, int shotCount) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_WELCOME_BARRAGE.getValue());
      packet.writeInt(3);
      packet.writeInt(angleRate);
      packet.writeInt(speed);
      packet.writeInt(interval);
      packet.writeInt(shotCount);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidFlyingMode(MapleCharacter player, boolean onoff) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_SET_FLYING_MODE.getValue());
      packet.write(onoff);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidStainedGlassOnOff(boolean onOff, List<String> str) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_STAINED_GLASS_ON_OFF.getValue());
      packet.write(onOff);
      packet.writeInt(str.size());
      str.forEach(packet::writeMapleAsciiString);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidStainedGlassBreak(List<String> str) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_STAINED_GLASS_BREAK.getValue());
      packet.writeInt(str.size());
      str.forEach(packet::writeMapleAsciiString);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidStainedGlassOnOff(MapleCharacter player, boolean onoff) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID2_STAINED_GLASS_ON_OFF.getValue());
      packet.write(onoff);
      packet.writeInt(stainedGlasses.size());

      for (String t : stainedGlasses) {
         packet.writeMapleAsciiString(t);
      }

      if (player != null) {
         player.send(packet.getPacket());
      } else {
         this.broadcastMessage(packet.getPacket());
      }
   }

   public void sendLucidCreateDragon(Point pt, Point createPt, boolean left) {
      this.lastCreateDragon = System.currentTimeMillis();
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_DRAGON_CREATE.getValue());
      packet.writeInt(this.phase);
      packet.writeInt(pt.x);
      packet.writeInt(pt.y);
      packet.writeInt(createPt.x);
      packet.writeInt(createPt.y);
      packet.write(left);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidCreateContagionField(List<Pair<Rectangle, Integer>> ptList) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_DO_SKILL.getValue());
      packet.writeInt(MobSkillID.LUCID_SKILL.getVal());
      packet.writeInt(12);
      packet.writeInt(ptList.size());

      for (Pair<Rectangle, Integer> pt : ptList) {
         this.contagionFieldList.add(pt.left);
         packet.encodeRect(pt.left);
         packet.writeInt(pt.right);
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void setStatueStack(int stack, boolean use) {
      this.statueStack = stack;
      this.sendLucidStatueStack(null, this.statueStack, use);
   }

   public void onActivateStatue() {
      if (this.statueStack > 0) {
         this.butterflies.clear();
         this.setStatueStack(this.statueStack - 1, true);
         this.sendLucidRemoveButterfly();
      }
   }

   public void onContagionResult(int playerID) {
      MapleCharacter chr = this.getCharacterById(playerID);
      if (chr != null && this.getBoss() != null) {
         MobSkillInfo skill = MobSkillFactory.getMobSkill(176, 38);
         int playerCount = this.getNumPlayersInRect(skill.calculateBoundingBox(chr.getTruePosition(), chr.isFacingLeft()));
         if (playerCount <= 1) {
            for (Rectangle rect : this.contagionFieldList) {
               if (rect.x <= chr.getTruePosition().x
                  && chr.getTruePosition().x <= rect.x + rect.width
                  && Math.abs(rect.y - chr.getTruePosition().y) <= rect.width) {
                  playerCount = 2;
                  break;
               }
            }
         }

         this.contagionFieldList.clear();
         chr.onDamageByMobSkill(MobSkillID.DAMAGE, 38, skill, 0, this.getBoss().getId(), playerCount, skill.getMobSkillStatsInt(MobSkillStat.fixDamR));
      }
   }

   public void sendLucidNotice(String message, int type) {
      if (type >= 0) {
         if (this.noticed.contains(type)) {
            return;
         }

         this.noticed.add(type);
      }

      this.broadcastMessage(CField.sendWeatherEffectNotice(222, 2000, false, message));
   }

   private void updateButterfly() {
      MapleMonster lucid = this.getBoss();
      if (lucid != null) {
         int hpPercent = lucid.getHPPercent();

         for (BossLucid.ButterflyData.InfoData.Entry entry : BossLucid.butterfly.info.entries) {
            if (entry.hpMin <= hpPercent && hpPercent <= entry.hpMax) {
               this.onCreateButterfly(entry);
               return;
            }
         }
      }
   }

   private void onCreateButterfly(BossLucid.ButterflyData.InfoData.Entry entry) {
      if (!this.illusionBarrage) {
         if (this.phase != 3) {
            if (System.currentTimeMillis() >= this.lastCreateButterfly + entry.term) {
               List<Point> positions = this.getBufferflyPos();
               if (this.butterflies.size() >= positions.size()) {
                  this.onButterflyFull();
                  return;
               }

               this.createButterfly();
               this.lastCreateButterfly = System.currentTimeMillis();
            }
         }
      }
   }

   public void doFairyDust(MapleMonster mob, int skillLevel, MobSkillInfo msi) {
      int max = Randomizer.nextInt(msi.getMobSkillStatsInt(MobSkillStat.w2)) + msi.getMobSkillStatsInt(MobSkillStat.w);
      int x = msi.getMobSkillStatsInt(MobSkillStat.x);
      List<Field_LucidBattle.FairyDust> list = new ArrayList<>();

      for (int i = 0; i < max; i++) {
         x += Randomizer.nextInt(x);
         int createDelay = msi.getMobSkillStatsInt(MobSkillStat.u);
         int moveSpeed = msi.getMobSkillStatsInt(MobSkillStat.v) + Randomizer.nextInt(msi.getMobSkillStatsInt(MobSkillStat.v2));
         int angle = Randomizer.rand(msi.getMobSkillStatsInt(MobSkillStat.s), msi.getMobSkillStatsInt(MobSkillStat.s2));
         list.add(new Field_LucidBattle.FairyDust(Randomizer.nextInt(3), createDelay, moveSpeed, x + angle));
      }

      this.sendLucidSkill_FairyDust(skillLevel, list);
   }

   public void createButterfly() {
      List<Point> positions = this.getBufferflyPos();
      int count = Randomizer.rand(1, 3);

      for (int i = 0; i < count && this.butterflies.size() < positions.size(); i++) {
         Field_LucidBattle.Butterfly newButterfly = new Field_LucidBattle.Butterfly(Randomizer.nextInt(9), positions.get(this.butterflies.size()));
         this.butterflies.add(newButterfly);
         this.sendLucidAddButterfly(newButterfly);
         if (this.butterflies.size() == 33) {
            this.sendLucidNotice("꿈이 강해지고 있습니다. 조심하세요!", -1);
         }
      }
   }

   public int getPhase() {
      return this.phase;
   }

   public abstract List<Point> getBufferflyPos();

   public abstract void onButterflyFull();

   public boolean isHellMode() {
      return this.hellMode;
   }

   public void setHellMode(boolean hellMode) {
      this.hellMode = hellMode;
   }

   public class Butterfly {
      public Point position;
      public int template;

      public Butterfly(int template, Point pos) {
         this.position = pos;
         this.template = template;
      }
   }

   public static enum ButterflyMode {
      Add(0),
      Move(1),
      Attack(2),
      Remove(3);

      private int type;

      private ButterflyMode(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public class FairyDust {
      public int angle;
      public int createDelay;
      public int moveSpeed;
      public int scale;

      public FairyDust(int scale, int createDelay, int moveSpeed, int angle) {
         this.scale = scale;
         this.createDelay = createDelay;
         this.moveSpeed = moveSpeed;
         this.angle = angle;
      }
   }
}
