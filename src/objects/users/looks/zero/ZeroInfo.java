package objects.users.looks.zero;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;

public class ZeroInfo {
   private boolean isBeta;
   private int mixAddHairColor;
   private int mixBaseHairColor;
   private int mixHairBaseProb;
   private int subFace;
   private int subHair;
   private int subHP;
   private int subMHP;
   private int subMP;
   private int subMMP;
   private int calcSubMHP;
   private int calcSubMMP;
   private int subSkin;
   private int zeroLinkCashPart;

   public void initZeroInfo(MapleCharacter player) {
      this.subHP = (int)player.getStat().getSecondHp();
      this.subMP = (int)player.getStat().getMp();
      if (this.isBeta) {
         this.calcSubMHP = (int)player.getStat().getCurrentMaxHp();
      } else {
         this.calcSubMHP = (int)player.getStat().getCurrentSubMaxHp();
      }

      this.subMHP = (int)player.getStat().getMaxHp();
      this.subMMP = (int)player.getStat().getCurrentMaxMp(player);
      this.calcSubMMP = (int)player.getStat().getCurrentMaxMp(player);
      if (!this.isBeta && player.getSkillLevel(101100203) > 0) {
         SecondaryStatEffect reinForceBody = SkillFactory.getSkill(101100203).getEffect(player.getSkillLevel(101100203));
         this.calcSubMMP = this.calcSubMMP + this.calcSubMMP * reinForceBody.getMDF() / 100;
      }

      if (this.subHP > this.calcSubMHP) {
         this.subHP = this.calcSubMHP;
      }

      if (this.subMP != this.calcSubMMP) {
         this.subMP = this.calcSubMMP;
      }

      this.subSkin = player.getSecondSkinColor();
      this.subHair = player.getSecondHair();
      this.subFace = player.getSecondFace();
      this.mixAddHairColor = player.getSecondAddColor();
      this.mixBaseHairColor = player.getSecondBaseColor();
      this.mixHairBaseProb = player.getSecondBaseProb();
   }

   public void updateHPMP(MapleCharacter player) {
   }

   public boolean isBeta() {
      return this.isBeta;
   }

   public void setBeta(boolean beta) {
      this.isBeta = beta;
   }

   public int getMixAddHairColor() {
      return this.mixAddHairColor;
   }

   public void setMixAddHairColor(int mixAddHairColor) {
      this.mixAddHairColor = mixAddHairColor;
   }

   public int getMixBaseHairColor() {
      return this.mixBaseHairColor;
   }

   public void setMixBaseHairColor(int mixBaseHairColor) {
      this.mixBaseHairColor = mixBaseHairColor;
   }

   public int getMixHairBaseProb() {
      return this.mixHairBaseProb;
   }

   public void setMixHairBaseProb(int mixHairBaseProb) {
      this.mixHairBaseProb = mixHairBaseProb;
   }

   public int getSubFace(MapleCharacter player) {
      int faceBaseColor = player.getSecondFaceBaseColor();
      int faceAddColor = player.getSecondFaceAddColor();
      int faceBaseProb = player.getSecondFaceBaseProb();
      int face = this.subFace;
      if (faceBaseProb != 0) {
         face = (face / 1000 * 1000 + face % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100 + faceBaseProb;
      }

      return face;
   }

   public int getSubFace() {
      return this.subFace;
   }

   public void setSubFace(int subFace) {
      this.subFace = subFace;
   }

   public int getSubHair() {
      return this.subHair;
   }

   public void setSubHair(int subHair) {
      this.subHair = subHair;
   }

   public int getSubHP() {
      return this.subHP;
   }

   public void setSubHP(int subHP) {
      this.subHP = subHP;
   }

   public int getSubMHP() {
      return this.subMHP;
   }

   public void setSubMHP(int subMHP) {
      this.subMHP = subMHP;
   }

   public int getSubMP() {
      return this.subMP;
   }

   public void setSubMP(int subMP) {
      this.subMP = subMP;
   }

   public int getSubMMP() {
      return this.subMMP;
   }

   public void setSubMMP(int subMMP) {
      this.subMMP = subMMP;
   }

   public int getSubSkin() {
      return this.subSkin;
   }

   public void setSubSkin(int subSkin) {
      this.subSkin = subSkin;
   }

   public int getZeroLinkCashPart() {
      return this.zeroLinkCashPart;
   }

   public void setZeroLinkCashPart(int zeroLinkCashPart) {
      this.zeroLinkCashPart = zeroLinkCashPart;
   }

   public void sendUpdateZeroInfo(MapleCharacter player, ZeroInfoFlag... flag) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ZERO_INFO.getValue());
      int f = 0;

      for (ZeroInfoFlag f_ : flag) {
         f |= f_.getFlag();
      }

      ZeroInfoFlag updateFlag = ZeroInfoFlag.getFlag(f);
      this.encode(player, updateFlag, packet);
      player.send(packet.getPacket());
   }

   public void encode(MapleCharacter player, ZeroInfoFlag flag, PacketEncoder packet) {
      packet.writeShort(flag.getFlag());
      if (flag.hasFlag(ZeroInfoFlag.IsBeta)) {
         packet.write(this.isBeta);
      }

      if (flag.hasFlag(ZeroInfoFlag.SubHP)) {
         packet.writeInt(this.getSubHP());
      }

      if (flag.hasFlag(ZeroInfoFlag.SubMP)) {
         packet.writeInt(this.getSubMP());
      }

      if (flag.hasFlag(ZeroInfoFlag.SubSkin)) {
         packet.write(this.getSubSkin());
      }

      if (flag.hasFlag(ZeroInfoFlag.SubHair)) {
         if (this.getMixBaseHairColor() == -1) {
            packet.writeInt(this.getSubHair());
         } else {
            int hair = this.getSubHair();
            if (hair >= 100000) {
               packet.writeInt(hair);
            } else {
               int baseColor = this.getMixBaseHairColor();
               int addColor = this.getMixAddHairColor();
               int baseProb = this.getMixHairBaseProb();
               int mixHair = hair / 10 * 10 * 1000 + baseColor * 1000 + addColor * 100 + baseProb;
               packet.writeInt(mixHair);
            }
         }
      }

      if (flag.hasFlag(ZeroInfoFlag.SubFace)) {
         packet.writeInt(this.getSubFace(player));
      }

      if (flag.hasFlag(ZeroInfoFlag.SubMHP)) {
         packet.writeInt(this.getCalcSubMHP());
      }

      if (flag.hasFlag(ZeroInfoFlag.SubMMP)) {
         packet.writeInt(this.getCalcSubMMP());
      }

      if (flag.hasFlag(ZeroInfoFlag.LinkCash)) {
         packet.writeLong(this.getZeroLinkCashPart());
      }
   }

   public int getCalcSubMHP() {
      return this.calcSubMHP;
   }

   public void setCalcSubMHP(int calcSubMHP) {
      this.calcSubMHP = calcSubMHP;
   }

   public int getCalcSubMMP() {
      return this.subMMP;
   }

   public void setCalcSubMMP(int calcSubMMP) {
      this.calcSubMMP = calcSubMMP;
   }
}
