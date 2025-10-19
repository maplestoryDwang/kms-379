package objects.fields.child.karing.FieldSkill;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.fields.fieldskill.FieldSkill;
import objects.fields.fieldskill.FieldSkillEntry;
import objects.fields.fieldskill.MuiltRayInfo;
import objects.fields.fieldskill.ParadeInfo;
import objects.users.MapleCharacter;
import objects.users.skills.SkillFactory;
import objects.utils.Randomizer;

public class KaringFieldAction {
   public static List<Rectangle> rects = new ArrayList<>();
   public static List<Point> points = new ArrayList<>();

   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
   }

   public void sendPacket(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      player.send(packet.getPacket());
   }

   public void broadcastPacket(Field field) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      field.broadcastMessage(packet.getPacket());
   }

   public void AddLinePos(int left, int right) {
      rects.clear();
      int center = (left + right) / 2;

      for (int z = 0; z < 10; z++) {
         int x = Randomizer.rand(left, center);
         int w = Randomizer.rand(-50, 50);
         rects.add(new Rectangle(x, -700, w, 231));
      }

      for (int z = 0; z < 9; z++) {
         int x = Randomizer.rand(center, right);
         int w = Randomizer.rand(-50, 50);
         rects.add(new Rectangle(x, -700, w, 231));
      }
   }

   public static class InitPacket extends KaringFieldAction {
      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
      }

      public static class DeathGrap extends KaringFieldAction.InitPacket {
         int fieldSkill;
         int fieldSkillLevel;
         FieldSkill fieldSkills;
         FieldSkillEntry entry;

         public DeathGrap(int fieldSkill, int fieldSkillLevel) {
            this.fieldSkill = fieldSkill;
            this.fieldSkillLevel = fieldSkillLevel;
            this.fieldSkills = SkillFactory.getFieldSkill(fieldSkill);
            this.entry = this.fieldSkills.getFieldSkillEntry(fieldSkillLevel);
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
         }
      }

      public static class MultiRay extends KaringFieldAction.InitPacket {
         int fieldSkill;
         int fieldSkillLevel;
         int getX;
         int getY;
         int createDelayMin;
         int createDelayMax;
         int attackDelay;
         int time;
         int subTime;
         int attackCountLimit;
         int attackTryCountLimit;
         int mobId;
         int left;
         int right;

         public MultiRay(MuiltRayInfo info, int fieldskill, int fieldskilllevel, int mobid, int left, int right) {
            this.fieldSkill = fieldskill;
            this.fieldSkillLevel = fieldskilllevel;
            this.getX = info.getX();
            this.getY = info.getY();
            this.attackDelay = info.getAttackDelay();
            this.time = info.getTime();
            this.subTime = info.getSubTime();
            this.attackCountLimit = info.getAttackCountLimit();
            this.attackTryCountLimit = info.getAttackTryCountLimit();
            this.createDelayMin = info.getCreateDelayMin();
            this.createDelayMax = info.getCreateDelayMax();
            this.mobId = mobid;
            this.left = left;
            this.right = right;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.fieldSkill);
            packet.writeInt(this.fieldSkillLevel);
            packet.writeInt(this.getX);
            packet.writeInt(this.getY);
            packet.writeInt(this.attackDelay);
            packet.writeInt(this.time);
            packet.writeInt(this.subTime);
            packet.writeInt(this.attackCountLimit);
            packet.writeInt(this.attackTryCountLimit);
            this.AddLinePos(this.left, this.right);
            int center = (this.left + this.right) / 2;
            int size = 19;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(a);
               packet.encodeRect(rects.get(a));
               packet.writeInt(this.mobId);
               if (rects.get(a).getX() <= center) {
                  packet.writeInt(0);
                  packet.writeInt(1);
               } else {
                  packet.writeInt(1);
                  packet.writeInt(2);
               }

               packet.writeShort(Randomizer.rand(this.createDelayMin, this.createDelayMax));
            }
         }
      }

      public static class Parade extends KaringFieldAction.InitPacket {
         Point pt;
         int fieldSkill;
         int fieldSkillLevel;
         FieldSkill fieldSkills;
         FieldSkillEntry entry;
         int preset = Randomizer.rand(0, 1);

         public Parade(int fieldSkill, int fieldSkillLevel) {
            this.fieldSkill = fieldSkill;
            this.fieldSkillLevel = fieldSkillLevel;
            this.fieldSkills = SkillFactory.getFieldSkill(fieldSkill);
            this.entry = this.fieldSkills.getFieldSkillEntry(fieldSkillLevel);
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.fieldSkill);
            packet.writeInt(this.fieldSkillLevel);
            packet.writeInt(8880838);
            packet.writeInt(4);
            int size = 119;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(-120);
               packet.writeInt(225);
               packet.writeInt(132);
               packet.writeInt(18);
               packet.writeInt(1140);
               packet.writeInt(1000);
               packet.writeInt(1);
               packet.writeInt(8880838);
               packet.writeInt(4);
            }

            packet.writeInt(this.entry.getParadeInfosForPreset(this.preset).size());

            for (ParadeInfo info : this.entry.getParadeInfosForPreset(this.preset)) {
               packet.encodePos4Byte(info.getPos());
               packet.writeInt(info.getDelay());
               packet.writeInt(info.getType());
            }
         }
      }
   }
}
