package objects.utils;

import database.DBConfig;
import database.DBConnection;
import database.DBEventManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatValueHolder;

public class Test {
   public static byte[] getHash(byte[] input) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         return md.digest(input);
      } catch (NoSuchAlgorithmException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static byte[] getHash(InputStream input) throws IOException {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         int read = -1;
         byte[] buffer = new byte[1024];

         while ((read = input.read(buffer)) != -1) {
            md.update(buffer, 0, read);
         }

         return md.digest();
      } catch (NoSuchAlgorithmException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static byte[] getHash(File file) throws IOException {
      byte[] hash = null;
      BufferedInputStream bis = null;

      try {
         bis = new BufferedInputStream(new FileInputStream(file));
         hash = getHash(bis);
      } finally {
         if (bis != null) {
            try {
               bis.close();
            } catch (IOException var9) {
            }
         }
      }

      return hash;
   }

   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(DBConfig.isGanglim ? 12 : 6);
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT sub_mission FROM `achievement_missions`");
         rs = ps.executeQuery();

         while (rs.next()) {
            String value = rs.getString("sub_mission");
            String[] splits = value.split("=");
            if (splits.length > 1) {
               try {
                  int e = Integer.parseInt(splits[1]);
               } catch (NumberFormatException var19) {
                  System.out.println(value);
               }
            }
         }
      } catch (SQLException var21) {
         var21.printStackTrace();
      } finally {
         try {
            if (rs != null) {
               rs.close();
               ResultSet var24 = null;
            }

            if (ps != null) {
               ps.close();
               PreparedStatement var23 = null;
            }
         } catch (SQLException var17) {
         }
      }
   }

   public static void test(int skillID, SecondaryStatFlag flag) {
      for (int i = 0; i < 15; i++) {
         System.out.println(Randomizer.isSuccess(500, 1000));
      }

      Map<SecondaryStatFlag, List<SecondaryStatValueHolder>> holders = new EnumMap<>(SecondaryStatFlag.class);
      SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(1);
      List<SecondaryStatValueHolder> holder = new LinkedList<>();
      SecondaryStatValueHolder h = new SecondaryStatValueHolder(effect, 0L, null, 0, 0, 0);
      holder.add(h);
      holders.put(flag, holder);
      if (holders.containsKey(flag)) {
         List<SecondaryStatValueHolder> list = holders.get(flag);
         List<SecondaryStatValueHolder> toRemoves = new ArrayList<>();
         list.stream().collect(Collectors.toList()).forEach(l -> {
            if (l.effect.getSourceId() == skillID) {
               toRemoves.add(l);
            }
         });
         toRemoves.stream().collect(Collectors.toList()).forEach(l -> list.remove(l));
         list.add(h);
      } else {
         List<SecondaryStatValueHolder> l = new LinkedList<>();
         l.add(h);
         holders.put(flag, l);
      }

      List<SecondaryStatValueHolder> aa = holders.get(flag);
      aa.add(h);
      System.out.println("ㅋㅋ");
   }
}
