package objects.fields.gameobject;

import constants.GameConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import network.models.CField;
import objects.effect.child.PostSkillEffect;
import objects.effect.child.SpecialSkillEffect;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.child.jinhillah.JinHillahPoisonMist;
import objects.fields.child.moonbridge.Field_FerociousBattlefield;
import objects.fields.gameobject.lifes.Element;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Rect;

public class AffectedArea extends MapleMapObject {
   private MapleCharacter owner;
   private Rectangle mistPosition;
   private Rect mistRect;
   private Rect buffRect;
   private Point position;
   private Point forcePos;
   private SecondaryStatEffect source;
   private MobSkillInfo skill;
   private boolean isMobMist;
   private int skillId = -1;
   private int skillDelay;
   private int skilllevel;
   private Element element;
   private int ownerId;
   private int rlType;
   private ScheduledFuture<?> schedule = null;
   private ScheduledFuture<?> poisonSchedule = null;
   private int remainHealCount = 0;
   private int preUOL = 0;
   private int dwOption = 0;
   private long startTime = 0L;
   private long endTime = 0L;
   private int tdBreakTime = 0;
   private long lastTimeDistortionTime = 0L;
   private JinHillahPoisonMist jHillahMist;
   private int jMistPhase = 0;

   public AffectedArea(Rect mistRect, MapleMonster mob, MobSkillInfo skill, Point position, long endTime) {
      this(mistRect, mob, skill, position, 1, endTime);
   }

   public AffectedArea(Rect mistRect, MapleMonster mob, MobSkillInfo skill, Point position, int rlType, long endTime) {
      this.mistRect = mistRect;
      this.position = position;
      this.ownerId = mob.getObjectId();
      this.skill = skill;
      this.skilllevel = skill.getSkillLevel();
      this.setRlType(rlType);
      this.endTime = endTime;
      this.startTime = System.currentTimeMillis();
      if (skill.getSkillId() == 131 && this.skilllevel == 28) {
         this.preUOL = 1;
         this.dwOption = 5;
      }

      this.isMobMist = true;
      this.skillDelay = 0;
      switch (skill.getSkillId()) {
         case 186:
         case 227:
            this.element = Element.Gravity;
            break;
         default:
            this.element = Element.Physical;
      }
   }

   public AffectedArea(Rect rect, int ownerID, MobSkillInfo skill, Point position, int rlType, long endTime) {
      this.mistRect = rect;
      this.position = position;
      this.ownerId = ownerID;
      this.skill = skill;
      this.skilllevel = skill.getSkillLevel();
      this.setRlType(rlType);
      this.endTime = endTime;
      this.startTime = System.currentTimeMillis();
      this.isMobMist = true;
      this.skillDelay = 0;
      this.element = Element.Physical;
   }

   public AffectedArea(Rect rect, MapleCharacter owner, SecondaryStatEffect source, Point position, long endTime) {
      this.mistRect = rect;
      this.mistPosition = new Rectangle(rect.getLt().x, rect.getLt().y, rect.getWidth(), rect.getHeight());
      this.position = position;
      this.owner = owner;
      this.ownerId = owner.getId();
      this.source = source;
      this.isMobMist = false;
      this.skilllevel = owner.getTotalSkillLevel(GameConstants.getLinkedAranSkill(source.getSourceId()));
      this.endTime = endTime;
      this.startTime = System.currentTimeMillis();
      this.element = Element.Physical;
   }

   public AffectedArea(Rectangle mistPosition, MapleMonster mob, MobSkillInfo skill, Point position, long endTime) {
      this(mistPosition, mob, skill, position, 1, endTime);
   }

   public AffectedArea(Rectangle mistPosition, MapleMonster mob, MobSkillInfo skill, Point position, int rlType, long endTime) {
      this.mistPosition = mistPosition;
      this.position = position;
      this.ownerId = mob.getObjectId();
      this.skill = skill;
      this.skilllevel = skill.getSkillLevel();
      this.setRlType(rlType);
      this.endTime = endTime;
      this.startTime = System.currentTimeMillis();
      this.isMobMist = true;
      this.element = Element.Physical;
      this.skillDelay = 0;
   }

   public AffectedArea(Rectangle mistPosition, MapleCharacter owner, SecondaryStatEffect source, Point position, long endTime) {
      this(mistPosition, owner, source, position, 1, endTime);
   }

   public AffectedArea(Rectangle mistPosition, MapleCharacter owner, SecondaryStatEffect source, Point position, long endTime, int tdBreakTime) {
      this(mistPosition, owner, source, position, 1, endTime);
      this.tdBreakTime = tdBreakTime;
   }

   public AffectedArea(Rectangle mistPosition, MapleCharacter owner, SecondaryStatEffect source, Point position, int rlType, long endTime) {
      this.mistPosition = mistPosition;
      this.position = position;
      this.owner = owner;
      this.ownerId = owner.getId();
      this.source = source;
      this.isMobMist = false;
      this.skilllevel = owner.getTotalSkillLevel(GameConstants.getLinkedAranSkill(source.getSourceId()));
      this.setRlType(rlType);
      this.endTime = endTime;
      this.startTime = System.currentTimeMillis();
      switch (source.getSourceId()) {
         case 1076:
         case 11076:
         case 2111003:
         case 2111013:
         case 2111014:
         case 14111006:
            this.element = Element.Poison;
            break;
         case 2100010:
            this.element = Element.Fire;
            break;
         case 4121015:
            this.element = Element.Dark;
            break;
         default:
            this.element = Element.Physical;
      }
   }

   public AffectedArea(Rectangle mistPosition, MapleCharacter owner, long endTime) {
      this.mistPosition = mistPosition;
      this.ownerId = owner.getId();
      this.source = new SecondaryStatEffect();
      this.source.setSourceId(2111003);
      this.skilllevel = 30;
      this.isMobMist = false;
      this.element = Element.Physical;
      this.endTime = endTime;
      this.startTime = System.currentTimeMillis();
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.MIST;
   }

   @Override
   public Point getPosition() {
      return this.mistPosition == null ? null : this.mistPosition.getLocation();
   }

   public int getSourceSkillID() {
      Skill skill = this.getSourceSkill();
      return skill == null ? 0 : skill.getId();
   }

   public Skill getSourceSkill() {
      return this.source == null ? null : SkillFactory.getSkill(this.source.getSourceId());
   }

   public void setSchedule(ScheduledFuture<?> s) {
      this.schedule = s;
   }

   public ScheduledFuture<?> getSchedule() {
      return this.schedule;
   }

   public void setPoisonSchedule(ScheduledFuture<?> s) {
      this.poisonSchedule = s;
   }

   public ScheduledFuture<?> getPoisonSchedule() {
      return this.poisonSchedule;
   }

   public boolean isMobMist() {
      return this.isMobMist;
   }

   public Element getElement() {
      return this.element;
   }

   public int getSkillDelay() {
      return this.skillDelay;
   }

   public void setSkillDelay(int delay) {
      this.skillDelay = delay;
   }

   public int getSkillLevel() {
      return this.skilllevel;
   }

   public int getOwnerId() {
      return this.ownerId;
   }

   public MobSkillInfo getMobSkill() {
      return this.skill;
   }

   public Rectangle getBox() {
      return this.mistPosition;
   }

   public int getRLType() {
      return this.getRlType();
   }

   public SecondaryStatEffect getSource() {
      return this.source;
   }

   @Override
   public void setPosition(Point position) {
   }

   public byte[] fakeSpawnData(int level) {
      return CField.createAffectedArea(this);
   }

   @Override
   public void sendSpawnData(MapleClient c) {
      c.getSession().writeAndFlush(CField.createAffectedArea(this));
   }

   @Override
   public void sendDestroyData(MapleClient c) {
      c.getSession().writeAndFlush(CField.removeAffectedArea(this.getObjectId(), this.getSourceSkillID(), false));
   }

   public boolean makeChanceResult() {
      return this.source.makeChanceResult();
   }

   public MapleCharacter getOwner() {
      return this.owner;
   }

   @Override
   public Point getTruePosition() {
      return this.position;
   }

   public int getRemainHealCount() {
      return this.remainHealCount;
   }

   public void setRemainHealCount(int count) {
      this.remainHealCount = count;
   }

   public Rect getMistRect() {
      return this.mistRect;
   }

   public boolean isInMistRect(Point pos) {
      return this.mistRect.getLeft() <= pos.x && this.mistRect.getTop() <= pos.y && this.mistRect.getRight() >= pos.x && this.mistRect.getBottom() >= pos.y;
   }

   public void setMistRect(Rect mistRect) {
      this.mistRect = mistRect;
   }

   public Point getForcePos() {
      return this.forcePos;
   }

   public void setForcePos(Point forcePos) {
      this.forcePos = forcePos;
   }

   public static final Rect calculateRect(Point posFrom, boolean facingLeft, Point lt, Point rb) {
      return facingLeft
         ? new Rect(posFrom.x + lt.x, posFrom.y + lt.y, posFrom.x + rb.x, posFrom.y + rb.y)
         : new Rect(posFrom.x + -1 * rb.x, posFrom.y + lt.y, posFrom.x + -1 * lt.x, posFrom.y + rb.y);
   }

   public static final Point calculateForce(Point posFrom, boolean facingLeft, int force, int forceX) {
      return facingLeft ? new Point(posFrom.x + -1 * forceX, force) : new Point(posFrom.x + forceX, force);
   }

   public int getPreUOL() {
      return this.preUOL;
   }

   public void setPreUOL(int preUOL) {
      this.preUOL = preUOL;
   }

   public int getDwOption() {
      return this.dwOption;
   }

   public void setDwOption(int dwOption) {
      this.dwOption = dwOption;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public int getRlType() {
      return this.rlType;
   }

   public void setRlType(int rlType) {
      this.rlType = rlType;
   }

   public long getEndTime() {
      return this.endTime;
   }

   public void setEndTime(long endTime) {
      this.endTime = endTime;
   }

   public int getTdBreakTime() {
      return this.tdBreakTime;
   }

   public void updatePerSecond(Field map, long now) {
      if (this.getEndTime() <= now) {
         if (map instanceof Field_FerociousBattlefield && this.isMobMist() && this.getMobSkill().getSkillId() == 252) {
            Field_FerociousBattlefield f = (Field_FerociousBattlefield)map;
            f.changeMobZone(1);
         }

         map.broadcastMessage(CField.removeAffectedArea(this.getObjectId(), this.getSourceSkillID(), false));
         map.removeMapObject(this);
         if (this.getPoisonSchedule() != null) {
            this.getPoisonSchedule().cancel(true);
            this.setPoisonSchedule(null);
         }
      } else {
         if (this.isMobMist()) {
            if (this.getMobSkill().getSkillId() == MobSkillID.AREA_TIMEZONE.getVal()) {
               for (MapleMonster mob : map.getMobsInRect(
                  this.getTruePosition(), this.skill.getLt().x, this.skill.getLt().y, this.skill.getRb().x, this.skill.getRb().y
               )) {
                  mob.applyTimeZone(map, this.getMobSkill().getSkillLevel());
               }
            } else if (this.getMobSkill().getSkillId() == MobSkillID.AREA_ABNORMAL.getVal()) {
               for (MapleCharacter player : map.getPlayerInRect(
                  this.getTruePosition(), this.skill.getLt().x, this.skill.getLt().y, this.skill.getRb().x, this.skill.getRb().y
               )) {
                  player.tryApplyAbnormal(this.getMobSkill());
               }
            }
         } else {
            switch (this.getSourceSkillID()) {
               case 100001261:
                  if (this.lastTimeDistortionTime != 0L && System.currentTimeMillis() - this.lastTimeDistortionTime < this.getSource().getSubTime()) {
                     break;
                  }

                  this.lastTimeDistortionTime = System.currentTimeMillis();

                  for (MapleMonster mob : map.getMobsInRect(
                     this.getTruePosition(), this.getSource().getLt().x, this.getSource().getLt().y, this.getSource().getRb().x, this.getSource().getRb().y
                  )) {
                     if (!mob.getStats().isBoss()) {
                        mob.applyStatus(
                           this.getOwner(),
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, this.getSourceSkillID(), null, false),
                           false,
                           this.getSource().getSubTime(),
                           false,
                           this.getSource()
                        );
                     }

                     mob.cancelStatus(MobTemporaryStatFlag.POWER_UP);
                     mob.cancelStatus(MobTemporaryStatFlag.MAGIC_UP);
                     mob.cancelStatus(MobTemporaryStatFlag.P_GUARD_UP);
                     mob.cancelStatus(MobTemporaryStatFlag.M_GUARD_UP);
                     mob.cancelStatus(MobTemporaryStatFlag.HARD_SKIN);
                     mob.applyStatus(
                        this.getOwner(),
                        new MobTemporaryStatEffect(MobTemporaryStatFlag.ADD_DAM_SKILL_2, this.getSource().getX(), this.getSourceSkillID(), null, false),
                        false,
                        this.getSource().getSubTime(),
                        false,
                        this.getSource()
                     );
                  }

                  for (MapleCharacter playerxx : map.getPlayerInRect(
                     this.getTruePosition(), this.getSource().getLt().x, this.getSource().getLt().y, this.getSource().getRb().x, this.getSource().getRb().y
                  )) {
                     if (playerxx.getId() == this.getOwnerId()
                        || this.getOwner().getParty() != null
                           && playerxx.getParty() != null
                           && this.getOwner().getParty().getId() == playerxx.getParty().getId()) {
                        playerxx.dispelDebuffs();
                        playerxx.temporaryStatSet(SecondaryStatFlag.indieBooster, 100001261, this.getSource().getSubTime(), this.getSource().getIndieBooster());
                        PostSkillEffect e_ = new PostSkillEffect(playerxx.getId(), 100001261, this.getSkillLevel(), null);
                        playerxx.send(e_.encodeForLocal());
                        playerxx.getMap().broadcastMessage(playerxx, e_.encodeForRemote(), false);
                     }
                  }
                  break;
               case 131001307:
                  SecondaryStatEffect e = this.getSource();
                  if (e != null) {
                     for (MapleMapObject mox : map.getMapObjectsInRect(this.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                        MapleMonster mob = (MapleMonster)mox;
                        if (mob != null) {
                           Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                           mse.put(MobTemporaryStatFlag.INDIE_PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_PDR, -30, 131001307, null, false));
                           mse.put(MobTemporaryStatFlag.SPEED, new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -80, 131001307, null, false));
                           Integer value = Integer.parseInt("" + Math.abs(this.getPosition().x) + Math.abs(this.getPosition().y));
                           mse.put(
                              MobTemporaryStatFlag.PINKBEAN_FLOWER_POT,
                              new MobTemporaryStatEffect(MobTemporaryStatFlag.PINKBEAN_FLOWER_POT, value, 131001307, null, false)
                           );
                           mob.applyMonsterBuff(mse, 131001307, 15000L, null, Collections.EMPTY_LIST);
                        }
                     }
                  }
                  break;
               case 162111000:
                  for (MapleCharacter playerx : map.getPlayerInRect(
                     this.getTruePosition(), this.getSource().getLt().x, this.getSource().getLt().y, this.getSource().getRb().x, this.getSource().getRb().y
                  )) {
                     if (playerx.getId() == this.getOwnerId()
                        || this.getOwner().getParty() != null && playerx.getParty() != null && this.getOwner().getParty().getId() == playerx.getParty().getId()
                        )
                      {
                        boolean self = this.getOwner().getId() == playerx.getId();
                        SecondaryStatEffect level = this.getSource();
                        int duration = self ? level.getU2() : level.getX();
                        duration *= 1000;
                        Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                        statups.put(SecondaryStatFlag.indieJump, level.getIndieJump());
                        statups.put(SecondaryStatFlag.indieSpeed, level.getIndieSpeed());
                        statups.put(SecondaryStatFlag.indieBooster, -1);
                        playerx.temporaryStatSet(162111001, level.getLevel(), duration, statups, true);
                     }
                  }
                  break;
               case 162111003:
                  for (MapleCharacter player : map.getPlayerInRect(
                     this.getTruePosition(), this.getSource().getLt().x, this.getSource().getLt().y, this.getSource().getRb().x, this.getSource().getRb().y
                  )) {
                     if (player.getId() == this.getOwnerId()
                        || this.getOwner().getParty() != null && player.getParty() != null && this.getOwner().getParty().getId() == player.getParty().getId()) {
                        boolean self = this.getOwner().getId() == player.getId();
                        SecondaryStatEffect level = this.getSource();
                        int duration = self ? level.getW() : level.getX();
                        duration *= 1000;
                        Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                        statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(level.getIndieDamR()));
                        player.temporaryStatSet(162111004, level.getLevel(), duration, statups, true);
                        int z = level.getZ();
                        if (player.getLastSunlightFilledGroundHealTime() == 0L
                           || System.currentTimeMillis() - player.getLastSunlightFilledGroundHealTime() >= z * 1000) {
                           int hp = level.getHp();
                           long heal = (int)(player.getStat().getCurrentMaxHp(player) * 0.01 * hp);
                           player.addHP(heal, false);
                           SpecialSkillEffect e2 = new SpecialSkillEffect(player.getId(), 162111003, null);
                           player.send(e2.encodeForLocal());
                           player.getMap().broadcastMessage(player, e2.encodeForRemote(), false);
                           player.setLastSunlightFilledGroundHealTime(System.currentTimeMillis());
                        }
                     }
                  }
                  break;
               case 162121043:
                  for (MapleMapObject mo : map.getMapObjectsInRect(this.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                     MapleMonster mob = (MapleMonster)mo;
                     if (mob != null) {
                        SecondaryStatEffect level = this.getSource();
                        Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                        mse.put(
                           MobTemporaryStatFlag.INDIE_PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_PDR, -level.getS(), 162121043, null, false)
                        );
                        mob.applyMonsterBuff(mse, 162121043, 15000L, null, Collections.EMPTY_LIST);
                     }
                  }
            }
         }

         switch (this.getElement()) {
            case Poison:
               MapleCharacter owner = map.getCharacterById(this.getOwnerId());
               SecondaryStatEffect e = this.getSource();
               if (e != null) {
                  for (MapleMapObject moxxx : map.getMapObjectsInRect(this.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                     if (this.makeChanceResult()) {
                        ((MapleMonster)moxxx)
                           .applyStatus(
                              owner,
                              new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, this.getSourceSkillID(), null, false),
                              true,
                              e.getDOTInterval() * 1000,
                              true,
                              this.getSource()
                           );
                     }
                  }
               }
               break;
            case Dark:
               owner = map.getCharacterById(this.getOwnerId());

               for (MapleMapObject moxx : map.getMapObjectsInRect(this.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                  if (this.makeChanceResult()) {
                     SecondaryStatEffect e3 = this.getSource();
                     if (e3 != null) {
                        boolean checkBoss = true;
                        if (owner != null && owner.getTotalSkillLevel(4120048) > 0) {
                           checkBoss = false;
                        }

                        ((MapleMonster)moxx)
                           .applyStatus(
                              owner,
                              new MobTemporaryStatEffect(MobTemporaryStatFlag.PAD, -this.getSource().getW(), this.getSourceSkillID(), null, false),
                              false,
                              10000L,
                              checkBoss,
                              this.getSource()
                           );
                        ((MapleMonster)moxx)
                           .applyStatus(
                              owner,
                              new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, -this.getSource().getW(), this.getSourceSkillID(), null, false),
                              false,
                              10000L,
                              checkBoss,
                              this.getSource()
                           );
                        ((MapleMonster)moxx)
                           .applyStatus(
                              owner,
                              new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, this.getSource().getY(), this.getSourceSkillID(), null, false),
                              false,
                              10000L,
                              checkBoss,
                              this.getSource()
                           );
                     }
                  }
               }
         }
      }
   }

   public Rect getBuffRect() {
      return this.buffRect;
   }

   public void setBuffRect(Rect buffRect) {
      this.buffRect = buffRect;
   }

   public int getSkillId() {
      return this.skillId;
   }

   public void setSkillId(int skillId) {
      this.skillId = skillId;
   }

   public JinHillahPoisonMist getJHillahMist() {
      return this.jHillahMist;
   }

   public void setJMist(JinHillahPoisonMist mist) {
      this.jHillahMist = mist;
   }

   public int getJMistPhase() {
      return this.jMistPhase;
   }

   public void setJMistPhase(int phase) {
      this.jMistPhase = phase;
   }
}
