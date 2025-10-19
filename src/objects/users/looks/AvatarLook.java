package objects.users.looks;

import constants.GameConstants;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryType;
import objects.users.MapleCharacter;
import objects.utils.BitWriter;
import objects.utils.Pair;

public class AvatarLook {
   public static int packedCharacterLookBytes = 120;
   public static int packedCharacterLookVersion = 24;
   private int gender = 0;
   private int skin = 0;
   private int face = 20000;
   private int job = 0;
   private int[] hairEquip = new int[32];
   private int[] unseenEquip = new int[32];
   private int cashWeapon = 0;
   private int weapon = 0;
   private int subWeapon = 0;
   private int elfEar = 0;
   private int addColor = 0;
   private int baseProb = 0;
   private int shift = 0;
   private int petEquipSlot1 = 0;
   private int petEquipSlot2 = 0;
   private int petEquipSlot3 = 0;
   private int faceAcc = 0;
   private int unk = 0;
   private int unk2 = 0;
   private int unk3 = 0;
   private int unk4 = 0;
   private boolean isBeta = false;

   public AvatarLook() {
   }

   public AvatarLook(String targetName) {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement(
               "SELECT `id`,`gender`,`skincolor`,`job`,`face`,`faceBaseColor`,`faceAddColor`,`faceBaseProb`,`hair`,`basecolor`,`addcolor`,`baseprob`,`shift`,`draw_elf_ear` FROM characters WHERE `name` = ?");
         ps.setString(1, targetName);
         rs = ps.executeQuery();

         while (rs.next()) {
            int playerID = rs.getInt("id");
            this.gender = rs.getInt("gender");
            this.skin = rs.getInt("skincolor");
            this.job = rs.getInt("job");
            int face = rs.getInt("face");
            int faceBaseColor = rs.getInt("faceBaseColor");
            int faceAddColor = rs.getInt("faceAddColor");
            int faceBaseProb = rs.getInt("faceBaseProb");
            if (faceBaseProb != 0) {
               face = (face / 1000 * 1000 + face % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100
                     + faceBaseProb;
            }

            this.face = face;
            this.hairEquip = new int[32];
            int hair = rs.getInt("hair");
            int baseColor = rs.getInt("basecolor");
            if (baseColor != -1) {
               this.hairEquip[0] = hair / 10 * 10 + baseColor;
            } else {
               this.hairEquip[0] = hair;
            }

            int drawElfEar = 1;
            if (GameConstants.isArk(this.job)
                  || GameConstants.isMercedes(this.job)
                  || GameConstants.isIllium(this.job)
                  || GameConstants.isAdele(this.job)
                  || GameConstants.isKhali(this.job)) {
               drawElfEar = rs.getInt("draw_elf_ear");
            }

            this.elfEar = drawElfEar > 0 ? 0 : 1;
            this.addColor = rs.getInt("addcolor");
            this.baseProb = rs.getInt("baseprob");
            this.shift = GameConstants.isHoyoung(this.job) ? rs.getInt("shift") : 0;
            PreparedStatement ps2 = con
                  .prepareStatement("SELECT `itemid`,`position` FROM inventoryitems WHERE `characterid` = ?");
            ps2.setInt(1, playerID);
            ResultSet rs2 = ps2.executeQuery();
            List<Pair<Integer, Integer>> itemLists = new ArrayList<>();

            while (rs2.next()) {
               int position = rs2.getInt("position");
               int itemID = rs2.getInt("itemid");
               if (position < 0 && (position <= -1 && position >= -32 || position <= -101 && position >= -132)) {
                  position *= -1;
                  itemLists.add(new Pair<>(position, itemID));
               }
            }

            for (Pair<Integer, Integer> pair : itemLists.stream()
                  .sorted(Comparator.comparingInt(pair -> pair.left))
                  .collect(Collectors.toList())) {
               int pos = pair.left;
               if (pos == 111) {
                  this.cashWeapon = specialItemReplaceOriginID(pair.right);
               }

               if (pos == 11) {
                  this.weapon = pair.right;
               }

               if (pos > 100) {
                  pos -= 100;
               }

               this.hairEquip[pos] = specialItemReplaceOriginID(pair.right);
            }

            rs2.close();
            ps2.close();
         }
      } catch (SQLException var31) {
         var31.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var33 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var34 = null;
            }
         } catch (SQLException var28) {
            var28.printStackTrace();
         }
      }
   }

   public AvatarLook(MapleCharacter chr, boolean isBeta, boolean isTag) {
      this.gender = chr.getGender();
      this.skin = chr.getSkinColor();
      this.job = chr.getJob();
      int face = isBeta ? chr.getSecondFace() : chr.getFace();
      int faceBaseColor = isBeta ? chr.getSecondFaceBaseColor() : chr.getFaceBaseColor();
      int faceAddColor = isBeta ? chr.getSecondFaceAddColor() : chr.getFaceAddColor();
      int faceBaseProb = isBeta ? chr.getSecondFaceBaseProb() : chr.getFaceBaseProb();
      if (faceBaseProb != 0) {
         face = (face / 1000 * 1000 + face % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100 + faceBaseProb;
      }

      this.face = face;
      this.hairEquip = new int[32];
      int hair = isBeta ? chr.getSecondHair() : chr.getHair();
      int hairBaseColor = isBeta ? chr.getSecondBaseColor() : chr.getBaseColor();
      int hairAddColor = isBeta ? chr.getSecondAddColor() : chr.getAddColor();
      int hairBaseProb = isBeta ? chr.getSecondBaseProb() : chr.getBaseProb();
      int mixedHair = hair;
      if (hairBaseProb > 0 && hair < 100000) {
         mixedHair = hair / 10 * 10 * 1000 + hairAddColor * 1000 + hairBaseColor * 100 + hairBaseProb;
      }

      this.hairEquip[0] = mixedHair;
      MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
      Map<Short, Integer> hairEquip = new LinkedHashMap<>();
      Map<Short, Integer> unseenEquip = new LinkedHashMap<>();

      for (Item item : equip.list().stream()
            .sorted(Comparator.comparingInt(Item::getPosition).reversed())
            .collect(Collectors.toCollection(ArrayList::new))) {
         short pos_ = 0;
         if (item.getPosition() <= -1499 && item.getPosition() >= -1509) {
            if (!GameConstants.isZero(chr.getJob()) || !isBeta) {
               continue;
            }

            pos_ = PacketHelper.BetaSlot(item.getPosition());
         } else if (item.getPosition() <= -1300 && item.getPosition() >= -1309) {
            if (!GameConstants.isAngelicBuster(chr.getJob()) || !chr.isDressUp()) {
               continue;
            }

            pos_ = PacketHelper.AngelicBusterSlot(item.getPosition());
         }

         if (item.getPosition() == -10 && isBeta && GameConstants.isZero(chr.getJob())) {
            pos_ = -11;
         } else if (item.getPosition() == -10 && !isBeta && GameConstants.isZero(chr.getJob())
               || item.getPosition() == -11 && isBeta && GameConstants.isZero(chr.getJob())
               || item.getPosition() <= -1 && item.getPosition() > -10 && chr.isDressUp()) {
            continue;
         }

         if (!isBeta || !GameConstants.isZero(chr.getJob()) || isTag
               || PacketHelper.checkZeroClothes(item.getPosition(), chr)) {
            Equip item_ = (Equip) item;
            short pos = pos_ == 0 ? (short) (item.getPosition() * -1) : (short) (pos_ * -1);
            if (pos < 100 && hairEquip.get(pos) == null) {
               String lol = Integer.valueOf(item.getItemId()).toString();
               String ss = lol.substring(0, 3);
               int moru = Integer.parseInt(ss + Integer.valueOf(item_.getFusionAnvil()).toString());
               hairEquip.put(pos, item_.getFusionAnvil() != 0 ? moru : item.getItemId());
            } else if (pos > 100 && pos != 111) {
               pos = (short) (pos - 100);
               if (hairEquip.get(pos) != null) {
                  if (pos_ > 0) {
                     hairEquip.remove(pos);
                  } else {
                     unseenEquip.put(pos, hairEquip.get(pos));
                  }
               }

               String lol = Integer.valueOf(item.getItemId()).toString();
               String ss = lol.substring(0, 3);
               int moru = Integer.parseInt(ss + Integer.valueOf(item_.getFusionAnvil()).toString());
               hairEquip.put(pos, item_.getFusionAnvil() != 0 ? moru : item.getItemId());
            } else if (hairEquip.get(pos) != null) {
               unseenEquip.put(pos, item.getItemId());
            }
         }
      }

      for (Entry<Short, Integer> entry : hairEquip.entrySet()) {
         if (entry.getKey() <= 32) {
            this.hairEquip[entry.getKey()] = specialItemReplaceOriginID(entry.getValue());
         }
      }

      Item cWeapon = equip.getItem((short) -111);
      Item weapon = equip.getItem((short) -11);
      this.cashWeapon = cWeapon != null ? specialItemReplaceOriginID(cWeapon.getItemId()) : 0;
      this.weapon = weapon != null ? weapon.getItemId() : 0;
      Equip e = weapon != null ? (Equip) weapon : null;
      if (e != null && e.getFusionAnvil() > 0) {
         this.weapon /= 10000;
         this.weapon *= 10000;
         this.weapon = this.weapon + e.getFusionAnvil();
      }

      this.elfEar = chr.getDrawElfEar_Look();
      this.addColor = chr.getAddColor();
      this.baseProb = chr.getBaseProb();
      this.shift = GameConstants.isHoyoung(chr.getJob()) ? chr.getShift() : 0;
   }

   public void decode(PacketDecoder packet) {
      this.gender = packet.readByte();
      this.skin = packet.readByte();
      this.face = packet.readInt();
      this.job = packet.readInt();
      this.hairEquip = new int[32];
      this.unseenEquip = new int[32];

      for (byte i = packet.readByte(); i != -1; i = packet.readByte()) {
         this.hairEquip[i] = packet.readInt();
      }

      for (byte i = packet.readByte(); i != -1; i = packet.readByte()) {
         this.unseenEquip[i] = packet.readInt();
      }

      this.cashWeapon = packet.readInt();
      this.weapon = packet.readInt();
      this.subWeapon = packet.readInt();
      this.elfEar = packet.readInt();
      this.unk = packet.readInt();
      this.unk2 = packet.readByte();
      this.unk3 = packet.readInt();
      this.petEquipSlot1 = packet.readInt();
      this.petEquipSlot2 = packet.readInt();
      this.petEquipSlot3 = packet.readInt();
      if (GameConstants.isDemonSlayer(this.job)
            || GameConstants.isDemonAvenger(this.job)
            || GameConstants.isXenon(this.job)
            || GameConstants.isArk(this.job)
            || GameConstants.isHoyoung(this.job)) {
         this.faceAcc = packet.readInt();
         if (this.faceAcc != 0) {
            this.shift = 1;
         }
      } else if (GameConstants.isZero(this.job)) {
         this.isBeta = packet.readByte() > 0;
      }

      this.unk4 = packet.readInt();
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.gender);
      packet.write(this.skin);
      packet.writeInt(this.face);
      packet.writeInt(this.job);
      packet.write(0);
      packet.writeInt(this.hairEquip[0]);

      for (int i = 0; i < 32; i++) {
         int eqp = this.hairEquip[i];
         if (eqp != 0) {
            packet.write(i);
            packet.writeInt(eqp);
         }
      }

      packet.write(-1);

      for (int ix = 0; ix < 32; ix++) {
         int eqp = this.unseenEquip[ix];
         if (eqp != 0) {
            packet.write(ix);
            packet.writeInt(eqp);
         }
      }

      packet.write(-1);
      packet.writeInt(this.cashWeapon);
      packet.writeInt(this.weapon);
      packet.writeInt(this.subWeapon);
      packet.writeInt(this.elfEar);
      packet.writeInt(this.unk);
      packet.write(this.unk2);
      packet.writeInt(this.unk3);
      packet.writeInt(this.petEquipSlot1);
      packet.writeInt(this.petEquipSlot2);
      packet.writeInt(this.petEquipSlot3);
      if (!GameConstants.isDemonSlayer(this.job)
            && !GameConstants.isDemonAvenger(this.job)
            && !GameConstants.isXenon(this.job)
            && !GameConstants.isArk(this.job)
            && !GameConstants.isHoyoung(this.job)) {
         if (GameConstants.isZero(this.job)) {
            packet.write(this.isBeta);
         }
      } else {
         packet.writeInt(this.shift == 0 ? 0 : this.faceAcc);
      }

      packet.writeInt(this.unk4);
   }

   public byte[] packedTo() {
      PacketEncoder packet = new PacketEncoder();
      this.encodePackedCharacterLook(packet);
      return packet.getPacket();
   }

   public void encodePackedCharacterLook(PacketEncoder packet) {
      BitWriter b = new BitWriter((packedCharacterLookBytes - 1) * 8);
      b.writeBits((byte) 1, this.gender);
      b.writeBits((byte) 10, this.skin);
      int face = this.face >= 10000000 ? this.face / 1000 : this.face;
      int f = 0;
      if (face / 1000 > 0) {
         if (this.face >= 10000000) {
            f = face % 1000 % 100;
         } else {
            f = face % 1000;
         }
      }

      b.writeBits((byte) 1, face / 10000 > 2 ? 1 : 0);
      b.writeBits((byte) 10, f);
      int face2 = face;
      if (face >= 10000000) {
         face2 = face / 1000;
      }

      b.writeBits((byte) 4, face2 / 1000 % 10 & 15);
      int hair2 = this.hairEquip[0];
      if (hair2 >= 10000000) {
         hair2 /= 1000;
      }

      b.writeBits((byte) 4, hair2 / 10000);
      b.writeBits((byte) 10, hair2 % 1000);
      b.writeBits((byte) 4, hair2 / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[1]);
      b.writeBits((byte) 3, this.hairEquip[1] / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[2]);
      b.writeBits((byte) 2, this.hairEquip[2] / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[3]);
      b.writeBits((byte) 2, this.hairEquip[3] / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[4]);
      b.writeBits((byte) 2, this.hairEquip[4] / 1000 % 10);
      b.writeBits((byte) 1, this.hairEquip[5] / 10000 % 10 - 4);
      b.writeBitsNullable(this.hairEquip[5]);
      b.writeBits((byte) 4, this.hairEquip[5] / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[6]);
      b.writeBits((byte) 2, this.hairEquip[6] / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[7]);
      b.writeBits((byte) 2, this.hairEquip[7] / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[8]);
      b.writeBits((byte) 2, this.hairEquip[8] / 1000 % 10);
      b.writeBitsNullable(this.hairEquip[9]);
      b.writeBits((byte) 2, this.hairEquip[9] / 1000 % 10);
      int hairEquip = this.hairEquip[10] / 10000;
      if (hairEquip == 134) {
         b.writeBits((byte) 2, 2);
      } else if (hairEquip == 135) {
         b.writeBits((byte) 2, 1);
      } else {
         b.writeBits((byte) 2, 0);
      }

      b.writeBitsNullable(this.hairEquip[10]);
      b.writeBits((byte) 4, this.hairEquip[10] / 1000 % 10);
      if (this.cashWeapon == 0) {
         b.writeBits((byte) 1, 0);
         b.writeBitsNullable(this.weapon);
         b.writeBits((byte) 2, this.weapon / 1000 % 10);
      } else {
         b.writeBits((byte) 1, 1);
         b.writeBitsNullable(this.cashWeapon);
         b.writeBits((byte) 2, this.cashWeapon / 1000 % 10);
      }

      b.writeBits((byte) 8, GameConstants.getWeaponTypeIndex(this.weapon) + 1);
      b.writeBits((byte) 4, this.elfEar);
      int mixHair = 0;
      if (this.hairEquip[0] > 10000000) {
         mixHair = this.hairEquip[0] % 1000;
      }

      int baseColor = mixHair / 100;
      int baseProb = 100 - mixHair % 100;
      b.writeBits((byte) 4, baseColor);
      b.writeBits((byte) 8, baseProb);
      int mixLens = 0;
      if (this.face >= 10000000) {
         mixLens = this.face % 1000;
      }

      b.writeBits((byte) 12, mixLens);
      b.writeBits((byte) 8, this.shift);
      b.writeBits((byte) 8, 0);
      packet.encodeBuffer(b.getData());
      packet.write(packedCharacterLookVersion);
   }

   public static int specialItemReplaceOriginID(int itemID) {
      switch (itemID) {
         case 1009000:
            return 1003867;
         case 1009001:
         case 1009002:
         case 1009003:
         case 1009004:
            return 1003955;
         case 1009005:
         case 1009006:
         case 1009007:
         case 1009008:
         case 1009009:
            return 1005411;
         case 1009010:
         case 1009011:
         case 1009012:
         case 1009013:
         case 1009014:
         case 1009015:
         case 1009016:
            return 1003998;
         case 1009017:
         case 1009018:
         case 1009019:
            return 1004576;
         case 1009020:
         case 1009021:
         case 1009022:
            return 1005143;
         case 1009023:
         case 1009024:
         case 1009025:
            return 1005092;
         case 1009026:
         case 1009027:
            return 1004923;
         case 1009028:
            return 1004948;
         case 1009029:
         case 1009030:
         case 1009031:
            return 1004468;
         case 1009032:
         case 1009033:
         case 1009034:
            return 1005354;
         case 1009035:
         case 1009036:
         case 1009037:
            return 1004327;
         case 1009038:
            return 1005495;
         case 1009039:
         case 1009040:
         case 1009041:
            return 1005112;
         case 1009042:
         case 1009043:
         case 1009044:
            return 1004590;
         case 1009045:
            return 1001036;
         case 1009046:
         case 1009047:
         case 1009048:
            return 1005217;
         case 1009049:
         case 1009050:
         case 1009051:
            return 1005324;
         case 1009052:
         case 1009053:
         case 1009054:
         case 1009055:
         case 1009056:
            return 1005388;
         case 1009057:
            return 1005151;
         case 1009058:
            return 1005431;
         case 1009059:
         case 1009060:
         case 1009061:
         case 1009062:
         case 1009063:
            return 1005153;
         case 1009064:
         case 1009065:
         case 1009066:
         case 1009067:
         case 1009068:
            return 1004541;
         case 1009069:
         case 1009070:
         case 1009071:
            return 1001098;
         case 1009072:
         case 1009073:
         case 1009074:
            return 1005145;
         case 1009078:
         case 1009079:
         case 1009080:
         case 1009081:
            return 1003998;
         case 1009082:
            return 1009082;
         case 1059000:
         case 1059001:
            return 1051390;
         case 1059002:
         case 1059003:
            return 1051432;
         case 1059004:
         case 1059005:
            return 1050362;
         case 1059006:
            return 1051613;
         case 1059007:
         case 1059008:
            return 1053505;
         case 1059009:
         case 1059010:
            return 1053546;
         case 1059011:
         case 1059012:
            return 1050504;
         case 1059013:
         case 1059014:
            return 1051574;
         case 1059015:
         case 1059016:
            return 1051382;
         case 1059017:
         case 1059018:
         case 1059019:
            return 1051456;
         case 1059020:
         case 1059021:
         case 1059022:
         case 1059023:
         case 1059024:
            return 1053572;
         case 1059027:
         case 1059028:
         case 1059029:
            return 1051610;
         case 1059030:
         case 1059031:
            return 1051465;
         case 1059032:
            return 1050488;
         case 1059033:
            return 1051555;
         case 1059034:
            return 1053257;
         case 1059035:
         case 1059036:
         case 1059037:
            return 1050386;
         case 1059038:
            return 1052671;
         case 1059039:
            return 1053547;
         case 1059040:
         case 1059041:
         case 1059042:
            return 1051367;
         case 1059043:
         case 1059044:
         case 1059045:
            return 1051491;
         case 1059046:
         case 1059047:
         case 1059048:
         case 1059049:
            return 1052923;
         case 1079000:
         case 1079001:
         case 1079002:
            return 1073258;
         case 1079003:
            return 1073425;
         case 1079004:
            return 1070099;
         case 1079005:
            return 1071116;
         case 1109000:
            return 1103241;
         case 1709000:
         case 1709001:
            return 1702174;
         case 1709002:
         case 1709003:
         case 1709004:
            return 1702810;
         case 1709005:
         case 1709006:
         case 1709007:
            return 1702856;
         case 1709008:
            return 1702976;
         case 1709009:
         case 1709010:
         case 1709011:
            return 1702625;
         case 1709012:
         case 1709013:
         case 1709014:
            return 1702565;
         case 1709015:
            return 1702721;
         case 1709016:
         case 1709017:
         case 1709018:
            return 1702786;
         default:
            return itemID;
      }
   }
}
