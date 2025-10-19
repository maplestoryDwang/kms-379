package objects.users.jobs.adventure.pirate;

import network.RecvPacketOpcode;
import network.game.processors.AttackInfo;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.jobs.Pirate;
import objects.users.skills.Skill;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.AttackPair;

public class DefaultPirate extends Pirate {
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
}
