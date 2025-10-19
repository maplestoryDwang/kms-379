package scripting.newscripting;

import objects.context.party.Party;
import scripting.ScriptMessageFlag;

public class TargetFunction {
   public ScriptConversation sc = null;

   public TargetFunction(ScriptConversation sc) {
      this.sc = sc;
   }

   private ScriptConversation getSC() {
      if (this.sc == null) {
         throw new RuntimeException("NULL ScriptConversation");
      } else {
         return this.sc;
      }
   }

   public void say(String str) {
      this.getSC().addSay(str, ScriptMessageFlag.NpcReplacedByUser.getFlag());
   }

   public void say(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().addSay(str, flagValue);
   }

   public void sayReplacedNpc(String str, int templateID, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().addSayReplacedTemplate(str, templateID, flagValue);
   }

   public void sayOk(String str) {
      this.getSC().sayOk(str, ScriptMessageFlag.NpcReplacedByUser.getFlag());
   }

   public void sayOk(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().sayOk(str, flagValue, 0);
   }

   public void sayOk(String str, int npcid, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().sayOk(str, flagValue, npcid);
   }

   public int askYesNo(String str) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();
      return this.getSC().askYesNo(str, flagValue);
   }

   public int askYesNo(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askYesNo(str, flagValue);
   }

   public int askYesNo(String str, int customNpcID, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askYesNo(str, flagValue, customNpcID);
   }

   public int askMenu(String str) {
      return this.getSC().askMenu(str, ScriptMessageFlag.NpcReplacedByUser.getFlag());
   }

   public int askMenu(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askMenu(str, flagValue);
   }

   public int askAccept(String str) {
      return this.getSC().askAccept(str, ScriptMessageFlag.NpcReplacedByUser.getFlag());
   }

   public int askAccept(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askAccept(str, flagValue);
   }

   public int askAccept(String str, int customNpcID, ScriptMessageFlag... flags) {
      int flagValue = 0;
      flagValue |= ScriptMessageFlag.NpcReplacedByUser.getFlag();

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askAccept(str, flagValue, customNpcID);
   }

   public int exchange(int... values) {
      return this.getSC().exchange(values);
   }

   public int exchange(int[][] values) {
      return this.getSC().exchange(values);
   }

   public String exchangeParty(int... values) {
      return this.getSC().exchangeParty(values);
   }

   public int getId() {
      if (this.sc == null) {
         return -1;
      } else {
         this.getSC().flushSay();
         return this.getSC().engine.getPlayer().getId();
      }
   }

   public int getMapId() {
      return this.sc == null ? -1 : this.getSC().engine.getPlayer().getMapId();
   }

   public Party getParty() {
      return this.sc == null ? null : this.getSC().engine.getPlayer().getParty();
   }

   public void registerTransferField(int mapId) {
      if (this.sc != null) {
         this.getSC().registerTransferField(mapId);
      }
   }
}
