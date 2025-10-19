package objects.users.skills;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import objects.fields.gameobject.lifes.Element;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Skill {
   private String name = "";
   private String psdDamR = "";
   private final List<SecondaryStatEffect> effects = new ArrayList<>();
   private List<SecondaryStatEffect> pvpEffects = null;
   private List<Integer> animation = null;
   private List<ExtraSkillInfo> extraSkillInfo = null;
   private List<Integer> skillList = null;
   private List<Integer> skillList2 = null;
   private List<Integer> skillList3 = null;
   private List<Integer> skillList4 = null;
   private List<Integer> skillListSub = null;
   private List<Integer> cancelableSkillID = null;
   private List<Integer> additionalProcess = null;
   private HashMap<Integer, Integer> skillSpecialValue = null;
   private final List<Pair<String, Integer>> requiredSkill = new ArrayList<>();
   private final List<Integer> psdSkill = new ArrayList<>();
   private Element element = Element.Physical;
   private int finalAttackId = 0;
   private final List<Integer> finalAttack = new ArrayList<>();
   private final List<Pair<Integer, Integer>> coolTimeReduceList = new ArrayList<>();
   private final List<RandomSkillInfo> randomSkillInfo = new ArrayList<>();
   private int randomSkillType = 0;
   private SecondAtomData secondAtomData = null;
   private int id;
   private int animationTime = 0;
   private int masterLevel = 0;
   private int maxLevel = 0;
   private int delay = 0;
   private int trueMax = 0;
   private int eventTamingMob = 0;
   private int skillType = 0;
   private int psd = 0;
   private int vehicleID = 0;
   private int processType = 0;
   private int notResetDarkSight = 0;
   private int screenTime = 0;
   private int setItemPartsCount = 0;
   private int setItemReason = 0;
   private int type = 0;
   private boolean notCooltimeReset = false;
   private boolean notIncBuffDuration = false;
   private ExceedInfo exceedInfo = null;
   private int exceedSkillParent = 0;
   private int reqLev = 0;
   private boolean areaAttack = false;
   private boolean invisible = false;
   private boolean disable = false;
   private boolean chargeskill = false;
   private boolean timeLimited = false;
   private boolean combatOrders = false;
   private boolean pvpDisabled = false;
   private boolean magic = false;
   private boolean casterMove = false;
   private boolean pushTarget = false;
   private boolean pullTarget = false;
   private boolean hyper = false;
   private boolean pairSkill = false;
   private boolean ignoreCounter = false;
   private int reqLevel = 0;
   private MapleData affected;
   private boolean isPetAutoBuff = false;
   private boolean isSummon = false;
   private boolean notCooltimeReduce = false;

   public Skill(int id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getReqLev() {
      return this.reqLev;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public final void afterLoad() {
      if (this.exceedInfo != null) {
         for (ExceedInfoEntry entry : this.exceedInfo.getList()) {
            if (entry.getSkillID() != 0) {
               Skill skill = SkillFactory.getSkill(entry.getSkillID());
               if (skill != null) {
                  skill.exceedSkillParent = this.id;
               }
            }
         }
      }
   }

   public static final Skill loadFromData(int id, MapleData data, MapleData delayData) {
      Skill ret = new Skill(id);
      boolean isBuff = false;
      int skillType = MapleDataTool.getInt("skillType", data, -1);
      String elem = MapleDataTool.getString("elemAttr", data, null);
      if (elem != null) {
         ret.element = Element.getFromChar(elem.charAt(0));
      }

      ret.skillType = skillType;
      ret.reqLev = MapleDataTool.getInt("reqLev", data, 0);
      ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
      ret.disable = MapleDataTool.getInt("disable", data, 0) > 0;
      ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
      ret.combatOrders = MapleDataTool.getInt("combatOrders", data, 0) > 0;
      ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
      ret.pairSkill = MapleDataTool.getInt("pairskill", data, 0) > 0;
      ret.hyper = data.getChildByPath("hyper") != null;
      ret.processType = MapleDataTool.getInt("processType", data, 0);
      ret.notResetDarkSight = MapleDataTool.getInt("notResetDarkSight", data, 0);
      ret.notCooltimeReset = MapleDataTool.getInt("notCooltimeReset", data, 0) == 1;
      ret.notIncBuffDuration = MapleDataTool.getInt("notIncBuffDuration", data, 0) == 1;
      ret.isPetAutoBuff = MapleDataTool.getInt("isPetAutoBuff", data, 0) == 1;
      ret.psd = MapleDataTool.getInt("psd", data, 0);
      ret.notCooltimeReduce = MapleDataTool.getInt("notCooltimeReduce", data, 0) == 1;
      if (ret.psd == 1) {
         MapleData psdskill = data.getChildByPath("psdSkill");
         if (psdskill != null) {
            for (MapleData psd : psdskill) {
               ret.psdSkill.add(Integer.parseInt(psd.getName()));
            }
         }
      }

      if (id == 22140000 || id == 22141002) {
         ret.masterLevel = 5;
      }

      ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
      ret.vehicleID = MapleDataTool.getInt("vehicleID", data, 0);
      ret.setSetItemPartsCount(MapleDataTool.getInt("setItemPartsCount", data, 0));
      ret.setSetItemReason(MapleDataTool.getInt("setItemReason", data, 0));
      MapleData inf = data.getChildByPath("info");
      if (inf != null) {
         ret.type = MapleDataTool.getInt("type", inf, 0);
         ret.setAreaAttack(MapleDataTool.getInt("areaAttack", inf, 0) == 1);
         ret.pvpDisabled = MapleDataTool.getInt("pvp", inf, 1) <= 0;
         ret.magic = MapleDataTool.getInt("magicDamage", inf, 0) > 0;
         ret.casterMove = MapleDataTool.getInt("casterMove", inf, 0) > 0;
         ret.pushTarget = MapleDataTool.getInt("pushTarget", inf, 0) > 0;
         ret.pullTarget = MapleDataTool.getInt("pullTarget", inf, 0) > 0;
      }

      MapleData req = data.getChildByPath("req");
      if (req != null) {
         ret.reqLevel = MapleDataTool.getInt("level", req, 0);
      }

      ret.additionalProcess = new ArrayList<>();
      ret.isSummon = data.getChildByPath("summon") != null;
      MapleData additionalProcess = data.getChildByPath("additional_process");
      if (additionalProcess != null) {
         for (MapleData d : additionalProcess.getChildren()) {
            ret.additionalProcess.add(MapleDataTool.getInt(d, 0));
         }
      }

      MapleData dat = data.getChildByPath("SecondAtom");
      if (dat != null) {
         ret.secondAtomData = new SecondAtomData(dat);
      }

      ret.extraSkillInfo = new ArrayList<>();
      MapleData extraSkill = data.getChildByPath("extraSkillInfo");
      if (extraSkill != null) {
         int i = 0;

         while (true) {
            MapleData d = extraSkill.getChildByPath(String.valueOf(i++));
            if (d == null) {
               break;
            }

            int skill = MapleDataTool.getInt("skill", d, 0);
            int delay = MapleDataTool.getInt("delay", d, 0);
            ExtraSkillInfo info = new ExtraSkillInfo(skill, delay);
            ret.getExtraSkillInfo().add(info);
         }
      }

      ret.skillList = new ArrayList<>();
      MapleData skillList = data.getChildByPath("skillList");
      if (skillList != null) {
         int i = 0;

         while (true) {
            MapleData d = skillList.getChildByPath(String.valueOf(i++));
            if (d == null) {
               break;
            }

            ret.skillList.add(MapleDataTool.getInt(d, 0));
         }
      }

      ret.skillList2 = new ArrayList<>();
      MapleData skillList2 = data.getChildByPath("skillList2");
      if (skillList2 != null) {
         int i = 0;

         while (true) {
            MapleData d = skillList2.getChildByPath(String.valueOf(i++));
            if (d == null) {
               break;
            }

            ret.skillList2.add(MapleDataTool.getInt(d, 0));
         }
      }

      ret.skillList3 = new ArrayList<>();
      MapleData skillList3 = data.getChildByPath("skillList3");
      if (skillList3 != null) {
         int i = 0;

         while (true) {
            MapleData d = skillList3.getChildByPath(String.valueOf(i++));
            if (d == null) {
               break;
            }

            ret.skillList3.add(MapleDataTool.getInt(d, 0));
         }
      }

      ret.skillList4 = new ArrayList<>();
      MapleData skillList4 = data.getChildByPath("skillList4");
      if (skillList4 != null) {
         int i = 0;

         while (true) {
            MapleData d = skillList4.getChildByPath(String.valueOf(i++));
            if (d == null) {
               break;
            }

            ret.skillList4.add(MapleDataTool.getInt(d, 0));
         }
      }

      ret.skillSpecialValue = new HashMap<>();
      MapleData skillSpecialValue = data.getChildByPath("skillSpecialValue");
      if (skillSpecialValue != null) {
         for (MapleData s : skillSpecialValue.getChildren()) {
            ret.skillSpecialValue.put(Integer.parseInt(s.getName()), MapleDataTool.getInt(s, 0));
         }
      }

      ret.skillListSub = new ArrayList<>();
      MapleData skillListSub = data.getChildByPath("skillListSub");
      if (skillListSub != null) {
         for (MapleData i : skillListSub.getChildren()) {
            ret.skillListSub.add(Integer.parseInt(i.getName()));
         }
      }

      ret.cancelableSkillID = new ArrayList<>();
      MapleData cs = data.getChildByPath("cancelableSkillID");
      if (cs != null) {
         for (MapleData i : cs.getChildren()) {
            ret.getCancelableSkillID().add(MapleDataTool.getInt(i, 0));
         }
      }

      MapleData randomSkill = data.getChildByPath("randomSkill");
      if (randomSkill != null) {
         int i = 0;

         while (true) {
            RandomSkillInfo randomSkillInfo = new RandomSkillInfo();
            MapleData d = randomSkill.getChildByPath(String.valueOf(i));
            if (d == null) {
               ret.randomSkillType = MapleDataTool.getInt("type", randomSkill, 0);
               break;
            }

            randomSkillInfo.setProb(MapleDataTool.getInt("prob", d, 0));
            MapleData skillData = d.getChildByPath("skillID");
            if (skillData == null) {
               MapleData sList = d.getChildByPath("skillList");
               if (sList != null) {
                  for (MapleData n : sList) {
                     randomSkillInfo.addSkillList(new Pair<>(Integer.parseInt(n.getName()), 0));
                  }
               }
            } else {
               randomSkillInfo.addSkillList(new Pair<>(MapleDataTool.getInt(skillData), 0));
            }

            ret.randomSkillInfo.add(randomSkillInfo);
            i++;
         }
      }

      MapleData info = data.getChildByPath("finalAttack");
      if (info != null) {
         for (MapleData finalAttack : info.getChildren()) {
            int finalAttackId = Integer.parseInt(finalAttack.getName());
            if (finalAttackId > 0) {
               ret.setFinalAttackId(finalAttackId);

               for (MapleData weaponType : finalAttack.getChildren()) {
                  ret.finalAttack.add(MapleDataTool.getInt(weaponType, 0));
               }
               break;
            }
         }
      }

      MapleData cooltimeReduceList = data.getChildByPath("cooltimeReduceList");
      if (cooltimeReduceList != null) {
         int i = 0;

         while (true) {
            MapleData cooltime_data = cooltimeReduceList.getChildByPath(String.valueOf(i++));
            if (cooltime_data == null) {
               break;
            }

            int portion = MapleDataTool.getInt("portion", cooltime_data, 0);
            int reduceRate = MapleDataTool.getInt("reduceRate", cooltime_data, 0);
            ret.coolTimeReduceList.add(new Pair<>(portion, reduceRate));
         }
      }

      MapleData effect = data.getChildByPath("effect");
      if (ret.skillType != -1) {
         if (ret.skillType == 2) {
            isBuff = true;
         }
      } else {
         MapleData action_ = data.getChildByPath("action");
         MapleData hit = data.getChildByPath("hit");
         MapleData ball = data.getChildByPath("ball");
         isBuff = effect != null && hit == null && ball == null;
         isBuff |= action_ != null && MapleDataTool.getString("0", action_, "").equals("alert2");
         if (StringUtil.getLeftPaddedStr(String.valueOf(id / 10000), '0', 3).equals("8000")) {
            isBuff = true;
         }

         if (MapleDataTool.getInt("attackCount", data, 0) > 0 || MapleDataTool.getInt("damage", data, 0) > 0) {
            isBuff = false;
         } else if (id != 22161003 && id != 32121006) {
            isBuff = true;
         }
      }

      ret.chargeskill = data.getChildByPath("keydown") != null;
      MapleData level = data.getChildByPath("common");
      if (level != null && data.getChildByPath("level") == null) {
         ret.maxLevel = MapleDataTool.getInt("maxLevel", level, 1);
         ret.psdDamR = MapleDataTool.getString("damR", level, "");
         ret.trueMax = ret.maxLevel + (ret.combatOrders ? 2 : 0);
         int maxLevel = ret.trueMax;
         if (id >= 400000000) {
            maxLevel += 10;
         }

         for (int i = 1; i <= maxLevel; i++) {
            ret.effects.add(SecondaryStatEffect.loadSkillEffectFromData(level, id, isBuff, i, "x"));
         }
      } else {
         for (MapleData leve : data.getChildByPath("level")) {
            ret.effects.add(SecondaryStatEffect.loadSkillEffectFromData(leve, id, isBuff, Byte.parseByte(leve.getName()), null));
         }

         ret.maxLevel = ret.effects.size();
         ret.trueMax = ret.effects.size();
      }

      MapleData info2 = data.getChildByPath("info2");
      if (info2 != null && MapleDataTool.getInt("ignoreCounter", info2, 0) > 0) {
         ret.ignoreCounter = true;
      }

      MapleData exceedInfo = data.getChildByPath("exceedInfo");
      if (exceedInfo != null) {
         ret.exceedInfo = new ExceedInfo(exceedInfo);
      }

      ret.affected = data.getChildByPath("affected");
      MapleData reqDataRoot = data.getChildByPath("req");
      if (reqDataRoot != null) {
         for (MapleData reqData : reqDataRoot.getChildren()) {
            ret.requiredSkill.add(new Pair<>(reqData.getName(), MapleDataTool.getInt(reqData, 1)));
         }
      }

      ret.animationTime = 0;
      if (effect != null) {
         for (MapleData effectEntry : effect) {
            ret.animationTime = ret.animationTime + MapleDataTool.getIntConvert("delay", effectEntry, 0);
         }
      }

      MapleData screen = data.getChildByPath("screen");
      ret.screenTime = 0;
      if (screen != null) {
         for (MapleData screenData : screen.getChildren()) {
            ret.screenTime = ret.screenTime + MapleDataTool.getInt("delay", screenData, 0);
         }
      }

      return ret;
   }

   public SecondaryStatEffect getEffect(int level) {
      if (level <= 0) {
         return null;
      } else if (this.effects.size() < level) {
         return this.effects.size() > 0 ? this.effects.get(this.effects.size() - 1) : null;
      } else {
         return this.effects.get(level - 1);
      }
   }

   public SecondaryStatEffect getPVPEffect(int level) {
      if (this.pvpEffects == null) {
         return this.getEffect(level);
      } else if (this.pvpEffects.size() < level) {
         return this.pvpEffects.size() > 0 ? this.pvpEffects.get(this.pvpEffects.size() - 1) : null;
      } else {
         return level <= 0 ? this.pvpEffects.get(0) : this.pvpEffects.get(level - 1);
      }
   }

   public int getSkillType() {
      return this.skillType;
   }

   public List<Integer> getAllAnimation() {
      return this.animation;
   }

   public int getAnimation() {
      return this.animation == null ? -1 : this.animation.get(Randomizer.nextInt(this.animation.size()));
   }

   public boolean isPVPDisabled() {
      return this.pvpDisabled;
   }

   public boolean isChargeSkill() {
      return this.chargeskill;
   }

   public boolean isInvisible() {
      return this.invisible;
   }

   public boolean isDisable() {
      return this.disable;
   }

   public boolean hasRequiredSkill() {
      return this.requiredSkill.size() > 0;
   }

   public List<Pair<String, Integer>> getRequiredSkills() {
      return this.requiredSkill;
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public int getTrueMax() {
      return this.trueMax;
   }

   public boolean combatOrders() {
      return this.combatOrders;
   }

   public boolean canBeLearnedBy(int job) {
      int skillForJob = this.id / 10000;
      if (skillForJob == 2001) {
         return GameConstants.isEvan(job);
      } else if (skillForJob == 0) {
         return GameConstants.isAdventurer(job);
      } else if (skillForJob == 1000) {
         return GameConstants.isKOC(job);
      } else if (skillForJob == 2000) {
         return GameConstants.isAran(job);
      } else if (skillForJob == 3000) {
         return GameConstants.isResist(job);
      } else if (skillForJob == 1) {
         return GameConstants.isCannon(job);
      } else if (skillForJob == 3001) {
         return GameConstants.isDemonSlayer(job);
      } else if (skillForJob == 2002) {
         return GameConstants.isMercedes(job);
      } else if (job / 100 != skillForJob / 100) {
         return false;
      } else if (job / 1000 != skillForJob / 1000) {
         return false;
      } else if (GameConstants.isCannon(skillForJob) && !GameConstants.isCannon(job)) {
         return false;
      } else if (GameConstants.isDemonSlayer(skillForJob) && !GameConstants.isDemonSlayer(job)) {
         return false;
      } else if (GameConstants.isDemonAvenger(skillForJob) && !GameConstants.isDemonAvenger(job)) {
         return false;
      } else if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
         return false;
      } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
         return false;
      } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
         return false;
      } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
         return false;
      } else if (GameConstants.isMercedes(skillForJob) && !GameConstants.isMercedes(job)) {
         return false;
      } else if (GameConstants.isResist(skillForJob) && !GameConstants.isResist(job)) {
         return false;
      } else {
         return job / 10 % 10 == 0 && skillForJob / 10 % 10 > job / 10 % 10 ? false : skillForJob / 10 % 10 == 0 || skillForJob / 10 % 10 == job / 10 % 10;
      }
   }

   public boolean isTimeLimited() {
      return this.timeLimited;
   }

   public boolean sub_4FD900(int a1) {
      if (a1 <= 5320007) {
         if (a1 == 5320007) {
            return true;
         }

         if (a1 > 4210012) {
            if (a1 > 5220012) {
               if (a1 == 5220014) {
                  return true;
               }

               boolean v1 = a1 == 5221022;
            } else {
               if (a1 == 5220012) {
                  return true;
               }

               if (a1 > 4340012) {
                  if (a1 >= 5120011 && a1 <= 5120012) {
                     return true;
                  }

                  return false;
               }

               if (a1 == 4340012) {
                  return true;
               }

               boolean v1 = a1 == 4340010;
            }
         } else {
            if (a1 == 4210012) {
               return true;
            }

            if (a1 > 2221009) {
               if (a1 == 2321010 || a1 == 3210015) {
                  return true;
               }

               boolean v1 = a1 == 4110012;
            } else {
               if (a1 == 2221009 || a1 == 1120012 || a1 == 1320011) {
                  return true;
               }

               boolean var5 = a1 == 2121009;
            }
         }
      }

      if (a1 <= 23120011) {
         if (a1 == 23120011) {
            return true;
         } else if (a1 <= 21120014) {
            return a1 != 21120014 && a1 != 5321004 && a1 - 5321003 + 1 != 2 ? a1 - 5321003 + 1 - 2 == 15799005 : true;
         } else if (a1 > 21121008) {
            return a1 == 22171069;
         } else if (a1 == 21121008) {
            return true;
         } else {
            return a1 < 21120020 ? false : a1 <= 21120021;
         }
      } else {
         boolean v1;
         if (a1 > 35120014) {
            if (a1 == 51120000) {
               return true;
            }

            v1 = a1 == 80001913;
         } else {
            if (a1 == 35120014 || a1 == 23120013 || a1 == 23121008) {
               return true;
            }

            v1 = a1 == 33120010;
         }

         return v1;
      }
   }

   public boolean sub_4FDA20(int a1) {
      boolean result = false;
      if (a1 - 92000000 >= 1000000 || a1 % 10000 != 0) {
         int v1 = 10000 * (a1 / 10000);
         if (v1 - 92000000 < 1000000 && v1 % 10000 == 0) {
            result = true;
         }
      }

      return result;
   }

   public boolean sub_4FD870(int a1) {
      int v1 = a1 / 10000;
      if (a1 / 10000 == 8000) {
         v1 = a1 / 100;
      }

      return v1 - 800000 <= 99;
   }

   public boolean sub_48AEF0(int a1) {
      int v1 = a1 / 10000;
      if (a1 / 10000 == 8000) {
         v1 = a1 / 100;
      }

      boolean result;
      if (v1 - 40000 > 5) {
         result = this.sub_48A360(v1);
      } else {
         result = false;
      }

      return result;
   }

   public boolean sub_48A360(int a1) {
      boolean v2;
      if (a1 > 6001) {
         if (a1 == 13000) {
            return true;
         }

         v2 = a1 == 14000;
      } else {
         if (a1 >= 6000) {
            return true;
         }

         if (a1 <= 3002) {
            if (a1 >= 3001 || a1 >= 2001 && a1 <= 2005) {
               return true;
            }

            if (a1 - 40000 <= 5) {
               return false;
            }

            if (a1 % 1000 == 0) {
               return true;
            }
         }

         v2 = a1 == 5000;
      }

      return v2 ? true : a1 - 800000 < 100;
   }

   public boolean sub_4FD8B0(int a1) {
      boolean result;
      if (a1 >= 0) {
         int v1 = a1 / 10000;
         if (a1 / 10000 == 8000) {
            v1 = a1 / 100;
         }

         result = v1 == 9500;
      } else {
         result = false;
      }

      return result;
   }

   public int sub_48A160(int a1) {
      int result = a1 / 10000;
      if (a1 / 10000 == 8000) {
         result = a1 / 100;
      }

      return result;
   }

   public int sub_489A10(int a1) {
      int result;
      if (!this.sub_48A360(a1) && a1 % 100 != 0 && a1 != 501 && a1 != 3101) {
         if (a1 - 2200 >= 100 && a1 != 2001) {
            if (a1 / 10 == 43) {
               result = 0;
               if ((a1 - 430) / 2 <= 2) {
                  result = (a1 - 430) / 2 + 2;
               }
            } else {
               result = 0;
               if (a1 % 10 <= 2) {
                  result = a1 % 10 + 2;
               }
            }
         } else {
            switch (a1) {
               case 2200:
               case 2210:
                  result = 1;
                  break;
               case 2201:
               case 2202:
               case 2203:
               case 2204:
               case 2205:
               case 2206:
               case 2207:
               case 2208:
               case 2209:
               default:
                  result = 0;
                  break;
               case 2211:
               case 2212:
               case 2213:
                  result = 2;
                  break;
               case 2214:
               case 2215:
               case 2216:
                  result = 3;
                  break;
               case 2217:
               case 2218:
                  result = 4;
            }
         }
      } else {
         result = 1;
      }

      return result;
   }

   public boolean sub_4FD7F0(int a1) {
      boolean v1;
      if (a1 > 101100101) {
         if (a1 > 101110203) {
            if (a1 == 101120104) {
               return true;
            }

            v1 = a1 - 101120104 == 100;
         } else {
            if (a1 == 101110203 || a1 == 101100201 || a1 == 101110102) {
               return true;
            }

            v1 = a1 - 101110102 == 98;
         }
      } else {
         if (a1 == 101100101) {
            return true;
         }

         if (a1 > 4331002) {
            if (a1 == 4340007 || a1 == 4341004) {
               return true;
            }

            v1 = a1 == 101000101;
         } else {
            if (a1 == 4331002 || a1 == 4311003 || a1 == 4321006) {
               return true;
            }

            v1 = a1 == 4330009;
         }
      }

      return v1;
   }

   public boolean isFourthJob() {
      int a1 = this.id;
      boolean result;
      if (!this.sub_4FD900(a1)
         && (a1 - 92000000 >= 1000000 || a1 % 10000 != 0)
         && !this.sub_4FDA20(a1)
         && !this.sub_4FD870(a1)
         && !this.sub_48AEF0(a1)
         && !this.sub_4FD8B0(a1)) {
         int v2 = this.sub_48A160(a1);
         int v3 = this.sub_489A10(v2);
         result = v2 - 40000 > 5 && (this.sub_4FD7F0(a1) || v3 == 4 && !GameConstants.isZero(v2));
      } else {
         result = false;
      }

      return result;
   }

   public Element getElement() {
      return this.element;
   }

   public int getAnimationTime() {
      return this.animationTime;
   }

   public int getMasterLevel() {
      return this.masterLevel;
   }

   public int getDelay() {
      return this.delay;
   }

   public int getTamingMob() {
      return this.eventTamingMob;
   }

   public boolean isBeginnerSkill() {
      int jobId = this.id / 10000;
      return GameConstants.isNovice(jobId);
   }

   public boolean isMagic() {
      return this.magic;
   }

   public boolean isMovement() {
      return this.casterMove;
   }

   public boolean isPush() {
      return this.pushTarget;
   }

   public boolean isPull() {
      return this.pullTarget;
   }

   public List<Integer> getPsdSkill() {
      return this.psdSkill;
   }

   public int getPsd() {
      return this.psd;
   }

   public String getPsdDamR() {
      return this.psdDamR;
   }

   public boolean isHyper() {
      return this.hyper;
   }

   public boolean isSpecialSkill() {
      int jobId = this.id / 10000;
      return jobId == 900 || jobId == 800 || jobId == 9000 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
   }

   public boolean isPairSkill() {
      return this.pairSkill;
   }

   public int getFinalAttackId() {
      return this.finalAttackId;
   }

   public void setFinalAttackId(int finalAttackId) {
      this.finalAttackId = finalAttackId;
   }

   public List<Integer> getFinalAttack() {
      return this.finalAttack;
   }

   public int getType() {
      return this.type;
   }

   public int getVehicleID() {
      return this.vehicleID;
   }

   public int getProcessType() {
      return this.processType;
   }

   public boolean findProcessType(int type) {
      return this.processType == type ? true : this.additionalProcess.contains(type);
   }

   public List<ExtraSkillInfo> getExtraSkillInfo() {
      return this.extraSkillInfo;
   }

   public void setExtraSkillInfo(List<ExtraSkillInfo> extraSkillInfo) {
      this.extraSkillInfo = extraSkillInfo;
   }

   public List<Integer> getSkillList() {
      return this.skillList;
   }

   public List<Integer> getSkillList2() {
      return this.skillList2;
   }

   public List<Integer> getSkillList3() {
      return this.skillList3;
   }

   public List<Integer> getSkillList4() {
      return this.skillList4;
   }

   public List<Integer> getSkillListSub() {
      return this.skillListSub;
   }

   public HashMap<Integer, Integer> getSkillSpecialValue() {
      return this.skillSpecialValue;
   }

   public boolean isNotCooltimeReset() {
      return this.notCooltimeReset;
   }

   public List<RandomSkillInfo> getRandomSkillInfo() {
      return this.randomSkillInfo;
   }

   public int getRandomSkillType() {
      return this.randomSkillType;
   }

   public SecondAtomData getSecondAtomData() {
      return this.secondAtomData;
   }

   public List<Integer> getCancelableSkillID() {
      return this.cancelableSkillID;
   }

   public boolean isNotIncBuffDuration() {
      return this.notIncBuffDuration;
   }

   public void setNotIncBuffDuration(boolean notIncBuffDuration) {
      this.notIncBuffDuration = notIncBuffDuration;
   }

   public ExceedInfo getExceedInfo() {
      return this.exceedInfo;
   }

   public int getExceedSkillParent() {
      return this.exceedSkillParent;
   }

   public void setExceedSkillParent(int exceedSkillParent) {
      this.exceedSkillParent = exceedSkillParent;
   }

   public boolean isAreaAttack() {
      return this.areaAttack;
   }

   public void setAreaAttack(boolean areaAttack) {
      this.areaAttack = areaAttack;
   }

   public boolean isIgnoreCounter() {
      return this.ignoreCounter;
   }

   public int getSetItemPartsCount() {
      return this.setItemPartsCount;
   }

   public void setSetItemPartsCount(int setItemPartsCount) {
      this.setItemPartsCount = setItemPartsCount;
   }

   public int getSetItemReason() {
      return this.setItemReason;
   }

   public void setSetItemReason(int setItemReason) {
      this.setItemReason = setItemReason;
   }

   public int getReqLevel() {
      return this.reqLevel;
   }

   public void setReqLevel(int reqLevel) {
      this.reqLevel = reqLevel;
   }

   public List<Pair<Integer, Integer>> getCoolTimeReduceList() {
      return this.coolTimeReduceList;
   }

   public MapleData getAffected() {
      return this.affected;
   }

   public boolean isPetAutoBuff() {
      return this.isPetAutoBuff;
   }

   public void setPetAutoBuff(boolean petAutoBuff) {
      this.isPetAutoBuff = petAutoBuff;
   }

   public boolean isSummon() {
      return this.isSummon;
   }

   public boolean isNotResetDarkSight() {
      return this.notResetDarkSight == 1;
   }

   public int getScreenTime() {
      return this.screenTime;
   }

   public boolean isNotCooltimeReduce() {
      return this.notCooltimeReduce;
   }
}
