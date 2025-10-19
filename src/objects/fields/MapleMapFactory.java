package objects.fields;

import constants.GameConstants;
import database.DBConfig;
import database.DBConnection;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import network.center.RebirthRank;
import objects.fields.child.arkarium.Field_Arkaium;
import objects.fields.child.blackheaven.Field_BlackHeavenBoss;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase1;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase2;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase3;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase4;
import objects.fields.child.cygnus.Field_Cygnus;
import objects.fields.child.demian.Field_Demian;
import objects.fields.child.dojang.DojangRanking;
import objects.fields.child.dojang.Field_Dojang;
import objects.fields.child.dreambreaker.Field_DreamBreaker;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.child.etc.Field_DamageMeasurement;
import objects.fields.child.etc.Field_EventRabbit;
import objects.fields.child.etc.Field_EventSnowman;
import objects.fields.child.etc.Field_MMRace;
import objects.fields.child.fritto.Field_CourtshipDance;
import objects.fields.child.fritto.Field_EagleHunt;
import objects.fields.child.fritto.Field_ReceivingTreasure;
import objects.fields.child.fritto.Field_StealDragonsEgg;
import objects.fields.child.hillah.Field_Hillah;
import objects.fields.child.horntail.Field_Horntail;
import objects.fields.child.jinhillah.Field_JinHillah;
import objects.fields.child.karing.Field_BossDoolPhase;
import objects.fields.child.karing.Field_BossGoongiPhase;
import objects.fields.child.karing.Field_BossHondonPhase;
import objects.fields.child.karing.Field_BossKaringMatch;
import objects.fields.child.karing.Field_BossKaringPhase2;
import objects.fields.child.karing.Field_BossKaringPhase3;
import objects.fields.child.karrotte.Field_BossKalosPhase1;
import objects.fields.child.karrotte.Field_BossKalosPhase2;
import objects.fields.child.lucid.Field_LucidBattlePhase1;
import objects.fields.child.lucid.Field_LucidBattlePhase2;
import objects.fields.child.magnus.Field_Magnus;
import objects.fields.child.minigame.battlereverse.Field_BattleReverse;
import objects.fields.child.minigame.battlereverse.Field_BattleReverseWait;
import objects.fields.child.minigame.onecard.Field_OneCard;
import objects.fields.child.minigame.onecard.Field_OneCardWait;
import objects.fields.child.minigame.rail.Field_ExtremeRail;
import objects.fields.child.minigame.soccer.Field_MultiSoccer;
import objects.fields.child.minigame.soccer.Field_MultiSoccerWait;
import objects.fields.child.minigame.space.Field_Mission2Space;
import objects.fields.child.minigame.topgame.Field_BuzzingHouse;
import objects.fields.child.minigame.train.Field_TrainMaster;
import objects.fields.child.minigame.train.Field_TrainMasterWait;
import objects.fields.child.minigame.yutgame.Field_MultiYutGame;
import objects.fields.child.minigame.yutgame.Field_MultiYutGameWait;
import objects.fields.child.moonbridge.Field_FerociousBattlefield;
import objects.fields.child.papulatus.Field_Papulatus;
import objects.fields.child.pinkbeen.Field_Pinkbeen;
import objects.fields.child.pollo.Field_BountyHunting;
import objects.fields.child.pollo.Field_FlameWolf;
import objects.fields.child.pollo.Field_MidnightMonsterHunting;
import objects.fields.child.pollo.Field_StormwingArea;
import objects.fields.child.pollo.Field_TownDefense;
import objects.fields.child.rimen.Field_RimenNearTheEnd;
import objects.fields.child.rootabyss.Field_CrimsonQueen;
import objects.fields.child.rootabyss.Field_Pierre;
import objects.fields.child.rootabyss.Field_Vellum;
import objects.fields.child.rootabyss.Field_Vonbon;
import objects.fields.child.sernium.Field_SerenPhase1;
import objects.fields.child.sernium.Field_SerenPhase2;
import objects.fields.child.slime.Field_GuardianAngelSlime;
import objects.fields.child.union.Field_UnionRaid;
import objects.fields.child.vonleon.Field_VonLeon;
import objects.fields.child.will.Field_WillBattlePhase1;
import objects.fields.child.will.Field_WillBattlePhase2;
import objects.fields.child.will.Field_WillBattlePhase3;
import objects.fields.child.will.Field_WillBattleReward;
import objects.fields.child.yutagolden.Field_YutaGolden;
import objects.fields.child.zakum.Field_Zakum;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.ReactorFactory;
import objects.fields.gameobject.lifes.AbstractLoadedMapleLife;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class MapleMapFactory {
   private final MapleDataProvider source = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Map.wz"));
   private final MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"))
      .getData("Map.img");
   private final ConcurrentHashMap<Integer, Field> maps = new ConcurrentHashMap<>();
   private final HashMap<Integer, Field> instanceMap = new HashMap<>();
   private final HashMap<Integer, Integer> fieldGeneratorMap = new HashMap<>();
   private final ReentrantLock lock = new ReentrantLock();
   private int channel;

   public MapleMapFactory(int channel) {
      this.channel = channel;
      this.loadFieldGenerator();
   }

   private void loadFieldGenerator() {
      MapleData md = this.source.getData("FieldGenerator.img");
      Consumer<MapleData> loader = data -> {
         int template = MapleDataTool.getInt("template", data, -1);
         int base = MapleDataTool.getInt("baseMapID", data, -1);
         int term = MapleDataTool.getInt("term", data, -1);
         int count = MapleDataTool.getInt("count", data, -1);

         for (int i = base; i < base + term * count; i += term) {
            this.fieldGeneratorMap.put(i, template);
         }
      };
      md.getChildren().forEach(loader);
   }

   public final Field getMap(int mapid) {
      return this.getMap(mapid, true, true, true);
   }

   public final Field getMap(int mapid, boolean respawns, boolean npcs) {
      return this.getMap(mapid, respawns, npcs, true);
   }

   public final Field getMap(int mapid, boolean respawns, boolean npcs, boolean reactors) {
      Integer omapid = mapid;
      Field map = this.maps.get(omapid);
      if (map == null) {
         this.lock.lock();

         Object link;
         try {
            map = this.maps.get(omapid);
            if (map != null) {
               return map;
            }

            Integer fm = this.fieldGeneratorMap.get(mapid);
            if (fm != null) {
               mapid = fm;
            }

            MapleData mapData = null;

            try {
               mapData = this.source.getData(this.getMapName(mapid));
            } catch (Exception var48) {
               return null;
            }

            if (mapData != null) {
               MapleData linkx = mapData.getChildByPath("info/link");
               if (linkx != null) {
                  mapData = this.source.getData(this.getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
               }

               float monsterRate = 0.0F;
               if (respawns) {
                  MapleData mobRate = mapData.getChildByPath("info/mobRate");
                  if (mobRate != null) {
                     monsterRate = (Float)mobRate.getData();
                  }
               }

               int fieldType = MapleDataTool.getInt(mapData.getChildByPath("info/fieldType"), 0);
               int returnMap = MapleDataTool.getInt("info/returnMap", mapData);
               FieldType fType = FieldType.getByType(fieldType);
               if (fType != null && fType != FieldType.Default) {
                  switch (fType) {
                     case Demian:
                        map = new Field_Demian(mapid, this.channel, returnMap, monsterRate, mapData);
                        break;
                     case LucidBattlePhase1:
                        map = new Field_LucidBattlePhase1(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case LucidBattlePhase2:
                        map = new Field_LucidBattlePhase2(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case DreamBreaker:
                        map = new Field_DreamBreaker(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case WillBattlePhase1:
                        map = new Field_WillBattlePhase1(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case WillBattlePhase2:
                        map = new Field_WillBattlePhase2(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case WillBattlePhase3:
                        map = new Field_WillBattlePhase3(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case JinHillah:
                        map = new Field_JinHillah(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case BlackMageBattlePhase1:
                        map = new Field_BlackMageBattlePhase1(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case BlackMageBattlePhase2:
                        map = new Field_BlackMageBattlePhase2(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case BlackMageBattlePhase3:
                        map = new Field_BlackMageBattlePhase3(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case BlackMageBattlePhase4:
                        map = new Field_BlackMageBattlePhase4(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case Fritto_ReceivingTreasure:
                        map = new Field_ReceivingTreasure(mapid, this.channel, returnMap, monsterRate, mapData.getChildByPath("pocketdrop"));
                        break;
                     case PoloFritoEagleHunting:
                        map = new Field_EagleHunt(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case PoloFritoStealDragonsEgg:
                        map = new Field_StealDragonsEgg(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case PoloFritoCourtshipDance:
                        map = new Field_CourtshipDance(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case StormwingArea:
                        map = new Field_StormwingArea(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case PoloFritoTownDefense:
                        map = new Field_TownDefense(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case PoloFritoBountyHunting:
                        map = new Field_BountyHunting(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case Fritto_MidnightMonsterHunting:
                        map = new Field_MidnightMonsterHunting(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case PoloFritoFlameWolf:
                        map = new Field_FlameWolf(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case Dojang:
                        map = new Field_Dojang(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case RimenNearTheEnd:
                        map = new Field_RimenNearTheEnd(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case FerociousBattlefield:
                     case EyeOfVoid:
                        map = new Field_FerociousBattlefield(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case Papulatus:
                        map = new Field_Papulatus(
                           mapid, this.channel, returnMap, monsterRate, MapleDataTool.getString(mapData.getChildByPath("info/difficulty"), "easy"), mapData
                        );
                        break;
                     case Zakum:
                        map = new Field_Zakum(mapid, this.channel, returnMap, monsterRate, mapData);
                        break;
                     case NewtroYut:
                        map = new Field_MultiYutGame(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case BuzzingHouse:
                        map = new Field_BuzzingHouse(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case Mission2Space:
                        map = new Field_Mission2Space(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case ExtremeRail:
                        map = new Field_ExtremeRail(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case Pierre:
                        map = new Field_Pierre(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case BanBan:
                        map = new Field_Vonbon(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case Bellum:
                        map = new Field_Vellum(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case BlackHeaven_Boss:
                        map = new Field_BlackHeavenBoss(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case MapleSoccer:
                        map = new Field_MultiSoccer(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case ArenaBattleStatistics:
                        map = new Field_DamageMeasurement(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case PinkBean:
                        map = new Field_Pinkbeen(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case SerenPhase1:
                        map = new Field_SerenPhase1(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case SerenPhase2:
                        map = new Field_SerenPhase2(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case GuardianAngelSlime:
                        map = new Field_GuardianAngelSlime(mapid, this.channel, returnMap, monsterRate, mapData);
                        break;
                     case KalosPhase1:
                        map = new Field_BossKalosPhase1(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case KalosPhase2:
                        map = new Field_BossKalosPhase2(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case UnionRaid:
                        map = new Field_UnionRaid(mapid, this.channel, returnMap, monsterRate);
                        break;
                     default:
                        map = new Field(mapid, this.channel, returnMap, monsterRate);
                  }
               } else {
                  switch (mapid) {
                     case 105200310:
                     case 105200710:
                        map = new Field_CrimsonQueen(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 209000001:
                        map = new Field_EventSnowman(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 211070100:
                        map = new Field_VonLeon(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 240060200:
                     case 240060201:
                     case 240060300:
                        map = new Field_Horntail(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 262030300:
                        map = new Field_Hillah(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 270050100:
                        map = new Field_Pinkbeen(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 271040100:
                     case 271041100:
                        map = new Field_Cygnus(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 272020200:
                     case 272020210:
                        map = new Field_Arkaium(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 350060650:
                     case 350060950:
                        map = new Field_BlackHeavenBoss(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 350160180:
                     case 350160280:
                        map = new Field_Demian(mapid, this.channel, returnMap, monsterRate, mapData);
                        break;
                     case 401060100:
                     case 401060200:
                     case 401060300:
                        map = new Field_Magnus(mapid, this.channel, returnMap, monsterRate, mapData);
                        break;
                     case 410007100:
                        map = new Field_BossKaringMatch(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 410007140:
                        map = new Field_BossGoongiPhase(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 410007180:
                        map = new Field_BossDoolPhase(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 410007220:
                        map = new Field_BossHondonPhase(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 410007260:
                        map = new Field_BossKaringPhase2(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 410007300:
                        map = new Field_BossKaringPhase3(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 450008080:
                     case 450008380:
                     case 450008980:
                        map = new Field_WillBattleReward(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 450009430:
                     case 450009480:
                        map = new Field_FerociousBattlefield(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 450010550:
                     case 450010950:
                        map = new Field_JinHillah(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 450012250:
                     case 450012650:
                        map = new Field_RimenNearTheEnd(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 910010000:
                        map = new Field_EventRabbit(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 926010100:
                        map = new Field_MMRace(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993033200:
                        map = new Field_YutaGolden(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993189400:
                        map = new Field_OneCardWait(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993189500:
                        map = new Field_OneCard(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993189600:
                        map = new Field_BattleReverseWait(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993189700:
                        map = new Field_BattleReverse(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993189800:
                        map = new Field_MultiYutGameWait(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993195100:
                        map = new Field_MultiSoccerWait(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993195300:
                        map = new Field_TrainMasterWait(mapid, this.channel, returnMap, monsterRate);
                        break;
                     case 993195400:
                        map = new Field_TrainMaster(mapid, this.channel, returnMap, monsterRate);
                        break;
                     default:
                        map = new Field(mapid, this.channel, returnMap, monsterRate);
                  }
               }

               this.loadPortals(map, mapData.getChildByPath("portal"));
               map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
               map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
               map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
               map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
               map.setNeedStarForce(MapleDataTool.getInt(mapData.getChildByPath("info/barrier"), 0));
               map.setNeedAuthenticForce(MapleDataTool.getInt(mapData.getChildByPath("info/barrierAut"), 0));
               map.setNeedArcaneForce(MapleDataTool.getInt(mapData.getChildByPath("info/barrierArc"), 0));
               map.setVrlimit(MapleDataTool.getInt("VRLimit", mapData, 0) == 1);
               map.loadDynamicObjects(mapData);
               List<MapleFoothold> allFootholds = new LinkedList<>();
               Point lBound = new Point();
               Point uBound = new Point();

               for (MapleData footRoot : mapData.getChildByPath("foothold")) {
                  for (MapleData footCat : footRoot) {
                     for (MapleData footHold : footCat) {
                        MapleFoothold fh = new MapleFoothold(
                           new Point(MapleDataTool.getInt(footHold.getChildByPath("x1"), 0), MapleDataTool.getInt(footHold.getChildByPath("y1"), 0)),
                           new Point(MapleDataTool.getInt(footHold.getChildByPath("x2"), 0), MapleDataTool.getInt(footHold.getChildByPath("y2"), 0)),
                           Integer.parseInt(footHold.getName())
                        );
                        fh.setPrev((short)MapleDataTool.getInt(footHold.getChildByPath("prev"), 0));
                        fh.setNext((short)MapleDataTool.getInt(footHold.getChildByPath("next"), 0));
                        if (fh.getX1() < lBound.x) {
                           lBound.x = fh.getX1();
                        }

                        if (fh.getX2() > uBound.x) {
                           uBound.x = fh.getX2();
                        }

                        if (fh.getY1() < lBound.y) {
                           lBound.y = fh.getY1();
                        }

                        if (fh.getY2() > uBound.y) {
                           uBound.y = fh.getY2();
                        }

                        allFootholds.add(fh);
                     }
                  }
               }

               MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);

               for (MapleFoothold foothold : allFootholds) {
                  fTree.insert(foothold);
               }

               map.setFootholds(fTree);
               if (map.getTop() == 0) {
                  map.setTop(lBound.y);
               }

               if (map.getBottom() == 0) {
                  map.setBottom(uBound.y);
               }

               if (map.getLeft() == 0) {
                  map.setLeft(lBound.x);
               }

               if (map.getRight() == 0) {
                  map.setRight(uBound.x);
               }

               int bossid = -1;
               String msg = null;
               if (mapData.getChildByPath("info/timeMob") != null) {
                  bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
                  msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
               }

               List<Point> herbRocks = new ArrayList<>();
               int lowestLevel = 200;
               int highestLevel = 0;

               for (MapleData life : mapData.getChildByPath("life")) {
                  String type = MapleDataTool.getString(life.getChildByPath("type"));
                  String limited = MapleDataTool.getString("limitedname", life, "");
                  if ((npcs || !type.equals("n")) && !limited.equals("Stage0")) {
                     AbstractLoadedMapleLife myLife = this.loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);
                     if (myLife instanceof MapleMonster && !GameConstants.isNoSpawn(mapid)) {
                        MapleMonster mob = (MapleMonster)myLife;
                        herbRocks.add(
                           map.addMonsterSpawn(
                                 mob,
                                 MapleDataTool.getInt("mobTime", life, 0),
                                 (byte)MapleDataTool.getInt("team", life, -1),
                                 mob.getId() == bossid ? msg : null
                              )
                              .getPosition()
                        );
                        if (mob.getStats().getLevel() > highestLevel && !mob.getStats().isBoss()) {
                           highestLevel = mob.getStats().getLevel();
                        }

                        if (mob.getStats().getLevel() < lowestLevel && !mob.getStats().isBoss()) {
                           lowestLevel = mob.getStats().getLevel();
                        }
                     } else if (myLife instanceof MapleNPC) {
                        map.addMapObject(myLife);
                     }
                  }
               }

               this.addAreaBossSpawn(map);
               map.setCreateMobInterval((short)MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 3500));
               map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
               map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
               map.loadMonsterRate(true);
               map.setNodes(this.loadNodes(mapid, mapData));
               if (reactors && mapData.getChildByPath("reactor") != null) {
                  for (MapleData reactor : mapData.getChildByPath("reactor")) {
                     String id = MapleDataTool.getString(reactor.getChildByPath("id"));
                     if (id != null) {
                        map.spawnReactor(this.loadReactor(reactor, id, (byte)MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                     }
                  }
               }

               map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
               map.setUserEnter(mapid == 180000002 ? "jail" : MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
               if (!map.isTown()) {
                  List<Integer> allowedSpawn = new ArrayList<>();
                  if (!FieldLimitType.ChannelSwitch.check(map.getFieldLimit())) {
                     int[] list = new int[]{100011, 100012, 100013, 200011, 200012, 200013};
                     allowedSpawn.add(list[Randomizer.nextInt(list.length)]);
                     int numSpawn = allowedSpawn.size();

                     for (int i = 0; i < numSpawn && !herbRocks.isEmpty(); i++) {
                        int idd = allowedSpawn.get(Randomizer.nextInt(allowedSpawn.size()));
                        int theSpawn = Randomizer.nextInt(herbRocks.size());
                        Reactor myReactor = new Reactor(ReactorFactory.getReactor(idd), idd);
                        myReactor.setPosition(herbRocks.get(theSpawn));
                        myReactor.setDelay((idd % 100 >= 11 ? '\uea60' : 5000) * 1000);
                        map.spawnReactor(myReactor);
                        herbRocks.remove(theSpawn);
                     }
                  }
               }

               DBConnection db = new DBConnection();

               try (Connection con = DBConnection.getConnection()) {
                  String sql = "SELECT * FROM `spawn` WHERE mapid = " + mapid + " AND type = 'n'";
                  PreparedStatement ps = con.prepareStatement(sql);
                  ResultSet rs = ps.executeQuery();

                  while (rs.next()) {
                     int npcid = rs.getInt("lifeid");
                     MapleNPC npc = MapleLifeFactory.getNPC(npcid);
                     if (npc == null) {
                        System.err.println("[오류] 존재하지 않는 NPCID : " + npcid);
                     } else {
                        npc.setRx0(rs.getInt("rx0"));
                        npc.setRx1(rs.getInt("rx1"));
                        npc.setCy(rs.getInt("cy"));
                        npc.setF(rs.getInt("dir"));
                        npc.setFh(rs.getInt("fh"));
                        npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                        if (npc != null) {
                           map.addMapObject(npc);
                        }
                     }
                  }

                  ps.close();
                  rs.close();
               } catch (Exception var47) {
                  System.err.println("[오류] 엔피시를 DB로부터 불러오는데 오류가 발생했습니다.");
                  var47.printStackTrace();
               }

               try {
                  map.setMapName(MapleDataTool.getString("mapName", this.nameData.getChildByPath(this.getMapStringName(mapid)), ""));
                  map.setStreetName(MapleDataTool.getString("streetName", this.nameData.getChildByPath(this.getMapStringName(mapid)), ""));
               } catch (Exception var44) {
                  map.setMapName("");
                  map.setStreetName("");
               }

               map.setClock(mapData.getChildByPath("clock") != null);
               map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
               map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
               map.setReviveCurFieldOfNoTransfer(MapleDataTool.getInt(mapData.getChildByPath("info/ReviveCurFieldOfNoTransfer"), 0) > 0);
               map.setReviveCurFieldOfNoTransferPoint(MapleDataTool.getPoint("info/ReviveCurFieldOfNoTransferPoint", mapData, new Point(0, 0)));
               map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
               map.setPersonalShop(MapleDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0);
               map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
               map.setLevelLimit(MapleDataTool.getInt(mapData.getChildByPath("info/lvLimit"), 0));
               map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
               map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
               map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
               map.setForcedReturnMap(mapid == 0 ? 999999999 : MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
               map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
               map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
               map.setMode(MapleDataTool.getString(mapData.getChildByPath("info/mode"), ""));
               map.setFieldType(fieldType);
               map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0F));
               map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
               map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
               map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
               FieldMonsterSpawner spawner = FieldMonsterSpawner.getSpawner(mapid);
               if (spawner != null) {
                  map.addFieldMonsterSpawner(spawner);
               }

               if (DBConfig.isGanglim) {
                  if (mapid == 450004000) {
                     MapleNPC npc = MapleLifeFactory.getNPC(3003208);
                     npc.setRx0(254);
                     npc.setRx1(304);
                     npc.setCy(-50);
                     npc.setF(0);
                     npc.setFh(1);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9010107);
                     npc.setRx0(-597);
                     npc.setRx1(-497);
                     npc.setCy(-26);
                     npc.setF(0);
                     npc.setFh(27);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9010106);
                     npc.setRx0(-724);
                     npc.setRx1(-624);
                     npc.setCy(-26);
                     npc.setF(0);
                     npc.setFh(28);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9090000);
                     npc.setRx0(53);
                     npc.setRx1(153);
                     npc.setCy(-21);
                     npc.setF(0);
                     npc.setFh(23);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9900002);
                     npc.setRx0(-351);
                     npc.setRx1(-251);
                     npc.setCy(321);
                     npc.setF(1);
                     npc.setFh(2);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9076004);
                     npc.setRx0(158);
                     npc.setRx1(258);
                     npc.setCy(-21);
                     npc.setF(0);
                     npc.setFh(25);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(3004823);
                     npc.setRx0(-73);
                     npc.setRx1(27);
                     npc.setCy(-21);
                     npc.setF(0);
                     npc.setFh(22);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9000178);
                     npc.setRx0(175);
                     npc.setRx1(275);
                     npc.setCy(321);
                     npc.setF(0);
                     npc.setFh(4);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(1052013);
                     npc.setRx0(826);
                     npc.setRx1(926);
                     npc.setCy(-88);
                     npc.setF(0);
                     npc.setFh(19);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     try {
                        PlayerNPC npcc = RebirthRank.numberOneNPC;
                        MapleCharacter player = MapleCharacter.loadCharFromDBFake(npcc.getCharId(), true);
                        PlayerNPC npc = new PlayerNPC(player, 9901001, 993165543);
                        if (npc != null) {
                           map.addMapObject(npc);
                        } else {
                           System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                        }
                     } catch (Exception var43) {
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9062550);
                     npc.setRx0(-1066);
                     npc.setRx1(-966);
                     npc.setCy(321);
                     npc.setF(1);
                     npc.setFh(8);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }

                  if (mapid == 993165543) {
                     MapleNPC npc = MapleLifeFactory.getNPC(9062461);
                     npc.setRx0(-915);
                     npc.setRx1(-815);
                     npc.setCy(321);
                     npc.setF(1);
                     npc.setFh(7);
                     npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                     if (npc != null) {
                        map.addMapObject(npc);
                     } else {
                        System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                     }
                  }
               } else {
                  if (mapid == 680000710) {
                     PlayerNPC npcc = DamageMeasurementRank.numberOneNPC;
                     if (npcc != null) {
                        MapleCharacter player = MapleCharacter.loadCharFromDBFake(npcc.getCharId(), true);
                        PlayerNPC npc = new PlayerNPC(player, 9901001, 680000710);
                        if (npc != null) {
                           map.addMapObject(npc);
                        } else {
                           System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                        }
                     }
                  }

                  if (mapid == 680000710) {
                     PlayerNPC npcc = DojangRanking.numberOneNPC;
                     if (npcc != null) {
                        MapleCharacter player = MapleCharacter.loadCharFromDBFake(npcc.getCharId(), true);
                        PlayerNPC npc = new PlayerNPC(player, 9901002, 680000710);
                        npc.setCoords(-1097, -220, 0, 32);
                        if (npc != null) {
                           map.addMapObject(npc);
                        } else {
                           System.err.println("[오류] 엔피시 데이터를 만드는중 널 포인터 오류가 발생했습니다.");
                        }
                     }
                  }
               }

               this.maps.put(omapid, map);
               return map;
            }

            link = null;
         } finally {
            this.lock.unlock();
         }

         return (Field)link;
      } else {
         return map;
      }
   }

   public Field getInstanceMap(int instanceid) {
      return this.instanceMap.get(instanceid);
   }

   public void removeInstanceMap(int instanceid) {
      this.lock.lock();

      try {
         if (this.isInstanceMapLoaded(instanceid)) {
            this.getInstanceMap(instanceid).checkStates("");
            this.instanceMap.remove(instanceid);
         }
      } finally {
         this.lock.unlock();
      }
   }

   public void removeMap(int instanceid) {
      this.lock.lock();

      try {
         if (this.isMapLoaded(instanceid)) {
            this.getMap(instanceid).checkStates("");
            this.maps.remove(instanceid);
         }
      } finally {
         this.lock.unlock();
      }
   }

   public Field CreateInstanceMap(int mapid, boolean respawns, boolean npcs, boolean reactors, int instanceid) {
      this.lock.lock();

      try {
         if (this.isInstanceMapLoaded(instanceid)) {
            return this.getInstanceMap(instanceid);
         }
      } finally {
         this.lock.unlock();
      }

      MapleData mapData = null;

      try {
         mapData = this.source.getData(this.getMapName(mapid));
      } catch (Exception var34) {
         return null;
      }

      if (mapData == null) {
         return null;
      } else {
         MapleData link = mapData.getChildByPath("info/link");
         if (link != null) {
            mapData = this.source.getData(this.getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
         }

         float monsterRate = 0.0F;
         if (respawns) {
            MapleData mobRate = mapData.getChildByPath("info/mobRate");
            if (mobRate != null) {
               monsterRate = (Float)mobRate.getData();
            }
         }

         int fieldType = MapleDataTool.getInt(mapData.getChildByPath("info/fieldType"), 0);
         Field map = new Field(mapid, this.channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);
         this.loadPortals(map, mapData.getChildByPath("portal"));
         map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
         map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
         map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
         map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
         List<MapleFoothold> allFootholds = new LinkedList<>();
         Point lBound = new Point();
         Point uBound = new Point();

         for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            for (MapleData footCat : footRoot) {
               for (MapleData footHold : footCat) {
                  MapleFoothold fh = new MapleFoothold(
                     new Point(MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))),
                     new Point(MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))),
                     Integer.parseInt(footHold.getName())
                  );
                  fh.setPrev((short)MapleDataTool.getInt(footHold.getChildByPath("prev")));
                  fh.setNext((short)MapleDataTool.getInt(footHold.getChildByPath("next")));
                  if (fh.getX1() < lBound.x) {
                     lBound.x = fh.getX1();
                  }

                  if (fh.getX2() > uBound.x) {
                     uBound.x = fh.getX2();
                  }

                  if (fh.getY1() < lBound.y) {
                     lBound.y = fh.getY1();
                  }

                  if (fh.getY2() > uBound.y) {
                     uBound.y = fh.getY2();
                  }

                  allFootholds.add(fh);
               }
            }
         }

         MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);

         for (MapleFoothold fhx : allFootholds) {
            fTree.insert(fhx);
         }

         map.setFootholds(fTree);
         if (map.getTop() == 0) {
            map.setTop(lBound.y);
         }

         if (map.getBottom() == 0) {
            map.setBottom(uBound.y);
         }

         if (map.getLeft() == 0) {
            map.setLeft(lBound.x);
         }

         if (map.getRight() == 0) {
            map.setRight(uBound.x);
         }

         int bossid = -1;
         String msg = null;
         if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
         }

         for (MapleData life : mapData.getChildByPath("life")) {
            String type = MapleDataTool.getString(life.getChildByPath("type"));
            String limited = MapleDataTool.getString("limitedname", life, "");
            if ((npcs || !type.equals("n")) && limited.equals("")) {
               AbstractLoadedMapleLife myLife = this.loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);
               if (myLife instanceof MapleMonster && !GameConstants.isNoSpawn(mapid)) {
                  MapleMonster mob = (MapleMonster)myLife;
                  map.addMonsterSpawn(
                     mob, MapleDataTool.getInt("mobTime", life, 0), (byte)MapleDataTool.getInt("team", life, -1), mob.getId() == bossid ? msg : null
                  );
               } else if (myLife instanceof MapleNPC) {
                  map.addMapObject(myLife);
               }
            }
         }

         this.addAreaBossSpawn(map);
         map.setCreateMobInterval((short)MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 3500));
         map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
         map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
         map.loadMonsterRate(true);
         map.setNodes(this.loadNodes(mapid, mapData));
         if (reactors && mapData.getChildByPath("reactor") != null) {
            for (MapleData reactor : mapData.getChildByPath("reactor")) {
               String id = MapleDataTool.getString(reactor.getChildByPath("id"));
               if (id != null) {
                  map.spawnReactor(this.loadReactor(reactor, id, (byte)MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
               }
            }
         }

         try {
            map.setMapName(MapleDataTool.getString("mapName", this.nameData.getChildByPath(this.getMapStringName(mapid)), ""));
            map.setStreetName(MapleDataTool.getString("streetName", this.nameData.getChildByPath(this.getMapStringName(mapid)), ""));
         } catch (Exception var33) {
            map.setMapName("");
            map.setStreetName("");
         }

         map.setClock(MapleDataTool.getInt(mapData.getChildByPath("info/clock"), 0) > 0);
         map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
         map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
         map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
         map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
         map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
         map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
         map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
         map.setForcedReturnMap(MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
         map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
         map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
         map.setMode(MapleDataTool.getString(mapData.getChildByPath("info/mode"), ""));
         map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
         map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
         map.setFieldType(fieldType);
         map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0F));
         map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
         map.setInstanceId(instanceid);
         this.lock.lock();

         try {
            this.instanceMap.put(instanceid, map);
         } finally {
            this.lock.unlock();
         }

         return map;
      }
   }

   public int getLoadedMaps() {
      return this.maps.size();
   }

   public boolean isMapLoaded(int mapId) {
      return this.maps.containsKey(mapId);
   }

   public boolean isInstanceMapLoaded(int instanceid) {
      return this.instanceMap.containsKey(instanceid);
   }

   public void clearLoadedMap() {
      this.lock.lock();

      try {
         this.maps.clear();
      } finally {
         this.lock.unlock();
      }
   }

   public List<Field> getAllLoadedMaps() {
      List<Field> ret = new ArrayList<>();
      this.lock.lock();

      try {
         ret.addAll(this.maps.values());
         ret.addAll(this.instanceMap.values());
      } finally {
         this.lock.unlock();
      }

      return ret;
   }

   public Collection<Field> getAllMaps() {
      return this.maps.values();
   }

   private AbstractLoadedMapleLife loadLife(MapleData life, String id, String type) {
      AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(Integer.parseInt(id), type);
      if (myLife == null) {
         return null;
      } else {
         myLife.setCy(MapleDataTool.getInt(life.getChildByPath("cy")));
         MapleData dF = life.getChildByPath("f");
         if (dF != null) {
            myLife.setF(MapleDataTool.getInt(dF));
         }

         myLife.setFh(MapleDataTool.getInt(life.getChildByPath("fh")));
         myLife.setRx0(MapleDataTool.getInt(life.getChildByPath("rx0")));
         myLife.setRx1(MapleDataTool.getInt(life.getChildByPath("rx1")));
         myLife.setPosition(new Point(MapleDataTool.getInt(life.getChildByPath("x")), MapleDataTool.getInt(life.getChildByPath("y"))));
         if (MapleDataTool.getInt("hide", life, 0) == 1 && myLife instanceof MapleNPC) {
            myLife.setHide(true);
         }

         return myLife;
      }
   }

   private final Reactor loadReactor(MapleData reactor, String id, byte FacingDirection) {
      Reactor myReactor = new Reactor(ReactorFactory.getReactor(Integer.parseInt(id)), Integer.parseInt(id));
      myReactor.setFacingDirection(FacingDirection);
      myReactor.setPosition(new Point(MapleDataTool.getInt(reactor.getChildByPath("x")), MapleDataTool.getInt(reactor.getChildByPath("y"))));
      myReactor.setDelay(MapleDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
      myReactor.setState((byte)0);
      myReactor.setName(MapleDataTool.getString(reactor.getChildByPath("name"), ""));
      return myReactor;
   }

   private String getMapName(int mapid) {
      return StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9) + ".img";
   }

   private String getMapStringName(int mapid) {
      StringBuilder builder = new StringBuilder();
      if (mapid < 100000000) {
         builder.append("maple");
      } else if ((mapid < 100000000 || mapid >= 200000000)
         && mapid != 862000002
         && mapid != 862000003
         && mapid != 862000004
         && mapid != 910050100
         && mapid != 910240000
         && mapid != 910510002) {
         if (mapid >= 200000000 && mapid < 300000000) {
            builder.append("ossyria");
         } else if (mapid >= 300000000 && mapid < 400000000) {
            builder.append("3rd");
         } else if (mapid >= 400000000 && mapid < 500000000) {
            builder.append("grandis");
         } else if (mapid >= 500000000 && mapid < 510000000) {
            builder.append("TH");
         } else if (mapid >= 555000000 && mapid < 556000000) {
            builder.append("SG");
         } else if (mapid >= 540000000 && mapid < 600000000) {
            builder.append("SG");
         } else if (mapid >= 682000000 && mapid < 683000000) {
            builder.append("GL");
         } else if (mapid >= 600000000 && mapid < 670000000) {
            builder.append("GL");
         } else if (mapid >= 677000000 && mapid < 678000000) {
            builder.append("GL");
         } else if (mapid >= 670000000 && mapid < 682000000) {
            builder.append("GL");
         } else if (mapid >= 687000000 && mapid < 688000000) {
            builder.append("Gacha_GL");
         } else if (mapid >= 689000000 && mapid < 690000000) {
            builder.append("CTF_GL");
         } else if (mapid >= 683000000 && mapid < 684000000) {
            builder.append("event");
         } else if (mapid >= 684000000 && mapid < 685000000) {
            builder.append("event_5th");
         } else if (mapid >= 700000000 && mapid < 700000300) {
            builder.append("wedding");
         } else if (mapid >= 701000000 && mapid < 701020000) {
            builder.append("china");
         } else if ((mapid < 702090000 || mapid > 702100000) && (mapid < 740000000 || mapid >= 741000000)) {
            if (mapid >= 702000000 && mapid < 742000000) {
               builder.append("CN");
            } else if (mapid >= 800000000 && mapid < 900000000) {
               builder.append("JP");
            } else {
               builder.append("etc");
            }
         } else {
            builder.append("TW");
         }
      } else {
         builder.append("victoria");
      }

      builder.append("/");
      builder.append(mapid);
      return builder.toString();
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }

   private void addAreaBossSpawn(Field map) {
      int monsterid = -1;
      int mobtime = -1;
      String msg = null;
      boolean shouldSpawn = true;
      Point pos1 = null;
      Point pos2 = null;
      Point pos3 = null;
      switch (map.getId()) {
         case 4000033:
         case 4000034:
         case 4000035:
         case 4000036:
         case 4000037:
         case 4000038:
         case 4000039:
            mobtime = 1200;
            monsterid = 9300815;
            msg = "서늘한 기운이 감돌면서 마노가 나타났습니다.";
            pos1 = new Point(-190, 150);
            pos2 = new Point(232, 150);
            pos3 = new Point(-190, 150);
         default:
            if (monsterid > 0) {
               map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(monsterid), pos1, pos2, pos3, mobtime, msg, shouldSpawn);
            }
      }
   }

   private void loadPortals(Field map, MapleData port) {
      if (port != null) {
         int nextDoorPortal = 128;

         for (MapleData portal : port.getChildren()) {
            Portal myPortal = new Portal(MapleDataTool.getInt(portal.getChildByPath("pt")));
            myPortal.setName(MapleDataTool.getString(portal.getChildByPath("pn")));
            myPortal.setTarget(MapleDataTool.getString(portal.getChildByPath("tn")));
            myPortal.setTargetMapId(MapleDataTool.getInt(portal.getChildByPath("tm")));
            myPortal.setPosition(new Point(MapleDataTool.getInt(portal.getChildByPath("x")), MapleDataTool.getInt(portal.getChildByPath("y"))));
            String script = MapleDataTool.getString("script", portal, null);
            if (script != null && script.equals("")) {
               script = null;
            }

            myPortal.setScriptName(script);
            if (myPortal.getType() == 6) {
               myPortal.setId(nextDoorPortal);
               nextDoorPortal++;
            } else {
               myPortal.setId(Integer.parseInt(portal.getName()));
            }

            map.addPortal(myPortal);
         }
      }
   }

   private MapleNodes loadNodes(int mapid, MapleData mapData) {
      MapleNodes nodeInfo = new MapleNodes(mapid);
      if (mapData.getChildByPath("nodeInfo") != null) {
         for (MapleData node : mapData.getChildByPath("nodeInfo")) {
            try {
               if (node.getName().equals("start")) {
                  nodeInfo.setNodeStart(MapleDataTool.getInt(node, 0));
               } else {
                  List<Integer> edges = new ArrayList<>();
                  if (node.getChildByPath("edge") != null) {
                     for (MapleData edge : node.getChildByPath("edge")) {
                        edges.add(MapleDataTool.getInt(edge, -1));
                     }
                  }

                  MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(
                     Integer.parseInt(node.getName()),
                     MapleDataTool.getIntConvert("key", node, 0),
                     MapleDataTool.getIntConvert("x", node, 0),
                     MapleDataTool.getIntConvert("y", node, 0),
                     MapleDataTool.getIntConvert("attr", node, 0),
                     edges
                  );
                  nodeInfo.addNode(mni);
               }
            } catch (NumberFormatException var12) {
            }
         }

         nodeInfo.sortNodes();
      }

      for (int i = 1; i <= 7; i++) {
         if (mapData.getChildByPath(String.valueOf(i)) != null && mapData.getChildByPath(i + "/obj") != null) {
            for (MapleData node : mapData.getChildByPath(i + "/obj")) {
               if (node.getChildByPath("SN_count") != null && node.getChildByPath("speed") != null) {
                  int sn_count = MapleDataTool.getIntConvert("SN_count", node, 0);
                  String name = MapleDataTool.getString("name", node, "");
                  int speed = MapleDataTool.getIntConvert("speed", node, 0);
                  if (sn_count > 0 && speed > 0 && !name.equals("")) {
                     List<Integer> SN = new ArrayList<>();

                     for (int x = 0; x < sn_count; x++) {
                        SN.add(MapleDataTool.getIntConvert("SN" + x, node, 0));
                     }

                     MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(
                        name,
                        MapleDataTool.getIntConvert("start", node, 2),
                        speed,
                        MapleDataTool.getIntConvert("x1", node, 0),
                        MapleDataTool.getIntConvert("y1", node, 0),
                        MapleDataTool.getIntConvert("x2", node, 0),
                        MapleDataTool.getIntConvert("y2", node, 0),
                        MapleDataTool.getIntConvert("r", node, 0),
                        SN
                     );
                     nodeInfo.addPlatform(mni);
                  }
               } else if (node.getChildByPath("tags") != null) {
                  String name = MapleDataTool.getString("tags", node, "");
                  nodeInfo.addFlag(new Pair<>(name, name.endsWith("3") ? 1 : 0));
               }
            }
         }
      }

      if (mapData.getChildByPath("area") != null) {
         for (MapleData area : mapData.getChildByPath("area")) {
            int x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
            int y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
            int x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
            int y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
            Rectangle mapArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);
            nodeInfo.addMapleArea(mapArea);
         }
      }

      if (mapData.getChildByPath("CaptureTheFlag") != null) {
         for (MapleData area : mapData.getChildByPath("CaptureTheFlag")) {
            nodeInfo.addGuardianSpawn(
               new Point(MapleDataTool.getInt(area.getChildByPath("FlagPositionX")), MapleDataTool.getInt(area.getChildByPath("FlagPositionY"))),
               area.getName().startsWith("Red") ? 0 : 1
            );
         }
      }

      if (mapData.getChildByPath("directionInfo") != null) {
         for (MapleData area : mapData.getChildByPath("directionInfo")) {
            MapleNodes.DirectionInfo di = new MapleNodes.DirectionInfo(
               Integer.parseInt(area.getName()),
               MapleDataTool.getInt("x", area, 0),
               MapleDataTool.getInt("y", area, 0),
               MapleDataTool.getInt("forcedInput", area, 0) > 0
            );
            MapleData mc2 = area.getChildByPath("EventQ");
            if (mc2 != null) {
               for (MapleData event : mc2) {
                  di.eventQ.add(MapleDataTool.getString(event));
               }
            }

            nodeInfo.addDirection(Integer.parseInt(area.getName()), di);
         }
      }

      if (mapData.getChildByPath("monsterCarnival") != null) {
         MapleData mc = mapData.getChildByPath("monsterCarnival");
         if (mc.getChildByPath("mobGenPos") != null) {
            for (MapleData area : mc.getChildByPath("mobGenPos")) {
               nodeInfo.addMonsterPoint(
                  MapleDataTool.getInt(area.getChildByPath("x")),
                  MapleDataTool.getInt(area.getChildByPath("y")),
                  MapleDataTool.getInt(area.getChildByPath("fh")),
                  MapleDataTool.getInt(area.getChildByPath("cy")),
                  MapleDataTool.getInt("team", area, -1)
               );
            }
         }

         if (mc.getChildByPath("mob") != null) {
            for (MapleData area : mc.getChildByPath("mob")) {
               nodeInfo.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")), MapleDataTool.getInt(area.getChildByPath("spendCP")));
            }
         }

         if (mc.getChildByPath("guardianGenPos") != null) {
            for (MapleData area : mc.getChildByPath("guardianGenPos")) {
               nodeInfo.addGuardianSpawn(
                  new Point(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y"))),
                  MapleDataTool.getInt("team", area, -1)
               );
            }
         }

         if (mc.getChildByPath("skill") != null) {
            for (MapleData area : mc.getChildByPath("skill")) {
               nodeInfo.addSkillId(MapleDataTool.getInt(area));
            }
         }
      }

      return nodeInfo;
   }
}
