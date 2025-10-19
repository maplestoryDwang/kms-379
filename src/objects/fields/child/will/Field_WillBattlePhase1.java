package objects.fields.child.will;

import network.game.GameServer;
import network.models.CField;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class Field_WillBattlePhase1 extends Field_WillBattle {
   public Field_WillBattlePhase1(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate, 1);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setPhase(1);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.getStartMonitorBreakTime() != 0L && System.currentTimeMillis() >= this.getStartMonitorBreakTime()) {
         this.monitorBreak();
         this.setStartMonitorBreakTime(0L);
         this.setMonitorBreakType(0);
      }

      int[] bossID = new int[]{8880363, 8880364, 8880303, 8880304, 8880343, 8880344};
      int count = 0;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         for (int id : bossID) {
            if (mob.getId() == id) {
               count++;
               break;
            }
         }
      }

      if (count < 2) {
         this.sendWillNotice("윌이 진지해졌네요. 거울 속 깊은 곳에 윌의 진심이 비춰질 것 같아요.", 245, 7000);
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
      int rand = Randomizer.nextInt(2);
      String portal = "";
      if (rand == 1) {
         portal = "ptup";
      } else {
         portal = "ptdown";
      }

      player.send(CField.userWarpPortal(portal));
      this.sendWillDisplayHP(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.setWillMoonGauge(0);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (mob.getId() == 8880343
         || mob.getId() == 8880344
         || mob.getId() == 8880303
         || mob.getId() == 8880304
         || mob.getId() == 8880363
         || mob.getId() == 8880364) {
         mob.addSkillFilter(2);
         mob.addSkillFilter(3);
      } else if (mob.getId() == 8880340 || mob.getId() == 8880300 || mob.getId() == 8880360) {
         mob.addSkillFilter(0);
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8880343
         || mob.getId() == 8880344
         || mob.getId() == 8880303
         || mob.getId() == 8880304
         || mob.getId() == 8880363
         || mob.getId() == 8880364) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            player.setCurrentBossPhase(2);
         }
      }
   }
}
