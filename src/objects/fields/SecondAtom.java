package objects.fields;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;

public class SecondAtom {
   private int playerID;
   private List<SecondAtom.Atom> atoms;
   private int showEffect = 0;
   public static AtomicInteger SN = new AtomicInteger(1);

   public SecondAtom(Field map, MapleCharacter player, int skillID, SecondAtom.SecondAtomType type, Skill skill, Point pos) {
      this(map, player, skillID, type, skill, pos, 0);
   }

   public SecondAtom(Field map, MapleCharacter player, int skillID, SecondAtom.SecondAtomType type, Skill skill, Point pos, int etherIndex) {
      this(map, player, skillID, type, skill, pos, etherIndex, 0);
   }

   public SecondAtom(Field map, MapleCharacter player, int skillID, SecondAtom.SecondAtomType type, Skill skill, Point pos, int etherIndex, int targetObjectID) {
      this.playerID = player.getId();
      SecondAtomData data = skill.getSecondAtomData();
      this.atoms = new ArrayList<>();
      if (data != null) {
         List<SecondAtomData.atom> atoms = data.getAtoms();
         int index = 0;

         for (SecondAtomData.atom a : atoms) {
            SecondAtom.Atom atom = new SecondAtom.Atom(map, this.playerID, skillID, SN.getAndAdd(1), type, index++, a, etherIndex);
            atom.initPos(pos);
            atom.setTargetObjectID(targetObjectID);
            this.atoms.add(atom);
            player.addSecondAtom(atom);
         }
      }
   }

   public SecondAtom(int playerID, int skillID, List<SecondAtom.Atom> atoms) {
      this.playerID = playerID;
      this.atoms = atoms;
   }

   public SecondAtom(SecondAtom.Atom atoms) {
      this.playerID = atoms.getPlayerID();
      this.atoms = Collections.singletonList(atoms);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getPlayerID());
      packet.writeInt(this.getAtoms().size());
      this.getAtoms().forEach(a -> a.encode(packet));
      packet.writeInt(this.getShowEffect());
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public List<SecondAtom.Atom> getAtoms() {
      return this.atoms;
   }

   public void setAtoms(List<SecondAtom.Atom> atoms) {
      this.atoms = atoms;
   }

   public int getShowEffect() {
      return this.showEffect;
   }

   public void setShowEffect(int showEffect) {
      this.showEffect = showEffect;
   }

   public static class Atom {
      private int key;
      private SecondAtom.SecondAtomType type;
      private int index;
      private int playerID;
      private int targetObjectID;
      private int createDelay;
      private int enableDelay;
      private int rotate;
      private int skillID;
      private int expire;
      private int attackableCount;
      private Point pos;
      private int angle;
      private List<SecondAtom.Custom> customs;
      private long createTime = System.currentTimeMillis();
      private int unk1;
      private int unk2;
      private int range;
      private int unk4;
      private int unk5;

      public Atom(Field map, int playerID, int skillID, int key, SecondAtom.SecondAtomType type, int index, SecondAtomData.atom atom, Point pos) {
         this(map, playerID, skillID, key, type, index, atom);
         this.pos = pos;
      }

      public Atom(Field map, int playerID, int skillID, int key, SecondAtom.SecondAtomType type, int index, SecondAtomData.atom atom) {
         this(map, playerID, skillID, key, type, index, atom, 0);
      }

      public Atom(Field map, int playerID, int skillID, int key, SecondAtom.SecondAtomType type, int index, SecondAtomData.atom atom, int etherIndex) {
         this.playerID = playerID;
         this.key = key;
         this.type = type;
         this.index = index;
         this.customs = new ArrayList<>();
         if (atom != null) {
            this.enableDelay = atom.getEnableDelay();
            this.rotate = atom.getRotate();
            this.skillID = skillID;
            this.expire = atom.getExpire();
            this.attackableCount = atom.getAttackableCount();
            if (type == SecondAtom.SecondAtomType.Order) {
               this.angle = atom.getCustoms().get(etherIndex).getValue();
               this.pos = atom.getExtraPos().get(etherIndex).getValue();
            } else {
               this.pos = atom.getPos();
            }

            this.customs = atom.getCustoms();
         }
      }

      public Atom(
         Field map, int playerID, int skillID, int key, SecondAtom.SecondAtomType type, int index, int enableDelay, int expire, int attackableCount, Point pos
      ) {
         this.playerID = playerID;
         this.key = key;
         this.type = type;
         this.index = index;
         this.customs = new ArrayList<>();
         this.enableDelay = enableDelay;
         this.skillID = skillID;
         this.expire = expire;
         this.attackableCount = attackableCount;
         this.pos = pos;
      }

      public void initPos(Point pos) {
         Point p = new Point(pos);
         p.x = p.x + this.pos.x;
         p.y = p.y + this.pos.y;
         this.pos = p;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.getKey());
         packet.writeInt(this.getUnk4());
         packet.writeInt(this.getType().type);
         packet.writeInt(this.getIndex());
         packet.writeInt(this.getPlayerID());
         packet.writeInt(this.getTargetObjectID());
         packet.writeInt(this.getCreateDelay());
         packet.writeInt(this.getEnableDelay());
         packet.writeInt(this.getRotate());
         packet.writeInt(this.getSkillID());
         packet.writeInt(0);
         packet.writeInt(this.getRange());
         packet.writeInt(this.getExpire());
         packet.writeInt(this.getAngle());
         packet.writeInt(this.getAttackableCount());
         packet.writeInt(this.getUnk1());
         packet.writeInt(this.getUnk5());
         packet.writeInt(this.getPos().x);
         packet.writeInt(this.getPos().y);
         packet.write(this.getUnk2());
         boolean unks = false;
         int unkint = unks ? 1 : 0;
         packet.write(unks);
         if (unks) {
            packet.writeInt(unkint);
         }

         if (unkint == 1) {
            packet.writeInt((new Point(100, 100)).x);
            packet.writeInt((new Point(100, 100)).y);
         }

         packet.write(0);
         packet.writeInt(this.getCustoms().size());
         this.getCustoms().forEach(c -> packet.writeInt(c.value));
      }

      public int getKey() {
         return this.key;
      }

      public void setKey(int key) {
         this.key = key;
      }

      public SecondAtom.SecondAtomType getType() {
         return this.type;
      }

      public void setType(SecondAtom.SecondAtomType type) {
         this.type = type;
      }

      public int getIndex() {
         return this.index;
      }

      public void setIndex(int index) {
         this.index = index;
      }

      public int getPlayerID() {
         return this.playerID;
      }

      public void setPlayerID(int playerID) {
         this.playerID = playerID;
      }

      public int getEnableDelay() {
         return this.enableDelay;
      }

      public void setEnableDelay(int enableDelay) {
         this.enableDelay = enableDelay;
      }

      public int getRotate() {
         return this.rotate;
      }

      public void setRotate(int rotate) {
         this.rotate = rotate;
      }

      public int getSkillID() {
         return this.skillID;
      }

      public void setSkillID(int skillID) {
         this.skillID = skillID;
      }

      public int getExpire() {
         return this.expire;
      }

      public void setExpire(int expire) {
         this.expire = expire;
      }

      public int getAttackableCount() {
         return this.attackableCount;
      }

      public void setAttackableCount(int attackableCount) {
         this.attackableCount = attackableCount;
      }

      public Point getPos() {
         return this.pos;
      }

      public void setPos(Point pos) {
         this.pos = pos;
      }

      public List<SecondAtom.Custom> getCustoms() {
         return this.customs;
      }

      public void setCustoms(List<SecondAtom.Custom> customs) {
         this.customs = customs;
      }

      public int getAngle() {
         return this.angle;
      }

      public void setAngle(int angle) {
         this.angle = angle;
      }

      public long getCreateTime() {
         return this.createTime;
      }

      public void setCreateTime(long createTime) {
         this.createTime = createTime;
      }

      public int getUnk1() {
         return this.unk1;
      }

      public void setUnk1(int unk1) {
         this.unk1 = unk1;
      }

      public int getUnk2() {
         return this.unk2;
      }

      public void setUnk2(int unk2) {
         this.unk2 = unk2;
      }

      public int getTargetObjectID() {
         return this.targetObjectID;
      }

      public void setTargetObjectID(int targetObjectID) {
         this.targetObjectID = targetObjectID;
      }

      public int getRange() {
         return this.range;
      }

      public void setRange(int range) {
         this.range = range;
      }

      public int getCreateDelay() {
         return this.createDelay;
      }

      public void setCreateDelay(int createDelay) {
         this.createDelay = createDelay;
      }

      public int getUnk4() {
         return this.unk4;
      }

      public void setUnk4(int unk4) {
         this.unk4 = unk4;
      }

      public int getUnk5() {
         return this.unk5;
      }

      public void setUnk5(int unk5) {
         this.unk5 = unk5;
      }
   }

   public static class Custom {
      private int index;
      private int value;

      public Custom(int index, int value) {
         this.index = index;
         this.value = value;
      }

      public int getIndex() {
         return this.index;
      }

      public int getValue() {
         return this.value;
      }
   }

   public static class ExtraPos {
      private int index;
      private Point value;

      public ExtraPos(int index, Point value) {
         this.index = index;
         this.value = value;
      }

      public int getIndex() {
         return this.index;
      }

      public Point getValue() {
         return this.value;
      }
   }

   public static enum SecondAtomType {
      Shard(0),
      Creation1(1),
      Creation2(2),
      Creation3(3),
      Creation4(4),
      Creation5(5),
      Creation6(6),
      Order(7),
      Ruin(8),
      PhotonRay(9),
      MecaCarrier(10),
      RedEnergy(11),
      SalamanderMischief(12),
      DragonBlazeFlame(13),
      RoyalKnights(14),
      DarknessAuraDrain(15),
      ScatteringShot(16),
      DragonFang(17),
      GripOfAgony(18),
      ThanatosDescent(19),
      DragonVein(20),
      SunriseWell(21),
      SunlightSprout(23),
      SlumberAwakening_1(24),
      SlumberAwakening_2(25),
      SlumberAwakening_3(26),
      SlumberAwakening_4(27),
      LargeStretch_2(28),
      LargeStretch_3(29),
      LargeStretch_4(30),
      WhereTheRiverFlows(31),
      WhereTheRiverFlows2(32),
      ShowdownChallenge(33),
      AssembleCrew(34),
      MegiddoFlame(35),
      SerpentAssaultEnrage(36),
      TriumphFeather(37),
      PoisonChain(38),
      FlashMirage(39),
      StormWhim(41),
      CosmikBurst(42),
      Resonate(43),
      SixResonate(44),
      unk45(45),
      ShadowThrowingStars(46),
      SpiritAura(47),
      SpiritAura2(48),
      SpiritAura3(49),
      UnlimitedCrystal(50),
      FrozenLightning(51),
      ZeroTimePieceAlpha(52),
      AdditionalBlastCurseArrow(53),
      ZeroTimePieceBeta(54),
      UnlimitedCrystal2(55),
      FrozenLightning2(56),
      FrozenLightning3(57),
      ShardVI(58);

      private int type;

      private SecondAtomType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static SecondAtom.SecondAtomType getSecondAtomType(int type) {
         for (SecondAtom.SecondAtomType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }
}
