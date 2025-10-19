package objects.fields.fieldset;

import constants.ServerConstants;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import network.game.GameServer;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Timer;

public abstract class FieldSetInstance {
   protected Properties Var = new Properties();
   protected int channel;
   protected FieldSet fs;
   public FieldSetInstanceMap fsim;
   public MapleCharacter leader;
   public List<Integer> userList;
   public boolean dispose;
   public String difficulty;
   public boolean isPracticeMode = false;
   protected int fieldSeteventTime;
   private long fieldSetStartTime;
   protected long fieldSetEndTime;
   protected int remainingTime;
   protected HashMap<String, ScheduledFuture<?>> eventSchedules = new HashMap<>();

   public FieldSetInstance(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      this.fs = fs;
      this.fsim = fsim;
      this.leader = leader;
   }

   public String getVar(String key) {
      return this.Var.getProperty(key, "0");
   }

   public void setVar(String key, String value) {
      this.Var.put(key, value);
   }

   public abstract void init(int var1);

   public void timeOut(int time) {
      if (this.eventSchedules.get("timeOut") != null) {
         this.eventSchedules.get("timeOut").cancel(false);
         this.eventSchedules.remove("timeOut");
      }

      this.eventSchedules.put("timeOut", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FieldSetInstance.this.dispose) {
               for (Integer map : FieldSetInstance.this.fsim.instances) {
                  Field f = FieldSetInstance.this.field(map);
                  if (f != null) {
                     for (MapleCharacter chr : f.getCharacters()) {
                        if (FieldSetInstance.this.userList.contains(chr.getId())) {
                           try {
                              chr.changeMap(chr.getMap().getForcedReturnMap());
                           } catch (Exception var7) {
                              chr.changeMap(ServerConstants.TownMap);
                           }
                        }
                     }
                  }
               }
            }
         }
      }, time));
   }

   public void restartTimeOut(int time) {
      this.fieldSeteventTime = time;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);

      for (Integer map : this.fsim.instances) {
         for (MapleCharacter chr : this.field(map).getCharacters()) {
            if (this.userList.contains(chr.getId())) {
               chr.send(CField.getClock(this.remainingTime));
            }
         }
      }

      this.timeOut(time);
   }

   public void restartTimeOutNoClock(int time) {
      this.fieldSeteventTime = time;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);
      this.timeOut(time);
   }

   public void initDeathCount(int count) {
      for (Integer userId : this.userList) {
         MapleCharacter user = GameServer.getInstance(this.channel).getPlayerStorage().getCharacterById(userId);
         if (user != null) {
            user.setDeathCount(count);
            user.setDecrementDeathCount(0);
         }
      }
   }

   public void userEnter(MapleCharacter user) {
      if (this.fieldSeteventTime > 0) {
         this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);
         user.send(CField.getClock(this.remainingTime));
      }

      int deathCount = user.getDeathCount();
      if (deathCount > 0) {
         user.deathCount();
      }
   }

   public void userLeave(MapleCharacter user, Field to) {
      int userCount = 0;
      if (to.getFieldSetInstance() != null && !to.getFieldSetInstance().equals(this) || to.getFieldSetInstance() == null) {
         for (Integer map : this.fsim.instances) {
            for (MapleCharacter chr : this.field(map).getCharacters()) {
               if (this.userList.contains(chr.getId())) {
                  userCount++;
               }
            }
         }

         if (userCount == 0) {
            this.dispose = true;
            this.Var.clear();
            List<String> keys = new ArrayList<>();

            for (String key : this.eventSchedules.keySet()) {
               keys.add(key);
            }

            for (String key : keys) {
               if (this.eventSchedules.get(key) != null) {
                  this.eventSchedules.get(key).cancel(false);
                  this.eventSchedules.remove(key);
               }
            }
         }
      }
   }

   public void userDead(MapleCharacter user) {
      int deathCount = user.getDeathCount();
      if (deathCount > 0) {
         user.setDeathCount(deathCount - 1);
         user.setDecrementDeathCount(user.getDecrementDeathCount() + 1);
         if (user.getDeathCount() == 0) {
            user.setClock(30);
         }
      }
   }

   public void userLeftParty(MapleCharacter user) {
      user.changeMap(user.getMap().getForcedReturnMap());
      int userCount = 0;

      for (Integer m : this.fsim.instances) {
         for (MapleCharacter c : this.field(m).getCharacters()) {
            if (this.userList.contains(c.getId())) {
               userCount++;
            }
         }
      }

      if (userCount < this.fs.minMember) {
         this.expelAllPlayer();
      }
   }

   public void userDisbandParty() {
      this.expelAllPlayer();
   }

   public void userDisconnected(MapleCharacter user) {
      int userCount = 0;

      for (Integer m : this.fsim.instances) {
         for (MapleCharacter c : this.field(m).getCharacters()) {
            if (this.userList.contains(c.getId())) {
               userCount++;
            }
         }
      }

      if (userCount < this.fs.minMember) {
         this.expelAllPlayer();
      }
   }

   public abstract void mobDead(MapleMonster var1);

   public void mobChangeHP(MapleMonster mob) {
   }

   protected void updateOfflineBossLimit(int playerId, int questId, String key, String value) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT `customData` FROM questinfo WHERE characterid = ? and quest = ?");
         ps.setInt(1, playerId);
         ps.setInt(2, questId);
         ResultSet rs = ps.executeQuery();
         boolean f = false;

         while (rs.next()) {
            f = true;
            String customData = rs.getString("customData");
            String[] v = customData.split(";");
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int count = 0;
            boolean a = false;

            for (String v_ : v) {
               String[] cd = v_.split("=");
               if (!cd[0].equals(key)) {
                  sb.append(cd[0]);
                  sb.append("=");
                  if (cd.length > 1) {
                     sb.append(cd[1]).append(";");
                  }
               } else {
                  a = true;
                  if (key.equals("count")) {
                     count = Integer.parseInt(cd[1].replace(";", ""));
                  }
               }
            }

            sb.append(key);
            sb.append("=");
            if (a && key.equals("count")) {
               sb.append(count + 1);
            } else {
               sb.append(value);
            }

            sb.append(";");
            PreparedStatement ps2 = con.prepareStatement("UPDATE questinfo SET customData = ? WHERE characterid = ? and quest = ?");
            ps2.setString(1, sb.toString());
            ps2.setInt(2, playerId);
            ps2.setInt(3, questId);
            ps2.executeUpdate();
            ps2.close();
         }

         if (!f) {
            PreparedStatement ps2 = con.prepareStatement("INSERT INTO questinfo (characterid, quest, customData, date) VALUES (?, ?, ?, ?)");
            ps2.setInt(1, playerId);
            ps2.setInt(2, questId);
            ps2.setString(3, key + "=" + value + ";");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String time = sdf.format(Calendar.getInstance().getTime());
            ps2.setString(4, time);
            ps2.executeQuery();
            ps2.close();
         }

         rs.close();
         ps.close();
      } catch (SQLException var23) {
         var23.printStackTrace();
      }
   }

   protected void expelAllPlayer() {
      for (Integer map : this.fsim.instances) {
         for (MapleCharacter chr : this.field(map).getCharacters()) {
            if (this.userList.contains(chr.getId())) {
               chr.changeMap(chr.getMap().getForcedReturnMap());
            }
         }
      }
   }

   public Field field(int map) {
      return GameServer.getInstance(this.channel).getMapFactory().getMap(map);
   }

   public long getFieldSetStartTime() {
      return this.fieldSetStartTime;
   }

   public void setFieldSetStartTime(long fieldSetStartTime) {
      this.fieldSetStartTime = fieldSetStartTime;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(String difficulty) {
      this.difficulty = difficulty;
   }
}
