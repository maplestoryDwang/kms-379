package objects.users.enchant;

import network.encode.PacketEncoder;
import objects.item.Equip;

public class EquipEnchantOption {
   public int flag;
   public int incPAD;
   public int incMAD;
   public int incSTR;
   public int incDEX;
   public int incINT;
   public int incLUK;
   public int incPDD;
   public int incMDD;
   public int incMHP;
   public int incMMP;
   public int incACC;
   public int incEVA;
   public int incJump;
   public int incSpeed;

   public void setOption(int flag, int value) {
      if (value > 0) {
         this.flag |= flag;
         if (ItemUpgradeFlag.INC_PAD.check(flag)) {
            this.incPAD = value;
         }

         if (ItemUpgradeFlag.INC_MAD.check(flag)) {
            this.incMAD = value;
         }

         if (ItemUpgradeFlag.INC_STR.check(flag)) {
            this.incSTR = value;
         }

         if (ItemUpgradeFlag.INC_DEX.check(flag)) {
            this.incDEX = value;
         }

         if (ItemUpgradeFlag.INC_INT.check(flag)) {
            this.incINT = value;
         }

         if (ItemUpgradeFlag.INC_LUK.check(flag)) {
            this.incLUK = value;
         }

         if (ItemUpgradeFlag.INC_PDD.check(flag)) {
            this.incPDD = value;
         }

         if (ItemUpgradeFlag.INC_MDD.check(flag)) {
            this.incMDD = value;
         }

         if (ItemUpgradeFlag.INC_MHP.check(flag)) {
            this.incMHP = value;
         }

         if (ItemUpgradeFlag.INC_ACC.check(flag)) {
            this.incACC = value;
         }

         if (ItemUpgradeFlag.INC_EVA.check(flag)) {
            this.incEVA = value;
         }

         if (ItemUpgradeFlag.INC_JUMP.check(flag)) {
            this.incJump = value;
         }

         if (ItemUpgradeFlag.INC_SPEED.check(flag)) {
            this.incSpeed = value;
         }
      }
   }

   public void applyUpgrade(Equip equip) {
      for (ItemUpgradeFlag flag : ItemUpgradeFlag.values()) {
         if (flag.check(this.flag)) {
            switch (flag) {
               case INC_PAD:
                  equip.setWatk((short)(equip.getWatk() + this.incPAD));
                  break;
               case INC_MAD:
                  equip.setMatk((short)(equip.getMatk() + this.incMAD));
                  break;
               case INC_STR:
                  equip.setStr((short)(equip.getStr() + this.incSTR));
                  break;
               case INC_DEX:
                  equip.setDex((short)(equip.getDex() + this.incDEX));
                  break;
               case INC_INT:
                  equip.setInt((short)(equip.getInt() + this.incINT));
                  break;
               case INC_LUK:
                  equip.setLuk((short)(equip.getLuk() + this.incLUK));
                  break;
               case INC_PDD:
                  equip.setWdef((short)(equip.getWdef() + this.incPDD));
                  break;
               case INC_MDD:
                  equip.setMdef((short)(equip.getMdef() + this.incMDD));
                  break;
               case INC_MHP:
                  equip.setHp((short)(equip.getHp() + this.incMHP));
                  break;
               case INC_MMP:
                  equip.setMp((short)(equip.getMp() + this.incMMP));
                  break;
               case INC_ACC:
                  equip.setAcc((short)(equip.getAcc() + this.incACC));
                  break;
               case INC_EVA:
                  equip.setAvoid((short)(equip.getAvoid() + this.incEVA));
                  break;
               case INC_JUMP:
                  equip.setJump((short)(equip.getJump() + this.incJump));
                  break;
               case INC_SPEED:
                  equip.setSpeed((short)(equip.getSpeed() + this.incSpeed));
            }
         }
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.flag);

      for (ItemUpgradeFlag flag : ItemUpgradeFlag.values()) {
         if ((this.flag & flag.getValue()) != 0) {
            int value = 0;
            switch (flag) {
               case INC_PAD:
                  value = this.incPAD;
                  break;
               case INC_MAD:
                  value = this.incMAD;
                  break;
               case INC_STR:
                  value = this.incSTR;
                  break;
               case INC_DEX:
                  value = this.incDEX;
                  break;
               case INC_INT:
                  value = this.incINT;
                  break;
               case INC_LUK:
                  value = this.incLUK;
                  break;
               case INC_PDD:
                  value = this.incPDD;
                  break;
               case INC_MDD:
                  value = this.incMDD;
                  break;
               case INC_MHP:
                  value = this.incMHP;
                  break;
               case INC_MMP:
                  value = this.incMMP;
                  break;
               case INC_ACC:
                  value = this.incACC;
                  break;
               case INC_EVA:
                  value = this.incEVA;
                  break;
               case INC_JUMP:
                  value = this.incJump;
                  break;
               case INC_SPEED:
                  value = this.incSpeed;
            }

            packet.writeInt(value);
         }
      }
   }

   @Override
   public String toString() {
      return "incSTR : "
         + this.incSTR
         + ", incDEX : "
         + this.incDEX
         + ", incINT"
         + this.incINT
         + ", incLUK : "
         + this.incLUK
         + ", incPAD : "
         + this.incPAD
         + ", incMAD : "
         + this.incMAD
         + ", incPDD : "
         + this.incPDD
         + ", incMDD : "
         + this.incMDD
         + ", inc incMHP : "
         + this.incMHP
         + ", incMMP : "
         + this.incMMP
         + ", incACC : "
         + this.incACC
         + ", incEVA : "
         + this.incEVA
         + ", incJUMP : "
         + this.incJump
         + ", incSPEED : "
         + this.incSpeed;
   }
}
