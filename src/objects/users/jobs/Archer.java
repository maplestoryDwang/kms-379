package objects.users.jobs;

import java.util.Collections;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.skills.Skill;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;

public class Archer extends CommonJob {
   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
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
      if (totalDamage > 0L
         && attack.skillID != 400031000
         && attack.skillID != 400031001
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.GuidedArrow) != null) {
         this.getPlayer().send(CField.getActiveAttackForceAtom(this.getPlayer(), monster.getObjectId()));
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
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
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void afterActiveSkill() {
      if (this.activeSkillID == 400031000) {
         ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
         ForceAtom forceAtom = new ForceAtom(
            info, 400031000, this.player.getId(), false, true, this.player.getId(), ForceAtom.AtomType.GUIDED_ARROW, Collections.singletonList(0), 1
         );
         this.player.getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
      }

      super.afterActiveSkill();
   }
}
