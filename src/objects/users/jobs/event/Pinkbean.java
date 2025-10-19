package objects.users.jobs.event;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.effect.child.SkillEffect;
import objects.fields.Field;
import objects.fields.ForceAtom;
import objects.fields.MapleFoothold;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleStat;
import objects.users.jobs.Warrior;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Pinkbean extends Warrior {
   final int BodyAttack = 131000016;
   final int LetsRoll = 131001004;
   final int PinkBeanAtom = 131003016;
   final int ElectricGuitar = 131001113;
   final int Whistle = 131001213;
   final int Megaphone = 131001313;
   final int TongueOut = 131001106;
   final int NomNomMeat = 131001206;
   final int Zzz = 131001306;
   final int MysteriousCocktail = 131001406;
   final int PinkBeanHeadset = 131001506;
   final int PinkBeanScooter = 131001021;
   final int Boradori = 131001107;
   final int Barami = 131001207;
   final int Yeppeuni = 131001307;
   final int MagicshowTime00 = 131001026;
   final int MagicshowTime01 = 131002026;
   final int MagicshowTime02 = 131003026;
   final int BlazingYoYo = 131001010;
   final int BlazingYoYo001 = 131001011;
   final int GoMiniBeans = 131001015;
   final int GoMiniBeansSummon = 131002015;
   final int PinkGenesis = 131001012;
   final int PinkBeanWarrior = 131001018;
   final int PinkSplitter = 131001025;
   final int PinkShadow = 131001017;
   final int PinkBean_PowerSet = 131001019;
   final int PinkBean_Tutu = 131001020;
   final int PinkBean_Matryoshka = 131001023;
   final int Matryoshka_PinkBean = 131003023;
   final int Matryoshka_Yeti = 131004023;
   final int Matryoshka_Pepe = 131005023;
   final int Matryoshka_Slime = 131006023;
   final int TwilightofGods = 131001022;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      switch (attack.skillID) {
         case 131001010:
         case 131001011:
            Integer Dvalue = this.getPlayer().getBuffedValue(SecondaryStatFlag.PinkBeanYoyoDamageUp);
            if (Dvalue == null) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.PinkBeanYoyoDamageUp, 131001010, 5000, 1);
            } else {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.PinkBeanYoyoDamageUp, 131001010, 5000, Math.min(8, Dvalue + 1));
            }

            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.PinkbeanYoYoStack);
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.PinkbeanYoYoStack, 131001010, value - 1);
            statManager.temporaryStatSet();
            break;
         default:
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.PinkBeanYoyoDamageUp);
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
      long totalDmg = 0L;
      boolean upShowTime = false;

      for (Pair<Long, Boolean> dmg : attackPair.attack) {
         totalDmg += dmg.left;
      }

      boolean mobAlive = monster.getHp() - totalDmg > 0L;
      if ((boss || !mobAlive) && !this.getPlayer().skillisCooling(131001026) && this.getPlayer().getLevel() >= 80) {
         if (this.getPlayer().getSkillLevel(131001026) < 1) {
            this.getPlayer().changeSkillLevel(131001026, 1, 1);
         }

         upShowTime = true;
      }

      if (upShowTime && SkillFactory.getSkill(131001026).getSkillList().contains(skill.getId())) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.PinkBean_MagicShowtimeStack);
         if (value == null) {
            this.getPlayer().temporaryStatSet(131001026, 2100000000, SecondaryStatFlag.PinkBean_MagicShowtimeStack, 0);
         }

         SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
         statManager.changeStatValue(SecondaryStatFlag.PinkBean_MagicShowtimeStack, 131001026, Math.min(35, value + 1));
         statManager.temporaryStatSet();
      }

      if (attack.skillID != 131000016) {
         this.attackPinkBeanAtom();
         this.goMiniBeans();
      }

      switch (attack.skillID) {
         case 131001213:
            monster.applyStatus(
               this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, 131001213, null, false), true, 5000L, false, effect
            );
            break;
         case 131001313:
            Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
            mse.put(MobTemporaryStatFlag.ACC, new MobTemporaryStatEffect(MobTemporaryStatFlag.ACC, effect.getZ(), 131001313, null, false));
            monster.applyMonsterBuff(mse, 131001313, 5000L, null, Collections.EMPTY_LIST);
            break;
         case 131003016:
            PacketEncoder p = new PacketEncoder();
            p.write(1);
            p.writeInt(monster.getPosition().x);
            p.writeInt(monster.getPosition().y);
            SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), attack.skillID, attack.skillLevel, p);
            this.getPlayer().send(e.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
      }

      if (effect.getCooldown(this.getPlayer()) > 0) {
         int skillid = attack.skillID;
         switch (skillid) {
            case 131001113:
            case 131001213:
            case 131001313:
               skillid = 131001013;
            default:
               this.getPlayer().send(CField.skillCooldown(skillid, effect.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
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
   public void activeSkillPrepare(PacketDecoder packet) {
      super.activeSkillPrepare(packet);
      switch (this.getActiveSkillPrepareID()) {
         case 131001004:
            this.getPlayer().setViperEnergyCharge(1);
            this.getPlayer().temporaryStatSet(131001004, 2100000000, SecondaryStatFlag.KeyDownMoving, 300);
            break;
         case 131001020:
            this.getPlayer().setViperEnergyCharge(1);
            this.getPlayer().temporaryStatSet(131001020, 2100000000, SecondaryStatFlag.KeyDownMoving, 200);
            break;
         case 131001021:
            HashMap<SecondaryStatFlag, Integer> flags = new HashMap<>();
            flags.put(SecondaryStatFlag.indieSummon, 1);
            flags.put(SecondaryStatFlag.KeyDownMoving, 450);
            this.getPlayer().temporaryStatSet(131001021, 1, 2100000000, flags);
      }
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(1);
      if (this.getActiveSkillID() != 131001025 && this.getActiveSkillID() != 131001026) {
         switch (this.getActiveSkillID()) {
            case 131003023:
            case 131004023:
            case 131005023:
            case 131006023:
               break;
            default:
               effect.applyTo(this.getPlayer(), null);
         }
      }

      super.beforeActiveSkill(packet);
      switch (this.getActiveSkillID()) {
         case 131001017:
            packet.readInt();
            Point spawnPosx = packet.readPos();
            boolean userLeftx = packet.readByte() > 0;
            if (effect.getSummonMovementType() != null) {
               Summoned summon = new Summoned(
                  this.getPlayer(), 131001017, 1, spawnPosx, effect.getSummonMovementType(), (byte)0, System.currentTimeMillis() + 7000L
               );
               this.getPlayer().getMap().spawnSummon(summon, effect.getDuration(), false, false);
               this.getPlayer().addSummon(summon);
            }
            break;
         case 131001018:
            this.getPlayer().temporaryStatSet(this.getActiveSkillID(), effect.getDuration(), SecondaryStatFlag.indieStatR, this.getPlayer().getLevel() / 5);
            break;
         case 131001019:
            packet.readInt();
            Point startPos = packet.readPos();
            boolean userLeftxxx = packet.readByte() > 0;
            List<MapleFoothold> fh = new ArrayList<>();

            for (MapleFoothold foothold : this.getPlayer().getMap().getFootholds().getFootholds()) {
               Point pos1 = foothold.getPoint1();
               Point pos2 = foothold.getPoint2();
               if (Field.inRect(pos1.x, pos1.y, startPos.x, startPos.y, effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y)) {
                  fh.add(foothold);
               } else if (Field.inRect(pos2.x, pos2.y, startPos.x, startPos.y, effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y)) {
                  fh.add(foothold);
               }
            }

            Collections.shuffle(fh);
            List<Point> spawnPoint = new ArrayList<>();
            int tryCount = 0;
            int randomSpawnCount = Randomizer.rand(7, 10);

            while (spawnPoint.size() < randomSpawnCount) {
               MapleFoothold mfh = fh.get(Randomizer.nextInt(fh.size()));
               int mfhX1 = mfh.getX1();
               int mfhX2 = mfh.getX2();
               int mfhY1 = mfh.getY1();
               int mfhY2 = mfh.getY2();
               if (mfh.getX1() >= mfh.getX2()) {
                  mfhX1 = mfh.getX2();
                  mfhX2 = mfh.getX1();
               }

               if (mfh.getY1() >= mfh.getY2()) {
                  mfhY1 = mfh.getY2();
                  mfhY2 = mfh.getY1();
               }

               int fX = Randomizer.rand(mfhX1, mfhX2);
               int fY = Randomizer.rand(mfhY1, mfhY2);
               if (spawnPoint.size() == 0) {
                  spawnPoint.add(new Point(fX, fY));
               } else {
                  Iterator var16 = spawnPoint.iterator();

                  while (true) {
                     if (var16.hasNext()) {
                        Point sP = (Point)var16.next();
                        if (Field.inRect(fX, fY, sP.x, sP.y, -200, -235, 200, 50) && tryCount <= 100) {
                           continue;
                        }

                        tryCount = 0;
                        spawnPoint.add(new Point(fX, fY));
                     }

                     tryCount++;
                     break;
                  }
               }
            }

            if (effect.getSummonMovementType() != null) {
               for (Point s : spawnPoint) {
                  Summoned summon = new Summoned(
                     this.getPlayer(),
                     131001019,
                     1,
                     this.getPlayer().getMap().calcDropPos(s, s),
                     effect.getSummonMovementType(),
                     (byte)0,
                     System.currentTimeMillis() + 10000L
                  );
                  this.getPlayer().getMap().spawnSummon(summon, 10000);
                  this.getPlayer().addSummon(summon);
               }
            }
            break;
         case 131001022:
            packet.readInt();
            Point spawnPosxxxx = packet.readPos();
            userLeftxxx = packet.readByte() > 0;
            int[] summons = new int[]{131001022, 131002022, 131003022, 131004022, 131005022, 131006022};

            for (int a : summons) {
               SecondaryStatEffect eff = SkillFactory.getSkill(a).getEffect(1);
               if (eff.getSummonMovementType() != null) {
                  Summoned summon = new Summoned(
                     this.getPlayer(),
                     a,
                     1,
                     spawnPosxxxx,
                     eff.getSummonMovementType(),
                     (byte)(userLeftxxx ? 0 : 1),
                     System.currentTimeMillis() + effect.getDuration()
                  );
                  this.getPlayer().getMap().spawnSummon(summon, effect.getDuration(), false, false);
                  this.getPlayer().addSummon(summon);
               }
            }
            break;
         case 131001023: {
            HashMap<SecondaryStatFlag, Integer> flags = new HashMap<>();
            flags.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            flags.put(SecondaryStatFlag.PinkBeanMatryoshka, 1);
            this.getPlayer().temporaryStatSet(this.getActiveSkillID(), 1, 12000, flags);
            break;
         }
         case 131001025:
            packet.readInt();
            Point spawnPosxx = packet.readPos();
            boolean userLeftxx = packet.readByte() > 0;
            packet.read(2);
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(packet.readInt());
            if (mob != null && effect.getSummonMovementType() != null) {
               Summoned summon = new Summoned(
                  this.getPlayer(), 131001025, 1, spawnPosxx, effect.getSummonMovementType(), (byte)0, System.currentTimeMillis() + 7000L
               );
               summon.setMobObjectID(mob.getId());
               this.getPlayer().getMap().spawnSummon(summon, 7000, false, false);
               this.getPlayer().addSummon(summon);
            }
            break;
         case 131001106:
         case 131001206:
         case 131001306:
         case 131001406:
         case 131001506: {
            HashMap<SecondaryStatFlag, Integer> flags = new HashMap<>();
            flags.put(SecondaryStatFlag.indieEXP, 50);
            flags.put(SecondaryStatFlag.PinkbeanRelax, 1);
            if (this.getActiveSkillID() == 131001106) {
               packet.readInt();
               byte count = packet.readByte();

               for (int i = 0; i < count; i++) {
                  MapleMonster monster = this.getPlayer().getMap().getMonsterByOid(packet.readInt());
                  Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                  mse.put(
                     MobTemporaryStatFlag.INDIE_PDR,
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_PDR, effect.getZ(), this.getActiveSkillID(), null, false)
                  );
                  monster.applyMonsterBuff(mse, this.getActiveSkillID(), effect.getDuration(), null, Collections.EMPTY_LIST);
               }
            } else if (this.getActiveSkillID() != 131001206) {
               if (this.getActiveSkillID() == 131001306) {
                  this.getPlayer().getStat().setHp(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()), this.getPlayer());
                  this.getPlayer().updateSingleStat(MapleStat.HP, this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()));
                  this.getPlayer().getStat().setMp(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()), this.getPlayer());
                  this.getPlayer().updateSingleStat(MapleStat.MP, this.getPlayer().getStat().getMaxMp());
                  flags.put(SecondaryStatFlag.indiePadR, 15);
               } else if (this.getActiveSkillID() == 131001406) {
                  flags.put(SecondaryStatFlag.indieAsrR, effect.getIndieAsrR());
               } else if (this.getActiveSkillID() == 131001506) {
                  flags.put(SecondaryStatFlag.indieAsrR, effect.getIndieAsrR());
                  flags.put(SecondaryStatFlag.indiePadR, 10);
               }
            }

            this.getPlayer().temporaryStatSet(this.getActiveSkillID(), 1, effect.getDuration(), flags);
            break;
         }
         case 131001107:
         case 131001207:
         case 131001307:
            packet.readInt();
            Point spawnPosxxx = packet.readPos();
            if (this.getActiveSkillID() != 131001307) {
               packet.readInt();
            }

            userLeftxxx = packet.readByte() > 0;
            Rect rect = new Rect(spawnPosxxx, effect.getLt(), effect.getRb(), userLeftxxx);
            AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, spawnPosxxx, System.currentTimeMillis() + effect.getDuration());
            area.setRlType(userLeftxxx ? 1 : 0);
            this.getPlayer().getMap().spawnMist(area);
            if (this.getActiveSkillID() == 131001307) {
               effect.applyTo(this.getPlayer(), spawnPosxxx, (byte)(userLeftxxx ? 1 : 0), this.exclusive);
            }
            break;
         case 131003023:
         case 131004023:
         case 131005023:
         case 131006023:
            packet.readInt();
            Point spawnPos = packet.readPos();
            boolean userLeft = packet.readByte() > 0;
            if (effect.getSummonMovementType() != null) {
               Summoned summon = new Summoned(
                  this.getPlayer(),
                  this.getActiveSkillID(),
                  1,
                  spawnPos,
                  effect.getSummonMovementType(),
                  (byte)(userLeft ? 0 : 1),
                  System.currentTimeMillis() + effect.getDuration()
               );
               this.getPlayer().getMap().spawnSummon(summon, effect.getDuration(), false, false);
               this.getPlayer().addSummon(summon);
            }
      }

      if (effect.getCooldown(this.getPlayer()) > 0) {
         int skillid = this.getActiveSkillID();
         switch (skillid) {
            case 131001106:
            case 131001206:
            case 131001306:
            case 131001406:
            case 131001506:
               skillid = 131001006;
               break;
            case 131001107:
            case 131001207:
            case 131001307:
               skillid = 131001007;
         }

         this.getPlayer().send(CField.skillCooldown(skillid, effect.getCooldown(this.getPlayer())));
         this.getPlayer().addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 131001020) {
         this.getPlayer().setViperEnergyCharge(0);
         this.getPlayer().temporaryStatResetBySkillID(131001020);
      } else if (this.getActiveSkillID() == 131001021) {
         this.getPlayer().temporaryStatResetBySkillID(131001021);
         this.getPlayer().addCooldown(131001021, System.currentTimeMillis(), 5000L);
         this.getPlayer().send(CField.skillCooldown(131001021, 5000));
      } else if (this.getActiveSkillID() == 131001004) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.KeyDownMoving);
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.PinkbeanRollingGrade);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      Integer nnMeat = this.getPlayer().getBuffedValue(SecondaryStatFlag.PinkbeanRelax);
      if (nnMeat != null && this.getPlayer().getSecondaryStat().PinkbeanRelaxReason == 131001206) {
         int recoverHp = (int)this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) / 100;
         this.getPlayer().getStat().setHp(this.getPlayer().getStat().getHp() + recoverHp, this.getPlayer());
         this.getPlayer().updateSingleStat(MapleStat.HP, this.getPlayer().getStat().getHp());
      }
   }

   private void attackPinkBeanAtom() {
      if (!this.getPlayer().skillisCooling(131003016)) {
         this.getPlayer().giveCoolDowns(131003016, System.currentTimeMillis(), 10000L);
         SecondaryStatEffect Atom = SkillFactory.getSkill(131003016).getEffect(1);
         List<Integer> mobList = new ArrayList<>();

         for (MapleMonster mob : this.getPlayer()
            .getMap()
            .getMobsInRect(this.getPlayer().getTruePosition(), Atom.getLt().x, Atom.getLt().y, Atom.getRb().x, Atom.getRb().y)) {
            if (mob.isAlive() && !mob.getStats().isFriendly()) {
               mobList.add(mob.getObjectId());
            }

            if (mobList.size() >= Atom.getBulletCount()) {
               break;
            }
         }

         ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
         ForceAtom forceAtom = new ForceAtom(
            info, 131003016, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.CLONE_ATTACK, mobList, mobList.size()
         );
         this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
      }
   }

   private void goMiniBeans() {
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.PinkbeanMinibeenMove) != null) {
         int percent = 15;
         if (this.getPlayer().skillisCooling(131001012)) {
            percent += 10;
         }

         if (Randomizer.nextInt(100) < percent) {
            SecondaryStatEffect summon = SkillFactory.getSkill(131002015).getEffect(1);
            SummonMoveAbility sma = summon.getSummonMovementType();
            int count = 0;

            try {
               for (Summoned s : this.getPlayer().getSummonsReadLock()) {
                  if (s.getSkill() == 131002015) {
                     count++;
                  }
               }
            } finally {
               this.getPlayer().unlockSummonsReadLock();
            }

            if (count < 3 && sma != null) {
               Summoned tosummon = new Summoned(
                  this.getPlayer(),
                  summon.getSourceId(),
                  1,
                  this.getPlayer().getMap().calcDropPos(this.getPlayer().getPosition(), this.getPlayer().getTruePosition()),
                  sma,
                  (byte)0,
                  System.currentTimeMillis() + 10000L
               );
               this.getPlayer().getMap().spawnSummon(tosummon, 10000, false, false);
               this.getPlayer().addSummon(tosummon);
            }
         }
      }
   }
}
