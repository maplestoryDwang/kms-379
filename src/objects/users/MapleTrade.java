package objects.users;

import constants.GameConstants;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import logging.LoggingManager;
import logging.entry.TradeLog;
import logging.entry.TradeLogType;
import network.center.Center;
import network.models.CField;
import network.models.CWvsContext;
import network.models.PlayerShopPacket;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.enchant.EquipSpecialAttribute;
import objects.users.enchant.ItemFlag;

public class MapleTrade {
   private MapleTrade partner = null;
   private int partnerChrID = 0;
   private String partnerChrName = "";
   private int partnerAccID = 0;
   private String partnerAccName = "";
   private final List<Item> items = new LinkedList<>();
   private List<Item> exchangeItems;
   private long meso = 0L;
   private long exchangeMeso = 0L;
   private boolean locked = false;
   private boolean inTrade = false;
   private final WeakReference<MapleCharacter> chr;
   private final byte tradingslot;
   private byte rps = 0;

   public MapleTrade(byte tradingslot, MapleCharacter chr) {
      this.tradingslot = tradingslot;
      this.chr = new WeakReference<>(chr);
   }

   public final void CompleteTrade() {
      if (this.exchangeItems != null) {
         for (Item item : new LinkedList<>(this.exchangeItems)) {
            int flag = item.getFlag();
            if (ItemFlag.KARMA_EQ.check(flag)) {
               item.setFlag((short)(flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
               item.setFlag((short)(flag - ItemFlag.KARMA_USE.getValue()));
            }

            MapleInventoryManipulator.addFromDrop(this.chr.get().getClient(), item, false);
         }

         this.exchangeItems.clear();
      }

      if (this.exchangeMeso > 0L) {
         this.chr.get().gainMeso(this.exchangeMeso - GameConstants.getTaxAmount(this.exchangeMeso), false, false, true);
      }

      this.exchangeMeso = 0L;
      this.chr.get().getClient().getSession().writeAndFlush(CField.InteractionPacket.TradeMessage(this.tradingslot, (byte)7));
   }

   public final void cancel(MapleClient c, MapleCharacter chr) {
      if (this.items != null) {
         for (Item item : new LinkedList<>(this.items)) {
            MapleInventoryManipulator.addFromDrop(c, item, false);
         }

         this.items.clear();
      }

      if (this.meso > 0L) {
         chr.gainMeso(this.meso, false, false, true);
      }

      this.meso = 0L;
      c.getSession().writeAndFlush(CField.InteractionPacket.getTradeCancel(this.tradingslot));
   }

   public final boolean isLocked() {
      return this.locked;
   }

   public final void setMeso(long meso) {
      if (!this.locked && this.partner != null && meso > 0L && this.meso + meso > 0L) {
         if (this.chr.get().getMeso() >= meso) {
            this.chr.get().gainMeso(-meso, false, false, true);
            this.meso += meso;
            this.chr.get().getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeMesoSet((byte)0, this.meso));
            if (this.partner != null) {
               this.partner.getChr().getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeMesoSet((byte)1, this.meso));
            }
         }
      }
   }

   public final void addItem(Item item) {
      if (!this.locked && this.partner != null) {
         this.items.add(item);
         this.chr.get().getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeItemAdd((byte)0, item));
         if (this.partner != null) {
            this.partner.getChr().getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeItemAdd((byte)1, item));
         }
      }
   }

   public final void chat(String message) {
      this.chr.get().dropMessage(-2, message);
      if (this.partner != null) {
         this.partner.getChr().getClient().getSession().writeAndFlush(PlayerShopPacket.shopChat(this.chr.get().getName(), message, this.chr.get().getId(), 1));
      }

      if (this.chr.get().getClient().isMonitored()) {
         Center.Broadcast.broadcastGMMessage(
            CWvsContext.serverNotice(6, this.chr.get().getName() + " said in trade with " + this.partner.getChr().getName() + ": " + message)
         );
      } else if (this.partner != null && this.partner.getChr() != null && this.partner.getChr().getClient().isMonitored()) {
         Center.Broadcast.broadcastGMMessage(
            CWvsContext.serverNotice(6, this.chr.get().getName() + " said in trade with " + this.partner.getChr().getName() + ": " + message)
         );
      }
   }

   public final void chatAuto(String message) {
      this.chr.get().dropMessage(-2, message);
      if (this.partner != null) {
         this.partner.getChr().getClient().getSession().writeAndFlush(PlayerShopPacket.shopChat(this.chr.get().getName(), message, this.chr.get().getId(), 1));
      }

      if (this.chr.get().getClient().isMonitored()) {
         Center.Broadcast.broadcastGMMessage(
            CWvsContext.serverNotice(6, this.chr.get().getName() + " said in trade [Automated] with " + this.partner.getChr().getName() + ": " + message)
         );
      } else if (this.partner != null && this.partner.getChr() != null && this.partner.getChr().getClient().isMonitored()) {
         Center.Broadcast.broadcastGMMessage(
            CWvsContext.serverNotice(6, this.chr.get().getName() + " said in trade [Automated] with " + this.partner.getChr().getName() + ": " + message)
         );
      }
   }

   public final MapleTrade getPartner() {
      return this.partner;
   }

   public final void setPartner(MapleTrade partner) {
      if (!this.locked) {
         this.partner = partner;
         if (partner != null && partner.getChr() != null) {
            this.partnerChrID = partner.getChr().getId();
            this.partnerChrName = partner.getChr().getName();
            this.partnerAccID = partner.getChr().getClient().getAccID();
            this.partnerAccName = partner.getChr().getClient().getAccountName();
         }
      }
   }

   public final int getPartnerAccID() {
      return this.partnerAccID;
   }

   public final String getPartnerAccName() {
      return this.partnerAccName;
   }

   public final int getPartnerChrID() {
      return this.partnerChrID;
   }

   public final String getPartnerChrName() {
      return this.partnerChrName;
   }

   public final MapleCharacter getChr() {
      return this.chr.get();
   }

   public final int getNextTargetSlot() {
      if (this.items.size() >= 9) {
         return -1;
      } else {
         int ret = 1;

         for (Item item : this.items) {
            if (item.getPosition() == ret) {
               ret++;
            }
         }

         return ret;
      }
   }

   public boolean inTrade() {
      return this.inTrade;
   }

   public final boolean setItems(MapleClient c, Item item, byte targetSlot, int quantity, MapleInventoryType t) {
      int target = this.getNextTargetSlot();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (this.partner != null
         && target != -1
         && !GameConstants.isPet(item.getItemId())
         && !this.isLocked()
         && (t != MapleInventoryType.EQUIP || quantity == 1)) {
         int flag = item.getFlag();
         if (ItemFlag.LOCK.check(flag)) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return false;
         } else if ((
               ItemFlag.POSSIBLE_TRADING.check(flag)
                  || GameConstants.isPet(item.getItemId())
                  || ii.isDropRestricted(item.getItemId())
                  || ii.isAccountShared(item.getItemId())
            )
            && !ItemFlag.KARMA_EQ.check(flag)
            && !ItemFlag.KARMA_USE.check(flag)) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return false;
         } else {
            if (item.getType() == 1) {
               Equip equip = (Equip)item;
               if ((equip.getSpecialAttribute() & EquipSpecialAttribute.VESTIGE.getType()) != 0
                  || (equip.getSpecialAttribute() & EquipSpecialAttribute.EXTENDED.getType()) != 0) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  c.getPlayer().dropMessage(1, "장비의 흔적은 교환하실 수 없습니다.");
                  return false;
               }
            }

            Item tradeItem = item.copy();
            if (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
               tradeItem.setQuantity((short)quantity);
               MapleInventoryManipulator.removeFromSlot(c, t, item.getPosition(), (short)quantity, true);
            } else {
               tradeItem.setQuantity(item.getQuantity());
               MapleInventoryManipulator.removeFromSlot(c, t, item.getPosition(), item.getQuantity(), true);
            }

            if (targetSlot < 0) {
               targetSlot = (byte)target;
            } else {
               for (Item itemz : this.items) {
                  if (itemz.getPosition() == targetSlot) {
                     targetSlot = (byte)target;
                     break;
                  }
               }
            }

            tradeItem.setPosition(targetSlot);
            this.addItem(tradeItem);
            return true;
         }
      } else {
         return false;
      }
   }

   private final int check() {
      if (this.chr.get().getMeso() + this.exchangeMeso < 0L) {
         return 1;
      } else {
         if (this.exchangeItems != null) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            byte eq = 0;
            byte use = 0;
            byte setup = 0;
            byte etc = 0;
            byte cash = 0;

            for (Item item : this.exchangeItems) {
               switch (GameConstants.getInventoryType(item.getItemId())) {
                  case EQUIP:
                     eq++;
                     break;
                  case USE:
                     use++;
                     break;
                  case SETUP:
                     setup++;
                     break;
                  case ETC:
                     etc++;
                     break;
                  case CASH:
                     cash++;
               }

               if (ii.isPickupRestricted(item.getItemId()) && this.chr.get().haveItem(item.getItemId(), 1, true, true)) {
                  return 2;
               }
            }

            if (this.chr.get().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq
               || this.chr.get().getInventory(MapleInventoryType.USE).getNumFreeSlot() < use
               || this.chr.get().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup
               || this.chr.get().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc
               || this.chr.get().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
               return 1;
            }
         }

         return 0;
      }
   }

   public static final void completeTrade(MapleCharacter c) {
      MapleTrade local = c.getTrade();
      MapleTrade partner = local.getPartner();
      if (partner != null && !local.locked) {
         local.locked = true;
         partner.getChr().getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeConfirmation());
         partner.exchangeItems = new LinkedList<>(local.items);
         partner.exchangeMeso = local.meso;
         if (partner.isLocked()) {
            int lz = local.check();
            int lz2 = partner.check();
            if (lz == 0 && lz2 == 0) {
               StringBuilder sb = new StringBuilder();
               sb.append("보낸 캐릭터 : ");
               sb.append(local.getChr().getName());
               sb.append("(").append(local.getChr().getClient().getAccountName()).append(") | ");
               sb.append("받은 캐릭터 : ");
               sb.append(partner.getChr().getName());
               sb.append("(").append(partner.getChr().getClient().getAccountName()).append(") | ");
               sb.append("\r\n아이템(").append(local.getChr().getName()).append(") :");
               if (local.exchangeItems != null) {
                  local.exchangeItems.forEach(item -> {
                     sb.append("[").append(item.getItemId());
                     sb.append(" ").append(item.getQuantity()).append("개");
                     long serialNumber = 0L;
                     if (item instanceof Equip) {
                        serialNumber = ((Equip)item).getSerialNumberEquip();
                     }

                     if (serialNumber > 0L) {
                        sb.append(", SN : ");
                        sb.append(serialNumber);
                     }

                     sb.append("]");
                  });
               }

               sb.append(", 메소 : ");
               sb.append(local.meso);
               sb.append(" | \r\n아이템(").append(partner.getChr().getName()).append(") :");
               if (partner.exchangeItems != null) {
                  partner.exchangeItems.forEach(item -> {
                     sb.append("[").append(item.getItemId());
                     sb.append(" ").append(item.getQuantity()).append("개");
                     long serialNumber = 0L;
                     if (item instanceof Equip) {
                        serialNumber = ((Equip)item).getSerialNumberEquip();
                     }

                     if (serialNumber > 0L) {
                        sb.append(", SN : ");
                        sb.append(serialNumber);
                     }

                     sb.append("]");
                  });
               }

               sb.append(", 메소 : ");
               sb.append(partner.meso);
               sb.append(")");
               LoggingManager.putLog(new TradeLog(c, partner.getChr(), TradeLogType.CompleteTrade.getType(), 0L, sb));
               local.CompleteTrade();
               partner.CompleteTrade();
            } else {
               String reason = "";
               if (lz != 0) {
                  if (lz == 1) {
                     reason = reason + c.getName() + "의 인벤토리 공간 부족, ";
                  } else if (lz == 2) {
                     reason = reason + c.getName() + "이 거래 불가 아이템 거래 시도, ";
                  }
               }

               if (lz2 != 0) {
                  if (lz == 1) {
                     reason = reason + partner.getChr().getName() + "의 인벤토리 공간 부족, ";
                  } else if (lz == 2) {
                     reason = reason + partner.getChr().getName() + "이 거래 불가 아이템 거래 시도, ";
                  }
               }

               StringBuilder sbx = new StringBuilder();
               sbx.append("취소 사유 : (");
               sbx.append(reason).append(") | \r\n");
               sbx.append("보낸 캐릭터 : ");
               sbx.append(local.getChr().getName());
               sbx.append("(").append(local.getChr().getClient().getAccountName()).append(") | ");
               sbx.append("받은 캐릭터 : ");
               sbx.append(partner.getChr().getName());
               sbx.append("(").append(partner.getChr().getClient().getAccountName()).append(") | ");
               sbx.append("\r\n아이템(").append(local.getChr().getName()).append(") :");
               if (local.exchangeItems != null) {
                  local.exchangeItems.forEach(item -> {
                     sbx.append("[").append(item.getItemId());
                     sbx.append(" ").append(item.getQuantity()).append("개");
                     long serialNumber = 0L;
                     if (item instanceof Equip) {
                        serialNumber = ((Equip)item).getSerialNumberEquip();
                     }

                     if (serialNumber > 0L) {
                        sbx.append(", SN : ");
                        sbx.append(serialNumber);
                     }

                     sbx.append("]");
                  });
               }

               sbx.append(", 메소 : ");
               sbx.append(local.meso);
               sbx.append(" | \r\n아이템(").append(partner.getChr().getName()).append(") :");
               if (partner.exchangeItems != null) {
                  partner.exchangeItems.forEach(item -> {
                     sbx.append("[").append(item.getItemId());
                     sbx.append(" ").append(item.getQuantity()).append("개");
                     long serialNumber = 0L;
                     if (item instanceof Equip) {
                        serialNumber = ((Equip)item).getSerialNumberEquip();
                     }

                     if (serialNumber > 0L) {
                        sbx.append(", SN : ");
                        sbx.append(serialNumber);
                     }

                     sbx.append("]");
                  });
               }

               sbx.append(", 메소 : ");
               sbx.append(partner.meso);
               sbx.append(")");
               LoggingManager.putLog(new TradeLog(c, partner.getChr(), TradeLogType.TradeDenied.getType(), 0L, sbx));
               partner.cancel(partner.getChr().getClient(), partner.getChr());
               local.cancel(c.getClient(), c);
            }

            partner.getChr().setTrade(null);
            c.setTrade(null);
         }
      }
   }

   public static final void cancelTrade(MapleTrade Localtrade, MapleClient c, MapleCharacter chr) {
      MapleTrade partner = Localtrade.getPartner();

      try {
         String reason = "캐릭터 이름 '" + chr.getName() + "' 거래 취소";
         StringBuilder sb = new StringBuilder();
         sb.append("취소 사유 : (");
         sb.append(reason).append(") | \r\n");
         sb.append("보낸 캐릭터 : ");
         sb.append(Localtrade.getChr().getName());
         sb.append("(").append(Localtrade.getChr().getClient().getAccountName()).append(") | ");
         sb.append("받은 캐릭터 : ");
         String partnerName = "";
         if (partner == null) {
            partnerName = Localtrade.getPartnerChrName();
         } else if (partner != null && partner.getChr() == null) {
            partnerName = partner.getPartnerChrName();
         } else {
            partnerName = partner.getChr().getName();
         }

         sb.append(partnerName);
         if (partner != null && partner.getChr() != null) {
            sb.append("(").append(partner.getChr().getClient().getAccountName()).append(") | ");
         } else if (partner == null) {
            sb.append("(").append(Localtrade.getPartnerAccName()).append(") | ");
         } else {
            sb.append("(").append(partner.getPartnerAccName()).append(") | ");
         }

         sb.append("\r\n아이템(").append(Localtrade.getChr().getName()).append(") :");
         if (Localtrade.items != null) {
            Localtrade.items.forEach(item -> {
               sb.append("[").append(item.getItemId());
               sb.append(" ").append(item.getQuantity()).append("개");
               long serialNumber = 0L;
               if (item instanceof Equip) {
                  serialNumber = ((Equip)item).getSerialNumberEquip();
               }

               if (serialNumber > 0L) {
                  sb.append(", SN : ");
                  sb.append(serialNumber);
               }

               sb.append("]");
            });
         }

         sb.append(", 메소 : ");
         sb.append(Localtrade.meso);
         if (partner != null && partner.getChr() != null) {
            sb.append(" | \r\n아이템(").append(partner.getChr().getName()).append(") :");
         } else {
            sb.append("(").append(Localtrade.getPartnerChrName()).append(") | ");
         }

         if (partner != null && partner.items != null) {
            partner.items.forEach(item -> {
               sb.append("[").append(item.getItemId());
               sb.append(" ").append(item.getQuantity()).append("개");
               long serialNumber = 0L;
               if (item instanceof Equip) {
                  serialNumber = ((Equip)item).getSerialNumberEquip();
               }

               if (serialNumber > 0L) {
                  sb.append(", SN : ");
                  sb.append(serialNumber);
               }

               sb.append("]");
            });
         } else {
            sb.append("파트너 정보 Null");
         }

         sb.append(", 메소 : ");
         if (partner != null) {
            sb.append(partner.meso);
         } else {
            sb.append("파트너 정보 Null");
         }

         sb.append(")");
         if (partner != null && partner.getChr() != null) {
            LoggingManager.putLog(new TradeLog(chr, partner.getChr(), TradeLogType.TradeDenied.getType(), 0L, sb));
         } else {
            LoggingManager.putLog(
               new TradeLog(
                  chr, Localtrade.getPartnerChrName(), Localtrade.getPartnerChrID(), Localtrade.getPartnerAccID(), TradeLogType.TradeDenied.getType(), 0L, sb
               )
            );
         }
      } catch (Exception var7) {
         System.err.println("로그 저장중 오류 발생");
         var7.printStackTrace();
      }

      Localtrade.cancel(c, chr);
      if (partner != null && partner.getChr() != null) {
         partner.cancel(partner.getChr().getClient(), partner.getChr());
         partner.getChr().setTrade(null);
      }

      chr.setTrade(null);
   }

   public static final void startTrade(MapleCharacter c, boolean isTrade) {
      if (c.getTrade() == null) {
         c.setTrade(new MapleTrade((byte)0, c));
         c.getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeStart(c.getClient(), c.getTrade(), (byte)0, isTrade));
         c.isTrade = isTrade;
      } else {
         c.getClient().getSession().writeAndFlush(CWvsContext.serverNotice(5, "이미 누군가와 트레이드 중입니다."));
      }
   }

   public static final void startCashTrade(MapleCharacter c) {
      if (c.getTrade() == null) {
         c.setTrade(new MapleTrade((byte)0, c));
         c.getClient().getSession().writeAndFlush(CField.InteractionPacket.getCashTradeStart(c.getClient(), c.getTrade(), (byte)0));
      } else {
         c.getClient().getSession().writeAndFlush(CWvsContext.serverNotice(5, "이미 누군가와 트레이드 중입니다."));
      }
   }

   public static final void inviteTrade(MapleCharacter c1, MapleCharacter c2, boolean isTrade) {
      if (c1 != null && c1.getTrade() != null) {
         if (c2 != null && c2.getTrade() == null) {
            c2.setTrade(new MapleTrade((byte)1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeInvite(c1, isTrade));
         } else {
            c1.getClient().getSession().writeAndFlush(CWvsContext.serverNotice(5, "이미 누군가와 트레이드 중입니다."));
            cancelTrade(c1.getTrade(), c1.getClient(), c1);
         }
      }
   }

   public static final void inviteCashTrade(MapleCharacter c1, MapleCharacter c2) {
      if (c1 != null && c1.getTrade() != null) {
         if (c2 != null && c2.getTrade() == null) {
            c2.setTrade(new MapleTrade((byte)1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().getSession().writeAndFlush(CField.InteractionPacket.getCashTradeInvite(c1));
         } else {
            c1.getClient().getSession().writeAndFlush(CWvsContext.serverNotice(5, "이미 누군가와 트레이드 중입니다."));
            cancelTrade(c1.getTrade(), c1.getClient(), c1);
         }
      }
   }

   public static final void visitTrade(MapleCharacter c1, MapleCharacter c2, boolean isTrade) {
      if (c2 != null
         && c1.getTrade() != null
         && c1.getTrade().getPartner() == c2.getTrade()
         && c2.getTrade() != null
         && c2.getTrade().getPartner() == c1.getTrade()) {
         c1.getTrade().inTrade = true;
         c2.getClient().getSession().writeAndFlush(PlayerShopPacket.shopVisitorAdd(c1, 1));
         c1.getClient().getSession().writeAndFlush(CField.InteractionPacket.getTradeStart(c1.getClient(), c1.getTrade(), (byte)1, isTrade));
      } else {
         c1.getClient().getSession().writeAndFlush(CWvsContext.serverNotice(5, "상대방이 이미 트레이드를 종료했습니다."));
      }
   }

   public static final void visitCashTrade(MapleCharacter c1, MapleCharacter c2) {
      if (c2 != null
         && c1.getTrade() != null
         && c1.getTrade().getPartner() == c2.getTrade()
         && c2.getTrade() != null
         && c2.getTrade().getPartner() == c1.getTrade()) {
         c1.getTrade().inTrade = true;
         c2.getClient().getSession().writeAndFlush(PlayerShopPacket.shopVisitorAdd(c1, 1));
         c1.getClient().getSession().writeAndFlush(CField.InteractionPacket.getCashTradeStart(c1.getClient(), c1.getTrade(), (byte)1));
      } else {
         c1.getClient().getSession().writeAndFlush(CWvsContext.serverNotice(5, "상대방이 이미 트레이드를 종료했습니다."));
      }
   }

   public static final void declineTrade(MapleCharacter c) {
      MapleTrade trade = c.getTrade();
      if (trade != null) {
         if (trade.getPartner() != null) {
            MapleCharacter other = trade.getPartner().getChr();
            if (c.getPlayerShop() != null) {
               other.getTrade().cancel(other.getClient(), other);
               other.setTrade(null);
               return;
            }

            if (other != null && other.getTrade() != null) {
               other.getTrade().cancel(other.getClient(), other);
               other.setTrade(null);
               other.dropMessage(5, c.getName() + " 님이 교환 요청을 거절하셨습니다.");
            }
         }

         trade.cancel(c.getClient(), c);
         c.setTrade(null);
      }
   }

   public byte getPRS() {
      return this.rps;
   }

   public void setRPS(byte rps) {
      this.rps = rps;
   }
}
