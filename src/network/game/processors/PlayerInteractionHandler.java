package network.game.processors;

import constants.GameConstants;
import constants.ServerConstants;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import network.center.Center;
import network.decode.PacketDecoder;
import network.models.CField;
import network.models.CWvsContext;
import network.models.FontColorType;
import network.models.FontType;
import network.models.PlayerShopPacket;
import objects.fields.FieldLimitType;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.child.minigame.battlereverse.BattleReverseGameInfo;
import objects.fields.child.minigame.battlereverse.BattleReversePacket;
import objects.fields.child.minigame.battlereverse.Field_BattleReverse;
import objects.fields.child.minigame.onecard.Field_OneCard;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.shop.HiredMerchant;
import objects.shop.IMaplePlayerShop;
import objects.shop.MapleMiniGame;
import objects.shop.MaplePlayerShop;
import objects.shop.MaplePlayerShopItem;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleTrade;
import objects.users.enchant.ItemFlag;
import objects.utils.Timer;

public class PlayerInteractionHandler {
   public static final void PlayerInteraction(PacketDecoder slea, final MapleClient c, final MapleCharacter chr) {
      PlayerInteractionHandler.Interaction action = PlayerInteractionHandler.Interaction.getByAction(slea.readByte() & 255);
      if (chr != null && action != null) {
         if (c.getChannelServer().getPlayerStorage().getCharacterById(c.getPlayer().getId()) != null) {
            c.getPlayer().setScrolledPosition((short)0);
            switch (action) {
               case CREATE:
                  if (chr.getPlayerShop() != null || c.getChannelServer().isShutdown()) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  byte createType = slea.readByte();
                  if (createType != 3 && createType != 4) {
                     if (createType != 1 && createType != 2 && createType != 5 && createType != 6) {
                        break;
                     }

                     if (chr.getMap()
                              .getMapObjectsInRange(chr.getTruePosition(), 20000.0, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT))
                              .size()
                           != 0
                        || chr.getMap().getPortalsInRange(chr.getTruePosition(), 20000.0).size() != 0) {
                        chr.dropMessage(1, "이곳에 상점을 세울 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if ((createType == 1 || createType == 2)
                        && (FieldLimitType.Minigames.check(chr.getMap().getFieldLimit()) || chr.getMap().allowPersonalShop())
                        && chr.getMap().getId() != ServerConstants.TownMap) {
                        chr.dropMessage(1, "이곳에 미니게임을 개설할 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     String desc = slea.readMapleAsciiString();
                     String pass = "";
                     if (slea.readByte() > 0) {
                        pass = slea.readMapleAsciiString();
                     }

                     if (createType != 1 && createType != 2) {
                        if (chr.getMap().allowPersonalShop()) {
                           Item shopxxxxx = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slea.readShort());
                           if (shopxxxxx == null
                              || shopxxxxx.getQuantity() <= 0
                              || shopxxxxx.getItemId() != slea.readInt()
                              || c.getPlayer().getMapId() < 910000001
                              || c.getPlayer().getMapId() > 910000022) {
                              return;
                           }

                           if (createType == 4) {
                           }
                        }
                        break;
                     }

                     int piece = slea.readByte();
                     int itemId = createType == 1 ? 4080000 + piece : 4080100;
                     if (!chr.haveItem(itemId) || c.getPlayer().getMapId() >= 910000001 && c.getPlayer().getMapId() <= 910000022) {
                        return;
                     }

                     MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, createType);
                     game.setPieceType(piece);
                     chr.setPlayerShop(game);
                     game.setAvailable(true);
                     game.setOpen(true);
                     game.send(c);
                     chr.getMap().addMapObject(game);
                     game.update();
                     break;
                  }

                  MapleTrade.startTrade(chr, createType == 4);
                  break;
               case INVITE_TRADE:
                  if (chr.getMap() == null) {
                     return;
                  }

                  MapleCharacter chrr = chr.getMap().getCharacterById(slea.readInt());
                  if (chrr == null || c.getChannelServer().isShutdown()) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (GameConstants.isYetiPinkBean(chrr.getJob())) {
                     c.getSession().writeAndFlush(CField.InteractionPacket.getTradeCancel((byte)0));
                     c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "핑크빈과 예티에게는 교환신청을 할 수 없습니다."));
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  MapleTrade.inviteTrade(chr, chrr, true);
                  break;
               case INVITE_ROCK_PAPER_SCISSORS:
                  if (chr.getMap() == null) {
                     return;
                  }

                  MapleCharacter chrrx = chr.getMap().getCharacterById(slea.readInt());
                  if (chrrx == null || c.getChannelServer().isShutdown()) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  MapleTrade.inviteTrade(chr, chrrx, false);
                  break;
               case DENY_TRADE:
                  MapleTrade.declineTrade(chr);
                  break;
               case VISIT:
                  if (c.getChannelServer().isShutdown()) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (chr.getTrade() == null && chr.getPlayerShop() != null) {
                     chr.dropMessage(1, "이미 닫힌 방입니다.");
                     return;
                  }

                  if (chr.getTrade() != null && chr.getTrade().getPartner() != null && !chr.getTrade().inTrade()) {
                     MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr(), chr.getTrade().getPartner().getChr().isTrade);
                  } else if (chr.getMap() != null && chr.getTrade() == null) {
                     int obid = slea.readInt();
                     MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                     if (ob == null) {
                        ob = chr.getMap().getMapObject(obid, MapleMapObjectType.SHOP);
                     }

                     if (ob instanceof IMaplePlayerShop && chr.getPlayerShop() == null) {
                        IMaplePlayerShop ipsxxxxxxxxxxxxx = (IMaplePlayerShop)ob;
                        if (ob instanceof HiredMerchant) {
                           HiredMerchant merchantxxxxx = (HiredMerchant)ipsxxxxxxxxxxxxx;
                           if (merchantxxxxx.isOpen() && merchantxxxxx.isAvailable()) {
                              if (ipsxxxxxxxxxxxxx.getFreeSlot() == -1) {
                                 chr.dropMessage(1, "해당 상점을 이미 수용가능한 최대 인원이 보고 있습니다. 잠시 후 다시 시도해 주세요.");
                              } else if (merchantxxxxx.isInBlackList(chr.getName())) {
                                 chr.dropMessage(1, "블랙리스트에 등록되어 해당 상점을 이용하실 수 없습니다.");
                              } else {
                                 chr.setPlayerShop(ipsxxxxxxxxxxxxx);
                                 merchantxxxxx.addVisitor(chr);
                                 c.getSession().writeAndFlush(PlayerShopPacket.getHiredMerch(chr, merchantxxxxx, false));
                              }
                           } else {
                              chr.dropMessage(1, "현재 고용상점이 준비중에 있습니다. 잠시 후에 다시 와주세요.");
                           }
                        } else {
                           if (ipsxxxxxxxxxxxxx instanceof MaplePlayerShop && ((MaplePlayerShop)ipsxxxxxxxxxxxxx).isBanned(chr.getName())) {
                              chr.dropMessage(1, "상점에서 강퇴당했습니다.");
                              return;
                           }

                           if (ipsxxxxxxxxxxxxx.getFreeSlot() >= 0
                              && ipsxxxxxxxxxxxxx.getVisitorSlot(chr) <= -1
                              && ipsxxxxxxxxxxxxx.isOpen()
                              && ipsxxxxxxxxxxxxx.isAvailable()) {
                              if (slea.available() > 0L && slea.readByte() > 0) {
                                 String pass = slea.readMapleAsciiString();
                                 if (!pass.equals(ipsxxxxxxxxxxxxx.getPassword())) {
                                    c.getPlayer().dropMessage(1, "비밀번호가 일치하지 않습니다. 다시 확인하시고 시도해주시기 바랍니다.");
                                    return;
                                 }
                              } else if (ipsxxxxxxxxxxxxx.getPassword().length() > 0) {
                                 c.getPlayer().dropMessage(1, "비밀번호가 일치하지 않습니다. 다시 확인하시고 시도해주시기 바랍니다.");
                                 return;
                              }

                              chr.setPlayerShop(ipsxxxxxxxxxxxxx);
                              ipsxxxxxxxxxxxxx.addVisitor(chr);
                              if (ipsxxxxxxxxxxxxx instanceof MapleMiniGame) {
                                 ((MapleMiniGame)ipsxxxxxxxxxxxxx).send(c);
                              } else {
                                 c.getSession().writeAndFlush(PlayerShopPacket.getPlayerStore(chr, false));
                              }
                           } else {
                              c.getSession().writeAndFlush(PlayerShopPacket.getMiniGameFull());
                           }
                        }
                     }
                  }
                  break;
               case HIRED_MERCHANT_MAINTENANCE:
                  if (c.getChannelServer().isShutdown() || chr.getMap() == null || chr.getTrade() != null) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  slea.skip(1);
                  byte type = slea.readByte();
                  if (type != 5) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  String password = slea.readMapleAsciiString();
                  int obidx = slea.readInt();
                  MapleMapObject obx = chr.getMap().getMapObject(obidx, MapleMapObjectType.HIRED_MERCHANT);
                  if (obx == null || chr.getPlayerShop() != null) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (obx instanceof IMaplePlayerShop && obx instanceof HiredMerchant) {
                     IMaplePlayerShop ipsxxxxxxxxxxxxx = (IMaplePlayerShop)obx;
                     HiredMerchant merchantxxxxx = (HiredMerchant)ipsxxxxxxxxxxxxx;
                     if (merchantxxxxx.isOwner(chr) && merchantxxxxx.isOpen() && merchantxxxxx.isAvailable()) {
                        merchantxxxxx.setOpen(false);
                        merchantxxxxx.removeAllVisitors(16, 0);
                        chr.setPlayerShop(ipsxxxxxxxxxxxxx);
                        c.getSession().writeAndFlush(PlayerShopPacket.getHiredMerch(chr, merchantxxxxx, false));
                     } else {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     }
                  }
                  break;
               case CHAT:
                  slea.readInt();
                  String message = slea.readMapleAsciiString();
                  if (chr.getTrade() != null) {
                     chr.getTrade().chat(message);
                  } else if (chr.getPlayerShop() != null) {
                     IMaplePlayerShop ipsxxxxxxxxx = chr.getPlayerShop();
                     ipsxxxxxxxxx.broadcastToVisitors(PlayerShopPacket.shopChat(chr.getName(), message, chr.getId(), ipsxxxxxxxxx.getVisitorSlot(chr)));
                     if (chr.getClient().isMonitored()) {
                        Center.Broadcast.broadcastGMMessage(
                           CWvsContext.serverNotice(6, chr.getName() + " said in " + ipsxxxxxxxxx.getOwnerName() + " shop : " + message)
                        );
                     }
                  }
                  break;
               case EXIT:
                  if (chr.getTrade() != null) {
                     MapleTrade.cancelTrade(chr.getTrade(), chr.getClient(), chr);
                  } else if (chr.getMap() instanceof Field_OneCard) {
                     ((Field_OneCard)chr.getMap()).playerDead(chr);
                  } else if (chr.getMap() instanceof Field_BattleReverse) {
                     Field_BattleReverse fbr = (Field_BattleReverse)chr.getMap();
                     int team = fbr.getBattleReverseGameDlg().getGameInfo().getTeamByChr(chr);
                     fbr.setWinTeam(team == 0 ? 1 : 0);
                     fbr.setEndGame(true);
                  } else {
                     IMaplePlayerShop ipsxxxxxxxxxxxx = chr.getPlayerShop();
                     if (ipsxxxxxxxxxxxx == null) {
                        return;
                     }

                     if (ipsxxxxxxxxxxxx.isOwner(chr) && ipsxxxxxxxxxxxx.getShopType() != 1) {
                        ipsxxxxxxxxxxxx.closeShop(false, ipsxxxxxxxxxxxx.isAvailable());
                     } else {
                        ipsxxxxxxxxxxxx.removeVisitor(chr);
                     }

                     chr.setPlayerShop(null);
                  }
                  break;
               case OPEN:
                  IMaplePlayerShop shopx = chr.getPlayerShop();
                  if (shopx != null && shopx.isOwner(chr) && shopx.getShopType() < 3 && !shopx.isAvailable() && chr.getMap().allowPersonalShop()) {
                     if (c.getChannelServer().isShutdown()) {
                        chr.dropMessage(1, "서버가 곧 종료되기때문에, 상점을 세울수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        shopx.closeShop(shopx.getShopType() == 1, false);
                        return;
                     }

                     if (shopx.getShopType() == 2) {
                        shopx.setOpen(true);
                        shopx.setAvailable(true);
                        shopx.update();
                     }
                  }
                  break;
               case SET_ITEMS4:
               case SET_ITEMS3:
               case SET_ITEMS2:
               case SET_ITEMS1:
                  MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                  MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
                  Item item = chr.getInventory(ivType).getItem(slea.readShort());
                  short quantity = slea.readShort();
                  byte targetSlot = slea.readByte();
                  if (item.getItemId() / 1000000 == 1) {
                     Equip e = (Equip)item;
                     if (e.getCashEnchantCount() > 0) {
                        c.getPlayer().dropMessage(1, "치장 옵션 부여가 적용된 아이템은 등록할 수 없습니다.");
                        return;
                     }
                  }

                  if (quantity < 0) {
                     return;
                  }

                  if (chr.getTrade() != null
                     && item != null
                     && (
                        quantity <= item.getQuantity() && quantity >= 0
                           || GameConstants.isThrowingStar(item.getItemId())
                           || GameConstants.isBullet(item.getItemId())
                     )) {
                     chr.getTrade().setItems(c, item, targetSlot, quantity, ivType);
                  }
                  break;
               case SET_MESO4:
               case SET_MESO3:
               case SET_MESO2:
               case SET_MESO1:
                  MapleTrade trade = chr.getTrade();
                  if (trade != null) {
                     trade.setMeso(slea.readLong());
                  }
                  break;
               case ADD_ITEM4:
               case ADD_ITEM3:
               case ADD_ITEM2:
               case ADD_ITEM1:
                  MapleInventoryType typex = MapleInventoryType.getByType(slea.readByte());
                  short slotx = slea.readShort();
                  short bundles = slea.readShort();
                  short perBundle = slea.readShort();
                  int price = slea.readInt();
                  if (price <= 0 || bundles <= 0 || perBundle <= 0) {
                     return;
                  }

                  IMaplePlayerShop shopxx = chr.getPlayerShop();
                  if (shopxx == null || !shopxx.isOwner(chr) || shopxx instanceof MapleMiniGame) {
                     return;
                  }

                  MapleItemInformationProvider iix = MapleItemInformationProvider.getInstance();
                  Item ivItem = chr.getInventory(typex).getItem(slotx);
                  if (ivItem != null) {
                     if (ivItem.getItemId() / 1000000 == 1) {
                        Equip e = (Equip)ivItem;
                        if (e.getCashEnchantCount() > 0) {
                           c.getPlayer().dropMessage(1, "치장 옵션 부여가 적용된 아이템은 등록할 수 없습니다.");
                           return;
                        }
                     }

                     long check = bundles * perBundle;
                     if (check > 32767L || check <= 0L) {
                        return;
                     }

                     short bundles_perbundle = (short)(bundles * perBundle);
                     if (ivItem.getQuantity() < bundles_perbundle) {
                        chr.dropMessage(1, "물품을 판매하려면 적어도 1개이상 있어야 합니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     int flag = ivItem.getFlag();
                     if (ItemFlag.POSSIBLE_TRADING.check(flag) || ItemFlag.PROTECTED.check(flag)) {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if ((iix.isDropRestricted(ivItem.getItemId()) || iix.isAccountShared(ivItem.getItemId()))
                        && !ItemFlag.KARMA_EQ.check(flag)
                        && !ItemFlag.KARMA_USE.check(flag)) {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (ivItem.getPet() != null) {
                        c.getPlayer().unequipPet(ivItem.getPet(), false, false, 0);
                     }

                     if (bundles_perbundle >= 50 && ivItem.getItemId() == 2340000) {
                        c.setMonitored(true);
                     }

                     if (GameConstants.getLowestPrice(ivItem.getItemId()) > price) {
                        c.getPlayer().dropMessage(1, "The lowest you can sell this for is " + GameConstants.getLowestPrice(ivItem.getItemId()));
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (!GameConstants.isThrowingStar(ivItem.getItemId()) && !GameConstants.isBullet(ivItem.getItemId())) {
                        MapleInventoryManipulator.removeFromSlot(c, typex, slotx, bundles_perbundle, true);
                        Item sellItem = ivItem.copy();
                        sellItem.setQuantity(perBundle);
                        shopxx.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                     } else {
                        MapleInventoryManipulator.removeFromSlot(c, typex, slotx, ivItem.getQuantity(), true);
                        Item sellItem = ivItem.copy();
                        shopxx.addItem(new MaplePlayerShopItem(sellItem, (short)1, price));
                     }

                     c.getSession().writeAndFlush(PlayerShopPacket.shopItemUpdate(shopxx));
                  }
                  break;
               case CONFIRM_TRADE_MESO1:
               case CONFIRM_TRADE_MESO2:
               case CONFIRM_TRADE2:
               case CONFIRM_TRADE1:
               case BUY_ITEM_PLAYER_SHOP:
               case BUY_ITEM_STORE:
               case BUY_ITEM_HIREDMERCHANT:
                  if (chr.getTrade() != null) {
                     MapleTrade.completeTrade(chr);
                  } else {
                     int itemx = slea.readByte();
                     short quantityx = slea.readShort();
                     IMaplePlayerShop shopxxx = chr.getPlayerShop();
                     if (quantityx < 0) {
                        return;
                     }

                     if (shopxxx == null || shopxxx.isOwner(chr) || shopxxx instanceof MapleMiniGame || itemx >= shopxxx.getItems().size()) {
                        return;
                     }

                     MaplePlayerShopItem tobuy = shopxxx.getItems().get(itemx);
                     if (tobuy == null) {
                        return;
                     }

                     long checkx = tobuy.bundles * quantityx;
                     long check2 = tobuy.price * quantityx;
                     long check3 = tobuy.item.getQuantity() * quantityx;
                     if (checkx <= 0L || check2 > 2147483647L || check2 <= 0L || check3 > 32767L || check3 < 0L) {
                        return;
                     }

                     if (tobuy.bundles < quantityx
                        || tobuy.bundles % quantityx != 0 && GameConstants.isEquip(tobuy.item.getItemId())
                        || chr.getMeso() - check2 < 0L
                        || chr.getMeso() - check2 > 2147483647L
                        || shopxxx.getMeso() + check2 < 0L
                        || shopxxx.getMeso() + check2 > 2147483647L) {
                        return;
                     }

                     if (quantityx >= 50 && tobuy.item.getItemId() == 2340000) {
                        c.setMonitored(true);
                     }

                     shopxxx.buy(c, itemx, quantityx);
                     shopxxx.broadcastToVisitors(PlayerShopPacket.shopItemUpdate(shopxxx));
                  }
                  break;
               case RESET_HIRED:
                  byte subpacket = slea.readByte();
                  byte typexx = slea.readByte();
                  if (subpacket != 19 || typexx != 5 && typexx != 6) {
                     if (subpacket == 11 && typexx == 4) {
                        IMaplePlayerShop shopxxxxx = chr.getPlayerShop();
                        shopxxxxx.setOpen(true);
                        shopxxxxx.setAvailable(true);
                        shopxxxxx.update();
                     } else if (subpacket == 16 && typexx == 7) {
                        String secondPassword = slea.readMapleAsciiString();
                        if (c.CheckSecondPassword(secondPassword)) {
                           chrrx = chr.getMap().getCharacterById(slea.readInt());
                           if (chrrx == null || c.getChannelServer().isShutdown()) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           MapleTrade.startCashTrade(chr);
                           MapleTrade.inviteCashTrade(chr, chrrx);
                        } else {
                           c.getPlayer().dropMessage(1, "2차비밀번호가 일치하지 않습니다. \r\n확인후 다시시도 해주세요.");
                        }
                     } else if (subpacket == 19 && typexx == 7) {
                        String secondPassword = slea.readMapleAsciiString();
                        if (c.CheckSecondPassword(secondPassword)) {
                           MapleTrade.visitCashTrade(chr, chr.getTrade().getPartner().getChr());
                        } else {
                           c.getPlayer().dropMessage(1, "2차비밀번호가 일치하지 않습니다. \r\n확인후 다시시도 해주세요.");
                        }
                     }
                  } else {
                     String secondPassword = slea.readMapleAsciiString();
                     if (c.CheckSecondPassword(secondPassword)) {
                        int obidxx = slea.readInt();
                        MapleMapObject obxx = chr.getMap().getMapObject(obidxx, MapleMapObjectType.HIRED_MERCHANT);
                        if (obxx == null) {
                           return;
                        }

                        IMaplePlayerShop ipsxxxxxxxxxxxxx = (IMaplePlayerShop)obxx;
                        if (obxx instanceof HiredMerchant) {
                           HiredMerchant merchantxxxxx = (HiredMerchant)ipsxxxxxxxxxxxxx;
                           if (merchantxxxxx.isOwner(chr) && merchantxxxxx.isOpen() && merchantxxxxx.isAvailable()) {
                              merchantxxxxx.setOpen(false);
                              merchantxxxxx.broadcastToVisitors(CWvsContext.serverNotice(1, "판매자가 물품을 정리하고 있습니다."));
                              merchantxxxxx.removeAllVisitors(0, 1);
                              chr.setPlayerShop(ipsxxxxxxxxxxxxx);
                              c.getSession().writeAndFlush(PlayerShopPacket.getHiredMerch(chr, merchantxxxxx, false));
                           }
                        }
                     } else {
                        c.getPlayer().dropMessage(1, "2차비밀번호가 일치하지 않습니다. \r\n확인후 다시시도 해주세요.");
                     }
                  }
                  break;
               case REMOVE_ITEM:
                  slea.skip(1);
                  int slotxx = slea.readShort();
                  IMaplePlayerShop shopxxxx = chr.getPlayerShop();
                  if (shopxxxx == null
                     || !shopxxxx.isOwner(chr)
                     || shopxxxx instanceof MapleMiniGame
                     || shopxxxx.getItems().size() <= 0
                     || shopxxxx.getItems().size() <= slotxx
                     || slotxx < 0) {
                     return;
                  }

                  MaplePlayerShopItem itemxx = shopxxxx.getItems().get(slotxx);
                  if (itemxx != null && itemxx.bundles > 0) {
                     Item item_get = itemxx.item.copy();
                     long checkxx = itemxx.bundles * itemxx.item.getQuantity();
                     if (checkxx < 0L || checkxx > 32767L) {
                        return;
                     }

                     item_get.setQuantity((short)checkxx);
                     if (item_get.getQuantity() >= 50 && itemxx.item.getItemId() == 2340000) {
                        c.setMonitored(true);
                     }

                     if (MapleInventoryManipulator.checkSpace(c, item_get.getItemId(), item_get.getQuantity(), item_get.getOwner())) {
                        MapleInventoryManipulator.addFromDrop(c, item_get, false);
                        itemxx.bundles = 0;
                        shopxxxx.removeFromSlot(slotxx);
                     }
                  }

                  c.getSession().writeAndFlush(PlayerShopPacket.shopItemUpdate(shopxxxx));
                  break;
               case MAINTANCE_OFF:
                  IMaplePlayerShop shop = chr.getPlayerShop();
                  if (shop != null && shop instanceof HiredMerchant && shop.isOwner(chr) && shop.isAvailable()) {
                     shop.setOpen(true);
                     shop.removeAllVisitors(-1, -1);
                  }
                  break;
               case MAINTANCE_ORGANISE:
                  IMaplePlayerShop impsx = chr.getPlayerShop();
                  if (impsx != null && impsx.isOwner(chr) && !(impsx instanceof MapleMiniGame)) {
                     for (int i = 0; i < impsx.getItems().size(); i++) {
                        if (impsx.getItems().get(i).bundles == 0) {
                           impsx.getItems().remove(i);
                        }
                     }

                     if (chr.getMeso() + impsx.getMeso() > 0L) {
                        chr.gainMeso(impsx.getMeso(), false);
                        impsx.setMeso(0);
                     }

                     c.getSession().writeAndFlush(PlayerShopPacket.shopItemUpdate(impsx));
                  }
                  break;
               case CLOSE_MERCHANT:
                  IMaplePlayerShop merchantxxxx = chr.getPlayerShop();
                  if (merchantxxxx != null && merchantxxxx.getShopType() == 1 && merchantxxxx.isOwner(chr)) {
                     boolean save = false;
                     if (chr.getMeso() + merchantxxxx.getMeso() < 0L) {
                        save = false;
                     } else {
                        if (merchantxxxx.getMeso() > 0) {
                           chr.gainMeso(merchantxxxx.getMeso(), false);
                        }

                        merchantxxxx.setMeso(0);
                        if (merchantxxxx.getItems().size() > 0) {
                           for (MaplePlayerShopItem items : merchantxxxx.getItems()) {
                              if (items.bundles > 0) {
                                 Item item_get = items.item.copy();
                                 item_get.setQuantity((short)(items.bundles * items.item.getQuantity()));
                                 if (!MapleInventoryManipulator.addFromDrop(c, item_get, false)) {
                                    save = true;
                                    break;
                                 }

                                 items.bundles = 0;
                                 save = false;
                              }
                           }
                        }
                     }

                     if (save) {
                        c.getPlayer().dropMessage(1, "프레드릭 에게서 아이템을 찾아가 주십시오.");
                        c.getSession().writeAndFlush(PlayerShopPacket.shopErrorMessage(20, 0));
                     }

                     merchantxxxx.closeShop(save, true);
                     chr.setPlayerShop(null);
                  }
                  break;
               case TAKE_MESOS:
                  IMaplePlayerShop imps = chr.getPlayerShop();
                  if (imps != null && imps.isOwner(chr)) {
                     if (chr.getMeso() + imps.getMeso() < 0L) {
                        c.getSession().writeAndFlush(PlayerShopPacket.shopItemUpdate(imps));
                     } else {
                        chr.gainMeso(imps.getMeso(), false);
                        imps.setMeso(0);
                        c.getSession().writeAndFlush(PlayerShopPacket.shopItemUpdate(imps));
                     }
                  }
                  break;
               case ADMIN_STORE_NAMECHANGE:
                  String storename = slea.readMapleAsciiString();
                  c.getSession().writeAndFlush(PlayerShopPacket.merchantNameChange(chr.getId(), storename));
                  break;
               case VIEW_MERCHANT_VISITOR:
                  IMaplePlayerShop merchantxxx = chr.getPlayerShop();
                  if (merchantxxx != null && merchantxxx.getShopType() == 1 && merchantxxx.isOwner(chr)) {
                     ((HiredMerchant)merchantxxx).sendVisitor(c);
                  }
                  break;
               case VIEW_MERCHANT_BLACKLIST:
                  IMaplePlayerShop merchantxx = chr.getPlayerShop();
                  if (merchantxx != null && merchantxx.getShopType() == 1 && merchantxx.isOwner(chr)) {
                     ((HiredMerchant)merchantxx).sendBlackList(c);
                  }
                  break;
               case MERCHANT_BLACKLIST_ADD:
                  IMaplePlayerShop merchantx = chr.getPlayerShop();
                  if (merchantx != null && merchantx.getShopType() == 1 && merchantx.isOwner(chr)) {
                     ((HiredMerchant)merchantx).addBlackList(slea.readMapleAsciiString());
                  }
                  break;
               case MERCHANT_BLACKLIST_REMOVE:
                  IMaplePlayerShop merchant = chr.getPlayerShop();
                  if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                     ((HiredMerchant)merchant).removeBlackList(slea.readMapleAsciiString());
                  }
                  break;
               case GIVE_UP:
                  IMaplePlayerShop ipsxxxxxxxx = chr.getPlayerShop();
                  if (ipsxxxxxxxx != null && ipsxxxxxxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxxxxxxx;
                     if (!game.isOpen()) {
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 0, game.getVisitorSlot(chr)));
                        game.nextLoser();
                        game.setOpen(true);
                        game.update();
                        game.checkExitAfterGame();
                     }
                  }
                  break;
               case EXPEL:
                  IMaplePlayerShop ipsxxxxxxx = chr.getPlayerShop();
                  if (ipsxxxxxxx != null && ipsxxxxxxx instanceof MapleMiniGame && ((MapleMiniGame)ipsxxxxxxx).isOpen()) {
                     ipsxxxxxxx.removeAllVisitors(5, 1);
                  }
                  break;
               case READY:
               case UN_READY:
                  IMaplePlayerShop ipsxxxxxx = chr.getPlayerShop();
                  if (ipsxxxxxx != null && ipsxxxxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxxxxx;
                     if (!game.isOwner(chr) && game.isOpen()) {
                        game.setReady(game.getVisitorSlot(chr));
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameReady(game.isReady(game.getVisitorSlot(chr))));
                     }
                  }
                  break;
               case START:
                  IMaplePlayerShop ipsxxxxxxxxx = chr.getPlayerShop();
                  if (ipsxxxxxxxxx != null && ipsxxxxxxxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxxxxxxxx;
                     if (game.isOwner(chr) && game.isOpen()) {
                        for (int ix = 1; ix < ipsxxxxxxxxx.getSize(); ix++) {
                           if (!game.isReady(ix)) {
                              return;
                           }
                        }

                        game.setGameType();
                        game.shuffleList();
                        if (game.getGameType() == 1) {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameStart(game.getLoser()));
                        } else {
                           game.broadcastToVisitors(PlayerShopPacket.getMatchCardStart(game, game.getLoser()));
                        }

                        game.setOpen(false);
                        game.update();
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameInfoMsg((byte)102, chr.getName()));
                     }
                  } else if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                     c.getSession().writeAndFlush(PlayerShopPacket.StartRPS());
                     chr.getTrade().getPartner().getChr().getClient().getSession().writeAndFlush(PlayerShopPacket.StartRPS());
                  }
                  break;
               case REQUEST_TIE:
                  IMaplePlayerShop ipsxxxxx = chr.getPlayerShop();
                  if (ipsxxxxx != null && ipsxxxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxxxx;
                     if (!game.isOpen()) {
                        if (game.isOwner(chr)) {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestTie(), false);
                        } else {
                           game.getMCOwner().getClient().getSession().writeAndFlush(PlayerShopPacket.getMiniGameRequestTie());
                        }

                        game.setRequestedTie(game.getVisitorSlot(chr));
                     }
                  }
                  break;
               case ANSWER_TIE:
                  IMaplePlayerShop ipsxxxx = chr.getPlayerShop();
                  if (ipsxxxx != null && ipsxxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxxx;
                     if (!game.isOpen() && game.getRequestedTie() > -1 && game.getRequestedTie() != game.getVisitorSlot(chr)) {
                        if (slea.readByte() > 0) {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 1, game.getRequestedTie()));
                           game.nextLoser();
                           game.setOpen(true);
                           game.update();
                           game.checkExitAfterGame();
                        } else {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
                        }

                        game.setRequestedTie(-1);
                     }
                  }
                  break;
               case REQUEST_REDO:
                  IMaplePlayerShop ipsxxx = chr.getPlayerShop();
                  if (ipsxxx != null && ipsxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxx;
                     if (!game.isOpen()) {
                        if (game.isOwner(chr)) {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestRedo(), false);
                        } else {
                           game.getMCOwner().getClient().getSession().writeAndFlush(PlayerShopPacket.getMiniGameRequestRedo());
                        }
                     }
                  }
                  break;
               case ANSWER_REDO:
                  IMaplePlayerShop ipsxx = chr.getPlayerShop();
                  if (ipsxx != null && ipsxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxx;
                     if (!game.isOpen()) {
                        if (slea.readByte() > 0) {
                           ipsxx.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip(ipsxx.getVisitorSlot(chr) == 0 ? 1 : 0));
                           game.nextLoser();
                        } else {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyRedo());
                        }
                     }
                  }
                  break;
               case SKIP:
                  IMaplePlayerShop ipsxxxxxxxxxx = chr.getPlayerShop();
                  if (ipsxxxxxxxxxx != null && ipsxxxxxxxxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxxxxxxxxx;
                     if (!game.isOpen()) {
                        if (game.getLoser() != ipsxxxxxxxxxx.getVisitorSlot(chr)) {
                           if (game.getGameType() == 1) {
                              game.forceFinishGame();
                           }

                           return;
                        }

                        ipsxxxxxxxxxx.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip(ipsxxxxxxxxxx.getVisitorSlot(chr) == 0 ? 1 : 0));
                        game.nextLoser();
                     }
                  } else if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                     chr.getTrade().setRPS(slea.readByte());
                     Timer.ShowTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           byte result = PlayerInteractionHandler.getResult(chr.getTrade().getPRS(), chr.getTrade().getPartner().getPRS());
                           if (result == 2) {
                              chr.dropMessage(1, "아쉽지만, 가위바위보에서 지셨습니다!");
                              chr.addFame(-1);
                           } else if (result == 0) {
                              chr.dropMessage(1, "축하합니다! 가위바위보에서 이기셨습니다!");
                              chr.addFame(1);
                           }

                           c.getSession().writeAndFlush(PlayerShopPacket.FinishRPS(result, chr.getTrade().getPartner().getPRS()));
                        }
                     }, 1000L);
                  }
                  break;
               case MOVE_OMOK:
                  IMaplePlayerShop ipsx = chr.getPlayerShop();
                  if (ipsx != null && ipsx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsx;
                     if (!game.isOpen()) {
                        if (game.getLoser() != game.getVisitorSlot(chr)) {
                           return;
                        }

                        game.setPiece(slea.readInt(), slea.readInt(), slea.readByte(), chr);
                        game.addOmokPlayers(chr.getId());
                     }
                  }
                  break;
               case SELECT_CARD:
                  IMaplePlayerShop ipsxxxxxxxxxxx = chr.getPlayerShop();
                  if (ipsxxxxxxxxxxx != null && ipsxxxxxxxxxxx instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ipsxxxxxxxxxxx;
                     if (!game.isOpen()) {
                        if (game.getLoser() != game.getVisitorSlot(chr)) {
                           return;
                        }

                        if (slea.readByte() != game.getTurn()) {
                           return;
                        }

                        int slot = slea.readByte();
                        int turn = game.getTurn();
                        int fs = game.getFirstSlot();
                        if (turn == 1) {
                           game.setFirstSlot(slot);
                           if (game.isOwner(chr)) {
                              game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn), false);
                           } else {
                              game.getMCOwner().getClient().getSession().writeAndFlush(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn));
                           }

                           game.setTurn(0);
                           return;
                        }

                        if (fs > 0 && game.getCardId(fs + 1) == game.getCardId(slot + 1)) {
                           game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 2 : 3));
                           game.setPoints(game.getVisitorSlot(chr));
                        } else {
                           game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 0 : 1));
                           game.nextLoser();
                        }

                        game.setTurn(1);
                        game.setFirstSlot(0);
                     }
                  }
                  break;
               case EXIT_AFTER_GAME:
               case CANCEL_EXIT:
                  IMaplePlayerShop ips = chr.getPlayerShop();
                  if (ips != null && ips instanceof MapleMiniGame) {
                     MapleMiniGame game = (MapleMiniGame)ips;
                     if (!game.isOpen()) {
                        game.setExitAfter(chr);
                        if (game.isExitAfter(chr)) {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameInfoMsg((byte)5, chr.getName()));
                        } else {
                           game.broadcastToVisitors(PlayerShopPacket.getMiniGameInfoMsg((byte)6, chr.getName()));
                        }
                     }
                  }
                  break;
               case ONE_CARD:
                  if (chr.getMap() instanceof Field_OneCard) {
                     ((Field_OneCard)chr.getMap()).setAction(chr, slea);
                  }
                  break;
               case ONE_CARD_EMOTION:
                  if (chr.getMap() instanceof Field_OneCard) {
                     ((Field_OneCard)chr.getMap()).setEmotion(chr, slea);
                  }
                  break;
               case BATTLE_REVERSE_TURN_END:
                  if (chr.getMap() instanceof Field_BattleReverse) {
                     Field_BattleReverse fbr = (Field_BattleReverse)chr.getMap();
                     BattleReverseGameInfo gameInfo = fbr.getBattleReverseGameDlg().getGameInfo();
                     int team = gameInfo.getTeamByChr(chr);
                     if (gameInfo.getTurnTeam() == team && gameInfo.canNextTurn()) {
                        gameInfo.incWarnCount(team);
                        int warncount = gameInfo.getWarnCount(team);
                        if (warncount >= 5) {
                           chr.send(CField.UIPacket.sendBigScriptProgressMessage("시간 초과 5회로 게임에서 패배합니다.", FontType.NanumGothic, FontColorType.Yellow));
                           fbr.setWinTeam(team == 0 ? 1 : 0);
                           fbr.setEndGame(true);
                           return;
                        }

                        chr.send(
                           CField.UIPacket.sendBigScriptProgressMessage(
                              "시간이 초과되어 턴이 넘어갑니다. 경고 " + warncount + "회. 5회 초과시 게임에서 패배합니다.", FontType.NanumGothic, FontColorType.Yellow
                           )
                        );
                        List<Point> putlists = gameInfo.getPuttableList(team);
                        Collections.shuffle(putlists);
                        Point pos = putlists.get(0);
                        int x = pos.x;
                        int y = pos.y;
                        gameInfo.ProcessChips(x, y, (byte)team);
                        MapleCharacter otherplayer = gameInfo.getCharacter(team == 1 ? 0 : 1);
                        chr.send(BattleReversePacket.PutBattleReverseStone(gameInfo, new Point(x, y), team == 1 ? 0 : 1, chr));
                        otherplayer.send(BattleReversePacket.PutBattleReverseStone(gameInfo, new Point(x, y), team == 1 ? 0 : 1, otherplayer));
                        gameInfo.nextTurnTeam();
                        byte[][] board = gameInfo.getBoard();
                        int chipcount = 0;
                        int freechip = 0;

                        for (int ixx = 0; ixx < 8; ixx++) {
                           for (int j = 0; j < 8; j++) {
                              if (board[ixx][j] == (team == 0 ? 1 : 0)) {
                                 chipcount++;
                              } else if (board[ixx][j] == -1) {
                                 freechip++;
                              }
                           }
                        }

                        if (freechip == 0) {
                           int team0hp = gameInfo.getTeamHP(0);
                           int team1hp = gameInfo.getTeamHP(1);
                           if (team0hp == team1hp) {
                              fbr.setWinTeam(2);
                              fbr.setEndGame(true);
                           } else if (team0hp > team1hp) {
                              fbr.setWinTeam(0);
                              fbr.setEndGame(true);
                           } else {
                              fbr.setWinTeam(1);
                              fbr.setEndGame(true);
                           }
                        } else if (chipcount == 0) {
                           fbr.setWinTeam(team);
                           fbr.setEndGame(true);
                        } else if (gameInfo.getPuttableList(team == 0 ? 1 : 0).size() == 0) {
                           gameInfo.nextTurnTeam();
                           if (gameInfo.getPuttableList(team).size() == 0) {
                              int team0hp = gameInfo.getTeamHP(0);
                              int team1hp = gameInfo.getTeamHP(1);
                              if (team0hp == team1hp) {
                                 fbr.setWinTeam(2);
                                 fbr.setEndGame(true);
                              } else if (team0hp > team1hp) {
                                 fbr.setWinTeam(0);
                                 fbr.setEndGame(true);
                              } else {
                                 fbr.setWinTeam(1);
                                 fbr.setEndGame(true);
                              }

                              return;
                           }

                           chr.send(CField.UIPacket.sendBigScriptProgressMessage("상대방이 놓을수 있는 곳이 없어 턴이 넘어옵니다.", FontType.NanumGothic, FontColorType.Yellow));
                           otherplayer.send(CField.UIPacket.sendBigScriptProgressMessage("놓을수 있는 곳이 없어 턴이 종료됩니다.", FontType.NanumGothic, FontColorType.Yellow));
                           chr.send(BattleReversePacket.StartBattleReverseStone(gameInfo, gameInfo.getTurnTeam(), chr));
                           otherplayer.send(BattleReversePacket.StartBattleReverseStone(gameInfo, gameInfo.getTurnTeam(), otherplayer));
                        }
                     }
                  }
                  break;
               default:
                  System.out.println("Unhandled interaction action by " + chr.getName() + " : " + action + ", " + slea.toString());
            }
         }
      }
   }

   public static final byte getResult(byte rps1, byte rps2) {
      switch (rps1) {
         case 0:
            if (rps2 == 1) {
               return 2;
            }

            if (rps2 == 2) {
               return 0;
            }
            break;
         case 1:
            if (rps2 == 2) {
               return 2;
            }

            if (rps2 == 0) {
               return 0;
            }
            break;
         case 2:
            if (rps2 == 0) {
               return 2;
            }

            if (rps2 == 1) {
               return 0;
            }
      }

      return 1;
   }

   public static enum Interaction {
      SET_ITEMS1(0),
      SET_ITEMS2(1),
      SET_ITEMS3(2),
      SET_ITEMS4(3),
      SET_MESO1(4),
      SET_MESO2(5),
      SET_MESO3(6),
      SET_MESO4(7),
      CONFIRM_TRADE1(8),
      CONFIRM_TRADE2(9),
      CONFIRM_TRADE_MESO1(10),
      CONFIRM_TRADE_MESO2(11),
      CREATE(16),
      VISIT(19),
      INVITE_TRADE(21),
      DENY_TRADE(22),
      CHAT(24),
      OPEN(26),
      EXIT(28),
      HIRED_MERCHANT_MAINTENANCE(29),
      RESET_HIRED(30),
      ADD_ITEM1(31),
      ADD_ITEM2(32),
      ADD_ITEM3(33),
      ADD_ITEM4(34),
      BUY_ITEM_HIREDMERCHANT(35),
      PLAYER_SHOP_ADD_ITEM(36),
      BUY_ITEM_PLAYER_SHOP(37),
      BUY_ITEM_STORE(38),
      REMOVE_ITEM(47),
      MAINTANCE_OFF(48),
      MAINTANCE_ORGANISE(49),
      CLOSE_MERCHANT(50),
      TAKE_MESOS(52),
      VIEW_MERCHANT_VISITOR(-1),
      VIEW_MERCHANT_BLACKLIST(-1),
      MERCHANT_BLACKLIST_ADD(-1),
      MERCHANT_BLACKLIST_REMOVE(-1),
      ADMIN_STORE_NAMECHANGE(-1),
      REQUEST_TIE(56),
      ANSWER_TIE(57),
      GIVE_UP(58),
      REQUEST_REDO(60),
      ANSWER_REDO(61),
      EXIT_AFTER_GAME(62),
      CANCEL_EXIT(63),
      READY(64),
      UN_READY(65),
      EXPEL(66),
      START(67),
      RESULT(68),
      SKIP(69),
      MOVE_OMOK(70),
      SELECT_CARD(74),
      INVITE_ROCK_PAPER_SCISSORS(83),
      ONE_CARD(126),
      ONE_CARD_EMOTION(127),
      BATTLE_REVERSE_TURN_END(163);

      public int action;

      private Interaction(int action) {
         this.action = action;
      }

      public static PlayerInteractionHandler.Interaction getByAction(int i) {
         for (PlayerInteractionHandler.Interaction s : values()) {
            if (s.action == i) {
               return s;
            }
         }

         return null;
      }
   }
}
