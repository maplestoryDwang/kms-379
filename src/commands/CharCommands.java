package commands;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import logging.LoggingManager;
import logging.entry.CustomLog;
import network.auction.AuctionServer;
import network.center.Center;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.shop.CashShopServer;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.shop.MapleShopFactory;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.enchant.EquipSpecialAttribute;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemStateFlag;
import objects.users.enchant.StarForceHyperUpgrade;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.ArrayMap;
import objects.utils.CurrentTime;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import objects.utils.Timer;
import scripting.newscripting.ScriptManager;

public class CharCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      if (splitted[0].equals("체력낮추기")) {
         c.getPlayer().getStat().setHp(1L, c.getPlayer());
         c.getPlayer().updateSingleStat(MapleStat.HP, 1L);
      } else if (splitted[0].equals("킬")) {
         c.getPlayer().addHP(-1000000L);
      } else if (splitted[0].equals("체력회복")) {
         c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp(c.getPlayer()), c.getPlayer());
         c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp(c.getPlayer()));
         c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp(c.getPlayer()), c.getPlayer());
         c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp(c.getPlayer()));
      } else if (splitted[0].equals("인벤초기화")) {
         Map<Pair<Short, Short>, MapleInventoryType> eqs = new ArrayMap<>();
         if (!splitted[1].equals("모두")) {
            if (splitted[1].equals("장착")) {
               for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                  Equip equip = (Equip)item;
                  if ((equip.getItemState() & ItemStateFlag.LOCK.getValue()) == 0) {
                     eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIPPED);
                  }
               }
            } else if (splitted[1].equals("장비")) {
               for (Item itemx : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                  Equip equip = (Equip)itemx;
                  if ((equip.getItemState() & ItemStateFlag.LOCK.getValue()) == 0) {
                     eqs.put(new Pair<>(itemx.getPosition(), itemx.getQuantity()), MapleInventoryType.EQUIP);
                  }
               }
            } else if (splitted[1].equals("소비")) {
               for (Item itemxx : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                  if ((itemxx.getFlag() & ItemFlag.LOCK.getValue()) == 0 && itemxx.getItemId() != 2431307 && itemxx.getItemId() != 2432128) {
                     eqs.put(new Pair<>(itemxx.getPosition(), itemxx.getQuantity()), MapleInventoryType.USE);
                  }
               }
            } else if (splitted[1].equals("설치")) {
               for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                  eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.SETUP);
               }
            } else if (splitted[1].equals("기타")) {
               for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                  eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.ETC);
               }
            } else if (splitted[1].equals("캐시")) {
               for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                  eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.CASH);
               }
            } else if (splitted[1].equals("치장")) {
               for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.CASH_EQUIP)) {
                  eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.CASH_EQUIP);
               }
            } else {
               c.getPlayer().dropMessage(6, "[모두/장착/장비/소비/설치/기타/캐시/치장]");
            }
         } else {
            for (MapleInventoryType type : MapleInventoryType.values()) {
               for (Item itemxxx : c.getPlayer().getInventory(type)) {
                  eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), type);
               }
            }
         }

         for (Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
            MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), (Short)eq.getKey().left, (Short)eq.getKey().right, false, false);
         }
      } else if (splitted[0].equals("스킬")) {
         Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
         byte level = (byte)StringUtil.getOptionalIntArg(splitted, 2, 1);
         byte masterlevel = (byte)StringUtil.getOptionalIntArg(splitted, 3, 1);
         if (level > skill.getMaxLevel()) {
            level = (byte)skill.getMaxLevel();
         }

         c.getPlayer().changeSkillLevel(skill, level, masterlevel);
         c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.SKILLS.getFlag());
         c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
      } else if (splitted[0].equals("스킬포인트")) {
         c.getPlayer().setRemainingSp(StringUtil.getOptionalIntArg(splitted, 1, 1));
         c.getPlayer().updateSingleStat(MapleStat.AVAILABLESP, c.getPlayer().getRemainingSp());
      } else if (splitted[0].equals("스탯포인트")) {
         c.getPlayer().setRemainingAp((short)Integer.parseInt(splitted[1]));
         c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
      } else if (splitted[0].equals("직업")) {
         c.getPlayer().changeJob(Integer.parseInt(splitted[1]));

         for (int i = 0; i < c.getPlayer().getJob() % 10 + 1; i++) {
            if (GameConstants.isAutoMaxSkill()) {
               c.getPlayer()
                  .maxskill(i + 1 == c.getPlayer().getJob() % 10 + 1 ? c.getPlayer().getJob() - c.getPlayer().getJob() % 100 : c.getPlayer().getJob() - (i + 1));
            }
         }

         if (GameConstants.isAutoMaxSkill()) {
            c.getPlayer().maxskill(c.getPlayer().getJob());
         }
      } else if (splitted[0].equals("현재맵")) {
         c.getPlayer().dropMessage(5, "현재 " + c.getPlayer().getMap().getId() + " 맵에 있습니다.");
      } else if (!splitted[0].equals("상점") && !splitted[0].equals("샵")) {
         if (splitted[0].equals("메소")) {
            c.getPlayer().gainMeso(30000000000L - c.getPlayer().getMeso(), true);
         } else if (splitted[0].equals("아이템")) {
            int itemId = Integer.parseInt(splitted[1]);
            short quantity = (short)StringUtil.getOptionalIntArg(splitted, 2, 1);
            if (c.getPlayer().getGMLevel() < 6) {
               for (int ix : GameConstants.itemBlock) {
                  if (itemId == ix) {
                     c.getPlayer().dropMessage(5, "해당 아이템은 당신의 GM 레벨로는 생성 하실수 없습니다.");
                  }
               }
            }

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
               Item itemr = new Item(itemId, (short)1, (short)1, 0);
               itemr.setExpiration(2475606994921L);
               MaplePet pet = MaplePet.createPet(itemId, (int)System.currentTimeMillis() / 100);
               itemr.setPet(pet);
               itemr.setUniqueId((int)System.currentTimeMillis() / 100);
               MapleInventoryManipulator.addbyItem(c, itemr);
            } else if (!ii.itemExists(itemId)) {
               c.getPlayer().dropMessage(5, itemId + " 번 아이템은 존재하지 않습니다.");
            } else {
               Item itemxxx;
               if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                  itemxxx = ii.randomizeStats((Equip)ii.getEquipById(itemId));
                  if (GameConstants.isTheSeedRing(itemxxx.getItemId())) {
                     ((Equip)itemxxx).setTheSeedRingLevel((byte)4);
                  }
               } else {
                  itemxxx = new Item(itemId, (short)0, quantity, 0);
               }

               itemxxx.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 명령어로 얻은 아이템.");
               MapleInventoryManipulator.addbyItem(c, itemxxx);
            }
         } else if (splitted[0].equals("드롭")) {
            int itemIdx = Integer.parseInt(splitted[1]);
            short quantityx = (short)StringUtil.getOptionalIntArg(splitted, 2, 1);
            if (itemIdx == 2100106 || itemIdx == 2100107) {
               c.getPlayer().dropMessage(5, "Item is blocked.");
               return;
            }

            if (GameConstants.isPet(itemIdx)) {
               c.getPlayer().dropMessage(5, "펫은 캐시샵에서 구매해 주세요.");
            } else {
               Item toDrop;
               if (GameConstants.getInventoryType(itemIdx) == MapleInventoryType.EQUIP) {
                  MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                  toDrop = ii.randomizeStats((Equip)ii.getEquipById(itemIdx));
               } else {
                  toDrop = new Item(itemIdx, (short)0, quantityx, 0);
               }

               toDrop.setGMLog(c.getPlayer().getName() + "이 드롭 명령어로 제작한 아이템");
               c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
         } else if (splitted[0].equals("레벨")) {
            c.getPlayer().setLevel(Short.parseShort(splitted[1]));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0L) {
               c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
         } else if (splitted[0].equals("레벨업")) {
            c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) - 4L, false, false, false);
            c.getPlayer().setLevel((short)(c.getPlayer().getLevel() + 1));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0L) {
               c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
         } else if (splitted[0].equals("온라인")) {
            c.getPlayer().dropMessage(6, "현재 채널에 접속된 유저는 다음과 같습니다. :");
            String names = "";

            for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
               names = names + chr.getName();
               names = names + ", ";
            }

            if (names.equals("")) {
               names = "현재채널에 접속중인 유저가 없습니다.";
            }

            c.getPlayer().dropMessage(6, names);
         } else if (splitted[0].equals("총온라인사냥터")) {
            for (GameServer cserv : GameServer.getAllInstances()) {
               String names = "채널 "
                  + (cserv.getChannel() == 1 ? 1 : (cserv.getChannel() == 2 ? "20세이상" : cserv.getChannel() - 1))
                  + " ("
                  + cserv.getPlayerStorage().getAllCharacters().size()
                  + " 명) : ";

               for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                  if (chr.getMapId() != ServerConstants.TownMap && chr.getMapId() != 993050600) {
                     names = names + chr.getName() + ", ";
                  }
               }

               c.getPlayer().dropMessage(6, names);
            }
         } else if (splitted[0].equals("총온라인")) {
            for (GameServer cserv : GameServer.getAllInstances()) {
               String names = "채널 "
                  + (cserv.getChannel() == 1 ? 1 : (cserv.getChannel() == 2 ? "20세이상" : cserv.getChannel() - 1))
                  + " ("
                  + cserv.getPlayerStorage().getAllCharacters().size()
                  + " 명) : ";

               for (MapleCharacter chrx : cserv.getPlayerStorage().getAllCharacters()) {
                  names = names + chrx.getName() + ", ";
               }

               c.getPlayer().dropMessage(6, names);
            }
         } else if (splitted[0].equals("모두저장")) {
            c.getPlayer().dropMessage(6, "저장을 시작합니다.");

            for (GameServer cserv : GameServer.getAllInstances()) {
               for (MapleCharacter hp : cserv.getPlayerStorage().getAllCharacters()) {
                  if (hp != null) {
                     hp.saveToDB(false, false);
                  }
               }
            }

            Timer.EtcTimer.getInstance().schedule(() -> {
               try (Connection con = DBConnection.getConnection()) {
                  Map<Integer, Long> cloneMap = new HashMap<>();
                  boolean clone = false;

                  while (!clone) {
                     try {
                        cloneMap.putAll(Center.ServerSave.characterMesoMap);
                        clone = true;
                     } catch (Exception var13) {
                        Thread.sleep(100L);
                        clone = false;
                     }
                  }

                  for (Entry<Integer, Long> entry : cloneMap.entrySet()) {
                     int charid = entry.getKey();
                     long meso = entry.getValue();

                     try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET `meso` = ? WHERE `id` = ?")) {
                        ps.setLong(1, meso);
                        ps.setInt(2, charid);
                        ps.executeUpdate();
                     } catch (Exception var15) {
                        System.out.println("캐릭터 메소 실패 " + charid);
                        var15.printStackTrace();
                     }
                  }
               } catch (Exception var17) {
                  System.out.println("캐릭터 메소 저장 실패");
                  var17.printStackTrace();
               }

               System.out.println("meso 저장완료");
            }, 100L);
            c.getPlayer().dropMessage(6, "[시스템] 저장이 완료되었습니다.");
         } else if (splitted[0].equals("백")) {
            Equip equip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
            int chuc = equip.getCHUC();
            int downGradable = StarForceHyperUpgrade.isDowngradable(chuc) ? 1 : 0;
            equip.setCHUC(Math.min(equip.getCHUC() - downGradable, 12));
            if (GameConstants.isZeroWeapon(equip.getItemId()) && equip.getItemId() % 100 >= 7) {
               equip.setItemId(equip.getItemId() / 1000 * 1000 + 7);
            }

            equip.setSpecialAttribute((short)EquipSpecialAttribute.VESTIGE.getType());
            if (ItemFlag.POSSIBLE_TRADING.check(equip.getFlag())) {
               equip.setItemState(ItemStateFlag.VESTIGE_POSSIBLE_TRADING.getValue());
            }

            if (ItemFlag.BINDED.check(equip.getFlag())) {
               equip.setItemState(ItemStateFlag.VESTIGE_BOUND.getValue());
            }

            if (ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.check(equip.getFlag())) {
               equip.setItemState(ItemStateFlag.VESTIGE_APPLIED_ACCOUNT_SHARE.getValue());
            }

            equip.setFlag((short)(equip.getFlag() & ~ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.getValue()));
            equip.setFlag((short)(equip.getFlag() & ~ItemFlag.POSSIBLE_TRADING.getValue()));
            equip.setFlag((short)(equip.getFlag() | (short)ItemFlag.BINDED.getValue()));
            Item zeroWeapon = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
            c.getPlayer().send(CWvsContext.InventoryPacket.deleteItem(equip));
            c.getPlayer().send(CWvsContext.InventoryPacket.deleteItem(zeroWeapon));
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem((short)(equip.getPosition() == -11 ? -10 : -11));
            MapleInventoryManipulator.addbyItem(c, equip);
            c.getSession()
               .writeAndFlush(
                  CWvsContext.InventoryPacket.moveInventoryItem(
                     MapleInventoryType.EQUIPPED, equip.getPosition(), equip.getPosition(), (short)1, false, false, false
                  )
               );
            c.getPlayer().equipChanged();
            ScriptManager.runScript(c, "zero_reinvoke_weapon", MapleLifeFactory.getNPC(2400009));
         } else if (splitted[0].equals("노래")) {
            c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
         } else if (splitted[0].equals("맥스스탯")) {
            c.getPlayer().getStat().setDex((short)32767, c.getPlayer());
            c.getPlayer().getStat().setInt((short)32767, c.getPlayer());
            c.getPlayer().getStat().setLuk((short)32767, c.getPlayer());
            c.getPlayer().getStat().setMaxHp(500000L, c.getPlayer());
            if (!GameConstants.isZero(c.getPlayer().getJob())) {
               c.getPlayer().getStat().setMaxMp(500000L, c.getPlayer());
               c.getPlayer().getStat().setMp(500000L, c.getPlayer());
            }

            c.getPlayer().getStat().setHp(500000L, c.getPlayer());
            c.getPlayer().getStat().setStr((short)32767, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.STR, 32767L);
            c.getPlayer().updateSingleStat(MapleStat.DEX, 32767L);
            c.getPlayer().updateSingleStat(MapleStat.INT, 32767L);
            c.getPlayer().updateSingleStat(MapleStat.LUK, 32767L);
            c.getPlayer().updateSingleStat(MapleStat.MAXHP, 500000L);
            if (!GameConstants.isZero(c.getPlayer().getJob())) {
               c.getPlayer().updateSingleStat(MapleStat.MAXMP, 500000L);
               c.getPlayer().updateSingleStat(MapleStat.MP, 500000L);
            }

            c.getPlayer().updateSingleStat(MapleStat.HP, 500000L);
         } else if (splitted[0].equals("스탯초기화")) {
            c.getPlayer().getStat().setStr((short)100, c.getPlayer());
            c.getPlayer().getStat().setDex((short)100, c.getPlayer());
            c.getPlayer().getStat().setInt((short)100, c.getPlayer());
            c.getPlayer().getStat().setLuk((short)100, c.getPlayer());
            c.getPlayer().getStat().setMaxHp(10000L, c.getPlayer());
            if (!GameConstants.isZero(c.getPlayer().getJob())) {
               c.getPlayer().getStat().setMaxMp(10000L, c.getPlayer());
               c.getPlayer().getStat().setMp(10000L, c.getPlayer());
            }

            c.getPlayer().getStat().setHp(10000L, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.STR, 100L);
            c.getPlayer().updateSingleStat(MapleStat.DEX, 100L);
            c.getPlayer().updateSingleStat(MapleStat.INT, 100L);
            c.getPlayer().updateSingleStat(MapleStat.LUK, 100L);
            c.getPlayer().updateSingleStat(MapleStat.MAXHP, 10000L);
            if (!GameConstants.isZero(c.getPlayer().getJob())) {
               c.getPlayer().updateSingleStat(MapleStat.MAXMP, 10000L);
               c.getPlayer().updateSingleStat(MapleStat.MP, 10000L);
            }

            c.getPlayer().updateSingleStat(MapleStat.HP, 10000L);
         } else if (splitted[0].equals("하이드")) {
            boolean hided = c.getPlayer().isHidden();
            if (hided) {
               c.getPlayer().dropMessage(6, "하이드 상태가 해제되었습니다.");
            } else {
               c.getPlayer().dropMessage(6, "하이드 상태가 적용되었습니다.");
            }

            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
         } else if (splitted[0].equals("패킷출력")) {
            if (ServerConstants.DEBUG_SEND) {
               ServerConstants.DEBUG_SEND = false;
               ServerConstants.DEBUG_RECEIVE = false;
               c.getPlayer().dropMessage(5, "[GM알림] 패킷출력이 해제되었습니다.");
            } else {
               ServerConstants.DEBUG_SEND = true;
               ServerConstants.DEBUG_RECEIVE = true;
               c.getPlayer().dropMessage(5, "[GM알림] 패킷출력이 설정되었습니다.");
            }
         } else if (splitted[0].equals("샌드출력")) {
            ServerConstants.DEBUG_SEND = !ServerConstants.DEBUG_SEND;
         } else if (splitted[0].equals("리시브출력")) {
            ServerConstants.DEBUG_RECEIVE = !ServerConstants.DEBUG_RECEIVE;
         } else if (splitted[0].equals("캐시")) {
            c.getPlayer().modifyCSPoints(1, Integer.parseInt(splitted[1]), true);
         } else if (splitted[0].equals("피버타임")) {
            if (splitted.length < 2) {
               c.getPlayer().dropMessage(5, "<문업오류> !피버타임 진행할시간(분단위)");
               return;
            }

            try {
               int minute = Integer.parseInt(splitted[1]);
               if (ServerConstants.JuhunFever == 0) {
                  ServerConstants.JuhunFever = 1;
                  GameServer.feverSchedule = Timer.EventTimer.getInstance().schedule(new Runnable() {
                     @Override
                     public void run() {
                        ServerConstants.JuhunFever = 0;
                        Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage("피버 타임이 종료되었습니다. 주문의 흔적 강화 확률이 기존상태로 돌아옵니다.", false, 0));
                        GameServer.feverSchedule.cancel(true);
                        GameServer.feverSchedule = null;
                     }
                  }, minute * 1000 * 60);
                  Center.Broadcast.broadcastGMMessage(CField.chatMsg(3, "[GM메세지] 피버 타임이 " + minute + "분 후에 종료됩니다."));
                  Center.Broadcast.broadcastMessage(CWvsContext.scrollUpgradeFeverTime(2));
                  Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage("피버 타임이 시작되었습니다. 피버 타임 진행중에는 주문의 흔적 강화 확률이 증가합니다.", false, 0));
               }
            } catch (NumberFormatException var42) {
               c.getPlayer().dropMessage(5, "진행할 시간은 분단위로 숫자만 입력하시기 바랍니다.");
               return;
            }
         } else if (splitted[0].equals("피버타임해제")) {
            if (GameServer.feverSchedule != null) {
               GameServer.feverSchedule.cancel(true);
               GameServer.feverSchedule = null;
            }

            if (ServerConstants.JuhunFever == 1) {
               ServerConstants.JuhunFever = 0;
               Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage("피버 타임이 종료되었습니다. 주문의 흔적 강화 확률이 원래대로 돌아옵니다.", false, 0));
            }
         } else if (splitted[0].equals("황단피버")) {
            if (splitted.length < 3) {
               c.getPlayer().dropMessage(5, "<문업오류> !황단피버 배율 진행할시간(분단위)");
               return;
            }

            try {
               int minute = Integer.parseInt(splitted[2]);
               ServerConstants.goldMapleDropRate = Integer.parseInt(splitted[1]);
               GameServer.goldMaplefeverSchedule = Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     ServerConstants.goldMapleDropRate = 1;
                     Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage("황금 단풍잎 드롭 피버 타임이 종료되었습니다.", false, 0));
                     GameServer.goldMaplefeverSchedule.cancel(true);
                     GameServer.goldMaplefeverSchedule = null;
                  }
               }, minute * 1000 * 60);
               Center.Broadcast.broadcastGMMessage(CField.chatMsg(3, "[GM메세지] 황단 피버 타임이 " + minute + "분 후에 종료됩니다."));
               Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage("황금 단풍잎 드롭 피버 타임이 시작되었습니다.", false, 0));
            } catch (NumberFormatException var41) {
               c.getPlayer().dropMessage(5, "진행할 시간은 분단위로 숫자만 입력하시기 바랍니다.");
               return;
            }
         } else if (splitted[0].equals("황단피버해제")) {
            if (GameServer.goldMaplefeverSchedule != null) {
               GameServer.goldMaplefeverSchedule.cancel(true);
               GameServer.goldMaplefeverSchedule = null;
            }

            if (ServerConstants.goldMapleDropRate != 1) {
               ServerConstants.goldMapleDropRate = 1;
               Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage("황금 단풍잎 드롭 피버 타임이 종료되었습니다.", false, 0));
            }
         } else if (splitted[0].equals("지엠부여")) {
            if (splitted.length < 3) {
               c.getPlayer().dropMessage(5, "<문법오류> !지엠부여 부여할대상 부여할권한레벨");
               return;
            }

            String targetName = splitted[1];
            int level = Integer.parseInt(splitted[2]);

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chrx : cs.getPlayerStorage().getAllCharacters()) {
                  if (chrx.getName().equals(targetName)) {
                     chrx.dropMessage(5, "관리자 권한 레벨 " + level + "으로 부여 되었습니다.");
                     c.getPlayer().dropMessage(5, targetName + "이(가) 관리자 권한 레벨 " + level + "으로 부여 되었습니다.");
                     chrx.setGMLevel((byte)level);
                     break;
                  }
               }
            }
         } else if (splitted[0].equals("인기도")) {
            c.getPlayer().setFame(Integer.parseInt(splitted[1]));
         } else if (!splitted[0].equals("후원지급")) {
            if (splitted[0].equals("홍보지급")) {
               if (splitted.length < 3) {
                  c.getPlayer().dropMessage(5, "<문법오류> !홍보지급 지급할대상 지급할포인트");
                  return;
               }

               String targetName = splitted[1];
               int rc = Integer.parseInt(splitted[2]);
               boolean find = false;

               try {
                  Connection con = DBConnection.getConnection();
                  if (con != null) {
                     con.close();
                  }
               } catch (Exception var40) {
                  c.getPlayer().dropMessage(5, "오류발생 : " + var40.toString());
               }

               boolean found = false;

               for (GameServer cs : GameServer.getAllInstances()) {
                  for (MapleCharacter chrxx : cs.getPlayerStorage().getAllCharacters()) {
                     if (chrxx.getName().equals(targetName)) {
                        chrxx.gainHongboPoint(rc);
                        chrxx.dropMessage(5, rc + " 홍보 포인트를 지급 받았습니다.");
                        c.getPlayer().dropMessage(5, targetName + "(이)에게 홍보 포인트 " + rc + "만큼 지급되었습니다.");
                        found = true;
                        break;
                     }
                  }
               }

               if (!found) {
                  for (MapleCharacter chrxxx : CashShopServer.getPlayerStorage().getAllCharacters()) {
                     if (chrxxx.getName().equals(targetName)) {
                        chrxxx.gainHongboPoint(rc);
                        chrxxx.dropMessage(5, rc + " 홍보 포인트를 지급 받았습니다.");
                        c.getPlayer().dropMessage(5, targetName + "(이)에게 홍보 포인트 " + rc + "만큼 지급되었습니다.");
                        found = true;
                        break;
                     }
                  }
               }

               if (!found) {
                  for (MapleCharacter chrxxxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                     if (chrxxxx.getName().equals(targetName)) {
                        chrxxxx.gainHongboPoint(rc);
                        chrxxxx.dropMessage(5, rc + " 홍보 포인트를 지급 받았습니다.");
                        c.getPlayer().dropMessage(5, targetName + "(이)에게 홍보 포인트 " + rc + "만큼 지급되었습니다.");
                        found = true;
                        break;
                     }
                  }
               }

               boolean f = false;
               if (!found) {
                  try (Connection con = DBConnection.getConnection()) {
                     PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
                     ps.setString(1, targetName);
                     ResultSet rs = ps.executeQuery();
                     String name = "";

                     while (rs.next()) {
                        boolean ff = false;

                        for (GameServer cs : GameServer.getAllInstances()) {
                           for (MapleCharacter chrxxxxx : cs.getPlayerStorage().getAllCharacters()) {
                              if (chrxxxxx.getAccountID() == rs.getInt("accountid")) {
                                 name = chrxxxxx.getName();
                                 chrxxxxx.gainHongboPoint(rc);
                                 chrxxxxx.dropMessage(5, rc + " 홍보 포인트를 지급 받았습니다.");
                                 c.getPlayer().dropMessage(5, targetName + "(이)에게 홍보 포인트 " + rc + "만큼 지급되었습니다.");
                                 ff = true;
                                 f = true;
                                 break;
                              }
                           }
                        }

                        if (!ff) {
                           for (MapleCharacter chrxxxxxx : CashShopServer.getPlayerStorage().getAllCharacters()) {
                              if (chrxxxxxx.getAccountID() == rs.getInt("accountid")) {
                                 name = chrxxxxxx.getName();
                                 chrxxxxxx.gainHongboPoint(rc);
                                 chrxxxxxx.dropMessage(5, rc + " 홍보 포인트를 지급 받았습니다.");
                                 c.getPlayer().dropMessage(5, targetName + "(이)에게 홍보 포인트 " + rc + "만큼 지급되었습니다.");
                                 ff = true;
                                 f = true;
                                 break;
                              }
                           }
                        }

                        if (!ff) {
                           for (MapleCharacter chrxxxxxxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                              if (chrxxxxxxx.getAccountID() == rs.getInt("accountid")) {
                                 chrxxxxxxx.gainHongboPoint(rc);
                                 chrxxxxxxx.dropMessage(5, rc + " 홍보 포인트를 지급 받았습니다.");
                                 c.getPlayer().dropMessage(5, targetName + "(이)에게 홍보 포인트 " + rc + "만큼 지급되었습니다.");
                                 ff = true;
                                 f = true;
                                 break;
                              }
                           }
                        }

                        if (!ff) {
                           PreparedStatement ps2 = con.prepareStatement("SELECT `hongbo_point` FROM accounts WHERE `id` = ?");
                           ps2.setInt(1, rs.getInt("accountid"));
                           ResultSet rs2 = ps2.executeQuery();

                           while (rs2.next()) {
                              int orig_rc = rs2.getInt("hongbo_point");
                              PreparedStatement ps3 = con.prepareStatement("UPDATE accounts SET hongbo_point = ? WHERE `id` = ?");
                              ps3.setInt(1, orig_rc + rc);
                              ps3.setInt(2, rs.getInt("accountid"));
                              ps3.executeUpdate();
                              c.getPlayer().dropMessage(5, targetName + "(이)에게 오프라인 홍보 포인트 " + rc + "만큼 지급되었습니다.");
                              ps3.close();
                              f = true;
                           }

                           rs2.close();
                           ps2.close();
                           LoggingManager.putLog(
                              new CustomLog(
                                 c.getPlayer().getName(),
                                 c.getAccountName(),
                                 c.getPlayer().getId(),
                                 c.getPlayer().getAccountID(),
                                 999,
                                 new StringBuilder(targetName + " 오프라인 홍보 포인트 지급 완료 (" + rc + " 포인트)")
                              )
                           );
                        } else {
                           LoggingManager.putLog(
                              new CustomLog(
                                 c.getPlayer().getName(),
                                 c.getAccountName(),
                                 c.getPlayer().getId(),
                                 c.getPlayer().getAccountID(),
                                 999,
                                 new StringBuilder(name + " 홍보 포인트 지급 완료 (" + rc + " 포인트)")
                              )
                           );
                        }
                     }

                     rs.close();
                     ps.close();
                  } catch (SQLException var44) {
                  }
               } else {
                  LoggingManager.putLog(
                     new CustomLog(
                        c.getPlayer().getName(),
                        c.getAccountName(),
                        c.getPlayer().getId(),
                        c.getPlayer().getAccountID(),
                        999,
                        new StringBuilder(c.getPlayer().getName() + " 홍보 포인트 지급 완료 (" + rc + " 포인트)")
                     )
                  );
               }
            } else if (splitted[0].equals("코어조각")) {
               if (splitted.length < 2) {
                  c.getPlayer().dropMessage(5, "<문법오류> !코어조각 코어조각갯수");
                  return;
               }

               try {
                  c.getPlayer().gainPieceOfCore(Integer.parseInt(splitted[1]));
               } catch (NumberFormatException var39) {
                  c.getPlayer().dropMessage(5, "설정할 코어조각 갯수를 숫자로 입력해주시기 바랍니다.");
               }
            } else if (splitted[0].equals("스킬초기화")) {
               List<Skill> removes = new LinkedList<>();

               for (Skill skill : c.getPlayer().getSkills().keySet()) {
                  removes.add(skill);
               }

               for (Skill remove : removes) {
                  c.getPlayer().changeSkillLevel(remove, 0, 0);
               }

               c.getPlayer().setChangedSkills();
               c.getPlayer().dropMessage(5, "스킬 초기화가 완료되었습니다.");
            } else if (splitted[0].equals("스킬선택초기화")) {
               int skillId = Integer.parseInt(splitted[1]);
               List<Skill> removes = new LinkedList<>();

               for (Skill skill : c.getPlayer().getSkills().keySet()) {
                  if (skill.getId() == skillId) {
                     removes.add(skill);
                  }
               }

               for (Skill remove : removes) {
                  c.getPlayer().changeSkillLevel(remove, 0, 0);
               }

               c.getPlayer().setChangedSkills();
               c.getPlayer().dropMessage(5, "선택된 스킬 초기화가 완료되었습니다.");
            } else if (splitted[0].equals("핫타임")) {
               if (splitted.length < 2) {
                  c.getPlayer().dropMessage(5, "<문법오류> !핫타임 지급될아이템 아이템갯수 받을유저수");
                  return;
               }

               List<MapleCharacter> player = new LinkedList<>();

               for (GameServer cs : GameServer.getAllInstances()) {
                  for (MapleCharacter chrxxxxxxxx : cs.getPlayerStorage().getAllCharacters()) {
                     player.add(chrxxxxxxxx);
                  }
               }

               Collections.shuffle(player);
               MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
               int itemID = 0;
               int quantityxx = 1;
               int userCount = 1;
               boolean all = false;

               try {
                  itemID = Integer.parseInt(splitted[1]);
                  if (splitted.length > 2) {
                     quantityxx = Integer.parseInt(splitted[2]);
                  }

                  if (splitted.length > 3) {
                     if (!splitted[3].equals("ALL") && !splitted[3].equals("all") && !splitted[3].equals("전체") && !splitted[3].equals("모두")) {
                        userCount = Math.min(Integer.parseInt(splitted[3]), player.size());
                     } else {
                        userCount = player.size();
                        all = true;
                     }
                  }
               } catch (NumberFormatException var45) {
                  c.getPlayer().dropMessage(5, "지급될 아이템 ID나 갯수를 숫자로 정확하게 입력해주세요.");
                  return;
               }

               if (!ii.itemExists(itemID)) {
                  c.getPlayer().dropMessage(5, itemID + "번 아이템은 존재하지 않는 아이템입니다.");
                  return;
               }

               List<MapleCharacter> recipient = new LinkedList<>();
               int requestCount = 0;

               while (true) {
                  int side = Randomizer.rand(0, player.size() - 1);
                  MapleCharacter g = player.get(side);
                  if (requestCount++ >= userCount * 10 || recipient.size() >= userCount) {
                     break;
                  }

                  if (g != null) {
                     boolean alreadyRecv = false;

                     for (MapleCharacter ch : recipient) {
                        if (ch.getName().equals(g.getName())) {
                           alreadyRecv = true;
                        }
                     }

                     if (!alreadyRecv && g.getInventory(GameConstants.getInventoryType(itemID)).getNextFreeSlot() > -1) {
                        g.gainItem(itemID, (short)quantityxx, false, 0L, "핫타임으로 지급받은 아이템");
                        String message = "";
                        if (!all) {
                           message = message + "축하드립니다! ";
                        }

                        message = message + "핫타임 이벤트로 " + ii.getName(itemID) + " " + quantityxx + "개를 지급 받았습니다. 지금 바로 인벤토리를 확인해보세요!";
                        g.dropMessage(1, message);
                        if (!all && userCount < 5) {
                           Center.Broadcast.broadcastMessage(
                              CField.chatMsg(21, "[" + g.getName() + "] 님이 핫타임 이벤트 보상으로 " + ii.getName(itemID) + " " + quantityxx + "개를 지급 받았습니다. 모두 축하해주세요!")
                           );
                        }

                        recipient.add(g);
                     }
                  }
               }
            } else if (splitted[0].equals("현재경험치")) {
               c.getPlayer().dropMessage(5, "현재경험치는 " + c.getPlayer().getExp() + "입니다.");
            } else if (splitted[0].equals("만렙찍기")) {
               for (int ixx = 0; ixx < 275; ixx++) {
                  c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()), true, true, true);
               }

               int job = c.getPlayer().getJob();
               int a = job / 100;
               if (a / 100 > 0) {
                  a /= 10;
               } else if (a / 10 > 0) {
                  a %= 10;
               }

               if (a / 10 > 0) {
                  a %= 10;
               }

               switch (a) {
                  case 1:
                     c.getPlayer().gainItem(1232122, 1);
                     c.getPlayer().gainItem(1402268, 1);
                     c.getPlayer().gainItem(1412189, 1);
                     c.getPlayer().gainItem(1432227, 1);
                     c.getPlayer().gainItem(1442285, 1);
                     c.getPlayer().gainItem(1302355, 1);
                     break;
                  case 2:
                     c.getPlayer().gainItem(1372237, 1);
                     c.getPlayer().gainItem(1382274, 1);
                     c.getPlayer().gainItem(1262051, 1);
                     c.getPlayer().gainItem(1282040, 1);
                     break;
                  case 3:
                     c.getPlayer().gainItem(1452266, 1);
                     c.getPlayer().gainItem(1462252, 1);
                     c.getPlayer().gainItem(1522152, 1);
                     break;
                  case 4:
                     c.getPlayer().gainItem(1472275, 1);
                     c.getPlayer().gainItem(1482232, 1);
                     c.getPlayer().gainItem(1332289, 1);
                     c.getPlayer().gainItem(1362149, 1);
                     c.getPlayer().gainItem(1242139, 1);
                     c.getPlayer().gainItem(1242141, 1);
                     c.getPlayer().gainItem(1242138, 1);
                     c.getPlayer().gainItem(1242141, 1);
                     break;
                  case 5:
                     c.getPlayer().gainItem(1492245, 1);
                     c.getPlayer().gainItem(1532157, 1);
                     c.getPlayer().gainItem(1582044, 1);
                     c.getPlayer().gainItem(1222122, 1);
                     c.getPlayer().gainItem(1242138, 1);
                     c.getPlayer().gainItem(1242141, 1);
               }

               c.getPlayer().gainItem(2000005, 999);
               c.getPlayer().setGMLevel((byte)10);
            } else if (splitted[0].equals("스크립트리셋")) {
               ScriptManager.resetScript(c.getPlayer());
            } else if (splitted[0].equals("스킬출력")) {
               if (splitted.length < 2) {
                  SkillFactory.printAllSkillInfos();
               } else {
                  SkillFactory.printSkillInfoDetail(Integer.parseInt(splitted[1]));
               }
            } else if (splitted[0].equals("디버프")) {
               c.getPlayer().giveDebuff(SecondaryStatFlag.Seal, 1, 0, 100000L, 120, 39);
               c.getPlayer().giveDebuff(SecondaryStatFlag.Darkness, MobSkillFactory.getMobSkill(121, 27));
            }
         } else {
            if (splitted.length < 3) {
               c.getPlayer().dropMessage(5, "<문법오류> !후원지급 지급할대상 지급할금액");
               return;
            }

            String targetName = splitted[1];
            int rc = Integer.parseInt(splitted[2]);
            boolean find = false;

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chrxxxxxxxx : cs.getPlayerStorage().getAllCharacters()) {
                  if (chrxxxxxxxx.getName().equals(targetName)) {
                     chrxxxxxxxx.dropMessage(5, "후원 포인트가 " + rc + "만큼 지급되었습니다.");
                     c.getPlayer().dropMessage(5, targetName + "(이)에게 후원 포인트 " + rc + "만큼 지급되었습니다.");
                     chrxxxxxxxx.gainRealCash(rc);
                     find = true;
                     break;
                  }
               }
            }

            if (!find) {
               DBConnection db = new DBConnection();
               PreparedStatement ps = null;
               PreparedStatement ps2 = null;
               ResultSet rs = null;
               ResultSet rs2 = null;

               try (Connection con = DBConnection.getConnection()) {
                  ps2 = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
                  ps2.setString(1, targetName);
                  rs = ps2.executeQuery();
                  if (!rs.next()) {
                     c.getPlayer().dropMessage(5, targetName + "은(는) 없는 캐릭터 이름입니다.");
                     return;
                  }

                  int accountid = rs.getInt("accountid");
                  ps2.close();
                  ps2 = con.prepareStatement("SELECT realCash FROM accounts WHERE id = ?");
                  ps2.setInt(1, accountid);
                  rs2 = ps2.executeQuery();
                  if (!rs2.next()) {
                     c.getPlayer().dropMessage(5, targetName + "은(는) 없는 캐릭터 이름입니다.");
                     return;
                  }

                  ps = con.prepareStatement("UPDATE accounts SET realCash = ? WHERE id = ?");

                  try {
                     ps.setInt(1, rs2.getInt("realCash") + rc);
                     ps.setInt(2, accountid);
                     ps.executeUpdate();
                  } catch (NumberFormatException var46) {
                     c.getPlayer().dropMessage(5, "포인트는 숫자로 입력해주시기 바랍니다.");
                     return;
                  }

                  ps.executeUpdate();
               } catch (SQLException var48) {
               } finally {
                  try {
                     if (ps != null) {
                        ps.close();
                        PreparedStatement var144 = null;
                     }

                     if (ps2 != null) {
                        ps2.close();
                        PreparedStatement var162 = null;
                     }

                     if (rs != null) {
                        rs.close();
                        ResultSet var173 = null;
                     }

                     if (rs2 != null) {
                        rs2.close();
                        ResultSet var178 = null;
                     }
                  } catch (SQLException var36) {
                  }
               }

               c.getPlayer().dropMessage(5, targetName + "(이)에게 후원 포인트 " + rc + "만큼 지급되었습니다.");
            }
         }
      } else {
         MapleShopFactory shop = MapleShopFactory.getInstance();
         int shopId = Integer.parseInt(splitted[1]);
         if (shop.getShop(shopId) != null) {
            shop.getShop(shopId).sendShop(c);
         }
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("디버프", "", "플레이어에게 디버프를 적용합니다.", 5),
         new CommandDefinition("스킬선택초기화", "<스킬id>", "선택된 스킬을 최소레벨로 내립니다.", 5),
         new CommandDefinition("스킬출력", "", "메이플 속 모든 스킬들의 정보를 클라이언트 폴더에 텍스트파일로 출력합니다", 5),
         new CommandDefinition("노래", "<재생할BGM>", "Sound.wz에서 해당 경로의 BGM을 재생합니다.", 1),
         new CommandDefinition("체력낮추기", "", "자신의 HP를 1, MP를 500으로 만듭니다.", 1),
         new CommandDefinition("킬", "", "", 1),
         new CommandDefinition("체력회복", "", "자신의 HP, MP를 모두 회복합니다.", 1),
         new CommandDefinition("힐", "", "자신의 HP와 MP를 서버에서 계산된 최대 HP,MP만큼 채웁니다.", 1),
         new CommandDefinition("스킬", "<스킬id> <스킬레벨> <스킬마스터레벨>", "해당 스킬id의 스킬레벨과 마스터레벨만큼 스킬을 올립니다.", 4),
         new CommandDefinition("맥스스탯", "", "모든 스탯을 최대로 만듭니다.", 5),
         new CommandDefinition("스탯초기화", "", "모든 스탯을 초기화 합니다.", 5),
         new CommandDefinition("스킬포인트", "<스킬포인트량>", "기본 스킬포인트를 입력한 스킬포인트 양으로 만듭니다.", 4),
         new CommandDefinition("스탯포인트", "<스탯포인트량>", "스탯 포인트를 늘립니다.", 1),
         new CommandDefinition("직업", "<직업id>", "해당하는 직업 id로 전직합니다. id를 잘못 입력할 시 게임접속이 불가능해질 수 있습니다.", 5),
         new CommandDefinition("현재맵", "", "현재 맵 고유넘버를 출력합니다.", 3),
         new CommandDefinition("모두저장", "", "현재 접속중인 모든 플레이어를 저장합니다.", 5),
         new CommandDefinition("샵", "<상점ID>", "해당 상점 ID를 가진 상점을 엽니다.", 5),
         new CommandDefinition("상점", "<상점ID>", "해당 상점 ID를 가진 상점을 엽니다.", 5),
         new CommandDefinition("메소", "", "9223372036854775807메소를 가지게 만듭니다.", 6),
         new CommandDefinition("레벨업", "", "레벨업이 바로 가능한 만큼의 경험치를 획득합니다.", 4),
         new CommandDefinition("아이템", "<아이템ID> (<아이템갯수> 기본값:1)", "해당 아이템을 아이템 갯수만큼 가집니다.", 2),
         new CommandDefinition("드롭", "<아이템ID> (<아이템갯수> 기본값:1)", "해당 아이템을 아이템 갯수만큼 드롭합니다.", 2),
         new CommandDefinition("레벨", "<레벨>", "입력한 레벨로 올리거나 내립니다.", 4),
         new CommandDefinition("레벨업", "", "현재 레벨에서 레벨을 1업시킵니다.", 4),
         new CommandDefinition("온라인", "", "현재 채널에 접속중인 유저를 모두 출력합니다.", 1),
         new CommandDefinition("총온라인", "", "모든 채널에 접속중인 유저를 모두 출력합니다.", 1),
         new CommandDefinition("총온라인사냥터", "", "모든 채널에 접속중인 유저중 마을을 제외한 유저를 모두 출력합니다.", 1),
         new CommandDefinition("인벤초기화", "모두/장착/장비/소비/설치/기타/캐시/치장", "해당 탭의 인벤토리를 모두 비워버립니다.", 1),
         new CommandDefinition("백", "", "현재 캐릭터의 위치로 몬스터를 자석처럼 붙여버립니다.", 5),
         new CommandDefinition("말", "<색깔코드> <메시지>", "전체 월드에 GM텍스트 패킷을 이용하여 메시지를 출력합니다.", 2),
         new CommandDefinition("하이드", "", "다른 플레이어에게 보이지 않게 숨어버립니다.", 2),
         new CommandDefinition("스킬마스터", "<직업코드>", "직업코드의 스킬을 모두 최대레벨로 올립니다.", 5),
         new CommandDefinition("스킬초기화", "", "직업코드의 스킬을 모두 최소레벨로 내립니다.", 5),
         new CommandDefinition("후원포인트지급", "<금액>", "후원포인트를 지급합니다.", 6),
         new CommandDefinition("패킷출력", "", "패킷출력을 설정합니다.", 5),
         new CommandDefinition("리시브출력", "", "리시브출력을 설정합니다.", 5),
         new CommandDefinition("샌드출력", "", "샌드출력을 설정합니다.", 5),
         new CommandDefinition("서버점검", "", "서버점검을 설정합니다.", 5),
         new CommandDefinition("테스트패킷", "", "", 5),
         new CommandDefinition("캐시", "", "", 5),
         new CommandDefinition("피버타임", "<진행할시간분단위>", "피버타임을 설정합니다.", 6),
         new CommandDefinition("피버타임해제", "", "피버타임을 강제 해제합니다.", 6),
         new CommandDefinition("황단피버", "<곱적용될 황단 드롭배율> <진행할시간분단위>", "황단 피버타임을 설정합니다.", 6),
         new CommandDefinition("황단피버해제", "", "황단 피버타임을 강제 해제합니다.", 6),
         new CommandDefinition("지엠부여", "<부여할닉네임> <부여할권한>", "지엠권한을 부여합니다.", 10),
         new CommandDefinition("인기도", "<인기도>", "인기도를 설정합니다.", 6),
         new CommandDefinition("후원지급", "<지급할대상> <지급할금액>", "후원 포인트를 지급합니다.", 10),
         new CommandDefinition("홍보지급", "<지급할대상> <지급할포인트>", "홍보 포인트를 지급합니다.", 10),
         new CommandDefinition("코어조각", "<생성할갯수>", "코어조각을 획득합니다.", 6),
         new CommandDefinition("만렙찍기", "", "275레벨까지 필요 경험치를 획득하여 순차적으로 레벨업 시킵니다.", 6),
         new CommandDefinition("현재경험치", "", "", 6),
         new CommandDefinition("핫타임", "<지급할 아이템ID> <아이템갯수> <지급될 인원수>", "핫타임으로 전체 인원중 n명에게 아이템을 지급합니다. ", 6),
         new CommandDefinition("스크립트리셋", "", "새로운 형식의 스크립트를 리셋 합니다", 6)
      };
   }
}
