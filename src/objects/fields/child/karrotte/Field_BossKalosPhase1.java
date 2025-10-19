package objects.fields.child.karrotte;

import java.awt.Point;
import java.util.ArrayList;
import objects.effect.child.TextEffect;
import objects.fields.child.karrotte.guardian.EyeOfAbyss;
import objects.fields.child.karrotte.guardian.EyeOfRedemption;
import objects.fields.child.karrotte.guardian.FighterPlane;
import objects.fields.child.karrotte.guardian.Guardian;
import objects.fields.child.karrotte.guardian.GuardianEntry;
import objects.fields.child.karrotte.guardian.GuardianType;
import objects.fields.child.karrotte.guardian.SphereOfOdium;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_BossKalosPhase1 extends Field_BossKalos {
   private static final Point EYE_OF_REDEMPTION_POS = new Point(-612, -233);
   private static final Point EYE_OF_THE_ABYSS_POS = new Point(1626, -233);
   private static final Point FIGHTER_PLANE_POS = new Point(630, -553);
   private static final Point SPHERE_OF_ODIUM_POS = new Point(0, 0);
   private long nextAssaultTime = 0L;
   private long nextSpecialAssaultTime = 0L;
   private long nextFieldSkillTime = 0L;
   private int AssaultInterval = 60000;
   private int SpecialAssaultInterval = 150000;
   private int FieldSkillInterval = 15000;
   private int guardianRefMobID = 8880801;
   private int nextPhaseMob = 8880811;

   public Field_BossKalosPhase1(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   public boolean nextPhase() {
      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (mob.getId() == this.nextPhaseMob) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.getCharactersSize() != 0) {
         if (this.nextPhase()) {
            if (!this.canClear) {
               return;
            }

            MapleCharacter player = null;

            for (MapleCharacter chr : this.getCharacters()) {
               if (chr.getParty() != null) {
                  player = chr;
                  break;
               }
            }

            if (player != null) {
               int warpTo = this.getId() + 20;
               if (player.getParty().getLeader().isSkipIntro()) {
                  warpTo += 20;
               }

               for (MapleCharacter chrx : player.getPartyMembers()) {
                  if (chrx.getMapId() == this.getId() - 20 || chrx.getMapId() == this.getId() && chrx.getRegisterTransferFieldTime() == 0L) {
                     TextEffect e = new TextEffect("#fn나눔고딕 ExtraBold##fs32##r#e아직 심판은 끝나지 않았다.", 100, 2500, 4, 0, 0, 1, 0);
                     chrx.send(e.encodeForLocal());
                     chrx.setRegisterTransferField(warpTo);
                     chrx.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
                  }
               }
            }
         }

         MapleMonster boss;
         if ((boss = this.findBoss()) != null) {
            if (this.nextAssaultTime <= System.currentTimeMillis()) {
               if (this.nextAssaultTime != 0L) {
                  this.doAssault();
               }

               this.nextAssaultTime = System.currentTimeMillis() + this.AssaultInterval;
            }

            if (this.nextSpecialAssaultTime <= System.currentTimeMillis()) {
               if (this.nextSpecialAssaultTime != 0L) {
                  boss.addOnetimeFsmSkill(1);
               }

               this.nextSpecialAssaultTime = System.currentTimeMillis() + this.SpecialAssaultInterval;
            }

            if (this.nextFieldSkillTime <= System.currentTimeMillis()) {
               if (this.nextFieldSkillTime != 0L && (double)boss.getHp() / boss.getMobMaxHp() <= 50.0) {
                  this.FieldSkillInterval = 10000;
               }

               this.nextFieldSkillTime = System.currentTimeMillis() + this.FieldSkillInterval;
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.nextAssaultTime = 0L;
      this.nextSpecialAssaultTime = 0L;
      this.nextFieldSkillTime = 0L;
      MapleMonster guardianAttackMob = MapleLifeFactory.getMonster(this.guardianRefMobID);
      this.spawnMonster_sSack(guardianAttackMob, new Point(398, 772), 0, false, true);
      int phase1BossMobID = 8880800;
      MapleMonster phase1Boss = MapleLifeFactory.getMonster(phase1BossMobID);
      this.spawnMonster_sSack(phase1Boss, new Point(398, 770), 0, false, true);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.guardian == null) {
         ArrayList<GuardianEntry> guardians = new ArrayList<>();
         guardians.add(new EyeOfRedemption(0, EYE_OF_REDEMPTION_POS, (byte)1, GuardianType.EyeOfRedemption, this.guardianRefMobID));
         guardians.add(new EyeOfAbyss(1, EYE_OF_THE_ABYSS_POS, (byte)0, GuardianType.EyeOfTheAbyss, this.guardianRefMobID));
         guardians.add(new FighterPlane(2, FIGHTER_PLANE_POS, (byte)0, GuardianType.FighterPlane, this.guardianRefMobID));
         guardians.add(new SphereOfOdium(3, SPHERE_OF_ODIUM_POS, (byte)0, GuardianType.SphereOfOdium, this.guardianRefMobID));
         this.guardian = new Guardian(guardians);
      }

      KalosAction.GuardianAction.CreateGuardian createGuardian = new KalosAction.GuardianAction.CreateGuardian(this.guardian.getGuardians());
      createGuardian.sendPacket(player);
      KalosAction.InitPacket.CreateLiberationTop liberationTop = new KalosAction.InitPacket.CreateLiberationTop();
      liberationTop.sendPacket(player);
      KalosAction.InitPacket.CreateLiberationBottom liberationBottom = new KalosAction.InitPacket.CreateLiberationBottom(1);
      liberationBottom.sendPacket(player);
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
   public void handleFreeze(MapleMonster mob) {
      if (mob != null && mob.getId() == this.nextPhaseMob) {
         this.SpecialAssaultInterval += 10000;
      }
   }
}
