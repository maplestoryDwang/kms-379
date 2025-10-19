package network.models;

import java.awt.Point;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.item.Item;
import objects.item.MaplePet;
import objects.movepath.LifeMovementFragment;
import objects.users.MapleCharacter;

public class PetPacket {
   public static final byte[] updatePet(MapleCharacter player, MaplePet pet, Item item, boolean unequip, boolean petLoot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(2);
      mplew.write(0);
      mplew.write(3);
      mplew.write(5);
      mplew.writeShort(pet.getInventoryPosition());
      mplew.write(0);
      mplew.write(5);
      mplew.writeShort(pet.getInventoryPosition());
      PacketHelper.addItemInfo(mplew, item, player, unequip);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static final byte[] showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger, int index) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_PET.getValue());
      mplew.writeInt(chr.getId());
      mplew.writeInt(hunger ? index : chr.getPetIndex(pet));
      if (remove) {
         mplew.writeShort(hunger ? 256 : 0);
      } else {
         mplew.write(1);
         mplew.write(1);
         mplew.writeInt(pet.getPetItemId());
         mplew.writeMapleAsciiString(pet.getName());
         mplew.writeLong(pet.getUniqueId());
         mplew.writeShort(pet.getPos() == null ? chr.getPosition().x : pet.getPos().x);
         mplew.writeShort(pet.getPos() == null ? chr.getPosition().y - 20 : pet.getPos().y - 20);
         mplew.write(pet.getStance());
         mplew.writeShort(pet.getFh());
         mplew.writeInt(pet.getColor());
         mplew.writeShort(pet.getWonderGrade());
         mplew.writeShort(pet.getPetSize());
         mplew.writeShort(0);
      }

      return mplew.getPacket();
   }

   public static final byte[] movePet(int cid, long pid, byte slot, List<LifeMovementFragment> moves, Point startPos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOVE_PET.getValue());
      mplew.writeInt(cid);
      mplew.writeInt((int)slot);
      mplew.writeInt(0);
      mplew.encodePos(startPos);
      mplew.encodePos(new Point(0, 0));
      PacketHelper.serializeMovementList(mplew, moves);
      return mplew.getPacket();
   }

   public static final byte[] petChat(int cid, int un, String text, byte slot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_CHAT.getValue());
      mplew.writeInt(cid);
      mplew.writeInt((int)slot);
      mplew.writeShort(un);
      mplew.writeMapleAsciiString(text);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static final byte[] commandResponse(int cid, byte command, byte slot, boolean success, boolean food) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_COMMAND.getValue());
      mplew.writeInt(cid);
      mplew.writeInt((int)slot);
      mplew.write(food ? 0 : 1);
      mplew.write(command);
      mplew.writeShort(success ? 1 : 0);
      return mplew.getPacket();
   }

   public static final byte[] updatePetLootStatus(int status) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_LOOT_STATUS.getValue());
      mplew.write(status);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static final byte[] updatePetAutoPetFood() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CashPetSkillSettingResult.getValue());
      return mplew.getPacket();
   }

   public static final byte[] loadExceptionList(MaplePet pet) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_LOAD_EXCEPTION_LIST.getValue());
      mplew.writeLong(pet.getUniqueId());
      mplew.write(pet.getLootException().size());

      for (Integer ex : pet.getLootException()) {
         mplew.writeInt(ex);
      }

      return mplew.getPacket();
   }
}
