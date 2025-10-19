package objects.users.skills;

import constants.GameConstants;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import database.loader.CharacterSaveFlag2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class LinkSkill {
   private List<LinkSkillEntry> linkSkills = new ArrayList<>();
   private List<List<LinkSkillEntry>> linkSkillPresets = new ArrayList<>();

   public LinkSkill(MapleCharacter player) {
      this.loadSkills(player);
      this.loadPresets(player);
   }

   public void loadSkills(MapleCharacter player) {
      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM linkskill WHERE `accid` = ?");
         ps.setInt(1, player.getAccountID());
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            LinkSkillEntry skill = new LinkSkillEntry(
               rs.getInt("realskillid"), rs.getInt("skillid"), rs.getInt("linking_cid"), rs.getInt("linked_cid"), rs.getShort("skilllevel"), rs.getLong("time")
            );
            this.linkSkills.add(skill);
         }

         rs.close();
         ps.close();
      } catch (SQLException var8) {
         var8.printStackTrace();
      }
   }

   public void loadPresets(MapleCharacter player) {
      for (int i = 0; i < 3; i++) {
         this.linkSkillPresets.add(new ArrayList<>());
      }

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM linkskill_preset WHERE `character_id` = ?");
         ps.setInt(1, player.getId());
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            int skillID = rs.getInt("skill_id");
            int linking_cid = rs.getInt("linking_cid");
            int preset = rs.getInt("preset");
            LinkSkillEntry skill = this.getLinkSkill(skillID, linking_cid);
            if (skill != null) {
               this.linkSkillPresets.get(preset).add(skill);
            }
         }

         rs.close();
         ps.close();
      } catch (SQLException var11) {
         var11.printStackTrace();
      }
   }

   public void unlinkSkill(int skillID, MapleCharacter chr) {
      List<LinkSkillEntry> skillList = new ArrayList<>();
      this.getLinkSkills().forEach(skillx -> {
         if (skillx.getLinkedPlayerID() == chr.getId() && skillx.getSkillID() != skillID && SkillEncode.getStackedLinkSkill(skillx.getSkillID()) == skillID) {
            skillList.add(skillx);
         }
      });
      chr.setSaveFlag(chr.getSaveFlag() | CharacterSaveFlag.LINK_SKILLS.getFlag());
      if (!skillList.isEmpty()) {
         int skillLv = 0;

         for (LinkSkillEntry skill : skillList) {
            skillLv += skill.getSkillLevel();
            skill.setLinkedPlayerID(skill.getLinkingPlayerID());
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.UNLINK_SKILL.getValue());
         packet.writeInt(skillList.size() + 1);
         packet.writeInt(skillID);
         packet.writeInt(skillLv);

         for (LinkSkillEntry skill : skillList) {
            packet.writeInt(skill.getRealSkillID());
            packet.writeInt(skill.getLinkingPlayerID());
         }

         chr.send(packet.getPacket());
      } else {
         LinkSkillEntry skill = this.getLinkSkill_(skillID, chr.getId());
         skill.setLinkedPlayerID(skill.getLinkingPlayerID());
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.UNLINK_SKILL.getValue());
         packet.writeInt(1);
         packet.writeInt(skillID);
         packet.writeInt(skill.getLinkingPlayerID());
         chr.send(packet.getPacket());
      }
   }

   public static void unlinkSkill(PacketDecoder o, MapleClient c) {
      int skillID = o.readInt();
      MapleCharacter chr = c.getPlayer();
      if (chr != null && GameConstants.isLinkSkill(skillID)) {
         LinkSkill linkSkill = chr.getLinkSkill();
         linkSkill.unlinkSkill(skillID, chr);
         chr.getStat().recalcLocalStats(chr);
      }
   }

   public void linkSkill(MapleCharacter chr, int skillID, int linkingPlayerID) {
      int ordinarySkillID = SkillEncode.getStackedLinkSkill(skillID);
      LinkSkillEntry skill = this.getLinkSkill(skillID, linkingPlayerID);
      chr.setSaveFlag(chr.getSaveFlag() | CharacterSaveFlag.LINK_SKILLS.getFlag());
      skill.setLinkedPlayerID(chr.getId());
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LINK_SKILL.getValue());
      packet.writeInt(linkingPlayerID);
      packet.writeInt(skill.getLinkedPlayerID());
      packet.writeInt(skillID);
      packet.writeShort(skill.getSkillLevel());
      packet.writeLong(PacketHelper.getTime(-2L));
      packet.writeInt(SkillFactory.getSkill(skillID).getType() == 51 ? 1 : 0);
      if (ordinarySkillID != skillID) {
         packet.writeInt(ordinarySkillID);
         int ordinarySkillLv = this.getLinkSkills()
            .stream()
            .filter(s -> SkillEncode.getStackedLinkSkill(s.getSkillID()) == ordinarySkillID && s.getLinkedPlayerID() == chr.getId())
            .mapToInt(LinkSkillEntry::getSkillLevel)
            .sum();
         packet.writeInt(ordinarySkillLv);
      } else {
         packet.writeInt(0);
      }

      chr.send(packet.getPacket());
   }

   public static void linkSkill(PacketDecoder o, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      int skillID = o.readInt();
      int linkingPlayerID = o.readInt();
      int linkedPlayerID = o.readInt();
      if (chr != null && GameConstants.isLinkSkill(skillID)) {
         LinkSkill linkSkill = chr.getLinkSkill();
         if (linkSkill != null) {
            linkSkill.linkSkill(chr, skillID, linkingPlayerID);
            chr.getStat().recalcLocalStats(chr);
         }
      }
   }

   public List<LinkSkillEntry> getLinkSkills() {
      return this.linkSkills;
   }

   public void setLinkSkills(List<LinkSkillEntry> linkSkills) {
      this.linkSkills = linkSkills;
   }

   public LinkSkillEntry getLinkSkill(int ID, int linkingID) {
      return this.linkSkills.stream().filter(skill -> skill.getRealSkillID() == ID && skill.getLinkingPlayerID() == linkingID).findFirst().orElse(null);
   }

   public LinkSkillEntry getFindResistanceLinkSkill(int skillID, int playerID) {
      return this.linkSkills.stream().filter(skill -> skill.getSkillID() == skillID && skill.getLinkedPlayerID() == playerID).findFirst().orElse(null);
   }

   public LinkSkillEntry getLinkSkill_(int ID, int linkedID) {
      return this.linkSkills.stream().filter(skill -> skill.getRealSkillID() == ID && skill.getLinkedPlayerID() == linkedID).findFirst().orElse(null);
   }

   public void updateLinkSkillByFreeJobChange(MapleCharacter chr) {
      LinkSkillEntry linkSkill = this.linkSkills.stream().filter(skill -> skill.getLinkingPlayerID() == chr.getId()).findFirst().orElse(null);
      if (linkSkill != null) {
         this.linkSkills.remove(linkSkill);
      }

      this.updateOwnLinkSkill(chr);
   }

   public void updateOwnLinkSkill(MapleCharacter chr) {
      int skillID = SkillEncode.getLinkSkill(chr.getJob());
      if (skillID > 0) {
         LinkSkill linkSkill = chr.getLinkSkill();
         int skillLevel = 1;
         if (!GameConstants.isZero(chr.getJob())) {
            if (chr.getLevel() >= 120) {
               skillLevel = 2;
            }
         } else if (chr.getLevel() >= 180) {
            skillLevel = 5;
         } else if (chr.getLevel() >= 160) {
            skillLevel = 4;
         } else if (chr.getLevel() >= 140) {
            skillLevel = 3;
         } else if (chr.getLevel() >= 120) {
            skillLevel = 2;
         }

         LinkSkillEntry skill = linkSkill.getLinkSkill(skillID, chr.getId());
         if (skill != null) {
            skill.setSkillLevel((short)skillLevel);
         } else {
            linkSkill.getLinkSkills()
               .add(
                  new LinkSkillEntry(
                     GameConstants.getDuplicateOfOriginalSkill(skillID), skillID, chr.getId(), chr.getId(), (short)skillLevel, System.currentTimeMillis()
                  )
               );
         }

         chr.setSaveFlag(chr.getSaveFlag() | CharacterSaveFlag.LINK_SKILLS.getFlag());
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.LINK_SKILL.getValue());
         packet.writeInt(skill != null ? skill.getLinkingPlayerID() : chr.getId());
         packet.writeInt(skill != null ? skill.getLinkedPlayerID() : chr.getId());
         packet.writeInt(skillID);
         packet.writeShort(skillLevel);
         packet.writeLong(PacketHelper.getTime(-2L));
         packet.writeInt(SkillFactory.getSkill(skillID).getType() == 51 ? 1 : 0);
         int ordinarySkillID = SkillEncode.getStackedLinkSkill(skillID);
         if (ordinarySkillID != skillID) {
            packet.writeInt(ordinarySkillID);
            int ordinarySkillLv = linkSkill.getLinkSkills()
               .stream()
               .filter(s -> SkillEncode.getStackedLinkSkill(s.getSkillID()) == ordinarySkillID && s.getLinkedPlayerID() == chr.getId())
               .mapToInt(LinkSkillEntry::getSkillLevel)
               .sum();
            packet.writeInt(ordinarySkillLv);
         } else {
            packet.writeInt(0);
         }

         chr.send(packet.getPacket());
      }
   }

   public static void linkSkillPreset(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         LinkSkill linkSkill = chr.getLinkSkill();
         if (linkSkill != null) {
            List<List<LinkSkillEntry>> presets = linkSkill.getLinkSkillPresets();
            if (presets != null) {
               byte[] array = slea.getByteArray();
               int index = slea.readInt();
               if (presets.size() >= index) {
                  chr.setSaveFlag2(chr.getSaveFlag2() | CharacterSaveFlag2.LINKSKILL_PRESET.getFlag());
                  List<LinkSkillEntry> preset = presets.get(index - 1);
                  preset.clear();
                  int size = slea.readInt();

                  for (int i = 0; i < size; i++) {
                     int skillID = slea.readInt();
                     int ownerID = slea.readInt();
                     LinkSkillEntry skill = linkSkill.getLinkSkill(skillID, ownerID);
                     if (GameConstants.isLinkSkill(skillID) && skill != null) {
                        preset.add(skill);
                     }
                  }

                  PacketEncoder packet = new PacketEncoder();
                  packet.writeShort(SendPacketOpcode.LINK_SKILL_PRESET.getValue());

                  for (int x = 2; x < array.length; x++) {
                     packet.write(array[x]);
                  }

                  packet.write(false);
                  chr.send(packet.getPacket());
               }
            }
         }
      }
   }

   public static void setLinkSkill(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         LinkSkill linkSkill = chr.getLinkSkill();
         if (linkSkill != null) {
            chr.setSaveFlag2(chr.getSaveFlag2() | CharacterSaveFlag2.LINKSKILL_PRESET.getFlag());
            int removeList = slea.readByte();

            for (int i = 0; i < removeList; i++) {
               int skillID = slea.readInt();
               if (GameConstants.isLinkSkill(skillID)) {
                  linkSkill.unlinkSkill(skillID, chr);
               }
            }

            int addList = slea.readByte();

            for (int ix = 0; ix < addList; ix++) {
               int skillID = slea.readInt();
               int ownerID = slea.readInt();
               slea.readInt();
               slea.readInt();
               slea.readInt();
               if (GameConstants.isLinkSkill(skillID)) {
                  LinkSkillEntry skill = linkSkill.getLinkSkill(skillID, ownerID);
                  if (skill != null) {
                     linkSkill.linkSkill(chr, skillID, ownerID);
                  }
               }
            }
         }
      }
   }

   public List<List<LinkSkillEntry>> getLinkSkillPresets() {
      return this.linkSkillPresets;
   }

   public static void linkSkillUpdate(MapleCharacter chr) {
      if (GameConstants.isAdventurer(chr.getJob())) {
         if (chr.getLevel() >= 200) {
            if (chr.getLinkSkill() != null) {
               int skillID = SkillEncode.getLinkSkill(chr.getJob());
               List<Integer> anotherJobLink = SkillEncode.getAdventureAnotherJobLink(chr.getJob());
               LinkSkillEntry linkSkill = chr.getLinkSkill().getLinkSkill(skillID, chr.getId());
               if (linkSkill == null) {
                  linkSkill = chr.getLinkSkill().getLinkSkill(GameConstants.getDuplicateOfOriginalSkill(skillID), chr.getId());
               }

               boolean change;
               if (linkSkill != null) {
                  if (linkSkill.getSkillLevel() < 2) {
                     change = true;
                     linkSkill.setSkillLevel((short)2);
                  } else {
                     change = false;
                  }
               } else {
                  change = true;
                  chr.getLinkSkill()
                     .getLinkSkills()
                     .add(
                        new LinkSkillEntry(
                           GameConstants.getDuplicateOfOriginalSkill(skillID), skillID, chr.getId(), chr.getId(), (short)2, System.currentTimeMillis()
                        )
                     );
               }

               for (Integer link : anotherJobLink) {
                  linkSkill = chr.getLinkSkill().getLinkSkill(link, chr.getId());
                  if (linkSkill != null) {
                     change = true;
                     linkSkill.setSkillLevel((short)0);
                  }
               }

               if (change && skillID > -1) {
                  chr.setSaveFlag(chr.getSaveFlag() | CharacterSaveFlag.LINK_SKILLS.getFlag());
                  PacketEncoder packet = new PacketEncoder();
                  packet.writeShort(SendPacketOpcode.LINK_SKILL.getValue());
                  packet.writeInt(chr.getId());
                  packet.writeInt(chr.getId());
                  packet.writeInt(skillID);
                  packet.writeShort(2);
                  packet.writeLong(PacketHelper.getTime(-2L));
                  packet.writeInt(SkillFactory.getSkill(skillID).getType() == 51 ? 1 : 0);
                  int ordinarySkillID = SkillEncode.getStackedLinkSkill(skillID);
                  if (ordinarySkillID != skillID) {
                     packet.writeInt(ordinarySkillID);
                     int ordinarySkillLv = chr.getLinkSkill()
                        .getLinkSkills()
                        .stream()
                        .filter(s -> SkillEncode.getStackedLinkSkill(s.getSkillID()) == ordinarySkillID && s.getLinkedPlayerID() == chr.getId())
                        .mapToInt(LinkSkillEntry::getSkillLevel)
                        .sum();
                     packet.writeInt(ordinarySkillLv);
                  } else {
                     packet.writeInt(0);
                  }
               }
            }
         }
      }
   }
}
