package objects.fields.gameobject.lifes;

import java.lang.ref.WeakReference;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;

public class MobTemporaryStatEffect {
   private MobTemporaryStatFlag stati;
   private final int skill;
   private final MobSkillInfo mobskill;
   private final boolean monsterSkill;
   private WeakReference<MapleCharacter> weakChr = null;
   private Integer value;
   private Integer x;
   private Integer y;
   private Integer u;
   private Integer n;
   private Integer c;
   private Integer p;
   private Integer w;
   private long poisonDamage = 0L;
   private boolean reflect = false;
   private long cancelTime = 0L;
   private int interval = 1000;
   private int end = 0;
   private int dotAnimation = 0;
   private int dotCount = 1;
   private int superPos = 0;
   private int attackDelay = 0;
   private int dotTickIdx = 0;
   private int dotTickDamR = 0;
   private int duration = 0;
   private int maxDotTickDamR = 0;

   public MobTemporaryStatEffect(MobTemporaryStatFlag stat, Integer x, int skillId, MobSkillInfo mobskill, boolean monsterSkill) {
      this.stati = stat;
      this.skill = skillId;
      this.monsterSkill = monsterSkill;
      this.mobskill = mobskill;
      this.x = x;
   }

   public MobTemporaryStatEffect(MobTemporaryStatFlag stat, Integer x, int skillId, MobSkillInfo mobskill, boolean monsterSkill, boolean reflect) {
      this.stati = stat;
      this.skill = skillId;
      this.monsterSkill = monsterSkill;
      this.mobskill = mobskill;
      this.x = x;
      this.reflect = reflect;
   }

   public final MobTemporaryStatFlag getStati() {
      return this.stati;
   }

   public final Integer getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public Integer getP() {
      return this.p == null ? 0 : this.p;
   }

   public void setP(Integer p) {
      this.p = p;
   }

   public Integer getC() {
      return this.c == null ? 0 : this.c;
   }

   public void setC(Integer c) {
      this.c = c;
   }

   public final Integer getW() {
      return this.w == null ? 0 : this.w;
   }

   public void setW(Integer w) {
      this.w = w;
   }

   public final void setValue(MobTemporaryStatFlag status, Integer newVal) {
      this.stati = status;
      this.x = newVal;
   }

   public final int getSkillID() {
      return this.skill;
   }

   public final MobSkillInfo getMobSkill() {
      return this.mobskill;
   }

   public final boolean isMonsterSkill() {
      return this.monsterSkill;
   }

   public final void setCancelTask(long cancelTask) {
      this.cancelTime = System.currentTimeMillis() + cancelTask;
   }

   public final long getCancelTask() {
      return this.cancelTime;
   }

   public final void setPoisonDamage(long poisonSchedule, MapleCharacter chrr) {
      this.poisonDamage = poisonSchedule;
      this.weakChr = new WeakReference<>(chrr);
   }

   public final long getPoisonDamage() {
      return this.poisonDamage;
   }

   public final boolean shouldCancel(long now) {
      return this.cancelTime > 0L && this.cancelTime <= now;
   }

   public final void cancelTask() {
      this.cancelTime = 0L;
   }

   public final boolean isReflect() {
      return this.reflect;
   }

   public final int getFromID() {
      return this.weakChr != null && this.weakChr.get() != null ? this.weakChr.get().getId() : 0;
   }

   public final void cancelPoisonSchedule(MapleMonster mm) {
      mm.doPoison(this, this.weakChr);
      this.poisonDamage = 0L;
      this.weakChr = null;
   }

   public final WeakReference<MapleCharacter> getPoisonOwner() {
      return this.weakChr;
   }

   public int getInterval() {
      return this.interval;
   }

   public void setInterval(int interval) {
      this.interval = interval;
   }

   public int getEnd() {
      return this.end;
   }

   public void setEnd(int end) {
      this.end = end;
   }

   public int getDotAnimation() {
      return this.dotAnimation;
   }

   public void setDotAnimation(int dotAnimation) {
      this.dotAnimation = dotAnimation;
   }

   public int getDotCount() {
      return this.dotCount;
   }

   public void setDotCount(int dotCount) {
      this.dotCount = dotCount;
   }

   public int getSuperPos() {
      return this.superPos;
   }

   public void setSuperPos(int superPos) {
      this.superPos = superPos;
   }

   public int getAttackDelay() {
      return this.attackDelay;
   }

   public void setAttackDelay(int attackDelay) {
      this.attackDelay = attackDelay;
   }

   public int getDotTickIdx() {
      return this.dotTickIdx;
   }

   public void setDotTickIdx(int dotTickIdx) {
      this.dotTickIdx = dotTickIdx;
   }

   public int getDotTickDamR() {
      return this.dotTickDamR;
   }

   public void setDotTickDamR(int dotTickDamR) {
      this.dotTickDamR = dotTickDamR;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public Integer getValue() {
      return this.value;
   }

   public void setValue(Integer value) {
      this.value = value;
   }

   public Integer getU() {
      return this.u == null ? 0 : this.u;
   }

   public void setU(Integer u) {
      this.u = u;
   }

   public Integer getN() {
      return this.n;
   }

   public void setN(Integer n) {
      this.n = n;
   }

   public int getMaxDotTickDamR() {
      return this.maxDotTickDamR;
   }

   public void setMaxDotTickDamR(int maxDotTickDamR) {
      this.maxDotTickDamR = maxDotTickDamR;
   }
}
