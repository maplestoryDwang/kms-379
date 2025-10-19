package network.game.processors.achievement;

import database.loader.CharacterSaveFlag2;
import java.util.List;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import network.models.PacketHelper;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.AchievementInsigniaEntry;
import objects.users.achievement.caching.AchievementInsigniaInfo;

public class AchievementHandler {
   public static void AchienvementAction(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         int action = slea.readInt();
         switch (action) {
            case 3:
               int insignia = slea.readInt() - 1;
               AchievementInsigniaInfo i = AchievementFactory.insigniaInfos.get(insignia);
               if (i == null) {
                  user.send(CWvsContext.enableActions(user));
                  return;
               }

               AchievementInsigniaEntry aie = user.getAchievement().getAchievementInsignias().get(insignia);
               if (aie == null) {
                  user.send(CWvsContext.enableActions(user));
                  return;
               }

               for (AchievementInsigniaEntry info : user.getAchievement().getAchievementInsignias()) {
                  if (info == aie) {
                     info.setStatus(2);
                  } else {
                     info.setStatus(1);
                  }
               }

               if (user.getSkillLevel(80002323) <= 0) {
                  user.changeSkillLevel(80002323, 1, 1);
                  user.setChangedSkills();
               }

               user.send(updateInsignia(user.getAchievement().getAchievementInsignias()));
               user.setSaveFlag2(user.getSaveFlag2() | CharacterSaveFlag2.ACHIEVEMENT.getFlag());
               user.send(CWvsContext.enableActions(user));
            case 7:
               user.send(CWvsContext.enableActions(user));
         }
      }
   }

   public static byte[] updateInsignia(List<AchievementInsigniaEntry> insignia) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
      packet.write(43);
      packet.writeInt(insignia.size());

      for (int i = 1; i <= insignia.size(); i++) {
         packet.writeInt(i);
         packet.writeInt(i);
         packet.write(insignia.get(i - 1).getStatus());
         packet.writeLong(PacketHelper.getKoreanTimestamp(insignia.get(i - 1).getAchieveTime()));
      }

      return packet.getPacket();
   }
}
