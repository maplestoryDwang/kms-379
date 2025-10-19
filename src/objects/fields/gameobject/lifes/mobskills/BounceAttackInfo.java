package objects.fields.gameobject.lifes.mobskills;

import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;

public class BounceAttackInfo {
   private int objectSN = 0;
   private int powerX = 0;
   private int powerY = 0;
   private int friction = 0;
   private int resititution = 0;
   private int destroyDelay = 0;
   private int startDelay = 0;
   private boolean noGravity = false;
   private boolean notDestroyByCollide = false;
   private int incScale = 0;
   private int maxScale = 0;
   private int decRadius = 0;
   private int angle = 0;
   private int limit = 0;
   private int maxSpeedX = 0;
   private int maxSpeedY = 0;

   public BounceAttackInfo(MapleMonster mob, MobSkillInfo mobSkillInfo, int skillDelay) {
      mob.getMap();
      this.objectSN = Field.bounceAttackSN.addAndGet(1);
      this.powerX = mobSkillInfo.getLt().x;
      this.powerY = mobSkillInfo.getLt().y;
      this.friction = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.z);
      this.resititution = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.w);
      this.destroyDelay = (int)mobSkillInfo.getDuration();
      this.startDelay = skillDelay;
      this.noGravity = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.noGravity) == 1;
      this.notDestroyByCollide = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.notDestroyByCollide) == 1;
      this.incScale = mobSkillInfo.getLt2().x;
      this.maxScale = mobSkillInfo.getLt2().y;
      this.decRadius = mobSkillInfo.getRb().x;
      this.angle = mobSkillInfo.getRb().y;
      this.limit = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.limit);
   }

   public void encode(PacketEncoder packet, int skillID, int skillLevel) {
      packet.writeInt(this.objectSN);
      packet.writeInt(this.powerX);
      packet.writeInt(this.powerY);
      packet.writeInt(this.friction);
      packet.writeInt(this.resititution);
      packet.writeInt(this.destroyDelay);
      packet.writeInt(this.startDelay);
      packet.writeInt(this.limit);
      packet.write(this.noGravity);
      packet.write(this.notDestroyByCollide);
      if (skillID == 217 && (skillLevel == 3 || skillLevel == 4)) {
         packet.writeInt(this.maxSpeedX);
         packet.writeInt(this.maxSpeedY);
      }

      if (this.notDestroyByCollide) {
         packet.writeInt(this.incScale);
         packet.writeInt(this.maxScale);
         packet.writeInt(this.decRadius);
         packet.writeInt(this.angle);
      }
   }
}
