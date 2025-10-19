package objects.users.skills;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import objects.fields.SecondAtom;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SecondAtomData {
   private List<SecondAtomData.atom> atoms = new ArrayList<>();
   private int firstAngleRange;
   private int firstAngleStart;
   private int expire;
   private int attackableCount;
   private int enableDelay;
   private int createDelay;
   private int rotate;
   private List<SecondAtom.Custom> customs;

   public SecondAtomData(MapleData data) {
      MapleData root = data.getChildByPath("atom");
      if (root != null) {
         for (MapleData d : root) {
            this.atoms.add(new SecondAtomData.atom(d));
         }
      }

      this.setCustoms(new ArrayList<>());
      MapleData custom = data.getChildByPath("custom");
      if (custom != null) {
         for (MapleData d : custom) {
            this.getCustoms().add(new SecondAtom.Custom(Integer.parseInt(d.getName()), MapleDataTool.getInt(d)));
         }
      }

      this.setFirstAngleRange(MapleDataTool.getInt("firstAngleRange", data, 0));
      this.setFirstAngleStart(MapleDataTool.getInt("firstAngleStart", data, 0));
      this.setAttackableCount(MapleDataTool.getInt("attackableCount", data, 0));
      this.setEnableDelay(MapleDataTool.getInt("enableDelay", data, 0));
      this.setCreateDelay(MapleDataTool.getInt("createDelay", data, 0));
      this.setExpire(MapleDataTool.getInt("expire", data, 0));
      this.setRotate(MapleDataTool.getInt("rotate", data, 0));
   }

   public int getFirstAngleRange() {
      return this.firstAngleRange;
   }

   public void setFirstAngleRange(int firstAngleRange) {
      this.firstAngleRange = firstAngleRange;
   }

   public int getFirstAngleStart() {
      return this.firstAngleStart;
   }

   public void setFirstAngleStart(int firstAngleStart) {
      this.firstAngleStart = firstAngleStart;
   }

   public List<SecondAtomData.atom> getAtoms() {
      return this.atoms;
   }

   public int getExpire() {
      return this.expire;
   }

   public void setExpire(int expire) {
      this.expire = expire;
   }

   public int getAttackableCount() {
      return this.attackableCount;
   }

   public void setAttackableCount(int attackableCount) {
      this.attackableCount = attackableCount;
   }

   public int getEnableDelay() {
      return this.enableDelay;
   }

   public void setEnableDelay(int enableDelay) {
      this.enableDelay = enableDelay;
   }

   public List<SecondAtom.Custom> getCustoms() {
      return this.customs;
   }

   public void setCustoms(List<SecondAtom.Custom> customs) {
      this.customs = customs;
   }

   public int getRotate() {
      return this.rotate;
   }

   public void setRotate(int rotate) {
      this.rotate = rotate;
   }

   public int getCreateDelay() {
      return this.createDelay;
   }

   public void setCreateDelay(int createDelay) {
      this.createDelay = createDelay;
   }

   public static class atom {
      private int createDelay;
      private int enableDelay;
      private int rotate;
      private int expire;
      private int attackableCount;
      private int dataIndex;
      private int firstAngleStart;
      private int firstAngleRange;
      private List<SecondAtom.Custom> customs;
      private Point pos;
      private List<SecondAtom.ExtraPos> extraPos;

      public atom(MapleData data) {
         this.createDelay = MapleDataTool.getInt("createDelay", data, 0);
         this.enableDelay = MapleDataTool.getInt("enableDelay", data, 0);
         this.rotate = MapleDataTool.getInt("rotate", data, 0);
         this.expire = MapleDataTool.getInt("expire", data, 0);
         this.attackableCount = MapleDataTool.getInt("attackableCount", data, 1);
         this.setFirstAngleStart(MapleDataTool.getInt("firstAngleStart", data, 0));
         this.setFirstAngleRange(MapleDataTool.getInt("firstAngleRange", data, 0));
         this.customs = new ArrayList<>();
         MapleData custom = data.getChildByPath("custom");
         if (custom != null) {
            for (MapleData d : custom) {
               this.customs.add(new SecondAtom.Custom(Integer.parseInt(d.getName()), MapleDataTool.getInt(d)));
            }
         }

         this.pos = MapleDataTool.getPoint("pos", data, new Point(0, 0));
         this.extraPos = new ArrayList<>();
         MapleData extraPos = data.getChildByPath("extraPos");
         if (extraPos != null) {
            for (MapleData d : extraPos) {
               this.extraPos.add(new SecondAtom.ExtraPos(Integer.parseInt(d.getName()), MapleDataTool.getPoint(d)));
            }
         }

         this.dataIndex = MapleDataTool.getInt("dataIndex", data, 0);
      }

      public int getCreateDelay() {
         return this.createDelay;
      }

      public int getEnableDelay() {
         return this.enableDelay;
      }

      public int getRotate() {
         return this.rotate;
      }

      public int getExpire() {
         return this.expire;
      }

      public int getAttackableCount() {
         return this.attackableCount;
      }

      public int getDataIndex() {
         return this.dataIndex;
      }

      public List<SecondAtom.Custom> getCustoms() {
         return this.customs;
      }

      public Point getPos() {
         return this.pos;
      }

      public List<SecondAtom.ExtraPos> getExtraPos() {
         return this.extraPos;
      }

      public int getFirstAngleStart() {
         return this.firstAngleStart;
      }

      public void setFirstAngleStart(int firstAngleStart) {
         this.firstAngleStart = firstAngleStart;
      }

      public int getFirstAngleRange() {
         return this.firstAngleRange;
      }

      public void setFirstAngleRange(int firstAngleRange) {
         this.firstAngleRange = firstAngleRange;
      }
   }
}
