package objects.fields.child.minigame.yutgame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;

public class YutGameResult {
   private List<YutGameResultEntry> resultList = new ArrayList<>();

   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.YUT_GAME_RESULT.getValue());
      this.getResultList().forEach(result -> result.encode(packet));
      packet.write(0);
   }

   public List<YutGameResultEntry> getResultList() {
      return this.resultList;
   }

   public void addResult(YutGameResultEntry entry) {
      this.resultList.add(entry);
   }

   public void setResultList(List<YutGameResultEntry> resultList) {
      this.resultList = resultList;
   }

   public void clearResult() {
      this.resultList.clear();
   }

   public void sortingLog() {
      this.resultList = this.resultList.stream().sorted(Comparator.comparingInt(a -> a.getType().getType())).collect(Collectors.toList());
   }
}
