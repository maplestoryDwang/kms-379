package objects.users.jobs.adventure.archer;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.game.processors.PlayerHandler;
import network.models.CField;
import objects.effect.child.FlashMirageEffect;
import objects.effect.child.SkillEffect;
import objects.effect.child.SpecialSkillEffect;
import objects.fields.ForceAtom;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Timer;

public class BowMaster extends DefaultArcher {
   private long lastActiveSilhouetteMirageTime = 0L;
   private long lastBowMasterConcentrationTime = 0L;
   private long lastQuiverFullBurstTime = 0L;
   private long illusionShotRemainTime = 0L;
   private long lastArmorPiercingTime = 0L;
   private int flashMirageCount = 0;
   private int autoChargeStackOnOffStack = 0;
   private int armorPiercing = 0;
   private long lastAscentShadeTime = 0L;

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
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.QuiverCatridge) != null
         && attack.skillID != 3100010
         && attack.skillID != 3120017
         && attack.skillID != 400031029) {
         SecondaryStatEffect quiverEff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.QuiverCatridge);
         SecondaryStatEffect advEff = this.getPlayer().getSkillLevelData(3120022);
         boolean quiverBurst = this.getPlayer().getBuffedEffect(SecondaryStatFlag.QuiverFullBurst) != null;
         switch (this.getPlayer().getBuffedValue(SecondaryStatFlag.QuiverCatridge)) {
            case 1:
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initBowMasterMagicArrow(this.getPlayer().changeType);
               if (advEff != null && Randomizer.isSuccess(advEff.getU())) {
                  ForceAtom forceAtom = new ForceAtom(
                     info,
                     3120017,
                     this.getPlayer().getId(),
                     false,
                     true,
                     this.getPlayer().getId(),
                     ForceAtom.AtomType.BOW_MASTER_MAGIC_ARROW,
                     Collections.singletonList(monster.getHp() - totalDamage > 0L ? monster.getObjectId() : 0),
                     1
                  );
                  this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               } else if (quiverEff != null && Randomizer.isSuccess(quiverEff.getU())) {
                  ForceAtom forceAtom = new ForceAtom(
                     info,
                     3100010,
                     this.getPlayer().getId(),
                     false,
                     true,
                     this.getPlayer().getId(),
                     ForceAtom.AtomType.BOW_MASTER_MAGIC_ARROW,
                     Collections.singletonList(monster.getHp() - totalDamage > 0L ? monster.getObjectId() : 0),
                     1
                  );
                  this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               }
               break;
            case 2:
               if (advEff != null && Randomizer.isSuccess(advEff.getW()) && !this.getPlayer().hasBuffBySkillID(80002543)) {
                  this.getPlayer().addHP((int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * (advEff.getX() / 100.0)), false);
               }
         }

         if (quiverBurst && this.getPlayer().checkInterval(this.lastQuiverFullBurstTime, 2000)) {
            SecondaryStatEffect e = SkillFactory.getSkill(400031029).getEffect(this.getPlayer().getTotalSkillLevel(400031028));
            List<Integer> monsters = new ArrayList<>();
            if (e != null) {
               List<MapleMonster> mobs = this.getPlayer().getMap().getMobsInRect(this.getPlayer().getPosition(), -300, -150, 300, 150);
               Collections.shuffle(mobs);

               for (MapleMonster mob : mobs) {
                  if (mob.getStats().isBoss()) {
                     for (int i = 0; i < 6; i++) {
                        monsters.add(mob.getObjectId());
                     }
                  }
               }

               if (monsters.isEmpty()) {
                  for (int i = 0; i < mobs.size(); i++) {
                     monsters.add(mobs.get(i).getObjectId());
                     if (i >= 6) {
                        break;
                     }
                  }
               }

               if (advEff != null && Randomizer.isSuccess(advEff.getW())) {
                  this.getPlayer().addHP((int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * (advEff.getX() / 100.0)), false);
               }

               if (monsters.size() > 0) {
                  this.getPlayer().addHP((int)(totalDamage * (quiverEff.getX() / 100.0)), false);
                  monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, quiverEff.getSourceId(), null, false),
                     true,
                     quiverEff.getDuration(),
                     true,
                     quiverEff
                  );
                  if (monster.getHp() - totalDamage > 0L) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initBowMasterMagicArrow(this.getPlayer().changeType);
                     ForceAtom forceAtom = new ForceAtom(
                        info,
                        3100010,
                        this.getPlayer().getId(),
                        false,
                        true,
                        this.getPlayer().getId(),
                        ForceAtom.AtomType.BOW_MASTER_MAGIC_ARROW,
                        Collections.singletonList(0),
                        1
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                  }

                  Timer.MapTimer.getInstance()
                     .schedule(
                        () -> {
                           if (this.getPlayer().getSecondaryStat().QuiverFullBurst > 0) {
                              this.getPlayer().getSecondaryStat().QuiverFullBurst--;

                              for (int ix = 0; ix < 6; ix++) {
                                 ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                                 MapleMonster m = mobs.get(0);
                                 Point pos = m.getPosition();
                                 if (m.getId() == 8880153) {
                                    pos.y -= 450;
                                    pos.x += 50;
                                 }

                                 info.initQuiverFullBurstFlameArrow(pos);
                                 ForceAtom forceAtomx = new ForceAtom(
                                    info,
                                    400031029,
                                    this.getPlayer().getId(),
                                    false,
                                    true,
                                    this.getPlayer().getId(),
                                    ForceAtom.AtomType.QUIVER_FULL_BURST_FLAME_ARROW,
                                    monsters,
                                    1
                                 );
                                 this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtomx));

                                 try {
                                    Thread.sleep(150L);
                                 } catch (InterruptedException var9x) {
                                    Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, var9x);
                                 }
                              }
                           }
                        },
                        0L
                     );
               }
            }

            this.lastQuiverFullBurstTime = System.currentTimeMillis();
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.QuiverFullBurst, 400031029, 1);
            statManager.temporaryStatSet();
         }
      }

      if (this.getPlayer().getSkillLevel(3120018) > 0) {
         SecondaryStatEffect armorPiercing = SkillFactory.getSkill(3120018).getEffect(this.getPlayer().getSkillLevel(3120018));
         if (System.currentTimeMillis() - this.lastArmorPiercingTime >= 1000L) {
            this.lastArmorPiercingTime = System.currentTimeMillis();
            if (this.getPlayer().getCooldownLimit(3120018) <= 0L) {
               this.getPlayer().addCooldown(3120018, System.currentTimeMillis(), armorPiercing.getY() * 1000L);
               this.armorPiercing = monster.getObjectId();
            } else {
               this.getPlayer().changeCooldown(3120018, -armorPiercing.getW() * 1000L);
            }
         }
      }

      if (totalDamage > 0L) {
         if (this.getPlayer().hasBuffBySkillID(400031002)
            && attack.skillID != 400031002
            && attack.skillID != 400030002
            && (this.getPlayer().getLastUseArrowRain() == 0L || this.getPlayer().getLastUseArrowRain() + 5000L < System.currentTimeMillis())) {
            SecondaryStatEffect eff = SkillFactory.getSkill(400030002).getEffect(this.getPlayer().getSkillLevel(400031002));
            if (attack.affectedSpawnPos != null) {
               for (Point pos : attack.affectedSpawnPos) {
                  Point pt = this.getPlayer().getMap().calcDropPos(pos, pos);
                  if (monster.getId() == 8880153) {
                     pt.x += 80;
                     pt.y -= 450;
                  }

                  Rect rect = new Rect(pt, eff.getLt2(), eff.getRb2(), (attack.display & 32768) > 0);
                  this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), eff, pt, System.currentTimeMillis() + eff.getDuration() / 1000));
               }
            }

            this.getPlayer().setLastUseArrowRain(System.currentTimeMillis());
         }

         if (!this.getPlayer().hasBuffBySkillID(400031020)
            && this.getPlayer().getCooldownLimit(400031020) > 0L
            && attack.skillID != 400031020
            && attack.skillID != 400031021) {
            int sLevel = this.getPlayer().getSkillLevel(400031020);
            if (sLevel > 0) {
               SecondaryStatEffect e = SkillFactory.getSkill(400031020).getEffect(sLevel);
               this.getPlayer().setSlowAttackCount(this.getPlayer().getSlowAttackCount() + 1);
               if (this.getPlayer().getSlowAttackCount() >= e.getU()) {
                  this.getPlayer().setSlowAttackCount(0);
                  this.getPlayer().temporaryStatSet(400031021, 1000, SecondaryStatFlag.ProfessionalAgent, 1);
                  SkillEffect eff = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), 400031021, sLevel, null);
                  this.getPlayer().send(eff.encodeForLocal());
                  this.getPlayer().getMap().broadcastMessage(this.getPlayer(), eff.encodeForRemote(), false);
               }
            }
         }

         if (attack.skillID == 3141000 || attack.skillID == 3141001) {
            int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SixthStormArrowEx, 0);
            if (attack.skillID == 3141001) {
               value--;
            } else {
               int inc = attack.allDamage.size();
               value = Math.min(75, value + inc);
            }

            value = Math.max(0, value);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.SixthStormArrowEx, 3141000, Integer.MAX_VALUE, value);
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0 && opcode == RecvPacketOpcode.SHOOT_ATTACK) {
         this.tryApplyBowMasterMortalBlow();
      }

      if (attack.targets > 0 && this.getPlayer().hasBuffBySkillID(3111015)) {
         Skill sk = SkillFactory.getSkill(3111015);
         if (sk.getSkillList().contains(attack.skillID) || sk.getSkillList2().contains(attack.skillID)) {
            boolean active = true;
            int skillId = this.getPlayer().getSkillLevel(3120021) > 0 ? 3120021 : 3111015;
            SecondaryStatEffect e = SkillFactory.getSkill(skillId).getEffect(this.getPlayer().getSkillLevel(skillId));
            if (sk.getSkillList2().contains(attack.skillID)) {
               active = false;
               this.flashMirageCount++;
               if (this.flashMirageCount >= e.getU2()) {
                  active = true;
                  this.flashMirageCount = 0;
               }
            }

            if (active) {
               int value = this.getPlayer().getBuffedValue(SecondaryStatFlag.FlashMirage);
               if (value >= e.getU()) {
                  value = 0;
                  SecondaryStatEffect level = this.getPlayer().getBuffedEffect(SecondaryStatFlag.FlashMirage);
                  if (level != null) {
                     MapleMonster monster = this.getPlayer()
                        .getMap()
                        .getMobsInRect(this.getPlayer().getTruePosition(), level.getLt().x, level.getLt().y, level.getRb().x, level.getRb().y, false)
                        .stream()
                        .findAny()
                        .orElse(null);
                     int count = e.getW();
                     if (monster != null) {
                        List<Pair<Point, Integer>> pointList = new ArrayList<>();
                        if (skillId == 3120021) {
                           pointList.add(new Pair<>(new Point(monster.getTruePosition().x - 150, monster.getTruePosition().y - 150), 0));
                           pointList.add(new Pair<>(new Point(monster.getTruePosition().x - 150, monster.getTruePosition().y + 150), 0));
                           pointList.add(new Pair<>(new Point(monster.getTruePosition().x + 150, monster.getTruePosition().y - 150), 1));
                           pointList.add(new Pair<>(new Point(monster.getTruePosition().x + 150, monster.getTruePosition().y + 150), 1));
                        } else {
                           pointList.add(new Pair<>(new Point(monster.getTruePosition().x, monster.getTruePosition().y - 150), 2));
                           pointList.add(new Pair<>(new Point(monster.getTruePosition().x - 150, monster.getTruePosition().y + 150), 0));
                           pointList.add(new Pair<>(new Point(monster.getTruePosition().x + 150, monster.getTruePosition().y + 150), 1));
                        }

                        FlashMirageEffect eff = new FlashMirageEffect(this.getPlayer().getId(), 3111015, pointList);
                        this.getPlayer().send(eff.encodeForLocal());
                        this.getPlayer().getMap().broadcastMessage(this.getPlayer(), eff.encodeForRemote(), false);

                        for (Pair<Point, Integer> data : pointList) {
                           SecondAtom.Atom a = new SecondAtom.Atom(
                              this.getPlayer().getMap(),
                              this.getPlayer().getId(),
                              3111016,
                              ForceAtom.SN.getAndAdd(1),
                              SecondAtom.SecondAtomType.FlashMirage,
                              0,
                              null,
                              data.left
                           );
                           a.setPlayerID(this.getPlayer().getId());
                           a.setTargetObjectID(monster.getObjectId());
                           a.setCreateDelay(480);
                           a.setEnableDelay(30);
                           a.setRotate(90 * (data.right == 1 ? -1 : (data.right == 0 ? 1 : 0)));
                           a.setSkillID(3111016);
                           a.setExpire(3000);
                           a.setAttackableCount(5);
                           a.getCustoms().add(new SecondAtom.Custom(0, 0));
                           this.getPlayer().addSecondAtom(a);
                           SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 3111016, Collections.singletonList(a));
                           this.getPlayer().getMap().createSecondAtom(secondAtom);
                        }
                     }
                  }
               }

               value++;
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.FlashMirage, effect.getSourceId(), value);
               statManager.temporaryStatSet();
            }
         }
      }

      if (this.getPlayer().getJob() == 312 && attack.targets > 0) {
         if (GameConstants.isArmorPiercingPossibleSkill(attack.skillID)) {
            this.tryArmorPiercing();
         }

         if (this.getPlayer().getCooldownLimit(3120018) > 0L) {
            SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(3120018);
            if (ex != null) {
               this.getPlayer().changeCooldown(3120018, -(ex.getW() * 1000));
            }
         }

         SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(3110012);
         if (ex != null && System.currentTimeMillis() - this.lastBowMasterConcentrationTime >= ex.getY()) {
            this.incrementFocusedFury();
            this.lastBowMasterConcentrationTime = System.currentTimeMillis();
         }
      }

      if (this.getPlayer().hasBuffBySkillID(400031020)
         && attack.skillID != 3100001
         && attack.skillID != 3100010
         && attack.skillID != 3111016
         && attack.skillID != 3120008
         && attack.skillID != 3120017
         && attack.skillID != 95001000
         && attack.skillID != 400031020) {
         SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(400031020);
         if (System.currentTimeMillis() - this.illusionShotRemainTime > 0L) {
            this.illusionShotRemainTime = System.currentTimeMillis() + ex.getS() * 1000L;
         }

         this.illusionShotRemainTime = Math.min(this.illusionShotRemainTime, System.currentTimeMillis() + ex.getDuration());
         this.onActiveIllusionShot();
      }

      if (attack.targets > 0
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.AutoChargeStackOnOff) != null
         && attack.skillID != 400031053
         && attack.skillID != 400031054
         && attack.skillID != 500061016
         && attack.skillID != 500061017) {
         int skillIdx = 400031053;
         SecondaryStatEffect ex = SkillFactory.getSkill(400031053).getEffect(this.getPlayer().getTotalSkillLevel(400031053));
         if (ex != null) {
            boolean faceLeft = (attack.display & 32768) != 0;
            if (this.autoChargeStackOnOffStack > 0 && this.getPlayer().checkInterval(this.lastActiveSilhouetteMirageTime, (int)(ex.getT() * 1000.0))) {
               Point pos = this.getPlayer().getTruePosition();
               PacketEncoder packet = new PacketEncoder();
               packet.writeInt(0);
               packet.writeInt(pos.x);
               packet.writeInt(pos.y);
               SpecialSkillEffect eff = new SpecialSkillEffect(this.getPlayer().getId(), 400031053, packet);
               this.getPlayer().send(eff.encodeForLocal());
               this.getPlayer().getMap().broadcastMessage(this.getPlayer(), eff.encodeForRemote(), false);
               List<Integer> targets = new ArrayList<>();
               List<Point> startPos = new ArrayList<>();

               for (int i = 0; i < ex.getBulletCount(); i++) {
                  startPos.add(new Point(42, -150));
               }

               int count = 0;
               Rect rect = new Rect(-10, -300, 500, 300);
               if (faceLeft) {
                  rect = new Rect(-500, -300, 10, 300);
               }

               for (MapleMonster mob : this.getPlayer()
                  .getMap()
                  .getMobsInRect(this.getPlayer().getTruePosition(), rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom())) {
                  targets.add(mob.getObjectId());
                  if (++count >= ex.getBulletCount()) {
                     break;
                  }
               }

               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initSilhouetteMirage(0, startPos);
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  400031054,
                  this.getPlayer().getId(),
                  false,
                  true,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.CARDINAL_DISCHARGE,
                  targets,
                  ex.getBulletCount()
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               this.lastActiveSilhouetteMirageTime = System.currentTimeMillis();
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      super.onActiveSkill(skill, effect, packet);
      switch (this.getActiveSkillID()) {
         case 3141501:
            this.getPlayer().temporaryStatSet(3141501, effect.getDuration(), SecondaryStatFlag.AscendantShade, 1);
         default:
            if (this.getActiveSkillID() == 400031020) {
               SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400031020);
               this.illusionShotRemainTime = System.currentTimeMillis() + e.getS() * 1000L;
               this.onActiveIllusionShot();
            }
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400031020) {
         this.illusionShotRemainTime = 0L;
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().hasBuffBySkillID(3141501) && System.currentTimeMillis() - this.lastAscentShadeTime > 0L) {
         this.lastAscentShadeTime = System.currentTimeMillis() + 3000L;
         this.getPlayer().send(CField.userBonusAttackRequest(3141502, true, Collections.emptyList()));
      }
   }

   public void onActiveIllusionShot() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.ACTIVE_ILLUSION_SHOT.getValue());
      p.writeInt(this.getPlayer().getId());
      p.write(true);
      p.writeInt(this.illusionShotRemainTime - System.currentTimeMillis());
      this.getPlayer().send(p.getPacket());
   }

   public void tryArmorPiercing() {
      int skillID = 3120018;
      SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(skillID);
      if (effect != null && this.getPlayer().getCooldownLimit(skillID) <= 0L) {
         this.getPlayer().addCooldown(skillID, System.currentTimeMillis(), effect.getY() * 1000L);
         this.getPlayer().send(CField.skillCooldown(skillID, effect.getY() * 1000));
      }
   }

   public void incrementFocusedFury() {
      int amount = 0;
      int skillID = 3110012;
      if (this.getPlayer().hasBuffBySkillID(skillID)) {
         amount = this.getPlayer().getBuffedValue(SecondaryStatFlag.BowMasterConcentration);
         if (amount < 20) {
            amount++;
         }
      }

      int skillLevel = this.getPlayer().getTotalSkillLevel(skillID);
      SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
      if (effect != null) {
         this.getPlayer().temporaryStatSet(skillID, effect.getDuration(), SecondaryStatFlag.BowMasterConcentration, amount);
         if (amount < 20) {
            SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), skillID, skillLevel, null);
            this.getPlayer().send(e.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case AutoChargeStackOnOff:
            packet.writeInt(this.autoChargeStackOnOffStack);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public void tryApplyBowMasterMortalBlow() {
      if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.indieDamR) == null
         || this.getPlayer().getBuffedEffect(SecondaryStatFlag.indieDamR).getSourceId() != 3110001) {
         int skillID = 3110001;
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(skillID);
         if (effect != null) {
            Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.BowMasterMortalBlow);
            int value = 0;
            if (v != null) {
               value = v;
            }

            if (value >= effect.getX()) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.BowMasterMortalBlow);
               Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
               flags.put(SecondaryStatFlag.indieDamR, effect.getY());
               flags.put(SecondaryStatFlag.indieFlyAcc, 1);
               this.getPlayer().temporaryStatSet(skillID, this.getPlayer().getSkillLevel(skillID), 5000, flags);
            } else {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.BowMasterMortalBlow, skillID, Integer.MAX_VALUE, Math.min(effect.getX(), ++value));
            }
         }
      }
   }
}
