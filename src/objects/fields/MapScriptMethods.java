package objects.fields;

import constants.ServerConstants;
import database.DBConfig;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.context.party.Party;
import objects.effect.child.WZEffect;
import objects.effect.child.WZEffect3;
import objects.fields.child.blackmage.Field_BlackMage;
import objects.fields.child.demian.Field_Demian;
import objects.fields.child.jinhillah.Field_JinHillah;
import objects.fields.child.karing.KaringMatching.KaringMatching;
import objects.fields.child.karrotte.Field_BossKalosPhase2;
import objects.fields.child.lucid.Field_LucidBattlePhase1;
import objects.fields.child.lucid.Field_LucidBattlePhase2;
import objects.fields.child.moonbridge.Field_FerociousBattlefield;
import objects.fields.child.rimen.Field_RimenNearTheEnd;
import objects.fields.child.sernium.Field_SerenPhase1;
import objects.fields.child.sernium.Field_SerenPhase2;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.child.will.SpiderWeb;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.instance.HardBlackHeavenBoss;
import objects.fields.fieldset.instance.HardDemianBoss;
import objects.fields.fieldset.instance.HardLucidBoss;
import objects.fields.fieldset.instance.HardWillBoss;
import objects.fields.fieldset.instance.HellBlackHeavenBoss;
import objects.fields.fieldset.instance.HellDemianBoss;
import objects.fields.fieldset.instance.HellDunkelBoss;
import objects.fields.fieldset.instance.HellLucidBoss;
import objects.fields.fieldset.instance.HellWillBoss;
import objects.fields.fieldset.instance.KalosBoss;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.OverrideMonsterStats;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.quest.QuestEx;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.Timer;
import scripting.EventManager;
import scripting.NPCConversationManager;
import scripting.NPCScriptManager;
import scripting.newscripting.ScriptManager;

public class MapScriptMethods {
   private static final Point witchTowerPos = new Point(-60, 184);
   private static final String[] mulungEffects = new String[]{
      "무릉도장에 도전한 것을 후회하게 해주겠다! 어서 들어와봐!", "기다리고 있었다! 용기가 남았다면 들어와 보시지!", "배짱 하나는 두둑하군! 현명함과 무모함을 혼동하지말라고!", "무릉도장에 도전하다니 용기가 가상하군!", "패배의 길을 걷고싶다면 들어오라고!"
   };

   public static void startScript_FirstUser(final MapleClient c, String scriptName) {
      if (c.getPlayer() != null) {
         switch (MapScriptMethods.onFirstUserEnter.fromString(scriptName)) {
            case first_SlimeChaos1:
               MapleMonster mobxxxxx = MapleLifeFactory.getMonster(8880700);
               if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                  long hp = mobxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxx.getStats());
                  cs.hp = hp * 3L;
                  if (cs.hp < 0L) {
                     cs.hp = Long.MAX_VALUE;
                  }

                  mobxxxxx.getStats().setMaxHp(cs.hp);
                  mobxxxxx.getStats().setHp(cs.hp);
                  mobxxxxx.setMaxHp(cs.hp);
                  mobxxxxx.setHp(cs.hp);
                  mobxxxxx.setOverrideStats(cs);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(700, -1378));
               break;
            case first_SlimeChaos2:
               if (c.getPlayer().getCurrentBossPhase() == 0) {
                  return;
               }

               mobxxxxx = MapleLifeFactory.getMonster(8880725);
               if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                  long hp = mobxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxx.getStats());
                  cs.hp = hp * 3L;
                  if (cs.hp < 0L) {
                     cs.hp = Long.MAX_VALUE;
                  }

                  mobxxxxx.getStats().setMaxHp(cs.hp);
                  mobxxxxx.getStats().setHp(cs.hp);
                  mobxxxxx.setMaxHp(cs.hp);
                  mobxxxxx.setHp(cs.hp);
                  mobxxxxx.setOverrideStats(cs);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(-179, 315));
               break;
            case first_SlimeNormal1:
               mobxxxxx = MapleLifeFactory.getMonster(8880711);
               if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                  long hp = mobxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxx.getStats());
                  cs.hp = hp * 3L;
                  if (cs.hp < 0L) {
                     cs.hp = Long.MAX_VALUE;
                  }

                  mobxxxxx.getStats().setMaxHp(cs.hp);
                  mobxxxxx.getStats().setHp(cs.hp);
                  mobxxxxx.setMaxHp(cs.hp);
                  mobxxxxx.setHp(cs.hp);
                  mobxxxxx.setOverrideStats(cs);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(700, -1378));
               break;
            case first_SlimeNormal2:
               if (c.getPlayer().getCurrentBossPhase() == 0) {
                  return;
               }

               mobxxxxx = MapleLifeFactory.getMonster(8880726);
               if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                  long hp = mobxxxxx.getStats().getMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  mobxxxxx.setHp(fixedhp);
                  mobxxxxx.setMaxHp(fixedhp);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(-179, 315));
               break;
            case first_SerenNormal1:
               if (c.getPlayer().getCurrentBossPhase() == 0) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_SerenPhase1) {
                  Field_SerenPhase1 f = (Field_SerenPhase1)c.getPlayer().getMap();
                  if (f != null) {
                     mobxxxxx = MapleLifeFactory.getMonster(8880630);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxx.setHp(fixedhp);
                        mobxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(150, 267));
                     mobxxxxx = MapleLifeFactory.getMonster(8880631);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(150, 267));
                  }
               }
               break;
            case first_SerenNormal2:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_SerenPhase2) {
                  Field_SerenPhase2 f = (Field_SerenPhase2)c.getPlayer().getMap();
                  if (f != null) {
                     mobxxxxx = MapleLifeFactory.getMonster(8880637);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxx.setHp(fixedhp);
                        mobxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 297));
                     mobxxxxx = MapleLifeFactory.getMonster(8880638);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 297));
                     mobxxxxx = MapleLifeFactory.getMonster(8880632);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 297));
                  }
               }
               break;
            case first_SerenNormal3:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               mobxxxxx = MapleLifeFactory.getMonster(8880644);
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 275));
               break;
            case first_SerenHard1:
               if (c.getPlayer().getCurrentBossPhase() == 0) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_SerenPhase1) {
                  Field_SerenPhase1 f = (Field_SerenPhase1)c.getPlayer().getMap();
                  if (f != null) {
                     mobxxxxx = MapleLifeFactory.getMonster(8880600);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxx.setHp(fixedhp);
                        mobxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(150, 267));
                     mobxxxxx = MapleLifeFactory.getMonster(8880601);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(150, 267));
                  }
               }
               break;
            case first_SerenHard2:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_SerenPhase2) {
                  Field_SerenPhase2 f = (Field_SerenPhase2)c.getPlayer().getMap();
                  if (f != null) {
                     mobxxxxx = MapleLifeFactory.getMonster(8880607);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxx.setHp(fixedhp);
                        mobxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 297));
                     mobxxxxx = MapleLifeFactory.getMonster(8880608);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 297));
                     mobxxxxx = MapleLifeFactory.getMonster(8880602);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 297));
                  }
               }
               break;
            case first_SerenHard3:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               mobxxxxx = MapleLifeFactory.getMonster(8880614);
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 275));
               break;
            case dusk_onFirstUserEnter:
               if (c.getPlayer().getCurrentBossPhase() == 0) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_FerociousBattlefield) {
                  Field_FerociousBattlefield f = (Field_FerociousBattlefield)c.getPlayer().getMap();
                  if (f != null) {
                     if (f.getId() == 450009450) {
                        mobxxxxx = MapleLifeFactory.getMonster(8644659);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(-80, -157));
                        mobxxxxx = MapleLifeFactory.getMonster(8644655);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(-45, -157));
                     } else {
                        mobxxxxx = MapleLifeFactory.getMonster(8644658);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(-80, -157));
                        mobxxxxx = MapleLifeFactory.getMonster(8644650);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(-45, -157));
                     }
                  }
               }
               break;
            case dunkel_boss:
               if (c.getPlayer().getCurrentBossPhase() == 0) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_RimenNearTheEnd) {
                  Field_RimenNearTheEnd f = (Field_RimenNearTheEnd)c.getPlayer().getMap();
                  if (f != null) {
                     if (f.getId() == 450012600) {
                        MapleMonster mobxxxx = MapleLifeFactory.getMonster(8645068);
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mobxxxx.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mobxxxx.setHp(fixedhp);
                           mobxxxx.setMaxHp(fixedhp);
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxx, new Point(0, 29));
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mobxxxx.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mobxxxx.setHp(fixedhp);
                           mobxxxx.setMaxHp(fixedhp);
                        }

                        mobxxxx = MapleLifeFactory.getMonster(8645066);
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mobxxxx.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mobxxxx.setHp(fixedhp);
                           mobxxxx.setMaxHp(fixedhp);
                        }

                        if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof HellDunkelBoss) {
                           long hp = mobxxxx.getStats().getMaxHp();
                           ChangeableStats cs = new ChangeableStats(mobxxxx.getStats());
                           if (DBConfig.isGanglim) {
                              cs.hp = hp * 10L;
                              mobxxxx.getStats().setMaxHp(hp * 10L);
                              mobxxxx.getStats().setHp(hp * 10L);
                              mobxxxx.setMaxHp(hp * 10L);
                              mobxxxx.setHp(hp * 10L);
                              mobxxxx.setOverrideStats(cs);
                           } else {
                              cs.hp = (long)(hp * 2.5 * 23.0);
                              mobxxxx.getStats().setMaxHp((long)(hp * 2.5 * 23.0));
                              mobxxxx.getStats().setHp((long)(hp * 2.5 * 23.0));
                              mobxxxx.setMaxHp((long)(hp * 2.5 * 23.0));
                              mobxxxx.setHp((long)(hp * 2.5 * 23.0));
                              mobxxxx.setOverrideStats(cs);
                           }
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxx, new Point(0, 29));
                     } else {
                        mobxxxxx = MapleLifeFactory.getMonster(8645067);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 29));
                        mobxxxxx = MapleLifeFactory.getMonster(8645009);
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mobxxxxx.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mobxxxxx.setHp(fixedhp);
                           mobxxxxx.setMaxHp(fixedhp);
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(0, 29));
                     }
                  }
               }
               break;
            case first_DemianNormal1:
               if (c.getPlayer().getCurrentBossPhase() < 1) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_Demian) {
                  Field_Demian map = (Field_Demian)c.getPlayer().getMap();
                  if (map.getMobsSize() == 0 && !map.isClear()) {
                     MapleMonster mobxxxx = MapleLifeFactory.getMonster(8880110);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxx.setHp(fixedhp);
                        mobxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxx, new Point(895, 16));
                  }
               }
               break;
            case first_DemianNormal2:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_Demian) {
                  Field_Demian map = (Field_Demian)c.getPlayer().getMap();
                  if (map.getMobsSize() == 0 && !map.isClear()) {
                     MapleMonster mobxxx = MapleLifeFactory.getMonster(8880111);
                     mobxxx.setHp((long)(mobxxx.getMobMaxHp() * 0.3));
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxx.setHp(fixedhp);
                        mobxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxx, new Point(895, 17));
                     mobxxx = MapleLifeFactory.getMonster(8880102);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxx.setHp(fixedhp);
                        mobxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxx, new Point(895, 17));
                  }
               }
               break;
            case first_DemianHard1:
               if (c.getPlayer().getCurrentBossPhase() < 1) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_Demian) {
                  Field_Demian map = (Field_Demian)c.getPlayer().getMap();
                  if (map.getMobsSize() == 0 && !map.isClear()) {
                     MapleMonster mobxx = MapleLifeFactory.getMonster(8880100);
                     if (!DBConfig.isGanglim
                        && c.getPlayer().getMap().getFieldSetInstance() != null
                        && c.getPlayer().getMap().getFieldSetInstance() instanceof HardDemianBoss
                        && c.getPlayer().isMultiMode()) {
                        long hp = mobxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxx.setHp(fixedhp);
                        mobxx.setMaxHp(fixedhp);
                     }

                     if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof HellDemianBoss) {
                        long hp = mobxx.getStats().getMaxHp();
                        ChangeableStats cs = new ChangeableStats(mobxx.getStats());
                        if (DBConfig.isGanglim) {
                           cs.hp = hp * 15L;
                           mobxx.getStats().setMaxHp(hp * 15L);
                           mobxx.getStats().setHp(hp * 15L);
                           mobxx.setMaxHp(hp * 15L);
                           mobxx.setHp(hp * 15L);
                           mobxx.setOverrideStats(cs);
                        } else {
                           cs.hp = (long)(hp * 2.5 * 23.0);
                           mobxx.getStats().setMaxHp((long)(hp * 2.5 * 23.0));
                           mobxx.getStats().setHp((long)(hp * 2.5 * 23.0));
                           mobxx.setMaxHp((long)(hp * 2.5 * 23.0));
                           mobxx.setHp((long)(hp * 2.5 * 23.0));
                           mobxx.setOverrideStats(cs);
                        }
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxx, new Point(895, 16));
                  }
               }
               break;
            case first_DemianHard2:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_Demian) {
                  Field_Demian map = (Field_Demian)c.getPlayer().getMap();
                  if (map.getMobsSize() == 0 && !map.isClear()) {
                     mobxxxxx = MapleLifeFactory.getMonster(8880101);
                     if (!DBConfig.isGanglim
                        && c.getPlayer().getMap().getFieldSetInstance() != null
                        && c.getPlayer().getMap().getFieldSetInstance() instanceof HardDemianBoss
                        && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxx.setHp(fixedhp);
                        mobxxxxx.setMaxHp(fixedhp);
                     }

                     if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof HellDemianBoss) {
                        long hp = mobxxxxx.getStats().getMaxHp();
                        ChangeableStats cs = new ChangeableStats(mobxxxxx.getStats());
                        if (DBConfig.isGanglim) {
                           cs.hp = hp * 15L;
                           mobxxxxx.getStats().setMaxHp(hp * 15L);
                           mobxxxxx.setMaxHp(hp * 15L);
                           mobxxxxx.setOverrideStats(cs);
                           mobxxxxx.setHp(162000000000000L);
                        } else {
                           cs.hp = (long)(hp * 2.5 * 23.0);
                           mobxxxxx.getStats().setMaxHp((long)(hp * 2.5 * 23.0));
                           mobxxxxx.setMaxHp((long)(hp * 2.5 * 23.0));
                           mobxxxxx.setOverrideStats(cs);
                           mobxxxxx.setHp(621000000000000L);
                        }
                     } else {
                        mobxxxxx.setHp((long)(mobxxxxx.getMobMaxHp() * 0.3));
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(895, 17));
                     mobxxxxx = MapleLifeFactory.getMonster(8880102);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(895, 17));
                     if (((Field_Demian)c.getPlayer().getMap()).isHellmode()) {
                        mobxxxxx = MapleLifeFactory.getMonster(8880102);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(895, 17));
                        mobxxxxx = MapleLifeFactory.getMonster(8880102);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxx, new Point(895, 17));
                     }
                  }
               }
               break;
            case Fenter_450004150:
               if (c.getPlayer().getCurrentBossPhase() < 1) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_LucidBattlePhase1) {
                  Field_LucidBattlePhase1 map = (Field_LucidBattlePhase1)c.getPlayer().getMap();
                  if (map.getMobsSize() == 0 && !map.isClear()) {
                     if (map.getId() / 100 == 4500047) {
                        MapleMonster mobx = MapleLifeFactory.getMonster(8880190);
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mobx.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mobx.setHp(fixedhp);
                           mobx.setMaxHp(fixedhp);
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(1000, 43));
                        mobx = MapleLifeFactory.getMonster(8880196);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(1000, 43));
                     } else if (map.getId() / 100 == 4500041) {
                        MapleMonster mobx = MapleLifeFactory.getMonster(8880140);
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mobx.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mobx.setHp(fixedhp);
                           mobx.setMaxHp(fixedhp);
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(1000, 43));
                        mobx = MapleLifeFactory.getMonster(8880166);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(1000, 43));
                     } else if (map.getId() / 100 == 4500044) {
                        MapleMonster mobx = MapleLifeFactory.getMonster(8880141);
                        if (!DBConfig.isGanglim
                           && c.getPlayer().getMap().getFieldSetInstance() != null
                           && c.getPlayer().getMap().getFieldSetInstance() instanceof HardLucidBoss
                           && c.getPlayer().isMultiMode()) {
                           long hp = mobx.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mobx.setHp(fixedhp);
                           mobx.setMaxHp(fixedhp);
                        }

                        if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof HellLucidBoss) {
                           long hp = mobx.getStats().getMaxHp();
                           ChangeableStats cs = new ChangeableStats(mobx.getStats());
                           if (DBConfig.isGanglim) {
                              cs.hp = hp * 20L;
                              mobx.getStats().setMaxHp(hp * 20L);
                              mobx.getStats().setHp(hp * 20L);
                              mobx.setMaxHp(hp * 20L);
                              mobx.setHp(hp * 20L);
                              mobx.setOverrideStats(cs);
                           } else {
                              cs.hp = hp * 3L * 23L;
                              mobx.getStats().setMaxHp(hp * 3L * 23L);
                              mobx.getStats().setHp(hp * 3L * 23L);
                              mobx.setMaxHp(hp * 3L * 23L);
                              mobx.setHp(hp * 3L * 23L);
                              mobx.setOverrideStats(cs);
                           }
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(1000, 43));
                        mobx = MapleLifeFactory.getMonster(8880166);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(1000, 43));
                     }

                     MapleMonster mobxx = MapleLifeFactory.getMonster(8880165);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxx, new Point(700, 43));
                     mobxx = MapleLifeFactory.getMonster(8880168);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxx, new Point(700, 43));
                     mobxx = MapleLifeFactory.getMonster(8880169);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxx, new Point(900, 43));
                  }
               }
               break;
            case Fenter_450004250:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               if (c.getPlayer().getMap() instanceof Field_LucidBattlePhase2) {
                  if (c.getPlayer().getMap().getFieldSetInstance() != null) {
                     c.getPlayer().getMap().getFieldSetInstance().userEnter(c.getPlayer());
                  }

                  Field_LucidBattlePhase2 map = (Field_LucidBattlePhase2)c.getPlayer().getMap();
                  if (map.getMobsSize() == 0 && !map.isClear()) {
                     if (map.getId() / 100 == 4500048) {
                        MapleMonster mob = MapleLifeFactory.getMonster(8880191);
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mob.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mob.setHp(fixedhp);
                           mob.setMaxHp(fixedhp);
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(711, -490));
                     } else if (map.getId() / 100 == 4500042) {
                        MapleMonster mob = MapleLifeFactory.getMonster(8880150);
                        if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                           long hp = mob.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mob.setHp(fixedhp);
                           mob.setMaxHp(fixedhp);
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(711, -490));
                     } else if (map.getId() / 100 == 4500045) {
                        MapleMonster mob = MapleLifeFactory.getMonster(8880151);
                        if (!DBConfig.isGanglim
                           && c.getPlayer().getMap().getFieldSetInstance() != null
                           && c.getPlayer().getMap().getFieldSetInstance() instanceof HardLucidBoss
                           && c.getPlayer().isMultiMode()) {
                           long hp = mob.getStats().getMaxHp();
                           long fixedhp = hp * 3L;
                           if (fixedhp < 0L) {
                              fixedhp = Long.MAX_VALUE;
                           }

                           mob.setHp(fixedhp);
                           mob.setMaxHp(fixedhp);
                        }

                        if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof HellLucidBoss) {
                           long hp = mob.getStats().getMaxHp();
                           ChangeableStats cs = new ChangeableStats(mob.getStats());
                           if (DBConfig.isGanglim) {
                              cs.hp = hp * 20L;
                              mob.getStats().setMaxHp(hp * 20L);
                              mob.getStats().setHp(hp * 20L);
                              mob.setMaxHp(hp * 20L);
                              mob.setHp(hp * 20L);
                              mob.setOverrideStats(cs);
                           } else {
                              cs.hp = hp * 2L * 23L;
                              mob.getStats().setMaxHp(hp * 2L * 23L);
                              mob.getStats().setHp(hp * 2L * 23L);
                              mob.setMaxHp(hp * 2L * 23L);
                              mob.setHp(hp * 2L * 23L);
                              mob.setOverrideStats(cs);
                           }
                        }

                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(711, -490));
                     }

                     MapleMonster mobx = MapleLifeFactory.getMonster(8880175);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(900, -331));
                     mobx = MapleLifeFactory.getMonster(8880178);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(900, -331));
                     mobx = MapleLifeFactory.getMonster(8880179);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobx, new Point(900, -331));
                  }
               }
               break;
            case Fenter_450004300:
               int fieldID = c.getPlayer().getMapId() / 100;
               if (fieldID == 4500043) {
                  MapleMonster mob = MapleLifeFactory.getMonster(8880167);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(85, 30));
               }

               if (fieldID == 4500046) {
                  MapleMonster mob = MapleLifeFactory.getMonster(8880177);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(85, 30));
               }
               break;
            case blackHeavenBoss1_summon:
               int fieldIDxxx = c.getPlayer().getMapId();
               MapleMonster mobxxxxxxxxxxxxx = MapleLifeFactory.getMonster(8950000);
               if (!DBConfig.isGanglim
                  && c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HardBlackHeavenBoss
                  && c.getPlayer().isMultiMode()) {
                  long hp = mobxxxxxxxxxxxxx.getStats().getMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  mobxxxxxxxxxxxxx.setHp(fixedhp);
                  mobxxxxxxxxxxxxx.setMaxHp(fixedhp);
               }

               if (c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HellBlackHeavenBoss
                  && !DBConfig.isGanglim) {
                  long hp = mobxxxxxxxxxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxxxxxxxxxx.getStats());
                  cs.hp = hp * 30L * 23L;
                  mobxxxxxxxxxxxxx.getStats().setMaxHp(hp * 30L * 23L);
                  mobxxxxxxxxxxxxx.getStats().setHp(hp * 30L * 23L);
                  mobxxxxxxxxxxxxx.setMaxHp(hp * 30L * 23L);
                  mobxxxxxxxxxxxxx.setHp(hp * 30L * 23L);
                  mobxxxxxxxxxxxxx.setOverrideStats(cs);
               } else if (c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HellBlackHeavenBoss
                  && DBConfig.isGanglim) {
                  long hp = mobxxxxxxxxxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxxxxxxxxxx.getStats());
                  cs.hp = hp * 10L;
                  mobxxxxxxxxxxxxx.getStats().setMaxHp(hp * 10L);
                  mobxxxxxxxxxxxxx.getStats().setHp(hp * 10L);
                  mobxxxxxxxxxxxxx.setMaxHp(hp * 10L);
                  mobxxxxxxxxxxxxx.setHp(hp * 10L);
                  mobxxxxxxxxxxxxx.setOverrideStats(cs);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxxxxx, new Point(-2, -18));
               break;
            case blackHeavenBoss1n_summon:
               fieldID = c.getPlayer().getMapId();
               MapleMonster mob = MapleLifeFactory.getMonster(8950100);
               if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                  long hp = mob.getStats().getMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  mob.setHp(fixedhp);
                  mob.setMaxHp(fixedhp);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-2, -18));
               break;
            case blackHeavenBoss2_summon:
               int fieldIDxx = c.getPlayer().getMapId();
               MapleMonster mobxxxxxxxxxxxx = MapleLifeFactory.getMonster(8950001);
               if (!DBConfig.isGanglim
                  && c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HardBlackHeavenBoss
                  && c.getPlayer().isMultiMode()) {
                  long hp = mobxxxxxxxxxxxx.getStats().getMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  mobxxxxxxxxxxxx.setHp(fixedhp);
                  mobxxxxxxxxxxxx.setMaxHp(fixedhp);
               }

               if (c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HellBlackHeavenBoss
                  && !DBConfig.isGanglim) {
                  long hp = mobxxxxxxxxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxxxxxxxxx.getStats());
                  cs.hp = hp * 6L * 23L;
                  mobxxxxxxxxxxxx.getStats().setMaxHp(hp * 6L * 23L);
                  mobxxxxxxxxxxxx.getStats().setHp(hp * 6L * 23L);
                  mobxxxxxxxxxxxx.setMaxHp(hp * 6L * 23L);
                  mobxxxxxxxxxxxx.setHp(hp * 6L * 23L);
                  mobxxxxxxxxxxxx.setOverrideStats(cs);
               } else if (c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HellBlackHeavenBoss
                  && DBConfig.isGanglim) {
                  long hp = mobxxxxxxxxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxxxxxxxxx.getStats());
                  cs.hp = hp * 10L;
                  mobxxxxxxxxxxxx.getStats().setMaxHp(hp * 10L);
                  mobxxxxxxxxxxxx.getStats().setHp(hp * 10L);
                  mobxxxxxxxxxxxx.setMaxHp(hp * 10L);
                  mobxxxxxxxxxxxx.setHp(hp * 10L);
                  mobxxxxxxxxxxxx.setOverrideStats(cs);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxxxx, new Point(515, -18));
               break;
            case blackHeavenBoss2n_summon:
               fieldID = c.getPlayer().getMapId();
               mob = MapleLifeFactory.getMonster(8950101);
               if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                  long hp = mob.getStats().getMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  mob.setHp(fixedhp);
                  mob.setMaxHp(fixedhp);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(515, -18));
               break;
            case blackHeavenBoss3_summon:
               int fieldIDx = c.getPlayer().getMapId();
               MapleMonster mobxxxxxxxxxxx = MapleLifeFactory.getMonster(8950002);
               if (!DBConfig.isGanglim
                  && c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HardBlackHeavenBoss
                  && c.getPlayer().isMultiMode()) {
                  long hp = mobxxxxxxxxxxx.getStats().getMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  mobxxxxxxxxxxx.setHp(fixedhp);
                  mobxxxxxxxxxxx.setMaxHp(fixedhp);
               }

               if (c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HellBlackHeavenBoss
                  && !DBConfig.isGanglim) {
                  long hp = mobxxxxxxxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxxxxxxxx.getStats());
                  cs.hp = hp * 6L * 23L;
                  mobxxxxxxxxxxx.getStats().setMaxHp(hp * 6L * 23L);
                  mobxxxxxxxxxxx.getStats().setHp(hp * 6L * 23L);
                  mobxxxxxxxxxxx.setMaxHp(hp * 6L * 23L);
                  mobxxxxxxxxxxx.setHp(hp * 6L * 23L);
                  mobxxxxxxxxxxx.setOverrideStats(cs);
               } else if (c.getPlayer().getMap().getFieldSetInstance() != null
                  && c.getPlayer().getMap().getFieldSetInstance() instanceof HellBlackHeavenBoss
                  && DBConfig.isGanglim) {
                  long hp = mobxxxxxxxxxxx.getStats().getMaxHp();
                  ChangeableStats cs = new ChangeableStats(mobxxxxxxxxxxx.getStats());
                  cs.hp = hp * 10L;
                  mobxxxxxxxxxxx.getStats().setMaxHp(hp * 10L);
                  mobxxxxxxxxxxx.getStats().setHp(hp * 10L);
                  mobxxxxxxxxxxx.setMaxHp(hp * 10L);
                  mobxxxxxxxxxxx.setHp(hp * 10L);
                  mobxxxxxxxxxxx.setOverrideStats(cs);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxxx, new Point(515, -16));
               break;
            case blackHeavenBoss3n_summon:
               fieldID = c.getPlayer().getMapId();
               mob = MapleLifeFactory.getMonster(8950102);
               if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                  long hp = mob.getStats().getMaxHp();
                  long fixedhp = hp * 3L;
                  if (fixedhp < 0L) {
                     fixedhp = Long.MAX_VALUE;
                  }

                  mob.setHp(fixedhp);
                  mob.setMaxHp(fixedhp);
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(515, -16));
               break;
            case will_phase1:
               if (c.getPlayer().getMap() instanceof Field_WillBattle) {
                  final Field_WillBattle f = (Field_WillBattle)c.getPlayer().getMap();
                  if (c.getPlayer().getCurrentBossPhase() < 1) {
                     return;
                  }

                  if (c.getPlayer().getMapId() == 450008750) {
                     MapleMonster mobxxxxxxxx = MapleLifeFactory.getMonster(8880343);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(352, 159));
                     mobxxxxxxxx = MapleLifeFactory.getMonster(8880344);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxx = MapleLifeFactory.getMonster(8880340);
                     mobxxxxxxxx.setStance(4);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxx = MapleLifeFactory.getMonster(8880351);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(352, 159));
                     mobxxxxxxxx = MapleLifeFactory.getMonster(8880352);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxx = MapleLifeFactory.getMonster(8880355);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(252, 159));
                     mobxxxxxxxx = MapleLifeFactory.getMonster(8880356);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(252, -2020));
                  } else if (c.getPlayer().getMapId() == 450008150) {
                     MapleMonster mobxxxxxxxxx = MapleLifeFactory.getMonster(8880303);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxxx.setMaxHp(fixedhp);
                     }

                     if (c.getPlayer().getMap().getFieldSetInstance() != null
                        && c.getPlayer().getMap().getFieldSetInstance() instanceof HellWillBoss
                        && DBConfig.isGanglim) {
                        long hp = mobxxxxxxxxx.getStats().getMaxHp();
                        ChangeableStats cs = new ChangeableStats(mobxxxxxxxxx.getStats());
                        cs.hp = hp * 20L;
                        mobxxxxxxxxx.getStats().setMaxHp(hp * 20L);
                        mobxxxxxxxxx.getStats().setHp(hp * 20L);
                        mobxxxxxxxxx.setMaxHp(hp * 20L);
                        mobxxxxxxxxx.setHp(hp * 20L);
                        mobxxxxxxxxx.setOverrideStats(cs);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxx, new Point(352, 159));
                     mobxxxxxxxxx = MapleLifeFactory.getMonster(8880304);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxxx.setMaxHp(fixedhp);
                     }

                     if (c.getPlayer().getMap().getFieldSetInstance() != null
                        && c.getPlayer().getMap().getFieldSetInstance() instanceof HellWillBoss
                        && DBConfig.isGanglim) {
                        long hp = mobxxxxxxxxx.getStats().getMaxHp();
                        ChangeableStats cs = new ChangeableStats(mobxxxxxxxxx.getStats());
                        cs.hp = hp * 20L;
                        mobxxxxxxxxx.getStats().setMaxHp(hp * 20L);
                        mobxxxxxxxxx.getStats().setHp(hp * 20L);
                        mobxxxxxxxxx.setMaxHp(hp * 20L);
                        mobxxxxxxxxx.setHp(hp * 20L);
                        mobxxxxxxxxx.setOverrideStats(cs);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxxx = MapleLifeFactory.getMonster(8880300);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxxx.getStats().setMaxHp(fixedhp);
                        mobxxxxxxxxx.getStats().setHp(fixedhp);
                        mobxxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxxx.setMaxHp(fixedhp);
                     }

                     if (c.getPlayer().getMap().getFieldSetInstance() != null
                        && c.getPlayer().getMap().getFieldSetInstance() instanceof HellWillBoss
                        && DBConfig.isGanglim) {
                        long hp = mobxxxxxxxxx.getStats().getMaxHp();
                        ChangeableStats cs = new ChangeableStats(mobxxxxxxxxx.getStats());
                        cs.hp = hp * 20L;
                        mobxxxxxxxxx.getStats().setMaxHp(hp * 20L);
                        mobxxxxxxxxx.getStats().setHp(hp * 20L);
                        mobxxxxxxxxx.setMaxHp(hp * 20L);
                        mobxxxxxxxxx.setHp(hp * 20L);
                        mobxxxxxxxxx.setOverrideStats(cs);
                     }

                     mobxxxxxxxxx.setStance(4);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxxx = MapleLifeFactory.getMonster(8880321);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxx, new Point(352, 159));
                     mobxxxxxxxxx = MapleLifeFactory.getMonster(8880322);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxxx = MapleLifeFactory.getMonster(8880325);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxx, new Point(252, 159));
                     mobxxxxxxxxx = MapleLifeFactory.getMonster(8880326);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxx, new Point(252, -2020));
                     if (c.getPlayer().getMap() instanceof Field_WillBattle) {
                        Field_WillBattle fwb = (Field_WillBattle)c.getPlayer().getMap();

                        for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
                           fwb.sendWillDisplayHP(chr);
                        }
                     }
                  } else if (c.getPlayer().getMapId() == 450007850) {
                     MapleMonster mobxxxxxxxxxx = MapleLifeFactory.getMonster(8880363);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxx, new Point(352, 159));
                     mobxxxxxxxxxx = MapleLifeFactory.getMonster(8880364);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxxxx.setMaxHp(fixedhp);
                     }

                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxxxx = MapleLifeFactory.getMonster(8880360);
                     if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                        long hp = mobxxxxxxxxxx.getStats().getMaxHp();
                        long fixedhp = hp * 3L;
                        if (fixedhp < 0L) {
                           fixedhp = Long.MAX_VALUE;
                        }

                        mobxxxxxxxxxx.setHp(fixedhp);
                        mobxxxxxxxxxx.setMaxHp(fixedhp);
                     }

                     mobxxxxxxxxxx.setStance(4);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxx, new Point(352, -2020));
                     mobxxxxxxxxxx = MapleLifeFactory.getMonster(8880372);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxx, new Point(252, 159));
                     mobxxxxxxxxxx = MapleLifeFactory.getMonster(8880373);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxx, new Point(252, -2020));
                     mobxxxxxxxxxx = MapleLifeFactory.getMonster(8880376);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxx, new Point(352, 159));
                     mobxxxxxxxxxx = MapleLifeFactory.getMonster(8880377);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxx, new Point(352, -2020));
                  }

                  Timer.MapTimer.getInstance().schedule(new Runnable() {
                     @Override
                     public void run() {
                        f.sendWillNotice("전혀 다른 2개의 공간에 있는 윌을 동시에 공격해야 해요. 달빛을 모아서 사용하면 다른 쪽으로 이동이 가능할 것 같아요.", 245, 7000);
                     }
                  }, 1000L);
               }
               break;
            case will_phase2:
               if (c.getPlayer().getCurrentBossPhase() < 2) {
                  return;
               }

               if (c.getPlayer().getMapId() == 450008850) {
                  MapleMonster mobxxxxxxx = MapleLifeFactory.getMonster(8880341);
                  if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                     long hp = mobxxxxxxx.getStats().getMaxHp();
                     long fixedhp = hp * 3L;
                     if (fixedhp < 0L) {
                        fixedhp = Long.MAX_VALUE;
                     }

                     mobxxxxxxx.setHp(fixedhp);
                     mobxxxxxxx.setMaxHp(fixedhp);
                  }

                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxx, new Point(0, 215));
               } else if (c.getPlayer().getMapId() == 450008250) {
                  MapleMonster mobxxxxxxx = MapleLifeFactory.getMonster(8880301);
                  if (!DBConfig.isGanglim
                     && c.getPlayer().getMap().getFieldSetInstance() != null
                     && c.getPlayer().getMap().getFieldSetInstance() instanceof HardWillBoss
                     && c.getPlayer().isMultiMode()) {
                     long hp = mobxxxxxxx.getStats().getMaxHp();
                     long fixedhp = hp * 3L;
                     if (fixedhp < 0L) {
                        fixedhp = Long.MAX_VALUE;
                     }

                     mobxxxxxxx.setHp(fixedhp);
                     mobxxxxxxx.setMaxHp(fixedhp);
                  }

                  if (c.getPlayer().getMap().getFieldSetInstance() != null
                     && c.getPlayer().getMap().getFieldSetInstance() instanceof HellWillBoss
                     && !DBConfig.isGanglim) {
                     long hp = mobxxxxxxx.getStats().getMaxHp();
                     ChangeableStats cs = new ChangeableStats(mobxxxxxxx.getStats());
                     cs.hp = hp * 3L * 23L;
                     mobxxxxxxx.getStats().setMaxHp(hp * 3L * 23L);
                     mobxxxxxxx.getStats().setHp(hp * 3L * 23L);
                     mobxxxxxxx.setMaxHp(hp * 3L * 23L);
                     mobxxxxxxx.setHp(hp * 3L * 23L);
                     mobxxxxxxx.setOverrideStats(cs);
                  } else if (c.getPlayer().getMap().getFieldSetInstance() != null
                     && c.getPlayer().getMap().getFieldSetInstance() instanceof HellWillBoss
                     && DBConfig.isGanglim) {
                     long hp = mobxxxxxxx.getStats().getMaxHp();
                     ChangeableStats cs = new ChangeableStats(mobxxxxxxx.getStats());
                     cs.hp = hp * 30L;
                     mobxxxxxxx.getStats().setMaxHp(hp * 30L);
                     mobxxxxxxx.getStats().setHp(hp * 30L);
                     mobxxxxxxx.setMaxHp(hp * 30L);
                     mobxxxxxxx.setHp(hp * 30L);
                     mobxxxxxxx.setOverrideStats(cs);
                  }

                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxx, new Point(0, 215));
               } else {
                  MapleMonster mobxxxxxxxx = MapleLifeFactory.getMonster(8880361);
                  if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                     long hp = mobxxxxxxxx.getStats().getMaxHp();
                     long fixedhp = hp * 3L;
                     if (fixedhp < 0L) {
                        fixedhp = Long.MAX_VALUE;
                     }

                     mobxxxxxxxx.setHp(fixedhp);
                     mobxxxxxxxx.setMaxHp(fixedhp);
                  }

                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(0, 215));
               }

               MapleMonster mobxxxxxxxx = MapleLifeFactory.getMonster(8880323);
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(352, 215));
               mobxxxxxxxx = MapleLifeFactory.getMonster(8880327);
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxx, new Point(252, 215));
               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     if (c.getPlayer().getMap() instanceof Field_WillBattle) {
                        Field_WillBattle f = (Field_WillBattle)c.getPlayer().getMap();
                        f.sendWillNotice("거울에 비친 진짜 모습을 조심하세요. 달빛을 모아서 사용하면 치유 불가 저주를 잠시 멈출 수 있을 것 같아요.", 245, 7000);
                     }
                  }
               }, 1000L);
               break;
            case will_phase3:
               if (c.getPlayer().getCurrentBossPhase() < 3) {
                  return;
               }

               if (c.getPlayer().getMapId() == 450008950) {
                  MapleMonster mobxxxxxx = MapleLifeFactory.getMonster(8880342);
                  if (!DBConfig.isGanglim && c.getPlayer().isMultiMode()) {
                     long hp = mobxxxxxx.getStats().getMaxHp();
                     long fixedhp = hp * 3L;
                     if (fixedhp < 0L) {
                        fixedhp = Long.MAX_VALUE;
                     }

                     mobxxxxxx.setHp(fixedhp);
                     mobxxxxxx.setMaxHp(fixedhp);
                  }

                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxx, new Point(80, 36));
               } else if (c.getPlayer().getMapId() == 450008350) {
                  MapleMonster mobxxxxxx = MapleLifeFactory.getMonster(8880302);
                  if (!DBConfig.isGanglim
                     && c.getPlayer().getMap().getFieldSetInstance() != null
                     && c.getPlayer().getMap().getFieldSetInstance() instanceof HardWillBoss
                     && c.getPlayer().isMultiMode()) {
                     long hp = mobxxxxxx.getStats().getMaxHp();
                     long fixedhp = hp * 3L;
                     if (fixedhp < 0L) {
                        fixedhp = Long.MAX_VALUE;
                     }

                     mobxxxxxx.setHp(fixedhp);
                     mobxxxxxx.setMaxHp(fixedhp);
                  }

                  if (c.getPlayer().getMap().getFieldSetInstance() != null
                     && c.getPlayer().getMap().getFieldSetInstance() instanceof HellWillBoss
                     && !DBConfig.isGanglim) {
                     long hp = mobxxxxxx.getStats().getMaxHp();
                     ChangeableStats cs = new ChangeableStats(mobxxxxxx.getStats());
                     cs.hp = hp * 3L * 23L;
                     mobxxxxxx.getStats().setMaxHp(hp * 3L * 23L);
                     mobxxxxxx.getStats().setHp(hp * 3L * 23L);
                     mobxxxxxx.setMaxHp(hp * 3L * 23L);
                     mobxxxxxx.setHp(hp * 3L * 23L);
                     mobxxxxxx.setOverrideStats(cs);
                  } else if (c.getPlayer().getMap().getFieldSetInstance() != null
                     && c.getPlayer().getMap().getFieldSetInstance() instanceof HellWillBoss
                     && DBConfig.isGanglim) {
                     long hp = mobxxxxxx.getStats().getMaxHp();
                     ChangeableStats cs = new ChangeableStats(mobxxxxxx.getStats());
                     cs.hp = hp * 30L;
                     mobxxxxxx.getStats().setMaxHp(hp * 30L);
                     mobxxxxxx.getStats().setHp(hp * 30L);
                     mobxxxxxx.setMaxHp(hp * 30L);
                     mobxxxxxx.setHp(hp * 30L);
                     mobxxxxxx.setOverrideStats(cs);
                  }

                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxx, new Point(80, 36));
               } else {
                  MapleMonster mobxxxxxxx = MapleLifeFactory.getMonster(8880362);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxx, new Point(80, 36));
               }

               MapleMonster mobxxxxxxx = MapleLifeFactory.getMonster(8880324);
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxx, new Point(352, 281));
               mobxxxxxxx = MapleLifeFactory.getMonster(8880328);
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxx, new Point(252, 281));
               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     if (c.getPlayer().getMap() instanceof Field_WillBattle) {
                        Field_WillBattle f = (Field_WillBattle)c.getPlayer().getMap();
                        f.addSpiderWeb(new SpiderWeb(2, -683, 395));
                        f.addSpiderWeb(new SpiderWeb(1, -701, 182));
                        f.addSpiderWeb(new SpiderWeb(2, 702, -280));
                        f.addSpiderWeb(new SpiderWeb(0, -711, -254));
                        f.addSpiderWeb(new SpiderWeb(1, 718, 432));
                        f.addSpiderWeb(new SpiderWeb(0, 712, 310));
                        f.addSpiderWeb(new SpiderWeb(1, -577, -298));
                        f.addSpiderWeb(new SpiderWeb(0, 552, 459));
                        f.addSpiderWeb(new SpiderWeb(0, 531, -268));
                        f.addSpiderWeb(new SpiderWeb(1, 699, -82));
                        f.addSpiderWeb(new SpiderWeb(0, -594, 251));
                        f.addSpiderWeb(new SpiderWeb(2, 378, 480));
                        f.addSpiderWeb(new SpiderWeb(1, 577, 345));
                        f.addSpiderWeb(new SpiderWeb(0, -506, 432));
                        f.addSpiderWeb(new SpiderWeb(1, -733, -122));
                        f.addSpiderWeb(new SpiderWeb(0, -626, -179));
                        f.addSpiderWeb(new SpiderWeb(0, 604, -153));
                        f.addSpiderWeb(new SpiderWeb(1, -405, 484));
                        f.addSpiderWeb(new SpiderWeb(0, 736, 56));
                        f.addSpiderWeb(new SpiderWeb(0, -749, 17));
                        f.addSpiderWeb(new SpiderWeb(2, -366, -325));
                        f.addSpiderWeb(new SpiderWeb(1, 391, -307));
                        f.addSpiderWeb(new SpiderWeb(0, -197, -300));
                        f.addSpiderWeb(new SpiderWeb(1, 458, -163));
                        f.addSpiderWeb(new SpiderWeb(0, -282, 488));
                        f.addSpiderWeb(new SpiderWeb(1, 80, 482));
                        f.addSpiderWeb(new SpiderWeb(1, -485, -148));
                        f.addSpiderWeb(new SpiderWeb(0, -606, -75));
                        f.addSpiderWeb(new SpiderWeb(1, 772, 169));
                        f.addSpiderWeb(new SpiderWeb(2, -84, 481));
                        f.addSpiderWeb(new SpiderWeb(1, -650, 45));
                        f.addSpiderWeb(new SpiderWeb(2, 558, -58));
                        f.addSpiderWeb(new SpiderWeb(2, 164, -308));
                        f.addSpiderWeb(new SpiderWeb(1, -61, -275));
                        f.sendWillNotice("윌이 진심이 된 것 같군요. 달빛을 모아서 사용하면 거미줄을 태워버릴 수 있을 것 같아요.", 245, 7000);
                     }
                  }
               }, 2000L);
               break;
            case JinHillah_onFirstUserEnter:
               if (c.getPlayer().getCurrentBossPhase() == 0) {
                  return;
               }

               if (c.getPlayer().getMapId() != 450010930) {
                  mob = MapleLifeFactory.getMonster(8880411);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 266));
                  mob = MapleLifeFactory.getMonster(8880412);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 266));
                  mob = MapleLifeFactory.getMonster(8880410);
                  if (c.getPlayer().isBmQuestBoss()) {
                     long hp = mob.getHp();
                     hp = (long)(hp * 0.75);
                     mob.setHp(hp);
                  }

                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 266));
               } else {
                  mob = MapleLifeFactory.getMonster(8880406);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 266));
                  mob = MapleLifeFactory.getMonster(8880407);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 266));
                  mob = MapleLifeFactory.getMonster(8880405);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 266));
               }

               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     if (c.getPlayer().getMap() instanceof Field_JinHillah) {
                        Field_JinHillah f = (Field_JinHillah)c.getPlayer().getMap();
                        f.sendJinHillahNotice("영혼이 타오르는 양초를 힐라가 일정 시간마다 베어 없앨 것이다. 영혼을 빼앗기지 않게 조심하자.", 8000);
                     }
                  }
               }, 1000L);
               break;
            case firstenter_bossBlackMage:
               if (c.getPlayer().getMapId() == 450013100) {
                  if (c.getPlayer().getCurrentBossPhase() < 1) {
                     return;
                  }

                  mob = MapleLifeFactory.getMonster(8880505);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 85));
                  mob = MapleLifeFactory.getMonster(8880500);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-350, 85));
                  mob = MapleLifeFactory.getMonster(8880501);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(350, 85));
                  mob = MapleLifeFactory.getMonster(8880512);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(5, 85));
                  Timer.MapTimer.getInstance().schedule(new Runnable() {
                     @Override
                     public void run() {
                        if (c.getPlayer().getMap() instanceof Field_BlackMage) {
                           Field_BlackMage f = (Field_BlackMage)c.getPlayer().getMap();
                           f.sendBlackMageNotice("검은 마법사와 대적하기 위해서는 그를 호위하는 창조와 파괴의 기사들을 물리쳐야 한다.", 8000);
                        }
                     }
                  }, 1000L);
               } else if (c.getPlayer().getMapId() == 450013300) {
                  if (c.getPlayer().getCurrentBossPhase() < 2) {
                     return;
                  }

                  mob = MapleLifeFactory.getMonster(8880502);
                  mob.setStance(4);
                  mob.setFh(3);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 88));
                  mob = MapleLifeFactory.getMonster(8880512);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(5, 85));
                  mob = MapleLifeFactory.getMonster(8880516);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(5, 85));
                  Timer.MapTimer.getInstance().schedule(new Runnable() {
                     @Override
                     public void run() {
                        if (c.getPlayer().getMap() instanceof Field_BlackMage) {
                           Field_BlackMage f = (Field_BlackMage)c.getPlayer().getMap();
                           f.sendBlackMageNotice("드디어 검은 마법사의 앞에 바로 섰다. 모든 힘을 다해 그를 물리치자.", 8000);
                        }
                     }
                  }, 1000L);
               } else if (c.getPlayer().getMapId() == 450013500) {
                  if (c.getPlayer().getCurrentBossPhase() < 3) {
                     return;
                  }

                  mob = MapleLifeFactory.getMonster(8880503);
                  mob.setStance(2);
                  mob.setFh(7);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 85));
                  mob = MapleLifeFactory.getMonster(8880512);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 85));
                  Timer.MapTimer.getInstance().schedule(new Runnable() {
                     @Override
                     public void run() {
                        if (c.getPlayer().getMap() instanceof Field_BlackMage) {
                           Field_BlackMage f = (Field_BlackMage)c.getPlayer().getMap();
                           f.sendBlackMageNotice("저 모습은 마치 신의 권능이라도 얻은 것 같다. 설사 상대가 신이라고 할지라도 모두를 위해 여기서 저지해야 한다.", 8000);
                        }
                     }
                  }, 1000L);
               } else if (c.getPlayer().getMapId() == 450013700) {
                  if (c.getPlayer().getCurrentBossPhase() < 4) {
                     return;
                  }

                  mob = MapleLifeFactory.getMonster(8880504);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 218));
                  mob = MapleLifeFactory.getMonster(8880512);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 218));
                  mob = c.getPlayer().getMap().getMonsterById(8880519);
                  if (mob == null) {
                     mob = MapleLifeFactory.getMonster(8880519);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 218));
                  }
               } else if (c.getPlayer().getMapId() == 450013750) {
                  if (c.getPlayer().getCurrentBossPhase() < 5) {
                     return;
                  }

                  mob = MapleLifeFactory.getMonster(8880518);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 218));
               }
               break;
            case dojang_Eff:
               int temp = (c.getPlayer().getMapId() - 925070000) / 100;
               int stage = temp - temp / 100 * 100;
               c.getPlayer().send(CField.environmentChange("Dojang/start", 5, 100));
               c.getPlayer().send(CField.environmentChange("dojang/start/stage", 19, 0));
               c.getPlayer().send(CField.environmentChange("dojang/start/number/" + temp, 19, 0));
               c.getSession().writeAndFlush(CField.trembleEffect(0, 1));
               break;
            case onRewordMap:
               reloadWitchTower(c);
               break;
            case moonrabbit_mapEnter:
               c.getPlayer().getMap().startMapEffect("월묘가 나타났습니다. 제한시간 내에 힘을 합쳐 월묘를 잡으면 달맞이 보상을 획득할 수 있습니다.", 5120016);
               break;
            case StageMsg_goddess:
               switch (c.getPlayer().getMapId()) {
                  case 920010000:
                     c.getPlayer().getMap().startMapEffect("Please save me by collecting Cloud Pieces!", 5120019);
                     return;
                  case 920010100:
                     c.getPlayer().getMap().startMapEffect("Bring all the pieces here to save Minerva!", 5120019);
                     return;
                  case 920010200:
                     c.getPlayer().getMap().startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019);
                     return;
                  case 920010300:
                     c.getPlayer().getMap().startMapEffect("Destroy the monsters in each room and gather Statue Pieces!", 5120019);
                     return;
                  case 920010400:
                     c.getPlayer().getMap().startMapEffect("Play the correct LP of the day!", 5120019);
                     return;
                  case 920010500:
                     c.getPlayer().getMap().startMapEffect("Find the correct combination!", 5120019);
                     return;
                  case 920010600:
                     c.getPlayer().getMap().startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019);
                     return;
                  case 920010700:
                     c.getPlayer().getMap().startMapEffect("Get the right combination once you get to the top!", 5120019);
                     return;
                  case 920010800:
                     c.getPlayer().getMap().startMapEffect("Summon and defeat Papa Pixie!", 5120019);
                     return;
                  default:
                     return;
               }
            case StageMsg_crack:
               switch (c.getPlayer().getMapId()) {
                  case 922010100:
                     c.getPlayer().getMap().startMapEffect("Defeat all the Ratz!", 5120018);
                     return;
                  case 922010200:
                     c.getPlayer().getMap().startSimpleMapEffect("Collect all the passes!", 5120018);
                     return;
                  case 922010300:
                     c.getPlayer().getMap().startMapEffect("Destroy the monsters!", 5120018);
                     return;
                  case 922010400:
                     c.getPlayer().getMap().startMapEffect("Destroy the monsters in each room!", 5120018);
                     return;
                  case 922010500:
                     c.getPlayer().getMap().startMapEffect("Collect passes from each room!", 5120018);
                     return;
                  case 922010600:
                     c.getPlayer().getMap().startMapEffect("Get to the top!", 5120018);
                     return;
                  case 922010700:
                     c.getPlayer().getMap().startMapEffect("Destroy the Rombots!", 5120018);
                     return;
                  case 922010800:
                     c.getPlayer().getMap().startSimpleMapEffect("Get the right combination!", 5120018);
                     return;
                  case 922010900:
                     c.getPlayer().getMap().startMapEffect("Defeat Alishar!", 5120018);
                     return;
                  default:
                     return;
               }
            case StageMsg_together:
               switch (c.getPlayer().getMapId()) {
                  case 103000800:
                     c.getPlayer().getMap().startMapEffect("Solve the question and gather the amount of passes!", 5120017);
                     return;
                  case 103000801:
                     c.getPlayer().getMap().startMapEffect("Get on the ropes and unveil the correct combination!", 5120017);
                     return;
                  case 103000802:
                     c.getPlayer().getMap().startMapEffect("Get on the platforms and unveil the correct combination!", 5120017);
                     return;
                  case 103000803:
                     c.getPlayer().getMap().startMapEffect("Get on the barrels and unveil the correct combination!", 5120017);
                     return;
                  case 103000804:
                     c.getPlayer().getMap().startMapEffect("Defeat King Slime and his minions!", 5120017);
                     return;
                  default:
                     return;
               }
            case StageMsg_romio:
               switch (c.getPlayer().getMapId()) {
                  case 926100000:
                     c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120021);
                     return;
                  case 926100001:
                     c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120021);
                     return;
                  case 926100100:
                     c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120021);
                     return;
                  case 926100200:
                     c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120021);
                     return;
                  case 926100203:
                     c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120021);
                     return;
                  case 926100300:
                     c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120021);
                     return;
                  case 926100401:
                     c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120021);
                     return;
                  default:
                     return;
               }
            case StageMsg_juliet:
               switch (c.getPlayer().getMapId()) {
                  case 926110000:
                     c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120022);
                     return;
                  case 926110001:
                     c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120022);
                     return;
                  case 926110100:
                     c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120022);
                     return;
                  case 926110200:
                     c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120022);
                     return;
                  case 926110203:
                     c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120022);
                     return;
                  case 926110300:
                     c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120022);
                     return;
                  case 926110401:
                     c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120022);
                     return;
                  default:
                     return;
               }
            case party6weatherMsg:
               switch (c.getPlayer().getMapId()) {
                  case 930000000:
                     c.getPlayer().getMap().startMapEffect("Step in the portal to be transformed.", 5120023);
                     break;
                  case 930000100:
                     c.getPlayer().getMap().startMapEffect("Defeat the poisoned monsters!", 5120023);
                     break;
                  case 930000200:
                     c.getPlayer().getMap().startMapEffect("Eliminate the spore that blocks the way by purifying the poison!", 5120023);
                     break;
                  case 930000300:
                     c.getPlayer().getMap().startMapEffect("Uh oh! The forest is too confusing! Find me, quick!", 5120023);
                     break;
                  case 930000400:
                     c.getPlayer().getMap().startMapEffect("Purify the monsters by getting Purification Marbles from me!", 5120023);
                     break;
                  case 930000500:
                     c.getPlayer().getMap().startMapEffect("Find the Purple Magic Stone!", 5120023);
                     break;
                  case 930000600:
                     c.getPlayer().getMap().startMapEffect("Place the Magic Stone on the altar!", 5120023);
               }
            case prisonBreak_mapEnter:
            case boss_Ravana_mirror:
            case boss_Ravana:
            case cygnus_Summon:
            case MalayBoss_Int:
            case storymap_scenario:
            case VanLeon_Before:
            case dojang_Msg:
            case balog_summon:
            case easy_balog_summon:
               break;
            case StageMsg_davy:
               switch (c.getPlayer().getMapId()) {
                  case 925100000:
                     c.getPlayer().getMap().startMapEffect("Defeat the monsters outside of the ship to advance!", 5120020);
                     break;
                  case 925100100:
                     c.getPlayer().getMap().startMapEffect("We must prove ourselves! Get me Pirate Medals!", 5120020);
                     break;
                  case 925100200:
                     c.getPlayer().getMap().startMapEffect("Defeat the guards here to pass!", 5120020);
                     break;
                  case 925100300:
                     c.getPlayer().getMap().startMapEffect("Eliminate the guards here to pass!", 5120020);
                     break;
                  case 925100400:
                     c.getPlayer().getMap().startMapEffect("Lock the doors! Seal the root of the Ship's power!", 5120020);
                     break;
                  case 925100500:
                     c.getPlayer().getMap().startMapEffect("Destroy the Lord Pirate!", 5120020);
               }

               EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
               if (c.getPlayer().getMapId() == 925100500 && em != null && em.getProperty("stage5") != null) {
                  int mobId = Randomizer.nextBoolean() ? 9300107 : 9300119;
                  int st = Integer.parseInt(em.getProperty("stage5"));
                  switch (st) {
                     case 1:
                        mobId = Randomizer.nextBoolean() ? 9300119 : 9300105;
                        break;
                     case 2:
                        mobId = Randomizer.nextBoolean() ? 9300106 : 9300105;
                  }

                  MapleMonster shammos = MapleLifeFactory.getMonster(mobId);
                  if (c.getPlayer().getEventInstance() != null) {
                     c.getPlayer().getEventInstance().registerMonster(shammos);
                  }

                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
               }
               break;
            case astaroth_summon:
               c.getPlayer().getMap().resetFully();
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400633), new Point(600, -26));
               break;
            case killing_BonusSetting:
               c.getPlayer().getMap().resetFully();
               c.getSession().writeAndFlush(CField.showEffect("killing/bonus/bonus"));
               c.getSession().writeAndFlush(CField.showEffect("killing/bonus/stage"));
               Point pos1 = null;
               Point pos2 = null;
               Point pos3 = null;
               int spawnPer = 0;
               int mobId = 0;
               byte var228;
               if (c.getPlayer().getMapId() >= 910320010 && c.getPlayer().getMapId() <= 910320029) {
                  pos1 = new Point(121, 218);
                  pos2 = new Point(396, 43);
                  pos3 = new Point(-63, 43);
                  mobId = 9700020;
                  var228 = 10;
               } else if (c.getPlayer().getMapId() >= 926010010 && c.getPlayer().getMapId() <= 926010029) {
                  pos1 = new Point(0, 88);
                  pos2 = new Point(-326, -115);
                  pos3 = new Point(361, -115);
                  mobId = 9700019;
                  var228 = 10;
               } else if (c.getPlayer().getMapId() >= 926010030 && c.getPlayer().getMapId() <= 926010049) {
                  pos1 = new Point(0, 88);
                  pos2 = new Point(-326, -115);
                  pos3 = new Point(361, -115);
                  mobId = 9700019;
                  var228 = 15;
               } else if (c.getPlayer().getMapId() >= 926010050 && c.getPlayer().getMapId() <= 926010069) {
                  pos1 = new Point(0, 88);
                  pos2 = new Point(-326, -115);
                  pos3 = new Point(361, -115);
                  mobId = 9700019;
                  var228 = 20;
               } else {
                  if (c.getPlayer().getMapId() < 926010070 || c.getPlayer().getMapId() > 926010089) {
                     break;
                  }

                  pos1 = new Point(0, 88);
                  pos2 = new Point(-326, -115);
                  pos3 = new Point(361, -115);
                  mobId = 9700029;
                  var228 = 20;
               }

               for (int i = 0; i < var228; i++) {
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
               }

               c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
               break;
            case mPark_summonBoss:
               if (c.getPlayer().getEventInstance() != null
                  && c.getPlayer().getEventInstance().getProperty("boss") != null
                  && c.getPlayer().getEventInstance().getProperty("boss").equals("0")) {
                  for (int i = 9800119; i < 9800125; i++) {
                     MapleMonster boss = MapleLifeFactory.getMonster(i);
                     c.getPlayer().getEventInstance().registerMonster(boss);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(boss, new Point(c.getPlayer().getMap().getPortal(2).getPosition()));
                  }
               }
               break;
            case shammos_Fenter:
               if (c.getPlayer().getMapId() >= 921120005 && c.getPlayer().getMapId() < 921120500) {
                  MapleMonster shammos = MapleLifeFactory.getMonster(9300275);
                  if (c.getPlayer().getEventInstance() != null) {
                     int averageLevel = 0;
                     int size = 0;

                     for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                        averageLevel += pl.getLevel();
                        size++;
                     }

                     if (size <= 0) {
                        return;
                     }

                     averageLevel /= size;
                     shammos.changeLevel(averageLevel);
                     c.getPlayer().getEventInstance().registerMonster(shammos);
                     if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                        c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                     }

                     shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                  }

                  c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                  shammos.switchController(c.getPlayer(), false);
                  c.getSession().writeAndFlush(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));
               }
               break;
            case iceman_FEnter:
               if (c.getPlayer().getMapId() >= 932000100 && c.getPlayer().getMapId() < 932000300) {
                  MapleMonster shammos = MapleLifeFactory.getMonster(9300438);
                  if (c.getPlayer().getEventInstance() != null) {
                     c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                  }

                  shammos.switchController(c.getPlayer(), false);
                  c.getSession().writeAndFlush(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));
               }
               break;
            case PRaid_D_Fenter:
               switch (c.getPlayer().getMapId() % 10) {
                  case 0:
                     c.getPlayer().getMap().startMapEffect("몬스터를 모두 퇴치해라!", 5120033);
                     return;
                  case 1:
                     c.getPlayer().getMap().startMapEffect("상자를 부수고, 나오는 몬스터를 모두 퇴치해라!", 5120033);
                     return;
                  case 2:
                     c.getPlayer().getMap().startMapEffect("일등항해사를 퇴치해라!", 5120033);
                     return;
                  case 3:
                     c.getPlayer().getMap().startMapEffect("몬스터를 모두 퇴치해라!", 5120033);
                     return;
                  case 4:
                     c.getPlayer().getMap().startMapEffect("몬스터를 모두 퇴치하고, 점프대를 작동시켜서 건너편으로 건너가라!", 5120033);
                     return;
                  default:
                     return;
               }
            case PRaid_B_Fenter:
               c.getPlayer().getMap().startMapEffect("상대편보다 먼저 몬스터를 퇴치하라!", 5120033);
               break;
            case summon_pepeking:
               c.getPlayer().getMap().resetFully();
               int rand = Randomizer.nextInt(10);
               int mob_ToSpawn = 100100;
               if (rand >= 4) {
                  mob_ToSpawn = 3300007;
               } else if (rand >= 1) {
                  mob_ToSpawn = 3300006;
               } else {
                  mob_ToSpawn = 3300005;
               }

               c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob_ToSpawn), c.getPlayer().getPosition());
               break;
            case Xerxes_summon:
               c.getPlayer().getMap().resetFully();
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(6160003), c.getPlayer().getPosition());
               break;
            case shammos_FStart:
               c.getPlayer().getMap().startMapEffect("Defeat the monsters!", 5120035);
               break;
            case kenta_mapEnter:
               switch (c.getPlayer().getMapId() / 100 % 10) {
                  case 1:
                     c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120052);
                     return;
                  case 2:
                     c.getPlayer().getMap().startMapEffect("Get me 20 Air Bubbles for me to survive!", 5120052);
                     return;
                  case 3:
                     c.getPlayer().getMap().startMapEffect("Help! Make sure I live for three minutes!", 5120052);
                     return;
                  case 4:
                     c.getPlayer().getMap().startMapEffect("Eliminate the two Pianus!", 5120052);
                     return;
                  default:
                     return;
               }
            case iceman_Boss:
               c.getPlayer().getMap().startMapEffect("You will perish!", 5120050);
               break;
            case Visitor_Cube_poison:
               c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120039);
               break;
            case Visitor_Cube_Hunting_Enter_First:
               c.getPlayer().getMap().startMapEffect("Eliminate all the Visitors!", 5120039);
               break;
            case VisitorCubePhase00_Start:
               c.getPlayer().getMap().startMapEffect("Eliminate all the flying monsters!", 5120039);
               break;
            case visitorCube_addmobEnter:
               c.getPlayer().getMap().startMapEffect("Eliminate all the monsters by moving around the map!", 5120039);
               break;
            case Visitor_Cube_PickAnswer_Enter_First_1:
               c.getPlayer().getMap().startMapEffect("One of the aliens must have a clue to the way out.", 5120039);
               break;
            case visitorCube_medicroom_Enter:
               c.getPlayer().getMap().startMapEffect("Eliminate all of the Unjust Visitors!", 5120039);
               break;
            case visitorCube_iceyunna_Enter:
               c.getPlayer().getMap().startMapEffect("Eliminate all of the Speedy Visitors!", 5120039);
               break;
            case Visitor_Cube_AreaCheck_Enter_First:
               c.getPlayer().getMap().startMapEffect("The switch at the top of the room requires a heavy weight.", 5120039);
               break;
            case visitorCube_boomboom_Enter:
               c.getPlayer().getMap().startMapEffect("The enemy is powerful! Watch out!", 5120039);
               break;
            case visitorCube_boomboom2_Enter:
               c.getPlayer().getMap().startMapEffect("This Visitor is strong! Be careful!", 5120039);
               break;
            case CubeBossbang_Enter:
               c.getPlayer().getMap().startMapEffect("This is it! Give it your best shot!", 5120039);
               break;
            case metro_firstSetting:
            case killing_MapSetting:
            case Sky_TrapFEnter:
            case balog_bonusSetting:
               c.getPlayer().getMap().resetFully();
               break;
            case first_kalos3:
               if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof KalosBoss) {
                  FieldSetInstance bossmap = (KalosBoss)c.getPlayer().getMap().getFieldSetInstance();
                  if (bossmap.isPracticeMode) {
                     return;
                  }
               }

               Field phase2 = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId() - 20);
               if (phase2 == null || !(phase2 instanceof Field_BossKalosPhase2)) {
                  return;
               }

               if (!((Field_BossKalosPhase2)phase2).nextPhase()) {
                  return;
               }

               if (c.getPlayer().getMap().getMonsterById(8880808) == null) {
                  MapleMonster mobxxxxxxxxxxxxxx = MapleLifeFactory.getMonster(8880808);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxxxxxx, new Point(0, 275));
               }
               break;
            case first_kalos3_easy:
               if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof KalosBoss) {
                  FieldSetInstance bossmap = (KalosBoss)c.getPlayer().getMap().getFieldSetInstance();
                  if (bossmap.isPracticeMode) {
                     return;
                  }
               }

               phase2 = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId() - 20);
               if (phase2 != null && phase2 instanceof Field_BossKalosPhase2) {
                  if (!((Field_BossKalosPhase2)phase2).nextPhase()) {
                     return;
                  }

                  if (c.getPlayer().getMap().getMonsterById(8881018) == null) {
                     mob = MapleLifeFactory.getMonster(8881018);
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(0, 275));
                  }
               }
               break;
            case first_kalos3_chaos:
               if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof KalosBoss) {
                  FieldSetInstance bossmap = (KalosBoss)c.getPlayer().getMap().getFieldSetInstance();
                  if (bossmap.isPracticeMode) {
                     return;
                  }
               }

               Field phase2x = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId() - 20);
               if (phase2x == null || !(phase2x instanceof Field_BossKalosPhase2)) {
                  return;
               }

               if (!((Field_BossKalosPhase2)phase2x).nextPhase()) {
                  return;
               }

               if (c.getPlayer().getMap().getMonsterById(8881038) == null) {
                  MapleMonster mobxxxxxxxxxxxxxx = MapleLifeFactory.getMonster(8881038);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxxxxxx, new Point(0, 275));
               }
               break;
            case first_kalos3_ex:
               if (c.getPlayer().getMap().getFieldSetInstance() != null && c.getPlayer().getMap().getFieldSetInstance() instanceof KalosBoss) {
                  FieldSetInstance bossmap = (KalosBoss)c.getPlayer().getMap().getFieldSetInstance();
                  if (bossmap.isPracticeMode) {
                     return;
                  }
               }

               Field phase2xx = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId() - 20);
               if (phase2xx == null || !(phase2xx instanceof Field_BossKalosPhase2)) {
                  return;
               }

               if (!((Field_BossKalosPhase2)phase2xx).nextPhase()) {
                  return;
               }

               if (c.getPlayer().getMap().getMonsterById(8881058) == null) {
                  MapleMonster mobxxxxxxxxxxxxxx = MapleLifeFactory.getMonster(8881058);
                  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mobxxxxxxxxxxxxxx, new Point(0, 275));
               }
               break;
            default:
               if (!scriptName.toLowerCase().contains("enter_")) {
                  FileoutputUtil.log(
                     "Log_Script_Except.rtf", "Unhandled script : " + scriptName + ", type : onFirstUserEnter - MAPID " + c.getPlayer().getMapId()
                  );
               }
         }
      }
   }

   public static void startScript_User(final MapleClient c, String scriptName) {
      if (c.getPlayer() != null) {
         String data = "";
         if (DBConfig.isGanglim && c.getPlayer().isGM()) {
            c.getPlayer().dropMessage(5, "*scriptName: " + scriptName);
         }

         if (!DBConfig.isHosting) {
            System.err.println(MapScriptMethods.onUserEnter.fromString(scriptName));
         }

         switch (MapScriptMethods.onUserEnter.fromString(scriptName)) {
            case goongi_direction:
               if (c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 20);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "goongi_direction");
               }
               break;
            case dool_direction:
               if (c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 20);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "dool_direction");
               }
               break;
            case hondon_direction:
               if (c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 20);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "hondon_direction");
               }
               break;
            case dusk_timeRecord:
               c.getPlayer().send(CField.sendWeatherEffectNotice(250, 3000, false, "점차 공포가 차올라 있을 수 없는 것이 보이게 됩니다! 견디지 못하면 공포가 전이되니 주의하세요!"));
               c.getSession().writeAndFlush(CField.addPopupSay(0, 3000, "촉수가 눈을 방어하고 있어 제대로 된 피해를 주기 힘들겠군.", ""));
               break;
            case dunkel_enter:
               c.getPlayer().send(CField.sendWeatherEffectNotice(272, 5000, false, "친위대장 듄켈 : 나와 나의 군단이 있는 이상 위대하신 분께는 손끝 하나 대지 못한다!"));
               break;
            case slime_direction1:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 10000);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "slime_direction1");
               }
               break;
            case slime_direction2:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 10000);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "slime_direction2");
               }
               break;
            case will_direction1:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 50);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "will_direction1");
               }
               break;
            case will_direction2:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 50);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "will_direction2");
               }
               break;
            case will_direction3:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 50);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "will_direction3");
               }
               break;
            case seren_direction1:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 20);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "seren_direction1");
               }
               break;
            case seren_direction2:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 20);
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "seren_direction2");
               }
               break;
            case will_phase1_everyone:
            case will_phase2_everyone:
            case will_phase3_everyone:
            case JinHillah_onUserEnter:
               c.getSession().writeAndFlush(CField.UIPacket.endInGameDirectionMode(1));
               break;
            case JinHillah_direction1:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  if (c.getPlayer().getMapId() == 450010900) {
                     c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 30);
                  } else {
                     c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 100);
                  }

                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "JinHillah_direction1");
               }
               break;
            case bossBlackMage_directionSc:
               NPCScriptManager.getInstance().dispose(c);
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "bossBlackMage_directionSc");
               break;
            case bossBlackMage_direction:
               if (DBConfig.isGanglim && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().isSkipIntro()) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 100);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().dispose(c);
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "bossBlackMage_direction");
               }
               break;
            case enter_bossBlackMage:
               if (c.getPlayer().getMap() instanceof Field_BlackMage) {
                  Field_BlackMage f = (Field_BlackMage)c.getPlayer().getMap();
                  c.getSession().writeAndFlush(CField.UIPacket.endInGameDirectionMode(1));
                  if (f.getPhase() < 3) {
                     c.getSession()
                        .writeAndFlush(CField.addPopupSay(0, 4000, "이 지역에서 발생되는 공격은 창조나 파괴의 저주를 거는 것 같다...만약 두 저주가 동시에 걸린다면 #b큰 피해#k를 입으니 조심하자", ""));
                  } else if (f.getPhase() == 3) {
                     c.getSession().writeAndFlush(CField.addPopupSay(3003902, 4000, "#face1#가자. 나는 복수를, 너는 세계를 지키는 거야.", ""));
                  } else {
                     c.getSession().writeAndFlush(CField.addPopupSay(0, 4000, "아무 것도 없는 공간…… 세계가 이런 모습이 되게 할 순 없어…", ""));
                  }
               }
               break;
            case bossBlackMage_directionFail:
               NPCScriptManager.getInstance().dispose(c);
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "bossBlackMage_directionFail");
               break;
            case enter_100:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_100");
               break;
            case enter_101:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_101");
               break;
            case enter_102:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_102");
               break;
            case enter_103:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_103");
               break;
            case enter_104:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_104");
               break;
            case enter_105:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_105");
               break;
            case enter_110:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_110");
               break;
            case enter_111:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_111");
               break;
            case enter_120:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_120");
               break;
            case enter_121:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_121");
               break;
            case enter_450004100:
               if (DBConfig.isGanglim
                  && c.getPlayer().getParty() != null
                  && c.getPlayer().getParty().getLeader().isSkipIntro()
                  && !(c.getPlayer().getMap().getFieldSetInstance() instanceof HellLucidBoss)) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 50);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  c.getSession().writeAndFlush(CField.UIPacket.setIngameDirectionMode(false, false, false));
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getSession().writeAndFlush(CField.EffectPacket.spineEffect("Map/Effect3.img/BossLucid/Lucid/lusi", "animation", 1, 0, 1, 0, ""));
                  c.getSession().writeAndFlush(CField.playSE("Sound/SoundEff.img/ArcaneRiver/lucid_spine"));
                  c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 9000));
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 50);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 9500L);
               }
               break;
            case enter_450004150:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(22, 700));
               c.getSession().writeAndFlush(CField.UIPacket.endInGameDirectionMode(1));
               break;
            case enter_450004200:
               NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
               if (cm != null) {
                  cm.dispose();
               }

               if (DBConfig.isGanglim
                  && c.getPlayer().getParty() != null
                  && c.getPlayer().getParty().getLeader().isSkipIntro()
                  && !(c.getPlayer().getMap().getFieldSetInstance() instanceof HellLucidBoss)) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 50);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setTransferFieldOverlap(true);
               } else {
                  NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_450004200");
               }
               break;
            case enter_993189101:
               if (c.getPlayer().getOneInfoQuestInteger(1234567, "see_intro") == 1) {
                  Field map = c.getChannelServer().getMapFactory().getMap(ServerConstants.TownMap);
                  c.getPlayer().changeMap(map, map.getPortal("sp"));
                  c.getPlayer().dropMessage(6, "해당 계정은 이미 인트로를 시청하여 진 캐슬로 이동됩니다.");
                  return;
               }

               c.getPlayer().temporaryStatSet(80002996, Integer.MAX_VALUE, SecondaryStatFlag.RideVehicle, 1932691);
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_993189101");
               break;
            case enter_993189102:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_993189102");
               break;
            case enter_993189103:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_993189103");
               break;
            case enter_680000710:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_680000710");
               break;
            case enter_993210000:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_993210000");
               break;
            case enter_410000300:
               NPCScriptManager.getInstance().startMapScript(c, 3003250, "enter_410000300");
               break;
            case cannon_tuto_direction:
               showIntro(c, "Effect/Direction4.img/cannonshooter/Scene00");
               showIntro(c, "Effect/Direction4.img/cannonshooter/out00");
               break;
            case cannon_tuto_direction1: {
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
               c.getSession().writeAndFlush(CField.UIPacket.IntroLock(true));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/0", 5000, 0, 0, 1, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/1", 5000, 0, 0, 1, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/2", 5000, 0, 0, 1, 0));
               WZEffect e = new WZEffect(c.getPlayer().getId(), 0, 0, 0, "Effect/Direction4.img/cannonshooter/face04");
               c.getSession().writeAndFlush(e.encodeForLocal());
               e = new WZEffect(c.getPlayer().getId(), 0, 0, 0, "Effect/Direction4.img/cannonshooter/out01");
               c.getSession().writeAndFlush(e.encodeForLocal());
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 5000));
               break;
            }
            case cannon_tuto_direction2:
               showIntro(c, "Effect/Direction4.img/cannonshooter/Scene01");
               showIntro(c, "Effect/Direction4.img/cannonshooter/out02");
               break;
            case cygnusTest:
               showIntro(c, "Effect/Direction.img/cygnus/Scene" + (c.getPlayer().getMapId() == 913040006 ? 9 : c.getPlayer().getMapId() - 913040000));
               break;
            case cygnusJobTutorial:
               showIntro(c, "Effect/Direction.img/cygnusJobTutorial/Scene" + (c.getPlayer().getMapId() - 913040100));
               break;
            case shammos_Enter:
               if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 921120500) {
                  NPCScriptManager.getInstance().dispose(c);
                  c.removeClickedNPC();
                  NPCScriptManager.getInstance().start(c, 2022006);
               }
               break;
            case iceman_Enter:
               if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 932000300) {
                  NPCScriptManager.getInstance().dispose(c);
                  c.removeClickedNPC();
                  NPCScriptManager.getInstance().start(c, 2159020);
               }
               break;
            case start_itemTake:
               EventManager em = c.getChannelServer().getEventSM().getEventManager("OrbisPQ");
               if (em != null && em.getProperty("pre").equals("0")) {
                  NPCScriptManager.getInstance().dispose(c);
                  c.removeClickedNPC();
                  NPCScriptManager.getInstance().start(c, 2013001);
               }
               break;
            case PRaid_W_Enter:
               c.getSession().writeAndFlush(CWvsContext.sendPyramidEnergy("PRaid_expPenalty", "0"));
               c.getSession().writeAndFlush(CWvsContext.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
               c.getSession().writeAndFlush(CWvsContext.sendPyramidEnergy("PRaid_Point", "-1"));
               c.getSession().writeAndFlush(CWvsContext.sendPyramidEnergy("PRaid_Bonus", "-1"));
               c.getSession().writeAndFlush(CWvsContext.sendPyramidEnergy("PRaid_Total", "-1"));
               c.getSession().writeAndFlush(CWvsContext.sendPyramidEnergy("PRaid_Team", ""));
               c.getSession().writeAndFlush(CWvsContext.sendPyramidEnergy("PRaid_IsRevive", "0"));
               c.getPlayer().writePoint("PRaid_Point", "-1");
               c.getPlayer().writeStatus("Red_Stage", "1");
               c.getPlayer().writeStatus("Blue_Stage", "1");
               c.getPlayer().writeStatus("redTeamDamage", "0");
               c.getPlayer().writeStatus("blueTeamDamage", "0");
               break;
            case jail:
               if (!c.getPlayer().isIntern()) {
                  c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(123455)).setCustomData(String.valueOf(System.currentTimeMillis()));
                  MapleQuestStatus stat = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(123456));
                  if (stat.getCustomData() != null) {
                     int seconds = Integer.parseInt(stat.getCustomData());
                     if (seconds > 0) {
                        c.getPlayer().startMapTimeLimitTask(seconds, c.getChannelServer().getMapFactory().getMap(100000000));
                     }
                  }
               }
               break;
            case TD_neo_BossEnter:
            case findvioleta:
               c.getPlayer().getMap().resetFully();
               break;
            case StageMsg_crack:
               if (c.getPlayer().getMapId() == 922010400) {
                  MapleMapFactory mf = c.getChannelServer().getMapFactory();
                  int q = 0;

                  for (int i = 0; i < 5; i++) {
                     q += mf.getMap(922010401 + i).getAllMonstersThreadsafe().size();
                  }

                  if (q > 0) {
                     c.getPlayer().dropMessage(-1, "There are still " + q + " monsters remaining.");
                  }
               } else if (c.getPlayer().getMapId() >= 922010401 && c.getPlayer().getMapId() <= 922010405) {
                  if (c.getPlayer().getMap().getAllMonstersThreadsafe().size() > 0) {
                     c.getPlayer().dropMessage(-1, "There are still some monsters remaining in this map.");
                  } else {
                     c.getPlayer().dropMessage(-1, "There are no monsters remaining in this map.");
                  }
               }
               break;
            case q31102e:
               if (c.getPlayer().getQuestStatus(31102) == 1) {
                  MapleQuest.getInstance(31102).forceComplete(c.getPlayer(), 2140000);
               }
               break;
            case q31103s:
               if (c.getPlayer().getQuestStatus(31103) == 0) {
                  MapleQuest.getInstance(31103).forceComplete(c.getPlayer(), 2142003);
               }
               break;
            case Resi_tutor20:
               c.getSession().writeAndFlush(CField.MapEff("resistance/tutorialGuide"));
               break;
            case Resi_tutor30: {
               WZEffect3 e = new WZEffect3(-1, "Effect/OnUserEff.img/guideEffect/resistanceTutorial/userTalk");
               c.getSession().writeAndFlush(e.encodeForLocal());
               break;
            }
            case Resi_tutor40:
               NPCScriptManager.getInstance().dispose(c);
               c.removeClickedNPC();
               NPCScriptManager.getInstance().start(c, 2159012);
               break;
            case Resi_tutor50:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
               c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               NPCScriptManager.getInstance().dispose(c);
               c.removeClickedNPC();
               NPCScriptManager.getInstance().start(c, 2159006);
               break;
            case Resi_tutor70:
               showIntro(c, "Effect/Direction4.img/Resistance/TalkJ");
               break;
            case prisonBreak_1stageEnter:
            case shammos_Start:
            case moonrabbit_takeawayitem:
            case TCMobrevive:
            case cygnus_ExpeditionEnter:
            case VanLeon_ExpeditionEnter:
            case Resi_tutor10:
            case Resi_tutor60:
            case Resi_tutor50_1:
            case sealGarden:
            case in_secretroom:
            case TD_MC_gasi2:
            case TD_MC_keycheck:
            case pepeking_effect:
            case userInBattleSquare:
            case summonSchiller:
            case VisitorleaveDirectionMode:
            case visitorPT_Enter:
            case VisitorCubePhase00_Enter:
            case visitor_ReviveMap:
            case PRaid_D_Enter:
            case PRaid_B_Enter:
            case PRaid_WinEnter:
            case PRaid_FailEnter:
            case PRaid_Revive:
            case metro_firstSetting:
            case blackSDI:
            case summonIceWall:
            case onSDI:
            case enterBlackfrog:
            case Sky_Quest:
            case dollCave00:
            case dollCave01:
            case dollCave02:
            case shammos_Base:
            case shammos_Result:
            case Sky_BossEnter:
            case Sky_GateMapEnter:
            case balog_dateSet:
            case balog_buff:
            case outCase:
            case Sky_StageEnter:
            case evanTogether:
            case merStandAlone:
            case EntereurelTW:
            case aranTutorAlone:
            case evanAlone:
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               break;
            case dojang_QcheckSet:
               NPCScriptManager.getInstance().start(c, 2091011);
               break;
            case merOutStandAlone:
               if (c.getPlayer().getQuestStatus(24001) == 1) {
                  MapleQuest.getInstance(24001).forceComplete(c.getPlayer(), 0);
                  c.getPlayer().dropMessage(5, "Quest complete.");
               }
               break;
            case merTutorSleep00: {
               showIntro(c, "Effect/Direction5.img/mersedesTutorial/Scene0");
               Map<Skill, SkillEntry> sa = new HashMap<>();
               sa.put(SkillFactory.getSkill(20021181), new SkillEntry(-1, (byte)0, -1L));
               sa.put(SkillFactory.getSkill(20021166), new SkillEntry(-1, (byte)0, -1L));
               sa.put(SkillFactory.getSkill(20020109), new SkillEntry(1, (byte)1, -1L));
               sa.put(SkillFactory.getSkill(20021110), new SkillEntry(1, (byte)1, -1L));
               sa.put(SkillFactory.getSkill(20020111), new SkillEntry(1, (byte)1, -1L));
               sa.put(SkillFactory.getSkill(20020112), new SkillEntry(1, (byte)1, -1L));
               c.getPlayer().changeSkillsLevel(sa);
               break;
            }
            case merTutorSleep01:
               while (c.getPlayer().getLevel() < 10) {
                  c.getPlayer().levelUp();
               }

               c.getPlayer().changeJob(2300);
               showIntro(c, "Effect/Direction5.img/mersedesTutorial/Scene1");
               break;
            case merTutorSleep02:
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(0));
               break;
            case merTutorDrecotion00: {
               c.getSession().writeAndFlush(CField.UIPacket.playMovie("Mercedes.avi", true));
               Map<Skill, SkillEntry> sa = new HashMap<>();
               sa.put(SkillFactory.getSkill(20021181), new SkillEntry(1, (byte)1, -1L));
               sa.put(SkillFactory.getSkill(20021166), new SkillEntry(1, (byte)1, -1L));
               c.getPlayer().changeSkillsLevel(sa);
               break;
            }
            case merTutorDrecotion10:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/6", 2000, 0, -100, 1, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 2000));
               c.getPlayer().setDirection(0);
               break;
            case merTutorDrecotion20:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/9", 2000, 0, -100, 1, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 2000));
               c.getPlayer().setDirection(0);
               break;
            case ds_tuto_ani:
               c.getSession().writeAndFlush(CField.UIPacket.playMovie("DemonSlayer1.avi", true));
               break;
            case Resi_tutor80:
            case startEreb:
            case mirrorCave:
            case babyPigMap:
            case evanleaveD:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
               c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               break;
            case dojang_Msg:
               c.getPlayer().getMap().startMapEffect(mulungEffects[Randomizer.nextInt(mulungEffects.length)], 5120024);
               break;
            case dojang_1st:
               c.getPlayer().writeMulungEnergy();
               break;
            case undomorphdarco:
            case reundodraco:
               c.getPlayer().temporaryStatResetBySkillID(2210016);
               c.getPlayer().temporaryStatResetBySkillID(-2210016);
               break;
            case enter_direction1:
               if (DBConfig.isGanglim
                  && c.getPlayer().getParty() != null
                  && c.getPlayer().getParty().getLeader().isSkipIntro()
                  && !(c.getPlayer().getMap().getFieldSetInstance() instanceof HellDemianBoss)) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setTransferFieldOverlap(true);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() - 60);
               } else {
                  c.getPlayer().send(CField.playSE("Sound/SoundEff.img/BossDemian/phase1"));
                  c.getSession().writeAndFlush(CField.UIPacket.setIngameDirectionMode(false, false, false));
                  c.getSession().writeAndFlush(CField.UIPacket.setVansheeMode(1));
                  c.getSession().writeAndFlush(CField.UIPacket.cameraZoom(0, 1000, 0, -125, -700));
                  c.getPlayer().send(CField.EffectPacket.spineEffect("Map/Effect2.img/DemianIllust/1pahseSp/demian", "animation", 0, 0, 1, 0, ""));
                  c.getPlayer().setTransferFieldOverlap(true);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 8000L);
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() - 60);
               }
               break;
            case enter_direction2:
               if (DBConfig.isGanglim
                  && c.getPlayer().getParty() != null
                  && c.getPlayer().getParty().getLeader().isSkipIntro()
                  && !(c.getPlayer().getMap().getFieldSetInstance() instanceof HellDemianBoss)) {
                  c.getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
                  c.getPlayer().setTransferFieldOverlap(true);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 20);
               } else {
                  c.getPlayer().send(CField.playSE("Sound/SoundEff.img/BossDemian/phase2"));
                  c.getSession().writeAndFlush(CField.UIPacket.setIngameDirectionMode(false, false, false));
                  c.getSession().writeAndFlush(CField.UIPacket.setVansheeMode(1));
                  c.getSession().writeAndFlush(CField.UIPacket.cameraZoom(0, 1000, 0, -125, -700));
                  c.getPlayer().send(CField.EffectPacket.spineEffect("Map/Effect2.img/DemianIllust/2pahseSp/003", "animation", 0, 0, 1, 0, ""));
                  c.getPlayer().setTransferFieldOverlap(true);
                  c.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 13000L);
                  c.getPlayer().setRegisterTransferField(c.getPlayer().getMapId() + 20);
               }
               break;
            case demian_UiOpen:
               c.getPlayer().send(CField.UIPacket.openUI(1115));
               break;
            case goAdventure:
               showIntro(c, "Effect/Direction3.img/goAdventure/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case crash_Dragon:
               showIntro(c, "Effect/Direction4.img/crash/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case getDragonEgg:
               showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case meetWithDragon:
               showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case PromiseDragon:
               showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case evanPromotion:
               switch (c.getPlayer().getMapId()) {
                  case 900090000:
                     data = "Effect/Direction4.img/promotion/Scene0" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                     break;
                  case 900090001:
                     data = "Effect/Direction4.img/promotion/Scene1";
                     break;
                  case 900090002:
                     data = "Effect/Direction4.img/promotion/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                     break;
                  case 900090003:
                     data = "Effect/Direction4.img/promotion/Scene3";
                     break;
                  case 900090004:
                     c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
                     c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     Field mapto = c.getChannelServer().getMapFactory().getMap(900010000);
                     c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                     return;
               }

               showIntro(c, data);
               break;
            case mPark_stageEff:
               c.getPlayer().getMap().setMobGen(false);
               c.getPlayer().dropMessage(-1, "필드 내의 모든 몬스터를 제거해야 다음 스테이지로 이동하실 수 있습니다.");
               switch (c.getPlayer().getMapId() % 1000 / 100) {
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                     c.getSession().writeAndFlush(CField.MapEff("monsterPark/stageEff/stage"));
                     c.getSession().writeAndFlush(CField.MapEff("monsterPark/stageEff/number/" + (c.getPlayer().getMapId() % 1000 / 100 + 1)));
                     return;
                  case 4:
                     if (c.getPlayer().getMapId() / 1000000 == 952) {
                        c.getSession().writeAndFlush(CField.MapEff("monsterPark/stageEff/final"));
                     } else {
                        c.getSession().writeAndFlush(CField.MapEff("monsterPark/stageEff/stage"));
                        c.getSession().writeAndFlush(CField.MapEff("monsterPark/stageEff/number/5"));
                     }

                     return;
                  case 5:
                     c.getSession().writeAndFlush(CField.MapEff("monsterPark/stageEff/final"));
                     return;
                  default:
                     return;
               }
            case TD_MC_title:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
               c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               c.getSession().writeAndFlush(CField.MapEff("temaD/enter/mushCatle"));
               break;
            case TD_NC_title:
               switch (c.getPlayer().getMapId() / 100 % 10) {
                  case 0:
                     c.getSession().writeAndFlush(CField.MapEff("temaD/enter/teraForest"));
                     return;
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                     c.getSession().writeAndFlush(CField.MapEff("temaD/enter/neoCity" + c.getPlayer().getMapId() / 100 % 10));
                     return;
                  default:
                     return;
               }
            case explorationPoint:
               if (c.getPlayer().getMapId() == 104000000) {
                  c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
                  c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  c.getSession().writeAndFlush(CField.MapNameDisplay(c.getPlayer().getMapId()));
               }

               MapleQuest.MedalQuest m = null;

               for (MapleQuest.MedalQuest mq : MapleQuest.MedalQuest.values()) {
                  for (int i : mq.maps) {
                     if (c.getPlayer().getMapId() == i) {
                        m = mq;
                        break;
                     }
                  }
               }

               if (m != null && c.getPlayer().getLevel() >= m.level && c.getPlayer().getQuestStatus(m.questid) != 2) {
                  if (c.getPlayer().getQuestStatus(m.lquestid) != 1) {
                     MapleQuest.getInstance(m.lquestid).forceStart(c.getPlayer(), 0, "0");
                  }

                  if (c.getPlayer().getQuestStatus(m.questid) != 1) {
                     MapleQuest.getInstance(m.questid).forceStart(c.getPlayer(), 0, null);
                     StringBuilder sb = new StringBuilder("enter=");

                     for (int ix = 0; ix < m.maps.length; ix++) {
                        sb.append("0");
                     }

                     c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                     MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, "0");
                  }

                  QuestEx ex = c.getPlayer().getInfoQuest(m.questid - 2005);
                  String quest = "";
                  if (ex != null) {
                     quest = ex.getData();
                  }

                  if (quest.length() != m.maps.length + 6) {
                     StringBuilder sb = new StringBuilder("enter=");

                     for (int ix = 0; ix < m.maps.length; ix++) {
                        sb.append("0");
                     }

                     quest = sb.toString();
                     c.getPlayer().updateInfoQuest(m.questid - 2005, quest);
                  }

                  MapleQuestStatus stat = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(m.questid - 1995));
                  if (stat.getCustomData() == null) {
                     stat.setCustomData("0");
                  }

                  int number = Integer.parseInt(stat.getCustomData());
                  StringBuilder sb = new StringBuilder("enter=");
                  boolean changedd = false;

                  for (int ix = 0; ix < m.maps.length; ix++) {
                     boolean changed = false;
                     if (c.getPlayer().getMapId() == m.maps[ix] && quest.substring(ix + 6, ix + 7).equals("0")) {
                        sb.append("1");
                        changed = true;
                        changedd = true;
                     }

                     if (!changed) {
                        sb.append(quest.substring(ix + 6, ix + 7));
                     }
                  }

                  if (changedd) {
                     number++;
                     c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                     MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, String.valueOf(number));
                     c.getPlayer().dropMessage(-1, number + "/" + m.maps.length + "개 탐험");
                     c.getPlayer().dropMessage(-1, "칭호 - " + m.questname + " 탐험가 도전 중");
                     c.getSession().writeAndFlush(CWvsContext.showQuestMsg("칭호 - " + m.questname + " 탐험가 도전 중. " + number + "/" + m.maps.length + "개 지역 완료"));
                  }
               }
               break;
            case go10000:
            case go1020000:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
               c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            case go20000:
            case go30000:
            case go40000:
            case go50000:
            case go1000000:
            case go2000000:
            case go1010000:
            case go1010100:
            case go1010200:
            case go1010300:
            case go1010400:
               c.getSession().writeAndFlush(CField.MapNameDisplay(c.getPlayer().getMapId()));
               break;
            case ds_tuto_ill0:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 6300));
               showIntro(c, "Effect/Direction6.img/DemonTutorial/SceneLogo");
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
                     c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     Field mapto = c.getChannelServer().getMapFactory().getMap(927000000);
                     c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                  }
               }, 6300L);
               break;
            case ds_tuto_home_before:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 30));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 90));
               c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text11"));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 4000));
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     MapScriptMethods.showIntro(c, "Effect/Direction6.img/DemonTutorial/Scene2");
                  }
               }, 1000L);
               break;
            case ds_tuto_1_0:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 30));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(4, 2159310));
                     NPCScriptManager.getInstance().start(c, 2159310);
                  }
               }, 1000L);
               break;
            case ds_tuto_4_0:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(4, 2159344));
               NPCScriptManager.getInstance().start(c, 2159344);
               break;
            case cannon_tuto_01:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110), 1, (byte)1);
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(4, 1096000));
               NPCScriptManager.getInstance().dispose(c);
               NPCScriptManager.getInstance().start(c, 1096000);
               break;
            case ds_tuto_5_0:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(4, 2159314));
               NPCScriptManager.getInstance().dispose(c);
               NPCScriptManager.getInstance().start(c, 2159314);
               break;
            case ds_tuto_3_0:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 30));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text12"));
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(4, 2159311));
                     NPCScriptManager.getInstance().dispose(c);
                     NPCScriptManager.getInstance().start(c, 2159311);
                  }
               }, 1000L);
               break;
            case ds_tuto_3_1:
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               if (!c.getPlayer().getMap().containsNPC(2159340)) {
                  c.getPlayer().getMap().spawnNpc(2159340, new Point(175, 0));
                  c.getPlayer().getMap().spawnNpc(2159341, new Point(300, 0));
                  c.getPlayer().getMap().spawnNpc(2159342, new Point(600, 0));
               }

               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/tuto/balloonMsg2/0", 2000, 0, -100, 1, 0));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/tuto/balloonMsg1/3", 2000, 0, -100, 1, 0));
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(4, 2159340));
                     NPCScriptManager.getInstance().dispose(c);
                     NPCScriptManager.getInstance().start(c, 2159340);
                  }
               }, 1000L);
               break;
            case ds_tuto_2_before:
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 30));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
                     c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text13"));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 500));
                  }
               }, 1000L);
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text14"));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 4000));
                  }
               }, 1500L);
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     Field mapto = c.getChannelServer().getMapFactory().getMap(927000020);
                     c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                     c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(0));
                     MapleQuest.getInstance(23204).forceStart(c.getPlayer(), 0, null);
                     MapleQuest.getInstance(23205).forceComplete(c.getPlayer(), 0);
                     Map<Skill, SkillEntry> sa = new HashMap<>();
                     sa.put(SkillFactory.getSkill(30011170), new SkillEntry(1, (byte)1, -1L));
                     sa.put(SkillFactory.getSkill(30011169), new SkillEntry(1, (byte)1, -1L));
                     sa.put(SkillFactory.getSkill(30011168), new SkillEntry(1, (byte)1, -1L));
                     sa.put(SkillFactory.getSkill(30011167), new SkillEntry(1, (byte)1, -1L));
                     sa.put(SkillFactory.getSkill(30010166), new SkillEntry(1, (byte)1, -1L));
                     c.getPlayer().changeSkillsLevel(sa);
                  }
               }, 5500L);
               break;
            case ds_tuto_1_before:
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 1));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 30));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
                     c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text8"));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 500));
                  }
               }, 1000L);
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text9"));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 3000));
                  }
               }, 1500L);
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     Field mapto = c.getChannelServer().getMapFactory().getMap(927000010);
                     c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                  }
               }, 4500L);
               break;
            case ds_tuto_0_0: {
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
               Map<Skill, SkillEntry> sa = new HashMap<>();
               sa.put(SkillFactory.getSkill(30011109), new SkillEntry(1, (byte)1, -1L));
               sa.put(SkillFactory.getSkill(30010110), new SkillEntry(1, (byte)1, -1L));
               sa.put(SkillFactory.getSkill(30010111), new SkillEntry(1, (byte)1, -1L));
               sa.put(SkillFactory.getSkill(30010185), new SkillEntry(1, (byte)1, -1L));
               c.getPlayer().changeSkillsLevel(sa);
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 0));
               c.getSession().writeAndFlush(CField.showEffect("demonSlayer/back"));
               c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text0"));
               c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 500));
               c.getPlayer().setDirection(0);
               if (!c.getPlayer().getMap().containsNPC(2159307)) {
                  c.getPlayer().getMap().spawnNpc(2159307, new Point(1305, 50));
               }
               break;
            }
            case ds_tuto_2_prep:
               if (!c.getPlayer().getMap().containsNPC(2159309)) {
                  c.getPlayer().getMap().spawnNpc(2159309, new Point(550, 50));
               }
               break;
            case goArcher:
               showIntro(c, "Effect/Direction3.img/archer/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case goPirate:
               showIntro(c, "Effect/Direction3.img/pirate/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case goRogue:
               showIntro(c, "Effect/Direction3.img/rogue/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case goMagician:
               showIntro(c, "Effect/Direction3.img/magician/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case goSwordman:
               showIntro(c, "Effect/Direction3.img/swordman/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case goLith:
               showIntro(c, "Effect/Direction3.img/goLith/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
               break;
            case TD_MC_Openning:
               showIntro(c, "Effect/Direction2.img/open");
               break;
            case TD_MC_gasi:
               showIntro(c, "Effect/Direction2.img/gasi");
               break;
            case aranDirection:
               switch (c.getPlayer().getMapId()) {
                  case 914090010:
                     data = "Effect/Direction1.img/aranTutorial/Scene0";
                     break;
                  case 914090011:
                     data = "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                     break;
                  case 914090012:
                     data = "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                     break;
                  case 914090013:
                     data = "Effect/Direction1.img/aranTutorial/Scene3";
                     break;
                  case 914090100:
                     data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                     break;
                  case 914090200:
                     data = "Effect/Direction1.img/aranTutorial/Maha";
               }

               showIntro(c, data);
               break;
            case iceCave: {
               Map<Skill, SkillEntry> sa = new HashMap<>();
               sa.put(SkillFactory.getSkill(20000014), new SkillEntry(-1, (byte)0, -1L));
               sa.put(SkillFactory.getSkill(20000015), new SkillEntry(-1, (byte)0, -1L));
               sa.put(SkillFactory.getSkill(20000016), new SkillEntry(-1, (byte)0, -1L));
               sa.put(SkillFactory.getSkill(20000017), new SkillEntry(-1, (byte)0, -1L));
               sa.put(SkillFactory.getSkill(20000018), new SkillEntry(-1, (byte)0, -1L));
               c.getPlayer().changeSkillsLevel(sa);
               WZEffect e = new WZEffect(c.getPlayer().getId(), 0, 0, 0, "Effect/Direction1.img/aranTutorial/ClickLirin");
               c.getSession().writeAndFlush(e.encodeForLocal());
               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
               c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               break;
            }
            case rienArrow:
               if (c.getPlayer().getInfoQuest(21019) == null) {
                  return;
               }

               if (c.getPlayer().getInfoQuest(21019).getData().equals("miss=o;helper=clear")) {
                  c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;helper=clear");
                  WZEffect3 ex = new WZEffect3(-1, "Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
                  c.getSession().writeAndFlush(ex.encodeForLocal());
               }
               break;
            case rien:
               if (c.getPlayer().getQuestStatus(21101) == 2 && c.getPlayer().getInfoQuest(21019).getData().equals("miss=o;arr=o;helper=clear")) {
                  c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;ck=1;helper=clear");
               }

               c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(false));
               c.getSession().writeAndFlush(CField.UIPacket.IntroLock(false));
               break;
            case check_count:
               if (c.getPlayer().getMapId() == 950101010 && (!c.getPlayer().haveItem(4001433, 20) || c.getPlayer().getLevel() < 50)) {
                  Field mapp = c.getChannelServer().getMapFactory().getMap(950101100);
                  c.getPlayer().changeMap(mapp, mapp.getPortal(0));
               }
               break;
            case Massacre_first:
               if (c.getPlayer().getPyramidSubway() == null) {
                  c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
               }
               break;
            case Massacre_result:
               c.getSession().writeAndFlush(CField.showEffect("killing/fail"));
               break;
            case PTtutor000:
               try {
                  c.getSession().writeAndFlush(CField.UIPacket.playMovie("phantom_memory.avi", true));
                  c.getSession().writeAndFlush(CField.showEffect("phantom/mapname1"));
                  c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(1));
                  c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 1));
                  c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 0, 0, -110, 1, 0));
                  Thread.sleep(1300L);
               } catch (InterruptedException var12) {
               }

               c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(false));
               c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(0));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               break;
            case enter_310070140:
               if (c.getPlayer().getQuestStatus(39116) == 1) {
                  c.getPlayer().spawnLocalNpc(2155116, -229, -329, 0, 9, false);
               }
               break;
            case enter_310070220:
               if (c.getPlayer().getQuestStatus(39125) == 1) {
                  c.getPlayer().spawnLocalNpc(2155117, 1413, -457, 0, 4, false);
               }
               break;
            case field_grave_fall_summon:
               if (c.getPlayer().getQuestStatus(39152) == 1) {
                  c.getPlayer().spawnLocalNpc(2155118, 646, -507, 0, 3, false);
               }
               break;
            case karing_first:
               Party party = c.getPlayer().getParty();
               if (party != null) {
                  c.getPlayer().getMap().broadcastMessage(KaringMatching.karingMatchingLoad(party, c.getPlayer()));
               }
               break;
            default:
               MapleNPC npc = MapleLifeFactory.getNPC(9010000);
               ScriptManager.runScript(c, scriptName, npc, null);
         }
      }
   }

   private static final int getTiming(int ids) {
      if (ids <= 5) {
         return 5;
      } else if (ids >= 7 && ids <= 11) {
         return 6;
      } else if (ids >= 13 && ids <= 17) {
         return 7;
      } else if (ids >= 19 && ids <= 23) {
         return 8;
      } else if (ids >= 25 && ids <= 29) {
         return 9;
      } else if (ids >= 31 && ids <= 35) {
         return 10;
      } else {
         return ids >= 37 && ids <= 38 ? 15 : 0;
      }
   }

   private static final int getDojoStageDec(int ids) {
      if (ids <= 5) {
         return 0;
      } else if (ids >= 7 && ids <= 11) {
         return 1;
      } else if (ids >= 13 && ids <= 17) {
         return 2;
      } else if (ids >= 19 && ids <= 23) {
         return 3;
      } else if (ids >= 25 && ids <= 29) {
         return 4;
      } else if (ids >= 31 && ids <= 35) {
         return 5;
      } else {
         return ids >= 37 && ids <= 38 ? 6 : 0;
      }
   }

   private static void showIntro(MapleClient c, String data) {
      c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
      c.getSession().writeAndFlush(CField.UIPacket.IntroLock(true));
      WZEffect e = new WZEffect(c.getPlayer().getId(), 0, 0, 0, data);
      c.getSession().writeAndFlush(e.encodeForLocal());
   }

   private static void sendDojoClock(MapleClient c, int time) {
      c.getSession().writeAndFlush(CField.getClock(time));
   }

   private static void sendDojoStart(MapleClient c, int stage) {
      c.getSession().writeAndFlush(CField.environmentChange("Dojang/start", 4));
      c.getSession().writeAndFlush(CField.environmentChange("dojang/start/stage", 3));
      c.getSession().writeAndFlush(CField.environmentChange("dojang/start/number/" + stage, 3));
      c.getSession().writeAndFlush(CField.trembleEffect(0, 1));
   }

   private static void handlePinkBeanStart(MapleClient c) {
      Field map = c.getPlayer().getMap();
      map.resetFully();
      if (!map.containsNPC(2141000)) {
         map.spawnNpc(2141000, new Point(-190, -42));
      }
   }

   private static void reloadWitchTower(MapleClient c) {
      Field map = c.getPlayer().getMap();
      map.killAllMonsters(false);
      int level = c.getPlayer().getLevel();
      int mob;
      if (level <= 10) {
         mob = 9300367;
      } else if (level <= 20) {
         mob = 9300368;
      } else if (level <= 30) {
         mob = 9300369;
      } else if (level <= 40) {
         mob = 9300370;
      } else if (level <= 50) {
         mob = 9300371;
      } else if (level <= 60) {
         mob = 9300372;
      } else if (level <= 70) {
         mob = 9300373;
      } else if (level <= 80) {
         mob = 9300374;
      } else if (level <= 90) {
         mob = 9300375;
      } else if (level <= 100) {
         mob = 9300376;
      } else {
         mob = 9300377;
      }

      MapleMonster theMob = MapleLifeFactory.getMonster(mob);
      OverrideMonsterStats oms = new OverrideMonsterStats();
      oms.setOMp(theMob.getMobMaxMp());
      oms.setOExp(theMob.getMobExp());
      oms.setOHp((long)Math.ceil(theMob.getMobMaxHp() * (level / 5.0)));
      theMob.setOverrideStats(oms);
      map.spawnMonsterOnGroundBelow(theMob, witchTowerPos);
   }

   public static void startDirectionInfo(MapleCharacter chr, boolean start) {
      final MapleClient c = chr.getClient();
      MapleNodes.DirectionInfo di = chr.getMap().getDirectionInfo(start ? 0 : chr.getDirection());
      if (di != null && di.eventQ.size() > 0) {
         if (start) {
            c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
            c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 4));
         } else {
            for (String s : di.eventQ) {
               switch (MapScriptMethods.directionInfo.fromString(s)) {
                  case merTutorDrecotion01:
                     c.getSession()
                        .writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", 2000, 0, -100, 1, 0));
                     break;
                  case merTutorDrecotion02:
                     c.getSession()
                        .writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", 2000, 0, -100, 1, 0));
                     break;
                  case merTutorDrecotion03:
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 2));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
                     c.getSession()
                        .writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 2000, 0, -100, 1, 0));
                     break;
                  case merTutorDrecotion04:
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 2));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
                     c.getSession()
                        .writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", 2000, 0, -100, 1, 0));
                     break;
                  case merTutorDrecotion05:
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 2));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
                     c.getSession()
                        .writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", 2000, 0, -100, 1, 0));
                     Timer.EventTimer.getInstance()
                        .schedule(
                           new Runnable() {
                              @Override
                              public void run() {
                                 c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 2));
                                 c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
                                 c.getSession()
                                    .writeAndFlush(
                                       CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", 2000, 0, -100, 1, 0)
                                    );
                              }
                           },
                           2000L
                        );
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(0));
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        }
                     }, 4000L);
                     break;
                  case merTutorDrecotion12:
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 2));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
                     c.getSession()
                        .writeAndFlush(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", 2000, 0, -100, 1, 0));
                     c.getSession().writeAndFlush(CField.UIPacket.setInGameDirectionMode(0));
                     break;
                  case merTutorDrecotion21:
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 1));
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionStatus(true));
                     Field mapto = c.getChannelServer().getMapFactory().getMap(910150005);
                     c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                     break;
                  case ds_tuto_0_2:
                     c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text1"));
                     break;
                  case ds_tuto_0_1:
                     c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(3, 2));
                     break;
                  case ds_tuto_0_3:
                     c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text2"));
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 4000));
                           c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text3"));
                        }
                     }, 2000L);
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 500));
                           c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text4"));
                        }
                     }, 6000L);
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 4000));
                           c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text5"));
                        }
                     }, 6500L);
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 500));
                           c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text6"));
                        }
                     }, 10500L);
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 4000));
                           c.getSession().writeAndFlush(CField.showEffect("demonSlayer/text7"));
                        }
                     }, 11000L);
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(4, 2159307));
                           NPCScriptManager.getInstance().dispose(c);
                           NPCScriptManager.getInstance().start(c, 2159307);
                        }
                     }, 15000L);
               }
            }
         }

         c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, 2000));
         chr.setDirection(chr.getDirection() + 1);
         if (chr.getMap().getDirectionInfo(chr.getDirection()) == null) {
            chr.setDirection(-1);
         }
      } else if (start) {
         switch (chr.getMapId()) {
            case 931050300:
               while (chr.getLevel() < 10) {
                  chr.levelUp();
               }

               Field mapto = c.getChannelServer().getMapFactory().getMap(931050000);
               chr.changeMap(mapto, mapto.getPortal(0));
         }
      }
   }

   private static enum directionInfo {
      merTutorDrecotion01,
      merTutorDrecotion02,
      merTutorDrecotion03,
      merTutorDrecotion04,
      merTutorDrecotion05,
      merTutorDrecotion12,
      merTutorDrecotion21,
      ds_tuto_0_1,
      ds_tuto_0_2,
      ds_tuto_0_3,
      NULL;

      private static MapScriptMethods.directionInfo fromString(String Str) {
         try {
            return valueOf(Str);
         } catch (IllegalArgumentException var2) {
            return NULL;
         }
      }
   }

   private static enum onFirstUserEnter {
      first_DemianNormal1,
      first_DemianNormal2,
      first_DemianHard1,
      first_DemianHard2,
      dojang_Eff,
      dojang_Msg,
      PinkBeen_before,
      onRewordMap,
      StageMsg_together,
      StageMsg_crack,
      StageMsg_davy,
      StageMsg_goddess,
      party6weatherMsg,
      StageMsg_juliet,
      StageMsg_romio,
      moonrabbit_mapEnter,
      astaroth_summon,
      boss_Ravana,
      boss_Ravana_mirror,
      killing_BonusSetting,
      killing_MapSetting,
      metro_firstSetting,
      balog_bonusSetting,
      balog_summon,
      easy_balog_summon,
      Sky_TrapFEnter,
      shammos_Fenter,
      PRaid_D_Fenter,
      PRaid_B_Fenter,
      summon_pepeking,
      Xerxes_summon,
      VanLeon_Before,
      cygnus_Summon,
      storymap_scenario,
      shammos_FStart,
      kenta_mapEnter,
      iceman_FEnter,
      iceman_Boss,
      prisonBreak_mapEnter,
      Visitor_Cube_poison,
      Visitor_Cube_Hunting_Enter_First,
      VisitorCubePhase00_Start,
      visitorCube_addmobEnter,
      Visitor_Cube_PickAnswer_Enter_First_1,
      visitorCube_medicroom_Enter,
      visitorCube_iceyunna_Enter,
      Visitor_Cube_AreaCheck_Enter_First,
      visitorCube_boomboom_Enter,
      visitorCube_boomboom2_Enter,
      CubeBossbang_Enter,
      MalayBoss_Int,
      mPark_summonBoss,
      queen_summon0,
      abysscave_ent,
      Fenter_450004150,
      Fenter_450004250,
      Fenter_450004300,
      will_phase1,
      will_phase2,
      will_phase3,
      JinHillah_story_onFirstUserEnter,
      JinHillah_onFirstUserEnter,
      firstenter_bossBlackMageSc,
      firstenter_bossBlackMage,
      dunkel_boss,
      dusk_onFirstUserEnter,
      enter_993189101,
      blackHeavenBoss1_summon,
      blackHeavenBoss2_summon,
      blackHeavenBoss3_summon,
      blackHeavenBoss1n_summon,
      blackHeavenBoss2n_summon,
      blackHeavenBoss3n_summon,
      first_SerenNormal1,
      first_SerenNormal2,
      first_SerenNormal3,
      first_SerenHard1,
      first_SerenHard2,
      first_SerenHard3,
      first_SlimeChaos1,
      first_SlimeNormal1,
      first_SlimeChaos2,
      first_SlimeNormal2,
      first_kalos1,
      first_kalos2,
      first_kalos3,
      first_kalos1_easy,
      first_kalos2_easy,
      first_kalos3_easy,
      first_kalos1_chaos,
      first_kalos2_chaos,
      first_kalos3_chaos,
      first_kalos1_ex,
      first_kalos2_ex,
      first_kalos3_ex,
      first_goongi,
      NULL;

      private static MapScriptMethods.onFirstUserEnter fromString(String Str) {
         try {
            return valueOf(Str);
         } catch (IllegalArgumentException var2) {
            return NULL;
         }
      }
   }

   private static enum onUserEnter {
      enter_direction1,
      enter_direction2,
      demian_UiOpen,
      demianDirectionClear,
      babyPigMap,
      crash_Dragon,
      evanleaveD,
      getDragonEgg,
      meetWithDragon,
      go1010100,
      go1010200,
      go1010300,
      go1010400,
      evanPromotion,
      PromiseDragon,
      evanTogether,
      incubation_dragon,
      TD_MC_Openning,
      TD_MC_gasi,
      TD_MC_title,
      cygnusJobTutorial,
      cygnusTest,
      startEreb,
      dojang_Msg,
      dojang_1st,
      reundodraco,
      undomorphdarco,
      explorationPoint,
      goAdventure,
      go10000,
      go20000,
      go30000,
      go40000,
      go50000,
      go1000000,
      go1010000,
      go1020000,
      go2000000,
      goArcher,
      goPirate,
      goRogue,
      goMagician,
      goSwordman,
      goLith,
      iceCave,
      mirrorCave,
      aranDirection,
      rienArrow,
      rien,
      check_count,
      Massacre_first,
      Massacre_result,
      aranTutorAlone,
      evanAlone,
      dojang_QcheckSet,
      Sky_StageEnter,
      outCase,
      balog_buff,
      balog_dateSet,
      Sky_BossEnter,
      Sky_GateMapEnter,
      shammos_Enter,
      shammos_Result,
      shammos_Base,
      dollCave00,
      dollCave01,
      dollCave02,
      Sky_Quest,
      enterBlackfrog,
      onSDI,
      blackSDI,
      summonIceWall,
      metro_firstSetting,
      start_itemTake,
      findvioleta,
      pepeking_effect,
      TD_MC_keycheck,
      TD_MC_gasi2,
      in_secretroom,
      sealGarden,
      TD_NC_title,
      TD_neo_BossEnter,
      PRaid_D_Enter,
      PRaid_B_Enter,
      PRaid_Revive,
      PRaid_W_Enter,
      PRaid_WinEnter,
      PRaid_FailEnter,
      Resi_tutor10,
      Resi_tutor20,
      Resi_tutor30,
      Resi_tutor40,
      Resi_tutor50,
      Resi_tutor60,
      Resi_tutor70,
      Resi_tutor80,
      Resi_tutor50_1,
      summonSchiller,
      q31102e,
      q31103s,
      jail,
      VanLeon_ExpeditionEnter,
      cygnus_ExpeditionEnter,
      knights_Summon,
      TCMobrevive,
      mPark_stageEff,
      moonrabbit_takeawayitem,
      StageMsg_crack,
      shammos_Start,
      iceman_Enter,
      prisonBreak_1stageEnter,
      VisitorleaveDirectionMode,
      visitorPT_Enter,
      VisitorCubePhase00_Enter,
      visitor_ReviveMap,
      cannon_tuto_01,
      cannon_tuto_direction,
      cannon_tuto_direction1,
      cannon_tuto_direction2,
      userInBattleSquare,
      merTutorDrecotion00,
      merTutorDrecotion10,
      merTutorDrecotion20,
      merStandAlone,
      merOutStandAlone,
      merTutorSleep00,
      merTutorSleep01,
      merTutorSleep02,
      EntereurelTW,
      ds_tuto_ill0,
      ds_tuto_0_0,
      ds_tuto_1_0,
      ds_tuto_3_0,
      ds_tuto_3_1,
      ds_tuto_4_0,
      ds_tuto_5_0,
      ds_tuto_2_prep,
      ds_tuto_1_before,
      ds_tuto_2_before,
      ds_tuto_home_before,
      ds_tuto_ani,
      PTtutor000,
      enter_450004100,
      enter_450004150,
      enter_450004200,
      slime_direction1,
      slime_direction2,
      will_direction1,
      will_direction2,
      will_direction3,
      will_phase1_everyone,
      will_phase2_everyone,
      will_phase3_everyone,
      JinHillah_direction1,
      JinHillah_onUserEnter,
      JinHillah_story_onUserEnter,
      bossBlackMage_directionSc,
      bossBlackMage_direction,
      enter_bossBlackMageSc,
      enter_bossBlackMage,
      bossBlackMage_directionFail,
      dunkel_enter,
      dusk_timeRecord,
      seren_direction1,
      seren_direction2,
      enter_100,
      enter_101,
      enter_102,
      enter_103,
      enter_104,
      enter_105,
      enter_110,
      enter_111,
      enter_120,
      enter_121,
      enter_993189101,
      enter_993189102,
      enter_993189103,
      enter_680000710,
      enter_410000300,
      enter_310070140,
      enter_310070220,
      enter_993210000,
      field_grave_fall_summon,
      karing_first,
      hondon_direction,
      goongi_direction,
      dool_direction,
      hondon_direction_st,
      goongi_direction_st,
      dool_direction_st,
      NULL;

      private static MapScriptMethods.onUserEnter fromString(String Str) {
         try {
            return valueOf(Str);
         } catch (IllegalArgumentException var2) {
            return NULL;
         }
      }
   }
}
