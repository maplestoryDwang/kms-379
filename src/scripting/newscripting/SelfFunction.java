package scripting.newscripting;

import java.util.List;
import scripting.ScriptMessageFlag;

public class SelfFunction {
   public ScriptConversation sc = null;

   public SelfFunction(ScriptConversation sc) {
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
      this.getSC().addSay(str);
   }

   public void say(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().addSay(str, flagValue);
   }

   public void sayReplacedNpc(String str, int templateID, ScriptMessageFlag... flags) {
      this.sayReplacedNpc(str, templateID, 1, flags);
   }

   public void sayReplacedNpc(String str, int templateID, int dlgType, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().addSayReplacedTemplate(str, templateID, (byte)dlgType, flagValue);
   }

   public void sayOk(String str) {
      this.getSC().sayOk(str);
   }

   public void sayOk(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().sayOk(str, flagValue);
   }

   public void sayOk(String str, int npcid, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      this.getSC().sayOk(str, flagValue, npcid);
   }

   public int askYesNo(String str) {
      return this.getSC().askYesNo(str);
   }

   public int askYesNo(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askYesNo(str, flagValue);
   }

   public int askYesNo(String str, int customNpcID, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askYesNo(str, flagValue, customNpcID);
   }

   public int askSelectMenu(List<String> args, int uiType, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askSelectMenu(flagValue, uiType, args);
   }

   public int askMenu(String str) {
      return this.getSC().askMenu(str);
   }

   public int askMenu(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askMenu(str, flagValue);
   }

   public int askMenuReplacedNpc(String str, int templateID, int dlgType, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askMenu(str, templateID, dlgType, flagValue);
   }

   public int askAccept(String str) {
      return this.getSC().askAccept(str);
   }

   public int askAccept(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askAccept(str, flagValue);
   }

   public int askAccept(String str, int customNpcID, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askAccept(str, flagValue, customNpcID);
   }

   public int askNumber(String str, int def, int min, int max, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askNumber(str, def, min, max, flagValue);
   }

   public String askText(String str) {
      return this.askText(str, ScriptMessageFlag.None);
   }

   public String askText(String str, ScriptMessageFlag... flags) {
      int flagValue = 0;

      for (ScriptMessageFlag flag : flags) {
         flagValue |= flag.getFlag();
      }

      return this.getSC().askText(str, flagValue);
   }

   public int askAvatar(String str, int... avatars) {
      int flagValue = 0;
      return this.getSC().askAvatar(str, avatars);
   }
}
