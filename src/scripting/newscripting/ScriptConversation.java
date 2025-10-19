package scripting.newscripting;

import constants.GameConstants;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.item.Equip;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.FileoutputUtil;
import scripting.GameObjectType;
import scripting.ScriptMessageFlag;
import scripting.ScriptMessageType;

public class ScriptConversation implements Runnable {
   final Object gate = new Object();
   int sayIndex = 0;
   final List<ScriptSayEntry> sayQueue = new LinkedList<>();
   String answerText = "";
   int answerMenu = 0;
   boolean stop = false;
   volatile Method method;
   volatile ScriptEngineNPC engine;
   int lastmsg = -1;
   int selection = -1;
   final String scriptName;
   String text = "";
   final boolean end = false;
   Runnable afterEnd = null;

   public ScriptConversation(Method method, ScriptEngineNPC engine) {
      this.method = method;
      this.engine = engine;
      this.scriptName = method.getName();
   }

   public void setAfterEnd(Runnable e) {
      this.afterEnd = e;
   }

   public void forceStop() {
      this.stop = true;
      this.selection = -1;
      this.text = "";
      this.method = null;
      this.engine = null;

      try {
         synchronized (this.gate) {
            this.gate.notify();
         }
      } catch (Exception var4) {
      }
   }

   public boolean isStop() {
      return this.stop;
   }

   @Override
   public void run() {
      if (this.method != null) {
         try {
            this.method.invoke(this.engine);
         } catch (Exception var7) {
         }

         if (!this.stop && this.engine != null) {
            MapleCharacter chr = this.engine.getPlayer();
            this.flushSay();
            if (this.engine != null) {
               this.engine.end();
            }

            chr.setScriptThread(null);
            Lock lock = chr.getClient().getNPCLock();
            lock.lock();

            try {
               if (this.afterEnd != null) {
                  this.afterEnd.run();
               }

               this.afterEnd = null;
            } finally {
               lock.unlock();
            }
         }
      }
   }

   public void flushSay() {
      while (this.sayIndex < this.sayQueue.size() && !this.stop) {
         boolean prev;
         boolean next;
         if (this.sayIndex != 0 && this.sayIndex + 1 == this.sayQueue.size()) {
            prev = true;
            next = true;
         } else if (this.sayIndex == 0 && this.sayQueue.size() >= 2) {
            prev = false;
            next = true;
         } else if (this.sayIndex > 0 && this.sayIndex + 1 < this.sayQueue.size()) {
            prev = true;
            next = true;
         } else {
            prev = false;
            next = true;
         }

         ScriptSayEntry entry = this.sayQueue.get(this.sayIndex);
         this.lastmsg = 0;
         PacketEncoder addPacket = new PacketEncoder();
         addPacket.write(prev);
         addPacket.write(next);
         addPacket.writeInt(0);
         if ((entry.getFlag() & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            entry.setCustomSpeakerTemplateID(entry.getnSpeakerTemplateID());
         }

         this.engine
            .getPlayer()
            .send(
               CField.NPCPacket.getScriptMessage(
                  entry.getnSpeakerTemplateID(),
                  entry.getCustomSpeakerTemplateID(),
                  entry.getnSpeakerTypeID(),
                  entry.getFlag(),
                  ScriptMessageType.Say,
                  entry.getDlgColor(),
                  entry.getMsg(),
                  addPacket
               )
            );

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var8) {
         }
      }

      this.sayIndex = 0;
      this.sayQueue.clear();
   }

   public void processSay(int mode) {
      switch (mode) {
         case 0:
            this.sayIndex--;
            break;
         case 1:
            this.sayIndex++;
            break;
         default:
            this.stop = true;
      }

      try {
         synchronized (this.gate) {
            this.gate.notify();
         }
      } catch (Exception var5) {
      }
   }

   public void processAnswer(int answer) {
      this.selection = answer;

      try {
         synchronized (this.gate) {
            this.gate.notify();
         }
      } catch (Exception var5) {
      }
   }

   public void processText(String answer) {
      this.text = answer;

      try {
         synchronized (this.gate) {
            this.gate.notify();
         }
      } catch (Exception var5) {
      }
   }

   public int getLastmsg() {
      return this.lastmsg;
   }

   public void setLastmsg(int lastmsg) {
      this.lastmsg = lastmsg;
   }

   public int getSelection() {
      return this.selection;
   }

   public void setSelection(int selection) {
      this.selection = selection;
   }

   public void addSay(String say) {
      this.sayQueue.add(new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.Say, 0, say));
   }

   public void addSay(String say, int flag) {
      ScriptSayEntry scriptSayEntry = new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.Say, flag, say);
      this.sayQueue.add(scriptSayEntry);
   }

   public void addSayReplacedTemplate(String say, int templacedID, int flag) {
      this.addSayReplacedTemplate(say, templacedID, (byte)1, flag);
   }

   public void addSayReplacedTemplate(String say, int templacedID, byte dlgType, int flag) {
      ScriptSayEntry scriptSayEntry = new ScriptSayEntry((byte)4, templacedID, ScriptMessageType.Say, flag, dlgType, say);
      this.sayQueue.add(scriptSayEntry);
   }

   public void sayOk(String str) {
      this.sayOk(str, ScriptMessageFlag.None.getFlag(), 0);
   }

   public void sayOk(String str, int flag) {
      this.sayOk(str, flag, 0);
   }

   public void sayOk(String str, int flag, int npcid) {
      this.flushSay();
      if (!this.stop) {
         ScriptSayEntry entry = new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.Say, flag, str);
         this.lastmsg = ScriptMessageType.Say.getType();
         PacketEncoder addPacket = new PacketEncoder();
         addPacket.write(false);
         addPacket.write(false);
         addPacket.writeInt(0);
         if ((flag & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            entry.setCustomSpeakerTemplateID(entry.getnSpeakerTemplateID());
         }

         this.engine
            .getPlayer()
            .send(
               CField.NPCPacket.getScriptMessage(
                  entry.getnSpeakerTemplateID(),
                  entry.getCustomSpeakerTemplateID(),
                  entry.getnSpeakerTypeID(),
                  entry.getFlag(),
                  ScriptMessageType.Say,
                  entry.getDlgColor(),
                  entry.getMsg(),
                  addPacket
               )
            );
      }
   }

   public int askYesNo(String str) {
      return this.askYesNo(str, ScriptMessageFlag.None.getFlag(), 0);
   }

   public int askYesNo(String str, int flag) {
      return this.askYesNo(str, flag, 0);
   }

   public int askYesNo(String str, int flag, int npcid) {
      this.flushSay();
      if (this.stop) {
         return -1;
      } else {
         this.lastmsg = ScriptMessageType.AskYesNo.getType();
         ScriptSayEntry sayEntry = new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.AskYesNo, flag, str);
         sayEntry.setDlgColor((byte)1);
         if (npcid > 0) {
            sayEntry.setCustomSpeakerTemplateID(npcid);
         }

         if ((sayEntry.getFlag() & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            sayEntry.setCustomSpeakerTemplateID(sayEntry.getnSpeakerTemplateID());
         }

         this.engine
            .getPlayer()
            .send(
               CField.NPCPacket.getScriptMessage(
                  sayEntry.getnSpeakerTemplateID(),
                  sayEntry.getCustomSpeakerTemplateID(),
                  GameObjectType.Npc,
                  sayEntry.getFlag(),
                  ScriptMessageType.AskYesNo,
                  sayEntry.getDlgColor(),
                  sayEntry.getMsg(),
                  null
               )
            );

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var8) {
         }

         return this.selection;
      }
   }

   public int askMenu(String str) {
      return this.askMenu(str, ScriptMessageFlag.None.getFlag());
   }

   public int askMenu(String str, int flag) {
      return this.askMenu(str, 0, 1, flag);
   }

   public int askMenu(String str, int dlgType, int flag) {
      return this.askMenu(str, 0, dlgType, flag);
   }

   public int askMenu(String str, int templateID, int dlgType, int flag) {
      this.flushSay();
      if (this.stop) {
         return -1;
      } else {
         try {
            this.lastmsg = ScriptMessageType.AskMenu.getType();
            ScriptSayEntry sayEntry = new ScriptSayEntry(
               (byte)4, templateID != 0 ? templateID : this.engine.getNpc().getId(), ScriptMessageType.AskMenu, flag, (byte)dlgType, str
            );
            if ((sayEntry.getFlag() & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
               sayEntry.setCustomSpeakerTemplateID(sayEntry.getnSpeakerTemplateID());
            }

            this.engine
               .getPlayer()
               .send(
                  CField.NPCPacket.getScriptMessage(
                     sayEntry.getnSpeakerTemplateID(),
                     sayEntry.getCustomSpeakerTemplateID(),
                     GameObjectType.Npc,
                     sayEntry.getFlag(),
                     ScriptMessageType.AskMenu,
                     sayEntry.getDlgColor(),
                     sayEntry.getMsg(),
                     null
                  )
               );
         } catch (Exception var9) {
            var9.printStackTrace();
         }

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var8) {
         }

         return this.selection;
      }
   }

   public int askAccept(String str) {
      return this.askAccept(str, ScriptMessageFlag.None.getFlag(), 0);
   }

   public int askAccept(String str, int flag) {
      return this.askAccept(str, flag, 0);
   }

   public int askAccept(String str, int flag, int npcid) {
      this.flushSay();
      if (this.stop) {
         return -1;
      } else {
         this.lastmsg = ScriptMessageType.AskAccept.getType();
         ScriptSayEntry sayEntry = new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.AskAccept, flag, str);
         sayEntry.setDlgColor((byte)1);
         if (npcid > 0) {
            sayEntry.setCustomSpeakerTemplateID(npcid);
         }

         if ((sayEntry.getFlag() & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            sayEntry.setCustomSpeakerTemplateID(sayEntry.getnSpeakerTemplateID());
         }

         this.engine
            .getPlayer()
            .send(
               CField.NPCPacket.getScriptMessage(
                  sayEntry.getnSpeakerTemplateID(),
                  sayEntry.getCustomSpeakerTemplateID(),
                  GameObjectType.Npc,
                  sayEntry.getFlag(),
                  ScriptMessageType.AskAccept,
                  sayEntry.getDlgColor(),
                  sayEntry.getMsg(),
                  null
               )
            );

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var8) {
         }

         return this.selection;
      }
   }

   public int askNumber(String str, int def, int min, int max, int flag) {
      this.flushSay();
      if (this.stop) {
         return -1;
      } else {
         this.lastmsg = ScriptMessageType.AskNumber.getType();
         ScriptSayEntry sayEntry = new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.AskNumber, flag, str);
         sayEntry.setDlgColor((byte)1);
         if ((sayEntry.getFlag() & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            sayEntry.setCustomSpeakerTemplateID(sayEntry.getnSpeakerTemplateID());
         }

         PacketEncoder addPacket = new PacketEncoder();
         addPacket.writeLong(def);
         addPacket.writeLong(min);
         addPacket.writeLong(max);
         addPacket.writeInt(0);
         this.engine
            .getPlayer()
            .send(
               CField.NPCPacket.getScriptMessage(
                  sayEntry.getnSpeakerTemplateID(),
                  sayEntry.getCustomSpeakerTemplateID(),
                  GameObjectType.Npc,
                  sayEntry.getFlag(),
                  ScriptMessageType.AskNumber,
                  sayEntry.getDlgColor(),
                  sayEntry.getMsg(),
                  addPacket
               )
            );

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var11) {
         }

         return this.selection;
      }
   }

   public String askText(String str, int flag) {
      this.flushSay();
      if (this.stop) {
         return "";
      } else {
         this.lastmsg = ScriptMessageType.AskText.getType();
         ScriptSayEntry sayEntry = new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.AskText, flag, str);
         sayEntry.setDlgColor((byte)1);
         if ((sayEntry.getFlag() & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            sayEntry.setCustomSpeakerTemplateID(sayEntry.getnSpeakerTemplateID());
         }

         this.engine.getPlayer().send(CField.NPCPacket.getNPCTalkText(sayEntry.getnSpeakerTemplateID(), sayEntry.getMsg()));

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var7) {
         }

         return this.text;
      }
   }

   public int askSelectMenu(int flag, int uiType, List<String> args) {
      this.flushSay();
      if (this.stop) {
         return -1;
      } else {
         this.lastmsg = ScriptMessageType.AskSelectMenu.getType();
         ScriptSayEntry sayEntry = new ScriptSayEntry(0, ScriptMessageType.AskSelectMenu, flag, "");
         sayEntry.setDlgColor((byte)0);
         PacketEncoder addPacket = new PacketEncoder();
         addPacket.writeInt(0);
         addPacket.writeInt(0);
         addPacket.write(uiType);
         addPacket.writeInt(0);
         addPacket.writeInt(args.size());

         for (String j : args) {
            addPacket.writeMapleAsciiString(j);
         }

         this.engine
            .getPlayer()
            .send(
               CField.NPCPacket.getScriptMessage(
                  sayEntry.getnSpeakerTemplateID(),
                  sayEntry.getCustomSpeakerTemplateID(),
                  GameObjectType.User,
                  sayEntry.getFlag(),
                  ScriptMessageType.AskSelectMenu,
                  sayEntry.getDlgColor(),
                  sayEntry.getMsg(),
                  addPacket
               )
            );

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var10) {
         }

         return this.selection;
      }
   }

   public int askAvatar(String str, int... avatar) {
      this.flushSay();
      if (this.stop) {
         return -1;
      } else {
         this.lastmsg = ScriptMessageType.AskAvatar.getType();
         ScriptSayEntry sayEntry = new ScriptSayEntry(this.engine.getNpc().getId(), ScriptMessageType.AskAvatar, 0, str);
         sayEntry.setDlgColor((byte)1);
         PacketEncoder addPacket = new PacketEncoder();
         addPacket.writeInt(0);
         addPacket.writeMapleAsciiString(str);
         addPacket.write(0);
         addPacket.write(avatar.length);

         for (int j : avatar) {
            addPacket.writeInt(j);
         }

         addPacket.writeInt(0);
         this.engine
            .getPlayer()
            .send(
               CField.NPCPacket.getScriptMessage(
                  sayEntry.getnSpeakerTemplateID(),
                  sayEntry.getCustomSpeakerTemplateID(),
                  GameObjectType.Npc,
                  sayEntry.getFlag(),
                  ScriptMessageType.AskAvatar,
                  sayEntry.getDlgColor(),
                  sayEntry.getMsg(),
                  addPacket
               )
            );

         try {
            synchronized (this.gate) {
               this.gate.wait();
            }
         } catch (Exception var11) {
            System.out.println("AskAvatar Err");
            var11.printStackTrace();
         }

         return this.selection;
      }
   }

   public void registerTransferField(int mapId) {
      this.flushSay();
      if (!this.stop) {
         Field field = this.engine.getClient().getChannelServer().getMapFactory().getMap(mapId);
         this.engine.getPlayer().changeMapNoSCEnd(field);
      }
   }

   public int exchange(int[][] values) {
      int[] arrays = new int[values.length * 2];
      int index = 0;

      for (int[] array : values) {
         arrays[index++] = array[0];
         arrays[index++] = array[1];
      }

      return this.exchange(arrays);
   }

   public int exchange(int... values) {
      this.flushSay();
      if (this.stop) {
         return -1;
      } else if (values.length % 2 > 0) {
         return -2;
      } else {
         HashMap<MapleInventoryType, Integer> slots = new HashMap<>();

         for (int i = 0; i < values.length; i++) {
            if (i % 2 == 0) {
               if (values[i + 1] > 0) {
                  MapleInventoryType type = GameConstants.getInventoryType(values[i]);
                  slots.putIfAbsent(type, 0);
                  slots.put(type, slots.get(type) + MapleInventoryManipulator.getNeedNumSlots(this.engine.getClient(), values[i], values[i + 1]));
               } else if (this.engine.getPlayer().getItemQuantity(values[i], false) < -values[i + 1]) {
                  return -1;
               }
            }
         }

         for (MapleInventoryType type : slots.keySet()) {
            if (this.engine.getPlayer().getInventory(type).isFull(slots.get(type) - 1)) {
               return -1;
            }
         }

         for (int ix = 0; ix < values.length; ix++) {
            if (ix % 2 == 0) {
               this.gainItem(values[ix], (short)values[ix + 1]);
            }
         }

         return 1;
      }
   }

   public String exchangeParty(int... values) {
      this.flushSay();
      if (this.stop) {
         return "stop";
      } else if (values.length % 2 > 0) {
         return "error";
      } else {
         String exMember = null;
         MapleCharacter partyLeader = this.engine.getPlayer();

         for (PartyMemberEntry partyMemberEntry : partyLeader.getParty().getPartyMemberList()) {
            MapleCharacter chr = partyLeader.getMap().getCharacterById(partyMemberEntry.getId());

            for (int i = 0; i < values.length; i++) {
               if (i % 2 == 0 && chr.getItemQuantity(values[i], false) < -values[i + 1]) {
                  if (exMember == null) {
                     exMember = "";
                  } else {
                     exMember = exMember + ", ";
                  }

                  exMember = exMember + chr.getName();
                  break;
               }
            }
         }

         if (exMember == null) {
            for (PartyMemberEntry partyMemberEntry : this.engine.getPlayer().getParty().getPartyMemberList()) {
               MapleCharacter chr = this.engine.getPlayer().getMap().getCharacterById(partyMemberEntry.getId());

               for (int ix = 0; ix < values.length; ix++) {
                  if (ix % 2 == 0) {
                     chr.gainItem(values[ix], values[ix + 1], false, 0L, null);
                  }
               }
            }
         }

         return exMember;
      }
   }

   public final void gainItem(int id, short quantity) {
      this.gainItem(id, quantity, false, 0L, -1, "");
   }

   public final void gainItemSilent(int id, short quantity) {
      this.gainItem(id, quantity, false, 0L, -1, "", this.engine.getClient(), false);
   }

   public final void gainItem(int id, short quantity, boolean randomStats) {
      this.gainItem(id, quantity, randomStats, 0L, -1, "");
   }

   public final void gainItem(int id, short quantity, boolean randomStats, int slots) {
      this.gainItem(id, quantity, randomStats, 0L, slots, "");
   }

   public final void gainItem(int id, short quantity, long period) {
      this.gainItem(id, quantity, false, period, -1, "");
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots) {
      this.gainItem(id, quantity, randomStats, period, slots, "");
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner) {
      this.gainItem(id, quantity, randomStats, period, slots, owner, this.engine.getClient());
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner, MapleClient cg) {
      this.gainItem(id, quantity, randomStats, period, slots, owner, cg, true);
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner, MapleClient cg, boolean show) {
      if (quantity >= 0) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         MapleInventoryType type = GameConstants.getInventoryType(id);
         if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
            return;
         }

         if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
            Equip item = (Equip)(randomStats ? ii.randomizeStats((Equip)ii.getEquipById(id)) : ii.getEquipById(id));
            if (period > 0L) {
               item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }

            if (ii.isCash(item.getItemId())) {
               item.setUniqueId(MapleInventoryIdentifier.getInstance());
            }

            if (slots > 0) {
               item.setUpgradeSlots((byte)(item.getUpgradeSlots() + slots));
            }

            if (owner != null) {
               item.setOwner(owner);
            }

            item.setGMLog(
               FileoutputUtil.CurrentReadable_Time()
                  + "에 "
                  + this.engine.getPlayer().getName()
                  + "이(가) NPC "
                  + this.engine.getNpc().getId()
                  + " (퀘스트아이디)[ "
                  + this.engine.getClient().getLastUsedScriptName()
                  + "로 부터 획득한 아이템."
            );
            String name = ii.getName(id);
            if (id / 10000 == 114 && name != null && name.length() > 0) {
               String msg = "<" + name + "> 훈장을 획득하셨습니다!";
               cg.getPlayer().dropMessage(-1, msg);
               cg.getPlayer().dropMessage(5, msg);
            }

            MapleInventoryManipulator.addbyItem(cg, item.copy());
            if (quantity > 1) {
               for (int i = 0; i < quantity - 1; i++) {
                  MapleInventoryManipulator.addbyItem(cg, item.copy());
               }
            }
         } else {
            MapleInventoryManipulator.addById(
               cg,
               id,
               quantity,
               owner == null ? "" : owner,
               null,
               period,
               FileoutputUtil.CurrentReadable_Time()
                  + "에 "
                  + this.engine.getPlayer().getName()
                  + "이(가) NPC "
                  + this.engine.getNpc().getId()
                  + " (퀘스트아이디)[ "
                  + this.engine.getClient().getLastUsedScriptName()
                  + "]로 부터 획득한 아이템."
            );
         }

         StringBuilder sb = new StringBuilder();
         sb.append("아이템 생성 로그 : ");
         sb.append(this.engine.getPlayer().getName());
         sb.append(" | 아이템 : ");
         sb.append(id);
         sb.append(" ");
         sb.append((int)quantity);
         sb.append("개");
         sb.append(" | ");
         sb.append("NPC " + this.engine.getNpc().getId() + " (퀘스트ID)[ " + this.engine.getClient().getLastUsedScriptName() + "]\r\n");
      } else {
         MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
      }

      if (show) {
         cg.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, quantity, true));
      }
   }
}
