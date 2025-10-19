package objects.fields.child.blackmage;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.game.GameServer;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_BlackMageBattlePhase1 extends Field_BlackMage {
   private long nextCreateDarkFallingTime = 0L;
   private long nextSpawnShriekingWallTime = 0L;
   private long shriekingWallCreateTime = 0L;
   private int spawnedShriekingWallsSize = 0;
   private long lastCreateBlackChainsTime = 0L;
   private long nextSpawnRedLightningTime = 0L;
   private List<Point> shriekingWallSpawnPoint = new ArrayList<>();

   public Field_BlackMageBattlePhase1(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
      this.setPhase(1);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.nextCreateDarkFallingTime = 0L;
      this.spawnedShriekingWallsSize = 0;
      this.shriekingWallSpawnPoint.clear();
      this.shriekingWallCreateTime = 0L;
      this.nextSpawnShriekingWallTime = 0L;
      this.lastCreateBlackChainsTime = 0L;
      this.nextSpawnRedLightningTime = 0L;
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      MapleMonster boss = this.getMonsterById(8880505);
      if (this.spawned1PhaseBoss && boss == null) {
         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            this.removeMonster(mob, 1);
         }

         this.sendBlackMageNotice("창조와 파괴의 기사가 쓰러져 검은 마법사에게로 가는 길이 열린다.", 7000);
         MapleCharacter p = null;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               p = player;
               break;
            }
         }

         Party party = p.getParty();
         if (party != null) {
            for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
               MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
               if (character != null
                  && character.getDeathCount() > 0
                  && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                  character.setCurrentBossPhase(2);
                  if (character.getRegisterTransferFieldTime() == 0L) {
                     character.setRegisterTransferField(this.getId() + 100);
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
                  }
               }
            }

            this.spawned1PhaseBoss = false;
         }
      } else {
         if (this.nextCreateDarkFallingTime == 0L) {
            this.nextCreateDarkFallingTime = System.currentTimeMillis() + 35000L;
         }

         if (this.nextCreateDarkFallingTime <= System.currentTimeMillis()) {
            this.createDarkFalling();
            this.nextCreateDarkFallingTime = System.currentTimeMillis() + 60000L;
         }

         if (this.nextSpawnShriekingWallTime == 0L && this.spawnedShriekingWallsSize < 10) {
            this.nextSpawnShriekingWallTime = System.currentTimeMillis() + 40000L;
         }

         if (this.nextSpawnShriekingWallTime != 0L && this.nextSpawnShriekingWallTime <= System.currentTimeMillis()) {
            this.prepareSpawnShriekingWalls();
            this.sendBlackMageNotice("통곡의 장벽이 솟아올라 공간을 잠식한다.", 3000);
            this.shriekingWallCreateTime = System.currentTimeMillis() + 2000L;
            if (this.spawnedShriekingWallsSize >= 10) {
               this.nextSpawnShriekingWallTime = 0L;
            } else {
               this.nextSpawnShriekingWallTime = System.currentTimeMillis() + 40000L;
            }
         }

         if (this.shriekingWallCreateTime != 0L && this.shriekingWallCreateTime <= System.currentTimeMillis()) {
            for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
               for (int i = 0; i < 4; i++) {
                  mob.addAttackBlocked(i);
               }

               mob.broadcastAttackBlocked();
               mob.addSkillFilter(0);
            }

            for (int i = 0; i < 2; i++) {
               Point pos = this.shriekingWallSpawnPoint.get(i);
               MapleMonster mob = MapleLifeFactory.getMonster(8880507 + i);
               this.spawnMonsterOnGroundBelow(mob, pos);
               this.sendRemoveShriekingWallsEffect();
               MapleMonster m = this.getMonsterById(8880500 + i);
               if (m != null) {
                  m.setSetShriekingWallPattern(true);
               }
            }

            this.shriekingWallSpawnPoint.clear();
            this.shriekingWallCreateTime = 0L;
         }

         if (this.lastCreateBlackChainsTime == 0L) {
            this.lastCreateBlackChainsTime = System.currentTimeMillis() + 15000L;
         }

         if (this.lastCreateBlackChainsTime <= System.currentTimeMillis()) {
            this.sendCreateBlackChains();
            this.lastCreateBlackChainsTime = System.currentTimeMillis() + 15000L;
         }

         if (this.nextSpawnRedLightningTime == 0L) {
            this.nextSpawnRedLightningTime = System.currentTimeMillis() + 78000L;
         }

         if (this.nextSpawnRedLightningTime <= System.currentTimeMillis()) {
            this.sendBlackMageNotice("불길한 붉은 번개가 내리쳐 움직임을 제한한다.", 3000);
            MapleMonster mob = MapleLifeFactory.getMonster(8880506);
            this.spawnMonsterOnGroundBelow(mob, new Point(400, 85));
            mob = MapleLifeFactory.getMonster(8880506);
            this.spawnMonsterOnGroundBelow(mob, new Point(-1600, 85));
            mob = MapleLifeFactory.getMonster(8880506);
            this.spawnMonsterOnGroundBelow(mob, new Point(1600, 85));
            mob = MapleLifeFactory.getMonster(8880506);
            this.spawnMonsterOnGroundBelow(mob, new Point(-1000, 85));
            mob = MapleLifeFactory.getMonster(8880506);
            this.spawnMonsterOnGroundBelow(mob, new Point(1000, 85));
            mob = MapleLifeFactory.getMonster(8880506);
            this.spawnMonsterOnGroundBelow(mob, new Point(-400, 85));
            this.nextSpawnRedLightningTime = System.currentTimeMillis() + 60000L;
         }
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.setPhase(1);
      player.setDeathCount(12);
      player.setDecrementDeathCount(0);
   }

   public void prepareSpawnShriekingWalls() {
      this.shriekingWallSpawnPoint.clear();
      int baseXLeft = -2172 + this.spawnedShriekingWallsSize * 174;
      int baseXRight = 2171 - this.spawnedShriekingWallsSize * 174;
      this.shriekingWallSpawnPoint.add(new Point(baseXLeft, 85));
      this.shriekingWallSpawnPoint.add(new Point(baseXRight, 85));
      this.sendCreateShriekingWallsEffect(this.shriekingWallSpawnPoint);
      this.spawnedShriekingWallsSize++;
   }
}
