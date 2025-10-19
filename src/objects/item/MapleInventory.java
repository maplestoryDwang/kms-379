package objects.item;

import constants.GameConstants;
import database.DBConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemStateFlag;
import objects.utils.FileoutputUtil;

public class MapleInventory implements Iterable<Item>, Serializable {
   private Map<Short, Item> inventory;
   private short slotLimit = 0;
   private MapleInventoryType type;

   public MapleInventory(MapleInventoryType type) {
      this.inventory = new LinkedHashMap<>();
      this.type = type;
   }

   public void addSlot(short slot) {
      this.slotLimit += slot;
      if (this.slotLimit > 128) {
         this.slotLimit = 128;
      }
   }

   public short getSlotLimit() {
      return this.slotLimit;
   }

   public void setSlotLimit(short slot) {
      if (slot > 128) {
         slot = 128;
      }

      if (DBConfig.isGanglim) {
         slot = 128;
      } else if (slot < 96) {
         slot = 96;
      }

      this.slotLimit = slot;
   }

   public Item findById(int itemId) {
      for (Item item : this.inventory.values()) {
         if (item.getItemId() == itemId) {
            return item;
         }
      }

      return null;
   }

   public Item findByIdWithoutLock(int itemId) {
      List<Item> list = new ArrayList<>(this.inventory.values());

      for (Item item : list.stream().sorted((a, b) -> a.getPosition() - b.getPosition()).collect(Collectors.toList())) {
         if (!ItemFlag.LOCK.check(item.getFlag()) && item.getItemId() == itemId) {
            return item;
         }
      }

      return null;
   }

   public Item findByUniqueId(long uniqueID) {
      for (Item item : this.inventory.values()) {
         if (item.getUniqueId() == uniqueID) {
            return item;
         }
      }

      return null;
   }

   public Item findByInventoryId(long itemId, int itemI) {
      for (Item item : this.inventory.values()) {
         if (item.getInventoryId() == itemId && item.getItemId() == itemI) {
            return item;
         }
      }

      return this.findById(itemI);
   }

   public Item findByInventoryIdOnly(long itemId, int itemI) {
      for (Item item : this.inventory.values()) {
         if (item.getInventoryId() == itemId && item.getItemId() == itemI) {
            return item;
         }
      }

      return null;
   }

   public int countByIdWithoutLock(int itemID) {
      int possesed = 0;

      for (Item item : this.inventory.values()) {
         if (item.getItemId() == itemID) {
            if (item.getType() == 1) {
               Equip equip = (Equip)item;
               int flag = equip.getItemState();
               if ((flag & ItemStateFlag.LOCK.getValue()) == 0) {
                  possesed += equip.getQuantity();
               }
            } else if (!ItemFlag.LOCK.check(item.getFlag())) {
               possesed += item.getQuantity();
            }
         }
      }

      return possesed;
   }

   public int countById(int itemId) {
      int possesed = 0;

      for (Item item : this.inventory.values()) {
         if (item.getItemId() == itemId) {
            possesed += item.getQuantity();
         }
      }

      return possesed;
   }

   public List<Item> listById(int itemId) {
      List<Item> ret = new ArrayList<>();

      for (Item item : this.inventory.values()) {
         if (item.getItemId() == itemId) {
            ret.add(item);
         }
      }

      if (ret.size() > 1) {
         Collections.sort(ret);
      }

      return ret;
   }

   public Collection<Item> list() {
      return this.inventory.values();
   }

   public List<Item> newList() {
      synchronized (this.inventory) {
         return (List<Item>)(this.inventory.size() <= 0 ? Collections.emptyList() : new LinkedList<>(this.inventory.values()));
      }
   }

   public List<Integer> listIds() {
      List<Integer> ret = new ArrayList<>();

      for (Item item : this.inventory.values()) {
         if (!ret.contains(item.getItemId())) {
            ret.add(item.getItemId());
         }
      }

      if (ret.size() > 1) {
         Collections.sort(ret);
      }

      return ret;
   }

   public short addItem(Item item) {
      short slotId = this.getNextFreeSlot();
      if (slotId < 0) {
         return -1;
      } else {
         this.inventory.put(slotId, item);
         item.setPosition(slotId);
         return slotId;
      }
   }

   public void addFromDB(Item item) {
      if (item.getPosition() >= 0 || this.type.equals(MapleInventoryType.EQUIPPED)) {
         if (item.getPosition() <= 0 || !this.type.equals(MapleInventoryType.EQUIPPED)) {
            this.inventory.put(item.getPosition(), item);
         }
      }
   }

   public void move(short sSlot, short dSlot, short slotMax) {
      Item source = this.inventory.get(sSlot);
      Item target = this.inventory.get(dSlot);
      if (source == null) {
         throw new InventoryException("Trying to move empty slot");
      } else {
         if (target == null) {
            if (dSlot < 0 && !this.type.equals(MapleInventoryType.EQUIPPED)) {
               return;
            }

            if (dSlot > 0 && this.type.equals(MapleInventoryType.EQUIPPED)) {
               return;
            }

            source.setPosition(dSlot);
            this.inventory.put(dSlot, source);
            this.inventory.remove(sSlot);
         } else if (target.getItemId() != source.getItemId()
            || target.getUniqueId() != source.getUniqueId()
            || GameConstants.isThrowingStar(source.getItemId())
            || GameConstants.isBullet(source.getItemId())
            || !target.getOwner().equals(source.getOwner())
            || target.getExpiration() != source.getExpiration()) {
            this.swap(target, source);
         } else if (this.type.getType() == MapleInventoryType.EQUIP.getType() || this.type.getType() == MapleInventoryType.CASH.getType()) {
            this.swap(target, source);
         } else if (source.getQuantity() + target.getQuantity() > slotMax) {
            source.setQuantity((short)(source.getQuantity() + target.getQuantity() - slotMax));
            target.setQuantity(slotMax);
         } else {
            target.setQuantity((short)(source.getQuantity() + target.getQuantity()));
            this.inventory.remove(sSlot);
         }
      }
   }

   private void swap(Item source, Item target) {
      this.inventory.remove(source.getPosition());
      this.inventory.remove(target.getPosition());
      short swapPos = source.getPosition();
      source.setPosition(target.getPosition());
      target.setPosition(swapPos);
      this.inventory.put(source.getPosition(), source);
      this.inventory.put(target.getPosition(), target);
   }

   public Item getItem(short slot) {
      return this.inventory.get(slot);
   }

   public void removeItem(short slot) {
      this.removeItem(slot, (short)1, false);
   }

   public void removeItem(short slot, short quantity, boolean allowZero) {
      Item item = this.inventory.get(slot);
      if (item != null) {
         int beforeQty = item.getQuantity();
         item.setQuantity((short)(item.getQuantity() - quantity));
         int afterQty = item.getQuantity();
         if (GameConstants.isNeedToWriteLogItem(item.getItemId()) && afterQty > beforeQty) {
            try {
               throw new Exception();
            } catch (Exception var8) {
               FileoutputUtil.log(
                  "./TextLog/GiveItemLog.txt",
                  "removeItem 중 아이템 추가지급 발생 : (itemId : "
                     + item.getItemId()
                     + ", quantity : "
                     + quantity
                     + "\r\nStackTrace : "
                     + FileoutputUtil.getString(var8)
                     + ")\r\n"
               );
            }
         }

         if (item.getQuantity() < 0) {
            item.setQuantity((short)0);
         }

         if (item.getQuantity() == 0 && !allowZero) {
            this.removeSlot(slot);
         }
      }
   }

   public void removeSlot(short slot) {
      this.inventory.remove(slot);
   }

   public int getInventorySize() {
      long count = this.newList().stream().filter(item -> item.getPosition() > 128).count();
      return (int)(this.inventory.size() - count);
   }

   public boolean isFull() {
      return this.getInventorySize() >= this.slotLimit;
   }

   public boolean isFull(int margin) {
      return this.getInventorySize() + margin >= this.slotLimit;
   }

   public short getNextFreeSlot() {
      if (this.isFull()) {
         return -1;
      } else {
         for (short i = 1; i <= this.slotLimit; i++) {
            if (!this.inventory.containsKey(i)) {
               return i;
            }
         }

         return -1;
      }
   }

   public short getNumFreeSlot() {
      if (this.isFull()) {
         return 0;
      } else {
         short free = 0;

         for (short i = 1; i <= this.slotLimit; i++) {
            if (!this.inventory.containsKey(i)) {
               free++;
            }
         }

         return free;
      }
   }

   public MapleInventoryType getType() {
      return this.type;
   }

   public short getNextItemSlot(short k) {
      if (this.isFull()) {
         return -1;
      } else {
         for (short i = k; i <= this.slotLimit; i++) {
            if (!this.inventory.containsKey(i)) {
               return i;
            }
         }

         return -1;
      }
   }

   @Override
   public Iterator<Item> iterator() {
      return Collections.unmodifiableCollection(this.inventory.values()).iterator();
   }
}
