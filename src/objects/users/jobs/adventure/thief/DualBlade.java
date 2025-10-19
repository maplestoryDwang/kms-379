package objects.users.jobs.adventure.thief;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.effect.child.SkillEffect;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Thief;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.CollectionUtil;
import objects.utils.Pair;

public class DualBlade extends Thief {
   boolean karmaBladeFinalAttack = false;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      int skillID = GameConstants.getLinkedSkillID(attack.skillID);
      if (attack.skillID == 400041042) {
         this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), (attack.display & 8388608) != 0, attack.skillID);
      }

      if (skillID == 4341011 && this.getPlayer().getCooldownLimit(4341002) > 0L) {
         long remain = this.getPlayer().getRemainCooltime(4341002);
         if (remain > 0L) {
            SecondaryStatEffect eff = SkillFactory.getSkill(4341002).getEffect(this.getPlayer().getTotalSkillLevel(4341002));
            if (eff != null) {
               this.getPlayer().changeCooldown(4341002, -((int)(remain * 0.01 * effect.getX())));
            }
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
      if (attack.skillID == 4321002 && effect.makeChanceResult()) {
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
         mse.put(MobTemporaryStatFlag.BLIND, new MobTemporaryStatEffect(MobTemporaryStatFlag.BLIND, effect.getX(), attack.skillID, null, false));
         mse.put(
            MobTemporaryStatFlag.ADD_DAM_SKILL_2, new MobTemporaryStatEffect(MobTemporaryStatFlag.ADD_DAM_SKILL_2, effect.getZ(), attack.skillID, null, false)
         );
         monster.applyMonsterBuff(mse, attack.skillID, effect.getDuration(), null, Collections.EMPTY_LIST);
      }

      if (attack.skillID == 4331006 && !monster.getStats().isBoss() && effect.makeChanceResult()) {
         monster.applyStatus(
            this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, attack.skillID, null, false), false, effect.getDuration(), true, effect
         );
      }

      if (totalDamage > 0L) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.KarmaBlade);
         List<Integer> list = SkillFactory.getSkill(4361501).getSkillList();
         if (value != null && value > 0 && list.contains(attack.skillID) && this.getPlayer().getRemainCooltime(4361501) <= 0L) {
            int value_ = value - 1;
            if (value_ == 0) {
               this.checkKarmaBladeFinalAttack();
               this.getPlayer()
                  .send(
                     CField.getRegisterExtraSkill(
                        4361502, attack.forcedPos.x, attack.forcedPos.y, (attack.display & 32768) != 0, List.of(new ExtraSkillInfo(4361502, 0)), 0
                     )
                  );
               int dur = (int)(this.getPlayer().getSecondaryStat().KarmaBladeTill - System.currentTimeMillis());
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.KarmaBlade, 4361501, dur, -1);
            } else {
               List<MapleMonster> mobs = this.getPlayer().getMap().getAllMonster();
               CollectionUtil.sortMonsterByBossHP(mobs);
               int dur = (int)(this.getPlayer().getSecondaryStat().KarmaBladeTill - System.currentTimeMillis());
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.KarmaBlade, 4361501, dur, value_);
               this.getPlayer().giveCoolDowns(4361501, System.currentTimeMillis(), 100L);
               this.getPlayer().send(CField.skillCooldown(4361501, 100));
               List<Pair<Integer, Integer>> mobList = new ArrayList<>();

               for (MapleMonster mob : mobs) {
                  if (mobList.size() >= 3) {
                     break;
                  }

                  mobList.add(new Pair<>(mob.getObjectId(), 0));
               }

               this.getPlayer().send(CField.userBonusAttackRequest(4361501, false, mobList));
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().getTotalSkillLevel(400041075) > 0 && this.getPlayer().getCooldownLimit(400041075) <= 0L) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400041075);
         if (eff != null) {
            if (attack.skillID == 4341004) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 4341004);
            } else if (attack.skillID == 4341009 || attack.skillID == 4361000) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, attack.skillID);
            }

            if (attack.skillID == 4341004 || attack.skillID == 4341009 || attack.skillID == 4361000) {
               this.getPlayer().giveCoolDowns(400041075, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
               this.getPlayer().send(CField.skillCooldown(400041075, eff.getCooldown(this.getPlayer())));
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 4331006:
            int size = packet.readInt();

            for (int i = 0; i < size; i++) {
               int objectID = packet.readInt();
               packet.skip(1);
               short rlyType = packet.readShort();
               PacketEncoder p = new PacketEncoder();
               p.write((int)rlyType);
               p.writeInt(objectID);
               SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), this.getActiveSkillID(), this.getActiveSkillLevel(), p);
               this.getPlayer().send(e.encodeForLocal());
               this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
            }

            int duration = (int)(effect.getT() * 1000.0);
            if (size > 0) {
               duration = effect.getY();
            }

            this.getPlayer().temporaryStatSet(4331006, duration, SecondaryStatFlag.NotDamaged, 1);
            effect.applyTo(this.getPlayer());
            this.getPlayer().doActiveSkillCooltime(this.getActiveSkillID(), this.getActiveSkillID(), this.getActiveSkillLevel());
            break;
         case 4341003:
            this.getPlayer().setKeyDownSkill_Time(0L);
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), CField.skillCancel(this.getPlayer(), this.getActiveSkillID()), false);
            break;
         case 4361501:
            this.karmaBladeFinalAttack = false;
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.KarmaBlade, this.getActiveSkillID(), effect.getDuration(), 50);
            break;
         case 400041075:
            effect.applyTo(this.getPlayer(), true);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      super.activeSkillCancel();
   }

   public void checkKarmaBladeFinalAttack() {
      if (!this.karmaBladeFinalAttack) {
         this.karmaBladeFinalAttack = true;
         this.getPlayer()
            .send(
               CField.getRegisterExtraSkill(
                  4361502,
                  this.getPlayer().getPosition().x,
                  this.getPlayer().getPosition().y,
                  this.getPlayer().isFacingLeft(),
                  List.of(new ExtraSkillInfo(4361502, 0)),
                  0
               )
            );
      }
   }
}
