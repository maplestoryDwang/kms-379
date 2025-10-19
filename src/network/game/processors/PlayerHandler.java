package network.game.processors;

import constants.GameConstants;
import constants.HexaMatrixConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import constants.undefinedIDA;
import constants.devtempConstants.MapleDailyGift;
import constants.devtempConstants.MapleDailyGiftInfo;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import database.loader.CharacterSaveFlag2;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.awt.Rectangle;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.sql.rowset.serial.SerialBlob;
import logging.LoggingManager;
import logging.entry.CabinetLog;
import logging.entry.CabinetLogType;
import logging.entry.CreateCharLog;
import logging.entry.CreateCharLogType;
import logging.entry.EnchantLog;
import logging.entry.EnchantLogType;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.center.Center;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.login.LoginInformationProvider;
import network.models.CField;
import network.models.CSPacket;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.androids.Android;
import objects.context.SpecialSunday;
import objects.context.guild.Guild;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.context.party.PartyOperation;
import objects.context.waitqueue.WaitQueueRequest;
import objects.effect.EffectHeader;
import objects.effect.child.HPHeal;
import objects.effect.child.IncDecHP;
import objects.effect.child.PostSkillEffect;
import objects.effect.child.SkillEffect;
import objects.effect.child.SpecialSkillEffect;
import objects.effect.child.UseWheel;
import objects.fields.AllowedDirectionFieldMan;
import objects.fields.CustomChair;
import objects.fields.Field;
import objects.fields.FieldLimitType;
import objects.fields.ForceAtom;
import objects.fields.Grenade;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.Portal;
import objects.fields.SecondAtom;
import objects.fields.child.blackmage.BlackMageOrcaAttackEntry;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase3;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase4;
import objects.fields.child.demian.Field_Demian;
import objects.fields.child.dreambreaker.Field_DreamBreaker;
import objects.fields.child.fritto.Field_CourtshipDance;
import objects.fields.child.fritto.Field_EagleHunt;
import objects.fields.child.hillah.Field_Hillah;
import objects.fields.child.jinhillah.Field_JinHillah;
import objects.fields.child.lucid.Field_LucidBattle;
import objects.fields.child.minigame.rail.Field_ExtremeRail;
import objects.fields.child.minigame.space.Field_Mission2Space;
import objects.fields.child.minigame.topgame.Field_BuzzingHouse;
import objects.fields.child.minigame.train.Field_TrainMaster;
import objects.fields.child.minigame.yutgame.Field_MultiYutGame;
import objects.fields.child.minigame.yutgame.MultiYutGameDlg;
import objects.fields.child.minigame.yutgame.YutGameResult_GameInfo;
import objects.fields.child.minigame.yutgame.YutGameSuperItem;
import objects.fields.child.moonbridge.Field_FerociousBattlefield;
import objects.fields.child.muto.FoodType;
import objects.fields.child.muto.HungryMuto;
import objects.fields.child.sernium.Field_SerenPhase2;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.child.will.SpiderWeb;
import objects.fields.fieldset.instance.MulungForest;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.FieldAttackObj;
import objects.fields.gameobject.lifes.AttackIndex;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobAttackInfo;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.OverrideMonsterStats;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.item.DamageSkinSaveData;
import objects.item.DamageSkinSaveInfo;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.movepath.LifeMovementFragment;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.AntiMacro;
import objects.users.AntiMacroFailedType;
import objects.users.AntiMacroType;
import objects.users.BlackList;
import objects.users.MapleCabinet;
import objects.users.MapleCabinetItem;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.users.MapleMessage;
import objects.users.MapleStat;
import objects.users.PlayerStats;
import objects.users.WorldLvChairInfo;
import objects.users.achievement.AchievementEntry;
import objects.users.achievement.AchievementFactory;
import objects.users.enchant.InnerAbility;
import objects.users.enchant.ItemStateFlag;
import objects.users.jobs.BasicJob;
import objects.users.jobs.anima.Lara;
import objects.users.looks.mannequin.Mannequin;
import objects.users.looks.mannequin.MannequinEntry;
import objects.users.looks.mannequin.MannequinRequestType;
import objects.users.looks.mannequin.MannequinResultType;
import objects.users.looks.mannequin.MannequinType;
import objects.users.looks.zero.ZeroInfo;
import objects.users.looks.zero.ZeroInfoFlag;
import objects.users.potential.CharacterPotentialHolder;
import objects.users.skills.DamageParse;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.JupiterThunder;
import objects.users.skills.KainStackSkill;
import objects.users.skills.LinkSkill;
import objects.users.skills.PsychicArea;
import objects.users.skills.PsychicLock;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.users.skills.SkillMacro;
import objects.users.skills.TeleportAttackAction;
import objects.users.skills.VCore;
import objects.users.stats.HexaCore;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.CollectionUtil;
import objects.utils.CurrentTime;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Timer;
import objects.utils.Triple;
import scripting.EventInstanceManager;
import scripting.newscripting.ScriptManager;
import security.anticheat.CheatingOffense;

public class PlayerHandler {
   private static final int MERGE_SYMBOL_ONE = 0;
   private static final int UPGRADE_SYMBOL = 1;
   private static final int MERGE_SYMBOL_ALL = 2;

   public static void ChangeSkillMacro(PacketDecoder slea, MapleCharacter chr) {
      int num = slea.readByte();

      for (int i = 0; i < num; i++) {
         String name = slea.readMapleAsciiString();
         int shout = slea.readByte();
         int skill1 = slea.readInt();
         int skill2 = slea.readInt();
         int skill3 = slea.readInt();
         SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
         chr.updateMacros(i, macro);
      }
   }

   public static final void ChangeKeymap(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null) {
         int mode = slea.readByteToInt();
         if (mode == 0) {
            int index = slea.readByteToInt();
            int numChanges = slea.readByteToInt();

            for (int i = 0; i < numChanges; i++) {
               int key = slea.readByteToInt();
               byte type = slea.readByte();
               int action = slea.readInt();
               if (type == 1 && action >= 1000 && action != 400051001) {
                  Skill skil = SkillFactory.getSkill(action);
                  if (skil != null
                     && (
                        !skil.isFourthJob() && !skil.isBeginnerSkill() && skil.isInvisible() && chr.getSkillLevel(skil) <= 0
                           || action >= 91000000 && action < 100000000
                     )
                     && !GameConstants.isLinkSkill(skil.getId())
                     && !GameConstants.isTheSeedSkill(skil.getId())) {
                     continue;
                  }
               }

               if (action != 26) {
                  chr.changeKeybinding(index, key, type, action);
               }
            }
         } else if (mode == 1) {
            int index = slea.readInt();
            if (index <= 0) {
               chr.getQuestRemove(MapleQuest.getInstance(122221));
            } else {
               chr.getQuestIfNullAdd(MapleQuest.getInstance(122221)).setCustomData(String.valueOf(index));
            }
         } else if (mode == 2) {
            int index = slea.readInt();
            if (index <= 0) {
               chr.getQuestRemove(MapleQuest.getInstance(122223));
            } else {
               chr.getQuestIfNullAdd(MapleQuest.getInstance(122223)).setCustomData(String.valueOf(index));
            }
         } else if (mode == 3) {
            int index = slea.readByteToInt();
            chr.updateOneInfo(100972, "no", String.valueOf(index));
         }
      }
   }

   public static void requestUpgradeTombEffect(PacketDecoder in, MapleClient c, MapleCharacter player) {
      int itemID = in.readInt();
      int posX = in.readInt();
      int posY = in.readInt();
      if (player != null) {
         PacketEncoder o = new PacketEncoder();
         o.writeShort(SendPacketOpcode.SHOW_UPGRADE_TOMB_EFFECT.getValue());
         o.writeInt(player.getId());
         o.writeInt(itemID);
         o.writeInt(posX);
         o.writeInt(posY);
         player.getMap().broadcastMessage(player, o.getPacket(), false);
      }
   }

   public static final void ActivateNickItem(int itemID, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemID);
         if (itemID == 0 || toUse != null) {
            MapleQuest quest = MapleQuest.getInstance(7290);
            MapleQuestStatus queststatus = new MapleQuestStatus(quest, 1);
            queststatus.setCustomData(String.valueOf(itemID));
            c.getPlayer().updateQuest(queststatus);
            chr.setActiveNickItem(itemID);
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), false));
         }
      }
   }

   public static final void UseChair(PacketDecoder slea, MapleClient c, final MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         final int itemId = slea.readInt();
         Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
         if (toUse == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
         } else {
            if (!DBConfig.isGanglim && ServerConstants.useChairFishing && chr.getMapId() == ServerConstants.chairFishingMapID) {
               chr.startFishingTask();
            }

            if (DBConfig.isGanglim && ServerConstants.isRoyalFishingMap(chr.getMapId())) {
               chr.startChairTask();
            }

            int slot = slea.readInt();
            slea.skip(1);
            slea.skip(4);
            int posX = slea.readInt();
            int posY = slea.readInt();
            boolean isHonorChair = itemId / 1000 == 3014;
            String message = null;
            int meso = 0;
            if (isHonorChair) {
               message = slea.readMapleAsciiString();
               chr.setChairText(message);
            }

            if (itemId == 3018996) {
               chr.clearCodyVote();
            }

            WorldLvChairInfo worldLvChair = null;
            if (slea.available() > 0L && slea.readByte() == 1) {
               if (itemId == 3018465) {
                  int floor = slea.readInt();
                  chr.setDojangChairFloor(floor);
               } else {
                  worldLvChair = new WorldLvChairInfo();
                  worldLvChair.decode(slea);
               }
            }

            chr.setChairInfo(worldLvChair);
            chr.setChair(itemId);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int itemID = chr.getChair();
            String type = ii.getChairType(itemID);
            if (!type.equals("popChair")) {
               chr.getMap().broadcastMessage(CField.showChair(chr, itemId, message, chr.getMesoChairCount()));
            }

            if (ii.getChairTamingMob(itemID) > 0 && itemID != 3015564) {
               chr.temporaryStatSet(80002894, Integer.MAX_VALUE, SecondaryStatFlag.RideVehicle, ii.getChairTamingMob(itemID));
            }

            if (itemId == 3015440) {
               meso = slea.readInt();
               chr.setMaxMesoChairCount(meso);
               if (chr.getMesoChairCount() > 0L) {
                  chr.getClient().getSession().writeAndFlush(CField.setMesoChairCount(chr.getId(), itemId, chr.getMesoChairCount()));
               }

               Timer.BuffTimer timer = Timer.BuffTimer.getInstance();
               chr.registerMesoChairTask(timer.register(new Runnable() {
                  @Override
                  public void run() {
                     if (chr.getMesoChairCount() >= 999999999L) {
                        chr.stopMesoChairTask();
                     }

                     if (chr.getMesoChairCount() < 499L) {
                        chr.gainMeso(-499L, true);
                        chr.setMesoChairCount(499L);
                        chr.getMap().broadcastMessage(CField.setMesoChairCount(chr.getId(), itemId, 499L));
                     } else {
                        long mesox = chr.getMesoChairCount() + 500L;
                        if (mesox > chr.getMeso()) {
                           chr.stopMesoChairTask();
                        }

                        if (chr.getMaxMesoChairCount() >= mesox) {
                           chr.setMesoChairCount(chr.getMesoChairCount() + 500L);
                           chr.gainMeso(-500L, true);
                           chr.getMap().broadcastMessage(CField.setMesoChairCount(chr.getId(), itemId, 500L));
                        } else {
                           chr.stopMesoChairTask();
                        }
                     }
                  }
               }, 2000L));
            }

            if (itemID / 1000 == 3016) {
               List<MapleCharacter> list = new ArrayList<>();
               list.add(chr);
               int[] randEmotions = new int[]{2, 10, 14, 17};
               chr.setChairEmotion(randEmotions[Randomizer.nextInt(randEmotions.length)]);
               CustomChair chair = new CustomChair(chr, itemID, list, new Rect(-400, -265, 400, 265), new Point(posX, posY));
               chr.getMap().addCustomChair(chair);
               chr.setCustomChair(chair);
            }

            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      }
   }

   public static final void CancelChair(short id, MapleClient c, MapleCharacter chr) {
      if (chr != null) {
         if (id == -1) {
            if (chr.getChair() == 3010587) {
               AffectedArea area = chr.getMap().getMistBySkillId(36121007);
               if (area != null) {
                  chr.getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 36121007, false));
                  chr.getMap().removeMapObject(area);
               }
            }

            if (chr.getChair() / 1000 == 3016) {
               chr.setChairEmotion(0);
               if (chr.getCustomChair() != null && chr.getCustomChair().getOwner().getId() == chr.getId()) {
                  CustomChair chair = chr.getMap().getCustomChairByOwner(chr.getId());
                  chair.getPlayers().forEach(p -> {
                     p.setChair(0);
                     p.getMap().broadcastMessage(CField.cancelChair(-1, p));
                     p.getMap().broadcastMessage(p, CField.showChair(p, 0, "", 0L), false);
                     p.setCustomChair(null);
                  });
                  if (chair != null) {
                     chr.getMap().removeCustomChair(chr, chair);
                  }
               } else {
                  CustomChair chair = chr.getCustomChair();
                  chair.removePlayer(chr);
                  chr.getMap().broadcastMessage(CField.customChairResult(chr, false, true, false, chair));
               }
            }

            chr.setChair(0);
            chr.getMap().broadcastMessage(chr, CField.cancelChair(-1, chr), true);
            if (chr.getMap() != null) {
               chr.getMap().broadcastMessage(chr, CField.showChair(chr, 0, null, 0L), false);
            }
         } else {
            chr.setChair(0);
            c.getSession().writeAndFlush(CField.cancelChair(id, chr));
         }

         chr.setChairText(null);
         chr.setMaxMesoChairCount(0L);
         chr.stopMesoChairTask();
         if (chr.inFishing()) {
            chr.cancelFishingTask();
         }

         chr.cancelChairTask();
         chr.temporaryStatReset(SecondaryStatFlag.RideVehicle);
      }
   }

   public static final void updateCodyVoteChair(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int target = slea.readInt();
         MapleCharacter targetChr = chr.getMap().getCharacterById(target);
         if (targetChr != null) {
            if (targetChr.getChair() == 0) {
               return;
            }

            int unknown = slea.readInt();
            int vote = slea.readInt();
            int unknown2 = slea.readInt();
            if (targetChr.isAlreadyCodyVote(chr.getId())) {
               chr.dropMessage(1, "투표는 캐릭터가 앉아있는 동안 1회만 할 수 있습니다.");
            } else {
               targetChr.setCodyVote(chr.getId(), vote);
            }

            byte[] packet = CField.upcateCodyVoteChair(targetChr, unknown, unknown2);
            chr.getMap().broadcastMessage(packet);
         }
      }
   }

   public static final void TrockAddMap(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      byte addrem = slea.readByte();
      byte vip = slea.readByte();
      if (vip == 1) {
         if (addrem == 0) {
            chr.deleteFromRegRocks(slea.readInt());
         } else if (addrem == 1) {
            if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
               chr.addRegRockMap();
            } else {
               chr.dropMessage(1, "This map is not available to enter for the list.");
            }
         }
      } else if (vip == 2) {
         if (addrem == 0) {
            chr.deleteFromRocks(slea.readInt());
         } else if (addrem == 1) {
            if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
               chr.addRockMap();
            } else {
               chr.dropMessage(1, "This map is not available to enter for the list.");
            }
         }
      } else if (vip == 3) {
         if (addrem == 0) {
            chr.deleteFromHyperRocks(slea.readInt());
         } else if (addrem == 1) {
            if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
               chr.addHyperRockMap();
            } else {
               chr.dropMessage(1, "This map is not available to enter for the list.");
            }
         }
      }

      c.getSession().writeAndFlush(CSPacket.OnMapTransferResult(chr, vip, addrem == 0));
   }

   public static final void CharInfoRequest(int objectid, MapleClient c, MapleCharacter chr) {
      if (c.getPlayer() != null && c.getPlayer().getMap() != null) {
         MapleCharacter player = c.getPlayer().getMap().getCharacterById(objectid);
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         if (player != null && !player.isClone() && (!player.isGM() || c.getPlayer().isGM())) {
            c.getSession().writeAndFlush(CWvsContext.charInfo(player));
         }
      }
   }

   public static final void onUserHit(PacketDecoder slea, MapleClient c, MapleCharacter player) {
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            slea.skip(4);
            slea.skip(4);
            AttackIndex attackIndex = AttackIndex.get(slea.readByte());
            slea.skip(1);
            boolean onAetherGuard = slea.readByte() == 1;
            int damage = slea.readInt();
            int originalDamage = damage;
            byte dmgType = slea.readByte();
            slea.skip(1);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(4);
            slea.skip(1);
            slea.skip(4);
            boolean hitByMob = attackIndex.getIndex() >= AttackIndex.Mob_Body.getIndex();
            MapleMonster attacker = null;
            boolean hitByDead = attackIndex == AttackIndex.Dead;
            PlayerStats stats = player.getStat();
            boolean deadlyAttack = false;
            int mpattack = 0;
            int fixDamR = 0;
            int fixDamRType = 0;
            int diseaseID = 0;
            int diseaseLVL = 0;
            boolean powerGuard = false;
            int reflectMobID = 0;
            int reflectAction = 0;
            new Point(0, 0);
            new Point(0, 0);
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
            packet.writeInt(player.getId());
            packet.write(attackIndex.getIndex());
            packet.writeInt(damage);
            packet.write(dmgType);
            boolean overHP = false;
            int specialSkill = -1;
            if (damage == -1) {
               int job = player.getJob();
               if (job == 412) {
                  specialSkill = 4120002;
               } else if (job == 422) {
                  specialSkill = 4220002;
               } else if (job == 1412) {
                  specialSkill = 14120010;
               } else if (job == 2411 || job == 2412) {
                  specialSkill = player.getTotalSkillLevel(24120002) <= 0 ? 24110004 : 24120002;
               } else if (GameConstants.isXenon(job)) {
                  specialSkill = 36120005;
               } else if (GameConstants.isLuminous(job)) {
                  specialSkill = 27111005;
               } else if (GameConstants.isMercedes(job)) {
                  specialSkill = 23110004;
               } else if (GameConstants.isMechanic(job)
                  && player.getBuffedValue(SecondaryStatFlag.RideVehicle) != null
                  && player.getBuffedValue(SecondaryStatFlag.RideVehicle) == 1932016) {
                  specialSkill = 35120018;
               } else if (GameConstants.isKinesis(job)) {
                  specialSkill = 142001007;
               } else if (GameConstants.isKadena(job)) {
                  specialSkill = 64110014;
               } else if (job == 332) {
                  specialSkill = 3320011;
               } else if (player.getBuffedValue(SecondaryStatFlag.IllusionStep) != null && (job == 312 || job == 322)) {
                  specialSkill = player.getBuffedEffect(SecondaryStatFlag.IllusionStep).getSourceId();
               }

               if (job == 121 || job == 122) {
                  specialSkill = 1210001;
                  if (player.getTotalSkillLevel(1220006) > 0) {
                     SecondaryStatEffect eff = SkillFactory.getSkill(1220006).getEffect(player.getTotalSkillLevel(1220006));
                     attacker.applyStatus(
                        player, new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, 1220006, null, false), false, eff.getDuration(), true, eff
                     );
                     specialSkill = 1220006;
                  }
               }

               int slv = player.getTotalSkillLevel(specialSkill);
               if (specialSkill == 23110004 && slv == 0) {
                  slv = 1;
               }

               if (specialSkill == -1 || slv <= 0) {
                  return;
               }
            }

            if (damage > 0) {
               player.checkSpecialCoreSkills("hitCount", 0, null);
               player.temporaryStatResetBySkillID(80001479);
            }

            if (onAetherGuard) {
               SecondaryStatEffect eff = player.getSkillLevelData(151121004);
               if (eff != null) {
                  player.temporaryStatSet(151121011, eff.getSubTime() / 1000, SecondaryStatFlag.indiePartialNotDamaged, 1);
                  SpecialSkillEffect e = new SpecialSkillEffect(player.getId(), 151121004, null);
                  player.send(e.encodeForLocal());
                  player.getMap().broadcastMessage(player, e.encodeForRemote(), false);
               }
            }

            if (player.getBuffedValue(SecondaryStatFlag.EtherealForm) == null || !player.onEtherealForm()) {
               boolean guard = false;
               if (damage == 0) {
                  Pair<Double, Boolean> modify = player.modifyDamageTaken(damage, attacker);
                  damage = modify.left.intValue();
                  List<Integer> dodgeSkillList = new ArrayList<>();
                  if (player.getSkillLevel(4330009) > 0) {
                     dodgeSkillList.add(4330009);
                  }

                  if (player.getTotalSkillLevel(33110008) > 0) {
                     dodgeSkillList.add(33110008);
                  }

                  for (Integer skill : dodgeSkillList) {
                     SecondaryStatEffect eff = player.getSkillLevelData(skill);
                     if (eff != null && (skill == 13110026 || Randomizer.nextInt(100) < eff.getER())) {
                        PacketEncoder pw = new PacketEncoder();
                        pw.writeShort(SendPacketOpcode.DODGE_SKILL_READY.getValue());
                        player.send(pw.getPacket());
                        guard = true;
                        break;
                     }
                  }

                  Integer v = player.getBuffedValue(SecondaryStatFlag.HolyMagicShell);
                  if (v != null) {
                     if (v <= 0) {
                        player.temporaryStatReset(SecondaryStatFlag.HolyMagicShell);
                     } else {
                        SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                        statManager.changeStatValue(SecondaryStatFlag.HolyMagicShell, 2311009, v - 1);
                        statManager.temporaryStatSet();
                     }

                     guard = true;
                  }

                  v = player.getBuffedValue(SecondaryStatFlag.BlessingArmor);
                  if (v != null) {
                     if (v <= 0) {
                        player.temporaryStatReset(SecondaryStatFlag.BlessingArmor);
                     } else {
                        SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                        statManager.changeStatValue(SecondaryStatFlag.BlessingArmor, 1210016, v - 1);
                        statManager.temporaryStatSet();
                     }

                     guard = true;
                  }
               }

               packet.write(guard);
               packet.write(guard);
               if (hitByMob) {
                  int mobTemplateID = slea.readInt();
                  int objectID = slea.readInt();
                  attacker = map.getMonsterByOid(objectID);
                  int faceLeft = slea.readByte();
                  packet.writeInt(mobTemplateID);
                  packet.write(faceLeft);
                  packet.writeInt(objectID);
                  if (attacker == null
                     || attacker.getId() != mobTemplateID
                     || attacker.getLinkCID() > 0
                     || attacker.isFake()
                     || attacker.getStats().isFriendly()) {
                     return;
                  }

                  if (attackIndex.getIndex() == AttackIndex.Mob_Body.getIndex()) {
                     map.onUserHit(player, mobTemplateID, -1, -1, attackIndex.getIndex());
                  }

                  if (attackIndex.getIndex() >= AttackIndex.Mob_Attack1.getIndex()) {
                     map.onUserHit(player, mobTemplateID, -1, -1, attackIndex.getIndex());
                     MobAttackInfo attackInfo = attacker.getStats().getMobAttack(attackIndex.getIndex());
                     if (attackInfo != null) {
                        if (attackInfo.isElement && stats.TER > 0 && Randomizer.nextInt(100) < stats.TER) {
                           System.out.println("Avoided ER from mob id: " + mobTemplateID);
                           return;
                        }

                        if (attackInfo.isDeadlyAttack()) {
                           deadlyAttack = true;
                           mpattack = (int)(stats.getMp() - 1L);
                        } else {
                           mpattack += attackInfo.getMpBurn();
                        }

                        if (attackInfo.getFixDamR() > 0) {
                           fixDamR = attackInfo.getFixDamR();
                           fixDamRType = attackInfo.getFixDamRType();
                        }

                        MobSkillInfo skillx = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                        if (skillx != null && (damage == -1 || damage > 0)) {
                           skillx.applyEffect(player, attacker, null, false);
                           attacker.setLastSkillUsed(skillx, System.currentTimeMillis(), skillx.getCoolTime());
                        }

                        attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                     }
                  }

                  int skillID = slea.readInt();
                  int reflectDamage = slea.readInt();
                  boolean g = slea.readByte() == 1;
                  int reflect = slea.readByte();
                  packet.writeInt(skillID);
                  packet.writeInt(reflectDamage);
                  packet.write(g);
                  if (g) {
                     Skill bx = SkillFactory.getSkill(31110008);
                     int bof = player.getTotalSkillLevel(bx);
                     if (bof > 0) {
                        SecondaryStatEffect eff = bx.getEffect(bof);
                        int hp = (int)(eff.getY() * player.getStat().getCurrentMaxHp() * 0.01);
                        player.addHP(hp);
                        int delta = (int)(GameConstants.getMPByJob(c.getPlayer()) - c.getPlayer().getStat().getMp());
                        if (delta > 0) {
                           delta = Math.min(delta, eff.getZ());
                           c.getPlayer().addMP(delta, true);
                        }
                     }
                  }

                  if (powerGuard && skillID == 1201007) {
                     player.getTotalSkillLevel(1201007);
                  }

                  if (reflect == 2 || reflectDamage > 0) {
                     powerGuard = slea.readByte() > 0;
                     reflectMobID = slea.readInt();
                     int var44 = slea.readByte();
                     slea.skip(4);
                     Point ptHit = slea.readPos();
                     Point unkPos = slea.readPos();
                     if (reflectDamage > 0) {
                        MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(reflectMobID);
                        if (mob != null) {
                           mob.damage(c.getPlayer(), reflectDamage, true);
                           if (skillID == 31101003) {
                              SecondaryStatEffect e = SkillFactory.getSkill(skillID).getEffect(player.getTotalSkillLevel(skillID));
                              if (e != null && e.makeChanceResult()) {
                                 mob.applyStatus(
                                    player, new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, skillID, null, false), false, e.getSubTime(), true, e
                                 );
                              }
                           }
                        }

                        packet.write(powerGuard);
                        packet.writeInt(reflectMobID);
                        packet.write((int)var44);
                        packet.writeShort(ptHit.x);
                        packet.writeShort(ptHit.y);
                     }
                  }

                  int stance = slea.readByte();
                  int unk1 = slea.readInt();
                  int unk2 = slea.readInt();
                  packet.write(stance);
                  if ((stance & 1) != 0) {
                     packet.writeInt(player.getStance());
                  }
               } else if (attackIndex.getIndex() == AttackIndex.ObstacleAtom.getIndex()) {
                  int sn = slea.readInt();
                  fixDamR = 1;
               } else if (attackIndex.getIndex() == AttackIndex.FieldSkill.getIndex()) {
                  int unk3 = slea.readInt();
                  short var61 = slea.readShort();
               } else if (attackIndex.getIndex() == AttackIndex.MobSkill.getIndex()) {
                  int skillIDx = slea.readInt();
                  int skillLevel = slea.readInt();
                  int bounceObjectSN = slea.readInt();
                  map.onUserHit(player, -1, skillIDx, skillLevel, attackIndex.getIndex());
                  MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skillIDx, skillLevel);
                  if (mobSkillInfo != null) {
                     fixDamR = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.fixDamR);
                  }

                  packet.writeInt(skillIDx);
                  packet.writeInt(skillLevel);
                  packet.writeInt(bounceObjectSN);
                  byte unk = slea.readByte();
                  packet.write(unk);
               } else if (attackIndex.getIndex() == AttackIndex.PartsSystem.getIndex()) {
                  int mobID = slea.readInt();
                  int mobTemplateIDx = slea.readInt();
                  byte var89 = slea.readByte();
               } else if (attackIndex.getIndex() == AttackIndex.FieldEtc.getIndex()) {
                  int type = slea.readByte();
                  Point var64 = slea.readPos();
               } else if (attackIndex.getIndex() != AttackIndex.Dead.getIndex()) {
                  int var40 = slea.readByte();
                  int var41 = slea.readByte();
               }

               if (fixDamR > 0 && damage == 0) {
                  SecondaryStatEffect e = player.getBuffedEffect(SecondaryStatFlag.StormGuard);
                  if (e != null) {
                     Integer vx = player.getBuffedValue(SecondaryStatFlag.StormGuard);
                     if (vx != null) {
                        damage = 0;
                        if (vx <= fixDamR) {
                           player.temporaryStatReset(SecondaryStatFlag.StormGuard);
                        } else {
                           vx = vx - fixDamR;
                           SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                           statManager.changeStatValue(SecondaryStatFlag.StormGuard, e.getSourceId(), vx);
                           statManager.temporaryStatSet();
                        }
                     }
                  }
               }

               overHP = fixDamR == 100 && originalDamage >= stats.getCurrentMaxHp();
               boolean canReduceDam = false;
               if (fixDamR == 0 || attackIndex == AttackIndex.Mob_Body) {
                  canReduceDam = true;
               }

               if (damage > 0) {
                  if (player.getMap() instanceof Field_WillBattle) {
                     if (attackIndex.equals(AttackIndex.Mob_Attack5)) {
                        Field_WillBattle f = (Field_WillBattle)player.getMap();
                        player.setWillMoonGaugeUpdateableTime(System.currentTimeMillis() + 3000L);
                     }

                     if (attackIndex.equals(AttackIndex.Mob_Attack6)) {
                        Field_WillBattle f = (Field_WillBattle)player.getMap();
                        player.setWillMoonGauge(Math.max(0, player.getWillMoonGauge() - 3));
                        f.sendWillUpdateMoonGauge(player, player.getWillMoonGauge());
                     }
                  }

                  if (player.getMap() instanceof Field_FerociousBattlefield && player.getBuffedValue(SecondaryStatFlag.DuskDarkness) == null) {
                     Field_FerociousBattlefield f = (Field_FerociousBattlefield)player.getMap();
                     f.setDuskGaugeByOnHit(player);
                  }

                  if (player.getMap() instanceof Field_Hillah) {
                     MapleMonster monster = player.getMap().getMonsterById(8870100);
                     if (monster != null && monster.isVampireState()) {
                        monster.heal((long)(monster.getMobMaxHp() * 0.05), 0, false);
                     }
                  }

                  SecondaryStatEffect e = player.getBuffedEffect(SecondaryStatFlag.StackDamR);
                  if (e != null) {
                     Integer vx = player.getBodyOfSteal();
                     if (vx < e.getY()) {
                        player.setBodyOfSteal(vx + 1);
                        SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                        statManager.changeStatValue(SecondaryStatFlag.StackDamR, 400011066, 1);
                        statManager.changeStatValue(SecondaryStatFlag.indieSuperStance, 400011066, 1);
                        statManager.changeStatValue(SecondaryStatFlag.indieAsrR, 400011066, e.getIndieAsrR());
                        statManager.temporaryStatSet();
                     }
                  }

                  if (!hitByDead) {
                     if (GameConstants.isBlaster(player.getJob()) && (fixDamR < 100 || fixDamRType == 1)) {
                        Integer value = player.getBuffedValue(SecondaryStatFlag.RWBarrier);
                        if (value != null) {
                           int reduceDam = value;
                           if (reduceDam > damage) {
                              reduceDam = damage;
                           }

                           damage -= reduceDam;
                           if (damage <= 0) {
                              damage = 1;
                           }

                           if (reduceDam >= value) {
                              player.temporaryStatReset(SecondaryStatFlag.RWBarrier);
                           } else {
                              SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                              statManager.changeStatValue(SecondaryStatFlag.RWBarrier, 37000006, value - reduceDam);
                              statManager.temporaryStatSet();
                           }
                        }

                        player.onSetRWBarrier(originalDamage);
                     }

                     if (fixDamR == 0 || attackIndex == AttackIndex.Mob_Body) {
                        Pair<Double, Boolean> modifyx = player.modifyDamageTaken(damage, attacker);
                        damage = modifyx.left.intValue();
                        if (GameConstants.isBlaster(player.getJob())) {
                           if (player.getBuffedValue(SecondaryStatFlag.RWBarrierHeal) != null) {
                              SecondaryStatEffect effect = player.getBuffedEffect(SecondaryStatFlag.RWBarrierHeal);
                              if (effect != null) {
                                 int reduceDamx = (int)Math.round(effect.getX() * (damage * 0.01));
                                 damage = reduceDamx;
                              }
                           } else if (c.getPlayer().getTotalSkillLevel(37120011) > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(37120011).getEffect(c.getPlayer().getTotalSkillLevel(37120011));
                              if (effect != null) {
                                 int reduceDamx = (int)(damage * 0.01 * effect.getDamAbsorbShieldR());
                                 damage -= reduceDamx;
                              }
                           }

                           if (player.getBuffedValue(SecondaryStatFlag.RWMagnumBlow) != null) {
                              SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.RWMagnumBlow);
                              if (eff != null) {
                                 Integer value = player.getBuffedValue(SecondaryStatFlag.RWMagnumBlow);
                                 int vx = 0;
                                 if (value != null) {
                                    vx = value;
                                 }

                                 int rwMagnumBlow = (Integer)player.getJobField("rwMagnumBlow");
                                 player.invokeJobMethod("setRWMagnumBlow", Math.min(rwMagnumBlow + 1, eff.getY() / eff.getX()));
                                 player.temporaryStatSet(37121052, eff.getSubTime(), SecondaryStatFlag.RWMagnumBlow, vx);
                              }
                           }
                        }

                        if (player.getStat().dodgeChance > 0 && Randomizer.nextInt(100) < player.getStat().dodgeChance) {
                           IncDecHP eff = new IncDecHP(player.getId(), 0);
                           c.getSession().writeAndFlush(eff.encodeForLocal());
                           player.getMap().broadcastMessage(player, eff.encodeForRemote(), false);
                           return;
                        }

                        int[] skills = new int[]{1000003, 2300003, 152000006, 155100010, 15512001, 33101005, 162000006};

                        for (int skillx : skills) {
                           int lev = player.getTotalSkillLevel(skillx);
                           if (lev > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(skillx).getEffect(lev);
                              if (effect != null) {
                                 int reduceDamx = (int)Math.ceil(originalDamage * 0.01 * effect.getDamAbsorbShieldR());
                                 damage -= reduceDamx;
                                 break;
                              }
                           }
                        }

                        skills = new int[]{151000005, 151110007, 5200009, 63000007, 63120014};

                        for (int skillxx : skills) {
                           int lev = player.getTotalSkillLevel(skillxx);
                           if (lev > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(skillxx).getEffect(lev);
                              if (effect != null) {
                                 int reduceDamx = (int)(originalDamage * 0.01 * effect.getDamAbsorbShieldR());
                                 damage -= reduceDamx;
                              }
                           }
                        }

                        skills = new int[]{110, 264, 265};

                        for (int skillxxx : skills) {
                           int lev = player.getTotalSkillLevel(skillxxx);
                           if (lev > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(skillxxx).getEffect(lev);
                              if (effect != null) {
                                 int reduceDamx = (int)(originalDamage * 0.01 * effect.getDamAbsorbShieldR());
                                 damage -= reduceDamx;
                              }
                           }
                        }

                        skills = new int[]{80000000, 80002775, 80002776};

                        for (int skillxxxx : skills) {
                           int lev = player.getTotalSkillLevel(skillxxxx);
                           if (lev > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(skillxxxx).getEffect(lev);
                              if (effect != null) {
                                 int reduceDamx = (int)(originalDamage * 0.01 * effect.getQ2());
                                 damage -= reduceDamx;
                              }
                           }
                        }

                        if (player.hasBuffBySkillID(1101006)) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(1101006).getEffect(player.getSkillLevel(1101006));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getX());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(15110024) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(15110024).getEffect(c.getPlayer().getTotalSkillLevel(15110024));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getDamAbsorbShieldR());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(20050285) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(20050285).getEffect(c.getPlayer().getTotalSkillLevel(20050285));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getDamAbsorbShieldR());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(33111011) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(33111011).getEffect(c.getPlayer().getTotalSkillLevel(33111011));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getDamAbsorbShieldR());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(35120018) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(35120018).getEffect(c.getPlayer().getTotalSkillLevel(35120018));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getX());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().getGuild() != null) {
                           int gSLV = Center.Guild.getSkillLevel(c.getPlayer().getGuildId(), 91000026);
                           if (gSLV > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(91000026).getEffect(gSLV);
                              if (effect != null) {
                                 int reduceDamx = (int)Math.ceil(originalDamage * 0.01 * effect.getDamAbsorbShieldR());
                                 damage -= reduceDamx;
                              }
                           }
                        }

                        if (player.getTotalSkillLevel(152100011) > 0 && damage > 0) {
                           Summoned summon = player.getSummonByMovementType(SummonMoveAbility.SHADOW_SERVANT_EXTEND);
                           if (summon != null) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(152100011).getEffect(player.getTotalSkillLevel(152100011));
                              if (effect != null) {
                                 PostSkillEffect e_ = new PostSkillEffect(player.getId(), 0, 1, null);
                                 player.send(e_.encodeForLocal());
                                 player.getMap().broadcastMessage(player, e_.encodeForRemote(), false);
                                 SpecialSkillEffect eff = new SpecialSkillEffect(player.getId(), 152100011, null);
                                 player.send(eff.encodeForLocal());
                                 player.getMap().broadcastMessage(player, eff.encodeForRemote(), false);
                                 int reduceDamx = (int)(originalDamage * 0.01 * effect.getX());
                                 damage -= reduceDamx;
                              }
                           }
                        }

                        if (player.hasBuffBySkillID(400001050)) {
                           SecondaryStat ss = player.getSecondaryStat();
                           if (ss != null && ss.EmpressBlessX == 400001053) {
                              SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.EmpressBless);
                              if (eff != null) {
                                 int reduceDamx = (int)(damage * 0.01 * eff.getZ());
                                 damage -= reduceDamx;
                              }
                           }
                        }

                        if (player.getBuffedValue(SecondaryStatFlag.IceAura) != null) {
                           SecondaryStatEffect effect = player.getBuffedEffect(SecondaryStatFlag.IceAura);
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getY());
                              damage -= reduceDamx;
                           }
                        }

                        if (player.hasBuffBySkillID(1101006)) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(1101006).getEffect(player.getSkillLevel(1101006));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getX());
                              damage -= reduceDamx;
                           }
                        }

                        if (player.getBuffedValue(SecondaryStatFlag.SpiritGuard) != null && damage > 0) {
                           damage = 0;
                           player.setSpiritWardCount(player.getSpiritWardCount() - 1);
                           if (player.getSpiritWardCount() == 0) {
                              player.temporaryStatReset(SecondaryStatFlag.SpiritGuard);
                           } else {
                              player.temporaryStatSet(25121209, Integer.MAX_VALUE, SecondaryStatFlag.SpiritGuard, player.getSpiritWardCount());
                           }
                        }

                        Integer vx = player.getBuffedValue(SecondaryStatFlag.CrewCommandership);
                        if (player.getBuffedValue(SecondaryStatFlag.DamageReduce) != null) {
                           SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.DamageReduce);
                           if (eff != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * eff.getX());
                              damage -= reduceDamx;
                           }
                        }

                        e = player.getBuffedEffect(SecondaryStatFlag.PartyBarrier);
                        if (e != null) {
                           vx = player.getBuffedValue(SecondaryStatFlag.PartyBarrier);
                           damage = (int)Math.ceil(originalDamage * 0.01 * vx.intValue());
                        }

                        e = player.getBuffedEffect(SecondaryStatFlag.KinesisPsychicEnrageShield);
                        if (e != null) {
                           int reduceDamx = (int)(originalDamage * 0.01 * e.getX());
                           damage -= reduceDamx;
                           player.invokeJobMethod("gainPP", e.getY());
                           Object point = player.getJobField("PPoint");
                           if (point != null && (Integer)point <= 0) {
                              player.temporaryStatReset(SecondaryStatFlag.KinesisPsychicEnrageShield);
                           }
                        }

                        Integer value = null;
                        if ((value = player.getBuffedValue(SecondaryStatFlag.SiphonVitalityShield)) != null) {
                           SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.SiphonVitalityShield);
                           if (eff != null) {
                              int shield = value;
                              int shieldLoss = Math.min(shield, damage);
                              damage -= shieldLoss;
                              if (damage <= 0) {
                                 damage = 1;
                              }

                              if (shield - shieldLoss <= 0) {
                                 player.temporaryStatReset(SecondaryStatFlag.SiphonVitalityShield);
                              } else {
                                 SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.SiphonVitalityShield, eff.getSourceId(), shield - shieldLoss);
                                 statManager.temporaryStatSet();
                              }
                           }
                        }

                        if (GameConstants.isAngelicBuster(player.getJob())) {
                           e = player.getBuffedEffect(SecondaryStatFlag.PowerTransferGauge);
                           if (e != null) {
                              vx = player.getBuffedValue(SecondaryStatFlag.PowerTransferGauge);
                              if (vx != null && vx > 0) {
                                 int shieldx = vx;
                                 int reduce = Math.min(damage, originalDamage * e.getX() / 100);
                                 int remainShield = Math.max(1000, shieldx - Math.min(shieldx, reduce));
                                 damage -= reduce;
                                 if (damage <= 0) {
                                    damage = 1;
                                 }

                                 SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.PowerTransferGauge, 65101002, remainShield);
                                 statManager.temporaryStatSet();
                              }
                           }
                        }

                        e = player.getBuffedEffect(SecondaryStatFlag.BlitzShield);
                        if (e != null) {
                           vx = player.getBuffedValue(SecondaryStatFlag.BlitzShield);
                           if (vx != null && vx > 0) {
                              int shieldx = vx;
                              int reduce = Math.min(damage, shieldx);
                              int remainShield = shieldx - reduce;
                              damage -= reduce;
                              if (remainShield <= 0) {
                                 player.temporaryStatReset(SecondaryStatFlag.BlitzShield);
                              } else {
                                 SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.BlitzShield, 400001010, remainShield);
                                 statManager.temporaryStatSet();
                              }
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(13110025) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(13110025).getEffect(c.getPlayer().getTotalSkillLevel(13110025));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getX());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(21120004) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(21120004).getEffect(c.getPlayer().getTotalSkillLevel(21120004));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getT());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(51120003) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(51120003).getEffect(c.getPlayer().getTotalSkillLevel(51120003));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getT());
                              damage -= reduceDamx;
                           }
                        }

                        if (c.getPlayer().hasBuffBySkillID(32111012)) {
                           SecondaryStatEffect effect = c.getPlayer().getBuffedEffect(SecondaryStatFlag.BlueAura);
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getY());
                              damage -= reduceDamx;
                           }
                        }

                        if (player.getBuffedValue(SecondaryStatFlag.AutoChargeStackOnOff) != null) {
                           SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.AutoChargeStackOnOff);
                           Object stack = player.getJobField("autoChargeStackOnOffStack");
                           if (eff != null && stack != null) {
                              int hp = (int)(player.getStat().getCurrentMaxHp() * 0.01) * eff.getY();
                              if ((Integer)stack > 0 && damage >= hp) {
                                 damage -= (int)(damage * 0.01) * eff.getQ();
                                 player.setJobField("autoChargeStackOnOffStack", (Integer)stack - 1);
                                 player.temporaryStatSet(400031053, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStackOnOff, 1);
                              }
                           }
                        }

                        if (c.getPlayer().getTotalSkillLevel(1210015) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(1210015).getEffect(c.getPlayer().getTotalSkillLevel(1210015));
                           if (effect != null) {
                              int reduceDamx = (int)(originalDamage * 0.01 * effect.getY());
                              damage -= reduceDamx;
                           }
                        }

                        e = player.getBuffedEffect(SecondaryStatFlag.DemonDamAbsorbShield);
                        if (e != null) {
                           vx = player.getBuffedValue(SecondaryStatFlag.DemonDamAbsorbShield);
                           if (vx != null && vx > 0) {
                              int shieldx = vx;
                              int reduce = (int)(damage * 0.01 * shieldx);
                              damage -= reduce;
                              player.setDemonDamAbsorbShieldX(player.getDemonDamAbsorbShieldX() - 1);
                              if (player.getDemonDamAbsorbShieldX() <= 0) {
                                 player.temporaryStatReset(SecondaryStatFlag.DemonDamAbsorbShield);
                              } else {
                                 SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.DemonDamAbsorbShield, 400001016, vx);
                                 statManager.temporaryStatSet();
                              }
                           }
                        }

                        e = player.getBuffedEffect(SecondaryStatFlag.indieDamReduceR);
                        if (e != null) {
                           for (IndieTemporaryStatEntry entry : player.getIndieTemporaryStatEntries(SecondaryStatFlag.indieDamReduceR)) {
                              int shieldx = entry.getStatValue();
                              int reduce = (int)(damage * 0.01 * shieldx);
                              damage += reduce;
                           }
                        }

                        value = player.getBuffedValue(SecondaryStatFlag.DarknessAura);
                        if (value != null && player.getBuffedValue(SecondaryStatFlag.DEF) != null) {
                           int reduce = (int)(damage * 0.01 * value.intValue());
                           damage -= reduce;
                        }

                        e = player.getBuffedEffect(SecondaryStatFlag.indieBarrier);
                        if (e != null) {
                           for (IndieTemporaryStatEntry entry : player.getIndieTemporaryStatEntries(SecondaryStatFlag.indieBarrier)) {
                              int shieldx = entry.getStatValue();
                              int temp = damage;
                              damage -= shieldx;
                              if (damage < 0) {
                                 damage = 0;
                              }

                              shieldx -= temp;
                              if (shieldx > 0) {
                                 SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.indieBarrier, e.getSourceId(), shieldx);
                                 statManager.temporaryStatSet();
                              } else {
                                 c.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.indieBarrier, entry.getSkillID());
                              }
                           }
                        }

                        e = player.getBuffedEffect(SecondaryStatFlag.DemonFrenzy);
                        if (e != null) {
                           int absorb = e.getS();
                           int reduce = (int)(damage * 0.01 * absorb);
                           damage -= reduce;
                        }
                     }

                     Integer vxx = player.getBuffedValue(SecondaryStatFlag.HolyMagicShell);
                     if (vxx != null) {
                        if (vxx <= 0) {
                           player.temporaryStatReset(SecondaryStatFlag.HolyMagicShell);
                        } else {
                           SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                           statManager.changeStatValue(SecondaryStatFlag.HolyMagicShell, 2311009, vxx - 1);
                           statManager.temporaryStatSet();
                        }
                     }

                     vxx = player.getBuffedValue(SecondaryStatFlag.GuardianOfLight);
                     if (vxx != null) {
                        SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                        statManager.changeStatValue(SecondaryStatFlag.GuardianOfLight, 50001214, vxx - 1);
                        statManager.temporaryStatSet();
                        if (vxx <= 1) {
                           player.temporaryStatResetBySkillID(50001214);
                        }
                     }

                     e = player.getBuffedEffect(SecondaryStatFlag.StormGuard);
                     if (e != null) {
                        vxx = player.getBuffedValue(SecondaryStatFlag.StormGuard);
                        if (vxx != null) {
                           int d = (int)(originalDamage / (player.getStat().getCurrentMaxHp() * 0.01));
                           int reduce = Math.min(100, Math.min(vxx, d));
                           if (fixDamR == 0) {
                              damage = 0;
                           } else {
                              reduce = fixDamR;
                           }

                           if (vxx <= reduce) {
                              player.temporaryStatReset(SecondaryStatFlag.StormGuard);
                           } else {
                              vxx = vxx - reduce;
                              SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                              statManager.changeStatValue(SecondaryStatFlag.StormGuard, e.getSourceId(), vxx);
                              statManager.temporaryStatSet();
                           }
                        }
                     }

                     e = player.getBuffedEffect(SecondaryStatFlag.Nobility);
                     if (e != null) {
                        int nobilityShield = (Integer)player.getJobField("nobilityShield");
                        if (nobilityShield > 0) {
                           if (nobilityShield > damage) {
                              nobilityShield -= damage;
                              damage = 0;
                           } else {
                              damage -= nobilityShield;
                              nobilityShield = 0;
                           }

                           player.invokeJobMethod("setNobilityShield", nobilityShield);
                        }
                     }
                  }
               }

               if (damage > 0 && player.hasBuffBySkillID(400001050)) {
                  SecondaryStat ss = player.getSecondaryStat();
                  if (ss != null && ss.EmpressBlessX == 400001053) {
                     ss.EmpressBlessX = 0;
                     SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), ss);
                     statManager.changeStatValue(SecondaryStatFlag.EmpressBless, 400001050, 1);
                     statManager.temporaryStatSet();
                  }
               }

               if (damage > 0) {
                  if (player.getBuffedValue(SecondaryStatFlag.RhoAias) != null) {
                     player.invokeJobMethod("onRhoAiasHit");
                  }

                  SecondaryStatEffect nobilityEffect;
                  if ((nobilityEffect = player.getBuffedEffect(SecondaryStatFlag.Nobility)) != null) {
                     damage -= (int)player.invokeJobMethod("onNobilityInit", nobilityEffect, damage);
                  }
               }

               boolean dead = false;
               if (damage > 0) {
                  if (damage >= stats.getCurrentMaxHp()) {
                     dead = true;
                  }

                  if ((overHP || dead || deadlyAttack) && player.getBuffedValue(SecondaryStatFlag.AutoChargeStackOnOff) != null) {
                     Object stack = player.getJobField("autoChargeStackOnOffStack");
                     stack = 2;
                     if (stack != null && (Integer)stack > 0) {
                        SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.AutoChargeStackOnOff);
                        if (eff != null) {
                           damage = (int)(damage - damage * 0.01 * eff.getQ());
                           player.setJobField("autoChargeStackOnOffStack", (Integer)stack - 1);
                           player.temporaryStatSet(400031053, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStackOnOff, 1);
                        }
                     }
                  }
               }

               if (player.isInvincible()) {
                  damage = 0;
               }

               if (!hitByDead && (fixDamR == 0 || attackIndex == AttackIndex.Mob_Body)) {
                  if (player.getTotalSkillLevel(12000024) > 0 || player.getTotalSkillLevel(27000003) > 0) {
                     int hploss = 0;
                     int mploss = 0;
                     int skillxxxxx = player.getTotalSkillLevel(12000024) > 0 ? 12000024 : 27000003;
                     SecondaryStatEffect eff = SkillFactory.getSkill(skillxxxxx).getEffect(player.getTotalSkillLevel(skillxxxxx));
                     if (eff != null) {
                        mploss = (int)(damage * (eff.getX() / 100.0)) + mpattack;
                        hploss = damage - mploss;
                        if (mploss > stats.getMp()) {
                           mploss = (int)stats.getMp();
                           hploss = damage - mploss + mpattack;
                        }

                        player.addMPHP(-hploss, -mploss);
                     }
                  } else if (player.getBuffedValue(SecondaryStatFlag.MagicGuard) != null) {
                     int hploss = 0;
                     int mploss = 0;
                     if (deadlyAttack) {
                        if (stats.getHp() > 1L) {
                           hploss = (int)(stats.getHp() - 1L);
                        }

                        if (stats.getMp() > 1L) {
                           mploss = (int)(stats.getMp() - 1L);
                        }

                        if (player.getBuffedValue(SecondaryStatFlag.Infinity) != null) {
                           mploss = 0;
                        }

                        player.addMPHP(-hploss, -mploss);
                     } else {
                        mploss = (int)(damage * (player.getBuffedValue(SecondaryStatFlag.MagicGuard).doubleValue() / 100.0)) + mpattack;
                        hploss = damage - mploss;
                        if (player.getBuffedValue(SecondaryStatFlag.Infinity) != null) {
                           mploss = 0;
                        } else if (mploss > stats.getMp()) {
                           mploss = (int)stats.getMp();
                           hploss = damage - mploss + mpattack;
                        }

                        player.addMPHP(-hploss, -mploss);
                     }
                  } else if (player.getBuffedValue(SecondaryStatFlag.MountainGuardian) != null) {
                     SecondaryStatEffect level = player.getBuffedEffect(SecondaryStatFlag.MountainGuardian);
                     if (level != null) {
                        damage -= (int)Math.ceil(damage * 0.01 * player.getBuffedValue(SecondaryStatFlag.MountainGuardian).intValue());
                        player.addMPHP(-damage, -level.getU());
                     }
                  } else if (deadlyAttack) {
                     player.addMPHP(stats.getHp() > 1L ? -(stats.getHp() - 1L) : 0L, stats.getMp() > 1L ? -(stats.getMp() - 1L) : 0L);
                  } else {
                     player.addMPHP(-damage, -mpattack);
                  }
               } else {
                  player.addMPHP(-damage, -mpattack);
               }

               AchievementFactory.resetUserHit(player);
               AchievementFactory.checkUserHitCheck(player, fixDamR);
               if (player.isGM()) {
               }

               packet.writeInt(damage);
               if (damage == -1) {
                  packet.writeInt(specialSkill);
               }

               map.broadcastMessage(player, packet.getPacket(), false);
               if (GameConstants.isXenon(player.getJob())) {
                  if (player.getBuffedValue(SecondaryStatFlag.ShadowPartner) != null && damage > 0) {
                     long virtualProjectionHP = (Long)player.getJobField("virtualProjectionHP") - damage;
                     if (virtualProjectionHP <= 0L) {
                        player.temporaryStatResetBySkillID(36111006);
                     }

                     player.setJobField("virtualProjectionHP", virtualProjectionHP);
                  }

                  SecondaryStatEffect ex = player.getBuffedEffect(SecondaryStatFlag.XenonAegisSystem);
                  if (ex != null && player.getCooldownLimit(36110004) == 0L && attacker != null) {
                     int bulletCount = ex.getX();
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initPinPointRocket();
                     ForceAtom forceAtom = new ForceAtom(
                        info,
                        36110004,
                        player.getId(),
                        false,
                        true,
                        player.getId(),
                        ForceAtom.AtomType.EAGIS_SYSTEM,
                        Collections.singletonList(attacker.getObjectId()),
                        bulletCount
                     );
                     player.getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                     player.send(CField.skillCooldown(36110004, 1500));
                     player.addCooldown(36110004, System.currentTimeMillis(), 1500L);
                  }

                  ex = SkillFactory.getSkill(30020232).getEffect(player.getTotalSkillLevel(30020232));
                  if (ex != null && ex.makeChanceResult()) {
                     player.gainXenonSurplus((short)1);
                  }
               }
            }
         }
      }
   }

   public static final void AranCombo(MapleClient c, MapleCharacter chr, int skillid) {
      if (chr != null && chr.getJob() >= 2000 && chr.getJob() <= 2112) {
         SecondaryStatEffect skill = SkillFactory.getSkill(skillid).getEffect(chr.getSkillLevel(skillid));
         if (skill != null) {
            int toAdd = skill.getAttackCount();
            short combo = chr.getCombo();
            long curr = System.currentTimeMillis();
            combo = (short)Math.min(1000, combo + toAdd);
            chr.setLastCombo(curr);
            chr.setCombo(combo);
            if (combo >= 1000
               && chr.getBuffedValue(SecondaryStatFlag.AdrenalinBoostActivate) == null
               && chr.getBuffedValue(SecondaryStatFlag.AdrenalinBoost) == null) {
               chr.temporaryStatSet(21111030, 300000, SecondaryStatFlag.AdrenalinBoostActivate, 1);
               chr.setCombo((short)1000);
            } else if (chr.getBuffedValue(SecondaryStatFlag.AdrenalinBoostActivate) == null
               && chr.getBuffedValue(SecondaryStatFlag.AdrenalinBoost) == null
               && chr.getCombo() > 0
               && chr.getComboBuffStack() / 50 != chr.getCombo() / 50) {
               int stack = Math.min(10, Math.max(0, chr.getCombo() / 50));
               chr.setComboBuffStack(stack);
               chr.temporaryStatSet(21000000, Integer.MAX_VALUE, SecondaryStatFlag.ComboAbilityBuff, chr.getComboBuffStack() * 50);
            }

            c.getSession().writeAndFlush(CField.aranCombo(combo));
         }
      }
   }

   public static final void LossAranCombo(MapleClient c, MapleCharacter chr, int toAdd) {
      if (chr != null && chr.getJob() >= 2000 && chr.getJob() <= 2112) {
         short combo = chr.getCombo();
         long curr = System.currentTimeMillis();
         if (combo <= 0) {
            combo = 0;
         }

         combo = (short)Math.max(0, combo - toAdd);
         chr.setLastCombo(curr);
         chr.setCombo(combo);
         c.getSession().writeAndFlush(CField.aranCombo(combo));
      }
   }

   public static void BlessOfDarkness(MapleCharacter chr) {
      if (GameConstants.isLuminous(chr.getJob())) {
         if (chr.getTotalSkillLevel(27101003) > 0) {
            ;
         }
      }
   }

   public static final void UseItemEffect(int itemId, MapleClient c, MapleCharacter chr) {
      Item toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
      if (toUse != null && toUse.getItemId() == itemId && toUse.getQuantity() >= 1) {
         if (itemId != 5510000) {
            chr.setItemEffect(itemId);
         }

         chr.getMap().broadcastMessage(chr, CField.itemEffect(chr.getId(), itemId), false);
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final void CancelItemEffect(int id, MapleCharacter chr) {
      chr.temporaryStatResetBySkillID(id);
   }

   public static final void useCashConsumeItem(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         short slot = slea.readShort();
         int itemID = slea.readInt();
         if (itemID == 2436907) {
            byte flag = slea.readByte();
            byte unk = slea.readByte();
            boolean isDressUp = false;
            boolean isZeroBeta = false;
            boolean isZeroMulti = false;
            if (flag == 1) {
               isDressUp = true;
            } else if (flag == 2) {
               isZeroBeta = true;
            } else if (flag == 101) {
               isZeroMulti = true;
            }

            boolean fakeRelog = false;
            if (isDressUp) {
               int mixInfo = slea.readInt();
               int afterHairBaseColor = mixInfo / 10000;
               int afterHairAddColor = mixInfo / 1000 % 10;
               int afterHairBaseProb = mixInfo % 100;
               chr.setSecondBaseColor(afterHairBaseColor);
               chr.setSecondAddColor(afterHairAddColor);
               chr.setSecondBaseProb(afterHairBaseProb);
               fakeRelog = true;
            } else if (isZeroMulti) {
               int mixInfo = slea.readInt();
               int afterHairBaseColor = mixInfo / 10000;
               int afterHairAddColor = mixInfo / 1000 % 10;
               int afterHairBaseProb = mixInfo % 100;
               int mixBetaInfo = slea.readInt();
               int afterBetaHairBaseColor = mixBetaInfo / 10000;
               int afterBetaHairAddColor = mixBetaInfo / 1000 % 10;
               int afterBetaHairBaseProb = mixBetaInfo % 100;
               chr.setBaseColor(afterHairBaseColor);
               chr.setAddColor(afterHairAddColor);
               chr.setBaseProb(afterHairBaseProb);
               chr.setSecondBaseColor(afterBetaHairBaseColor);
               chr.setSecondAddColor(afterBetaHairAddColor);
               chr.setSecondBaseProb(afterBetaHairBaseProb);
               if (chr.getZeroInfo() != null) {
                  chr.getZeroInfo().setMixBaseHairColor(afterBetaHairBaseColor);
                  chr.getZeroInfo().setMixAddHairColor(afterBetaHairAddColor);
                  chr.getZeroInfo().setMixHairBaseProb(afterBetaHairBaseProb);
               }

               fakeRelog = true;
            } else if (isZeroBeta) {
               int mixInfo = slea.readInt();
               int afterHairBaseColor = mixInfo / 10000;
               int afterHairAddColor = mixInfo / 1000 % 10;
               int afterHairBaseProb = mixInfo % 100;
               chr.setSecondBaseColor(afterHairBaseColor);
               chr.setSecondAddColor(afterHairAddColor);
               chr.setSecondBaseProb(afterHairBaseProb);
               if (chr.getZeroInfo() != null) {
                  chr.getZeroInfo().setMixBaseHairColor(afterHairBaseColor);
                  chr.getZeroInfo().setMixAddHairColor(afterHairAddColor);
                  chr.getZeroInfo().setMixHairBaseProb(afterHairBaseProb);
               }

               fakeRelog = true;
            } else {
               int mixInfo = slea.readInt();
               int afterFaceBaseColor = mixInfo / 10000;
               int afterFaceAddColor = mixInfo / 1000 % 10;
               int afterFaceBaseProb = mixInfo % 100;
               chr.setBaseColor(afterFaceBaseColor);
               chr.setAddColor(afterFaceAddColor);
               chr.setBaseProb(afterFaceBaseProb);
            }

            int hair = chr.isDressUp() ? chr.getSecondHair() : chr.getHair();
            int hairBaseColor = chr.isDressUp() ? chr.getSecondBaseColor() : chr.getBaseColor();
            int hairAddColor = chr.isDressUp() ? chr.getSecondAddColor() : chr.getAddColor();
            int hairBaseProb = chr.isDressUp() ? chr.getSecondBaseProb() : chr.getBaseProb();
            if (!fakeRelog) {
               long mixedHair = hair / 10 * 10 * 1000 + hairBaseColor * 1000 + hairAddColor * 100 + hairBaseProb;
               Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);
               statupdate.put(MapleStat.HAIR, mixedHair);
               chr.send(CWvsContext.updatePlayerStats(statupdate, false, chr));
               chr.getMap().broadcastMessage(CField.updateCharLook(chr));
            }

            MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, slot, (short)1, false, true);
            chr.send(CWvsContext.enableActions(chr));
            if (fakeRelog) {
               chr.fakeRelog();
            }
         }
      }
   }

   public static final void skillCancel(int skillID, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         if (skillID != 80003059) {
            BasicJob bj = chr.getPlayerJob();
            bj.setActiveSkill(skillID);
            bj.activeSkillCancel();
         }
      }
   }

   public static final void CancelMech(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null) {
         int sourceid = slea.readInt();
         if (sourceid % 10000 < 1000 && SkillFactory.getSkill(sourceid) == null) {
            sourceid += 1000;
         }

         Skill skill = SkillFactory.getSkill(sourceid);
         if (skill != null) {
            if (skill.isChargeSkill()) {
               chr.setKeyDownSkill_Time(0L);
               chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, sourceid), false);
            } else {
               chr.temporaryStatResetBySkillID(sourceid);
            }
         }
      }
   }

   public static void movingShootAttackPrepare(PacketDecoder in, MapleCharacter chr) {
      int skillID = in.readInt();
      short display = in.readShort();
      byte actionSpeed = in.readByte();
      int skillLevel = chr.getTotalSkillLevel(skillID);
      if (skillLevel > 0) {
         PacketEncoder o = new PacketEncoder();
         o.writeShort(SendPacketOpcode.MOVING_SHOOT_ATTACK_PREPARE.getValue());
         o.writeInt(chr.getId());
         o.write(0);
         o.write(skillLevel);
         o.writeInt(skillID);
         o.writeShort(display);
         o.write(actionSpeed);
         chr.getMap().broadcastMessage(chr, o.getPacket(), false);
      }
   }

   public static final void SkillPrepare(PacketDecoder slea, MapleCharacter chr) {
      int skillID = slea.readInt();
      int SLV = slea.readInt();
      TeleportAttackAction teleportAction = TeleportAttackAction.fromRemote(slea);
      short moveAction = slea.readShort();
      byte speed = slea.readByte();
      byte unk = slea.readByte();
      Point keyDownRectMoveXY = new Point(0, 0);
      if (GameConstants.isKeydownSkillRectMoveXY(skillID)) {
         keyDownRectMoveXY = slea.readPos();
      }

      slea.skip(4);
      if (skillID == 14121004) {
         slea.skip(2);
         slea.skip(2);
      }

      Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(skillID));
      if (chr != null && skill != null && chr.getMap() != null) {
         int SLV_Server = chr.getTotalSkillLevel(skill);
         if (skillID == 64121002) {
            SecondaryStatEffect level = chr.getSkillLevelData(64121002);
            if (level != null && chr.getCooldownLimit(64121002) <= 0L) {
               chr.temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 64120010, level.getQ() * 1000, 1);
               int cooldown = level.getCooldown(chr);
               chr.send(CField.skillCooldown(64121002, cooldown));
               chr.addCooldown(64121002, System.currentTimeMillis(), cooldown);
            }
         }

         BasicJob bj = chr.getPlayerJob();
         bj.setActiveSkillPrepareID(skillID);
         bj.setActiveSkillPrepareSLV(SLV);
         bj.setTeleportAttackAction(teleportAction);
         bj.setKeyDownRectMoveXY(keyDownRectMoveXY);
         bj.setMoveAction(moveAction);
         bj.setSpeed(speed);
         bj.activeSkillPrepare(slea);
      }
   }

   public static final void useSkillRequest(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null && slea.available() >= 9L) {
         slea.skip(4);
         int skillID = slea.readInt();
         boolean exclusive = true;
         Skill skill_ = SkillFactory.getSkill(skillID);
         if (skill_ != null) {
            if (skill_.getType() == 40) {
               exclusive = false;
            } else if (skill_.getType() == 1) {
               exclusive = false;
            }
         } else {
            exclusive = true;
         }

         if (skillID == 4211016
            || skillID == 4101011
            || skillID == 30010186
            || skillID == 400021041
            || skillID == 4331006
            || skillID == 24121003
            || skillID == 32111016
            || skillID == 31101002
            || skillID == 5201005
            || skillID == 5201011
            || skillID == 400021049
            || skillID == 400021050
            || skillID == 80003228) {
            exclusive = true;
         }

         if (skillID == 4120019) {
            skillID = 4120018;
         }

         if (skillID >= 51001005 && skillID <= 51001013) {
            skillID = 51001005;
         }

         if (GameConstants.isZeroSkill(skillID)) {
            slea.skip(1);
         }

         if (skillID == 63101104 || skillID == 63121102) {
            chr.maxSkillByID(GameConstants.getLinkedAranSkill(skillID));
         }

         int skillLevel = slea.readInt();
         if (chr.isGM() || chr.getClient().isGm() && !DBConfig.isHosting) {
            chr.send(
               CField.chatMsg(
                  16,
                  "스킬 사용 : "
                     + skillID
                     + " ("
                     + SkillFactory.getSkillName(skillID)
                     + ", SkillLevel : "
                     + skillLevel
                     + ") [Type : "
                     + SkillFactory.getSkill(skillID).getType()
                     + "]"
               )
            );
            chr.dropMessage(5, slea.toString());
         }

         Skill skill = SkillFactory.getSkill(skillID);
         if (skill != null
            && (!GameConstants.isAngel(skillID) || chr.getStat().equippedSummon % 10000 == skillID % 10000)
            && (!chr.inPVP() || !skill.isPVPDisabled())) {
            if ((
                  chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillID)) <= 0
                     || chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillID)) != skillLevel
               )
               && skillID != 32120019
               && chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillID)) <= 0
               && !GameConstants.isStackedLinkSkill(skillID, chr)
               && !GameConstants.isAngel(skillID)
               && skill.getId() != 80001242
               && skill.getId() != 80003062
               && skill.getId() != 80001416
               && skill.getId() != 30001080
               && skill.getId() != 400001064
               && !GameConstants.isObsidianBarrier(skillID)
               && !GameConstants.isRelicUnbound(skillID)
               && undefinedIDA.isTemporarySkill(skillID)
               && chr.getJob() != 13100
               && chr.getJob() != 13500) {
               if (!GameConstants.isTheSeedSkill(skillID)) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), exclusive));
                  return;
               }

               Item item = chr.getInventory(MapleInventoryType.EQUIPPED).findById(GameConstants.getTheSeedRingBySkill(skillID));
               if (item == null) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), exclusive));
                  return;
               }
            }

            int realSkillID = GameConstants.getLinkedAranSkill(skillID);
            skillLevel = Math.max(1, chr.getTotalSkillLevel(realSkillID));
            if (GameConstants.isTheSeedSkill(skillID)) {
               Item item = chr.getInventory(MapleInventoryType.EQUIPPED).findById(GameConstants.getTheSeedRingBySkill(skillID));
               if (item == null) {
                  return;
               }

               skillLevel = ((Equip)item).getTheSeedRingLevel();
            }

            switch (skillID) {
               case 2311014:
               case 2311015:
                  realSkillID = skillID;
                  break;
               case 3211020:
                  realSkillID = 3211020;
                  break;
               case 500061062:
                  realSkillID = 400011015;
            }

            slea.skip(8);
            TeleportAttackAction teleportAttackAction = TeleportAttackAction.fromRemote(slea);
            specialMoveAdditionalInfo(slea);
            int cancelFlag = slea.readInt();
            chr.doActiveSkillCooltime(realSkillID, skillID, skillLevel);
            BasicJob job = chr.getPlayerJob();
            job.setActiveSkill(skillID);
            job.setActiveSkillLevel(skillLevel);
            job.setActiveSkillFlag(cancelFlag);
            job.setTeleportAttackAction(teleportAttackAction);
            job.beforeActiveSkill(slea);
            c.getPlayer().send(CWvsContext.enableActions(chr, exclusive));
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), exclusive));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   static final void specialMoveAdditionalInfo(PacketDecoder slea) {
      slea.readByte();
      slea.readByte();
      slea.readByte();
      slea.readShort();
      slea.readInt();
      slea.readByte();
   }

   public static final void meleeAttack(PacketDecoder slea, MapleClient c, MapleCharacter chr, RecvPacketOpcode opcode) {
      if ((
            chr == null
               || opcode == RecvPacketOpcode.BODY_ATTACK
                  && chr.getBuffedValue(SecondaryStatFlag.Inflation) == null
                  && chr.getBuffedValue(SecondaryStatFlag.BulletParty) == null
                  && chr.getBuffedValue(SecondaryStatFlag.MegaSmasher) == null
                  && chr.getBuffedValue(SecondaryStatFlag.BodyPressure) == null
                  && chr.getBuffedValue(SecondaryStatFlag.indieSummon) == null
                  && chr.getBuffedValue(SecondaryStatFlag.TeleportMasteryOn) == null
                  && chr.getBuffedValue(SecondaryStatFlag.ChillingStep) == null
                  && chr.getBuffedValue(SecondaryStatFlag.Asura) == null
                  && chr.getBuffedValue(SecondaryStatFlag.indieSummon) == null
         )
         && chr.getJob() != 13100
         && chr.getJob() != 13500
         && !GameConstants.isLara(chr.getJob())) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else if (chr.getMap() == null) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         AttackInfo attack = null;
         long pos = slea.getPosition();

         try {
            attack = DamageParse.onAttack(slea, chr, opcode);
         } catch (Exception var17) {
            try {
               slea.seek(pos);
               attack = DamageParse.onAttack(slea, chr, opcode, true, true);
            } catch (Exception var16) {
               slea.seek(pos);
               FileoutputUtil.outputFileErrorReason("Log_DamageParseError.rtf", "물리 데미지 패킷 분석 실패 : " + slea.toString(), var17);
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }
         }

         if (attack == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            boolean mirror = chr.getBuffedValue(SecondaryStatFlag.ShadowPartner) != null || chr.getBuffedValue(SecondaryStatFlag.BuckShot) != null;
            double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
            Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
            int attackCount = shield != null && shield.getItemId() / 10000 == 134 ? 2 : 1;
            int skillLevel = 0;
            SecondaryStatEffect effect = null;
            Skill skill = null;
            if (attack.skillID != 0) {
               skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skillID));
               if (attack.skillID != 80001770 && skill == null
                  || GameConstants.isAngel(attack.skillID) && chr.getStat().equippedSummon % 10000 != attack.skillID % 10000) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               skillLevel = chr.getTotalSkillLevel(skill);
               if (skillLevel == 0) {
                  skillLevel = 1;
               }

               effect = attack.getAttackEffect(chr, skillLevel, skill);
               if (effect == null) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (skill.getId() == 400031034) {
                  chr.temporaryStatResetBySkillID(400031034);
               }
            }

            if (!chr.isHidden()) {
               if (!chr.hasBuffBySkillID(400011003) && !chr.hasBuffBySkillID(500061000)) {
                  chr.getMap().broadcastMessage(chr, CField.closeRangeAttack(chr, opcode == RecvPacketOpcode.BODY_ATTACK, attack), chr.getTruePosition());
               } else {
                  MapleCharacter target = chr.getClient()
                     .getChannelServer()
                     .getPlayerStorage()
                     .getCharacterById(chr.getBuffedValueDefault(SecondaryStatFlag.HolyUnity, 0));
                  if (target != null) {
                     target.getMap().broadcastMessage(CField.closeRangeAttack(target, opcode == RecvPacketOpcode.BODY_ATTACK, attack), attack.targetPosition);
                  }
               }
            } else if (!chr.hasBuffBySkillID(400011003) && !chr.hasBuffBySkillID(500061000)) {
               chr.getMap().broadcastGMMessage(chr, CField.closeRangeAttack(chr, opcode == RecvPacketOpcode.BODY_ATTACK, attack), false);
            } else {
               MapleCharacter target = chr.getClient()
                  .getChannelServer()
                  .getPlayerStorage()
                  .getCharacterById(chr.getBuffedValueDefault(SecondaryStatFlag.HolyUnity, 0));
               if (target != null) {
                  target.getMap().broadcastGMMessage(chr, CField.closeRangeAttack(target, opcode == RecvPacketOpcode.BODY_ATTACK, attack), false);
               }
            }

            attack = DamageParse.Modify_AttackCrit(attack, chr, 1, effect);
            DamageParse.applyAttack(
               attack,
               skill,
               c.getPlayer(),
               attackCount,
               maxdamage,
               effect,
               mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED,
               opcode == RecvPacketOpcode.BODY_ATTACK,
               opcode
            );
         }
      }
   }

   public static final void shootAttack(PacketDecoder slea, MapleClient c, MapleCharacter chr, RecvPacketOpcode opcode) {
      if (chr == null) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else if (chr.getMap() == null) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         AttackInfo attack = null;
         long pos = slea.getPosition();

         try {
            attack = DamageParse.onAttack(slea, chr, opcode);
         } catch (Exception var19) {
            slea.seek(pos);
            FileoutputUtil.outputFileErrorReason("Log_DamageParseError.rtf", "원거리 데미지 패킷 분석 실패 : " + slea.toString(), var19);
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (attack == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            int bulletCount = 1;
            int skillLevel = 0;
            SecondaryStatEffect effect = null;
            Skill skill = null;
            if (attack.skillID != 0) {
               skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skillID));
               if (skill == null || GameConstants.isAngel(attack.skillID) && chr.getStat().equippedSummon % 10000 != attack.skillID % 10000) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               skillLevel = chr.getTotalSkillLevel(skill);
               if (GameConstants.isYetiPinkBean(chr.getJob()) && skillLevel == 0) {
                  skillLevel = 1;
               }

               effect = attack.getAttackEffect(chr, skillLevel, skill);
               if (effect == null) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }
            }

            attack = DamageParse.Modify_AttackCrit(attack, chr, 2, effect);
            Integer ShadowPartner = chr.getBuffedValue(SecondaryStatFlag.ShadowPartner);
            if (ShadowPartner != null) {
               bulletCount *= 2;
            }

            int projectile = 0;
            int visProjectile = 0;
            Item ipp = chr.getInventory(MapleInventoryType.USE).getItem(attack.slot);
            if (ipp != null && ipp.getItemId() > 0) {
               projectile = ipp.getItemId();
            }

            if (attack.bulletCashItemPos > 0) {
               if (chr.getInventory(MapleInventoryType.CASH).getItem(attack.bulletCashItemPos) == null) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem(attack.bulletCashItemPos).getItemId();
            } else {
               visProjectile = projectile;
            }

            if (visProjectile == 0) {
               visProjectile = attack.consumeItemID;
            }

            if (GameConstants.isMercedes(chr.getJob())) {
               visProjectile = 2060000;
            }

            if (chr.getJob() >= 3500 && chr.getJob() <= 3512) {
               visProjectile = 2333000;
            } else if (GameConstants.isCannon(chr.getJob())) {
               visProjectile = 2333001;
            }

            int projectileWatk = 0;
            if (projectile != 0) {
               projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
            }

            PlayerStats statst = chr.getStat();
            double basedamage;
            switch (attack.skillID) {
               case 4001344:
               case 4121007:
               case 14001004:
               case 14111005:
                  basedamage = Math.max(statst.getCurrentMaxBaseDamage(), statst.getTotalLuk() * 5.0F * (statst.getTotalWatk() + projectileWatk) / 100.0F);
                  break;
               case 4111004:
                  basedamage = 53000.0;
                  break;
               default:
                  basedamage = statst.getCurrentMaxBaseDamage();
                  if (attack.skillID == 3101005) {
                     basedamage *= effect.getX() / 100.0;
                  }
            }

            if (effect != null) {
               basedamage *= (effect.getDamage() + statst.getDamageIncrease(attack.skillID)) / 100.0;
            }

            if (attack.skillID == 14141003) {
               chr.send(CField.skillCooldown(14141002, 6000));
               chr.addCooldown(14141002, System.currentTimeMillis(), 6000L);
            }

            if (!chr.isHidden()) {
               if (attack.skillID == 3211006) {
                  chr.getMap().broadcastMessage(chr, CField.strafeAttack(chr, visProjectile, chr.getTotalSkillLevel(3220010), attack), chr.getTruePosition());
               } else {
                  chr.getMap().broadcastMessage(chr, CField.rangedAttack(chr, visProjectile, attack), chr.getTruePosition());
               }
            } else if (attack.skillID == 3211006) {
               chr.getMap().broadcastGMMessage(chr, CField.strafeAttack(chr, visProjectile, chr.getTotalSkillLevel(3220010), attack), false);
            } else {
               chr.getMap().broadcastGMMessage(chr, CField.rangedAttack(chr, visProjectile, attack), false);
            }

            DamageParse.applyAttack(
               attack,
               skill,
               chr,
               bulletCount,
               basedamage,
               effect,
               ShadowPartner != null ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED,
               false,
               opcode
            );
         }
      }
   }

   public static final void magicAttack(PacketDecoder slea, MapleClient c, MapleCharacter chr, RecvPacketOpcode opcode) {
      if (chr != null && chr.getMap() != null) {
         AttackInfo attack = null;
         long pos = slea.getPosition();

         try {
            attack = DamageParse.onAttack(slea, chr, opcode);
         } catch (Exception var13) {
            slea.seek(pos);
            FileoutputUtil.outputFileErrorReason("Log_DamageParseError.rtf", "마법 데미지 패킷 분석 실패 : " + slea.toString(), var13);
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (attack == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skillID));
            if (skill != null && (!GameConstants.isAngel(attack.skillID) || chr.getStat().equippedSummon % 10000 == attack.skillID % 10000)) {
               int skillLevel = chr.getTotalSkillLevel(skill);
               if (GameConstants.isYetiPinkBean(chr.getJob()) && skillLevel == 0) {
                  skillLevel = 1;
               }

               if (skillLevel == 0 && attack.skillID == 12120019 || attack.skillID == 12120020) {
                  skillLevel = 31;
               }

               if (skillLevel == 0 && attack.skillID == 80001762) {
                  skillLevel = 1;
               }

               SecondaryStatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);
               if (effect == null && !GameConstants.isYetiPinkBean(chr.getJob())) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               } else {
                  attack = DamageParse.Modify_AttackCrit(attack, chr, 3, effect);
                  double maxdamage = chr.getStat().getCurrentMaxBaseDamage() * (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skillID)) / 100.0;
                  if (GameConstants.isPyramidSkill(attack.skillID)) {
                     maxdamage = 1.0;
                  } else if (GameConstants.isNovice(skill.getId() / 10000) && skill.getId() % 10000 == 1000) {
                     maxdamage = 40.0;
                  }

                  if (GameConstants.isKinesis(chr.getJob()) && attack.skillID != 142001002 && attack.skillID != 142141500) {
                     chr.invokeJobMethod("givePPoint", attack.skillID, false, attack.targets);
                  }

                  if (!chr.isHidden()) {
                     chr.getMap().broadcastMessage(chr, CField.magicAttack(chr, attack), chr.getTruePosition());
                  } else {
                     chr.getMap().broadcastGMMessage(chr, CField.magicAttack(chr, attack), false);
                  }

                  int bulletCount = 1;
                  switch (attack.skillID) {
                     case 12121001:
                     case 27001201:
                     case 27101100:
                     case 27111100:
                     case 27111101:
                     case 27111202:
                     case 27111303:
                     case 27121100:
                     case 27121202:
                     case 27121303:
                        int var16 = effect.getAttackCount();
                        DamageParse.applyAttack(attack, skill, chr, skillLevel, maxdamage, effect, AttackType.RANGED, false, opcode);
                        break;
                     default:
                        DamageParse.applyAttackMagic(attack, skill, chr, effect, maxdamage, opcode);
                  }
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final void DropMeso(int meso, MapleCharacter chr) {
      if (chr.isAlive() && meso >= 10 && meso <= 50000 && meso <= chr.getMeso()) {
         chr.gainMeso(-meso, false, true, true);
         chr.getMap().spawnMesoDrop(meso, chr.getTruePosition(), chr, chr, true, (byte)0);
         chr.getCheatTracker().checkDrop(true);
      } else {
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public static final void ChangeAndroidEmotion(int emote, MapleCharacter chr) {
      if (emote > 0 && chr != null && chr.getMap() != null && !chr.isHidden() && emote <= 17 && chr.getAndroid() != null) {
         chr.getMap().broadcastMessage(CField.showAndroidEmotion(chr.getId(), emote));
      }
   }

   public static final void MoveAndroid(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.skip(12);
      slea.skip(4);
      List<LifeMovementFragment> res = MovementParse.parseMovement(slea);
      if (res != null && chr != null && res.size() != 0 && chr.getMap() != null && chr.getAndroid() != null) {
         Point pos = new Point(chr.getAndroid().getPos());
         chr.getAndroid().updatePosition(res);
         chr.getMap().broadcastMessage(chr, CField.moveAndroid(chr.getId(), pos, res), false);
      }
   }

   public static final void ChangeEmotion(int emote, MapleCharacter chr) {
      chr.setChangeEmotionTime(System.currentTimeMillis());
      if (emote > 7) {
         int emoteid = 5159992 + emote;
         MapleInventoryType type = GameConstants.getInventoryType(emoteid);
         if (chr.getInventory(type).findById(emoteid) == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
            return;
         }
      }

      if (emote > 0 && chr.getMap() != null && !chr.isHidden()) {
         chr.getMap().broadcastMessage(chr, CField.facialExpression(chr, emote), false);
         if (chr.getAndroid() != null) {
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.FACIAL_EXPRESSION_ANDROID.getValue());
            p.writeInt(chr.getId());
            p.writeInt(emote);
            p.writeInt(-1);
            chr.getMap().broadcastMessage(chr, p.getPacket(), false);
         }
      }
   }

   public static final void Heal(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null) {
         slea.readInt();
         if (slea.available() >= 8L) {
            slea.skip(4);
         }

         int healHP = slea.readShort();
         int healMP = slea.readShort();
         PlayerStats stats = chr.getStat();
         if (stats.getHp() > 0L) {
            long now = System.currentTimeMillis();
            if (healHP != 0 && chr.canHP(now + 1000L)) {
               if (healHP > stats.getHealHP()) {
                  healHP = (int)stats.getHealHP();
               }

               chr.addHP(healHP);
               if (chr.getJob() == 2711 || chr.getJob() == 2712) {
                  SecondaryStatEffect eff = chr.getSkillLevelData(27110007);
                  if (eff != null) {
                     int hpPercent = (int)(chr.getStat().getHp() * 100L / chr.getStat().getCurrentMaxHp());
                     int mpPercent = (int)(chr.getStat().getMp() * 100L / chr.getStat().getCurrentMaxMp(chr));
                     if (hpPercent < mpPercent) {
                        int recoveryHp = (int)(chr.getStat().getCurrentMaxHp() / 100L * eff.getT());
                        chr.addHP(recoveryHp);
                     }
                  }
               }
            }

            if (healMP != 0 && !GameConstants.isDemonSlayer(chr.getJob()) && chr.canMP(now + 1000L)) {
               if (healMP > stats.getHealMP()) {
                  healMP = (int)stats.getHealMP();
               }

               chr.addMP(healMP);
               if (chr.getJob() == 2711 || chr.getJob() == 2712) {
                  SecondaryStatEffect eff = chr.getSkillLevelData(27110007);
                  if (eff != null) {
                     int hpPercent = (int)(chr.getStat().getHp() * 100L / chr.getStat().getCurrentMaxHp());
                     int mpPercent = (int)(chr.getStat().getMp() * 100L / chr.getStat().getCurrentMaxMp(chr));
                     if (mpPercent < hpPercent) {
                        int recoveryMp = (int)(chr.getStat().getCurrentMaxMp(chr) / 100L * eff.getT());
                        chr.addMP(recoveryMp);
                     }
                  }
               }
            }
         }
      }
   }

   public static final void MovePlayer(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      chr.setLastMoveTime(System.currentTimeMillis());
      chr.setNotMovingCount(0);
      slea.skip(1);
      slea.skip(4);
      slea.skip(4);
      slea.skip(1);
      int encodedGatherDuration = slea.readInt();
      Point startPos = new Point(slea.readShort(), slea.readShort());
      Point ovPos = new Point(slea.readShort(), slea.readShort());
      if (chr != null) {
         Point Original_Pos = chr.getPosition();

         List<LifeMovementFragment> res;
         try {
            res = MovementParse.parseMovement(slea);
         } catch (ArrayIndexOutOfBoundsException var16) {
            System.out.println("unk movement type \n : " + slea.toString(true));
            return;
         }

         int keyPadStateSize = slea.readByte();
         List<Integer> keyPadStates = new ArrayList<>();
         byte z = 0;

         for (int i = 0; i < keyPadStateSize; i++) {
            z = i % 2 != 0 ? (byte)(z >> 4) : slea.readByte();
            keyPadStates.add(z & 15);
         }

         if (res != null && c.getPlayer().getMap() != null) {
            Field map = c.getPlayer().getMap();

            try {
               if (chr.isHidden()) {
                  chr.setLastRes(res);
                  c.getPlayer().getMap().broadcastGMMessage(chr, CField.movePlayer(chr.getId(), res, startPos, ovPos, encodedGatherDuration), false);
               } else {
                  c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.movePlayer(chr.getId(), res, startPos, ovPos, encodedGatherDuration), false);
               }
            } catch (Exception var15) {
               System.out.println("movePlayer 함수 실행중 오류 발생 broadcastMessage 오류 : " + var15.toString());
               var15.printStackTrace();
            }

            MovementParse.updatePosition(res, chr, 0);
            Point pos = chr.getTruePosition();
            map.movePlayer(chr, pos);
            if (chr.getFollowId() > 0 && chr.isFollowOn() && chr.isFollowInitiator()) {
               MapleCharacter fol = map.getCharacterById(chr.getFollowId());
               if (fol != null) {
                  Point original_pos = fol.getPosition();
                  fol.getClient().getSession().writeAndFlush(CField.moveFollow(Original_Pos, original_pos, pos, res, encodedGatherDuration));
                  MovementParse.updatePosition(res, fol, 0);
                  map.movePlayer(fol, pos);
                  map.broadcastMessage(fol, CField.movePlayer(fol.getId(), res, startPos, ovPos, encodedGatherDuration), false);
               } else {
                  chr.checkFollow();
               }
            }

            int count = c.getPlayer().getFallCounter();
            boolean samepos = pos.y > c.getPlayer().getOldPosition().y && Math.abs(pos.x - c.getPlayer().getOldPosition().x) < 5;
            if (samepos && (pos.y > map.getBottom() + 250 || map.getFootholds().findBelow(pos) == null)) {
               if (count > 5) {
                  c.getPlayer().changeMap(map, map.getPortal(0));
                  c.getPlayer().setFallCounter(0);
               } else {
                  c.getPlayer().setFallCounter(++count);
               }
            } else if (count > 0) {
               c.getPlayer().setFallCounter(0);
            }

            c.getPlayer().setOldPosition(pos);
         }
      }
   }

   public static final void ChangeMapSpecial(String portal_name, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         Portal portal = chr.getMap().getPortal(portal_name);
         if (portal != null) {
            portal.enterPortal(c);
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      }
   }

   public static final void ChangeMap(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         if (slea.available() != 0L) {
            slea.readInt();
            slea.readShort();
            slea.readLong();
            slea.readByte();
            int targetid = slea.readInt();
            Portal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
            boolean wheel = false;
            boolean buffFreezer = true;
            if (slea.available() >= 4L) {
               wheel = slea.readByte() > 0;
               slea.readByte();
               slea.readByte();
               slea.readByte();
            }

            boolean bmCurse = false;
            boolean restoreBuff = false;
            if (targetid != -1 && !chr.isAlive()) {
               if (buffFreezer) {
                  MapleQuestStatus questStatus = new MapleQuestStatus(MapleQuest.getInstance(1097), 1);
                  questStatus.setCustomData("1");
                  chr.updateQuest(questStatus);
                  chr.send(CWvsContext.setBuffProtector(0));
                  chr.setUseBuffProtector(true);
               } else {
                  MapleQuestStatus questStatus = new MapleQuestStatus(MapleQuest.getInstance(1097), 1);
                  questStatus.setCustomData("0");
                  chr.updateQuest(questStatus);
                  if (GameConstants.isArk(chr.getJob())) {
                     chr.setJobField("plainSpellBulletCount", 0);
                     chr.setJobField("scarletSpellBulletCount", 0);
                     chr.setJobField("gustSpellBulletCount", 0);
                     chr.setJobField("abyssSpellBulletCount", 0);
                  }
               }

               chr.setNextConsume(0L);
               chr.setStance(0);
               chr.cancelAllTask(false, chr.isUseBuffProtector());
               if (!chr.isUseBuffProtector()) {
                  chr.cancelAllBuffs();
               } else {
                  chr.temporaryStatReset(SecondaryStatFlag.FullSoulMP);
               }

               chr.setPlayerDead(false);
               chr.checkEquippedMasterLabel();
               if (chr.getEventInstance() != null && chr.getEventInstance().revivePlayer(chr) && chr.isAlive()) {
                  return;
               }

               if (chr.getPyramidSubway() != null) {
                  chr.getStat().setHp(50L, chr);
                  chr.getPyramidSubway().fail(chr);
                  return;
               }

               if (chr.getCooldownLimit(80002282) != 0L) {
                  chr.temporaryStatSet(80002282, (int)(900000L - chr.getCooldownLimit(80002282)), SecondaryStatFlag.RuneBlocked, 1);
               }

               if (chr.getCooldownLimit(2310013) != 0L) {
                  SecondaryStatEffect effect = chr.getSkillLevelData(2311009);
                  if (effect != null) {
                     chr.temporaryStatSet(2310013, (int)(effect.getY() * 1000 - chr.getCooldownLimit(2310013)), SecondaryStatFlag.HolyMagicShellBlocked, 1);
                  }
               }

               if (chr.getDeathCount() > 0) {
                  if (c.getPlayer().getMap().getId() == 105200520) {
                     Field target = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId() - 10);
                     chr.changeMap(target);
                  } else {
                     chr.changeMap(chr.getMap());
                  }

                  chr.addHP(500000L, true);
                  c.getPlayer().applySpiritOfFreedom();
                  return;
               }

               if (wheel) {
                  UseWheel e = new UseWheel(c.getPlayer().getId(), chr.getInventory(MapleInventoryType.CASH).countById(5510000) - 1);
                  c.getSession().writeAndFlush(e.encodeForLocal());
                  chr.getStat().setHp(chr.getStat().getCurrentMaxHp() / 100L * 40L, chr);
                  MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);
                  Field to = chr.getMap();
                  chr.changeMap(to, to.getPortal(0));
               } else {
                  chr.getStat().setHp(50L, chr);
                  Field to = chr.getMap().getForcedReturnMap();
                  if (to == null || to.getId() == 999999999) {
                     to = chr.getMap().getReturnMap();
                  }

                  chr.changeMap(to, to.getPortal(0));
               }

               c.getPlayer().applySpiritOfFreedom();
               if (!chr.isUseBuffProtector()) {
                  c.getPlayer().onJaguarLinkPassive();
                  if (bmCurse) {
                  }
               }

               if (GameConstants.isBlaster(chr.getJob())) {
                  chr.invokeJobMethod("setRWCylinder", 37000010, -1, -1, -1);
               }

               chr.setUseBuffProtector(false);
            } else if (targetid != -1 && chr.isIntern()) {
               Field to = GameServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
               if (to != null) {
                  chr.changeMap(to, to.getPortal(0));
               } else {
                  chr.dropMessage(5, "Map is NULL. Use !warp <mapid> instead.");
               }
            } else if (targetid != -1 && !chr.isIntern()) {
               String curDirection = chr.getCurrentDirection();
               if (curDirection == null || !AllowedDirectionFieldMan.isAllowed(curDirection, targetid)) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               Field to = GameServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
               chr.changeMap(to, to.getPortal(0));
            } else if (portal != null) {
               portal.enterPortal(c);
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
         }
      }
   }

   public static final void InnerPortal(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         Portal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
         int toX = slea.readShort();
         int toY = slea.readShort();
         if (portal != null) {
            if (portal.getPosition().distanceSq(chr.getTruePosition()) > 22500.0 && !chr.isGM()) {
               chr.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            } else {
               chr.checkHiddenMissionComplete(QuestExConstants.SuddenMKInnerPortal.getQuestID());
               chr.getMap().movePlayer(chr, new Point(toX, toY));
               chr.checkFollow();
            }
         }
      }
   }

   public static final void snowBall(PacketDecoder slea, MapleClient c) {
      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static final void leftKnockBack(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getMapId() / 10000 == 10906) {
         c.getSession().writeAndFlush(CField.leftKnockBack());
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final void ReIssueMedal(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      MapleQuest q = MapleQuest.getInstance(slea.readShort());
      int itemid = q.getMedalItem();
      if (itemid != slea.readInt() || itemid <= 0 || !GameConstants.isMedal(itemid) || chr.getQuestStatus(q.getId()) != 2) {
         c.getSession().writeAndFlush(CField.UIPacket.reissueMedal(itemid, 4));
      } else if (chr.haveItem(itemid, 1, true, true)) {
         c.getSession().writeAndFlush(CField.UIPacket.reissueMedal(itemid, 3));
      } else if (!MapleInventoryManipulator.checkSpace(c, itemid, 1, "")) {
         c.getSession().writeAndFlush(CField.UIPacket.reissueMedal(itemid, 2));
      } else if (chr.getMeso() < 100L) {
         c.getSession().writeAndFlush(CField.UIPacket.reissueMedal(itemid, 1));
      } else {
         chr.gainMeso(-100L, true, true, true);
         MapleInventoryManipulator.addById(
            c, itemid, (short)1, "Redeemed item through medal quest " + q.getId() + " on " + FileoutputUtil.CurrentReadable_Date()
         );
         c.getSession().writeAndFlush(CField.UIPacket.reissueMedal(itemid, 0));
      }
   }

   public static final void requestCharacterPotentialSkillRandSet(PacketDecoder slea, MapleClient c) {
      MapleCharacter User = c.getPlayer();
      List<Integer> fixedOptions = new ArrayList<>();
      int fixedSize = slea.readInt();

      for (int i = 0; i < fixedSize; i++) {
         fixedOptions.add(slea.readInt());
      }

      if (User.getInnerLevel() == 0) {
         System.out.println("[오류] 어빌리티를 활성화 하지 않은 상태로 어빌리티 스킬 변화를 시도하였습니다. (Name : " + User.getName() + ")");
      } else if (fixedOptions.size() > 2) {
         System.out.println("[오류] 어빌리티 스킬을 2개 이상 고정 시도하였습니다. (Name : " + User.getName() + ")");
      } else {
         int rare = 0;
         int epic = 1;
         int unique = 2;
         int legendary = 3;
         int userInnerRank = User.getInnerSkills().get(0).getRank();
         int OptionRequirePoint = 0;
         switch (userInnerRank) {
            case 0:
               OptionRequirePoint += InnerAbility.OptionRequirePointAbility.get("rare").get(fixedSize);
               break;
            case 1:
               OptionRequirePoint += InnerAbility.OptionRequirePointAbility.get("epic").get(fixedSize);
               break;
            case 2:
               OptionRequirePoint += InnerAbility.OptionRequirePointAbility.get("unique").get(fixedSize);
               break;
            case 3:
               OptionRequirePoint += InnerAbility.OptionRequirePointAbility.get("legendary").get(fixedSize);
         }

         if (OptionRequirePoint == 0) {
            User.send(CWvsContext.enableActions(User));
         } else {
            if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeAbility) {
               OptionRequirePoint /= 2;
            }

            if (User.getInnerExp() < OptionRequirePoint) {
               System.out.println("[오류] 어빌리티를 재설정 하는데 필요한 명성치가 부족합니다. (Name : " + User.getName() + ")");
            } else {
               User.addHonorExp(-OptionRequirePoint);
               InnerAbility.gachaAbility(User, fixedOptions);
            }
         }
      }
   }

   public static final void legendaryAbilityCir(PacketDecoder slea, MapleClient c) {
      MapleCharacter User = c.getPlayer();
      int itemID = slea.readInt();
      int itemSLOT = slea.readInt();
      if (User.getInventory(MapleInventoryType.USE).getItem((short)itemSLOT).getItemId() == itemID && itemID == 2702006) {
         if (User.getInnerSkills().size() < 1) {
            User.send(CWvsContext.enableActions(User));
         } else {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short)itemSLOT, (short)1, false);
            InnerAbility.gachaAbilityLegendary(User);
         }
      } else {
         User.send(CWvsContext.enableActions(User));
      }
   }

   public static final void chaosAbilityCir(PacketDecoder slea, MapleClient c) {
      MapleCharacter User = c.getPlayer();
      int itemID = slea.readInt();
      int itemSLOT = slea.readInt();
      if (User.getInventory(MapleInventoryType.USE).getItem((short)itemSLOT).getItemId() == itemID && (itemID == 2702003 || itemID == 2702004)) {
         if (User.getInnerSkills().size() < 1) {
            User.send(CWvsContext.enableActions(User));
         } else {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short)itemSLOT, (short)1, false);
            InnerAbility.gachaAbilityChaos(User);
         }
      } else {
         User.send(CWvsContext.enableActions(User));
      }
   }

   public static final void blackAbilityCir(PacketDecoder slea, MapleClient c) {
      MapleCharacter User = c.getPlayer();
      int itemID = slea.readInt();
      int itemSLOT = slea.readInt();
      if (User.getInventory(MapleInventoryType.USE).getItem((short)itemSLOT).getItemId() == itemID && (itemID == 2702007 || itemID == 2702008)) {
         if (User.getInnerSkills().size() < 1) {
            User.send(CWvsContext.enableActions(User));
         } else {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short)itemSLOT, (short)1, false);
            InnerAbility.gachaAbilityBlack(User, itemID, itemSLOT);
         }
      } else {
         User.send(CWvsContext.enableActions(User));
      }
   }

   public static final void selectBlackCir(PacketDecoder slea, MapleClient c) {
      MapleCharacter User = c.getPlayer();
      int tick = slea.readInt();
      byte type = slea.readByte();
      if (User.blackAbilitys.size() > 0 && type == 1) {
         if (User.getInnerSkills().size() < 1) {
            User.send(CWvsContext.enableActions(User));
            return;
         }

         User.getInnerSkills().clear();
         List<CharacterPotentialHolder> holders = new ArrayList<>();

         for (Triple<Integer, Integer, Integer> ability : User.blackAbilitys) {
            holders.add(
               new CharacterPotentialHolder(
                  ability.getLeft(),
                  (byte)ability.getMid().intValue(),
                  (byte)InnerAbility.innerAbilityInfos.get(ability.getLeft()).getMaxLevel(),
                  (byte)ability.getRight().intValue(),
                  false
               )
            );
         }

         for (CharacterPotentialHolder cph : holders) {
            User.getInnerSkills().add(cph);
         }

         User.setSaveFlag(User.getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());

         for (int i = 0; i < holders.size(); i++) {
            CharacterPotentialHolder holder = holders.get(i);
            User.send(CField.updateInnerPotential((byte)(i + 1), holder.getSkillId(), holder.getSkillLevel(), holder.getRank()));
            AchievementFactory.checkAbility(User, holders.get(i).getSkillId(), holders.get(i).getSkillLevel(), holders.get(i).getRank(), 0);
         }

         User.send(CWvsContext.showPopupMessage("어빌리티 재설정에 성공 하였습니다."));
         User.getStat().recalcLocalStats(User);
         User.blackAbilitys.clear();
      }
   }

   public static void forceAtomRegen(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int removeCount = slea.readInt();
         int skillID = slea.readInt();
         int room = 0;
         int unk = 0;
         int objectID = 0;
         switch (skillID) {
            case 0:
               int atomCountxxxx = slea.readInt();

               for (int nextxx = 0; nextxx < atomCountxxxx; nextxx++) {
                  int skillID2 = slea.readInt();
                  int keyxx = slea.readInt();
                  int posXxx = slea.readInt();
                  int var138 = slea.readInt();
               }

               for (int nextxx = 0; nextxx < removeCount; nextxx++) {
                  int atomKey = slea.readInt();
                  byte hasSubData = slea.readByte();
                  int fromMobID = slea.readInt();
                  slea.skip(4);
                  int keyxx = slea.readInt();
                  int x = slea.readInt();
                  int y = slea.readInt();
                  slea.skip(1);
                  skillID = slea.readInt();
                  ForceAtom atom = chr.getMap().getForceAtom(atomKey);
                  if (atom != null) {
                     if (atom.getForceAtomType().equals(ForceAtom.AtomType.BLACK_JACK)) {
                        chr.setBlackJackCount(0);
                        chr.send(CField.blackJack(400041024, chr.getSkillLevel(400041022), x, y));
                     }

                     if (GameConstants.isDemonSlayer(chr.getJob())) {
                        int delta = (int)(chr.getStat().getCurrentMaxMp(chr) - chr.getStat().getMp());
                        if (delta > 0) {
                           delta = Math.min(delta, atom.getInfo().inc);
                           chr.addMP(delta, true);
                        }
                     } else if (GameConstants.isAngelicBuster(chr.getJob())) {
                        SecondaryStatEffect effectxx = SkillFactory.getSkill(400051011).getEffect(chr.getSkillLevel(400051011));
                        if (effectxx != null) {
                           Integer valuex = chr.getBuffedValue(SecondaryStatFlag.EnergyBurst);
                           int vx = 0;
                           if (valuex != null) {
                              vx = valuex;
                           }

                           if (vx < 3) {
                              chr.setEnergyBurst(chr.getEnergyBurst() + atom.getInfo().inc);
                              if (chr.getEnergyBurst() >= effectxx.getW()) {
                                 chr.setEnergyBurst(0);
                                 vx++;
                                 int unit = vx - 1;
                                 SecondaryStatManager statManager = new SecondaryStatManager(chr.getClient(), chr.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.indiePMDR, 400051011, unit * effectxx.getQ());
                                 statManager.changeStatValue(SecondaryStatFlag.EnergyBurst, 400051011, vx);
                                 statManager.temporaryStatSet();
                              }

                              chr.getMap().removeForceAtom(keyxx);
                           }
                        }
                     }

                     if (atom.getForceAtomType() == ForceAtom.AtomType.HARMONY_WING_BEAT_TO_MOB) {
                        MapleMonster target = chr.getMap().getMonsterByOid(keyxx);
                        if (target != null) {
                           List<Pair<Integer, Integer>> list = new ArrayList<>();
                           list.add(new Pair<>(target.getObjectId(), 0));
                           chr.send(CField.userBonusAttackRequest(skillID, true, list));
                           target.tryApplyCurseMark(chr, 0);
                        }
                     } else if (atom.getForceAtomType() == ForceAtom.AtomType.HARMONY_WING_BEAT_TO_USER) {
                        MapleCharacter target = chr.getMap().getCharacterById(keyxx);
                        if (target != null) {
                           int blessMarkSKillID = chr.getBlessMarkSkillID();
                           SecondaryStatEffect blessMark = SkillFactory.getSkill(blessMarkSKillID).getEffect(chr.getTotalSkillLevel(blessMarkSKillID));
                           target.applyBlessMark(blessMark, 1, false, 0);
                        }
                     }

                     chr.getMap().removeForceAtom(atomKey);
                  } else if (skillID == 400011058) {
                     SecondaryStatEffect eff = SkillFactory.getSkill(400011060).getEffect(chr.getSkillLevel(400011058));
                     chr.getMap()
                        .spawnMist(
                           new AffectedArea(eff.calculateBoundingBox(new Point(x, y), false), chr, eff, new Point(x, y), System.currentTimeMillis() + 2000L)
                        );
                  } else if (skillID == 400011059) {
                     SecondaryStatEffect eff = SkillFactory.getSkill(400011061).getEffect(chr.getSkillLevel(400011059));
                     chr.getMap()
                        .spawnMist(
                           new AffectedArea(eff.calculateBoundingBox(new Point(x, y), false), chr, eff, new Point(x, y), System.currentTimeMillis() + 2000L)
                        );
                  }
               }
               break;
            case 14000028:
            case 14000029:
            case 14110033:
            case 14110034:
            case 14110035:
               int atomCountxxxxx = slea.readInt();
               SecondaryStatEffect effectxxx = SkillFactory.getSkill(14000027).getEffect(chr.getSkillLevel(14000027));
               if (effectxxx != null) {
                  for (int nextxxxx = 0; nextxxxx < atomCountxxxxx; nextxxxx++) {
                     int skillID2 = slea.readInt();
                     int keyxx = slea.readInt();
                     int posXxx = slea.readInt();
                     int var153 = slea.readInt();
                  }

                  int nextxxxx = 0;
                  if (nextxxxx < removeCount) {
                     int keyxx = slea.readInt();
                     slea.skip(1);
                     int byMobIDxx = slea.readInt();
                     int byPosX = slea.readShort() & 255;
                     int byPosY = slea.readShort() & 255;
                     int toMobIDxx = slea.readInt();
                     int posXxx = slea.readInt();
                     int posYxx = slea.readInt();
                     slea.skip(1);
                     slea.skip(4);
                     int mobCount = effectxxx.getMobCount();
                     if (chr.getSkillLevel(14100027) > 0) {
                        effectxxx = SkillFactory.getSkill(14100027).getEffect(chr.getSkillLevel(14100027));
                        if (effectxxx != null) {
                           mobCount += effectxxx.getMobCount();
                        }
                     }

                     if (chr.getSkillLevel(14110029) > 0) {
                        effectxxx = SkillFactory.getSkill(14110029).getEffect(chr.getSkillLevel(14110029));
                        if (effectxxx != null) {
                           mobCount += effectxxx.getMobCount();
                        }
                     }

                     if (chr.getSkillLevel(14120008) > 0) {
                        effectxxx = SkillFactory.getSkill(14120008).getEffect(chr.getSkillLevel(14120008));
                        if (effectxxx != null) {
                           mobCount += 3;
                        }
                     }

                     List<MapleMonster> mobs_ = chr.getMap().getMobsInRect(new Point(posXxx, posYxx), -600, -600, 600, 600);
                     Collections.shuffle(mobs_);
                     MapleMonster m = mobs_.stream().filter(m_ -> m_.getObjectId() != byMobIDxx).findAny().orElse(null);
                     if (mobs_.size() == 1) {
                        m = mobs_.get(0);
                        mobCount = effectxxx.getMobCount();
                     }

                     if (m == null) {
                        return;
                     }

                     if (skillID == 14000028 || skillID == 14110033) {
                        int x = chr.getSkillLevelDataOne(skillID == 14000028 ? 14001027 : 14121016, SecondaryStatEffect::getX);
                        int heal = (int)(chr.getStat().getCurrentMaxHp(chr) * 0.01 * x);
                        chr.healHP(heal);
                     }

                     if (skillID == 14110035 || skillID == 14110033 || skillID == 14110034) {
                        chr.sendRegisterExtraSkill(new Point(posXxx, posYxx), false, 14110034);
                     }

                     if (keyxx < mobCount) {
                        ForceAtom.AtomInfo atomInfox = new ForceAtom.AtomInfo();
                        atomInfox.initShadowBatRegen(++keyxx, new Point(posXxx, posYxx));
                        ForceAtom atom = new ForceAtom(
                           atomInfox,
                           skillID != 14110035 && skillID != 14110033 && skillID != 14110034 ? 14000029 : 14110035,
                           chr.getId(),
                           true,
                           true,
                           byMobIDxx,
                           ForceAtom.AtomType.SHADOW_BAT_REGEN,
                           Collections.singletonList(m.getObjectId()),
                           1
                        );
                        chr.getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                        chr.setBlackJackCount(chr.getBlackJackCount() + 1);
                     }
                  }
               }
               break;
            case 25100010:
            case 25120115:
            case 65111007:
            case 65111100:
            case 65120011:
            case 65141502:
               int atomCountxx = slea.readInt();
               int realSkillID = skillID;
               if (skillID == 65111007) {
                  realSkillID = 65111100;
               } else if (skillID == 65120011) {
                  realSkillID = 65121100;
               } else if (skillID == 65141502) {
                  realSkillID = 65141500;
               }

               SecondaryStatEffect effectx = SkillFactory.getSkill(realSkillID).getEffect(chr.getSkillLevel(realSkillID));
               if (effectx != null) {
                  for (int nextx = 0; nextx < atomCountxx; nextx++) {
                     int skillID2 = slea.readInt();
                     int keyx = slea.readInt();
                     int posXx = slea.readInt();
                     int var163 = slea.readInt();
                  }

                  int nextx = 0;
                  if (nextx < removeCount) {
                     int keyx = slea.readInt();
                     slea.skip(1);
                     int byMobIDx = slea.readInt();
                     int tickCountx = slea.readInt();
                     int toMobIDx = slea.readInt();
                     int posXx = slea.readInt();
                     int posYx = slea.readInt();
                     slea.skip(1);
                     slea.skip(4);
                     boolean active = false;
                     if (realSkillID == 65111100 || realSkillID == 65121100) {
                        int s = effectx.getS();
                        s += chr.getSkillLevelDataOne(65120044, SecondaryStatEffect::getProb);
                        if (realSkillID == 65121100) {
                           s = chr.getSkillLevelDataOne(65111100, SecondaryStatEffect::getS);
                        }

                        if (Randomizer.nextInt(100) < s) {
                           int zx = effectx.getZ();
                           if (realSkillID == 65121100) {
                              zx = chr.getSkillLevelDataOne(65111100, effx -> effx.getZ());
                           }

                           if (chr.getAtomRegenCount() < zx) {
                              active = true;
                           }
                        }
                     } else if (realSkillID == 65141500) {
                        int prop = chr.getSkillLevelDataOne(65111007, SecondaryStatEffect::getProb);
                        if (Randomizer.nextInt(100) < prop && chr.getAtomRegenCount() < effectx.getW()) {
                           active = true;
                        }
                     } else {
                        int add = 0;
                        int slvx = 0;
                        if ((slvx = chr.getTotalSkillLevel(25120153)) > 0) {
                           SecondaryStatEffect eff = SkillFactory.getSkill(25120153).getEffect(slvx);
                           if (eff != null) {
                              add = eff.getZ();
                           }
                        }

                        if (chr.getAtomRegenCount() <= effectx.getZ() + add) {
                           active = true;
                        }
                     }

                     if (active) {
                        ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
                        if (realSkillID == 65111100 || realSkillID == 65121100) {
                           atomInfo.initSoulSeekerRegen(posXx, posYx);
                        } else if (skillID == 65141502) {
                           atomInfo.initCheerballoonRegen();
                        } else {
                           atomInfo.initFoxGhostRegen(skillID, posXx, posYx);
                        }

                        ForceAtom atom = new ForceAtom(
                           atomInfo,
                           skillID,
                           chr.getId(),
                           true,
                           true,
                           byMobIDx,
                           ForceAtom.AtomType.ATOM_REGEN,
                           Collections.singletonList(toMobIDx),
                           atomCountxx
                        );
                        if (realSkillID == 65121100) {
                           atom = new ForceAtom(
                              atomInfo,
                              skillID,
                              chr.getId(),
                              true,
                              true,
                              byMobIDx,
                              ForceAtom.AtomType.AUTO_SOUL_SEEKER_REGEN,
                              Collections.singletonList(toMobIDx),
                              atomCountxx
                           );
                        } else if (skillID == 65141502) {
                           atom = new ForceAtom(
                              atomInfo,
                              skillID,
                              chr.getId(),
                              true,
                              true,
                              byMobIDx,
                              ForceAtom.AtomType.GrandFinale_Cheerballoon_REGEN,
                              Collections.singletonList(toMobIDx),
                              atomCountxx
                           );
                        }

                        chr.getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                        chr.setAtomRegenCount(chr.getAtomRegenCount() + 1);
                     } else {
                        if (chr.getMap().getForceAtom(keyx) != null) {
                           chr.getMap().removeForceAtom(keyx);
                        }

                        chr.setAtomRegenCount(0);
                     }
                  }
               }
               break;
            case 31221014:
            case 31241001:
               int atomCountx = slea.readInt();
               SecondaryStatEffect effect = SkillFactory.getSkill(31221001).getEffect(chr.getSkillLevel(31221001));
               if (effect != null) {
                  for (int nextx = 0; nextx < atomCountx; nextx++) {
                     int skillID2 = slea.readInt();
                     int key = slea.readInt();
                     int posX = slea.readInt();
                     int var149 = slea.readInt();
                  }

                  int nextx = 0;
                  if (nextx < removeCount) {
                     int key = slea.readInt();
                     slea.skip(1);
                     int byMobID = slea.readInt();
                     int tickCount = slea.readInt();
                     int toMobID = slea.readInt();
                     int posX = slea.readInt();
                     int posY = slea.readInt();
                     slea.skip(1);
                     slea.skip(4);
                     int mobID = slea.readInt();
                     int count = chr.getShieldChasingCount(key);
                     if (count == -1) {
                        return;
                     }

                     int z = effect.getZ();
                     int slv = 0;
                     if ((slv = chr.getTotalSkillLevel(31220050)) > 0) {
                        SecondaryStatEffect eff = SkillFactory.getSkill(31220050).getEffect(slv);
                        if (eff != null) {
                           z += eff.getZ();
                        }
                     }

                     z *= 2;
                     if (count < z - 2) {
                        ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
                        atomInfo.initShieldChasingRegen(key, posX, posY);
                        ForceAtom.AtomType atomType = ForceAtom.AtomType.ATOM_REGEN;
                        if (chr.getTotalSkillLevel(31241000) > 0) {
                           atomType = ForceAtom.AtomType.DAShieldChasing2_2;
                        }

                        ForceAtom atom = new ForceAtom(atomInfo, 31221014, chr.getId(), true, true, byMobID, atomType, Collections.singletonList(toMobID), 1);
                        chr.getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                        chr.addShieldChasingCount(key);
                     } else {
                        chr.resetShieldChasingCount(key);
                     }
                  }
               }
               break;
            case 152110004:
               int atomCountxxxxxx = slea.readInt();

               for (int nextxxxx = 0; nextxxxx < atomCountxxxxxx; nextxxxx++) {
                  int skillID2 = slea.readInt();
                  int keyxxx = slea.readInt();
                  int posXxxx = slea.readInt();
                  int posYxxx = slea.readInt();
                  chr.getMap().removeForceAtom(keyxxx);
               }

               for (int nextxxxx = 0; nextxxxx < removeCount; nextxxxx++) {
                  int atomKey = slea.readInt();
                  byte hasSubData = slea.readByte();
                  int fromMobID = slea.readInt();
                  slea.skip(4);
                  int keyxxx = slea.readInt();
                  int x = slea.readInt();
                  int y = slea.readInt();
                  slea.skip(1);
                  slea.skip(4);
               }

               ForceAtom.AtomInfo atomInfox = new ForceAtom.AtomInfo();
               SecondaryStatEffect e = SkillFactory.getSkill(152120016).getEffect(1);

               for (int ix = 0; ix < e.getBulletCount(); ix++) {
                  List<MapleMonster> mobs = chr.getMap().getMobsInRect(chr.getTruePosition(), e.getLt2().x, e.getLt2().y, e.getRb2().x, e.getRb2().y);
                  if (!mobs.isEmpty()) {
                     Collections.shuffle(mobs);
                     MapleMonster target = mobs.stream().findAny().orElse(null);
                     if (target != null) {
                        atomInfox.initMagicMissile();
                        ForceAtom atom = new ForceAtom(
                           atomInfox,
                           152120016,
                           chr.getId(),
                           false,
                           true,
                           chr.getId(),
                           ForceAtom.AtomType.MAGIC_MISSILE,
                           Collections.singletonList(target.getObjectId()),
                           1
                        );
                        chr.getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                     }
                  }
               }
               break;
            case 152120001:
            case 152141000:
               int boundCount = slea.readInt();

               for (int nextxxxx = 0; nextxxxx < boundCount; nextxxxx++) {
                  int skillID2 = slea.readInt();
                  int keyxxx = slea.readInt();
                  int posXxxx = slea.readInt();
                  int posYxxx = slea.readInt();
                  atomInfox = new ForceAtom.AtomInfo();
                  int effID = 152120002;
                  ForceAtom.AtomType atomType = ForceAtom.AtomType.CRAFT_JABELIN_SPLITTED;
                  if (skillID == 152141000) {
                     effID = 152141001;
                     atomType = ForceAtom.AtomType.CraftJavelinVI_Split;
                  }

                  e = SkillFactory.getSkill(effID).getEffect(1);
                  List<MapleMonster> mobs = chr.getMap().getMobsInRect(new Point(posXxxx, posYxxx), e.getLt().x, e.getLt().y, e.getRb().x, e.getRb().y);
                  if (!mobs.isEmpty()) {
                     CollectionUtil.sortMonsterByBossHP(mobs);
                  }

                  for (int ixx = 0; ixx < e.getBulletCount(); ixx++) {
                     if (!mobs.isEmpty()) {
                        MapleMonster target = mobs.remove(0);
                        if (target != null) {
                           atomInfox.initCraftJabelinSplitted(0, new Rect(e.getLt(), e.getRb()), new Point(posXxxx, posYxxx));
                           ForceAtom atom = new ForceAtom(
                              atomInfox, effID, chr.getId(), false, true, chr.getId(), atomType, Collections.singletonList(target.getObjectId()), 1
                           );
                           chr.getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                        }
                     }
                  }
               }

               for (int nextxxxx = 0; nextxxxx < removeCount; nextxxxx++) {
                  int atomKey = slea.readInt();
                  byte hasSubData = slea.readByte();
                  int fromMobID = slea.readInt();
                  slea.skip(4);
                  int keyxxxx = slea.readInt();
                  int x = slea.readInt();
                  int y = slea.readInt();
                  slea.skip(1);
                  slea.skip(4);
               }
               break;
            case 152141004:
               atomCountxxxxxx = slea.readInt();

               for (int nextxxxx = 0; nextxxxx < atomCountxxxxxx; nextxxxx++) {
                  int skillID2 = slea.readInt();
                  int keyxxx = slea.readInt();
                  int posXxxx = slea.readInt();
                  int posYxxx = slea.readInt();
                  chr.getMap().removeForceAtom(keyxxx);
               }

               for (int nextxxxx = 0; nextxxxx < removeCount; nextxxxx++) {
                  int atomKey = slea.readInt();
                  byte hasSubData = slea.readByte();
                  int fromMobID = slea.readInt();
                  slea.skip(4);
                  int keyxxx = slea.readInt();
                  int x = slea.readInt();
                  int y = slea.readInt();
                  slea.skip(1);
                  slea.skip(4);
               }

               atomInfox = new ForceAtom.AtomInfo();
               SecondaryStatEffect e4 = SkillFactory.getSkill(152141004).getEffect(1);

               for (int i = 0; i < e4.getBulletCount(); i++) {
                  List<MapleMonster> mobs = chr.getMap().getMobsInRect(chr.getTruePosition(), e4.getLt2().x, e4.getLt2().y, e4.getRb2().x, e4.getRb2().y);
                  if (!mobs.isEmpty()) {
                     Collections.shuffle(mobs);
                     MapleMonster target = mobs.stream().findAny().orElse(null);
                     if (target != null) {
                        atomInfox.initMagicMissile();
                        ForceAtom atom = new ForceAtom(
                           atomInfox,
                           152141005,
                           chr.getId(),
                           false,
                           true,
                           chr.getId(),
                           ForceAtom.AtomType.GloryWingJavelin_VI_MagicMissile,
                           Collections.singletonList(target.getObjectId()),
                           1
                        );
                        chr.getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                     }
                  }
               }
               break;
            case 400011131:
               int atomCount = slea.readInt();
               int next = 0;
               if (next < atomCount) {
                  int skillID2 = slea.readInt();
                  int key = slea.readInt();
                  int posX = slea.readInt();
                  int posY = slea.readInt();
                  chr.sendRegisterExtraSkill(new Point(posX, posY), false, 400011131);
               }
               break;
            case 400021045:
               chr.invokeJobMethod("atomRegen", slea, removeCount);
               break;
            case 400021069:
               atomCountxxxxxx = slea.readInt();

               for (int nextxxxx = 0; nextxxxx < atomCountxxxxxx; nextxxxx++) {
                  int skillID2x = slea.readInt();
                  int keyxxxx = slea.readInt();
                  int posXxxxx = slea.readInt();
                  int var122 = slea.readInt();
               }

               for (int ixxx = 0; ixxx < removeCount; ixxx++) {
                  slea.readInt();
                  slea.readByte();
                  slea.readInt();
                  slea.readInt();
                  slea.readInt();
                  slea.readInt();
                  slea.readInt();
                  slea.readByte();
                  slea.readInt();
                  int incGuage = slea.readInt();
                  if (incGuage > 2000) {
                     incGuage = 200;
                  }

                  SecondaryStatManager statManager = new SecondaryStatManager(chr.getClient(), chr.getSecondaryStat());
                  statManager.changeTill(SecondaryStatFlag.indieSummon, 400021069, incGuage);
                  statManager.temporaryStatSet();
               }
               break;
            case 400041023:
               atomCountxxxx = slea.readInt();
               SecondaryStatEffect effectxx = SkillFactory.getSkill(400041022).getEffect(chr.getSkillLevel(400041022));
               if (effectxx != null) {
                  for (int nextxxx = 0; nextxxx < atomCountxxxx; nextxxx++) {
                     int skillID2 = slea.readInt();
                     int keyxx = slea.readInt();
                     int posXxx = slea.readInt();
                     int var141 = slea.readInt();
                  }

                  for (int nextxxx = 0; nextxxx < removeCount; nextxxx++) {
                     int keyxx = slea.readInt();
                     slea.skip(1);
                     int byMobIDxx = slea.readInt();
                     int tickCountxx = slea.readInt();
                     int toMobIDxx = slea.readInt();
                     int posXxx = slea.readInt();
                     int posYxx = slea.readInt();
                     slea.skip(1);
                     slea.skip(4);
                     boolean blackJackBomb = slea.readByte() > 0;
                     int mobIDx = slea.readInt();
                     int specialPos1 = slea.readInt();
                     int specialPos2 = slea.readInt();
                     ForceAtom atom = chr.getMap().getForceAtom(keyxx);
                     if (atom != null) {
                        if (!blackJackBomb) {
                           if (atom.getAttackCount() < 6) {
                              atom.addAttackCount();
                              atom.setByMob(true);
                              atom.setToMob(true);
                              atom.setTargetMobObjectID(byMobIDxx);
                              atom.setTargetMobs(Collections.singletonList(mobIDx));
                              atom.getInfo().initBlackJackRegen(posXxx, posYxx);
                              atom.getInfo().specialPos1 = 0;
                              atom.getInfo().specialPos2 = 0;
                              chr.getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                           } else {
                              chr.setBlackJackCount(0);
                              chr.getMap().removeForceAtom(keyxx);
                              chr.send(CField.blackJack(400041024, chr.getSkillLevel(400041022), posXxx, posYxx));
                           }
                        } else {
                           int attackCount = 3;
                           List<ForceAtom> atoms = new ArrayList<>();

                           for (ForceAtom atomObj : chr.getMap().getForceAtoms()) {
                              if (atomObj.getOwnerID() == chr.getId() && atomObj.getSkillID() == 400041023) {
                                 atoms.add(atomObj);
                                 attackCount += 6 - atomObj.getAttackCount();
                              }
                           }

                           for (ForceAtom atomObjx : atoms) {
                              chr.getMap().removeForceAtom(atomObjx.getKey());
                           }

                           attackCount = Math.min(21, attackCount);
                           chr.send(CField.blackJack(400041080, chr.getSkillLevel(400041022), posXxx, posYxx, attackCount));
                        }
                     }
                  }
               }
               break;
            case 400051011:
               int atomCountxxx = slea.readInt();
               effectx = SkillFactory.getSkill(400051011).getEffect(chr.getSkillLevel(400051011));
               if (effectx != null) {
                  for (int nextx = 0; nextx < atomCountxxx; nextx++) {
                     int skillID2 = slea.readInt();
                     int keyxx = slea.readInt();
                     int posXxx = slea.readInt();
                     int posYxx = slea.readInt();
                     Summoned summoned = chr.getSummonBySkillID(400051011);
                     if (summoned != null) {
                        Integer value = chr.getBuffedValue(SecondaryStatFlag.EnergyBurst);
                        int v = 0;
                        if (value != null) {
                           v = value;
                        }

                        if (v < 3) {
                           ForceAtom atom = chr.getMap().getForceAtom(keyxx);
                           if (atom != null) {
                              chr.setEnergyBurst(chr.getEnergyBurst() + atom.getInfo().inc);
                              if (chr.getEnergyBurst() >= effectx.getW()) {
                                 chr.setEnergyBurst(0);
                                 v++;
                                 int unit = v - 1;
                                 SecondaryStatManager statManager = new SecondaryStatManager(chr.getClient(), chr.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.indiePMDR, 400051011, unit * effectx.getQ());
                                 statManager.changeStatValue(SecondaryStatFlag.EnergyBurst, 400051011, v);
                                 statManager.temporaryStatSet();
                              }

                              chr.getMap().removeForceAtom(keyxx);
                           }
                        }
                     }
                  }

                  int nextxx = 0;
                  if (nextxx < removeCount) {
                     int keyxx = slea.readInt();
                     slea.skip(1);
                     int byMobIDxx = slea.readInt();
                     int tickCountxx = slea.readInt();
                     int toMobIDxx = slea.readInt();
                     int posXxx = slea.readInt();
                     int posYxx = slea.readInt();
                  }
               }
         }
      }
   }

   public static void OrbitalFlame(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      int tempskill = slea.readInt();
      byte unk = slea.readByte();
      int direction = slea.readShort();
      slea.skip(8);
      slea.skip(1);
      slea.skip(4);
      slea.skip(1);
      slea.skip(1);
      slea.skip(4);
      int skillid = slea.readInt();
      slea.skip(4);
      slea.skip(4);
      slea.skip(1);
      slea.skip(4);
      slea.skip(4);
      slea.skip(4);
      slea.skip(4);
      int elementid = 0;
      int effect = 0;
      int mobCount = 8;
      switch (tempskill) {
         case 12001020:
            elementid = 12000022;
            int var17 = 4;
            break;
         case 12100020:
            elementid = 12100026;
            int var15 = 2;
            break;
         case 12110020:
            elementid = 12110024;
            int var14 = 3;
            break;
         case 12120006:
            elementid = 12120007;
            int var13 = 4;
            break;
         case 12120018:
            elementid = 12120007;
         case 12141000:
            elementid = 12120007;
      }

      if (elementid != 0) {
         if (SkillFactory.getSkill(elementid) == null) {
            FileoutputUtil.log("Log_Skill_Except.rtf", elementid + "스킬이 NULL임 ");
         } else {
            SecondaryStatEffect e = SkillFactory.getSkill(elementid).getEffect(chr.getTotalSkillLevel(tempskill));
            if (e != null && chr.getBuffedEffect(SecondaryStatFlag.indieSummon, elementid) == null) {
               e.applyTo(chr, true);
            }
         }
      }

      if (skillid >= 12141000 && skillid <= 12141006 && chr.getRemainCooltime(12141006) <= 0L) {
         chr.sendRegisterExtraSkill(chr.getPosition(), chr.isFacingLeft(), 12141000);
         chr.giveCoolDowns(12141006, System.currentTimeMillis(), 15000L);
         chr.send(CField.skillCooldown(12141006, 15000));
      }

      new ForceAtom.AtomInfo();
      SecondaryStatEffect flame = SkillFactory.getSkill(tempskill).getEffect(chr.getTotalSkillLevel(tempskill));
      if (flame != null) {
         flame.applyTo(chr, true);
         chr.applyOverloadMana(flame.getSourceId(), flame.getLevel());
      }
   }

   public static void keyDownAreaMovePath(PacketDecoder slea, MapleCharacter chr) {
      byte size = slea.readByte();
      byte[] path = new byte[size];

      for (int i = 0; i < size; i++) {
         path[i] = slea.readByte();
      }

      chr.getMap().broadcastMessage(chr, CField.keyDownAreaMovePath(chr.getId(), path), false);
   }

   public static void throwGrenade(PacketDecoder slea, MapleCharacter player) {
      int x = slea.readInt();
      int y = slea.readInt();
      slea.skip(4);
      slea.skip(4);
      int keyDown = slea.readInt();
      int skillID = slea.readInt();
      int bySummoned = slea.readInt();
      boolean isLeft = slea.readByte() == 1;
      slea.skip(4);
      int id = slea.readInt();
      int skillLevel = player.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillID));
      if (skillLevel > 0) {
         if (skillID == 61111100) {
            isLeft = !isLeft;
         }

         if (skillID == 400031036 || skillID == 400031067) {
            SecondaryStatEffect eff = SkillFactory.getSkill(400031036).getEffect(skillLevel);
            if (eff != null) {
               player.handleRelicChargeCon(skillID, eff.getForceCon(), 0);
            }
         }

         if (skillID == 11101029) {
            player.temporaryStatReset(SecondaryStatFlag.CosmikOrb);
         }

         if (skillID == 61111100 || skillID == 61111113 || skillID == 61111218) {
            List<Grenade> gCount = new ArrayList<>();

            for (Grenade g : player.getMap().getAllGrenadesThreadsafe()) {
               if ((g.getSkillId() == 61111100 || g.getSkillId() == 61111113 || g.getSkillId() == 61111218) && g.getOwnerId() == player.getId()) {
                  gCount.add(g);
               }
            }

            if (gCount.size() > 1) {
               Grenade fG = gCount.get(0);
               if (gCount.get(1).getStartTime() < gCount.get(0).getStartTime()) {
                  fG = gCount.get(1);
               }

               PacketEncoder p = new PacketEncoder();
               p.writeShort(SendPacketOpcode.DetonateBomb.getValue());
               p.writeInt(fG.getId());
               player.send(p.getPacket());
               player.getMap().removeGrenade(player, fG);
            }
         }

         boolean check = true;
         if (skillID == 400021131) {
            slea.skip(6);
            int idx = slea.readInt();
            if (idx != 0) {
               check = false;
            } else {
               SecondaryStatEffect level = player.getSkillLevelData(400021130);
               if (level != null) {
                  player.send(CField.skillCooldown(400021130, level.getCooldown(player)));
                  player.addCooldown(400021130, System.currentTimeMillis(), level.getCooldown(player));
               }
            }
         }

         if (skillID == 400031068) {
            check = false;
         }

         SecondaryStatEffect effect = player.getSkillLevelData(skillID);
         if (effect != null && check) {
            if (!player.checkSpiritFlow(skillID) && !GameConstants.isKeydownEndCooltimeSkill(skillID) && effect.getCooldown(player) > 0) {
               player.send(CField.skillCooldown(GameConstants.getLinkedAranSkill(skillID), effect.getCooldown(player)));
               player.addCooldown(GameConstants.getLinkedAranSkill(skillID), System.currentTimeMillis(), effect.getCooldown(player));
            }

            if (skillID != 400031003 && skillID != 22140024) {
               effect.applyTo(player);
            }
         }

         Grenade grenade = new Grenade(id, keyDown, skillID, skillLevel, bySummoned, new Point(x, y), isLeft);
         grenade.setOwnerId(player.getId());
         grenade.setStartTime(System.currentTimeMillis());
         player.getMap().spawnGrenade(player, grenade);
      }
   }

   public static void destroyGrenade(PacketDecoder slea, MapleCharacter player) {
      int id = slea.readInt();
      slea.skip(1);
      int skillID = slea.readInt();
      if (player != null) {
         Grenade grenade = player.getMap().getGrenadeById(id);
         if (grenade != null) {
            if (grenade.getSkillId() == skillID) {
               if (skillID == 400031036) {
                  SecondaryStatEffect eff = player.getSkillLevelData(3311009);
                  if (eff != null) {
                     eff.applyTo(player);
                  }
               }

               player.getMap().removeGrenade(player, grenade);
            }
         }
      }
   }

   public static void absorbingSword(PacketDecoder slea, MapleCharacter chr) {
      int skillID = slea.readInt();
      int mobSize = slea.readInt();
      List<Integer> oids = new ArrayList<>();

      for (int i = 0; i < mobSize; i++) {
         oids.add(slea.readInt());
      }

      int bossOid = 0;

      for (MapleMonster mob : chr.getMap().getMobsInRange(chr.getTruePosition(), 1000000.0, oids.size(), true)) {
         for (int i = 0; i < oids.size(); i++) {
            if (oids.get(i) == mob.getObjectId() && mob.getStats().isBoss()) {
               bossOid = mob.getObjectId();
               break;
            }
         }
      }

      if (bossOid != 0) {
         oids.clear();

         for (int ix = 0; ix < mobSize; ix++) {
            oids.add(bossOid);
         }
      }

      boolean isStrike = skillID == 400011058 || skillID == 400011059;
      if (isStrike) {
         SecondaryStatEffect eff = SkillFactory.getSkill(400011058).getEffect(chr.getTotalSkillLevel(400011058));
         if (eff != null) {
            int cooltime = eff.getCooldown(chr);
            chr.temporaryStatSet(400011058, cooltime, SecondaryStatFlag.DracoSlasher, 3);
            chr.send(CField.skillCooldown(400011079, 0));
            chr.addCooldown(400011079, System.currentTimeMillis(), 0L);
            chr.send(CField.skillCooldown(400011058, cooltime));
            chr.addCooldown(400011058, System.currentTimeMillis(), cooltime);
         }
      }

      if (chr.getBuffedValue(SecondaryStatFlag.StopForceAtomInfo) != null && chr.getBuffedValue(SecondaryStatFlag.StopForceAtomInfo) != null) {
         SecondaryStatEffect effect = chr.getBuffedEffect(SecondaryStatFlag.StopForceAtomInfo);
         if (effect != null) {
            int activeSkillID = chr.getBuffedEffect(SecondaryStatFlag.StopForceAtomInfo).getSourceId();
            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            boolean transform = activeSkillID == 61110211 || activeSkillID == 61121217 || activeSkillID == 61101002 || activeSkillID == 400011059;
            boolean advanced = activeSkillID == 61120007 || activeSkillID == 61121217 || activeSkillID == 61101002 || activeSkillID == 400011059;
            info.initWillOfSword(transform, isStrike);
            ForceAtom forceAtom = new ForceAtom(
               info,
               skillID,
               chr.getId(),
               false,
               true,
               chr.getId(),
               isStrike ? ForceAtom.AtomType.WILL_OF_SWORD_STRIKE : ForceAtom.AtomType.WILL_OF_SWORD,
               oids,
               advanced ? 5 : 3
            );
            chr.getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            chr.temporaryStatSet(SecondaryStatFlag.StopForceAtomInfo, activeSkillID, Integer.MAX_VALUE, effect.getLevel(), 0);
            chr.setJobField("lastWillOfSwordCharge", System.currentTimeMillis());
         }
      }
   }

   public static void DressUpRequest(MapleCharacter chr, PacketDecoder slea) {
      byte type = slea.readByte();
      if (type == 1 && !chr.isDressUp()) {
         if (GameConstants.isAngelicBuster(chr.getJob())) {
            chr.setDressUp(true);
            chr.getMap().broadcastMessage(CField.updateCharLook(chr));
            chr.getClient().getSession().writeAndFlush(CField.setDressChanged(true, true));
         }
      } else if (type == 0) {
         chr.setDressUp(false);
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
         chr.getMap().broadcastMessage(CField.updateCharLook(chr));
         return;
      }
   }

   public static void throwJoker(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      chr.invokeJobMethod("throwJokerResult", slea);
   }

   public static void arcaneCatalystPreview(PacketDecoder slea, MapleClient c, boolean unstability) {
      int toArcane = 0;
      if (unstability) {
         toArcane = slea.readInt();
      }

      int slot = slea.readInt();
      Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)slot);
      if (item != null) {
         Equip equip = (Equip)item.copy();
         String owner = equip.getOwner();
         int totalExp = 0;
         int finalExp = 0;
         if (toArcane > 0) {
            equip.setArc(30);

            for (int i = 1; i < equip.getArcLevel(); i++) {
               totalExp += GameConstants.getExpNeededForArcaneSymbol(i);
            }

            totalExp += equip.getArcEXP();
            finalExp = (int)Math.floor(totalExp * 0.8);
            equip.setArcEXP(finalExp);
            equip.setArcLevel(1);
         }

         equip.setStr((short)0);
         equip.setInt((short)0);
         equip.setDex((short)0);
         equip.setLuk((short)0);
         equip.setWatk((short)0);
         equip.setMatk((short)0);
         if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
            && c.getPlayer().getJob() != 512
            && c.getPlayer().getJob() != 1512
            && c.getPlayer().getJob() != 2512
            && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
            && !GameConstants.isAran(c.getPlayer().getJob())
            && !GameConstants.isBlaster(c.getPlayer().getJob())
            && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
            && !GameConstants.isMichael(c.getPlayer().getJob())
            && !GameConstants.isKaiser(c.getPlayer().getJob())
            && !GameConstants.isZero(c.getPlayer().getJob())
            && !GameConstants.isArk(c.getPlayer().getJob())
            && !GameConstants.isAdele(c.getPlayer().getJob())) {
            if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
               && !GameConstants.isFlameWizard(c.getPlayer().getJob())
               && !GameConstants.isEvan(c.getPlayer().getJob())
               && !GameConstants.isLuminous(c.getPlayer().getJob())
               && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
               && !GameConstants.isKinesis(c.getPlayer().getJob())
               && !GameConstants.isIllium(c.getPlayer().getJob())
               && !GameConstants.isLara(c.getPlayer().getJob())) {
               if ((c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                  && c.getPlayer().getJob() != 522
                  && c.getPlayer().getJob() != 532
                  && !GameConstants.isMechanic(c.getPlayer().getJob())
                  && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                  && !GameConstants.isMercedes(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                  if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                     && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                     && !GameConstants.isPhantom(c.getPlayer().getJob())
                     && !GameConstants.isKadena(c.getPlayer().getJob())
                     && !GameConstants.isHoyoung(c.getPlayer().getJob())
                     && !GameConstants.isKhali(c.getPlayer().getJob())) {
                     if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                        equip.setHp((short)6300);
                     } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                        equip.setStr((short)144);
                        equip.setDex((short)144);
                        equip.setLuk((short)144);
                     }
                  } else {
                     equip.setLuk((short)300);
                  }
               } else {
                  equip.setDex((short)300);
               }
            } else {
               equip.setInt((short)300);
            }
         } else {
            equip.setStr((short)300);
         }

         if (!owner.isEmpty()) {
            equip.setWatk((short)(equip.getWatk() + 750));
            equip.setMatk((short)(equip.getMatk() + 750));
            equip.setStr((short)(equip.getStr() + 1500));
            equip.setDex((short)(equip.getDex() + 1500));
            equip.setInt((short)(equip.getInt() + 1500));
            equip.setLuk((short)(equip.getLuk() + 1500));
         }

         if (toArcane > 0) {
            equip.setItemState(equip.getItemState() | ItemStateFlag.UNSTABLITY.getValue());
         } else {
            equip.setItemState(equip.getItemState() - ItemStateFlag.UNSTABLITY.getValue());
         }

         c.getPlayer().send(CWvsContext.arcaneCatalyst(equip, 20));
      }
   }

   public static void arcaneCatalystRequest(PacketDecoder slea, MapleClient c, boolean unstability) {
      int toArcane = 0;
      if (unstability) {
         toArcane = slea.readInt();
      }

      int slot = slea.readInt();
      Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)slot);
      if (item != null) {
         Equip equip = (Equip)item;
         int totalExp = 0;
         int finalExp = 0;
         if (toArcane > 0) {
            equip.setArc(30);

            for (int i = 1; i < equip.getArcLevel(); i++) {
               totalExp += GameConstants.getExpNeededForArcaneSymbol(i);
            }

            totalExp += equip.getArcEXP();
            finalExp = (int)Math.floor(totalExp * 0.8);
            equip.setArcEXP(finalExp);
            equip.setArcLevel(1);
         }

         equip.setStr((short)0);
         equip.setInt((short)0);
         equip.setDex((short)0);
         equip.setLuk((short)0);
         equip.setWatk((short)0);
         equip.setMatk((short)0);
         if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
            && c.getPlayer().getJob() != 512
            && c.getPlayer().getJob() != 532
            && c.getPlayer().getJob() != 1512
            && c.getPlayer().getJob() != 2512
            && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
            && !GameConstants.isAran(c.getPlayer().getJob())
            && !GameConstants.isBlaster(c.getPlayer().getJob())
            && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
            && !GameConstants.isMichael(c.getPlayer().getJob())
            && !GameConstants.isKaiser(c.getPlayer().getJob())
            && !GameConstants.isZero(c.getPlayer().getJob())
            && !GameConstants.isArk(c.getPlayer().getJob())
            && !GameConstants.isAdele(c.getPlayer().getJob())) {
            if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
               && !GameConstants.isFlameWizard(c.getPlayer().getJob())
               && !GameConstants.isEvan(c.getPlayer().getJob())
               && !GameConstants.isLuminous(c.getPlayer().getJob())
               && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
               && !GameConstants.isKinesis(c.getPlayer().getJob())
               && !GameConstants.isIllium(c.getPlayer().getJob())
               && !GameConstants.isLara(c.getPlayer().getJob())) {
               if ((c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                  && c.getPlayer().getJob() != 522
                  && !GameConstants.isMechanic(c.getPlayer().getJob())
                  && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                  && !GameConstants.isMercedes(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                  if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                     && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                     && !GameConstants.isPhantom(c.getPlayer().getJob())
                     && !GameConstants.isKadena(c.getPlayer().getJob())
                     && !GameConstants.isHoyoung(c.getPlayer().getJob())
                     && !GameConstants.isKhali(c.getPlayer().getJob())) {
                     if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                        equip.setHp((short)6300);
                     } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                        equip.setStr((short)144);
                        equip.setDex((short)144);
                        equip.setLuk((short)144);
                     }
                  } else {
                     equip.setLuk((short)300);
                  }
               } else {
                  equip.setDex((short)300);
               }
            } else {
               equip.setInt((short)300);
            }
         } else {
            equip.setStr((short)300);
         }

         if (!equip.getOwner().isEmpty()) {
            equip.setWatk((short)(equip.getWatk() + 750));
            equip.setMatk((short)(equip.getMatk() + 750));
            equip.setStr((short)(equip.getStr() + 1500));
            equip.setDex((short)(equip.getDex() + 1500));
            equip.setInt((short)(equip.getInt() + 1500));
            equip.setLuk((short)(equip.getLuk() + 1500));
         }

         if (toArcane > 0) {
            equip.setItemState(equip.getItemState() | ItemStateFlag.UNSTABLITY.getValue());
         } else {
            equip.setItemState(equip.getItemState() - ItemStateFlag.UNSTABLITY.getValue());
         }

         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short)toArcane, (short)1, false);
         c.getPlayer().send(CWvsContext.arcaneCatalyst2(equip));
         c.getPlayer().send(CWvsContext.InventoryPacket.updateArcaneSymbol(equip));
      }
   }

   public static void UpgradeAuthenticSymbol(PacketDecoder slea, MapleClient c, int p) {
      try {
         Equip item = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)p);
         if (item == null) {
            return;
         }

         if (c.getPlayer().getMeso() < GameConstants.getMesoNeededForAuthenticSymbolUpgrade(item.getArcLevel(), item.getItemId())) {
            return;
         }

         if (item.getArcLevel() >= 11) {
            c.getPlayer().dropMessage(1, "더 이상 강화할 수 없습니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (item.getArcEXP() <= 0) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (item.getArcEXP() < GameConstants.getExpNeededForAuthenticSymbol(item.getArcLevel())) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (item.getArcLevel() == 1) {
            item.setStr((short)0);
            item.setInt((short)0);
            item.setDex((short)0);
            item.setLuk((short)0);
            if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
               && c.getPlayer().getJob() != 512
               && c.getPlayer().getJob() != 1512
               && c.getPlayer().getJob() != 2512
               && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
               && !GameConstants.isAran(c.getPlayer().getJob())
               && !GameConstants.isBlaster(c.getPlayer().getJob())
               && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
               && !GameConstants.isMichael(c.getPlayer().getJob())
               && !GameConstants.isKaiser(c.getPlayer().getJob())
               && !GameConstants.isZero(c.getPlayer().getJob())
               && !GameConstants.isArk(c.getPlayer().getJob())
               && !GameConstants.isAdele(c.getPlayer().getJob())) {
               if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
                  && !GameConstants.isFlameWizard(c.getPlayer().getJob())
                  && !GameConstants.isEvan(c.getPlayer().getJob())
                  && !GameConstants.isLuminous(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
                  && !GameConstants.isKinesis(c.getPlayer().getJob())
                  && !GameConstants.isIllium(c.getPlayer().getJob())
                  && !GameConstants.isLara(c.getPlayer().getJob())) {
                  if (!GameConstants.isKain(c.getPlayer().getJob())
                     && (c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                     && c.getPlayer().getJob() != 522
                     && c.getPlayer().getJob() != 532
                     && !GameConstants.isMechanic(c.getPlayer().getJob())
                     && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                     && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                     && !GameConstants.isMercedes(c.getPlayer().getJob())
                     && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                     if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                        && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                        && !GameConstants.isPhantom(c.getPlayer().getJob())
                        && !GameConstants.isKadena(c.getPlayer().getJob())
                        && !GameConstants.isHoyoung(c.getPlayer().getJob())
                        && !GameConstants.isKhali(c.getPlayer().getJob())) {
                        if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                           item.setHp((short)1050);
                        } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                           item.setStr((short)240);
                           item.setDex((short)240);
                           item.setLuk((short)240);
                        }
                     } else {
                        item.setLuk((short)500);
                     }
                  } else {
                     item.setDex((short)500);
                  }
               } else {
                  item.setInt((short)500);
               }
            } else {
               item.setStr((short)500);
            }
         }

         c.getPlayer().gainMeso(-GameConstants.getMesoNeededForAuthenticSymbolUpgrade(item.getArcLevel(), item.getItemId()), true);
         item.setArcEXP(item.getArcEXP() - GameConstants.getExpNeededForAuthenticSymbol(item.getArcLevel()));
         item.setArcLevel(item.getArcLevel() + 1);
         item.setArc((short)(10 * item.getArcLevel()));
         if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
            && c.getPlayer().getJob() != 512
            && c.getPlayer().getJob() != 1512
            && c.getPlayer().getJob() != 2512
            && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
            && !GameConstants.isAran(c.getPlayer().getJob())
            && !GameConstants.isBlaster(c.getPlayer().getJob())
            && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
            && !GameConstants.isMichael(c.getPlayer().getJob())
            && !GameConstants.isKaiser(c.getPlayer().getJob())
            && !GameConstants.isZero(c.getPlayer().getJob())
            && !GameConstants.isArk(c.getPlayer().getJob())
            && !GameConstants.isAdele(c.getPlayer().getJob())) {
            if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
               && !GameConstants.isFlameWizard(c.getPlayer().getJob())
               && !GameConstants.isEvan(c.getPlayer().getJob())
               && !GameConstants.isLuminous(c.getPlayer().getJob())
               && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
               && !GameConstants.isKinesis(c.getPlayer().getJob())
               && !GameConstants.isIllium(c.getPlayer().getJob())
               && !GameConstants.isLara(c.getPlayer().getJob())) {
               if (!GameConstants.isKain(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                  && c.getPlayer().getJob() != 522
                  && c.getPlayer().getJob() != 532
                  && !GameConstants.isMechanic(c.getPlayer().getJob())
                  && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                  && !GameConstants.isMercedes(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                  if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                     && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                     && !GameConstants.isPhantom(c.getPlayer().getJob())
                     && !GameConstants.isKadena(c.getPlayer().getJob())
                     && !GameConstants.isHoyoung(c.getPlayer().getJob())
                     && !GameConstants.isKhali(c.getPlayer().getJob())) {
                     if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                        item.setHp((short)(item.getHp() + 420));
                     } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                        item.setStr((short)(item.getStr() + 96));
                        item.setDex((short)(item.getDex() + 96));
                        item.setLuk((short)(item.getLuk() + 96));
                     }
                  } else {
                     item.setLuk((short)(item.getLuk() + 200));
                  }
               } else {
                  item.setDex((short)(item.getDex() + 200));
               }
            } else {
               item.setInt((short)(item.getInt() + 200));
            }
         } else {
            item.setStr((short)(item.getStr() + 200));
         }

         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateArcaneSymbol(item));
         c.getPlayer().send(UpgradeAuthenticSymbol(p));
      } catch (Exception var5) {
         System.out.println("[오류] UpgradeAuthenticSymbol 함수 실행중 오류 발생 (캐릭터 이름 : " + c.getPlayer().getName() + ") " + var5.toString());
         var5.printStackTrace();
      }
   }

   public static void UpdateSymbol(PacketDecoder slea, MapleClient c) {
      try {
         int pos = slea.readInt() * -1;
         if (pos <= -1700) {
            UpgradeAuthenticSymbol(slea, c, pos);
            return;
         }

         Equip item = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)pos);
         if (item == null) {
            return;
         }

         if (c.getPlayer().getMeso() < GameConstants.getMesoNeededForArcaneSymbolUpgrade(item.getArcLevel(), item.getItemId())) {
            return;
         }

         if (item.getArcLevel() >= 20) {
            c.getPlayer().dropMessage(1, "더 이상 강화할 수 없습니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (item.getArcEXP() <= 0) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (item.getArcEXP() < GameConstants.getExpNeededForArcaneSymbol(item.getArcLevel())) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (item.getArcLevel() == 1 && item.getOwner().isEmpty()) {
            item.setStr((short)0);
            item.setInt((short)0);
            item.setDex((short)0);
            item.setLuk((short)0);
            if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
               && c.getPlayer().getJob() != 512
               && c.getPlayer().getJob() != 1512
               && c.getPlayer().getJob() != 2512
               && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
               && !GameConstants.isAran(c.getPlayer().getJob())
               && !GameConstants.isBlaster(c.getPlayer().getJob())
               && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
               && !GameConstants.isMichael(c.getPlayer().getJob())
               && !GameConstants.isKaiser(c.getPlayer().getJob())
               && !GameConstants.isZero(c.getPlayer().getJob())
               && !GameConstants.isArk(c.getPlayer().getJob())
               && !GameConstants.isAdele(c.getPlayer().getJob())) {
               if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
                  && !GameConstants.isFlameWizard(c.getPlayer().getJob())
                  && !GameConstants.isEvan(c.getPlayer().getJob())
                  && !GameConstants.isLuminous(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
                  && !GameConstants.isKinesis(c.getPlayer().getJob())
                  && !GameConstants.isIllium(c.getPlayer().getJob())
                  && !GameConstants.isLara(c.getPlayer().getJob())) {
                  if ((c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                     && c.getPlayer().getJob() != 522
                     && c.getPlayer().getJob() != 532
                     && !GameConstants.isMechanic(c.getPlayer().getJob())
                     && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                     && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                     && !GameConstants.isMercedes(c.getPlayer().getJob())
                     && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                     if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                        && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                        && !GameConstants.isPhantom(c.getPlayer().getJob())
                        && !GameConstants.isKadena(c.getPlayer().getJob())
                        && !GameConstants.isHoyoung(c.getPlayer().getJob())
                        && !GameConstants.isKhali(c.getPlayer().getJob())) {
                        if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                           item.setHp((short)630);
                        } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                           item.setStr((short)144);
                           item.setDex((short)144);
                           item.setLuk((short)144);
                        }
                     } else {
                        item.setLuk((short)300);
                     }
                  } else {
                     item.setDex((short)300);
                  }
               } else {
                  item.setInt((short)300);
               }
            } else {
               item.setStr((short)300);
            }
         }

         c.getPlayer().gainMeso(-GameConstants.getMesoNeededForArcaneSymbolUpgrade(item.getArcLevel(), item.getItemId()), true);
         item.setArcEXP(item.getArcEXP() - GameConstants.getExpNeededForArcaneSymbol(item.getArcLevel()) + 1);
         item.setArcLevel(item.getArcLevel() + 1);
         item.setArc((short)(10 * (item.getArcLevel() + 2)));
         if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
            && c.getPlayer().getJob() != 512
            && c.getPlayer().getJob() != 1512
            && c.getPlayer().getJob() != 2512
            && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
            && !GameConstants.isAran(c.getPlayer().getJob())
            && !GameConstants.isBlaster(c.getPlayer().getJob())
            && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
            && !GameConstants.isMichael(c.getPlayer().getJob())
            && !GameConstants.isKaiser(c.getPlayer().getJob())
            && !GameConstants.isZero(c.getPlayer().getJob())
            && !GameConstants.isArk(c.getPlayer().getJob())
            && !GameConstants.isAdele(c.getPlayer().getJob())) {
            if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
               && !GameConstants.isFlameWizard(c.getPlayer().getJob())
               && !GameConstants.isEvan(c.getPlayer().getJob())
               && !GameConstants.isLuminous(c.getPlayer().getJob())
               && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
               && !GameConstants.isKinesis(c.getPlayer().getJob())
               && !GameConstants.isIllium(c.getPlayer().getJob())
               && !GameConstants.isLara(c.getPlayer().getJob())) {
               if (!GameConstants.isKain(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                  && c.getPlayer().getJob() != 522
                  && c.getPlayer().getJob() != 532
                  && !GameConstants.isMechanic(c.getPlayer().getJob())
                  && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                  && !GameConstants.isMercedes(c.getPlayer().getJob())
                  && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                  if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                     && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                     && !GameConstants.isPhantom(c.getPlayer().getJob())
                     && !GameConstants.isKadena(c.getPlayer().getJob())
                     && !GameConstants.isHoyoung(c.getPlayer().getJob())
                     && !GameConstants.isKhali(c.getPlayer().getJob())) {
                     if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                        item.setHp((short)(item.getHp() + 210));
                     } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                        item.setStr((short)(item.getStr() + 48));
                        item.setDex((short)(item.getDex() + 48));
                        item.setLuk((short)(item.getLuk() + 48));
                     }
                  } else {
                     item.setLuk((short)(item.getLuk() + 100));
                  }
               } else {
                  item.setDex((short)(item.getDex() + 100));
               }
            } else {
               item.setInt((short)(item.getInt() + 100));
            }
         } else {
            item.setStr((short)(item.getStr() + 100));
         }

         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateArcaneSymbol(item));
         c.getPlayer().send(UpgradeArcaneSymbol(pos));
      } catch (Exception var4) {
         System.out.println("[오류] UpdateSymbol 함수 실행중 오류 발생 (캐릭터 이름 : " + c.getPlayer().getName() + ") " + var4.toString());
         var4.printStackTrace();
      }
   }

   public static byte[] UpgradeArcaneSymbol(int pos) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ARCANE_SYMBOL_UPDATE.getValue());
      p.writeInt(1);
      p.writeInt(0);
      p.writeInt(pos);
      return p.getPacket();
   }

   public static byte[] UpgradeAuthenticSymbol(int pos) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.AUTHENTIC_SYMBOL_UPDATE.getValue());
      p.writeInt(1);
      p.writeInt(0);
      p.writeInt(pos);
      return p.getPacket();
   }

   public static void SymbolExp(PacketDecoder slea, MapleClient c) {
      MapleCharacter User = c.getPlayer();
      int command = slea.readInt();
      switch (command) {
         case 0:
            int srcItemSlot = slea.readInt();
            int targetItemSlot = slea.readInt();
            Equip src = (Equip)User.getInventory(MapleInventoryType.EQUIP).getItem((short)srcItemSlot);
            Equip target = (Equip)User.getInventory(MapleInventoryType.EQUIPPED).listById(src.getItemId()).get(0);
            if (!GameConstants.isArcaneSymbol(src.getItemId()) && !GameConstants.isAuthenticSymbol(src.getItemId())) {
               return;
            }

            if (!GameConstants.isArcaneSymbol(target.getItemId()) && !GameConstants.isAuthenticSymbol(target.getItemId())) {
               return;
            }

            if (src != null && target != null) {
               if (src.getItemId() == target.getItemId()) {
                  if (!src.getOwner().isEmpty()) {
                     c.getPlayer().dropMessage(1, "강화된 심볼은 합칠 수 없습니다.");
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.mergeArcaneSymbol(target, src));
                  User.getInventory(MapleInventoryType.EQUIP).removeSlot((short)srcItemSlot);
                  int incEXP = src.getArcEXP();
                  if (src.getArcLevel() > 1) {
                     incEXP = 0;

                     for (int i = 1; i < src.getArcLevel(); i++) {
                        if (GameConstants.isAuthenticSymbol(src.getItemId())) {
                           incEXP += GameConstants.getExpNeededForAuthenticSymbol(i);
                        } else {
                           incEXP += GameConstants.getExpNeededForArcaneSymbol(i);
                        }
                     }

                     incEXP += src.getArcEXP();
                  }

                  target.setArcEXP(target.getArcEXP() + incEXP);
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateArcaneSymbol(target));
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
            break;
         case 1:
            UpdateSymbol(slea, c);
            break;
         case 2:
            int itemId = slea.readInt();
            if (!GameConstants.isArcaneSymbol(itemId) && !GameConstants.isAuthenticSymbol(itemId)) {
               return;
            }

            int canMergeQty = slea.readInt();
            int haveNumber = slea.readInt();
            List<Equip> equips = new ArrayList<>();
            List<Item> items = User.getInventory(MapleInventoryType.EQUIP).listById(itemId);
            if (items.size() < haveNumber) {
               return;
            }

            Equip targetItem = (Equip)User.getInventory(MapleInventoryType.EQUIPPED).listById(itemId).get(0);

            for (Item item : items) {
               Equip symbol = (Equip)item;
               if (symbol.getArcLevel() == 1 && symbol.getArcEXP() == 1 && symbol.getOwner().isEmpty()) {
                  equips.add(symbol);
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.mergeArcaneSymbol(targetItem, symbol));
                  User.getInventory(MapleInventoryType.EQUIP).removeSlot(symbol.getPosition());
                  targetItem.setArcEXP(targetItem.getArcEXP() + 1);
                  if (equips.size() == canMergeQty) {
                     break;
                  }
               }
            }

            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateArcaneSymbol(targetItem));
      }
   }

   public static void createPsychicLock(PacketDecoder slea, MapleClient c) {
      int skillid = slea.readInt();
      short skillLevel = slea.readShort();
      int action = slea.readInt();
      int actionSpeed = slea.readInt();
      boolean end = false;
      List<PsychicLock.Info> locks = new LinkedList<>();
      int index = 1;

      while (!end) {
         end = slea.readByte() == 0;
         if (end) {
            break;
         }

         PsychicLock.Info lock = new PsychicLock.Info();
         lock.setPsychicLockKey(index++);
         lock.setLocalKey(slea.readInt());
         slea.skip(4);
         lock.setMobId(slea.readInt());
         lock.setStuffId(slea.readShort());
         lock.setUnkNew(slea.readInt());
         slea.skip(2);
         lock.setLeft(slea.readByte() == 1);
         lock.setStartPosX(slea.readInt());
         lock.setStartPosY(slea.readInt());
         lock.setRelX(slea.readInt());
         lock.setRelY(slea.readInt());
         locks.add(lock);
      }

      PsychicLock lock = new PsychicLock(c.getPlayer(), skillid, skillLevel, action, actionSpeed, locks);
      c.getPlayer().setPsychicLock(lock);
      c.getPlayer().getMap().broadcastMessage(CField.getCreatePsychicLock(lock));
   }

   public static void recreatePathPsychicLock(PacketDecoder slea, MapleClient c) {
      int skillId = slea.readInt();
      short skillLevel = slea.readShort();
      int action = slea.readInt();
      int actionSpeed = slea.readInt();
      boolean isLeft = slea.readByte() == 1;
      int skillInfoSize = slea.readInt();
      List<Pair<Integer, Integer>> skillInfos = new LinkedList<>();

      for (int next = 0; next < skillInfoSize; next++) {
         Pair<Integer, Integer> skillInfo = new Pair<>(null, null);
         skillInfo.left = slea.readInt();
         skillInfo.right = slea.readInt();
         skillInfos.add(skillInfo);
      }

      int keySize = slea.readInt();
      List<Integer> keys = new LinkedList<>();

      for (int next = 0; next < keySize; next++) {
         keys.add(slea.readInt());
         slea.skip(4);
      }

      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         if (skillId == 142120002) {
            int pp = chr.getSkillLevelDataOne(142120002, SecondaryStatEffect::getPPCon);
            Integer value;
            if ((value = chr.getBuffedValue(SecondaryStatFlag.KinesisPsychicOver)) != null) {
               pp = (100 - Math.max(0, Math.min(100, value))) * pp / 100;
            }

            chr.invokeJobMethod("gainPP", -pp);
         }

         chr.getMap().broadcastMessage(CField.getRecreatePathPsychicLock(chr.getId(), skillId, skillLevel, action, actionSpeed, isLeft, skillInfos, keys));
      }
   }

   public static void releasePsychicLock(PacketDecoder slea, MapleClient c) {
      slea.skip(8);
      c.getPlayer().getMap().broadcastMessage(CField.getReleasePsychicLock(c.getPlayer().getId(), slea.readInt()));
      if (c.getPlayer().getPsychicLock() != null) {
         c.getPlayer().setPsychicLock(null);
      }
   }

   public static void releasePsychicLockMob(PacketDecoder slea, MapleClient c) {
      int mobId = slea.readInt();
      c.getPlayer().getMap().broadcastMessage(CField.getReleasePsychicLockMob(c.getPlayer().getId(), mobId));
   }

   public static void psychicTornadoActiveBuff(PacketDecoder slea, MapleClient c) {
      int key = slea.readInt();
      if (c.getPlayer().getPsychicArea(key) != null) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400021008).getEffect(400021008);
         Integer value = c.getPlayer().getBuffedValue(SecondaryStatFlag.PsychicTornado);
         if (value == null) {
            if (effect != null) {
               c.getPlayer().setPsychicTornadoStartTime(System.currentTimeMillis());
               c.getPlayer().temporaryStatSet(400021008, effect.getDuration() + 2000, SecondaryStatFlag.PsychicTornado, 1);
            }
         } else if (value < 3) {
            int duration = (int)(effect.getDuration() - (System.currentTimeMillis() - c.getPlayer().getPsychicTornadoStartTime())) + 2000;
            c.getPlayer().temporaryStatSet(400021008, duration, SecondaryStatFlag.PsychicTornado, value + 1);
         }
      }
   }

   public static void createPsychicArea(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         PsychicArea area = new PsychicArea();
         area.setAction(slea.readInt());
         area.setSpeed(slea.readInt());
         slea.skip(4);
         int key = chr.getAndIncPsychicAreaIdx();
         area.setKey(key);
         area.setV6(slea.readInt());
         int skillID = slea.readInt();
         short skillLevel = slea.readShort();
         area.setSkillId(skillID);
         area.setSkillLevel(skillLevel);
         int duration = slea.readInt();
         if (skillID != 142111006 && skillID != 142120003) {
            if (skillID == 142121030 || skillID == 142120030) {
               SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(chr.getSkillLevel(skillID));
               chr.temporaryStatSet(142121030, 5000, SecondaryStatFlag.NotDamaged, 1);
               if (chr.getCooldownLimit(142121030) <= 0L) {
                  chr.send(CField.skillCooldown(142121030, effect.getCooldown(chr)));
                  chr.addCooldown(142121030, System.currentTimeMillis(), effect.getCooldown(chr));
               }

               chr.invokeJobMethod("gainPP", 40);
            } else if (skillID == 142121005) {
               SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(chr.getSkillLevel(skillID));
               if (effect == null) {
                  return;
               }

               if (chr.getCooldownLimit(142121005) <= 0L) {
                  chr.send(CField.skillCooldown(142121005, effect.getCooldown(chr)));
                  chr.addCooldown(142121005, System.currentTimeMillis(), effect.getCooldown(chr));
               }

               if (chr.getBuffedEffect(SecondaryStatFlag.UltimateBPM, 142121005) == null) {
                  chr.temporaryStatSet(142121005, Integer.MAX_VALUE, SecondaryStatFlag.UltimateBPM, 1);
               } else {
                  chr.temporaryStatResetBySkillID(SecondaryStatFlag.UltimateBPM, 142121005);
               }
            } else if (skillID == 400021008) {
               SecondaryStatEffect effectx = SkillFactory.getSkill(skillID).getEffect(chr.getSkillLevel(skillID));
               if (effectx == null) {
                  return;
               }

               if (chr.getCooldownLimit(skillID) <= 0L) {
                  chr.send(CField.skillCooldown(skillID, effectx.getCooldown(chr)));
                  chr.addCooldown(skillID, System.currentTimeMillis(), effectx.getCooldown(chr));
               }
            } else if (skillID == 142101009) {
               SecondaryStatEffect effectxx = SkillFactory.getSkill(skillID).getEffect(chr.getSkillLevel(skillID));
               if (effectxx == null) {
                  return;
               }

               chr.send(CField.skillCooldown(142101009, effectxx.getCooldown(chr)));
               chr.addCooldown(142101009, System.currentTimeMillis(), effectxx.getCooldown(chr));
            } else if (skillID == 142111007) {
               int pp = chr.getSkillLevelDataOne(142111007, SecondaryStatEffect::getPPCon);
               Integer value = null;
               if ((value = chr.getBuffedValue(SecondaryStatFlag.KinesisPsychicOver)) != null) {
                  pp = (100 - Math.max(0, Math.min(100, value))) * pp / 100;
               }

               chr.invokeJobMethod("gainPP", -pp);
            } else if (skillID == 142001002 || skillID == 142141000) {
               chr.invokeJobMethod("givePPoint", skillID, false, (byte)0);
            }
         } else if (chr.getSkillLevel(142120038) > 0) {
            SecondaryStatEffect effectxx = SkillFactory.getSkill(142120038).getEffect(chr.getSkillLevel(142120038));
            if (effectxx != null) {
               duration += effectxx.getDuration();
            }
         }

         area.setDurationTIme(duration);
         area.setIsLeft_second(slea.readByte() == 1);
         area.setSkeletonFilePathIdx(slea.readShort());
         area.setSkeletonAniIdx(slea.readShort());
         area.setSkeletonLoop(slea.readShort());
         area.setPosStartX(slea.readInt());
         area.setPosStartY(slea.readInt());
         chr.addPsychicArea(area);
         if (skillID == 400021008) {
            chr.invokeJobMethod("givePPoint", 400021008, true, (byte)0);
         }

         chr.getMap().broadcastMessage(CField.getCreatePsychicArea(chr.getId(), area));
      }
   }

   public static void releasePsychicArea(PacketDecoder slea, MapleClient c) {
      int psychicKey = slea.readInt();
      if (psychicKey >= 0) {
         PsychicArea area = c.getPlayer().getPsychicArea(psychicKey);
         if (area != null) {
            c.getPlayer().removePsychicArea(psychicKey);
            c.getPlayer().setActivePsychicAreaCount(1);
            c.getPlayer().setPsychicTornadoStartTime(0L);
            c.getPlayer().getMap().broadcastMessage(CField.getReleasePsychicArea(c.getPlayer().getId(), psychicKey));
         }
      }
   }

   public static void doActivePsychicArea(PacketDecoder slea, MapleClient c) {
      int psychicKey = slea.readInt();
      PsychicArea area = c.getPlayer().getPsychicArea(psychicKey);
      if (area != null) {
         int skillid = area.getSkillId();
         if (skillid > 0) {
            SecondaryStatEffect effect = SkillFactory.getSkill(skillid).getEffect(c.getPlayer().getSkillLevel(skillid));
            if (effect == null) {
               return;
            }
         }

         c.getPlayer().getMap().broadcastMessage(CField.getDoActivePsychicArea(psychicKey, c.getPlayer().getAndAddActivePsychicAreaCount()));
      }
   }

   public static void debuffPsychicArea(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int skillID = slea.readInt();
            short skillLevel = slea.readShort();
            int areaKey = slea.readInt();
            slea.skip(1);
            new Point(slea.readInt(), slea.readInt());
            PsychicArea area = c.getPlayer().getPsychicArea(areaKey);
            if (area != null) {
               SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
               if (effect != null) {
                  short mobCount = slea.readShort();
                  List<MapleMonster> mobs = new ArrayList<>();

                  for (int i = 0; i < mobCount; i++) {
                     int mobID = slea.readInt();
                     MapleMonster mob = map.getMonsterByOid(mobID);
                     if (mob != null) {
                        mobs.add(mob);
                     }
                  }

                  short dCount = slea.readShort();

                  for (int ix = 0; ix < dCount; ix++) {
                     slea.readInt();
                  }

                  int count = mobs.size();
                  int hyper = player.getSkillLevelDataOne(142120036, SecondaryStatEffect::getX);
                  if (skillID == 142111006 || skillID == 142120003) {
                     int add = hyper * count;
                     int dr = effect.getY() * count + add - effect.getS();
                     Map<MobTemporaryStatFlag, MobTemporaryStatEffect> statups = new HashMap<>();
                     statups.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, dr, skillID, null, false));
                     statups.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, dr, skillID, null, false));
                     int speed = effect.getZ() * count - effect.getS();
                     statups.put(MobTemporaryStatFlag.SPEED, new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, speed, skillID, null, false));
                     int groundMark = effect.getX() * count + effect.getS();
                     statups.put(
                        MobTemporaryStatFlag.PSYCHIC_GROUND_MARK,
                        new MobTemporaryStatEffect(MobTemporaryStatFlag.PSYCHIC_GROUND_MARK, groundMark, skillID, null, false)
                     );

                     for (MapleMonster mob : mobs) {
                        mob.applyMonsterBuff(statups, skillID, effect.getDuration(), null, Collections.EMPTY_LIST);
                     }
                  }
               }
            }
         }
      }
   }

   public static void setDefaultWingItem(PacketDecoder slea, MapleClient c) {
      int itemId = slea.readInt();
      c.getPlayer().setDefaultWingItem(itemId);
      c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.setDefaultWingItem(c.getPlayer().getId(), itemId), false);
   }

   public static void battleRecordServerOnCalcRequest(PacketDecoder slea, MapleClient c) {
      boolean active = slea.readByte() == 1;
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         chr.setBattleRecordOnCalc(active);
      }
   }

   public static void chainArtsTakeDown(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         Object sonicBlowCount = chr.getJobField("sonicBlowAttackCount");
         if (sonicBlowCount != null) {
            if ((Integer)sonicBlowCount > 0) {
               int reduce = 2500 * (15 - (Integer)sonicBlowCount);
               chr.changeCooldown(400041039, -reduce);
               chr.setJobField("sonicBlowAttackCount", 0);
            }
         } else {
            slea.skip(4);
            slea.skip(10);
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.CHAIN_ARTS_TAKE_DOWN.getValue());
            p.writeInt(chr.getId());
            int skillID = slea.readInt();
            p.writeInt(skillID);
            p.writeInt(slea.readInt());
            p.write(slea.readByte());
            chr.getMap().broadcastMessage(chr, p.getPacket(), false);
            if (GameConstants.isKadena(chr.getJob())) {
               chr.invokeJobMethod("applyWeaponVariety", 64121001, true, null);
            }

            if (GameConstants.isPhantom(chr.getJob())) {
               slea.readInt();
               slea.readInt();
               slea.readInt();
               slea.readByte();
               int remainCount = slea.readInt();
               if (chr.getRemainCooltime(skillID) > 0L) {
                  int cooltime = (int)(chr.getRemainCooltime(skillID) - remainCount * 3500);
                  chr.giveCoolDowns(skillID, System.currentTimeMillis(), cooltime);
                  chr.send(CField.skillCooldown(skillID, cooltime));
               }
            }
         }
      }
   }

   public static void activeIllusionaryShot(PacketDecoder slea, MapleClient c) {
      int targetID = slea.readInt();
      ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
      info.initIllusionaryShot(c.getPlayer().isFacingLeft());
      if (c.getPlayer().hasBuffBySkillID(400031020)) {
         ForceAtom atom = new ForceAtom(
            info,
            400031020,
            c.getPlayer().getId(),
            false,
            true,
            c.getPlayer().getId(),
            ForceAtom.AtomType.ILLUSIONARY_SHOT,
            Collections.singletonList(targetID),
            1
         );
         c.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
      } else if (c.getPlayer().hasBuffBySkillID(400031021)) {
         ForceAtom atom = new ForceAtom(
            info,
            400031021,
            c.getPlayer().getId(),
            false,
            true,
            c.getPlayer().getId(),
            ForceAtom.AtomType.ILLUSIONARY_SHOT,
            Collections.singletonList(targetID),
            1
         );
         c.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
      }
   }

   public static void activePrayBuff(MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Party party = player.getParty();
         SecondaryStatEffect effect = player.getBuffedEffect(SecondaryStatFlag.Pray);
         if (effect != null) {
            int incPMdRBishop = 0;
            if (effect.getSourceId() == 500061002) {
               incPMdRBishop = effect.getV2();
               effect = SkillFactory.getSkill(400021003).getEffect(player.getTotalSkillLevel(400021003));
            }

            int int_ = player.getStat().getTotalInt();
            int incPMdR = effect.getQ();
            incPMdR = Math.min(effect.getW(), incPMdR + int_ / effect.getQ2());
            int booster = Math.max(int_ / -effect.getU(), effect.getV());
            int incRecovery = int_ / effect.getY();
            if (party != null) {
               for (PartyMemberEntry pc : party.getPartyMemberList()) {
                  if (pc.isOnline() && pc.getFieldID() == player.getMapId() && pc.getChannel() == player.getClient().getChannel()) {
                     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(pc.getName());
                     if (victim != null && victim.isAlive()) {
                        Rectangle bounds = effect.calculateBoundingBox(player.getPosition(), player.isFacingLeft());

                        for (MapleMapObject ch : player.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER))) {
                           MapleCharacter chr = (MapleCharacter)ch;
                           if (chr.getId() == victim.getId()) {
                              int incRecoveryHP = Math.min(effect.getZ(), incRecovery + effect.getX());
                              int incRecoveryMP = Math.min(effect.getZ(), incRecovery + effect.getX());
                              incRecoveryHP = (int)(victim.getStat().getCurrentMaxHp() * (incRecoveryHP * 0.01));
                              incRecoveryMP = (int)(victim.getStat().getCurrentMaxMp(chr) * (incRecoveryMP * 0.01));
                              victim.addMPHP(incRecoveryHP, incRecoveryMP);
                              HPHeal e = new HPHeal(victim.getId(), incRecoveryHP);
                              victim.send(e.encodeForLocal());
                              victim.getMap().broadcastMessage(victim, e.encodeForRemote(), false);
                              if (victim.getId() != player.getId()) {
                                 Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                                 statups.put(SecondaryStatFlag.indiePMDR, incPMdR);
                                 statups.put(SecondaryStatFlag.indieBooster, booster);
                                 victim.temporaryStatSet(400021003, effect.getLevel(), 10000, statups);
                                 victim.dispelDebuffs();
                              }
                           }
                        }
                     }
                  }
               }
            }

            player.temporaryStatSet(400021003, 10000, SecondaryStatFlag.indiePMDR, incPMdR + incPMdRBishop);
         }
      }
   }

   public static void shadowServantExtendChangePos(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      byte mode = slea.readByte();
      Point pos = null;
      if (!c.getPlayer().isShadowServantChangePos()) {
         if (mode == 1) {
            pos = c.getPlayer().getTruePosition();
         } else {
            for (Summoned summon : c.getPlayer().getSummons()) {
               if (summon.getSkill() == skillID) {
                  pos = summon.getTruePosition();
                  break;
               }
            }
         }

         if (pos != null) {
            SecondaryStatEffect effect = c.getPlayer().getSkillLevelData(skillID);
            if (effect != null && c.getPlayer().getShadowServantChangePosCount() < effect.getW()) {
               c.getPlayer().send(CField.getShadowServantExtendChangePos(pos.x, pos.y, mode));
               c.getPlayer().setShadowServantChangePosCount(c.getPlayer().getShadowServantChangePosCount() + 1);
            }
         }
      }
   }

   public static void megaSmasherRequest(PacketDecoder slea, MapleClient c) {
      boolean start = slea.readByte() == 1;
      MapleCharacter player = c.getPlayer();
      SecondaryStatEffect effect = SkillFactory.getSkill(400041007).getEffect(player.getSkillLevel(400041007));
      if (effect != null) {
         if (player.hasBuffBySkillID(36141502)) {
            if (start) {
               player.temporaryStatSet(400041007, Integer.MAX_VALUE, SecondaryStatFlag.MegaSmasher, -25);
               player.setMegaSmasherChargeStartTime(System.currentTimeMillis());
            } else {
               int maxChargeTime = effect.getDuration() + effect.getZ() * 1000;
               int chargeTime = Math.min(
                  maxChargeTime,
                  effect.getDuration() + (int)((System.currentTimeMillis() - player.getMegaSmasherChargeStartTime()) / (effect.getY() * 1000)) * 1000
               );
               player.send(CField.megaSmasherAttack(100, player.getPosition(), player.isFacingLeft(), chargeTime));
               player.temporaryStatResetBySkillID(400041007);
               player.setMegaSmasherChargeStartTime(0L);
            }
         } else if (start) {
            player.temporaryStatSet(400041007, Integer.MAX_VALUE, SecondaryStatFlag.MegaSmasher, -1);
            player.setMegaSmasherChargeStartTime(System.currentTimeMillis());
         } else {
            Integer value = player.getBuffedValue(SecondaryStatFlag.MegaSmasher);
            if (value != null && value != -1) {
               player.temporaryStatResetBySkillID(400041007);
               return;
            }

            int maxChargeTime = effect.getDuration() + effect.getZ() * 1000;
            int chargeTime = Math.min(
               maxChargeTime,
               effect.getDuration() + (int)((System.currentTimeMillis() - player.getMegaSmasherChargeStartTime()) / (effect.getY() * 1000)) * 1000
            );
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            statups.put(SecondaryStatFlag.MegaSmasher, 1);
            player.temporaryStatSet(400041007, effect.getLevel(), chargeTime, statups);
            player.setMegaSmasherChargeStartTime(0L);
         }
      }
   }

   public static void spotlightBuff(PacketDecoder slea, MapleClient c) {
      boolean cancel = slea.readByte() == 0;
      MapleCharacter player = c.getPlayer();
      if (!cancel) {
         int spotlightCount = slea.readInt();
         SecondaryStatEffect effect = player.getBuffedEffect(SecondaryStatFlag.SpotLight);
         if (effect != null) {
            int duration = effect.getDuration();
            duration = (int)(duration - (System.currentTimeMillis() - (player.getSecondaryStat().getTill(SecondaryStatFlag.SpotLight) - duration)));
            if (duration > 0) {
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               statups.put(SecondaryStatFlag.indieAsrR, effect.getW() * spotlightCount);
               statups.put(SecondaryStatFlag.indieCR, effect.getV() * spotlightCount);
               statups.put(SecondaryStatFlag.indieStance, effect.getQ() * spotlightCount);
               statups.put(SecondaryStatFlag.indiePMDR, effect.getS() * spotlightCount);
               player.temporaryStatSet(400051018, effect.getLevel(), duration, statups, false);
            }
         }
      }
   }

   public static void crystalActionRequest(PacketDecoder slea, MapleClient c) {
      int summonID = slea.readInt();
   }

   public static void crystalStackRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         chr.invokeJobMethod("summonedEnergyStackRequest", slea);
      }
   }

   public static void activeHarmonyLink(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int skillID = slea.readInt();
            Point summonPosition = slea.readPos();
            Point playerPosition = slea.readPos();
            SecondaryStatEffect effect = player.getSkillLevelData(skillID);
            if (effect != null) {
               if (skillID == 152111007) {
                  if (player.getBuffedValue(SecondaryStatFlag.HarmonyLink) != null) {
                     PacketEncoder packet = new PacketEncoder();
                     packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
                     packet.writeInt(player.getId());
                     packet.write(EffectHeader.HarmonyLink.getValue());
                     packet.writeInt(skillID);
                     packet.encodePos(summonPosition);
                     packet.encodePos(playerPosition);
                     map.broadcastMessage(player, packet.getPacket(), false);
                     int mobCount = slea.readInt();

                     for (int i = 0; i < mobCount; i++) {
                        int mobID = slea.readInt();
                        MapleMonster mob = map.getMonsterByOid(mobID);
                        if (mob != null) {
                           mob.tryApplyCurseMark(player, 0);
                           map.broadcastMessage(MobPacket.mobHitEffect(skillID, Collections.singletonList(mobID)));
                        }
                     }

                     int partyCount = slea.readInt();
                     int blessMarkSKillID = player.getBlessMarkSkillID();
                     SecondaryStatEffect blessMark = SkillFactory.getSkill(blessMarkSKillID).getEffect(player.getTotalSkillLevel(blessMarkSKillID));

                     for (int ix = 0; ix < partyCount; ix++) {
                        MapleCharacter target = map.getCharacterById(slea.readInt());
                        if (target != null
                           && (
                              target.getId() == player.getId()
                                 || target.getParty() != null && player.getParty() != null && target.getParty().getId() == player.getParty().getId()
                           )) {
                           int addHP = (int)(effect.getDamage() * (target.getStat().getCurrentMaxHp() * 0.01));
                           target.healHP(addHP);
                           target.applyBlessMark(blessMark, 1, false, 0);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void useHollySkill(PacketDecoder slea, MapleClient c) {
      slea.skip(1);
      int objectID = slea.readInt();
      int skillID = slea.readInt();
      Point usePosition = slea.readPos();
      AffectedArea mist = (AffectedArea)c.getPlayer().getMap().getMapObject(objectID, MapleMapObjectType.MIST);
      SecondaryStatEffect effect = mist.getSource();
      if (effect != null) {
         switch (mist.getSourceSkillID()) {
            case 2311011:
               if (mist.getRemainHealCount() > 0) {
                  int delta = (int)(effect.getX() * (c.getPlayer().getStat().getCurrentMaxHp() * 0.01));
                  if (c.getPlayer().getStat().getHp() < c.getPlayer().getStat().getCurrentMaxHp()) {
                     c.getPlayer().addHP(delta);
                     PostSkillEffect e_ = new PostSkillEffect(c.getPlayer().getId(), mist.getSourceSkillID(), mist.getSkillLevel(), null);
                     c.getPlayer().send(e_.encodeForLocal());
                     c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e_.encodeForRemote(), false);
                     mist.setRemainHealCount(mist.getRemainHealCount() - 1);
                  }
               }
               break;
            case 2321015:
               MapleCharacter chr = c.getPlayer();
               MapleCharacter owner = null;
               if (chr.getParty() != null) {
                  owner = chr.getPartyMembers().stream().filter(pUser -> pUser.getId() == mist.getOwnerId()).findFirst().orElse(null);
               } else if (mist.getOwnerId() == chr.getId()) {
                  owner = chr;
               }

               if (owner != null) {
                  chr.addHP(
                     chr.getStat().getCurrentMaxHp()
                        * (mist.getSource().getU2() + owner.getStat().getTotalInt() / mist.getSource().getDOT() * mist.getSource().getW2())
                        / 100L
                  );
                  chr.getMap().broadcastMessage(CField.removeAffectedArea(mist.getObjectId(), 2321015, false));
                  chr.getMap().removeMapObject(mist);
                  return;
               }
               break;
            case 162111000:
               boolean ownerOnly = mist.getOwner().getOneInfoQuestInteger(1544, "162111000") == 1;
               if (ownerOnly && c.getPlayer().getId() != mist.getOwnerId()) {
                  return;
               }

               double t = effect.getT();
               c.getPlayer().temporaryStatSet(SecondaryStatFlag.NewFlying, 80003059, (int)(t * 1000.0), objectID);
               break;
            case 400051076:
               if (c.getPlayer().getId() == mist.getOwnerId()) {
                  c.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(mist.getObjectId(), 400051076, false));
                  c.getPlayer().getMap().removeMapObject(mist);
                  SecondaryStatEffect e = SkillFactory.getSkill(400051077).getEffect(effect.getLevel());
                  if (e != null) {
                     e.applyTo(c.getPlayer());
                     int hp = (int)(c.getPlayer().getStat().getCurrentMaxHp() * 0.01) * effect.getX();
                     c.getPlayer().healHP(hp, true);
                  }
               }
               break;
            case 500061031:
               if (c.getPlayer().getId() == mist.getOwnerId()) {
                  c.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(mist.getObjectId(), 500061031, false));
                  c.getPlayer().getMap().removeMapObject(mist);
                  SecondaryStatEffect e = SkillFactory.getSkill(500061032).getEffect(effect.getLevel());
                  if (e != null) {
                     e.applyTo(c.getPlayer());
                     int hp = (int)(c.getPlayer().getStat().getCurrentMaxHp() * 0.01) * effect.getX();
                     c.getPlayer().healHP(hp, true);
                  }
               }
         }
      }
   }

   public static void bingoCellClick(int number, MapleClient c) {
      if (!c.getPlayer().getBingoGame().getRanking().contains(c.getPlayer())) {
         int[][] table = c.getPlayer().getBingoGame().getTable(c.getPlayer());
         c.getSession().writeAndFlush(CField.BingoCheckNumberAck(number));
         int jj = 0;

         for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
               if (table[x][y] == number) {
                  table[x][y] = 255;
               }
            }
         }

         int temp = 0;

         for (int y = 0; y < 5; y++) {
            for (int xx = 0; xx < 5; xx++) {
               if (table[xx][y] == 255 || table[xx][y] == 0) {
                  temp++;
               }
            }

            if (temp == 5) {
               c.getSession().writeAndFlush(CField.BingoCheckNumberAck(y * 5, 0, number));
            }

            temp = 0;
         }

         temp = 0;

         for (int xxx = 0; xxx < 5; xxx++) {
            for (int y = 0; y < 5; y++) {
               if (table[xxx][y] == 255 || table[xxx][y] == 0) {
                  temp++;
               }
            }

            if (temp == 5) {
               c.getSession().writeAndFlush(CField.BingoCheckNumberAck(xxx, 1, number));
            }

            temp = 0;
         }

         int crossCnt = 0;
         int rcrossCnt = 0;

         for (int i = 0; i < 5; i++) {
            if (table[i][i] == 255 || table[i][i] == 0) {
               crossCnt++;
            }

            if (table[i][4 - i] == 255 || table[i][4 - i] == 0) {
               rcrossCnt++;
            }

            if (crossCnt == 5) {
               c.getSession().writeAndFlush(CField.BingoCheckNumberAck(1, 2, number));
            }

            if (rcrossCnt == 5) {
               c.getSession().writeAndFlush(CField.BingoCheckNumberAck(1, 3, number));
            }
         }
      }
   }

   public static void bingoAddRank(MapleClient c) {
      c.getPlayer().getBingoGame().addRank(c.getPlayer());
   }

   public static void skillAutoUseLock(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      int questID = 1544;
      String value = c.getPlayer().getOneInfo(1544, String.valueOf(skillID));
      int v = 1;
      if (value != null) {
         v = Integer.parseInt(value) ^ 1;
      }

      c.getPlayer().updateOneInfo(1544, String.valueOf(skillID), String.valueOf(v));
   }

   public static void skillCommandLock(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      int questID = 21770;
      if (skillID == 37101001 || skillID == 37111003) {
         questID = 1544;
      }

      int index = GameConstants.getSkillCommandStateIndex(c.getPlayer().getJob(), skillID);
      if (index >= 0) {
         short job = c.getPlayer().getJob();
         if (GameConstants.isAran(job)) {
            String value = c.getPlayer().getOneInfo(questID, String.valueOf(index));
            int v = 1;
            if (value != null) {
               v = Integer.parseInt(value) ^ 1;
            }

            c.getPlayer().updateOneInfo(questID, String.valueOf(index), String.valueOf(v));
         } else if (GameConstants.isHoyoung(job)) {
            String key = "at";
            String value = c.getPlayer().getOneInfo(questID, key + index);
            int v = 1;
            if (value != null) {
               v = Integer.parseInt(value) ^ 1;
            }

            c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
         } else if (GameConstants.isAdele(job)) {
            String key = "lw";
            String value = c.getPlayer().getOneInfo(questID, key + index);
            int v = 1;
            if (value != null) {
               v = Integer.parseInt(value) ^ 1;
            }

            c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
         } else if (GameConstants.isDemonAvenger(job) || GameConstants.isDemonSlayer(job)) {
            String key = "ds";
            String value = c.getPlayer().getOneInfo(questID, key + index);
            int v = 1;
            if (value != null) {
               v = Integer.parseInt(value) ^ 1;
            }

            c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
         } else if (GameConstants.isBlaster(job)) {
            String key = "bl";
            String value = c.getPlayer().getOneInfo(questID, key + index);
            int v = 1;
            if (value != null) {
               v = Integer.parseInt(value) ^ 1;
            }

            c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
         } else if (job != 500 && (job < 510 || job > 512) && (job < 520 || job > 522)) {
            if (job >= 3510 && job <= 3512) {
               String key = "mc";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isZero(job)) {
               String key = "z";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isArk(job)) {
               String key = "lp";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isEvan(job)) {
               String key = "ev";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isAngelicBuster(job)) {
               String key = "ab";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isKhali(job)) {
               String key = "lt";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isMichael(job)) {
               String key = "me";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isNightWalker(job)) {
               String key = "nw";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            } else if (GameConstants.isLuminous(job)) {
               String key = "lm";
               String value = c.getPlayer().getOneInfo(questID, key + index);
               int v = 1;
               if (value != null) {
                  v = Integer.parseInt(value) ^ 1;
               }

               c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
            }
         } else {
            String key = "pr";
            String value = c.getPlayer().getOneInfo(questID, key + index);
            int v = 1;
            if (value != null) {
               v = Integer.parseInt(value) ^ 1;
            }

            c.getPlayer().updateOneInfo(questID, key + index, String.valueOf(v));
         }
      }
   }

   public static void activePassiveSkill(PacketDecoder slea, MapleClient c) {
      int stack = slea.readInt();
      int skillID = slea.readInt();
      if (skillID == 100865) {
         if (c.getPlayer() == null) {
            return;
         }

         c.getPlayer().send(CField.UIPacket.openUI(stack));
      }

      int skillLevel = c.getPlayer().getTotalSkillLevel(skillID);
      SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
      if (effect != null) {
         c.getPlayer().setPassiveStack(stack);
         effect.applyTo(c.getPlayer());
      }
   }

   public static void userTowerChairSetting(PacketDecoder slea, MapleClient c) {
      List<String> chairs = new ArrayList<>();
      String arr = "";
      int cnt = 0;

      for (int i = 0; i < 6; i++) {
         int chairID = slea.readInt();
         if (chairID != 0) {
            chairs.add(cnt++ + "=" + chairID);
         }
      }

      arr = String.join(";", chairs);
      c.getPlayer().updateInfoQuest(101309, arr);
      cnt = 0;
      chairs.clear();

      for (int ix = 0; ix < 6; ix++) {
         int chairID = slea.readInt();
         if (chairID != 0) {
            chairs.add(cnt++ + "=" + chairID);
         }
      }

      arr = String.join(";", chairs);
      c.getPlayer().updateInfoQuest(7266, arr);
      cnt = 0;
      chairs.clear();

      for (int ixx = 0; ixx < 6; ixx++) {
         int chairID = slea.readInt();
         if (chairID != 0) {
            chairs.add(cnt++ + "=" + chairID);
         }
      }

      arr = String.join(";", chairs);
      c.getPlayer().updateInfoQuest(101310, arr);
      c.getSession().writeAndFlush(CWvsContext.onTowerChairSettingResult());
   }

   public static void createArrowFlaterRequest(PacketDecoder slea, MapleClient c) {
      byte direction = slea.readByte();
      int positionX = slea.readInt();
      int positionY = slea.readInt();
      if (c.getPlayer().getMapId() != ServerConstants.TownMap
         && c.getPlayer().getMapId() != ServerConstants.TownMap2
         && c.getPlayer().getMapId() != ServerConstants.TownMap3) {
         c.getPlayer()
            .getMap()
            .getAllFieldAttackObj()
            .stream()
            .filter(objx -> objx.getPlayerID() == c.getPlayer().getId())
            .collect(Collectors.toList())
            .forEach(objx -> c.getPlayer().getMap().removeFieldAttackObj(objx));
         SecondaryStatEffect effect = SkillFactory.getSkill(3111013).getEffect(c.getPlayer().getSkillLevel(3111013));
         if (effect != null) {
            FieldAttackObj obj = new FieldAttackObj(c.getPlayer().getId(), 0, direction, new Point(positionX, positionY), effect.getU() * 1000);
            c.getPlayer().getMap().spawnFieldAttackObj(obj);
            c.getPlayer().giveCoolDowns(3111013, System.currentTimeMillis(), effect.getCooldown(c.getPlayer()));
            c.getPlayer().send(CField.skillCooldown(3111013, effect.getCooldown(c.getPlayer())));
         }
      } else {
         c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void userB2BodyRequest(PacketDecoder slea, MapleClient c) {
      short reqType = slea.readShort();
      int playerID = slea.readInt();
      int idx = slea.readInt();
      if (c.getPlayer().getId() == playerID) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.B2BODY_RESULT.getValue());
         packet.writeShort(reqType);
         packet.writeInt(playerID);
         packet.writeInt(c.getPlayer().getMapId());
         if (reqType != 4) {
            if (reqType != 0) {
               if (reqType == 3) {
                  int skillID = slea.readInt();
                  slea.skip(4);
                  int vecX = slea.readInt();
                  int vecY = slea.readInt();
                  packet.writeInt(playerID);
                  packet.writeInt(skillID);
                  packet.writeInt(vecX);
                  packet.writeInt(vecY);
                  c.getPlayer().getMap().broadcastMessage(packet.getPacket());
                  c.getSession().writeAndFlush(CField.getB2BodyAck(reqType, idx));
               }
            } else {
               byte type = slea.readByte();
               Point pos = slea.readPos();
               int unk = 0;
               int radius = 0;
               if (type == 5) {
                  unk = slea.readShort();
                  radius = slea.readShort();
               } else if (type == 6) {
                  unk = slea.readInt();
               }

               short destroyTerm = slea.readShort();
               int skillID = slea.readInt();
               short slv = slea.readShort();
               byte u0 = slea.readByte();
               slea.skip(1);
               short density = slea.readShort();
               short friction = slea.readShort();
               short restitution = slea.readShort();
               int count = 1;
               packet.writeShort(count);

               for (int i = 0; i < count; i++) {
                  packet.writeInt(idx);
                  packet.write(type);
                  packet.write(0);
                  packet.encodePos(pos);
                  if (type == 5) {
                     packet.writeShort(unk);
                     packet.writeShort(radius);
                  } else if (type == 6) {
                     packet.writeInt(unk);
                  }

                  packet.writeShort(destroyTerm);
                  packet.writeShort(density);
                  packet.writeShort(friction);
                  packet.writeShort(restitution);
                  packet.writeInt(skillID);
                  packet.writeShort(slv);
                  packet.write(u0);
               }

               c.getPlayer().getMap().broadcastMessage(packet.getPacket());
               c.getSession().writeAndFlush(CField.getB2BodyAck(reqType, idx));
            }
         } else {
            Point pos = slea.readPos();
            Point pos2 = slea.readPos();
            int skillID = slea.readInt();
            boolean left = slea.readByte() == 1;
            slea.skip(1);
            int v1 = slea.readInt();
            int v2 = slea.readInt();
            int v9 = slea.readInt();
            int v10 = slea.readInt();
            byte v3 = slea.readByte();
            short v4 = slea.readShort();
            short v5 = slea.readShort();
            short v6 = slea.readShort();
            byte v7 = slea.readByte();
            String v7_0 = "";
            if (v7 != 0) {
               v7_0 = slea.readMapleAsciiString();
            }

            int v8 = slea.readInt();
            int forceX = slea.readInt();
            int forceY = slea.readInt();
            int count = 1;
            packet.writeShort(count);
            SecondaryStatEffect eff = SkillFactory.getSkill(skillID).getEffect(c.getPlayer().getTotalSkillLevel(skillID));
            if (eff != null) {
               for (int i = 0; i < count; i++) {
                  packet.write(0);
                  packet.encodePos(pos);
                  packet.writeInt(eff.getDuration() / 1000);
                  packet.writeShort(v4);
                  packet.writeShort(v5);
                  packet.writeShort(v6);
                  packet.write(v7);
                  if (v7 != 0) {
                     packet.writeMapleAsciiString(v7_0);
                  }

                  packet.writeInt(v8);
                  packet.writeInt(skillID);
                  packet.write(left);
                  packet.writeInt(v1);
                  packet.writeInt(v2);
                  packet.writeInt(v9);
                  packet.writeInt(v10);
                  packet.write(v3);
               }

               packet.writeInt(forceX * (left ? -1 : 1));
               packet.writeInt(forceY);
               c.getPlayer().getMap().broadcastMessage(c.getPlayer(), packet.getPacket(), false);
               c.getSession().writeAndFlush(CField.getB2BodyAck(reqType, idx));
            }
         }
      }
   }

   public static void BuzzingHouseRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field instanceof Field_BuzzingHouse) {
            Field_BuzzingHouse f = (Field_BuzzingHouse)field;
            byte type = slea.readByte();
            if (type == 3) {
               int neoGem = (player.getBuzzingHouseBlockCount() - player.getBuzzingHousePerfectCount()) * 5 + player.getBuzzingHousePerfectCount() * 10;
               player.updateOneInfo(1234569, "miniGame2_coin", String.valueOf(neoGem));
               player.send(CField.getBuzzingHouseCoinCount(neoGem));
               player.send(CField.getBuzzingHouseResult(3));
               f.setEndGameTime(System.currentTimeMillis() + 3000L);
            } else {
               player.addBuzzingHouseBlockCount(1);
               if (type == 2) {
                  player.addBuzzingHousePerfectCount(1);
               }

               int blockCount = player.getBuzzingHouseBlockCount();
               if (blockCount % 10 == 0) {
                  int velocity = 180 + blockCount / 10 * 30;
                  int misplaceAllowance = 1 + blockCount / 10;
                  switch (blockCount) {
                     case 80:
                        player.send(CField.WeatherPacket_Add(1));
                        player.send(CField.musicChange("Bgm45/Time Is Gold"));
                        break;
                     case 100:
                        player.send(CField.musicChange("Bgm45/Demian Spine"));
                        break;
                     case 140:
                        player.send(CField.WeatherPacket_Remove(1));
                        player.send(CField.WeatherPacket_Add(2));
                  }

                  player.send(CField.getBuzzingHouseRequest(velocity, misplaceAllowance));
               }
            }
         }
      }
   }

   public static void ExitBuzzingHouse(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field instanceof Field_BuzzingHouse) {
            Field_BuzzingHouse f = (Field_BuzzingHouse)field;
            int neoGem = (player.getBuzzingHouseBlockCount() - player.getBuzzingHousePerfectCount()) * 5 + player.getBuzzingHousePerfectCount() * 10;
            player.updateOneInfo(1234569, "miniGame2_coin", String.valueOf(neoGem));
            player.send(CField.getBuzzingHouseCoinCount(neoGem));
            player.send(CField.getBuzzingHouseResult(3));
            f.setEndGameTime(System.currentTimeMillis() + 3000L);
         }
      }
   }

   public static void playerRespawn(PacketDecoder slea, MapleClient c) {
      byte unk = slea.readByte();
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         boolean isAlive = chr.isAlive();
         chr.setPlayerDead(false);
         chr.setNextConsume(0L);
         boolean deathCount = chr.getDeathCount() > 0;
         if (chr.getMap() instanceof Field_JinHillah) {
            Field_JinHillah f = (Field_JinHillah)chr.getMap();
            int count = 0;

            for (Field_JinHillah.JinHillahDeathCount dc : new ArrayList<>(chr.getJinHillahDeathCount())) {
               if (dc.getStatus() == Field_JinHillah.JinHillahDeathCountType.Green) {
                  count++;
               }
            }

            if (count == 0) {
               deathCount = false;
            }
         }

         if (deathCount) {
            boolean buffFreezer = true;
            if (buffFreezer) {
               if (chr.isEquippedSoulWeapon()) {
                  if (!isAlive) {
                     chr.setSoulCount((short)0);
                  }

                  chr.temporaryStatReset(SecondaryStatFlag.FullSoulMP);
               }

               chr.setUseBuffProtector(true);
               MapleQuestStatus questStatus = new MapleQuestStatus(MapleQuest.getInstance(1097), 1);
               questStatus.setCustomData("1");
               chr.updateQuest(questStatus);
            } else {
               chr.setUseBuffProtector(false);
               MapleQuestStatus questStatus = new MapleQuestStatus(MapleQuest.getInstance(1097), 1);
               questStatus.setCustomData("0");
               chr.updateQuest(questStatus);
               if (GameConstants.isArk(chr.getJob())) {
                  chr.setJobField("plainSpellBulletCount", 0);
                  chr.setJobField("scarletSpellBulletCount", 0);
                  chr.setJobField("gustSpellBulletCount", 0);
                  chr.setJobField("abyssSpellBulletCount", 0);
               }
            }

            if (chr.getMap().getId() == 105200520) {
               Field target = c.getChannelServer().getMapFactory().getMap(chr.getMapId() - 10);
               chr.changeMap(target);
            } else {
               if (chr.getMap().isReviveCurFieldOfNoTransfer()) {
                  chr.addHP(500000L, true);
                  Point pos = chr.getMap().getReviveCurFieldOfNoTransferPoint();
                  chr.send(CField.onUserTeleport(pos.x, pos.y));
               } else {
                  chr.changeMap(chr.getMap());
                  chr.addHP(500000L, true);
               }

               if (chr.getMap() instanceof Field_BlackMageBattlePhase4) {
                  chr.setBlackMageAttributes(2);
                  chr.temporaryStatReset(SecondaryStatFlag.BlackMageAttributes);
                  chr.temporaryStatSet(SecondaryStatFlag.BlackMageAttributes, 0, Integer.MAX_VALUE, chr.getBlackMageAttributes());
               }

               if (chr.getMap() instanceof Field_WillBattle) {
                  Field_WillBattle f = (Field_WillBattle)chr.getMap();
                  if (f.getPhase() == 1) {
                     f.sendWillSetMoonGauge(chr, 100, 45);
                  } else if (f.getPhase() == 2) {
                     f.sendWillSetMoonGauge(chr, 100, 40);
                  } else if (f.getPhase() == 3) {
                     f.sendWillSetMoonGauge(chr, 100, 25);
                  }
               }

               if (chr.getMap() instanceof Field_Demian && chr.getStigma() == 7) {
                  chr.resetStigma();
               }
            }
         } else {
            Field target = chr.getMap().getForcedReturnMap();
            if (target == null || target.getId() == 99999999) {
               target = chr.getMap().getReturnMap();
            }

            if (chr.isEquippedSoulWeapon()) {
               if (!isAlive) {
                  chr.setSoulCount((short)0);
               }

               chr.temporaryStatReset(SecondaryStatFlag.FullSoulMP);
            }

            chr.changeMap(target);
         }

         chr.checkEquippedMasterLabel();
         chr.applySpiritOfFreedom();
         if (!chr.isUseBuffProtector()) {
            chr.onJaguarLinkPassive();
         }

         if (GameConstants.isBlaster(chr.getJob())) {
            chr.setJobField("rwCylinderC", 0);
            chr.invokeJobMethod("setRWCylinder", 37000010, 6, 0, 0);
         }

         if (chr.isEquippedSoulWeapon()) {
            chr.temporaryStatSet(chr.getEquippedSoulSkill(), Integer.MAX_VALUE, SecondaryStatFlag.SoulMP, chr.getSoulCount());
            chr.checkSoulState(false);
         }

         chr.setUseBuffProtector(false);
      }
   }

   public static void dailyGiftRequest(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getDailyGift() == null) {
         c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "알 수 없는 오류가 발생하였습니다."));
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         if (c.getPlayer().getDailyGift().getDailyDay() == 0 || !c.getPlayer().getDailyGift().getDailyData().equals(CurrentTime.getCurrentTime2())) {
            String value = c.getPlayer().getOneInfoQuest(16700, "count");
            int data = c.getPlayer().getDailyGift().getDailyDay();
            if (value == null || value.isEmpty()) {
               return;
            }

            MapleDailyGiftInfo daily = MapleDailyGift.dailyItems.get(data);
            int itemID = daily.getItemId();
            int quantity = daily.getQuantity();
            if (!c.getPlayer().getAchievement().checkCompleteAchievement(2)) {
               c.getPlayer().getAchievement().getAchievements();
               SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

               for (AchievementEntry ae : new ArrayList<>(c.getPlayer().getAchievement().getAchievements())) {
                  if (ae.getAchievementID() == 2) {
                     try {
                        Date date = sdf.parse(sdf.format(ae.getTime()));
                        Date now = new Date();
                        if (date.getMonth() != now.getMonth() || date.getYear() != now.getYear()) {
                           ae.setSubMission("dailygift_get_reward=0");
                        }
                     } catch (ParseException var13) {
                        var13.printStackTrace();
                     }
                     break;
                  }
               }
            }

            if (!c.getPlayer().getAchievement().checkCompleteAchievement(3)) {
               c.getPlayer().getAchievement().getAchievements();
               SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
               Date now = new Date();

               for (AchievementEntry aex : new ArrayList<>(c.getPlayer().getAchievement().getAchievements())) {
                  if (aex.getAchievementID() == 3 && aex.getMission() == now.getMonth()) {
                     try {
                        Date date = sdf.parse(sdf.format(aex.getTime()));
                        if (date.getMonth() != now.getMonth() || date.getYear() != now.getYear()) {
                           aex.setSubMission("dailygift_get_reward=0");
                        }
                     } catch (ParseException var12) {
                        var12.printStackTrace();
                     }
                     break;
                  }
               }
            }

            AchievementFactory.checkDailygiftGetReward(c.getPlayer());
            c.getPlayer().gainItem(itemID, quantity);
            c.getPlayer().getDailyGift().setDailyDay(c.getPlayer().getDailyGift().getDailyDay() + 1);
            c.getPlayer().getDailyGift().setDailyCount(1);
            c.getPlayer().getDailyGift().setDailyData(CurrentTime.getCurrentTime2());
            c.getPlayer()
               .getDailyGift()
               .saveDailyGift(
                  c.getPlayer().getClient().getAccID(),
                  c.getPlayer().getDailyGift().getDailyDay(),
                  c.getPlayer().getDailyGift().getDailyCount(),
                  c.getPlayer().getDailyGift().getDailyData()
               );
            c.getSession()
               .writeAndFlush(
                  CField.getDailyGiftRecord(
                     "count="
                        + c.getPlayer().getDailyGift().getDailyCount()
                        + ";day="
                        + c.getPlayer().getDailyGift().getDailyDay()
                        + ";date="
                        + CurrentTime.getCurrentTime2()
                  )
               );
            c.getPlayer().send(CField.OnDailyGift((byte)2, 0, quantity));
         }

         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void damageSkinSaveRequest(PacketDecoder slea, MapleClient c) {
      byte type = slea.readByte();
      DamageSkinSaveInfo info = c.getPlayer().getDamageSkinSaveInfo();
      if (info != null) {
         if (type == 0) {
            MapleQuest quest = MapleQuest.getInstance(7291);
            if (quest != null) {
               MapleQuestStatus q = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(7291));
               if (q == null) {
                  return;
               }

               String val = q.getCustomData();
               int damageSkinID = 0;
               if (val != null && !val.isEmpty()) {
                  damageSkinID = Integer.parseInt(val);
               }

               int itemID = GameConstants.getDamageSkinItemID(damageSkinID);
               if (info.hasSaveDamageSkin(itemID)) {
                  c.getPlayer().dropMessage(1, "이미 해당 데미지 스킨이 저장되어있습니다.");
                  c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               info.putDamageSkinData(new DamageSkinSaveData(damageSkinID, itemID, false, DamageSkinSaveInfo.getDefaultDesc()));
               c.getPlayer().send(CField.damageSkinSaveResult((byte)0, info));
               c.getPlayer().dropMessage(1, "데미지 스킨이 저장되었습니다.");
               c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.DAMAGE_SKIN_SAVE.getFlag());
            }
         } else if (type == 1) {
            short damageSkinIDx = slea.readShort();
            int itemID = GameConstants.getDamageSkinItemID(damageSkinIDx);
            if (!info.hasSaveDamageSkin(itemID)) {
               c.getPlayer().send(CField.damageSkinSaveResult((byte)1, info));
               return;
            }

            info.removeDamageSkinData(damageSkinIDx);
            c.getPlayer().send(CField.damageSkinSaveResult((byte)0, info));
            c.getPlayer().dropMessage(1, "데미지 스킨이 삭제되었습니다.");
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.DAMAGE_SKIN_SAVE.getFlag());
         } else if (type == 2) {
            short damageSkinIDx = slea.readShort();
            int itemID = GameConstants.getDamageSkinItemID(damageSkinIDx);
            if (!info.hasSaveDamageSkin(itemID)) {
               c.getPlayer().send(CField.damageSkinSaveResult((byte)1, info));
               return;
            }

            MapleQuest quest = MapleQuest.getInstance(7291);
            MapleQuestStatus questStatus = new MapleQuestStatus(quest, 1);
            String skinString = String.valueOf((int)damageSkinIDx);
            questStatus.setCustomData(skinString == null ? "0" : skinString);
            c.getPlayer().updateQuest(questStatus);
            info.setActiveDamageSkinData(new DamageSkinSaveData(damageSkinIDx, itemID, false, DamageSkinSaveInfo.getDefaultDesc()));
            c.getPlayer().send(CField.damageSkinSaveResult((byte)0, info));
            c.getPlayer().dropMessage(1, "데미지 스킨이 변경되었습니다.");
         }
      }
   }

   public static void quickSlotKeyMappedModified(PacketDecoder slea, MapleClient c) {
      List<Integer> quickSlotKeyMapped = new LinkedList<>();

      for (int index = 0; index < 32; index++) {
         quickSlotKeyMapped.add(slea.readInt());
      }

      c.getPlayer().setQuickSlotKeyMapped(quickSlotKeyMapped);
   }

   public static void miracleCirculatorSelectRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      slea.skip(4);
      if (slea.readByte() == 1 && !player.getLastMiracleCirculator().isEmpty()) {
         player.getInnerSkills().clear();

         for (CharacterPotentialHolder h : player.getLastMiracleCirculator()) {
            player.getInnerSkills().add(h);
         }

         player.getLastMiracleCirculator().clear();
         c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());
         List<CharacterPotentialHolder> skills = c.getPlayer().getInnerSkills();

         for (byte i = 0; i < skills.size(); i++) {
            c.getSession()
               .writeAndFlush(CField.updateInnerPotential((byte)(i + 1), skills.get(i).getSkillId(), skills.get(i).getSkillLevel(), skills.get(i).getRank()));
         }
      }

      player.send(CWvsContext.enableActions(player));
   }

   public static void protectBuffOnDieItemRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         player.send(CWvsContext.setBuffProtector(0));
      }
   }

   public static void userCatchDebuffCollision(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            map.onCatchDebuffCollision(player, slea.readInt());
         }
      }
   }

   public static void demianObjectMakeEnterAck(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getMap() instanceof Field_Demian) {
         Field_Demian map = (Field_Demian)c.getPlayer().getMap();
         map.onFlyingSwordMakeEnterAck(slea);
      }
   }

   public static void demianNodeEnd(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getMap() instanceof Field_Demian) {
         Field_Demian map = (Field_Demian)c.getPlayer().getMap();
         map.onDemianNodeEnd(c.getPlayer(), slea);
      }
   }

   public static void stigmaDeleveryRequest(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getMap() instanceof Field_Demian) {
         int type = slea.readInt();
         int target = slea.readInt();
         int targetType = slea.readInt();
         Field_Demian map = (Field_Demian)c.getPlayer().getMap();
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_REMOTE_STIGMA_RESPONSE.getValue());
         packet.writeInt(c.getPlayer().getId());
         if (type == 0) {
            packet.writeInt(3);
            packet.writeInt(target);
            packet.writeInt(targetType);
         } else {
            if (type > 2) {
               return;
            }

            packet.writeInt(4);
            packet.writeInt(target);
            packet.writeInt(targetType);
         }

         if (type == 1) {
            if (targetType != 2) {
               if (targetType != 1) {
                  return;
               }

               MapleCharacter player = map.getCharacterById(target);
               if (player != null) {
                  int stigma = c.getPlayer().getStigma();
                  player.incStigma(stigma);
                  c.getPlayer().resetStigma();
               }
            } else if (map.checkStigmaDecObject()) {
               c.getPlayer().resetStigma();
               if (((Field_Demian)c.getPlayer().getMap()).isHellmode()) {
                  for (MapleCharacter chrs : c.getPlayer().getMap().getCharacters()) {
                     chrs.temporaryStatReset(SecondaryStatFlag.DebuffIncHP);
                  }

                  ((Field_Demian)c.getPlayer().getMap()).sendDemianNotice(216, "낙인이 해제되어 5초간 물약 사용이 가능해집니다.", -1, 5000);
                  ((Field_Demian)c.getPlayer().getMap()).setPotionTime(System.currentTimeMillis() + 5000L);
               }
            }
         }

         map.broadcastMessage(packet.getPacket());
      }
   }

   public static void updateLapidification(PacketDecoder slea, MapleClient c) {
      if (slea.readInt() == 0) {
         c.getPlayer().dispelDebuff(SecondaryStatFlag.Lapidification);
      }
   }

   public static void userChangeMobZoneState(PacketDecoder slea, MapleClient c) {
      int objectID = slea.readInt();
      slea.skip(4);
      slea.skip(4);
      MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(objectID);
      if (mob == null) {
         c.getPlayer().clearMobZoneState();
         c.getPlayer().temporaryStatReset(SecondaryStatFlag.MobZoneState);
      } else if (c.getPlayer().getBuffedValue(SecondaryStatFlag.MobZoneState) == null) {
         c.getPlayer().addMobZoneState(objectID);
         c.getPlayer().temporaryStatSet(0, Integer.MAX_VALUE, SecondaryStatFlag.MobZoneState, 1);
      } else {
         c.getPlayer().clearMobZoneState();
         c.getPlayer().temporaryStatReset(SecondaryStatFlag.MobZoneState);
      }

      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_SERVER_ACK_MOB_ZONE_STATE_CHANGE.getValue());
      packet.write(1);
      c.getPlayer().send(packet.getPacket());
   }

   public static void rwActionCancel(PacketDecoder slea, MapleClient c) {
      c.getPlayer().onRWCombination(slea.readInt(), 1);
   }

   public static void rwMultiChargeCancelRequest(PacketDecoder slea, MapleClient c) {
      if (GameConstants.isBlaster(c.getPlayer().getJob())) {
         byte i = slea.readByte();
         List<Integer> skills = new ArrayList<>();

         for (int a = 0; a < i; a++) {
            int skillID = slea.readInt();
            if (c.getPlayer().getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillID)) > 0) {
               skills.add(skillID);
            }
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.REMOTE_RW_MULTI_CHARGE_CANCEL.getValue());
         packet.writeInt(c.getPlayer().getId());
         packet.write(skills.size());

         for (int ax : skills) {
            packet.writeInt(ax);
         }

         c.getPlayer().getMap().broadcastMessage(packet.getPacket());
      }
   }

   public static void userKeyDownStepRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      int keyDown = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         switch (skillID) {
            case 37121052:
               if (player.getBuffedValue(SecondaryStatFlag.RWMagnumBlow) == null) {
                  return;
               }

               SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(player.getTotalSkillLevel(skillID));
               if (effect != null) {
                  int time = effect.getDuration(effect.getSubTime(), player);
                  switch (player.getBuffedValue(SecondaryStatFlag.RWMagnumBlow)) {
                     case 1:
                        if (keyDown >= 750) {
                           player.invokeJobMethod("setRWMagnumBlow", skillID, effect, 2, -1, -1);
                        }

                        return;
                     case 2:
                        if (keyDown >= 1500) {
                           player.invokeJobMethod("setRWMagnumBlow", skillID, effect, 3, -1, -1);
                        }

                        return;
                     case 3:
                        if (keyDown >= 2250) {
                           player.invokeJobMethod("setRWMagnumBlow", skillID, effect, 4, -1, -1);
                        }
                  }
               }
               break;
            case 131001020:
               int charge = 1;
               switch (keyDown) {
                  case 1110:
                     charge = 2;
                     break;
                  case 5010:
                     charge = 3;
               }

               player.setViperEnergyCharge(charge);
               player.temporaryStatSet(131001020, 2100000000, SecondaryStatFlag.KeyDownMoving, charge * 100);
               break;
            case 400011072:
               if (player.getBuffedValueDefault(SecondaryStatFlag.GrandCrossSize, 0) == 1) {
                  SecondaryStatManager statManager = new SecondaryStatManager(c, player.getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.GrandCrossSize, 400011072, 2);
                  statManager.temporaryStatSet();
               }
         }
      }
   }

   public static void rwClearCurrentAttackRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      if (skillID == 37121004) {
         c.getPlayer().temporaryStatReset(SecondaryStatFlag.NotDamaged);
         c.getPlayer().setJobField("rwCylinderC", 0);
         c.getPlayer().setJobField("rwCylinderB", 0);
      }
   }

   public static void jaguarChangeRequest(PacketDecoder slea, MapleClient c) {
      int beforce = slea.readInt();
      int after = slea.readInt();
      if (beforce != after) {
         int newIndex = after + 1;
         String value = c.getPlayer().getOneInfoQuest(23008, String.valueOf(newIndex));
         if (value != null && !value.isEmpty() && value.equals("1")) {
            c.getPlayer().getWildHunterInfo().setRidingType(newIndex);
            c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.WILD_HUNTER_INFO.getFlag());
            c.getPlayer().send(CWvsContext.updateJaguar(c.getPlayer()));
         }
      }
   }

   public static void onSetOffTrinity(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         chr.invokeJobMethod("onSetOffTrinity");
      }
   }

   public static void endThrowingBombRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         player.onExplodingThrowingBomb(slea);
      }
   }

   public static void stopShurrikaneRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null && player.getMap() != null) {
         PacketEncoder p = new PacketEncoder();
         p.writeShort(SendPacketOpcode.STOP_SHURRIKANE_RESULT.getValue());
         p.writeInt(player.getId());
         p.writeInt(slea.readInt());
         p.writeInt(slea.readInt());
         if (slea.available() > 0L) {
            p.write(slea.readByte());
            p.writeInt(slea.readInt());
            p.writeInt(slea.readInt());
         }

         player.getMap().broadcastMessage(player, p.getPacket(), false);
      }
   }

   public static void createAreaDotRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int key = slea.readInt();
         int skillID = slea.readInt();
         slea.readInt();
         short count = slea.readShort();
         SecondaryStatEffect effect = player.getSkillLevelData(skillID);
         if (effect != null) {
            for (int i = 0; i < count; i++) {
               Rect rect = new Rect(slea.readInt(), slea.readInt(), slea.readInt(), slea.readInt());
               Point point = new Point((rect.getLeft() + rect.getRight()) / 2, (rect.getTop() + rect.getBottom()) / 2);
               Point pos = player.getMap().calcDropPos(point, point);
               rect.setTop(pos.y - 30);
               rect.setBottom(pos.y + 30);
               int y = effect.getTime();
               if (skillID == 400020002) {
                  y = player.getSkillLevelDataOne(400021002, eff -> eff.getY());
               }

               player.getMap().spawnMist(new AffectedArea(rect, player, effect, pos, System.currentTimeMillis() + y * 1000 + 3000L));
            }
         }
      }
   }

   public static void reinstallAreaRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int skillID = slea.readInt();
         slea.skip(4);
         Point pos = slea.readPos();
         if (skillID == 400031037) {
            AffectedArea area = player.getMap()
               .getAllMistsThreadsafe()
               .stream()
               .filter(m -> m.getOwnerId() == player.getId())
               .filter(m -> m.getSourceSkillID() == 400031040)
               .findFirst()
               .orElse(null);
            if (area != null) {
               SecondaryStatEffect eff = area.getSource();
               if (eff != null) {
                  int remainDuration = (int)(eff.getDuration() - (System.currentTimeMillis() - area.getStartTime()));
                  if (remainDuration > 0) {
                     player.getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 400031040, false));
                     player.getMap().removeMapObject(area);
                  }

                  Rect rect = new Rect(pos, eff.getLt(), eff.getRb(), false);
                  AffectedArea area2 = new AffectedArea(rect, player, eff, pos, System.currentTimeMillis() + remainDuration);
                  area2.setStartTime(area.getStartTime());
                  player.getMap().spawnMist(area2);
               }
            }
         }

         player.send(CWvsContext.enableActions(player));
      }
   }

   public static void bulletPartyKeyInput(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int skillID = slea.readInt();
         SecondaryStatEffect effect = player.getSkillLevelData(skillID);
         if (effect != null) {
            if (skillID == 400051006
               && player.getBuffedValue(SecondaryStatFlag.BulletParty) != null
               && player.getBulletPartyValue() < effect.getX() * effect.getY()) {
               player.setBulletPartyValue(player.getBulletPartyValue() + 1);
               if (player.getBulletPartyValue() % effect.getX() == 0) {
                  SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.BulletParty, 400051006, player.getBulletPartyValue());
                  statManager.changeTill(SecondaryStatFlag.BulletParty, 400051006, 1000);
                  statManager.temporaryStatSet();
               }
            }
         }
      }
   }

   public static void updateStackSkillRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      int sn = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         BasicJob job = player.getPlayerJob();
         if (job != null) {
            if (skillID != 162101012 && skillID != 162121042 && skillID != 162111006) {
               if (c.isGm()) {
                  SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                  c.getPlayer().dropMessage(5, "StackRequest Skill : " + skillID + " Time : " + sdf.format(System.currentTimeMillis()));
               }

               if (GameConstants.isBlessStackSkill(skillID)) {
                  int currentBlessStack = c.getPlayer().getBuffedValueDefault(SecondaryStatFlag.EmpressBlessStack, 0);
                  if (currentBlessStack >= 2) {
                     c.getPlayer().temporaryStatSet(SecondaryStatFlag.EmpressBlessStack, skillID, Integer.MAX_VALUE, 2);
                  } else {
                     c.getPlayer().temporaryStatSet(SecondaryStatFlag.EmpressBlessStack, skillID, Integer.MAX_VALUE, currentBlessStack + 1);
                  }
               }

               if (skillID == 400011000) {
                  int currentAuraWeaponStack = c.getPlayer().getBuffedValueDefault(SecondaryStatFlag.AuraWeaponStack, 0);
                  if (currentAuraWeaponStack >= 2) {
                     c.getPlayer().temporaryStatSet(SecondaryStatFlag.AuraWeaponStack, skillID, Integer.MAX_VALUE, 2);
                  } else {
                     c.getPlayer().temporaryStatSet(SecondaryStatFlag.AuraWeaponStack, skillID, Integer.MAX_VALUE, currentAuraWeaponStack + 1);
                  }
               }

               if (skillID == 5311004) {
                  int value = player.getBuffedValueDefault(SecondaryStatFlag.RouletteStack, 0);
                  if (value >= 4) {
                     player.temporaryStatSet(SecondaryStatFlag.RouletteStack, skillID, Integer.MAX_VALUE, 4);
                  } else {
                     player.temporaryStatSet(SecondaryStatFlag.RouletteStack, skillID, Integer.MAX_VALUE, value + 1);
                  }
               }

               if (skillID == 400001037) {
                  int value = player.getBuffedValueDefault(SecondaryStatFlag.MagicCircuitFullDriveStack, 0);
                  if (value >= 4) {
                     player.temporaryStatSet(SecondaryStatFlag.MagicCircuitFullDriveStack, skillID, Integer.MAX_VALUE, 4);
                  } else {
                     player.temporaryStatSet(SecondaryStatFlag.MagicCircuitFullDriveStack, skillID, Integer.MAX_VALUE, value + 1);
                  }
               }

               if (skillID == 64141000) {
                  int value = player.getBuffedValueDefault(SecondaryStatFlag.ChainArtsStrokeVI, 0);
                  if (value >= 3) {
                     player.temporaryStatSet(SecondaryStatFlag.ChainArtsStrokeVI, skillID, Integer.MAX_VALUE, 3);
                  } else {
                     player.temporaryStatSet(SecondaryStatFlag.ChainArtsStrokeVI, skillID, Integer.MAX_VALUE, value + 1);
                  }
               }

               player.send(CField.updateSkillStackRequestResult(skillID));
            } else if (GameConstants.isLara(player.getJob())) {
               Lara lara = (Lara)job;
               lara.updateLaraSkillStack(skillID);
            }
         }
      }
   }

   public static void updateKeydownReadySkillRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      SecondaryStatEffect effect = c.getPlayer().getSkillLevelData(400011038);
      if (effect != null) {
         int x = effect.getX();
         int hp = (int)(c.getPlayer().getStat().getCurrentMaxHp(c.getPlayer()) * 0.01 * x);
         c.getPlayer().healHP(-hp);
      }
   }

   public static void summonedRespawnRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      int skillLevel = slea.readInt();
      byte flip = slea.readByte();
      if (skillID == 400021069) {
         Summoned summoned = c.getPlayer().getSummonBySkillID(skillID);
         if (summoned != null) {
            long removeTime = summoned.getSummonRemoveTime();
            long span = removeTime - System.currentTimeMillis();
            c.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summoned, true));
            c.getPlayer().getMap().removeMapObject(summoned);
            summoned.getOwner().removeVisibleMapObject(summoned);
            c.getPlayer().removeSummon(summoned);
            Summoned summon = new Summoned(
               c.getPlayer(), skillID, skillLevel, c.getPlayer().getPosition(), SummonMoveAbility.SHADOW_SERVANT_EXTEND, flip, removeTime
            );
            c.getPlayer().getMap().spawnSummon(summon, (int)span);
            c.getPlayer().addSummon(summon);
         }
      } else if (skillID == 400031005) {
         if (c.getPlayer().hasBuffBySkillID(400031005)) {
            c.getPlayer().removeJaguarStorm();
            SecondaryStatEffect effect = c.getPlayer().getSkillLevelData(400031005);
            if (effect != null) {
               c.getPlayer().onJaguarStorm(effect, c.getPlayer().getPosition(), skillLevel, flip);
            }
         }
      } else if (skillID == 500061007) {
         if (c.getPlayer().hasBuffBySkillID(500061007)) {
            c.getPlayer().removeJaguarStorm();
            SecondaryStatEffect effect = c.getPlayer().getSkillLevelData(500061007);
            if (effect != null) {
               c.getPlayer().onJaguarStorm(effect, c.getPlayer().getPosition(), skillLevel, flip);
            }
         }
      } else if (skillID == 400011012) {
         Summoned summoned = c.getPlayer().getSummonBySkillID(skillID);
         if (summoned != null) {
            long removeTime = summoned.getSummonRemoveTime();
            long span = removeTime - System.currentTimeMillis();

            for (Summoned summon : c.getPlayer().getSummons()) {
               if (summon.getSkill() >= 400011012 && summon.getSkill() <= 400011014) {
                  c.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                  c.getPlayer().getMap().removeMapObject(summon);
                  c.getPlayer().removeVisibleMapObject(summon);
                  c.getPlayer().removeSummon(summon);
               }
            }

            for (int i = 0; i < 3; i++) {
               Summoned summonx = new Summoned(
                  c.getPlayer(), 400011012 + i, skillLevel, c.getPlayer().getPosition(), SummonMoveAbility.FIX_V_MOVE, flip, removeTime
               );
               c.getPlayer().getMap().spawnSummon(summonx, (int)span);
               c.getPlayer().addSummon(summonx);
            }
         }
      }
   }

   public static void affectedAreaRemoveBySkill(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int skillID = slea.readInt();
         AffectedArea area = player.getMap().getAffectedAreaBySkillId(skillID, player.getId());
         if (area != null) {
            if (skillID == 80001455) {
               player.temporaryStatResetBySkillID(80001455);
            }

            if (skillID == 400031012) {
               player.send(CField.userBonusAttackRequest(400031013, true, Collections.emptyList()));
            }

            player.getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), skillID, false));
            player.getMap().removeMapObject(area);
         }
      }
   }

   public static void affectedAreaRemoveBySkill2(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int objectID = slea.readInt();
         int skillID = slea.readInt();
         AffectedArea area = (AffectedArea)player.getMap().getMapObject(objectID, MapleMapObjectType.MIST);
         if (skillID == 2111013 && player.isGM()) {
            player.dropMessage(6, "remove area x: " + area.getTruePosition().getX());
         }

         if (area != null) {
            if (area.getSourceSkillID() == 80001455) {
               player.temporaryStatResetBySkillID(80001455);
            }

            player.getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), area.getSourceSkillID(), false));
            player.getMap().removeMapObject(area);
         }
      }
   }

   public static void tryRegisterTeleport(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      if (skillID == 20031205) {
         c.getSession().writeAndFlush(CField.registerTeleportResult(skillID));
         c.getPlayer().setPhantomShroudCount(c.getPlayer().getPhantomShroudCount() + 1);
      }
   }

   public static void passiveSetStatRequest(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      int skillID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         SecondaryStatEffect effect = player.getSkillLevelData(skillID);
         if (effect != null) {
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            Integer v = player.getBuffedValue(SecondaryStatFlag.Solus);
            int v2 = 0;
            if (v != null) {
               v2 = v;
            }

            int value = Math.min(effect.getX(), v2 + 1);
            statups.put(SecondaryStatFlag.Solus, value);
            statups.put(SecondaryStatFlag.indieDamR, Math.max(0, value - 1) * effect.getY() + effect.getQ());
            player.temporaryStatSet(skillID, effect.getLevel(), effect.getDuration(), statups);
         }
      }
   }

   public static void tideOfBattleRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int skillLevel = player.getTotalSkillLevel(skillID);
         if (skillLevel <= 0) {
            skillID = 80000268;
         }

         SecondaryStatEffect eff = player.getSkillLevelData(skillID);
         if (eff != null) {
            if (skillID == 14110032) {
               int value = Math.min(eff.getX(), player.getBuffedValueDefault(SecondaryStatFlag.ShadowMomentum, 0) + 1);
               player.temporaryStatSet(SecondaryStatFlag.ShadowMomentum, skillID, eff.getDuration(), value);
            } else {
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               Integer v = player.getBuffedValue(SecondaryStatFlag.TideOfBattle);
               int v2 = 0;
               if (v != null) {
                  v2 = v;
               }

               int value = Math.min(eff.getX(), v2 + 1);
               statups.put(SecondaryStatFlag.TideOfBattle, value);
               statups.put(SecondaryStatFlag.indieDamR, value * eff.getY());
               player.temporaryStatSet(skillID, eff.getLevel(), eff.getDuration(), statups);
            }
         }
      }
   }

   public static void userFieldTransferRequest(PacketDecoder slea, MapleClient c) {
      int questID = slea.readInt();
      int fieldID = -1;
      switch (questID) {
         case 7860:
            fieldID = 910001000;
            break;
         case 26015:
            fieldID = 200000301;
      }

      Field to = GameServer.getInstance(c.getChannel()).getMapFactory().getMap(fieldID);
      if (to != null) {
         c.getPlayer().changeMap(to);
      }
   }

   public static void removeSecondAtomRequest(PacketDecoder slea, MapleClient c) {
      int size = slea.readInt();

      for (int next = 0; next < size; next++) {
         int key = slea.readInt();
         slea.readInt();
         MapleCharacter player = c.getPlayer();
         if (player == null) {
            return;
         }

         Field map = player.getMap();
         if (map != null) {
            SecondAtom.Atom atom = map.getSecondAtom(key);
            if (atom != null) {
               if (atom.getType() == SecondAtom.SecondAtomType.WhereTheRiverFlows || atom.getType() == SecondAtom.SecondAtomType.WhereTheRiverFlows2) {
                  return;
               }

               switch (atom.getSkillID()) {
                  case 2121052:
                     SecondaryStatEffect e = player.getSkillLevelData(atom.getSkillID());
                     List<SecondAtom.Atom> atoms = new ArrayList<>();
                     int i = 0;

                     for (; i < 2; i++) {
                        if (player.getBlackJackCount() < e.getX()) {
                           player.setBlackJackCount(player.getBlackJackCount() + 1);
                           SecondAtomData data = SkillFactory.getSkill(atom.getSkillID()).getSecondAtomData();
                           SecondAtomData.atom at = data.getAtoms().get(3);
                           SecondAtom.Atom a = new SecondAtom.Atom(
                              map, player.getId(), 2121052, ForceAtom.SN.getAndAdd(1), SecondAtom.SecondAtomType.MegiddoFlame, 0, null
                           );
                           a.setPlayerID(player.getId());
                           a.setTargetObjectID(atom.getTargetObjectID());
                           a.setExpire(at.getExpire());
                           a.setCreateDelay(at.getCreateDelay());
                           a.setEnableDelay(at.getEnableDelay());
                           a.setRange(1);
                           a.setUnk4(1);
                           a.setAttackableCount(1);
                           a.setSkillID(2121052);
                           a.setCustoms(Collections.singletonList(new SecondAtom.Custom(0, 1)));
                           MapleMonster target = map.getMonsterByOid(atom.getTargetObjectID());
                           a.setPos(target != null ? target.getTruePosition() : player.getTruePosition());
                           player.addSecondAtom(a);
                           atoms.add(a);
                        }
                     }

                     if (!atoms.isEmpty()) {
                        SecondAtom secondAtom = new SecondAtom(player.getId(), 2121052, atoms);
                        map.createSecondAtom(secondAtom);
                     }
                     break;
                  case 400011047:
                     SecondaryStatEffect effect = SkillFactory.getSkill(400011047).getEffect(player.getTotalSkillLevel(400011047));
                     if (effect == null) {
                        int maxHP = (int)(player.getStat().getCurrentMaxHp() * 0.01 * effect.getX());
                        if (player.getStat().getCurrentMaxHp() <= player.getStat().getHp()) {
                           int y = effect.getY();
                           int v = effect.getV();
                           Integer value = player.getBuffedValue(SecondaryStatFlag.indieBarrier);
                           int v_ = 0;
                           if (value != null) {
                              v_ = value;
                           }

                           int barrier = (int)Math.min(player.getStat().getCurrentMaxHp() * 0.01 * y, v_ + maxHP * 0.01 * v);
                           player.temporaryStatSet(400011047, effect.getDuration(), SecondaryStatFlag.indieBarrier, barrier);
                        } else {
                           player.healHP(maxHP, true, false);
                        }
                     }
                     break;
                  case 400011108:
                     player.temporaryStatSet(400011108, 2000, SecondaryStatFlag.indiePartialNotDamaged, 1);
                     break;
                  case 400021092:
                     Summoned summon = player.getSummonBySkillID(400021092);
                     if (summon != null) {
                        PacketEncoder packet = new PacketEncoder();
                        packet.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
                        packet.writeInt(player.getId());
                        packet.writeInt(summon.getObjectId());
                        packet.write(0);
                        packet.writeInt(0);
                        if (summon.getSkill() == 400021092) {
                           packet.writeInt(0);
                        }

                        map.broadcastMessage(packet.getPacket());
                     }
               }
            }

            map.removeSecondAtom(key);
            player.removeSecondAtom(key);
         }
      }
   }

   public static void moveSecondAtom(PacketDecoder slea, MapleClient c) {
   }

   public static void recreateSecondAtom(PacketDecoder slea, MapleClient c) {
      int secondAtomSize = slea.readInt();

      for (int i = 0; i < secondAtomSize; i++) {
         int key = slea.readInt();
         int type = slea.readInt();
         slea.readInt();
         slea.readInt();
         slea.readInt();
         SecondAtom.Atom atom = c.getPlayer().getSecondAtom(key);
         if (atom != null) {
            if (atom.getType().getType() >= SecondAtom.SecondAtomType.Creation1.getType()
               && atom.getType().getType() <= SecondAtom.SecondAtomType.Creation6.getType()) {
               SecondaryStatEffect effect = c.getPlayer().getSkillLevelData(151111002);
               if (effect != null) {
                  int ether = secondAtomSize * effect.getY();
                  c.getPlayer().addEtherPoint(ether);
               }
            } else if (atom.getType().getType() == SecondAtom.SecondAtomType.DragonVein.getType() && type == 1) {
               long remain = c.getPlayer().getRemainCooltime(162121001);
               SecondaryStatEffect level = c.getPlayer().getSkillLevelData(162121001);
               if (level != null) {
                  c.getPlayer().changeCooldown(162121001, remain + level.getU2() * 1000);
               }
            }
         }
      }
   }

   public static void requestFreeChangeJob(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int beforeJob = player.getJob();
         int afterJob = slea.readInt();
         int payType = slea.readByte();
         switch (beforeJob) {
            case 112:
            case 122:
            case 132:
            case 212:
            case 222:
            case 232:
            case 312:
            case 322:
            case 332:
            case 412:
            case 422:
            case 434:
            case 512:
            case 522:
            case 532:
               int quantity = 1;
               if (!player.haveItem(4310086, quantity)) {
                  player.dropMessage(1, "자유전직 코인이 부족합니다. " + quantity + "개 필요합니다.");
                  return;
               } else {
                  if (beforeJob / 100 == afterJob / 100 && beforeJob / 1000 == 0 && afterJob / 1000 == 0) {
                     for (VCore core : player.getVCoreSkillsNoLock()) {
                        if (core.getState() == 2) {
                           player.dropMessage(1, "V스킬 코어를 전부 장착해제 후 시도해주시기 바랍니다.");
                           return;
                        }
                     }

                     player.getClient().getSession().writeAndFlush(CField.UIPacket.closeUI(3));
                     player.dispel();
                     player.removeItem(4310086, -quantity);

                     for (Entry<Skill, SkillEntry> entry : new HashMap<>(player.getSkills()).entrySet()) {
                        if (GameConstants.getSkillRootFromSkill(entry.getKey().getId()) / 100 == beforeJob / 100) {
                           player.changeSkillLevel(entry.getKey().getId(), 0, 0);
                        }
                     }

                     player.changeJob(afterJob, true);

                     for (int i = 0; i < player.getJob() % 10 + 1; i++) {
                        int job = i + 1 == player.getJob() % 10 + 1 ? player.getJob() - player.getJob() % 100 : player.getJob() - (i + 1);
                        if (player.getJob() >= 330 && player.getJob() <= 332 && job == 300) {
                           job = 301;
                        }

                        if (GameConstants.isAutoMaxSkill()) {
                           player.maxskill(job);
                        }
                     }

                     int div = player.getJob() < 1000 ? 100 : 1000;
                     int jobx = player.getJob();
                     if (GameConstants.isKadena(jobx)) {
                        div = 6002;
                     } else if (GameConstants.isAngelicBuster(jobx)) {
                        div = 6001;
                     } else if (GameConstants.isEvan(jobx)) {
                        div = 2001;
                     } else if (GameConstants.isMercedes(jobx)) {
                        div = 2002;
                     } else if (GameConstants.isDemonSlayer(jobx) || GameConstants.isDemonAvenger(jobx)) {
                        div = 3001;
                     } else if (GameConstants.isPhantom(jobx)) {
                        div = 2003;
                     } else if (GameConstants.isLuminous(jobx)) {
                        div = 2004;
                     } else if (GameConstants.isXenon(jobx)) {
                        div = 3002;
                     } else if (GameConstants.isEunWol(jobx)) {
                        div = 2005;
                     } else if (GameConstants.isArk(jobx)) {
                        div = 15001;
                     }

                     if (GameConstants.isAutoMaxSkill()) {
                        player.maxskill(player.getJob() / div * div);
                     }

                     player.getLinkSkill().updateLinkSkillByFreeJobChange(player);
                     player.updateOneInfo(122870, "AutoJob", String.valueOf(player.getJob() / 10 * 10));
                     short ap = 20;
                     ap = (short)(ap + (player.getLevel() * 5 - player.getHpApUsed()));
                     player.setRemainingAp(ap);
                     player.getStat().setStr((short)4, player);
                     player.getStat().setDex((short)4, player);
                     player.getStat().setInt((short)4, player);
                     player.getStat().setLuk((short)4, player);
                     Map<MapleStat, Long> statups = new EnumMap<>(MapleStat.class);
                     statups.put(MapleStat.STR, 4L);
                     statups.put(MapleStat.DEX, 4L);
                     statups.put(MapleStat.INT, 4L);
                     statups.put(MapleStat.LUK, 4L);
                     statups.put(MapleStat.AVAILABLEAP, (long)ap);
                     player.send(CWvsContext.updatePlayerStats(statups, true, player));
                     if (GameConstants.isAutoMaxSkill()) {
                        player.maxskill(player.getJob());
                     }

                     player.getStat().recalcLocalStats(player);
                     player.firstLoadMapleUnion();
                     player.sendUnionPacket();
                     player.checkSkills();
                     player.fakeRelog();
                     LinkSkill.linkSkillUpdate(player);
                     player.dropMessage(1, "자유전직이 완료되었습니다.");
                     return;
                  }

                  player.dropMessage(1, "알 수 없는 오류가 발생하였습니다.");
                  return;
               }
         }
      }
   }

   public static void lucidActivateStatue(PacketDecoder slea, MapleClient c) {
      Field map = c.getPlayer().getMap();
      if (map != null) {
         if (map instanceof Field_LucidBattle) {
            Field_LucidBattle field = (Field_LucidBattle)map;
            field.onActivateStatue();
         }
      }
   }

   public static void lucidContagionResult(PacketDecoder slea, MapleClient c) {
      Field map = c.getPlayer().getMap();
      if (map != null) {
         if (map instanceof Field_LucidBattle) {
            Field_LucidBattle field = (Field_LucidBattle)map;
            field.onContagionResult(slea.readInt());
         }
      }
   }

   public static void cabinetRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         MapleCabinet cabinet = player.getCabinet();
         if (cabinet == null) {
            return;
         }

         int type = slea.readInt();
         if (type == 0) {
            player.send(CField.maplecabinetResult(player.getCabinet()));
         } else if (type != 1 && (type == 4 || type == 5)) {
            int index = slea.readInt();
            MapleCabinetItem item = cabinet.getCabinetItem(index);
            if (item != null) {
               if (item.getExpiredTime() < System.currentTimeMillis()) {
                  player.dropMessage(1, "받을 수 있는 유효 기간이 지나 수령할 수 없습니다.");
                  cabinet.removeCabinetItem(index);
                  player.send(CWvsContext.enableActions(player));
                  return;
               }

               Item item_ = item.getItem();
               MapleInventoryType TI = MapleInventoryType.getByType(item_.getType());
               if (!MapleInventoryManipulator.checkSpace(c, item_.getItemId(), item_.getQuantity(), "")) {
                  player.send(CField.maplecabinetResult(16));
                  return;
               }

               MapleInventoryManipulator.addbyItem(c, item_);
               player.send(CWvsContext.InfoPacket.getShowItemGain(item_.getItemId(), item_.getQuantity()));
               cabinet.removeCabinetItem(index);
               player.setSaveFlag(player.getSaveFlag() | CharacterSaveFlag.CABINET.getFlag());
               StringBuilder sb = new StringBuilder("캐비넷에서 아이템 출고");
               LoggingManager.putLog(new CabinetLog(player, item_.getItemId(), item_.getQuantity(), CabinetLogType.TakeOutItem.getType(), sb));
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.MAPLE_CABINET.getValue());
               packet.writeInt(12);
               packet.writeInt(index);
               player.send(packet.getPacket());
               player.send(CWvsContext.enableActions(player));
            }
         }
      }
   }

   public static void dreamBreakerSkillRequest(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      int skillIndex = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            if (map instanceof Field_DreamBreaker) {
               Field_DreamBreaker f = (Field_DreamBreaker)map;
               if (f != null) {
                  int dreamPoint = f.getDreamPoint();
                  if (f.isUsedSkill(skillIndex)) {
                     player.send(Field_DreamBreaker.dreamBreakerMsg("해당 스킬은 현재 스테이지에서 이미 사용하여 사용이 불가능합니다!"));
                     return;
                  }

                  switch (skillIndex) {
                     case 0:
                        if (dreamPoint >= 200) {
                           c.getPlayer().updateOneInfo(15901, "dream", String.valueOf(dreamPoint - 200));
                           f.setDreamPoint(dreamPoint - 200);
                           player.send(Field_DreamBreaker.dreamBreakerMsg("게이지 홀드! 5초동안 게이지의 이동이 멈춥니다!"));
                           f.setStopGaugeTime(System.currentTimeMillis() + 5000L);
                           f.addUsedSkill(skillIndex);
                        } else {
                           player.send(Field_DreamBreaker.dreamBreakerMsg("드림 포인트가 부족하여 스킬을 사용할 수 없습니다."));
                        }
                        break;
                     case 1:
                        if (dreamPoint < 300) {
                           player.send(Field_DreamBreaker.dreamBreakerMsg("드림 포인트가 부족하여 스킬을 사용할 수 없습니다."));
                        } else {
                           List<MapleMonster> orgels = new ArrayList<>();

                           for (MapleMonster m : c.getPlayer().getMap().getAllMonster()) {
                              if (m.getId() >= 9833080 && m.getId() <= 9833084) {
                                 orgels.add(m);
                              }
                           }

                           if (orgels.size() > 0) {
                              c.getPlayer().getMap().killMonster(orgels.get(Randomizer.rand(0, orgels.size() - 1)));
                              player.send(Field_DreamBreaker.dreamBreakerMsg("자각의 종소리를 울려 한 곳의 오르골이 깨어났습니다!"));
                              f.addUsedSkill(skillIndex);
                              c.getPlayer().updateOneInfo(15901, "dream", String.valueOf(dreamPoint - 300));
                              f.setDreamPoint(dreamPoint - 300);
                           } else {
                              player.send(Field_DreamBreaker.dreamBreakerMsg("모든 오르골이 이미 깨어있는 상태입니다."));
                           }
                        }
                        break;
                     case 2:
                        if (dreamPoint >= 400) {
                           c.getPlayer().updateOneInfo(15901, "dream", String.valueOf(dreamPoint - 400));
                           f.setDreamPoint(dreamPoint - 400);
                           player.send(Field_DreamBreaker.dreamBreakerMsg("꿈속의 헝겊인형이 소환되어 몬스터들을 도발합니다!"));
                           MapleMonster m = MapleLifeFactory.getMonster(9833100);
                           m.setHp(m.getStats().getHp());
                           m.getStats().setHp(m.getStats().getHp());
                           c.getPlayer().getMap().spawnMonsterOnGroundBelow(m, c.getPlayer().getPosition());
                           f.addUsedSkill(skillIndex);
                        } else {
                           player.send(Field_DreamBreaker.dreamBreakerMsg("드림 포인트가 부족하여 스킬을 사용할 수 없습니다."));
                        }
                        break;
                     case 3:
                        if (dreamPoint < 900) {
                           player.send(Field_DreamBreaker.dreamBreakerMsg("드림 포인트가 부족하여 스킬을 사용할 수 없습니다."));
                        } else {
                           c.getPlayer().updateOneInfo(15901, "dream", String.valueOf(dreamPoint - 900));
                           f.setDreamPoint(dreamPoint - 900);
                           player.send(Field_DreamBreaker.dreamBreakerMsg("숙면의 오르골을 공격하던 모든 몬스터가 사라졌습니다!"));

                           for (MapleMonster mx : c.getPlayer().getMap().getAllMonster()) {
                              switch (mx.getId()) {
                                 case 9833070:
                                 case 9833071:
                                 case 9833072:
                                 case 9833073:
                                 case 9833074:
                                 case 9833080:
                                 case 9833081:
                                 case 9833082:
                                 case 9833083:
                                 case 9833084:
                                 case 9833100:
                                    break;
                                 case 9833075:
                                 case 9833076:
                                 case 9833077:
                                 case 9833078:
                                 case 9833079:
                                 case 9833085:
                                 case 9833086:
                                 case 9833087:
                                 case 9833088:
                                 case 9833089:
                                 case 9833090:
                                 case 9833091:
                                 case 9833092:
                                 case 9833093:
                                 case 9833094:
                                 case 9833095:
                                 case 9833096:
                                 case 9833097:
                                 case 9833098:
                                 case 9833099:
                                 default:
                                    c.getPlayer().getMap().killMonster(mx);
                              }
                           }

                           f.setStopRespawnTime(System.currentTimeMillis() + 10000L);
                           f.addUsedSkill(skillIndex);
                        }
                  }

                  player.send(Field_DreamBreaker.dreamBreaker_UseSkillResult());
               }
            }
         }
      }
   }

   public static void willUseMoonGauge(PacketDecoder packet, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            if (map instanceof Field_WillBattle) {
               Field_WillBattle f = (Field_WillBattle)map;
               int needGauge = 45;
               if (f.getPhase() == 2) {
                  needGauge = 40;
               }

               if (player.getWillMoonGauge() < needGauge) {
                  return;
               }

               if (f.getPhase() == 1) {
                  String portal = "";
                  if (player.getTruePosition().y > -2000) {
                     portal = "ptup";
                  } else {
                     portal = "ptdown";
                  }

                  player.send(CField.userWarpPortal(portal));
                  player.temporaryStatSet(182, 3000, SecondaryStatFlag.indiePartialNotDamaged, 1);
               } else if (f.getPhase() == 2) {
                  player.temporaryStatReset(SecondaryStatFlag.DebuffIncHP);
                  player.setNextDebuffIncHPTime(System.currentTimeMillis() + 8000L);
               } else if (f.getPhase() == 3) {
                  player.setWillCanRemoveWeb(3);
                  player.setNextEndRemoveWebTime(System.currentTimeMillis() + 5000L);
               }

               if (!player.isGM()) {
                  player.setWillMoonGauge(player.getWillMoonGauge() - needGauge);
                  f.sendWillUpdateMoonGauge(player, player.getWillMoonGauge());
                  f.sendWillSetCooltimeMoonGauge(player, 7000);
               }
            }
         }
      }
   }

   public static void touchSpiderWeb(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            if (player.isAlive()) {
               if (map instanceof Field_WillBattle) {
                  Field_WillBattle f = (Field_WillBattle)map;
                  int objectID = slea.readInt();
                  SpiderWeb web = f.getSpiderWeb(objectID);
                  if (web != null) {
                     if (player.getWillCanRemoveWeb() > 0) {
                        f.removeSpiderWeb(web);
                        player.setWillCanRemoveWeb(player.getWillCanRemoveWeb() - 1);
                     } else {
                        if (player.getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() > 0
                           || player.getBuffedValue(SecondaryStatFlag.NotDamaged) != null
                           || player.getBuffedValue(SecondaryStatFlag.BlitzShield) != null
                           || player.getBuffedValue(SecondaryStatFlag.StormGuard) != null
                           || player.getBuffedValue(SecondaryStatFlag.EtherealForm) != null
                           || player.getBuffedValue(SecondaryStatFlag.Asura) != null) {
                           return;
                        }

                        if (player.getBuffedValue(SecondaryStatFlag.HolyMagicShell) != null) {
                           Integer value = player.getBuffedValue(SecondaryStatFlag.HolyMagicShell);
                           SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.HolyMagicShell);
                           if (value <= 0) {
                              player.temporaryStatReset(SecondaryStatFlag.HolyMagicShell);
                           } else {
                              SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                              statManager.changeStatValue(SecondaryStatFlag.HolyMagicShell, eff.getSourceId(), value - 1);
                              statManager.temporaryStatSet();
                           }

                           return;
                        }

                        if (player.getBuffedValue(SecondaryStatFlag.BlessingArmor) != null) {
                           Integer v = player.getBuffedValue(SecondaryStatFlag.BlessingArmor);
                           if (v != null) {
                              if (v <= 0) {
                                 player.temporaryStatReset(SecondaryStatFlag.BlessingArmor);
                              } else {
                                 SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.BlessingArmor, 1210016, v - 1);
                                 statManager.temporaryStatSet();
                              }

                              return;
                           }
                        }

                        if (!player.isGM()) {
                           player.addHP(-((long)(player.getStat().getCurrentMaxHp() * 0.3)));
                           player.giveDebuff(SecondaryStatFlag.Seal, 1, 0, 5000L, 120, 40);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void jinHillahThreadHitUser(PacketDecoder slea, MapleClient c) {
      int mobObjectID = slea.readInt();
      int threadObjectID = slea.readInt();
      int playerID = slea.readInt();
      Point pos = slea.readPos();
      Rectangle rect = new Rectangle(slea.readInt(), slea.readInt(), slea.readInt(), slea.readInt());
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            if (player.isAlive()) {
               if (!player.isJinHillahDeathCountOut()) {
                  if (map instanceof Field_JinHillah) {
                     Field_JinHillah f = (Field_JinHillah)map;
                     if (player.getId() == playerID) {
                        if (player.getDiseases(SecondaryStatFlag.Stun) != null) {
                           player.send(CWvsContext.enableActions(player));
                           return;
                        }

                        if (player.getBuffedValue(SecondaryStatFlag.NotDamaged) != null
                           || player.getBuffedValue(SecondaryStatFlag.indiePartialNotDamaged) != null) {
                           player.send(CWvsContext.enableActions(player));
                           return;
                        }

                        if (player.decrementJinHillahDeathCount() == -1) {
                           EventInstanceManager eim = player.getEventInstance();
                           if (eim != null) {
                              eim.stopEventTimer();
                           }

                           player.send(CField.getClock(3));
                           player.setRegisterTransferField(15);
                           player.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
                           player.send(CField.makeEffectScreen("hillah/fail"));
                           player.setJinHillahDeathCountOut();
                           player.addHP(-500000L);
                        } else {
                           player.giveDebuff(SecondaryStatFlag.Stun, 1, 0, 2000L, 123, 83);
                           f.jinHillahGrabSoul(mobObjectID, playerID, rect, pos, 1);
                           f.updateJinHillahCandle();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void updateJinHillahAltarRequest(PacketDecoder slea, MapleClient c) {
      int type = slea.readInt();
      if (type == 9) {
         MapleCharacter player = c.getPlayer();
         if (player == null) {
            return;
         }

         Field map = player.getMap();
         if (map == null) {
            return;
         }

         if (map instanceof Field_JinHillah) {
            Field_JinHillah f = (Field_JinHillah)map;
            f.sendJinHillahUpdateAltarStatus();
         }
      } else if (c.isGm()) {
         c.getPlayer().dropMessage(5, "다른 타입 : " + type);
      }
   }

   public static void setAndroidEar(PacketDecoder slea, MapleClient c) {
      Android android = c.getPlayer().getAndroid();
      if (android == null) {
         c.getPlayer().dropMessage(1, "알 수 없는 오류가 발생 하였습니다.");
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         short slot = slea.readShort();
         Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
         if (item != null && (item.getItemId() == 2892000 || item.getItemId() == 2892001)) {
            android.setEar(!android.isEar());
            c.getPlayer().updateAndroid();
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, true);
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            c.getPlayer().dropMessage(1, "알 수 없는 오류가 발생했습니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      }
   }

   public static void orcaDoAttack(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            if (map instanceof Field_BlackMageBattlePhase3) {
               Field_BlackMageBattlePhase3 f = (Field_BlackMageBattlePhase3)map;
               int objectID = slea.readInt();
               int key = slea.readInt();
               BlackMageOrcaAttackEntry entry = f.getOrcaAttackEntry(key);
               if (entry != null) {
                  MapleMonster boss = f.getMonsterByOid(objectID);
                  if (boss != null) {
                     boss.damage(player, entry.getDamage(), true);
                  }

                  f.removeOrcaAttackEntry(entry);
               }
            }
         }
      }
   }

   public static void blackMageChangeAttributes(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            if (map instanceof Field_BlackMageBattlePhase4) {
               Field_BlackMageBattlePhase4 f = (Field_BlackMageBattlePhase4)map;
               Integer value = player.getBuffedValue(SecondaryStatFlag.BlackMageAttributes);
               int v = 2;
               if (value != null) {
                  v = value;
               }

               if (v == 1) {
                  player.setBlackMageAttributes(2);
               } else {
                  player.setBlackMageAttributes(1);
               }

               player.temporaryStatReset(SecondaryStatFlag.BlackMageAttributes);
               c.getPlayer().temporaryStatSet(SecondaryStatFlag.BlackMageAttributes, 0, Integer.MAX_VALUE, player.getBlackMageAttributes());
               f.blackMageTamporarySkill(player, 8);
            }
         }
      }
   }

   public static void endBurningBreaker(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      byte unk = slea.readByte();
      if (skillID == 400011091) {
         c.getPlayer().setJobField("burningBreakerCount", 0);
         c.getPlayer().invokeJobMethod("setRWCylinder", skillID, -1, -1, 0);
      }
   }

   public static void peaceMakerHealUser(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int skillID = slea.readInt();
            int key = slea.readInt();
            int partyFlag = slea.readInt();
            new Rect(slea.readInt(), slea.readInt(), slea.readInt(), slea.readInt());
            int idCount = slea.readInt();
            List<Integer> ids = new ArrayList<>();

            for (int i = 0; i < idCount; i++) {
               ids.add(slea.readInt());
            }

            SecondaryStatEffect effect = player.getSkillLevelData(skillID);
            if (effect != null) {
               int maxHP = (int)(player.getStat().getCurrentMaxHp() * effect.getHp() / 100L);
               List<Integer> targets = player.parsePartyFlag(partyFlag);
               int count = 0;

               for (int id : targets) {
                  MapleCharacter target = map.getCharacterById(id);
                  if (target != null) {
                     count++;
                     target.healHP(maxHP);
                     PostSkillEffect e_ = new PostSkillEffect(c.getPlayer().getId(), skillID, effect.getLevel(), null);
                     c.getPlayer().send(e_.encodeForLocal());
                     c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e_.encodeForRemote(), false);
                  }
               }

               player.send(CField.incPeaceMakerCount(key, count));
            }
         }
      }
   }

   public static void peaceMakerBuffUser(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int skillID = slea.readInt();
            slea.skip(4);
            int partyFlag = slea.readInt();
            new Rect(slea.readInt(), slea.readInt(), slea.readInt(), slea.readInt());
            int ucount = slea.readInt();
            int tcount = slea.readInt();
            SecondaryStatEffect effect = player.getSkillLevelData(skillID);
            if (effect != null) {
               int hp = (int)(player.getStat().getCurrentMaxHp() * effect.getHp() / 100L);
               List<Integer> targets = player.parsePartyFlag(partyFlag);
               int remaining = Math.min(tcount - ucount, 12);
               int indieDamR = effect.getIndieDamR() + remaining * effect.getX();

               for (int id : targets) {
                  MapleCharacter target = map.getCharacterById(id);
                  if (target != null) {
                     target.temporaryStatSet(skillID, effect.getDuration(), SecondaryStatFlag.indieDamR, indieDamR);
                     PostSkillEffect e_ = new PostSkillEffect(target.getId(), skillID, effect.getLevel(), null);
                     target.send(e_.encodeForLocal());
                     target.getMap().broadcastMessage(target, e_.encodeForRemote(), false);
                  }
               }
            }
         }
      }
   }

   public static void antiMacroRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         AntiMacro macro = player.getAntiMacro();
         if (macro != null) {
            slea.skip(4);
            String input = slea.readMapleAsciiString();
            if (macro.getCaptcha() != null) {
               if (macro.getCaptcha().getAnswer().equals(input)) {
                  player.onSuccessAntiMacro(macro);
               } else {
                  player.onFailedAntiMacro(macro, AntiMacroFailedType.InputFailed);
               }
            }
         }
      }
   }

   public static void userAntiMacroItemUseRequest(PacketDecoder slea, MapleClient c) {
      String target = slea.readMapleAsciiString();
      slea.skip(2);
      int itemID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            MapleCharacter p = map.getCharacterByName(target);
            if (p == null) {
               c.getPlayer().dropMessage(1, "해당 유저를 찾을 수 없습니다.");
            }

            p.tryAntiMacro(AntiMacroType.FromUser, c.getPlayer());
            player.dropMessage(5, target + "(이)에게 거짓말 탐지기 테스트를 요청하였습니다.");
         }
      }
   }

   public static void skillEffectOnOff(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int skillID = slea.readInt();
            String key = String.valueOf(skillID);
            String value = player.getOneInfoQuest(1544, key);
            int value_ = -1;
            if (value != null && !value.isEmpty()) {
               value_ = Integer.parseInt(value);
            } else {
               value_ = 0;
            }

            value_ = value_ != 0 ? 0 : 1;
            switch (skillID) {
               case 1001011:
               case 1101013:
               case 2311001:
               case 3001007:
               case 3011005:
               case 3111013:
               case 3121013:
               case 3141000:
               case 4141500:
               case 5100015:
               case 11001025:
               case 14001026:
               case 14121052:
               case 15100027:
               case 15101021:
               case 20040217:
               case 20040219:
               case 24141500:
               case 32001016:
               case 33111007:
               case 33121114:
               case 35101002:
               case 35101005:
               case 36141000:
               case 36141500:
               case 51121009:
               case 65120006:
               case 65141500:
               case 154121003:
               case 162001002:
               case 162111000:
               case 400011055:
               case 400011121:
               case 400021003:
               case 400021032:
               case 400031003:
               case 400031036:
               case 400041032:
               case 400041035:
               case 400051008:
               case 400051018:
               case 400051072:
                  player.updateOneInfo(1544, key, String.valueOf(value_));
                  break;
               case 1210016:
               case 14110032:
                  player.updateOneInfo(1544, key, String.valueOf(value_));
                  map.broadcastMessage(CField.skillEffectLookLock(player.getId(), skillID, value_));
                  break;
               default:
                  if (player.getClient().isGm()) {
                     player.dropMessage(5, "SkillID : " + skillID);
                  }

                  return;
            }

            switch (skillID) {
               case 1101013:
                  Integer v = player.getBuffedValue(SecondaryStatFlag.Combo);
                  player.setComboX(value_);
                  if (v != null && v > 0) {
                     player.temporaryStatSet(skillID, Integer.MAX_VALUE, SecondaryStatFlag.Combo, v);
                  }
                  break;
               case 2311001:
                  map.broadcastMessage(CField.skillEffectOnOff(player.getId(), value_));
                  break;
               case 5100015:
               case 32001016:
                  map.broadcastMessage(CField.skillEffectLookLock(player.getId(), skillID, value_));
                  break;
               case 20040217:
                  player.setLockEclipseLook(value_);
                  map.broadcastMessage(CField.skillEffectLookLock(player.getId(), skillID, value_));
                  break;
               case 20040219:
               case 35101002:
                  player.setLockEquilibriumLook(value_);
                  map.broadcastMessage(CField.skillEffectLookLock(player.getId(), skillID, value_));
                  break;
               case 33111007:
                  player.updateOneInfo(1544, "wing", String.valueOf(value_ ^ 1));
                  player.setLockBeastFormWingEffect(value_);
                  map.broadcastMessage(CField.skillEffectOnOff(player.getId(), value_));
            }
         }
      }
   }

   public static void userShootAttackInFPSMode(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      slea.skip(4);
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field_EagleHunt f = (Field_EagleHunt)player.getMap();
         int size = slea.readInt();
         f.shootResult();

         for (int i = 0; i < size; i++) {
            int objectID = slea.readInt();
            slea.skip(14);
            MapleMonster mob = f.getMonsterByOid(objectID);
            if (mob != null) {
               f.addScore(mob.getId(), mob.getObjectId());
               f.removeMonster(mob, 1);
            }
         }
      }
   }

   public static void courtshipCommandRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int result = slea.readByte();
            if (result == 1 && map instanceof Field_CourtshipDance) {
               Field_CourtshipDance f = (Field_CourtshipDance)map;
               f.nextStep();
            }
         }
      }
   }

   public static void moveMightyMjolnir(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int inc = slea.readInt();
            int atomKey = slea.readInt();
            int v3 = 0;
            int v4 = 0;
            ForceAtom atom = null;
            if ((atom = map.getForceAtom(atomKey)) != null) {
               if (atom.getForceAtomType() != ForceAtom.AtomType.GLORY_WING_JABELIN) {
                  v3 = slea.readInt();
                  int var13 = slea.readByte();
               }

               int v2 = slea.readInt();
               Point start = slea.readPos();
               Point next = slea.readPos();
            }
         }
      }
   }

   public static void removeSecondAtomWithHeal(PacketDecoder slea, MapleClient c) {
      int key = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         player.removeSecondAtom(key);
         player.send(CField.removeSecondAtom(player.getId(), key));
         SecondaryStatEffect effect = SkillFactory.getSkill(400011047).getEffect(player.getTotalSkillLevel(400011047));
         if (effect != null) {
            if (map != null) {
               SecondAtom.Atom atom = player.getSecondAtom(key);
               if (atom != null) {
                  int maxHP = (int)(player.getStat().getCurrentMaxHp() * 0.01 * effect.getX());
                  if (player.getStat().getCurrentMaxHp() <= player.getStat().getHp()) {
                     int y = effect.getY();
                     int v = effect.getV();
                     Integer value = player.getBuffedValue(SecondaryStatFlag.indieBarrier);
                     int v_ = 0;
                     if (value != null) {
                        v_ = value;
                     }

                     int barrier = (int)Math.min(player.getStat().getCurrentMaxHp() * 0.01 * y, v_ + maxHP * 0.01 * v);
                     player.temporaryStatSet(400011047, effect.getDuration(), SecondaryStatFlag.indieBarrier, barrier);
                  } else {
                     player.healHP(maxHP, true, false);
                  }
               }
            }
         }
      }
   }

   public static void decrementAutoChargeStack(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (skillID == 400011072) {
            SecondaryStatEffect e = player.getBuffedEffect(SecondaryStatFlag.GrandCrossSize);
            if (e != null) {
               if (player.getStat().getHPPercent() >= e.getU2()) {
                  player.addHP((long)(-player.getStat().getCurrentMaxHp() * e.getU2() / 100.0));
               } else {
                  player.temporaryStatReset(SecondaryStatFlag.GrandCrossSize);
               }
            }
         }

         if (skillID == 400021086 && player.getAutoChargeStack() > 0) {
            player.setAutoChargeStack(player.getAutoChargeStack() - 1);
            player.temporaryStatSet(400021086, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, player.getAutoChargeStack());
         }
      }
   }

   public static void incrementSilhouetteMirageStack(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (skillID == 400031053 || skillID == 500061016) {
            player.setJobField("autoChargeStackOnOffStack", (Integer)player.getJobField("autoChargeStackOnOffStack") + 1);
            player.temporaryStatSet(400031053, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStackOnOff, 1);
         }
      }
   }

   public static void incrementAutoChargeStack(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (skillID != 400021047) {
            if (GameConstants.isCannon(player.getJob())) {
               if (skillID == 5311013) {
                  int value = player.getBuffedValueDefault(SecondaryStatFlag.MiniCannonBallStack, -1);
                  player.temporaryStatSet(SecondaryStatFlag.MiniCannonBallStack, skillID, Integer.MAX_VALUE, value + 1);
               } else if (skillID == 400051008) {
                  SecondaryStatEffect effect = player.getSkillLevelData(skillID);
                  if (effect != null) {
                     int value = player.getBuffedValueDefault(SecondaryStatFlag.AutoChargeStack, -1);
                     if (value >= effect.getY()) {
                        return;
                     }

                     Long time = (Long)player.getTempKeyValue("NextAutoChargeTime");
                     if (time != null && time > System.currentTimeMillis()) {
                        player.temporaryStatSet(SecondaryStatFlag.AutoChargeStack, skillID, Integer.MAX_VALUE, value < 0 ? 0 : value);
                     } else {
                        player.setTempKeyValue("NextAutoChargeTime", System.currentTimeMillis() + effect.getQ() * 1000);
                        player.temporaryStatSet(SecondaryStatFlag.AutoChargeStack, skillID, Integer.MAX_VALUE, value + 1);
                     }
                  }
               }
            } else if (player.getAutoChargeSkillID() == skillID
               && (player.getLastAutoChargeTime() == 0L || System.currentTimeMillis() - player.getLastAutoChargeTime() >= player.getAutoChargeCycle())) {
               if (player.getBuffedValue(SecondaryStatFlag.AutoChargeStack) != null && player.getAutoChargeStack() < player.getAutoChargeMaxStack()) {
                  player.setAutoChargeStack(player.getAutoChargeStack() + 1);
               }

               player.temporaryStatSet(player.getAutoChargeSkillID(), Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, player.getAutoChargeStack());
               player.setLastAutoChargeTime(System.currentTimeMillis());
            }
         }
      }
   }

   public static void skillActionCancel(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int skillID = slea.readInt();
         slea.skip(1);
         if (skillID == 400051024) {
            player.temporaryStatResetBySkillID(400051024);
            Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
            flags.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            flags.put(SecondaryStatFlag.indieFlyAcc, 1);
            player.temporaryStatSet(400051024, player.getTotalSkillLevel(400051024), 1800, flags);
         }

         int SLV = player.getTotalSkillLevel(skillID);
         if (SLV > 0) {
            if (skillID == 400031024) {
               player.setStateIrkallasWrath(false);
            }

            player.getMap().broadcastMessage(CField.skillCancel(player, skillID));
         }
      }
   }

   public static void revenantRageRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int rage = slea.readInt();
         if (player.getRemainRevenantCount() > 0) {
            if (player.isAlive()) {
               player.setRemainRevenantCount(player.getRemainRevenantCount() - 1);
               long hp = Math.max(1L, player.getStat().getHp() - rage);
               if (player.getStat().setHp(hp, player)) {
                  player.updateSingleStat(MapleStat.HP, player.getStat().getHp());
               }

               if (player.getRemainRevenantCount() > 0) {
                  int skillID = 400011129;
                  if (player.getTotalSkillLevel(500061058) > 0) {
                     skillID = 500061058;
                  }

                  player.temporaryStatSet(skillID, Integer.MAX_VALUE, SecondaryStatFlag.RevenantRage, 1);
               } else {
                  player.setRevenantRage(0);
                  player.setRemainRevenantCount(0);
                  player.temporaryStatReset(SecondaryStatFlag.RevenantRage);
               }
            }
         }
      }
   }

   public static void poisonChainRequest(PacketDecoder p, MapleClient c) {
      int nextTargetSize = p.readInt();
      List<Integer> nextTargets = new ArrayList<>();

      for (int i = 0; i < nextTargetSize; i++) {
         nextTargets.add(p.readInt());
      }

      int oldTargetSize = p.readInt();
      List<Integer> oldTargets = new ArrayList<>();

      for (int i = 0; i < oldTargetSize; i++) {
         oldTargets.add(p.readInt());
      }
   }

   public static void phtonRayFullCharge(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (player.getBuffedValue(SecondaryStatFlag.PhotonRay) != null) {
            player.setPhotonRayCharge(1);
            SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.PhotonRay, 400041058, 1);
            statManager.temporaryStatSet();
         }
      }
   }

   public static void hungryMutoRequest(PacketDecoder slea, MapleClient c) {
      HungryMuto muto = c.getPlayer().getHungryMuto();
      if (muto != null) {
         boolean find = false;
         boolean endGame = false;

         for (int i = 0; i < muto.getRecipes().length; i++) {
            int[] recipe = muto.getRecipes()[i];
            int itemID = recipe[0];
            int needQ = recipe[1];
            int currentQ = recipe[2];
            if (c.getPlayer().getMutoPickupItemID() == itemID) {
               int delta = Math.min(needQ, currentQ + c.getPlayer().getMutoPickupItemQ());
               muto.updateRecipe(itemID, delta);
               c.getPlayer()
                  .getMap()
                  .broadcastMessage(new HungryMuto.RecipeUpdate(FoodType.getFoodType(muto.getType()), muto.getDifficulty(), muto.getRecipes()).encode());
               c.getSession().writeAndFlush(CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg4"));
               if (muto.checkClear()) {
                  boolean perfect = muto.addScore();
                  c.getPlayer().getMap().broadcastMessage(CField.fieldValue("score", String.valueOf(muto.getScore())));
                  if (perfect) {
                     c.getPlayer().getMap().broadcastMessage(CField.MapEff("Map/Effect3.img/hungryMuto/perfect"));
                  } else {
                     c.getPlayer().getMap().broadcastMessage(CField.MapEff("Map/Effect3.img/hungryMuto/good"));
                  }

                  if (!muto.isEnhanceMob() && muto.getScore() >= 700) {
                     c.getPlayer().getMap().killAllMonsters(true);

                     for (int next = 0; next < 6; next++) {
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642001), new Point(105, -354));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642003), new Point(2644, -345));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642005), new Point(-929, -356));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642007), new Point(3778, -343));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642009), new Point(-1045, -847));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642011), new Point(3935, -841));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642013), new Point(1434, -791));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642015), new Point(1405, -1637));
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8642016), muto.getRandArea());
                     c.getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage("굴라가 더욱 격렬하게 저항하며 강력한 몬스터들이 등장합니다!"));
                     muto.setEnhanceMob(true);
                  }

                  c.getPlayer().getMap().broadcastMessage(CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg5"));
                  if (muto.getScore() >= 990) {
                     endGame = true;
                  } else {
                     muto.changeFood(c.getPlayer());
                  }
               }

               find = true;
            }
         }

         if (c.getPlayer().getMutoPickupItemQ() > 0) {
            if (!find) {
               c.getSession().writeAndFlush(CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg3"));
            }

            c.getPlayer().setMutoPickupItemID(0);
            c.getPlayer().setMutoPickupItemQ(0);
            c.getPlayer().getMap().broadcastMessage(new HungryMuto.PickupItemUpdate(c.getPlayer().getId(), 0, 0).encode());
         }

         if (endGame) {
            muto.gameOver(c.getPlayer(), true);
         }
      }
   }

   public static void setCustomChair(PacketDecoder slea, MapleClient c) {
      int type = slea.readInt();
      if (type == 3) {
         int count = slea.readInt();

         for (int i = 0; i < count; i++) {
            c.getPlayer().getPopChairInfos().add(new Pair<>(slea.readMapleAsciiString(), slea.readInt()));
         }
      }

      c.getPlayer().getMap().broadcastMessage(CField.showChair(c.getPlayer(), c.getPlayer().getChair(), null, c.getPlayer().getMesoChairCount()));
   }

   public static void inviteChairRequest(PacketDecoder slea, MapleClient c) {
      int targetID = slea.readInt();
      MapleCharacter player = c.getPlayer().getMap().getCharacterById(targetID);
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.INVITE_CHAIR.getValue());
      if (player != null) {
         packet.writeInt(7);
         c.getPlayer().send(packet.getPacket());
         packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.REQUIRE_CHAIR.getValue());
         packet.writeInt(c.getPlayer().getId());
         player.send(packet.getPacket());
      } else {
         packet.writeInt(8);
         c.getPlayer().send(packet.getPacket());
      }
   }

   public static void inviteChairResult(PacketDecoder slea, MapleClient c) {
      int ownerID = slea.readInt();
      int result = slea.readInt();
      if (result == 7) {
         MapleCharacter owner = c.getPlayer().getMap().getCharacterById(ownerID);
         if (owner != null) {
            int max = 4;
            if (owner.getChair() == 3016206) {
               max = 3;
            }

            c.getPlayer().send(CField.sitOnDummyChair(owner.getChair(), 0));
            CustomChair chair = owner.getCustomChair();
            if (chair.getPlayers().size() >= max) {
               return;
            }

            if (chair != null) {
               int[] randEmotions = new int[]{2, 10, 14, 17};
               c.getPlayer().setChairEmotion(randEmotions[Randomizer.nextInt(randEmotions.length)]);
               chair.updatePlayer(c.getPlayer());
               c.getPlayer().setChair(owner.getChair());
               c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.showChair(c.getPlayer(), c.getPlayer().getChair(), "", 0L), false);
               c.getPlayer().getMap().broadcastMessage(CField.customChairResult(c.getPlayer(), true, true, false, chair));
               c.getPlayer().setCustomChair(chair);
            } else {
               c.getPlayer().send(CField.sitOnDummyChair(ownerID, 1));
            }
         } else {
            c.getPlayer().send(CField.sitOnDummyChair(ownerID, 1));
         }
      } else {
         c.getPlayer().send(CField.sitOnDummyChair(ownerID, 1));
      }
   }

   public static void kickChairRequest(PacketDecoder slea, MapleClient c) {
      int targetID = slea.readInt();
      MapleCharacter target = c.getPlayer().getMap().getCharacterById(targetID);
      if (target != null) {
         CustomChair chair = c.getPlayer().getCustomChair();
         if (chair != null) {
            chair.removePlayer(target);
            target.setChair(0);
            target.setChairEmotion(0);
            target.setCustomChair(null);
            target.getMap().broadcastMessage(CField.cancelChair(-1, target));
            target.getMap().broadcastMessage(target, CField.showChair(target, 0, "", 0L), false);
            target.getMap().broadcastMessage(CField.customChairResult(target, false, true, false, chair));
         }
      }
   }

   public static void jupiterThunderRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      int delay = slea.readInt();
      int unk2 = slea.readInt();
      int unk3 = slea.readInt();
      int x = slea.readInt();
      int y = slea.readInt();
      int rlType = slea.readInt();
      int unk5 = slea.readInt();
      SecondaryStatEffect effect = c.getPlayer().getSkillLevelData(skillID);
      if (effect != null) {
         JupiterThunder thunder = new JupiterThunder(
            c.getPlayer().getId(), skillID, x, y, delay, unk2, unk3, rlType, unk5, effect.getX(), effect.getSubTime() / 1000, effect.getTime()
         );
         c.getPlayer().setJupiterThunder(thunder);
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.JUPITER_THUNDER.getValue());
         thunder.encode(packet);
         c.getPlayer().send(packet.getPacket());
         c.getPlayer().giveCoolDowns(skillID, System.currentTimeMillis(), effect.getCooldown(c.getPlayer()));
         c.getPlayer().send(CField.skillCooldown(skillID, effect.getCooldown(c.getPlayer())));
      }
   }

   public static void jupiterThunderMoveRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int type = slea.readInt();
         int objectId = slea.readInt();
         int chrId = slea.readInt();
         int skillId = slea.readInt();
         int action = slea.readInt();
         Point pos = slea.readPos();
         int unk1 = slea.readInt();
         int unk2 = slea.readInt();
         int unk3 = slea.readInt();
         if (chr.getJupiterThunder() != null) {
            PacketEncoder mplew = new PacketEncoder();
            mplew.writeShort(SendPacketOpcode.JUPITER_THUNDER_MOVE.getValue());
            mplew.writeInt(chrId);
            mplew.writeInt(type);
            mplew.writeInt(objectId);
            mplew.writeInt(action);
            if (type == 1) {
               mplew.encodePos(pos);
               mplew.writeInt(unk1);
               mplew.writeInt(unk2);
               mplew.writeInt(unk3);
            }

            chr.getMap().broadcastMessage(mplew.getPacket());
         }
      }
   }

   public static void jupiterThunderRemoveRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int objectId = slea.readInt();
         int type = slea.readInt();
         int remainCount = slea.readInt();
         JupiterThunder jupiterThunder = chr.getJupiterThunder();
         if (jupiterThunder != null) {
            if (chr.getSkillLevel(jupiterThunder.getSkillID()) > 0) {
               SecondaryStatEffect effect = SkillFactory.getSkill(jupiterThunder.getSkillID()).getEffect(chr.getSkillLevel(jupiterThunder.getSkillID()));
               long changeTime = (long)(-effect.getT() * remainCount * 1000.0);
               chr.changeCooldown(jupiterThunder.getSkillID(), changeTime);
            }

            PacketEncoder mplew = new PacketEncoder();
            mplew.writeShort(SendPacketOpcode.JUPITER_THUNDER_REMOVE.getValue());
            mplew.writeInt(chr.getId());
            mplew.writeInt(1);
            mplew.writeInt(jupiterThunder.getObjectID());
            chr.getMap().broadcastMessage(mplew.getPacket());
            chr.setJupiterThunder(null);
         }
      }
   }

   public static void skillUpdatePerTick(PacketDecoder slea, MapleClient c) {
      int size = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int skillID = slea.readInt();
            slea.skip(4);
            player.invokeJobMethod("skillUpdatePerTick", skillID, slea);
         }
      }
   }

   public static void applyPirateBless(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      if (skillID == 110 || skillID == 264 || skillID == 265) {
         boolean applyed = c.getPlayer().getOneInfoQuestInteger(1548, "onoff") > 0;
         c.getPlayer().updateOneInfo(1548, "onoff", applyed ? "0" : "1");
      }
   }

   public static void userKaiserColorChangeItemUseRequest(PacketDecoder slea, MapleClient c) {
      int itemPOS = slea.readInt();
      int itemID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (player.getBuffedValue(SecondaryStatFlag.Morph) != null) {
            Item item = player.getInventory(MapleInventoryType.USE).getItem((short)itemPOS);
            if (item != null && (item == null || item.getItemId() == itemID)) {
               int extern = player.getOneInfoQuestInteger(12860, "extern");
               int inner = player.getOneInfoQuestInteger(12860, "inner");
               boolean black = false;
               boolean primium = false;
               switch (itemID) {
                  case 2350004:
                     black = Randomizer.isSuccess(5);
                     if (black) {
                        extern = 721;
                     } else {
                        extern = Randomizer.rand(0, 720);
                     }
                     break;
                  case 2350005:
                     black = Randomizer.isSuccess(5);
                     if (black) {
                        inner = 705;
                     } else {
                        inner = Randomizer.rand(0, 704);
                     }
                     break;
                  case 2350006:
                     extern = 721;
                     inner = 10;
                     primium = true;
                     break;
                  case 2350007:
                     extern = 0;
                     inner = 0;
               }

               MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte)itemPOS, (short)1, false);
               player.updateInfoQuest(12860, "extern=" + extern + ";inner=" + inner + ";primium=" + (primium ? 1 : 0) + ";");
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.USER_KAISER_COLOR_OR_MORPH_CHANGE.getValue());
               packet.writeInt(player.getId());
               packet.writeInt(extern);
               packet.writeInt(inner);
               packet.write(primium);
               player.getMap().broadcastMessage(player, packet.getPacket(), false);
               player.send(CWvsContext.enableActions(player));
            } else {
               player.send(CWvsContext.enableActions(player));
            }
         }
      }
   }

   public static void userCreateAuraByGrenade(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            int grenadeID = packet.readInt();
            int skillID = packet.readInt();
            Point point = packet.readPos();
            boolean left = packet.readByte() == 1;
            SecondaryStatEffect effect = player.getSkillLevelData(skillID);
            if (effect != null) {
               if (skillID != 101120206 && skillID != 101141011) {
                  System.out.println("[ERROR] 지역 스킬 시전에 오류가 발생하였습니다. (skillID: " + skillID + ")");
               } else {
                  Rect rect = new Rect(point, effect.getLt(), effect.getRb(), false);
                  AffectedArea area = new AffectedArea(rect, player, effect, point, System.currentTimeMillis() + effect.getDuration());
                  field.spawnMist(area);
               }
            }
         }
      }
   }

   public static void userOnSetMoveGrenade(PacketDecoder slea, MapleClient c) {
      int grenadeID = slea.readInt();
      long walkSpeed = slea.readLong();
      int moveEndingX = slea.readInt();
      byte left = slea.readByte();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.SET_MOVE_GRENADE.getValue());
            packet.writeInt(player.getId());
            packet.writeInt(grenadeID);
            packet.writeLong(walkSpeed);
            packet.writeInt(moveEndingX);
            packet.write(left);
            map.broadcastMessage(player, packet.getPacket(), false);
         }
      }
   }

   public static void checkKainStackSkillRequest(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (player.getKainStackSKill() == null) {
            player.setKainStackSKill(new KainStackSkill(player));
         }

         KainStackSkill kainStackSkill = player.getKainStackSKill();
         if (!kainStackSkill.hasSkill(skillID)) {
            SecondaryStatEffect effect = player.getSkillLevelData(skillID);
            if (effect == null) {
               return;
            }

            int maxCharge = effect.getW();
            int cycle = effect.getU() * 1000;
            kainStackSkill.addKainStackSkill(skillID, maxCharge, cycle);
         } else {
            kainStackSkill.incrementStack(skillID);
         }
      }
   }

   public static void poolMakerCancelRequest(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         if (chr.getPoolMakerRemain() > 0) {
            SecondaryStatEffect effect = chr.getSkillLevelData(400051074);
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.POOL_MAKER_REQUEST.getValue());
            packet.write(1);
            chr.setPoolMakerRemain(chr.getPoolMakerRemain());
            packet.writeInt(400051074);
            packet.writeInt(chr.getPoolMakerRemain());
            packet.writeInt(effect.getCoolTime());
            chr.send(packet.getPacket());
            effect.applyTo(chr);
         } else {
            PacketEncoder _p = new PacketEncoder();
            _p.writeShort(SendPacketOpcode.POOL_MAKER_REQUEST.getValue());
            _p.write(false);
            chr.send(_p.getPacket());
            chr.setPoolMakerRemain(0);
         }
      }
   }

   public static void removeKainDeathBlessingRequest(PacketDecoder slea, MapleClient c) {
      int size = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         player.invokeJobMethod("removeKainStack", size, slea);
      }
   }

   public static void userDragonAction(PacketDecoder slea, MapleClient c) {
      int action = slea.readInt();
      int skillID = slea.readInt();
      int skillLevel = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (player.getTotalSkillLevel(skillID) == skillLevel) {
            if (skillID == 22140024 || skillID == 22170065) {
               SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
               effect.applyTo(player);
            }

            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.DRAGON_ACTION.getValue());
            packet.writeInt(player.getId());
            packet.writeInt(action);
            packet.writeInt(skillID);
            packet.writeInt(skillLevel);
            player.getMap().broadcastMessage(player, packet.getPacket(), false);
         }
      }
   }

   public static void userDragonBreathEarthEffect(PacketDecoder slea, MapleClient c) {
      int skillID = slea.readInt();
      int skillLevel = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (player.getTotalSkillLevel(skillID) == skillLevel) {
            Point startPos = new Point(slea.readInt(), slea.readInt());
            Point endPos = new Point(slea.readInt(), slea.readInt());
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.DRAGON_BREATH_EARTH_EFFECT.getValue());
            packet.writeInt(player.getId());
            packet.writeInt(skillID);
            packet.writeInt(skillLevel);
            packet.writeInt(startPos.x);
            packet.writeInt(startPos.y);
            packet.writeInt(endPos.x);
            packet.writeInt(endPos.y);
            player.getMap().broadcastMessage(player, packet.getPacket(), false);
         }
      }
   }

   public static void userRenameRequest(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      byte result = slea.readByte();
      int itemID = slea.readInt();
      String currentName = slea.readMapleAsciiString();
      String requestName = slea.readMapleAsciiString();
      boolean tempNameChange = false;
      if (ServerConstants.useTempCharacterName && !c.getPlayer().getName().startsWith("임시") && c.getPlayer().getLevel() >= 210) {
         tempNameChange = true;
      }

      boolean tempNick = false;
      if (c.getPlayer().getOneInfoCustomInteger("Char", "TempChangeNick") == 0 && c.getPlayer().getName().startsWith("임시")) {
         tempNick = true;
      }

      if (itemID != 4034803 || c.getPlayer().getItemQuantity(itemID, false) >= 1 || DBConfig.isGanglim && c.getPlayer().getName().contains("휴면")) {
         int r = 6;
         if (MapleCharacterUtil.canCreateChar(requestName, false, false) && !LoginInformationProvider.getInstance().isForbiddenName(requestName)) {
            r = 0;
         }

         if (currentName.equals(requestName)) {
            r = 7;
         }

         if (r != 6 && r != 7) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            MapleCharacter player = c.getPlayer();
            Field field = player.getMap();
            Party party = player.getParty();
            if (party != null) {
               PartyMemberEntry leaderEntry = player.getParty().getLeader();
               PartyMemberEntry playerEntry = player.getParty().getMemberById(player.getId());
               if (leaderEntry.getId() == player.getId()) {
                  field.onDisbandParty();
                  Center.Party.updateParty(party.getId(), PartyOperation.Disband, playerEntry);
               } else {
                  c.getPlayer().getMap().onLeftParty(c.getPlayer());
                  Center.Party.updateParty(party.getId(), PartyOperation.Withdraw, playerEntry);
               }
            }

            c.getPlayer().setName(requestName);
            if (c.getPlayer().getGuild() == null) {
               int gid = c.getPlayer().getRequestGuildByPlayerId();
               if (gid > 0) {
                  Guild guild = Center.Guild.getGuild(gid);
                  if (guild != null) {
                     guild.removeJoinRequester(c.getPlayer().getId(), false);
                     guild.insertJoinRequester(c.getPlayer(), requestName);
                  }
               }
            }

            if (tempNick) {
               c.getPlayer().updateOneInfoCustom("Char", "TempChangeNick", "1");
            } else if (c.getPlayer().getOneInfoCustomInteger("Char", "TempChangeNick") == 0 && requestName.startsWith("임시")) {
               c.getPlayer().updateOneInfoCustom("Char", "TempChangeNick", "1");
            }

            try (Connection con = DBConnection.getConnection()) {
               ps = con.prepareStatement("SELECT `name` FROM `characters` WHERE `name` = ?");
               ps.setString(1, requestName);
               rs = ps.executeQuery();
               boolean find = false;
               if (rs.next()) {
                  find = true;
               }

               if (find) {
                  r = 7;
               }

               if (r == 0) {
                  ps.close();
                  ps = con.prepareStatement("UPDATE `characters` SET `name` = ? WHERE `name` = ?");
                  ps.setString(1, requestName);
                  ps.setString(2, currentName);
                  ps.executeUpdate();
                  c.getPlayer().removeItem(itemID, -1);
                  StringBuilder sb = new StringBuilder();
                  sb.append("닉네임 변경 (아이피 : ");
                  sb.append(c.getSessionIPAddress());
                  sb.append(", 이전 닉네임 : ");
                  sb.append(currentName);
                  sb.append(", 변경 닉네임 : ");
                  sb.append(requestName);
                  sb.append(")");
                  LoggingManager.putLog(
                     new CreateCharLog(
                        c.getPlayer().getName(), c.getAccountName(), c.getPlayer().getId(), c.getAccID(), CreateCharLogType.ChangeCharName.getType(), sb
                     )
                  );
               }
            } catch (SQLException var29) {
               System.out.println("[ERROR] 닉네임 변경 중 DB오류가 발생하였습니다.");
               var29.printStackTrace();
            } finally {
               try {
                  if (ps != null) {
                     ps.close();
                     PreparedStatement var33 = null;
                  }

                  if (rs != null) {
                     rs.close();
                     ResultSet var35 = null;
                  }
               } catch (SQLException var26) {
               }
            }
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_RENAME_RESULT.getValue());
         packet.write(r);
         c.getPlayer().send(packet.getPacket());
      } else {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_RENAME_RESULT.getValue());
         packet.write(3);
         c.getPlayer().send(packet.getPacket());
      }
   }

   public static void userRenameCheckSPW(PacketDecoder slea, MapleClient c) {
      String spw = slea.readMapleAsciiString();
      int itemID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int result = 9;
         if (!player.haveItem(itemID)
            && (
               !ServerConstants.useTempCharacterName
                  || ServerConstants.useTempCharacterName && !c.getPlayer().getName().startsWith("임시") && c.getPlayer().getLevel() < 210
            )) {
            result = 3;
         }

         if (!c.getSecondPassword().equals(spw)) {
            result = 10;
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_RENAME_RESULT.getValue());
         packet.write(result);
         if (result == 9) {
            packet.writeInt(itemID);
         }

         player.send(packet.getPacket());
      }
   }

   public static void yutGameNextTurn(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_MultiYutGame) {
               Field_MultiYutGame f = (Field_MultiYutGame)field;
               MultiYutGameDlg game = f.getYutGameDlg();
               if (game == null) {
                  return;
               }

               int team = player.getMiniGameTeam();
               if (team == -1) {
                  return;
               }

               YutGameResult_GameInfo gameInfo = f.getYutGameDlg().getGameInfo(team);
               if (gameInfo == null) {
                  return;
               }

               if (!gameInfo.isNextTurn()) {
                  return;
               }

               f.setCurrentTeam(team);
               gameInfo.setDisableYutItem(false);
               gameInfo.setNextTurn(true);
               gameInfo.setThrowYut(false);
               if (f.isAuto()) {
                  f.setAuto(false);
               }

               game.nextTurn(true);
            }
         }
      }
   }

   public static void yutGameRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_MultiYutGame) {
               Field_MultiYutGame f = (Field_MultiYutGame)field;
               MultiYutGameDlg game = f.getYutGameDlg();
               if (game == null) {
                  return;
               }

               int team = player.getMiniGameTeam();
               if (team == -1) {
                  return;
               }

               if (f.getCurrentTeam() != team) {
                  return;
               }

               YutGameResult_GameInfo gameInfo = f.getYutGameDlg().getGameInfo(team);
               if (gameInfo == null) {
                  return;
               }

               if (f.isAuto()) {
                  return;
               }

               int type = slea.readInt();
               if (type == 0) {
                  if (!gameInfo.isNextTurn()) {
                     return;
                  }

                  game.throwYut(gameInfo);
               } else if (type == 1) {
                  int pieceIndex = slea.readInt();
                  int yutIndex = slea.readInt();
                  int position = slea.readInt();
                  game.movePiece(gameInfo, pieceIndex, yutIndex, position);
               } else if (type == 2) {
                  int itemType = slea.readInt();
                  int itemIndex = slea.readInt();
                  game.activeItem(gameInfo, itemType, itemIndex);
               }
            }
         }
      }
   }

   public static void yutExitRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_MultiYutGame) {
               Field_MultiYutGame f = (Field_MultiYutGame)field;
               MultiYutGameDlg game = f.getYutGameDlg();
               if (game == null) {
                  return;
               }

               int team = player.getMiniGameTeam();
               if (team == -1) {
                  return;
               }

               game.surrender(player, team);
            }
         }
      }
   }

   public static void yutGoHomeRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_MultiYutGame) {
               Field_MultiYutGame f = (Field_MultiYutGame)field;
               MultiYutGameDlg game = f.getYutGameDlg();
               if (game == null) {
                  return;
               }

               int team = player.getMiniGameTeam();
               if (team == -1) {
                  return;
               }

               if (f.getCurrentTeam() != team) {
                  return;
               }

               YutGameResult_GameInfo gameInfo = f.getYutGameDlg().getGameInfo(team);
               if (gameInfo == null) {
                  return;
               }

               if (gameInfo.getSuperItem().getActivePieceItem() == YutGameSuperItem.PieceItemType.GoHome.getType()) {
                  int pieceIndex = slea.readInt();
                  YutGameResult_GameInfo info = f.getYutGameDlg().getGameInfo(team ^ 1);
                  game.doGoHome(info, pieceIndex);
               } else if (gameInfo.getSuperItem().getActivePieceItem() == YutGameSuperItem.PieceItemType.CarryAndGo.getType()) {
                  int piece1 = slea.readInt();
                  int piece2 = slea.readInt();
                  game.doCarryAndGo(gameInfo, piece1, piece2);
               } else if (gameInfo.getSuperItem().getActivePieceItem() == YutGameSuperItem.PieceItemType.ChangePosition.getType()) {
                  int piece1 = slea.readInt();
                  int piece2 = slea.readInt();
                  game.doChangePosition(gameInfo, piece1, piece2);
               } else if (gameInfo.getSuperItem().getActivePieceItem() == YutGameSuperItem.PieceItemType.GoMyFront.getType()) {
                  int pieceIndex = slea.readInt();
                  int position = slea.readInt();
                  YutGameResult_GameInfo info = f.getYutGameDlg().getGameInfo(team ^ 1);
                  game.doGoMyFront(info, pieceIndex, position);
               } else if (gameInfo.getSuperItem().getActivePieceItem() == YutGameSuperItem.PieceItemType.BackHug.getType()) {
                  int pieceIndex = slea.readInt();
                  int position = slea.readInt();
                  YutGameResult_GameInfo info = f.getYutGameDlg().getGameInfo(team);
                  game.doBackHug(info, pieceIndex, position);
               } else if (gameInfo.getSuperItem().getActivePieceItem() == YutGameSuperItem.PieceItemType.FinishLineFront.getType()) {
                  int position = slea.readInt();
                  game.doFinishLineFront(gameInfo, position);
               } else if (gameInfo.getSuperItem().getActivePieceItem() == YutGameSuperItem.PieceItemType.InstallBomb.getType()) {
                  int position = slea.readInt();
                  game.doInstallBomb(gameInfo, position);
               }
            }
         }
      }
   }

   public static void eventSkillOnOff(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int skillID = slea.readInt();
      }
   }

   public static void dimensionalMirrorRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         slea.readInt();
         int type = slea.readInt();
         UnionHandler.startNpcWithFlag((DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/dimensional_mirror.js", c, type, 9062454);
      }
   }

   public static void waitQueueRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            slea.readInt();
            WaitQueueRequest request = WaitQueueRequest.get(slea.readByte());
            int queueType = slea.readInt();
            int queueID = slea.readInt();
            switch (request) {
               case DelUser:
                  Center.GameWaitQueue.deleteQueue(player, queueType, queueID);
                  break;
               case AcceptCompleteUser:
                  Center.GameWaitQueue.acceptCompleteUser(player, queueType, queueID);
                  break;
               case CancelCompleteUser:
                  Center.GameWaitQueue.cancelCompleteUser(player, queueType, queueID);
            }
         }
      }
   }

   public static void mission2SpaceInput(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            slea.readInt();
            int type = slea.readInt();
            if (field instanceof Field_Mission2Space) {
               Field_Mission2Space f = (Field_Mission2Space)field;
               f.checkInput(type);
            }
         }
      }
   }

   public static void mission2SpaceExit(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_Mission2Space) {
               Field_Mission2Space f = (Field_Mission2Space)field;
               f.endGame();
            }
         }
      }
   }

   public static void extremeRailEndGame(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_ExtremeRail) {
               Field_ExtremeRail f = (Field_ExtremeRail)field;
               slea.readInt();
               slea.readInt();
               int distance = slea.readInt();
               f.endGame(distance);
            }
         }
      }
   }

   public static void extremeRailExit(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_ExtremeRail) {
               Field_ExtremeRail f = (Field_ExtremeRail)field;
               f.endGame(f.getDistance());
            }
         }
      }
   }

   public static void extremeRailUpdateDistance(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_ExtremeRail) {
               Field_ExtremeRail f = (Field_ExtremeRail)field;
               int distance = slea.readInt();
               f.setDistance(distance);
            }
         }
      }
   }

   public static void mannequinRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         slea.skip(4);
         MannequinType type = MannequinType.getType(slea.readByte());
         MannequinRequestType requestType = MannequinRequestType.getType(slea.readByte());
         byte slot = slea.readByte();
         Mannequin mannequin = null;
         int itemID = 0;
         if (slea.available() == 4L) {
            itemID = slea.readInt();
         }

         boolean second = false;
         byte jobflag = 0;
         if (requestType != MannequinRequestType.Expend) {
            jobflag = slea.readByte();
            if (jobflag == 2 && GameConstants.isZero(player.getJob())) {
               second = player.getZeroInfo().isBeta();
            } else if (jobflag == 1 && GameConstants.isAngelicBuster(player.getJob())) {
               second = player.isDressUp();
            }
         }

         switch (type) {
            case HairRoom:
               mannequin = player.getHairMannequin();
               break;
            case FaceRoom:
               mannequin = player.getFaceMannequin();
               break;
            case SkinRoom:
               mannequin = player.getSkinMannequin();
         }

         MannequinEntry entry = mannequin.getRooms()[slot];
         if (requestType == MannequinRequestType.Expend || entry != null) {
            switch (requestType) {
               case Expend:
                  if (itemID == 5680222) {
                     if (mannequin.getSlotMax() <= mannequin.getSaveCount()) {
                        player.dropMessage(1, "슬롯이 부족하여 마네킹을 보관할 수 없습니다.");
                        return;
                     }

                     MannequinEntry newEntry = mannequin.extendMannequin();
                     if (newEntry == null) {
                        return;
                     }

                     mannequin.updateMannequin(player, type, requestType, MannequinResultType.Extend, slot, newEntry);
                     mannequin.updateMannequin(player, type, requestType, MannequinResultType.Unk, slot, newEntry);
                     player.setSaveFlag2(player.getSaveFlag2() | CharacterSaveFlag2.MANNEQUIN.getFlag());
                     player.removeItem(itemID, -1);
                  }
                  break;
               case Save:
                  if (type == MannequinType.HairRoom) {
                     if (GameConstants.isZero(player.getJob()) && second) {
                        ZeroInfo zeroInfox = player.getZeroInfo();
                        entry.setItemID(zeroInfox.getSubHair());
                        entry.setBaseProb((byte)zeroInfox.getMixHairBaseProb());
                        entry.setBaseColor((byte)zeroInfox.getMixBaseHairColor());
                        entry.setAddColor((byte)zeroInfox.getMixAddHairColor());
                     } else {
                        entry.setItemID(second ? player.getSecondHair() : player.getHair());
                        entry.setBaseProb((byte)(second ? player.getSecondBaseProb() : player.getBaseProb()));
                        entry.setBaseColor((byte)(second ? player.getSecondBaseColor() : player.getBaseColor()));
                        entry.setAddColor((byte)(second ? player.getSecondAddColor() : player.getAddColor()));
                     }
                  } else if (type == MannequinType.FaceRoom) {
                     int face = second ? player.getSecondFace() : player.getFace();
                     if (GameConstants.isZero(player.getJob()) && second) {
                        ZeroInfo zeroInfox = player.getZeroInfo();
                        face = zeroInfox.getSubFace();
                     }

                     int faceBaseProb = second ? player.getSecondFaceBaseProb() : player.getFaceBaseProb();
                     int faceBaseColor = second ? player.getSecondFaceBaseColor() : player.getFaceBaseColor();
                     int faceAddColor = second ? player.getSecondFaceAddColor() : player.getFaceAddColor();
                     if (face > 100000) {
                        int tempFace = face;
                        if (face % 1000 == 0) {
                           face /= 1000;
                           faceBaseColor = -1;
                           faceAddColor = 0;
                           faceBaseProb = 0;
                        } else {
                           face /= 1000;
                           faceBaseColor = face / 100 % 10;
                           faceAddColor = tempFace / 100 % 10;
                           faceBaseProb = tempFace % 100;
                        }
                     }

                     entry.setItemID(face);
                     entry.setBaseProb((byte)faceBaseProb);
                     entry.setBaseColor((byte)faceBaseColor);
                     entry.setAddColor((byte)faceAddColor);
                  } else if (type == MannequinType.SkinRoom) {
                     if (GameConstants.isZero(player.getJob()) && second) {
                        entry.setItemID(player.getZeroInfo().getSubSkin());
                     } else {
                        entry.setItemID(second ? player.getSecondSkinColor() : player.getSkinColor());
                     }
                  }

                  mannequin.updateMannequin(player, type, requestType, MannequinResultType.Save, slot, entry);
                  mannequin.sendMannequinResult(player, type, requestType, 1);
                  player.setSaveFlag2(player.getSaveFlag2() | CharacterSaveFlag2.MANNEQUIN.getFlag());
                  break;
               case Delete:
                  if (type == MannequinType.SkinRoom) {
                     entry.setItemID(-1);
                  } else {
                     entry.setItemID(0);
                  }

                  entry.setBaseProb((byte)0);
                  entry.setBaseColor((byte)-1);
                  entry.setAddColor((byte)0);
                  mannequin.updateMannequin(player, type, requestType, MannequinResultType.Save, slot, entry);
                  mannequin.sendMannequinResult(player, type, requestType, 1);
                  player.setSaveFlag2(player.getSaveFlag2() | CharacterSaveFlag2.MANNEQUIN.getFlag());
                  break;
               case Change:
                  if (type == MannequinType.HairRoom) {
                     if (GameConstants.isZero(player.getJob()) && second) {
                        ZeroInfo zeroInfo = player.getZeroInfo();
                        int beforeHair = zeroInfo.getSubHair();
                        int beforeBaseProb = zeroInfo.getMixHairBaseProb();
                        int beforeBaseColor = zeroInfo.getMixBaseHairColor();
                        int beforeAddColor = zeroInfo.getMixAddHairColor();
                        zeroInfo.setSubHair(entry.getItemID());
                        zeroInfo.setMixHairBaseProb(entry.getBaseProb());
                        zeroInfo.setMixBaseHairColor(entry.getBaseColor());
                        zeroInfo.setMixAddHairColor(entry.getAddColor());
                        entry.setItemID(beforeHair);
                        entry.setBaseProb((byte)beforeBaseProb);
                        entry.setBaseColor((byte)beforeBaseColor);
                        entry.setAddColor((byte)beforeAddColor);
                        zeroInfo.sendUpdateZeroInfo(player, ZeroInfoFlag.SubHair);
                     } else {
                        int beforeHair = second ? player.getSecondHair() : player.getHair();
                        int beforeBaseProb = second ? player.getSecondBaseProb() : player.getBaseProb();
                        int beforeBaseColor = second ? player.getSecondBaseColor() : player.getBaseColor();
                        int beforeAddColor = second ? player.getSecondAddColor() : player.getAddColor();
                        if (second) {
                           player.setSecondHair(entry.getItemID());
                           player.setSecondBaseProb(entry.getBaseProb());
                           player.setSecondBaseColor(entry.getBaseColor());
                           player.setSecondAddColor(entry.getAddColor());
                           int entryHair = entry.getItemID();
                           int entryBaseColor = entry.getBaseColor();
                           int entryAddColor = entry.getAddColor();
                           int entryBaseProb = entry.getBaseProb();
                           int mixHair = entryHair;
                           if (entryBaseColor != -1 && entryBaseProb > 0) {
                              mixHair = entryHair / 10 * 10 * 1000 + entryBaseColor * 1000 + entryAddColor * 100 + entryBaseProb;
                           }

                           player.updateSingleStat(MapleStat.HAIR, mixHair);
                        } else {
                           player.setHair(entry.getItemID());
                           player.setBaseProb(entry.getBaseProb());
                           player.setBaseColor(entry.getBaseColor());
                           player.setAddColor(entry.getAddColor());
                           int entryHair = entry.getItemID();
                           int entryBaseColor = entry.getBaseColor();
                           int entryAddColor = entry.getAddColor();
                           int entryBaseProb = entry.getBaseProb();
                           int mixHair = entryHair;
                           if (entryBaseColor != -1 && entryBaseProb > 0) {
                              mixHair = entryHair / 10 * 10 * 1000 + entryBaseColor * 1000 + entryAddColor * 100 + entryBaseProb;
                           }

                           player.updateSingleStat(MapleStat.HAIR, mixHair);
                        }

                        entry.setItemID(beforeHair);
                        entry.setBaseProb((byte)beforeBaseProb);
                        entry.setBaseColor((byte)beforeBaseColor);
                        entry.setAddColor((byte)beforeAddColor);
                     }
                  } else if (type == MannequinType.FaceRoom) {
                     ZeroInfo zeroInfo = null;
                     int beforeFace = second ? player.getSecondFace() : player.getFace();
                     if (GameConstants.isZero(player.getJob()) && second) {
                        zeroInfo = player.getZeroInfo();
                        beforeFace = zeroInfo.getSubFace();
                     }

                     int beforeBaseProb = second ? player.getSecondFaceBaseProb() : player.getFaceBaseProb();
                     int beforeBaseColor = second ? player.getSecondFaceBaseColor() : player.getFaceBaseColor();
                     int beforeAddColor = second ? player.getSecondFaceAddColor() : player.getFaceAddColor();
                     if (beforeFace > 100000) {
                        int tempFace = beforeFace;
                        if (beforeFace % 1000 == 0) {
                           beforeFace /= 1000;
                           beforeBaseColor = -1;
                           beforeAddColor = 0;
                           beforeBaseProb = 0;
                        } else {
                           beforeFace /= 1000;
                           beforeBaseColor = beforeFace / 100 % 10;
                           beforeAddColor = tempFace / 100 % 10;
                           beforeBaseProb = tempFace % 100;
                        }
                     }

                     if (second) {
                        player.setSecondFace(entry.getItemID());
                        if (zeroInfo != null) {
                           zeroInfo.setSubFace(entry.getItemID());
                        }

                        player.setSecondFaceBaseProb(entry.getBaseProb());
                        player.setSecondFaceBaseColor(entry.getBaseColor());
                        player.setSecondFaceAddColor(entry.getAddColor());
                        int entryFace = entry.getItemID();
                        int entryBaseColor = entry.getBaseColor();
                        int entryAddColor = entry.getAddColor();
                        int entryBaseProb = entry.getBaseProb();
                        int mixFace = entryFace;
                        if (entryBaseColor != -1 && entryBaseProb > 0) {
                           mixFace = (entryFace / 1000 * 1000 + entryFace % 100 + entryBaseColor * 100) * 1000 + entryAddColor * 100 + entryBaseProb;
                        }

                        player.updateSingleStat(MapleStat.FACE, mixFace);
                     } else {
                        player.setFace(entry.getItemID());
                        player.setFaceBaseProb(entry.getBaseProb());
                        player.setFaceBaseColor(entry.getBaseColor());
                        player.setFaceAddColor(entry.getAddColor());
                        int entryFace = entry.getItemID();
                        int entryBaseColor = entry.getBaseColor();
                        int entryAddColor = entry.getAddColor();
                        int entryBaseProb = entry.getBaseProb();
                        int mixFace = entryFace;
                        if (entryBaseColor != -1 && entryBaseProb > 0) {
                           mixFace = (entryFace / 1000 * 1000 + entryFace % 100 + entryBaseColor * 100) * 1000 + entryAddColor * 100 + entryBaseProb;
                        }

                        player.updateSingleStat(MapleStat.FACE, mixFace);
                     }

                     entry.setItemID(beforeFace);
                     entry.setBaseProb((byte)beforeBaseProb);
                     entry.setBaseColor((byte)beforeBaseColor);
                     entry.setAddColor((byte)beforeAddColor);
                     if (zeroInfo != null) {
                        zeroInfo.sendUpdateZeroInfo(player, ZeroInfoFlag.SubFace);
                     }
                  } else if (type == MannequinType.SkinRoom) {
                     if (GameConstants.isZero(player.getJob()) && second) {
                        int beforeSkin = player.getZeroInfo().getSubSkin();
                        if (entry.getItemID() >= 0 && entry.getItemID() < 100) {
                           player.getZeroInfo().setSubSkin(entry.getItemID());
                           player.setSecondSkinColor((byte)entry.getItemID());
                        } else if (entry.getItemID() >= 12000 && entry.getItemID() <= 13000) {
                           player.getZeroInfo().setSubSkin(entry.getItemID() - 12000);
                           player.setSecondSkinColor((byte)(entry.getItemID() - 12000));
                        } else {
                           player.getZeroInfo().setSubSkin(0);
                           player.setSecondSkinColor((byte)0);
                        }

                        player.getZeroInfo().sendUpdateZeroInfo(player, ZeroInfoFlag.SubSkin);
                        entry.setItemID(beforeSkin);
                     } else {
                        int beforeSkin = second ? player.getSecondSkinColor() : player.getSkinColor();
                        if (second) {
                           if (entry.getItemID() >= 0 && entry.getItemID() < 100) {
                              player.setSecondSkinColor((byte)entry.getItemID());
                              player.updateSingleStat(MapleStat.SKIN, player.getSecondSkinColor());
                           } else if (entry.getItemID() >= 12000 && entry.getItemID() <= 13000) {
                              player.setSecondSkinColor((byte)(entry.getItemID() - 12000));
                              player.updateSingleStat(MapleStat.SKIN, player.getSecondSkinColor());
                           } else {
                              player.setSecondSkinColor((byte)0);
                              player.updateSingleStat(MapleStat.SKIN, player.getSecondSkinColor());
                           }
                        } else if (entry.getItemID() >= 0 && entry.getItemID() < 100) {
                           player.setSkinColor((byte)entry.getItemID());
                           player.updateSingleStat(MapleStat.SKIN, player.getSkinColor());
                        } else if (entry.getItemID() >= 12000 && entry.getItemID() <= 13000) {
                           player.setSkinColor((byte)(entry.getItemID() - 12000));
                           player.updateSingleStat(MapleStat.SKIN, player.getSkinColor());
                        } else {
                           player.setSkinColor(0);
                           player.updateSingleStat(MapleStat.SKIN, player.getSkinColor());
                        }

                        entry.setItemID(beforeSkin);
                     }
                  }

                  mannequin.updateMannequin(player, type, requestType, MannequinResultType.Save, slot, entry);
                  mannequin.updateMannequin(player, type, requestType, MannequinResultType.Change, slot, entry);
                  player.send(CWvsContext.enableActions(player));
                  player.setSaveFlag2(player.getSaveFlag2() | CharacterSaveFlag2.MANNEQUIN.getFlag());
                  player.equipChanged();
            }
         }
      }
   }

   public static void debuffObjCollision(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            int key = slea.readInt();
            int dataType = slea.readInt();
            field.onDebuffObjCollision(player, key, dataType);
         }
      }
   }

   public static void laraDragonVeinCreateRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            byte mode = packet.readByte();
            SecondaryStatEffect level = player.getSkillLevelData(162101000);
            if (level != null) {
               int key = packet.readInt();
               byte type = packet.readByte();
               int xPos = packet.readInt();
               int yPos = packet.readInt();
               if (level.getQ2() > player.getSecondAtomCount(SecondAtom.SecondAtomType.DragonVein)) {
                  SecondAtom.Atom a = new SecondAtom.Atom(
                     field, player.getId(), 162101000, SecondAtom.SN.getAndAdd(1), SecondAtom.SecondAtomType.DragonVein, 0, null, new Point(xPos, yPos)
                  );
                  Skill skill = SkillFactory.getSkill(162101000);
                  List<SecondAtom.Atom> atoms = new ArrayList<>();
                  if (skill != null) {
                     a.setPlayerID(player.getId());
                     a.setSkillID(162101000);
                     a.setAttackableCount(1);
                     a.setRange(1);
                     a.setPos(new Point(xPos, yPos));
                     a.setUnk2(1);
                     List<SecondAtom.Custom> customs = new ArrayList<>();
                     customs.add(new SecondAtom.Custom(0, type));
                     customs.add(new SecondAtom.Custom(1, key));
                     a.setCustoms(customs);
                     player.addSecondAtom(a);
                     atoms.add(a);
                  }

                  if (!atoms.isEmpty()) {
                     SecondAtom secondAtom = new SecondAtom(player.getId(), 162101000, atoms);
                     field.createSecondAtom(secondAtom);
                  }
               }
            }
         }
      }
   }

   public static void quickMoveRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         int npcID = packet.readInt();
         if (npcID == 1531030) {
            ScriptManager.runScript(client, "Royal_QuickMove", MapleLifeFactory.getNPC(1531030));
         }
      }
   }

   public static void dazzleHit(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         packet.skip(4);
         int attractReason = client.getPlayer().getSecondaryStatReason(SecondaryStatFlag.Attract);
         if (attractReason == 188) {
            int attractLevel = client.getPlayer().getSecondaryStat().AttractLevel;
            player.temporaryStatReset(SecondaryStatFlag.Attract);
            player.send(CField.getCharacterExpression(17, 1000));
            player.getMap().broadcastMessage(player, CField.facialExpressionWithDuration(player, 17, 1000), false);
            player.addHP(-99999999L, true);
            MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(attractReason, attractLevel);
            if (mobSkillInfo != null) {
               for (MapleMonster mob : player.getMap().getAllMonstersThreadsafe()) {
                  if (mob.getStats().isBoss() || mob.getId() == 8920005) {
                     int heal = (int)(mob.getStats().getMaxHp() * 0.01 * mobSkillInfo.getMobSkillStatsInt(MobSkillStat.bossHeal));
                     mob.heal(heal, 0, true);
                  }
               }
            }
         }
      }
   }

   public static void bounceAttackCollision(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int id = packet.readInt();
            boolean userCollide = packet.readInt() != 0;
            map.onDynamicObjectCollision(player, id, userCollide);
         }
      }
   }

   public static void serenDelayEnd(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            if (map instanceof Field_SerenPhase2) {
               Field_SerenPhase2 f = (Field_SerenPhase2)map;
               packet.skip(8);
               int delaySN = packet.readInt();
               int serealNumber = f.getDelaySN();
               if (serealNumber == delaySN) {
                  f.getAndAddDelaySN();
                  if (delaySN % 2 == 0) {
                     f.startNextPhase();
                  } else {
                     f.doNextPhase();
                  }
               }
            }
         }
      }
   }

   public static void HolyUnityChangeTarget(PacketDecoder o, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         boolean isInvincible = false;
         int targetID = o.readInt();
         MapleCharacter target = chr.getMap().getCharacterById(targetID);
         if (target != null) {
            SecondaryStatEffect holyUnity = chr.getBuffedEffect(SecondaryStatFlag.HolyUnity);
            if (holyUnity != null) {
               MapleCharacter oldTarget = chr.getMap().getCharacterById(chr.getBuffedValue(SecondaryStatFlag.HolyUnity));
               if (oldTarget != null) {
                  oldTarget.temporaryStatReset(SecondaryStatFlag.HolyUnity);
                  if (oldTarget.hasBuffBySkillID(1221054)) {
                     oldTarget.temporaryStatResetBySkillID(1221054);
                     isInvincible = true;
                  }
               }

               SecondaryStatManager statManager = new SecondaryStatManager(c, chr.getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.HolyUnity, 400011003, targetID);
               statManager.temporaryStatSet();
               long duration = chr.getSecondaryStat().getTill(SecondaryStatFlag.HolyUnity) - System.currentTimeMillis();
               target.temporaryStatSet(SecondaryStatFlag.HolyUnity, 400011021, (int)duration, chr.getId());
               chr.setJobField("lastHolyUnityCharId", target.getId());
               if (isInvincible) {
                  target.temporaryStatSet(1221054, (int)duration, SecondaryStatFlag.indiePartialNotDamaged, 1);
               }
            }
         }
      }
   }

   public static void reincarnationAccept(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      int type = slea.readInt();
      if (chr != null && chr.getMap() != null && chr.getJob() == 132 && chr.getSkillLevel(1321020) > 0) {
         chr.temporaryStatSet(SecondaryStatFlag.ReincarnationAccept, 1321020, Integer.MAX_VALUE, type);
      }
   }

   public static void trainMaster(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      int type = slea.readInt();
      if (chr != null && chr.getMap() != null) {
         if (chr.getMap() instanceof Field_TrainMaster) {
            switch (type) {
               case 0:
                  int option1 = slea.readInt();
                  int option2 = slea.readInt();
                  int option3 = slea.readInt();
                  slea.skip(1);
                  int option4 = slea.readInt();
                  ((Field_TrainMaster)chr.getMap()).movePlayer(chr.getId(), option1, option2, option3, option4);
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
            }
         }

         if (chr.getMap().getFieldSetInstance() != null && chr.getMap().getFieldSetInstance() instanceof MulungForest) {
            switch (type) {
               case 101:
                  chr.getMap().killAllMonsters(true);
                  int fieldType = slea.readInt();
                  int fieldForce = slea.readInt();
                  switch (fieldType) {
                     case 0:
                        chr.getMap().setNeedStarForce(0);
                        chr.getMap().setNeedAuthenticForce(0);
                        chr.getMap().setNeedArcaneForce(0);
                        break;
                     case 1:
                        chr.getMap().setNeedStarForce(fieldForce);
                        chr.getMap().setNeedAuthenticForce(0);
                        chr.getMap().setNeedArcaneForce(0);
                        break;
                     case 2:
                        chr.getMap().setNeedStarForce(0);
                        chr.getMap().setNeedAuthenticForce(0);
                        chr.getMap().setNeedArcaneForce(fieldForce);
                        break;
                     case 3:
                        chr.getMap().setNeedStarForce(0);
                        chr.getMap().setNeedAuthenticForce(fieldForce);
                        chr.getMap().setNeedArcaneForce(0);
                  }

                  chr.getMap().broadcastMessage(MulungForest.initMulungField(fieldType, fieldForce));
                  break;
               case 102:
                  int command = slea.readInt();
                  switch (command) {
                     case 1:
                        if (chr.getMap().getAllMonster().size() >= 10) {
                           chr.dropMessage(5, "10마리를 넘게 소환할 수 없습니다.");
                           return;
                        }

                        int size = slea.readInt();
                        if (size > 2 || size < 0) {
                           return;
                        }

                        int level = slea.readInt();
                        slea.readInt();
                        slea.readInt();
                        int pdRate = slea.readInt();
                        int mdRate = slea.readInt();
                        int acc = slea.readInt();
                        int eva = slea.readInt();
                        boolean harved = slea.readInt() > 0;
                        boolean isBoss = slea.readInt() > 0;
                        long maxHp = slea.readLong();
                        Point pos = new Point(slea.readInt(), slea.readInt());
                        int[][] mobList = new int[][]{
                           {9834020, 9834021, 9834022, 9834023}, {9834024, 9834025, 9834026, 9834027}, {9834028, 9834029, 9834030, 9834031}
                        };
                        int a = 1;
                        if (isBoss && harved) {
                           a = 0;
                        } else if (!isBoss && harved) {
                           a = 2;
                        } else if (!isBoss && !harved) {
                           a = 3;
                        }

                        int b = mobList[size][a];
                        MapleMonster mob = MapleLifeFactory.getMonster(b);
                        if (!mob.getStats().isChangeable()) {
                           mob.getStats().setChange(true);
                        }

                        ChangeableStats stat = new ChangeableStats(mob.getStats(), new OverrideMonsterStats(maxHp, mob.getStats().getMp(), 0L));
                        stat.level = level;
                        stat.PDRate = pdRate;
                        stat.MDRate = mdRate;
                        stat.acc = acc;
                        stat.eva = eva;
                        mob.changeCustomStat(stat);
                        chr.getMap().spawnMonsterOnGroundBelow(mob, pos);
                        return;
                     case 2:
                        if (chr.getMap().getAllMonster().size() > 0) {
                           chr.getMap().killMonster(chr.getMap().getAllMonster().get(chr.getMap().getAllMonster().size() - 1));
                        }

                        return;
                     case 3:
                        chr.getMap().killAllMonsters(true);
                        return;
                     default:
                        return;
                  }
               case 103:
                  chr.addMPHP(500000L, 500000L);
            }
         }
      }
   }

   public static void completeHiddenMission(MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null && !DBConfig.isGanglim) {
         if (chr.getOneInfoQuestInteger(QuestExConstants.SuddenMKInit.getQuestID(), "state") != 3) {
            return;
         }

         int[][] itemList = new int[][]{
            {4001832, 100}, {4001832, 200}, {4001832, 300}, {2711001, 10}, {2711005, 10}, {2711006, 10}, {2048723, 1}, {2048724, 1}
         };
         int rand = Randomizer.nextInt(itemList.length);
         double rate;
         switch (chr.getLevel() / 10) {
            case 11:
               rate = 9.5;
               break;
            case 12:
               rate = 9.0;
               break;
            case 13:
               rate = 8.5;
               break;
            case 14:
               rate = 8.0;
               break;
            case 15:
               rate = 7.5;
               break;
            case 16:
               rate = 7.0;
               break;
            case 17:
               rate = 6.5;
               break;
            case 18:
               rate = 6.0;
               break;
            case 19:
               rate = 5.5;
               break;
            case 20:
               rate = 5.0;
               break;
            case 21:
               rate = 4.5;
               break;
            case 22:
               rate = 4.0;
               break;
            case 23:
               rate = 3.0;
               break;
            case 24:
               rate = 2.0;
               break;
            case 25:
               rate = 1.0;
               break;
            case 26:
               rate = 0.5;
               break;
            case 27:
               rate = 0.1;
               break;
            case 28:
               rate = 0.05;
               break;
            case 29:
               rate = 0.01;
               break;
            default:
               rate = 10.0;
         }

         if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() > 0 && chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() > 0) {
            chr.gainExp(GameConstants.getExpNeededForLevel(chr.getLevel()) * rate / 100.0, false, false, false);
            if (Randomizer.nextBoolean()) {
               int meso = 5000000;
               chr.gainMeso(meso, true);
               AchievementFactory.checkSuddenmissionReward(chr, meso);
            } else {
               chr.gainItem(itemList[rand][0], itemList[rand][1]);
            }

            chr.checkHasteQuestComplete(QuestExConstants.HasteEventSuddenMK.getQuestID());
            chr.updateOneInfo(QuestExConstants.SuddenMKInit.getQuestID(), "Quest", "0");
            chr.updateOneInfo(QuestExConstants.SuddenMKInit.getQuestID(), "state", "1");
            chr.updateOneInfo(QuestExConstants.SuddenMKInit.getQuestID(), "LastComplete", System.currentTimeMillis() + "");
            c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(itemList[rand][0], (byte)itemList[rand][1], true));
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
         } else {
            chr.dropMessage(5, "소비창과 기타창을 1칸 비워주세요.");
         }
      }
   }

   public static void onSkillRequesetArea(PacketDecoder slea, MapleClient c) {
      int type = slea.readInt();
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         switch (type) {
            case 0:
               int skillID = slea.readInt();
               Point point = new Point(slea.readInt(), slea.readInt());
               chr.skillRequestArea.put(skillID, point);
               break;
            case 1:
               chr.skillRequestArea.clear();
         }
      }
   }

   public static void handleBlackList(PacketDecoder slea, MapleClient c) {
      byte type = slea.readByte();
      byte type2 = slea.readByte();
      String chrName = slea.readMapleAsciiString();
      String denoteName = slea.readMapleAsciiString();
      int chrId = MapleCharacterUtil.getIdByName(chrName);
      int accId = MapleCharacterUtil.getAccByName(chrName);
      if (chrId == -1 && type2 == 0) {
         c.getPlayer().send(CWvsContext.blackList(false, (byte)6, chrName, denoteName, chrId, 0));
      } else {
         if (type2 == 0) {
            BlackList blackList = new BlackList(chrName, denoteName, chrId, 0);
            c.getPlayer().getBlackLists().add(blackList);
         }

         if (type2 == 2) {
            BlackList blackList = c.getPlayer().getBlackListByDenoteName(denoteName);
            chrName = blackList.getName();
            chrId = blackList.getId();
            c.getPlayer().getBlackLists().remove(blackList);
         }

         c.getPlayer().setSaveFlag2(c.getPlayer().getSaveFlag2() | CharacterSaveFlag2.BLACK_LIST.getFlag());
         c.getPlayer().send(CWvsContext.blackList(false, ++type2, chrName, denoteName, chrId, 0));
      }
   }

   public static void setMemoryChoice(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int skillID = slea.readInt();
         chr.invokeJobMethod("setMemoryChoice", skillID, true);
      }
   }

   public static void addPoisonRegion(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int skillID = 2111013;
         slea.skip(1);
         Summoned summon;
         if ((summon = chr.getSummonBySkillID(skillID)) != null) {
            SecondaryStatEffect summonEffect = SkillFactory.getSkill(skillID).getEffect(summon.getSkillLevel());
            int count = slea.readInt();

            for (int i = 0; i < count; i++) {
               int x = slea.readInt();
               int y = slea.readInt();
               Point addPos = new Point(x, y);
               Rectangle rectangle = new Rectangle(x - 100, y - 100, 200, 200);
               if (chr.getMap().getLeft() <= x - 100 && x + 100 <= chr.getMap().getRight()) {
                  AffectedArea aa = new AffectedArea(rectangle, chr, summonEffect, addPos, 1, System.currentTimeMillis() + summonEffect.getDOTTime() * 1000);
                  chr.getMap().spawnMist(aa);
               }
            }
         }
      }
   }

   public static void chargeInfinityFlameCircle(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         int stack = slea.readInt();
         if (stack != 10) {
            player.invokeJobMethod("setInfinityFlameCircle", stack);
         }
      }
   }

   public static void guardStackRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         chr.invokeJobMethod("onCheckRoyalGuard");
      }
   }

   public static void demonAvengerInit(MapleClient c) {
      MapleCharacter chr;
      if ((chr = c.getPlayer()) != null) {
         chr.invokeJobMethod("initDemonAvenger");
      }
   }

   public static void demonFrenzyRequest(MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         SecondaryStatEffect frenzyEffect = chr.getBuffedEffect(SecondaryStatFlag.DemonFrenzy, 400011010);
         if (frenzyEffect != null) {
            if (chr.getStat().getHPPercent() > frenzyEffect.getQ2()) {
               chr.addHP(-frenzyEffect.getY());
            }
         }
      }
   }

   public static void openUIInfo(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      int status = p.readInt();
      int uiType = p.readInt();
      if (chr == null || chr.getMap() == null) {
         ;
      }
   }

   public static void saveChattingShortCut(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int index = p.readInt();
         int remain = (int)p.available();
         byte[] buffer = p.read(remain);
         boolean delete = buffer[0] == 0;
         new String(buffer);

         try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `index` FROM `shortcutcmd` WHERE `chrid` = ?")) {
               ps.setInt(1, chr.getId());

               try (ResultSet rs = ps.executeQuery()) {
                  while (rs.next()) {
                     if (rs.getInt("index") == index) {
                        try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM `shortcutcmd` WHERE `chrid` = ? AND `index` = ?")) {
                           ps2.setInt(1, chr.getId());
                           ps2.setInt(2, index);
                           ps2.execute();
                           break;
                        }
                     }
                  }
               }
            }

            if (!delete) {
               try (PreparedStatement ps = con.prepareStatement("INSERT INTO `shortcutcmd` (`chrid`, `index`, `data`) VALUES (?, ?, ?)")) {
                  ps.setInt(1, chr.getId());
                  ps.setInt(2, index);
                  Blob blob = new SerialBlob(buffer);
                  ps.setBlob(3, blob);
                  ps.execute();
               } catch (Exception var20) {
                  System.out.println("채팅 단축키 삽입 오류 발생");
                  var20.printStackTrace();
               }
            }
         } catch (Exception var25) {
            System.out.println("채팅 단축키 저장 오류 발생");
            var25.printStackTrace();
         }
      }
   }

   public static void loadChattingShortCut(MapleCharacter chr) {
      if (chr != null) {
         Map<Integer, byte[]> chatShortKeyMap = new HashMap<>();

         try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT `index`, `data` FROM `shortcutcmd` WHERE `chrid` = ?");
         ) {
            ps.setInt(1, chr.getId());

            try (ResultSet rs = ps.executeQuery()) {
               while (rs.next()) {
                  int index = rs.getInt("index");
                  Blob data = rs.getBlob("data");
                  chatShortKeyMap.put(index, data.getBytes(1L, (int)data.length()));
               }
            }
         } catch (Exception var13) {
            System.out.println("채팅 단축키 불러오기 오류 발생");
            var13.printStackTrace();
         }

         if (chatShortKeyMap.size() > 0) {
            chr.send(CWvsContext.loadChattingShortCut(chatShortKeyMap));
         }
      }
   }

   public static void setDodgeSkill(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int skillID = p.readInt();
         SecondaryStatEffect eff = chr.getSkillLevelData(skillID);
         if (eff != null && !chr.hasBuffBySkillID(skillID)) {
            eff.applyTo(chr);
         }
      }
   }

   public static void changeWeaponMotion(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int type = p.readByte();
         chr.updateOneInfo(27042, "use", String.valueOf(type));
         chr.getMap().broadcastMessage(CWvsContext.updateWeaponMotion(chr.getId(), type));
      }
   }

   public static void showMedal(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int type = p.readByte();
         chr.updateOneInfo(101149, "1007", String.valueOf(type));
         chr.getMap().broadcastMessage(CWvsContext.updateShowMedal(chr.getId(), type));
      }
   }

   public static void showItemEffect(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int type = p.readByte();
         chr.updateOneInfo(101149, "1009", String.valueOf(type));
         chr.getMap().broadcastMessage(CWvsContext.updateShowItemEffect(chr.getId(), type));
      }
   }

   public static void handleCashCodyPreset(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         p.readInt();
         short sel = p.readShort();
         short src = p.readShort();
         short dst = p.readShort();
         short qty = p.readShort();
         if (src == 0 && dst == 0 && qty == 0) {
            if (chr.getCurrentCashCodyPreset() == sel || sel < 0 || sel > 2) {
               return;
            }

            chr.setCurrentCashCodyPreset(sel);
         } else if (src == 0 && dst < 0 && qty == -1) {
            MapleInventoryManipulator.equipCashCodyPreset0(c, dst);
         } else {
            chr.dropMessage(5, "알수없는 액션입니다. " + src + " / " + dst + " / " + qty);
         }
      }
   }

   public static void memoRequest(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         int opcode = p.readByte();
         switch (opcode) {
            case 0:
               if (chr.getMeso() < 10000L) {
                  chr.dropMessage(1, "메소가 부족합니다.");
                  return;
               } else {
                  String targetName = p.readMapleAsciiString();
                  String context = p.readMapleAsciiString();
                  p.readByte();
                  if (targetName.equals(chr.getName())) {
                     chr.send(CSPacket.OnMemoResult((byte)9, (byte)5));
                     return;
                  } else {
                     int mySentBox = 0;

                     for (MapleMessage msg : chr.getSentMessages()) {
                        if (msg != null) {
                           mySentBox++;
                        }
                     }

                     if (mySentBox >= 30) {
                        chr.dropMessage(1, "보낸 메시지함이 가득찼습니다.");
                        return;
                     } else if (MapleCharacterUtil.canCreateChar(targetName, false, true)) {
                        chr.send(CSPacket.OnMemoResult((byte)9, (byte)1));
                        return;
                     } else {
                        int ch = Center.Find.findChannel(targetName);
                        int count = MapleCharacterUtil.memoCount(targetName);
                        if (count >= 30) {
                           chr.send(CSPacket.OnMemoResult((byte)9, (byte)2));
                           return;
                        } else {
                           int targetId = MapleCharacterUtil.getIdByName(targetName);
                           if (targetId == -1) {
                              chr.send(CSPacket.OnMemoResult((byte)9, (byte)1));
                              return;
                           } else {
                              Pair<MapleMessage, MapleMessage> memoResult = MapleCharacterUtil.sendNewMemo(
                                 chr.getId(), chr.getName(), targetId, targetName, context, 0, chr.getClient().isGm()
                              );
                              if (memoResult == null) {
                                 chr.send(CSPacket.OnMemoResult((byte)9, (byte)1));
                                 return;
                              } else {
                                 int index = 0;

                                 for (int i = 0; i < 30; i++) {
                                    if (chr.getSentMessages()[i] == null) {
                                       chr.getSentMessages()[i] = memoResult.right;
                                       index = i;
                                       break;
                                    }
                                 }

                                 chr.send(CSPacket.OnMemoResult((byte)8, (byte)0));
                                 chr.send(CSPacket.updateSentMessage(index, memoResult.right));
                                 chr.gainMeso(-10000L, true);
                                 if (ch >= 0) {
                                    MapleCharacter target = null;

                                    for (GameServer cs : GameServer.getAllInstances()) {
                                       target = cs.getPlayerStorage().getCharacterById(targetId);
                                       if (target != null) {
                                          break;
                                       }
                                    }

                                    if (target != null) {
                                       for (int ix = 0; ix < 30; ix++) {
                                          if (target.getReceivedMessages()[ix] == null) {
                                             target.getReceivedMessages()[ix] = memoResult.left;
                                             break;
                                          }
                                       }

                                       target.showNote();
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
         }
      }
   }

   public static void skillEffectRequest(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int skillId = p.readInt();
         byte skillLv = p.readByte();
         boolean bSendLocal = p.readByte() == 1;
         PacketEncoder p_ = new PacketEncoder();
         if (skillId == 32111016) {
            p_.writeInt(chr.getLevel());
            p_.write(skillLv);

            while (p.available() > 0L) {
               p_.write(p.readByte());
            }

            SpecialSkillEffect e = new SpecialSkillEffect(chr.getId(), skillId, p_);
            chr.send(e.encodeForLocal());
            if (!bSendLocal) {
               chr.getMap().broadcastMessage(chr, e.encodeForRemote(), false);
            }
         } else {
            while (p.available() > 0L) {
               p_.write(p.readByte());
            }

            SkillEffect e = new SkillEffect(chr.getId(), chr.getLevel(), skillId, skillLv, p_);
            if (skillId != 35000006) {
               chr.send(e.encodeForLocal());
            }

            if (!bSendLocal) {
               chr.getMap().broadcastMessage(chr, e.encodeForRemote(), false);
            }
         }
      }
   }

   public static void howlingGaleStackRequest(PacketDecoder packet, MapleClient client) {
      if (client.getPlayer().getSkillLevel(400031003) > 0) {
         int value = client.getPlayer().getBuffedValueDefault(SecondaryStatFlag.HowlingGale, 0);
         if (value < 3) {
            if (value == 2) {
               client.getPlayer().temporaryStatSet(400031003, Integer.MAX_VALUE, SecondaryStatFlag.HowlingGale, value + 1);
               client.getPlayer().setLastHowlingGaleUseTime(0L);
            } else {
               client.getPlayer().temporaryStatSet(400031003, 20000, SecondaryStatFlag.HowlingGale, value + 1);
               client.getPlayer().setLastHowlingGaleUseTime(System.currentTimeMillis());
            }
         }
      }
   }

   public static void removeDivineJudgementRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            int size = packet.readInt();

            for (int i = 0; i < size; i++) {
               MapleMonster mob = field.getMonsterByOid(packet.readInt());
               if (mob != null) {
                  mob.setDivineJudgement(0);
               }
            }
         }
      }
   }

   public static void curseEnchantRequest(PacketDecoder packet, MapleCharacter chr) {
      int skillid = packet.readInt();
   }

   public static void curseEnchantSelected(PacketDecoder packet, MapleCharacter chr) {
      byte pattern = packet.readByte();
      chr.temporaryStatSet(SecondaryStatFlag.CurseEnchant, 3321042, Integer.MAX_VALUE, pattern);
   }

   public static void phalanxRequest(PacketDecoder packet, MapleCharacter chr) {
      packet.skip(20);
      int skillID = packet.readInt();
      int linkedSkillID = GameConstants.getLinked5thSkill(skillID);
      SecondaryStatEffect effect = SkillFactory.getSkill(linkedSkillID).getEffect(chr.getSkillLevel(linkedSkillID));
      if (effect != null) {
         int cooldown = effect.getCooldown(chr);
         chr.send(CField.skillCooldown(linkedSkillID, cooldown));
         chr.addCooldown(linkedSkillID, System.currentTimeMillis(), cooldown);
         if (skillID == 400031067) {
            chr.send(CField.skillCooldown(400031036, cooldown));
            chr.addCooldown(400031036, System.currentTimeMillis(), cooldown);
         }
      }
   }

   public static void keyPressing(MapleClient c) {
      if (c.getPlayer() != null) {
         MapleCharacter chr = c.getPlayer();
         if (GameConstants.isBlaster(chr.getJob())) {
         }
      }
   }

   public static void showCubeLevelUpLimit(MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         chr.send(CField.showCubeLevelupLimit());
      }
   }

   public static void hexaMatrixOperation(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null && slea.available() > 4L) {
         int type = slea.readInt();
         switch (type) {
            case 0:
               int coreIdxx = slea.readInt();
               int coreType = coreIdxx / 10000000;
               int job = chr.getJob();
               if (coreType == 1 && !HexaMatrixConstants.sixthJobSkillCore.get(job).contains(coreIdxx)) {
                  chr.dropMessage(1, "해당 직업에서는 개방할 수 없는 스킬입니다.");
                  return;
               }

               if (coreType == 2 && !HexaMatrixConstants.sixthJobMasteryCore.get(job).contains(coreIdxx)) {
                  chr.dropMessage(1, "해당 직업에서는 개방할 수 없는 스킬입니다.");
                  return;
               }

               if (coreType == 3 && !HexaMatrixConstants.sixthJobEnforceCore.get(job).contains(coreIdxx)) {
                  chr.dropMessage(1, "해당 직업에서는 개방할 수 없는 스킬입니다.");
                  return;
               }

               if (coreType == 4 && !HexaMatrixConstants.sixthJobCommonCore.get(job).contains(coreIdxx)) {
                  chr.dropMessage(1, "해당 직업에서는 개방할 수 없는 스킬입니다.");
                  return;
               }

               int needErdax;
               if (coreType == 1) {
                  needErdax = HexaMatrixConstants.getNeedSolErdaToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.SKILL_CORE);
               } else if (coreType == 2) {
                  needErdax = HexaMatrixConstants.getNeedSolErdaToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.MASTERY_CORE);
               } else if (coreType == 3) {
                  needErdax = HexaMatrixConstants.getNeedSolErdaToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.ENFORCE_CORE);
               } else {
                  if (coreType != 4) {
                     chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.UnknownError.getType(), 0, 0));
                     return;
                  }

                  needErdax = HexaMatrixConstants.getNeedSolErdaToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.COMMON_CORE);
               }

               int needPiecex;
               if (coreType == 1) {
                  needPiecex = HexaMatrixConstants.getNeedSolErdaPieceToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.SKILL_CORE);
               } else if (coreType == 2) {
                  needPiecex = HexaMatrixConstants.getNeedSolErdaPieceToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.MASTERY_CORE);
               } else if (coreType == 3) {
                  needPiecex = HexaMatrixConstants.getNeedSolErdaPieceToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.ENFORCE_CORE);
               } else {
                  if (coreType != 4) {
                     chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.UnknownError.getType(), 0, 0));
                     return;
                  }

                  needPiecex = HexaMatrixConstants.getNeedSolErdaPieceToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.COMMON_CORE);
               }

               int haveErdax = chr.getSolErda();
               int havePiecexx = chr.getItemQuantity(4009547, false) + chr.getItemQuantity(4009548, false);
               if (needErdax > haveErdax) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.NotEnoughSolErda.getType(), 0, 0));
                  return;
               }

               if (needPiecex > havePiecexx) {
                  chr.dropMessage(1, "솔 에르다 조각이 부족합니다.");
                  return;
               }

               if (chr.getHexaCore() == null) {
                  HexaCore stat = new HexaCore(chr.getId());
                  chr.setHexaCore(stat);
               }

               chr.getHexaCore().setSkillCoreLevel(chr, coreIdxx, 1);
               chr.gainSolErda(-needErdax);
               int cant_trade = chr.getItemQuantity(4009548, false);
               if (cant_trade >= needPiecex) {
                  chr.removeItem(4009548, -needPiecex);
               } else if (cant_trade > 0) {
                  int can_trade = chr.getItemQuantity(4009547, false);
                  int can_need = needPiecex - cant_trade;
                  chr.removeItem(4009548, -cant_trade);
                  chr.removeItem(4009547, -can_need);
               } else {
                  int can_trade = chr.getItemQuantity(4009547, false);
                  if (can_trade < needPiecex) {
                     return;
                  }

                  chr.removeItem(4009547, -needPiecex);
               }

               chr.send(CWvsContext.hexaMatrixCoreLevelUp(chr.getHexaCore()));
               chr.send(CWvsContext.hexaMatrixReslut(type, 0, coreIdxx, 0));
               break;
            case 1:
               int coreIdx = slea.readInt();
               int checkLevel = slea.readInt();
               int coreLevel = chr.getHexaCore().getSkillCoreLevel(coreIdx);
               if (coreLevel != checkLevel) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.UserInfoError.getType(), 0, 0));
                  return;
               }

               int coreEnforcelevel = slea.readInt();
               if (coreLevel >= coreEnforcelevel) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.UserInfoError.getType(), 0, 0));
                  return;
               }

               int checkErda = slea.readInt();
               int checkPiece = slea.readInt();
               needErdax = 0;
               needPiecex = 0;
               coreType = coreIdx / 10000000;
               int i = coreLevel;

               for (; i < coreEnforcelevel; i++) {
                  if (coreType == 1) {
                     needErdax += HexaMatrixConstants.getNeedSolErdaToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.SKILL_CORE, i);
                  } else if (coreType == 2) {
                     needErdax += HexaMatrixConstants.getNeedSolErdaToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.MASTERY_CORE, i);
                  } else if (coreType == 3) {
                     needErdax += HexaMatrixConstants.getNeedSolErdaToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.ENFORCE_CORE, i);
                  } else {
                     if (coreType != 4) {
                        chr.dropMessage(1, "오류가 발생했습니다.");
                        return;
                     }

                     needErdax += HexaMatrixConstants.getNeedSolErdaToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.COMMON_CORE, i);
                  }

                  if (coreType == 1) {
                     needPiecex += HexaMatrixConstants.getNeedSolErdaPieceToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.SKILL_CORE, i);
                  } else if (coreType == 2) {
                     needPiecex += HexaMatrixConstants.getNeedSolErdaPieceToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.MASTERY_CORE, i);
                  } else if (coreType == 3) {
                     needPiecex += HexaMatrixConstants.getNeedSolErdaPieceToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.ENFORCE_CORE, i);
                  } else {
                     if (coreType != 4) {
                        chr.dropMessage(1, "오류가 발생했습니다.");
                        return;
                     }

                     needPiecex += HexaMatrixConstants.getNeedSolErdaPieceToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.COMMON_CORE, i);
                  }
               }

               if (needErdax != checkErda || needPiecex != checkPiece) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.UserInfoError.getType(), 0, 0));
                  return;
               }

               i = chr.getSolErda();
               int havePiecex = chr.getItemQuantity(4009547, false) + chr.getItemQuantity(4009548, false);
               if (needErdax > i) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.NotEnoughSolErda.getType(), 0, 0));
                  return;
               }

               if (needPiecex > havePiecex) {
                  chr.dropMessage(1, "솔 에르다 조각이 부족합니다.");
                  return;
               }

               chr.getHexaCore().setSkillCoreLevel(chr, coreIdx, coreEnforcelevel);
               chr.gainSolErda(-needErdax);
               cant_trade = chr.getItemQuantity(4009548, false);
               if (cant_trade >= needPiecex) {
                  chr.removeItem(4009548, -needPiecex);
               } else if (cant_trade > 0) {
                  int can_trade = chr.getItemQuantity(4009547, false);
                  int can_need = needPiecex - cant_trade;
                  chr.removeItem(4009548, -cant_trade);
                  chr.removeItem(4009547, -can_need);
               } else {
                  int can_trade = chr.getItemQuantity(4009547, false);
                  if (can_trade < needPiecex) {
                     return;
                  }

                  chr.removeItem(4009547, -needPiecex);
               }

               chr.send(CWvsContext.hexaMatrixCoreLevelUp(chr.getHexaCore()));
               chr.send(CWvsContext.hexaMatrixReslut(type, 0, coreIdx, checkLevel));
               break;
            case 2: {
               int needErda = HexaMatrixConstants.getNeedSolErdaToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.HEXA_STAT);
               int needPiece = HexaMatrixConstants.getNeedSolErdaPieceToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.HEXA_STAT);
               int haveErda = chr.getSolErda();
               int havePiece = chr.getItemQuantity(4009547, false) + chr.getItemQuantity(4009548, false);
               if (needErda > haveErda) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.NotEnoughSolErda.getType(), 0, 0));
                  return;
               }

               if (needPiece > havePiece) {
                  chr.dropMessage(1, "솔 에르다 조각이 부족합니다.");
                  return;
               }

               int coreId = slea.readInt();
               int stat0 = slea.readInt();
               int stat1 = slea.readInt();
               int stat2 = slea.readInt();
               if (chr.getHexaCore() == null) {
                  HexaCore stat = new HexaCore(chr.getId());
                  chr.setHexaCore(stat);
               }

               int index = HexaMatrixConstants.getHexaStatIndexByCoreId(coreId);
               HexaCore.HexaStatData data = chr.getHexaCore().getStat(index);
               data.resetAndAddHexaStat(stat0, stat1, stat2);
               Skill skill = SkillFactory.getSkill(500071000);
               byte level = 1;
               byte masterlevel = (byte)skill.getMaxLevel();
               if (level > skill.getMaxLevel()) {
                  level = (byte)skill.getMaxLevel();
               }

               c.getPlayer().changeSkillLevel(skill, level, masterlevel);
               c.getPlayer().send(CWvsContext.hexaMatrixStatUpdate(chr.getHexaCore()));
               c.getPlayer().send(CWvsContext.hexaMatrixReslut(type, 0, coreId, 0));
               break;
            }
            case 3: {
               int coreIdxxxx = slea.readInt();
               int index = HexaMatrixConstants.getHexaStatIndexByCoreId(coreIdxxxx);
               HexaCore.HexaStatData data = chr.getHexaCore().getStat(index);
               if (data == null || data.getStats().get(0) == null || data.getStats().get(1) == null || data.getStats().get(2) == null) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.UnknownError.getType(), 0, 0));
                  return;
               }

               if (data.getStats().get(0).level + data.getStats().get(1).level + data.getStats().get(2).level >= 20) {
                  chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.UnknownError.getType(), 0, 0));
                  return;
               }

               int havePiecexxx = chr.getItemQuantity(4009547, false) + chr.getItemQuantity(4009548, false);
               int needPiecexx = HexaMatrixConstants.getNeedSolErdaPieceToUpgradeMainHexaStat(data.getStats().get(0).level);
               if (havePiecexxx < needPiecexx) {
                  chr.dropMessage(1, "솔 에르다 조각이 부족합니다.");
                  return;
               }

               double weight0 = HexaMatrixConstants.getHexaStatWeight(data.getStats().get(0).level);
               double other = 1.0 - weight0;
               double per1 = other / 2.0;
               double per2 = other / 2.0;
               if (data.getStats().get(1).level == 10) {
                  per1 = 0.0;
               }

               if (data.getStats().get(2).level == 10) {
                  per1 = other;
                  per2 = 0.0;
               }

               double random = ThreadLocalRandom.current().nextDouble();
               int result;
               if (random < weight0 && data.getStats().get(0).level < 10) {
                  result = 0;
               } else if (random < weight0 + per1 && data.getStats().get(1).level < 10) {
                  result = 1;
               } else {
                  if (data.getStats().get(2).level >= 10) {
                     return;
                  }

                  result = 2;
               }

               data.getStats().get(result).level++;
               cant_trade = chr.getItemQuantity(4009548, false);
               if (cant_trade >= needPiecexx) {
                  chr.removeItem(4009548, -needPiecexx);
               } else if (cant_trade > 0) {
                  int can_trade = chr.getItemQuantity(4009547, false);
                  int can_need = needPiecexx - cant_trade;
                  chr.removeItem(4009548, -cant_trade);
                  chr.removeItem(4009547, -can_need);
               } else {
                  int can_trade = chr.getItemQuantity(4009547, false);
                  if (can_trade < needPiecexx) {
                     data.getStats().get(result).level--;
                     return;
                  }

                  chr.removeItem(4009547, -needPiecexx);
               }

               StringBuilder sb = new StringBuilder("헥사스텟 강화 (");
               sb.append("계정 : ");
               sb.append(c.getAccountName());
               sb.append(", 캐릭터 : ");
               sb.append(c.getPlayer().getName());
               sb.append("(");
               sb.append(result == 0 ? "메인스탯" : (result == 1 ? "에디셔널스탯1" : "에디셔널스탯2"));
               sb.append("+1) 결과 : (" + data.getStats().get(0).level + " / " + data.getStats().get(1).level + " / " + data.getStats().get(2).level + ")");
               LoggingManager.putLog(new EnchantLog(c.getPlayer(), 0, 0, 0L, EnchantLogType.HexaStat.getType(), 0, sb));
               data.setChanged(true);
               chr.send(CWvsContext.hexaMatrixStatUpdate(chr.getHexaCore()));
               chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.Reslut.getType(), 0, 0));
               break;
            }
            case 4: {
               HexaCore.HexaStatData data = chr.getHexaCore().getStat(0).clone();
               data.setIndex(-1);
               data.setChanged(true);
               chr.getHexaCore().setSavedStatData(data);
               chr.send(CWvsContext.hexaMatrixStatUpdate(chr.getHexaCore()));
               chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.Reslut.getType(), 0, 0));
               break;
            }
            case 5: {
               int coreid = slea.readInt();
               int preset = slea.readInt();
               int index = HexaMatrixConstants.getHexaStatIndexByCoreId(coreid);
               HexaCore.HexaStatData saved = chr.getHexaCore().getSavedStatData();
               saved.setIndex(index);
               saved.setChanged(true);
               HexaCore.HexaStatData temp = chr.getHexaCore().getStat(index);
               temp.setIndex(-1);
               temp.setChanged(true);
               chr.getHexaCore().setStat(index, saved);
               chr.getHexaCore().setSavedStatData(temp);
               chr.send(CWvsContext.hexaMatrixStatUpdate(chr.getHexaCore()));
               chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.Reslut.getType(), 0, 0));
               break;
            }
            case 6: {
               int coreid = slea.readInt();
               int index = HexaMatrixConstants.getHexaStatIndexByCoreId(coreid);
               HexaCore.HexaStatData stat = chr.getHexaCore().getStat(index);
               if (chr.getMeso() < 10000000L) {
                  chr.dropMessage(1, "메소가 부족합니다.");
                  return;
               }

               stat.getStats().get(0).level = 0;
               stat.getStats().get(1).level = 0;
               stat.getStats().get(2).level = 0;
               stat.setChanged(true);
               chr.gainMeso(-10000000L, true);
               chr.send(CWvsContext.hexaMatrixStatUpdate(chr.getHexaCore()));
               chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.Reslut.getType(), 0, 0));
               break;
            }
            case 7:
               long meso = slea.readLong();
               int changed = slea.readInt();
               if (chr.getMeso() < meso) {
                  chr.dropMessage(1, "메소가 부족합니다.");
                  return;
               }

               for (int ix = 0; ix < changed; ix++) {
                  int coreIdxxx = slea.readInt();
                  int indexx = HexaMatrixConstants.getHexaStatIndexByCoreId(coreIdxxx);
                  HexaCore.HexaStatData datax = chr.getHexaCore().getStat(indexx);
                  int type0 = slea.readInt();
                  HexaMatrixConstants.HexaStatOption opt0 = HexaMatrixConstants.HexaStatOption.findByValue(type0);
                  if (opt0 != null) {
                     datax.getStats().get(0).changeType(opt0);
                  }

                  int type1 = slea.readInt();
                  HexaMatrixConstants.HexaStatOption opt1 = HexaMatrixConstants.HexaStatOption.findByValue(type1);
                  if (opt1 != null) {
                     datax.getStats().get(1).changeType(opt1);
                  }

                  int type2 = slea.readInt();
                  HexaMatrixConstants.HexaStatOption opt2 = HexaMatrixConstants.HexaStatOption.findByValue(type2);
                  if (opt2 != null) {
                     datax.getStats().get(2).changeType(opt2);
                  }

                  datax.setChanged(true);
               }

               chr.gainMeso(-meso, true);
               chr.send(CWvsContext.hexaMatrixStatUpdate(chr.getHexaCore()));
               chr.send(CWvsContext.hexaMatrixReslut(type, HexaMatrixConstants.HexaMatrixMsg.Reslut.getType(), 0, 0));
               break;
            case 8:
               String secondPassword = slea.readMapleAsciiString();
               chr.send(
                  CWvsContext.hexaMatrixReslut(
                     type,
                     c.CheckSecondPassword(secondPassword)
                        ? HexaMatrixConstants.HexaMatrixMsg.Reslut.getType()
                        : HexaMatrixConstants.HexaMatrixMsg.SecondPassWordError.getType(),
                     0,
                     0
                  )
               );
         }
      }
   }

   public static void checkSixthFrozenCancel(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         if (chr.getBuffedEffect(SecondaryStatFlag.SixthSkillFrozen) != null) {
            chr.temporaryStatReset(SecondaryStatFlag.SixthSkillFrozen);
         }
      }
   }

   public static void uiOptionSaveRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int type = slea.readByteToInt();
         String qinfo = slea.readMapleAsciiString();
         String[] infos = qinfo.split("\\;");
         if (type == 0) {
            chr.updateInfoQuest(101563, "", false);

            for (String info : infos) {
               String[] keyvalue = info.split("\\=");
               chr.updateOneInfo(101563, keyvalue[0], keyvalue[1]);
            }
         } else {
            chr.updateInfoQuest(368, "", false);
            chr.updateInfoQuest(369, "", false);
            chr.updateInfoQuest(370, "", false);

            for (String info : infos) {
               String[] keyvalue = info.split("\\=");
               String[] keys368 = new String[]{
                  "wndHtK1",
                  "damEff1",
                  "mAnd1",
                  "mSE1",
                  "vME1",
                  "fLoc1",
                  "sNum1",
                  "mE1",
                  "vSE1",
                  "fType1",
                  "mBG1",
                  "mobInf1",
                  "mM1",
                  "sAuto1",
                  "mSV1",
                  "vE1",
                  "sCon1",
                  "magUI1",
                  "vSync1",
                  "qsEff1",
                  "mME1",
                  "vBG1",
                  "vM1",
                  "vSV1"
               };
               String[] keys369 = new String[]{
                  "cmbMsg2",
                  "chTime2",
                  "fcW2",
                  "mlUpTy2",
                  "chPos2",
                  "fcA2",
                  "petHP2",
                  "pBack2",
                  "simItm2",
                  "aOther2",
                  "qsTime2",
                  "fSize2",
                  "mEdge2",
                  "fcF2",
                  "fcG2",
                  "aMine2",
                  "avMega2",
                  "mlUpAc2",
                  "trem2",
                  "aUI2"
               };
               String[] keys370 = new String[]{
                  "silTy3",
                  "gqCB3",
                  "gqI3",
                  "silBo3",
                  "gqCE3",
                  "gqL3",
                  "gqP3",
                  "gqCL3",
                  "gqT3",
                  "silTh3",
                  "gqCP3",
                  "gqCT3",
                  "damAmt3",
                  "wrnHP3",
                  "gqB3",
                  "gqE3",
                  "wrnMP3"
               };

               for (String check : keys368) {
                  if (check.equals(keyvalue[0])) {
                     chr.updateOneInfo(368, keyvalue[0], keyvalue[1], false);
                  }
               }

               for (String checkx : keys369) {
                  if (checkx.equals(keyvalue[0])) {
                     chr.updateOneInfo(369, keyvalue[0], keyvalue[1], false);
                  }
               }

               for (String checkxx : keys370) {
                  if (checkxx.equals(keyvalue[0])) {
                     chr.updateOneInfo(370, keyvalue[0], keyvalue[1], false);
                  }
               }
            }
         }
      }
   }

   public static void extraSkillRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int skillID = slea.readInt();
         if (!DBConfig.isHosting && c.isGm()) {
            chr.dropMessage(5, "ExtraSkillRequest : " + skillID);
         }

         if (skillID == 4141502 && chr.hasBuffBySkillID(4141502)) {
            chr.sendRegisterExtraSkill(chr.getPosition(), false, skillID);
         }

         if (skillID == 63141503 && chr.hasBuffBySkillID(63141503)) {
            chr.invokeJobMethod("checkKainDeathBlessing");
         }

         if (skillID == 500061065 && chr.hasBuffBySkillID(500061065)) {
            chr.invokeJobMethod("skillUpdatePerTick", skillID, slea);
         }
      }
   }

   public static void summonSecondAtomRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr == null || chr.getMap() == null) {
         ;
      }
   }

   public static void cashBulletEffectOnOff(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int bullet = slea.readInt();
         chr.updateOneInfo(27038, "bullet", String.valueOf(bullet));
         chr.send(CWvsContext.cashBulletOnOffResult(true));
      }
   }

   public static void onHitFieldSkill(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         chr.dropMessageGM(5, "OnHitFieldSkill : " + slea.toString());
      }
   }
}
