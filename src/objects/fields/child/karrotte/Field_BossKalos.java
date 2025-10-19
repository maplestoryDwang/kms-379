package objects.fields.child.karrotte;

import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import network.decode.PacketDecoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.effect.child.TextEffect;
import objects.fields.Field;
import objects.fields.child.karrotte.guardian.EyeOfAbyss;
import objects.fields.child.karrotte.guardian.EyeOfAbyssAttackEntry;
import objects.fields.child.karrotte.guardian.EyeOfRedemption;
import objects.fields.child.karrotte.guardian.FighterPlane;
import objects.fields.child.karrotte.guardian.Guardian;
import objects.fields.child.karrotte.guardian.GuardianEntry;
import objects.fields.child.karrotte.guardian.GuardianType;
import objects.fields.child.karrotte.guardian.MysticShot;
import objects.fields.child.karrotte.guardian.SphereOfOdium;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.skills.SkillFactory;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Field_BossKalos extends Field {
   private int CHARGE_DELAY = 1000;
   private final int CHARGE_VALUE = 20;
   private final int MAX_CHARGE_VALUE = 100;
   private static GuardianType[] assaultOrders = new GuardianType[]{
      GuardianType.EyeOfRedemption, GuardianType.FighterPlane, GuardianType.SphereOfOdium, GuardianType.EyeOfTheAbyss
   };
   protected boolean canClear = true;
   protected Guardian guardian;

   @Override
   public String getMode() {
      return "normal";
   }

   public Field_BossKalos(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.guardian = null;
      this.canClear = true;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.UIPacket.endInGameDirectionMode(0));
      KalosAction.InitPacket.Gauge gauge = new KalosAction.InitPacket.Gauge(this.CHARGE_DELAY, 20, 100);
      gauge.sendPacket(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      super.onMobSkill(mob, skillID, skillLevel);
      if (skillID == 268 && skillLevel == 1) {
         this.doAssault();
      }
   }

   public MapleMonster findBoss() {
      int[] mobs = new int[]{8880800, 8880803, 8880804, 8880805, 8880806};

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         for (int id : mobs) {
            if (mob.getId() == id) {
               return mob;
            }
         }
      }

      return null;
   }

   public void doAction(PacketDecoder packet, MapleCharacter player) {
      int actType = packet.readInt();
      KalosActionType actionType = KalosActionType.getType(actType);
      int subActType = packet.readInt();
      KalosActionSubType.Recv actionSubType = KalosActionSubType.Recv.getType(subActType);
      MapleMonster boss = this.findBoss();
      if (boss != null) {
         if (actionType != null && actionSubType != null) {
            if (actionType == KalosActionType.AssaultAction) {
               int actionSN = packet.readInt();
               switch (actionSubType) {
                  case FighterPlaneAttack_Receive:
                     GuardianEntry guardian = this.guardian.findGuardian(GuardianType.FighterPlane);
                     if (guardian == null) {
                        return;
                     }

                     if (guardian.getActionSN() != actionSN) {
                        return;
                     }

                     int direction = packet.readInt();
                     new Point(packet.readInt(), packet.readInt());
                     Point targetPosition = new Point(Randomizer.rand(-545, 1640), -657);
                     int unk4 = 330;
                     int unk2 = 150;
                     int attackInterval = 10000;
                     int attackDelay = 200;
                     int deactiveHitCount = this.getCharactersSize() * 2;
                     int unk3 = 2;
                     Rect rect = FighterPlane.basicRect;
                     long time = System.currentTimeMillis();
                     FighterPlane fighterPlane = (FighterPlane)guardian;
                     fighterPlane.setUnk2(unk2);
                     fighterPlane.setAttackInterval(attackInterval);
                     fighterPlane.setAttackDelay(attackDelay);
                     fighterPlane.setDeactiveHitCount(deactiveHitCount);
                     fighterPlane.setUnk3(unk3);
                     fighterPlane.setDirection((byte)(direction != -1 ? 1 : 0));
                     fighterPlane.setPosition(targetPosition);
                     fighterPlane.setRect(rect);
                     fighterPlane.setTime(time);
                     fighterPlane.setActionSN(actionSN + 1);
                     KalosAction.AssaultAction.FighterPlane fp = new KalosAction.AssaultAction.FighterPlane(guardian.getActionSN(), unk4, fighterPlane);
                     fp.broadcastPacket(this);
                     break;
                  case EyeOfRedemptionAttack_Receive:
                     GuardianEntry guardianxx = this.guardian.findGuardian(GuardianType.EyeOfRedemption);
                     if (guardianxx == null) {
                        return;
                     }

                     if (guardianxx.getActionSN() != actionSN) {
                        return;
                     }

                     int bulletCount = 1;
                     if (boss.getHPPercent() < 50) {
                        bulletCount = 2;
                     }

                     ArrayList<Point> positionList = new ArrayList<>();
                     ArrayList<MapleCharacter> playersx = new ArrayList<>(this.getCharactersThreadsafe());
                     Collections.shuffle(playersx);
                     Point lastPos = null;

                     for (int i = 0; i < bulletCount; i++) {
                        MapleCharacter pick = null;
                        Point position = null;

                        try {
                           pick = playersx.get(i);
                        } catch (Exception var25) {
                           pick = player;
                        }

                        if (pick != null) {
                           position = new Point(pick.getTruePosition());
                        } else if (lastPos == null) {
                           position = new Point(0, 0);
                        } else {
                           int posDiff = (ThreadLocalRandom.current().nextInt(6) - 3) * 10;

                           while (posDiff == 0) {
                              posDiff = (ThreadLocalRandom.current().nextInt(6) - 3) * 10;
                           }

                           position = new Point(lastPos.x + posDiff, lastPos.y);
                        }

                        positionList.add(position);
                        lastPos = new Point(position.x, position.y);
                     }

                     guardianxx.setActionSN(actionSN + 1);
                     KalosAction.AssaultAction.EyeOfRedemptionAttack attack = new KalosAction.AssaultAction.EyeOfRedemptionAttack(
                        guardianxx.getActionSN(), positionList
                     );
                     attack.broadcastPacket(this);
                     break;
                  case EyeOfAbyssAttack_Receive:
                     GuardianEntry guardianx = this.guardian.findGuardian(GuardianType.EyeOfTheAbyss);
                     if (guardianx == null) {
                        return;
                     }

                     if (guardianx.getActionSN() != actionSN) {
                        return;
                     }

                     bulletCount = 3;
                     if (boss.getHPPercent() < 50) {
                        bulletCount = 6;
                     }

                     int playerID = player.getId();
                     ArrayList<Integer> playerIDList = new ArrayList<>();
                     playerIDList.add(playerID);

                     try {
                        ArrayList<MapleCharacter> players = new ArrayList<>(this.getCharactersThreadsafe());
                        Collections.shuffle(players);
                        MapleCharacter pickPlayer = players.stream().findAny().orElse(null);
                        if (pickPlayer != null) {
                           playerID = pickPlayer.getId();
                        }

                        if (playerID != player.getId()) {
                           playerIDList.add(playerID);
                        }
                     } catch (Exception var24) {
                     }

                     ArrayList<EyeOfAbyssAttackEntry> attacks = new ArrayList<>();

                     for (int i = 0; i < bulletCount; i++) {
                        if (i >= playerIDList.size()) {
                           playerID = playerIDList.get(ThreadLocalRandom.current().nextInt(playerIDList.size()));
                        }

                        attacks.add(new EyeOfAbyssAttackEntry(17 + i * 6, 1, playerID, 0));
                     }

                     guardianx.setActionSN(actionSN + 1);
                     KalosAction.AssaultAction.EyeOfAbyssAttack attack2 = new KalosAction.AssaultAction.EyeOfAbyssAttack(guardianx.getActionSN(), attacks);
                     attack2.broadcastPacket(this);
                     break;
                  case EyeOfAbyssAttack_HitBlind:
                     KalosAction.AssaultAction.EyeOfAbyssDoBlind doBlind = new KalosAction.AssaultAction.EyeOfAbyssDoBlind(actionSN, player.getId());
                     doBlind.broadcastPacket(this);
               }
            } else if (actionType == KalosActionType.Init) {
               switch (actionSubType) {
                  case CreateMysticShotRequest_Receive:
                     int tick = packet.readInt();
                     Point position = new Point(packet.readInt(), packet.readInt());
                     int createDelay = 1500;
                     KalosAction.InitPacket.CreateMysticShot createMysticShot = new KalosAction.InitPacket.CreateMysticShot(
                        new MysticShot(player.getId(), createDelay, tick, position)
                     );
                     createMysticShot.broadcastPacket(this);
                     break;
                  case HitMysticShot_Top: {
                     int unk = packet.readInt();
                     int objectID = packet.readInt();
                     KalosAction.InitPacket.HitMysticShotTop hitMysticShot = new KalosAction.InitPacket.HitMysticShotTop(player.getId(), objectID);
                     hitMysticShot.broadcastPacket(this);
                     break;
                  }
                  case HitMysticShot_Bottom: {
                     int unk = packet.readInt();
                     int objectID = packet.readInt();
                     KalosAction.InitPacket.HitMysticShotBottom hitMysticShot = new KalosAction.InitPacket.HitMysticShotBottom(player.getId(), objectID);
                     hitMysticShot.broadcastPacket(this);
                     break;
                  }
                  case HitMysticShot_Left:
                     GuardianEntry eor = this.guardian.findGuardian(GuardianType.EyeOfRedemption);
                     if (eor == null) {
                        return;
                     }

                     if (!eor.isAssault()) {
                        return;
                     }

                     int deactCntx = eor.getDeactiveHitCount();
                     eor.setCurrentHitCount(eor.getCurrentHitCount() + 1);
                     int currentHitx = eor.getCurrentHitCount();
                     int statusx = deactCntx - currentHitx;
                     if (deactCntx > 3) {
                        double people = deactCntx / 3;
                        statusx = (int)Math.ceil(statusx / people);
                     }

                     if (deactCntx <= currentHitx) {
                        KalosAction.AssaultAction.EyeOfRedemptionDeactive deactive = new KalosAction.AssaultAction.EyeOfRedemptionDeactive();
                        deactive.broadcastPacket(this);
                        eor.setCurrentHitCount(0);
                        eor.setActionSN(0);
                        eor.setAssault(false);
                     }

                     KalosAction.AssaultAction.EyeOfRedemptionStatus eStatus = new KalosAction.AssaultAction.EyeOfRedemptionStatus(player.getId(), statusx);
                     eStatus.broadcastPacket(this);
                     break;
                  case HitMysticShot_Right:
                     GuardianEntry eoa = this.guardian.findGuardian(GuardianType.EyeOfTheAbyss);
                     if (eoa == null) {
                        return;
                     }

                     if (!eoa.isAssault()) {
                        return;
                     }

                     int deactCnt = eoa.getDeactiveHitCount();
                     eoa.setCurrentHitCount(eoa.getCurrentHitCount() + 1);
                     int currentHit = eoa.getCurrentHitCount();
                     int status = deactCnt - currentHit;
                     if (deactCnt > 3) {
                        double people = deactCnt / 3;
                        status = (int)Math.ceil(status / people);
                     }

                     if (deactCnt <= currentHit) {
                        KalosAction.AssaultAction.EyeOfTheAbyssDeactive deactive = new KalosAction.AssaultAction.EyeOfTheAbyssDeactive();
                        deactive.broadcastPacket(this);
                        eoa.setCurrentHitCount(0);
                        eoa.setActionSN(0);
                        eoa.setAssault(false);
                     }

                     KalosAction.AssaultAction.EyeOfTheAbyssStatus eStatus3 = new KalosAction.AssaultAction.EyeOfTheAbyssStatus(player.getId(), status);
                     eStatus3.broadcastPacket(this);
                     break;
                  case FighterPlane_Deactive:
                     GuardianEntry plane = this.guardian.findGuardian(GuardianType.FighterPlane);
                     if (plane == null) {
                        return;
                     }

                     if (!plane.isAssault()) {
                        return;
                     }

                     int hitpos = packet.readInt();
                     packet.readInt();
                     Point pos = new Point(packet.readInt(), packet.readInt());
                     int deactiveCnt = plane.getDeactiveHitCount();
                     plane.setCurrentHitCount(plane.getCurrentHitCount() + 1);
                     currentHit = plane.getCurrentHitCount();
                     boolean disabled = false;
                     if (deactiveCnt <= currentHit) {
                        KalosAction.AssaultAction.FighterPlaneDeactive deactive = new KalosAction.AssaultAction.FighterPlaneDeactive(pos);
                        deactive.broadcastPacket(this);
                        plane.setCurrentHitCount(0);
                        plane.setActionSN(0);
                        plane.setAssault(false);
                        disabled = true;
                     }

                     KalosAction.AssaultAction.FighterPlaneStatus fStatus = new KalosAction.AssaultAction.FighterPlaneStatus(hitpos, 1, disabled ? 0 : 1);
                     fStatus.broadcastPacket(this);
                     break;
                  case SphereOfOdium_Deactive:
                     GuardianEntry soo = this.guardian.findGuardian(GuardianType.SphereOfOdium);
                     if (soo == null) {
                        return;
                     }

                     if (!soo.isAssault()) {
                        return;
                     }

                     hitpos = packet.readInt();
                     packet.readInt();
                     deactiveCnt = soo.getDeactiveHitCount();
                     soo.setCurrentHitCount(soo.getCurrentHitCount() + 1);
                     currentHit = soo.getCurrentHitCount();
                     disabled = false;
                     if (deactiveCnt <= currentHit) {
                        KalosAction.AssaultAction.SphereOfOdiumDeactive deactive = new KalosAction.AssaultAction.SphereOfOdiumDeactive();
                        deactive.broadcastPacket(this);
                        soo.setCurrentHitCount(0);
                        soo.setActionSN(0);
                        soo.setAssault(false);
                        disabled = true;
                     }

                     KalosAction.AssaultAction.SphereOfOdiumStatus sStatus = new KalosAction.AssaultAction.SphereOfOdiumStatus(hitpos, 1, disabled ? 0 : 1);
                     sStatus.broadcastPacket(this);
               }
            }
         }
      }
   }

   public void doAssault() {
      GuardianEntry pick = null;
      List<GuardianType> assaults = Arrays.asList(assaultOrders);
      Collections.shuffle(assaults);

      for (GuardianType type : assaults) {
         for (GuardianEntry entry : new ArrayList<>(this.guardian.getGuardians())) {
            if (entry.getType() == type && !entry.isAssault()) {
               pick = entry;
               break;
            }
         }

         if (pick != null) {
            break;
         }
      }

      if (pick != null) {
         switch (pick.getType()) {
            case EyeOfRedemption: {
               int unk = 100;
               int angle = 360;
               int attackInterval = 15000;
               int deactiveHitCount = this.getCharactersSize() * 3;
               EyeOfRedemption eyeOfRedemption = (EyeOfRedemption)pick;
               eyeOfRedemption.setUnk2(unk);
               eyeOfRedemption.setAngle(angle);
               eyeOfRedemption.setAttackInterval(attackInterval);
               eyeOfRedemption.setDeactiveHitCount(deactiveHitCount);
               KalosAction.AssaultAction.EyeOfRedemption eor = new KalosAction.AssaultAction.EyeOfRedemption(eyeOfRedemption);
               eor.broadcastPacket(this);
               this.broadcastMessage(CWvsContext.InfoPacket.brownMessage("T-boy의 간섭으로 구속의 눈이 잠에서 깨어납니다."));
               KalosAction.GuardianAction.AssaultGuardian assaultGuardian = new KalosAction.GuardianAction.AssaultGuardian(GuardianType.EyeOfRedemption, 60000);
               assaultGuardian.broadcastPacket(this);
               break;
            }
            case FighterPlane: {
               int actionSN = 0;
               int unk4 = 330;
               int unk2 = 150;
               int attackInterval = 10000;
               int attackDelay = 200;
               int deactiveHitCount = this.getCharactersSize() * 2;
               int unk3 = 2;
               byte direction = 1;
               Point position = new Point(Randomizer.rand(-545, 1640), -657);
               Rect rect = FighterPlane.basicRect;
               long time = System.currentTimeMillis();
               FighterPlane fighterPlane = (FighterPlane)pick;
               fighterPlane.setUnk2(unk2);
               fighterPlane.setAttackInterval(attackInterval);
               fighterPlane.setAttackDelay(attackDelay);
               fighterPlane.setDeactiveHitCount(deactiveHitCount);
               fighterPlane.setUnk3(unk3);
               fighterPlane.setDirection(direction);
               fighterPlane.setPosition(position);
               fighterPlane.setRect(rect);
               fighterPlane.setTime(time);
               KalosAction.AssaultAction.FighterPlane fp = new KalosAction.AssaultAction.FighterPlane(actionSN, unk4, fighterPlane);
               fp.broadcastPacket(this);
               this.broadcastMessage(CWvsContext.InfoPacket.brownMessage("T-boy의 간섭으로 포격 전투기가 활성화됩니다."));
               KalosAction.GuardianAction.AssaultGuardian assaultGuardian = new KalosAction.GuardianAction.AssaultGuardian(GuardianType.FighterPlane, 60000);
               assaultGuardian.broadcastPacket(this);
               break;
            }
            case SphereOfOdium: {
               int unk4 = 6;
               byte unk5 = 0;
               byte unk6 = 1;
               int unk7 = 500;
               int unk8 = 720;
               int deactiveHitCount = this.getCharactersSize() * 2;
               SphereOfOdium sphereOfOdium = (SphereOfOdium)pick;
               sphereOfOdium.setUnk4(unk4);
               sphereOfOdium.setUnk5(unk5);
               sphereOfOdium.setUnk6(unk6);
               sphereOfOdium.setUnk7(unk7);
               sphereOfOdium.setUnk8(unk8);
               sphereOfOdium.setDeactiveHitCount(deactiveHitCount);
               KalosAction.AssaultAction.SphereOfOdium soo = new KalosAction.AssaultAction.SphereOfOdium(sphereOfOdium);
               soo.broadcastPacket(this);
               this.broadcastMessage(CWvsContext.InfoPacket.brownMessage("T-boy의 간섭으로 오디움의 구체가 적을 감지합니다."));
               KalosAction.GuardianAction.AssaultGuardian assaultGuardian = new KalosAction.GuardianAction.AssaultGuardian(GuardianType.SphereOfOdium, 60000);
               assaultGuardian.broadcastPacket(this);
               break;
            }
            case EyeOfTheAbyss: {
               int attackInterval = 15000;
               int attackDelay = 500;
               int deactiveHitCount = this.getCharactersSize() * 3;
               ArrayList<EyeOfAbyssAttackEntry> bullets = new ArrayList<>();
               int bulletCount = 3;

               for (int i = 0; i < bulletCount; i++) {
                  bullets.add(new EyeOfAbyssAttackEntry(17 + i * 6, 1, 1, 0));
               }

               EyeOfAbyss eyeOfAbyss = (EyeOfAbyss)pick;
               eyeOfAbyss.setAttackInterval(attackInterval);
               eyeOfAbyss.setAttackDelay(attackDelay);
               eyeOfAbyss.setDeactiveHitCount(deactiveHitCount);
               eyeOfAbyss.setBullets(bullets);
               KalosAction.AssaultAction.EyeOfAbyss eoa = new KalosAction.AssaultAction.EyeOfAbyss(eyeOfAbyss);
               eoa.broadcastPacket(this);
               this.broadcastMessage(CWvsContext.InfoPacket.brownMessage("T-boy의 간섭으로 심연의 눈이 잠에서 깨어납니다."));
               KalosAction.GuardianAction.AssaultGuardian assaultGuardian = new KalosAction.GuardianAction.AssaultGuardian(GuardianType.EyeOfTheAbyss, 60000);
               assaultGuardian.broadcastPacket(this);
            }
         }

         pick.setAssault(true);
      } else {
         this.broadcastMessage(CField.MapEff("Map/Effect.img/hillah/fail2"));
         this.broadcastMessage(CWvsContext.InfoPacket.brownMessage("자격을 확인 받지 못해 다음 관문으로 넘어가지 못하고 추방당했습니다."));
         TextEffect e = new TextEffect("#r#fn나눔고딕 ExtraBold##fs26#자격이 없는 자에게 문은 열리지 않는다...", 100, 2500, 4, 0, 0, 1, 0);
         this.broadcastMessage(e.encodeForLocal());
         this.canClear = false;

         for (MapleCharacter chr : this.getCharacters()) {
            chr.setRegisterTransferField(410005005);
            chr.setRegisterTransferFieldTime(System.currentTimeMillis() + 4500L);
         }
      }
   }

   public void doFieldAttack(int skillID, int level, int refMobID, int mobSkill, int minBullet, int maxBullet) {
      this.broadcastMessage(SkillFactory.EncodeFieldSkill(skillID, level, refMobID, mobSkill, minBullet, maxBullet));
   }
}
