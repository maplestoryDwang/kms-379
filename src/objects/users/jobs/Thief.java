package objects.users.jobs;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.adventure.thief.DualBlade;
import objects.users.jobs.adventure.thief.NightLord;
import objects.users.jobs.adventure.thief.Shadower;
import objects.users.skills.Skill;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Rect;
import objects.utils.Triple;

public class Thief extends CommonJob {
   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      SecondaryStatEffect darkSight;
      if ((darkSight = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DarkSight)) != null && darkSight.getSourceId() != 400001023) {
         boolean isCanceled = true;
         SecondaryStatEffect advDarkSight = null;
         if (this.getPlayer().getTotalSkillLevel(4210015) > 0) {
            advDarkSight = this.getPlayer().getSkillLevelData(4210015);
            AffectedArea area = this.getPlayer().getMap().getMistBySkillId(4221006);
            if (area != null && area.getOwnerId() == this.getPlayer().getId()) {
               Rect rect = area.getMistRect();
               if (rect != null) {
                  if (rect.getLeft() <= attack.attackPosition.x
                     && rect.getTop() <= attack.attackPosition.y
                     && rect.getRight() >= attack.attackPosition.x
                     && rect.getBottom() >= attack.attackPosition.y) {
                     isCanceled = false;
                  }
               } else {
                  Rectangle rectangle = area.getBox();
                  if (rectangle != null && rectangle.contains(attack.attackPosition)) {
                     isCanceled = false;
                  }
               }
            }
         } else if (this.getPlayer().getTotalSkillLevel(4330001) > 0) {
            advDarkSight = this.getPlayer().getSkillLevelData(4330001);
         }

         if (advDarkSight != null && advDarkSight.makeChanceResult()) {
            isCanceled = false;
         }

         if (darkSight.getSourceId() == 14001031) {
            isCanceled = attack.isAttackWithDarkSight;
         }

         if (attack.skillID == 14001024) {
            isCanceled = false;
         }

         if (this.getPlayer().skillRequestArea.containsKey(4221052)) {
            SecondaryStatEffect vailOfShadow = this.getPlayer().getSkillLevelData(4221052);
            Rectangle rect = vailOfShadow.calculateBoundingBox(this.getPlayer().skillRequestArea.get(4221052), false);
            if (rect.contains(attack.attackPosition)) {
               isCanceled = false;
            }
         }

         if (attack.skillID == 400040008
            || attack.skillID == 14110034
            || attack.skillID == 14120020
            || attack.skillID == 14000027
            || attack.skillID == 14000028
            || attack.skillID == 14110033
            || attack.skillID == 14110034) {
            isCanceled = false;
         }

         if (attack.skillID == 400041060 || attack.skillID == 400041059) {
            isCanceled = false;
         }

         if (darkSight.getSourceId() == 64121025) {
            isCanceled = false;
         }

         if (isCanceled) {
            this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.DarkSight, darkSight.getSourceId());
         }
      }

      super.prepareAttack(attack, effect, opcode);
   }

   @Override
   public void onAttack(
      MapleMonster monster,
      boolean boss,
      AttackPair attackPair,
      Skill skill,
      long totalDamage,
      AttackInfo attack,
      SecondaryStatEffect effect,
      RecvPacketOpcode opcode
   ) {
      if (totalDamage > 0L && attack.skillID != 400041030 && attack.skillID > 0) {
         int dotSkillID = 400040000;
         int advDotSkillID = -1;
         if (this instanceof NightLord) {
            dotSkillID = 4110011;
            advDotSkillID = 4120011;
         } else if (this instanceof Shadower) {
            dotSkillID = 4210010;
            advDotSkillID = 4220011;
         } else if (this instanceof DualBlade) {
            dotSkillID = 4320005;
            advDotSkillID = 4340012;
         }

         SecondaryStatEffect dotEffect = this.getPlayer().getSkillLevelData(dotSkillID);
         int maxStack = 1;
         if (advDotSkillID >= 0 && this.getPlayer().getTotalSkillLevel(advDotSkillID) > 0) {
            dotEffect = this.getPlayer().getSkillLevelData(advDotSkillID);
            maxStack = dotEffect.getDotSuperpos();
         }

         if (attack.skillID != 4120019 && attack.skillID != 4100012 && dotEffect != null && monster.getBurnedSizeBySkillID(dotSkillID) < maxStack) {
            List<Integer> suddenRaids = List.of(4121016, 4221010, 4341011);
            if (dotEffect.makeChanceResult() || suddenRaids.contains(attack.skillID)) {
               int duration = dotEffect.getDOTTime() > 0 ? dotEffect.getDOTTime() * 1000 : dotEffect.getDuration();
               monster.applyStatus(
                  this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, dotSkillID, null, false), true, duration, true, dotEffect
               );
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400041030) {
         List<MapleMapObject> objects = this.getPlayer()
            .getMap()
            .getMapObjectsInRange(this.getPlayer().getPosition(), Double.POSITIVE_INFINITY, List.of(MapleMapObjectType.MONSTER));
         Collections.shuffle(objects);
         List<MapleMapObject> targets = objects.stream()
            .filter(object -> attack.allDamage.stream().filter(pair -> pair.objectid == object.getObjectId()).findFirst().orElse(null) == null)
            .filter(object -> ((MapleMonster)object).getBurnedSizeBySkillID(this.getPlayer().getId()) <= 0)
            .limit(effect.getY())
            .collect(Collectors.toList());
         targets.forEach(
            object -> {
               MapleMonster mob = (MapleMonster)object;
               mob.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, 400040000, null, false),
                  true,
                  effect.getDuration(),
                  true,
                  effect
               );
            }
         );
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 400041000:
            List<MapleMapObject> objects = this.getPlayer()
               .getMap()
               .getMapObjectsInRange(this.getPlayer().getPosition(), Double.POSITIVE_INFINITY, List.of(MapleMapObjectType.MONSTER));
            List<Triple<Integer, Integer, Integer>> mobList = new ArrayList<>();
            objects.forEach(object -> {
               MapleMonster mob = (MapleMonster)object;
               int size = mob.getBurendSizeByPlayerID(this.getPlayer().getId());
               if (size > 0) {
                  mobList.add(new Triple<>(mob.getObjectId(), 0, mobList.size() + 1));
               }
            });
            this.getPlayer().send(CField.userBonusAttackRequest(400041030, mobList, false, 0, 0, 0));
            effect.applyTo(this.getPlayer(), true);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }
}
