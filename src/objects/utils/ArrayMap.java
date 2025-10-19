package objects.utils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ArrayMap<K, V> extends AbstractMap<K, V> implements Serializable {
   public static final long serialVersionUID = 9179541993413738569L;
   private transient Set<? extends java.util.Map.Entry<K, V>> entries = null;
   private ArrayList<ArrayMap.Entry<K, V>> list;

   public ArrayMap() {
      this.list = new ArrayList<>();
   }

   public ArrayMap(Map<K, V> map) {
      this.list = new ArrayList<>();
      this.putAll(map);
   }

   public ArrayMap(int initialCapacity) {
      this.list = new ArrayList<>(initialCapacity);
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      if (this.entries == null) {
         this.entries = new AbstractSet<ArrayMap.Entry<K, V>>() {
            @Override
            public void clear() {
               throw new UnsupportedOperationException();
            }

            @Override
            public Iterator<ArrayMap.Entry<K, V>> iterator() {
               return ArrayMap.this.list.iterator();
            }

            @Override
            public int size() {
               return ArrayMap.this.list.size();
            }
         };
      }

      return (Set<java.util.Map.Entry<K, V>>)this.entries;
   }

   @Override
   public V put(K key, V value) {
      int size = this.list.size();
      ArrayMap.Entry<K, V> entry = null;
      int i;
      if (key == null) {
         for (i = 0; i < size; i++) {
            entry = this.list.get(i);
            if (entry.getKey() == null) {
               break;
            }
         }
      } else {
         for (i = 0; i < size; i++) {
            entry = this.list.get(i);
            if (key.equals(entry.getKey())) {
               break;
            }
         }
      }

      V oldValue = null;
      if (i < size) {
         oldValue = entry.getValue();
         entry.setValue(value);
      } else {
         this.list.add(new ArrayMap.Entry<>(key, value));
      }

      return oldValue;
   }

   public static class Entry<K, V> implements java.util.Map.Entry<K, V>, Serializable {
      public static final long serialVersionUID = 9179541993413738569L;
      protected K key;
      protected V value;

      public Entry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V newValue) {
         V oldValue = this.value;
         this.value = newValue;
         return oldValue;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry e = (java.util.Map.Entry)o;
            return (this.key == null ? e.getKey() == null : this.key.equals(e.getKey()))
               && (this.value == null ? e.getValue() == null : this.value.equals(e.getValue()));
         }
      }

      @Override
      public int hashCode() {
         int keyHash = this.key == null ? 0 : this.key.hashCode();
         int valueHash = this.value == null ? 0 : this.value.hashCode();
         return keyHash ^ valueHash;
      }

      @Override
      public String toString() {
         return this.key + "=" + this.value;
      }
   }
}
