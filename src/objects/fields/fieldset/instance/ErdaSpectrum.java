package objects.fields.fieldset.instance;

import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.FontColorType;
import network.models.FontType;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.MapleFoothold;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class ErdaSpectrum extends FieldSetInstance {
   public ErdaSpectrum(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.fieldSeteventTime = 600000;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);

      for (Integer map : this.fsim.instances) {
         this.field(map).resetFully(false);
         this.field(map).setLastRespawnTime(System.currentTimeMillis());
         this.field(map).setFieldSetInstance(this);
      }

      this.fs.fInstance.putIfAbsent(this, new ArrayList<>());

      for (PartyMemberEntry mpc : this.leader.getParty().getPartyMemberList()) {
         this.fs.fInstance.get(this).add(mpc.getId());
      }

      this.userList = this.fs.fInstance.get(this);
      this.timeOut(this.fieldSeteventTime);
      this.erdaBonusBall();
      this.Var.setProperty("Erda", "0");
      this.Var.setProperty("Phase", "1");
      this.Var.setProperty("TransferCount", "0");
      this.Var.setProperty("Red", "0");
      this.Var.setProperty("Blue", "0");
      this.Var.setProperty("lastPuck", "red");
      this.Var.setProperty("lastPuckTime", "0");
   }

   @Override
   public void userEnter(MapleCharacter user) {
      if (user.getMap().getId() == 450001400) {
         user.updateOneInfo(34170, "date", new SimpleDateFormat("yy/MM/dd").format(new Date()));
         user.updateOneInfo(34170, "ctype", "0");
         user.updateOneInfo(34170, "clear", "0");
      }
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
   }

   @Override
   public void userDead(MapleCharacter user) {
   }

   @Override
   public void userLeftParty(MapleCharacter user) {
      super.userLeftParty(user);
   }

   @Override
   public void userDisbandParty() {
      super.userDisbandParty();
   }

   @Override
   public void userDisconnected(MapleCharacter user) {
      super.userDisconnected(user);
   }

   @Override
   public void mobDead(MapleMonster mMob) {
      int Erda = Integer.parseInt(this.Var.getProperty("Erda")) + 1;
      this.Var.setProperty("Erda", String.valueOf(Erda));
      mMob.getMap().broadcastMessage(ErdaSpectrumIncEffect(mMob.getPosition()));
      if (mMob.getMap().getId() != 450001500) {
         if (this.Var.getProperty("startCrackMap", "0").equals("0")) {
            mMob.getMap()
               .broadcastMessage(ErdaSpectrumErdaInfo(Erda, Integer.parseInt(this.Var.getProperty("TransferCount")), this.Var.getProperty("lastPuck")));
         } else {
            mMob.getMap().broadcastMessage(ErdaSpectrumCrackInfo(Erda, mMob.getMap().getAllMonstersThreadsafe().size() - 1));
         }
      } else {
         mMob.getMap().broadcastMessage(ErdaSpectrumWormInfo(Erda, this.Var.getProperty("elim", "0")));
      }
   }

   public void incErdaGuage(int num) {
      int Erda = Integer.parseInt(this.Var.getProperty("Erda")) + num;
      this.Var.setProperty("Erda", String.valueOf(Erda));
   }

   public void erdaBonusBall() {
      if (this.eventSchedules.get("erdaBonusBall") != null) {
         this.eventSchedules.get("erdaBonusBall").cancel(false);
         this.eventSchedules.remove("erdaBonusBall");
      }

      this.eventSchedules.put("erdaBonusBall", Timer.EventTimer.getInstance().register(new Runnable() {
         @Override
         public void run() {
            if (!ErdaSpectrum.this.dispose) {
               final Field map = ErdaSpectrum.this.field(450001400);
               if (map.getCharactersSize() > 0) {
                  int random = Randomizer.rand(0, 6);
                  MapleFoothold f = map.getFootholds().getFootholds().get(random);
                  boolean x1 = Randomizer.nextBoolean();
                  int posX = 0;
                  int posY = 0;
                  if (x1) {
                     posX = f.getX1();
                     posY = f.getY1();
                  } else {
                     posX = f.getX2();
                     posY = f.getY2();
                  }

                  ErdaSpectrum.this.Var.setProperty("ballPos", posX + "," + posY);
                  if (ErdaSpectrum.this.Var.getProperty("Phase").equals("1")) {
                     map.broadcastMessage(ErdaSpectrum.ErdaSpectrumBombObject(new Point(posX, posY), false));
                  } else {
                     map.broadcastMessage(ErdaSpectrum.ErdaSpectrumBombObject(new Point(posX, posY), true));
                     final Point dsPoint = new Point(posX, posY);
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           List<MapleMonster> monsterList = new ArrayList<>();

                           for (MapleMonster mob : map.getAllMonstersThreadsafe()) {
                              if (mob.getId() >= 8641019 && mob.getId() <= 8641021) {
                                 boolean bomb = mob.getPosition().x >= dsPoint.x - 150 && mob.getPosition().x <= dsPoint.x + 150;
                                 if (bomb) {
                                    monsterList.add(mob);
                                 }
                              }
                           }

                           for (MapleMonster mobx : monsterList) {
                              map.killMonster(mobx);
                           }
                        }
                     }, 3000L);
                  }
               }

               Field map2 = ErdaSpectrum.this.field(450001500);
               if (map2.getCharactersSize() > 0) {
                  int randomx = Randomizer.rand(0, 33);
                  MapleFoothold fx = map2.getFootholds().getFootholds().get(randomx);
                  boolean x1x = Randomizer.nextBoolean();
                  int posXx = 0;
                  int posYx = 0;
                  if (x1x) {
                     posXx = fx.getX1();
                     posYx = fx.getY1();
                  } else {
                     posXx = fx.getX2();
                     posYx = fx.getY2();
                  }

                  ErdaSpectrum.this.Var.setProperty("ballPos", posXx + "," + posYx);
                  map2.broadcastMessage(ErdaSpectrum.ErdaSpectrumBombObject(new Point(posXx, posYx), false));
               }

               Field map3 = ErdaSpectrum.this.field(450001450);
               if (map3.getCharactersSize() > 0) {
                  int randomx = Randomizer.rand(0, map3.getFootholds().getFootholds().size() - 1);
                  MapleFoothold fx = map3.getFootholds().getFootholds().get(randomx);
                  boolean x1x = Randomizer.nextBoolean();
                  int posXx = 0;
                  int posYx = 0;
                  if (x1x) {
                     posXx = fx.getX1();
                     posYx = fx.getY1();
                  } else {
                     posXx = fx.getX2();
                     posYx = fx.getY2();
                  }

                  ErdaSpectrum.this.Var.setProperty("ballPos", posXx + "," + posYx);
                  map3.broadcastMessage(ErdaSpectrum.ErdaSpectrumBombObject(new Point(posXx, posYx), false));
               }
            }
         }
      }, 10000L, 3000L));
   }

   public void erdaDec() {
      this.Var.setProperty("Phase", "2");
      if (this.eventSchedules.get("erdaDec") != null) {
         this.eventSchedules.get("erdaDec").cancel(false);
         this.eventSchedules.remove("erdaDec");
      }

      this.eventSchedules
         .put(
            "erdaDec",
            Timer.EventTimer.getInstance()
               .register(
                  new Runnable() {
                     @Override
                     public void run() {
                        if (!ErdaSpectrum.this.dispose) {
                           Field map = ErdaSpectrum.this.field(450001400);
                           if (map.getCharactersSize() > 0) {
                              int Erda = Math.max(0, Integer.parseInt(ErdaSpectrum.this.Var.getProperty("Erda")) - 1);
                              ErdaSpectrum.this.Var.setProperty("Erda", String.valueOf(Erda));
                              map.broadcastMessage(
                                 ErdaSpectrum.ErdaSpectrumErdaInfo(
                                    Erda, Integer.parseInt(ErdaSpectrum.this.Var.getProperty("TransferCount")), ErdaSpectrum.this.Var.getProperty("lastPuck")
                                 )
                              );
                              if (Erda <= 0) {
                                 map.broadcastMessage(ErdaSpectrum.ErdaSpectrumPhase(2));
                                 map.killAllMonsters(true);
                                 map.setLastRespawnTime(System.currentTimeMillis());
                                 ErdaSpectrum.this.Var.setProperty("Phase", "1");
                                 ErdaSpectrum.this.eventSchedules.get("erdaDec").cancel(false);
                                 ErdaSpectrum.this.eventSchedules.remove("erdaDec");
                              }
                           }
                        }
                     }
                  },
                  1000L
               )
         );
   }

   public void changePuck() {
      if (this.eventSchedules.get("changePuck") != null) {
         this.eventSchedules.get("changePuck").cancel(false);
         this.eventSchedules.remove("changePuck");
      }

      this.Var.setProperty("lastPuckTime", String.valueOf(System.currentTimeMillis()));
      this.eventSchedules
         .put(
            "changePuck",
            Timer.EventTimer.getInstance()
               .register(
                  new Runnable() {
                     @Override
                     public void run() {
                        if (!ErdaSpectrum.this.dispose) {
                           Field map = ErdaSpectrum.this.field(450001400);
                           if (map.getCharactersSize() > 0) {
                              long now = System.currentTimeMillis();
                              long lastPuckTime = Long.valueOf(ErdaSpectrum.this.Var.getProperty("lastPuckTime"));
                              long diff = now - lastPuckTime;
                              String Erda = ErdaSpectrum.this.Var.getProperty("lastPuck");
                              switch (Erda) {
                                 case "red":
                                    if (diff >= 5000L) {
                                       ErdaSpectrum.this.Var.setProperty("lastPuck", "blue");
                                       ErdaSpectrum.this.Var.setProperty("lastPuckTime", String.valueOf(System.currentTimeMillis()));
                                    }
                                    break;
                                 case "blue":
                                    if (diff >= 5000L) {
                                       ErdaSpectrum.this.Var.setProperty("lastPuck", "purple");
                                       ErdaSpectrum.this.Var.setProperty("lastPuckTime", String.valueOf(System.currentTimeMillis()));
                                    }
                                    break;
                                 case "purple":
                                    if (diff >= 2500L) {
                                       ErdaSpectrum.this.Var.setProperty("lastPuck", "red");
                                       ErdaSpectrum.this.Var.setProperty("lastPuckTime", String.valueOf(System.currentTimeMillis()));
                                    }
                              }

                              int Erda2 = Math.max(0, Integer.parseInt(ErdaSpectrum.this.Var.getProperty("Erda")));
                              map.broadcastMessage(
                                 ErdaSpectrum.ErdaSpectrumErdaInfo(
                                    Erda2, Integer.parseInt(ErdaSpectrum.this.Var.getProperty("TransferCount")), ErdaSpectrum.this.Var.getProperty("lastPuck")
                                 )
                              );
                              if (ErdaSpectrum.this.Var.getProperty("Phase").equals("1")) {
                                 ErdaSpectrum.this.eventSchedules.get("changePuck").cancel(false);
                                 ErdaSpectrum.this.eventSchedules.remove("changePuck");
                              }
                           }
                        }
                     }
                  },
                  500L
               )
         );
   }

   public void addTransferCount(int num) {
      int tCount = Integer.parseInt(this.Var.getProperty("TransferCount", "0"));
      this.Var.setProperty("TransferCount", String.valueOf(Math.min(10, tCount + num)));
      Field map = this.field(450001400);
      int Erda = Math.max(0, Integer.parseInt(this.Var.getProperty("Erda")));
      map.broadcastMessage(ErdaSpectrumErdaInfo(Erda, Integer.parseInt(this.Var.getProperty("TransferCount")), this.Var.getProperty("lastPuck")));
      if (this.Var.getProperty("TransferCount").equals("10")) {
         int redCount = Integer.parseInt(this.Var.getProperty("Red", "0"));
         int blueCount = Integer.parseInt(this.Var.getProperty("Blue", "0"));
         List<Integer> rand = new ArrayList<>();

         for (int i = 0; i < redCount; i++) {
            rand.add(0);
         }

         for (int i = 0; i < blueCount; i++) {
            rand.add(1);
         }

         Collections.shuffle(rand);
         int changeMap = rand.get(Randomizer.nextInt(rand.size()));
         List<MapleCharacter> chars = map.getCharacters();
         if (changeMap == 0) {
            for (MapleCharacter chr : chars) {
               chr.changeMap(450001500);
            }
         } else {
            for (MapleCharacter chr : chars) {
               chr.changeMap(450001450);
            }
         }
      }
   }

   public void startCrackMap() {
      final Point[] leftTop = new Point[]{new Point(-625, -120), new Point(-425, -120), new Point(-525, -120), new Point(-725, -120), new Point(-825, -120)};
      final Point[] leftBottom = new Point[]{new Point(-625, 220), new Point(-425, 220), new Point(-525, 220), new Point(-725, 220), new Point(-825, 220)};
      final Point[] rightTop = new Point[]{new Point(625, -120), new Point(425, -120), new Point(525, -120), new Point(725, -120), new Point(825, -120)};
      final Point[] rightBottom = new Point[]{new Point(625, 220), new Point(425, 220), new Point(525, 220), new Point(725, 220), new Point(825, 220)};
      if (this.Var.getProperty("startCrackMap", "0").equals("0")) {
         this.Var.setProperty("startCrackMap", "1");
         this.Var.setProperty("Phase", "1");
         this.Var.setProperty("CrackMapPhase", "0");
         this.Var.setProperty("mobGenPos", String.valueOf(Randomizer.rand(0, 3)));
         this.Var.setProperty("CrackMapStartTime", String.valueOf(System.currentTimeMillis()));
         this.eventSchedules
            .put(
               "startCrackMap",
               Timer.EventTimer.getInstance()
                  .register(
                     new Runnable() {
                        @Override
                        public void run() {
                           if (!ErdaSpectrum.this.dispose) {
                              Field map = ErdaSpectrum.this.field(450001450);
                              if (map.getCharactersSize() > 0) {
                                 long crackMapStartTime = Long.parseLong(ErdaSpectrum.this.Var.getProperty("CrackMapStartTime"));
                                 if (ErdaSpectrum.this.Var.getProperty("startCrackMap").equals("2")) {
                                    String var4 = ErdaSpectrum.this.Var.getProperty("CrackMapPhase");
                                    label118:
                                    switch (var4) {
                                       case "0":
                                          String var11 = ErdaSpectrum.this.Var.getProperty("mobGenPos");
                                          switch (var11) {
                                             case "0":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641026),
                                                   leftTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break label118;
                                             case "1":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641026),
                                                   leftBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break label118;
                                             case "2":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641026),
                                                   rightTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break label118;
                                             case "3":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641026),
                                                   rightBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                             default:
                                                break label118;
                                          }
                                       case "1":
                                          String var10 = ErdaSpectrum.this.Var.getProperty("mobGenPos");
                                          switch (var10) {
                                             case "0":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   leftTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   leftBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break label118;
                                             case "1":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   leftBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   rightTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break label118;
                                             case "2":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   rightTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   rightBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break label118;
                                             case "3":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   rightBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641027),
                                                   leftTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                             default:
                                                break label118;
                                          }
                                       case "2":
                                          String var6 = ErdaSpectrum.this.Var.getProperty("mobGenPos");
                                          switch (var6) {
                                             case "0":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   leftTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   leftBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   rightTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break;
                                             case "1":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   leftBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   rightTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   rightBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break;
                                             case "2":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   leftTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   rightTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   rightBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                break;
                                             case "3":
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   leftBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   rightBottom[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                                map.spawnMonsterOnGroundBelow(
                                                   MapleLifeFactory.getMonster(8641028),
                                                   leftTop[Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0"))]
                                                );
                                          }
                                    }

                                    ErdaSpectrum.this.Var
                                       .setProperty("SumCount", String.valueOf(Integer.parseInt(ErdaSpectrum.this.Var.getProperty("SumCount", "0")) + 1));
                                    if (ErdaSpectrum.this.Var.getProperty("SumCount").equals("4")) {
                                       ErdaSpectrum.this.Var.setProperty("mobGenPos", String.valueOf(Randomizer.rand(0, 3)));
                                       ErdaSpectrum.this.Var.setProperty("SumCount", "0");
                                    }

                                    map.broadcastMessage(
                                       ErdaSpectrum.ErdaSpectrumCrackInfo(
                                          Integer.parseInt(ErdaSpectrum.this.Var.getProperty("Erda")), map.getAllMonstersThreadsafe().size()
                                       )
                                    );
                                    if (map.getAllMonstersThreadsafe().size() >= 50) {
                                       ErdaSpectrum.this.timeOut(0);
                                    }
                                 }

                                 String var8 = ErdaSpectrum.this.Var.getProperty("CrackMapPhase");
                                 switch (var8) {
                                    case "0":
                                       if (System.currentTimeMillis() - crackMapStartTime >= 45000L) {
                                          ErdaSpectrum.this.Var.setProperty("CrackMapPhase", "1");
                                          map.broadcastMessage(
                                             CField.UIPacket.sendBigScriptProgressMessage(
                                                "균열이 커져 더 많은 몬스터가 소환됩니다.", FontType.NanumGothicBold, FontColorType.White
                                             )
                                          );
                                       }
                                       break;
                                    case "1":
                                       if (System.currentTimeMillis() - crackMapStartTime >= 90000L) {
                                          ErdaSpectrum.this.Var.setProperty("CrackMapPhase", "2");
                                          map.broadcastMessage(
                                             CField.UIPacket.sendBigScriptProgressMessage(
                                                "균열이 커져 더 많은 몬스터가 소환됩니다.", FontType.NanumGothicBold, FontColorType.White
                                             )
                                          );
                                       }
                                 }

                                 if (ErdaSpectrum.this.Var.getProperty("startCrackMap").equals("1")) {
                                    ErdaSpectrum.this.Var.setProperty("startCrackMap", "2");
                                    map.broadcastMessage(ErdaSpectrum.ErdaSpectrumPhase(2));
                                    map.broadcastMessage(ErdaSpectrum.ErdaSpectrumCrackInfo(Integer.parseInt(ErdaSpectrum.this.Var.getProperty("Erda")), 0));
                                    map.broadcastMessage(ErdaSpectrum.ErdaSpectrumTimer(120000, -1));
                                    ErdaSpectrum.this.clearSpectrum(120000);
                                    ErdaSpectrum.this.restartTimeOutNoClock(Integer.MAX_VALUE);
                                 }
                              }
                           }
                        }
                     },
                     1000L,
                     3000L
                  )
            );
      }
   }

   public void startWormMap() {
      if (this.Var.getProperty("startWormMap", "0").equals("0")) {
         this.Var.setProperty("startWormMap", "1");
         this.Var.setProperty("Phase", "1");
         this.eventSchedules.put("startWormMap", Timer.EventTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
               if (!ErdaSpectrum.this.dispose) {
                  Field map = ErdaSpectrum.this.field(450001500);
                  if (map.getCharactersSize() > 0) {
                     if (ErdaSpectrum.this.Var.getProperty("startWormMap", "0").equals("2")) {
                        ErdaSpectrum.this.spawnWorm();
                     }

                     if (ErdaSpectrum.this.Var.getProperty("startWormMap").equals("1")) {
                        ErdaSpectrum.this.Var.setProperty("startWormMap", "2");
                        ErdaSpectrum.this.spawnWorm();
                        map.broadcastMessage(ErdaSpectrum.ErdaSpectrumPhase(2));
                        map.broadcastMessage(ErdaSpectrum.ErdaSpectrumCrackInfo(Integer.parseInt(ErdaSpectrum.this.Var.getProperty("Erda")), 0));
                        map.broadcastMessage(ErdaSpectrum.ErdaSpectrumTimer(150000, -1));
                        ErdaSpectrum.this.restartTimeOutNoClock(150000);
                     }
                  }
               }
            }
         }, 1000L, 3000L));
      }
   }

   public void spawnWorm() {
      Field map = this.field(450001500);
      int wormSize = 0;

      for (MapleMonster worm : map.getAllMonstersThreadsafe()) {
         if (worm.getId() == 8641030 || worm.getId() == 8641031) {
            wormSize++;
         }
      }

      if (wormSize == 0) {
         Point leftTop = new Point(-800, -160);
         Point leftBottom = new Point(-800, 168);
         Point rightTop = new Point(600, -160);
         Point rightBottom = new Point(600, 168);
         int rand = Randomizer.rand(0, 5);
         MobSkillInfo msi = MobSkillFactory.getMobSkill(146, 19);
         MobTemporaryStatEffect eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, msi, true);
         eff.setDuration(10000);
         eff.setCancelTask(10000L);
         switch (rand) {
            case 0: {
               MapleMonster mob1 = MapleLifeFactory.getMonster(8641031);
               MapleMonster mob2 = MapleLifeFactory.getMonster(8641031);
               map.spawnMonsterOnGroundBelow(mob1, leftTop, (byte)-1, true);
               map.spawnMonsterOnGroundBelow(mob2, leftBottom, (byte)-1, true);
               mob1.applyStatus(eff);
               mob2.applyStatus(eff);
               break;
            }
            case 1: {
               MapleMonster mob1 = MapleLifeFactory.getMonster(8641031);
               MapleMonster mob2 = MapleLifeFactory.getMonster(8641030);
               map.spawnMonsterOnGroundBelow(mob1, leftTop, (byte)-1, true);
               map.spawnMonsterOnGroundBelow(mob2, rightTop, (byte)-1, true);
               mob1.applyStatus(eff);
               mob2.applyStatus(eff);
               break;
            }
            case 2: {
               MapleMonster mob1 = MapleLifeFactory.getMonster(8641031);
               MapleMonster mob2 = MapleLifeFactory.getMonster(8641030);
               map.spawnMonsterOnGroundBelow(mob1, leftTop, (byte)-1, true);
               map.spawnMonsterOnGroundBelow(mob2, rightBottom, (byte)-1, true);
               mob1.applyStatus(eff);
               mob2.applyStatus(eff);
               break;
            }
            case 3: {
               MapleMonster mob1 = MapleLifeFactory.getMonster(8641031);
               MapleMonster mob2 = MapleLifeFactory.getMonster(8641030);
               map.spawnMonsterOnGroundBelow(mob1, leftBottom, (byte)-1, true);
               map.spawnMonsterOnGroundBelow(mob2, rightTop, (byte)-1, true);
               mob1.applyStatus(eff);
               mob2.applyStatus(eff);
               break;
            }
            case 4: {
               MapleMonster mob1 = MapleLifeFactory.getMonster(8641031);
               MapleMonster mob2 = MapleLifeFactory.getMonster(8641030);
               map.spawnMonsterOnGroundBelow(mob1, leftBottom, (byte)-1, true);
               map.spawnMonsterOnGroundBelow(mob2, rightBottom, (byte)-1, true);
               mob1.applyStatus(eff);
               mob2.applyStatus(eff);
               break;
            }
            case 5: {
               MapleMonster mob1 = MapleLifeFactory.getMonster(8641030);
               MapleMonster mob2 = MapleLifeFactory.getMonster(8641030);
               map.spawnMonsterOnGroundBelow(mob1, rightTop, (byte)-1, true);
               map.spawnMonsterOnGroundBelow(mob2, rightBottom, (byte)-1, true);
               mob1.applyStatus(eff);
               mob2.applyStatus(eff);
            }
         }
      }
   }

   public void incElim() {
      Field map = this.field(450001500);
      int elim = Integer.parseInt(this.Var.getProperty("elim", "0"));
      elim = Math.min(5, elim + 1);
      this.Var.setProperty("elim", String.valueOf(elim));
      int Erda = Integer.parseInt(this.Var.getProperty("Erda"));
      map.broadcastMessage(ErdaSpectrumWormInfo(Erda, this.Var.getProperty("elim")));
      map.broadcastMessage(CField.UIPacket.sendBigScriptProgressMessage("아르마 주니어가 동굴 뒤로 숨어 들어갑니다.", FontType.NanumGothicBold, FontColorType.White));
      if (elim == 5) {
         this.clearSpectrum(0);
      }
   }

   public void clearSpectrum(int time) {
      if (this.eventSchedules.get("clearSpectrum") != null) {
         this.eventSchedules.get("clearSpectrum").cancel(false);
         this.eventSchedules.remove("clearSpectrum");
      }

      this.eventSchedules.put("clearSpectrum", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!ErdaSpectrum.this.dispose) {
               for (Integer map : ErdaSpectrum.this.fsim.instances) {
                  for (MapleCharacter chr : ErdaSpectrum.this.field(map).getCharacters()) {
                     if (ErdaSpectrum.this.userList.contains(chr.getId())) {
                        chr.updateOneInfo(34170, "ctype", "1");
                        chr.updateOneInfo(34170, "clear", "1");
                        chr.changeMap(chr.getMap().getForcedReturnMap());
                     }
                  }
               }
            }
         }
      }, time));
   }

   public static byte[] ErdaSpectrumBombObject(Point pos, boolean distract) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ERDA_SPECTRUM_BOMB_OBJECT.getValue());
      p.writeInt(1);
      p.writeInt(distract ? 31 : 33);
      p.writeInt(distract ? 70001 : 70000);
      p.writeInt(0);
      p.writeInt(distract ? 3000 : 2500);
      p.writeInt(-120);
      p.writeInt(distract ? -154 : -128);
      p.writeInt(120);
      p.writeInt(5);
      p.writeInt(pos.x);
      p.writeInt(pos.y);
      return p.getPacket();
   }

   public static byte[] ErdaSpectrumIncEffect(Point pos) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ERDA_SPECTRUM_INC_EFFECT.getValue());
      p.writeInt(pos.x);
      p.writeInt(pos.y);
      p.writeInt(2);
      p.writeInt(1);
      return p.getPacket();
   }

   public static byte[] ErdaSpectrumTimer(int timeMS, int transferNumber) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ERDA_SPECTRUM_TIMER.getValue());
      p.writeInt(timeMS);
      if (transferNumber > -1) {
         p.writeInt(transferNumber);
      }

      return p.getPacket();
   }

   public static byte[] ErdaSpectrumPhase(int phase) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ERDA_SPECTRUM_PHASE.getValue());
      p.writeInt(phase);
      return p.getPacket();
   }

   public static byte[] ErdaSpectrumErdaInfo(int Erda, int transferCount, String puck) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ERDA_SPECTRUM_ERDA_INFO.getValue());
      p.writeInt(Erda);
      p.writeInt(-1);
      p.writeInt(-1);
      p.writeInt(transferCount);
      switch (puck) {
         case "red":
            p.writeInt(0);
            break;
         case "blue":
            p.writeInt(1);
            break;
         case "purple":
            p.writeInt(2);
      }

      return p.getPacket();
   }

   public static byte[] ErdaSpectrumCrackInfo(int Erda, int mobCount) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ERDA_SPECTRUM_ERDA_INFO.getValue());
      p.writeInt(Erda);
      p.writeInt(mobCount);
      p.writeInt(50);
      return p.getPacket();
   }

   public static byte[] ErdaSpectrumWormInfo(int Erda, String eliminate) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ERDA_SPECTRUM_ERDA_INFO.getValue());
      p.writeInt(Erda);
      p.writeInt(Integer.parseInt(eliminate));
      p.writeInt(0);
      return p.getPacket();
   }
}
