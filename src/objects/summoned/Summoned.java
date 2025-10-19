package objects.summoned;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.models.CField;
import objects.fields.AnimatedMapleMapObject;
import objects.fields.Field;
import objects.fields.MapleMapObjectType;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.Rect;
import security.anticheat.CheatingOffense;

public class Summoned extends AnimatedMapleMapObject {
   private final int ownerid;
   private final int skillLevel;
   private final int ownerLevel;
   private final int skill;
   private Field map;
   private byte rltype;
   private int hp;
   private boolean changedMap = false;
   private SummonMoveAbility movementType;
   private int lastSummonTickCount;
   private byte Summon_tickResetCount;
   private long Server_ClientSummonTickDiff;
   private long lastAttackTime;
   private long summonRemoveTime;
   private int mobObjectID = 0;
   private List<SummonedAura> auraList = new ArrayList<>();
   private List<Integer> summonedGroup = new ArrayList<>();
   private Rect area;
   private int absorbingVortexCount = 0;
   private int absorbingVortexStack = 0;
   private boolean subJaguar = false;
   private boolean shieldDebuff = false;
   private boolean attackActive = false;
   private boolean isFixed = false;
   private boolean isZeroBeta = false;
   private int energyCharge = 0;
   private int crystalPos = 0;
   private int[] energySkillEnable = new int[4];

   public Summoned(MapleCharacter owner, SecondaryStatEffect skill, Point pos, SummonMoveAbility movementType, long summonRemoveTime) {
      this(owner, skill.getSourceId(), skill.getLevel(), pos, movementType, (byte)0, summonRemoveTime, false);
   }

   public Summoned(MapleCharacter owner, SecondaryStatEffect skill, Point pos, SummonMoveAbility movementType, byte rltype, long summonRemoveTime) {
      this(owner, skill.getSourceId(), skill.getLevel(), pos, movementType, rltype, summonRemoveTime, false);
   }

   public Summoned(MapleCharacter owner, int sourceid, int level, Point pos, SummonMoveAbility movementType, byte rltype, long summonRemoveTime) {
      this(owner, sourceid, level, pos, movementType, rltype, summonRemoveTime, false);
   }

   public Summoned(
      MapleCharacter owner, int sourceid, int level, Point pos, SummonMoveAbility movementType, byte rltype, long summonRemoveTime, boolean isZeroBeta
   ) {
      this.ownerid = owner.getId();
      this.ownerLevel = owner.getLevel();
      this.skill = sourceid;
      this.map = owner.getMap();
      this.skillLevel = level;
      this.rltype = rltype;
      this.movementType = movementType;
      this.summonRemoveTime = summonRemoveTime;
      this.isZeroBeta = isZeroBeta;
      this.setPosition(pos);
      if (!this.isPuppet()) {
         this.lastSummonTickCount = 0;
         this.Summon_tickResetCount = 0;
         this.Server_ClientSummonTickDiff = 0L;
         this.lastAttackTime = 0L;
      }

      if (sourceid >= 400051052 && sourceid <= 400051053) {
         this.summonedGroup.add(400051038);
         this.summonedGroup.add(400051052);
         this.summonedGroup.add(400051053);
      }
   }

   public void addAura(SummonedAura aura) {
      this.auraList.add(aura);
   }

   public void setArea(Rect rect) {
      this.area = rect;
   }

   public Rect getArea() {
      return this.area;
   }

   public void affectToMob(MapleMonster mob) {
      for (SummonedAura aura : this.auraList) {
         SecondaryStatEffect effect = SkillFactory.getSkill(this.getSkill()).getEffect(this.getSkillLevel());
         if (effect != null) {
            aura.affectToMob(this.getSkill(), effect, mob);
         }
      }
   }

   public void affectToUser(MapleCharacter player) {
      for (SummonedAura aura : this.auraList) {
         SecondaryStatEffect effect = SkillFactory.getSkill(this.getSkill()).getEffect(this.getSkillLevel());
         if (effect != null) {
            aura.affectToUser(this.getSkill(), effect, player);
         }
      }
   }

   @Override
   public final void sendSpawnData(MapleClient client) {
   }

   @Override
   public final void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.SummonPacket.removeSummon(this, false));
   }

   public final void updateMap(Field map) {
      this.map = map;
   }

   public final MapleCharacter getOwner() {
      return this.map.getCharacterById(this.ownerid);
   }

   public final int getOwnerId() {
      return this.ownerid;
   }

   public final int getOwnerLevel() {
      return this.ownerLevel;
   }

   public final int getSkill() {
      return this.skill;
   }

   public final int getHP() {
      return this.hp;
   }

   public final int getSummonRLType() {
      return this.rltype;
   }

   public void setSummonRLType(byte rlType) {
      this.rltype = rlType;
   }

   public final void addHP(int delta) {
      this.hp += delta;
   }

   public final SummonMoveAbility getMoveAbility() {
      return this.movementType;
   }

   public final boolean isPuppet() {
      switch (this.skill) {
         case 4341006:
         case 13111004:
         case 13111024:
         case 13120007:
            return true;
         default:
            return this.isAngel();
      }
   }

   public final boolean isAngel() {
      return GameConstants.isAngel(this.skill);
   }

   public final boolean isMultiAttack() {
      switch (this.skill) {
         case 80002888:
         case 80002889:
         case 131001022:
         case 131002022:
         case 131003022:
         case 131003023:
         case 131004022:
         case 131004023:
         case 131005022:
         case 131005023:
         case 131006022:
         case 131006023:
         case 154121041:
         case 162101003:
         case 162101006:
         case 162101012:
         case 162121012:
         case 162121015:
         case 400001059:
         case 400001060:
         case 400001062:
         case 400001064:
         case 400001071:
            return true;
         default:
            return this.skill == 51121016
               || this.skill == 101100100
               || this.skill == 101100101
               || this.skill == 400021095
               || this.skill == 400031047
               || this.skill == 400031049
               || this.skill == 400031051
               || this.skill == 25121133
               || this.skill == 400051038
               || this.skill == 400051039
               || this.skill == 400051052
               || this.skill == 400051053
               || this.skill == 400001059
               || this.skill == 400001040
               || this.skill == 400001039
               || this.skill == 151111001
               || this.skill == 400051039
               || this.skill == 400051052
               || this.skill == 400051053
               || this.skill == 400041052
               || this.skill == 400041050
               || this.skill == 164121008
               || this.skill == 164111007
               || this.skill == 400021068
               || this.skill == 400021063
               || this.skill == 400051046
               || this.skill == 400041044
               || this.skill == 400021069
               || this.skill == 400011090
               || this.skill == 400011077
               || this.skill == 400011078
               || this.skill == 400021071
               || this.skill == 400021073
               || this.skill == 400011088
               || this.skill == 400021067
               || this.skill == 36121013
               || this.skill == 36121002
               || this.skill == 35111002
               || this.skill == 35121003
               || this.skill == 61111220
               || this.skill == 61111002
               || !this.isGaviota() && this.skill != 33101008 && this.skill < 35000000
               || this.skill == 35111009
               || this.skill == 35111010
               || this.skill == 35111001
               || this.skill == 35101012
               || this.skill == 400001012
               || this.skill == 400011001
               || this.skill == 400011002
               || this.skill == 400021033
               || this.skill == 400021005
               || this.skill == 400001019
               || this.skill == 400001022
               || this.skill == 400051009
               || this.skill == 400011006
               || this.skill == 400011057
               || this.skill == 400001013
               || this.skill >= 400011012 && this.skill <= 400011014
               || this.skill == 400041033
               || this.skill == 152001003
               || this.skill == 152101008
               || this.skill == 152101000
               || this.skill == 152121005
               || this.skill == 152121006
               || this.skill == 131003026
               || this.skill == 131002015
               || this.skill == 131001019;
      }
   }

   public final boolean isSoulWeaponSummon(int skillID) {
      switch (skillID) {
         case 80001217:
         case 80001219:
         case 80001266:
         case 80001269:
         case 80001270:
         case 80001281:
         case 80001282:
         case 80001322:
         case 80001395:
         case 80001396:
         case 80001493:
         case 80001494:
         case 80001495:
         case 80001496:
         case 80001497:
         case 80001498:
         case 80001499:
         case 80001500:
         case 80001501:
         case 80001502:
            return true;
         default:
            return false;
      }
   }

   public final boolean isGaviota() {
      return this.skill == 5211002;
   }

   public final boolean isBeholder() {
      return this.skill == 1301013;
   }

   public final int getSkillLevel() {
      return this.skillLevel;
   }

   public final int getAssistType() {
      if (this.isAngel()) {
         return SummonAssistType.Heal.getType();
      } else if ((this.skill == 33111003 || this.skill == 3120012 || this.skill == 3220012 || !this.isPuppet())
         && this.skill != 5221029
         && this.skill != 33101008
         && this.skill != 35111002) {
         switch (this.skill) {
            case 1301013:
            case 36121014:
               return SummonAssistType.Heal.getType();
            case 4341006:
            case 13111024:
            case 13120007:
            case 14110033:
            case 14111024:
            case 14120017:
            case 14141502:
            case 32001014:
            case 32100010:
            case 32110017:
            case 32120019:
            case 32141000:
            case 131001017:
            case 131001307:
            case 152141501:
            case 152141505:
            case 154110010:
            case 164121006:
            case 164121011:
            case 400001071:
            case 400021092:
            case 400051011:
               return SummonAssistType.None.getType();
            case 5201012:
            case 5211019:
            case 5221022:
            case 5221027:
               return SummonAssistType.AttackB2Body.getType();
            case 5210015:
               return SummonAssistType.AttackCrew.getType();
            case 12120013:
            case 12120014:
            case 25121133:
            case 35121003:
            case 152001003:
            case 152101000:
            case 400001064:
            case 400011001:
            case 400011077:
            case 400011078:
            case 400021068:
            case 400021100:
            case 400041052:
            case 500061012:
            case 500061066:
               return SummonAssistType.AttackManual.getType();
            case 23111010:
            case 35111001:
            case 35111009:
            case 35111010:
            case 36121002:
               return SummonAssistType.AttackEx.getType();
            case 35121009:
            case 400051022:
               return SummonAssistType.Summon.getType();
            case 151100002:
               return SummonAssistType.AttackEx2.getType();
            case 162101003:
            case 162101006:
            case 162121012:
            case 162121015:
            case 400001040:
            case 400021071:
            case 400021073:
            case 400031047:
            case 400041044:
            case 400051038:
            case 400051046:
            case 400051052:
            case 400051053:
            case 400051068:
               return SummonAssistType.AttackSpider.getType();
            case 164111007:
               return SummonAssistType.AttackEx3.getType();
            case 400011012:
            case 400011013:
            case 400011014:
               return SummonAssistType.AttackGuard.getType();
            case 400021047:
            case 400021063:
               return SummonAssistType.AttackBomb.getType();
            case 400041038:
               return SummonAssistType.AttackForceAtom.getType();
            case 400051009:
               return SummonAssistType.AttackProtector.getType();
            default:
               return !this.subJaguar && this.skill >= 33001007 && this.skill <= 33001015
                  ? SummonAssistType.AttackJaguar.getType()
                  : SummonAssistType.Attack.getType();
         }
      } else {
         return SummonAssistType.None.getType();
      }
   }

   @Override
   public final MapleMapObjectType getType() {
      return MapleMapObjectType.SUMMON;
   }

   public final void CheckSummonAttackFrequency(MapleCharacter chr, int tickcount) {
      int tickdifference = tickcount - this.lastSummonTickCount;
      if (tickdifference < SkillFactory.getSummonData(this.skill).delay) {
         chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
      }

      long STime_TC = System.currentTimeMillis() - tickcount;
      long S_C_Difference = this.Server_ClientSummonTickDiff - STime_TC;
      if (S_C_Difference > 500L) {
         chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
      }

      this.Summon_tickResetCount++;
      if (this.Summon_tickResetCount > 4) {
         this.Summon_tickResetCount = 0;
         this.Server_ClientSummonTickDiff = STime_TC;
      }

      this.lastSummonTickCount = tickcount;
   }

   public final void CheckPVPSummonAttackFrequency(MapleCharacter chr) {
      long tickdifference = System.currentTimeMillis() - this.lastAttackTime;
      if (tickdifference < SkillFactory.getSummonData(this.skill).delay) {
         chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
      }

      this.lastAttackTime = System.currentTimeMillis();
   }

   public final boolean isChangedMap() {
      return this.changedMap;
   }

   public final void setChangedMap(boolean cm) {
      this.changedMap = cm;
   }

   public long getSummonRemoveTime() {
      return this.summonRemoveTime;
   }

   public void setSummonRemoveTime(long time) {
      this.summonRemoveTime = time;
   }

   public int getMobObjectID() {
      return this.mobObjectID;
   }

   public void setMobObjectID(int mobObjectID) {
      this.mobObjectID = mobObjectID;
   }

   public List<Integer> getSummonedGroup() {
      return this.summonedGroup;
   }

   public void addSummonedGroup(Integer summonedID) {
      this.summonedGroup.add(summonedID);
   }

   public int getAbsorbingVortexCount() {
      return this.absorbingVortexCount;
   }

   public void setAbsorbingVortexCount(int absorbingVortexCount) {
      this.absorbingVortexCount = absorbingVortexCount;
   }

   public int getAbsorbingVortexStack() {
      return this.absorbingVortexStack;
   }

   public void setAbsorbingVortexStack(int absorbingVortexStack) {
      this.absorbingVortexStack = absorbingVortexStack;
   }

   public boolean isShieldDebuff() {
      return this.shieldDebuff;
   }

   public void setShieldDebuff(boolean shieldDebuff) {
      this.shieldDebuff = shieldDebuff;
   }

   public boolean isSubJaguar() {
      return this.subJaguar;
   }

   public void setSubJaguar(boolean subJaguar) {
      this.subJaguar = subJaguar;
   }

   public boolean isAttackActive() {
      return this.attackActive;
   }

   public void setAttackActive(boolean attackActive) {
      this.attackActive = attackActive;
   }

   public boolean isFixed() {
      return this.isFixed;
   }

   public void setFixed(boolean fixed) {
      this.isFixed = fixed;
   }

   public int getEnergyCharge() {
      return this.energyCharge;
   }

   public void setEnergyCharge(int energyCharge) {
      this.energyCharge = energyCharge;
   }

   public void addEnergyCharge(int charge, int max) {
      this.energyCharge = Math.min(max, this.energyCharge + charge);
   }

   public int getEnergyLevel() {
      int level = 0;
      if (this.energyCharge >= 30 && this.energyCharge < 60) {
         level = 1;
      } else if (this.energyCharge >= 60 && this.energyCharge < 90) {
         level = 2;
      } else if (this.energyCharge >= 90 && this.energyCharge < 150) {
         level = 3;
      } else if (this.energyCharge >= 150) {
         level = 4;
      }

      return level;
   }

   public int getEnableEnergySkill(int level) {
      return this.energySkillEnable[level];
   }

   public void setEnableEnergySkill(int level, int value) {
      this.energySkillEnable[level - 1] = value;
   }

   public void resetEnableEnergySkill() {
      this.energyCharge = 0;
      this.energySkillEnable = new int[4];
   }

   public boolean isZeroBeta() {
      return this.isZeroBeta;
   }

   public void setCrystalPos(int pos) {
      this.crystalPos = pos;
   }

   public int getCrystalPos() {
      return this.crystalPos;
   }
}
