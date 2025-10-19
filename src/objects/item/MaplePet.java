package objects.item;

import database.DBConfig;
import database.DBConnection;
import database.DBEventManager;
import database.DBSelectionKey;
import database.callback.DBCallback;
import java.awt.Point;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.movepath.AbsoluteLifeMovement;
import objects.movepath.LifeMovement;
import objects.movepath.LifeMovementFragment;
import objects.users.MapleCharacter;

public class MaplePet implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private String name;
   private int Fh = 0;
   private int stance = 0;
   private int color = -1;
   private int petitemid;
   private int secondsLeft = 0;
   private int[] buffSkills = new int[]{0, 0};
   private long uniqueid = 0L;
   private Point pos;
   private byte fullness = 100;
   private byte level = 1;
   private byte summoned = 0;
   private short inventorypos = 0;
   private short closeness = 0;
   private short flags = 0;
   private short size = 100;
   private boolean changed = false;
   private short wonderGrade = -1;
   private long lastGiantPetBuffTime = 0L;
   private List<Integer> lootException = new ArrayList<>();

   private MaplePet(int petitemid, long uniqueid) {
      this.petitemid = petitemid;
      this.uniqueid = uniqueid;
   }

   private MaplePet(int petitemid, long uniqueid, short inventorypos) {
      this.petitemid = petitemid;
      this.uniqueid = uniqueid;
      this.inventorypos = inventorypos;
   }

   public static final MaplePet loadFromDb(int itemid, long petid, short inventorypos) {
      DBConnection db = new DBConnection();

      try {
         MaplePet pet;
         try (Connection con = DBConnection.getConnection()) {
            MaplePet ret = new MaplePet(itemid, petid, inventorypos);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE petid = ?");
            ps.setLong(1, petid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               pet = createPet(itemid, petid);
               rs.close();
               ps.close();
               return pet;
            }

            ret.setName(rs.getString("name"));
            ret.setCloseness(rs.getShort("closeness"));
            ret.setLevel(rs.getByte("level"));
            ret.setFullness(rs.getByte("fullness"));
            ret.setSecondsLeft(rs.getInt("seconds"));
            ret.setFlags(rs.getShort("flags"));
            if (DBConfig.isGanglim) {
               ret.setFlags(-1);
            }

            ret.setBuffSkill(0, rs.getInt("petbuff"));
            ret.setBuffSkill(1, rs.getInt("petbuff2"));
            ret.setPetSize(DBConfig.isGanglim ? rs.getShort("size") : 100);
            ret.setWonderGrade(rs.getShort("wonderGrade"));
            ret.changed = false;
            rs.close();
            ps.close();
            pet = ret;
         }

         return pet;
      } catch (SQLException var13) {
         Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, var13);
         return null;
      }
   }

   public final void saveToDb() {
      if (this.changed) {
         DBEventManager.getNextProcessor()
            .addQuery(
               DBSelectionKey.INSERT_OR_UPDATE,
               "UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, seconds = ?, flags = ?, petbuff = ?, petbuff2 = ?, size = ?, wonderGrade = ? WHERE petid = ?",
               new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     ps.setString(1, MaplePet.this.name);
                     ps.setByte(2, MaplePet.this.level);
                     ps.setShort(3, MaplePet.this.closeness);
                     ps.setByte(4, MaplePet.this.fullness);
                     ps.setInt(5, MaplePet.this.secondsLeft);
                     ps.setShort(6, MaplePet.this.flags);
                     ps.setInt(7, MaplePet.this.buffSkills[0]);
                     ps.setInt(8, MaplePet.this.buffSkills[1]);
                     ps.setShort(9, MaplePet.this.size);
                     ps.setShort(10, MaplePet.this.wonderGrade);
                     ps.setLong(11, MaplePet.this.uniqueid);
                  }
               }
            );
      }
   }

   public static final MaplePet createPet(int itemid, long uniqueid) {
      return createPet(itemid, MapleItemInformationProvider.getInstance().getName(itemid), 1, 0, 100, uniqueid, itemid == 5000054 ? 18000 : 0, (short)0);
   }

   public static final MaplePet createPet(int itemid, String name, int level, int closeness, int fullness, long uniqueid, int secondsLeft, short flag) {
      if (uniqueid <= -1L) {
         uniqueid = MapleInventoryIdentifier.getInstance();
      }

      DBConnection db = new DBConnection();
      int buffFlag = 0;
      int wonderGrade = -1;

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement pse = con.prepareStatement(
            "INSERT INTO pets (petid, name, level, closeness, fullness, seconds, flags, wonderGrade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
         );
         pse.setLong(1, uniqueid);
         pse.setString(2, name);
         pse.setByte(3, (byte)level);
         pse.setShort(4, (short)closeness);
         pse.setByte(5, (byte)fullness);
         pse.setInt(6, secondsLeft);
         PetData data = PetDataFactory.getPetData(itemid);
         if (DBConfig.isGanglim) {
            buffFlag = -1;
         } else if (data != null) {
            if (data.getPickupItem() != 0) {
               buffFlag |= MaplePet.PetFlag.ITEM_PICKUP.getValue();
            }

            if (data.getSwwepForDrop() != 0) {
               buffFlag |= MaplePet.PetFlag.AUTO_PICKUP.getValue();
            }

            if (data.getAutoBuff() != 0) {
               buffFlag |= MaplePet.PetFlag.PET_BUFF.getValue();
            }

            if (data.getConsumeHP() != 0) {
               buffFlag |= MaplePet.PetFlag.HP_CHARGE.getValue();
            }

            if (data.getConsumeMP() != 0) {
               buffFlag |= MaplePet.PetFlag.MP_CHARGE.getValue();
            }
         }

         wonderGrade = data.getWonderGrade();
         pse.setShort(7, (short)buffFlag);
         pse.setShort(8, (short)wonderGrade);
         pse.executeUpdate();
         pse.close();
      } catch (SQLException var17) {
         var17.printStackTrace();
         return null;
      }

      MaplePet pet = new MaplePet(itemid, uniqueid);
      pet.setName(name);
      pet.setLevel(level);
      pet.setFullness(fullness);
      pet.setCloseness(closeness);
      pet.setFlags(buffFlag);
      pet.setSecondsLeft(secondsLeft);
      pet.setWonderGrade((short)wonderGrade);
      return pet;
   }

   public final String getName() {
      return this.name == null ? "" : this.name;
   }

   public final void setName(String name) {
      this.name = name;
      this.changed = true;
   }

   public final boolean getSummoned() {
      return this.summoned > 0;
   }

   public final byte getSummonedValue() {
      return this.summoned;
   }

   public final void setSummoned(byte summoned) {
      this.summoned = summoned;
   }

   public final short getInventoryPosition() {
      return this.inventorypos;
   }

   public final void setInventoryPosition(short inventorypos) {
      this.inventorypos = inventorypos;
   }

   public long getUniqueId() {
      return this.uniqueid;
   }

   public final short getCloseness() {
      return this.closeness;
   }

   public final void setCloseness(int closeness) {
      this.closeness = (short)closeness;
      this.changed = true;
   }

   public final byte getLevel() {
      return this.level;
   }

   public final void setLevel(int level) {
      this.level = (byte)level;
      this.changed = true;
   }

   public final byte getFullness() {
      return this.fullness;
   }

   public final void setFullness(int fullness) {
      this.fullness = (byte)fullness;
      this.changed = true;
   }

   public final short getFlags() {
      return this.flags;
   }

   public final void setFlags(int fffh) {
      short newFlag = (short)fffh;
      this.flags = newFlag;
      this.changed = true;
   }

   public final short getPetSize() {
      return this.size;
   }

   public final void setPetSize(short size) {
      this.size = size;
      this.changed = true;
   }

   public void addPetSize(short size) {
      this.size += size;
      this.changed = true;
   }

   public final int getFh() {
      return this.Fh;
   }

   public final void setFh(int Fh) {
      this.Fh = Fh;
   }

   public final Point getPos() {
      return this.pos;
   }

   public final void setPos(Point pos) {
      this.pos = pos;
   }

   public final int getStance() {
      return this.stance;
   }

   public final void setStance(int stance) {
      this.stance = stance;
   }

   public final int getColor() {
      return this.color;
   }

   public final void setColor(int color) {
      this.color = color;
   }

   public final int getPetItemId() {
      return this.petitemid;
   }

   public final boolean canConsume(int itemId) {
      MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();

      for (int petId : mii.getItemEffect(itemId).getPetsCanConsume()) {
         if (petId == this.petitemid) {
            return true;
         }
      }

      return false;
   }

   public final void updatePosition(List<LifeMovementFragment> movement) {
      for (LifeMovementFragment move : movement) {
         if (move instanceof LifeMovement) {
            if (move instanceof AbsoluteLifeMovement) {
               this.setPos(move.getPosition());
            }

            this.setStance(((LifeMovement)move).getNewstate());
         }
      }
   }

   public final int getSecondsLeft() {
      return this.secondsLeft;
   }

   public final void setSecondsLeft(int sl) {
      this.secondsLeft = sl;
      this.changed = true;
   }

   public final short getWonderGrade() {
      return this.wonderGrade;
   }

   public final void setWonderGrade(short wonderGrade) {
      this.wonderGrade = wonderGrade;
      this.changed = true;
   }

   public int[] getBuffSkills() {
      return this.buffSkills;
   }

   public void setBuffSkill(int slot, int buffSkill) {
      this.getBuffSkills()[slot] = buffSkill;
      this.changed = true;
   }

   public void setBuffSkills(int[] buffSkills) {
      this.buffSkills = buffSkills;
      this.changed = true;
   }

   public boolean isMultiPet(int petid) {
      switch (petid) {
         case 5000065:
         case 5000072:
         case 5000083:
         case 5000206:
         case 5000210:
         case 5000216:
         case 5000228:
         case 5000229:
         case 5000230:
         case 5000231:
         case 5000232:
         case 5000233:
         case 5000239:
         case 5000316:
         case 5000341:
            return true;
         default:
            return false;
      }
   }

   public byte[] giantPetBuff() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.GIANT_PET_BUFF.getValue());
      packet.writeInt(this.getPetItemId());
      packet.writeInt(2023091);
      return packet.getPacket();
   }

   public byte[] petModified(MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PET_MODIFIED.getValue());
      packet.writeInt(chr.getId());
      packet.writeInt(chr.getPetIndex(this));
      packet.writeShort(this.size);
      packet.write(0);
      packet.write(0);
      return packet.getPacket();
   }

   public long getLastGiantPetBuffTime() {
      return this.lastGiantPetBuffTime;
   }

   public void setLastGiantPetBuffTime(long lastGiantPetBuffTime) {
      this.lastGiantPetBuffTime = lastGiantPetBuffTime;
   }

   public List<Integer> getLootException() {
      return this.lootException;
   }

   public static enum PetFlag {
      ITEM_PICKUP(1, 5190000, 5191000),
      EXPAND_PICKUP(2, 5190002, 5191002),
      AUTO_PICKUP(4, 5190003, 5191003),
      UNPICKABLE(8, 5190005, -1),
      LEFTOVER_PICKUP(16, 5190004, 5191004),
      HP_CHARGE(32, 5190001, 5191001),
      MP_CHARGE(64, 5190006, -1),
      PET_BUFF(128, 5190010, -1),
      PET_TRAINING(256, 5190011, -1),
      PET_GIANT(512, 5190012, 5190014),
      PET_SHOP(1024, 5190013, -1);

      private final int i;
      private final int item;
      private final int remove;

      private PetFlag(int i, int item, int remove) {
         this.i = i;
         this.item = item;
         this.remove = remove;
      }

      public final int getValue() {
         return this.i;
      }

      public final boolean check(int flag) {
         return (flag & this.i) == this.i;
      }

      public static final MaplePet.PetFlag getByAddId(int itemId) {
         for (MaplePet.PetFlag flag : values()) {
            if (flag.item == itemId) {
               return flag;
            }
         }

         return null;
      }

      public static final MaplePet.PetFlag getByDelId(int itemId) {
         for (MaplePet.PetFlag flag : values()) {
            if (flag.remove == itemId) {
               return flag;
            }
         }

         return null;
      }
   }
}
