package objects.context.party;

import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.PacketHelper;
import objects.context.party.boss.BossPartyRecruiment;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.ServerProperties;

public class Party implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private PartyMember partyMember;
   private int id;
   private int expeditionLink = -1;
   private boolean privateParty;
   private boolean onlyLeaderPickUp = false;
   private boolean disbanded = false;
   private String partyTitle;
   private BossPartyRecruiment bossPartyRecruiment;

   public Party(int id, PartyMemberEntry leader) {
      this.setPartyMember(new PartyMember(leader));
      this.id = id;
      this.setBossPartyRecruiment(new BossPartyRecruiment());
   }

   public Party(int id, PartyMemberEntry leader, int expeditionLink) {
      this.setPartyMember(new PartyMember(leader));
      this.id = id;
      this.expeditionLink = expeditionLink;
      this.setBossPartyRecruiment(new BossPartyRecruiment());
   }

   public boolean isPrivateParty() {
      return this.privateParty;
   }

   public void setPrivateParty(boolean set) {
      this.privateParty = set;
   }

   public String getPatryTitle() {
      return this.partyTitle;
   }

   public void setPartyTitle(String title) {
      this.partyTitle = title;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getExpeditionId() {
      return this.expeditionLink;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + this.id;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Party other = (Party)obj;
         return this.id == other.id;
      }
   }

   public boolean isDisbanded() {
      return this.disbanded;
   }

   public void disband() {
      this.disbanded = true;
   }

   public boolean isPartySameMap() {
      boolean sameMap = true;
      PartyMemberEntry leaderEntry = this.getPartyMember().getLeader();
      if (leaderEntry == null) {
         return false;
      } else {
         MapleCharacter leader = GameServer.getInstance(leaderEntry.getChannel()).getPlayerStorage().getCharacterById(leaderEntry.getId());
         if (leader == null) {
            for (int ch = 1; ch <= Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.channel.count")); ch++) {
               MapleCharacter le = null;
               if (GameServer.getInstance(ch) != null && GameServer.getInstance(ch).getPlayerStorage().getCharacterById(leaderEntry.getId()) != null) {
                  le = GameServer.getInstance(ch).getPlayerStorage().getCharacterById(leaderEntry.getId());
               }

               if (le != null) {
                  leader = le;
                  leaderEntry.setChannel(ch);
                  break;
               }
            }
         }

         Field field = leader.getMap();

         for (PartyMemberEntry party : this.partyMember.getPartyMemberList()) {
            MapleCharacter chr = field.getCharacterById(party.getId());
            if (chr == null) {
               sameMap = false;
               break;
            }

            if (field.getId() != chr.getMapId()) {
               sameMap = false;
               break;
            }
         }

         return sameMap;
      }
   }

   public boolean isOnlyLeaderPickUp() {
      return this.onlyLeaderPickUp;
   }

   public void setOnlyLeaderPickUp(boolean onlyLeaderPickUp) {
      this.onlyLeaderPickUp = onlyLeaderPickUp;
   }

   public void registerTransferField(int mapId) {
      Field field = GameServer.getInstance(this.getPartyMember().getLeader().getChannel()).getMapFactory().getMap(mapId);

      for (PartyMemberEntry party : this.getPartyMember().getPartyMemberList()) {
         Field m = GameServer.getInstance(party.getChannel()).getMapFactory().getMap(party.getFieldID());
         MapleCharacter chr = m.getCharacterById(party.getId());
         chr.changeMap(field);
      }
   }

   public PartyMember getPartyMember() {
      return this.partyMember;
   }

   public void setPartyMember(PartyMember partyMember) {
      this.partyMember = partyMember;
   }

   public List<PartyMemberEntry> getPartyMemberList() {
      return this.getPartyMember().getPartyMemberList();
   }

   public List<PartyMemberEntry> getMembers() {
      return this.getPartyMemberList();
   }

   public PartyMemberEntry getLeader() {
      return this.getPartyMember().getLeader();
   }

   public PartyMemberEntry getMemberById(int id) {
      for (PartyMemberEntry chr : this.getPartyMemberList()) {
         if (chr.getId() == id) {
            return chr;
         }
      }

      return null;
   }

   public PartyMemberEntry getMemberByIndex(int index) {
      return this.getPartyMember().getMemberByIndex(index);
   }

   public void encode(PacketEncoder packet, int channel, boolean withdraw) {
      packet.writeInt(this.getId());
      packet.write(0);
      this.partyMember.encode(packet);

      for (int i = 0; i < PartyMember.MAX_USER; i++) {
         PartyMemberEntry entry = this.getMemberByIndex(i);
         if (entry == null) {
            packet.writeInt(0);
         } else {
            packet.writeInt(channel == entry.getChannel() ? entry.getFieldID() : 999999999);
         }
      }

      for (int ix = 0; ix < PartyMember.MAX_USER; ix++) {
         PartyMemberEntry entry = this.getMemberByIndex(ix);
         this.encodeTownPortal(entry, channel, withdraw, packet);
      }

      packet.write(0);
      packet.writeMapleAsciiString(this.getPatryTitle());
      packet.write(this.isPrivateParty());
      packet.write(this.isOnlyLeaderPickUp());
      this.getBossPartyRecruiment().encode(packet);
   }

   public void encodeTownPortal(PartyMemberEntry character, int channel, boolean withdraw, PacketEncoder packet) {
      if (character != null && character.getChannel() == channel && !withdraw) {
         packet.writeInt(character.getDoorTown());
         packet.writeInt(character.getDoorTarget());
         packet.writeInt(character.getDoorSkill());
         packet.writeInt(character.getDoorPosition().x);
         packet.writeInt(character.getDoorPosition().y);
      } else {
         packet.writeInt(withdraw ? 999999999 : 0);
         packet.writeInt(withdraw ? 999999999 : 0);
         packet.writeInt(0);
         packet.writeInt(withdraw ? -1 : 0);
         packet.writeInt(withdraw ? -1 : 0);
      }
   }

   public BossPartyRecruiment getBossPartyRecruiment() {
      return this.bossPartyRecruiment;
   }

   public void setBossPartyRecruiment(BossPartyRecruiment bossPartyRecruiment) {
      this.bossPartyRecruiment = bossPartyRecruiment;
   }

   public static class BossPartyRecruimentMessage {
      private BossPartyRecruimentMessageType type;

      public BossPartyRecruimentMessage(BossPartyRecruimentMessageType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
         packet.write(PartyType.BossPartyRecruimentMessage.getType());
         packet.writeInt(this.type.getType());
      }

      public static class CompleteJoinRequest extends Party.BossPartyRecruimentMessage {
         String leaderName;
         int partyID;
         Party party;

         public CompleteJoinRequest(String leaderName, int partyID, Party party) {
            super(BossPartyRecruimentMessageType.CompleteJoinRequest);
            this.leaderName = leaderName;
            this.partyID = partyID;
            this.party = party;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeMapleAsciiString(this.leaderName);
            packet.writeInt(this.partyID);
            packet.write(0);
            this.party.getPartyMember().encode(packet);
            this.party.getBossPartyRecruiment().encode(packet);
         }
      }

      public static class DeclineJoinRequest extends Party.BossPartyRecruimentMessage {
         String leaderName;
         int partyID;

         public DeclineJoinRequest(String leaderName, int partyID) {
            super(BossPartyRecruimentMessageType.DeclineJoinRequest);
            this.leaderName = leaderName;
            this.partyID = partyID;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeMapleAsciiString(this.leaderName);
            packet.writeInt(this.partyID);
         }
      }
   }

   public static class BossPartyRecruimentPacket {
      private BossPartyRecruimentType type;

      public BossPartyRecruimentPacket(BossPartyRecruimentType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
         packet.write(PartyType.BossPartyRecruimentResult.getType());
         packet.writeInt(this.type.getType());
      }

      public static class CancelJoinMember extends Party.BossPartyRecruimentPacket {
         private int partyID;
         private String leaderName;

         public CancelJoinMember(int partyID, String name) {
            super(BossPartyRecruimentType.CancelRequestJoin);
            this.partyID = partyID;
            this.leaderName = name;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.partyID);
            packet.write(1);
            packet.writeMapleAsciiString(this.leaderName);
         }
      }

      public static class DisplayList extends Party.BossPartyRecruimentPacket {
         int bossType;
         byte bossDifficulty;
         List<Party> partyList;

         public DisplayList(int bossType, byte bossDifficulty, List<Party> partyList) {
            super(BossPartyRecruimentType.DisplayBossPartyRecruimentList);
            this.partyList = partyList;
            this.bossType = bossType;
            this.bossDifficulty = bossDifficulty;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.partyList.size());
            this.partyList.forEach(p -> {
               packet.writeInt(p.getId());
               packet.write(0);
               p.getPartyMember().encode(packet);
               p.getBossPartyRecruiment().encode(packet);
            });
         }
      }

      public static class Open extends Party.BossPartyRecruimentPacket {
         Party party;
         private boolean expired;
         int partyID;
         int bossType;
         byte bossDifficulty;
         List<Party> partyList;

         public Open(Party party) {
            super(BossPartyRecruimentType.OpenBossPartyRecruiment);
            this.party = party;
            this.expired = false;
         }

         public Open(int partyID, int bossType, byte bossDifficulty, List<Party> partyList) {
            super(BossPartyRecruimentType.OpenBossPartyRecruiment);
            this.partyID = partyID;
            this.bossType = bossType;
            this.bossDifficulty = bossDifficulty;
            this.partyList = partyList;
            this.expired = true;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.expired ? this.partyID : this.party.getId());
            packet.write(this.expired ? 0 : 1);
            if (this.expired) {
               packet.writeInt(this.partyList.size());
               this.partyList.forEach(p -> {
                  packet.writeInt(p.getId());
                  packet.write(0);
                  p.getPartyMember().encode(packet);
                  p.getBossPartyRecruiment().encode(packet);
               });
            } else {
               packet.write(0);
               this.party.getPartyMember().encode(packet);
               this.party.getBossPartyRecruiment().encode(packet);
            }
         }
      }

      public static class UpdateJoinMember extends Party.BossPartyRecruimentPacket {
         private int partyID;
         PartyMemberEntry entry;

         public UpdateJoinMember(int partyID, PartyMemberEntry entry) {
            super(BossPartyRecruimentType.UpdateRequestJoinMember);
            this.partyID = partyID;
            this.entry = entry;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.partyID);
            this.entry.encode(packet);
            packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
         }
      }
   }

   public static class CancelJoinRequestPacket {
      private CancelJoinRequestType type;

      public CancelJoinRequestPacket(CancelJoinRequestType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
         packet.write(PartyType.CancelJoinRequest.getType());
         packet.writeInt(this.type.getType());
      }

      public static class CancelJoinRequest extends Party.CancelJoinRequestPacket {
         private int partyID;

         public CancelJoinRequest(int partyID) {
            super(CancelJoinRequestType.CancelJoinRequest);
            this.partyID = partyID;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.partyID);
            packet.writeInt(this.partyID);
            packet.write(0);
         }
      }
   }

   public static class PartyPacket {
      PartyType type;

      public PartyPacket(PartyType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
         packet.write(this.type.getType());
      }

      public static class ChangeLeader extends Party.PartyPacket {
         int playerID;
         boolean leaderDC;

         public ChangeLeader(int playerID, boolean leaderDC) {
            super(PartyType.ChangeLeader);
            this.playerID = playerID;
            this.leaderDC = leaderDC;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.playerID);
            packet.write(this.leaderDC);
         }
      }

      public static class ChangePartySetting extends Party.PartyPacket {
         String title;
         boolean privateParty;
         boolean onlyLeaderPickUp;

         public ChangePartySetting(String title, boolean privateParty, boolean onlyLeaderPickUp) {
            super(PartyType.PartySetting);
            this.title = title;
            this.privateParty = privateParty;
            this.onlyLeaderPickUp = onlyLeaderPickUp;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeMapleAsciiString(this.title);
            packet.write(this.privateParty);
            packet.write(this.onlyLeaderPickUp);
         }
      }

      public static class CreateParty extends Party.PartyPacket {
         Party party;

         public CreateParty(Party party) {
            super(PartyType.CreateParty);
            this.party = party;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.party.getId());
            packet.write(0);
            PartyMemberEntry leaderEntry = this.party.getLeader();
            packet.writeInt(leaderEntry.getDoorTown());
            packet.writeInt(leaderEntry.getDoorTarget());
            packet.writeInt(leaderEntry.getDoorSkill());
            packet.encodePos(leaderEntry.getDoorPosition());
            leaderEntry.encode(packet);
            packet.writeMapleAsciiString(this.party.getPatryTitle());
            packet.write(this.party.isPrivateParty());
            packet.write(this.party.isOnlyLeaderPickUp());
            BossPartyRecruiment recruiment = this.party.getBossPartyRecruiment();
            if (recruiment != null && recruiment.getEntry() != null) {
               recruiment.encode(packet);
            } else {
               packet.writeInt(-1);
            }

            packet.writeInt(leaderEntry.getId());
         }
      }

      public static class InviteParty extends Party.PartyPacket {
         int partyID;
         int playerID;
         String name;
         int level;
         int job;

         public InviteParty(int partyID, int playerID, String name, int level, int job) {
            super(PartyType.InviteParty);
            this.partyID = partyID;
            this.playerID = playerID;
            this.name = name;
            this.level = level;
            this.job = job;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.playerID);
            packet.writeInt(this.partyID);
            packet.writeMapleAsciiString(this.name);
            packet.writeInt(this.level);
            packet.writeInt(this.job);
            packet.writeInt(0);
         }
      }

      public static class JoinCompleteMessage extends Party.PartyPacket {
         boolean onlyLeaderPickUp;

         public JoinCompleteMessage(boolean onlyLeaderPickUp) {
            super(PartyType.JoinCompleteMessage);
            this.onlyLeaderPickUp = onlyLeaderPickUp;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.write(0);
            packet.writeInt(0);
            packet.write(this.onlyLeaderPickUp);
         }
      }

      public static class JoinMember extends Party.PartyPacket {
         Party party;
         String name;
         int channel;

         public JoinMember(Party party, String name, int channel) {
            super(PartyType.JoinMember);
            this.party = party;
            this.name = name;
            this.channel = channel;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeMapleAsciiString(this.name);
            packet.write(0);
            packet.writeInt(0);
            this.party.encode(packet, this.channel, false);
         }
      }

      public static class PartyDataUpdate extends Party.PartyPacket {
         Party party;
         private int channel;

         public PartyDataUpdate(Party party, int channel) {
            super(PartyType.PartyDataUpdate);
            this.party = party;
            this.channel = channel;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            boolean unk = true;
            packet.write(unk);
            if (unk) {
               this.party.encode(packet, this.channel, true);
            } else {
               boolean unk2 = true;
               packet.write(unk2);
               if (unk2) {
                  packet.writeInt(0);
                  packet.write(0);
                  this.party.getPartyMember().encode(packet);
                  this.party.getBossPartyRecruiment().encode(packet);
               }
            }
         }
      }

      public static class TownPortal extends Party.PartyPacket {
         int townFieldID;
         int targetFieldID;
         int skillID;
         boolean create;
         Point position;

         public TownPortal(int townFieldID, int targetFieldID, int skillID, boolean create, Point position) {
            super(PartyType.TownPortal);
            this.townFieldID = townFieldID;
            this.targetFieldID = targetFieldID;
            this.skillID = skillID;
            this.create = create;
            this.position = position;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.write(this.create);
            packet.writeInt(this.townFieldID);
            packet.writeInt(this.targetFieldID);
            packet.writeInt(this.skillID);
            packet.encodePos(this.position);
         }
      }

      public static class WithdrawParty extends Party.PartyPacket {
         Party party;
         String name;
         int channel;
         int playerID;
         PartyOperation operation;

         public WithdrawParty(Party party, String name, int channel, int playerID, PartyOperation operation) {
            super(PartyType.WithdrawParty);
            this.party = party;
            this.name = name;
            this.channel = channel;
            this.playerID = playerID;
            this.operation = operation;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.playerID);
            packet.write(this.operation != PartyOperation.Disband);
            if (this.operation != PartyOperation.Disband) {
               packet.write(this.operation == PartyOperation.KickParty);
               packet.writeMapleAsciiString(this.name);
               this.party.encode(packet, this.channel, true);
            }
         }
      }
   }

   public static class RequestJoinPartyFromRecruiment {
      private RequestJoinPartyFromRecruimentType type;

      public RequestJoinPartyFromRecruiment(RequestJoinPartyFromRecruimentType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
         packet.write(PartyType.RequestJoinPartyFromRecruiment.getType());
         packet.writeInt(this.type.getType());
      }

      public static class RemoveRequest extends Party.RequestJoinPartyFromRecruiment {
         int playerID;

         public RemoveRequest(int playerID) {
            super(RequestJoinPartyFromRecruimentType.RemoveRequest);
            this.playerID = playerID;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.playerID);
         }
      }

      public static class RequestJoinParty extends Party.RequestJoinPartyFromRecruiment {
         PartyMemberEntry entry;

         public RequestJoinParty(PartyMemberEntry entry) {
            super(RequestJoinPartyFromRecruimentType.RequestJoinParty);
            this.entry = entry;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            this.entry.encode(packet);
            packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
         }
      }
   }

   public static class UpdateBossPartyRecruiment {
      private UpdateBossPartyRecruimentType type;

      public UpdateBossPartyRecruiment(UpdateBossPartyRecruimentType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
         packet.write(PartyType.UpdateBossPartyRecruiment.getType());
         packet.writeInt(this.type.getType());
      }

      public static class CancelBossPartyRecruiment extends Party.UpdateBossPartyRecruiment {
         public CancelBossPartyRecruiment() {
            super(UpdateBossPartyRecruimentType.CancelBossPartyRecruiment);
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
         }
      }

      public static class ChangeSetting extends Party.UpdateBossPartyRecruiment {
         BossPartyRecruiment recruiment;

         public ChangeSetting(BossPartyRecruiment recruiment) {
            super(UpdateBossPartyRecruimentType.ChangeSetting);
            this.recruiment = recruiment;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            this.recruiment.encode(packet);
            packet.writeInt(0);
         }
      }

      public static class Create extends Party.UpdateBossPartyRecruiment {
         BossPartyRecruiment recruiment;

         public Create(BossPartyRecruiment recruiment) {
            super(UpdateBossPartyRecruimentType.CreateBossPartyRecruimentList);
            this.recruiment = recruiment;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            this.recruiment.encode(packet);
         }
      }
   }
}
