package objects.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import objects.users.stats.SecondaryStatFlag;

public class FlagTools {
   public static void main(String[] args) {
      int[] DecodeForRemote = new int[]{
         81,
         95,
         96,
         98,
         352,
         91,
         209,
         94,
         93,
         106,
         205,
         107,
         108,
         204,
         189,
         192,
         190,
         191,
         92,
         92,
         101,
         84,
         90,
         109,
         127,
         115,
         257,
         258,
         116,
         122,
         128,
         137,
         129,
         131,
         132,
         133,
         134,
         135,
         136,
         147,
         149,
         150,
         151,
         153,
         156,
         157,
         206,
         187,
         160,
         162,
         175,
         164,
         176,
         271,
         185,
         193,
         200,
         208,
         141,
         213,
         220,
         222,
         224,
         232,
         237,
         256,
         239,
         270,
         282,
         240,
         245,
         439,
         248,
         249,
         251,
         254,
         272,
         274,
         273,
         280,
         281,
         283,
         284,
         286,
         507,
         287,
         296,
         152,
         278,
         291,
         292,
         293,
         294,
         294,
         295,
         297,
         298,
         299,
         300,
         301,
         302,
         303,
         304,
         219,
         275,
         161,
         309,
         139,
         310,
         311,
         312,
         312,
         314,
         315,
         316,
         320,
         325,
         327,
         329,
         332,
         363,
         333,
         334,
         335,
         336,
         337,
         341,
         342,
         343,
         344,
         346,
         356,
         183,
         364,
         365,
         366,
         366,
         350,
         275,
         165,
         380,
         425,
         385,
         328,
         401,
         402,
         410,
         487,
         488,
         489,
         490,
         491,
         492,
         407,
         408,
         420,
         247,
         450,
         457,
         458,
         458,
         463,
         468,
         476,
         474,
         475,
         693,
         694,
         259,
         260,
         261,
         262,
         263,
         482,
         422,
         498,
         499,
         483,
         497,
         375,
         512,
         511,
         510,
         509,
         97,
         516,
         517,
         518,
         519,
         520,
         521,
         522,
         523,
         524,
         514,
         525,
         525,
         526,
         527,
         528,
         538,
         543,
         551,
         526,
         560,
         266,
         576,
         579,
         580,
         581,
         582,
         583,
         584,
         585,
         589,
         594,
         599,
         572,
         573,
         605,
         606,
         615,
         617,
         618,
         619,
         620,
         621,
         622,
         623,
         624,
         625,
         626,
         627,
         628,
         629,
         634,
         635,
         645,
         633,
         648,
         656,
         671,
         673,
         681,
         682,
         683,
         696,
         698,
         701,
         702,
         117,
         181,
         359,
         179,
         705,
         706,
         423,
         642,
         454,
         521,
         522,
         342,
         432,
         434,
         450,
         468,
         482,
         422,
         423,
         424,
         498,
         270,
         514,
         261,
         533,
         574,
         578,
         580,
         512,
         247,
         551,
         95,
         594,
         606,
         615,
         620,
         621,
         622,
         623,
         624,
         625,
         626,
         627,
         628,
         629,
         634,
         635,
         633,
         117,
         673
      };
      CheckCTS(DecodeForRemote);
   }

   private static void SendOpcodeValue(int start, int diff) {
      SendOpcodeValue(start, -1, diff);
   }

   private static void SendOpcodeValue(int start, int end, int diff) {
      OpcodeValue(false, start, end, diff);
   }

   private static void RecvOpcodeValue(int start, int diff) {
      RecvOpcodeValue(start, -1, diff);
   }

   private static void RecvOpcodeValue(int start, int end, int diff) {
      OpcodeValue(true, start, end, diff);
   }

   private static void OpcodeValue(boolean recv, int start, int end, int diff) {
      if (recv) {
         RecvPacketOpcode.reloadValues();
         RecvPacketOpcode[] opcodes = RecvPacketOpcode.values();
         List<Short> iopcodes = new ArrayList<>();

         for (RecvPacketOpcode opcode : opcodes) {
            iopcodes.add(opcode.getValue());
         }

         Collections.sort(iopcodes);

         for (short opcode : iopcodes) {
            if (opcode >= start && (end == -1 || end <= opcode)) {
               System.out.println(RecvPacketOpcode.getOpcodeName(opcode) + " = " + (opcode + diff));
            }
         }
      } else {
         SendPacketOpcode.reloadValues();
         SendPacketOpcode[] opcodes = SendPacketOpcode.values();
         List<Short> iopcodes = new ArrayList<>();

         for (SendPacketOpcode opcodex : opcodes) {
            iopcodes.add(opcodex.getValue());
         }

         Collections.sort(iopcodes);

         for (short opcodex : iopcodes) {
            if (opcodex >= start && (end == -1 || end <= opcodex)) {
               System.out.println(SendPacketOpcode.getOpcodeName(opcodex) + " = " + (opcodex + diff));
            }
         }
      }
   }

   private static void CTSValue(int start, int diff) {
      CTSValue(start, -1, diff);
   }

   private static void CTSValue(int start, int end, int diff) {
      for (SecondaryStatFlag flag : SecondaryStatFlag.values()) {
         if (flag.getBit() >= start && (end == -1 || end <= flag.getBit())) {
            System.out.println(flag.toString() + " (" + (flag.getBit() + diff) + "),");
         }
      }
   }

   private static void CheckCTS(int[] values) {
      for (int value : values) {
         System.out.println(SecondaryStatFlag.getByBit(value).toString() + " //" + value);
      }
   }
}
