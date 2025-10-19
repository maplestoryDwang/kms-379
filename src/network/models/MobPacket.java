package network.models;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.fields.MapleNodes;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobMoveAction;
import objects.fields.gameobject.lifes.MobTemporaryStat;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillCommand;
import objects.fields.gameobject.lifes.mobskills.MobTeleportType;
import objects.movepath.LifeMovementFragment;
import objects.users.MapleCharacter;
import objects.users.skills.DarkLigntningEntry;
import objects.utils.Pair;
import objects.utils.Rect;
import objects.utils.Triple;

public class MobPacket {
   public static byte[] damageMonster(int oid, long damage) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.write(0);
      mplew.writeLong(damage);
      return mplew.getPacket();
   }

   public static byte[] damageFriendlyMob(MapleMonster mob, long damage, boolean display) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
      mplew.writeInt(mob.getObjectId());
      mplew.write(display ? 1 : 2);
      mplew.writeLong(damage);
      Pair<Long, Long> pair = mob.getHPForDisplay();
      mplew.writeLong(pair.left);
      mplew.writeLong(pair.right);
      return mplew.getPacket();
   }

   public static byte[] killMonster(int oid, int animation) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.write(animation);
      mplew.write(0);
      switch (animation) {
         case 0:
         case 1:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
            mplew.writeInt(0);
            mplew.writeInt(0);
         case 2:
         case 3:
         case 4:
         default:
            if (animation == 9) {
               mplew.writeInt(-1);
            }

            return mplew.getPacket();
      }
   }

   public static byte[] healMonster(int oid, long heal) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.write(0);
      mplew.writeLong(-heal);
      return mplew.getPacket();
   }

   public static byte[] MobToMobDamage(int oid, int dmg, int mobid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOB_TO_MOB_DAMAGE.getValue());
      mplew.writeInt(oid);
      mplew.write(0);
      mplew.writeInt(dmg);
      mplew.writeInt(mobid);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] showMonsterHP(int oid, int remhppercentage) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
      mplew.writeInt(oid);
      mplew.writeInt(remhppercentage);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] showBossHP(MapleMonster mob) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(8);
      mplew.writeInt(mob.getId());
      Pair<Long, Long> pair = mob.getHPForDisplay();
      mplew.writeLong(pair.left);
      mplew.writeLong(pair.right);
      mplew.write(mob.getId() == 8880504 ? 1 : mob.getStats().getTagColor());
      mplew.write(mob.getStats().getTagBgColor());
      return mplew.getPacket();
   }

   public static byte[] showBossHP(int monsterId, long currentHp, long maxHp) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(8);
      mplew.writeInt(monsterId);
      mplew.writeLong((int)(currentHp <= 0L ? -1L : currentHp));
      mplew.writeLong((int)maxHp);
      mplew.write(6);
      mplew.write(5);
      return mplew.getPacket();
   }

   public static byte[] moveMonster(
      byte unk,
      int action,
      int skillID,
      int skillLevel,
      int skillDelay,
      int oid,
      int tEncodedGatherDuration,
      Point startPos,
      Point velPos,
      List<LifeMovementFragment> moves,
      List<Pair<Short, Short>> multiTargetForBall,
      List<Short> randTimeForAreaAttack,
      int v329,
      List<Integer> v329List,
      boolean cannotUseSkill
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
      packet.writeInt(oid);
      packet.write(unk);
      packet.write(action);
      packet.writeShort(skillID);
      packet.writeShort(skillLevel);
      packet.writeInt(skillDelay);
      packet.write(multiTargetForBall.size());

      for (Pair<Short, Short> shortShortPair : multiTargetForBall) {
         packet.writeShort(shortShortPair.left);
         packet.writeShort(shortShortPair.right);
      }

      packet.write(randTimeForAreaAttack.size());

      for (Short aShort : randTimeForAreaAttack) {
         packet.writeShort(aShort);
      }

      packet.writeInt(v329);
      if (v329List.size() > 0) {
         for (int a : v329List) {
            packet.writeInt(a);
         }
      }

      packet.writeInt(0);
      packet.writeInt(tEncodedGatherDuration);
      packet.encodePos(startPos);
      packet.encodePos(velPos);
      PacketHelper.serializeMovementList(packet, moves);
      packet.write(cannotUseSkill);
      return packet.getPacket();
   }

   public static byte[] spawnMonster(MapleMonster life, int spawnType, int createDelay) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
      mplew.write(0);
      int objectID = life.getObjectId();
      if (objectID % 178 == 0) {
         mplew.writeInt(0);
      }

      mplew.writeInt(life.getObjectId());
      mplew.write(1);
      mplew.writeInt(life.getId());
      addMonsterStatus(mplew, life);
      mplew.writeShort(life.getPosition().x);
      mplew.writeShort(life.getPosition().y);
      mplew.write(life.getStance() == 0 ? 5 : life.getStance());
      if (life.getId() == 8910000 || life.getId() == 8910100 || life.getId() == 9990033) {
         mplew.write(0);
      }

      mplew.writeShort(life.getFh());
      mplew.writeShort(life.originFh());
      if (life.getStats().getEx() == 1) {
         mplew.write(-2);
      } else {
         mplew.write(spawnType);
         if (spawnType == -3 || spawnType == -6 || spawnType >= 0) {
            mplew.writeInt(createDelay != 0 ? createDelay : life.getCreateDelay());
         }
      }

      mplew.write(255);
      Pair<Long, Long> pair = life.getHPForDisplay();
      mplew.writeLong(pair.left);
      mplew.writeInt(0);
      mplew.writeInt(life.getPhase());
      mplew.writeInt(life.getMobZoneDataType());
      mplew.writeInt(0);
      mplew.writeInt(-1);
      mplew.writeInt(0);
      mplew.writeInt(life.getPhase() != 0 ? life.getPhase() : -1);
      switch (life.getId()) {
         case 8880180:
         case 8880186:
            mplew.write(101);
            break;
         case 8880182:
            mplew.write(61);
            break;
         case 8880340:
            mplew.write(100);
            break;
         case 8880344:
            mplew.write(48);
            break;
         case 8880502:
            mplew.write(32);
            break;
         default:
            mplew.write(0);
      }

      boolean unk = false;
      mplew.writeInt(unk ? 1 : 0);
      if (unk) {
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      mplew.writeInt(Math.max(life.getEliteMobGrade().getType() < 0 && life.getScale() == 100 ? 100 : life.getScale(), 100));
      mplew.writeInt(life.getEliteMobGrade().getType() < 0 ? -1 : life.getEliteMobGrade().getType());
      if (life.getEliteMobGrade().getType() >= 0) {
         life.addEliteMobSkills(mplew);
         mplew.writeInt(life.getEliteMobType().getType());
      }

      boolean unk2 = false;
      mplew.write(unk2);
      if (unk2) {
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      boolean unk3 = false;
      mplew.write(unk3);
      if (unk3) {
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      mplew.writeInt(life.getRelMobZones().size());
      life.getRelMobZones().forEach(z -> {
         mplew.writeInt(z.left);
         mplew.writeInt(z.right);
      });
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      if (life.getId() == 8880102 || life.getTargetFromSvr() > 0 || life.getId() == 8880605) {
         mplew.writeInt(life.getTargetFromSvr());
      }

      boolean isGollemSpecial = life.getId() == 8880181 || life.getId() == 8880187;
      mplew.writeInt(isGollemSpecial ? 1 : 0);
      if (isGollemSpecial) {
         mplew.writeInt(200);
      }

      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(0);
      if (life.getStats().getEx() == 1) {
         mplew.writeInt(1);
         mplew.writeInt(190000);
      } else {
         mplew.writeInt(0);
      }

      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      if (life.getStats().getEx() == 1) {
         mplew.writeInt(life.getOwner());
         mplew.writeInt(0);
         mplew.writeInt(0);
      } else {
         mplew.writeInt(0);
      }

      if (life.getStats() == null) {
         mplew.write(0);
      } else {
         List<Triple<String, Integer, Integer>> skeletonData = life.getStats().getSkeletonData();
         mplew.write(skeletonData.size());

         for (Triple<String, Integer, Integer> skeleton : skeletonData) {
            mplew.writeMapleAsciiString(skeleton.left);
            mplew.write(skeleton.mid);
            mplew.writeInt(skeleton.right);
         }
      }

      return mplew.getPacket();
   }

   public static void addMonsterStatus(PacketEncoder mplew, MapleMonster life) {
      if (life.getStati().size() <= 1) {
         life.addEmpty();
      }

      mplew.write(life.getChangedStats() != null ? 1 : 0);
      if (life.getChangedStats() != null) {
         mplew.writeLong(life.getChangedStats().hp);
         mplew.writeInt(life.getChangedStats().mp);
         mplew.writeInt(life.getChangedStats().exp);
         mplew.writeInt(life.getChangedStats().watk);
         mplew.writeInt(life.getChangedStats().matk);
         mplew.writeInt(life.getChangedStats().PDRate);
         mplew.writeInt(life.getChangedStats().MDRate);
         mplew.writeInt(life.getChangedStats().acc);
         mplew.writeInt(life.getChangedStats().eva);
         mplew.writeInt(life.getChangedStats().pushed);
         mplew.writeInt(life.getChangedStats().speed);
         mplew.writeInt(life.getChangedStats().level);
         mplew.writeInt(0);
         mplew.write(0);
      }

      Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new HashMap<>();

      for (MobTemporaryStatEffect effect : life.getStati().values()) {
         stats.put(effect.getStati(), effect);
      }

      MobTemporaryStat.encodeStat(0, mplew, stats, life, 0, false);
   }

   public static byte[] mobAffected(int objectID, int skillID, int delay, int skillLevel) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOB_AFFECTED.getValue());
      mplew.writeInt(objectID);
      mplew.writeInt(skillID);
      mplew.writeShort(delay);
      mplew.write(1);
      mplew.writeInt(skillLevel);
      return mplew.getPacket();
   }

   public static byte[] controlMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
      mplew.write(aggro ? 2 : 1);
      mplew.writeInt(life.getObjectId());
      mplew.write(1);
      mplew.writeInt(life.getId());
      addMonsterStatus(mplew, life);
      mplew.writeShort(life.getPosition().x);
      mplew.writeShort(life.getPosition().y);
      mplew.write(life.getStance() == 0 ? 5 : life.getStance());
      if (life.getId() == 8910000 || life.getId() == 8910100 || life.getId() == 9990033) {
         mplew.write(0);
      }

      mplew.writeShort(life.getFh());
      mplew.writeShort(life.originFh());
      mplew.write(-1);
      mplew.write(255);
      mplew.writeLong(life.getHPForDisplay().left);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(life.getMobZoneDataType());
      mplew.writeInt(0);
      mplew.writeInt(-1);
      mplew.writeInt(0);
      mplew.writeInt(life.getPhase() != 0 ? life.getPhase() : -1);
      switch (life.getId()) {
         case 8880182:
            mplew.write(61);
            break;
         case 8880340:
            mplew.write(100);
            break;
         case 8880344:
            mplew.write(48);
            break;
         case 8880502:
            mplew.write(32);
            break;
         default:
            mplew.write(0);
      }

      mplew.writeInt(0);
      mplew.writeInt(life.getEliteMobGrade().getType() <= 0 && life.getScale() == 100 ? 100 : life.getScale());
      mplew.writeInt(life.getEliteMobGrade().getType() <= 0 ? -1 : life.getEliteMobGrade().getType());
      if (life.getEliteMobGrade().getType() > 0) {
         life.addEliteMobSkills(mplew);
         mplew.writeInt(life.getEliteMobType().getType());
      }

      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(life.getRelMobZones().size());
      life.getRelMobZones().forEach(z -> {
         mplew.writeInt(z.left);
         mplew.writeInt(z.right);
      });
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      if (life.getId() == 8880102 || life.getTargetFromSvr() > 0 || life.getId() == 8880605) {
         mplew.writeInt(life.getTargetFromSvr());
      }

      boolean isGollemSpecial = life.getId() == 8880181 || life.getId() == 8880187;
      mplew.writeInt(isGollemSpecial ? 1 : 0);
      if (isGollemSpecial) {
         mplew.writeInt(200);
      }

      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(life.getController().getId());
      List<Triple<String, Integer, Integer>> skeletonData = life.getStats().getSkeletonData();
      mplew.write(skeletonData.size());

      for (Triple<String, Integer, Integer> skeleton : skeletonData) {
         mplew.writeMapleAsciiString(skeleton.left);
         mplew.write(skeleton.mid);
         mplew.writeInt(skeleton.right);
      }

      return mplew.getPacket();
   }

   public static byte[] stopControllingMonster(int oid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
      mplew.write(0);
      mplew.writeInt(oid);
      return mplew.getPacket();
   }

   public static byte[] makeMonsterReal(MapleMonster life) {
      return spawnMonster(life, -1, 0);
   }

   public static byte[] makeMonsterFake(MapleMonster life) {
      return spawnMonster(life, -4, 0);
   }

   public static byte[] makeMonsterEffect(MapleMonster life, int effect) {
      return spawnMonster(life, effect, 0);
   }

   public static byte[] mobCtrlAck(int objectID, short ctrlSN, int currentMp, boolean nextAttackPossible, MobSkillCommand command) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOB_CTRL_ACK.getValue());
      mplew.writeInt(objectID);
      mplew.writeShort(ctrlSN);
      mplew.write(nextAttackPossible);
      mplew.writeInt(currentMp);
      mplew.writeInt(command.getSkillCommand());
      mplew.writeShort(command.getSkillCommandLevel());
      mplew.writeInt(command.getAttackCommand());
      mplew.writeInt(command.getSkillCommand());
      mplew.write(command.getSkillCommand() > 0);
      return mplew.getPacket();
   }

   public static byte[] mobStatSet(int skillID, MapleMonster mob, MobTemporaryStatEffect effect) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
      mplew.writeInt(mob.getObjectId());
      MobTemporaryStat.encodeStat(skillID, mplew, Collections.singletonMap(effect.getStati(), effect), mob, 0, true);
      return mplew.getPacket();
   }

   public static byte[] applyMonsterStatus(int skillID, MapleMonster mob, MobTemporaryStatEffect ms) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
      mplew.writeInt(mob.getObjectId());
      MobTemporaryStat.encodeStat(skillID, mplew, Collections.singletonMap(ms.getStati(), ms), mob, 0, true);
      return mplew.getPacket();
   }

   public static byte[] applyMonsterStatus(int skillID, MapleMonster mob, List<MobTemporaryStatEffect> ms) {
      if (ms.size() > 0 && ms.get(0) != null) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
         mplew.writeInt(mob.getObjectId());
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> map = new HashMap<>();
         ms.forEach(l -> map.put(l.getStati(), l));
         MobTemporaryStat.encodeStat(skillID, mplew, map, mob, 0, true);
         return mplew.getPacket();
      } else {
         return CWvsContext.enableActions(null);
      }
   }

   public static byte[] applyMonsterStatus(int skillID, MapleMonster mob, Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stati) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
      mplew.writeInt(mob.getObjectId());
      MobTemporaryStat.encodeStat(skillID, mplew, stati, mob, 0, true);
      return mplew.getPacket();
   }

   public static byte[] cancelMonsterStatus(MapleMonster mob, Map<MobTemporaryStatFlag, MobTemporaryStatEffect> tempStat, int playerID) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
      mplew.writeInt(mob.getObjectId());
      MobTemporaryStat.encodeStatReset(mplew, mob, tempStat, playerID);
      return mplew.getPacket();
   }

   public static byte[] talkMonster(int oid, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TALK_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.writeInt(type);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] talkMonster(int oid, int itemId, String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TALK_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.writeInt(500);
      mplew.writeInt(itemId);
      mplew.write(itemId <= 0 ? 0 : 1);
      mplew.write(msg != null && msg.length() > 0 ? 1 : 0);
      if (msg != null && msg.length() > 0) {
         mplew.writeMapleAsciiString(msg);
      }

      mplew.writeInt(1);
      return mplew.getPacket();
   }

   public static byte[] removeTalkMonster(int oid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_TALK_MONSTER.getValue());
      mplew.writeInt(oid);
      return mplew.getPacket();
   }

   public static final byte[] getNodeProperties(MapleMonster objectid, Field map) {
      if (objectid.getNodePacket() != null) {
         return objectid.getNodePacket();
      } else {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.MONSTER_PROPERTIES.getValue());
         mplew.writeInt(objectid.getObjectId());
         mplew.writeInt(map.getNodes().size());
         mplew.writeInt(objectid.getPosition().x);
         mplew.writeInt(objectid.getPosition().y);

         for (MapleNodes.MapleNodeInfo mni : map.getNodes()) {
            mplew.writeInt(mni.x);
            mplew.writeInt(mni.y);
            mplew.writeInt(mni.attr);
            if (mni.attr == 2) {
               mplew.writeInt(500);
            }
         }

         mplew.writeInt(0);
         mplew.write(0);
         mplew.write(0);
         objectid.setNodePacket(mplew.getPacket());
         return objectid.getNodePacket();
      }
   }

   public static byte[] showMagnet(int mobid, boolean success) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_MAGNET.getValue());
      mplew.writeInt(mobid);
      mplew.write(success ? 1 : 0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] catchMonster(int mobObjectID, boolean success, boolean delay) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CATCH_MONSTER.getValue());
      mplew.writeInt(mobObjectID);
      mplew.write(success);
      mplew.write(delay);
      return mplew.getPacket();
   }

   public static byte[] mobLaserControl(int mobID, int u, int n, int direction) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOB_LASER_CONTROL.getValue());
      mplew.writeInt(mobID);
      mplew.writeInt(u);
      mplew.writeInt(n);
      mplew.write(direction);
      return mplew.getPacket();
   }

   public static byte[] teleportRequest(int mobOjectID, MobTeleportType type, PacketEncoder p) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_TELEPORT.getValue());
      packet.writeInt(mobOjectID);
      packet.write(0);
      packet.writeInt(type.getType());
      if (p != null) {
         packet.encodeBuffer(p.getPacket());
      }

      return packet.getPacket();
   }

   public static byte[] willTeleportRequest(int mobID, int x, int y) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_TELEPORT.getValue());
      packet.writeInt(mobID);
      packet.write(0);
      packet.writeInt(12);
      packet.writeInt(x);
      packet.writeInt(y);
      return packet.getPacket();
   }

   public static byte[] teleportRequest(int objectID, int mobID, int skillAfter) {
      return teleportRequest(objectID, mobID, skillAfter, 0, 0);
   }

   public static byte[] teleportRequest(int objectID, int skillAfter, int x, int y) {
      return teleportRequest(objectID, 0, skillAfter, x, y);
   }

   public static byte[] teleportRequest(int objectID, int mobID, int skillAfter, int x, int y) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_TELEPORT.getValue());
      packet.writeInt(mobID);
      packet.write(skillAfter == 0);
      if (skillAfter != 0) {
         packet.writeInt(skillAfter);
         switch (skillAfter) {
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 16:
               packet.writeInt(mobID != 8930000 && mobID != 8930100 ? x : -1710);
               packet.writeInt(mobID != 8930000 && mobID != 8930100 ? y : 410);
               break;
            case 4:
               packet.writeInt(0);
            case 13:
            case 15:
         }
      }

      return packet.getPacket();
   }

   public static byte[] mobSkillDelay(int mobID, int skillDelayTime, int skillID, int skillLevel, int sequenceDelay, List<Rect> areaWarnings) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_SKILL_DELAY.getValue());
      packet.writeInt(mobID);
      packet.writeInt(skillDelayTime);
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      packet.writeInt(sequenceDelay);
      packet.writeInt(0);
      packet.writeInt(areaWarnings.size());

      for (Rect rect : areaWarnings) {
         rect.encode(packet);
      }

      return packet.getPacket();
   }

   public static byte[] mobDamageShareInfoToLocal(int mobObjectID, byte localUserInInserver, byte countInServer) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DAMAGE_SHARE_INFO_TO_LOCAL.getValue());
      packet.writeInt(mobObjectID);
      packet.write(localUserInInserver);
      packet.write(countInServer);
      return packet.getPacket();
   }

   public static byte[] mobDamageShareInfoToRemote(int mobObjectID, byte countInServer) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DAMAGE_SHARE_INFO_TO_REMOTE.getValue());
      packet.writeInt(mobObjectID);
      packet.write(countInServer);
      return packet.getPacket();
   }

   public static byte[] mobSetAfterAttack(int mobObjectID, int afterAttack, int afterAttackCount, int serverAction, boolean isLeft) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_SET_AFTER_ATTACK.getValue());
      packet.writeInt(mobObjectID);
      packet.writeShort(afterAttack);
      packet.writeInt(afterAttackCount);
      packet.writeInt(serverAction);
      packet.write(isLeft);
      return packet.getPacket();
   }

   public static byte[] mobEmberExplode(int mobObjectID, int playerID, int skillID, int skillLevel, long damage, int attackCount) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_EMBER_EXPLODE.getValue());
      packet.writeInt(mobObjectID);
      packet.write(0);
      packet.writeLong(damage);
      packet.writeInt(playerID);
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      packet.writeInt(attackCount);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] sendAttackBlocked(int mobOjectID, List<Integer> attackBlocked) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_ATTACK_BLOCK.getValue());
      packet.writeInt(mobOjectID);
      packet.writeInt(attackBlocked.size());
      attackBlocked.forEach(m -> packet.writeInt(m));
      return packet.getPacket();
   }

   public static byte[] mobDirectionActionPacket(int mobObjectID, MobMoveAction action) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DIRECTION_ACTION.getValue());
      packet.writeInt(mobObjectID);
      packet.writeInt(action.getType());
      return packet.getPacket();
   }

   public static byte[] mobForcedSkillAction(int mobObjectID, int type, boolean forced) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_FORCED_SKILL_ACTION.getValue());
      packet.writeInt(mobObjectID);
      packet.writeInt(type);
      packet.write(forced);
      return packet.getPacket();
   }

   public static byte[] blackMageSkillAction(int mobObjectID, int type, boolean forced) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_MAGE_SKILL_ACTION.getValue());
      packet.writeInt(mobObjectID);
      packet.writeInt(type);
      packet.write(forced);
      packet.write(0);
      return packet.getPacket();
   }

   public static byte[] mobHitEffectBySkill(int mobObjectID, int skillID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_SHOW_HIT_EFFECT_BY_SKILL.getValue());
      packet.writeInt(mobObjectID);
      packet.writeInt(skillID);
      return packet.getPacket();
   }

   public static byte[] mobHitEffect(int skillID, List<Integer> targets) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_SHOW_HIT_EFFECT.getValue());
      packet.writeInt(targets.size());
      targets.forEach(t -> {
         packet.writeInt(t);
         packet.writeInt(skillID);
      });
      return packet.getPacket();
   }

   public static byte[] mobTemporarySetDarkLightning(HashMap<Integer, DarkLigntningEntry> targets, int duration) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DARKLIGHTNING_TEMPORARY_SET.getValue());
      packet.write(duration > 0 ? 1 : 0);
      if (duration > 0) {
         packet.writeInt(targets.size());

         for (Integer target : targets.keySet()) {
            packet.writeInt(target);
         }

         packet.writeInt(targets.size());

         for (Integer target : targets.keySet()) {
            packet.writeInt(target);
            packet.writeInt(System.currentTimeMillis() + duration - targets.get(target).getEndTime());
            packet.writeInt(duration);
         }
      }

      return packet.getPacket();
   }

   public static byte[] mobDieEffectBySkill(int mobOid, int skillId, int userId) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DIE_EFFECT_BY_SKILL.getValue());
      packet.writeInt(mobOid);
      packet.writeInt(skillId);
      packet.writeInt(userId);
      packet.writeInt(0);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] monsterResistOrigin(MapleMonster monster, MapleCharacter player, int time) {
      return monsterResist(monster, player, time, 80003365, 8);
   }

   public static byte[] monsterResist(MapleMonster monster, MapleCharacter player, int time, int skill) {
      return monsterResist(monster, player, time, skill, 2);
   }

   public static byte[] monsterResist(MapleMonster monster, MapleCharacter player, int time, int skill, int type) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.MONSTER_RESIST.getValue());
      o.writeInt(monster.getObjectId());
      o.writeInt(type);
      o.writeInt(skill);
      o.writeShort(time);
      o.writeInt(player.getId());
      o.write(type == 1 || type == 8);
      o.writeInt(0);
      return o.getPacket();
   }

   public static byte[] monsterResistRemove(MapleMonster monster) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.MONSTER_RESIST_REMOVE.getValue());
      o.writeInt(monster.getObjectId());
      o.writeInt(1);
      o.write(false);
      return o.getPacket();
   }

   public static byte[] guardianAngelSlimeAction(MapleMonster monster, List<Triple<Integer, Integer, Integer>> dataList) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.GUARDIAN_ANGEL_SLIME_ACTION.getValue());
      o.writeInt(monster.getObjectId());
      o.writeInt(1);
      o.writeInt(dataList.size());

      for (Triple<Integer, Integer, Integer> data : dataList) {
         o.writeInt(data.left);
         o.writeInt(data.mid);
         o.writeInt(data.right);
      }

      return o.getPacket();
   }
}
