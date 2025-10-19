package network.game.processors;

import constants.GameConstants;
import database.DBConfig;
import database.loader.CharacterSaveFlag;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.effect.child.PostSkillEffect;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.jobs.zero.Zero;
import objects.users.skills.KainStackSkill;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackAction;
import objects.users.skills.VCore;
import objects.users.skills.VCoreData;
import objects.users.skills.VCoreEnforcement;
import objects.users.skills.VMatrixOption;
import objects.users.skills.VMatrixSlot;
import objects.users.skills.VMatrixSlotExpensionMeso;
import objects.users.skills.VSpecialCoreOption;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.Pair;
import objects.utils.Randomizer;
import scripting.NPCScriptManager;

public class VMatrixHandler {
   public static void VCoreCheckSecondPassowrd(PacketDecoder slea, MapleClient c) {
      int type = slea.readInt();
      if (type == 1) {
         int result = 0;
         if (slea.available() >= 2L) {
            String password = slea.readMapleAsciiString();
            if (c.getSecondPassword().equals(password)) {
               result = 1;
            }
         } else {
            result = 1;
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.CORE_SECOND_PASSWORD_CHECK_RESULT.getValue());
         packet.writeInt(3);
         packet.write(result);
         c.getPlayer().send(packet.getPacket());
      }
   }

   public static void VCoreQuestion(PacketDecoder slea, MapleClient c) {
      int type = slea.readInt();
      if (type == 1) {
         NPCScriptManager.getInstance().start(c, 1540945, "CoreMakeMode", true);
      } else if (type == 2) {
         NPCScriptManager.getInstance().start(c, 1540945, "CoreMatrixMode", true);
      }
   }

   public static int getEmptyVMatrixSlots(MapleCharacter player) {
      List<VMatrixSlot> slots = player.getVMatrixSlots();
      AtomicInteger ret = new AtomicInteger(-1);
      slots.stream().sorted((a, b) -> b.getIndex() - a.getIndex()).collect(Collectors.toList()).forEach(slot -> {
         if (slot.getEquippedCore() == -1) {
            ret.set(slot.getIndex());
         }
      });
      return ret.get();
   }

   public static void EquipVCore(VCore vcore, MapleClient c, boolean unequip) {
      Skill skill1 = SkillFactory.getSkill(vcore.getSkill1());
      Skill skill2 = SkillFactory.getSkill(vcore.getSkill2());
      Skill skill3 = SkillFactory.getSkill(vcore.getSkill3());
      c.getPlayer().updateMatrixSkillsNoLock();
      VCoreData.VCoreInfo info = VCoreData.getCoreInfo(vcore.getCoreId());
      int maxLevel = 25;
      if (info != null) {
         int type = info.getType();
         maxLevel = type == 0 ? 25 : (type == 1 ? 50 : 1);
      }

      if (unequip) {
         if (skill1 != null) {
            if (skill1.getId() == 400031003) {
               c.getPlayer().temporaryStatResetBySkillID(400031003);
            }

            c.getPlayer()
               .changeSkillLevel_Skip(skill1, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill1) - vcore.getLevel()), (byte)maxLevel, false);
         }

         if (skill2 != null) {
            c.getPlayer()
               .changeSkillLevel_Skip(skill2, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill2) - vcore.getLevel()), (byte)maxLevel, false);
         }

         if (skill3 != null) {
            c.getPlayer()
               .changeSkillLevel_Skip(skill3, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill3) - vcore.getLevel()), (byte)maxLevel, false);
         }

         if (info != null
            && info.getSpecialCoreOption() != null
            && c.getPlayer().getEquippedSpecialCore().getRight() == info.getSpecialCoreOption().getSkillId()) {
            c.getPlayer().setEquippedSpecialCore(null);
         }
      } else {
         if (skill1 != null) {
            if (GameConstants.isAngelicBuster(c.getPlayer().getJob())
               || GameConstants.isArk(c.getPlayer().getJob())
               || GameConstants.isEunWol(c.getPlayer().getJob())
               || GameConstants.isXenon(c.getPlayer().getJob())
               || GameConstants.isStriker(c.getPlayer().getJob())) {
               c.getPlayer().changeSkillLevel(400051001, c.getPlayer().getTotalSkillLevel(400051000), 30);
            }

            c.getPlayer()
               .changeSkillLevel_Skip(skill1, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill1) + vcore.getLevel()), (byte)maxLevel, false);
         }

         if (skill2 != null) {
            c.getPlayer()
               .changeSkillLevel_Skip(skill2, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill2) + vcore.getLevel()), (byte)maxLevel, false);
         }

         if (skill3 != null) {
            c.getPlayer()
               .changeSkillLevel_Skip(skill3, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill3) + vcore.getLevel()), (byte)maxLevel, false);
         }

         if (info.getSpecialCoreOption() != null && c.getPlayer().getEquippedSpecialCore() == null) {
            c.getPlayer().setEquippedSpecialCore(new Pair<>(vcore.getCoreId(), info.getSpecialCoreOption().getSkillId()));
         }
      }
   }

   public static boolean checkEquippedVCore(int slot, MapleCharacter player) {
      int reqLevel = 200;
      short var4;
      switch (slot) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
            var4 = 200;
            break;
         case 7:
            var4 = 205;
            break;
         case 8:
         case 9:
            var4 = 210;
            break;
         case 10:
            var4 = 215;
            break;
         case 11:
         case 12:
            var4 = 220;
            break;
         case 13:
            var4 = 225;
            break;
         case 14:
         case 15:
            var4 = 230;
            break;
         case 16:
            var4 = 235;
            break;
         case 17:
         case 18:
            var4 = 240;
            break;
         case 19:
            var4 = 245;
            break;
         case 20:
         case 21:
            var4 = 250;
            break;
         case 22:
            var4 = 255;
            break;
         case 23:
         case 24:
            var4 = 260;
            break;
         default:
            return false;
      }

      if (player.getLevel() >= var4) {
         return true;
      } else {
         VMatrixSlot matrixSlot = player.getVMatrixSlot(slot);
         return matrixSlot == null ? false : matrixSlot.getReleased() != 0;
      }
   }

   public static void VCoreRequest(PacketDecoder slea, MapleClient c) {
      int type = slea.readInt();
      if (DBConfig.isGanglim && c.getPlayer().getLevel() >= 300) {
         for (VMatrixSlot vMatrixSlot : c.getPlayer().getVMatrixSlots()) {
            vMatrixSlot.setSlotEnforcement(5);
         }
      }

      switch (type) {
         case 0:
         case 1:
            int coreIndex = slea.readInt();
            int eqq = slea.readInt();
            int dest = -1;
            boolean dragEquip = false;
            if (type == 0) {
               slea.skip(4);
               dest = slea.readInt();
               dragEquip = slea.readByte() == 1;
            }

            if (eqq != -1) {
               VCore equippedCore = null;

               for (VCore corex : c.getPlayer().getVCoreSkillsNoLock()) {
                  if (corex.getState() == 2 && corex.getIndex() == eqq) {
                     equippedCore = corex;
                  }

                  if (equippedCore != null) {
                     equippedCore.setState(1);
                     equippedCore.setPosition(-1);
                     EquipVCore(equippedCore, c, true);
                     break;
                  }
               }
            }

            VCore vcore = null;

            for (VCore corex : c.getPlayer().getVCoreSkillsNoLock()) {
               if (corex.getIndex() == coreIndex) {
                  vcore = corex;
                  break;
               }
            }

            if (vcore == null) {
               return;
            }

            boolean equip = type == 0;
            if (equip) {
               if (dest == -1) {
                  dest = getEmptyVMatrixSlots(c.getPlayer());
               }

               if (dest == -1) {
                  c.getPlayer().dropMessage(1, "장착 가능한 슬롯이 없습니다.");
                  return;
               }

               if (!checkEquippedVCore(dest, c.getPlayer())) {
                  c.getPlayer().dropMessage(1, "개방되지 않은 슬롯에는 장착할 수 없습니다.");
                  return;
               }
            }

            VMatrixSlot slot = c.getPlayer().getVMatrixSlot(dest);
            if (slot == null) {
               return;
            }

            vcore.setPosition(dest);
            vcore.setState(2 - type);
            if (!equip) {
               for (VMatrixSlot s : new ArrayList<>(c.getPlayer().getVMatrixSlots())) {
                  if (s.getEquippedCore() == vcore.getCoreId()) {
                     s.setEquippedCore(-1);
                     break;
                  }
               }
            } else {
               c.getPlayer().getVMatrixSlot(dest).setEquippedCore(vcore.getCoreId());
            }

            EquipVCore(vcore, c, !equip);
            c.getPlayer().setChangedSkills();
            c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 0, coreIndex));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
            if (c.getPlayer().getHexaCore() != null) {
               c.getPlayer().getHexaCore().checkDisabledSkillCore(c.getPlayer());
               c.getPlayer().send(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
               HyperHandler.updateSkills(c.getPlayer(), 0);
            }
            break;
         case 2:
            coreIndex = slea.readInt();
            eqq = slea.readInt();
            int originPos = slea.readInt();
            int destPos = slea.readInt();
            vcore = null;
            Iterator var85 = c.getPlayer().getVCoreSkillsNoLock().iterator();

            while (true) {
               if (var85.hasNext()) {
                  VCore core = (VCore)var85.next();
                  if (core.getIndex() != coreIndex) {
                     continue;
                  }

                  vcore = core;
               }

               if (!checkEquippedVCore(destPos, c.getPlayer())) {
                  c.getPlayer().dropMessage(1, "개방되지 않은 슬롯에는 장착할 수 없습니다.");
                  return;
               }

               if (vcore == null) {
                  return;
               }

               int slotIndex = vcore.getPosition();
               if (slotIndex != originPos) {
                  System.out.println("클라이언트가 보낸 원본 위치와 실제 장착된 위치가 다름. (캐릭터 이름 : " + c.getPlayer().getName() + ")");
                  return;
               }

               if (vcore.getState() != 2) {
                  System.out.println("장착 되어 있는 코어가 아닌 코어를 이동하려함. (캐릭터 이름 : " + c.getPlayer().getName() + ")");
                  return;
               }

               VMatrixSlot originSlot = c.getPlayer().getVMatrixSlot(originPos);
               VMatrixSlot destSlot = c.getPlayer().getVMatrixSlot(destPos);
               if (originSlot == null || destSlot == null) {
                  return;
               }

               int destSlotCoreIndex = destSlot.getIndex();
               VCore destCore = null;

               for (VCore core : c.getPlayer().getVCoreSkillsNoLock()) {
                  if (core.getIndex() == eqq) {
                     destCore = core;
                     break;
                  }
               }

               EquipVCore(vcore, c, true);
               vcore.setPosition(destPos);
               EquipVCore(vcore, c, false);
               if (eqq != -1) {
                  if (destCore != null) {
                     EquipVCore(destCore, c, true);
                     destCore.setPosition(originPos);
                     EquipVCore(destCore, c, false);
                     originSlot.setEquippedCore(destCore.getCoreId());
                  }
               } else {
                  originSlot.setEquippedCore(-1);
               }

               destSlot.setEquippedCore(vcore.getCoreId());
               c.getPlayer().setChangedSkills();
               c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 2, 0));
               c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
               break;
            }
         case 3:
         case 8:
         case 12:
         default:
            break;
         case 4:
            int sidex = slea.readInt();
            int length = slea.readInt();
            int totalExp = 0;
            List<VCore> coreList = new LinkedList<>();
            c.getPlayer().sortedVCoreSkillsReadLock();
            c.getPlayer().lockVCoreSkillsReadLock();
            VCore corexx = null;

            for (VCore vcorexxxx : c.getPlayer().getVCoreSkillsNoLock()) {
               if (vcorexxxx.getIndex() == sidex) {
                  corexx = vcorexxxx;
                  break;
               }
            }

            try {
               for (int i = 0; i < length; i++) {
                  int slotx = slea.readInt();
                  VCore mCore = null;

                  for (VCore vcorexxxxx : c.getPlayer().getVCoreSkillsNoLock()) {
                     if (vcorexxxxx.getIndex() == slotx) {
                        mCore = vcorexxxxx;
                        break;
                     }
                  }

                  if (mCore != null) {
                     coreList.add(mCore);
                     if (mCore.getCoreId() == 40000000) {
                        totalExp += 150;
                     } else {
                        VCoreData.VCoreInfo infoxx = VCoreData.getCoreInfo(mCore.getCoreId());
                        if (infoxx != null) {
                           VCoreEnforcement.EnforceInfo eInfo = getEnforceInfo(infoxx.getType(), mCore.getLevel());
                           if (eInfo != null) {
                              totalExp += eInfo.getExpEnforce();
                           }
                        }
                     }
                  }
               }

               VCoreData.VCoreInfo infoxx = VCoreData.getCoreInfo(corexx.getCoreId());
               if (infoxx != null) {
                  int currentLevel = corexx.getLevel();
                  int afterLevel = currentLevel;
                  VCoreEnforcement.EnforceInfo eInfo = getEnforceInfo(infoxx.getType(), currentLevel);
                  if (eInfo != null) {
                     int nextExp = eInfo.getNextExp();
                     corexx.setExp(corexx.getExp() + totalExp);

                     while (corexx.getExp() >= nextExp) {
                        afterLevel = corexx.getLevel() + 1;
                        corexx.setLevel(afterLevel);
                        corexx.setExp(corexx.getExp() - nextExp);
                        if (afterLevel >= infoxx.getMaxLevel()) {
                           afterLevel = infoxx.getMaxLevel();
                           corexx.setLevel(afterLevel);
                           corexx.setExp(0);
                           break;
                        }

                        VCoreEnforcement.EnforceInfo e = getEnforceInfo(infoxx.getType(), afterLevel);
                        if (e != null) {
                           nextExp = e.getNextExp();
                        }
                     }
                  }

                  coreList.forEach(c.getPlayer()::removeVCoreSkillsNoLock);
                  c.getPlayer().updateMatrixSkillsNoLock();
                  int maxLevel = 25;
                  if (infoxx != null) {
                     maxLevel = infoxx.getType() == 0 ? 25 : 50;
                  }

                  Skill skill1 = SkillFactory.getSkill(corexx.getSkill1());
                  Skill skill2 = SkillFactory.getSkill(corexx.getSkill2());
                  Skill skill3 = SkillFactory.getSkill(corexx.getSkill3());
                  c.getPlayer()
                     .changeSkillLevel_Skip(
                        skill1, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill1) + (afterLevel - currentLevel)), (byte)maxLevel, false
                     );
                  if (skill2 != null) {
                     c.getPlayer()
                        .changeSkillLevel_Skip(
                           skill2, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill1) + (afterLevel - currentLevel)), (byte)maxLevel, false
                        );
                  }

                  if (skill3 != null) {
                     c.getPlayer()
                        .changeSkillLevel_Skip(
                           skill3, (byte)Math.min(maxLevel, c.getPlayer().getSkillLevel(skill1) + (afterLevel - currentLevel)), (byte)maxLevel, false
                        );
                  }

                  if (GameConstants.isAngelicBuster(c.getPlayer().getJob())
                     || GameConstants.isArk(c.getPlayer().getJob())
                     || GameConstants.isEunWol(c.getPlayer().getJob())) {
                     c.getPlayer().changeSkillLevel(400051001, c.getPlayer().getTotalSkillLevel(400051000), 30);
                  }

                  c.getPlayer().setChangedSkills();
                  c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 3, 0));
                  c.getSession().writeAndFlush(CWvsContext.CoreEnforcementResult(sidex, totalExp, currentLevel, afterLevel));
                  c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
               }
               break;
            } finally {
               c.getPlayer().unlockVCoreSkillsReadLock();
            }
         case 5:
            int side = slea.readInt();
            corexx = null;

            for (VCore vcore3 : c.getPlayer().getVCoreSkillsNoLock()) {
               if (vcore3.getIndex() == side) {
                  corexx = vcore3;
                  break;
               }
            }

            c.getPlayer().sortedVCoreSkillsReadLock();
            if (corexx == null) {
               return;
            }

            VCoreData.VCoreInfo info = VCoreData.getCoreInfo(corexx.getCoreId());
            if (info == null) {
               return;
            }

            int extract = 0;
            if (info.getType() == 0) {
               extract = VCoreEnforcement.getSkillInfo(corexx.getLevel()).getExtract();
            } else if (info.getType() == 1) {
               extract = VCoreEnforcement.getEnforceInfo(corexx.getLevel()).getExtract();
            } else if (info.getType() == 2) {
               extract = VCoreEnforcement.getSpecialInfo(corexx.getLevel()).getExtract();
            }

            c.getPlayer().gainPieceOfCore(extract);
            c.getPlayer().removeVCoreSkillsNoLock(corexx);
            c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 5, 0));
            c.getSession().writeAndFlush(CWvsContext.UpdateCoreQuantity(extract));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
            break;
         case 6:
            int coreLength = slea.readInt();
            int quantityx = 0;
            coreList = new LinkedList<>();
            c.getPlayer().sortedVCoreSkillsReadLock();
            c.getPlayer().lockVCoreSkillsReadLock();

            try {
               for (int nextx = 0; nextx < coreLength; nextx++) {
                  corexx = null;
                  int coreIndexx = slea.readInt();

                  for (VCore vcorexxx : c.getPlayer().getVCoreSkillsNoLock()) {
                     if (vcorexxx.getIndex() == coreIndexx) {
                        corexx = vcorexxx;
                        break;
                     }
                  }

                  if (corexx != null) {
                     VCoreData.VCoreInfo infoxx = VCoreData.getCoreInfo(corexx.getCoreId());
                     if (infoxx != null) {
                        coreList.add(corexx);
                        extract = 0;
                        if (infoxx.getType() == 0) {
                           extract = VCoreEnforcement.getSkillInfo(corexx.getLevel()).getExtract();
                        } else if (infoxx.getType() == 1) {
                           extract = VCoreEnforcement.getEnforceInfo(corexx.getLevel()).getExtract();
                        } else if (infoxx.getType() == 2) {
                           extract = VCoreEnforcement.getSpecialInfo(corexx.getLevel()).getExtract();
                        }

                        quantityx += extract;
                     }
                  }
               }

               coreList.forEach(c.getPlayer()::removeVCoreSkillsNoLock);
               c.getPlayer().gainPieceOfCore(quantityx);
               c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 5, 0));
               c.getSession().writeAndFlush(CWvsContext.UpdateCoreQuantity(quantityx));
               c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
               break;
            } finally {
               c.getPlayer().unlockVCoreSkillsReadLock();
            }
         case 7:
            int coreID = slea.readInt();
            int size = slea.readInt();
            VCoreData.VCoreInfo infox = VCoreData.getCoreInfo(coreID);
            if (infox == null) {
               return;
            }

            if (c.getPlayer().getVCoreSkillsNoLock().size() >= 500) {
               c.getPlayer().dropMessage(1, "스킬 코어를 더 이상 소지할 수 없습니다. 코어를 분해하거나 강화하여 공간을 확보해주시기 바랍니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }

            int quantity = VCoreData.getNeedMakePieceOfCore(infox.getType()) * size;
            if (c.getPlayer().getPieceOfCore() < quantity) {
               c.getPlayer().dropMessage(1, "V코어 조각이 부족합니다!");
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }

            c.getPlayer().gainPieceOfCore(-quantity);
            int[] connectSkill = VCoreData.getMainCoreSkill(coreID);

            for (int a = 0; a < size; a++) {
               int jobid = c.getPlayer().getJob();
               List<Integer> skillList = VCoreData.generateConnectSkillByJob(jobid);
               connectSkill = VCoreData.getMainCoreSkill(coreID);
               if (connectSkill[1] == 0 && coreID >= 20000000 && coreID < 30000000) {
                  for (int i = 1; i < 3; i++) {
                     int count = 0;

                     do {
                        int skill = skillList.get(Randomizer.nextInt(skillList.size()));
                        boolean exist = false;

                        for (int next = 0; next < i; next++) {
                           if (skill == connectSkill[next]) {
                              exist = true;
                              break;
                           }
                        }

                        if (!exist) {
                           connectSkill[i] = skill;
                           break;
                        }
                     } while (++count > 10000);
                  }
               }

               VSpecialCoreOption specialCoreOption = VCoreData.getSpecialCoreOption(coreID);
               long availableTime = -1L;
               if (specialCoreOption != null) {
                  availableTime = System.currentTimeMillis() + 1209600000L;
               }

               corexx = new VCore(
                  System.currentTimeMillis(),
                  coreID,
                  c.getPlayer().getId(),
                  1,
                  0,
                  1,
                  connectSkill[0],
                  connectSkill[1],
                  connectSkill[2],
                  specialCoreOption,
                  availableTime,
                  -1,
                  false
               );
               c.getPlayer().addVCoreSkills(corexx);
               c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 13, 0));
            }

            VSpecialCoreOption specialCoreOption = VCoreData.getSpecialCoreOption(coreID);
            long availableTime = -1L;
            if (specialCoreOption != null) {
               availableTime = System.currentTimeMillis() + 1209600000L;
            }

            corexx = new VCore(
               System.currentTimeMillis(),
               coreID,
               c.getPlayer().getId(),
               1,
               0,
               1,
               connectSkill[0],
               connectSkill[1],
               connectSkill[2],
               specialCoreOption,
               availableTime,
               -1,
               false
            );
            c.getSession().writeAndFlush(CWvsContext.MakeCoreResult(corexx, size));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
            break;
         case 9:
            int slotIndexx = slea.readInt();
            slea.skip(4);
            VMatrixSlot slotx = c.getPlayer().getVMatrixSlot(slotIndexx);
            int totalPoint = c.getPlayer().getLevel() - 200;
            int usingPoint = 0;

            for (VMatrixSlot slots : new ArrayList<>(c.getPlayer().getVMatrixSlots())) {
               usingPoint += slots.getSlotEnforcement();
            }

            if (totalPoint - usingPoint < 1) {
               System.out.println("매트릭스 포인트 부족. (캐릭터 이름 : " + c.getPlayer().getName() + ")");
               return;
            }

            if (slotx.getSlotEnforcement() >= VMatrixOption.info.equipSlotEnhanceMax) {
               System.out.println("최대 슬롯 강화를 달성하였지만 슬롯 강화 시도 (캐릭터 이름 : " + c.getPlayer().getName() + ")");
               return;
            }

            slotx.setSlotEnforcement(slotx.getSlotEnforcement() + 1);
            c.getPlayer().updateMatrixSkillsNoLock();
            c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 10, 0));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX_SLOTS.getFlag());
            break;
         case 10:
            slotIndexx = slea.readInt();
            slea.skip(4);
            slotx = c.getPlayer().getVMatrixSlot(slotIndexx);
            if (slotx.getReleased() != 0 || slotx.getIndex() < 4) {
               c.getPlayer().dropMessage(1, "이미 개방된 슬롯이거나 개방할 수 없는 슬롯입니다. (캐릭터 이름 : " + c.getPlayer().getName() + ")");
               return;
            }

            int userLevel = c.getPlayer().getLevel();
            int reqLevel = 200 + (slotx.getIndex() + 1 - VMatrixOption.info.equipSlotMin) * VMatrixOption.info.extendLevel;
            if (reqLevel > 300) {
               System.out.println("확장 불가능 슬롯. 만렙 초과 슬롯 (Index: " + slotIndexx + ") (캐릭터 이름 : " + c.getPlayer().getName() + ")");
               return;
            }

            VMatrixSlotExpensionMeso mesos = VMatrixOption.getSlotExpesionMeso(userLevel);
            if (mesos == null) {
               return;
            }

            int diffLevel = reqLevel - (userLevel + 1);
            int expansionIndex = diffLevel / VMatrixOption.info.extendLevel;
            long meso = mesos.getExpendedSlotMeso(expansionIndex);
            if (meso == -1L) {
               return;
            }

            if (meso > c.getPlayer().getMeso()) {
               c.getPlayer().dropMessage(1, "슬롯 개방 도중 메소가 부족합니다. (캐릭터 이름 : " + c.getPlayer().getName() + ")");
               return;
            }

            c.getPlayer().gainMeso(-meso, true);
            slotx.setReleased(1);
            c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 11, 0));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX_SLOTS.getFlag());
            break;
         case 11:
            usingPoint = 0;

            for (VMatrixSlot slotxxx : new ArrayList<>(c.getPlayer().getVMatrixSlots())) {
               usingPoint += slotxxx.getSlotEnforcement();
            }

            if (usingPoint == 0) {
               return;
            }

            if (c.getPlayer().getMeso() < VMatrixOption.info.matrixPointResetMeso) {
               c.getPlayer().dropMessage(1, "슬롯 강화 초기화 중 메소가 부족합니다. (캐릭터 이름 : " + c.getPlayer().getName() + ")");
               return;
            }

            c.getPlayer().gainMeso(-VMatrixOption.info.matrixPointResetMeso, true);

            for (VMatrixSlot slotxx : new ArrayList<>(c.getPlayer().getVMatrixSlots())) {
               slotxx.setSlotEnforcement(0);
            }

            c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 10, 0));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX_SLOTS.getFlag());
            break;
         case 13:
            side = slea.readInt();
            corexx = null;

            for (VCore vcorex : c.getPlayer().getVCoreSkillsNoLock()) {
               if (vcorex.getIndex() == side) {
                  corexx = vcorex;
                  break;
               }
            }

            c.getPlayer().sortedVCoreSkillsReadLock();
            if (corexx == null) {
               return;
            }

            corexx.setLocked(true);
            c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 14, 0));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
            break;
         case 14:
            side = slea.readInt();
            String password = slea.readMapleAsciiString();
            if (!c.getSecondPassword().equals(password)) {
               c.getPlayer().dropMessage(1, "2차 비밀번호를 확인해 주세요.");
               return;
            }

            corexx = null;

            for (VCore vcorexx : c.getPlayer().getVCoreSkillsNoLock()) {
               if (vcorexx.getIndex() == side) {
                  corexx = vcorexx;
                  break;
               }
            }

            c.getPlayer().sortedVCoreSkillsReadLock();
            if (corexx == null) {
               return;
            }

            corexx.setLocked(false);
            c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 15, 0));
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
      }
   }

   public static VCoreEnforcement.EnforceInfo getEnforceInfo(int type, int level) {
      VCoreEnforcement.EnforceInfo eInfo = null;
      switch (type) {
         case 0:
            eInfo = VCoreEnforcement.getSkillInfo(level);
            break;
         case 1:
            eInfo = VCoreEnforcement.getEnforceInfo(level);
            break;
         case 2:
            eInfo = VCoreEnforcement.getSpecialInfo(level);
      }

      return eInfo;
   }

   public static void UserThrowingBombAction(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int skillID = slea.readInt();

         try {
            if (GameConstants.isPathFinder(chr.getJob())) {
               chr.checkPathfinderPattern(skillID);
            }

            if (GameConstants.isKain(chr.getJob())) {
               if (GameConstants.isKainStackSkill(skillID)) {
                  KainStackSkill kss = chr.getKainStackSKill();
                  if (kss != null) {
                     kss.decrementStack(skillID);
                  }
               }

               if (GameConstants.isPossessSkill(skillID)) {
                  chr.onPossessSkill(skillID);
               }
            }

            if (GameConstants.isZero(chr.getJob()) && chr.hasBuffBySkillID(Zero.ZeroSkill.Transcendent_Time.getSkillID()) && skillID == 400001068) {
               int skillLevel = slea.readInt();
               slea.skip(96);
               List<Integer> idx = new ArrayList<>();
               idx.add(slea.readInt());
               c.getPlayer().send(CField.userThrowingBombAck(skillID, (byte)skillLevel, idx));
               return;
            }

            switch (skillID) {
               case 151101001:
                  SecondaryStatEffect effect = SkillFactory.getSkill(151101001).getEffect(chr.getTotalSkillLevel(151101001));
                  if (effect != null) {
                     chr.addEtherPoint(-effect.getY());
                  }
                  break;
               case 400011004: {
                  SecondaryStatEffect e = chr.getSkillLevelData(400011004);
                  chr.temporaryStatSet(400011004, e.getW2(), SecondaryStatFlag.indieDamReduceR, e.getW());
                  break;
               }
               case 400021048:
                  chr.invokeJobMethod("givePPoint", 400021048, true, (byte)0);
                  break;
               case 400031056: {
                  Integer value = chr.getBuffedValue(SecondaryStatFlag.RepeatingCrossbowCartridge);
                  if (value == null) {
                     return;
                  }

                  SecondaryStatEffect e = chr.getBuffedEffect(SecondaryStatFlag.RepeatingCrossbowCartridge);
                  if (e == null) {
                     return;
                  }

                  if (value <= 1) {
                     chr.temporaryStatReset(SecondaryStatFlag.RepeatingCrossbowCartridge);
                  } else {
                     value = value - 1;
                     SecondaryStatManager statManager = new SecondaryStatManager(chr.getClient(), chr.getSecondaryStat());
                     statManager.changeStatValue(SecondaryStatFlag.RepeatingCrossbowCartridge, 400031055, value);
                     statManager.temporaryStatSet();
                  }
                  break;
               }
               case 400051003:
                  if (chr.getViperEnergyOrb() <= 0) {
                     return;
                  }

                  effect = SkillFactory.getSkill(400051002).getEffect(chr.getTotalSkillLevel(400051002));
                  chr.setViperEnergyOrb(chr.getViperEnergyOrb() - 1);
                  SecondaryStatManager statManager = new SecondaryStatManager(chr.getClient(), chr.getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.Transform, 400051002, chr.getViperEnergyOrb());
                  statManager.temporaryStatSet();
                  Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
                  flags.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                  flags.put(SecondaryStatFlag.indieFlyAcc, 1);
                  chr.temporaryStatSet(400051003, chr.getTotalSkillLevel(400051002), 1020, flags);
                  break;
               case 400051008: {
                  long savepos = slea.getPosition();
                  slea.skip(32);
                  boolean allUse = slea.readByte() == 1;
                  slea.seek(savepos);
                  int value = chr.getBuffedValueDefault(SecondaryStatFlag.AutoChargeStack, 0);
                  chr.temporaryStatSet(400051008, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, allUse ? 0 : value - 1);
               }
            }

            if (skillID != 400021048
               && skillID != 400041059
               && skillID != 400041060
               && !chr.checkSpiritFlow(skillID)
               && !GameConstants.isKeydownEndCooltimeSkill(skillID)) {
               if (skillID == 12120023) {
                  skillID = 12121002;
               }

               SecondaryStatEffect effect = chr.getSkillLevelData(skillID);
               if (effect != null) {
                  effect.applyTo(chr);
                  chr.addCooldown(skillID, System.currentTimeMillis(), effect.getCooldown(chr));
                  c.getSession().writeAndFlush(CField.skillCooldown(skillID, effect.getCooldown(chr)));
               }
            }

            int skillLevel = slea.readInt();
            TeleportAttackAction teleportAction = TeleportAttackAction.fromRemote(slea);
            int action = slea.readInt();
            int actionSpeed = slea.readInt();
            byte usingConsumeItemID = slea.readByte();
            int consumeItemID = slea.readInt();
            slea.skip(4);
            slea.skip(1);
            slea.skip(4);
            slea.skip(1);
            slea.skip(1);
            new Point(slea.readShort(), slea.readShort());
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.USER_THROWING_BOMB_ACTION.getValue());
            packet.writeInt(chr.getId());
            packet.writeInt(skillID);
            packet.writeInt(skillLevel);
            packet.writeInt(action);
            packet.writeInt(actionSpeed);
            packet.write(usingConsumeItemID);
            packet.writeInt(consumeItemID);
            boolean d = slea.readByte() == 1;
            packet.write(d);
            if (d) {
               AdditionalThrowingBombAction(packet, slea);
            }

            slea.readInt();
            slea.skip(16);
            slea.readInt();
            slea.readInt();
            slea.readInt();
            if (skillID == 154121001) {
               packet.writeInt(chr.getPosition().x);
               packet.writeInt(chr.getPosition().y);
               packet.write(chr.isFacingLeft());
            }

            int angleSize = slea.readInt();
            packet.writeInt(angleSize);
            List<Integer> idxs = new LinkedList<>();

            for (int i = 0; i < angleSize; i++) {
               ThrowingBombInfo(packet, slea, idxs);
            }

            chr.getMap().broadcastMessage(packet.getPacket());
            if (skillID != 152001002 && skillID != 152120003) {
               chr.getMap().broadcastMessage(chr, CField.userThrowingBombAck(skillID, skillLevel, idxs), true);
            } else {
               int blessMarkSKillID = chr.getBlessMarkSkillID();
               SecondaryStatEffect blessMark = SkillFactory.getSkill(blessMarkSKillID).getEffect(chr.getTotalSkillLevel(blessMarkSKillID));
               chr.applyBlessMark(blessMark, 1, false, 0);
               SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(chr.getTotalSkillLevel(skillID));
               if (effect != null) {
                  effect.applyTo(chr);
               }

               chr.getMap().broadcastMessage(chr, CField.userThrowingBombAck(skillID, skillLevel, idxs), true);
               PostSkillEffect e_ = new PostSkillEffect(chr.getId(), skillID, chr.getTotalSkillLevel(skillID), null);
               chr.send(e_.encodeForLocal());
               chr.getMap().broadcastMessage(chr, e_.encodeForRemote(), false);
            }
         } catch (Exception var19) {
            System.out.println("THROWING ERROR Skill : " + skillID);
            var19.printStackTrace();
         }
      }
   }

   static void AdditionalThrowingBombAction(PacketEncoder packet, PacketDecoder slea) {
      packet.writeInt(slea.readInt());
      packet.writeInt(slea.readInt());
      packet.writeInt(slea.readInt());
      packet.writeInt(slea.readInt());
      packet.writeInt(slea.readInt());
      packet.writeInt(slea.readInt());
      packet.write(slea.readByte());
      packet.writeInt(slea.readInt());
      packet.write(slea.readByte());
      packet.writeLong(slea.readLong());
   }

   static void ThrowingBombInfo(PacketEncoder packet, PacketDecoder slea, List<Integer> idxs) {
      packet.writeInt(slea.readInt());
      packet.writeInt(slea.readInt());
      int idx = slea.readInt();
      idxs.add(idx);
      packet.writeInt(idx);
      packet.writeInt(slea.readInt());
      packet.writeShort(slea.readShort());
      packet.writeShort(slea.readShort());
      packet.writeShort(slea.readShort());
      packet.writeShort(slea.readShort());
      packet.writeShort(slea.readShort());
      packet.writeInt(slea.readInt());
      packet.write(slea.readByte());
      byte unkbool1 = slea.readByte();
      packet.write(unkbool1);
      if (unkbool1 != 0) {
         packet.writeInt(slea.readInt());
         packet.writeInt(slea.readInt());
      }

      byte unkbool2 = slea.readByte();
      packet.write(unkbool2);
      if (unkbool2 != 0) {
         packet.writeInt(slea.readInt());
         packet.writeInt(slea.readInt());
      }
   }

   public static void VCoreMakeJamStoneRequest(PacketDecoder packet, MapleClient client) {
      NPCScriptManager.getInstance().start(client, 1540945, "CoreJamStone", true);
      client.getSession().writeAndFlush(VCoreRequest());
   }

   public static byte[] VCoreRequest() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.VCORE_REQUEST.getValue());
      p.write(1);
      return p.getPacket();
   }
}
