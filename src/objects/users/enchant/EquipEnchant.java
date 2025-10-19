package objects.users.enchant;

import constants.GameConstants;
import database.DBConfig;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.context.SpecialSunday;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleItemInformationProvider;

public class EquipEnchant {
   public List<EquipEnchantScroll> scrolls = new LinkedList<>();

   public EquipEnchant(Equip equip, boolean includeSpecialScrolls, boolean fever) {
      List<EquipEnchantScroll> source = new LinkedList<>();
      Item item = MapleItemInformationProvider.getInstance().getEquipById(equip.getItemId());
      Equip origin = (Equip)item;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (origin.getUpgradeSlots() > 0 && equip.getUpgradeSlots() > 0) {
         if (GameConstants.isGlove(equip.getItemId())) {
            int flag = EquipEnchantMan.filterForJobGloves(equip.getItemId());
            ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_PAD, ItemUpgradeFlag.INC_MAD, ItemUpgradeFlag.INC_PDD};

            for (ItemUpgradeFlag f : flagArray) {
               for (int index = 0; index < 3; index++) {
                  if (f.check(flag)) {
                     EquipEnchantOption option = new EquipEnchantOption();
                     option.setOption(f.getValue(), EquipEnchantMan.getIncATTDEFGloves(ii.getReqLevel(equip.getItemId()), index, f));
                     if (option.flag > 0) {
                        source.add(new EquipEnchantScroll(equip.getItemId(), index, option, ScrollType.UPGRADE, 0, fever));
                     }
                  }
               }
            }
         } else if (GameConstants.isWeapon(equip.getItemId())) {
            int flag = EquipEnchantMan.filterForJobWeapon(equip.getItemId());
            ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_PAD, ItemUpgradeFlag.INC_MAD};
            ItemUpgradeFlag[] flagArray2 = new ItemUpgradeFlag[]{
               ItemUpgradeFlag.INC_STR, ItemUpgradeFlag.INC_DEX, ItemUpgradeFlag.INC_LUK, ItemUpgradeFlag.INC_MHP
            };
            ItemUpgradeFlag[] flagArray3 = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_INT};

            for (ItemUpgradeFlag f : flagArray) {
               for (ItemUpgradeFlag f2 : f == ItemUpgradeFlag.INC_PAD ? flagArray2 : flagArray3) {
                  for (int indexx = 0; indexx < 4; indexx++) {
                     EquipEnchantOption option = new EquipEnchantOption();
                     option.setOption(f.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(equip.getItemId()), indexx));
                     if (f2.check(flag)) {
                        option.setOption(
                           f2.getValue(),
                           EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(equip.getItemId()), indexx) * (f2 == ItemUpgradeFlag.INC_MHP ? 50 : 1)
                        );
                        if (option.flag > 0) {
                           source.add(new EquipEnchantScroll(equip.getItemId(), indexx, option, ScrollType.UPGRADE, 0, fever));
                        }
                     }
                  }
               }
            }
         } else if (EquipEnchantMan.IsArmorCategory(equip.getItemId())) {
            int flag = EquipEnchantMan.filterForJobArmor(equip.getItemId());
            ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{
               ItemUpgradeFlag.INC_STR, ItemUpgradeFlag.INC_INT, ItemUpgradeFlag.INC_DEX, ItemUpgradeFlag.INC_LUK, ItemUpgradeFlag.INC_MHP
            };

            for (ItemUpgradeFlag f : flagArray) {
               for (int indexxx = 0; indexxx < 3; indexxx++) {
                  if (f.check(flag)) {
                     EquipEnchantOption option = new EquipEnchantOption();
                     option.setOption(
                        f.getValue(),
                        f.equals(ItemUpgradeFlag.INC_MHP)
                           ? EquipEnchantMan.getIncPrimaryMHPStatArmor(ii.getReqLevel(equip.getItemId()), indexxx)
                           : EquipEnchantMan.getIncPrimaryStatArmor(ii.getReqLevel(equip.getItemId()), indexxx)
                     );
                     option.setOption(ItemUpgradeFlag.INC_PDD.getValue(), EquipEnchantMan.getIncDEFArmor(ii.getReqLevel(equip.getItemId()), indexxx));
                     if (!f.equals(ItemUpgradeFlag.INC_MHP)) {
                        option.setOption(ItemUpgradeFlag.INC_MHP.getValue(), EquipEnchantMan.getIncMHPArmor(ii.getReqLevel(equip.getItemId()), indexxx));
                     }

                     if (option.flag > 0) {
                        source.add(new EquipEnchantScroll(equip.getItemId(), indexxx, option, ScrollType.UPGRADE, 0, fever));
                     }
                  }
               }
            }

            EquipEnchantOption optionx = new EquipEnchantOption();
            optionx.setOption(ItemUpgradeFlag.INC_STR.getValue(), EquipEnchantMan.getIncAllStatArmor(ii.getReqLevel(equip.getItemId())));
            optionx.setOption(ItemUpgradeFlag.INC_DEX.getValue(), EquipEnchantMan.getIncAllStatArmor(ii.getReqLevel(equip.getItemId())));
            optionx.setOption(ItemUpgradeFlag.INC_INT.getValue(), EquipEnchantMan.getIncAllStatArmor(ii.getReqLevel(equip.getItemId())));
            optionx.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncAllStatArmor(ii.getReqLevel(equip.getItemId())));
            if (optionx.flag > 0) {
               source.add(new EquipEnchantScroll(equip.getItemId(), 2, optionx, ScrollType.UPGRADE, 0, fever));
            }
         } else if (GameConstants.isAndroidHeart(equip.getItemId())) {
            ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_PAD, ItemUpgradeFlag.INC_MAD};

            for (ItemUpgradeFlag f : flagArray) {
               for (int indexxxx = 0; indexxxx < 3; indexxxx++) {
                  EquipEnchantOption optionx = new EquipEnchantOption();
                  optionx.setOption(f.getValue(), indexxxx + 1);
                  if (optionx.flag > 0) {
                     source.add(new EquipEnchantScroll(equip.getItemId(), indexxxx, optionx, ScrollType.UPGRADE, 0, fever));
                  }
               }
            }
         } else if (EquipEnchantMan.IsAccessoryCategory(equip.getItemId())) {
            int flag = EquipEnchantMan.filterForJobArmor(equip.getItemId());
            ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{
               ItemUpgradeFlag.INC_STR, ItemUpgradeFlag.INC_INT, ItemUpgradeFlag.INC_DEX, ItemUpgradeFlag.INC_LUK, ItemUpgradeFlag.INC_MHP
            };

            for (ItemUpgradeFlag f : flagArray) {
               for (int indexxxxx = 0; indexxxxx < 3; indexxxxx++) {
                  if (f.check(flag)) {
                     EquipEnchantOption optionx = new EquipEnchantOption();
                     optionx.setOption(
                        f.getValue(),
                        EquipEnchantMan.getIncPrimaryStatAccessory(ii.getReqLevel(equip.getItemId()), indexxxxx) * (f == ItemUpgradeFlag.INC_MHP ? 50 : 1)
                     );
                     if (optionx.flag > 0) {
                        source.add(new EquipEnchantScroll(equip.getItemId(), indexxxxx, optionx, ScrollType.UPGRADE, 0, fever));
                     }
                  }
               }
            }
         }
      }

      if (includeSpecialScrolls) {
         if ((!DBConfig.isGanglim || equip.getSPGrade() == 0) && origin.getUpgradeSlots() > 0) {
            source.add(new EquipEnchantScroll(equip.getItemId(), 4, new EquipEnchantOption(), ScrollType.INNOCENT, 1, fever));
            source.add(new EquipEnchantScroll(equip.getItemId(), 4, new EquipEnchantOption(), ScrollType.INNOCENT, 4, fever));
         }

         if (origin.getUpgradeSlots() + equip.getViciousHammer() - equip.getUpgradeSlots() - equip.getLevel() > 0) {
            source.add(new EquipEnchantScroll(equip.getItemId(), 5, new EquipEnchantOption(), ScrollType.WHITE, 1, fever));
         }
      }

      this.scrolls = source;
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.scrolls.size());

      for (EquipEnchantScroll scroll : this.scrolls) {
         packet.writeInt(scroll.index);
         packet.writeMapleAsciiString(scroll.name);
         packet.writeInt(scroll.scrollType.type);
         packet.writeInt(scroll.scrollOption);
         packet.writeInt(-1);
         scroll.option.encode(packet);
         packet.writeInt(scroll.cost);
         int afterCost = scroll.cost;
         if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSpellTrace) {
            afterCost /= 2;
         }

         packet.writeInt(afterCost);
         packet.write(scroll.success == 100);
      }
   }
}
