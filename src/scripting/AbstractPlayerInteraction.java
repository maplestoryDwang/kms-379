package scripting;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.loader.CharacterSaveFlag;
import java.awt.Point;
import java.util.List;
import network.center.Center;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.PetPacket;
import objects.context.guild.Guild;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.EffectHeader;
import objects.effect.NormalEffect;
import objects.effect.child.WZEffect;
import objects.fields.Event_DojoAgent;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.SavedLocationType;
import objects.fields.child.muto.FoodType;
import objects.fields.child.muto.HungryMuto;
import objects.fields.child.papulatus.Field_Papulatus;
import objects.fields.events.MapleEvent;
import objects.fields.events.MapleEventType;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.item.Equip;
import objects.item.MapleInventory;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.quest.QuestEx;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleTrait;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.Timer;

public abstract class AbstractPlayerInteraction {
   protected MapleClient c;
   protected int id;
   protected int id2;

   public AbstractPlayerInteraction(MapleClient c, int id, int id2) {
      this.c = c;
      this.id = id;
      this.id2 = id2;
   }

   public final MapleClient getClient() {
      return this.c;
   }

   public final MapleClient getC() {
      return this.c;
   }

   public MapleCharacter getChar() {
      return this.c.getPlayer();
   }

   public final GameServer getChannelServer() {
      return this.c.getChannelServer();
   }

   public final MapleCharacter getPlayer() {
      return this.c.getPlayer();
   }

   public final EventManager getEventManager(String event) {
      return this.c.getChannelServer().getEventSM().getEventManager(event);
   }

   public final EventInstanceManager getEventInstance() {
      return this.c.getPlayer().getEventInstance();
   }

   public final void warp(int map) {
      Field mapz = this.getWarpMap(map);

      try {
         this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
      } catch (Exception var4) {
         this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
      }
   }

   public final void warpChangeChannel(int channel, int map) {
      this.c.getPlayer().setRegisterTransferField(map);
      this.c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 500L);
      if (this.c.getPlayer().getClient().getChannel() != channel) {
         this.c.getPlayer().dropMessage(5, channel + "채널로 변경되어 이동됩니다.");
         this.c.getPlayer().changeChannel(channel);
      }
   }

   public final void warp_Instanced(int map) {
      Field mapz = this.getMap_Instanced(map);

      try {
         this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
      } catch (Exception var4) {
         this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
      }
   }

   public final void warp(int map, int portal) {
      Field mapz = this.getWarpMap(map);
      if (portal != 0 && map == this.c.getPlayer().getMapId()) {
         Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
         if (portalPos.distanceSq(this.getPlayer().getTruePosition()) < 90000.0) {
            this.c.getSession().writeAndFlush(CField.instantMapWarp((byte)portal));
            this.c.getPlayer().checkFollow();
            this.c.getPlayer().getMap().movePlayer(this.c.getPlayer(), portalPos);
         } else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
         }
      } else {
         this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
      }
   }

   public final void instantMapWarp(int x, int y) {
      this.c.getSession().writeAndFlush(CField.getTeleport(0, 2, this.c.getPlayer().getId(), new Point(x, y)));
      this.c.getPlayer().checkFollow();
      this.c.getPlayer().getMap().movePlayer(this.c.getPlayer(), new Point(x, y));
   }

   public void showEffect(String effect) {
      this.c.getPlayer().getMap().broadcastMessage(CField.showEffect(effect));
   }

   public final void instantMapWarp(int portal) {
      this.c.getSession().writeAndFlush(CField.instantMapWarp((byte)portal));
   }

   public final void warpS(int map, int portal) {
      Field mapz = this.getWarpMap(map);
      this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
   }

   public final void warp(int map, String portal) {
      Field mapz = this.getWarpMap(map);
      if (map == 109060000 || map == 109060002 || map == 109060004) {
         portal = mapz.getSnowballPortal();
      }

      if (map == this.c.getPlayer().getMapId()) {
         Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
         if (portalPos.distanceSq(this.getPlayer().getTruePosition()) < 90000.0) {
            this.c.getPlayer().checkFollow();
            this.c.getSession().writeAndFlush(CField.instantMapWarp((byte)this.c.getPlayer().getMap().getPortal(portal).getId()));
            this.c.getPlayer().getMap().movePlayer(this.c.getPlayer(), new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition()));
         } else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
         }
      } else {
         this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
      }
   }

   public final void warpS(int map, String portal) {
      Field mapz = this.getWarpMap(map);
      if (map == 109060000 || map == 109060002 || map == 109060004) {
         portal = mapz.getSnowballPortal();
      }

      this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
   }

   public final void warpMap(int mapid, int portal) {
      Field map = this.getMap(mapid);

      for (MapleCharacter chr : this.c.getPlayer().getMap().getCharactersThreadsafe()) {
         chr.changeMap(map, map.getPortal(portal));
      }
   }

   public final void playPortalSE() {
      NormalEffect e = new NormalEffect(-1, EffectHeader.PortalSound);
      this.c.getSession().writeAndFlush(e.encodeForLocal());
   }

   private final Field getWarpMap(int map) {
      if (map == 15 || map == 240000000) {
         map = ServerConstants.TownMap;
      }

      return GameServer.getInstance(this.c.getChannel()).getMapFactory().getMap(map);
   }

   public final Field getMap() {
      return this.c.getPlayer().getMap();
   }

   public final Field getMap(int map) {
      return this.getWarpMap(map);
   }

   public final Field getMap_Instanced(int map) {
      return this.c.getPlayer().getEventInstance() == null ? this.getMap(map) : this.c.getPlayer().getEventInstance().getMapInstance(map);
   }

   public void spawnMonster(int id, int qty) {
      this.spawnMob(id, qty, this.c.getPlayer().getTruePosition());
   }

   public final void spawnMobOnMap(int id, int qty, int x, int y, int map) {
      for (int i = 0; i < qty; i++) {
         this.getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y));
      }
   }

   public final void spawnMob(int id, int qty, int x, int y) {
      this.spawnMob(id, qty, new Point(x, y));
   }

   public final void spawnMob(int id, int x, int y) {
      if (DBConfig.isGanglim) {
         this.spawnMob(id, 1, new Point(x, y));
      } else if (this.c.getPlayer().isMultiMode()) {
         if (id != 8500000 && id != 8500010 && id != 8500020 && id != 8810215 && id != 8810026 && id != 8810130) {
            this.spawnMob(id, 1, new Point(x, y));
         } else {
            MapleMonster papul = MapleLifeFactory.getMonster(id);
            papul.setPosition(new Point(x, y));
            long hp = papul.getMobMaxHp();
            long fixedhp = hp * 3L;
            if (fixedhp < 0L) {
               fixedhp = Long.MAX_VALUE;
            }

            papul.setHp(fixedhp);
            papul.setMaxHp(fixedhp);

            for (MapleCharacter partyMember : this.c.getPlayer().getPartyMembers()) {
               if (partyMember.getMapId() == this.c.getPlayer().getMapId()) {
                  partyMember.applyBMCurseJinMulti();
               }
            }

            this.c.getPlayer().getMap().spawnMonster(papul, -2);
         }
      } else {
         this.spawnMob(id, 1, new Point(x, y));
      }
   }

   private final void spawnMob(int id, int qty, Point pos) {
      for (int i = 0; i < qty; i++) {
         this.c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
      }
   }

   public final void killMob(int ids) {
      this.c.getPlayer().getMap().killMonster(ids);
   }

   public final void killAllMob() {
      this.c.getPlayer().getMap().killAllMonsters(true);
   }

   public final void addHP(int delta) {
      this.c.getPlayer().addHP(delta);
   }

   public final int getPlayerStat(String type) {
      if (type.equals("LVL")) {
         return this.c.getPlayer().getLevel();
      } else if (type.equals("STR")) {
         return this.c.getPlayer().getStat().getStr();
      } else if (type.equals("DEX")) {
         return this.c.getPlayer().getStat().getDex();
      } else if (type.equals("INT")) {
         return this.c.getPlayer().getStat().getInt();
      } else if (type.equals("LUK")) {
         return this.c.getPlayer().getStat().getLuk();
      } else if (type.equals("HP")) {
         return (int)this.c.getPlayer().getStat().getHp();
      } else if (type.equals("MP")) {
         return (int)this.c.getPlayer().getStat().getMp();
      } else if (type.equals("MAXHP")) {
         return (int)this.c.getPlayer().getStat().getMaxHp();
      } else if (type.equals("MAXMP")) {
         return (int)this.c.getPlayer().getStat().getMaxMp();
      } else if (type.equals("RAP")) {
         return this.c.getPlayer().getRemainingAp();
      } else if (type.equals("RSP")) {
         return this.c.getPlayer().getRemainingSp();
      } else if (type.equals("GID")) {
         return this.c.getPlayer().getGuildId();
      } else if (type.equals("GRANK")) {
         return this.c.getPlayer().getGuildRank();
      } else if (type.equals("ARANK")) {
         return this.c.getPlayer().getAllianceRank();
      } else if (type.equals("GM")) {
         return this.c.getPlayer().isGM() ? 1 : 0;
      } else if (type.equals("ADMIN")) {
         return this.c.getPlayer().isAdmin() ? 1 : 0;
      } else if (type.equals("GENDER")) {
         return this.c.getPlayer().getGender();
      } else if (type.equals("FACE")) {
         return this.c.getPlayer().getFace();
      } else {
         return type.equals("HAIR") ? this.c.getPlayer().getHair() : -1;
      }
   }

   public final String getName() {
      return this.c.getPlayer().getName();
   }

   public final boolean haveItem(int itemid) {
      return this.haveItem(itemid, 1);
   }

   public final boolean haveItem(int itemid, int quantity) {
      return this.haveItem(itemid, quantity, false, true);
   }

   public final boolean haveItemWithoutLock(int itemid, int quantity) {
      return this.haveItemWithoutLock(itemid, quantity, false);
   }

   public final boolean haveItemWithoutLock(int itemid, int quantity, boolean equipped) {
      MapleInventoryType type = GameConstants.getInventoryType(itemid);
      int possesed = this.c.getPlayer().getInventory(type).countByIdWithoutLock(itemid);
      if (type == MapleInventoryType.EQUIP && equipped) {
         possesed += this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).countByIdWithoutLock(itemid);
      }

      return possesed >= quantity;
   }

   public final boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
      return this.c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
   }

   public final boolean canHold() {
      for (int i = 1; i <= 5; i++) {
         if (this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)i)).getNextFreeSlot() <= -1) {
            return false;
         }
      }

      return true;
   }

   public final boolean canHoldSlots(int slot) {
      for (int i = 1; i <= 5; i++) {
         if (this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)i)).isFull(slot)) {
            return false;
         }
      }

      return true;
   }

   public final boolean canHold(int itemid) {
      return this.c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
   }

   public final boolean canHold(int itemid, int quantity) {
      return MapleInventoryManipulator.checkSpace(this.c, itemid, quantity, "");
   }

   public final MapleQuestStatus getQuestRecord(int id) {
      return this.c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(id));
   }

   public final MapleQuestStatus getQuestNoRecord(int id) {
      return this.c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(id));
   }

   public final int getQuestStatus(int id) {
      return this.c.getPlayer().getQuestStatus(id);
   }

   public final boolean isQuestActive(int id) {
      return this.getQuestStatus(id) == 1;
   }

   public final boolean isQuestFinished(int id) {
      return this.getQuestStatus(id) == 2;
   }

   public final void showQuestMsg(String msg) {
      this.c.getSession().writeAndFlush(CWvsContext.showQuestMsg(msg));
   }

   public final void forceStartQuest(int id, String data) {
      MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, data);
   }

   public final void forceStartQuest(int id, int data, boolean filler) {
      MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, filler ? String.valueOf(data) : null);
   }

   public void forceStartQuest(int id) {
      MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, null);
   }

   public void forceCompleteQuest(int id) {
      MapleQuest.getInstance(id).forceComplete(this.getPlayer(), 0);
   }

   public void spawnNpc(int npcId) {
      this.c.getPlayer().getMap().spawnNpc(npcId, this.c.getPlayer().getPosition());
   }

   public final void spawnNpc(int npcId, int x, int y) {
      this.c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
   }

   public final void spawnNpc(int npcId, Point pos) {
      this.c.getPlayer().getMap().spawnNpc(npcId, pos);
   }

   public final void removeNpc(int mapid, int npcId) {
      this.c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
   }

   public final void removeNpc(int npcId) {
      this.c.getPlayer().getMap().removeNpc(npcId);
   }

   public final void forceStartReactor(int mapid, int id) {
      Field map = this.c.getChannelServer().getMapFactory().getMap(mapid);

      for (MapleMapObject remo : map.getAllReactorsThreadsafe()) {
         Reactor react = (Reactor)remo;
         if (react.getReactorId() == id) {
            react.forceStartReactor(this.c);
            break;
         }
      }
   }

   public final void destroyReactor(int mapid, int id) {
      Field map = this.c.getChannelServer().getMapFactory().getMap(mapid);

      for (MapleMapObject remo : map.getAllReactorsThreadsafe()) {
         Reactor react = (Reactor)remo;
         if (react.getReactorId() == id) {
            react.hitReactor(this.c);
            break;
         }
      }
   }

   public final void hitReactor(int mapid, int id) {
      Field map = this.c.getChannelServer().getMapFactory().getMap(mapid);

      for (MapleMapObject remo : map.getAllReactorsThreadsafe()) {
         Reactor react = (Reactor)remo;
         if (react.getReactorId() == id) {
            react.hitReactor(this.c);
            break;
         }
      }
   }

   public final int getJob() {
      return this.c.getPlayer().getJob();
   }

   public final void gainNX(int amount) {
      this.c.getPlayer().modifyCSPoints(1, amount, true);
   }

   public final void gainNX(int type, int amount) {
      this.c.getPlayer().modifyCSPoints(type, amount, true);
   }

   public final void gainItemPeriod(int id, short quantity, int period) {
      this.gainItem(id, quantity, false, period, -1, "");
   }

   public final void gainItemPeriod(int id, short quantity, long period, String owner) {
      this.gainItem(id, quantity, false, period, -1, owner);
   }

   public final void gainItem(int id, short quantity) {
      this.gainItem(id, quantity, false, 0L, -1, "");
   }

   public final void gainItemSilent(int id, short quantity) {
      this.gainItem(id, quantity, false, 0L, -1, "", this.c, false);
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
      this.gainItem(id, quantity, randomStats, period, slots, owner, this.c);
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

            if (slots > 0) {
               item.setUpgradeSlots((byte)(item.getUpgradeSlots() + slots));
            }

            if (owner != null) {
               item.setOwner(owner);
            }

            item.setGMLog(
               FileoutputUtil.CurrentReadable_Time()
                  + "에 "
                  + this.c.getPlayer().getName()
                  + "이(가) NPC "
                  + this.id
                  + " ("
                  + this.id2
                  + ")[ "
                  + this.c.getLastUsedScriptName()
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
                  + this.c.getPlayer().getName()
                  + "이(가) NPC "
                  + this.id
                  + " ("
                  + this.id2
                  + ")[ "
                  + this.c.getLastUsedScriptName()
                  + "]로 부터 획득한 아이템."
            );
         }

         StringBuilder sb = new StringBuilder();
         sb.append("아이템 생성 로그 : ");
         sb.append(this.c.getPlayer().getName());
         sb.append(" | 아이템 : ");
         sb.append(id);
         sb.append(" ");
         sb.append((int)quantity);
         sb.append("개");
         sb.append(" | ");
         sb.append("NPC " + this.id + " (" + this.id2 + ")[ " + this.c.getLastUsedScriptName() + "]\r\n");
      } else {
         MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
      }

      if (show) {
         cg.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, quantity, true));
      }
   }

   public final boolean removeItem(int id, int quantity) {
      if (MapleInventoryManipulator.removeById_Lock(this.c, GameConstants.getInventoryType(id), id, quantity)) {
         this.c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, -quantity, true));
         return true;
      } else {
         return false;
      }
   }

   public final void changeMusic(String songName) {
      this.getPlayer().getMap().broadcastMessage(CField.musicChange(songName));
   }

   public final void worldMessage(int type, String message) {
      Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(type, message));
   }

   public final void worldGMMessage(int type, String message) {
      Center.Broadcast.broadcastMessage(CField.chatMsg(type, message));
   }

   public final void playerMessage(String message) {
      this.playerMessage(5, message);
   }

   public final void mapMessage(String message) {
      this.mapMessage(5, message);
   }

   public final void guildMessage(String message) {
      this.guildMessage(5, message);
   }

   public final void playerMessage(int type, String message) {
      this.c.getPlayer().dropMessage(type, message);
   }

   public final void mapMessage(int type, String message) {
      this.c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(type, message));
   }

   public final void guildMessage(int type, String message) {
      if (this.getPlayer().getGuildId() > 0) {
         Center.Guild.guildPacket(this.getPlayer().getGuildId(), CWvsContext.serverNotice(type, message));
      }
   }

   public final Guild getGuild() {
      return this.getGuild(this.getPlayer().getGuildId());
   }

   public final Guild getGuild(int guildid) {
      return Center.Guild.getGuild(guildid);
   }

   public final Party getParty() {
      return this.c.getPlayer().getParty();
   }

   public final int getCurrentPartyId(int mapid) {
      return this.getMap(mapid).getCurrentPartyId();
   }

   public final boolean isLeader() {
      return this.getPlayer().getParty() == null ? false : this.getParty().getLeader().getId() == this.c.getPlayer().getId();
   }

   public final boolean isAllPartyMembersAllowedJob(int job) {
      if (this.c.getPlayer().getParty() == null) {
         return false;
      } else {
         for (PartyMemberEntry mem : this.c.getPlayer().getParty().getPartyMemberList()) {
            if (mem.getJobId() / 100 != job) {
               return false;
            }
         }

         return true;
      }
   }

   public final boolean isSoloParty() {
      return this.c.getPlayer().getParty() == null ? false : this.c.getPlayer().getParty().getPartyMemberList().size() == 1;
   }

   public final boolean allMembersHere() {
      if (this.c.getPlayer().getParty() == null) {
         return false;
      } else {
         for (PartyMemberEntry mem : this.c.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter chr = this.c.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr == null) {
               return false;
            }
         }

         return true;
      }
   }

   public final void warpParty(int mapId) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         Field target = this.getMap(mapId);
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null
               && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())
               && curChar.getParty() != null
               && this.getPlayer().getParty() != null
               && curChar.getParty().getId() == this.getPlayer().getParty().getId()) {
               curChar.changeMap(target, target.getPortal(0));
            }
         }
      } else {
         this.warp(mapId, 0);
      }
   }

   public final void warpParty(int mapId, int portal) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMember().getPartyMemberList().size() != 1) {
         boolean rand = portal < 0;
         Field target = this.getMap(mapId);
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMember().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null
               && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())
               && curChar.getParty() != null
               && this.getPlayer().getParty() != null
               && curChar.getParty().getId() == this.getPlayer().getParty().getId()) {
               if (rand) {
                  try {
                     curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                  } catch (Exception var10) {
                     curChar.changeMap(target, target.getPortal(0));
                  }
               } else {
                  curChar.changeMap(target, target.getPortal(portal));
               }
            }
         }
      } else {
         if (portal < 0) {
            this.warp(mapId);
         } else {
            this.warp(mapId, portal);
         }
      }
   }

   public final void warpParty_Instanced(int mapId) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         Field target = this.getMap_Instanced(mapId);
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
               curChar.changeMap(target, target.getPortal(0));
            }
         }
      } else {
         this.warp_Instanced(mapId);
      }
   }

   public void gainMeso(long gain) {
      this.c.getPlayer().gainMeso(gain, true, true, true);
   }

   public void gainExp(double gain) {
      this.c.getPlayer().gainExp(gain, true, true, true);
   }

   public void gainExpR(int gain) {
      this.c.getPlayer().gainExp(gain * this.c.getChannelServer().getExpRate(), true, true, true);
   }

   public final void givePartyItems(int id, short quantity, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         if (quantity >= 0) {
            MapleInventoryManipulator.addById(chr.getClient(), id, quantity, "Received from party interaction " + id + " (" + this.id2 + ")");
         } else {
            MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
         }

         chr.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, quantity, true));
      }
   }

   public void addPartyTrait(String t, int e, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         chr.getTrait(MapleTrait.MapleTraitType.valueOf(t)).addExp(e, chr);
      }
   }

   public void addPartyTrait(String t, int e) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
               curChar.getTrait(MapleTrait.MapleTraitType.valueOf(t)).addExp(e, curChar);
            }
         }
      } else {
         this.addTrait(t, e);
      }
   }

   public void addTrait(String t, int e) {
      this.getPlayer().getTrait(MapleTrait.MapleTraitType.valueOf(t)).addExp(e, this.getPlayer());
   }

   public final void givePartyItems(int id, short quantity) {
      this.givePartyItems(id, quantity, false);
   }

   public final void givePartyItems(int id, short quantity, boolean removeAll) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
               this.gainItem(id, (short)(removeAll ? -curChar.itemQuantity(id) : quantity), false, 0L, 0, "", curChar.getClient());
            }
         }
      } else {
         this.gainItem(id, (short)(removeAll ? -this.getPlayer().itemQuantity(id) : quantity));
      }
   }

   public final void givePartyExp_PQ(int maxLevel, double mod, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         int amount = (int)Math.round(
            GameConstants.getExpNeededForLevel(chr.getLevel() > maxLevel ? maxLevel + (maxLevel - chr.getLevel()) / 10 : chr.getLevel())
               / (Math.min(chr.getLevel(), maxLevel) / 5.0)
               / (mod * 2.0)
         );
         chr.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
      }
   }

   public final void gainExp_PQ(int maxLevel, double mod) {
      int amount = (int)Math.round(
         GameConstants.getExpNeededForLevel(this.getPlayer().getLevel() > maxLevel ? maxLevel + this.getPlayer().getLevel() / 10 : this.getPlayer().getLevel())
            / (Math.min(this.getPlayer().getLevel(), maxLevel) / 10.0)
            / mod
      );
      this.gainExp(amount * this.c.getChannelServer().getExpRate());
   }

   public final void givePartyExp_PQ(int maxLevel, double mod) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
               int amount = (int)Math.round(
                  GameConstants.getExpNeededForLevel(curChar.getLevel() > maxLevel ? maxLevel + curChar.getLevel() / 10 : curChar.getLevel())
                     / (Math.min(curChar.getLevel(), maxLevel) / 10.0)
                     / mod
               );
               curChar.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
            }
         }
      } else {
         int amount = (int)Math.round(
            GameConstants.getExpNeededForLevel(
                  this.getPlayer().getLevel() > maxLevel ? maxLevel + this.getPlayer().getLevel() / 10 : this.getPlayer().getLevel()
               )
               / (Math.min(this.getPlayer().getLevel(), maxLevel) / 10.0)
               / mod
         );
         this.gainExp(amount * this.c.getChannelServer().getExpRate());
      }
   }

   public final void givePartyExp(int amount, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         chr.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
      }
   }

   public final void givePartyExp(int amount) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
               curChar.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
            }
         }
      } else {
         this.gainExp(amount * this.c.getChannelServer().getExpRate());
      }
   }

   public final void givePartyNX(int amount, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         chr.modifyCSPoints(1, amount, true);
      }
   }

   public final void givePartyNX(int amount) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
               curChar.modifyCSPoints(1, amount, true);
            }
         }
      } else {
         this.gainNX(amount);
      }
   }

   public final void endPartyQuest(int amount, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         chr.endPartyQuest(amount);
      }
   }

   public final void endPartyQuest(int amount) {
      if (this.getPlayer().getParty() != null && this.getPlayer().getParty().getPartyMemberList().size() != 1) {
         int cMap = this.getPlayer().getMapId();

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMemberList()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
               curChar.endPartyQuest(amount);
            }
         }
      } else {
         this.getPlayer().endPartyQuest(amount);
      }
   }

   public final void removeFromParty(int id, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
         if (possesed > 0) {
            MapleInventoryManipulator.removeById(this.c, GameConstants.getInventoryType(id), id, possesed, true, false);
            chr.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, (short)(-possesed), true));
         }
      }
   }

   public final void removeFromParty(int id) {
      this.givePartyItems(id, (short)0, true);
   }

   public final void useSkill(int skill, int level) {
      if (level > 0) {
         SkillFactory.getSkill(skill).getEffect(level).applyTo(this.c.getPlayer());
      }
   }

   public final void useItem(int id) {
      MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(this.c.getPlayer());
      this.c.getSession().writeAndFlush(CWvsContext.InfoPacket.getStatusMsg(id));
   }

   public final void cancelItem(int id) {
      this.c.getPlayer().temporaryStatResetBySkillID(id);
      this.c.getPlayer().temporaryStatResetBySkillID(-id);
   }

   public final void removeAll(int id) {
      this.c.getPlayer().removeAll(id);
   }

   public final void gainCloseness(int closeness, int index) {
      MaplePet pet = this.getPlayer().getPet(index);
      if (pet != null) {
         pet.setCloseness(pet.getCloseness() + closeness * this.getChannelServer().getTraitRate());
         this.getClient()
            .getSession()
            .writeAndFlush(
               PetPacket.updatePet(
                  this.c.getPlayer(),
                  pet,
                  this.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()),
                  false,
                  this.c.getPlayer().getPetLoot()
               )
            );
      }
   }

   public final void gainClosenessAll(int closeness) {
      for (MaplePet pet : this.getPlayer().getPets()) {
         if (pet != null && pet.getSummoned()) {
            pet.setCloseness(pet.getCloseness() + closeness);
            this.getClient()
               .getSession()
               .writeAndFlush(
                  PetPacket.updatePet(
                     this.c.getPlayer(),
                     pet,
                     this.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()),
                     false,
                     this.c.getPlayer().getPetLoot()
                  )
               );
         }
      }
   }

   public final void resetMap(int mapid) {
      this.getMap(mapid).resetFully();
   }

   public final void resetMap(int mapid, boolean respawn) {
      this.getMap(mapid).resetFully(respawn);
   }

   public final void openNpc(int id) {
      this.getClient().removeClickedNPC();
      NPCScriptManager.getInstance().start(this.getClient(), id);
   }

   public final void openNpcCustom(MapleClient client, int id, String name) {
      this.getClient().removeClickedNPC();
      NPCScriptManager.getInstance().start(client, id, name, true);
   }

   public final void openNpc(int id, String name) {
      this.getClient().removeClickedNPC();
      NPCScriptManager.getInstance().start(this.c, id, name, true);
   }

   public final void openNpc(MapleClient cg, int id) {
      cg.removeClickedNPC();
      NPCScriptManager.getInstance().start(cg, id);
   }

   public final int getMapId() {
      return this.c.getPlayer().getMap().getId();
   }

   public final boolean haveMonster(int mobid) {
      for (MapleMapObject obj : this.c.getPlayer().getMap().getAllMonstersThreadsafe()) {
         MapleMonster mob = (MapleMonster)obj;
         if (mob.getId() == mobid) {
            return true;
         }
      }

      return false;
   }

   public final int getChannelNumber() {
      return this.c.getChannel();
   }

   public final int getMonsterCount(int mapid) {
      return this.c.getChannelServer().getMapFactory().getMap(mapid).getNumMonsters();
   }

   public final void teachSkill(int id, int level, byte masterlevel) {
      this.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
   }

   public final void teachSkill(int id, int level) {
      Skill skil = SkillFactory.getSkill(id);
      if (this.getPlayer().getSkillLevel(skil) > level) {
         level = this.getPlayer().getSkillLevel(skil);
      }

      this.getPlayer().changeSingleSkillLevel(skil, level, (byte)skil.getMaxLevel());
      this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.SKILLS.getFlag());
   }

   public final int getPlayerCount(int mapid) {
      return this.c.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
   }

   public final void dojo_getUp() {
      this.c.getSession().writeAndFlush(CWvsContext.InfoPacket.updateInfoQuest(1207, "pt=1;min=4;belt=1;tuto=1"));
      this.c.getSession().writeAndFlush(CField.instantMapWarp(6));
      NormalEffect e = new NormalEffect(this.c.getPlayer().getId(), EffectHeader.PortalSound);
      this.c.getSession().writeAndFlush(e.encodeForLocal());
   }

   public final boolean dojoAgent_NextMap(boolean dojo, boolean fromresting) {
      return dojo
         ? Event_DojoAgent.warpNextMap(this.c.getPlayer(), fromresting, this.c.getPlayer().getMap())
         : Event_DojoAgent.warpNextMap_Agent(this.c.getPlayer(), fromresting);
   }

   public final boolean dojoAgent_NextMap(boolean dojo, boolean fromresting, int mapid) {
      return dojo
         ? Event_DojoAgent.warpNextMap(this.c.getPlayer(), fromresting, this.getMap(mapid))
         : Event_DojoAgent.warpNextMap_Agent(this.c.getPlayer(), fromresting);
   }

   public final int dojo_getPts() {
      return this.c.getPlayer().getIntNoRecord(150100);
   }

   public final MapleEvent getEvent(String loc) {
      return this.c.getChannelServer().getEvent(MapleEventType.valueOf(loc));
   }

   public final int getSavedLocation(String loc) {
      Integer ret = this.c.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
      return ret != null && ret != -1 ? ret : 100000000;
   }

   public final void saveLocation(String loc) {
      this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
   }

   public final void saveReturnLocation(String loc) {
      this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc), this.c.getPlayer().getMap().getReturnMap().getId());
   }

   public final void clearSavedLocation(String loc) {
      this.c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
   }

   public final void summonMsg(String msg) {
      if (!this.c.getPlayer().hasSummon()) {
         this.playerSummonHint(true);
      }

      this.c.getSession().writeAndFlush(CField.UIPacket.summonMessage(msg));
   }

   public final void summonMsg(int type) {
      if (!this.c.getPlayer().hasSummon()) {
         this.playerSummonHint(true);
      }

      this.c.getSession().writeAndFlush(CField.UIPacket.summonMessage(type));
   }

   public final void showInstruction(String msg, int width, int height) {
      this.c.getSession().writeAndFlush(CField.sendHint(msg, width, height));
   }

   public final void playerSummonHint(boolean summon) {
      this.c.getPlayer().setHasSummon(summon);
      this.c.getSession().writeAndFlush(CField.UIPacket.summonHelper(summon));
   }

   public final String getInfoQuest(int id) {
      QuestEx ex = this.c.getPlayer().getInfoQuest(id);
      return ex == null ? "" : ex.getData();
   }

   public final void updateInfoQuest(int id, String data) {
      this.c.getPlayer().updateInfoQuest(id, data);
   }

   public final boolean getEvanIntroState(String data) {
      return this.getInfoQuest(22013).equals(data);
   }

   public final void updateEvanIntroState(String data) {
      this.updateInfoQuest(22013, data);
   }

   public final void Aran_Start() {
      this.c.getSession().writeAndFlush(CField.Aran_Start());
   }

   public final void evanTutorial(String data, int v1) {
      this.c.getSession().writeAndFlush(CField.NPCPacket.getEvanTutorial(data));
   }

   public final void showWZEffect(String data) {
      WZEffect e = new WZEffect(this.c.getPlayer().getId(), 0, 0, 0, data);
      this.c.getSession().writeAndFlush(e.encodeForLocal());
   }

   public final void EarnTitleMsg(String data) {
      this.c.getSession().writeAndFlush(CWvsContext.getScriptProgressMessage(data));
   }

   public final void setInGameDirectionMode(short i) {
      this.c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(i));
   }

   public final void setStandAloneMode(boolean enabled) {
      this.c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(enabled));
   }

   public final void MovieClipIntroUI(boolean enabled) {
      this.c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(enabled));
      this.c.getSession().writeAndFlush(CField.UIPacket.IntroLock(enabled));
   }

   public MapleInventoryType getInvType(int i) {
      return MapleInventoryType.getByType((byte)i);
   }

   public String getItemName(int id) {
      return MapleItemInformationProvider.getInstance().getName(id);
   }

   public void gainPet(int id, String name, int level, int closeness, int fullness, long period, short flags) {
      if (id > 5000200 || id < 5000000) {
         id = 5000000;
      }

      if (level > 30) {
         level = 30;
      }

      if (closeness > 30000) {
         closeness = 30000;
      }

      if (fullness > 100) {
         fullness = 100;
      }

      try {
         MapleInventoryManipulator.addById(
            this.c,
            id,
            (short)1,
            "",
            MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int)period : 0, flags),
            45L,
            "Pet from interaction " + id + " (" + this.id2 + ") on " + FileoutputUtil.CurrentReadable_Date()
         );
      } catch (NullPointerException var10) {
         var10.printStackTrace();
      }
   }

   public void removeSlot(int invType, short slot, short quantity) {
      MapleInventoryManipulator.removeFromSlot(this.c, this.getInvType(invType), slot, quantity, true);
   }

   public void gainGP(int gp) {
      if (this.getPlayer().getGuildId() > 0) {
         Center.Guild.gainContribution(this.getPlayer().getGuildId(), gp);
      }
   }

   public int getGP() {
      return this.getPlayer().getGuildId() <= 0 ? 0 : Center.Guild.getGP(this.getPlayer().getGuildId());
   }

   public void showMapEffect(String path) {
      this.getClient().getSession().writeAndFlush(CField.MapEff(path));
   }

   public int itemQuantity(int itemid) {
      return this.getPlayer().itemQuantity(itemid);
   }

   public EventInstanceManager getDisconnected(String event) {
      EventManager em = this.getEventManager(event);
      if (em == null) {
         return null;
      } else {
         for (EventInstanceManager eim : em.getInstances()) {
            if (eim.isDisconnected(this.c.getPlayer()) && eim.getPlayerCount() > 0) {
               return eim;
            }
         }

         return null;
      }
   }

   public boolean isAllReactorState(int reactorId, int state) {
      boolean ret = false;

      for (Reactor r : this.getMap().getAllReactorsThreadsafe()) {
         if (r.getReactorId() == reactorId) {
            ret = r.getState() == state;
         }
      }

      return ret;
   }

   public long getCurrentTime() {
      return System.currentTimeMillis();
   }

   public void spawnMonster(int id) {
      this.spawnMonster(id, 1, this.getPlayer().getTruePosition());
   }

   public void spawnMonster(int id, int x, int y) {
      if (DBConfig.isGanglim) {
         this.spawnMonster(id, 1, new Point(x, y));
      } else if (this.c.getPlayer().isMultiMode()) {
         if (id != 8920102 && id != 8920002 && id != 8910100 && id != 8910000) {
            this.spawnMonster(id, 1, new Point(x, y));
         } else {
            MapleMonster crimsonQueen = MapleLifeFactory.getMonster(id);
            crimsonQueen.setPosition(new Point(x, y));
            long hp = crimsonQueen.getMobMaxHp();
            long fixedhp = hp * 3L;
            if (fixedhp < 0L) {
               fixedhp = Long.MAX_VALUE;
            }

            crimsonQueen.getStats().setHp(fixedhp);
            crimsonQueen.getStats().setMaxHp(fixedhp);
            this.c.getPlayer().getMap().spawnMonster(crimsonQueen, -2);

            for (MapleCharacter chr : this.getMap().getCharacters()) {
               chr.applyBMCurseJinMulti();
            }
         }
      } else {
         this.spawnMonster(id, 1, new Point(x, y));
      }
   }

   public void spawnMonster(int id, int qty, int x, int y) {
      this.spawnMonster(id, qty, new Point(x, y));
   }

   public void spawnMonster(int id, int qty, Point pos) {
      for (int i = 0; i < qty; i++) {
         this.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
      }
   }

   public void spawnMonster(final int templateID, final int x, final int y, final int spawnType, int delay) {
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (DBConfig.isGanglim) {
               AbstractPlayerInteraction.this.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(templateID), new Point(x, y), (byte)spawnType);
            } else if (AbstractPlayerInteraction.this.c.getPlayer().isMultiMode()) {
               if (AbstractPlayerInteraction.this.id != 8930100 && AbstractPlayerInteraction.this.id != 8930000) {
                  AbstractPlayerInteraction.this.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(templateID), new Point(x, y), (byte)spawnType);
               } else {
                  MapleMonster vellum = MapleLifeFactory.getMonster(AbstractPlayerInteraction.this.id);
                  vellum.setPosition(new Point(x, y));
                  long hp = vellum.getMobMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  vellum.setHp(fixedhp);
                  vellum.setMaxHp(fixedhp);
                  AbstractPlayerInteraction.this.getMap().spawnMonster(vellum, spawnType);

                  for (MapleCharacter chr : AbstractPlayerInteraction.this.getMap().getCharacters()) {
                     chr.applyBMCurseJinMulti();
                  }
               }
            } else {
               AbstractPlayerInteraction.this.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(templateID), new Point(x, y), (byte)spawnType);
            }
         }
      }, delay);
   }

   public void sendNPCText(String text, int npc) {
      this.getMap().broadcastMessage(CField.NPCPacket.getNPCTalk(npc, (byte)0, text, "00 00", (byte)0));
   }

   public boolean getTempFlag(int flag) {
      return (this.c.getChannelServer().getTempFlag() & flag) == flag;
   }

   public void logPQ(String text) {
   }

   public void outputFileError(Throwable t) {
      FileoutputUtil.outputFileError("Log_Script_Except.rtf", t);
   }

   public void trembleEffect(int type, int delay) {
      this.c.getSession().writeAndFlush(CField.trembleEffect(type, delay));
   }

   public int nextInt(int arg0) {
      return Randomizer.nextInt(arg0);
   }

   public MapleQuest getQuest(int arg0) {
      return MapleQuest.getInstance(arg0);
   }

   public void achievement(int a) {
      this.c.getPlayer().getMap().broadcastMessage(CField.achievementRatio(a));
   }

   public final MapleInventory getInventory(int type) {
      return this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type));
   }

   public int randInt(int arg0) {
      return Randomizer.nextInt(arg0);
   }

   public void sendDirectionStatus(int key, int value) {
      this.c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(key, value));
      this.c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
   }

   public void sendDirectionInfo(String data) {
      this.c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(data, 2000, 0, -100, 0, 0));
      this.c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 2000));
   }

   public void addEquip(short pos, int itemid, short watk, short wdef, short mdef, byte upslot, short hp, short mp) {
      MapleInventory equip = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
      Equip eq = new Equip(itemid, pos, (byte)0);
      eq.setWatk(watk);
      eq.setWdef(wdef);
      eq.setMdef(mdef);
      eq.setMp(mp);
      eq.setHp(hp);
      if (itemid == 1099004) {
         eq.setStr((short)12);
         eq.setDex((short)12);
      }

      if (itemid == 1098002) {
         eq.setStr((short)7);
         eq.setDex((short)7);
      }

      if (itemid == 1098003) {
         eq.setStr((short)12);
         eq.setDex((short)12);
      }

      eq.setUpgradeSlots(upslot);
      eq.setExpiration(-1L);
      equip.addFromDB(eq.copy());
   }

   public final void PartyTimeMove(int map1, int map2, int time) {
      Party party = this.c.getPlayer().getParty();
      if (party != null) {
         for (PartyMemberEntry partymem : party.getPartyMemberList()) {
            MapleCharacter chr = this.c.getChannelServer().getPlayerStorage().getCharacterById(partymem.getId());
            if (chr != null) {
               chr.timeMoveMap(map1, map2, time);
            }
         }
      }
   }

   public void cancelBuff(int skill) {
      this.c.getPlayer().temporaryStatResetBySkillID(skill);
   }

   public final void timeMoveMap(final int destination, final int movemap, int time) {
      this.warp(movemap, 0);
      this.getClient().getSession().writeAndFlush(CField.getClock(time));
      Timer.CloneTimer tMan = Timer.CloneTimer.getInstance();
      Runnable r = new Runnable() {
         @Override
         public void run() {
            if (AbstractPlayerInteraction.this.getPlayer() != null && AbstractPlayerInteraction.this.getPlayer().getMapId() == movemap) {
               AbstractPlayerInteraction.this.warp(destination);
               AbstractPlayerInteraction.this.cancelBuff(80001027);
               AbstractPlayerInteraction.this.cancelBuff(80001028);
            }
         }
      };
      tMan.schedule(r, time * 1000);
   }

   public final void TimeMoveMap(int movemap, int destination, int time) {
      this.timeMoveMap(destination, movemap, time);
   }

   public final void startMapEffect(String text, int itemID, boolean active) {
      this.c.getPlayer().getMap().broadcastMessage(CField.startMapEffect(text, itemID, active));
   }

   public final void startMapEffect(String text, int itemID, int seconds) {
      this.c.getPlayer().getMap().broadcastMessage(CField.startMapEffect(text, itemID, true, seconds));
   }

   public final void registerTimeOutMove(int target, final int destination, int minute) {
      this.warp(target, 0);
      this.getClient().getPlayer().setTimeoutMoveDuration(minute);
      this.getClient().getPlayer().setStartTimeoutMoveTime(System.currentTimeMillis());
      this.getClient().getSession().writeAndFlush(CField.getClock(minute));
      this.getClient().getPlayer().registerTimeoutMoveMapTask(new Runnable() {
         @Override
         public void run() {
            if (AbstractPlayerInteraction.this.getPlayer() != null) {
               AbstractPlayerInteraction.this.warp(destination);
               AbstractPlayerInteraction.this.cancelBuff(80001027);
               AbstractPlayerInteraction.this.cancelBuff(80001028);
            }
         }
      }, minute * 60);
   }

   public final void cancelTimeOutMove() {
      this.getClient().getPlayer().cancelTimeoutMoveMapTask();
      this.getClient().getSession().writeAndFlush(CField.getClock(0));
   }

   public final void checkRecipes() {
      HungryMuto muto = this.getClient().getPlayer().getHungryMuto();
      if (muto != null) {
         boolean find = false;
         boolean endGame = false;

         for (int i = 0; i < muto.getRecipes().length; i++) {
            int[] recipe = muto.getRecipes()[i];
            int itemID = recipe[0];
            int needQ = recipe[1];
            int currentQ = recipe[2];
            if (this.getClient().getPlayer().getMutoPickupItemID() == itemID) {
               int delta = Math.min(needQ, currentQ + this.getClient().getPlayer().getMutoPickupItemQ());
               muto.updateRecipe(itemID, delta);
               this.getClient()
                  .getPlayer()
                  .getMap()
                  .broadcastMessage(new HungryMuto.RecipeUpdate(FoodType.getFoodType(muto.getType()), muto.getDifficulty(), muto.getRecipes()).encode());
               this.getClient().getSession().writeAndFlush(CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg4"));
               if (muto.checkClear()) {
                  boolean perfect = muto.addScore();
                  this.getClient().getPlayer().getMap().broadcastMessage(CField.fieldValue("score", String.valueOf(muto.getScore())));
                  if (perfect) {
                     this.getClient().getPlayer().getMap().broadcastMessage(CField.MapEff("Map/Effect3.img/hungryMuto/perfect"));
                  } else {
                     this.getClient().getPlayer().getMap().broadcastMessage(CField.MapEff("Map/Effect3.img/hungryMuto/good"));
                  }

                  if (!muto.isEnhanceMob() && muto.getScore() >= 700) {
                     this.c.getPlayer().getMap().killAllMonsters(true);

                     for (int next = 0; next < 6; next++) {
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642001), new Point(105, -354));
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642003), new Point(2644, -345));
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642005), new Point(-929, -356));
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642007), new Point(3778, -343));
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642009), new Point(-1045, -847));
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642011), new Point(3935, -841));
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642013), new Point(1434, -791));
                        this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642015), new Point(1405, -1637));
                     }

                     this.getClient().getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642016), muto.getRandArea());
                     this.getClient().getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage("굴라가 더욱 격렬하게 저항하며 강력한 몬스터들이 등장합니다!"));
                     muto.setEnhanceMob(true);
                  }

                  this.getClient().getPlayer().getMap().broadcastMessage(CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg5"));
                  if (muto.getScore() >= 990) {
                     endGame = true;
                  } else {
                     muto.changeFood(this.getClient().getPlayer());
                  }
               }

               find = true;
            }
         }

         if (this.getClient().getPlayer().getMutoPickupItemQ() > 0) {
            if (!find) {
               this.getClient().getSession().writeAndFlush(CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg3"));
            }

            this.getClient().getPlayer().setMutoPickupItemID(0);
            this.getClient().getPlayer().setMutoPickupItemQ(0);
            this.getClient().getPlayer().getMap().broadcastMessage(new HungryMuto.PickupItemUpdate(this.getClient().getPlayer().getId(), 0, 0).encode());
         }

         if (endGame) {
            muto.gameOver(this.getClient().getPlayer(), true);
         }
      }
   }

   public final void onUserTeleport(int x, int y) {
      this.c.getPlayer().send(CField.getTeleport(1, 1, this.c.getPlayer().getId(), new Point(x, y - 10)));
   }

   public final void onTeleportLucid() {
      int rand = Randomizer.rand(0, 3);
      Point pos = new Point(0, 0);
      if (rand == 0) {
         pos = new Point(569, -1966);
      } else if (rand == 1) {
         pos = new Point(1023, -895);
      } else if (rand == 2) {
         pos = new Point(87, -774);
      } else if (rand == 3) {
         pos = new Point(569, -126);
      }

      this.c.getPlayer().send(CField.getTeleport(1, 3, this.c.getPlayer().getId(), pos));
      if (this.c.getPlayer().getMapId() / 100 == 4500045) {
         long hp = Math.round(this.c.getPlayer().getStat().getHp() * 0.01 * 50.0);
         this.c.getPlayer().addHP(-hp, true);
         this.c.getPlayer().temporaryStatSet(SecondaryStatFlag.DebuffIncHP, 80002255, 6000, 100);
      }
   }

   public final void tryRemoveWheel() {
      if (this.getMap() instanceof Field_Papulatus) {
         Field_Papulatus f = (Field_Papulatus)this.getMap();
         f.tryRemoveWheel();
      }
   }

   public final int getInvSlots(int type) {
      return this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type)).getNumFreeSlot();
   }
}
