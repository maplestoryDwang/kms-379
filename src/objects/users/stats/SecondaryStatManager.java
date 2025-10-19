package objects.users.stats;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import objects.users.MapleClient;
import objects.users.skills.IndieTemporaryStatEntry;

public class SecondaryStatManager {
   MapleClient c = null;
   Flag992 toSet = new Flag992();
   SecondaryStat secondaryStat;
   int skillID = 0;
   int skillLevel = 0;
   int duration = 0;
   long startTime = 0L;
   int fromID = 0;
   boolean fromMob = false;

   public SecondaryStatManager(MapleClient c, SecondaryStat secondaryStat) {
      this.c = c;
      this.secondaryStat = secondaryStat;
   }

   public SecondaryStatManager(MapleClient c, SecondaryStat secondaryStat, int skillID, int skillLevel, int duration, long startTime) {
      this(c, secondaryStat, skillID, skillLevel, duration, startTime, 0);
   }

   public SecondaryStatManager(MapleClient c, SecondaryStat secondaryStat, int skillID, int skillLevel, int duration, long startTime, int fromID) {
      this.c = c;
      this.secondaryStat = secondaryStat;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.duration = duration;
      this.startTime = startTime;
      this.fromID = fromID;
   }

   public void putIndieStatValue(SecondaryStatFlag flag, int value) {
      this.putIndieStatValue(flag, value, true);
   }

   public void putIndieStatValue(SecondaryStatFlag flag, int value, boolean showBuffIcon) {
      IndieTemporaryStatEntry entry = new IndieTemporaryStatEntry(this.skillID, this.skillLevel, this.duration, value, this.startTime, this.fromID);
      entry.setShowBuffIcon(showBuffIcon);
      this.secondaryStat.putIndieStatValue(flag.name(), entry);
      this.toSet.setFlag(flag);
      if (this.secondaryStat != null) {
         this.secondaryStat.getFlag().setFlag(flag);
      }
   }

   public void putIndieStatValue(SecondaryStatFlag flag, int value, boolean showBuffIcon, int duration) {
      IndieTemporaryStatEntry entry = new IndieTemporaryStatEntry(this.skillID, this.skillLevel, duration, value, this.startTime, this.fromID);
      entry.setShowBuffIcon(showBuffIcon);
      this.secondaryStat.putIndieStatValue(flag.name(), entry);
      this.toSet.setFlag(flag);
      if (this.secondaryStat != null) {
         this.secondaryStat.getFlag().setFlag(flag);
      }
   }

   public void putStatValue(SecondaryStatFlag flag, int value) {
      if (flag != null) {
         this.secondaryStat.putStatValue(flag.name(), this.skillID, this.skillLevel, value, System.currentTimeMillis() + this.duration, this.fromID);
         this.toSet.setFlag(flag);
         if (this.secondaryStat != null) {
            this.secondaryStat.getFlag().setFlag(flag);
         }
      }
   }

   public void putStatValue(SecondaryStatFlag flag, int value, int duration) {
      this.secondaryStat.putStatValue(flag.name(), this.skillID, this.skillLevel, value, System.currentTimeMillis() + duration, this.fromID);
      this.toSet.setFlag(flag);
      if (this.secondaryStat != null) {
         this.secondaryStat.getFlag().setFlag(flag);
      }
   }

   public void changeStatValue(SecondaryStatFlag flag, int skillID, int value) {
      if (flag.isIndie()) {
         if (this.secondaryStat.hasIndieStat(flag.name(), skillID)) {
            this.secondaryStat.changeIndieStatValue(flag.name(), skillID, value);
         } else {
            IndieTemporaryStatEntry entry = new IndieTemporaryStatEntry(skillID, this.skillLevel, this.duration, value, System.currentTimeMillis(), this.fromID);
            this.secondaryStat.putIndieStatValue(flag.name(), entry);
         }
      } else if (this.secondaryStat.getFlag().check(flag)) {
         this.secondaryStat.changeStatValue(flag.name(), value);
      } else {
         this.secondaryStat.putStatValue(flag.name(), skillID, this.skillLevel, value, System.currentTimeMillis() + this.duration, this.fromID);
      }

      this.toSet.setFlag(flag);
      if (this.secondaryStat != null) {
         this.secondaryStat.getFlag().setFlag(flag);
      }
   }

   public void changeStatValueAndTill(SecondaryStatFlag flag, int skillID, int value, int addTill, int limit) {
      this.changeStatValue(flag, skillID, value);
      this.changeTill(flag, skillID, addTill, limit);
   }

   public void changeTill(SecondaryStatFlag flag, int skillID, int value) {
      this.changeTill(flag, skillID, value, 0);
   }

   public void changeTill(SecondaryStatFlag flag, int skillID, int value, int limit) {
      if (flag.isIndie()) {
         this.secondaryStat.changeIndieTill(flag.name(), skillID, value);
      } else {
         this.secondaryStat.changeTill(flag.name(), value, limit);
      }

      this.toSet.setFlag(flag);
   }

   public void temporaryStatSet() {
      if (this.c.getPlayer() != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
         this.secondaryStat.encodeForLocal(packet, this.toSet, this.c.getPlayer().getPlayerJob(), this.fromMob);
         this.c.getSession().writeAndFlush(packet.getPacket());
         this.c.getPlayer().getMap().broadcastMessage(this.c.getPlayer(), CWvsContext.BuffPacket.remoteTemporaryStatSet(this.c.getPlayer(), this.toSet), false);
      }
   }

   public void setFromMob(boolean fromMob) {
      this.fromMob = fromMob;
   }
}
