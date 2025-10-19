package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.List;
import objects.users.MapleCharacter;
import objects.users.achievement.AchievementSubMissionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class CheckValue {
   private AchievementSubMissionType subMissionType;
   private boolean isMaxDamageUser;
   private boolean isLastAttack;
   private Mob mob;
   private Character character;
   private Party party;
   private UnionAttacker unionAttacker;
   private UnionInfo unionInfo;
   private Fieldset fieldset;
   private SpelltraceEnchant spelltraceEnchant;
   private EnchantResult enchantResult;
   private StarforceEnchant starforceEnchant;
   private Meso meso;
   private SkillUse skillUse;
   private QuestChangeInfo questChangeInfo;
   private AchievementChangeInfo achievementChangeInfo;
   private Ability ability;
   private ItemCheck item;
   private SuddenMissionClearTime suddenMissionClearTime;
   private RuneStoneID runeStoneID;
   private ObjectTemplateID objectTemplateID;
   private IncHP incHP;
   private PetCheck petCheck;
   private NpcID npcID;
   private SkillID skillID;
   private ItemReward itemReward;
   private DateInfo date;
   private Field field;
   private NCStatType ncStatType;
   private MakingskillResult makingskillResult;
   private SkillCheck skillCheck;
   private LotteryItemID lotteryItemID;
   private LotteryResultItemID lotteryResultItemID;
   private LotteryResultItemLevel lotteryResultItemLevel;
   private ComboKillCount comboKillCount;
   private MultiKillCount multiKillCount;
   private HitDamagePercent hitDamagePercent;

   public CheckValue(MapleData root, String type) {
      this.setSubMissionType(AchievementSubMissionType.getType(type));
      this.setMaxDamageUser(MapleDataTool.getInt("is_max_damage_user", root, 0) == 1);
      this.setLastAttack(MapleDataTool.getInt("is_last_attack", root, 0) == 1);
      MapleData mob = root.getChildByPath("mob");
      if (mob != null) {
         this.setMob(new Mob(mob));
      }

      MapleData character = root.getChildByPath("character");
      if (character != null) {
         this.setCharacter(new Character(character));
      }

      MapleData party = root.getChildByPath("party");
      if (party != null) {
         this.setParty(new Party(party));
      }

      MapleData unionAttacker = root.getChildByPath("union_attacker");
      if (unionAttacker != null) {
         this.setUnionAttacker(new UnionAttacker(unionAttacker));
      }

      MapleData unionInfo = root.getChildByPath("union_info");
      if (unionInfo != null) {
         this.setUnionInfo(new UnionInfo(unionInfo));
      }

      MapleData spellTraceEnchantInfo = root.getChildByPath("spell_trace_enchant");
      if (spellTraceEnchantInfo != null) {
         this.setSpelltraceEnchant(new SpelltraceEnchant(spellTraceEnchantInfo));
      }

      MapleData enchantResultInfo = root.getChildByPath("enchant_result");
      if (enchantResultInfo != null) {
         this.setEnchantResult(new EnchantResult(enchantResultInfo));
      }

      MapleData starforceEnchantInfo = root.getChildByPath("starforce_enchant");
      if (starforceEnchantInfo != null) {
         this.setStarforceEnchant(new StarforceEnchant(starforceEnchantInfo));
      }

      MapleData mesoInfo = root.getChildByPath("meso");
      if (mesoInfo != null) {
         this.setMeso(new Meso(mesoInfo));
      }

      MapleData fieldset = root.getChildByPath("fieldset");
      if (fieldset != null) {
         this.setFieldset(new Fieldset(fieldset));
      }

      MapleData skillUse = root.getChildByPath("skill_use");
      if (skillUse != null) {
         this.setSkillUse(new SkillUse(skillUse));
      }

      MapleData questStateChange = root.getChildByPath("quest_change_info");
      if (questStateChange != null) {
         this.setQuestChangeInfo(new QuestChangeInfo(questStateChange));
      }

      MapleData achievementChangeInfo = root.getChildByPath("achievement_change_info");
      if (achievementChangeInfo != null) {
         this.setAchievementChangeInfo(new AchievementChangeInfo(achievementChangeInfo));
      }

      MapleData itemInfo = root.getChildByPath("item");
      if (itemInfo != null) {
         this.setItem(new ItemCheck(itemInfo));
      }

      MapleData ability = root.getChildByPath("ability");
      if (ability != null) {
         this.setAbility(new Ability(ability));
      }

      MapleData smct = root.getChildByPath("suddenmission_clear_time");
      if (smct != null) {
         this.setSuddenMissionClearTime(new SuddenMissionClearTime(smct));
      }

      MapleData rid = root.getChildByPath("rune_stone_id");
      if (rid != null) {
         this.setRuneStoneID(new RuneStoneID(rid));
      }

      MapleData oti = root.getChildByPath("object_template_id");
      if (oti != null) {
         this.setObjectTemplateID(new ObjectTemplateID(oti));
      }

      MapleData ihp = root.getChildByPath("inc_hp");
      if (ihp != null) {
         this.setIncHP(new IncHP(ihp));
      }

      MapleData pet = root.getChildByPath("pet");
      if (pet != null) {
         this.setPetCheck(new PetCheck(pet));
      }

      MapleData npcID = root.getChildByPath("npc_id");
      if (npcID != null) {
         this.setNpcID(new NpcID(npcID));
      }

      MapleData sid = root.getChildByPath("skill_id");
      if (sid != null) {
         this.setSkillID(new SkillID(sid));
      }

      MapleData iReward = root.getChildByPath("item_reward");
      if (iReward != null) {
         this.setItemReward(new ItemReward(iReward));
      }

      MapleData dateData = root.getChildByPath("date");
      if (dateData != null) {
         this.setDate(new DateInfo(dateData));
      }

      MapleData field = root.getChildByPath("field");
      if (field != null) {
         this.setField(new Field(field));
      }

      String ncStatType = MapleDataTool.getString("nc_stat_type", root, "");
      if (!ncStatType.isEmpty()) {
         this.setNcStatType(new NCStatType(ncStatType));
      }

      MapleData makingskillResult = root.getChildByPath("makingskill_result");
      if (makingskillResult != null) {
         this.setMakingskillResult(new MakingskillResult(makingskillResult));
      }

      MapleData skillCheck = root.getChildByPath("skill");
      if (skillCheck != null) {
         this.setSkillCheck(new SkillCheck(skillCheck));
      }

      MapleData lotteryCheck = root.getChildByPath("lottery_item_id");
      if (lotteryCheck != null) {
         this.setLotteryItemID(new LotteryItemID(lotteryCheck));
      }

      MapleData lotteryResultItemID = root.getChildByPath("lottery_result_item_id");
      if (lotteryResultItemID != null) {
         this.setLotteryResultItemID(new LotteryResultItemID(lotteryResultItemID));
      }

      MapleData lotteryResultItemLevel = root.getChildByPath("lottery_result_item_level");
      if (lotteryResultItemLevel != null) {
         this.setLotteryResultItemLevel(new LotteryResultItemLevel(lotteryResultItemLevel));
      }

      MapleData comboKillCount = root.getChildByPath("combokill_count");
      if (comboKillCount != null) {
         this.setComboKillCount(new ComboKillCount(comboKillCount));
      }

      MapleData multiKillCount = root.getChildByPath("multikill_count");
      if (multiKillCount != null) {
         this.setMultiKillCount(new MultiKillCount(multiKillCount));
      }

      MapleData hitDamagePercent = root.getChildByPath("hit_damage_percent");
      if (hitDamagePercent != null) {
         this.setHitDamagePercent(new HitDamagePercent(hitDamagePercent));
      }
   }

   public boolean check(
      MapleCharacter player,
      MapleCharacter lastAttacker,
      MapleCharacter highestDamagedAttacker,
      List<MapleCharacter> players,
      int skillID,
      int achievementID,
      int achievementState,
      int questID,
      int questState,
      int scroll,
      int sucRate,
      boolean success,
      int abilitySkillID,
      int abilitySkillLevel,
      int abilityGrade,
      String result,
      int starForce,
      String catchResult,
      int clearTimeSecond,
      int runeNumber
   ) {
      if (this.isLastAttack) {
         if (lastAttacker == null) {
            return false;
         }

         if (player.getId() != lastAttacker.getId()) {
            return false;
         }
      }

      if (this.isMaxDamageUser) {
         if (highestDamagedAttacker == null) {
            return false;
         }

         if (player.getId() != highestDamagedAttacker.getId()) {
            return false;
         }
      }

      boolean characterCheck = true;
      Character character = this.getCharacter();
      if (character != null) {
         characterCheck = character.check(player);
      }

      boolean partyCheck = true;
      Party party = this.getParty();
      if (party != null) {
         if (player.getParty() == null) {
            partyCheck = false;
         } else {
            partyCheck = party.check(players, player.getMap().getFieldSetInstance());
         }
      }

      boolean unionAttackerCheck = true;
      UnionAttacker unionAttacker = this.getUnionAttacker();
      if (unionAttacker != null) {
         unionAttackerCheck = unionAttacker.check(player);
      }

      boolean unionInfoCheck = true;
      UnionInfo unionInfo = this.getUnionInfo();
      if (unionInfo != null) {
         unionInfoCheck = unionInfo.check(player);
      }

      boolean fieldsetCheck = true;
      Fieldset fieldset = this.getFieldset();
      if (fieldset != null) {
         fieldsetCheck = fieldset.check(player.getMap().getFieldSetInstance());
      }

      boolean skillUseCheck = true;
      SkillUse skillUse = this.getSkillUse();
      if (skillUse != null) {
         skillUseCheck = skillUse.check(skillID);
      }

      boolean achievementChangeCheck = true;
      AchievementChangeInfo achievementChangeInfo = this.getAchievementChangeInfo();
      if (achievementChangeInfo != null) {
         achievementChangeCheck = achievementChangeInfo.check(achievementID, achievementState);
      }

      boolean questStateChangeCheck = true;
      QuestChangeInfo questChangeInfo = this.getQuestChangeInfo();
      if (questChangeInfo != null) {
         questStateChangeCheck = questChangeInfo.check(questID, questState);
      }

      boolean suddenMissionClearTime = true;
      SuddenMissionClearTime sdt = this.getSuddenMissionClearTime();
      if (sdt != null) {
         suddenMissionClearTime = sdt.check(clearTimeSecond);
      }

      boolean enchantCheck = true;
      SpelltraceEnchant s = this.getSpelltraceEnchant();
      if (s != null) {
         enchantCheck = s.check(scroll);
      }

      boolean resultCheck = true;
      EnchantResult r = this.getEnchantResult();
      if (r != null) {
         resultCheck = r.check(sucRate, success);
      }

      boolean starForceEnchantCheck = true;
      StarforceEnchant starforceEnchant = this.getStarforceEnchant();
      if (starforceEnchant != null) {
         starForceEnchantCheck = starforceEnchant.check(result, starForce, catchResult);
      }

      boolean abilityCheck = true;
      Ability ability = this.getAbility();
      if (ability != null) {
         abilityCheck = ability.check(abilityGrade, abilitySkillID, abilitySkillLevel);
      }

      boolean runeCheck = true;
      RuneStoneID rid = this.getRuneStoneID();
      if (rid != null) {
         runeCheck = rid.check(runeNumber);
      }

      boolean objectTemplateCheck = true;
      ObjectTemplateID oti = this.getObjectTemplateID();
      if (oti != null) {
         int objectTID = 0;
         objectTemplateCheck = oti.check(objectTID);
      }

      return characterCheck
         && partyCheck
         && unionAttackerCheck
         && unionInfoCheck
         && fieldsetCheck
         && skillUseCheck
         && achievementChangeCheck
         && questStateChangeCheck
         && enchantCheck
         && resultCheck
         && abilityCheck
         && starForceEnchantCheck
         && suddenMissionClearTime
         && runeCheck
         && objectTemplateCheck;
   }

   public Mob getMob() {
      return this.mob;
   }

   public void setMob(Mob mob) {
      this.mob = mob;
   }

   public boolean isMaxDamageUser() {
      return this.isMaxDamageUser;
   }

   public void setMaxDamageUser(boolean maxDamageUser) {
      this.isMaxDamageUser = maxDamageUser;
   }

   public boolean isLastAttack() {
      return this.isLastAttack;
   }

   public void setLastAttack(boolean lastAttack) {
      this.isLastAttack = lastAttack;
   }

   public Character getCharacter() {
      return this.character;
   }

   public void setCharacter(Character character) {
      this.character = character;
   }

   public Party getParty() {
      return this.party;
   }

   public void setParty(Party party) {
      this.party = party;
   }

   public UnionAttacker getUnionAttacker() {
      return this.unionAttacker;
   }

   public void setUnionAttacker(UnionAttacker unionAttacker) {
      this.unionAttacker = unionAttacker;
   }

   public UnionInfo getUnionInfo() {
      return this.unionInfo;
   }

   public void setUnionInfo(UnionInfo unionInfo) {
      this.unionInfo = unionInfo;
   }

   public SpelltraceEnchant getSpelltraceEnchant() {
      return this.spelltraceEnchant;
   }

   public EnchantResult getEnchantResult() {
      return this.enchantResult;
   }

   public StarforceEnchant getStarforceEnchant() {
      return this.starforceEnchant;
   }

   public Meso getMeso() {
      return this.meso;
   }

   public AchievementSubMissionType getSubMissionType() {
      return this.subMissionType;
   }

   public void setSubMissionType(AchievementSubMissionType subMissionType) {
      this.subMissionType = subMissionType;
   }

   public Fieldset getFieldset() {
      return this.fieldset;
   }

   public void setFieldset(Fieldset fieldset) {
      this.fieldset = fieldset;
   }

   public void setSpelltraceEnchant(SpelltraceEnchant spelltraceEnchant) {
      this.spelltraceEnchant = spelltraceEnchant;
   }

   public void setEnchantResult(EnchantResult enchantResult) {
      this.enchantResult = enchantResult;
   }

   public void setStarforceEnchant(StarforceEnchant starforceEnchant) {
      this.starforceEnchant = starforceEnchant;
   }

   public void setMeso(Meso meso) {
      this.meso = meso;
   }

   public SkillUse getSkillUse() {
      return this.skillUse;
   }

   public void setSkillUse(SkillUse skillUse) {
      this.skillUse = skillUse;
   }

   public QuestChangeInfo getQuestChangeInfo() {
      return this.questChangeInfo;
   }

   public void setQuestChangeInfo(QuestChangeInfo questChangeInfo) {
      this.questChangeInfo = questChangeInfo;
   }

   public AchievementChangeInfo getAchievementChangeInfo() {
      return this.achievementChangeInfo;
   }

   public void setAchievementChangeInfo(AchievementChangeInfo achievementChangeInfo) {
      this.achievementChangeInfo = achievementChangeInfo;
   }

   public ItemCheck getItem() {
      return this.item;
   }

   public void setItem(ItemCheck item) {
      this.item = item;
   }

   public Ability getAbility() {
      return this.ability;
   }

   public void setAbility(Ability ability) {
      this.ability = ability;
   }

   public SuddenMissionClearTime getSuddenMissionClearTime() {
      return this.suddenMissionClearTime;
   }

   public void setSuddenMissionClearTime(SuddenMissionClearTime suddenMissionClearTime) {
      this.suddenMissionClearTime = suddenMissionClearTime;
   }

   public RuneStoneID getRuneStoneID() {
      return this.runeStoneID;
   }

   public void setRuneStoneID(RuneStoneID runeStoneID) {
      this.runeStoneID = runeStoneID;
   }

   public ObjectTemplateID getObjectTemplateID() {
      return this.objectTemplateID;
   }

   public void setObjectTemplateID(ObjectTemplateID objectTemplateID) {
      this.objectTemplateID = objectTemplateID;
   }

   public IncHP getIncHP() {
      return this.incHP;
   }

   public void setIncHP(IncHP incHP) {
      this.incHP = incHP;
   }

   public PetCheck getPetCheck() {
      return this.petCheck;
   }

   public void setPetCheck(PetCheck petCheck) {
      this.petCheck = petCheck;
   }

   public NpcID getNpcID() {
      return this.npcID;
   }

   public void setNpcID(NpcID npcID) {
      this.npcID = npcID;
   }

   public SkillID getSkillID() {
      return this.skillID;
   }

   public void setSkillID(SkillID skillID) {
      this.skillID = skillID;
   }

   public ItemReward getItemReward() {
      return this.itemReward;
   }

   public void setItemReward(ItemReward itemReward) {
      this.itemReward = itemReward;
   }

   public DateInfo getDate() {
      return this.date;
   }

   public void setDate(DateInfo date) {
      this.date = date;
   }

   public Field getField() {
      return this.field;
   }

   public void setField(Field field) {
      this.field = field;
   }

   public NCStatType getNcStatType() {
      return this.ncStatType;
   }

   public void setNcStatType(NCStatType ncStatType) {
      this.ncStatType = ncStatType;
   }

   public MakingskillResult getMakingskillResult() {
      return this.makingskillResult;
   }

   public void setMakingskillResult(MakingskillResult makingskillResult) {
      this.makingskillResult = makingskillResult;
   }

   public SkillCheck getSkillCheck() {
      return this.skillCheck;
   }

   public void setSkillCheck(SkillCheck skillCheck) {
      this.skillCheck = skillCheck;
   }

   public LotteryItemID getLotteryItemID() {
      return this.lotteryItemID;
   }

   public void setLotteryItemID(LotteryItemID lotteryItemID) {
      this.lotteryItemID = lotteryItemID;
   }

   public LotteryResultItemID getLotteryResultItemID() {
      return this.lotteryResultItemID;
   }

   public void setLotteryResultItemID(LotteryResultItemID lotteryResultItemID) {
      this.lotteryResultItemID = lotteryResultItemID;
   }

   public LotteryResultItemLevel getLotteryResultItemLevel() {
      return this.lotteryResultItemLevel;
   }

   public void setLotteryResultItemLevel(LotteryResultItemLevel lotteryResultItemLevel) {
      this.lotteryResultItemLevel = lotteryResultItemLevel;
   }

   public ComboKillCount getComboKillCount() {
      return this.comboKillCount;
   }

   public void setComboKillCount(ComboKillCount comboKillCount) {
      this.comboKillCount = comboKillCount;
   }

   public MultiKillCount getMultiKillCount() {
      return this.multiKillCount;
   }

   public void setMultiKillCount(MultiKillCount multiKillCount) {
      this.multiKillCount = multiKillCount;
   }

   public HitDamagePercent getHitDamagePercent() {
      return this.hitDamagePercent;
   }

   public void setHitDamagePercent(HitDamagePercent hitDamagePercent) {
      this.hitDamagePercent = hitDamagePercent;
   }
}
