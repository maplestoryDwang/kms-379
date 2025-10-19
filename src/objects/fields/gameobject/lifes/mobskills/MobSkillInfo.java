package objects.fields.gameobject.lifes.mobskills;

import constants.GameConstants;
import constants.JosaType;
import constants.Locales;
import constants.ServerConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.effect.EffectHeader;
import objects.effect.NormalEffect;
import objects.fields.DynamicObject;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SmartMobMsgType;
import objects.fields.SmartMobNoticeType;
import objects.fields.child.blackheaven.Field_BlackHeavenBoss;
import objects.fields.child.blackmage.Field_BlackMage;
import objects.fields.child.jinhillah.Field_JinHillah;
import objects.fields.child.jinhillah.JinHillahPoisonMist;
import objects.fields.child.lucid.Field_LucidBattle;
import objects.fields.child.lucid.Field_LucidBattlePhase1;
import objects.fields.child.lucid.Field_LucidBattlePhase2;
import objects.fields.child.papulatus.Field_Papulatus;
import objects.fields.child.rimen.Field_RimenNearTheEnd;
import objects.fields.child.sernium.Field_Seren;
import objects.fields.child.sernium.Field_SerenPhase2;
import objects.fields.child.sernium.SerenLaser;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.child.will.WillBeholder;
import objects.fields.child.zakum.Field_Zakum;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.BanishInfo;
import objects.fields.gameobject.lifes.BossWill;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.MoveAbility;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomAction;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleRadialInfo;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.MapleDiseaseValueHolder;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Triple;
import objects.wz.provider.MapleData;

public class MobSkillInfo {
   private int skillId;
   private int skillLevel;
   private int mpCon;
   private int spawnEffect;
   private int summonEffect;
   private int targetType;
   private int hp;
   private int x;
   private int y;
   private int force;
   private int forcex;
   private long duration;
   private long cooltime;
   private float prop;
   private HashMap<Integer, Integer> screen_delay = new HashMap<>();
   private short limit;
   private Map<MobSkillStat, String> mobSkillStats = new HashMap<>();
   private List<Integer> toSummon = new ArrayList<>();
   private Point lt;
   private Point rb;
   private Point lt2;
   private Point rb2;
   private boolean summonOnce;
   private List<Integer> summonIDs = new ArrayList<>();
   private List<List<Integer>> mobGroup = new ArrayList<>();
   private List<Point> fixedPos = new ArrayList<>();
   private List<Integer> fixedDir = new ArrayList<>();
   private JinHillahPoisonMist jMist = null;
   private FieldCommand fieldCommand = null;
   private List<AffectedOtherSkill> affectedOtherSkills = new ArrayList<>();
   private int otherSkillID = 0;
   private int otherSkillLev = 0;
   private MobSkillInfo.CastingActionData succeed;
   private MobSkillInfo.CastingActionData failed;

   public MobSkillInfo(int skillId, int level) {
      this.skillId = skillId;
      this.skillLevel = level;
   }

   public void setOnce(boolean o) {
      this.summonOnce = o;
   }

   public boolean onlyOnce() {
      return this.summonOnce;
   }

   public void setMpCon(int mpCon) {
      this.mpCon = mpCon;
   }

   public void addSummons(List<Integer> toSummon) {
      this.toSummon = toSummon;
   }

   public void setSpawnEffect(int spawnEffect) {
      this.spawnEffect = spawnEffect;
   }

   public void setSummonEffect(int summonEffect) {
      this.summonEffect = summonEffect;
   }

   public void setTargetType(int targetType) {
      this.targetType = targetType;
   }

   public int getSummonEffect() {
      return this.summonEffect;
   }

   public int getTargetType() {
      return this.targetType;
   }

   public void setHp(int hp) {
      this.hp = hp;
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public void setCoolTime(long cooltime) {
      this.cooltime = cooltime;
   }

   public void setProp(float prop) {
      this.prop = prop;
   }

   public void setLtRb(Point lt, Point rb) {
      this.lt = lt;
      this.rb = rb;
   }

   public void setLtRb2(Point lt2, Point rb2) {
      this.lt2 = lt2;
      this.rb2 = rb2;
   }

   public void setLimit(short limit) {
      this.limit = limit;
   }

   public boolean checkCurrentBuff(MapleCharacter player, MapleMonster monster) {
      boolean stop = false;
      MobSkillID msi = MobSkillID.getMobSkillIDByValue(this.skillId);
      switch (msi) {
         case POWERUP:
         case POWERUP_M:
         case PAD:
            stop = monster.isBuffed(MobTemporaryStatFlag.POWER_UP);
            break;
         case MAGICUP:
         case MAGICUP_M:
         case MAD:
            stop = monster.isBuffed(MobTemporaryStatFlag.MAGIC_UP);
            break;
         case PGUARDUP:
         case PGUARDUP_M:
         case PDR:
            stop = monster.isBuffed(MobTemporaryStatFlag.P_GUARD_UP);
            break;
         case MGUARDUP:
         case MGUARDUP_M:
         case MDR:
            stop = monster.isBuffed(MobTemporaryStatFlag.M_GUARD_UP);
            break;
         case PHYSICAL_IMMUNE:
         case MAGIC_IMMUNE:
         case HARDSKIN:
         case P_COUNTER:
         case M_COUNTER:
         case PM_COUNTER:
            stop = monster.isBuffed(MobTemporaryStatFlag.HARD_SKIN)
               || monster.isBuffed(MobTemporaryStatFlag.M_IMMUNE)
               || monster.isBuffed(MobTemporaryStatFlag.P_IMMUNE);
            break;
         case SUMMON:
            stop = player.getMap().getNumMonsters() >= this.limit;
      }

      return stop | monster.isBuffed(MobTemporaryStatFlag.MAGIC_CRASH);
   }

   public void setCasting(MapleMonster mob, int castingTime) {
      MobTemporaryStatEffect effect = new MobTemporaryStatEffect(MobTemporaryStatFlag.CASTING, 1, this.skillId, this, true);
      effect.setDuration(castingTime);
      mob.applyStatus(effect);
   }

   public void doSkill_Summon(MapleCharacter player, MapleMonster from) {
      List<Integer> ids = new ArrayList<>();
      if (this.summonIDs != null) {
         ids.addAll(this.summonIDs);
      }

      List<Integer> mobGroup = this.getMobGroup(0);
      if (mobGroup != null) {
         MobSkill mobSkill = null;

         for (MobSkill ms : from.getSkills()) {
            if (ms.getMobSkillID() == this.skillId && ms.getLevel() == this.skillLevel) {
               mobSkill = ms;
               break;
            }
         }

         if (mobSkill != null) {
            int skillIndex = mobSkill.getIndex();
            int mobID = mobGroup.get(from.getSkillIndexUsedCount(skillIndex) - 1);
            if (mobID > 0) {
               ids.add(mobID);
            }
         }
      }

      int[] summonIDs = new int[ids.size()];

      for (int i = 0; i < ids.size(); i++) {
         summonIDs[i] = ids.get(i);
      }

      if (from.getMap().getAllMonstersThreadsafe().size() < 50) {
         List<Point> fhs = new ArrayList<>();
         if (this.getFixedPos().size() > 0) {
            fhs = this.getFixedPos();
         } else if (this.targetType != 1) {
            Point leftTop = this.getLt();
            Point point = new Point();
            if (leftTop == point) {
               Point rightBottom = this.getRb();
               point = new Point();
               if (rightBottom == point) {
                  List<Point> points = new ArrayList<>();
                  points.add(from.getTruePosition());
               }
            }

            Rect rrr = Rect.GetOrDefault(this.getLt(), this.getRb(), new Rect(-150, -100, 100, 150));
            Rect rc = new Rect(from.getTruePosition(), rrr.getLt(), rrr.getRb(), from.isFacingLeft());
            fhs = from.getMap().getFootholdRandomly(summonIDs.length, rc);
            String info = this.getMobSkillStats(MobSkillStat.info);
            if (info != null
               && info.isEmpty()
               && (this.getMobSkillStats(MobSkillStat.info).contains("악몽의골렘") || this.getMobSkillStats(MobSkillStat.info).contains("독버섯"))) {
               fhs = from.getMap().getRandomPositions(summonIDs.length, from.getMap().getFootholds().getFootholds());
            }

            if (this.skillLevel == 238 || this.skillLevel == 239) {
               int y = player.getTruePosition().y < -1500 ? -2300 : -122;
               fhs.add(new Point(-580, y));
               fhs.add(new Point(-450, y - 200));
               fhs.add(new Point(450, y - 200));
               fhs.add(new Point(580, y));
            } else if (this.skillLevel >= 261 && this.skillLevel <= 263) {
               fhs.add(from.getTruePosition());
               if (from.getMap() instanceof Field_JinHillah) {
                  Field_JinHillah f = (Field_JinHillah)from.getMap();
                  if (this.skillLevel == 261) {
                     f.sendJinHillahNotice("힐라가 죽음의 밑바닥에서 스우의 사령을 끌어올리는 소리가 들린다.", 5000);
                  } else if (this.skillLevel == 262) {
                     f.sendJinHillahNotice("힐라가 죽음의 밑바닥에서 데미안의 사령을 끌어올리는 소리가 들린다.", 5000);
                  }
               }
            } else if (fhs.size() < summonIDs.length) {
               fhs.clear();

               for (int i = 0; i < summonIDs.length; i++) {
                  fhs.add(from.getTruePosition());
               }
            }
         } else {
            summonIDs = new int[(int)Math.ceil(from.getMap().getCharactersSize() / 2.0) * this.summonIDs.size()];

            for (int i = 0; i < summonIDs.length; i++) {
               summonIDs[i] = this.summonIDs.get(i % this.summonIDs.size());
            }

            if (this.skillLevel == 237) {
               fhs.add(new Point(-710, 200));
               fhs.add(new Point(720, 200));
            } else {
               for (MapleCharacter p : from.getMap().getCharactersThreadsafe()) {
                  fhs.add(p.getPosition());
               }
            }
         }

         int summonedSize = 0;

         for (int summonID : summonIDs) {
            summonedSize += from.getMap().getMobsSize(summonID);
         }

         for (int i = 0; i < summonIDs.length && (this.getLimit() <= 0 || summonedSize++ < this.getLimit()); i++) {
            Point fhpt = fhs.get(i);
            int mobID = summonIDs[i];
            MapleMonster mob = MapleLifeFactory.getMonster(mobID);
            if (mob != null) {
               if (mob.getMap() instanceof Field_BlackHeavenBoss) {
                  int moveAction;
                  if ((mob.getStats().getMoveAbility() & MoveAbility.Fly.getType()) != 0) {
                     moveAction = (mob.getStats().getMoveAbility() & MoveAbility.Stop.getType()) != 0 ? 2 : 1;
                  } else {
                     moveAction = 6;
                  }

                  if (!this.getFixedDir().isEmpty()) {
                     int dir = this.getFixedDir().get(i);
                     moveAction = dir | moveAction << 1;
                  }

                  mob.setStance(moveAction);
               }

               if (mob.getId() == 8500014) {
                  mob.setCreateDelay(3000);
               }

               mob.setParentObjectID(from.getObjectId());
               if (this.getSkillStatIntValue(MobSkillStat.linkHP) == 1) {
                  mob.setHp(from.getHp());
                  mob.setStance(2);
               }

               try {
                  from.getMap()
                     .spawnMonsterOnGroundBelow(
                        mob,
                        fhpt,
                        (byte)this.summonEffect,
                        true,
                        !this.fixedPos.isEmpty() || this.skillLevel == 237 || this.skillLevel == 238 || this.skillLevel == 239
                     );
               } catch (NullPointerException var14) {
                  System.out.println(mob.getId());
                  System.out.println(this.skillId);
                  System.out.println(this.skillLevel);
                  System.out.println(var14.getCause() + " SUMMON2");
               }
            }
         }

         if (this.skillLevel == 236 && player.getMap() instanceof Field_Papulatus) {
            Field_Papulatus f = (Field_Papulatus)player.getMap();
            MapleMonster boss = f.getBoss();
            if (boss != null) {
               player.getMap().broadcastMessage(MobPacket.talkMonster(boss.getObjectId(), 2));
            }
         }
      }
   }

   private void sendDemianDelayedAttackCreateMulti(MapleMonster monster, int attackIdx, boolean flip, int ox, int oy) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DEMIAN_DELAYED_ATTACK_CREATE.getValue());
      packet.writeInt(monster.getObjectId());
      packet.writeInt(this.getSkillId());
      packet.writeInt(this.getSkillLevel());
      MobSkillID skill = MobSkillID.getMobSkillIDByValue(this.getSkillId());
      if (skill == MobSkillID.TELEPORT && this.skillLevel == 42) {
         MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(this.getSkillId(), this.getSkillLevel());
         if (mobSkillInfo != null) {
            packet.write(flip);
            packet.writeInt(attackIdx);
            int count = Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.x2));
            int x = ox;
            packet.writeInt(count);

            for (int i = 0; i < count; i++) {
               monster.setDemianDelayedAttackID(monster.getDemianDelayedAttackID() + 1);
               packet.writeInt(monster.getDemianDelayedAttackID());
               if (!flip) {
                  int num = x
                     + Randomizer.rand(
                        Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.w)), Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.w2))
                     );
                  x = num;
                  packet.writeInt(num);
               } else {
                  int num = x
                     - Randomizer.rand(
                        Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.w)), Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.w2))
                     );
                  x = num;
                  packet.writeInt(num);
               }

               packet.writeInt(oy - Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.y2)));
               packet.writeInt(Randomizer.rand(90, 120));
            }

            monster.getMap().broadcastMessage(packet.getPacket());
         }
      }
   }

   private void sendDemianDelayedAttackCreateSingle(MapleMonster monster, int attackIdx, boolean flip, int x, int y) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DEMIAN_DELAYED_ATTACK_CREATE.getValue());
      packet.writeInt(monster.getObjectId());
      packet.writeInt(this.getSkillId());
      packet.writeInt(this.getSkillLevel());
      MobSkillID skill = MobSkillID.getMobSkillIDByValue(this.getSkillId());
      MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(this.getSkillId(), this.getSkillLevel());
      if (skill == MobSkillID.TELEPORT && (this.skillLevel > 44 && this.skillLevel <= 47 || this.skillLevel == 61) && mobSkillInfo != null) {
         packet.write(flip);
         packet.writeInt(attackIdx);
         monster.setDemianDelayedAttackID(monster.getDemianDelayedAttackID() + 1);
         packet.writeInt(monster.getDemianDelayedAttackID());
         packet.writeInt(x);
         packet.writeInt(y);
         monster.getMap().broadcastMessage(packet.getPacket());
      }
   }

   private void doSkill_ObstacleAttack(MapleMonster monster) {
      MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(this.getSkillId(), this.getSkillLevel());
      int yOffset = Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.y2));
      Point pos = monster.getPosition();
      pos = monster.getMap().calcDropPos(new Point(pos.x, pos.y - 23), monster.getPosition());
      int groundY = pos.y + 40;
      int x = monster.getPosition().x;
      int y = monster.getPosition().y;
      Rect mbr = monster.getMap().calculateMBR();
      if (mbr != null) {
         int left = mbr.getLeft();
         int right = mbr.getRight();
         if (this.skillLevel == 2) {
            int newX = Randomizer.rand(monster.getPosition().x - 600, x + 600);
            int randomX = Math.max(left, Math.min(right, newX));
            int fixX = Randomizer.rand(randomX - 3, randomX + 3);
            this.onRegisterRadialObstracleAtom(monster, 3, 13000, 700, 1000, 58, 0, 194, fixX, yOffset, groundY, x, y);
            return;
         }

         if (this.skillLevel == 4) {
            this.onRegisterRadialObstracleAtom(monster, 4, 13000, 250, 500, 59, 1, 0, x, yOffset, groundY, x, y);
         }
      }
   }

   private void onRegisterRadialObstracleAtom(
      final MapleMonster monster,
      int count,
      int duration,
      final int dMin,
      final int dMax,
      final int type,
      int interval,
      final int createDelay,
      int fixX,
      final int yOffset,
      final int yy,
      final int x,
      final int y
   ) {
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(MobSkillInfo.this.getSkillId(), MobSkillInfo.this.getSkillLevel());
            ObstacleRadialInfo obstacleRadialInfo = new ObstacleRadialInfo(
               Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.x)), x, y + yOffset, dMin, dMax, 0
            );
            Set<ObstacleAtom> obstacleAtomInfosSet = new HashSet<>();
            Point pos = monster.getPosition();
            int xx = pos.x;
            int delta = 0;
            int c = Randomizer.rand(3, 5);
            delta = c * 90;
            int x1 = xx - delta;
            int x2 = xx + delta;
            int vPerSec = Randomizer.rand(75, 105);
            int maxP = Randomizer.rand(1, 2);
            if (x1 < monster.getMap().getLeft() + 100) {
               x1 = monster.getMap().getLeft() + 100;
               x2 = x1 + delta * 2;
            } else if (x2 > monster.getMap().getRight() - 100) {
               x2 = monster.getMap().getRight() - 100;
               x1 = x2 - delta * 2;
            }

            for (int i = 0; i < c; i++) {
               int d = (x2 - x1) / c;
               int xxx = x1 + d * i;
               ObstacleAtom atom = new ObstacleAtom(type, new Point(xx, y + yOffset), new Point(Randomizer.rand(xxx - 5, xxx + 5), yy), createDelay);
               ObstacleAtom.setInfo(atom);
               int sn = ObstacleAtom.SN.getAndIncrement();
               atom.setKey(sn);
               atom.setvPerSec(vPerSec);
               atom.setMaxP(maxP);
               obstacleAtomInfosSet.add(atom);
            }

            monster.getMap().broadcastMessage(CField.createObstacle(ObstacleAtomCreateType.RADIAL, null, obstacleRadialInfo, obstacleAtomInfosSet));
         }
      };
      ObstacleAtomAction action = new ObstacleAtomAction();
      action.setDuration(duration);
      action.setInterval(interval);
      action.setRunnable(runnable);
      monster.getMap().addObstacleAtomAction(action);
   }

   private void doSkill_PassiveFirewalk(MapleMonster monster) {
      MapleCharacter controller = monster.getController();
      if (controller != null) {
         monster.getMap()
            .broadcastMessage(
               CWvsContext.getScriptProgressMessage(
                  monster.getStats().getName() + "가 [" + controller.getName() + "]" + Locales.getKoreanJosa(controller.getName(), JosaType.을를) + " 추격합니다."
               )
            );
         this.sendChaseEffectSet(monster.getMap(), controller.getId(), monster.getObjectId(), true);
      }
   }

   private void doSkill_AffectedArea(MapleMonster monster, Point forcedPoint) {
      MapleCharacter controller = monster.getController();
      if (controller != null) {
         Point point = controller.getTruePosition();
         int rangeGap = this.getSkillStatIntValue(MobSkillStat.rangeGap);
         if (rangeGap <= 0) {
            if (forcedPoint != null) {
               point = monster.getMap().calcDropPos(forcedPoint, point);
            } else if (this.getTargetType() != 1) {
               point = monster.getTruePosition();
            } else {
               point = controller.getTruePosition();
            }
         } else {
            boolean find = false;

            for (int i = 0; i < 1000; i++) {
               List<Point> fhs = monster.getMap().getFootholdRandomly(1, monster.getMap().calculateMBR());
               Point fh = fhs.get(0);
               point = fh;
               List<AffectedArea> areas = monster.getMap().getAffectedAreasBySkillID(this.skillId);
               if (areas.isEmpty()) {
                  break;
               }

               find = false;

               for (AffectedArea area : areas) {
                  if (!(area.getTruePosition().distance(fh) >= rangeGap * 2)) {
                     find = true;
                     break;
                  }
               }

               if (!find) {
                  break;
               }
            }
         }

         point = monster.getMap().calcDropPos(point, point);
         Rect rect = new Rect(point, this.getLt(), this.getRb(), false);
         if (this.getJMist() != null) {
            point = new Point(
               Randomizer.rand(this.getJMist().getLt().x, this.getJMist().getRb().x), Randomizer.rand(this.getJMist().getLt().y, this.getJMist().getRb().y)
            );
            rect = new Rect(point.x - 200, point.y - 200, point.x + 200, point.y + 200);
            if (monster.getMap() instanceof Field_JinHillah) {
               this.duration = this.getJMist().getDuration(((Field_JinHillah)monster.getMap()).getPhase() - 1);
            }
         }

         if (this.getMobSkillStatsInt(MobSkillStat.footholdRect) != 0) {
            point = monster.getTruePosition();
            Rect mbr = monster.getMap().calculateMBR();
            Point lt = new Point(mbr.getLeft() - 30, (int)(point.getY() + 3.0 - this.getLt().getY()));
            Point rb = new Point(mbr.getRight() + 30, (int)(point.getY() + 3.0));
            rect = new Rect(lt, rb);
         }

         if (this.getSkillId() == MobSkillID.AREA_FORCE.getVal() && this.getSkillLevel() == 11) {
            rect = new Rect(point.x - 619, point.y - 783, point.x + 707, point.y + 20);
         }

         AffectedArea mist = new AffectedArea(rect, monster, this, point, System.currentTimeMillis() + this.getDuration());
         if (this.getSkillId() == MobSkillID.AREA_FORCE.getVal() && this.getSkillLevel() == 11) {
            mist.setEndTime(mist.getEndTime() + 5000L);
         }

         if (this.getForce() > 0) {
            int x;
            if (this.getTargetType() != 1) {
               x = point.x + this.getForceX() * (monster.isFaceLeft() ? -1 : 1);
            } else {
               x = point.x + this.getForceX() * (controller.isFacingLeft() ? -1 : 1);
            }

            if (this.getSkillLevel() == 9) {
               x = point.x;
            }

            mist.setForcePos(new Point(x, this.getForce()));
         }

         if (monster.getMap() instanceof Field_JinHillah) {
            mist.setJMist(this.getJMist());
            mist.setJMistPhase(((Field_JinHillah)monster.getMap()).getPhase() - 1);
         }

         monster.getMap().spawnMist(mist);
      }
   }

   public void sendChaseEffectSet(Field field, int targetCharacterID, int mobObjectID, boolean on) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_CHASE_EFFECT_SET.getValue());
      packet.writeInt(targetCharacterID);
      packet.writeShort(on ? 1 : 0);
      packet.writeInt(mobObjectID);
      field.broadcastMessage(packet.getPacket());
   }

   public void applyEffect(MapleCharacter player, MapleMonster monster, MobSkill mobSkill, boolean skill) {
      this.applyEffect(player, monster, mobSkill, skill, false, new Point(0, 0));
   }

   public void applyEffect(MapleCharacter player, MapleMonster monster, MobSkill mobSkill, boolean skill, boolean isLeft, Point startPos) {
      SecondaryStatFlag disease = SecondaryStatFlag.getBySkill(this.skillId);
      Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new EnumMap<>(MobTemporaryStatFlag.class);
      List<Integer> reflection = new LinkedList<>();
      MobSkillID msi = MobSkillID.getMobSkillIDByValue(this.skillId);
      MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(this.getSkillId(), this.getSkillLevel());
      if (player != null && player.isGM() && monster.getStats().isBoss()) {
         player.dropMessage(
            5,
            "MobID : "
               + monster.getId()
               + ", MobSkill : "
               + this.skillId
               + "("
               + msi.name()
               + ") (SkillLevel : "
               + this.getSkillLevel()
               + ") info : "
               + mobSkillInfo.getMobSkillStats(MobSkillStat.info)
         );
      }

      label1171:
      switch (msi) {
         case POWERUP:
         case POWERUP_M:
            stats.put(MobTemporaryStatFlag.POWER_UP, new MobTemporaryStatEffect(MobTemporaryStatFlag.POWER_UP, this.x, this.skillId, this, true));
            break;
         case PAD:
            stats.put(MobTemporaryStatFlag.PAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.PAD, this.x, this.skillId, this, true));
            break;
         case MAGICUP:
         case MAGICUP_M:
            stats.put(MobTemporaryStatFlag.MAGIC_UP, new MobTemporaryStatEffect(MobTemporaryStatFlag.MAGIC_UP, this.x, this.skillId, this, true));
            break;
         case MAD:
            stats.put(MobTemporaryStatFlag.MAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.MAD, this.x, this.skillId, this, true));
            break;
         case PGUARDUP:
         case PGUARDUP_M:
            stats.put(MobTemporaryStatFlag.P_GUARD_UP, new MobTemporaryStatEffect(MobTemporaryStatFlag.P_GUARD_UP, this.x, this.skillId, this, true));
            break;
         case PDR:
            stats.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, this.x, this.skillId, this, true));
            break;
         case MGUARDUP:
         case MGUARDUP_M:
            stats.put(MobTemporaryStatFlag.M_GUARD_UP, new MobTemporaryStatEffect(MobTemporaryStatFlag.M_GUARD_UP, this.x, this.skillId, this, true));
            break;
         case MDR:
            stats.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, this.x, this.skillId, this, true));
            break;
         case PHYSICAL_IMMUNE:
            stats.put(MobTemporaryStatFlag.P_IMMUNE, new MobTemporaryStatEffect(MobTemporaryStatFlag.P_IMMUNE, this.x, this.skillId, this, true));
            break;
         case MAGIC_IMMUNE:
            stats.put(MobTemporaryStatFlag.M_IMMUNE, new MobTemporaryStatEffect(MobTemporaryStatFlag.M_IMMUNE, this.x, this.skillId, this, true));
            break;
         case HARDSKIN:
            stats.put(MobTemporaryStatFlag.HARD_SKIN, new MobTemporaryStatEffect(MobTemporaryStatFlag.HARD_SKIN, this.x, this.skillId, this, true));
            break;
         case P_COUNTER:
            stats.put(MobTemporaryStatFlag.P_COUNTER, new MobTemporaryStatEffect(MobTemporaryStatFlag.P_COUNTER, this.x, this.skillId, this, true));
            stats.put(MobTemporaryStatFlag.P_IMMUNE, new MobTemporaryStatEffect(MobTemporaryStatFlag.P_IMMUNE, this.x, this.skillId, this, true));
            reflection.add(this.x);
            break;
         case M_COUNTER:
            stats.put(MobTemporaryStatFlag.M_COUNTER, new MobTemporaryStatEffect(MobTemporaryStatFlag.M_COUNTER, this.x, this.skillId, this, true));
            stats.put(MobTemporaryStatFlag.M_IMMUNE, new MobTemporaryStatEffect(MobTemporaryStatFlag.M_IMMUNE, this.x, this.skillId, this, true));
            reflection.add(this.x);
            break;
         case PM_COUNTER:
            if (this.skillLevel != 18 && this.skillLevel != 19) {
               stats.put(MobTemporaryStatFlag.P_COUNTER, new MobTemporaryStatEffect(MobTemporaryStatFlag.P_COUNTER, this.x, this.skillId, this, true));
               stats.put(MobTemporaryStatFlag.P_IMMUNE, new MobTemporaryStatEffect(MobTemporaryStatFlag.P_IMMUNE, this.x, this.skillId, this, true));
               stats.put(MobTemporaryStatFlag.M_COUNTER, new MobTemporaryStatEffect(MobTemporaryStatFlag.M_COUNTER, this.x, this.skillId, this, true));
               stats.put(MobTemporaryStatFlag.M_IMMUNE, new MobTemporaryStatEffect(MobTemporaryStatFlag.M_IMMUNE, this.x, this.skillId, this, true));
               reflection.add(this.x);
               break;
            } else {
               monster.getMap()
                  .broadcastMessage(
                     MobPacket.mobSkillDelay(
                        monster.getObjectId(), mobSkill.getSkillAfter(), this.getSkillId(), this.getSkillLevel(), 0, Collections.EMPTY_LIST
                     )
                  );
            }
         case INVINCIBLE:
            stats.put(MobTemporaryStatFlag.INVINCIBLE, new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, this.skillId, this, true));
            break;
         case SUMMON:
            if (monster == null) {
               return;
            }

            this.doSkill_Summon(player, monster);
            break;
         case ACC:
            stats.put(MobTemporaryStatFlag.ACC, new MobTemporaryStatEffect(MobTemporaryStatFlag.ACC, this.x, this.skillId, this, true));
            break;
         case EVA:
            stats.put(MobTemporaryStatFlag.EVA, new MobTemporaryStatEffect(MobTemporaryStatFlag.EVA, this.x, this.skillId, this, true));
            break;
         case HASTE_M:
         case HASTE:
         case SPEED:
            stats.put(MobTemporaryStatFlag.SPEED, new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, this.x, this.skillId, this, true));
            break;
         case SEAL_SKILL:
            stats.put(MobTemporaryStatFlag.SEAL_SKILL, new MobTemporaryStatEffect(MobTemporaryStatFlag.SEAL_SKILL, this.x, this.skillId, this, true));
            break;
         case HEAL_M:
            if (this.lt != null && this.rb != null && skill && monster != null) {
               List<MapleMapObject> objects = this.getObjectsInRange(monster, MapleMapObjectType.MONSTER);
               int hp = this.getX() / 1000 * (int)(950.0 + 1050.0 * Math.random());

               for (MapleMapObject mons : objects) {
                  ((MapleMonster)mons).heal(hp, this.getY(), true);
               }
            } else if (monster != null) {
               monster.heal(this.getX(), this.getY(), true);
            }
            break;
         case NEAR_BUFF:
            stats.put(MobTemporaryStatFlag.PAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.PAD, this.x, this.skillId, this, true));
            stats.put(MobTemporaryStatFlag.MAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.MAD, this.x, this.skillId, this, true));
            stats.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, this.y, this.skillId, this, true));
            stats.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, this.y, this.skillId, this, true));
            break;
         case MOB_CONSUME:
            int count = monster.getMap().getMobsSize(this.summonIDs.get(0));
            if (count <= 0) {
               for (int i = 0; i < this.y; i++) {
                  MapleMonster mob = MapleLifeFactory.getMonster(this.summonIDs.get(0));
                  if (mob != null) {
                     player.getMap()
                        .spawnMonsterOnGroundBelow(mob, monster.getTruePosition(), (byte)-2, true, this.skillLevel == 238 || this.skillLevel == 239);
                  }
               }

               if (this.skillLevel >= 12 && this.skillLevel <= 13) {
                  if (monster.getHPPercent() <= 30) {
                     monster.getMap()
                        .sendSmartMobNotice(
                           SmartMobNoticeType.Normal, monster.getId(), SmartMobMsgType.Field, this.skillLevel, "위협을 느낀 반 레온이 몬스터를 소환하여 체력을 흡수하려 합니다."
                        );
                     monster.getMap().broadcastMessage(CWvsContext.serverNotice(5, "위협을 느낀 반 레온이 몬스터를 소환하여 체력을 흡수하려 합니다."));
                  } else if (monster.getHPPercent() <= 50) {
                     monster.getMap()
                        .sendSmartMobNotice(
                           SmartMobNoticeType.Normal, monster.getId(), SmartMobMsgType.Field, this.skillLevel, "반 레온이 주변에 체력을 흡수할 몬스터가 없어 새롭게 소환을 시도합니다."
                        );
                     monster.getMap().broadcastMessage(CWvsContext.serverNotice(5, "반 레온이 주변에 체력을 흡수할 몬스터가 없어 새롭게 소환을 시도합니다."));
                  }
               }

               int skillIndex = monster.getSkillIndex(this.skillId, this.skillLevel);
               if (skillIndex != -1) {
                  monster.addIgnoreIntervalSkill(skillIndex);
                  monster.addOnetimeFsmSkill(skillIndex);
               }
            } else {
               Point lt_ = this.lt;
               Point rb_ = this.rb;
               if (lt_ == null) {
                  lt_ = new Point(-150, -100);
                  rb_ = new Point(100, 150);
               }

               List<MapleMonster> mobs = monster.getMap().getMobsInRect(monster.getTruePosition(), lt_.x, lt_.y, rb_.x, rb_.y, isLeft);
               AtomicInteger consumed = new AtomicInteger(0);
               mobs.stream().limit(mobSkillInfo.limit).collect(Collectors.toList()).forEach(m -> {
                  if (m.getId() == this.summonIDs.get(0)) {
                     consumed.addAndGet(1);
                     monster.getMap().removeMonster(m, 1);
                  }
               });
               if (consumed.get() > 0) {
                  monster.getMap()
                     .broadcastMessage(
                        CWvsContext.serverNotice(
                           5, monster.getStats().getName() + "가 " + MapleLifeFactory.getMonsterStats(this.summonIDs.get(0)).getName() + "를 흡수하여 HP를 회복합니다."
                        )
                     );
                  long hp = monster.getHp();
                  long heal = this.getX() * consumed.get();
                  long after = Math.min(monster.getMobMaxHp(), heal + hp);
                  long diff = after - hp;
                  if (diff > 0L) {
                     monster.setHp(after);
                     monster.getMap().broadcastMessage(MobPacket.showBossHP(monster.getId(), after, monster.getMobMaxHp()));
                  }
               }
            }
            break;
         case SLOW:
            if (player != null) {
               player.giveDebuff(SecondaryStatFlag.Slow, this);
            }
            break;
         case DISPEL:
            if (this.lt != null && this.rb != null && skill && monster != null && player != null) {
               for (MapleCharacter character : this.getPlayersInRange(monster, player)) {
                  character.dispel();
               }
            } else if (player != null) {
               player.dispel();
            }
            break;
         case BAN_MAP:
            if (!ServerConstants.disableBanMap
               && monster != null
               && monster.getMap().getSquadByMap() == null
               && (monster.getEventInstance() == null || monster.getEventInstance().getName().indexOf("BossQuest") == -1)) {
               BanishInfo info = monster.getStats().getBanishInfo();
               if (info != null) {
                  if (this.lt != null && this.rb != null && skill && player != null) {
                     for (MapleCharacter chrxxx : this.getPlayersInRange(monster, player)) {
                        chrxxx.dropMessage(5, info.getMsg());
                        chrxxx.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
                        chrxxx.setRegisterTransferField(info.getMap());
                     }
                  } else if (player != null) {
                     player.dropMessage(5, info.getMsg());
                     player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
                     player.setRegisterTransferField(info.getMap());
                  }
               }
            }
            break;
         case AREA_POISON:
         case AREA_FORCE_FROM_USER:
         case AREA_ABNORMAL:
            this.doSkill_AffectedArea(monster, null);
            break;
         case STOP_PORTION:
            int skillLevel;
            if (mobSkill == null) {
               skillLevel = 1;
            } else {
               skillLevel = mobSkill.getLevel();
            }

            if (msi == null) {
               System.out.println("msi가 Null 해당 몹스킬 : " + this.skillId);
            } else {
               player.giveDebuff(SecondaryStatFlag.StopPortion, this.x, this.y, this.getDuration(), msi.getVal(), skillLevel);
            }
            break;
         case DAZZLE:
            MapleCharacter target = monster.getMap().getCharactersThreadsafe().stream().filter(p -> !p.isHidden()).findAny().orElse(null);
            if (target != null) {
               int duration = 30000;
               int xx = this.getMobSkillStatsInt(MobSkillStat.x);
               String face = this.getMobSkillStats(MobSkillStat.face);
               int prop = this.getMobSkillStatsInt(MobSkillStat.prop);
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               if (prop > 0) {
                  statups.put(SecondaryStatFlag.indieStance, prop);
               }

               if (xx > 0) {
                  statups.put(SecondaryStatFlag.Slow, xx);
               }

               if (face != null && !face.isEmpty() && face.equals("love")) {
                  target.send(CField.getCharacterExpression(17, duration));
                  target.getMap().broadcastMessage(target, CField.facialExpressionWithDuration(target, 17, duration), false);
               }

               int attractValue = 0;
               byte var171;
               if (monster.getTruePosition().x < target.getTruePosition().x) {
                  var171 = 1;
               } else {
                  var171 = 2;
               }

               statups.put(SecondaryStatFlag.Attract, Integer.valueOf(var171));
               target.temporaryStatSet(this.skillId, this.skillLevel, duration, statups);
            }
            break;
         case RETURN_TELEPORT:
            disease = SecondaryStatFlag.ReturnTeleport;
            Point charPos = player.getPosition();
            Point pos = player.getMap().calcDropPos(new Point(charPos.x, charPos.y), player.getTruePosition());
            this.setX(pos.x);
            this.setY(pos.y - 5);
            break;
         case AREA_TIMEZONE:
            this.doSkill_AffectedArea(monster, null);
            monster.getMap().broadcastMessage(CWvsContext.getScriptProgressMessage("시간의 틈새에 '균열'이 발생하였습니다."));
            break;
         case BOUNCE_ATTACK: {
            monster.clearAreaWarnings();
            if (this.skillLevel == 6 || this.skillLevel == 13 || this.skillLevel == 14 || this.skillLevel == 16) {
               monster.clearDynamicObjects();

               for (DynamicObject obj : monster.getMap().pickDynamicObjectRandomly(mobSkillInfo.getMobSkillStatsInt(MobSkillStat.count))) {
                  List<DynamicObject.CollisionDisease> collisionDiseases = new ArrayList<>();
                  if (!this.getAffectedOtherSkills().isEmpty()) {
                     for (AffectedOtherSkill affectedOtherSkill : this.getAffectedOtherSkills()) {
                        collisionDiseases.add(
                           new DynamicObject.CollisionDisease(affectedOtherSkill.getAffectedOtherSkillID(), affectedOtherSkill.getAffectedOtherSkillLev())
                        );
                     }
                  }

                  monster.getMap()
                     .setDynamicObjectWaiting(obj.getIndex(), (int)mobSkillInfo.getDuration(), obj.getPosX(), monster.getTruePosition().y, collisionDiseases);
                  monster.addDynamicObject(obj.getIndex());
                  monster.addAreaWarning(new Rect(new Point(obj.getPosX(), obj.getPosY()), mobSkillInfo.getLt(), mobSkillInfo.getRb(), false));
               }
            }

            int sequenceDelay = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.areaSequenceDelay);
            int skillAfter = Math.max(mobSkill.getSkillAfter(), mobSkillInfo.getMobSkillStatsInt(MobSkillStat.skillAfter));
            monster.getMap()
               .broadcastMessage(
                  MobPacket.mobSkillDelay(monster.getObjectId(), skillAfter, this.getSkillId(), this.getSkillLevel(), sequenceDelay, monster.getAreaWarnings())
               );
            break;
         }
         case TOSS: {
            int d = Randomizer.rand(-10, 10);
            monster.clearAreaWarnings();
            monster.addAreaWarning(new Rect(new Point(-510 + d, -16), this.getLt(), this.getRb(), false));
            monster.addAreaWarning(new Rect(new Point(-30 + d, -16), this.getLt(), this.getRb(), false));
            monster.addAreaWarning(new Rect(new Point(480 + d, -16), this.getLt(), this.getRb(), false));
            int sequenceDelay = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.areaSequenceDelay);
            int skillAfter = Math.max(mobSkill.getSkillAfter(), mobSkillInfo.getMobSkillStatsInt(MobSkillStat.skillAfter));
            monster.getMap()
               .broadcastMessage(
                  MobPacket.mobSkillDelay(monster.getObjectId(), skillAfter, this.getSkillId(), this.getSkillLevel(), sequenceDelay, monster.getAreaWarnings())
               );
            break;
         }
         case AREA_WARNING: {
            System.out.println("asfasfafa?");
            monster.clearAreaWarnings();
            List<Point> pts = new ArrayList<>();
            List<MapleCharacter> users = monster.getMap()
               .getPlayerInRect(monster.getTruePosition(), this.getLt2().x, this.getLt2().y, this.getRb2().x, this.getRb2().y)
               .stream()
               .filter(aa -> !aa.isHidden())
               .collect(Collectors.toList());
            Collections.shuffle(users);
            users.forEach(p -> pts.add(p.getTruePosition()));
            pts.forEach(point -> monster.addAreaWarning(new Rect(point, this.getLt(), this.getRb(), false)));
            int sequenceDelay = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.areaSequenceDelay);
            int skillAfter = Math.max(mobSkill.getSkillAfter(), mobSkillInfo.getMobSkillStatsInt(MobSkillStat.skillAfter));
            monster.getMap()
               .broadcastMessage(
                  MobPacket.mobSkillDelay(monster.getObjectId(), skillAfter, this.getSkillId(), this.getSkillLevel(), sequenceDelay, monster.getAreaWarnings())
               );
            break;
         }
         case FIRE_AT_RANDOM_ATTACK:
            this.fireAtRandomAttack(
               monster,
               Math.max(mobSkill.getSkillAfter(), mobSkillInfo.getMobSkillStatsInt(MobSkillStat.skillAfter)),
               mobSkillInfo.getSkillStatIntValue(MobSkillStat.areaSequenceDelay)
            );
            break;
         case TELEPORT:
            int xxx = mobSkillInfo.getSkillStatIntValue(MobSkillStat.x);
            int y = mobSkillInfo.getSkillStatIntValue(MobSkillStat.y);
            if (this.skillLevel == 65 || this.skillLevel == 66) {
               int x_ = this.skillLevel == 65 ? -350 : 350;
               int y_ = 85;
               monster.setPosition(new Point(x_, y_));
               monster.getMap().broadcastMessage(MobPacket.spawnMonster(monster, -1, 0));
               monster.getController().send(MobPacket.willTeleportRequest(monster.getObjectId(), x_, y_));
               if (monster.getMap() instanceof Field_BlackMage) {
                  Field_BlackMage fx = (Field_BlackMage)monster.getMap();
                  fx.setCreateBarrierTime(System.currentTimeMillis() + 3000L);
               }

               return;
            }

            switch (xxx) {
               case 1:
               case 2:
                  monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.Controller, null));
                  break label1171;
               case 3:
                  monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.Controller, null));
                  break label1171;
               case 4:
                  PacketEncoder packetx = new PacketEncoder();
                  packetx.writeInt(y);
                  monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.OffsetX, packetx));
                  break label1171;
               case 5:
                  List<Summoned> summonedList = new ArrayList<>();

                  for (Summoned summoned : monster.getMap().getAllSummonsThreadsafe()) {
                     if (GameConstants.isTauntSkill(summoned.getSkill())) {
                        summonedList.add(summoned);
                     }
                  }

                  if (!summonedList.isEmpty()) {
                     Summoned targetx = summonedList.stream().findAny().orElse(null);
                     if (targetx != null) {
                        PacketEncoder packetxxx = new PacketEncoder();
                        packetxxx.writeInt(targetx.getTruePosition().x);
                        packetxxx.writeInt(targetx.getTruePosition().y);
                        monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.RandomUser, packetxxx));
                     }
                  } else {
                     MapleCharacter targetx = monster.getMap().getCharactersThreadsafe().stream().filter(pxxx -> !pxxx.isHidden()).findAny().orElse(null);
                     if (targetx != null) {
                        PacketEncoder packetxxx = new PacketEncoder();
                        packetxxx.writeInt(targetx.getPosition().x);
                        packetxxx.writeInt(targetx.getPosition().y);
                        monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.RandomUser, packetxxx));
                     }
                  }
                  break label1171;
               case 6:
               case 7:
               case 8:
               case 9:
               case 15:
               default:
                  if (this.skillLevel == 13) {
                     monster.setPosition(new Point(-1710, 410));
                  }

                  monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), monster.getId(), xxx));
                  break label1171;
               case 10:
               case 11:
                  MobTeleportType type = MobTeleportType.get(xxx);
                  Point newPos = monster.getPosition();
                  boolean flip = monster.isFacingLeft();
                  Rect mbr = monster.getMap().calculateMBR();
                  if (flip) {
                     if (type != MobTeleportType.MapBorder) {
                        newPos.x -= y;
                     } else {
                        newPos.x = mbr.getLeft() + 50;
                     }
                  } else if (type != MobTeleportType.MapBorder) {
                     newPos.x += y;
                  } else {
                     newPos.x = mbr.getRight() - 50;
                  }

                  PacketEncoder packetxxx = new PacketEncoder();
                  packetxxx.writeInt(newPos.x);
                  packetxxx.writeInt(newPos.y);
                  monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), type, packetxxx));
                  if (this.skillLevel != 42) {
                     this.sendDemianDelayedAttackCreateSingle(monster, 1, flip, newPos.x, newPos.y);
                  } else {
                     this.sendDemianDelayedAttackCreateMulti(monster, 1, flip, monster.getPosition().x, monster.getPosition().y);
                  }
                  break label1171;
               case 12:
                  List<MapleCharacter> list = monster.getMap().getCharactersThreadsafe();
                  Collections.shuffle(list);
                  int x_ = 0;

                  for (MapleCharacter pxxx : monster.getMap().getCharactersThreadsafe()) {
                     boolean top = monster.getPosition().getY() < -500.0;
                     if (top) {
                        if (pxxx.getPosition().getY() < -500.0) {
                           x_ = pxxx.getPosition().x;
                           break;
                        }
                     } else if (pxxx.getPosition().getY() > 0.0) {
                        x_ = pxxx.getPosition().x;
                        break;
                     }
                  }

                  int y_ = monster.getId() != 8880343 && monster.getId() != 8880303 && monster.getId() != 8880363 ? -2020 : 129;
                  monster.setPosition(new Point(x_, y_));
                  monster.getMap().broadcastMessage(MobPacket.spawnMonster(monster, -1, 0));
                  monster.getController().send(MobPacket.willTeleportRequest(monster.getObjectId(), x_, y_));
                  break label1171;
               case 13:
                  monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.Controller, null));
                  monster.getController().send(MobPacket.mobSetAfterAttack(monster.getObjectId(), 1, 1, 31, false));
                  break label1171;
               case 14:
                  PacketEncoder packetxx = new PacketEncoder();
                  packetxx.writeInt(monster.getTruePosition().x);
                  packetxx.writeInt(monster.getTruePosition().y);
                  monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.StayX, packetxx));
                  break label1171;
               case 16:
                  if (this.skillLevel == 77) {
                     System.out.println("!무적");
                     monster.getMap()
                        .broadcastMessage(
                           MobPacket.mobSkillDelay(
                              monster.getObjectId(),
                              mobSkillInfo.getMobSkillStatsInt(MobSkillStat.skillAfter),
                              this.getSkillId(),
                              this.getSkillLevel(),
                              0,
                              Collections.EMPTY_LIST
                           )
                        );
                  } else {
                     int m = 1;
                     if (isLeft) {
                        m = -1;
                     }

                     packetxxx = new PacketEncoder();
                     int newX = startPos.x + m * y;
                     int newY = startPos.y;
                     packetxxx.writeInt(newX);
                     packetxxx.writeInt(newY);
                     monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.RandomUser2, packetxxx));
                     boolean flipx = monster.isFacingLeft();
                     if (this.skillLevel > 44 && this.skillLevel <= 47 || this.skillLevel == 61) {
                        this.sendDemianDelayedAttackCreateSingle(monster, 1, flipx, newX, newY);
                     }
                  }
                  break label1171;
            }
         case FIRE_BOMB_DISPEL:
            player.cancelFireBombTask();
            break;
         case FIRE_BOMB:
         case AREA_FORCE:
            if (msi == MobSkillID.AREA_FORCE && this.skillLevel == 11) {
               this.doSkill_AffectedArea(monster, null);
            } else if (this.skillLevel != 2 && this.skillLevel != 9) {
               monster.getMap()
                  .broadcastMessage(
                     MobPacket.mobSkillDelay(
                        monster.getObjectId(), mobSkill.getSkillAfter(), this.getSkillId(), this.getSkillLevel(), 0, Collections.EMPTY_LIST
                     )
                  );
            } else if (this.skillLevel == 2) {
               Point pos3 = monster.getMap().calcDropPos(new Point(monster.getPosition().x, monster.getPosition().y), monster.getPosition());
               AffectedArea mist = null;
               MobSkillInfo ms = MobSkillFactory.getMobSkill(this.skillId, this.skillLevel);
               Rect rect2 = AffectedArea.calculateRect(pos3, isLeft, ms.getLt(), ms.getRb());
               mist = new AffectedArea(rect2, monster, ms, pos3, System.currentTimeMillis() + 1000L);
               mist.setForcePos(AffectedArea.calculateForce(pos3, isLeft, ms.getForce(), ms.getForceX()));
               if (mist != null) {
                  monster.getMap().spawnMist(mist);
               }
            } else if (this.skillLevel == 9) {
               Point pos3 = monster.getMap().calcDropPos(new Point(monster.getPosition().x - 200, monster.getPosition().y), monster.getTruePosition());
               AffectedArea mist = null;
               MobSkillInfo ms = MobSkillFactory.getMobSkill(this.skillId, this.skillLevel);
               Rect rect2 = AffectedArea.calculateRect(pos3, false, ms.getLt(), ms.getRb());
               mist = new AffectedArea(rect2, monster, ms, pos3, System.currentTimeMillis() + 1000L);
               mist.setForcePos(AffectedArea.calculateForce(pos3, false, ms.getForce(), ms.getForceX() / 2));
               if (mist != null) {
                  monster.getMap().spawnMist(mist);
               }

               rect2 = AffectedArea.calculateRect(pos3, true, ms.getLt(), ms.getRb());
               mist = new AffectedArea(rect2, monster, ms, pos3, System.currentTimeMillis() + 1000L);
               mist.setForcePos(AffectedArea.calculateForce(pos3, false, ms.getForce(), ms.getForceX() / 2));
               if (mist != null) {
                  monster.getMap().spawnMist(mist);
               }
            }
            break;
         case DEATHMARK:
            Integer v = player.getBuffedValue(SecondaryStatFlag.DeathMark);
            int value = 0;
            if (v != null) {
               value = v;
            }

            if (this.lt != null && this.rb != null && skill && monster != null) {
               for (MapleCharacter chrxx : this.getPlayersInRange(monster, player)) {
                  if (chrxx.getBuffedValue(SecondaryStatFlag.NotDamaged) == null && chrxx.getBuffedValue(SecondaryStatFlag.indiePartialNotDamaged) == null) {
                     chrxx.giveDebuff(SecondaryStatFlag.DeathMark, Math.min(value + 1, 3), 0, this.getDuration(), msi.getVal(), mobSkill.getLevel());
                  }
               }
            }
            break;
         case VENOMSNAKE:
            if (this.lt != null && this.rb != null && skill && monster != null) {
               for (MapleCharacter chrx : this.getPlayersInRange(monster, player)) {
                  if (chrx.getBuffedValue(SecondaryStatFlag.NotDamaged) == null
                     && chrx.getBuffedValue(SecondaryStatFlag.indiePartialNotDamaged) == null
                     && chrx.giveDebuff(SecondaryStatFlag.VenomSnake, this.x, 0, this.getDuration(), 177, 1)) {
                     MobSkillInfo morph = MobSkillFactory.getMobSkill(MobSkillID.USER_MORPH.getVal(), this.y);
                     chrx.giveDebuff(SecondaryStatFlag.Morph, morph.getX(), 0, this.getDuration(), MobSkillID.USER_MORPH.getVal(), this.y, true);
                  }
               }
            }
            break;
         case PAINMARK:
            int stack = 0;
            if (player.getBuffedValue(SecondaryStatFlag.PainMark) != null) {
               stack = player.getBuffedValue(SecondaryStatFlag.PainMark);
               player.temporaryStatReset(SecondaryStatFlag.PainMark);
            }

            player.giveDebuff(SecondaryStatFlag.PainMark, Math.min(3, ++stack), 0, this.getDuration(), msi.getVal(), mobSkill.getLevel());
            break;
         case VAMPDEATH:
            player.giveDebuff(SecondaryStatFlag.VampDeath, this.x * 100, 0, this.getDuration(), msi.getVal(), mobSkill.getLevel());
            break;
         case MAGNET:
            player.setMagnetAreaFrom(monster.getObjectId());
            player.giveDebuff(
               Map.of(SecondaryStatFlag.Magnet, monster.getTruePosition().x, SecondaryStatFlag.MagnetArea, this.x),
               0,
               this.getDuration(),
               msi.getVal(),
               mobSkill.getLevel(),
               false
            );
            break;
         case OBSTACLE_ATTACK:
            this.doSkill_ObstacleAttack(monster);
            break;
         case STIGMA:
            player.incStigma(1);
            break;
         case LAPIDIFICATION:
            if (this.skillLevel == 14) {
               player.giveDebuff(
                  SecondaryStatFlag.Lapidification,
                  Randomizer.rand(5, 8),
                  0,
                  Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.limit)) * 1000,
                  174,
                  14
               );
            } else if (this.skillLevel == 16) {
               player.giveDebuff(SecondaryStatFlag.Lapidification, 1, 0, this.getDuration(), 174, 16);
            } else if (this.skillLevel == 26) {
               player.giveDebuff(SecondaryStatFlag.Lapidification, 1, 0, this.getDuration(), 174, 26);
            } else {
               player.giveDebuff(SecondaryStatFlag.Lapidification, Randomizer.rand(1, Math.max(1, this.x - 1)), 0, this.getDuration(), 174, mobSkill.getLevel());
            }
            break;
         case CASTINGBAR:
            monster.doSkill_CastingBar(
               this.skillId, this.skillLevel, Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.castingTime)), this.succeed, this.failed
            );
            break;
         case UNDEAD:
            if (this.skillLevel == 17) {
               disease = null;
               monster.getMap()
                  .broadcastMessage(
                     MobPacket.mobSkillDelay(
                        monster.getObjectId(), mobSkill.getSkillAfter(), this.getSkillId(), this.getSkillLevel(), 0, Collections.EMPTY_LIST
                     )
                  );
            }
            break;
         case SUMMON2:
            if (this.skillLevel == 162 && monster.getMap().getMobsSize(8800117) > 0) {
               return;
            }

            if (this.skillLevel != 47 && this.skillLevel != 48 && this.skillLevel != 59 && this.skillLevel != 60) {
               this.doSkill_Summon(player, monster);
            } else {
               monster.getMap()
                  .broadcastMessage(
                     MobPacket.mobSkillDelay(
                        monster.getObjectId(), mobSkill.getSkillAfter(), this.getSkillId(), this.getSkillLevel(), 0, Collections.EMPTY_LIST
                     )
                  );
            }
            break;
         case CONTAGION:
            if (player != null) {
               player.giveDebuff(
                  SecondaryStatFlag.Contagion,
                  (int)(player.getStat().getCurrentMaxHp(player) * this.getMobSkillStatsInt(MobSkillStat.fixDamR) / 100.0),
                  0,
                  this.getDuration(),
                  msi.getVal(),
                  mobSkill.getLevel()
               );
            }
            break;
         case LUCID_SKILL:
            if (monster.getMap() instanceof Field_LucidBattle) {
               Field_LucidBattle lucidField = (Field_LucidBattle)monster.getMap();
               switch (this.skillLevel) {
                  case 1:
                  case 2:
                  case 3:
                     lucidField.sendLucidSkill_FlowerTrap(this.skillLevel, Randomizer.nextInt(3), monster.getTruePosition(), (Randomizer.nextInt() & 1) != 0);
                     return;
                  case 4:
                  case 10:
                     lucidField.doFairyDust(monster, this.skillLevel, this);
                     lucidField.sendLucidNotice("저 바람을 맞으면 꿈이 강해질 겁니다!", 4);
                     return;
                  case 5:
                     List<Integer> rains = new ArrayList<>();

                     for (int i = 0; i < this.getMobSkillStatsInt(MobSkillStat.z); i++) {
                        rains.add(this.getMobSkillStatsInt(MobSkillStat.w));
                     }

                     lucidField.sendLucidSkill_LaserRain(this.getMobSkillStatsInt(MobSkillStat.s), rains);
                     lucidField.sendLucidNotice("루시드가 강력한 공격을 사용하려 합니다!", -1);
                     return;
                  case 6:
                  case 11:
                     MapleCharacter controller = monster.getController();
                     if (controller != null) {
                        lucidField.sendLucidSkill_ForcedTeleport(controller, Randomizer.nextInt(8));
                     }

                     return;
                  case 7:
                     lucidField.sendLucidNotice("루시드가 강력한 소환수를 소환했습니다!", -1);
                     boolean left = (Randomizer.nextInt() & 1) != 0;
                     if (lucidField.getPhase() == 1) {
                        lucidField.sendLucidCreateDragon(new Point(0, 0), new Point(0, 0), left);
                        return;
                     }

                     Point createPt = !left ? new Point(1498, 238) : new Point(-138, -1312);
                     xxx = createPt.x;
                     Point pt = new Point(xxx, monster.getPosition().y);
                     lucidField.sendLucidCreateDragon(pt, createPt, left);
                     return;
                  case 8:
                     lucidField.sendLucidSkill_Rush(0);
                     lucidField.sendLucidNotice("루시드가 강력한 공격을 사용하려 합니다!", -1);
                     return;
                  case 9:
                     if (lucidField instanceof Field_LucidBattlePhase2) {
                        Field_LucidBattlePhase2 f = (Field_LucidBattlePhase2)monster.getMap();
                        f.doIllusionBarrage();
                        f.setLastBarriageTime(System.currentTimeMillis());
                     }

                     return;
                  case 12:
                     for (MapleCharacter chr : monster.getMap().getPlayerInRect(monster.getTruePosition(), this.lt.x, this.lt.y, this.rb.x, this.rb.y)) {
                        if (chr.getBuffedValue(SecondaryStatFlag.NotDamaged) == null
                           && chr.getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() == 0) {
                           chr.giveDebuff(SecondaryStatFlag.Contagion, 1, 0, this.duration, this.skillId, this.skillLevel);
                        }
                     }

                     List<Pair<Rectangle, Integer>> ptList = new ArrayList<>();
                     if (lucidField instanceof Field_LucidBattlePhase1) {
                        ptList.add(new Pair<>(new Rectangle(750, 44, 540, 1), 1056));
                        ptList.add(new Pair<>(new Rectangle(75, 44, 195, 1), 210));
                     } else if (lucidField instanceof Field_LucidBattlePhase2) {
                        ptList.add(new Pair<>(new Rectangle(940, -850, 150, 1), 940));
                        ptList.add(new Pair<>(new Rectangle(0, -275, 248, 1), 211));
                        ptList.add(new Pair<>(new Rectangle(535, -60, 150, 1), 591));
                     }

                     lucidField.sendLucidCreateContagionField(ptList);
                     return;
               }
            }
            break;
         case WILL_SKILL:
            switch (this.skillLevel) {
               case 1:
               case 2:
               case 3:
                  int mobTemplateIDx = 0;
                  if (monster.getId() == 8880321 || monster.getId() == 8880322) {
                     mobTemplateIDx = monster.getId() - 18;
                  } else if (monster.getId() == 8880323 || monster.getId() == 8880324) {
                     mobTemplateIDx = monster.getId() - 22;
                  } else if (monster.getId() == 8880353 || monster.getId() == 8880354) {
                     mobTemplateIDx = monster.getId() - 12;
                  } else if (monster.getId() != 8880372 && monster.getId() != 8880373) {
                     mobTemplateIDx = monster.getId() - 8;
                  } else {
                     mobTemplateIDx = monster.getId() - 11;
                  }

                  if (monster.getMap() instanceof Field_WillBattle) {
                     Field_WillBattle fxxx = (Field_WillBattle)monster.getMap();
                     if (fxxx.getNextDestructionTime() != 0L || fxxx.getNextTakeDownTime() != 0L || fxxx.getStartTakeDownTime() != 0L) {
                        return;
                     }

                     List<Integer> gList = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
                     List<Pair<Integer, Integer>> hList = new ArrayList<>();
                     List<Triple<Integer, Integer, Integer>> idx = new ArrayList<>();
                     int index = 0;

                     for (int ix = 1; ix <= 4; ix++) {
                        int size = Math.min(gList.size(), Randomizer.rand(3, 6));
                        int delay = 1800 * ix;

                        for (int j = 0; j < size; j++) {
                           int randomIdx = Randomizer.nextInt(gList.size());
                           int listIdx = gList.get(randomIdx);

                           while (hList.contains(new Pair<>(ix, listIdx))) {
                              randomIdx = Randomizer.nextInt(gList.size());
                              listIdx = gList.get(randomIdx);
                           }

                           hList.add(new Pair<>(ix, listIdx));
                           idx.add(new Triple<>(++index, delay, -650 + 130 * listIdx));
                        }

                        for (Pair<Integer, Integer> h : hList) {
                           if (h.getLeft() == ix) {
                              gList.remove(h.getRight());
                           }

                           if (h.getLeft() == ix - 1 && !gList.contains(h.getRight())) {
                              gList.add(h.getRight());
                           }
                        }
                     }

                     fxxx.sendWillSpiderAttack(null, mobTemplateIDx, this.skillId, this.skillLevel, idx);
                  }
                  break label1171;
               case 4:
                  if (monster.getMap() instanceof Field_WillBattle) {
                     Field_WillBattle fx = (Field_WillBattle)monster.getMap();
                     fx.sendWillNotice("눈동자를 공격해서 다른 공간에 달빛을 흘려보내세요. 어서 달빛 보호막을 생성해야 해요", 245, 28000);
                     fx.sendWillUnk();
                     MapleMonster mob = MapleLifeFactory.getMonster(8880305);
                     mob.setMaxHp(Long.MAX_VALUE);
                     mob.getStats().setMaxHp(Long.MAX_VALUE);
                     player.getMap().spawnMonsterOnGroundBelow(mob, new Point(0, -2020));
                     mob = MapleLifeFactory.getMonster(8880305);
                     mob.setMaxHp(Long.MAX_VALUE);
                     mob.getStats().setMaxHp(Long.MAX_VALUE);
                     player.getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 159));
                     List<MapleCharacter> playerList = new ArrayList<>(mob.getMap().getCharactersThreadsafe());
                     Collections.shuffle(playerList);
                     int a = 0;

                     for (MapleCharacter pxxxx : playerList) {
                        fx.sendWillSpiderAttack(
                           pxxxx, 0, this.skillId, this.skillLevel, a == 0 ? Collections.emptyList() : Collections.singletonList(null), true
                        );
                        a ^= 1;
                     }

                     fx.setNextDestructionTime(System.currentTimeMillis() + 21000L);
                  }
                  break label1171;
               case 5:
                  if ((monster.getId() == 8880300 || monster.getId() == 8880340 || monster.getId() == 8880360) && monster.getMap() instanceof Field_WillBattle) {
                     Field_WillBattle fx = (Field_WillBattle)monster.getMap();
                     if (fx.getNextDestructionTime() != 0L || fx.getNextTakeDownTime() != 0L || fx.getStartTakeDownTime() != 0L) {
                        return;
                     }

                     MapleMonster will1 = player.getMap().getMonsterById(monster.getId() + 3);
                     MapleMonster will2 = player.getMap().getMonsterById(monster.getId() + 4);
                     if (will1 != null && will2 != null) {
                        long hp1 = 0L;
                        long hp2 = 0L;
                        if (will1 != null) {
                           hp1 = will1.getHp();
                        }

                        if (will2 != null) {
                           hp2 = will2.getHp();
                        }

                        long newhp = Math.max(hp1, hp2);
                        monster.setHp(newhp);
                        will1.setHp(newhp);
                        will2.setHp(newhp);
                        monster.getMap().broadcastMessage(MobPacket.showBossHP(will1));
                        monster.getMap().broadcastMessage(MobPacket.showBossHP(will2));
                        int typex = Randomizer.nextInt(2);
                        fx.sendWillNotice("윌이 힘을 개방하려 해요. 거짓된 공간이 붕괴할 테니 진실한 공간을 찾아 대피하세요.", 245, 3000);
                        fx.setMonitorBreakType(typex);
                        fx.setStartMonitorBreakTime(System.currentTimeMillis() + 3000L);
                        player.getMap().broadcastMessage(MobPacket.showBossHP(monster));
                        fx.sendWillSpiderAttack(null, 0, this.skillId, this.skillLevel, typex == 0 ? Collections.emptyList() : Collections.singletonList(null));
                     }
                  }
               case 6:
               case 7:
               case 8:
               case 13:
               case 14:
               default:
                  break label1171;
               case 9:
                  if (monster.getMap() instanceof Field_WillBattle) {
                     Field_WillBattle fx = (Field_WillBattle)monster.getMap();
                     List<MapleCharacter> players = monster.getMap().getCharactersThreadsafe();
                     Collections.shuffle(players);
                     MapleCharacter targetx = players.get(Randomizer.rand(0, players.size() - 1));
                     if (targetx.giveDebuff(SecondaryStatFlag.WillPoison, 1, 0, 15000L, 242, 9)) {
                        fx.sendWillCreatePoison(targetx);
                        targetx.setLastWillAttackTime(System.currentTimeMillis());
                     }
                  }
                  break label1171;
               case 10:
               case 11:
                  if (monster.getMap() instanceof Field_WillBattle) {
                     Field_WillBattle fx = (Field_WillBattle)monster.getMap();
                     fx.setNext3rdAttackType(fx.getNext3rdAttackType() ^ 1);
                     fx.setNext3rdAttackTime(System.currentTimeMillis() + 5000L);
                     fx.sendWillSpiderAttack(player, monster.getId(), this.skillId, this.skillLevel, Collections.singletonList(null));
                  }
                  break label1171;
               case 12:
                  int mobTemplateID = 0;
                  if (monster.getId() == 8880325 || monster.getId() == 8880326) {
                     mobTemplateID = monster.getId() - 22;
                  } else if (monster.getId() == 8880327 || monster.getId() == 8880328) {
                     mobTemplateID = monster.getId() - 26;
                  } else if (monster.getId() == 8880355 || monster.getId() == 8880356) {
                     mobTemplateID = monster.getId() - 12;
                  } else if (monster.getId() != 8880376 && monster.getId() != 8880377) {
                     mobTemplateID = monster.getId() - 8;
                  } else {
                     mobTemplateID = monster.getId() - 13;
                  }

                  if (monster.getMap() instanceof Field_WillBattle) {
                     Field_WillBattle fxx = (Field_WillBattle)monster.getMap();
                     if (fxx.getNextDestructionTime() != 0L || fxx.getNextTakeDownTime() != 0L || fxx.getStartTakeDownTime() != 0L) {
                        return;
                     }

                     List<BossWill.BeholderData.InfoData.Gen> gen = null;
                     if (fxx.getPhase() == 1) {
                        gen = BossWill.beholder.normalData.phase1.getGen();
                     } else if (fxx.getPhase() == 2) {
                        gen = BossWill.beholder.normalData.phase2.getGen();
                     } else if (fxx.getPhase() == 3) {
                        gen = BossWill.beholder.normalData.phase3.getGen();
                     }

                     if (gen != null) {
                        int rand = Randomizer.rand(0, gen.size() - 1);
                        BossWill.BeholderData.InfoData.Gen g = gen.get(rand);

                        for (BossWill.BeholderData.InfoData.Gen.GenEntry entry : g.getEntry()) {
                           WillBeholder.Gen e = new WillBeholder.Gen(mobTemplateID, entry.getX(), entry.getRy());
                           WillBeholder beholder = new WillBeholder(e);
                           fxx.sendWillCreateBeholder(beholder);
                        }
                     }

                     List<BossWill.BeholderData.InfoData.Straight> straight = null;
                     if (fxx.getPhase() == 1) {
                        straight = BossWill.beholder.normalData.phase1.getStraight();
                     } else if (fxx.getPhase() == 2) {
                        straight = BossWill.beholder.normalData.phase2.getStraight();
                     } else if (fxx.getPhase() == 3) {
                        straight = BossWill.beholder.normalData.phase3.getStraight();
                     }

                     if (straight != null) {
                        int rand = Randomizer.rand(0, straight.size() - 1);
                        BossWill.BeholderData.InfoData.Straight s = straight.get(rand);
                        BossWill.BeholderData.InfoData.Straight.StraightEntry entry = s.getEntry();
                        new Rect(0, 0, 0, 0);
                        Rect var234;
                        if (monster.getTruePosition().y < -500) {
                           var234 = new Rect(-690, -2634, 695, -2019);
                        } else {
                           var234 = new Rect(-690, -455, 695, 160);
                        }

                        WillBeholder.Straight e = new WillBeholder.Straight(
                           mobTemplateID, entry.getShootAngle(), entry.getShootSpeed(), entry.getShootInterval(), entry.getShootCount(), var234
                        );
                        WillBeholder beholder = new WillBeholder(e);
                        fxx.sendWillCreateBeholder(beholder);
                     }
                  }
                  break label1171;
               case 15:
                  if (monster.getMap() instanceof Field_WillBattle) {
                     Field_WillBattle fx = (Field_WillBattle)monster.getMap();
                     fx.destructionPattern();
                  }
                  break label1171;
            }
         case JINHILLAH_THREAD:
            if (monster.getMap() instanceof Field_JinHillah) {
               Field_JinHillah f = (Field_JinHillah)monster.getMap();
               if (f.isDisabledRedThread()) {
                  return;
               }

               f.createJinHillahCreateThread(monster.getObjectId(), this.skillLevel);
            }
            break;
         case JINHILLAH_SKILL:
            switch (this.skillLevel) {
               case 1:
                  if (monster.getMap() instanceof Field_JinHillah) {
                     Field_JinHillah f = (Field_JinHillah)monster.getMap();

                     for (MapleCharacter p : f.getCharactersThreadsafe()) {
                        if (!p.isJinHillahDeathCountOut()) {
                           for (Field_JinHillah.JinHillahDeathCount dc : new ArrayList<>(p.getJinHillahDeathCount())) {
                              if (dc.getStatus() == Field_JinHillah.JinHillahDeathCountType.Red) {
                                 dc.setStatus(Field_JinHillah.JinHillahDeathCountType.Out);
                              }
                           }
                        }

                        f.sendJinHillahDeathCount(p);
                        f.updatePartyMemberDeathCount(p, true);
                        p.updatePartyMemberHP();
                        p.send(CWvsContext.enableActions(p));
                     }

                     f.resetJinHillahCandle();
                     int time = 0;
                     if (monster.getHPPercent() >= 66) {
                        time = 152000;
                     } else if (monster.getHPPercent() >= 33) {
                        time = 126000;
                     } else {
                        time = 100000;
                     }

                     f.sendJinHillahSandglass(null, time, 247, 1);
                     f.setNextFullMapAttackTime(System.currentTimeMillis() + time);
                     monster.removeSkillFilter(3);
                     monster.removeSkillFilter(5);
                     monster.removeAttackBlocked(1);
                     monster.removeAttackBlocked(2);
                     monster.removeAttackBlocked(3);
                     monster.removeAttackBlocked(5);
                     monster.removeAttackBlocked(7);
                     monster.removeAttackBlocked(8);
                     monster.removeAttackBlocked(9);
                     if (monster.getHPPercent() <= 75) {
                        monster.removeAttackBlocked(0);
                        monster.removeAttackBlocked(4);
                        monster.removeAttackBlocked(6);
                     }

                     if (monster.getHPPercent() <= 50) {
                        monster.removeSkillFilter(0);
                     }

                     if (monster.getHPPercent() <= 25) {
                        monster.removeSkillFilter(1);
                     }

                     monster.broadcastAttackBlocked();
                  }
               default:
                  break label1171;
            }
         case JINHILLAH_MIST:
            try {
               if (this.jMist != null && monster.getMap().getAffectedAreasBySkillID(this.skillId).size() < this.jMist.getAreaMax()) {
                  this.doSkill_AffectedArea(monster, null);
               }
            } catch (Exception var30) {
               System.out.println("Jinhilla Mist Err");
               var30.printStackTrace();
            }
            break;
         case DAMAGE:
            Point basePoint = monster.getTruePosition();
            if (this.skillLevel == 25 || this.skillLevel == 26 || this.skillLevel == 33 || this.skillLevel == 34) {
               basePoint = Field_Zakum.getZakumHandThrowingDamagePos(Field_Zakum.getZakumArmIndex(monster.getId()));
            } else if (this.skillLevel == 27) {
               int index = Field_Zakum.getZakumArmIndex(monster.getId());
               int level = 0;
               byte var180;
               if (index == 0) {
                  var180 = 0;
               } else if (index != 1) {
                  if (index != 2) {
                     return;
                  }

                  var180 = 2;
               } else {
                  var180 = 1;
               }

               basePoint = Field_Zakum.getZakumHandClapDamagePos(var180);
            }

            Point lt2 = mobSkillInfo.lt;
            Point rb2 = mobSkillInfo.rb;

            for (MapleCharacter list : monster.getMap().getPlayerInRect(basePoint, lt2.x, lt2.y, rb2.x, rb2.y)) {
               list.onDamageByMobSkill(msi, this.skillLevel, mobSkillInfo, 0, monster.getId(), 0, 0);
            }
            break;
         case PAPULATUS_SKILL:
            if (monster.getMap() instanceof Field_Papulatus) {
               Field_Papulatus f = (Field_Papulatus)monster.getMap();
               switch (this.skillLevel) {
                  case 1:
                  case 2:
                     this.doSkill_Summon(player, monster);
                     f.applyTimeCurse(monster, this.skillLevel, mobSkillInfo.getMobSkillStatsInt(MobSkillStat.time));
                  case 3:
                  case 6:
                  default:
                     break;
                  case 4:
                     monster.getMap()
                        .broadcastMessage(
                           MobPacket.mobSkillDelay(
                              monster.getObjectId(), mobSkill.getSkillAfter(), this.getSkillId(), this.getSkillLevel(), 0, Collections.EMPTY_LIST
                           )
                        );
                     MapleCharacter targetx = null;
                     List<MapleCharacter> players = new ArrayList<>();

                     for (MapleCharacter p : f.getCharactersThreadsafe()) {
                        if (p != null && !p.isHidden()) {
                           players.add(p);
                        }
                     }

                     if (!players.isEmpty()) {
                        Collections.shuffle(players);
                        targetx = players.stream().findAny().orElse(null);
                        PacketEncoder packetx = new PacketEncoder();
                        packetx.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
                        packetx.write(73);
                        packetx.write(0);
                        packetx.writeInt(this.skillId);
                        packetx.writeInt(this.skillLevel);
                        targetx.send(packetx.getPacket());
                        packetx = new PacketEncoder();
                        packetx.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
                        packetx.writeInt(targetx.getId());
                        packetx.write(73);
                        packetx.write(0);
                        packetx.writeInt(this.skillId);
                        packetx.writeInt(this.skillLevel);
                        f.broadcastMessage(packetx.getPacket());
                        f.setTeleportUser(targetx);
                     }
                     break;
                  case 5:
                     monster.getMap().broadcastMessage(MobPacket.talkMonster(monster.getObjectId(), 1));
                     targetx = null;
                     players = new ArrayList<>();

                     for (MapleCharacter px : f.getCharactersThreadsafe()) {
                        if (px != null && !px.isHidden()) {
                           players.add(px);
                        }
                     }

                     if (!players.isEmpty()) {
                        Collections.shuffle(players);
                        targetx = players.stream().findAny().orElse(null);
                        targetx.giveDebuff(
                           SecondaryStatFlag.TimeTorrent, 1, 0, mobSkillInfo.getMobSkillStatsInt(MobSkillStat.time) * 1000, this.skillId, this.skillLevel
                        );
                     }
                     break;
                  case 7:
                     int castingTime = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.castingTime);
                     this.setCasting(monster, castingTime);
                     f.startPapulatusCrack(castingTime, mobSkillInfo.getMobSkillStatsInt(MobSkillStat.x), mobSkillInfo.getMobSkillStatsInt(MobSkillStat.y));
               }
            }
            break;
         case BLACK_MAGE_CURSE:
            if (monster.getMap() instanceof Field_BlackMage) {
               Field_BlackMage f = (Field_BlackMage)monster.getMap();
               if (f != null) {
                  if (player.hasDisease(SecondaryStatFlag.CurseOfDestruction)) {
                     MapleDiseaseValueHolder h = player.getDiseases(SecondaryStatFlag.CurseOfDestruction);
                     if (h != null && h.level != this.skillLevel) {
                        f.decrementDeathCount(player, false);
                        int hp = (int)(player.getStat().getCurrentMaxHp(player) * 0.01 * 60.0);
                        player.addHP(-hp, false);
                        player.giveDebuff(SecondaryStatFlag.Seal, 1, 0, 5000L, 120, 39);
                     }
                  }

                  player.giveDebuff(SecondaryStatFlag.CurseOfDestruction, this.skillLevel == 1 ? 4 : 10, 0, 5000L, this.skillId, this.skillLevel);
               }
            }
            break;
         case CREATE_OBSTACLE:
            if (monster.getMap() instanceof Field_RimenNearTheEnd) {
               int size = 3;
               if (this.skillLevel == 3 || this.skillLevel == 4) {
                  size = 4;
               }

               Field_RimenNearTheEnd f = (Field_RimenNearTheEnd)monster.getMap();
               f.createMeteorite(size);
            }

            if (monster.getMap() instanceof Field_SerenPhase2) {
               Field_SerenPhase2 f = (Field_SerenPhase2)monster.getMap();
               f.createFlames();
            }
            break;
         case FIELD_COMMAND:
            monster.getMap().setFieldCommand(mobSkillInfo.fieldCommand);
            if (monster.getMap() instanceof Field_Zakum) {
               Field_Zakum f = (Field_Zakum)monster.getMap();
               if (f.isZakum(monster.getId())) {
                  f.sendSmartMobNotice(SmartMobNoticeType.Normal, monster.getId(), SmartMobMsgType.Field, this.skillLevel, "자쿰이 팔을 내려칠 준비를 합니다.");
               }
            }
            break;
         case PASSIVE_FIREWALK:
            this.doSkill_PassiveFirewalk(monster);
            break;
         case CAPDEBUFF_BLUE:
         case CAPDEBUFF_RED:
            for (MapleCharacter pxx : monster.getMap().getCharactersThreadsafe()) {
               MobSkillInfo info = MobSkillFactory.getMobSkill(this.skillId, this.skillLevel);
               if (pxx.getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() > 0 || pxx.getBuffedValue(SecondaryStatFlag.NotDamaged) != null) {
                  NormalEffect e = new NormalEffect(pxx.getId(), EffectHeader.ResistEffect);
                  pxx.send(e.encodeForLocal());
                  pxx.getMap().broadcastMessage(pxx, e.encodeForRemote(), false);
               } else if (info != null) {
                  pxx.dispelDebuff(SecondaryStatFlag.CapDebuff);
                  pxx.giveDebuff(SecondaryStatFlag.CapDebuff, info.getX(), info.getY(), info.getDuration(), this.skillId, this.skillLevel, true);
               }
            }
            break;
         case BREAKDOWN_TIMEZONE:
            Field field = monster.getMap();
            Field insideField = GameServer.getInstance(field.getChannel()).getMapFactory().getMap(field.getId() + 10);
            if (insideField != null) {
               insideField.resetFully(false);
               field.setObjectEnable("Pt0" + Randomizer.rand(1, 9) + "gate");
               monster.onVonbonBreakDownTimeZone();
               stats.put(MobTemporaryStatFlag.INVINCIBLE, new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, this.skillId, this, true));
            }
            break;
         case LASER_ATTACK:
            if (monster.getBuff(MobTemporaryStatFlag.LASER) != null) {
               return;
            }

            int laserSpeed = 1;
            int laserAngle = 45;
            int laserDirection = 1;
            monster.setLaserSpeed(laserSpeed);
            monster.setLaserAngle(laserAngle);
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(
               MobTemporaryStatFlag.LASER, mobSkillInfo.getX(), MobSkillID.LASER_ATTACK.getVal(), mobSkillInfo, true
            );
            e.setValue(laserSpeed);
            e.setN(laserSpeed);
            e.setU(laserAngle);
            e.setW(laserDirection);
            e.setDuration(5000000);
            monster.applyStatus(e);
            monster.getMap().broadcastMessage(MobPacket.mobLaserControl(monster.getObjectId(), laserAngle, laserSpeed, laserDirection));
            break;
         case LASER_CONTROL:
            monster.setNextLaserDirection((Randomizer.nextInt() & 3) == 3 ? 1 : 0);
            y = mobSkillInfo.getY();
            if (monster.getMap() instanceof Field_BlackHeavenBoss) {
               Field_BlackHeavenBoss f = (Field_BlackHeavenBoss)monster.getMap();
               if (f != null && f.isHellMode()) {
                  y = 2;
               }
            }

            monster.setNextLaserValue(y);
            monster.setLaserControlEndTime(System.currentTimeMillis() + this.getDuration());
            break;
         case HILLAH_VAMPIRE:
            monster.getMap()
               .broadcastMessage(
                  MobPacket.mobSkillDelay(monster.getObjectId(), mobSkill.getMobSkillID(), this.getSkillId(), this.getSkillLevel(), 0, Collections.EMPTY_LIST)
               );
            monster.getMap().broadcastMessage(MobPacket.talkMonster(monster.getObjectId(), 4));
            break;
         case SEREN_LASER:
            player.addSerenLaserDebuffTime(500);
            break;
         case SEREN_SKILL2:
            int x = isLeft ? monster.getTruePosition().x - 250 : monster.getTruePosition().x + 250;
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.SEREN_SPECIAL_ATTACK.getValue());
            boolean unk = false;
            packet.write(false);
            if (!unk) {
               MapleMonster boss = player.getMap().getMonsterById(8880603);
               if (boss == null) {
                  boss = player.getMap().getMonsterById(8880633);
               }

               if (boss != null) {
                  packet.writeInt(263);
                  packet.writeInt(1);
                  packet.writeInt(boss.getObjectId());
                  packet.write(isLeft);
                  packet.writeInt(x);
                  packet.writeInt(305);
                  player.send(packet.getPacket());
               }
            }
            break;
         case SEREN_SKILL:
            switch (this.skillLevel) {
               case 1:
               case 2:
                  if (monster.getMap() instanceof Field_Seren) {
                     Field_Seren f = (Field_Seren)monster.getMap();
                     List<SerenLaser> serenLasers = new ArrayList<>();
                     boolean phase2 = this.skillLevel == 2;
                     int angle = Randomizer.rand(0, 45);
                     if (phase2) {
                        angle = Randomizer.rand(0, 30);
                     }

                     int size = 2;
                     if (phase2) {
                        size = 3;
                     }

                     int posx = 0;
                     int posy = -109;
                     int length = 1500;

                     for (int ix = 0; ix < size * 4; ix++) {
                        xxx = (int)(length * Math.sin(angle * Math.PI / 180.0));
                        y = (int)(length * Math.cos(angle * Math.PI / 180.0));
                        angle += 90 / size;
                        xxx += posx;
                        y += posy;
                        serenLasers.add(new SerenLaser(posx, posy, xxx, y));
                     }

                     f.sendSerenLaser(monster.getObjectId(), 2, this.skillLevel == 2 ? 1320 : 1800, serenLasers);
                  }
               default:
                  break label1171;
            }
         case GUARDIAN_ANGEL_SLIME:
            switch (this.skillLevel) {
               case 1:
                  Point[] posList = new Point[]{
                     new Point(300, -1636), new Point(1200, -1638), new Point(700, -1937), new Point(700, -1382), new Point(700, -1089)
                  };
                  int[] mobList;
                  if (monster.getId() == 8880700) {
                     mobList = new int[]{8880704, 8880705, 8880706, 8880707, 8880708};
                  } else {
                     mobList = new int[]{8880715, 8880716, 8880717, 8880718, 8880719};
                  }

                  monster.getMap()
                     .spawnMonsterOnGroundBelow(
                        MapleLifeFactory.getMonster(mobList[Randomizer.nextInt(mobList.length)]), posList[Randomizer.nextInt(posList.length)]
                     );
                  break;
               default:
                  if (player != null && player.isGM()) {
                     player.dropMessage(5, "skillLv : " + this.skillLevel + " found.");
                  }
            }
      }

      if (stats.size() > 0 && monster != null) {
         int targetMobType = this.getMobSkillStatsInt(MobSkillStat.targetMobType);
         Field fieldx = monster.getMap();
         if (targetMobType == 0) {
            if (this.lt != null && this.rb != null && (this.lt.x != 0 || this.lt.y != 0 || this.rb.x != 0 || this.rb.y != 0) && skill) {
               for (MapleMonster mob : fieldx.getMobsInRect(monster.getTruePosition(), this.lt.x, this.lt.y, this.rb.x, this.rb.y)) {
                  mob.applyMonsterBuff(stats, this.getSkillId(), this.getDuration(), this, reflection);
               }
            } else {
               long dur = this.getDuration();
               if (this.skillId == MobSkillID.BREAKDOWN_TIMEZONE.getVal()) {
                  dur = 20000L;
               }

               monster.applyMonsterBuff(stats, this.getSkillId(), dur, this, reflection);
            }
         } else {
            switch (targetMobType) {
               case 1:
                  monster.applyMonsterBuff(stats, this.getSkillId(), this.getDuration(), this, reflection);
                  break;
               case 2:
                  int parentObjectID = monster.getParentObjectID();
                  MapleMonster mob = monster.getMap().getMonsterByOid(parentObjectID);
                  if (mob != null) {
                     mob.applyMonsterBuff(stats, this.getSkillId(), this.getDuration(), this, reflection);
                  }
                  break;
               case 3:
                  for (MapleMonster mobx : fieldx.getMobsInRect(monster.getTruePosition(), this.lt.x, this.lt.y, this.rb.x, this.rb.y)) {
                     mobx.applyMonsterBuff(stats, this.getSkillId(), this.getDuration(), this, reflection);
                  }
            }
         }
      }

      if (disease == SecondaryStatFlag.Stun && player.getBuffedValue(SecondaryStatFlag.GrandCrossSize) != null) {
         disease = null;
      }

      if (disease != null && player != null) {
         if (this.lt != null && this.rb != null && skill && monster != null) {
            for (MapleCharacter chrxxx : this.getPlayersInRange(monster, player)) {
               if (chrxxx.getBuffedValue(SecondaryStatFlag.NotDamaged) == null
                  && chrxxx.getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() == 0) {
                  chrxxx.giveDebuff(disease, this);
               }
            }
         } else if (player.getBuffedValue(SecondaryStatFlag.NotDamaged) == null
            && player.getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() == 0) {
            player.giveDebuff(disease, this);
         }
      }

      if (monster != null) {
         monster.setMp(monster.getMp() - this.getMpCon());
      }
   }

   public int getSkillId() {
      return this.skillId;
   }

   public int getSkillLevel() {
      return this.skillLevel;
   }

   public int getMpCon() {
      return this.mpCon;
   }

   public List<Integer> getSummons() {
      return Collections.unmodifiableList(this.toSummon);
   }

   public int getSpawnEffect() {
      return this.spawnEffect;
   }

   public int getHP() {
      return this.hp;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public long getDuration() {
      return this.duration;
   }

   public long getCoolTime() {
      return this.cooltime;
   }

   public Point getLt() {
      return this.lt;
   }

   public Point getRb() {
      return this.rb;
   }

   public Point getLt2() {
      return this.lt2;
   }

   public Point getRb2() {
      return this.rb2;
   }

   public int getLimit() {
      return this.limit;
   }

   public int getForce() {
      return this.force;
   }

   public void setForce(int force) {
      this.force = force;
   }

   public int getForceX() {
      return this.forcex;
   }

   public void setForceX(int forceX) {
      this.forcex = forceX;
   }

   public boolean makeChanceResult() {
      return this.prop >= 1.0 || Math.random() < this.prop;
   }

   public int getSummonID(int idx) {
      return this.summonIDs.get(idx);
   }

   public int getSummonSize() {
      return this.summonIDs.size();
   }

   public void addSummonID(int mobID) {
      this.summonIDs.add(mobID);
   }

   public Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
      Point mylt;
      Point myrb;
      if (facingLeft) {
         mylt = new Point(this.lt.x + posFrom.x, this.lt.y + posFrom.y);
         myrb = new Point(this.rb.x + posFrom.x, this.rb.y + posFrom.y);
      } else {
         myrb = new Point(this.lt.x * -1 + posFrom.x, this.rb.y + posFrom.y);
         mylt = new Point(this.rb.x * -1 + posFrom.x, this.lt.y + posFrom.y);
      }

      return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
   }

   private List<MapleCharacter> getPlayersInRange(MapleMonster monster, MapleCharacter player) {
      Rectangle bounds = this.calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
      List<MapleCharacter> players = new ArrayList<>();
      players.add(player);
      return monster.getMap().getPlayersInRectAndInList(bounds, players);
   }

   private List<MapleMapObject> getObjectsInRange(MapleMonster monster, MapleMapObjectType objectType) {
      Rectangle bounds = this.calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
      List<MapleMapObjectType> objectTypes = new ArrayList<>();
      objectTypes.add(objectType);
      return monster.getMap().getMapObjectsInRect(bounds, objectTypes);
   }

   public void putMobSkillStats(MobSkillStat key, String value) {
      this.mobSkillStats.put(key, value);
   }

   public String getMobSkillStats(MobSkillStat key) {
      return this.mobSkillStats.get(key);
   }

   public int getMobSkillStatsInt(MobSkillStat key) {
      String v = this.mobSkillStats.get(key);
      int ret = 0;
      if (v != null) {
         ret = Integer.parseInt(v);
      }

      return ret;
   }

   public Map<MobSkillStat, String> getMobSkillStats() {
      return this.mobSkillStats;
   }

   public int getSkillStatIntValue(MobSkillStat key) {
      return !this.getMobSkillStats().containsKey(key) ? 0 : Integer.parseInt(this.getMobSkillStats(key));
   }

   public void fireAtRandomAttack(MapleMonster monster, int afterSkillDelay, int sequenceDelay) {
      List<Rect> rect = new ArrayList<>();
      int xLeft = monster.getMap().getLeft();
      int createCount = 10;
      int limitCount = 0;
      List<Point> posList = new ArrayList<>();

      while (limitCount++ < 100) {
         int randomX = Randomizer.rand(-710, 710);
         posList.add(new Point(randomX, -16));
         if (createCount <= posList.size()) {
            break;
         }
      }

      for (int i = 0; i < createCount; i++) {
         int x = posList.get(i).x;
         int y = posList.get(i).y;
         rect.add(new Rect(x + this.lt.x, y + this.lt.y, x + this.rb.x, y + this.rb.y));
      }

      monster.setFireAtRandomAttack(rect);
      monster.getMap().broadcastMessage(MobPacket.mobSkillDelay(monster.getObjectId(), 960, this.getSkillId(), this.getSkillLevel(), 900, rect));
   }

   public void createDynamicFoothold(MapleMonster monster, int afterSkillDelay) {
      List<Rect> rect = new ArrayList<>();
      int xLeft = monster.getMap().getLeft();
      int createCount = Randomizer.rand(2, 5);
      int count = 0;
      int limitCount = 0;
      List<Point> posList = new ArrayList<>();

      while (limitCount++ < 100) {
         int randomX = Math.max(-515, Math.min(557, Randomizer.nextInt(monster.getMap().getWidth()) + xLeft));
         if (count == 0) {
            posList.add(new Point(randomX, -16));
         } else {
            boolean find = true;

            for (Point p : posList) {
               if (Math.abs(p.x - randomX) <= 200) {
                  find = false;
               }
            }

            if (!find) {
               continue;
            }

            posList.add(new Point(randomX, -16));
         }

         if (createCount <= ++count) {
            break;
         }
      }

      monster.setObstaclePosition(posList);

      for (int i = 0; i < posList.size(); i++) {
         int x = posList.get(i).x;
         int y = posList.get(i).y;
         rect.add(new Rect(x + this.lt.x, y + this.lt.y, x + this.rb.x, y + this.rb.y));
      }

      monster.getMap().broadcastMessage(MobPacket.mobSkillDelay(monster.getObjectId(), afterSkillDelay, this.getSkillId(), this.getSkillLevel(), 0, rect));
   }

   public int getOtherSkillID() {
      return this.otherSkillID;
   }

   public void setOtherSkillID(int otherSkillID) {
      this.otherSkillID = otherSkillID;
   }

   public int getOtherSkillLev() {
      return this.otherSkillLev;
   }

   public void setOtherSkillLev(int otherSkillLev) {
      this.otherSkillLev = otherSkillLev;
   }

   public MobSkillInfo.CastingActionData getSucceed() {
      return this.succeed;
   }

   public void setSucceed(MobSkillInfo.CastingActionData succeed) {
      this.succeed = succeed;
   }

   public MobSkillInfo.CastingActionData getFailed() {
      return this.failed;
   }

   public void setFailed(MobSkillInfo.CastingActionData failed) {
      this.failed = failed;
   }

   public HashMap<Integer, Integer> getScreen_delay() {
      return this.screen_delay;
   }

   public List<Point> getFixedPos() {
      return this.fixedPos;
   }

   public void addFixedPos(Point pos) {
      this.fixedPos.add(pos);
   }

   public List<Integer> getFixedDir() {
      return this.fixedDir;
   }

   public void addFixedDir(int dir) {
      this.fixedDir.add(dir);
   }

   public FieldCommand getFieldCommand() {
      return this.fieldCommand;
   }

   public void setFieldCommand(FieldCommand fieldCommand) {
      this.fieldCommand = fieldCommand;
   }

   public List<Integer> getMobGroup(int index) {
      return this.mobGroup.isEmpty() ? null : this.mobGroup.get(index);
   }

   public void addMobGroup(List<Integer> groups) {
      this.mobGroup.add(groups);
   }

   public void addAffectedOtherSkill(AffectedOtherSkill affectedOtherSkill) {
      this.affectedOtherSkills.add(affectedOtherSkill);
   }

   public List<AffectedOtherSkill> getAffectedOtherSkills() {
      return this.affectedOtherSkills;
   }

   public JinHillahPoisonMist getJMist() {
      return this.jMist;
   }

   public void setJMist(JinHillahPoisonMist mist) {
      this.jMist = mist;
   }

   public static class CastingActionData {
      public int attackIdx;
      public int skillIdx;

      public CastingActionData(MapleData data) {
         MapleData skill = data.getChildByPath("skill");
         if (skill != null) {
            for (MapleData d : skill) {
               this.skillIdx = Integer.parseInt(d.getName());
            }
         }

         MapleData attack = data.getChildByPath("attack");
         if (attack != null) {
            for (MapleData d : attack) {
               this.attackIdx = Integer.parseInt(d.getName());
            }
         }
      }
   }
}
