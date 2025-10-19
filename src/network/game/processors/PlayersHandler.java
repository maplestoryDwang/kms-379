package network.game.processors;

import constants.GameConstants;
import constants.QuestExConstants;
import database.DBConfig;
import java.awt.Point;
import java.awt.Rectangle;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.Party;
import objects.fields.EliteMobEvent;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.RandomPortal;
import objects.fields.RandomPortalGameType;
import objects.fields.child.etc.Field_MMRace;
import objects.fields.child.minigame.soccer.Field_MultiSoccer;
import objects.fields.child.pollo.Field_TownDefense;
import objects.fields.events.MapleCoconut;
import objects.fields.events.MapleEventType;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.OpenGate;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.RuneStone;
import objects.fields.gameobject.RuneStoneArrow;
import objects.fields.gameobject.RuneStoneResultType;
import objects.fields.gameobject.RuneStoneStep;
import objects.fields.gameobject.RuneStoneType;
import objects.fields.gameobject.TownPortal;
import objects.fields.gameobject.lifes.Element;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MapleRing;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleCoolDownValueHolder;
import objects.users.MapleMessage;
import objects.users.MapleStat;
import objects.users.achievement.AchievementFactory;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import scripting.NPCScriptManager;
import scripting.ReactorScriptManager;
import security.anticheat.ReportType;

public class PlayersHandler {
   public static void Note(PacketDecoder slea, MapleCharacter chr) {
      byte type = slea.readByte();
      switch (type) {
         case 0:
            String name = slea.readMapleAsciiString();
            String msg = slea.readMapleAsciiString();
            boolean fame = slea.readByte() > 0;

            try {
               chr.sendNote(name, msg, fame ? 1 : 0);
            } catch (Exception var20) {
               System.out.println("쪽지 오류 발생");
               var20.printStackTrace();
            }
            break;
         case 1:
            short num = slea.readShort();
            if (num < 0) {
               num = 32767;
            }

            slea.skip(1);

            for (int i = 0; i < num; i++) {
               int id = slea.readInt();
               chr.deleteNote(id, slea.readByte() > 0 ? 1 : 0);
            }
            break;
         case 2:
            slea.readByte();
            slea.readByte();
            slea.readByte();
            slea.readByte();
            int uniqueid = slea.readInt();
            boolean ck = false;

            for (MapleMessage m : chr.getReceivedMessages()) {
               if (m != null && m.getUniqueID() == uniqueid) {
                  ck = true;
                  break;
               }
            }

            if (ck) {
               chr.deleteReceivedMessage(uniqueid);
            }
            break;
         case 3:
            int opcode = slea.readInt();
            if (opcode == 1) {
               uniqueid = slea.readInt();
               ck = false;

               for (MapleMessage mx : chr.getSentMessages()) {
                  if (mx != null && mx.getUniqueID() == uniqueid) {
                     ck = true;
                     break;
                  }
               }

               if (ck) {
                  chr.deleteSentMessage(uniqueid);
               }
            }
            break;
         case 4:
         default:
            System.out.println("Unhandled note action, " + type);
            if (type < 0) {
               try {
                  chr.getClient().getSession().close();
                  System.out.println("팅겼다고인마");
                  return;
               } catch (Exception var18) {
                  return;
               } finally {
                  return;
               }
            }
            break;
         case 5:
            int opcodex = slea.readByte();
            if (opcodex == 2) {
               uniqueid = slea.readInt();
               ck = false;

               for (MapleMessage mxx : chr.getReceivedMessages()) {
                  if (mxx != null && mxx.getUniqueID() == uniqueid) {
                     ck = true;
                     break;
                  }
               }

               if (ck) {
                  chr.checkedMessage(uniqueid);
               }
            }
      }
   }

   public static void GiveFame(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      int who = slea.readInt();
      int mode = slea.readByte();
      int famechange = mode == 0 ? -1 : 1;
      MapleCharacter target = chr.getMap().getCharacterById(who);
      if (target != null && target != chr) {
         if (chr.getLevel() < 15) {
            c.getSession().writeAndFlush(CWvsContext.giveFameErrorResponse(2));
         } else {
            switch (chr.canGiveFame(target)) {
               case OK:
                  if (Math.abs(target.getFame() + famechange) <= 99999) {
                     target.addFame(famechange);
                     target.updateSingleStat(MapleStat.FAME, target.getFame());
                  }

                  if (!chr.isGM()) {
                     chr.hasGivenFame(target);
                  }

                  if (target.getChair() == 3014005 && famechange == 1) {
                     MapleItemInformationProvider.getInstance().getItemEffect(2023355).applyTo(chr);
                  }

                  if (target.getChair() == 3014028) {
                     if (!DBConfig.isGanglim && target.getQuestStatus(2000017) == 1 && target.getOneInfoQuestInteger(2000017, "clear") < 1) {
                        target.updateOneInfo(2000017, "clear", "1");
                     }

                     if (famechange == 1) {
                        MapleItemInformationProvider.getInstance().getItemEffect(2023819).applyTo(chr);
                     }
                  }

                  c.getSession().writeAndFlush(CWvsContext.OnFameResult(0, target.getName(), famechange == 1, target.getFame()));
                  target.getClient().getSession().writeAndFlush(CWvsContext.OnFameResult(5, chr.getName(), famechange == 1, 0));
                  break;
               case NOT_TODAY:
                  c.getSession().writeAndFlush(CWvsContext.giveFameErrorResponse(3));
                  break;
               case NOT_THIS_MONTH:
                  c.getSession().writeAndFlush(CWvsContext.giveFameErrorResponse(4));
            }
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.giveFameErrorResponse(1));
      }
   }

   public static void UseDoor(PacketDecoder slea, MapleCharacter chr) {
      int oid = slea.readInt();
      boolean mode = slea.readByte() == 0;

      for (MapleMapObject obj : chr.getMap().getAllDoorsThreadsafe()) {
         TownPortal door = (TownPortal)obj;
         if (door.getOwnerId() == oid) {
            door.warp(chr, mode);
            break;
         }
      }
   }

   public static void UseMechDoor(PacketDecoder slea, MapleCharacter chr) {
      int oid = slea.readInt();
      Point pos = slea.readPos();
      int mode = slea.readByte();
      chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));

      for (MapleMapObject obj : chr.getMap().getAllMechDoorsThreadsafe()) {
         OpenGate door = (OpenGate)obj;
         if (door.getOwnerId() == oid && door.getId() == mode) {
            chr.checkFollow();
            chr.getMap().movePlayer(chr, pos);
            break;
         }
      }
   }

   public static void UseRandomPortal(PacketDecoder slea, MapleCharacter chr) {
      int objectID = slea.readInt();
      int mode = slea.readByte();
      Field map = chr.getMap();
      if (map != null) {
         RandomPortal randomPortal = chr.getRandomPortal();
         if (randomPortal == null) {
            chr.send(CWvsContext.enableActions(chr));
         } else if (randomPortal.getObjectID() != objectID) {
            chr.send(CWvsContext.enableActions(chr));
         } else {
            Party party = chr.getParty();
            if (party != null) {
               chr.dropMessage(5, "파티를 해제하고 다시 시도해 주세요.");
               chr.send(CWvsContext.enableActions(chr));
            } else {
               chr.getClient().removeClickedNPC();
               NPCScriptManager.getInstance().dispose(chr.getClient());
               chr.send(CWvsContext.enableActions(chr));
               if (randomPortal.getGameType().getType() < RandomPortalGameType.StormwingArea.getType()) {
                  NPCScriptManager.getInstance().start(chr.getClient(), 9001060);
               } else if (randomPortal.getGameType().getType() >= RandomPortalGameType.StormwingArea.getType()
                  && randomPortal.getGameType().getType() < RandomPortalGameType.FireWolf.getType()) {
                  NPCScriptManager.getInstance().start(chr.getClient(), 9001059);
               } else {
                  NPCScriptManager.getInstance().start(chr.getClient(), 9001059);
               }
            }
         }
      }
   }

   public static void HitReactor(PacketDecoder slea, MapleClient c) {
      int oid = slea.readInt();
      int charPos = slea.readInt();
      short stance = slea.readShort();
      Reactor reactor = c.getPlayer().getMap().getReactorByOid(oid);
      if (reactor != null && reactor.isAlive()) {
         reactor.hitReactor(charPos, stance, c);
         if (c.getPlayer().getMapId() == 109090300) {
            int rand = Randomizer.rand(1, 10);
            int itemid = 0;
            if (rand <= 2) {
               itemid = 2022163;
            } else if (rand > 2 && rand <= 4) {
               itemid = 2022165;
            } else if (rand > 4 && rand <= 6) {
               itemid = 2022166;
            }

            c.getPlayer().getMap().destroyReactor(oid);
            Item idrop = new Item(itemid, (short)0, (short)1, 0);
            c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), idrop, reactor.getPosition(), true, true);
         }
      }
   }

   public static void TouchReactor(PacketDecoder slea, MapleClient c) {
      int oid = slea.readInt();
      boolean touched = false;
      Reactor reactor = c.getPlayer().getMap().getReactorByOid(oid);
      if (reactor != null) {
         if (slea.available() >= 4L) {
            if (reactor.getReactorId() == 9239000) {
               int mobObjectID = slea.readInt();
               MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(mobObjectID);
               if (mob != null) {
                  if (c.getPlayer().getMap() instanceof Field_TownDefense) {
                     Field_TownDefense f = (Field_TownDefense)c.getPlayer().getMap();
                     f.decLife();
                  }

                  if (c.getPlayer().getMap() instanceof Field_MMRace) {
                     Field_MMRace f = (Field_MMRace)c.getPlayer().getMap();
                     f.setVictoryMob(mob);
                  }

                  c.getPlayer().getMap().removeMonster(mob);
               }

               return;
            }
         } else {
            touched = slea.available() == 0L || slea.readByte() > 0;
         }

         if (touched && reactor != null && reactor.isAlive() && reactor.getTouch() != 0) {
            if (reactor.getTouch() == 2) {
               ReactorScriptManager.getInstance().act(c, reactor);
            } else if (reactor.getTouch() == 1 && !reactor.isTimerActive()) {
               if (reactor.getReactorType() == 100) {
                  int itemid = GameConstants.getCustomReactItem(reactor.getReactorId(), reactor.getReactItem().getLeft());
                  if (c.getPlayer().haveItem(itemid, reactor.getReactItem().getRight())) {
                     if (reactor.getArea().contains(c.getPlayer().getTruePosition())) {
                        MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, reactor.getReactItem().getRight(), true, false);
                        reactor.hitReactor(c);
                     } else {
                        c.getPlayer().dropMessage(5, "너무 멀리 있습니다.");
                     }
                  } else {
                     c.getPlayer().dropMessage(5, "You don't have the item required.");
                  }
               } else {
                  reactor.hitReactor(c);
               }
            }
         }
      }
   }

   public static void hitCoconut(PacketDecoder slea, MapleClient c) {
      int id = slea.readShort();
      String co = "coconut";
      MapleCoconut map = (MapleCoconut)c.getChannelServer().getEvent(MapleEventType.Coconut);
      if (map == null || !map.isRunning()) {
         map = (MapleCoconut)c.getChannelServer().getEvent(MapleEventType.CokePlay);
         co = "coke cap";
         if (map == null || !map.isRunning()) {
            return;
         }
      }

      MapleCoconut.MapleCoconuts nut = map.getCoconut(id);
      if (nut != null && nut.isHittable()) {
         if (System.currentTimeMillis() >= nut.getHitTime()) {
            if (nut.getHits() > 2 && Math.random() < 0.4 && !nut.isStopped()) {
               nut.setHittable(false);
               if (Math.random() < 0.01 && map.getStopped() > 0) {
                  nut.setStopped(true);
                  map.stopCoconut();
                  c.getPlayer().getMap().broadcastMessage(CField.hitCoconut(false, id, 1));
                  return;
               }

               nut.resetHits();
               if (Math.random() < 0.05 && map.getBombings() > 0) {
                  c.getPlayer().getMap().broadcastMessage(CField.hitCoconut(false, id, 2));
                  map.bombCoconut();
               } else if (map.getFalling() > 0) {
                  c.getPlayer().getMap().broadcastMessage(CField.hitCoconut(false, id, 3));
                  map.fallCoconut();
                  if (c.getPlayer().getTeam() == 0) {
                     map.addMapleScore();
                  } else {
                     map.addStoryScore();
                  }

                  c.getPlayer().getMap().broadcastMessage(CField.coconutScore(map.getCoconutScore()));
               }
            } else {
               nut.hit();
               c.getPlayer().getMap().broadcastMessage(CField.hitCoconut(false, id, 1));
            }
         }
      }
   }

   public static void FollowRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter tt = c.getPlayer().getMap().getCharacterById(slea.readInt());
      if (slea.readByte() <= 0) {
         if (slea.readByte() > 0) {
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getFollowId() == c.getPlayer().getId() && c.getPlayer().isFollowOn()) {
               c.getPlayer().checkFollow();
            }
         } else {
            tt.setFollowId(c.getPlayer().getId());
            tt.setFollowOn(false);
            tt.setFollowInitiator(false);
            c.getPlayer().setFollowOn(false);
            c.getPlayer().setFollowInitiator(false);
            tt.getClient().getSession().writeAndFlush(CWvsContext.followRequest(c.getPlayer().getId()));
         }
      } else {
         tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
         if (tt != null && tt.getFollowId() == c.getPlayer().getId()) {
            tt.setFollowOn(true);
            c.getPlayer().setFollowOn(true);
         } else {
            c.getPlayer().checkFollow();
         }
      }
   }

   public static void FollowReply(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getFollowId() > 0 && c.getPlayer().getFollowId() == slea.readInt()) {
         MapleCharacter tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
         if (tt != null && tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000.0 && tt.getFollowId() == 0 && tt.getId() != c.getPlayer().getId()) {
            boolean accepted = slea.readByte() > 0;
            if (accepted) {
               tt.setFollowId(c.getPlayer().getId());
               tt.setFollowOn(true);
               tt.setFollowInitiator(false);
               c.getPlayer().setFollowOn(true);
               c.getPlayer().setFollowInitiator(true);
               c.getPlayer().getMap().broadcastMessage(CField.followEffect(tt.getId(), c.getPlayer().getId(), null));
            } else {
               c.getPlayer().setFollowId(0);
               tt.setFollowId(0);
               tt.getClient().getSession().writeAndFlush(CField.getFollowMsg(5));
            }
         } else {
            if (tt != null) {
               tt.setFollowId(0);
               c.getPlayer().setFollowId(0);
            }

            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "너무 멀리 있습니다."));
         }
      } else {
         c.getPlayer().setFollowId(0);
      }
   }

   public static void DoRing(MapleClient c, String name, int itemid) {
      int newItemId = itemid == 2240000
         ? 1112803
         : (itemid == 2240001 ? 1112806 : (itemid == 2240002 ? 1112807 : (itemid == 2240003 ? 1112809 : 1112300 + (itemid - 2240004))));
      MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
      int errcode = 0;
      if (c.getPlayer().getMarriageId() > 0) {
         errcode = 23;
      } else if (chr == null) {
         errcode = 18;
      } else if (chr.getMapId() != c.getPlayer().getMapId()) {
         errcode = 19;
      } else if (!c.getPlayer().haveItem(itemid, 1) || itemid < 2240000 || itemid > 2240015) {
         errcode = 13;
      } else if (chr.getMarriageId() > 0 || chr.getMarriageItemId() > 0) {
         errcode = 24;
      } else if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "")) {
         errcode = 20;
      } else if (!MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId, 1, "")) {
         errcode = 21;
      }

      if (errcode > 0) {
         c.getSession().writeAndFlush(CWvsContext.sendEngagement((byte)errcode, 0, null, null));
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         c.getPlayer().setMarriageItemId(itemid);
         chr.getClient().getSession().writeAndFlush(CWvsContext.sendEngagementRequest(c.getPlayer().getName(), c.getPlayer().getId()));
      }
   }

   public static void RingAction(PacketDecoder slea, MapleClient c) {
      byte mode = slea.readByte();
      if (mode == 0) {
         DoRing(c, slea.readMapleAsciiString(), slea.readInt());
      } else if (mode == 1) {
         c.getPlayer().setMarriageItemId(0);
      } else if (mode == 2) {
         boolean accepted = slea.readByte() > 0;
         String name = slea.readMapleAsciiString();
         int id = slea.readInt();
         MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
         if (c.getPlayer().getMarriageId() > 0
            || chr == null
            || chr.getId() != id
            || chr.getMarriageItemId() <= 0
            || !chr.haveItem(chr.getMarriageItemId(), 1)
            || chr.getMarriageId() > 0
            || !chr.isAlive()
            || chr.getEventInstance() != null
            || !c.getPlayer().isAlive()
            || c.getPlayer().getEventInstance() != null) {
            c.getSession().writeAndFlush(CWvsContext.sendEngagement((byte)29, 0, null, null));
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (accepted) {
            int itemid = chr.getMarriageItemId();
            int newItemId = itemid == 2240000
               ? 1112803
               : (itemid == 2240001 ? 1112806 : (itemid == 2240002 ? 1112807 : (itemid == 2240003 ? 1112809 : 1112300 + (itemid - 2240004))));
            if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "") || !MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId, 1, "")) {
               c.getSession().writeAndFlush(CWvsContext.sendEngagement((byte)21, 0, null, null));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }

            try {
               long[] ringID = MapleRing.makeRing(newItemId, c.getPlayer(), chr);
               Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(newItemId, ringID[1]);
               MapleRing ring = MapleRing.loadFromDb(ringID[1]);
               if (ring != null) {
                  eq.setRing(ring);
               }

               MapleInventoryManipulator.addbyItem(c, eq);
               eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(newItemId, ringID[0]);
               ring = MapleRing.loadFromDb(ringID[0]);
               if (ring != null) {
                  eq.setRing(ring);
               }

               MapleInventoryManipulator.addbyItem(chr.getClient(), eq);
               MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, chr.getMarriageItemId(), 1, false, false);
               chr.getClient().getSession().writeAndFlush(CWvsContext.sendEngagement((byte)16, newItemId, chr, c.getPlayer()));
               chr.setMarriageId(c.getPlayer().getId());
               c.getPlayer().setMarriageId(chr.getId());
               chr.fakeRelog();
               c.getPlayer().fakeRelog();
            } catch (Exception var12) {
               FileoutputUtil.outputFileError("Log_Packet_Except.rtf", var12);
            }
         } else {
            chr.getClient().getSession().writeAndFlush(CWvsContext.sendEngagement((byte)30, 0, null, null));
         }

         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         chr.setMarriageItemId(0);
      } else if (mode == 3) {
         int itemId = slea.readInt();
         MapleInventoryType type = GameConstants.getInventoryType(itemId);
         Item item = c.getPlayer().getInventory(type).findById(itemId);
         if (item != null && type == MapleInventoryType.ETC && itemId / 10000 == 421) {
            MapleInventoryManipulator.drop(c, type, item.getPosition(), item.getQuantity());
         }
      }
   }

   public static void Report(PacketDecoder slea, MapleClient c) {
      MapleCharacter other = c.getPlayer().getMap().getCharacterById(slea.readInt());
      ReportType type = ReportType.getById(slea.readByte());
      if (other != null && type != null && !other.isIntern()) {
         MapleQuestStatus stat = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(123457));
         if (stat.getCustomData() == null) {
            stat.setCustomData("0");
         }

         long currentTime = System.currentTimeMillis();
         long theTime = Long.parseLong(stat.getCustomData());
         if (theTime + 7200000L > currentTime && !c.getPlayer().isIntern()) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            c.getPlayer().dropMessage(5, "You may only report every 2 hours.");
         } else {
            stat.setCustomData(String.valueOf(currentTime));
            other.addReport(type);
            c.getSession().writeAndFlush(CWvsContext.report(2));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.report(4));
      }
   }

   public static final void stealSkill(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null && GameConstants.isPhantom(chr.getJob())) {
         int skill = slea.readInt();
         int cid = slea.readInt();
         byte action = slea.readByte();
         if (action == 0) {
            MapleCharacter other = chr.getMap().getCharacterById(cid);
            if (other != null && other.getId() != chr.getId() && other.getTotalSkillLevel(skill) > 0) {
               chr.addStolenSkill(skill, other.getTotalSkillLevel(skill));
            } else {
               chr.dropMessage(1, "상대방이 해당 스킬을 보유하고 있지 않습니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
            }
         } else if (action == 1) {
            chr.invokeJobMethod("removeStolenSkill", skill);
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public static final void ChooseSkill(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null && GameConstants.isPhantom(chr.getJob())) {
         int base = slea.readInt();
         int skill = slea.readInt();
         if (skill <= 0) {
            chr.invokeJobMethod("unchooseStolenSkill", base);
         } else {
            chr.invokeJobMethod("chooseStolenSkill", skill, base);
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public static final void viewSkills(PacketDecoder slea, MapleClient c) {
      int victim = slea.readInt();
      int jobid = c.getChannelServer().getPlayerStorage().getCharacterById(victim).getJob();
      if (!c.getChannelServer().getPlayerStorage().getCharacterById(victim).getSkills().isEmpty() && GameConstants.isAdventurer(jobid)) {
         c.getSession().writeAndFlush(CField.viewSkills(c.getChannelServer().getPlayerStorage().getCharacterById(victim)));
      } else {
         c.getPlayer().dropMessage(6, "훔칠 수 있는 스킬이 없습니다.");
      }
   }

   public static boolean inArea(MapleCharacter chr) {
      for (Rectangle rect : chr.getMap().getAreas()) {
         if (rect.contains(chr.getTruePosition())) {
            return true;
         }
      }

      for (AffectedArea mist : chr.getMap().getAllMistsThreadsafe()) {
         if (mist.getOwnerId() == chr.getId() && mist.getElement() == Element.Fire && mist.getBox().contains(chr.getTruePosition())) {
            return true;
         }
      }

      return false;
   }

   public static final void RuneStoneUseReq(PacketDecoder slea, MapleCharacter chr) {
      slea.skip(4);
      int type = slea.readInt();
      RuneStone rune = chr.getMap().getAllRune().get(0);
      RuneStoneResultType result = rune != null ? rune.check(chr) : RuneStoneResultType.Failed;
      if (rune != null && rune.getRuneType().getType() == type && result == RuneStoneResultType.Success) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.RUNE_STONE_USE_ACK.getValue());
         packet.writeInt(RuneStoneResultType.Success.getType());
         packet.write(0);
         packet.write(1);
         packet.write(1);
         rune.encodeArrows(packet);
         chr.send(packet.getPacket());
         chr.setTouchedRune(type);
         chr.setLastRuneStoneUseTime(System.currentTimeMillis());
         rune.setLastRuneTouchTime(System.currentTimeMillis());
      } else {
         chr.send(CWvsContext.enableActions(chr));
      }
   }

   public static final void RuneStoneStep(PacketDecoder slea, MapleCharacter player) {
      int pos = slea.readInt();
      int arrow = slea.readInt();
      if (player.getMap().getAllRune().isEmpty()) {
         player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
      } else {
         RuneStone rune = player.getMap().getAllRune().get(0);
         if (rune != null) {
            RuneStoneStep step = rune.step(player, pos, RuneStoneArrow.get(arrow));
            if (step != RuneStoneStep.Clear || player.getLastRuneStoneUseTime() == 0L) {
               player.temporaryStatSet(80002282, 5000, SecondaryStatFlag.RuneBlocked, 1);
            }
         }

         player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
      }
   }

   public static final void RuneStoneSkillReq(PacketDecoder slea, MapleCharacter chr) {
      byte result = slea.readByte();
      if (chr.getMap().getAllRune().isEmpty()) {
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
      } else {
         RuneStone rune = chr.getMap().getAllRune().get(0);
         if (chr.getLastRuneStoneUseTime() == 0L) {
            chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
         } else {
            if (result == 1) {
               slea.skip(9);

               for (int i = 0; i < 4; i++) {
                  int arrow = slea.readInt();
                  RuneStoneStep step = rune.step(chr, i, RuneStoneArrow.get(arrow));
                  if (step != RuneStoneStep.Clear || chr.getLastRuneStoneUseTime() == 0L) {
                     chr.temporaryStatSet(80002282, 5000, SecondaryStatFlag.RuneBlocked, 1);
                     return;
                  }
               }

               if (!DBConfig.isGanglim) {
                  int questID = QuestExConstants.JinQuestExAccount.getQuestID();
                  String questKey = "DailyRuneUse";
                  chr.updateOneInfo(questID, questKey, String.valueOf(chr.getOneInfoQuestInteger(questID, questKey) + 1));
               }

               chr.updateOneInfo(QuestExConstants.CustomQuests.getQuestID(), "RuneChannel", String.valueOf(chr.getClient().getChannel()));
               chr.updateOneInfo(QuestExConstants.CustomQuests.getQuestID(), "RuneMapID", String.valueOf(chr.getMapId()));
               chr.checkHiddenMissionComplete(QuestExConstants.SuddenMKRuneAct.getQuestID());
               chr.checkHasteQuestComplete(QuestExConstants.HasteEventRuneAct.getQuestID());
               chr.getMap().broadcastMessage(CField.removeRune(rune, chr));
               chr.getMap().removeMapObject(rune);
               chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
               SecondaryStatEffect effect = SkillFactory.getSkill(80002280).getEffect(1);
               effect.applyTo(chr, true);
               switch (RuneStoneType.get(chr.getTouchedRune())) {
                  case DefenceUp:
                     effect = SkillFactory.getSkill(80001428).getEffect(1);
                     effect.applyTo(chr);
                     break;
                  case DotAttack:
                     effect = SkillFactory.getSkill(80001432).getEffect(1);
                     effect.applyTo(chr);
                     break;
                  case ThunderAttack:
                     effect = SkillFactory.getSkill(80001762).getEffect(1);
                     effect.applyTo(chr);
                     break;
                  case EarthQuake:
                     effect = SkillFactory.getSkill(80001757).getEffect(1);
                     effect.applyTo(chr);
                     break;
                  case EliteMob:
                     if (chr.getMap().getFieldEvent() == null) {
                        EliteMobEvent event = new EliteMobEvent(chr.getMap(), System.currentTimeMillis() + 600000L, chr.getPosition(), 3);
                        chr.getMap().setFieldEvent(event);
                     }
                     break;
                  case Mimic:
                     MapleMonster mob = MapleLifeFactory.getMonster(8220028);
                     chr.getMap().spawnMonsterOnGroundBelow(mob, chr.getTruePosition());
                     break;
                  case ReduceCooltime:
                     effect = SkillFactory.getSkill(80001875).getEffect(1);
                     effect.applyTo(chr);

                     for (MapleCoolDownValueHolder ix : chr.getCooldowns()) {
                        Skill skill = SkillFactory.getSkill(ix.skillId);
                        if (skill != null
                           && GameConstants.isResettableCooltimeSkill(ix.skillId)
                           && ix.length - chr.getCooldownLimit(ix.skillId) >= effect.getFixCoolTime()) {
                           chr.rawSetCooldown(ix.skillId, 5000L);
                        }
                     }
                     break;
                  case Increase:
                     chr.getSecondaryStat().RuneofPurificationGuage = 0;
                     effect = SkillFactory.getSkill(80002888).getEffect(1);
                     effect.applyTo(chr);
                     break;
                  case Razer:
                     effect = SkillFactory.getSkill(80002889).getEffect(1);
                     effect.applyTo(chr);
                     break;
                  case Ignition:
                     effect = SkillFactory.getSkill(80002890).getEffect(1);
                     effect.applyTo(chr);
               }

               AchievementFactory.checkRuneStoneUseResultSuccess(chr, chr.getTouchedRune());
               effect = SkillFactory.getSkill(80002282).getEffect(1);
               effect.applyTo(chr, true);
               if (chr.isQuestStarted(QuestExConstants.NeoEventRuneAct.getQuestID())) {
                  MapleQuestStatus status = chr.getQuest(MapleQuest.getInstance(QuestExConstants.NeoEventRuneAct.getQuestID()));
                  if (status != null) {
                     String v = "000";
                     int count = chr.getOneInfoQuestInteger(QuestExConstants.NeoEventRuneAct.getQuestID(), "RunAct");
                     if (count < 7) {
                        v = StringUtil.getLeftPaddedStr(String.valueOf(++count), '0', 3);
                        status.setCustomData(v);
                        chr.updateQuest(status);
                        chr.updateOneInfo(QuestExConstants.NeoEventRuneAct.getQuestID(), "RunAct", String.valueOf(count));
                        if (count >= 7) {
                           chr.updateOneInfo(QuestExConstants.NeoEventAdventureLog.getQuestID(), "state", "2");
                        }
                     }
                  }
               }

               if (chr.isQuestStarted(100571)) {
                  MapleQuestStatus status = chr.getQuest(MapleQuest.getInstance(100571));
                  if (status != null) {
                     String v = "000";
                     int count = chr.getOneInfoQuestInteger(100571, "RunAct");
                     if (count < 2) {
                        v = StringUtil.getLeftPaddedStr(String.valueOf(++count), '0', 3);
                        status.setCustomData(v);
                        chr.updateQuest(status);
                        chr.updateOneInfo(100571, "RunAct", String.valueOf(count));
                        if (count >= 2) {
                           chr.updateOneInfo(100565, "questNum", "100570");
                        }
                     }
                  }
               }

               if (chr.isQuestStarted(501528)) {
                  if (chr.getOneInfoQuestInteger(501528, "value") < 1) {
                     chr.updateOneInfo(501528, "value", "1");
                  }

                  if (chr.getOneInfoQuestInteger(501524, "state") < 2) {
                     chr.updateOneInfo(501524, "state", "2");
                  }
               }

               chr.addCooldown(80002282, System.currentTimeMillis(), 900000L);
               chr.checkSpecialCoreSkills("rune", 0, effect);
               chr.getMap().setEliteBossCurseLevel(0);
               chr.setLastRuneStoneUseTime(0L);
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.RUNE_STONE_SKILL_ACK.getValue());
               packet.writeInt(chr.getTouchedRune());
               packet.write(0);
               chr.send(packet.getPacket());
            }
         }
      }
   }

   public static void onMultiSoccerTryDoingShoot(PacketDecoder o, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null && chr.getMap() instanceof Field_MultiSoccer) {
         int srcX = o.readInt();
         int srcY = o.readInt();
         int plus = o.readInt();
         boolean bFacingLeft = o.readByte() == 1;
         boolean bPutTop = o.readByte() == 1;
         boolean bPutBottom = o.readByte() == 1;
         boolean bPutLeft = o.readByte() == 1;
         boolean bPutRight = o.readByte() == 1;
         int destY = srcY;
         int var13;
         if (!bPutLeft && !bFacingLeft) {
            var13 = srcX + plus / 2;
            var13 = Math.min(980, var13);
         } else {
            var13 = srcX - plus / 2;
            var13 = Math.max(-980, var13);
         }

         if (bPutTop) {
            destY = srcY - plus / 2;
            destY = Math.max(-680, destY);
         } else if (bPutBottom) {
            destY = srcY + plus / 2;
            destY = Math.min(680, destY);
         }

         ((Field_MultiSoccer)chr.getMap()).shootBall(chr, var13, destY);
      }
   }

   public static void userRideSet(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null && chr.getBuffedValue(SecondaryStatFlag.RideVehicle) != null) {
         byte[] data = slea.read((int)slea.available());
         chr.getMap().broadcastMessage(chr, CField.onSetRideVehicleUser(chr, data), false);
      }
   }

   public static void setPartyAuraBuff(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null && chr.getParty() != null && chr.getPartyMembers().size() > 1) {
         int size = slea.readInt();

         for (int i = 0; i < size; i++) {
            int type = slea.readInt();
            int skillID = slea.readInt();
            int value = slea.readInt();
            int playerID = slea.readInt();
            int chrPosX = slea.readInt();
            int chrPosY = slea.readInt();
            MapleCharacter owner = chr.getMap().getCharacterById(playerID);
            if (type == 1) {
               if (owner != null && owner.getTotalSkillLevel(skillID) > 0) {
                  SecondaryStatEffect effect = owner.getSkillLevelData(skillID);
                  Rectangle rect = effect.calculateBoundingBox(new Point(chrPosX, chrPosY), owner.isFacingLeft());
                  boolean isUsableSkill = false;
                  switch (skillID) {
                     case 1211014:
                     case 2221054:
                     case 2221055:
                     case 32001016:
                     case 32101009:
                     case 32111012:
                     case 32121017:
                     case 63121044:
                     case 100001263:
                     case 100001264:
                     case 400021006:
                        isUsableSkill = true;
                  }

                  if (isUsableSkill && rect.contains(chr.getTruePosition()) && !chr.hasBuffBySkillID(skillID)) {
                     effect.applyBuffEffect(owner, chr, false, Integer.MAX_VALUE, false, chr.isFacingLeft() ? 0 : 1);
                  }
               }
            } else if (type == 2) {
               if (chr.hasBuffBySkillID(skillID)) {
                  chr.temporaryStatResetBySkillID(skillID);
               }
            } else if (type == 3 && owner != null && owner.getTotalSkillLevel(32120062) > 0) {
               SecondaryStatEffect e = owner.getSkillLevelData(32120062);
               SecondaryStat secondaryStat = chr.getSecondaryStat();
               if (chr.checkInterval(chr.lastBlueAuraDispelTime, e.getDuration()) && secondaryStat.getVarriableInt("BlueAuraDispelCount") > 0) {
                  chr.dispelDebuff(1);
                  chr.lastBlueAuraDispelTime = System.currentTimeMillis();
                  SecondaryStatManager statManager = new SecondaryStatManager(c, secondaryStat);
                  statManager.changeStatValue(SecondaryStatFlag.BlueAura, 32111012, e.getLevel());
                  secondaryStat.setVarriableInt("BlueAuraFromID", owner.getId());
                  secondaryStat.setVarriableInt("BlueAuraDispelCount", 0);
                  statManager.temporaryStatSet();
               }
            }
         }
      }
   }
}
