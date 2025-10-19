package objects.fields.child.union;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import network.encode.PacketEncoder;
import objects.utils.Pair;

public class MapleUnion {
   public static final int MAX_PRESETS = 5;
   public List<MapleUnionEntry> activeRaiders = new ArrayList<>();
   public List<MapleUnionEntry> raiders = new ArrayList<>();
   public LinkedList<Integer> changeableGroup = new LinkedList<>();
   public int currentPreset;
   public MapleUnionEntry mapleM;
   public String name;
   public int rank = 101;

   public List<Pair<Integer, Integer>> calculateMapleUnionPassive() {
      List<UnionSkillEntry> entryList = new ArrayList<>();
      Map<Point, Integer> groupRecord = new HashMap<>();
      List<Pair<Integer, Integer>> ret = new ArrayList<>();
      List<UnionSkillEntry> remove = new ArrayList<>();

      for (MapleUnionEntry raider : this.activeRaiders.stream().filter(copyList -> true).collect(Collectors.toList())) {
         UnionCardData data = MapleUnionData.getUnionCardData(raider.job);
         if (data != null) {
            UnionSkillEntry entry = new UnionSkillEntry(data.getSkillID(), 1 + MapleUnionData.getRankByJobLevel(raider.job, raider.level));
            if (entry.getSkillLevel() > 0) {
               new AtomicBoolean(false);
               entryList.stream().filter(r -> r.getSkillID() == data.getSkillID()).forEach(e -> {
                  if (e != null && e.getSkillLevel() <= entry.getSkillLevel()) {
                     remove.add(e);
                  }
               });
               entryList.add(entry);
            }

            UnionBoardInfo info = MapleUnionData.getUnionBoardInfo(raider.board);
            if (info != null) {
               Point pos = new Point(info.getxPos(), info.getyPos());

               for (Point piece : MapleUnionData.getPointsByJobLevel(raider.job, raider.level)) {
                  int px = piece.x;
                  int py = piece.y;
                  int num = raider.angle / 1000;
                  int angle = raider.angle % 1000 / 90;
                  int dy = 1;
                  int dx = 1;
                  if ((num & 2) != 0) {
                     dy = -dy;
                  }

                  if ((num & 1) != 0) {
                     dx = -dx;
                  }

                  int num1 = angle % 4;
                  if (num1 == 1 || num1 == 3) {
                     int temp = px;
                     px = py;
                     py = temp;
                  }

                  num1 = angle % 4;
                  switch (num1) {
                     case 0:
                        dy = -dy;
                        break;
                     case 1:
                        dx = -dx;
                        dy = -dy;
                        break;
                     case 2:
                        dx = -dx;
                  }

                  px *= dx;
                  py *= dy;
                  Point piecePos = new Point(pos.x + px, pos.y - py);
                  UnionBoardInfo bi = MapleUnionData.getUnionBoardInfoByPos(piecePos);
                  if (bi != null && bi.getOpenLevel() <= this.rank) {
                     int realGroupID = bi.getGroupIndex();
                     if (bi.isChangeable() && bi.getGroupIndex() < this.changeableGroup.size()) {
                        realGroupID = this.changeableGroup.get(bi.getGroupIndex());
                     }

                     UnionSkillInfo groupSkill = MapleUnionData.getGroupSkillInfo(realGroupID);
                     if (groupSkill != null) {
                        groupRecord.put(piecePos, groupSkill.getSkillID());
                     }
                  }
               }
            }
         }
      }

      for (UnionSkillEntry r : remove) {
         entryList.remove(r);
      }

      for (UnionSkillEntry entryx : entryList) {
         ret.add(new Pair<>(entryx.getSkillID(), entryx.getSkillLevel()));
      }

      groupRecord.forEach((point, skillID) -> {
         boolean find = false;

         for (Pair<Integer, Integer> pair : ret) {
            if (pair.left.equals(skillID)) {
               find = true;
            }
         }

         if (!find) {
            long skillLevel = groupRecord.values().stream().filter(p -> p.equals(skillID)).count();
            ret.add(new Pair<>(skillID, (int)skillLevel));
         }
      });
      return ret;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.rank);
      packet.writeInt(this.currentPreset);
      this.encodeGroup(packet);
      this.encodeContext(packet);
   }

   public void encodeGroup(PacketEncoder packet) {
      for (int i = 0; i < MapleUnionData.changeableGroup.size(); i++) {
         packet.writeInt(this.changeableGroup.get(i));
      }
   }

   public void encodeUI(PacketEncoder packet) {
      packet.writeInt(this.rank);
      this.encodeContext(packet);
   }

   public void encodeContext(PacketEncoder packet) {
      packet.writeInt(this.raiders.size());

      for (MapleUnionEntry raider : this.raiders) {
         raider.encode(packet);
      }

      packet.writeInt(this.activeRaiders.size());

      for (MapleUnionEntry activeRaider : this.activeRaiders) {
         activeRaider.encode(packet);
      }

      packet.write(this.mapleM != null);
      if (this.mapleM != null) {
         this.mapleM.encode(packet);
      }
   }

   public long getAttackPower() {
      long ret = 0L;

      for (MapleUnionEntry raider : this.activeRaiders) {
         ret += MapleUnionConstants.getBattleScore(raider);
      }

      return ret;
   }
}
