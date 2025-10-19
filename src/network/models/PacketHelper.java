package network.models;

import constants.GameConstants;
import constants.HexaMatrixConstants;
import constants.QuestExConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import network.encode.PacketEncoder;
import objects.androids.Android;
import objects.context.ReportLogEntry;
import objects.item.Equip;
import objects.item.IntensePowerCrystal;
import objects.item.Item;
import objects.item.ItemPot;
import objects.item.MapleInventory;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.item.MapleRing;
import objects.movepath.LifeMovementFragment;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.quest.QuestEx;
import objects.shop.AbstractPlayerStore;
import objects.shop.IMaplePlayerShop;
import objects.shop.MapleMiniGame;
import objects.shop.MapleShop;
import objects.shop.MapleShopItem;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleCoolDownValueHolder;
import objects.users.MapleTrait;
import objects.users.MobCollectionEx;
import objects.users.enchant.EquipStat;
import objects.users.enchant.ItemStateFlag;
import objects.users.jobs.hero.Phantom;
import objects.users.looks.mannequin.Mannequin;
import objects.users.looks.zero.ZeroInfo;
import objects.users.looks.zero.ZeroInfoFlag;
import objects.users.looks.zero.ZeroLinkCashPartFlag;
import objects.users.potential.CharacterPotentialHolder;
import objects.users.skills.SkillEncode;
import objects.users.skills.VCore;
import objects.users.skills.VMatrixSlot;
import objects.users.stats.CTS;
import objects.users.stats.HexaCore;
import objects.utils.HexTool;
import objects.utils.Pair;
import objects.utils.StringUtil;
import objects.utils.Triple;

public class PacketHelper {
   public static final long FT_UT_OFFSET = 116444592000000000L;
   public static final long MAX_TIME = 150842304000000000L;
   public static final long ZERO_TIME = 94354848000000000L;
   public static final long PERMANENT = 150841440000000000L;
   public static final long ZERO_TIME_REVERSE = -153052018564450501L;
   public static int[] g_anWeaponType = new int[] {
         0, 30, 31, 32, 33, 37, 38, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 39, 34, 52, 53, 35, 36, 21, 22, 23, 24, 56,
         57, 26, 58
   };

   public static final long getKoreanTimestamp(long realTimestamp) {
      return getTime(realTimestamp);
   }

   public static final long getTime(long realTimestamp) {
      if (realTimestamp == -1L) {
         return 150842304000000000L;
      } else if (realTimestamp == -2L) {
         return 94354848000000000L;
      } else if (realTimestamp == -3L) {
         return 150841440000000000L;
      } else {
         return realTimestamp == -4L ? -153052018564450501L : realTimestamp * 10000L + 116445060000000000L;
      }
   }

   public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
      if (SimpleTimeZone.getDefault().inDaylightTime(new Date())) {
         timeStampinMillis -= 3600000L;
      }

      long time;
      if (roundToMinutes) {
         time = timeStampinMillis / 1000L / 60L * 600000000L;
      } else {
         time = timeStampinMillis * 10000L;
      }

      return time + 116444592000000000L;
   }

   public static byte[] sendPacket(String args) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.encodeBuffer(HexTool.getByteArrayFromHexString(args));
      return mplew.getPacket();
   }

   public static void addQuestInfo(PacketEncoder mplew, MapleCharacter chr, long flag) {
      if ((flag & 512L) != 0L) {
         List<MapleQuestStatus> started = chr.getStartedQuests();
         mplew.write(true);
         mplew.writeShort(started.size());

         for (MapleQuestStatus q : started) {
            mplew.writeInt(q.getQuest().getId());
            if (q.hasMobKills()) {
               StringBuilder sb = new StringBuilder();

               for (int kills : q.getMobKills().values()) {
                  sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
               }

               mplew.writeMapleAsciiString(sb.toString());
            } else {
               mplew.writeMapleAsciiString(q.getCustomData() == null ? "" : q.getCustomData());
            }
         }
      }

      if ((flag & 16384L) != 0L) {
         mplew.write(true);
         List<MapleQuestStatus> completed = chr.getCompletedQuests();
         mplew.writeShort(completed.size());

         for (MapleQuestStatus qx : completed) {
            mplew.writeInt(qx.getQuest().getId());
            mplew.writeLong(getTime(qx.getCompletionTime()));
         }
      }
   }

   public static final void addCoolDownInfo(PacketEncoder mplew, MapleCharacter chr) {
      List<MapleCoolDownValueHolder> cd = chr.getCooldowns();
      mplew.writeShort(cd.size());

      for (MapleCoolDownValueHolder cooling : cd) {
         mplew.writeInt(cooling.skillId);
         mplew.writeInt((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
      }
   }

   public static final void addRocksInfo(PacketEncoder mplew, MapleCharacter chr) {
      int[] mapz = chr.getRegRocks();

      for (int i = 0; i < 5; i++) {
         mplew.writeInt(mapz[i]);
      }

      int[] map = chr.getRocks();

      for (int i = 0; i < 10; i++) {
         mplew.writeInt(map[i]);
      }

      int[] maps = chr.getHyperRocks();

      for (int i = 0; i < 13; i++) {
         mplew.writeInt(maps[i]);
      }
   }

   public static final void addRingInfo(PacketEncoder mplew, MapleCharacter chr) {
      Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
      List<MapleRing> cRing = aRing.getLeft();
      mplew.writeShort(cRing.size());

      for (MapleRing ring : cRing) {
         mplew.writeInt(ring.getPartnerChrId());
         mplew.writeMapleAsciiString_(ring.getPartnerName(), 13);
         mplew.writeLong(ring.getRingId());
         mplew.writeLong(ring.getPartnerRingId());
      }

      List<MapleRing> fRing = aRing.getMid();
      mplew.writeShort(fRing.size());

      for (MapleRing ring : fRing) {
         mplew.writeInt(ring.getPartnerChrId());
         mplew.writeMapleAsciiString_(ring.getPartnerName(), 13);
         mplew.writeLong(ring.getRingId());
         mplew.writeLong(ring.getPartnerRingId());
         mplew.writeInt(ring.getItemId());
      }

      List<MapleRing> mRing = aRing.getRight();
      mplew.writeShort(mRing.size());
      int marriageId = 30000;

      for (MapleRing ring : mRing) {
         mplew.writeInt(marriageId);
         mplew.writeInt(chr.getId());
         mplew.writeInt(ring.getPartnerChrId());
         mplew.writeShort(3);
         mplew.writeInt(ring.getItemId());
         mplew.writeInt(ring.getItemId());
         mplew.writeMapleAsciiString_(chr.getName(), 13);
         mplew.writeMapleAsciiString_(ring.getPartnerName(), 13);
      }
   }

   public static void addInventoryInfo(PacketEncoder mplew, MapleCharacter chr, long flag) {
      if ((flag & 2L) != 0L) {
         mplew.writeLong(chr.getMeso());
      }

      if ((flag & 8L) != 0L || (flag & 33554432L) != 0L) {
         mplew.writeInt(0);

         for (int i = 0; i < 0; i++) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeLong(0L);
         }
      }

      if ((flag & 128L) != 0L) {
         mplew.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
         mplew.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
         mplew.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
         mplew.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
         mplew.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());
         mplew.write(chr.getInventory(MapleInventoryType.CASH_EQUIP).getSlotLimit());
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
      if ((flag & 4L) != 0L) {
         mplew.write(false);
         List<Item> equipped = iv.newList();
         Collections.sort(equipped);

         for (Item item : equipped) {
            if (item.getPosition() < 0 && item.getPosition() > -100) {
               addItemPosition(mplew, item, false, false);
               addItemInfo(mplew, item, chr, false);
            }
         }

         mplew.writeShort(0);
         iv = chr.getInventory(MapleInventoryType.EQUIP);

         for (Item itemx : iv.list()) {
            if (!ii.isCash(itemx.getItemId())) {
               addItemPosition(mplew, itemx, false, false);
               addItemInfo(mplew, itemx, chr, false);
            }
         }

         mplew.writeShort(0);

         for (Item itemxx : equipped) {
            if (itemxx.getPosition() <= -1000 && itemxx.getPosition() > -1100) {
               addItemPosition(mplew, itemxx, false, false);
               addItemInfo(mplew, itemxx, chr, false);
            }
         }

         mplew.writeShort(0);

         for (Item itemxxx : equipped) {
            if (itemxxx.getPosition() <= -1100 && itemxxx.getPosition() > -1200) {
               addItemPosition(mplew, itemxxx, false, false);
               addItemInfo(mplew, itemxxx, chr, false);
            }
         }

         mplew.writeShort(0);
         mplew.writeShort(0);
         mplew.writeShort(0);

         for (Item itemxxxx : equipped) {
            if (itemxxxx.getPosition() <= -1600 && itemxxxx.getPosition() > -1700) {
               chr.getSymbols().add(itemxxxx);
               addItemPosition(mplew, itemxxxx, false, false);
               addItemInfo(mplew, itemxxxx, chr, false);
            }
         }

         mplew.writeShort(0);

         for (Item itemxxxxx : equipped) {
            if (itemxxxxx.getPosition() <= -1700 && itemxxxxx.getPosition() > -1800) {
               addItemPosition(mplew, itemxxxxx, false, false);
               addItemInfo(mplew, itemxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);
      }

      if ((flag & 140737488355328L) != 0L) {
         mplew.write(false);
         iv = chr.getInventory(MapleInventoryType.EQUIPPED);
         List<Item> equipped = iv.newList();
         Collections.sort(equipped);

         for (Item itemxxxxxx : equipped) {
            if (itemxxxxxx.getPosition() <= -100 && itemxxxxxx.getPosition() > -1000) {
               addItemPosition(mplew, itemxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxx, chr, false);
            }

            if (chr != null && chr.getCurrentCashCodyPreset() != 0) {
               int currentPreset = chr.getCurrentCashCodyPreset();
               if (itemxxxxxx.getPosition() <= -1800 && itemxxxxxx.getPosition() > -1900
                     && itemxxxxxx instanceof Equip) {
                  Equip equip = (Equip) itemxxxxxx;
                  if ((equip.getItemState() & ItemStateFlag.CODY_PRESET_ITEM.getValue()) > 0) {
                     int preset0 = chr.getOneInfoQuestInteger(QuestExConstants.CashCodyPreset.getQuestID(), "preset0");
                     short addPos = 1700;
                     switch (preset0) {
                        case 0:
                           addPos = 1700;
                           break;
                        case 1:
                           addPos = 1717;
                     }

                     short dst = (short) (itemxxxxxx.getPosition() + addPos);
                     Equip equipCheck = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                     if (equipCheck == null) {
                        if (currentPreset == 1
                              && (equip.getItemState() & ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue()) <= 0) {
                           Item clone = itemxxxxxx.copy();
                           clone.setPosition((short) (clone.getPosition() + addPos));
                           addItemPosition(mplew, clone, false, false);
                           addItemInfo(mplew, clone, chr, false);
                           Object var70 = null;
                        }

                        if (currentPreset == 2
                              && (equip.getItemState() & ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue()) <= 0) {
                           Item clone = itemxxxxxx.copy();
                           clone.setPosition((short) (clone.getPosition() + addPos));
                           addItemPosition(mplew, clone, false, false);
                           addItemInfo(mplew, clone, chr, false);
                           Object var72 = null;
                        }
                     }
                  }
               }
            }
         }

         mplew.writeShort(0);
         iv = chr.getInventory(MapleInventoryType.CASH_EQUIP);

         for (Item itemxxxxxx : iv.list()) {
            addItemPosition(mplew, itemxxxxxx, false, false);
            addItemInfo(mplew, itemxxxxxx, chr, false);
         }

         mplew.writeShort(0);

         for (Item itemxxxxxx : equipped) {
            if (itemxxxxxx.getPosition() <= -1200 && itemxxxxxx.getPosition() > -1500) {
               addItemPosition(mplew, itemxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);

         for (Item itemxxxxxxx : equipped) {
            if (itemxxxxxxx.getPosition() <= -1300 && itemxxxxxxx.getPosition() > -1400) {
               addItemPosition(mplew, itemxxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);

         for (Item itemxxxxxxxx : equipped) {
            if (itemxxxxxxxx.getPosition() <= -1500 && itemxxxxxxxx.getPosition() > -1600) {
               addItemPosition(mplew, itemxxxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);
         iv = chr.getInventory(MapleInventoryType.EQUIPPED);
         equipped = iv.newList();
         Collections.sort(equipped);

         for (Item itemxxxxxxxxx : equipped) {
            if (itemxxxxxxxxx.getPosition() <= -1800 && itemxxxxxxxxx.getPosition() > -1900) {
               addItemPosition(mplew, itemxxxxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);
         mplew.writeShort(0);
         mplew.writeShort(0);
      }

      if ((flag & 8L) != 0L) {
         iv = chr.getInventory(MapleInventoryType.USE);

         for (Item itemxxxxxxxxxx : iv.list()) {
            if (itemxxxxxxxxxx.getPosition() <= 128) {
               addItemPosition(mplew, itemxxxxxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);
      }

      if ((flag & 16L) != 0L) {
         iv = chr.getInventory(MapleInventoryType.SETUP);

         for (Item itemxxxxxxxxxxx : iv.list()) {
            if (itemxxxxxxxxxxx.getPosition() <= 128) {
               addItemPosition(mplew, itemxxxxxxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxxxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);
      }

      if ((flag & 32L) != 0L) {
         iv = chr.getInventory(MapleInventoryType.ETC);

         for (Item itemxxxxxxxxxxxx : iv.list()) {
            if (itemxxxxxxxxxxxx.getPosition() <= 128) {
               addItemPosition(mplew, itemxxxxxxxxxxxx, false, false);
               addItemInfo(mplew, itemxxxxxxxxxxxx, chr, false);
            }
         }

         mplew.writeShort(0);
      }

      if ((flag & 64L) != 0L) {
         iv = chr.getInventory(MapleInventoryType.CASH);

         for (Item itemxxxxxxxxxxxxx : iv.list()) {
            addItemPosition(mplew, itemxxxxxxxxxxxxx, false, false);
            addItemInfo(mplew, itemxxxxxxxxxxxxx, chr, false);
         }

         mplew.writeShort(0);
      }

      for (byte bagInvType = 2; bagInvType <= 4; bagInvType++) {
         List<Integer> bagInfo = chr.getExtendedSlots(bagInvType);
         if ((flag & (int) Math.pow(2.0, bagInvType + 1)) != 0L) {
            mplew.writeInt(bagInfo.size());

            for (Integer BagItemID : bagInfo) {
               List<Item> bagItemList = new ArrayList<>();
               int index = bagInfo.indexOf(BagItemID);
               chr.getInventory(GameConstants.getInventoryType(BagItemID)).forEach(itemxxxxxxxxxxxxx -> {
                  if (itemxxxxxxxxxxxxx.getPosition() > 10000 + (index + 1) * 100
                        && itemxxxxxxxxxxxxx.getPosition() < 10000 + (index + 2) * 100) {
                     bagItemList.add(itemxxxxxxxxxxxxx);
                  }
               });
               mplew.writeInt(index);
               mplew.writeInt(BagItemID);

               for (Item itemxxxxxxxxxxxxx : bagItemList) {
                  addItemPosition(mplew, itemxxxxxxxxxxxxx, false, true);
                  addItemInfo(mplew, itemxxxxxxxxxxxxx, chr, false);
               }

               mplew.writeInt(-1);
            }
         }
      }

      if ((flag & 16777216L) != 0L) {
         mplew.writeInt(chr.getConsumeItemLimits().size());

         for (Entry<Integer, Long> entry : chr.getConsumeItemLimits().entrySet()) {
            mplew.writeInt(entry.getKey());
            mplew.writeLong(getKoreanTimestamp(entry.getValue()));
         }
      }

      if ((flag & 1073741824L) != 0L) {
         mplew.writeInt(0);

         for (int i = 0; i < 0; i++) {
            mplew.writeLong(0L);
            mplew.writeLong(0L);
         }
      }

      if ((flag & 8388608L) != 0L) {
         for (int i = 0; i < chr.getItemPots().length; i++) {
            ItemPot pot = chr.getItemPots()[i];
            mplew.write(pot != null);
            if (pot == null) {
               break;
            }

            pot.encode(mplew);
         }
      }
   }

   public static final void addCharStats(PacketEncoder mplew, MapleCharacter chr) {
      mplew.writeInt(chr.getId());
      mplew.writeInt(chr.getId());
      mplew.writeInt(0);
      mplew.writeMapleAsciiString_(chr.getName(), 13);
      mplew.write(chr.getGender());
      mplew.write(chr.getSkinColor());
      int face = chr.getFace();
      int faceBaseColor = chr.isDressUp() ? chr.getSecondFaceBaseColor() : chr.getFaceBaseColor();
      int faceAddColor = chr.isDressUp() ? chr.getSecondFaceAddColor() : chr.getFaceAddColor();
      int faceBaseProb = chr.isDressUp() ? chr.getSecondFaceBaseProb() : chr.getFaceBaseProb();
      if (faceBaseProb > 0 && face < 100000) {
         face = (face / 1000 * 1000 + face % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100 + faceBaseProb;
      }

      mplew.writeInt(face);
      int hair = chr.isDressUp() ? chr.getSecondHair() : chr.getHair();
      int hairBaseColor = chr.isDressUp() ? chr.getSecondBaseColor() : chr.getBaseColor();
      int hairAddColor = chr.isDressUp() ? chr.getSecondAddColor() : chr.getAddColor();
      int hairBaseProb = chr.isDressUp() ? chr.getSecondBaseProb() : chr.getBaseProb();
      if (hairBaseProb > 0 && hair < 100000) {
         hair = hair / 10 * 10 * 1000 + hairBaseColor * 1000 + hairAddColor * 100 + hairBaseProb;
      }

      mplew.writeInt(hair);
      mplew.writeInt(chr.getLevel());
      mplew.writeShort(chr.getJob());
      chr.getStat().connectData(mplew, chr.getZeroInfo());
      mplew.writeShort(chr.getRemainingAp());
      int size = chr.getRemainingSpSize();
      if (GameConstants.isSeparatedSp(chr.getJob())) {
         mplew.write(size);

         for (int i = 0; i < chr.getRemainingSps().length; i++) {
            if (chr.getRemainingSp(i) > 0) {
               mplew.write(i + 1);
               mplew.writeInt(chr.getRemainingSp(i));
            }
         }
      } else {
         mplew.writeShort(chr.getRemainingSp());
      }

      mplew.writeLong(chr.getExp());
      mplew.writeInt(chr.getFame());
      mplew.writeInt(GameConstants.isZero(chr.getJob()) ? chr.getStat().getMp() : 99999L);
      mplew.writeInt(chr.getMapId());
      mplew.write(chr.getInitialSpawnpoint());
      mplew.writeShort(chr.getSubcategory());
      if (GameConstants.isDemonSlayer(chr.getJob())
            || GameConstants.isXenon(chr.getJob())
            || GameConstants.isDemonAvenger(chr.getJob())
            || GameConstants.isArk(chr.getJob())
            || GameConstants.isHoyoung(chr.getJob())) {
         mplew.writeInt(chr.getDemonMarking());
      }

      mplew.write(0);
      mplew.writeLong(getTime(-2L));
      mplew.writeShort(chr.getFatigue());
      mplew.writeInt(GameConstants.getCurrentDate());

      for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
         mplew.writeInt(chr.getTrait(t).getLocalTotalExp());
      }

      for (int ix = 0; ix < 2; ix++) {
         for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.writeShort(chr.getTrait(t).getTodayExp());
         }
      }

      mplew.write(0);
      mplew.writeLong(getTime(-2L));
      mplew.writeInt(GameConstants.getCurrentDate_NoTime());
      mplew.writeInt(0);
      mplew.write(10);
      mplew.writeInt(0);
      mplew.write(5);
      mplew.write(5);
      mplew.writeInt(0);
      mplew.writeLong(getKoreanTimestamp(System.currentTimeMillis()));
      mplew.writeLong(getKoreanTimestamp(System.currentTimeMillis() - 10000L));
      mplew.writeLong(getKoreanTimestamp(System.currentTimeMillis() + 1000000L));
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
   }

   public static final void addCharLook(PacketEncoder mplew, MapleCharacter chr, boolean mega, boolean isBeta) {
      addCharLook(mplew, chr, mega, isBeta, false);
   }

   public static final void addCharLook(PacketEncoder mplew, MapleCharacter chr, boolean mega, boolean isBeta,
         boolean isTag) {
      addCharLook(mplew, chr, mega, isBeta, isTag, -1);
   }

   public static final void addCharLook(PacketEncoder mplew, MapleCharacter chr, boolean mega, boolean isBeta,
         boolean isTag, int forcePreset) {
      ZeroInfo zeroInfo = chr.getZeroInfo();
      if (GameConstants.isAngelicBuster(chr.getJob())) {
         mplew.write(chr.isDressUp() ? chr.getSecondGender() : chr.getGender());
         mplew.write(chr.isDressUp() ? chr.getSecondSkinColor() : chr.getSkinColor());
         int face = chr.isDressUp() ? chr.getSecondFace() : chr.getFace();
         int faceBaseColor = chr.isDressUp() ? chr.getSecondFaceBaseColor() : chr.getFaceBaseColor();
         int faceAddColor = chr.isDressUp() ? chr.getSecondFaceAddColor() : chr.getFaceAddColor();
         int faceBaseProb = chr.isDressUp() ? chr.getSecondFaceBaseProb() : chr.getFaceBaseProb();
         if (faceBaseProb > 0 && face < 100000) {
            face = (face / 1000 * 1000 + face % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100 + faceBaseProb;
         }

         mplew.writeInt(face);
         mplew.writeInt(chr.getJob());
         mplew.write(mega ? 0 : 1);
         int hair = chr.isDressUp() ? chr.getSecondHair() : chr.getHair();
         int hairBaseColor = chr.isDressUp() ? chr.getSecondBaseColor() : chr.getBaseColor();
         int hairAddColor = chr.isDressUp() ? chr.getSecondAddColor() : chr.getAddColor();
         int hairBaseProb = chr.isDressUp() ? chr.getSecondBaseProb() : chr.getBaseProb();
         int mixedHair = hair;
         if (hairBaseProb > 0 && hair < 100000) {
            mixedHair = hair / 10 * 10 * 1000 + hairBaseColor * 1000 + hairAddColor * 100 + hairBaseProb;
         }

         mplew.writeInt(mixedHair);
      } else {
         int facex = isBeta ? zeroInfo.getSubFace() : chr.getFace();
         int faceBaseColorx = isBeta ? chr.getSecondFaceBaseColor() : chr.getFaceBaseColor();
         int faceAddColorx = isBeta ? chr.getSecondFaceAddColor() : chr.getFaceAddColor();
         int faceBaseProbx = isBeta ? chr.getSecondFaceBaseProb() : chr.getFaceBaseProb();
         if (faceBaseProbx > 0 && facex < 100000) {
            facex = (facex / 1000 * 1000 + facex % 100 + faceBaseColorx * 100) * 1000 + faceAddColorx * 100
                  + faceBaseProbx;
         }

         mplew.write(2);
         mplew.write(isBeta ? zeroInfo.getSubSkin() : chr.getSkinColor());
         mplew.writeInt(facex);
         mplew.writeInt(chr.getJob());
         mplew.write(0);
         int hair = isBeta ? zeroInfo.getSubHair() : chr.getHair();
         int hairBaseColor = isBeta ? zeroInfo.getMixBaseHairColor() : chr.getBaseColor();
         int hairAddColor = isBeta ? zeroInfo.getMixAddHairColor() : chr.getAddColor();
         int hairBaseProb = isBeta ? zeroInfo.getMixHairBaseProb() : chr.getBaseProb();
         int mixedHair = hair;
         if (hairBaseProb > 0 && hair < 100000) {
            mixedHair = hair / 10 * 10 * 1000 + hairBaseColor * 1000 + hairAddColor * 100 + hairBaseProb;
         }

         mplew.writeInt(mixedHair);
      }

      Map<Short, Integer> myEquip = new LinkedHashMap<>();
      Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
      MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
      int currentPreset = chr.getCurrentCashCodyPreset();

      for (Item item : equip.list().stream()
            .sorted(Comparator.comparingInt(Item::getPosition).reversed())
            .collect(Collectors.toList())) {
         short pos_ = 0;
         if (item.getPosition() <= -1499 && item.getPosition() >= -1509) {
            if (!GameConstants.isZero(chr.getJob()) || !isBeta) {
               continue;
            }

            pos_ = BetaSlot(item.getPosition());
         } else if (item.getPosition() <= -1300 && item.getPosition() >= -1309) {
            if (!GameConstants.isAngelicBuster(chr.getJob()) || !chr.isDressUp()) {
               continue;
            }

            pos_ = AngelicBusterSlot(item.getPosition());
         }

         if (item.getPosition() == -10 && isBeta && GameConstants.isZero(chr.getJob())) {
            pos_ = -11;
         } else if (item.getPosition() == -10 && !isBeta && GameConstants.isZero(chr.getJob())
               || item.getPosition() == -11 && isBeta && GameConstants.isZero(chr.getJob())
               || item.getPosition() <= -1 && item.getPosition() > -10 && chr.isDressUp()) {
            continue;
         }

         if (!isBeta || !GameConstants.isZero(chr.getJob()) || isTag || checkZeroClothes(item.getPosition(), chr)) {
            Equip item_ = (Equip) item;
            short pos = pos_ == 0 ? (short) (item.getPosition() * -1) : (short) (pos_ * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
               String lol = Integer.valueOf(item.getItemId()).toString();
               String ss = lol.substring(0, 3);
               int moru = Integer.parseInt(ss + Integer.valueOf(item_.getFusionAnvil()).toString());
               myEquip.put(pos, item_.getFusionAnvil() != 0 ? moru : item.getItemId());
            } else if (pos > 1800 & pos < 1900) {
               if (currentPreset != 0
                     && (item_.getItemState() & ItemStateFlag.CODY_PRESET_ITEM.getValue()) > 0
                     && (currentPreset != 1
                           || (item_.getItemState() & ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue()) <= 0)
                     && (currentPreset != 2
                           || (item_.getItemState() & ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue()) <= 0)) {
                  if (pos > 1817) {
                     pos = (short) (pos - 1817);
                  } else {
                     pos = (short) (pos - 1800);
                  }

                  if (myEquip.get(pos) != null) {
                     short key = (short) ((pos + 100) * -1);
                     if (equip.getItem(key) != null) {
                        continue;
                     }

                     if (pos_ > 0) {
                        myEquip.remove(pos);
                     } else {
                        maskedEquip.put(pos, myEquip.get(pos));
                     }
                  }

                  String lol = Integer.valueOf(item.getItemId()).toString();
                  String ss = lol.substring(0, 3);
                  int moru = Integer.parseInt(ss + Integer.valueOf(item_.getFusionAnvil()).toString());
                  myEquip.put(pos, item_.getFusionAnvil() != 0 ? moru : item.getItemId());
               }
            } else if (pos > 100 && pos != 111) {
               pos = (short) (pos - 100);
               if (myEquip.get(pos) != null) {
                  if (pos_ > 0) {
                     myEquip.remove(pos);
                  } else {
                     maskedEquip.put(pos, myEquip.get(pos));
                  }
               }

               String lol = Integer.valueOf(item.getItemId()).toString();
               String ss = lol.substring(0, 3);
               int moru = Integer.parseInt(ss + Integer.valueOf(item_.getFusionAnvil()).toString());
               myEquip.put(pos, item_.getFusionAnvil() != 0 ? moru : item.getItemId());
            } else if (myEquip.get(pos) != null) {
               maskedEquip.put(pos, item.getItemId());
            }
         }
      }

      if (chr.isDressUp() && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -1307) == null) {
         myEquip.remove(5);
         int clothe = chr.getDressUp_Clothe();
         myEquip.put((short) 5, clothe == 0 ? 1051291 : clothe);
      }

      int forceCashWeapon = 0;
      if (forcePreset != -1) {
         int forcePresetDiff = 0;
         switch (chr.getCashCodyPresetSlot(forcePreset)) {
            case 0:
               forcePresetDiff = 1700;
               break;
            case 1:
               forcePresetDiff = 1717;
         }

         int preset0Diff = 0;
         switch (chr.getCashCodyPresetSlot(0)) {
            case 0:
               preset0Diff = 1700;
               break;
            case 1:
               preset0Diff = 1717;
         }

         for (short i = -101; i > -117; i--) {
            short forcePos = (short) (i - forcePresetDiff);
            short fixPos = (short) (forcePos * -1);
            Equip srcEquip = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(forcePos);
            int itemID = 0;
            if (srcEquip == null) {
               if (preset0Diff == 0) {
                  continue;
               }

               short preset0EquipPos = (short) (i - forcePresetDiff);
               Equip preset0Equip = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(preset0EquipPos);
               if (preset0Equip == null) {
                  continue;
               }

               if (forcePreset == 0) {
                  itemID = preset0Equip.getItemId();
               } else if ((preset0Equip.getItemState() & ItemStateFlag.CODY_PRESET_ITEM.getValue()) > 0) {
                  if (forcePreset == 1
                        && (preset0Equip.getItemState() & ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue()) > 0
                        || forcePreset == 2
                              && (preset0Equip.getItemState() & ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue()) > 0) {
                     continue;
                  }

                  itemID = preset0Equip.getItemId();
               }
            } else {
               itemID = srcEquip.getItemId();
            }

            if (itemID != 0) {
               fixPos = (short) (fixPos - forcePresetDiff);
               if (fixPos == 111) {
                  forceCashWeapon = itemID;
               }

               maskedEquip.put(fixPos, itemID);
               fixPos = (short) (fixPos - 100);
               if (fixPos != 11) {
                  myEquip.put(fixPos, itemID);
               }
            }
         }
      }

      for (Entry<Short, Integer> entry : myEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }

      mplew.write(255);

      for (Entry<Short, Integer> entry : maskedEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }

      mplew.write(255);
      Item cWeapon = equip.getItem((short) -111);
      if (isBeta && (chr.getZeroLinkCashPart() & ZeroLinkCashPartFlag.Weapon.getFlag()) == 0) {
         cWeapon = equip.getItem((short) -1507);
      }

      int cAnvil = cWeapon != null ? ((Equip) cWeapon).getFusionAnvil() : 0;
      Item weapon = equip.getItem((short) (isBeta && GameConstants.isZero(chr.getJob()) ? -10 : -11));
      int mainAnvil = weapon != null ? ((Equip) weapon).getFusionAnvil() : 0;
      Item subWeapon = equip.getItem((short) -10);
      int subAnvil = subWeapon != null ? ((Equip) subWeapon).getFusionAnvil() : 0;
      boolean zero = GameConstants.isZero(chr.getJob());
      mplew.writeInt(
            forceCashWeapon != 0 ? forceCashWeapon
                  : (cWeapon != null
                        ? (cAnvil == 0 ? cWeapon.getItemId() : cWeapon.getItemId() / 10000 * 10000 + cAnvil)
                        : 0));
      mplew.writeInt(
            weapon != null ? (mainAnvil == 0 ? weapon.getItemId() : weapon.getItemId() / 10000 * 10000 + mainAnvil)
                  : 0);
      mplew.writeInt(
            subWeapon == null || GameConstants.isZero(chr.getJob())
                  ? 0
                  : (subAnvil == 0 ? subWeapon.getItemId() : subWeapon.getItemId() / 10000 * 10000 + subAnvil));
      mplew.writeInt(chr.getDrawElfEar_Look());
      mplew.writeInt(chr.getKeyValue(QuestExConstants.PinkBeanColor.getQuestID(), "hue"));
      if (GameConstants.isHoyoung(chr.getJob())) {
         mplew.write(chr.getShift());
      } else {
         mplew.write(chr.getDrawTail() == 0 ? 1 : 0);
      }

      mplew.writeInt(0);
      mplew.writeZeroBytes(12);
      if (!GameConstants.isDemonSlayer(chr.getJob())
            && !GameConstants.isXenon(chr.getJob())
            && !GameConstants.isDemonAvenger(chr.getJob())
            && !GameConstants.isArk(chr.getJob())
            && !GameConstants.isHoyoung(chr.getJob())) {
         if (GameConstants.isAngelicBuster(chr.getJob())) {
            mplew.write(0);
         } else if (GameConstants.isZero(chr.getJob())) {
            mplew.write(isBeta);
         }
      } else {
         mplew.writeInt(chr.getShift() != 0 ? 0 : chr.getDemonMarking());
      }

      mplew.writeInt(153380);
   }

   public static short AngelicBusterSlot(short slot) {
      switch (slot) {
         case -1309:
            return -107;
         case -1308:
            return -106;
         case -1307:
            return -105;
         case -1306:
            return -104;
         case -1305:
            return -103;
         case -1304:
            return -108;
         case -1303:
         default:
            return 0;
         case -1302:
            return -102;
         case -1301:
            return -109;
         case -1300:
            return -101;
      }
   }

   public static short BetaSlot(short slot) {
      switch (slot) {
         case -1509:
            return -107;
         case -1508:
            return -106;
         case -1507:
            return -111;
         case -1506:
            return -108;
         case -1505:
            return -105;
         case -1504:
            return -109;
         case -1503:
            return -108;
         case -1502:
            return -102;
         case -1501:
            return -101;
         case -1500:
            return -103;
         case -1499:
            return -103;
         default:
            return 0;
      }
   }

   public static final void addExpirationTime(PacketEncoder mplew, long time) {
      mplew.writeLong(getTime(time));
   }

   public static void addItemPosition(PacketEncoder mplew, Item item, boolean trade, boolean bagSlot) {
      if (item == null) {
         mplew.writeShort(0);
      } else {
         short pos = item.getPosition();
         if (pos <= -1) {
            pos = (short) (pos * -1);
            if (pos > 100 && pos < 1000) {
               pos = (short) (pos - 100);
            }
         }

         if (bagSlot) {
            mplew.writeInt(pos % 100 - 1);
         } else {
            mplew.writeShort(pos);
         }
      }
   }

   public static final void addItemInfo(PacketEncoder mplew, Item item) {
      addItemInfo(mplew, item, null, true);
   }

   public static final void addItemInfo(PacketEncoder mplew, Item item, MapleCharacter chr) {
      addItemInfo(mplew, item, chr, true);
   }

   public static final void addItemInfo(PacketEncoder mplew, Item item, MapleCharacter chr, boolean unequipPet) {
      mplew.write(item.getPet() != null ? 3 : item.getType());
      mplew.writeInt(item.getItemId());
      boolean hasUniqueId = !GameConstants.isIntensePowerCrystal(item.getItemId())
            && item.getUniqueId() > 0L
            && !GameConstants.isMarriageRing(item.getItemId())
            && item.getItemId() / 10000 != 166
            && !GameConstants.isFairyPendant(item.getItemId());
      mplew.write(hasUniqueId ? 1 : 0);
      if (hasUniqueId) {
         mplew.writeLong(item.getUniqueId());
      }

      if (item.getPet() != null) {
         addPetItemInfo(mplew, chr, item, item.getPet(), unequipPet, false);
      } else {
         addExpirationTime(mplew, item.getExpiration());
         if (chr == null) {
            mplew.writeInt(-1);
         } else {
            byte type = (byte) (item.getItemId() / 1000000);
            if (type > 1 && type < 5) {
               int index = chr.getExtendedSlots(type).indexOf(item.getItemId());
               mplew.writeInt(index);
            } else {
               mplew.writeInt(-1);
            }
         }

         mplew.write(item.getType() == 1 || GameConstants.isIntensePowerCrystal(item.getItemId()));
         if (item.getType() == 1) {
            Equip equip = Equip.calculateEquipStats((Equip) item);
            addEquipStats(mplew, equip);
            addEquipBonusStats(mplew, equip, hasUniqueId);
         } else {
            mplew.writeShort(item.getQuantity());
            mplew.writeMapleAsciiString_(item.getOwner(), 13);
            mplew.writeShort(item.getFlag());
            mplew.write(0);
            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
               mplew.writeLong(item.getInventoryId() <= 0L ? -1L : item.getInventoryId());
            } else if (GameConstants.isIntensePowerCrystal(item.getItemId())) {
               mplew.writeLong(item.getUniqueId());
            }

            mplew.writeInt(0);
         }
      }
   }

   private static void fixJinItem(MapleCharacter chr, Item item) {
      if ((item.getItemId() == 2633912 || item.getItemId() == 2633913 || item.getItemId() == 2633914
            || item.getItemId() == 2633915)
            && item.getExpiration() == -1L) {
         long currentTime = System.currentTimeMillis();
         item.setExpiration(currentTime + 604800000L);
      }

      if (item.getItemId() == 1182285) {
         Equip equip = (Equip) item;
         if (equip.getStr() <= 30 || equip.getDex() <= 30 || equip.getInt() <= 30 || equip.getLuk() <= 30
               || equip.getWatk() <= 20 || equip.getMatk() <= 20) {
            equip.setStr((short) 75);
            equip.setDex((short) 75);
            equip.setInt((short) 75);
            equip.setLuk((short) 75);
            equip.setWatk((short) 50);
            equip.setMatk((short) 50);
         }
      }

      if (GameConstants.isArcaneSymbol(item.getItemId()) && chr != null) {
         int jobId = chr.getJob();
         if (((Equip) item).getArcLevel() == 5 && ((Equip) item).getArc() == 0) {
            ((Equip) item).setArc(70);
            if ((jobId < 100 || jobId >= 200)
                  && jobId != 512
                  && jobId != 1512
                  && jobId != 2512
                  && (jobId < 1100 || jobId >= 1200)
                  && !GameConstants.isAran(jobId)
                  && !GameConstants.isBlaster(jobId)
                  && !GameConstants.isDemonSlayer(jobId)
                  && !GameConstants.isMichael(jobId)
                  && !GameConstants.isKaiser(jobId)
                  && !GameConstants.isZero(jobId)
                  && !GameConstants.isArk(jobId)
                  && !GameConstants.isAdele(jobId)) {
               if ((jobId < 200 || jobId >= 300)
                     && !GameConstants.isFlameWizard(jobId)
                     && !GameConstants.isEvan(jobId)
                     && !GameConstants.isLuminous(jobId)
                     && (jobId < 3200 || jobId >= 3300)
                     && !GameConstants.isKinesis(jobId)
                     && !GameConstants.isIllium(jobId)
                     && !GameConstants.isLara(jobId)) {
                  if (!GameConstants.isKain(jobId)
                        && (jobId < 300 || jobId >= 400)
                        && jobId != 522
                        && jobId != 532
                        && !GameConstants.isMechanic(jobId)
                        && !GameConstants.isAngelicBuster(jobId)
                        && (jobId < 1300 || jobId >= 1400)
                        && !GameConstants.isMercedes(jobId)
                        && (jobId < 3300 || jobId >= 3400)) {
                     if ((jobId < 400 || jobId >= 500)
                           && (jobId < 1400 || jobId >= 1500)
                           && !GameConstants.isPhantom(jobId)
                           && !GameConstants.isKadena(jobId)
                           && !GameConstants.isHoyoung(jobId)) {
                        if (GameConstants.isDemonAvenger(jobId)) {
                           ((Equip) item).setHp((short) 1470);
                        } else if (GameConstants.isXenon(jobId)) {
                           ((Equip) item).setStr((short) 336);
                           ((Equip) item).setDex((short) 336);
                           ((Equip) item).setLuk((short) 336);
                        }
                     } else {
                        ((Equip) item).setLuk((short) 700);
                     }
                  } else {
                     ((Equip) item).setDex((short) 700);
                  }
               } else {
                  ((Equip) item).setInt((short) 700);
               }
            } else {
               ((Equip) item).setStr((short) 700);
            }
         }
      }
   }

   public static void addEquipMainStats(PacketEncoder mplew, Equip equip, boolean isStarforce) {
      if (isStarforce) {
         EquipStat[] stats = new EquipStat[] {
               EquipStat.STR,
               EquipStat.DEX,
               EquipStat.INT,
               EquipStat.LUK,
               EquipStat.MHP,
               EquipStat.MMP,
               EquipStat.WATK,
               EquipStat.MATK,
               EquipStat.WDEF,
               EquipStat.HANDS,
               EquipStat.SPEED,
               EquipStat.JUMP
         };
         int head = 0;
         PacketEncoder sfInfo = new PacketEncoder();

         for (EquipStat stat : stats) {
            if (equip.getStats().contains(stat)) {
               short sfValue = 0;
               switch (stat) {
                  case STR:
                     sfValue = equip.getStarForceStat(stat, equip.getStr());
                     break;
                  case DEX:
                     sfValue = equip.getStarForceStat(stat, equip.getDex());
                     break;
                  case INT:
                     sfValue = equip.getStarForceStat(stat, equip.getInt());
                     break;
                  case LUK:
                     sfValue = equip.getStarForceStat(stat, equip.getLuk());
                     break;
                  case MHP:
                     sfValue = equip.getStarForceStat(stat, equip.getHp());
                     break;
                  case MMP:
                     sfValue = equip.getStarForceStat(stat, equip.getMp());
                     break;
                  case WATK:
                     sfValue = equip.getStarForceStat(stat, equip.getWatk());
                     break;
                  case MATK:
                     sfValue = equip.getStarForceStat(stat, equip.getMatk());
                     break;
                  case WDEF:
                     sfValue = equip.getStarForceStat(stat, equip.getWdef());
                     break;
                  case HANDS:
                     sfValue = equip.getStarForceStat(stat, equip.getHands());
                     break;
                  case SPEED:
                     sfValue = equip.getStarForceStat(stat, equip.getSpeed());
                     break;
                  case JUMP:
                     sfValue = equip.getStarForceStat(stat, equip.getJump());
                     break;
                  default:
                     continue;
               }

               if (sfValue > 0) {
                  head |= stat.getValue();
                  sfInfo.writeShort(sfValue);
               }
            }
         }

         mplew.writeInt(head);
         mplew.encodeBuffer(sfInfo.getPacket());
      } else {
         int head = 0;
         if (equip.getStats(0).size() > 0) {
            for (EquipStat statx : equip.getStats(0)) {
               head |= statx.getValue();
            }
         }

         mplew.writeInt(head);
         if (equip.getStats().contains(EquipStat.STR)) {
            mplew.writeShort(equip.getTotalStr());
         }

         if (equip.getStats().contains(EquipStat.DEX)) {
            mplew.writeShort(equip.getTotalDex());
         }

         if (equip.getStats().contains(EquipStat.INT)) {
            mplew.writeShort(equip.getTotalInt());
         }

         if (equip.getStats().contains(EquipStat.LUK)) {
            mplew.writeShort(equip.getTotalLuk());
         }

         if (equip.getStats().contains(EquipStat.MHP)) {
            int hp = equip.getTotalHp();
            if (GameConstants.isArcaneSymbol(equip.getItemId()) || GameConstants.isAuthenticSymbol(equip.getItemId())) {
               hp = equip.getTotalHp_Int() / 10;
            }

            mplew.writeShort(hp);
         }

         if (equip.getStats().contains(EquipStat.MMP)) {
            mplew.writeShort(equip.getTotalMp());
         }

         if (equip.getStats().contains(EquipStat.WATK)) {
            mplew.writeShort(equip.getTotalWatk());
         }

         if (equip.getStats().contains(EquipStat.MATK)) {
            mplew.writeShort(equip.getTotalMatk());
         }

         if (equip.getStats().contains(EquipStat.WDEF)) {
            mplew.writeShort(equip.getTotalWdef());
         }

         if (equip.getStats().contains(EquipStat.HANDS)) {
            mplew.writeShort(equip.getHands());
         }

         if (equip.getStats().contains(EquipStat.SPEED)) {
            mplew.writeShort(equip.getTotalSpeed());
         }

         if (equip.getStats().contains(EquipStat.JUMP)) {
            mplew.writeShort(equip.getTotalJump());
         }
      }
   }

   public static void addEquipStats(PacketEncoder mplew, Equip equip) {
      addEquipMainStats(mplew, equip, false);
      int head = 0;
      if (equip.getStats(1).size() > 0) {
         for (EquipStat stat : equip.getStats(1)) {
            head |= stat.getValue();
         }
      }

      mplew.writeInt(head);
      if (head != 0) {
         if (equip.getStats().contains(EquipStat.SLOTS)) {
            mplew.write(equip.getUpgradeSlots());
         }

         if (equip.getStats().contains(EquipStat.LEVEL)) {
            mplew.write(equip.getLevel());
         }

         if (equip.getStats().contains(EquipStat.FLAG)) {
            mplew.writeShort(equip.getFlag());
         }

         if (equip.getStats().contains(EquipStat.INC_SKILL)) {
            mplew.write(equip.getIncSkill() > 0 ? 1 : 0);
         }

         if (equip.getStats().contains(EquipStat.ITEM_LEVEL)) {
            mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel()));
         }

         if (equip.getStats().contains(EquipStat.ITEM_EXP)) {
            mplew.writeLong(equip.getExpPercentage() * 100000);
         }

         if (equip.getStats().contains(EquipStat.DURABILITY)) {
            mplew.writeInt(equip.getDurability());
         }

         if (equip.getStats().contains(EquipStat.VICIOUS_HAMMER)) {
            mplew.writeInt(equip.getViciousHammer());
         }

         if (equip.getStats().contains(EquipStat.DOWNLEVEL)) {
            mplew.write(equip.getTotalDownLevel());
         }

         if (equip.getStats().contains(EquipStat.SPECIAL_ATTRIBUTE)) {
            mplew.writeShort(equip.getSpecialAttribute());
         }

         if (equip.getStats().contains(EquipStat.DURABILITY_SPECIAL)) {
            mplew.writeInt(equip.getDurability());
         }

         if (equip.getStats().contains(EquipStat.REQUIRED_LEVEL)) {
            mplew.write(equip.getReqLevel());
         }

         if (equip.getStats().contains(EquipStat.GROWTH_ENCHANT)) {
            mplew.write(equip.getGrowthEnchant());
         }

         if (equip.getStats().contains(EquipStat.FINAL_STRIKE)) {
            mplew.write(equip.getFinalStrike());
         }

         if (equip.getStats().contains(EquipStat.BOSS_DAMAGE)) {
            mplew.write(equip.getTotalBossDamage());
         }

         if (equip.getStats().contains(EquipStat.IGNORE_PDR)) {
            mplew.write(equip.getTotalIgnorePDR());
         }

         if (equip.getStats().contains(EquipStat.DAM_R)) {
            mplew.write(equip.getTotalMaxDamage());
         }

         if (equip.getStats().contains(EquipStat.STAT_R)) {
            mplew.write(equip.getTotalAllStat());
         }

         if (equip.getStats().contains(EquipStat.CUTTABLE)) {
            mplew.write(equip.getKarmaCount());
         }

         if (equip.getStats().contains(EquipStat.EX_GRADE_OPTION)) {
            mplew.writeLong(equip.getExGradeOption());
         }

         if (equip.getStats().contains(EquipStat.ITEM_STATE)) {
            int itemState = equip.getItemState();
            mplew.writeInt(itemState);
         }
      }
   }

   public static void addEquipExceptionalStats(PacketEncoder mplew, Equip equip) {
      int head = 0;
      if (equip.getStats(2).size() > 0) {
         for (EquipStat stat : equip.getStats(2)) {
            head |= stat.getValue();
         }
      }

      mplew.writeInt(head);
      if (head != 0) {
         if (equip.getStats().contains(EquipStat.ExceptSTR)) {
            mplew.writeShort(equip.getExceptSTR());
         }

         if (equip.getStats().contains(EquipStat.ExceptDEX)) {
            mplew.writeShort(equip.getExceptDEX());
         }

         if (equip.getStats().contains(EquipStat.ExceptINT)) {
            mplew.writeShort(equip.getExceptINT());
         }

         if (equip.getStats().contains(EquipStat.ExceptLUK)) {
            mplew.writeShort(equip.getExceptLUK());
         }

         if (equip.getStats().contains(EquipStat.ExceptMHP)) {
            mplew.writeShort(equip.getExceptHP());
         }

         if (equip.getStats().contains(EquipStat.ExceptMMP)) {
            mplew.writeShort(equip.getExceptMP());
         }

         if (equip.getStats().contains(EquipStat.ExceptWATK)) {
            mplew.writeShort(equip.getExceptWATK());
         }

         if (equip.getStats().contains(EquipStat.ExceptMATK)) {
            mplew.writeShort(equip.getExceptMATK());
         }

         if (equip.getStats().contains(EquipStat.ExceptWDEF)) {
            mplew.writeShort(equip.getExceptWDEF());
         }

         if (equip.getStats().contains(EquipStat.ExceptMDEF)) {
            mplew.writeShort(equip.getExceptMDEF());
         }

         if (equip.getStats().contains(EquipStat.ExceptACC)) {
            mplew.writeShort(equip.getExceptACC());
         }

         if (equip.getStats().contains(EquipStat.ExceptJUMP)) {
            mplew.writeShort(equip.getExceptJUMP());
         }

         if (equip.getStats().contains(EquipStat.ExceptSPEED)) {
            mplew.writeShort(equip.getExceptSPEED());
         }
      }
   }

   public static void addEquipBonusStats(PacketEncoder mplew, Equip equip, boolean hasUniqueId) {
      mplew.writeMapleAsciiString_(equip.getOwner(), 13);
      List<Integer> PCroomlegendartyArcane = new ArrayList<>(
            Arrays.asList(
                  1212131,
                  1213030,
                  1214030,
                  1222124,
                  1232124,
                  1242144,
                  1242145,
                  1262053,
                  1272043,
                  1282043,
                  1292030,
                  1302359,
                  1312215,
                  1322266,
                  1332291,
                  1362151,
                  1372239,
                  1382276,
                  1402271,
                  1412191,
                  1422199,
                  1432229,
                  1442287,
                  1452269,
                  1462254,
                  1472277,
                  1482234,
                  1492247,
                  1522154,
                  1532159,
                  1582046,
                  1592037));
      List<Integer> PCroomlegendartyAbs = new ArrayList<>(
            Arrays.asList(
                  1212121,
                  1213028,
                  1214028,
                  1222114,
                  1232114,
                  1242123,
                  1242124,
                  1262040,
                  1272021,
                  1282022,
                  1292028,
                  1302344,
                  1312204,
                  1322256,
                  1332280,
                  1342105,
                  1362141,
                  1372229,
                  1382266,
                  1402260,
                  1412182,
                  1422190,
                  1432219,
                  1442276,
                  1452258,
                  1462244,
                  1472266,
                  1482222,
                  1492236,
                  1522144,
                  1532148,
                  1582027,
                  1592028));
      if (PCroomlegendartyArcane.contains(equip.getItemId())) {
         mplew.write(20);
         mplew.write(15);
         mplew.writeShort(40602);
         mplew.writeShort(60085);
         mplew.writeShort(60086);
         mplew.writeShort(0);
         mplew.writeShort(0);
         mplew.writeShort(0);
      } else if (PCroomlegendartyAbs.contains(equip.getItemId())) {
         mplew.write(19);
         mplew.write(17);
         mplew.writeShort(60056);
         mplew.writeShort(40602);
         mplew.writeShort(60012);
         mplew.writeShort(0);
         mplew.writeShort(0);
         mplew.writeShort(0);
      } else {
         mplew.write(equip.getState());
         mplew.write(equip.getCHUC());
         mplew.writeShort(equip.getPotential1());
         mplew.writeShort(equip.getPotential2());
         mplew.writeShort(equip.getPotential3());
         mplew.writeShort(equip.getPotential4());
         mplew.writeShort(equip.getPotential5());
         mplew.writeShort(equip.getPotential6());
      }

      mplew.writeShort(equip.getFusionAnvil() % 100000);
      if (!hasUniqueId) {
         if (GameConstants.isFairyPendant(equip.getItemId())) {
            mplew.writeLong(equip.getUniqueId());
         } else if (GameConstants.isAndroid(equip.getItemId())) {
            Android android = equip.getAndroid();
            if (android == null) {
               if (equip.getUniqueId() < 0L) {
                  long uid = MapleInventoryIdentifier.getInstance();
                  equip.setUniqueId(uid);
               }

               android = Android.create(equip.getItemId(), equip.getUniqueId());
            }

            equip.setAndroid(android);
            mplew.writeLong(equip.getUniqueId());
         } else {
            mplew.writeLong(equip.getTempUniqueID() <= 0L ? -1L : equip.getTempUniqueID());
         }
      }

      mplew.writeLong(equip.getUniqueId());
      mplew.writeLong(getTime(equip.getCsOptionExpireDate()));
      mplew.writeInt(equip.getCsGrade());
      mplew.writeInt(equip.getCsOption1());
      mplew.writeInt(equip.getCsOption2());
      mplew.writeInt(equip.getCsOption3());
      mplew.writeShort(equip.getSoulName());
      mplew.writeShort(equip.getSoulEnchanter());
      mplew.writeShort(equip.getSoulPotential());
      if (GameConstants.isArcaneSymbol(equip.getItemId()) || GameConstants.isAuthenticSymbol(equip.getItemId())) {
         mplew.writeShort(equip.getArc());
         mplew.writeInt(equip.getArcEXP());
         mplew.writeShort(equip.getArcLevel());
      }

      mplew.writeShort(-1);
      mplew.writeLong(getTime(-1L));
      mplew.writeLong(getTime(-2L));
      mplew.writeLong(getTime(-1L));
      if (equip.getItemId() / 1000 == 1662) {
         Android android = equip.getAndroid();
         if (android == null) {
            if (equip.getUniqueId() < 0L) {
               long uid = MapleInventoryIdentifier.getInstance();
               equip.setUniqueId(uid);
            }

            android = Android.create(equip.getItemId(), equip.getUniqueId());
         }

         mplew.writeShort(android.getSkin());
         mplew.writeInt(android.getHair());
         mplew.writeInt(android.getFace());
         mplew.writeMapleAsciiString(android.getName());
         mplew.writeInt(android.isEar() ? 1032024 : 2892000);
         mplew.writeLong(getTime(-2L));
      }

      mplew.write(equip.getExceptionalSlot());
      mplew.write(0);
      addEquipExceptionalStats(mplew, equip);
      if (!equip.isAmazingHyperUpgradeUsed() && equip.getCHUC() > 0) {
         mplew.write(1);
         addEquipMainStats(mplew, equip, true);
      } else {
         mplew.write(0);
      }
   }

   public static final void serializeMovementList(PacketEncoder lew, List<LifeMovementFragment> moves) {
      lew.writeShort(moves.size());

      for (LifeMovementFragment move : moves) {
         move.serialize(lew);
      }
   }

   public static final void addAnnounceBox(PacketEncoder mplew, MapleCharacter chr) {
      if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1
            && chr.getPlayerShop().isAvailable()) {
         addInteraction(mplew, chr.getPlayerShop());
      } else {
         mplew.write(0);
      }
   }

   public static final void addInteraction(PacketEncoder mplew, IMaplePlayerShop shop) {
      mplew.write(shop.getGameType());
      mplew.writeInt(((AbstractPlayerStore) shop).getObjectId());
      mplew.writeMapleAsciiString(shop.getDescription());
      if (shop.getShopType() != 1) {
         mplew.write(shop.getPassword().length() > 0 ? 1 : 0);
      }

      if (shop.getItemId() == 4080100) {
         mplew.write(((MapleMiniGame) shop).getPieceType());
      } else if (shop.getItemId() >= 4080000 && shop.getItemId() < 4080100) {
         mplew.write(((MapleMiniGame) shop).getPieceType());
      } else {
         mplew.write(shop.getItemId() % 10);
      }

      mplew.write(shop.getSize());
      mplew.write(shop.getMaxSize());
      if (shop.getShopType() != 1) {
         mplew.write(shop.isOpen() ? 0 : 1);
      }

      ReportLogEntry report = new ReportLogEntry(shop.getOwnerName(), shop.getDescription(), shop.getOwnerId());
      report.encode(mplew);
   }

   public static void onChatCommonPacket(PacketEncoder mplew, String name, String text) {
      mplew.writeMapleAsciiString(name);
      mplew.writeMapleAsciiString(text);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(1);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeMapleAsciiString("");
      mplew.writeInt(0);
   }

   public static void onChatBonusPacket(PacketEncoder mplew, int type, Item item, String itemName, int achievementID,
         long time) {
      mplew.writeInt(type);
      switch (type) {
         case 1:
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            mplew.write(item != null);
            if (item != null) {
               addItemInfo(mplew, item);
               mplew.writeMapleAsciiString(itemName);
            }
            break;
         case 2:
            mplew.writeInt(achievementID);
            mplew.writeLong(getTime(System.currentTimeMillis()));
            mplew.writeLong(time);
      }
   }

   public static final void addCharacterInfo(PacketEncoder mplew, MapleCharacter chr, long flag) {
      for (int i = 0; i < 100; i++) {
         mplew.write(1);
      }

      mplew.write(100);

      for (int i = 0; i < 3; i++) {
         mplew.writeInt(-2);
      }

      mplew.write(0);
      mplew.writeInt(0);
      mplew.write(0);
      if ((flag & 1L) != 0L) {
         addCharStats(mplew, chr);
         mplew.write(chr.getBuddylist().getCapacity());
         if (chr.getBlessOfFairyOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
         } else {
            mplew.write(0);
         }

         if (chr.getBlessOfEmpressOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
         } else {
            mplew.write(0);
         }

         MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
         if (ultExplorer != null && ultExplorer.getCustomData() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
         } else {
            mplew.write(0);
         }
      }

      addInventoryInfo(mplew, chr, flag);
      if ((flag & 256L) != 0L) {
         SkillEncode.encode(mplew, chr);
      }

      if ((flag & 32768L) != 0L) {
         addCoolDownInfo(mplew, chr);
      }

      if ((flag & 1L) != 0L) {
         chr.getSkillAlarm().encode(mplew);
      }

      addQuestInfo(mplew, chr, flag);
      if ((flag & 1024L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flag & 2048L) != 0L) {
         addRingInfo(mplew, chr);
      }

      if ((flag & 4096L) != 0L) {
         addRocksInfo(mplew, chr);
      }

      if ((flag & 262144L) != 0L) {
         chr.questInfoPacket(mplew, false);
      }

      if ((flag & 524288L) != 0L) {
         mplew.writeShort(0);
      }

      int unkNum = 1;
      mplew.write(unkNum);
      if (unkNum != 0) {
         boolean a = false;
         mplew.writeInt(a ? 1 : 0);
         if (a) {
            int size = 0;

            for (int next = 0; next < size; next++) {
               mplew.writeInt(19);
               mplew.writeMapleAsciiString("CashPurchased=1");
            }
         }
      }

      if ((flag & 17592186044416L) != 0L) {
         mplew.writeInt(1);

         for (int i = 0; i < 1; i++) {
            mplew.writeInt(chr.getAccountID());
            mplew.writeInt(-1);
         }
      }

      if ((flag & 2097152L) != 0L && chr.getJob() >= 3300 && chr.getJob() <= 3312) {
         addJaguarInfo(mplew, chr);
      }

      if ((flag & 8796093022208L) != 0L && GameConstants.isZero(chr.getJob())) {
         chr.getZeroInfo().encode(chr, ZeroInfoFlag.All, mplew);
      }

      if ((flag & 67108864L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flag & 36028797018963968L) != 0L) {
         mplew.writeShort(0);
      }

      addStealSkills(mplew, chr, flag);
      if ((flag & -2147483648L) != 0L) {
         addAbilityInfo(mplew, chr);
      }

      if ((flag & 281474976710656L) != 0L) {
         mplew.writeShort(0);
      }

      mplew.writeInt(0);
      mplew.write(0);
      if ((flag & 4294967296L) != 0L) {
         addHonorInfo(mplew, chr);
      }

      if ((flag & 8589934592L) != 0L) {
         mplew.write(1);
         mplew.writeShort(0);
      }

      if ((flag & 17179869184L) != 0L) {
         mplew.write(0);
      }

      if ((flag & 34359738368L) != 0L) {
         boolean tr = GameConstants.isAngelicBuster(chr.getJob());
         int face = tr ? chr.getSecondFace() : 0;
         int faceBaseColor = tr ? chr.getSecondFaceBaseColor() : 0;
         int faceAddColor = tr ? chr.getSecondFaceAddColor() : 0;
         int faceBaseProb = tr ? chr.getSecondFaceBaseProb() : 0;
         if (faceBaseProb > 0 && face < 100000) {
            face = (face / 1000 * 1000 + face % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100 + faceBaseProb;
         }

         mplew.writeInt(tr ? face : 0);
         int hair = tr ? chr.getSecondHair() : 0;
         int hairBaseColor = tr ? chr.getSecondBaseColor() : 0;
         int hairAddColor = tr ? chr.getSecondAddColor() : 0;
         int hairBaseProb = tr ? chr.getSecondBaseProb() : 0;
         if (hairBaseProb > 0 && hair < 100000) {
            hair = hair / 10 * 10 * 1000 + hairBaseColor * 1000 + hairAddColor * 100 + hairBaseProb;
         }

         mplew.writeInt(tr ? hair : 0);
         mplew.writeInt(tr ? 1051291 : 0);
         mplew.write(tr ? chr.getSecondSkinColor() : 0);
      }

      if ((flag & 137438953472L) != 0L) {
         mplew.write(0);
      }

      mplew.write(0);
      if ((flag & 274877906944L) != 0L) {
         addFarmInfo(mplew);
         mplew.writeInt(-1);
         mplew.writeInt(0);
      }

      if ((flag & 549755813888L) != 0L) {
         mplew.write(0);
      }

      if ((flag & 72057594037927936L) != 0L) {
         mplew.writeLong(0L);
         mplew.writeLong(0L);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeShort(0);
      }

      if ((flag & 4398046511104L) != 0L) {
         mplew.writeInt(0);
         mplew.writeLong(getTime(-2L));
         mplew.writeInt(0);
      }

      if ((flag & 562949953421312L) != 0L) {
         mplew.writeInt(chr.getId());
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeLong(getTime(-2L));
         mplew.writeInt(10);
      }

      if ((flag & 2251799813685248L) != 0L) {
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeLong(0L);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      if ((flag & 4194304L) != 0L) {
         HashMap<Integer, String> records = new HashMap<>();
         int qst = 368;
         QuestEx data = chr.getInfoQuest(qst);
         if (data != null) {
            records.put(qst, data.getData());
         }

         int var42 = 369;
         if ((data = chr.getInfoQuest(369)) != null) {
            records.put(Integer.valueOf(var42), data.getData());
         }

         var42 = 370;
         if ((data = chr.getInfoQuest(370)) != null) {
            records.put(Integer.valueOf(var42), data.getData());
         }

         mplew.writeShort(records.size());

         for (Entry entry : records.entrySet()) {
            mplew.writeInt((Integer) entry.getKey());
            mplew.writeMapleAsciiString((String) entry.getValue());
         }
      }

      if ((flag & 1125899906842624L) != 0L) {
         mplew.writeShort(chr.getCollectionInfo().size());

         for (Entry<Integer, MobCollectionEx> entry : chr.getCollectionInfo().entrySet()) {
            mplew.writeInt(entry.getKey());
            mplew.writeMapleAsciiString(entry.getValue().getData());
         }
      }

      mplew.writeInt(0);
      if ((flag & 4503599627370496L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flag & 9007199254740992L) != 0L) {
         addMatrixInfo(mplew, chr.getVCoreSkillsNoLock(), chr.getVMatrixSlots());
      }

      HexaCore hexaCore = chr.getHexaCore();
      if (hexaCore == null) {
         mplew.writeInt(0);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
      } else {
         Map<Integer, Integer> hexaSkillCores = hexaCore.getSkillCores();
         mplew.writeInt(hexaSkillCores.size());

         for (Entry<Integer, Integer> entry : hexaSkillCores.entrySet()) {
            mplew.writeInt(entry.getKey());
            mplew.writeInt(entry.getValue());
            mplew.write(true);
            mplew.write(hexaCore.isDisabledSkillCore(entry.getKey()));
         }

         mplew.write(false);
         mplew.writeInt(0);
         int size = hexaCore.getStatSize();
         mplew.writeInt(size);
         int coreid = 0;

         for (int i = 0; i < size; i++) {
            HexaCore.HexaStatData datax = hexaCore.getStat(i);
            coreid = HexaMatrixConstants.getHexaStatCoreIdByIndex(i);
            Map<Integer, HexaCore.HexaStatInfo> statmap = datax.getStats();
            boolean disable = false;
            if (statmap.isEmpty()) {
               disable = true;
            }

            HexaCore.HexaStatInfo info0 = statmap.get(0);
            HexaCore.HexaStatInfo info1 = statmap.get(1);
            HexaCore.HexaStatInfo info2 = statmap.get(2);
            int stat0 = info0 == null ? -1 : info0.type.getType();
            int stat1 = info1 == null ? -1 : info1.type.getType();
            int stat2 = info2 == null ? -1 : info2.type.getType();
            int level0 = info0 == null ? 0 : info0.level;
            int level1 = info1 == null ? 0 : info1.level;
            int level2 = info2 == null ? 0 : info2.level;
            mplew.writeInt(coreid);
            mplew.writeInt(coreid);
            mplew.writeInt(0);
            mplew.writeInt(disable ? 0 : 1);
            mplew.writeInt(stat0);
            mplew.writeInt(level0);
            mplew.writeInt(stat1);
            mplew.writeInt(level1);
            mplew.writeInt(stat2);
            mplew.writeInt(level2);
            mplew.writeInt(coreid);
            if (hexaCore.getSavedStatData() == null) {
               mplew.writeInt(1);
               mplew.writeInt(0);
               mplew.writeInt(-1);
               mplew.writeInt(0);
               mplew.writeInt(-1);
               mplew.writeInt(0);
               mplew.writeInt(-1);
               mplew.writeInt(0);
            } else {
               mplew.writeInt(1);
               Map<Integer, HexaCore.HexaStatInfo> sstatmap = hexaCore.getSavedStatData().getStats();
               mplew.writeInt(sstatmap.isEmpty() ? 0 : 1);
               HexaCore.HexaStatInfo sinfo0 = sstatmap.get(0);
               HexaCore.HexaStatInfo sinfo1 = sstatmap.get(1);
               HexaCore.HexaStatInfo sinfo2 = sstatmap.get(2);
               int sstat0 = sinfo0 == null ? -1 : sinfo0.type.getType();
               int sstat1 = sinfo1 == null ? -1 : sinfo1.type.getType();
               int sstat2 = sinfo2 == null ? -1 : sinfo2.type.getType();
               int slevel0 = sinfo0 == null ? 0 : sinfo0.level;
               int slevel1 = sinfo1 == null ? 0 : sinfo1.level;
               int slevel2 = sinfo2 == null ? 0 : sinfo2.level;
               mplew.writeInt(sstat0);
               mplew.writeInt(slevel0);
               mplew.writeInt(sstat1);
               mplew.writeInt(slevel1);
               mplew.writeInt(sstat2);
               mplew.writeInt(slevel2);
            }
         }

         mplew.writeInt(size);

         for (int i = 0; i < size; i++) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
         }

         int size1 = 0;
         mplew.writeInt(size1);

         for (int i = 0; i < size1; i++) {
            mplew.writeInt(0);
            mplew.writeInt(0);
         }

         int size2 = 0;
         mplew.writeInt(size2);

         for (int i = 0; i < size2; i++) {
            mplew.writeInt(0);
         }
      }

      if ((flag & 18014398509481984L) != 0L) {
         chr.getAchievement().encode(mplew);
      }

      if ((flag & 32L) != 0L) {
         mplew.writeInt(chr.getIntensePowerCrystals().size());

         for (IntensePowerCrystal cop : chr.getIntensePowerCrystals().values()) {
            cop.encode(mplew);
         }
      }

      if ((flag & 144115188075855872L) != 0L) {
         addMannequinInfo(mplew, chr.getHairMannequin());
      }

      if ((flag & 288230376151711744L) != 0L) {
         addMannequinInfo(mplew, chr.getFaceMannequin());
      }

      if ((flag & 72057594037927936L) != 0L) {
         addMannequinInfo(mplew, chr.getSkinMannequin());
      }

      if ((flag & 576460752303423488L) != 0L) {
         mplew.writeInt(20);

         for (int i = 0; i < 20; i++) {
            int a = 0;
            if (i < 10) {
               a = 10080001 + i;
            } else if (i < 14) {
               a = 10090001 + (i - 10);
            } else {
               a = 10100001 + (i - 14);
            }

            mplew.writeInt(a);
            mplew.writeInt(a);
            mplew.writeLong(getTime(-2L));
            mplew.writeShort(0);
         }

         mplew.writeInt(3);

         for (int i = 0; i < 3; i++) {
            mplew.writeShort(i + 1);
            mplew.writeInt(1008 + i);
         }

         mplew.writeShort(8);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      if ((flag & 2305843009213693952L) != 0L) {
         mplew.writeInt(chr.getPets().length);

         for (int i = 0; i < 3; i++) {
            MaplePet pet = chr.getPet(i);
            if (pet != null) {
               mplew.writeLong(pet.getUniqueId());
               mplew.writeInt(pet.getLootException().size());

               for (Integer ex : pet.getLootException()) {
                  mplew.writeInt(ex);
               }
            } else {
               mplew.writeLong(0L);
               mplew.writeInt(0);
            }
         }
      }
   }

   public static void addMannequinInfo(PacketEncoder packet, Mannequin mannequin) {
      mannequin.encode(packet);
   }

   public static void addMatrixInfo(PacketEncoder mplew, List<VCore> core, List<VMatrixSlot> slots) {
      mplew.writeInt(core.size());
      AtomicInteger idx = new AtomicInteger(0);
      core.stream().sorted(Comparator.comparingInt(VCore::getCoreId)).forEach(c -> {
         c.setIndex(idx.getAndAdd(1));
         mplew.writeLong(c.getCrcid());
         mplew.writeInt(c.getCoreId());
         mplew.writeInt(c.getLevel());
         mplew.writeInt(c.getExp());
         mplew.writeInt(c.getState());
         mplew.writeInt(c.getSkill1());
         mplew.writeInt(c.getSkill2());
         mplew.writeInt(c.getSkill3());
         mplew.writeInt(c.getPosition());
         mplew.writeLong(getTime(c.getAvailableTime()));
         mplew.write(c.isLocked());
      });
      mplew.writeInt(slots.size());
      slots.stream().sorted(Comparator.comparingInt(VMatrixSlot::getIndex)).forEach(c -> {
         VCore vc = null;
         if (c.getEquippedCore() != -1) {
            for (VCore a : core) {
               if (a.getPosition() == c.getIndex()) {
                  vc = a;
                  break;
               }
            }
         }

         if (vc == null) {
            c.encode(mplew, -1);
         } else {
            c.encode(mplew, c.getEquippedCore() != -1 ? vc.getIndex() : -1);
         }
      });
   }

   public static void addFarmInfo(PacketEncoder mplew) {
      mplew.writeMapleAsciiString("생성중");
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(1);
   }

   public static void addAbilityInfo(PacketEncoder mplew, MapleCharacter chr) {
      List<CharacterPotentialHolder> skills = chr.getInnerSkills();
      mplew.writeShort(skills.size());

      for (int i = 0; i < skills.size(); i++) {
         mplew.write(i + 1);
         mplew.writeInt(skills.get(i).getSkillId());
         mplew.write(skills.get(i).getSkillLevel());
         mplew.write(skills.get(i).getRank());
      }
   }

   public static void addHonorInfo(PacketEncoder mplew, MapleCharacter chr) {
      mplew.writeInt(chr.getInnerLevel());
      mplew.writeInt(chr.getInnerExp());
   }

   public static void addStolenSkills(PacketEncoder mplew, MapleCharacter chr, int jobNum) {
      int count = 0;
      if (chr.getPlayerJob() instanceof Phantom) {
         Phantom job = (Phantom) chr.getPlayerJob();
         if (chr.getStolenSkills() != null) {
            for (Pair<Integer, Integer> sk : chr.getStolenSkills()) {
               if (Phantom.getJobNumber(sk.left) == jobNum) {
                  mplew.writeInt(sk.left);
                  if (++count >= GameConstants.getNumSteal(jobNum)) {
                     break;
                  }
               }
            }
         }
      }

      while (count < GameConstants.getNumSteal(jobNum)) {
         mplew.writeInt(0);
         count++;
      }
   }

   public static void addChosenSkills(PacketEncoder mplew, MapleCharacter chr) {
      if (chr.getPlayerJob() instanceof Phantom) {
         Phantom job = (Phantom) chr.getPlayerJob();

         for (int i = 1; i <= 5; i++) {
            boolean found = false;
            if (chr.getStolenSkills() != null) {
               for (Pair<Integer, Integer> sk : chr.getStolenSkills()) {
                  if (job.getStealSkill(i) == sk.getRight()) {
                     mplew.writeInt(sk.getLeft());
                     found = true;
                     break;
                  }
               }
            }

            if (!found) {
               mplew.writeInt(0);
            }
         }
      } else {
         for (int i = 1; i <= 5; i++) {
            mplew.writeInt(0);
         }
      }
   }

   public static void addStealSkills(PacketEncoder mplew, MapleCharacter chr, long flag) {
      if ((flag & 536870912L) != 0L) {
         for (int i = 1; i <= 5; i++) {
            addStolenSkills(mplew, chr, i);
         }
      }

      if ((flag & 268435456L) != 0L) {
         addChosenSkills(mplew, chr);
      }
   }

   public static final void addPetItemInfo(PacketEncoder mplew, MapleCharacter player, Item item, MaplePet pet,
         boolean unequip, boolean petLoot) {
      if (item == null) {
         mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
      } else {
         addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
      }

      mplew.writeInt(-1);
      mplew.write(1);
      mplew.writeMapleAsciiString_(pet.getName(), 13);
      mplew.write(pet.getLevel());
      mplew.writeShort(pet.getCloseness());
      mplew.write(pet.getFullness());
      if (item == null) {
         mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
      } else {
         addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
      }

      mplew.writeShort(0);
      mplew.writeShort(pet.getFlags());
      mplew.writeInt(0);
      int petFlag = 0;
      if (item != null && (item.getFlag() & 16) != 0) {
         petFlag |= 1;
      }

      if (player != null && !player.getPetLoot()) {
         petFlag |= 2;
      }

      mplew.writeShort(petFlag);
      mplew.write(unequip ? 0 : player.getPetIndex(pet) + 1);
      mplew.writeInt(pet.getColor());
      mplew.writeShort(pet.getPetSize());
      mplew.writeShort(pet.getWonderGrade());
   }

   public static void addShopInfo(PacketEncoder mplew, MapleShop shop, MapleClient c) {
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeMapleAsciiString("");
      mplew.writeInt(0);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      mplew.writeShort(shop.getItems().size() + c.getPlayer().getRebuy().size());

      for (MapleShopItem item : shop.getItems()) {
         addShopItemInfo(mplew, item, shop, ii, null, c.getPlayer());
      }

      for (Item i : c.getPlayer().getRebuy()) {
         addShopItemInfo(mplew,
               new MapleShopItem(0, i.getItemId(), i.getRebuyPrice(), i.getQuantity(), i.getPosition()), shop, ii, i,
               c.getPlayer());
      }
   }

   public static void addShopItemInfo(PacketEncoder mplew, MapleShopItem item, MapleShop shop,
         MapleItemInformationProvider ii, Item i, MapleCharacter chr) {
      mplew.writeInt(100000);
      mplew.writeInt(item.getItemId());
      mplew.writeInt(item.getCategory());
      mplew.writeInt(100000);
      mplew.writeInt(1440 * item.getExpiration());
      mplew.writeInt(0);
      mplew.writeLong(item.getReqItem() > 0 ? 0L : item.getPrice());
      mplew.writeInt(item.getReqItem());
      mplew.writeInt(item.getReqItemQ());
      mplew.writeInt(item.getPointQuestExID());
      mplew.writeInt(item.getPointPrice());
      mplew.write(false);
      boolean isWorldLimit = item.getWorldBuyLimit() > 0;
      mplew.write(isWorldLimit);
      int limitCount = isWorldLimit
            ? chr.getWorldBuyLimit().getBuyableCount(shop, item.getWorldBuyLimit())
            : chr.getBuyLimit().getBuyableCount(shop, item.getBuyLimit());
      mplew.writeInt(limitCount);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeShort(item.getMinLevel());
      mplew.writeShort(0);
      mplew.write(0);
      mplew.writeLong(getTime(-2L));
      mplew.writeLong(getTime(-1L));
      mplew.writeInt(0);
      mplew.writeShort(1);
      mplew.write(0);
      mplew.writeInt(item.getLimitQuestExID());
      mplew.writeMapleAsciiString(item.getLimitQuestExKey());
      mplew.writeInt(item.getLimitQuestExValue());
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      if (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
         mplew.writeShort(item.getQuantity());
         mplew.writeShort(item.getBuyable());
      } else {
         mplew.writeLong(Double.doubleToLongBits(ii.getPrice(item.getItemId())));
         mplew.writeShort(ii.getSlotMax(item.getItemId()));
      }

      mplew.write(i == null ? 0 : 1);
      if (i != null) {
         addItemInfo(mplew, i);
      }
   }

   public static final void addJaguarInfo(PacketEncoder packet, MapleCharacter chr) {
      chr.getWildHunterInfo().encode(packet);
   }

   public static <E extends CTS> void writeMonsterSingleMask(PacketEncoder mplew, E statup) {
      for (int i = 4; i >= 1; i--) {
         mplew.writeInt(i == statup.getPosition() ? statup.getValue() : 0L);
      }
   }

   public static <E extends CTS> void writeSingleMask(PacketEncoder mplew, E statup) {
      for (int i = 31; i >= 1; i--) {
         mplew.writeInt(i == statup.getPosition() ? statup.getValue() : 0L);
      }
   }

   public static <E extends CTS> void writeMask(PacketEncoder mplew, Collection<E> statups) {
      int[] mask = new int[31];

      for (E statup : statups) {
         int var10001 = statup.getPosition() - 1;
         mask[var10001] = (int) (mask[var10001] | statup.getValue());
      }

      for (int i = mask.length; i >= 1; i--) {
         mplew.writeInt(mask[i - 1]);
      }
   }

   public static <E extends CTS> void writeMobMask(PacketEncoder mplew, Collection<E> statups) {
      int[] mask = new int[4];

      for (E statup : statups) {
         int var10001 = statup.getPosition() - 1;
         mask[var10001] = (int) (mask[var10001] | statup.getValue());
      }

      for (int i = mask.length; i >= 1; i--) {
         mplew.writeInt(mask[i - 1]);
      }
   }

   public static <E extends CTS> void writeBuffMask(PacketEncoder mplew, Collection<Pair<E, Integer>> statups) {
      int[] mask = new int[31];

      for (Pair<E, Integer> statup : statups) {
         int var10001 = statup.left.getPosition() - 1;
         mask[var10001] = (int) (mask[var10001] | statup.left.getValue());
      }

      for (int i = mask.length; i >= 1; i--) {
         mplew.writeInt(mask[i - 1]);
      }
   }

   public static <E extends CTS> void writeBuffMask(PacketEncoder mplew, Map<E, Integer> statups) {
      int[] mask = new int[31];

      for (E statup : statups.keySet()) {
         int var10001 = statup.getPosition() - 1;
         mask[var10001] = (int) (mask[var10001] | statup.getValue());
      }

      for (int i = mask.length; i >= 1; i--) {
         mplew.writeInt(mask[i - 1]);
      }
   }

   public static void androidInfo(PacketEncoder o, Android a) {
      o.writeShort(a.getSkin());
      o.writeInt(a.getHair());
      o.writeInt(a.getFace());
      o.writeMapleAsciiString(a.getName());
      o.writeInt(a.isEar() ? 1032024 : 0);
      o.writeLong(getTime(-2L));
   }

   public static void set2barr(int value, byte[] dest, int index) {
      dest[index + 0] = (byte) (value >> 0);
      dest[index + 1] = (byte) (value >> 8);
      dest[index + 2] = (byte) (value >> 16);
      dest[index + 3] = (byte) (value >> 24);
   }

   public static int byteArrayToInt(byte[] bytes, int index) {
      return (bytes[index] & 0xFF) << 0 | (bytes[index + 1] & 0xFF) << 8 | (bytes[index + 2] & 0xFF) << 16
            | bytes[index + 3] & 0xFF000000;
   }

   public static int b2ui32(byte[] bytes, int index) {
      return byteArrayToInt(bytes, index);
   }

   public static long toUnsigned(int value) {
      return value & 4294967295L;
   }

   public static int get_hundred(int val) {
      return val == 0 ? -1 : val % 1000;
   }

   public static void encodePackedCharacterLook(PacketEncoder packet, MapleCharacter player) {
      Map<Short, Integer> myEquip = new LinkedHashMap<>();
      Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
      MapleInventory equip = player.getInventory(MapleInventoryType.EQUIPPED);

      for (Item item : equip.list()) {
         Equip item_ = (Equip) item;
         short pos = (short) (item.getPosition() * -1);
         if (pos < 100 && myEquip.get(pos) == null) {
            String lol = Integer.valueOf(item.getItemId()).toString();
            String ss = lol.substring(0, 3);
            int moru = Integer.parseInt(ss + Integer.valueOf(item_.getFusionAnvil()).toString());
            myEquip.put(pos, item_.getFusionAnvil() != 0 ? moru : item.getItemId());
         } else if (pos > 100 && pos != 111) {
            pos = (short) (pos - 100);
            if (myEquip.get(pos) != null) {
               maskedEquip.put(pos, myEquip.get(pos));
            }

            String lol = Integer.valueOf(item.getItemId()).toString();
            String ss = lol.substring(0, 3);
            int moru = Integer.parseInt(ss + Integer.valueOf(item_.getFusionAnvil()).toString());
            myEquip.put(pos, item_.getFusionAnvil() != 0 ? moru : item.getItemId());
         } else if (myEquip.get(pos) != null) {
            maskedEquip.put(pos, item.getItemId());
         }
      }

      int[] anHairEquip = new int[100];

      for (Entry<Short, Integer> entry : myEquip.entrySet()) {
         if (entry.getKey() < 100) {
            anHairEquip[entry.getKey()] = entry.getValue();
         }
      }

      byte[] data = new byte[124];
      int datai = b2ui32(data, 0);
      datai |= player.getGender() & 1;
      datai |= 2 * (player.getSkinColor() & 15);
      datai |= 32 * (get_hundred(player.getFace()) & 1023);
      set2barr(datai, data, 0);
      datai = b2ui32(data, 1);
      datai |= (player.getFace() / 1000 % 10 & 7) << 7;
      set2barr(datai, data, 1);
      datai = b2ui32(data, 2);
      datai |= 4 * (player.getHair() / 10000 == 4 ? 1 : 0);
      datai |= 8 * (get_hundred(player.getHair()) & 1023);
      set2barr(datai, data, 2);
      datai = b2ui32(data, 3);
      datai |= 32 * (player.getHair() / 1000 % 10 & 15);
      set2barr(datai, data, 3);
      datai = b2ui32(data, 4);
      datai |= 2 * (get_hundred(anHairEquip[1]) & 1023);
      set2barr(datai, data, 4);
      datai = b2ui32(data, 5);
      datai |= 8 * (anHairEquip[1] / 1000 % 10 & 7);
      datai |= (get_hundred(anHairEquip[2]) & 1023) << 6;
      set2barr(datai, data, 5);
      datai = b2ui32(data, 7);
      datai |= anHairEquip[2] / 1000 % 10 & 3;
      datai |= 4 * (get_hundred(anHairEquip[3]) & 1023);
      set2barr(datai, data, 7);
      datai = b2ui32(data, 8);
      datai |= 16 * (anHairEquip[3] / 1000 % 10 & 3);
      datai |= (get_hundred(anHairEquip[4]) & 1023) << 6;
      set2barr(datai, data, 8);
      datai = b2ui32(data, 10);
      datai |= anHairEquip[4] / 1000 % 10 & 3;
      datai |= 4 * (anHairEquip[5] / 10000 == 105 ? 1 : 0);
      datai |= 8 * (get_hundred(anHairEquip[5]) & 1023);
      set2barr(datai, data, 10);
      datai = b2ui32(data, 11);
      datai |= 32 * (anHairEquip[5] / 1000 % 10 & 15);
      set2barr(datai, data, 11);
      datai = b2ui32(data, 12);
      datai |= 2 * (get_hundred(anHairEquip[6]) & 1023);
      set2barr(datai, data, 12);
      datai = b2ui32(data, 13);
      datai |= 8 * (anHairEquip[6] / 1000 % 10 & 3);
      datai |= 32 * (get_hundred(anHairEquip[7]) & 1023);
      set2barr(datai, data, 13);
      datai = b2ui32(data, 14);
      datai |= (anHairEquip[7] / 1000 % 10 & 3) << 7;
      set2barr(datai, data, 14);
      datai = b2ui32(data, 15);
      datai |= 2 * (get_hundred(anHairEquip[8]) & 1023);
      set2barr(datai, data, 15);
      datai = b2ui32(data, 16);
      datai |= 8 * (anHairEquip[8] / 1000 % 10 & 3);
      datai |= 32 * (get_hundred(anHairEquip[9]) & 1023);
      set2barr(datai, data, 16);
      datai = b2ui32(data, 17);
      datai |= (anHairEquip[9] / 1000 % 10 & 3) << 7;
      set2barr(datai, data, 17);
      datai = b2ui32(data, 18);
      int v39 = 0;
      if (anHairEquip[10] != 0) {
         if (anHairEquip[10] / 10000 == 109) {
            v39 = 1;
         } else {
            v39 = (anHairEquip[10] / 10000 != 134 ? 1 : 0) + 2;
         }
      }

      datai |= 2 * (v39 & 3);
      datai |= 8 * (get_hundred(anHairEquip[10]) & 1023);
      set2barr(datai, data, 18);
      datai = b2ui32(data, 19);
      datai |= 32 * (anHairEquip[10] / 1000 % 10 & 15);
      set2barr(datai, data, 19);
      Item cWeapon = equip.getItem((short) -111);
      Item weapon = equip.getItem((short) -11);
      int nWeapon = cWeapon != null ? cWeapon.getItemId() : (weapon != null ? weapon.getItemId() : 0);
      datai = b2ui32(data, 20);
      if (cWeapon != null) {
         datai |= 2;
      }

      datai |= 4 * (get_hundred(nWeapon) & 1023);
      set2barr(datai, data, 20);
      int v7 = 1;
      int v6 = weapon == null ? 0 : weapon.getItemId() / 10000 % 100;
      int v45 = 0;
      boolean error = false;

      while (g_anWeaponType[v7] != v6) {
         if (++v7 > 30) {
            v45 = 0;
            error = true;
            break;
         }
      }

      if (!error) {
         v45 = v7;
      }

      datai = b2ui32(data, 21);
      datai |= 16 * (nWeapon / 1000 % 10 & 3);
      datai |= (v45 & 31) << 6;
      set2barr(datai, data, 21);
      datai = b2ui32(data, 22);
      datai |= 0;
      set2barr(datai, data, 22);
      data[119] = 12;
      byte[] toWrite = new byte[120];
      System.arraycopy(data, 0, toWrite, 0, 120);
      packet.encodeBuffer(toWrite);
   }

   public static final boolean checkZeroClothes(short pos, MapleCharacter chr) {
      ZeroInfo zeroInfo = chr.getZeroInfo();
      if (zeroInfo == null) {
         return false;
      } else {
         int clothesFlag = zeroInfo.getZeroLinkCashPart();
         if (pos == -103 && (clothesFlag & ZeroLinkCashPartFlag.EyeAccessory.getFlag()) != 0) {
            return true;
         } else if (pos == -101 && (clothesFlag & ZeroLinkCashPartFlag.Cap.getFlag()) != 0) {
            return true;
         } else if (pos == -102 && (clothesFlag & ZeroLinkCashPartFlag.ForeHead.getFlag()) != 0) {
            return true;
         } else if (pos == -104 && (clothesFlag & ZeroLinkCashPartFlag.EarRing.getFlag()) != 0) {
            return true;
         } else if (pos == -109 && (clothesFlag & ZeroLinkCashPartFlag.Cape.getFlag()) != 0) {
            return true;
         } else if (pos == -105 && (clothesFlag & ZeroLinkCashPartFlag.Clothes.getFlag()) != 0) {
            return true;
         } else if (pos == -108 && (clothesFlag & ZeroLinkCashPartFlag.Gloves.getFlag()) != 0) {
            return true;
         } else if (pos == -111 && (clothesFlag & ZeroLinkCashPartFlag.Weapon.getFlag()) != 0) {
            return true;
         } else if (pos == -106 && (clothesFlag & ZeroLinkCashPartFlag.Pants.getFlag()) != 0) {
            return true;
         } else if (pos == -107 && (clothesFlag & ZeroLinkCashPartFlag.Shoes.getFlag()) != 0) {
            return true;
         } else if (pos == -112 && (clothesFlag & ZeroLinkCashPartFlag.Ring1.getFlag()) != 0) {
            return true;
         } else {
            return pos == -113 && (clothesFlag & ZeroLinkCashPartFlag.Ring2.getFlag()) != 0 ? true
                  : pos >= -100 || pos <= -1499;
         }
      }
   }
}
