package objects.fields.fieldset.instance;

import database.DBConfig;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.HexTool;
import objects.utils.Properties;
import objects.utils.Randomizer;
import objects.utils.Table;
import objects.utils.Timer;

public class TangyoonKitchen extends FieldSetInstance {
   private static HashMap<Integer, TangyoonKitchen.TangyoonRecipe> recipeHashMap = new HashMap<>();
   private LinkedHashMap<Integer, TangyoonKitchen.TangyoonGameRecipe> gameRecipe = new LinkedHashMap<>();
   private HashMap<Integer, Integer> ReadyFood = new HashMap<>();
   private List<Integer> orderMobs = new ArrayList<>();
   private int orderKey = 0;

   public TangyoonKitchen(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.fieldSeteventTime = 1800000;
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
      this.waitGameStart();
      this.startOrder();
      this.timeOut(this.fieldSeteventTime + 20000);
   }

   @Override
   public void userEnter(MapleCharacter user) {
      if (user.getMap().getId() == 993194400) {
         user.send(CField.getClock(19));
      }

      if (user.getMap().getId() == 993194500) {
         SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
         Date lastTime = null;
         Date now = null;

         try {
            lastTime = sdf.parse(user.getOneInfo(501555, "date"));
            now = sdf.parse(sdf.format(new Date()));
         } catch (Exception var6) {
            lastTime = null;
         }

         if (lastTime != null && !lastTime.equals(now) || lastTime == null) {
            user.updateOneInfo(501555, "start", "1");
            user.updateOneInfo(501555, "date", sdf.format(new Date()));
            user.updateOneInfo(501555, "today", "0");
         }

         user.updateOneInfo(501555, "complete", "0");
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
   }

   public void waitGameStart() {
      if (this.eventSchedules.get("waitGameStart") != null) {
         this.eventSchedules.get("waitGameStart").cancel(false);
         this.eventSchedules.remove("waitGameStart");
      }

      this.eventSchedules.put("waitGameStart", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!TangyoonKitchen.this.dispose) {
               Field map = TangyoonKitchen.this.field(993194400);
               if (map.getCharactersSize() > 0) {
                  for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                     chr.changeMap(993194500);
                  }
               }
            }
         }
      }, 20000L));
   }

   public Integer getReadyFood(int userId) {
      if (this.ReadyFood.get(userId) == null) {
         this.ReadyFood.putIfAbsent(userId, -1);
      }

      return this.ReadyFood.get(userId);
   }

   public void setReadyFood(int userId, int food) {
      this.ReadyFood.put(userId, food);
   }

   public void putOven(MapleCharacter user, int oven) {
      int checkPoint = this.gameRecipe.get(oven + 1).getCheckPoint();
      int userPicked = Integer.parseInt(this.Var.getProperty(String.valueOf(user.getId()), "0"));
      if (this.gameRecipe.get(oven + 1).getRecipe().getRecipes().get(checkPoint) == userPicked) {
         this.gameRecipe.get(oven + 1).addCheckPoint();
         if (this.gameRecipe.get(oven + 1).getCheckPoint() == this.gameRecipe.get(oven + 1).getRecipe().getRecipes().size()) {
            this.Var.setProperty(String.valueOf(oven + 1), "2");
            user.getMap().broadcastMessage(CWvsContext.InfoPacket.brownMessage(oven + 1 + "번 주문 음식이 완성되었습니다."));
            user.getMap().broadcastMessage(setObjectState("cookPlate" + (oven + 1), 2));
            user.getMap().broadcastMessage(CField.environmentChange("Sound/MiniGame.img/14thTerra/reward", 5, 100));
            user.getMap().broadcastMessage(TangyoonBadOrCool(user.getId(), "Effect/OnUserEff.img/urus/great"));
         } else {
            if (checkPoint == 0) {
               user.getMap().broadcastMessage(setObjectState("cookPlate" + (oven + 1), 1));
            }

            user.getMap().broadcastMessage(CField.environmentChange("Sound/MiniGame.img/CrazySplash/revive", 5, 100));
            user.getMap().broadcastMessage(TangyoonBadOrCool(user.getId(), "Effect/OnUserEff.img/urus/good"));
         }
      } else {
         int random = Randomizer.rand(0, 2);
         switch (random) {
            case 0:
               user.send(CField.addPopupSay(9062552, 3000, "레시피를 제대로 보란 말일세!", ""));
               break;
            case 1:
               user.send(CField.addPopupSay(9062552, 3000, "빠르다고 다가 아니네!\r\n제대로 하게!", ""));
               break;
            case 2:
               user.send(CField.addPopupSay(9062552, 3000, "지금 뭐 하는 건가!", ""));
         }

         user.temporaryStatSet(7798907, 1000, SecondaryStatFlag.Stun, 1);
         user.send(CField.getCharacterExpression(7, 1000));
         user.getMap().broadcastMessage(user, CField.facialExpressionWithDuration(user, 7, 1000), false);
         user.getMap().broadcastMessage(CField.environmentChange("Sound/MiniGame.img/ironBoxEvent/failLight", 5, 100));
         user.getMap().broadcastMessage(TangyoonBadOrCool(user.getId(), "Effect/OnUserEff.img/urus/bad"));
         user.send(CWvsContext.InfoPacket.brownMessage("조리에 실패했습니다. 레시피와 동일한 재료를 놓거나 알맞은 가공을 시도해주세요."));
      }

      this.Var.setProperty(String.valueOf(user.getId()), "0");
      user.getMap().broadcastMessage(this.TangyoonObjectPacket_2());
   }

   public void pickUpOven(MapleCharacter user, int oven) {
      int food = this.gameRecipe.get(oven + 1).getRecipe().getFood();
      int checkPoint = this.gameRecipe.get(oven + 1).getCheckPoint();
      int recipeSize = this.gameRecipe.get(oven + 1).getRecipe().getRecipes().size();
      if (checkPoint == recipeSize) {
         user.getMap().broadcastMessage(setObjectState("cookPlate" + (oven + 1), 3));
         user.getMap().broadcastMessage(CField.environmentChange("Sound/MiniGame.img/CrazySplash/install", 5, 100));
         this.Var.setProperty(String.valueOf(user.getId()), String.valueOf(food));
         this.Var.setProperty(String.valueOf(oven + 1), "3");
      }

      user.getMap().broadcastMessage(this.TangyoonObjectPacket_2());
   }

   public void serving(MapleCharacter user, int customer) {
      int table = -1;

      for (Integer a : this.gameRecipe.keySet()) {
         if (this.gameRecipe.get(a).getOrder() == customer
            && this.gameRecipe.get(a).getRecipe().getFood() == Integer.parseInt(this.Var.getProperty(String.valueOf(user.getId()), "0"))) {
            table = a;
         }
      }

      if (table > -1) {
         int score = Integer.parseInt(this.Var.getProperty("score", "0"));
         int plus = this.gameRecipe.get(table).getRecipe().getScore();
         this.Var.setProperty("score", String.valueOf(score + plus));
         this.orderMobs.remove(Integer.valueOf(this.gameRecipe.get(table).getOrder()));
         this.Var.setProperty(String.valueOf(user.getId()), "0");
         this.Var.setProperty(String.valueOf(table), "0");
         user.getMap().broadcastMessage(CWvsContext.InfoPacket.brownMessage(table + "번 주문 배달에 성공했습니다. " + plus + "메소를 획득했습니다."));
         user.getMap().broadcastMessage(setObjectState("cookPlate" + table, 0));
         user.getMap().broadcastMessage(this.TangyoonObjectPacket_2());
         user.getMap().broadcastMessage(CField.environmentChange("Sound/Item.img/03015651/Appear", 5, 100));
         user.getMap().broadcastMessage(TangyoonBadOrCool(user.getId(), "Effect/OnUserEff.img/aquarisTower/success"));
         String message = "아주 훌륭하군!";
         switch (Randomizer.rand(0, 2)) {
            case 1:
               message = "자네, 나와 함께 일해보지 않겠나?";
               break;
            case 2:
               message = "신속하군! 아주 좋네!";
         }

         user.getMap().startMapEffect(message, 5120216, 4000);
         if (score + plus >= 50000) {
            user.getMap().startMapEffect("고생 많았네, 음식의 소중함을 깨달았기를 바라네.", 5120216, 4000);
            user.getMap().broadcastMessage(CField.environmentChange("Effect/EventEffect.img/2021BloomingRace/success", 16));
            user.getMap().broadcastMessage(CField.environmentChange("Sound/MiniGame.img/autopvp/result", 5, 100));
            user.getMap().broadcastMessage(TangyoonOrder(4));
            this.clearGame();
         }
      }
   }

   public void startOrder() {
      if (this.eventSchedules.get("startOrder") == null) {
         this.orderKey = 0;
         this.Var.setProperty("gameStatus", "0");
         this.eventSchedules
            .put(
               "startOrder",
               Timer.EventTimer.getInstance()
                  .register(
                     new Runnable() {
                        @Override
                        public void run() {
                           if (!TangyoonKitchen.this.dispose) {
                              Field map = TangyoonKitchen.this.field(993194500);
                              if (map.getCharactersSize() > 0) {
                                 if (TangyoonKitchen.this.Var.getProperty("gameStatus", "0").equals("0")) {
                                    TangyoonKitchen.this.Var.setProperty("gameStatus", "1");
                                 }

                                 int kitchen1 = Integer.parseInt(TangyoonKitchen.this.Var.getProperty("1", "0"));
                                 int kitchen2 = Integer.parseInt(TangyoonKitchen.this.Var.getProperty("2", "0"));
                                 int kitchen3 = Integer.parseInt(TangyoonKitchen.this.Var.getProperty("3", "0"));
                                 if (kitchen1 == 0) {
                                    boolean ok = false;
                                    int food = -1;

                                    while (!ok) {
                                       int random = Randomizer.nextInt(TangyoonKitchen.recipeHashMap.size());
                                       food = new ArrayList<>(TangyoonKitchen.recipeHashMap.keySet()).get(random);
                                       if (!TangyoonKitchen.this.Var.getProperty("2food", "0").equals(String.valueOf(food))
                                          && !TangyoonKitchen.this.Var.getProperty("3food", "0").equals(String.valueOf(food))) {
                                          TangyoonKitchen.this.Var.setProperty("1food", String.valueOf(food));
                                          TangyoonKitchen.this.Var.setProperty("1", "1");
                                          ok = true;
                                       }
                                    }

                                    int orderMob = -1;

                                    while (orderMob == -1) {
                                       int check = Randomizer.rand(0, 4);
                                       if (!TangyoonKitchen.this.orderMobs.contains(check)) {
                                          TangyoonKitchen.this.orderMobs.add(check);
                                          orderMob = check;
                                       }
                                    }

                                    if (food > -1) {
                                       TangyoonKitchen.this.gameRecipe
                                          .put(
                                             1,
                                             new TangyoonKitchen.TangyoonGameRecipe(
                                                0,
                                                TangyoonKitchen.this.orderKey++,
                                                orderMob,
                                                System.currentTimeMillis() + TangyoonKitchen.recipeHashMap.get(food).getTime(),
                                                TangyoonKitchen.recipeHashMap.get(food)
                                             )
                                          );
                                    }

                                    map.broadcastMessage(TangyoonKitchen.setObjectState("cookPlate1", 0));
                                    map.broadcastMessage(TangyoonKitchen.this.TangyoonObjectPacket_2());
                                 } else if (TangyoonKitchen.this.gameRecipe.get(1) != null) {
                                    long endTime = TangyoonKitchen.this.gameRecipe.get(1).getEndTime();
                                    if (System.currentTimeMillis() > endTime) {
                                       TangyoonKitchen.this.orderMobs.remove(Integer.valueOf(TangyoonKitchen.this.gameRecipe.get(1).getOrder()));
                                       TangyoonKitchen.this.Var.setProperty("1", "0");
                                       int userCount = TangyoonKitchen.this.fs.fInstance.get(TangyoonKitchen.this).size();

                                       for (int i = 0; i < userCount; i++) {
                                          int userId = TangyoonKitchen.this.fs.fInstance.get(TangyoonKitchen.this).get(i);
                                          if (TangyoonKitchen.this.Var
                                             .getProperty(String.valueOf(userId), "0")
                                             .equals(String.valueOf(TangyoonKitchen.this.gameRecipe.get(1).getRecipe().getFood()))) {
                                             TangyoonKitchen.this.Var.setProperty(String.valueOf(userId), "0");
                                          }
                                       }

                                       map.startMapEffect("주문이 취소되었네!", 5120216, 4000);
                                       map.broadcastMessage(TangyoonKitchen.this.TangyoonObjectPacket_2());
                                    }
                                 }

                                 if (kitchen2 == 0) {
                                    boolean ok = false;
                                    int food = -1;

                                    while (!ok) {
                                       int random = Randomizer.nextInt(TangyoonKitchen.recipeHashMap.size());
                                       food = new ArrayList<>(TangyoonKitchen.recipeHashMap.keySet()).get(random);
                                       if (!TangyoonKitchen.this.Var.getProperty("1food", "0").equals(String.valueOf(food))
                                          && !TangyoonKitchen.this.Var.getProperty("3food", "0").equals(String.valueOf(food))) {
                                          TangyoonKitchen.this.Var.setProperty("2food", String.valueOf(food));
                                          TangyoonKitchen.this.Var.setProperty("2", "1");
                                          ok = true;
                                       }
                                    }

                                    int orderMob = -1;

                                    while (orderMob == -1) {
                                       int check = Randomizer.rand(0, 4);
                                       if (!TangyoonKitchen.this.orderMobs.contains(check)) {
                                          TangyoonKitchen.this.orderMobs.add(check);
                                          orderMob = check;
                                       }
                                    }

                                    if (food > -1) {
                                       TangyoonKitchen.this.gameRecipe
                                          .put(
                                             2,
                                             new TangyoonKitchen.TangyoonGameRecipe(
                                                0,
                                                TangyoonKitchen.this.orderKey++,
                                                orderMob,
                                                System.currentTimeMillis() + TangyoonKitchen.recipeHashMap.get(food).getTime(),
                                                TangyoonKitchen.recipeHashMap.get(food)
                                             )
                                          );
                                    }

                                    map.broadcastMessage(TangyoonKitchen.setObjectState("cookPlate2", 0));
                                    map.broadcastMessage(TangyoonKitchen.this.TangyoonObjectPacket_2());
                                 } else if (TangyoonKitchen.this.gameRecipe.get(2) != null) {
                                    long endTime = TangyoonKitchen.this.gameRecipe.get(2).getEndTime();
                                    if (System.currentTimeMillis() > endTime) {
                                       TangyoonKitchen.this.orderMobs.remove(Integer.valueOf(TangyoonKitchen.this.gameRecipe.get(2).getOrder()));
                                       TangyoonKitchen.this.Var.setProperty("2", "0");
                                       int userCount = TangyoonKitchen.this.fs.fInstance.get(TangyoonKitchen.this).size();

                                       for (int ix = 0; ix < userCount; ix++) {
                                          int userId = TangyoonKitchen.this.fs.fInstance.get(TangyoonKitchen.this).get(ix);
                                          if (TangyoonKitchen.this.gameRecipe.get(2) != null
                                             && TangyoonKitchen.this.Var
                                                .getProperty(String.valueOf(userId), "0")
                                                .equals(String.valueOf(TangyoonKitchen.this.gameRecipe.get(2).getRecipe().getFood()))) {
                                             TangyoonKitchen.this.Var.setProperty(String.valueOf(userId), "0");
                                          }
                                       }

                                       map.startMapEffect("주문이 취소되었네!", 5120216, 4000);
                                       map.broadcastMessage(TangyoonKitchen.this.TangyoonObjectPacket_2());
                                    }
                                 }

                                 if (kitchen3 == 0) {
                                    boolean ok = false;
                                    int food = -1;

                                    while (!ok) {
                                       int random = Randomizer.nextInt(TangyoonKitchen.recipeHashMap.size());
                                       food = new ArrayList<>(TangyoonKitchen.recipeHashMap.keySet()).get(random);
                                       if (!TangyoonKitchen.this.Var.getProperty("1food", "0").equals(String.valueOf(food))
                                          && !TangyoonKitchen.this.Var.getProperty("2food", "0").equals(String.valueOf(food))) {
                                          TangyoonKitchen.this.Var.setProperty("3food", String.valueOf(food));
                                          TangyoonKitchen.this.Var.setProperty("3", "1");
                                          ok = true;
                                       }
                                    }

                                    int orderMob = -1;

                                    while (orderMob == -1) {
                                       int check = Randomizer.rand(0, 4);
                                       if (!TangyoonKitchen.this.orderMobs.contains(check)) {
                                          TangyoonKitchen.this.orderMobs.add(check);
                                          orderMob = check;
                                       }
                                    }

                                    if (food > -1) {
                                       TangyoonKitchen.this.gameRecipe
                                          .put(
                                             3,
                                             new TangyoonKitchen.TangyoonGameRecipe(
                                                0,
                                                TangyoonKitchen.this.orderKey++,
                                                orderMob,
                                                System.currentTimeMillis() + TangyoonKitchen.recipeHashMap.get(food).getTime(),
                                                TangyoonKitchen.recipeHashMap.get(food)
                                             )
                                          );
                                    }

                                    map.broadcastMessage(TangyoonKitchen.setObjectState("cookPlate3", 0));
                                    map.broadcastMessage(TangyoonKitchen.this.TangyoonObjectPacket_2());
                                 } else if (TangyoonKitchen.this.gameRecipe.get(3) != null) {
                                    long endTime = TangyoonKitchen.this.gameRecipe.get(3).getEndTime();
                                    if (System.currentTimeMillis() > endTime) {
                                       TangyoonKitchen.this.orderMobs.remove(Integer.valueOf(TangyoonKitchen.this.gameRecipe.get(3).getOrder()));
                                       TangyoonKitchen.this.Var.setProperty("3", "0");
                                       int userCount = TangyoonKitchen.this.fs.fInstance.get(TangyoonKitchen.this).size();

                                       for (int ixx = 0; ixx < userCount; ixx++) {
                                          int userId = TangyoonKitchen.this.fs.fInstance.get(TangyoonKitchen.this).get(ixx);
                                          if (TangyoonKitchen.this.gameRecipe.get(3) != null
                                             && TangyoonKitchen.this.Var
                                                .getProperty(String.valueOf(userId), "0")
                                                .equals(String.valueOf(TangyoonKitchen.this.gameRecipe.get(3).getRecipe().getFood()))) {
                                             TangyoonKitchen.this.Var.setProperty(String.valueOf(userId), "0");
                                          }
                                       }

                                       map.startMapEffect("주문이 취소되었네!", 5120216, 4000);
                                       map.broadcastMessage(TangyoonKitchen.this.TangyoonObjectPacket_2());
                                    }
                                 }
                              }
                           }
                        }
                     },
                     2000L,
                     29000L
                  )
            );
         Field field = this.field(993194500);
         if (field.getNumMonsters() < 1) {
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833966), new Point(474, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833967), new Point(474, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833968), new Point(474, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833966), new Point(660, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833967), new Point(660, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833968), new Point(660, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833966), new Point(838, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833967), new Point(838, Randomizer.rand(-400, 200)), true);
            field.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833968), new Point(838, Randomizer.rand(-400, 200)), true);
         }
      }
   }

   public void clearGame() {
      if (this.eventSchedules.get("clearGame") == null) {
         this.eventSchedules.put("clearGame", Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               if (!TangyoonKitchen.this.dispose) {
                  Field map = TangyoonKitchen.this.field(993194500);
                  if (map.getCharactersSize() > 0) {
                     for (MapleCharacter user : new ArrayList<>(map.getCharacters())) {
                        user.updateOneInfo(501555, "start", "0");
                        user.updateOneInfo(501555, "complete", "1");
                        user.changeMap(993194401);
                     }
                  }
               }
            }
         }, 5000L));
      }
   }

   public void PickUpFood(MapleCharacter user, int food) {
      String pickUpFoodID = "2024020";
      String getFood = "Sound/MiniGame.img/TyoonKitchen/get_bread";
      switch (food) {
         case 1:
            pickUpFoodID = "2024021";
            getFood = "Sound/MiniGame.img/TyoonKitchen/get_meat";
            break;
         case 2:
            pickUpFoodID = "2024022";
            getFood = "Sound/MiniGame.img/TyoonKitchen/get_egg";
            break;
         case 3:
            pickUpFoodID = "2024023";
            getFood = "Sound/MiniGame.img/TyoonKitchen/get_vegetable";
            break;
         case 4:
            pickUpFoodID = "2024024";
            getFood = "Sound/MiniGame.img/TyoonKitchen/get_fish";
            break;
         case 5:
            pickUpFoodID = "2024025";
            getFood = "Sound/MiniGame.img/TyoonKitchen/get_fryingpan";
            break;
         case 6:
            pickUpFoodID = "2024026";
            getFood = "Sound/MiniGame.img/TyoonKitchen/get_pot";
            break;
         case 7:
            pickUpFoodID = "2024027";
            getFood = "Sound/MiniGame.img/TyoonKitchen/get_knife";
      }

      this.setVar(String.valueOf(user.getId()), pickUpFoodID);
      user.getMap().broadcastMessage(CField.environmentChange(getFood, 5, 100));
      user.getMap().broadcastMessage(CField.environmentChange("Sound/MiniGame.img/CrazySplash/install", 5, 100));
      user.getMap().broadcastMessage(this.TangyoonObjectPacket_2());
      user.send(TangyoonPickUp());
   }

   public static byte[] TangyoonMultiMotion(int userId, boolean attack, int setfood) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.TANGYOON_OBJECT_MULTI_MOTION.getValue());
      p.writeInt(userId);
      p.write(attack);
      if (attack) {
         p.writeInt(289);
         switch (setfood) {
            case 5:
               p.writeInt(1500);
               break;
            case 6:
            case 7:
               p.writeInt(5000);
               break;
            default:
               p.writeInt(500);
         }

         p.write(0);
         p.writeInt(0);
      }

      return p.getPacket();
   }

   public static byte[] TangyoonOrder(int order) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.TANGYOON_ORDER.getValue());
      p.writeInt(order);
      return p.getPacket();
   }

   public static byte[] TangyoonObjectPacket_1() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.TANGYOON_OBJECT_PACKET_1.getValue());
      p.writeInt(8);

      for (int i = 0; i < 8; i++) {
         p.writeInt(2024020 + i);
         switch (i) {
            case 5:
               p.writeInt(1);
               break;
            case 6:
               p.writeInt(3);
               break;
            case 7:
               p.writeInt(2);
               break;
            default:
               p.writeInt(0);
         }

         switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
               p.writeInt(500);
               break;
            case 5:
               p.writeInt(1500);
               break;
            case 6:
            case 7:
               p.writeInt(5000);
         }

         switch (i) {
            case 5:
               p.writeMapleAsciiString("Sound/MiniGame.img/TyoonKitchen/cook_bake");
               break;
            case 6:
               p.writeMapleAsciiString("Sound/MiniGame.img/TyoonKitchen/cook_boil");
               break;
            case 7:
               p.writeMapleAsciiString("Sound/MiniGame.img/TyoonKitchen/cook_slice");
               break;
            default:
               p.writeMapleAsciiString("");
         }
      }

      return p.getPacket();
   }

   protected byte[] TangyoonObjectPacket_2() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.TANGYOON_OBJECT_PACKET_2.getValue());
      p.writeInt(3);

      for (int i = 0; i < 3; i++) {
         p.writeInt(i);
         boolean notReadyFood = this.Var.getProperty(String.valueOf(i + 1), "0").equals("0");
         p.writeInt(Integer.parseInt(this.Var.getProperty(String.valueOf(i + 1), "0")));
         p.writeInt(i);
         if (notReadyFood) {
            p.writeInt(0);
            p.writeInt(-1);
            p.writeInt(0);
            p.writeInt(0);
            p.writeInt(0);
            p.writeInt(0);
            p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
         } else {
            p.writeInt(this.gameRecipe.get(i + 1).getCheckPoint());
            p.writeInt(this.gameRecipe.get(i + 1).getOrder());
            p.writeInt(this.gameRecipe.get(i + 1).getOrderKey());
            p.writeInt(this.gameRecipe.get(i + 1).getRecipe().getTime());
            p.writeInt(this.gameRecipe.get(i + 1).getRecipe().getFood());
            p.writeInt(this.gameRecipe.get(i + 1).getRecipe().getScore());
            int recipeSize = this.gameRecipe.get(i + 1).getRecipe().getRecipes().size();
            p.writeInt(recipeSize);

            for (int r = 0; r < recipeSize; r++) {
               p.writeInt(r);
               p.writeInt(this.gameRecipe.get(i + 1).getRecipe().getRecipes().get(r));
            }
         }
      }

      p.writeInt(8);
      p.writeInt(0);
      p.writeInt(0);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FE FF FF 81 FF FF FF 42 FF FF FF DB FF FF FF"));
      p.writeInt(2024020);
      p.writeInt(289);
      p.writeMapleAsciiString("빵");
      p.writeInt(1);
      p.writeInt(1);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FE FF FF F6 FF FF FF 42 FF FF FF 50 00 00 00"));
      p.writeInt(2024021);
      p.writeInt(289);
      p.writeMapleAsciiString("고기");
      p.writeInt(2);
      p.writeInt(2);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FE FF FF 11 FF FF FF 42 FF FF FF 6B FF FF FF"));
      p.writeInt(2024022);
      p.writeInt(289);
      p.writeMapleAsciiString("계란");
      p.writeInt(3);
      p.writeInt(3);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FE FF FF A3 FE FF FF 42 FF FF FF FD FE FF FF"));
      p.writeInt(2024023);
      p.writeInt(289);
      p.writeMapleAsciiString("채소");
      p.writeInt(4);
      p.writeInt(4);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FE FF FF 5F 00 00 00 42 FF FF FF B9 00 00 00"));
      p.writeInt(2024024);
      p.writeInt(289);
      p.writeMapleAsciiString("생선");
      p.writeInt(5);
      p.writeInt(5);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("32 00 00 00 E0 FE FF FF A0 00 00 00 4E FF FF FF"));
      p.writeInt(2024025);
      p.writeInt(289);
      p.writeMapleAsciiString("굽기");
      p.writeInt(6);
      p.writeInt(6);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("35 00 00 00 7E FF FF FF A3 00 00 00 EC FF FF FF"));
      p.writeInt(2024026);
      p.writeInt(289);
      p.writeMapleAsciiString("끓이기");
      p.writeInt(7);
      p.writeInt(7);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("33 00 00 00 19 00 00 00 A1 00 00 00 87 00 00 00"));
      p.writeInt(2024027);
      p.writeInt(289);
      p.writeMapleAsciiString("썰기");
      p.writeInt(3);
      p.writeInt(0);
      p.writeInt(0);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("01 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("75 FF FF FF E3 FE FF FF E3 FF FF FF 51 FF FF FF"));
      p.writeInt(2024072);
      p.writeInt(289);
      p.writeMapleAsciiString("cookPlate1");
      p.writeInt(1);
      p.writeInt(1);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("01 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("73 FF FF FF 7F FF FF FF E1 FF FF FF ED FF FF FF"));
      p.writeInt(2024073);
      p.writeInt(289);
      p.writeMapleAsciiString("cookPlate2");
      p.writeInt(2);
      p.writeInt(2);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("01 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("75 FF FF FF 19 00 00 00 E3 FF FF FF 87 00 00 00"));
      p.writeInt(2024074);
      p.writeInt(289);
      p.writeMapleAsciiString("cookPlate3");
      p.writeInt(5);
      p.writeInt(0);
      p.writeInt(0);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("02 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(1500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("C0 03 00 00 29 FE FF FF 24 04 00 00 8D FE FF FF"));
      p.writeInt(2120000);
      p.writeInt(289);
      p.writeMapleAsciiString("1번 손님");
      p.writeInt(1);
      p.writeInt(1);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("02 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(1500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("C0 03 00 00 C0 FE FF FF 24 04 00 00 24 FF FF FF"));
      p.writeInt(2120000);
      p.writeInt(289);
      p.writeMapleAsciiString("2번 손님");
      p.writeInt(2);
      p.writeInt(2);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("02 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(1500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("C0 03 00 00 59 FF FF FF 24 04 00 00 BD FF FF FF"));
      p.writeInt(2120000);
      p.writeInt(289);
      p.writeMapleAsciiString("3번 손님");
      p.writeInt(3);
      p.writeInt(3);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("02 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(1500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("C0 03 00 00 ED FF FF FF 24 04 00 00 51 00 00 00"));
      p.writeInt(2120000);
      p.writeInt(289);
      p.writeMapleAsciiString("4번 손님");
      p.writeInt(4);
      p.writeInt(4);
      p.encodeBuffer(HexTool.getByteArrayFromHexString("02 00 00 00"));
      p.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      p.writeInt(1500);
      p.writeMapleAsciiString("");
      p.encodeBuffer(HexTool.getByteArrayFromHexString("C0 03 00 00 82 00 00 00 24 04 00 00 E6 00 00 00"));
      p.writeInt(2120000);
      p.writeInt(289);
      p.writeMapleAsciiString("5번 손님");
      int userCount = this.fs.fInstance.get(this).size();
      p.writeInt(userCount);

      for (int ix = 0; ix < userCount; ix++) {
         int userId = this.fs.fInstance.get(this).get(ix);
         p.writeInt(userId);
         int userPick = Integer.parseInt(this.getVar(String.valueOf(userId)));
         p.writeInt(userPick);
         if (userPick >= 2024028 && userPick <= 2024071) {
            int ovenCode = 0;

            for (Integer k : this.gameRecipe.keySet()) {
               if (this.gameRecipe.get(k).getRecipe().getFood() == userPick) {
                  ovenCode = k - 1;
               }
            }

            p.writeInt(ovenCode);
         } else {
            p.encodeBuffer(HexTool.getByteArrayFromHexString("FF FF FF FF"));
         }

         p.writeInt(0);
      }

      p.writeInt(Math.min(50000, Integer.parseInt(this.Var.getProperty("score", "0"))));
      return p.getPacket();
   }

   public static byte[] TangyoonBadOrCool(int userId, String sEff) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.TANGYOON_BAD_OR_COOL.getValue());
      p.writeInt(userId);
      p.writeMapleAsciiString(sEff);
      p.write(0);
      return p.getPacket();
   }

   public static byte[] TangyoonPickUp() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.TANGYOON_PICK_UP.getValue());
      return p.getPacket();
   }

   public static byte[] setObjectState(String object, int status) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.SET_OBJECT_STATE.getValue());
      p.writeMapleAsciiString(object);
      p.writeInt(status);
      return p.getPacket();
   }

   public static void loadTangyoonRecipe() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "TangyoonRecipe.data");

      for (Table child : table.list()) {
         int food = Integer.parseInt(child.getName());
         int time = Integer.parseInt(child.getProperty("time"));
         int score = Integer.parseInt(child.getProperty("score"));
         List<Integer> recipes = new ArrayList<>();

         for (Integer r : child.getChild("recipes").values(0)) {
            recipes.add(r);
         }

         recipeHashMap.put(food, new TangyoonKitchen.TangyoonRecipe(time, food, score, recipes));
      }
   }

   public static class TangyoonGameRecipe {
      int checkPoint;
      int orderKey;
      int order;
      long endTime;
      TangyoonKitchen.TangyoonRecipe recipe;

      public TangyoonGameRecipe(int checkPoint, int orderKey, int order, long endTime, TangyoonKitchen.TangyoonRecipe recipe) {
         this.checkPoint = checkPoint;
         this.orderKey = orderKey;
         this.order = order;
         this.endTime = endTime;
         this.recipe = recipe;
      }

      public int getCheckPoint() {
         return this.checkPoint;
      }

      public void addCheckPoint() {
         this.checkPoint++;
      }

      public int getOrderKey() {
         return this.orderKey;
      }

      public int getOrder() {
         return this.order;
      }

      public long getEndTime() {
         return this.endTime;
      }

      public TangyoonKitchen.TangyoonRecipe getRecipe() {
         return this.recipe;
      }
   }

   public static class TangyoonRecipe {
      int time;
      int food;
      int score;
      List<Integer> recipes;

      public TangyoonRecipe(int time, int food, int score, List<Integer> recipes) {
         this.time = time;
         this.food = food;
         this.score = score;
         this.recipes = recipes;
      }

      public int getTime() {
         return this.time;
      }

      public int getFood() {
         return this.food;
      }

      public int getScore() {
         return this.score;
      }

      public List<Integer> getRecipes() {
         return this.recipes;
      }
   }
}
