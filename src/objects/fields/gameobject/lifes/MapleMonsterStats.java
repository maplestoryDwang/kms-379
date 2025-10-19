package objects.fields.gameobject.lifes;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import objects.fields.gameobject.lifes.mobskills.LinkMobInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.PassiveInfo;
import objects.fields.gameobject.lifes.mobskills.TransInfo;
import objects.utils.Triple;

public class MapleMonsterStats {
   private byte cp;
   private byte selfDestruction_action;
   private byte tagColor;
   private byte tagBgColor;
   private byte rareItemDropLevel;
   private byte HPDisplayType;
   private byte summonType;
   private byte PDRate;
   private byte MDRate;
   private byte category;
   private short level;
   private short charismaEXP;
   private long hp;
   private long maxHp;
   private long exp;
   private int id;
   private int mp;
   private int maxMp;
   private int removeAfter;
   private int buffToGive;
   private int rewardSprinkle;
   private int rewardSprinkleSpeed;
   private int fixedDamage;
   private int selfDestruction_hp;
   private int dropItemPeriod;
   private int point;
   private int eva;
   private int acc;
   private int PhysicalAttack;
   private int MagicAttack;
   private int speed;
   private int partyBonusR;
   private int pushed;
   private int dieDelay;
   private int ex;
   private boolean skeleton;
   private boolean boss;
   private boolean undead;
   private boolean ffaLoot;
   private boolean firstAttack;
   private boolean isExplosiveReward;
   private boolean mobile;
   private boolean fly;
   private boolean onlyHittedByCommonAttack;
   private boolean onlyNormalAttack;
   private boolean friendly;
   private boolean noDoom;
   private boolean invincible;
   private boolean partyBonusMob;
   private boolean changeable;
   private boolean escort;
   private boolean allyMob;
   private int hpNoticePerNum = 100;
   private String name;
   private String mobType;
   private EnumMap<Element, ElementalEffectiveness> resistance = new EnumMap<>(Element.class);
   private List<Integer> revives = new ArrayList<>();
   private List<MobSkill> skills = new ArrayList<>();
   private List<MobAttackInfo> mai = new ArrayList<>();
   private BanishInfo banish;
   private int moveAbility = 0;
   private TransInfo transInfo;
   private LinkMobInfo linkMobInfo;
   private List<PassiveInfo> passiveInfos;
   private List<Triple<String, Integer, Integer>> skeletonData = new ArrayList<>();

   public MapleMonsterStats(int id) {
      this.id = id;
   }

   public void copy(MapleMonsterStats stat) {
      this.cp = stat.cp;
      this.selfDestruction_action = stat.selfDestruction_action;
      this.tagColor = stat.tagColor;
      this.tagBgColor = stat.tagBgColor;
      this.rareItemDropLevel = stat.rareItemDropLevel;
      this.HPDisplayType = stat.HPDisplayType;
      this.summonType = stat.summonType;
      this.PDRate = stat.PDRate;
      this.MDRate = stat.MDRate;
      this.category = stat.category;
      this.level = stat.level;
      this.charismaEXP = stat.charismaEXP;
      this.hp = stat.hp;
      this.maxHp = stat.maxHp;
      this.exp = stat.exp;
      this.mp = stat.mp;
      this.maxMp = stat.maxMp;
      this.removeAfter = stat.removeAfter;
      this.buffToGive = stat.buffToGive;
      this.fixedDamage = stat.fixedDamage;
      this.selfDestruction_hp = stat.selfDestruction_hp;
      this.dropItemPeriod = stat.dropItemPeriod;
      this.point = stat.point;
      this.eva = stat.eva;
      this.acc = stat.acc;
      this.PhysicalAttack = stat.PhysicalAttack;
      this.MagicAttack = stat.MagicAttack;
      this.speed = stat.speed;
      this.partyBonusR = stat.partyBonusR;
      this.pushed = stat.pushed;
      this.dieDelay = stat.dieDelay;
      this.boss = stat.boss;
      this.skeleton = stat.skeleton;
      this.undead = stat.undead;
      this.ffaLoot = stat.ffaLoot;
      this.firstAttack = stat.firstAttack;
      this.isExplosiveReward = stat.isExplosiveReward;
      this.mobile = stat.mobile;
      this.fly = stat.fly;
      this.onlyNormalAttack = stat.onlyNormalAttack;
      this.friendly = stat.friendly;
      this.noDoom = stat.noDoom;
      this.invincible = stat.invincible;
      this.partyBonusMob = stat.partyBonusMob;
      this.changeable = stat.changeable;
      this.escort = stat.escort;
      this.name = stat.name;
      this.mobType = stat.mobType;
      this.moveAbility = stat.moveAbility;
      this.onlyHittedByCommonAttack = stat.onlyHittedByCommonAttack;
      this.hpNoticePerNum = stat.hpNoticePerNum;
      this.ex = stat.ex;
      this.setTransInfo(stat.getTransInfo());
      this.setLinkMobInfo(stat.getLinkMobInfo());
      this.setPassiveInfos(stat.getPassiveInfos());

      for (Entry<Element, ElementalEffectiveness> entry : stat.resistance.entrySet()) {
         this.resistance.put(entry.getKey(), entry.getValue());
      }

      for (Integer r : stat.revives) {
         this.revives.add(r);
      }

      for (MobSkill ms : stat.skills) {
         this.skills.add(ms);
      }

      for (MobAttackInfo mai : stat.mai) {
         this.mai.add(mai);
      }

      for (Triple<String, Integer, Integer> sk : stat.getSkeletonData()) {
         this.getSkeletonData().add(sk);
      }

      this.banish = stat.banish;
      this.allyMob = stat.allyMob;
   }

   public int getId() {
      return this.id;
   }

   public long getExp() {
      return this.exp;
   }

   public void setExp(long exp) {
      this.exp = exp;
   }

   public long getHp() {
      return this.hp;
   }

   public void setHp(long hp) {
      this.hp = hp;
   }

   public void setMaxHp(long hp) {
      this.maxHp = hp;
   }

   public long getMaxHp() {
      return this.maxHp;
   }

   public int getMp() {
      return this.mp;
   }

   public void setMp(int mp) {
      this.mp = mp;
   }

   public void setMaxMp(int mp) {
      this.maxMp = mp;
   }

   public int getMaxMp() {
      return this.maxMp;
   }

   public short getLevel() {
      return this.level;
   }

   public void setLevel(short level) {
      this.level = level;
   }

   public short getCharismaEXP() {
      return this.charismaEXP;
   }

   public void setCharismaEXP(short leve) {
      this.charismaEXP = leve;
   }

   public void setSelfD(byte selfDestruction_action) {
      this.selfDestruction_action = selfDestruction_action;
   }

   public byte getSelfD() {
      return this.selfDestruction_action;
   }

   public void setSelfDHP(int selfDestruction_hp) {
      this.selfDestruction_hp = selfDestruction_hp;
   }

   public int getSelfDHp() {
      return this.selfDestruction_hp;
   }

   public void setFixedDamage(int damage) {
      this.fixedDamage = damage;
   }

   public int getFixedDamage() {
      return this.fixedDamage;
   }

   public void setPushed(int damage) {
      this.pushed = damage;
   }

   public int getPushed() {
      return this.pushed;
   }

   public void setHpNoticePerNum(int value) {
      this.hpNoticePerNum = value;
   }

   public int getHpNoticePerNum() {
      return this.hpNoticePerNum;
   }

   public void setPhysicalAttack(int PhysicalAttack) {
      this.PhysicalAttack = PhysicalAttack;
   }

   public int getPhysicalAttack() {
      return this.PhysicalAttack;
   }

   public final void setMagicAttack(int MagicAttack) {
      this.MagicAttack = MagicAttack;
   }

   public final int getMagicAttack() {
      return this.MagicAttack;
   }

   public final void setEva(int eva) {
      this.eva = eva;
   }

   public final void setRewardSprinkleSpeed(int rewardSprinkleSpeed) {
      this.rewardSprinkleSpeed = rewardSprinkleSpeed;
   }

   public final int getRewardSprinkleSpeed() {
      return this.rewardSprinkleSpeed;
   }

   public final void setRewardSprinkle(int rewardSprinkle) {
      this.rewardSprinkle = rewardSprinkle;
   }

   public final int getRewardSprinkle() {
      return this.rewardSprinkle;
   }

   public final int getEva() {
      return this.eva;
   }

   public final void setAcc(int acc) {
      this.acc = acc;
   }

   public final int getAcc() {
      return this.acc;
   }

   public final void setSpeed(int speed) {
      this.speed = speed;
   }

   public final int getSpeed() {
      return this.speed;
   }

   public final void setPartyBonusRate(int speed) {
      this.partyBonusR = speed;
   }

   public final int getPartyBonusRate() {
      return this.partyBonusR;
   }

   public void setOnlyNormalAttack(boolean onlyNormalAttack) {
      this.onlyNormalAttack = onlyNormalAttack;
   }

   public boolean getOnlyNormalAttack() {
      return this.onlyNormalAttack;
   }

   public BanishInfo getBanishInfo() {
      return this.banish;
   }

   public void setBanishInfo(BanishInfo banish) {
      this.banish = banish;
   }

   public int getRemoveAfter() {
      return this.removeAfter;
   }

   public void setRemoveAfter(int removeAfter) {
      this.removeAfter = removeAfter;
   }

   public byte getrareItemDropLevel() {
      return this.rareItemDropLevel;
   }

   public void setrareItemDropLevel(byte rareItemDropLevel) {
      this.rareItemDropLevel = rareItemDropLevel;
   }

   public void setBoss(boolean boss) {
      this.boss = boss;
   }

   public boolean isBoss() {
      return this.boss;
   }

   public void setSkeleton(boolean skeleton) {
      this.skeleton = skeleton;
   }

   public boolean isSkeleton() {
      return this.skeleton;
   }

   public void setFfaLoot(boolean ffaLoot) {
      this.ffaLoot = ffaLoot;
   }

   public boolean isFfaLoot() {
      return this.ffaLoot;
   }

   public void setEscort(boolean ffaL) {
      this.escort = ffaL;
   }

   public boolean isEscort() {
      return this.escort;
   }

   public void setExplosiveReward(boolean isExplosiveReward) {
      this.isExplosiveReward = isExplosiveReward;
   }

   public boolean isExplosiveReward() {
      return this.isExplosiveReward;
   }

   public void setMobile(boolean mobile) {
      this.mobile = mobile;
   }

   public boolean getMobile() {
      return this.mobile;
   }

   public void setFly(boolean fly) {
      this.fly = fly;
   }

   public boolean getFly() {
      return this.fly;
   }

   public List<Integer> getRevives() {
      return this.revives;
   }

   public void setRevives(List<Integer> revives) {
      this.revives = revives;
   }

   public void setUndead(boolean undead) {
      this.undead = undead;
   }

   public boolean getUndead() {
      return this.undead;
   }

   public void setSummonType(byte selfDestruction) {
      this.summonType = selfDestruction;
   }

   public byte getSummonType() {
      return this.summonType;
   }

   public void setCategory(byte selfDestruction) {
      this.category = selfDestruction;
   }

   public byte getCategory() {
      return this.category;
   }

   public void setPDRate(byte selfDestruction) {
      this.PDRate = selfDestruction;
   }

   public byte getPDRate() {
      return this.PDRate;
   }

   public void setMDRate(byte selfDestruction) {
      this.MDRate = selfDestruction;
   }

   public byte getMDRate() {
      return this.MDRate;
   }

   public EnumMap<Element, ElementalEffectiveness> getElements() {
      return this.resistance;
   }

   public void setEffectiveness(Element e, ElementalEffectiveness ee) {
      this.resistance.put(e, ee);
   }

   public void removeEffectiveness(Element e) {
      this.resistance.remove(e);
   }

   public ElementalEffectiveness getEffectiveness(Element e) {
      ElementalEffectiveness elementalEffectiveness = this.resistance.get(e);
      return elementalEffectiveness == null ? ElementalEffectiveness.NORMAL : elementalEffectiveness;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getType() {
      return this.mobType;
   }

   public void setType(String mobt) {
      this.mobType = mobt;
   }

   public byte getTagColor() {
      return this.tagColor;
   }

   public void setTagColor(int tagColor) {
      this.tagColor = (byte)tagColor;
   }

   public byte getTagBgColor() {
      return this.tagBgColor;
   }

   public void setTagBgColor(int tagBgColor) {
      this.tagBgColor = (byte)tagBgColor;
   }

   public void setSkills(List<MobSkill> skill_) {
      for (MobSkill skill : skill_) {
         this.skills.add(skill);
      }
   }

   public List<MobSkill> getSkills() {
      return Collections.unmodifiableList(this.skills);
   }

   public byte getNoSkills() {
      return (byte)this.skills.size();
   }

   public boolean hasSkill(int skillID, int level) {
      for (MobSkill skill : this.skills) {
         if (skill.getMobSkillID() == skillID && skill.getLevel() == level) {
            return true;
         }
      }

      return false;
   }

   public void setFirstAttack(boolean firstAttack) {
      this.firstAttack = firstAttack;
   }

   public boolean isFirstAttack() {
      return this.firstAttack;
   }

   public void setCP(byte cp) {
      this.cp = cp;
   }

   public byte getCP() {
      return this.cp;
   }

   public void setPoint(int cp) {
      this.point = cp;
   }

   public int getPoint() {
      return this.point;
   }

   public void setFriendly(boolean friendly) {
      this.friendly = friendly;
   }

   public boolean isFriendly() {
      return this.friendly;
   }

   public void setInvincible(boolean invin) {
      this.invincible = invin;
   }

   public boolean isInvincible() {
      return this.invincible;
   }

   public void setChange(boolean invin) {
      this.changeable = invin;
   }

   public boolean isChangeable() {
      return this.changeable;
   }

   public void setPartyBonus(boolean invin) {
      this.partyBonusMob = invin;
   }

   public boolean isPartyBonus() {
      return this.partyBonusMob;
   }

   public void setNoDoom(boolean doom) {
      this.noDoom = doom;
   }

   public boolean isNoDoom() {
      return this.noDoom;
   }

   public void setBuffToGive(int buff) {
      this.buffToGive = buff;
   }

   public int getBuffToGive() {
      return this.buffToGive;
   }

   public byte getHPDisplayType() {
      return this.HPDisplayType;
   }

   public void setHPDisplayType(byte HPDisplayType) {
      this.HPDisplayType = HPDisplayType;
   }

   public int getDropItemPeriod() {
      return this.dropItemPeriod;
   }

   public void setDropItemPeriod(int d) {
      this.dropItemPeriod = d;
   }

   public void addMobAttack(MobAttackInfo ma) {
      this.mai.add(ma);
   }

   public MobAttackInfo getMobAttack(int attack) {
      return attack < this.mai.size() && attack >= 0 ? this.mai.get(attack) : null;
   }

   public List<MobAttackInfo> getMobAttacks() {
      return this.mai;
   }

   public int dropsMeso() {
      if (this.getRemoveAfter() == 0
         && !this.isInvincible()
         && !this.getOnlyNormalAttack()
         && this.getDropItemPeriod() <= 0
         && this.getCP() <= 0
         && this.getPoint() <= 0
         && this.getFixedDamage() <= 0
         && this.getSelfD() == -1
         && this.getPDRate() > 0
         && this.getMDRate() > 0) {
         int mobId = this.getId() / 100000;
         if (GameConstants.getPartyPlayHP(this.getId()) > 0 || mobId == 97 || mobId == 95 || mobId == 93 || mobId == 91 || mobId == 90) {
            return 0;
         } else if (this.isExplosiveReward()) {
            return 7;
         } else {
            return this.isBoss() ? 2 : 1;
         }
      } else {
         return 0;
      }
   }

   public int getDieDelay() {
      return this.dieDelay;
   }

   public void setDieDelay(int dieDelay) {
      this.dieDelay = dieDelay;
   }

   public int getMoveAbility() {
      return this.moveAbility;
   }

   public void setMoveAbility(int moveAbility) {
      this.moveAbility = moveAbility;
   }

   public TransInfo getTransInfo() {
      return this.transInfo;
   }

   public void setTransInfo(TransInfo transInfo) {
      this.transInfo = transInfo;
   }

   public LinkMobInfo getLinkMobInfo() {
      return this.linkMobInfo;
   }

   public void setLinkMobInfo(LinkMobInfo linkMobInfo) {
      this.linkMobInfo = linkMobInfo;
   }

   public List<PassiveInfo> getPassiveInfos() {
      return this.passiveInfos;
   }

   public void setPassiveInfos(List<PassiveInfo> passiveInfos) {
      this.passiveInfos = passiveInfos;
   }

   public List<Triple<String, Integer, Integer>> getSkeletonData() {
      return this.skeletonData;
   }

   public void addSkeletonData(String key, Integer mid, Integer value) {
      this.skeletonData.add(new Triple<>(key, mid, value));
   }

   public void setSkeletonData(List<Triple<String, Integer, Integer>> skeletonData) {
      this.skeletonData = skeletonData;
   }

   public boolean isAllyMob() {
      return this.allyMob;
   }

   public void setAllyMob(boolean allyMob) {
      this.allyMob = allyMob;
   }

   public boolean isOnlyHittedByCommonAttack() {
      return this.onlyHittedByCommonAttack;
   }

   public void setOnlyHittedByCommonAttack(boolean onlyHittedByCommonAttack) {
      this.onlyHittedByCommonAttack = onlyHittedByCommonAttack;
   }

   public void setEx(int ex) {
      this.ex = ex;
   }

   public int getEx() {
      return this.ex;
   }
}
