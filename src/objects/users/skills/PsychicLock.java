package objects.users.skills;

import java.util.List;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class PsychicLock {
   private List<PsychicLock.Info> locks;
   private MapleCharacter player;
   private int skillId;
   private short skillLevel;
   private int action;
   private int actionSpeed;

   public PsychicLock(MapleCharacter player, int skillId, short skillLevel, int action, int actionSpeed, List<PsychicLock.Info> locks) {
      this.player = player;
      this.skillId = skillId;
      this.skillLevel = skillLevel;
      this.action = action;
      this.actionSpeed = actionSpeed;
      this.locks = locks;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.player.getId());
      packet.write(1);
      packet.writeInt(this.skillId);
      packet.writeShort(this.skillLevel);
      packet.writeInt(this.action);
      packet.writeInt(this.actionSpeed);
      if (!this.locks.isEmpty()) {
         this.getLocks().forEach(p -> {
            packet.write(1);
            packet.write(1);
            packet.writeInt(p.getLocalKey());
            packet.writeInt(p.getPsychicLockKey());
            packet.writeInt(p.getMobId());
            packet.writeShort(p.getStuffId());
            packet.writeInt(p.getUnkNew());
            if (p.getMobId() > 0) {
               MapleMonster mob = this.player.getMap().getMonsterByOid(p.getMobId());
               if (mob == null) {
                  packet.writeLong(100L);
                  packet.writeLong(100L);
               } else {
                  packet.writeLong((int)mob.getMobMaxHp());
                  packet.writeLong((int)mob.getStats().getHp());
               }
            } else {
               packet.writeLong(100L);
               packet.writeLong(100L);
            }

            packet.write(p.isLeft() ? 0 : 1);
            packet.writeInt(p.getStartPosX());
            packet.writeInt(p.getStartPosY());
            packet.writeInt(p.getRelX());
            packet.writeInt(p.getRelY());
         });
      }

      packet.write(0);
   }

   public List<PsychicLock.Info> getLocks() {
      return this.locks;
   }

   public static class Info {
      private boolean first;
      private int localKey;
      private int psychicLockKey;
      private int mobId;
      private short stuffId;
      private int startPosX;
      private int startPosY;
      private int relX;
      private int relY;
      private int unkNew;

      public int getLocalKey() {
         return this.localKey;
      }

      public void setLocalKey(int localKey) {
         this.localKey = localKey;
      }

      public int getPsychicLockKey() {
         return this.psychicLockKey;
      }

      public void setPsychicLockKey(int psychicLockKey) {
         this.psychicLockKey = psychicLockKey;
      }

      public int getMobId() {
         return this.mobId;
      }

      public void setMobId(int mobId) {
         this.mobId = mobId;
      }

      public short getStuffId() {
         return this.stuffId;
      }

      public void setStuffId(short stuffId) {
         this.stuffId = stuffId;
      }

      public int getStartPosX() {
         return this.startPosX;
      }

      public void setStartPosX(int startPosX) {
         this.startPosX = startPosX;
      }

      public int getStartPosY() {
         return this.startPosY;
      }

      public void setStartPosY(int startPosY) {
         this.startPosY = startPosY;
      }

      public int getRelX() {
         return this.relX;
      }

      public void setRelX(int relX) {
         this.relX = relX;
      }

      public int getRelY() {
         return this.relY;
      }

      public void setRelY(int relY) {
         this.relY = relY;
      }

      public boolean isLeft() {
         return this.first;
      }

      public void setLeft(boolean first) {
         this.first = first;
      }

      public int getUnkNew() {
         return this.unkNew;
      }

      public void setUnkNew(int unkNew) {
         this.unkNew = unkNew;
      }
   }
}
