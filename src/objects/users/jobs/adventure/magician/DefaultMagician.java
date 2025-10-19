package objects.users.jobs.adventure.magician;

import constants.GameConstants;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.effect.child.HPHeal;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.jobs.Magician;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;

public class DefaultMagician extends Magician {
   public int memoryChoiceSkillID = 0;
   public int infinityStack = 0;

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
      if (totalDamage > 0L) {
         Skill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(this.getPlayer().getJob()));
         int eaterLevel = this.getPlayer().getTotalSkillLevel(eaterSkill);
         if (eaterLevel > 0) {
            eaterSkill.getEffect(eaterLevel).applyPassive(this.getPlayer(), monster);
         }
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
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 2001009:
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ChillingStep) != null) {
               SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ChillingStep);
               if (eff != null && eff.makeChanceResult()) {
                  this.getPlayer().sendAffectedArea(this.getPlayer().getPosition(), eff, eff.getTime() * 1000);
               }
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 2121004:
         case 2221004:
         case 2321004:
            this.infinityStack = 1;
            effect.applyTo(this.getPlayer(), true);
            break;
         case 400001021:
            this.tryUnstableMemorize();
            effect.applyTo(this.getPlayer());
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      this.resetMemoryChoice();
      super.activeSkillCancel();
   }

   @Override
   public void afterActiveSkill() {
      this.resetMemoryChoice();
      super.afterActiveSkill();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Infinity) != null && this.getPlayer().checkInterval(this.getPlayer().lastActiveInfinityTime, 4000)) {
         SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.Infinity);
         if (eff != null) {
            int regenHP = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * (eff.getY() / 100.0));
            int regenMP = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * (eff.getY() / 100.0));
            this.getPlayer().addMPHP(regenHP, regenMP);
            if (this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) - regenHP > 0L) {
               HPHeal e = new HPHeal(this.getPlayer().getId(), regenHP);
               this.getPlayer().send(e.encodeForLocal());
               this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
            }

            if (this.infinityStack < 20) {
               double baseRate = 1.7;
               double exponentialRate = 1.017;
               baseRate *= Math.pow(exponentialRate, this.infinityStack);
               this.infinityStack++;
               if (baseRate < 2.15) {
                  SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.Infinity, eff.getSourceId(), this.infinityStack);
                  statManager.temporaryStatSet();
               }
            }
         }

         this.getPlayer().lastActiveInfinityTime = System.currentTimeMillis();
      }
   }

   public void setMemoryChoice(int skillID, boolean change) {
      SecondaryStatEffect e = SkillFactory.getSkill(400001063).getEffect(this.getPlayer().getTotalSkillLevel(400001021));
      if (e != null) {
         if (change) {
            this.memoryChoiceSkillID = skillID;
            this.getPlayer().addCooldown(e.getSourceId(), System.currentTimeMillis(), e.getCoolTime());
            this.getPlayer().send(CField.skillCooldown(e.getSourceId(), e.getCoolTime()));
         }

         this.getPlayer().send(CField.setMemoryChoice(this.memoryChoiceSkillID));
      }
   }

   public void tryUnstableMemorize() {
      if (this.getPlayer().getTotalSkillLevel(this.memoryChoiceSkillID) > 0) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.FORCE_SKILL_USE.getValue());
         packet.writeInt(this.memoryChoiceSkillID);
         packet.writeInt(0);
         this.getPlayer().send(packet.getPacket());
      }
   }

   public void resetMemoryChoice() {
      if (this.memoryChoiceSkillID == this.getActiveSkillID() && this.getPlayer().getMemorize()) {
         this.getPlayer().setMemorize(false);
      }
   }
}
