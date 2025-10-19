package network.game.processors.inventory;

import constants.GameConstants;
import java.awt.Point;
import java.util.List;
import network.decode.PacketDecoder;
import network.game.processors.MovementParse;
import network.models.CWvsContext;
import network.models.PetPacket;
import objects.effect.child.PetLevelUp;
import objects.fields.FieldLimitType;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.item.PetCommand;
import objects.movepath.LifeMovementFragment;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.achievement.AchievementFactory;
import objects.users.enchant.ItemFlag;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;

public class PetHandler {
   public static void SpawnPet(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.skip(4);
      short slot = slea.readShort();
      Item item = chr.getInventory(MapleInventoryType.CASH).getItem(slot);
      MaplePet pet = item.getPet();
      if (pet != null) {
         if (chr.getPetIndex(pet) != -1) {
            chr.unequipPet(pet, false, false, 0);
            chr.updatePetSkills();
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         Point pos = chr.getPosition();
         pet.setPos(pos);

         try {
            pet.setFh(chr.getMap().getFootholds().findBelow(pet.getPos()).getId());
         } catch (Exception var11) {
            pet.setFh(0);
         }

         pet.setStance(0);
         if ((item.getFlag() & ItemFlag.KARMA_EQ.getValue()) != 0) {
            item.setFlag((short)(item.getFlag() - ItemFlag.KARMA_EQ.getValue()));
         }

         int index = slot - 1;
         int questId = 101080 + index;
         int buffSkill1 = chr.getOneInfoQuestInteger(questId, String.valueOf(index * 10));
         int buffSkill2 = chr.getOneInfoQuestInteger(questId, String.valueOf(index * 10 + 1));
         pet.setBuffSkill(0, buffSkill1);
         pet.setBuffSkill(1, buffSkill2);
         chr.addPet(pet);
         c.getSession()
            .writeAndFlush(
               PetPacket.updatePet(
                  c.getPlayer(),
                  pet,
                  c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()),
                  false,
                  c.getPlayer().getPetLoot()
               )
            );
         chr.getMap().broadcastMessage(chr, PetPacket.showPet(chr, pet, false, false, 0), true);
         chr.updatePetSkills();
      }

      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static final void Pet_AutoPotion(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.skip(1);
      slea.readInt();
      short slot = slea.readShort();
      if (chr != null && chr.isAlive() && chr.getMapId() != 749040100 && chr.getMap() != null && !chr.hasDisease(SecondaryStatFlag.StopPortion)) {
         Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
         if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == slea.readInt()) {
            long time = System.currentTimeMillis();
            if (chr.getNextConsume() > time) {
               chr.dropMessage(5, "You may not use this item yet.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), true));
            } else {
               if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
                  if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr, true)) {
                     if (toUse.getItemId() != 2000054) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                     }

                     if (chr.getMap().getConsumeItemCoolTime() > 0) {
                        chr.setNextConsume(time + chr.getMap().getConsumeItemCoolTime() * 1000);
                     }
                  }
               } else {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), true));
               }
            }
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), true));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), true));
      }
   }

   public static final void Pet_ExceptionList(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      int petIndex = slea.readInt();
      long petUniqueID = slea.readLong();
      byte exceptionCount = slea.readByte();
      if (chr != null) {
         if (chr.getMap() != null) {
            MaplePet pet = chr.getPet(petIndex);
            if (pet != null) {
               if (pet.getUniqueId() == petUniqueID) {
                  pet.getLootException().clear();
                  String values = "";

                  for (int i = 0; i < exceptionCount; i++) {
                     int itemID = slea.readInt();
                     values = values + itemID;
                     if (i + 1 != exceptionCount) {
                        values = values + ",";
                     }

                     pet.getLootException().add(itemID);
                  }

                  chr.updateOneInfo(987654, String.valueOf(petUniqueID), values);
                  chr.send(PetPacket.loadExceptionList(pet));
               }
            }
         }
      }
   }

   public static final void PetChat(int petid, short command, String text, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null && chr.getPet(petid) != null) {
         chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), command, text, (byte)petid), false);
      }
   }

   public static final void PetCommand(MaplePet pet, PetCommand petCommand, MapleClient c, MapleCharacter chr) {
      if (petCommand != null) {
         byte petIndex = (byte)chr.getPetIndex(pet);
         boolean success = false;
         if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
               int newCloseness = pet.getCloseness() + petCommand.getIncrease() * c.getChannelServer().getTraitRate();
               if (newCloseness > 30000) {
                  newCloseness = 30000;
               }

               pet.setCloseness(newCloseness);
               if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                  pet.setLevel(pet.getLevel() + 1);
                  PetLevelUp e = new PetLevelUp(c.getPlayer().getId(), 0, c.getPlayer().getPetIndex(pet));
                  c.getSession().writeAndFlush(e.encodeForLocal());
                  c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
               }

               c.getSession()
                  .writeAndFlush(
                     PetPacket.updatePet(chr, pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), false, chr.getPetLoot())
                  );
            }
         }

         chr.getMap().broadcastMessage(PetPacket.commandResponse(chr.getId(), (byte)petCommand.getSkillId(), petIndex, success, false));
      }
   }

   public static void PetFood(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));

      for (MaplePet pet : chr.getPets()) {
         if (pet != null) {
            slea.skip(6);
            int itemId = slea.readInt();
            boolean gainCloseness = false;
            if (Randomizer.nextInt(99) <= 50) {
               gainCloseness = true;
            }

            if (pet.getFullness() < 100) {
               int newFullness = pet.getFullness() + 30;
               if (newFullness > 100) {
                  newFullness = 100;
               }

               pet.setFullness(newFullness);
               int index = chr.getPetIndex(pet);
               if (gainCloseness && pet.getCloseness() < 30000) {
                  int newCloseness = pet.getCloseness() + 1;
                  if (newCloseness > 30000) {
                     newCloseness = 30000;
                  }

                  pet.setCloseness(newCloseness);
                  if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                     pet.setLevel(pet.getLevel() + 1);
                     PetLevelUp e = new PetLevelUp(c.getPlayer().getId(), 0, c.getPlayer().getPetIndex(pet));
                     c.getSession().writeAndFlush(e.encodeForLocal());
                     c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
                  }
               }

               AchievementFactory.checkItemUse(chr, itemId, 0, 1);
               c.getSession()
                  .writeAndFlush(
                     PetPacket.updatePet(chr, pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), false, chr.getPetLoot())
                  );
               chr.getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(chr.getId(), (byte)1, (byte)index, true, true), true);
            } else {
               if (gainCloseness) {
                  int newClosenessx = pet.getCloseness() - 1;
                  if (newClosenessx < 0) {
                     newClosenessx = 0;
                  }

                  pet.setCloseness(newClosenessx);
                  if (newClosenessx < GameConstants.getClosenessNeededForLevel(pet.getLevel())) {
                     pet.setLevel(pet.getLevel() - 1);
                  }
               }

               c.getSession()
                  .writeAndFlush(
                     PetPacket.updatePet(chr, pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), false, chr.getPetLoot())
                  );
               chr.getMap().broadcastMessage(chr, PetPacket.commandResponse(chr.getId(), (byte)1, (byte)chr.getPetIndex(pet), false, true), true);
            }

            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemId, 1, true, false);
            return;
         }
      }
   }

   public static final void MovePet(PacketDecoder slea, MapleCharacter chr) {
      int petId = slea.readInt();
      slea.skip(13);
      List<LifeMovementFragment> res = MovementParse.parseMovement(slea);
      if (res != null && chr != null && res.size() != 0 && chr.getMap() != null) {
         MaplePet pet = chr.getPet(petId);
         if (pet == null) {
            return;
         }

         pet.updatePosition(res);
         chr.getMap().broadcastMessage(chr, PetPacket.movePet(chr.getId(), pet.getUniqueId(), (byte)petId, res, pet.getPos()), false);
         chr.setScrolledPosition((short)0);
      }
   }

   public static void ChangePetBuff(PacketDecoder slea, MapleCharacter chr) {
      int petIndex = slea.readInt();
      int skillIndex = slea.readInt();
      int skillId = slea.readInt();
      int mode = slea.readByte();
      MaplePet pet = chr.getPet(petIndex);
      int questId = 101080 + petIndex;
      String questKey = String.valueOf(petIndex * 10 + skillIndex);
      if (pet == null) {
         chr.dropMessage(1, "펫 정보가 없습니다.");
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
      } else if (skillIndex >= 2) {
         chr.dropMessage(1, "인덱스 정보에 오류가 발생하였습니다.");
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
      } else if (skillId != 0 && SkillFactory.getSkill(skillId) == null) {
         chr.dropMessage(1, "스킬 정보에 오류가 발생하였습니다.");
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
      } else {
         if (skillId != 0) {
            Skill skill = SkillFactory.getSkill(skillId);
            SecondaryStatEffect eff = skill.getEffect(chr.getSkillLevel(skillId));
            if (eff != null && eff.getCooldown(chr) > 0 && !skill.isPetAutoBuff()) {
               chr.dropMessage(1, "스킬 하위 정보에 오류가 발생하였습니다.");
               chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return;
            }
         }

         if (mode == 0 || mode == 1) {
            if ((pet.getFlags() & MaplePet.PetFlag.PET_BUFF.getValue()) > 0) {
               pet.setBuffSkill(skillIndex, skillId);
               Item item = chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition());
               item.setPet(pet);
               chr.updateOneInfo(questId, questKey, String.valueOf(skillId));
               chr.getClient().getSession().writeAndFlush(PetPacket.updatePet(chr, pet, item, false, chr.getPetLoot()));
            } else {
               chr.send(CWvsContext.serverNotice(1, "등록된 펫정보를 불러오는데 실패했습니다."));
            }
         }
      }
   }
}
