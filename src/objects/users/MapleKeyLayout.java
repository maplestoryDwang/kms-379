package objects.users;

import database.DBConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import network.encode.PacketEncoder;
import objects.utils.Pair;

public class MapleKeyLayout implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private boolean changed = false;
   private Map<Integer, Pair<Byte, Integer>> keymap;

   public MapleKeyLayout() {
      this.keymap = Collections.synchronizedMap(new HashMap<>());
   }

   public MapleKeyLayout(HashMap<Integer, Pair<Byte, Integer>> keys) {
      this.keymap = Collections.synchronizedMap(keys);
   }

   public final Map<Integer, Pair<Byte, Integer>> Layout() {
      this.changed = true;
      return this.keymap;
   }

   public final void unchanged() {
      this.changed = false;
   }

   public final void encode(PacketEncoder packet) {
      for (int x = 0; x < 89; x++) {
         Pair<Byte, Integer> binding = this.keymap.get(x);
         if (binding != null) {
            packet.write(binding.getLeft());
            packet.writeInt(binding.getRight());
         } else {
            packet.write(0);
            packet.writeInt(0);
         }
      }

      for (int i = 0; i < 10; i++) {
         Pair<Byte, Integer> binding = this.keymap.get(i + 102);
         if (binding != null) {
            packet.write(binding.getLeft());
            packet.writeInt(binding.getRight());
         } else {
            packet.write(0);
            packet.writeInt(0);
         }
      }
   }

   public final void saveKeys(int index, int charid) {
      if (this.changed) {
         if (this.keymap != null && !this.keymap.isEmpty()) {
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("DELETE FROM keymap WHERE characterid = ? AND `index` = ?");
               ps.setInt(1, charid);
               ps.setInt(2, index);
               ps.execute();
               ps.close();
               ps = con.prepareStatement("INSERT INTO keymap (`id`, `characterid`, `key`, `type`, `action`, `index`) VALUES (DEFAULT, ?, ?, ?, ?, ?)");
               ps.setInt(1, charid);
               Integer[] keys = this.keymap.keySet().toArray(new Integer[this.keymap.size()]);

               for (Integer key : keys) {
                  Pair<Byte, Integer> value = this.keymap.get(key);
                  if (value != null) {
                     ps.setInt(2, key);
                     ps.setByte(3, value.getLeft());
                     ps.setInt(4, value.getRight());
                     ps.setInt(5, index);
                     ps.executeUpdate();
                  }
               }

               ps.close();
            } catch (SQLException var13) {
            }
         }
      }
   }
}
