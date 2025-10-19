package objects.fields.child.will;

import java.util.ArrayList;
import java.util.List;
import network.game.GameServer;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;

public class Field_WillBattlePhase3 extends Field_WillBattle {
   List<SpiderWeb> spiderWebList = new ArrayList<>();

   public Field_WillBattlePhase3(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate, 3);
      this.spiderWebList.add(new SpiderWeb(2, -683, 395));
      this.spiderWebList.add(new SpiderWeb(1, -701, 182));
      this.spiderWebList.add(new SpiderWeb(2, 702, -280));
      this.spiderWebList.add(new SpiderWeb(0, -711, -254));
      this.spiderWebList.add(new SpiderWeb(1, 718, 432));
      this.spiderWebList.add(new SpiderWeb(0, 712, 310));
      this.spiderWebList.add(new SpiderWeb(1, -577, -298));
      this.spiderWebList.add(new SpiderWeb(0, 552, 459));
      this.spiderWebList.add(new SpiderWeb(0, 531, -268));
      this.spiderWebList.add(new SpiderWeb(1, 699, -82));
      this.spiderWebList.add(new SpiderWeb(0, -594, 251));
      this.spiderWebList.add(new SpiderWeb(2, 378, 480));
      this.spiderWebList.add(new SpiderWeb(1, 577, 345));
      this.spiderWebList.add(new SpiderWeb(0, -506, 432));
      this.spiderWebList.add(new SpiderWeb(1, -733, -122));
      this.spiderWebList.add(new SpiderWeb(0, -626, -179));
      this.spiderWebList.add(new SpiderWeb(0, 604, -153));
      this.spiderWebList.add(new SpiderWeb(1, -405, 484));
      this.spiderWebList.add(new SpiderWeb(0, 736, 56));
      this.spiderWebList.add(new SpiderWeb(0, -749, 17));
      this.spiderWebList.add(new SpiderWeb(2, -366, -325));
      this.spiderWebList.add(new SpiderWeb(1, 391, -307));
      this.spiderWebList.add(new SpiderWeb(0, -197, -300));
      this.spiderWebList.add(new SpiderWeb(1, 458, -163));
      this.spiderWebList.add(new SpiderWeb(0, -282, 488));
      this.spiderWebList.add(new SpiderWeb(1, 80, 482));
      this.spiderWebList.add(new SpiderWeb(1, -485, -148));
      this.spiderWebList.add(new SpiderWeb(0, -606, -75));
      this.spiderWebList.add(new SpiderWeb(1, 772, 169));
      this.spiderWebList.add(new SpiderWeb(2, -84, 481));
      this.spiderWebList.add(new SpiderWeb(1, -650, 45));
      this.spiderWebList.add(new SpiderWeb(2, 558, -58));
      this.spiderWebList.add(new SpiderWeb(2, 164, -308));
      this.spiderWebList.add(new SpiderWeb(1, -61, -275));
      this.spiderWebList.add(new SpiderWeb(1, 626, 69));
      this.spiderWebList.add(new SpiderWeb(0, -509, 329));
      this.spiderWebList.add(new SpiderWeb(1, -213, -200));
      this.spiderWebList.add(new SpiderWeb(0, -335, -147));
      this.spiderWebList.add(new SpiderWeb(1, 304, -169));
      this.spiderWebList.add(new SpiderWeb(0, 567, 203));
      this.spiderWebList.add(new SpiderWeb(0, 649, 240));
      this.spiderWebList.add(new SpiderWeb(1, 55, -142));
      this.spiderWebList.add(new SpiderWeb(0, -86, -138));
      this.spiderWebList.add(new SpiderWeb(1, -494, 14));
      this.spiderWebList.add(new SpiderWeb(0, -555, 147));
      this.spiderWebList.add(new SpiderWeb(0, -485, 218));
      this.spiderWebList.add(new SpiderWeb(2, -315, 308));
      this.spiderWebList.add(new SpiderWeb(0, -435, 138));
      this.spiderWebList.add(new SpiderWeb(1, -335, 85));
      this.spiderWebList.add(new SpiderWeb(0, -375, -48));
      this.spiderWebList.add(new SpiderWeb(2, -235, -59));
      this.spiderWebList.add(new SpiderWeb(1, 179, -105));
      this.spiderWebList.add(new SpiderWeb(2, 338, -18));
      this.spiderWebList.add(new SpiderWeb(1, 481, 128));
      this.spiderWebList.add(new SpiderWeb(2, 445, 283));
      this.spiderWebList.add(new SpiderWeb(2, 190, 308));
      this.spiderWebList.add(new SpiderWeb(1, -195, 155));
      this.spiderWebList.add(new SpiderWeb(1, 303, 158));
      this.spiderWebList.add(new SpiderWeb(2, -20, 55));
      this.spiderWebList.add(new SpiderWeb(2, 155, 72));
      this.spiderWebList.add(new SpiderWeb(2, -40, 293));
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setPhase(3);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);

      for (SpiderWeb web : this.getSpiderWebs()) {
         this.sendWillSpiderWeb(player, web, true);
      }

      player.temporaryStatResetBySkillID(80002404);
      player.setNextDebuffIncHPTime(0L);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.setLastWillAttackTime(0L);
      player.dispelDebuff(SecondaryStatFlag.WillPoison);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.getNextCreateWebTime() == 0L) {
         this.setNextCreateWebTime(System.currentTimeMillis() + 12000L);
      }

      if (this.getNextCreateWebTime() <= System.currentTimeMillis() && this.isSpawnSpiderWeb()) {
         this.spawnSpiderWeb();
         this.setNextCreateWebTime(System.currentTimeMillis() + 12000L);
      }

      if (this.getNext3rdAttackTime() == 0L) {
         this.setNext3rdAttackTime(System.currentTimeMillis() + 5000L);
      }
   }

   public void spawnSpiderWeb() {
      int createSize = Randomizer.rand(1, 3);
      int spawned = 0;

      for (SpiderWeb web : this.spiderWebList) {
         boolean find = false;

         for (SpiderWeb w : new ArrayList<>(this.getSpiderWebs())) {
            if (web.getX1() == w.getX1() && web.getY1() == w.getY1()) {
               find = true;
               break;
            }
         }

         if (!find) {
            this.addSpiderWeb(web);
            if (++spawned >= createSize) {
               break;
            }
         }
      }
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (mob.getId() == 8880302 || mob.getId() == 8880342 || mob.getId() == 8880362) {
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8880302 || mob.getId() == 8880342 || mob.getId() == 8880362) {
         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            this.removeMonster(m, 1);
         }

         for (SpiderWeb web : new ArrayList<>(this.getSpiderWebs())) {
            this.sendWillSpiderWeb(null, web, false);
         }

         this.clearSpiderWeb();
         this.setSpawnSpiderWeb(false);
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
               character.setRegisterTransferField(this.getId() + 30);
               character.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
            }
         }
      }
   }
}
