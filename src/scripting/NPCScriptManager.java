package scripting;

import database.DBConfig;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import objects.quest.MapleQuest;
import objects.users.MapleClient;
import objects.utils.FileoutputUtil;

public class NPCScriptManager extends AbstractScriptManager {
   private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<>();
   private static final NPCScriptManager instance = new NPCScriptManager();

   public static final NPCScriptManager getInstance() {
      return instance;
   }

   public final void startMapScript(MapleClient c, int npc, String script) {
      Lock lock = c.getNPCLock();
      lock.lock();

      try {
         try {
            if (this.cms.containsKey(c)) {
               return;
            }

            Invocable iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "map/" + script + ".js", c);
            if (iv == null) {
               iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/notcoded.js", c);
               if (iv == null) {
                  this.dispose(c);
                  return;
               }
            }

            ScriptEngine scriptengine = (ScriptEngine)iv;
            NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte)-1, iv);
            this.cms.put(c, cm);
            scriptengine.put("cm", cm);
            c.getPlayer().setConversation(1);
            c.setClickedNPC();

            try {
               iv.invokeFunction("start");
            } catch (NoSuchMethodException var14) {
               iv.invokeFunction("action", (byte)1, (byte)0, 0);
            }

            Object var18 = null;
         } catch (NumberFormatException var15) {
            System.err.println("넘버포맷오류 영자님 구동기찍어주셈 : " + script);
            this.dispose(c);
         } catch (Exception var16) {
            System.err.println("Error executing NPC script, NPC ID : " + npc + " / " + script + "." + var16);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing NPC script, NPC ID : " + npc + " / " + (script != null ? script : "") + "." + var16);
            this.dispose(c);
         }
      } finally {
         lock.unlock();
      }
   }

   public final void start(MapleClient c, int npc) {
      this.start(c, npc, null, false);
   }

   public final void start(MapleClient c, int npc, String script, boolean customNpc) {
      Lock lock = c.getNPCLock();
      lock.lock();

      try {
         try {
            if ((this.cms.containsKey(c) || !c.canClickNPC()) && !c.isGm()) {
               return;
            }

            Invocable iv;
            if (script == null) {
               iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/" + npc + ".js", c);
               c.setLastUsedScriptName("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/" + npc + ".js");
            } else {
               iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "item/" + script + ".js", c);
               c.setLastUsedScriptName("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "item/" + script + ".js");
            }

            if (iv == null && script != null && customNpc) {
               c.setLastUsedScriptName("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/" + script + ".js");
               iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/" + script + ".js", c);
            }

            if (iv == null) {
               iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/notcoded.js", c);
               if (iv == null) {
                  this.dispose(c);
                  return;
               }
            }

            ScriptEngine scriptengine = (ScriptEngine)iv;
            NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte)-1, iv);
            if (script != null) {
               cm.script = script;
            }

            this.cms.put(c, cm);
            scriptengine.put("cm", cm);
            c.getPlayer().setConversation(1);
            c.setClickedNPC();

            try {
               iv.invokeFunction("start");
            } catch (NoSuchMethodException var15) {
               iv.invokeFunction("action", (byte)1, (byte)0, 0);
            }

            return;
         } catch (NumberFormatException var16) {
            System.err.println("넘버포맷오류 영자님 구동기찍어주셈 : " + script);
            this.dispose(c);
         } catch (Exception var17) {
            System.err.println("Error executing NPC script, NPC ID : " + npc + " / " + script + "." + var17);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing NPC script, NPC ID : " + npc + " / " + (script != null ? script : "") + "." + var17);
            if (DBConfig.isGanglim && c.isGm()) {
               c.getPlayer().dropMessage(5, "Error! " + var17.toString());
            }

            this.dispose(c);
         }
      } finally {
         lock.unlock();
      }
   }

   public final void action(MapleClient c, byte mode, byte type, int selection) {
      if (mode != -1) {
         NPCConversationManager cm = this.cms.get(c);
         if (cm == null || cm.getLastMsg() > -1) {
            return;
         }

         Lock lock = c.getNPCLock();
         lock.lock();

         try {
            if (cm.pendingDisposal) {
               this.dispose(c);
            } else {
               c.setClickedNPC();
               cm.getIv().invokeFunction("action", mode, type, selection);
            }
         } catch (NumberFormatException var12) {
            System.err.println("넘버포맷오류 영자님 구동기찍어주셈 : " + cm.script);
            this.dispose(c);
         } catch (Exception var13) {
            System.err.println("Error executing NPC script. NPC ID : " + cm.getNpc() + " / " + c.getLastUsedScriptName() + ":" + var13);
            this.dispose(c);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing NPC script, NPC ID : " + cm.getNpc() + "." + var13);
         } finally {
            lock.unlock();
         }
      }
   }

   public final void action(MapleClient c, byte mode, byte type, int selection1, int selection2) {
      if (mode != -1) {
         NPCConversationManager cm = this.cms.get(c);
         if (cm == null || cm.getLastMsg() > -1) {
            return;
         }

         Lock lock = c.getNPCLock();
         lock.lock();

         try {
            if (cm.pendingDisposal) {
               this.dispose(c);
            } else {
               c.setClickedNPC();
               cm.getIv().invokeFunction("action", mode, type, selection1, selection2);
            }
         } catch (NumberFormatException var13) {
            System.err.println("넘버포맷오류 영자님 구동기찍어주셈 : " + cm.script);
            this.dispose(c);
         } catch (Exception var14) {
            System.err.println("Error executing NPC script. NPC ID : " + cm.getNpc() + ":" + var14);
            this.dispose(c);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing NPC script, NPC ID : " + cm.getNpc() + " / " + c.getLastUsedScriptName() + "." + var14);
         } finally {
            lock.unlock();
         }
      }
   }

   public final void startQuest(MapleClient c, int npc, int quest) {
      if (MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
         Lock lock = c.getNPCLock();
         lock.lock();

         try {
            if (this.cms.containsKey(c) || !c.canClickNPC()) {
               return;
            }

            Invocable iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "quest/" + quest + ".js", c);
            if (iv != null) {
               ScriptEngine scriptengine = (ScriptEngine)iv;
               NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte)0, iv);
               this.cms.put(c, cm);
               scriptengine.put("qm", cm);
               c.getPlayer().setConversation(1);
               c.setClickedNPC();
               iv.invokeFunction("start", (byte)1, (byte)0, 0);
               Object var13 = null;
               return;
            }

            this.dispose(c);
         } catch (Exception var11) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + var11);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + var11);
            this.dispose(c);
            return;
         } finally {
            lock.unlock();
         }
      }
   }

   public final void CompleteNpcSpeech(MapleClient c, int npc, int quest) {
      Lock lock = c.getNPCLock();
      lock.lock();

      try {
         if (this.cms.containsKey(c) || !c.canClickNPC()) {
            return;
         }

         Invocable iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "quest/" + quest + ".js", c);
         if (iv != null) {
            ScriptEngine scriptengine = (ScriptEngine)iv;
            NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte)0, iv);
            this.cms.put(c, cm);
            scriptengine.put("qm", cm);
            c.getPlayer().setConversation(1);
            c.setClickedNPC();
            iv.invokeFunction("start", (byte)1, (byte)0, 0);
            Object var13 = null;
            return;
         }

         this.dispose(c);
      } catch (Exception var11) {
         System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + var11);
         FileoutputUtil.log("Log_Script_Except.rtf", "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + var11);
         this.dispose(c);
         return;
      } finally {
         lock.unlock();
      }
   }

   public final void startQuest(MapleClient c, byte mode, byte type, int selection) {
      Lock lock = c.getNPCLock();
      NPCConversationManager cm = this.cms.get(c);
      if (cm != null && cm.getLastMsg() <= -1) {
         lock.lock();

         try {
            if (cm.pendingDisposal) {
               this.dispose(c);
            } else {
               c.setClickedNPC();
               cm.getIv().invokeFunction("start", mode, type, selection);
            }
         } catch (Exception var11) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + var11);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + var11);
            this.dispose(c);
         } finally {
            lock.unlock();
         }
      }
   }

   public final void endQuest(MapleClient c, int npc, int quest, boolean customEnd) {
      if (customEnd || MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
         Lock lock = c.getNPCLock();
         lock.lock();

         try {
            if (this.cms.containsKey(c) || !c.canClickNPC()) {
               return;
            }

            Invocable iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "quest/" + quest + ".js", c);
            if (iv != null) {
               ScriptEngine scriptengine = (ScriptEngine)iv;
               NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte)1, iv);
               this.cms.put(c, cm);
               scriptengine.put("qm", cm);
               c.getPlayer().setConversation(1);
               c.setClickedNPC();
               iv.invokeFunction("end", (byte)1, (byte)0, 0);
               return;
            }

            this.dispose(c);
         } catch (Exception var12) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + var12);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + var12);
            this.dispose(c);
            return;
         } finally {
            lock.unlock();
         }
      }
   }

   public final void endQuest(MapleClient c, byte mode, byte type, int selection) {
      Lock lock = c.getNPCLock();
      NPCConversationManager cm = this.cms.get(c);
      if (cm != null && cm.getLastMsg() <= -1) {
         lock.lock();

         try {
            if (cm.pendingDisposal) {
               this.dispose(c);
            } else {
               c.setClickedNPC();
               cm.getIv().invokeFunction("end", mode, type, selection);
            }
         } catch (Exception var11) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + var11);
            FileoutputUtil.log("Log_Script_Except.rtf", "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + var11);
            this.dispose(c);
         } finally {
            lock.unlock();
         }
      }
   }

   public final void putConversationManager(MapleClient c, NPCConversationManager cms) {
      this.cms.put(c, cms);
   }

   public final void dispose(MapleClient c) {
      this.cms.remove(c);
      if (c.getPlayer() != null) {
         c.getPlayer().setSelectDungeon(false);
      }

      c.removeScriptEngine();
      if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
         c.getPlayer().setConversation(0);
      }
   }

   public final NPCConversationManager getCM(MapleClient c) {
      return this.cms.get(c);
   }
}
