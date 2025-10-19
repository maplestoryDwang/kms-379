package objects.utils;

import constants.GameConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;
import objects.item.MapleItemInformationProvider;

public class ShopAnalyzer {
   public static void main(String[] args) throws IOException {
      new Scanner(System.in);
      String data = "";
      System.out.println("\r\n\r\n");
      StringBuilder all = new StringBuilder();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (int size = 1; size < data.split("@").length; size++) {
         byte[] hexdata = HexTool.getByteArrayFromHexString(data.split("@")[size]);
         PacketDecoder slea = new PacketDecoder(new ByteArrayByteStream(hexdata));
         StringBuilder sb = new StringBuilder();
         PacketDecoder slea2 = new PacketDecoder(new ByteArrayByteStream(hexdata));
         int header = slea2.readShort();
         if (header > 1 && header < 65535) {
            slea.readShort();
         }

         int scriptedItem = slea.readInt();
         int shopid = slea.readInt();
         sb.append("#SQL:\r\n");
         sb.append("#shops:\r\n\r\n");
         sb.append("INSERT INTO shops (`shopid`, `npcid`) VALUES (").append(shopid).append(", ").append(shopid).append(");\r\n\r\n");
         sb.append("#shopitems:\r\n\r\n");
         boolean ranks = slea.readByte() == 1;
         int rank = 0;
         String rankmsg = null;
         if (ranks) {
            int ranksize = slea.readByte();

            for (int r = 0; r < ranksize; r++) {
               rank = slea.readInt();
               rankmsg = slea.readMapleAsciiString();
            }
         }

         short itemsize = slea.readShort();

         for (int i = 1; i <= itemsize && slea.available() >= 29L; i++) {
            int itemid = slea.readInt();
            int price = slea.readInt();
            byte discountR = slea.readByte();
            int reqItem = slea.readInt();
            int reqItemQuantity = slea.readInt();
            int expiration = slea.readInt();
            int minLevel = slea.readInt();
            slea.readInt();
            boolean recharge = GameConstants.isThrowingStar(itemid) || GameConstants.isBullet(itemid);
            int category;
            short buyable;
            short quantity;
            if (slea.available() > 28 + (recharge ? 4 : 0)) {
               slea.readLong();
               slea.readLong();
               category = slea.readInt();
               slea.readByte();
               slea.readInt();
               if (!recharge) {
                  quantity = slea.readShort();
                  buyable = slea.readShort();
               } else {
                  quantity = 1;
                  slea.readLong();
                  buyable = ii.getSlotMax(itemid);
               }
            } else {
               category = 0;
               quantity = 1;
               buyable = ii.getSlotMax(itemid);
            }

            slea.skip(49 + (recharge ? 4 : 0));
            if (itemid >= 1000000 && itemid <= 6000000 && itemid / 10000 != 207 && itemid / 10000 != 233) {
               sb.append(
                     "INSERT INTO shopitems (`shopid`, `itemid`, `price`, `position`, `reqitem`, `reqitemq`, `rank`, `quantity`, `buyable`, `category`) VALUES("
                  )
                  .append(shopid)
                  .append(", ")
                  .append(itemid)
                  .append(", ")
                  .append(price)
                  .append(", ")
                  .append(i)
                  .append(", ")
                  .append(reqItem)
                  .append(", ")
                  .append(reqItemQuantity)
                  .append(", ")
                  .append(rank)
                  .append(", ")
                  .append((int)quantity)
                  .append(", ")
                  .append((int)buyable)
                  .append(", ")
                  .append(category)
                  .append(");\r\n");
            }
         }

         File outfile = new File("ShopAnalyzer");
         outfile.mkdir();
         FileOutputStream out = new FileOutputStream(outfile + "/" + shopid + ".txt", false);
         out.write(sb.toString().getBytes());
         all.append(sb.toString());
         if (size == 1) {
            System.out.println(sb.toString());
         }
      }

      File outfile = new File("ShopAnalyzer");
      outfile.mkdir();
      FileOutputStream out = new FileOutputStream(outfile + "/all.txt", false);
      out.write(all.toString().getBytes());
   }
}
