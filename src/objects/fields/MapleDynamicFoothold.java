package objects.fields;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.users.MapleClient;

public class MapleDynamicFoothold extends MapleMapObject {
   List<MapleDynamicFoothold.DynamicFoothold> footholds = new ArrayList<>();
   private long endTime = 0L;

   public MapleDynamicFoothold(List<MapleDynamicFoothold.DynamicFoothold> footholds, long endTime) {
      this.footholds.addAll(new ArrayList<>(footholds));
      this.endTime = endTime;
   }

   public MapleDynamicFoothold(long endTime) {
      this.endTime = endTime;
   }

   public MapleDynamicFoothold.DynamicFoothold getDynamicFoothold(String name) {
      return this.footholds.stream().filter(fh -> fh.getFootholdName().equals(name)).findFirst().orElse(null);
   }

   public List<MapleDynamicFoothold.DynamicFoothold> getDynamicFootholds() {
      return this.footholds;
   }

   public void clear() {
      this.getDynamicFootholds().forEach(f -> f.setCurState(0));
   }

   public void putDynamicFoothold(String name, int curState, Point position) {
      MapleDynamicFoothold.DynamicFoothold foothold = new MapleDynamicFoothold.DynamicFoothold(name + this.getDynamicFootholdSize(), curState, position);
      this.footholds.add(foothold);
   }

   public void putDynamicFootholdRealName(String name, int curState, Point point) {
      MapleDynamicFoothold.DynamicFoothold foothold = new MapleDynamicFoothold.DynamicFoothold(name, curState, point);
      this.footholds.add(foothold);
   }

   public void putDynamicFootholdRealName(String name, int id, int curState, Point point) {
      MapleDynamicFoothold.DynamicFoothold foothold = new MapleDynamicFoothold.DynamicFoothold(name, id, curState, point);
      this.footholds.add(foothold);
   }

   public int getDynamicFootholdSize() {
      return this.footholds.size();
   }

   public long getEndTime() {
      return this.endTime;
   }

   public void setEndTime(long endTime) {
      this.endTime = endTime;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.DYNAMIC_FOOTHOLD;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.syncDynamicFoothold(this));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      this.clear();
      client.getSession().writeAndFlush(CField.syncDynamicFoothold(this));
   }

   public class DynamicFoothold {
      private String footholdName;
      private int id;
      private int curState;
      private Point position;

      public DynamicFoothold(String footholdName, int curState, Point position) {
         this.footholdName = footholdName;
         this.curState = curState;
         this.position = position;
      }

      public DynamicFoothold(String footholdName, int id, int curState, Point position) {
         this.footholdName = footholdName;
         this.id = id;
         this.curState = curState;
         this.position = position;
      }

      public void encode(PacketEncoder packet) {
         packet.writeMapleAsciiString(this.getFootholdName());
         packet.write(0);
         packet.writeInt(this.getCurState());
         packet.writeInt(this.getPosition().x);
         packet.writeInt(this.getPosition().y);
      }

      public String getFootholdName() {
         return this.footholdName;
      }

      public void setFootholdName(String footholdName) {
         this.footholdName = footholdName;
      }

      public int getCurState() {
         return this.curState;
      }

      public void setCurState(int curState) {
         this.curState = curState;
      }

      public Point getPosition() {
         return this.position;
      }

      public void setPosition(Point position) {
         this.position = position;
      }

      public int getId() {
         return this.id;
      }

      public void setId(int id) {
         this.id = id;
      }
   }
}
