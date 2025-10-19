package scripting;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import objects.fields.ReactorDropEntry;
import objects.fields.gameobject.Reactor;
import objects.users.MapleClient;
import objects.utils.FileoutputUtil;

public class ReactorScriptManager extends AbstractScriptManager {
   private static final ReactorScriptManager instance = new ReactorScriptManager();
   private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

   public static final ReactorScriptManager getInstance() {
      return instance;
   }

   public final void act(MapleClient c, Reactor reactor) {
      try {
         if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage(5, "Reactor : " + reactor.getReactorId());
         }

         Invocable iv = this.getInvocable("reactor/" + reactor.getReactorId() + ".js", c);
         if (iv == null) {
            return;
         }

         ScriptEngine scriptengine = (ScriptEngine)iv;
         ReactorActionManager rm = new ReactorActionManager(c, reactor);
         scriptengine.put("rm", rm);
         iv.invokeFunction("act");
         Object var7 = null;
      } catch (Exception var6) {
         System.err.println("Error executing reactor script. ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + ":" + var6);
         FileoutputUtil.log(
            "Log_Script_Except.rtf",
            "Error executing reactor script. ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + ":" + var6
         );
      }
   }

   public final void destroy(MapleClient c, Reactor reactor) {
      try {
         if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage(5, "Reactor : " + reactor.getReactorId());
         }

         Invocable iv = this.getInvocable("reactor/" + reactor.getReactorId() + ".js", c);
         if (iv == null) {
            return;
         }

         ScriptEngine scriptengine = (ScriptEngine)iv;
         ReactorActionManager rm = new ReactorActionManager(c, reactor);
         scriptengine.put("rm", rm);
         iv.invokeFunction("destroy");
         Object var7 = null;
      } catch (Exception var6) {
         System.err.println("Error executing reactor script. ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + ":" + var6);
         FileoutputUtil.log(
            "Log_Script_Except.rtf",
            "Error executing reactor script. ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + ":" + var6
         );
      }
   }

   public final List<ReactorDropEntry> getDrops(int rid) {
      List<ReactorDropEntry> ret = this.drops.get(rid);
      if (ret != null) {
         return ret;
      } else {
         List<ReactorDropEntry> var23 = new LinkedList<>();
         PreparedStatement ps = null;
         ResultSet rs = null;
         DBConnection db = new DBConnection();

         label138: {
            List<ReactorDropEntry> var7;
            try (Connection con = DBConnection.getConnection()) {
               ps = con.prepareStatement("SELECT * FROM reactordrops WHERE reactorid = ?");
               ps.setInt(1, rid);
               rs = ps.executeQuery();

               while (rs.next()) {
                  var23.add(new ReactorDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("questid")));
               }

               rs.close();
               ps.close();
               break label138;
            } catch (SQLException var21) {
               System.err.println("Could not retrieve drops for reactor " + rid + var21);
               var7 = var23;
            } finally {
               try {
                  if (rs != null) {
                     rs.close();
                  }

                  if (ps != null) {
                     ps.close();
                  }
               } catch (SQLException var19) {
                  return var23;
               }
            }

            return var7;
         }

         this.drops.put(rid, var23);
         return var23;
      }
   }

   public final void clearDrops() {
      this.drops.clear();
   }
}
