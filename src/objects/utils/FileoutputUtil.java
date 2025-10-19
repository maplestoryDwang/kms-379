package objects.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FileoutputUtil {
   public static final String Acc_Stuck = "Log_AccountStuck.rtf";
   public static final String GameServerLogIn_Error = "Log_GameServerLogIn_Error.rtf";
   public static final String Login_Error = "Log_Login_Error.rtf";
   public static final String Pinkbean_Log = "Log_Pinkbean.rtf";
   public static final String ScriptEx_Log = "Log_Script_Except.rtf";
   public static final String PacketEx_Log = "Log_Packet_Except.rtf";
   public static final String Donator_Log = "Log_Donator.rtf";
   public static final String Hacker_Log = "Log_Hacker.rtf";
   public static final String Connector_Log = "Log_ConnectorLog.rtf";
   public static final String Movement_Log = "Log_Movement.rtf";
   public static final String CommandEx_Log = "Log_Command_Except.rtf";
   public static final String Damage_Log = "Log_Damage_Except.rtf";
   public static final String Disconnect_Log = "Log_Disconnect_Except.rtf";
   public static final String PlayerUpdate_Log = "Log_PlayerUpdate_Except.rtf";
   public static final String MapUpdate_Log = "Log_MapUpdate_Except.rtf";
   public static final String Skill_Log = "Log_Skill_Except.rtf";
   public static final String FieldItem_Log = "Log_FieldItem_Except.rtf";
   public static final String EventField_Log = "Log_EventField_Except.rtf";
   public static final String Respawn_Log = "Log_Respawn_Except.rtf";
   public static final String FieldUpdate_Log = "Log_FieldUpdate_Except.rtf";
   public static final String MiniGame_Log = "Log_MiniGame_Except.rtf";
   public static final String Player_Error_Log = "Log_Player_Except.rtf";
   public static final String FieldSetUpdate_Log = "Log_FieldSetUpdate_Except.rtf";
   public static final String DBProcessor_Error_Log = "Log_DBProcessor_Except.rtf";
   public static final String Query_NotInserted_Log = "Log_NotInserted.rtf";
   public static final String DamageParseError_Log = "Log_DamageParseError.rtf";
   private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static final SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");
   private static String lastGuildSaveFile = "";
   private static FileOutputStream guildSaveStream = null;

   public static void log(String file, String msg) {
      log(file, msg, true);
   }

   public static void log(String file, String msg, boolean useTimeStamp) {
      FileOutputStream out = null;

      try {
         out = new FileOutputStream(file, true);
         if (useTimeStamp) {
            out.write(("\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n").getBytes());
         }

         out.write(msg.getBytes());
         out.write("\r\n".getBytes());
      } catch (IOException var13) {
      } finally {
         try {
            if (out != null) {
               out.close();
            }
         } catch (IOException var12) {
         }
      }
   }

   public static void guildLog(String file, String msg) {
      try {
         if (guildSaveStream == null) {
            lastGuildSaveFile = file;
            guildSaveStream = new FileOutputStream(file, true);
         } else if (!lastGuildSaveFile.equals(file)) {
            try {
               guildSaveStream.close();
            } catch (Exception var3) {
            }

            lastGuildSaveFile = file;
            guildSaveStream = new FileOutputStream(file, true);
         }

         guildSaveStream.write((msg + "\r\n").getBytes());
      } catch (Exception var4) {
      }
   }

   public static void outputFileErrorReason(String file, String errorReason, Throwable t) {
      FileOutputStream out = null;

      try {
         out = new FileOutputStream("./ErrorLog/" + file, true);
         out.write(("\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n").getBytes());
         out.write(("에러원인 : " + errorReason + "\r\n").getBytes());
         out.write(getString(t).getBytes());
         out.write("\r\n".getBytes());
      } catch (IOException var13) {
      } finally {
         try {
            if (out != null) {
               out.close();
            }
         } catch (IOException var12) {
         }
      }
   }

   public static void outputFileError(String file, Throwable t) {
      FileOutputStream out = null;

      try {
         out = new FileOutputStream("./ErrorLog/" + file, true);
         out.write(("\n------------------------ " + CurrentReadable_Time() + " ------------------------\n").getBytes());
         out.write(getString(t).getBytes());
         out.write("\n".getBytes());
      } catch (IOException var12) {
      } finally {
         try {
            if (out != null) {
               out.close();
            }
         } catch (IOException var11) {
         }
      }
   }

   public static String CurrentReadable_Date() {
      return sdf_.format(Calendar.getInstance().getTime());
   }

   public static String CurrentReadable_Time() {
      return sdf.format(Calendar.getInstance().getTime());
   }

   public static String CurrentReadable_TimeGMT() {
      return sdfGMT.format(new Date());
   }

   public static String getString(Throwable e) {
      String retValue = null;
      StringWriter sw = null;
      PrintWriter pw = null;

      try {
         sw = new StringWriter();
         pw = new PrintWriter(sw);
         e.printStackTrace(pw);
         retValue = sw.toString();
      } finally {
         try {
            if (pw != null) {
               pw.close();
            }

            if (sw != null) {
               sw.close();
            }
         } catch (IOException var10) {
         }
      }

      return retValue;
   }

   static {
      sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
   }
}
