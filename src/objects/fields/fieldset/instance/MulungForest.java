package objects.fields.fieldset.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.HexTool;

public class MulungForest extends FieldSetInstance {
   public MulungForest(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.fieldSeteventTime = 3600000;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);

      for (Integer map : this.fsim.instances) {
         this.field(map).resetFully(false);
         this.field(map).setLastRespawnTime(System.currentTimeMillis());
         this.field(map).setFieldSetInstance(this);
      }

      this.fs.fInstance.putIfAbsent(this, new ArrayList<>());

      for (PartyMemberEntry mpc : this.leader.getParty().getPartyMemberList()) {
         this.fs.fInstance.get(this).add(mpc.getId());
      }

      this.userList = this.fs.fInstance.get(this);
      this.timeOut(this.fieldSeteventTime);
   }

   @Override
   public void userEnter(MapleCharacter user) {
      super.userEnter(user);
      if (user.getMap().getId() == 993198000) {
         SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
         Date lastTime = null;
         Date now = null;

         try {
            lastTime = sdf.parse(user.getOneInfo(100936, "date"));
            now = sdf.parse(sdf.format(new Date()));
         } catch (Exception var6) {
            lastTime = null;
         }

         if (lastTime != null && !lastTime.equals(now)) {
            user.updateOneInfo(100936, "count", "0");
            user.updateOneInfo(100936, "date", sdf.format(new Date()));
         }

         if (lastTime == null) {
            user.updateOneInfo(100936, "count", "0");
            user.updateOneInfo(100936, "date", sdf.format(new Date()));
         }

         user.updateOneInfo(100936, "count", String.valueOf(user.getOneInfoQuestInteger(100936, "count") + 1));
         user.send(initMulungForest());
      }
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
   }

   @Override
   public void userDead(MapleCharacter user) {
   }

   @Override
   public void userLeftParty(MapleCharacter user) {
      super.userLeftParty(user);
   }

   @Override
   public void userDisbandParty() {
      super.userDisbandParty();
   }

   @Override
   public void userDisconnected(MapleCharacter user) {
      super.userDisconnected(user);
   }

   @Override
   public void mobDead(MapleMonster mMob) {
   }

   public static byte[] initMulungForest() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.MULUNG_FOREST.getValue());
      p.writeInt(201);
      p.writeInt(100);
      p.writeInt(9999);
      p.writeInt(750);
      p.writeInt(10);
      p.writeInt(1);
      p.writeInt(300);
      p.writeInt(0);
      p.writeInt(500);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("07 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 F2 05 2A 01 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 E4 0B 54 02 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 AC 23 FC 06 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 74 3B A4 0B 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 5C B2 EC 22 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 88 52 6A 74 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("FF FF FF FF FF FF FF 7F"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("06 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 E8 76 48 17 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 10 A5 D4 E8 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 A0 72 4E 18 09 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 40 7A 10 F3 5A 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 80 C6 A4 7E 8D 03 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("FF FF FF FF FF FF FF 7F"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("03 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("04 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("24 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("25 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("26 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("27 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("01 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("04 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("28 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("29 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("2A 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("2B 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("02 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("04 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("2C 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("2D 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("2E 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("2F 0E 96 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("03 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("01 00 00 00 01 00 00 00 01 00 00 00 8C 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("02 00 00 00 01 00 00 00 01 00 00 00 28 05 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("03 00 00 00 01 00 00 00 01 00 00 00 C8 00 00 00"));
      return p.getPacket();
   }

   public static byte[] initMulungField(int fieldType, int fieldForce) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.MULUNG_FOREST.getValue());
      p.writeInt(202);
      p.writeInt(fieldType);
      p.writeInt(fieldForce);
      return p.getPacket();
   }
}
