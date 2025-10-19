package objects.utils;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

public class Table extends LinkedHashMap<String, String> {
   private final String name;
   private Table parent;
   private Map<String, Table> children;

   public Table(String name) {
      this.name = name;
      this.children = new LinkedHashMap<>();
   }

   public Table(String name, Table parent) {
      this(name);
      this.parent = parent;
   }

   public int getID() {
      try {
         return Integer.parseInt(this.name);
      } catch (NumberFormatException var2) {
         return 0;
      }
   }

   public String getName() {
      return this.name;
   }

   public String getProperty(String key) {
      return this.get(key);
   }

   public String getProperty(String key, String defaultValue) {
      String val = this.getProperty(key);
      return val == null ? defaultValue : val;
   }

   public boolean getProperty(String key, boolean defaultValue) {
      String val = this.getProperty(key);

      try {
         int n = Integer.parseInt(val);
         return n != 0;
      } catch (NumberFormatException var5) {
         return val == null ? defaultValue : val.equalsIgnoreCase("true");
      }
   }

   public byte getProperty(String key, byte defaultValue) {
      try {
         return Byte.parseByte(this.getProperty(key, Byte.toString(defaultValue)));
      } catch (NumberFormatException var4) {
         return defaultValue;
      }
   }

   public short getProperty(String key, short defaultValue) {
      try {
         return Short.parseShort(this.getProperty(key, Short.toString(defaultValue)));
      } catch (NumberFormatException var4) {
         return defaultValue;
      }
   }

   public int getProperty(String key, int defaultValue) {
      try {
         return Integer.parseInt(this.getProperty(key, Integer.toString(defaultValue)));
      } catch (NumberFormatException var4) {
         return defaultValue;
      }
   }

   public long getProperty(String key, long defaultValue) {
      try {
         return Long.parseLong(this.getProperty(key, Long.toString(defaultValue)));
      } catch (NumberFormatException var5) {
         return defaultValue;
      }
   }

   public float getProperty(String key, float defaultValue) {
      try {
         return Float.parseFloat(this.getProperty(key, Float.toString(defaultValue)));
      } catch (NumberFormatException var4) {
         return defaultValue;
      }
   }

   public double getProperty(String key, double defaultValue) {
      try {
         return Double.parseDouble(this.getProperty(key, Double.toString(defaultValue)));
      } catch (NumberFormatException var5) {
         return defaultValue;
      }
   }

   public Point getVector(String key, Point defaultValue) {
      try {
         String[] xy = this.getProperty(key).split(",");
         return new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
      } catch (RuntimeException var4) {
         return defaultValue;
      }
   }

   public Collection<String> values(String defaultValue) {
      Collection<String> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Collection<Boolean> values(boolean defaultValue) {
      Collection<Boolean> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Collection<Byte> values(byte defaultValue) {
      Collection<Byte> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Collection<Short> values(short defaultValue) {
      Collection<Short> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Collection<Integer> values(int defaultValue) {
      Collection<Integer> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Collection<Long> values(long defaultValue) {
      Collection<Long> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Collection<Float> values(float defaultValue) {
      Collection<Float> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Collection<Double> values(double defaultValue) {
      Collection<Double> result = new LinkedList<>();

      for (String key : this.keySet()) {
         result.add(this.getProperty(key, defaultValue));
      }

      return result;
   }

   public Table getParent() {
      return this.parent;
   }

   public Table computeIfAbsentChild(String key, Function<? super String, ? extends Table> d) {
      return this.children.computeIfAbsent(key, d);
   }

   public Table putChild(Table table) {
      return this.children.put(table.getName(), table);
   }

   public Table getChild(String name) {
      return this.children.get(name);
   }

   public Table removeChild(String name) {
      return this.children.remove(name);
   }

   public Table removeChildByValue(Table table) {
      for (Entry<String, Table> e : this.children.entrySet()) {
         if (e.getValue() == table) {
            this.children.remove(e.getKey());
            return e.getValue();
         }
      }

      return null;
   }

   public Collection<Table> list() {
      return this.children.values();
   }

   public void forEach(Consumer<? super Table> action) {
      this.list().forEach(action);
   }

   public int count() {
      return this.children.size();
   }

   public void save(Path path) throws IOException {
      this.save(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   }

   public void save(Path path, OpenOption... oo) throws IOException {
      try (BufferedWriter bw = Files.newBufferedWriter(path, Charset.forName("UTF-8"), oo)) {
         this.write0(bw, this, "");
      }
   }

   private void write0(BufferedWriter bw, Table table, String prefix) throws IOException {
      if (!table.getName().isEmpty()) {
         bw.write(prefix + table.getName() + " = {\r\n");
      }

      for (Entry<String, String> e : table.entrySet()) {
         bw.write(prefix + "\t" + e.getKey() + " = " + e.getValue() + "\r\n");
      }

      for (Table t : table.list()) {
         this.write0(bw, t, prefix + "\t");
      }

      if (!table.getName().isEmpty()) {
         bw.write(prefix + "}\r\n");
      }
   }

   public void clearChilds() {
      this.children.clear();
   }
}
