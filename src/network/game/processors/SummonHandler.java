package network.game.processors;

import constants.GameConstants;
import constants.QuestExConstants;
import database.DBConfig;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.effect.child.PostSkillEffect;
import objects.fields.Field;
import objects.fields.ForceAtom;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SecondAtom;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.Dragon;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.Skeleton;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.item.MapleItemInformationProvider;
import objects.movepath.LifeMovementFragment;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.summoned.SummonedSkillEntry;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.PlayerStats;
import objects.users.achievement.AchievementFactory;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.StringUtil;

public class SummonHandler {
   public static final void MoveDragon(PacketDecoder slea, MapleCharacter chr) {
      slea.skip(12);
      List<LifeMovementFragment> res = MovementParse.parseMovement(slea);
      if (chr != null && chr.getDragon() != null && res.size() > 0) {
         Point pos = chr.getDragon().getPosition();
         MovementParse.updatePosition(res, chr.getDragon(), 0);
         if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.moveDragon(chr.getDragon(), pos, res), chr.getTruePosition());
         }
      }
   }

   public static final void MoveSummon(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         MapleMapObject obj = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
         if (obj != null) {
            if (obj instanceof Dragon) {
               MoveDragon(slea, chr);
            } else {
               Summoned sum = (Summoned)obj;
               if (sum.getOwnerId() == chr.getId() && sum.getSkillLevel() > 0 && sum.getMoveAbility() != SummonMoveAbility.STATIONARY) {
                  slea.skip(12);
                  List<LifeMovementFragment> res = MovementParse.parseMovement(slea);
                  Point pos = sum.getPosition();
                  MovementParse.updatePosition(res, sum, 0);
                  if (res.size() > 0) {
                     chr.getMap().broadcastMessage(chr, CField.SummonPacket.moveSummon(chr.getId(), sum.getObjectId(), pos, res), sum.getTruePosition());
                  }
               } else {
                  if (!DBConfig.isHosting) {
                     System.out.println(sum.getMoveAbility().toString());
                  }
               }
            }
         }
      }
   }

   public static final void DamageSummon(PacketDecoder slea, MapleCharacter chr) {
      int objectID = slea.readInt();
      int unkByte = slea.readByte();
      int damage = slea.readInt();
      int monsterIdFrom = slea.readInt();
      Summoned summon = (Summoned)chr.getMap().getMapObject(objectID, MapleMapObjectType.SUMMON);
      boolean remove = false;
      if (summon != null) {
         if (summon.getSkill() == 4341006 && chr.getSkillLevel(4330009) > 0) {
            PacketEncoder pw = new PacketEncoder();
            pw.writeShort(SendPacketOpcode.DODGE_SKILL_READY.getValue());
            chr.send(pw.getPacket());
         }

         if ((summon.isPuppet() || summon.getSkill() == 3221014) && summon.getOwnerId() == chr.getId() && damage > 0) {
            summon.addHP(-damage);
            if (summon.getHP() <= 0) {
               remove = true;
            }

            chr.getMap()
               .broadcastMessage(
                  chr, CField.SummonPacket.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getTruePosition()
               );
         }
      }

      if (remove) {
         chr.temporaryStatReset(SecondaryStatFlag.indieSummon);
      }
   }

   public static void SummonAttack(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.isAlive() && chr.getMap() != null) {
         Field map = chr.getMap();
         int objectId = slea.readInt();
         MapleMapObject obj = map.getMapObject(objectId, MapleMapObjectType.SUMMON);
         if (obj != null && obj instanceof Summoned) {
            Summoned summon = (Summoned)obj;
            if (summon.getOwnerId() == chr.getId() && summon.getSkillLevel() > 0) {
               SummonedSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
               if (summon.getSkill() / 1000000 != 35
                  && summon.getSkill() != 400041038
                  && summon.getSkill() != 400011065
                  && summon.getSkill() != 33101008
                  && summon.getSkill() != 12120013
                  && summon.getSkill() != 131001025
                  && sse == null) {
                  chr.dropMessage(5, "공격 도중 오류가 발생했습니다.");
               } else {
                  slea.skip(4);
                  int reactionSkillID = slea.readInt();
                  int attackSkillID = slea.readInt();

                  try {
                     boolean isBeta = false;
                     byte a = slea.readByte();
                     byte b = slea.readByte();
                     slea.readByte();
                     if (attackSkillID == 152110001) {
                        slea.skip(4);
                     }

                     List<Point> posList = new ArrayList<>();
                     if (reactionSkillID == 154121041) {
                        int count = slea.readInt();

                        for (int next = 0; next < count; next++) {
                           posList.add(new Point(slea.readInt(), slea.readInt()));
                        }
                     } else {
                        int count = slea.readInt();
                        if (attackSkillID != 152110001) {
                           for (int i = 0; i < count; i++) {
                              slea.readInt();
                              slea.readInt();
                           }
                        }
                     }

                     byte moveAction = slea.readByte();
                     byte tbyte = slea.readByte();
                     byte numAttacked = (byte)(tbyte >>> 4 & 15);
                     byte hitCount = (byte)(tbyte & 15);
                     slea.skip(1);
                     if (summon.getSkill() == 35111002 && numAttacked > 0) {
                        int triangleObjectID1 = slea.readInt();
                        int triangleObjectID2 = slea.readInt();
                        int triangleObjectID3 = slea.readInt();
                        slea.readPos();
                        slea.readPos();
                        slea.readByte();
                        slea.readInt();
                     } else {
                        slea.readPos();
                        slea.readPos();
                        slea.readByte();
                        if (summon.getSkill() == 400051046 && b == 0) {
                           slea.readPos();
                        }

                        slea.readInt();
                     }

                     if (reactionSkillID == 5221022 && attackSkillID == 0) {
                        slea.skip(4);
                     }

                     slea.readShort();
                     slea.readInt();
                     slea.readInt();
                     if (reactionSkillID == 5221022 && attackSkillID == 5221028) {
                        slea.skip(4);
                        slea.skip(4);
                     }

                     if (reactionSkillID == 5221027) {
                        slea.skip(4);
                     }

                     List<Pair<Integer, List<Long>>> allDamage = new ArrayList<>();

                     for (int i = 0; i < numAttacked; i++) {
                        int mobObjectID = slea.readInt();
                        MapleMonster mob = map.getMonsterByOid(mobObjectID);
                        int mobId = slea.readInt();
                        byte action = slea.readByte();
                        slea.readByte();
                        slea.readByte();
                        slea.readByte();
                        slea.readByte();
                        slea.readInt();
                        slea.readByte();
                        slea.readPos();
                        slea.readPos();
                        slea.readInt();
                        slea.readShort();
                        slea.readInt();
                        slea.readInt();
                        slea.readByte();
                        List<Long> damages = new ArrayList<>();

                        for (int j = 0; j < hitCount; j++) {
                           long damge = slea.readLong();
                           damages.add(damge);
                        }

                        slea.skip(4);
                        long posSave = slea.getPosition();

                        try {
                           Skeleton.attackSkeletonImage(slea, new AttackInfo());
                        } catch (Exception var37) {
                           slea.seek(posSave);
                           FileoutputUtil.outputFileErrorReason("Log_DamageParseError.rtf", "Summon 데미지 Skeleton 파싱 오류 " + slea.toString(true), var37);
                           return;
                        }

                        allDamage.add(new Pair<>(mobObjectID, damages));
                     }

                     map.broadcastMessage(
                        chr,
                        CField.SummonPacket.summonAttack(
                           summon.getOwnerId(), summon.getObjectId(), summon.getSkill(), moveAction, tbyte, allDamage, chr.getLevel(), false
                        ),
                        summon.getTruePosition()
                     );
                     Skill summonSkill = SkillFactory.getSkill(summon.getSkill());
                     SecondaryStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
                     if (summonEffect == null) {
                        return;
                     }

                     double grimReaper = 0.0;
                     Map<MapleMonster, Integer> mobKillCount = new HashMap<>();
                     if (GameConstants.isMichael(chr.getJob()) && chr.hasBuffBySkillID(51121059)) {
                        List<MapleMonster> mobList = new ArrayList<>();
                        allDamage.forEach(p -> {
                           MapleMonster mob = chr.getMap().getMonsterByOid(p.left);
                           mobList.add(mob);
                        });
                        chr.invokeJobMethod("onOffensiveDefense", mobList);
                     }

                     if (reactionSkillID == 154121041) {
                        SecondaryStatEffect level = chr.getSkillLevelData(154121041);
                        if (level != null && level.makeChanceResult()) {
                           for (Point pos : posList) {
                              chr.invokeJobMethod("tryCreateSummonChakri", pos, 154121041, true);
                           }
                        }
                     }

                     for (Pair<Integer, List<Long>> attackEntry : allDamage) {
                        long toDamage = 0L;

                        for (long dmg : attackEntry.right) {
                           toDamage += dmg;
                        }

                        MapleMonster mob = map.getMonsterByOid(attackEntry.left);
                        if (mob != null) {
                           if (toDamage > 0L && summonEffect.getMonsterStati().size() > 0 && summonEffect.makeChanceResult()) {
                              for (Entry<MobTemporaryStatFlag, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                                 mob.applyStatus(
                                    chr,
                                    new MobTemporaryStatEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false),
                                    summonEffect.isPoison(),
                                    4000L,
                                    true,
                                    summonEffect
                                 );
                              }
                           }

                           if (attackSkillID == 1311014) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(attackSkillID).getEffect(chr.getTotalSkillLevel(attackSkillID));
                              if (effect != null) {
                                 chr.giveCoolDowns(attackSkillID, System.currentTimeMillis(), effect.getCooldown(chr));
                                 chr.send(CField.skillCooldown(attackSkillID, effect.getCooldown(chr)));
                              }
                           }

                           if (reactionSkillID == 12120013) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(400021042).getEffect(chr.getTotalSkillLevel(400021042));
                              if (effect != null) {
                                 chr.giveCoolDowns(400021042, System.currentTimeMillis(), effect.getCooldown(chr));
                                 chr.send(CField.skillCooldown(400021042, effect.getCooldown(chr)));
                              }
                           }

                           if (attackSkillID == 152110002 || attackSkillID == 152100002) {
                              mob.tryApplyCurseMark(chr, attackSkillID);
                           }

                           if (GameConstants.isWildHunter(chr.getJob())) {
                              boolean bActive = (Boolean)chr.getJobField("jaguarActive");
                              if (!bActive) {
                                 chr.send((byte[])chr.invokeJobMethod("jaguarActive", true));
                                 chr.setJobField("jaguarActive", true);
                              }

                              SecondaryStatEffect eff = SkillFactory.getSkill(33000036).getEffect(chr.getLevel());
                              if (eff != null) {
                                 chr.invokeJobMethod("activeAnotherBite", eff, mob, attackSkillID);
                              }
                           }

                           if (chr.getBuffedValue(SecondaryStatFlag.WizardIgnite) != null
                              && chr.getJob() >= 210
                              && chr.getJob() <= 212
                              && summon.getSkill() == 2121005) {
                              SecondaryStatEffect eff = SkillFactory.getSkill(2100010).getEffect(chr.getSkillLevel(2101010));
                              if (eff != null && eff.makeChanceResult()) {
                                 Point pos = mob.getPosition();
                                 if (mob.getId() == 8880153) {
                                    pos.y -= 450;
                                    pos.x += 150;
                                 }

                                 Rectangle rect = eff.calculateBoundingBox(pos, false);
                                 pos = chr.getMap().calcDropPos(new Point(rect.x, rect.y - 23), pos);
                                 chr.getMap().spawnMist(new AffectedArea(rect, chr, eff, pos, System.currentTimeMillis() + eff.getDuration()));
                                 chr.getMap().spawnMist(new AffectedArea(rect, chr, eff, pos, System.currentTimeMillis() + eff.getDuration()));
                              }
                           }

                           if (GameConstants.isColdSkill(summon.getSkill()) && chr.getJob() >= 220 && chr.getJob() <= 222) {
                              MobTemporaryStatEffect eff = mob.getBuff(MobTemporaryStatFlag.SPEED);
                              int stack = 0;
                              if (eff != null) {
                                 stack = eff.getValue();
                              }

                              int inc = 1;
                              if (summon.getSkill() == 400021067) {
                                 inc = 3;
                              }

                              stack = Math.min(5, stack + inc);
                              eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -15 * stack, summon.getSkill(), null, false);
                              eff.setDuration(5000);
                              eff.setCancelTask(5000L);
                              eff.setValue(stack);
                              mob.applyStatus(eff);
                           }

                           if (chr.getJob() >= 3211 && chr.getJob() <= 3212 && chr.getDarkLightning(mob.getObjectId()) != null) {
                              chr.send(CField.userBonusAttackRequest(32110020, false, Collections.singletonList(new Pair<>(mob.getObjectId(), 1)), 0));
                              chr.removeDarkLightning(mob.getObjectId());
                           }

                           if (summon.getSkill() == 14121003) {
                              SecondaryStatEffect effect = chr.getSkillLevelData(14121003);
                              if (effect != null) {
                                 int[] skills = new int[]{14100027, 14110029, 14120008};
                                 SecondaryStatEffect batSummon = null;

                                 for (int skillID_ : skills) {
                                    if (chr.getTotalSkillLevel(skillID_) > 0) {
                                       SecondaryStatEffect e = SkillFactory.getSkill(skillID_).getEffect(chr.getTotalSkillLevel(skillID_));
                                       if (e != null) {
                                          batSummon = e;
                                       }
                                    }
                                 }

                                 if (!chr.getDarknessOmenTargets().contains(mob.getObjectId())) {
                                    chr.getDarknessOmenTargets().add(mob.getObjectId());
                                 }

                                 if (chr.getDarknessOmenTargets().size() >= effect.getX()
                                    && chr.getDarknessOmenTargets().size() % effect.getX() == 0
                                    && chr.getDarknessOmenBatCount() < effect.getZ()) {
                                    chr.summonShadowBat(batSummon, chr.getPosition());
                                    chr.setDarknessOmenBatCount(chr.getDarknessOmenBatCount() + 1);
                                 }
                              }
                           }

                           if (summon.getSkill() == 3311009) {
                              SecondaryStatEffect effx = SkillFactory.getSkill(3320000).getEffect(1);
                              SecondaryStatEffect eff2 = SkillFactory.getSkill(3300000).getEffect(1);
                              if (effx != null && eff2 != null) {
                                 int max = eff2.getU();
                                 int delta = effx.getS();
                                 chr.setRelicCharge(Math.min(max, chr.getRelicCharge() + delta));
                                 eff2.applyTo(chr);
                              }
                           }

                           if (summon.getSkill() == 2321003) {
                              SecondaryStatEffect effx = SkillFactory.getSkill(2321003).getEffect(chr.getTotalSkillLevel(2321003));
                              if (effx != null) {
                                 mob.setBahamutLightElemAddDamC(chr.getId());
                                 mob.setBahamutLightElemAddDamP(chr.getParty() != null ? chr.getParty().getId() : 0);
                                 mob.applyStatus(
                                    chr,
                                    new MobTemporaryStatEffect(MobTemporaryStatFlag.BAHAMUT_LIGHT_ELEM_ADD_DAM, effx.getX(), 2321003, null, false),
                                    false,
                                    effx.getSubTime(),
                                    false,
                                    effx
                                 );
                              }
                           }

                           if (summon.getSkill() == 400021033) {
                              SecondaryStatEffect effx = SkillFactory.getSkill(400021033).getEffect(chr.getTotalSkillLevel(400021033));
                              if (effx != null) {
                                 mob.setBahamutLightElemAddDamC(chr.getId());
                                 mob.setBahamutLightElemAddDamP(chr.getParty() != null ? chr.getParty().getId() : 0);
                                 mob.applyStatus(
                                    chr,
                                    new MobTemporaryStatEffect(MobTemporaryStatFlag.BAHAMUT_LIGHT_ELEM_ADD_DAM, effx.getY(), 400021033, null, false),
                                    false,
                                    effx.getSubTime(),
                                    false,
                                    effx
                                 );
                              }
                           }

                           if (summon.getSkill() == 400011065) {
                              SecondaryStatEffect e = SkillFactory.getSkill(400011055).getEffect(summon.getSkillLevel());
                              chr.send(CField.skillCooldown(400011065, e.getZ() * 1000));
                              chr.addCooldown(400011065, System.currentTimeMillis(), e.getZ() * 1000);
                           }

                           if (summon.getSkill() == 400051023) {
                              SecondaryStatEffect e = SkillFactory.getSkill(400051022).getEffect(summon.getSkillLevel());
                              MobTemporaryStatEffect effx = mob.getBuff(MobTemporaryStatFlag.GHOST_DISPOSITION);
                              int stackx = 0;
                              if (effx != null) {
                                 stackx = effx.getX();
                              }

                              SecondaryStatEffect e2 = SkillFactory.getSkill(400051023).getEffect(summon.getSkillLevel());
                              int newStack = Math.min(stackx + 1, e2.getX());
                              if (e2 != null) {
                                 mob.applyStatus(
                                    chr,
                                    new MobTemporaryStatEffect(MobTemporaryStatFlag.GHOST_DISPOSITION, newStack, 400051023, null, false),
                                    false,
                                    e2.getSubTime(),
                                    false,
                                    e2
                                 );
                              }
                           }

                           if (summon.getSkill() == 164121008) {
                              summon.setAbsorbingVortexCount(summon.getAbsorbingVortexCount() + 1);
                              if (summon.getAbsorbingVortexCount() >= 4) {
                                 summon.setAbsorbingVortexCount(0);
                                 if (summon.getAbsorbingVortexStack() < 9) {
                                    summon.setAbsorbingVortexStack(summon.getAbsorbingVortexStack() + 1);
                                 }
                              }
                           }

                           if (mob.getId() == 8880305 && mob.getMap() instanceof Field_WillBattle) {
                              Field_WillBattle f = (Field_WillBattle)mob.getMap();
                              boolean soloPlay = f.getCharactersThreadsafe().size() == 1;
                              Set<ObstacleAtom> atoms = new HashSet<>();
                              if (Randomizer.isSuccess(30)) {
                                 for (int i = 0; i < Randomizer.rand(1, 3); i++) {
                                    int type = 63 + Randomizer.nextInt(2);
                                    int x = Randomizer.nextInt(1200) - 600;
                                    int y = summon.getTruePosition().y > 0 ? -2020 : 159;
                                    if (soloPlay) {
                                       y = summon.getTruePosition().y > 0 ? 159 : -2020;
                                    }

                                    ObstacleAtom atom = new ObstacleAtom(type, new Point(x, y - 599), new Point(x, y), 1013);
                                    atom.setHitBoxRange(40);
                                    atom.setKey(Randomizer.nextInt());
                                    atom.setTrueDamR(type == 64 ? 0 : 60);
                                    atom.setMobDamR(0);
                                    atom.setHeight(6400);
                                    atom.setvPerSec(138);
                                    atom.setMaxP(3);
                                    atom.setAngle(0);
                                    atom.setLength(599);
                                    atoms.add(atom);
                                    mob.getMap().addObstacleAtom(atom);
                                 }
                              }

                              if (!atoms.isEmpty()) {
                                 map.broadcastMessage(CField.createObstacle(ObstacleAtomCreateType.NORMAL, null, null, atoms));
                              }
                           }

                           mob.damage(chr, toDamage, true, summonSkill.getId());
                           chr.checkMonsterAggro(mob);
                           if (!mob.isAlive()) {
                              mobKillCount.put(mob, mobKillCount.getOrDefault(mob, 0) + 1);
                              if (summon.getSkill() == 400021069 && grimReaper + 200.0 < 3000.0 && !chr.hasBuffBySkillID(32121056)) {
                                 ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                                 info.initGrimReaper(mob.getPosition(), summon.getPosition());
                                 ForceAtom forceAtom = new ForceAtom(
                                    info, 400021069, chr.getId(), true, false, mob.getObjectId(), ForceAtom.AtomType.ENERGY_BURST, Collections.EMPTY_LIST, 1
                                 );
                                 chr.getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                                 grimReaper += 200.0;
                              }

                              chr.getClient().getSession().writeAndFlush(MobPacket.killMonster(mob.getObjectId(), 1));
                           }

                           if (mob.getStats().isBoss() && summon.getSkill() == 400021069 && grimReaper + 2000.0 < 3000.0 && !chr.hasBuffBySkillID(32121056)) {
                              ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                              info.initGrimReaperBoss(mob.getPosition(), summon.getPosition());
                              ForceAtom forceAtom = new ForceAtom(
                                 info, 400021069, chr.getId(), true, false, mob.getObjectId(), ForceAtom.AtomType.ENERGY_BURST, Collections.EMPTY_LIST, 1
                              );
                              chr.getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                              grimReaper += 2000.0;
                           }
                        }
                     }

                     Map<Integer, Integer> mobCount = new HashMap<>();

                     for (Entry<MapleMonster, Integer> entry : mobKillCount.entrySet()) {
                        mobCount.put(entry.getKey().getId(), mobCount.getOrDefault(entry.getKey().getId(), 0) + 1);
                        boolean delta = Math.abs(chr.getLevel() - entry.getKey().getStats().getLevel()) < 20 || chr.getLevel() >= 275;
                        if (delta) {
                           mobCount.put(9101025, mobCount.getOrDefault(9101025, 0) + 1);
                           if (chr.getGuild() != null && !entry.getKey().getStats().isBoss() && chr.getOneInfoQuestInteger(100813, "huntPoint") < 25000) {
                              chr.addGuildBossPointByBossID(-1);
                           }

                           if (entry.getKey().getEliteMobType().getType() > 0) {
                              mobCount.put(9101223, mobCount.getOrDefault(9101223, 0) + 1);
                              if (chr.isQuestStarted(501534)) {
                                 if (chr.getOneInfoQuestInteger(501534, "value") < 1) {
                                    chr.updateOneInfo(501534, "value", "1");
                                 }

                                 if (chr.getOneInfoQuestInteger(501524, "state") < 2) {
                                    chr.updateOneInfo(501524, "state", "2");
                                 }
                              }
                           }
                        }
                     }

                     for (Entry<Integer, Integer> entryx : mobCount.entrySet()) {
                        chr.mobKilled(entryx.getKey(), entryx.getValue(), attackSkillID);
                        if (chr.isQuestStarted(QuestExConstants.NeoEventNormalMob.getQuestID())) {
                           int m0 = chr.getOneInfoQuestInteger(QuestExConstants.NeoEventNormalMob.getQuestID(), "m0");
                           if (m0 < 10000) {
                              m0 = Math.min(10000, m0 + entryx.getValue());
                              chr.updateOneInfo(QuestExConstants.NeoEventNormalMob.getQuestID(), "m0", String.valueOf(m0));
                              MapleQuestStatus status = chr.getQuest(MapleQuest.getInstance(QuestExConstants.NeoEventNormalMob.getQuestID()));
                              if (status != null) {
                                 String keySet = "000";
                                 if (m0 < 100) {
                                    keySet = "0" + m0;
                                 } else {
                                    keySet = String.valueOf(m0);
                                 }

                                 keySet = StringUtil.getLeftPaddedStr(String.valueOf(m0), '0', 3);
                                 status.setCustomData(keySet);
                                 chr.updateQuest(status);
                              }

                              if (m0 >= 10000) {
                                 chr.updateOneInfo(QuestExConstants.NeoEventAdventureLog.getQuestID(), "state", "2");
                              }
                           }
                        } else if (chr.isQuestStarted(QuestExConstants.NeoEventEliteMob.getQuestID())) {
                           MapleQuestStatus statusx = chr.getQuest(MapleQuest.getInstance(QuestExConstants.NeoEventEliteMob.getQuestID()));
                           if (statusx != null && entryx.getKey() == 9101223) {
                              String keySet = "000";
                              int countx = 0;
                              if (!statusx.getCustomData().isEmpty()) {
                                 keySet = statusx.getCustomData();
                                 countx = Integer.parseInt(keySet);
                              }

                              if (countx < 20) {
                                 countx = Math.min(20, countx + entryx.getValue());
                                 keySet = StringUtil.getLeftPaddedStr(String.valueOf(countx), '0', 3);
                                 statusx.setCustomData(keySet);
                                 chr.updateQuest(statusx);
                                 chr.updateOneInfo(QuestExConstants.NeoEventEliteMob.getQuestID(), "m1", String.valueOf(countx));
                                 if (countx >= 20) {
                                    chr.updateOneInfo(QuestExConstants.NeoEventAdventureLog.getQuestID(), "state", "2");
                                 }
                              }
                           }
                        }

                        if (entryx.getKey() != 9101025 && entryx.getKey() != 9101223) {
                           AchievementFactory.checkMobKillOptimum(MapleLifeFactory.getMonster(entryx.getKey()), chr, entryx.getValue());
                        }
                     }

                     mobKillCount.clear();
                     if (attackSkillID == 400021062) {
                        chr.send(CField.skillCooldown(400021062, 1000));
                        chr.addCooldown(400021062, System.currentTimeMillis(), 1000L);
                        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.summonSetEnergy(c.getPlayer(), summon, 3), true);
                     }

                     if (summon.getSkill() == 32001014
                        || summon.getSkill() == 32100010
                        || summon.getSkill() == 32110017
                        || summon.getSkill() == 32120019
                        || summon.getSkill() == 32141000) {
                        chr.resetBMDeath();
                     }

                     if (summon.getSkill() == 400021069) {
                        int var59 = allDamage.size();
                     }

                     if ((attackSkillID == 152110002 || attackSkillID == 152100002 || attackSkillID == 152100001 || attackSkillID == 152110001)
                        && reactionSkillID != 400021068
                        && reactionSkillID != 500061012) {
                        SecondaryStatEffect effect = SkillFactory.getSkill(attackSkillID).getEffect(chr.getTotalSkillLevel(attackSkillID));
                        if (effect != null) {
                           chr.giveCoolDowns(attackSkillID, System.currentTimeMillis(), effect.getCooldown(chr));
                           chr.send(CField.skillCooldown(attackSkillID, effect.getCooldown(chr)));
                           chr.getMap().broadcastMessage(chr, CField.summonEnergyUpdate(chr.getId(), summon, 3), true);
                           chr.getMap().broadcastMessage(chr, CField.summonSetEnergy(chr, summon, 3), true);
                        }
                     }

                     if (reactionSkillID == 400021068 || reactionSkillID == 500061012) {
                        SecondaryStatEffect effect = SkillFactory.getSkill(attackSkillID).getEffect(chr.getTotalSkillLevel(attackSkillID));
                        chr.send(CField.crystalReactionCooltime(chr.getId(), summon.getObjectId(), attackSkillID, effect.getCooldown(chr)));
                     }

                     if (attackSkillID == 152101006) {
                        summon.setEnableEnergySkill(1, 0);
                        chr.getMap().broadcastMessage(chr, CField.summonCrystalToggleSkill(chr, summon, 2), true);
                        chr.getMap().broadcastMessage(chr, CField.summonSetEnergy(chr, summon, 3), true);
                     }

                     if (summon.getSkill() == 131002015 && c.getPlayer().skillisCooling(131001012)) {
                        c.getPlayer().changeCooldown(131001012, -1000L);
                     }

                     if (!summon.isMultiAttack() && !GameConstants.isSoulSummonSkill(summon.getSkill())) {
                        chr.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                        chr.getMap().removeMapObject(summon);
                        chr.removeVisibleMapObject(summon);
                        chr.removeSummon(summon);
                        if (summon.getSkill() != 35121011 && summon.getSkill() != 400051023 && summon.getSkill() != 400041038 && summon.getSkill() != 400001071
                           )
                         {
                           chr.temporaryStatResetBySkillID(summon.getSkill());
                        }
                     }
                  } catch (Exception var38) {
                     slea.seek(0L);
                     FileoutputUtil.outputFileErrorReason(
                        "Log_DamageParseError.rtf", "SummonAttack 파싱 오류 : " + summon.getSkill() + " // Packet : " + slea.toString(), var38
                     );
                  }
               }
            }
         }
      }
   }

   public static final void RemoveSummon(PacketDecoder slea, MapleClient c) {
      MapleMapObject obj = c.getPlayer().getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
      if (obj != null && obj instanceof Summoned) {
         Summoned summon = (Summoned)obj;
         if (summon.getOwnerId() == c.getPlayer().getId() && summon.getSkillLevel() > 0) {
            if (summon.getSkill() != 151111001 && summon.getSkill() != 35111002 && summon.getSkill() != 35121010) {
               c.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
               c.getPlayer().getMap().removeMapObject(summon);
               c.getPlayer().removeVisibleMapObject(summon);
               c.getPlayer().removeSummon(summon);
               if (summon.getSkill() != 35121011 && summon.getSkill() != 400001061) {
                  c.getPlayer().temporaryStatResetBySkillID(summon.getSkill());
               }
            }
         }
      }
   }

   public static final void OnSkill(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null) {
         MapleMapObject obj = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
         if (obj != null && obj instanceof Summoned) {
            Summoned sum = (Summoned)obj;
            if (sum != null && sum.getSkillLevel() > 0 && chr.isAlive()) {
               switch (sum.getSkill()) {
                  case 1301013:
                     Skill skill = SkillFactory.getSkill(slea.readInt());
                     int skillLevel = chr.getTotalSkillLevel(skill);
                     if (skillLevel <= 0 || skill == null) {
                        return;
                     }

                     SecondaryStatEffect healEffect = skill.getEffect(skillLevel);
                     if (skill.getId() == 1310016) {
                        Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
                        flags.put(SecondaryStatFlag.indieCR, Integer.valueOf(healEffect.getIndieCr()));
                        flags.put(SecondaryStatFlag.EnhancedPAD, healEffect.getEnhancedWatk());
                        flags.put(SecondaryStatFlag.EnhancedDEF, healEffect.getEnhancedWdef());
                        chr.temporaryStatSet(skill.getId(), skillLevel, healEffect.getDuration(), flags);
                        if (chr.getParty() != null) {
                           Map<SecondaryStatFlag, Integer> partyFlags = new HashMap<>();
                           flags.forEach((k, v) -> partyFlags.put(k, v / 2));

                           for (MapleCharacter pChr : chr.getPartyMembers()) {
                              if (pChr.getId() != chr.getId()) {
                                 pChr.temporaryStatSet(skill.getId(), skillLevel, healEffect.getDuration(), partyFlags);
                              }
                           }
                        }
                     } else if (skill.getId() == 1301013) {
                        if (!chr.canSummon(healEffect.getX() * 1000)) {
                           return;
                        }

                        chr.addHP(healEffect.getHp(), false);
                     }

                     PostSkillEffect e = new PostSkillEffect(chr.getId(), skill.getId(), skillLevel, null);
                     chr.getClient().getSession().writeAndFlush(e.encodeForLocal());
                     chr.getMap().broadcastMessage(chr, e.encodeForRemote(), false);
                     break;
                  case 5201012:
                  case 5210015:
                     slea.skip(9);
                     Point pos = new Point(slea.readInt(), slea.readInt());
                     slea.skip(1);
                     int size = slea.readInt();
                     List<Integer> mobList = new ArrayList<>();

                     for (int i = 0; i < size; i++) {
                        mobList.add(slea.readInt());
                     }

                     List<SecondAtom.Atom> atoms = new ArrayList<>();

                     for (int i = 0; i < 2; i++) {
                        SecondAtom.Atom a = new SecondAtom.Atom(
                           chr.getMap(), chr.getId(), 5201017, SecondAtom.SN.getAndAdd(1), SecondAtom.SecondAtomType.AssembleCrew, 0, null, pos
                        );
                        a.setPlayerID(chr.getId());
                        SecondAtomData.atom atom = SkillFactory.getSkill(5201017).getSecondAtomData().getAtoms().get(i);
                        a.setExpire(atom.getExpire());
                        a.setCreateDelay(atom.getCreateDelay());
                        a.setEnableDelay(atom.getEnableDelay());
                        a.setAttackableCount(1);
                        a.setRange(10);
                        a.setExpire(atom.getExpire());
                        a.setSkillID(5201017);
                        if (!mobList.isEmpty()) {
                           a.setTargetObjectID(mobList.get(Randomizer.rand(0, mobList.size() - 1)));
                        }

                        chr.addSecondAtom(a);
                        atoms.add(a);
                     }

                     if (atoms.size() > 0) {
                        SecondAtom secondAtom = new SecondAtom(chr.getId(), 5201017, atoms);
                        chr.getMap().createSecondAtom(secondAtom);
                     }
                     break;
                  case 25121133:
                     int skillID = 25121209;
                     SecondaryStatEffect effect = chr.getSkillLevelData(skillID);
                     if (effect != null && chr.getBuffedValue(SecondaryStatFlag.SpiritGuard) == null) {
                        chr.temporaryStatSet(skillID, effect.getDuration(effect.getDuration(), chr), SecondaryStatFlag.SpiritGuard, effect.getX());
                     }
                     break;
                  case 35111008:
                  case 35120002:
                     int skillIDxx = slea.readInt();
                     slea.skip(1);
                     int tickTimex = slea.readInt();
                     SecondaryStatEffect effectxx = SkillFactory.getSkill(skillIDxx).getEffect(sum.getSkillLevel());
                     if (effectxx != null) {
                        int hp = (int)(chr.getStat().getCurrentMaxHp(chr) * 0.01 * effectxx.getHp());
                        chr.addHP(hp, false);
                     }
                     break;
                  case 36121014:
                     int skillIDx = slea.readInt();
                     slea.skip(1);
                     int tickTime = slea.readInt();
                     SecondaryStatEffect effectx = SkillFactory.getSkill(skillIDx).getEffect(sum.getSkillLevel());
                     if (effectx != null) {
                        Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                        statups.put(SecondaryStatFlag.EVAR, effectx.getEvaR());
                        statups.put(SecondaryStatFlag.indieMHPR, effectx.getIndieMhpR());
                        chr.temporaryStatSet(skillIDx, effectx.getLevel(), effectx.getSubTime(), statups);
                     }
                     break;
                  case 152101000:
                     int skillId = slea.readInt();
                     if (chr.getTotalSkillLevel(skillId) <= 0) {
                        return;
                     }

                     Summoned summon = chr.getSummonByMovementType(SummonMoveAbility.SHADOW_SERVANT_EXTEND);
                     if (summon != null) {
                        chr.getMap().broadcastMessage(chr, CField.summonCrystalToggleSkill(chr, summon, 1), true);
                        SecondaryStatEffect effectxxxx = SkillFactory.getSkill(skillId).getEffect(skillId);
                        if (effectxxxx != null) {
                           effectxxxx.applyTo(chr);
                        }

                        summon.setEnableEnergySkill(2, 0);
                        chr.getMap().broadcastMessage(CField.summonCrystalToggleSkill(chr, summon, 2));
                        chr.getMap().broadcastMessage(chr, CField.summonSetEnergy(chr, summon, 3), true);
                        PostSkillEffect e_ = new PostSkillEffect(chr.getId(), 152101000, summon.getSkillLevel(), null);
                        chr.send(e_.encodeForLocal());
                        chr.getMap().broadcastMessage(chr, e_.encodeForRemote(), false);
                     }
                  case 35121009:
                     if (!chr.canSummon(3500)) {
                        return;
                     }

                     int skillIdxx = slea.readInt();
                     if (sum.getSkill() != skillIdxx) {
                        return;
                     }

                     slea.skip(1);
                     slea.readInt();

                     for (int ix = 0; ix < 3; ix++) {
                        SecondaryStatEffect effectxxxxx = SkillFactory.getSkill(35121011).getEffect(sum.getSkillLevel());
                        long removeTime = System.currentTimeMillis() + effectxxxxx.getDuration();
                        Summoned tosummon = new Summoned(
                           chr, effectxxxxx, new Point(sum.getTruePosition().x, sum.getTruePosition().y - 5), SummonMoveAbility.WALK_STATIONARY, removeTime
                        );
                        chr.getMap().spawnSummon(tosummon);
                        chr.addSummon(tosummon);
                     }
                     break;
                  case 400001013:
                     SecondaryStatEffect effectxxx = SkillFactory.getSkill(400001016).getEffect(sum.getSkillLevel());
                     if (effectxxx != null) {
                        chr.setDemonDamAbsorbShieldX(effectxxx.getX());
                        chr.temporaryStatSet(
                           400001016, effectxxx.getDuration(effectxxx.getDuration(), chr), SecondaryStatFlag.DemonDamAbsorbShield, effectxxx.getY()
                        );
                     }
                     break;
                  case 400041038:
                     SecondaryStatEffect effectxxxx = SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel());
                     if (effectxxxx != null) {
                        List<MapleMonster> mobs = chr.getMap()
                           .getMobsInRect(sum.getPosition(), effectxxxx.getLt().x, effectxxxx.getLt().y, effectxxxx.getRb().x, effectxxxx.getRb().y);
                        if (mobs.size() > 0) {
                           List<Integer> targets = new ArrayList<>();
                           List<Point> startPos = new ArrayList<>();

                           for (MapleMonster mob : mobs) {
                              targets.add(mob.getObjectId());
                              startPos.add(sum.getPosition());
                              if (targets.size() >= effectxxxx.getBulletCount()) {
                                 break;
                              }
                           }

                           ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                           pos = sum.getPosition();
                           pos.y -= 250;
                           int bulletItemID = chr.getBulletItemID();
                           Integer normalThrowingStar = (Integer)chr.getJobField("normalThrowingStar");
                           int cashThrowingStar = chr.getOneInfoQuestInteger(27038, "bullet");
                           if (normalThrowingStar != null && normalThrowingStar > 0) {
                              bulletItemID = normalThrowingStar;
                           }

                           if (cashThrowingStar > 0) {
                              bulletItemID = cashThrowingStar;
                           }

                           if (bulletItemID == 0) {
                              bulletItemID = 2070006;
                           }

                           info.initBullet(pos, bulletItemID, sum.getObjectId());
                           ForceAtom forceAtom = new ForceAtom(
                              info,
                              400041038,
                              chr.getId(),
                              false,
                              true,
                              chr.getId(),
                              ForceAtom.AtomType.BULLET,
                              targets,
                              targets.size() * effectxxxx.getBulletCount()
                           );
                           chr.getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                        }
                     }
                     break;
                  case 400041044:
                     SecondaryStatEffect effectxxxxx = SkillFactory.getSkill(400041047).getEffect(chr.getTotalSkillLevel(400041044));
                     if (effectxxxxx != null) {
                        SecondaryStatEffect e2 = chr.getSkillLevelData(400041044);
                        if (e2 != null) {
                           for (MapleCharacter player : chr.getMap().getPlayerInRect(sum.getPosition(), e2.getLt().x, e2.getLt().y, e2.getRb().x, e2.getRb().y)) {
                              if (player != null
                                 && player.isAlive()
                                 && (
                                    player.getId() == sum.getOwnerId()
                                       || chr.getParty() != null && player.getParty() != null && chr.getParty().getId() == player.getParty().getId()
                                 )) {
                                 player.temporaryStatSet(400041047, effectxxxxx.getDuration(), SecondaryStatFlag.indieDamR, effectxxxxx.getIndieDamR());
                                 PostSkillEffect e_ = new PostSkillEffect(player.getId(), 400041047, effectxxxxx.getLevel(), null);
                                 player.send(e_.encodeForLocal());
                                 player.getMap().broadcastMessage(player, e_.encodeForRemote(), false);
                              }
                           }
                        }
                     }
                     break;
                  case 400051022:
                     int skillIdx = slea.readInt();
                     if (sum.getSkill() != skillIdx) {
                        return;
                     }

                     effectxxxxx = SkillFactory.getSkill(400051023).getEffect(sum.getSkillLevel());
                     SecondaryStatEffect effect2 = SkillFactory.getSkill(400051022).getEffect(sum.getSkillLevel());
                     if (effectxxxxx != null) {
                        for (int i = 0; i < effect2.getX(); i++) {
                           if (chr.getSummonedSize(400051023) < effectxxxxx.getY()) {
                              long removeTime = System.currentTimeMillis() + effectxxxxx.getDuration();
                              Summoned tosummon = new Summoned(
                                 chr,
                                 effectxxxxx,
                                 new Point(sum.getTruePosition().x + i * 30, sum.getTruePosition().y - 5),
                                 SummonMoveAbility.WALK_CLONE2,
                                 removeTime
                              );
                              chr.getMap().spawnSummon(tosummon);
                              chr.addSummon(tosummon);
                           }
                        }
                     }
               }

               if (GameConstants.isAngel(sum.getSkill())) {
                  if (sum.getSkill() % 10000 == 1087) {
                     MapleItemInformationProvider.getInstance().getItemEffect(2022747).applyTo(chr);
                  } else if (sum.getSkill() % 10000 == 1085) {
                     MapleItemInformationProvider.getInstance().getItemEffect(2022746).applyTo(chr);
                  } else if (sum.getSkill() % 10000 == 1090) {
                     MapleItemInformationProvider.getInstance().getItemEffect(2022764).applyTo(chr);
                  } else if (sum.getSkill() % 10000 == 1179) {
                     MapleItemInformationProvider.getInstance().getItemEffect(2022823).applyTo(chr);
                  } else {
                     MapleItemInformationProvider.getInstance().getItemEffect(2022746).applyTo(chr);
                  }

                  int skillid = chr.getBuffedEffect(SecondaryStatFlag.RepeatEffect).getSourceId();
                  chr.getMap().broadcastMessage(CWvsContext.BuffPacket.showAngelicBlessBuffEffect(chr.getId(), skillid));
               }
            }
         }
      }
   }

   public static final void SummonPVP(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && !chr.isHidden() && chr.isAlive() && chr.getMap() != null && chr.inPVP() && chr.getEventInstance().getProperty("started").equals("1")) {
         Field map = chr.getMap();
         MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
         if (obj != null && obj instanceof Summoned) {
            int tick = -1;
            if (slea.available() == 27L) {
               slea.skip(23);
               tick = slea.readInt();
            }

            Summoned summon = (Summoned)obj;
            if (summon.getOwnerId() == chr.getId() && summon.getSkillLevel() > 0) {
               Skill skil = SkillFactory.getSkill(summon.getSkill());
               SecondaryStatEffect effect = skil.getEffect(summon.getSkillLevel());
               int lvl = Integer.parseInt(chr.getEventInstance().getProperty("lvl"));
               int type = Integer.parseInt(chr.getEventInstance().getProperty("type"));
               int ourScore = Integer.parseInt(chr.getEventInstance().getProperty(String.valueOf(chr.getId())));
               int addedScore = 0;
               boolean magic = skil.isMagic();
               boolean killed = false;
               boolean didAttack = false;
               double maxdamage = lvl == 3 ? chr.getStat().getCurrentMaxBasePVPDamageL() : chr.getStat().getCurrentMaxBasePVPDamage();
               maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(summon.getSkill())) / 100.0;
               int mobCount = 1;
               int attackCount = 1;
               int ignoreDEF = chr.getStat().ignoreTargetDEF;
               SummonedSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
               if (summon.getSkill() / 1000000 != 35 && summon.getSkill() != 33101008 && sse == null) {
                  chr.dropMessage(5, "Error in processing attack.");
               } else {
                  Point lt;
                  Point rb;
                  if (sse != null) {
                     if (sse.delay > 0) {
                        if (tick != -1) {
                           summon.CheckSummonAttackFrequency(chr, tick);
                           slea.readInt();
                        } else {
                           summon.CheckPVPSummonAttackFrequency(chr);
                        }

                        chr.getCheatTracker().checkSummonAttack();
                     }

                     mobCount = sse.mobCount;
                     attackCount = sse.attackCount;
                     lt = sse.lt;
                     rb = sse.rb;
                  } else {
                     lt = new Point(-100, -100);
                     rb = new Point(100, 100);
                  }

                  Rectangle box = SecondaryStatEffect.calculateBoundingBox(0, chr.getTruePosition(), chr.isFacingLeft(), lt, rb, null, null, 0);
                  List<AttackPair> ourAttacks = new ArrayList<>();
                  maxdamage *= chr.getStat().dam_r / 100.0;

                  for (MapleMapObject mo : chr.getMap().getCharactersIntersect(box)) {
                     MapleCharacter attacked = (MapleCharacter)mo;
                     if (attacked.getId() != chr.getId() && attacked.isAlive() && !attacked.isHidden() && (type == 0 || attacked.getTeam() != chr.getTeam())) {
                        double rawDamage = maxdamage
                           / Math.max(
                              0.0,
                              (magic ? attacked.getStat().mdef : attacked.getStat().wdef) * Math.max(1.0, 100.0 - ignoreDEF) / 100.0 * (type == 3 ? 0.1 : 0.25)
                           );
                        if (PlayersHandler.inArea(attacked)) {
                           rawDamage = 0.0;
                        }

                        rawDamage += rawDamage * chr.getDamageIncrease(attacked.getId()) / 100.0;
                        rawDamage = attacked.modifyDamageTaken(rawDamage, attacked).left;
                        double min = rawDamage * chr.getStat().trueMastery / 100.0;
                        List<Pair<Long, Boolean>> attacks = new ArrayList<>(attackCount);
                        int totalMPLoss = 0;
                        int totalHPLoss = 0;

                        for (int i = 0; i < attackCount; i++) {
                           int mploss = 0;
                           double ourDamage = Randomizer.nextInt((int)Math.abs(Math.round(rawDamage - min)) + 1) + min;
                           if (attacked.getStat().dodgeChance > 0 && Randomizer.nextInt(100) < attacked.getStat().dodgeChance) {
                              ourDamage = 0.0;
                           }

                           if (attacked.getBuffedValue(SecondaryStatFlag.MagicGuard) != null) {
                              mploss = (int)Math.min(
                                 (double)attacked.getStat().getMp(), ourDamage * attacked.getBuffedValue(SecondaryStatFlag.MagicGuard).doubleValue() / 100.0
                              );
                           }

                           ourDamage -= mploss;
                           if (attacked.getBuffedValue(SecondaryStatFlag.Infinity) != null) {
                              mploss = 0;
                           }

                           attacks.add(new Pair<>((long)Math.floor(ourDamage), false));
                           totalHPLoss = (int)(totalHPLoss + Math.floor(ourDamage));
                           totalMPLoss += mploss;
                        }

                        attacked.addMPHP(-totalHPLoss, -totalMPLoss);
                        ourAttacks.add(new AttackPair(attacked.getId(), attacked.getPosition(), attacks));
                        attacked.getCheatTracker().setAttacksWithoutHit(false);
                        if (totalHPLoss > 0) {
                           didAttack = true;
                        }

                        if (attacked.getStat().getHPPercent() <= 20) {
                           attacked.getStat();
                           SkillFactory.getSkill(PlayerStats.getSkillByJob(93, attacked.getJob())).getEffect(1).applyTo(attacked);
                        }

                        if (effect != null) {
                           if (effect.getMonsterStati().size() > 0 && effect.makeChanceResult()) {
                              for (Entry<MobTemporaryStatFlag, Integer> z : effect.getMonsterStati().entrySet()) {
                                 SecondaryStatFlag d = MobTemporaryStatFlag.getLinkedDisease(z.getKey());
                                 if (d != null) {
                                    attacked.giveDebuff(d, z.getValue(), 0, effect.getDuration(), d.getDisease(), 1);
                                 }
                              }
                           }

                           effect.handleExtraPVP(chr, attacked);
                        }

                        chr.getClient()
                           .getSession()
                           .writeAndFlush(CField.getPVPHPBar(attacked.getId(), (int)attacked.getStat().getHp(), (int)attacked.getStat().getCurrentMaxHp(chr)));
                        addedScore += totalHPLoss / 100 + totalMPLoss / 100;
                        if (!attacked.isAlive()) {
                           killed = true;
                        }

                        if (ourAttacks.size() >= mobCount) {
                           break;
                        }
                     }
                  }

                  if (killed || addedScore > 0) {
                     chr.getEventInstance().addPVPScore(chr, addedScore);
                     chr.getClient().getSession().writeAndFlush(CField.getPVPScore(ourScore + addedScore, killed));
                  }

                  if (didAttack) {
                     chr.getMap()
                        .broadcastMessage(
                           CField.SummonPacket.pvpSummonAttack(
                              chr.getId(), chr.getLevel(), summon.getObjectId(), summon.isFacingLeft() ? 4 : 132, summon.getTruePosition(), ourAttacks
                           )
                        );
                     if (!summon.isMultiAttack()) {
                        chr.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                        chr.getMap().removeMapObject(summon);
                        chr.removeVisibleMapObject(summon);
                        chr.removeSummon(summon);
                        if (summon.getSkill() != 35121011) {
                           chr.temporaryStatReset(SecondaryStatFlag.indieSummon);
                        }
                     }
                  }
               }
            } else {
               chr.dropMessage(5, "Error.");
            }
         } else {
            chr.dropMessage(5, "The summon has disappeared.");
         }
      }
   }

   public static void activeVMatrixSummon(PacketDecoder slea, MapleClient c) {
      int objectID = slea.readInt();
      slea.skip(1);
      int skillID = slea.readInt();
      int skillLevel = slea.readInt();
      if (skillID == 1301014 || skillID == 1310018 || skillID == 1311014 || skillID == 1311019 || skillID == 1321024 || skillID == 1321025) {
         c.getPlayer().doActiveSkillCooltime(1301014, skillID, c.getPlayer().getTotalSkillLevel(1301014));
      }

      slea.skip(4);
      slea.skip(4);
      slea.skip(4);
      Point unkpos = slea.readPos();
      slea.skip(4);
      slea.skip(4);
      slea.skip(4);
      new Point(slea.readInt(), slea.readInt());
      slea.skip(4);
      slea.skip(1);
      slea.skip(4);
      slea.skip(1);
      slea.skip(8);
      int count = slea.readInt();
      List<Integer> idx = new ArrayList<>();

      for (int i = 0; i < count; i++) {
         slea.readInt();
         slea.readInt();
         int key = slea.readInt();
         slea.readInt();
         idx.add(key);
         slea.skip(2);
         slea.skip(2);
         slea.skip(2);
         slea.skip(2);
         slea.skip(2);
         slea.skip(4);
         slea.skip(1);
         int unk = slea.readByte();
         if (unk == 1) {
            slea.skip(4);
            slea.skip(4);
         }

         int unk2 = slea.readByte();
         if (unk2 == 1) {
            slea.skip(4);
            slea.skip(4);
         }
      }

      Summoned summon = null;

      try {
         for (Summoned sum : c.getPlayer().getSummonsReadLock()) {
            if (sum.getObjectId() == objectID) {
               summon = sum;
               break;
            }
         }
      } finally {
         c.getPlayer().unlockSummonsReadLock();
      }

      if (summon != null) {
         c.getPlayer().send(CField.userThrowingBombAck(skillID, (byte)skillLevel, idx));
      }
   }

   public static void summonedSkillUseRequest(PacketDecoder slea, MapleClient c) {
      int objectID = slea.readInt();
      if (slea.available() >= 12L) {
         slea.skip(4);
         slea.skip(4);
         int skillID = slea.readInt();
         int skillLevel = c.getPlayer().getTotalSkillLevel(skillID);
         if (skillLevel > 0) {
            SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
            if (effect != null) {
               if (skillID == 400021066) {
                  c.getPlayer().giveCoolDowns(skillID, System.currentTimeMillis(), effect.getCooldown(c.getPlayer()));
                  c.getPlayer().send(CField.skillCooldown(skillID, effect.getCooldown(c.getPlayer())));
               }

               if (skillID == 400011054) {
                  c.getPlayer().doActiveSkillCooltime(skillID, skillID, skillLevel);
               }
            }
         }
      }
   }

   public static void summonedChangeSkillRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         chr.invokeJobMethod("summonedChangeSkillRequest", skillID);
      }
   }

   public static void chargeWildGrenade(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         slea.skip(4);
         player.onChargeWildGrenade(slea.readInt());
      }
   }

   public static void fixSummonRequest(PacketDecoder slea, MapleClient c) {
      int objectID = slea.readInt();
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         Summoned summon = (Summoned)chr.getMap().getMapObject(objectID, MapleMapObjectType.SUMMON);
         if (summon != null) {
            summon.setFixed(!summon.isFixed());
            chr.send(CField.SummonPacket.summonSetFix(chr.getId(), objectID, summon.isFixed()));
         }
      }
   }
}
