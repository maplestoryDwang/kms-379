package objects.users.jobs;

import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.skills.Skill;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.AttackPair;

public class Warrior extends CommonJob {
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
   public void updatePerSecond() {
      super.updatePerSecond();
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }
}
