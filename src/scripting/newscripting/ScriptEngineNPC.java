package scripting.newscripting;

import database.DBConnection;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import network.models.CField;
import network.models.FontColorType;
import network.models.FontType;
import objects.context.party.Party;
import objects.effect.EffectHeader;
import objects.effect.NormalEffect;
import objects.fields.Field;
import objects.fields.Portal;
import objects.fields.fieldset.FieldSet;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.quest.MapleQuest;
import objects.shop.MapleShopFactory;
import objects.users.MapleCharacter;

public abstract class ScriptEngineNPC extends NewScriptEngine {
   protected MapleNPC npc;
   protected Portal portal;
   protected MapleQuest quest;
   protected int itemID;
   private ScriptConversation sc = null;
   public SelfFunction self;
   public TargetFunction target;

   public void initNPC(MapleNPC npc) {
      this.npc = npc;
   }

   public void initPortal(Portal portal) {
      this.portal = portal;
   }

   public void initQuest(MapleQuest quest) {
      this.quest = quest;
   }

   public void initItemID(int itemID) {
      this.itemID = itemID;
   }

   protected ScriptEngineNPC() {
   }

   public ScriptConversation getSc() {
      return this.sc == null ? null : this.sc;
   }

   public void init(Method method) {
      if (this.sc != null) {
         throw new IllegalStateException();
      } else {
         this.sc = new ScriptConversation(method, this);
         this.self = new SelfFunction(this.sc);
         this.target = new TargetFunction(this.sc);
         ScriptThreadManager.getInstance().execute(this.sc);
      }
   }

   public void end() {
      if (this.getClient() != null && this.getClient().getPlayer() != null) {
         MapleCharacter chr = this.getClient().getPlayer();
         this.initEngine(null);
         if (this.sc != null) {
            this.sc.forceStop();
         }

         this.sc = null;
         Lock lock = chr.getClient().getNPCLock();
         lock.lock();

         try {
            chr.setScriptThread(null);
         } finally {
            lock.unlock();
         }
      }
   }

   public MapleNPC getNpc() {
      return this.npc;
   }

   public Portal getPortal() {
      return this.portal;
   }

   public MapleQuest getQuest() {
      return this.quest;
   }

   public void userLocalTeleport(int portalID) {
      Portal tPortal = this.getPlayer().getMap().getPortal(portalID);
      if (tPortal != null) {
         this.getPlayer().send(CField.instantMapWarp(portalID));
      }
   }

   public void registerTransferField(int map) {
      Field field = this.getClient().getChannelServer().getMapFactory().getMap(map);
      this.getPlayer().changeMapNoSCEnd(field);
   }

   public void registerTransferField(int map, int portal) {
      Field field = this.getClient().getChannelServer().getMapFactory().getMap(map);
      this.getPlayer().changeMapNoSCEnd(field, field.getPortal(portal));
   }

   public final void playPortalSE() {
      NormalEffect e = new NormalEffect(this.getPlayer().getId(), EffectHeader.PortalSound);
      this.getPlayer().getClient().getSession().writeAndFlush(e.encodeForLocal());
   }

   public FieldSet fieldSet(String name) {
      return this.getClient().getChannelServer().getFieldSet(name);
   }

   public void bigScriptProgressMessage(String message) {
      this.getPlayer().send(CField.UIPacket.sendBigScriptProgressMessage(message, FontType.NanumGothic, FontColorType.Yellow));
   }

   public void bigScriptProgressMessage(String message, FontType fontType, FontColorType fontColorType) {
      this.getPlayer().send(CField.UIPacket.sendBigScriptProgressMessage(message, fontType, fontColorType));
   }

   public void setBossEnter(Party party, String bossName, String bossKeyValue, String canTimeKey, int canTimeType) {
      this.getClient().getChannelServer().getPartyMembers(party).forEach(p -> {
         this.logBossEnter(bossName, p.getName());
         p.CountAdd(bossKeyValue);
         if (canTimeKey != null) {
            p.updateCanTime(canTimeKey, canTimeType);
         }
      });
   }

   public void logBossEnter(String bossName, String... playerName) {
      PreparedStatement ps = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("INSERT INTO `boss_log` (`bossname`, `name`) VALUES(?, ?)");

         for (String name : playerName) {
            ps.setString(1, bossName);
            ps.setString(2, name);
            ps.addBatch();
         }

         ps.executeBatch();
      } catch (SQLException var21) {
         var21.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var23 = null;
            }
         } catch (SQLException var18) {
            var18.printStackTrace();
         }
      }
   }

   public void openShop(int id) {
      MapleShopFactory.getInstance().getShop(id).sendShop(this.getClient());
   }
}
