package objects.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ConcurrentEnumMap<K extends Enum<K>, V> extends EnumMap<K, V> implements Serializable {
   private static final long serialVersionUID = 11920818021L;
   private ReentrantReadWriteLock reentlock = new ReentrantReadWriteLock();
   private Lock rL = this.reentlock.readLock();
   private Lock wL = this.reentlock.writeLock();

   public ConcurrentEnumMap(Class<K> keyType) {
      super(keyType);
   }

   @Override
   public void clear() {
      this.wL.lock();

      try {
         super.clear();
      } finally {
         this.wL.unlock();
      }
   }

   @Override
   public EnumMap<K, V> clone() {
      return super.clone();
   }

   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }

   @Override
   public boolean containsKey(Object key) {
      this.rL.lock();

      boolean var2;
      try {
         var2 = super.containsKey(key);
      } finally {
         this.rL.unlock();
      }

      return var2;
   }

   @Override
   public boolean containsValue(Object value) {
      this.rL.lock();

      boolean var2;
      try {
         var2 = super.containsValue(value);
      } finally {
         this.rL.unlock();
      }

      return var2;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      this.rL.lock();

      Set var1;
      try {
         var1 = super.entrySet();
      } finally {
         this.rL.unlock();
      }

      return var1;
   }

   @Override
   public V get(Object key) {
      this.rL.lock();

      Object var2;
      try {
         var2 = super.get(key);
      } finally {
         this.rL.unlock();
      }

      return (V)var2;
   }

   @Override
   public Set<K> keySet() {
      this.rL.lock();

      Set var1;
      try {
         var1 = super.keySet();
      } finally {
         this.rL.unlock();
      }

      return var1;
   }

   @Override
   public V put(K key, V value) {
      this.wL.lock();

      Object var3;
      try {
         var3 = super.put(key, value);
      } finally {
         this.wL.unlock();
      }

      return (V)var3;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      this.wL.lock();

      try {
         super.putAll(m);
      } finally {
         this.wL.unlock();
      }
   }

   @Override
   public V remove(Object key) {
      this.wL.lock();

      Object var2;
      try {
         var2 = super.remove(key);
      } finally {
         this.wL.unlock();
      }

      return (V)var2;
   }

   @Override
   public int size() {
      this.rL.lock();

      int var1;
      try {
         var1 = super.size();
      } finally {
         this.rL.unlock();
      }

      return var1;
   }

   @Override
   public Collection<V> values() {
      this.rL.lock();

      Collection var1;
      try {
         var1 = super.values();
      } finally {
         this.rL.unlock();
      }

      return var1;
   }
}
