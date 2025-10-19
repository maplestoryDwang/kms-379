package objects.fields.child.will;

import java.awt.Point;
import network.game.GameServer;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;

public class Field_WillBattlePhase2 extends Field_WillBattle {
   public Field_WillBattlePhase2(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate, 2);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setPhase(2);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      int[] bossID = new int[]{8880301, 8880341, 8880361};
      MapleMonster boss = null;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         for (int id : bossID) {
            if (mob.getId() == id) {
               boss = mob;
               break;
            }
         }
      }

      if (boss == null) {
         this.sendWillNotice("윌이 여유가 없어졌군요. 거울 세계의 가장 깊은 곳이 드러날 것 같아요.", 245, 7000);
         MapleCharacter p = null;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               p = player;
               break;
            }
         }

         if (p == null) {
            return;
         }

         Party party = p.getParty();
         if (party == null) {
            return;
         }

         this.sendWillNotice("윌이 여유가 없어졌군요. 거울 세계의 가장 깊은 곳이 드러날 것 같아요.", 245, 7000);

         for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
            MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
            if (character != null
               && character.getDeathCount() > 0
               && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)
               && character.getRegisterTransferFieldTime() == 0L) {
               character.setRegisterTransferField(this.getId() + 50);
               character.setRegisterTransferFieldTime(System.currentTimeMillis() + 4000L);
            }
         }
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.willHPList.clear();
      this.willHPList.add(500);
      this.willHPList.add(3);
      this.sendWillDisplayHP2Phase(player);
      SecondaryStatEffect eff = SkillFactory.getSkill(80002404).getEffect(50);
      if (eff != null) {
         eff.applyTo(player);
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.temporaryStatResetBySkillID(80002404);
      player.setNextDebuffIncHPTime(0L);
      player.setWillMoonGauge(0);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (mob.getId() == 8880301 || mob.getId() == 8880341 || mob.getId() == 8880361) {
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
         MobSkillInfo msi = MobSkillFactory.getMobSkill(201, 237);
         msi.applyEffect(null, mob, MapleLifeFactory.getRealMobSkill(201, 237), true, mob.isFacingLeft(), new Point(0, 0));
      } else if (mob.getId() == 8880323) {
         mob.addSkillFilter(1);
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
      if (mob.getId() == 8880301 || mob.getId() == 8880341 || mob.getId() == 8880361) {
         if (mob.getMobMaxHp() * 0.001 * 500.0 >= mob.getHp() && this.getWillHPList().contains(500) && this.getMirrorOfLiesCount() == 0) {
            this.setTakeDown();
            this.setMirrorOfLiesCount(1);
         }

         if (mob.getMobMaxHp() * 0.001 * 3.0 >= mob.getHp() && this.getWillHPList().contains(3) && this.getMirrorOfLiesCount() == 1) {
            this.setMirrorOfLiesCount(2);
         }
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8880301 || mob.getId() == 8880341 || mob.getId() == 8880361) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            player.setCurrentBossPhase(3);
         }
      }
   }
}
