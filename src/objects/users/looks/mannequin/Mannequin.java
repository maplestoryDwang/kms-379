package objects.users.looks.mannequin;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;

public class Mannequin {
   public static final int DefaultSlotMax = 42;
   public static final int SkinRoomSlotMax = 6;
   private int slotMax;
   private MannequinEntry[] rooms = new MannequinEntry[42];

   public boolean loadFromDB(int accountID, MannequinType type, MapleCharacter player) {
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean ret = false;

      try (Connection con = DBConnection.getConnection()) {
         this.slotMax = 42;
         if (type == MannequinType.SkinRoom) {
            this.slotMax = 6;
         }

         this.setSlotMax(this.slotMax);
         ps = con.prepareStatement("SELECT * FROM `mannequin` WHERE `account_id` = ? and `type` = ?");
         ps.setInt(1, accountID);
         ps.setInt(2, type.getType());
         rs = ps.executeQuery();
         int index = 0;

         while (rs.next() && index < this.slotMax) {
            int itemID = rs.getInt("item_id");
            byte baseColor = rs.getByte("base_color");
            byte addColor = rs.getByte("add_color");
            byte baseProb = rs.getByte("base_prob");
            this.getRooms()[index++] = new MannequinEntry(type.getType(), itemID, baseColor, addColor, baseProb);
         }

         if (player.getOneInfoQuestInteger(1234567, "add_mannequin_" + type.getType()) == 0) {
            int saveCount = this.getSaveCount();

            for (int i = saveCount; i < Math.min(this.slotMax, saveCount + 3); i++) {
               this.getRooms()[i] = new MannequinEntry();
            }

            ret = true;
            player.updateOneInfo(1234567, "add_mannequin_" + type.getType(), "1");
         }

         return ret;
      } catch (SQLException var25) {
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var27 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var28 = null;
            }
         } catch (SQLException var22) {
         }
      }

      return false;
   }

   public void encode(PacketEncoder packet) {
      this.encodeSlot(packet);
      this.encodeMannequin(packet);
   }

   public void encodeSlot(PacketEncoder packet) {
      packet.write(this.getSlotMax());
      packet.write(this.getSaveCount());
   }

   public void encodeMannequin(PacketEncoder packet) {
      for (int i = 1; i <= 42; i++) {
         MannequinEntry entry = this.getRooms()[i - 1];
         if (entry != null) {
            packet.write(i);
            entry.encode(packet);
         } else {
            packet.write(0);
         }
      }
   }

   public int getSaveCount() {
      int count = 0;

      for (MannequinEntry entry : this.getRooms()) {
         if (entry != null) {
            count++;
         }
      }

      return count;
   }

   public MannequinEntry extendMannequin() {
      for (int i = 0; i < 21; i++) {
         MannequinEntry entry = this.getRooms()[i];
         if (entry == null) {
            entry = new MannequinEntry();
            this.rooms[i] = entry;
            return entry;
         }
      }

      return null;
   }

   public int getSlotMax() {
      return this.slotMax;
   }

   public void setSlotMax(int slotMax) {
      this.slotMax = slotMax;
   }

   public MannequinEntry[] getRooms() {
      return this.rooms;
   }

   public void setRooms(MannequinEntry[] rooms) {
      this.rooms = rooms;
   }

   public void sendMannequinResult(MapleCharacter player, MannequinType type, MannequinRequestType requestType, int result) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MANNEQUIN_RESULT.getValue());
      packet.write(type.getType());
      packet.write(requestType.getType());
      packet.writeInt(result);
      player.send(packet.getPacket());
   }

   public void updateMannequin(
      MapleCharacter player, MannequinType type, MannequinRequestType requestType, MannequinResultType resultType, byte slot, MannequinEntry entry
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MANNEQUIN_UPDATE.getValue());
      packet.write(type.getType());
      packet.write(requestType.getType());
      packet.write(resultType.getType());
      switch (resultType) {
         case IncSlotMax:
            this.encodeSlot(packet);
            break;
         case Save:
            packet.write(slot);
            entry.encode(packet);
            break;
         case Extend:
            packet.write(this.getSlotMax());
            packet.write(this.getSaveCount());
            packet.write(this.getSaveCount() - 1);
            entry.encode(packet);
            break;
         case Unk:
            packet.writeInt(0);
      }

      player.send(packet.getPacket());
   }
}
