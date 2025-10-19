package objects.fields.child.karing;

import java.awt.Point;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.child.karing.FieldSkill.KaringFieldAction;
import objects.fields.fieldskill.FieldSkill;
import objects.fields.fieldskill.FieldSkillEntry;
import objects.fields.fieldskill.MuiltRayInfo;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.skills.SkillFactory;

public class Field_BossGoongiPhase extends Field_BossKaring {
   private long nextMultiRayTime = 0L;
   private int FieldSkill = 100023;
   private int FieldSkillLevel = 6;
   private int Goongi = 8880830;
   private int InvisibleGoongi = 8880833;
   private long nextMemtalityTime = 0L;

   public Field_BossGoongiPhase(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.nextMultiRayTime <= System.currentTimeMillis()) {
         this.nextMultiRayTime = System.currentTimeMillis() + 1000L;
         FieldSkill fieldSkill = SkillFactory.getFieldSkill(this.FieldSkill);
         FieldSkillEntry entry = fieldSkill.getFieldSkillEntry(this.FieldSkillLevel);
         MuiltRayInfo info = entry.getMuiltRayInfo();
         KaringFieldAction.InitPacket.MultiRay ray = new KaringFieldAction.InitPacket.MultiRay(
            info, this.FieldSkill, this.FieldSkillLevel, this.InvisibleGoongi, this.getLeft(), this.getRight()
         );
         ray.broadcastPacket(this);
      }

      if (this.nextMemtalityTime <= System.currentTimeMillis()) {
         this.nextMemtalityTime = System.currentTimeMillis() + 3000L;
      }

      MapleMonster boss = this.findBoss();
      if (boss != null) {
         ;
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      int goongiRef = 8880830;
      MapleMonster guardianAttackMob = MapleLifeFactory.getMonster(goongiRef);
      guardianAttackMob.setStance(2);
      guardianAttackMob.setFh(4);
      this.spawnMonster_sSack(guardianAttackMob, new Point(513, 106), 0, false, true);
      int phase1BossMobID = 8880833;
      MapleMonster phase1Boss = MapleLifeFactory.getMonster(phase1BossMobID);
      this.spawnMonster_sSack(phase1Boss, new Point(167, 106), 1, false, true);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.sendWeatherEffectNotice(382, 5000, false, "카링을 쫓기 위해서는 도원경의 각 계절을 침공한 사흉을 물리쳐야 한다."));
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
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      this.isClear(0);

      for (Reactor r : this.getAllReactorsThreadsafe()) {
         r.setState((byte)1);
         mob.getMap().broadcastMessage(CField.triggerReactor(r, 0));
      }
   }

   @Override
   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      super.onMobSkill(mob, skillID, skillLevel);
      mob.getMap().getCharacters().get(0).dropMessage(6, "skillID : " + skillID + " skillLevel : " + skillLevel);
      if (skillID == 268 && skillLevel == 1) {
      }
   }

   public static void unkHandler(PacketDecoder slea, MapleClient c) {
      int objectID = slea.readInt();
      MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(objectID);
      KaringUnkPacket(mob, mob.getPosition());
      new PacketEncoder();
      c.getPlayer().dropMessage(-8, "타입 3번의 패킷을 전송했습니다." + mob.getId());
   }

   public static void GoongiTestHandler(PacketDecoder slea, MapleClient c) {
      int objectID = slea.readInt();
      MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(objectID);
      int type = slea.readInt();
      c.getPlayer().dropMessageGM(6, "타입 : " + type);
      switch (type) {
         case 0: {
            int unk2 = slea.readInt();
            int unk3 = slea.readInt();
            int unk4 = slea.readInt();
            long unk5 = slea.readLong();
            byte unk6 = slea.readByte();
            long unk7 = slea.readLong();
            int unk8 = slea.readInt();
            break;
         }
         case 1: {
            int AttackIndex = slea.readInt();
            int SkillIndex = slea.readInt();
            long unklong = slea.readLong();
            int unk3 = slea.readInt();
            byte unk4 = slea.readByte();
            Point pos = slea.readPosInt();
            int rPunkValue = slea.readInt();
            c.getPlayer()
               .dropMessage(
                  5,
                  "mobID : "
                     + mob.getObjectId()
                     + " : AttackIndex : "
                     + AttackIndex
                     + " SkillIndex : "
                     + SkillIndex
                     + " unklong : "
                     + unklong
                     + " unk3 : "
                     + unk3
                     + " unk4 : "
                     + unk4
                     + " pos : "
                     + pos
                     + " PunkValue : "
                     + rPunkValue
               );
            c.getPlayer().send(BossActionReset(mob, c.getPlayer(), 0, pos));
            c.getPlayer().send(KaringBossAttack(mob, AttackIndex, SkillIndex, 0, unklong, c.getPlayer().getPosition()));
            break;
         }
         case 2: {
            int AttackIndex = slea.readInt();
            int SkillIndex = slea.readInt();
            int mobskill = slea.readInt();
            int mobSkillLevel = slea.readInt();
            int delay = slea.readInt();
            int rPunkValue = slea.readInt();
            c.getPlayer()
               .dropMessage(
                  5,
                  mob.getObjectId()
                     + " : AttackIndex : "
                     + AttackIndex
                     + " SkillIndex : "
                     + SkillIndex
                     + " mobskill : "
                     + mobskill
                     + " mobSkillLevel : "
                     + mobSkillLevel
                     + " delay : "
                     + delay
                     + " PunkValue : "
                     + rPunkValue
               );
            c.getPlayer().send(BossActionReset(mob, c.getPlayer(), 0, mob.getPosition()));
            c.getPlayer().send(KaringBossRushEffect(mob, SkillIndex, mobskill, mobSkillLevel, new Point(1654, -272)));
            c.getPlayer().send(BossActionReset(mob, c.getPlayer(), 0, mob.getPosition()));
            c.getPlayer().send(KaringBossunkpacket(mob, true, AttackIndex, mob.getPosition()));
            c.getPlayer().send(BossActionReset(mob, c.getPlayer(), 1, mob.getPosition()));
            c.getPlayer().send(KaringBossMobSkillAction(mob, 1, 5, mob.getPosition()));
         }
         case 3:
         case 4:
         case 6:
         default:
            break;
         case 5: {
            int rPunkValue = slea.readInt();
            Point pos = slea.readPosInt();
            c.getPlayer().dropMessage(5, mob.getObjectId() + " : PunkValue : " + rPunkValue + " pos : " + pos);
            c.getPlayer().send(BossActionReset(mob, c.getPlayer(), 1, mob.getPosition()));
            c.getPlayer().send(KaringBossunkpacket(mob, false, 52, pos));
         }
      }
   }

   public static byte[] BossInit(MapleMonster mob, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1390);
      packet.writeInt(mob.getId());
      packet.writeInt(chr.getId());
      return packet.getPacket();
   }

   public static byte[] BossActionReset(MapleMonster mob, MapleCharacter chr, int b, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1400);
      packet.writeInt(mob.getObjectId());
      packet.writeInt(mob.getId());
      packet.writeInt(chr.getId());
      packet.writeInt(b);
      packet.encodePos4Byte(pos);
      return packet.getPacket();
   }

   public static byte[] KaringBossAttack(MapleMonster mob, int AttackIndex, int SkillIndex, int unk1, long unk2, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1400);
      packet.writeInt(mob.getObjectId());
      packet.writeInt(1);
      packet.writeInt(AttackIndex);
      packet.writeInt(SkillIndex);
      packet.writeLong(unk2);
      packet.encodePos4Byte(pos);
      packet.writeInt(unk1);
      return packet.getPacket();
   }

   public static byte[] KaringBossMobSkillAction(MapleMonster mob, int SkillIndex, int unk1, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1400);
      packet.writeInt(mob.getObjectId());
      packet.writeInt(2);
      packet.writeInt(SkillIndex);
      packet.write(true);
      packet.write(true);
      packet.encodePos4Byte(pos);
      packet.writeInt(unk1);
      return packet.getPacket();
   }

   public static byte[] KaringUnkPacket(MapleMonster mob, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1400);
      packet.writeInt(mob.getObjectId());
      packet.writeInt(3);
      packet.writeInt(mob.getId());
      packet.encodePos4Byte(pos);
      return packet.getPacket();
   }

   public static byte[] KaringBossunkpacket(MapleMonster mob, boolean skill, int AttackIndex, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1400);
      packet.writeInt(mob.getObjectId());
      packet.writeInt(4);
      packet.write(skill);
      packet.writeInt(AttackIndex);
      packet.encodePos4Byte(pos);
      return packet.getPacket();
   }

   public static byte[] KaringBossRushEffect(MapleMonster mob, int SkillIndex, int mobskill, int level, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1400);
      packet.writeInt(mob.getObjectId());
      packet.writeInt(5);
      packet.writeInt(SkillIndex);
      packet.writeInt(mobskill);
      packet.writeInt(level);
      packet.encodePos4Byte(pos);
      return packet.getPacket();
   }
}
