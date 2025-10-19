package objects.users.stats;

import database.DBConfig;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import network.encode.PacketEncoder;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryType;
import objects.users.BuffedForSpecMap;
import objects.users.MapleCharacter;
import objects.users.jobs.BasicJob;
import objects.users.jobs.adventure.thief.Shadower;
import objects.users.jobs.adventure.warrior.DarkKnight;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.utils.Annotation;
import objects.utils.CFlagOperator;

public class SecondaryStat {
   private MapleCharacter player = null;
   List<BuffedForSpecMap> buffedForSpecMaps = new ArrayList<>();
   int defenseAtt = 0;
   int defenseState = 0;
   int pvpDamage = 0;
   int unk1 = 0;
   int viperEnergyCharge = 0;
   short delay = 0;
   boolean optionForIcon = false;
   boolean justBuffCheck = false;
   private boolean firstSet = false;
   private int attackCount = 0;
   int unk2 = 0;
   private Flag992 flag = new Flag992();
   private Flag992 toRemote = new Flag992();
   private Flag992 enDecode4Bytes = new Flag992();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indiePAD = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMAD = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDEF = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMHP = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMHPR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMMP = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMMPR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieACC = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieEVA = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieJump = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieSpeed = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieAllStat = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieStatRBasic = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDodgeCriticalTime = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieEXP = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieBooster = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieFixedDamageR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> pyramidStunBuff = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> pyramidFrozenBuff = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> pyramidBonusDamageBuff = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieRelaxEXP = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieSTR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDEX = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieINT = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieLUK = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDamR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMDF = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMaxDamageOver = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieAsrR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieTerR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieCR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indiePddR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieCD = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieBDR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieStatR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieStance = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieIgnoreMobPdpR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieEmpty = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indiePadR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMadR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieEvaR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDrainUp = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indiePMDR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieForceJump = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieForceSpeed = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> IndieDamageReduce = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieSummon = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieCooltimeReduce = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indiePartialNotDamaged = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieJointAttack = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieKeyDownMoving = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieQrPointTerm = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieEvasion = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieIncreaseHitDamage = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieSuperStance = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieGrandCross = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDamReduceR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieArc = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieAut = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indiePowerGuard = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDropPer = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMesoAmountRate = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieFlyAcc = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieRocketBooster = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDotHeal = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDuskDarkness = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieBlockSkill = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieBarrier = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieStarForce = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieNBDR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMonsterCollectionR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieMPConReduceR = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieAntiMagicShell = new ArrayList<>();
   @Annotation("Indie")
   private List<IndieTemporaryStatEntry> indieDecDamTargetMob = new ArrayList<>();
   public int PADValue = -99999;
   public int PADReason = -99999;
   public long PADTill = -99999L;
   public int PADLevel = -99999;
   public int PADFromID = -99999;
   public int MADValue = -99999;
   public int MADReason = -99999;
   public long MADTill = -99999L;
   public int MADLevel = -99999;
   public int MADFromID = -99999;
   public int DEFValue = -99999;
   public int DEFReason = -99999;
   public long DEFTill = -99999L;
   public int DEFLevel = -99999;
   public int DEFFromID = -99999;
   public int ACCValue = -99999;
   public int ACCReason = -99999;
   public long ACCTill = -99999L;
   public int ACCLevel = -99999;
   public int ACCFromID = -99999;
   public int EVAValue = -99999;
   public int EVAReason = -99999;
   public long EVATill = -99999L;
   public int EVALevel = -99999;
   public int EVAFromID = -99999;
   public int CraftValue = -99999;
   public int CraftReason = -99999;
   public long CraftTill = -99999L;
   public int CraftLevel = -99999;
   public int CraftFromID = -99999;
   public int SpeedValue = -99999;
   public int SpeedReason = -99999;
   public long SpeedTill = -99999L;
   public int SpeedLevel = -99999;
   public int SpeedFromID = -99999;
   public int JumpValue = -99999;
   public int JumpReason = -99999;
   public long JumpTill = -99999L;
   public int JumpLevel = -99999;
   public int JumpFromID = -99999;
   public int MagicGuardValue = -99999;
   public int MagicGuardReason = -99999;
   public long MagicGuardTill = -99999L;
   public int MagicGuardLevel = -99999;
   public int MagicGuardFromID = -99999;
   public int DarkSightValue = -99999;
   public int DarkSightReason = -99999;
   public long DarkSightTill = -99999L;
   public int DarkSightLevel = -99999;
   public int DarkSightFromID = -99999;
   public int BoosterValue = -99999;
   public int BoosterReason = -99999;
   public long BoosterTill = -99999L;
   public int BoosterLevel = -99999;
   public int BoosterFromID = -99999;
   public int PowerGuardValue = -99999;
   public int PowerGuardReason = -99999;
   public long PowerGuardTill = -99999L;
   public int PowerGuardLevel = -99999;
   public int PowerGuardFromID = -99999;
   public int MaxHPValue = -99999;
   public int MaxHPReason = -99999;
   public long MaxHPTill = -99999L;
   public int MaxHPLevel = -99999;
   public int MaxHPFromID = -99999;
   public int MaxMPValue = -99999;
   public int MaxMPReason = -99999;
   public long MaxMPTill = -99999L;
   public int MaxMPLevel = -99999;
   public int MaxMPFromID = -99999;
   public int InvincibleValue = -99999;
   public int InvincibleReason = -99999;
   public long InvincibleTill = -99999L;
   public int InvincibleLevel = -99999;
   public int InvincibleFromID = -99999;
   public int SoulArrowValue = -99999;
   public int SoulArrowReason = -99999;
   public long SoulArrowTill = -99999L;
   public int SoulArrowLevel = -99999;
   public int SoulArrowFromID = -99999;
   public int StunValue = -99999;
   public int StunReason = -99999;
   public long StunTill = -99999L;
   public int StunLevel = -99999;
   public int StunFromID = -99999;
   public int PoisonValue = -99999;
   public int PoisonReason = -99999;
   public long PoisonTill = -99999L;
   public int PoisonLevel = -99999;
   public int PoisonFromID = -99999;
   public int SealValue = -99999;
   public int SealReason = -99999;
   public long SealTill = -99999L;
   public int SealLevel = -99999;
   public int SealFromID = -99999;
   public int DarknessValue = -99999;
   public int DarknessReason = -99999;
   public long DarknessTill = -99999L;
   public int DarknessLevel = -99999;
   public int DarknessFromID = -99999;
   public int ComboValue = -99999;
   public int ComboReason = -99999;
   public long ComboTill = -99999L;
   public int ComboLevel = -99999;
   public int ComboFromID = -99999;
   public int ComboMaxValue = 0;
   public int ComboRate = 0;
   public int SnowChargeValue = -99999;
   public int SnowChargeReason = -99999;
   public long SnowChargeTill = -99999L;
   public int SnowChargeLevel = -99999;
   public int SnowChargeFromID = -99999;
   public int BlessedHammerValue = -99999;
   public int BlessedHammerReason = -99999;
   public long BlessedHammerTill = -99999L;
   public int BlessedHammerLevel = -99999;
   public int BlessedHammerFromID = -99999;
   public int BlessedHammerBigValue = -99999;
   public int BlessedHammerBigReason = -99999;
   public long BlessedHammerBigTill = -99999L;
   public int BlessedHammerBigLevel = -99999;
   public int BlessedHammerBigFromID = -99999;
   public int HolySymbolValue = -99999;
   public int HolySymbolReason = -99999;
   public long HolySymbolTill = -99999L;
   public int HolySymbolLevel = -99999;
   public int HolySymbolFromID = -99999;
   public int MesoUpValue = -99999;
   public int MesoUpReason = -99999;
   public long MesoUpTill = -99999L;
   public int MesoUpLevel = -99999;
   public int MesoUpFromID = -99999;
   public int ShadowPartnerValue = -99999;
   public int ShadowPartnerReason = -99999;
   public long ShadowPartnerTill = -99999L;
   public int ShadowPartnerLevel = -99999;
   public int ShadowPartnerFromID = -99999;
   public int PickPocketValue = -99999;
   public int PickPocketReason = -99999;
   public long PickPocketTill = -99999L;
   public int PickPocketLevel = -99999;
   public int PickPocketFromID = -99999;
   public int EnhanceAssassinateValue = -99999;
   public int EnhanceAssassinateReason = -99999;
   public long EnhanceAssassinateTill = -99999L;
   public int EnhanceAssassinateLevel = -99999;
   public int EnhanceAssassinateFromID = -99999;
   public int ThawValue = -99999;
   public int ThawReason = -99999;
   public long ThawTill = -99999L;
   public int ThawLevel = -99999;
   public int ThawFromID = -99999;
   public int WeaknessValue = -99999;
   public int WeaknessReason = -99999;
   public long WeaknessTill = -99999L;
   public int WeaknessLevel = -99999;
   public int WeaknessFromID = -99999;
   public int CurseValue = -99999;
   public int CurseReason = -99999;
   public long CurseTill = -99999L;
   public int CurseLevel = -99999;
   public int CurseFromID = -99999;
   public int SlowValue = -99999;
   public int SlowReason = -99999;
   public long SlowTill = -99999L;
   public int SlowLevel = -99999;
   public int SlowFromID = -99999;
   public int MorphValue = -99999;
   public int MorphReason = -99999;
   public long MorphTill = -99999L;
   public int MorphLevel = -99999;
   public int MorphFromID = -99999;
   public int RecoveryValue = -99999;
   public int RecoveryReason = -99999;
   public long RecoveryTill = -99999L;
   public int RecoveryLevel = -99999;
   public int RecoveryFromID = -99999;
   public int BasicStatUpValue = -99999;
   public int BasicStatUpReason = -99999;
   public long BasicStatUpTill = -99999L;
   public int BasicStatUpLevel = -99999;
   public int BasicStatUpFromID = -99999;
   public int StanceValue = -99999;
   public int StanceReason = -99999;
   public long StanceTill = -99999L;
   public int StanceLevel = -99999;
   public int StanceFromID = -99999;
   public int SharpEyesValue = -99999;
   public int SharpEyesReason = -99999;
   public long SharpEyesTill = -99999L;
   public int SharpEyesLevel = -99999;
   public int SharpEyesFromID = -99999;
   public int ManaReflectionValue = -99999;
   public int ManaReflectionReason = -99999;
   public long ManaReflectionTill = -99999L;
   public int ManaReflectionLevel = -99999;
   public int ManaReflectionFromID = -99999;
   public int AttractValue = -99999;
   public int AttractReason = -99999;
   public long AttractTill = -99999L;
   public int AttractLevel = -99999;
   public int AttractFromID = -99999;
   public int NoBulletConsumeValue = -99999;
   public int NoBulletConsumeReason = -99999;
   public long NoBulletConsumeTill = -99999L;
   public int NoBulletConsumeLevel = -99999;
   public int NoBulletConsumeFromID = -99999;
   public int InfinityValue = -99999;
   public int InfinityReason = -99999;
   public long InfinityTill = -99999L;
   public int InfinityLevel = -99999;
   public int InfinityFromID = -99999;
   public int AdvancedBlessValue = -99999;
   public int AdvancedBlessReason = -99999;
   public long AdvancedBlessTill = -99999L;
   public int AdvancedBlessLevel = -99999;
   public int AdvancedBlessFromID = -99999;
   public int IllusionStepValue = -99999;
   public int IllusionStepReason = -99999;
   public long IllusionStepTill = -99999L;
   public int IllusionStepLevel = -99999;
   public int IllusionStepFromID = -99999;
   public int BlindValue = -99999;
   public int BlindReason = -99999;
   public long BlindTill = -99999L;
   public int BlindLevel = -99999;
   public int BlindFromID = -99999;
   public int ConcentrationValue = -99999;
   public int ConcentrationReason = -99999;
   public long ConcentrationTill = -99999L;
   public int ConcentrationLevel = -99999;
   public int ConcentrationFromID = -99999;
   public int BanMapValue = -99999;
   public int BanMapReason = -99999;
   public long BanMapTill = -99999L;
   public int BanMapLevel = -99999;
   public int BanMapFromID = -99999;
   public int MaxLevelBuffValue = -99999;
   public int MaxLevelBuffReason = -99999;
   public long MaxLevelBuffTill = -99999L;
   public int MaxLevelBuffLevel = -99999;
   public int MaxLevelBuffFromID = -99999;
   public int MesoUpByItemValue = -99999;
   public int MesoUpByItemReason = -99999;
   public long MesoUpByItemTill = -99999L;
   public int MesoUpByItemLevel = -99999;
   public int MesoUpByItemFromID = -99999;
   public int WealthOfUnionValue = -99999;
   public int WealthOfUnionReason = -99999;
   public long WealthOfUnionTill = -99999L;
   public int WealthOfUnionLevel = -99999;
   public int WealthOfUnionFromID = -99999;
   public int RuneOfGreedValue = -99999;
   public int RuneOfGreedReason = -99999;
   public long RuneOfGreedTill = -99999L;
   public int RuneOfGreedLevel = -99999;
   public int RuneOfGreedFromID = -99999;
   public int GhostValue = -99999;
   public int GhostReason = -99999;
   public long GhostTill = -99999L;
   public int GhostLevel = -99999;
   public int GhostFromID = -99999;
   public int BarrierValue = -99999;
   public int BarrierReason = -99999;
   public long BarrierTill = -99999L;
   public int BarrierLevel = -99999;
   public int BarrierFromID = -99999;
   public int ReverseInputValue = -99999;
   public int ReverseInputReason = -99999;
   public long ReverseInputTill = -99999L;
   public int ReverseInputLevel = -99999;
   public int ReverseInputFromID = -99999;
   public int ItemUpByItemValue = -99999;
   public int ItemUpByItemReason = -99999;
   public long ItemUpByItemTill = -99999L;
   public int ItemUpByItemLevel = -99999;
   public int ItemUpByItemFromID = -99999;
   public int RespectPImmuneValue = -99999;
   public int RespectPImmuneReason = -99999;
   public long RespectPImmuneTill = -99999L;
   public int RespectPImmuneLevel = -99999;
   public int RespectPImmuneFromID = -99999;
   public int RespectMImmuneValue = -99999;
   public int RespectMImmuneReason = -99999;
   public long RespectMImmuneTill = -99999L;
   public int RespectMImmuneLevel = -99999;
   public int RespectMImmuneFromID = -99999;
   public int DefenseAttValue = -99999;
   public int DefenseAttReason = -99999;
   public long DefenseAttTill = -99999L;
   public int DefenseAttLevel = -99999;
   public int DefenseAttFromID = -99999;
   public int DefenseStateValue = -99999;
   public int DefenseStateReason = -99999;
   public long DefenseStateTill = -99999L;
   public int DefenseStateLevel = -99999;
   public int DefenseStateFromID = -99999;
   public int DojangBerserkValue = -99999;
   public int DojangBerserkReason = -99999;
   public long DojangBerserkTill = -99999L;
   public int DojangBerserkLevel = -99999;
   public int DojangBerserkFromID = -99999;
   public int DojangInvincibleValue = -99999;
   public int DojangInvincibleReason = -99999;
   public long DojangInvincibleTill = -99999L;
   public int DojangInvincibleLevel = -99999;
   public int DojangInvincibleFromID = -99999;
   public int DojangShieldValue = -99999;
   public int DojangShieldReason = -99999;
   public long DojangShieldTill = -99999L;
   public int DojangShieldLevel = -99999;
   public int DojangShieldFromID = -99999;
   public int SoulMasterFinalValue = -99999;
   public int SoulMasterFinalReason = -99999;
   public long SoulMasterFinalTill = -99999L;
   public int SoulMasterFinalLevel = -99999;
   public int SoulMasterFinalFromID = -99999;
   public int DualBladeFinalValue = -99999;
   public int DualBladeFinalReason = -99999;
   public long DualBladeFinalTill = -99999L;
   public int DualBladeFinalLevel = -99999;
   public int DualBladeFinalFromID = -99999;
   public int ElementResetValue = -99999;
   public int ElementResetReason = -99999;
   public long ElementResetTill = -99999L;
   public int ElementResetLevel = -99999;
   public int ElementResetFromID = -99999;
   public int HideAttackValue = -99999;
   public int HideAttackReason = -99999;
   public long HideAttackTill = -99999L;
   public int HideAttackLevel = -99999;
   public int HideAttackFromID = -99999;
   public int EventRateValue = -99999;
   public int EventRateReason = -99999;
   public long EventRateTill = -99999L;
   public int EventRateLevel = -99999;
   public int EventRateFromID = -99999;
   public int ComboAbilityBuffValue = -99999;
   public int ComboAbilityBuffReason = -99999;
   public long ComboAbilityBuffTill = -99999L;
   public int ComboAbilityBuffLevel = -99999;
   public int ComboAbilityBuffFromID = -99999;
   public int ComboDrainValue = -99999;
   public int ComboDrainReason = -99999;
   public long ComboDrainTill = -99999L;
   public int ComboDrainLevel = -99999;
   public int ComboDrainFromID = -99999;
   public int ComboBarrierValue = -99999;
   public int ComboBarrierReason = -99999;
   public long ComboBarrierTill = -99999L;
   public int ComboBarrierLevel = -99999;
   public int ComboBarrierFromID = -99999;
   public int BodyPressureValue = -99999;
   public int BodyPressureReason = -99999;
   public long BodyPressureTill = -99999L;
   public int BodyPressureLevel = -99999;
   public int BodyPressureFromID = -99999;
   public int RepeatEffectValue = -99999;
   public int RepeatEffectReason = -99999;
   public long RepeatEffectTill = -99999L;
   public int RepeatEffectLevel = -99999;
   public int RepeatEffectFromID = -99999;
   public int ExpBuffRateValue = -99999;
   public int ExpBuffRateReason = -99999;
   public long ExpBuffRateTill = -99999L;
   public int ExpBuffRateLevel = -99999;
   public int ExpBuffRateFromID = -99999;
   public int StopPortionValue = -99999;
   public int StopPortionReason = -99999;
   public long StopPortionTill = -99999L;
   public int StopPortionLevel = -99999;
   public int StopPortionFromID = -99999;
   public int StopMotionValue = -99999;
   public int StopMotionReason = -99999;
   public long StopMotionTill = -99999L;
   public int StopMotionLevel = -99999;
   public int StopMotionFromID = -99999;
   public int FearValue = -99999;
   public int FearReason = -99999;
   public long FearTill = -99999L;
   public int FearLevel = -99999;
   public int FearFromID = -99999;
   public int HiddenPieceOnValue = -99999;
   public int HiddenPieceOnReason = -99999;
   public long HiddenPieceOnTill = -99999L;
   public int HiddenPieceOnLevel = -99999;
   public int HiddenPieceOnFromID = -99999;
   public int HiddenPotentialValue = -99999;
   public int HiddenPotentialReason = -99999;
   public long HiddenPotentialTill = -99999L;
   public int HiddenPotentialLevel = -99999;
   public int HiddenPotentialFromID = -99999;
   public int MagicShieldValue = -99999;
   public int MagicShieldReason = -99999;
   public long MagicShieldTill = -99999L;
   public int MagicShieldLevel = -99999;
   public int MagicShieldFromID = -99999;
   public int MagicResistanceValue = -99999;
   public int MagicResistanceReason = -99999;
   public long MagicResistanceTill = -99999L;
   public int MagicResistanceLevel = -99999;
   public int MagicResistanceFromID = -99999;
   public int FlyingValue = -99999;
   public int FlyingReason = -99999;
   public long FlyingTill = -99999L;
   public int FlyingLevel = -99999;
   public int FlyingFromID = -99999;
   public int IceSkillValue = -99999;
   public int IceSkillReason = -99999;
   public long IceSkillTill = -99999L;
   public int IceSkillLevel = -99999;
   public int IceSkillFromID = -99999;
   public int FrozenValue = -99999;
   public int FrozenReason = -99999;
   public long FrozenTill = -99999L;
   public int FrozenLevel = -99999;
   public int FrozenFromID = -99999;
   public int AssistChargeValue = -99999;
   public int AssistChargeReason = -99999;
   public long AssistChargeTill = -99999L;
   public int AssistChargeLevel = -99999;
   public int AssistChargeFromID = -99999;
   public int EnrageValue = -99999;
   public int EnrageReason = -99999;
   public long EnrageTill = -99999L;
   public int EnrageLevel = -99999;
   public int EnrageFromID = -99999;
   public int DrawBackValue = -99999;
   public int DrawBackReason = -99999;
   public long DrawBackTill = -99999L;
   public int DrawBackLevel = -99999;
   public int DrawBackFromID = -99999;
   public int NotDamagedValue = -99999;
   public int NotDamagedReason = -99999;
   public long NotDamagedTill = -99999L;
   public int NotDamagedLevel = -99999;
   public int NotDamagedFromID = -99999;
   public int FinalCutValue = -99999;
   public int FinalCutReason = -99999;
   public long FinalCutTill = -99999L;
   public int FinalCutLevel = -99999;
   public int FinalCutFromID = -99999;
   public int HowlingAttackDamageValue = -99999;
   public int HowlingAttackDamageReason = -99999;
   public long HowlingAttackDamageTill = -99999L;
   public int HowlingAttackDamageLevel = -99999;
   public int HowlingAttackDamageFromID = -99999;
   public int BeastFormDamageUpValue = -99999;
   public int BeastFormDamageUpReason = -99999;
   public long BeastFormDamageUpTill = -99999L;
   public int BeastFormDamageUpLevel = -99999;
   public int BeastFormDamageUpFromID = -99999;
   public int DanceValue = -99999;
   public int DanceReason = -99999;
   public long DanceTill = -99999L;
   public int DanceLevel = -99999;
   public int DanceFromID = -99999;
   public int EnhancedMaxHPValue = -99999;
   public int EnhancedMaxHPReason = -99999;
   public long EnhancedMaxHPTill = -99999L;
   public int EnhancedMaxHPLevel = -99999;
   public int EnhancedMaxHPFromID = -99999;
   public int EnhancedMaxMPValue = -99999;
   public int EnhancedMaxMPReason = -99999;
   public long EnhancedMaxMPTill = -99999L;
   public int EnhancedMaxMPLevel = -99999;
   public int EnhancedMaxMPFromID = -99999;
   public int EnhancedPADValue = -99999;
   public int EnhancedPADReason = -99999;
   public long EnhancedPADTill = -99999L;
   public int EnhancedPADLevel = -99999;
   public int EnhancedPADFromID = -99999;
   public int EnhancedMADValue = -99999;
   public int EnhancedMADReason = -99999;
   public long EnhancedMADTill = -99999L;
   public int EnhancedMADLevel = -99999;
   public int EnhancedMADFromID = -99999;
   public int EnhancedDEFValue = -99999;
   public int EnhancedDEFReason = -99999;
   public long EnhancedDEFTill = -99999L;
   public int EnhancedDEFLevel = -99999;
   public int EnhancedDEFFromID = -99999;
   public int GuardValue = -99999;
   public int GuardReason = -99999;
   public long GuardTill = -99999L;
   public int GuardLevel = -99999;
   public int GuardFromID = -99999;
   public int SylvidiaValue = -99999;
   public int SylvidiaReason = -99999;
   public long SylvidiaTill = -99999L;
   public int SylvidiaLevel = -99999;
   public int SylvidiaFromID = -99999;
   public int SkillDamageRValue = -99999;
   public int SkillDamageRReason = -99999;
   public long SkillDamageRTill = -99999L;
   public int SkillDamageRLevel = -99999;
   public int SkillDamageRFromID = -99999;
   public int HowlingCriticalValue = -99999;
   public int HowlingCriticalReason = -99999;
   public long HowlingCriticalTill = -99999L;
   public int HowlingCriticalLevel = -99999;
   public int HowlingCriticalFromID = -99999;
   public int HowlingMaxMPValue = -99999;
   public int HowlingMaxMPReason = -99999;
   public long HowlingMaxMPTill = -99999L;
   public int HowlingMaxMPLevel = -99999;
   public int HowlingMaxMPFromID = -99999;
   public int HowlingDefenceValue = -99999;
   public int HowlingDefenceReason = -99999;
   public long HowlingDefenceTill = -99999L;
   public int HowlingDefenceLevel = -99999;
   public int HowlingDefenceFromID = -99999;
   public int HowlingEvasionValue = -99999;
   public int HowlingEvasionReason = -99999;
   public long HowlingEvasionTill = -99999L;
   public int HowlingEvasionLevel = -99999;
   public int HowlingEvasionFromID = -99999;
   public int PinkbeanMinibeenMoveValue = -99999;
   public int PinkbeanMinibeenMoveReason = -99999;
   public long PinkbeanMinibeenMoveTill = -99999L;
   public int PinkbeanMinibeenMoveLevel = -99999;
   public int PinkbeanMinibeenMoveFromID = -99999;
   public int SneakValue = -99999;
   public int SneakReason = -99999;
   public long SneakTill = -99999L;
   public int SneakLevel = -99999;
   public int SneakFromID = -99999;
   public int MechanicValue = -99999;
   public int MechanicReason = -99999;
   public long MechanicTill = -99999L;
   public int MechanicLevel = -99999;
   public int MechanicFromID = -99999;
   public int BeastFormMaxHPValue = -99999;
   public int BeastFormMaxHPReason = -99999;
   public long BeastFormMaxHPTill = -99999L;
   public int BeastFormMaxHPLevel = -99999;
   public int BeastFormMaxHPFromID = -99999;
   public int DiceValue = -99999;
   public int DiceReason = -99999;
   public long DiceTill = -99999L;
   public int DiceLevel = -99999;
   public int DiceFromID = -99999;
   public int BlessingArmorValue = -99999;
   public int BlessingArmorReason = -99999;
   public long BlessingArmorTill = -99999L;
   public int BlessingArmorLevel = -99999;
   public int BlessingArmorFromID = -99999;
   public int DamRValue = -99999;
   public int DamRReason = -99999;
   public long DamRTill = -99999L;
   public int DamRLevel = -99999;
   public int DamRFromID = -99999;
   public int TeleportMasteryOnValue = -99999;
   public int TeleportMasteryOnReason = -99999;
   public long TeleportMasteryOnTill = -99999L;
   public int TeleportMasteryOnLevel = -99999;
   public int TeleportMasteryOnFromID = -99999;
   public int CombatOrdersValue = -99999;
   public int CombatOrdersReason = -99999;
   public long CombatOrdersTill = -99999L;
   public int CombatOrdersLevel = -99999;
   public int CombatOrdersFromID = -99999;
   public int BeholderValue = -99999;
   public int BeholderReason = -99999;
   public long BeholderTill = -99999L;
   public int BeholderLevel = -99999;
   public int BeholderFromID = -99999;
   public int DispelItemOptionValue = -99999;
   public int DispelItemOptionReason = -99999;
   public long DispelItemOptionTill = -99999L;
   public int DispelItemOptionLevel = -99999;
   public int DispelItemOptionFromID = -99999;
   public int InflationValue = -99999;
   public int InflationReason = -99999;
   public long InflationTill = -99999L;
   public int InflationLevel = -99999;
   public int InflationFromID = -99999;
   public int OnixDivineProtectionValue = -99999;
   public int OnixDivineProtectionReason = -99999;
   public long OnixDivineProtectionTill = -99999L;
   public int OnixDivineProtectionLevel = -99999;
   public int OnixDivineProtectionFromID = -99999;
   public int WebValue = -99999;
   public int WebReason = -99999;
   public long WebTill = -99999L;
   public int WebLevel = -99999;
   public int WebFromID = -99999;
   public int BlessValue = -99999;
   public int BlessReason = -99999;
   public long BlessTill = -99999L;
   public int BlessLevel = -99999;
   public int BlessFromID = -99999;
   public int TimeBombValue = -99999;
   public int TimeBombReason = -99999;
   public long TimeBombTill = -99999L;
   public int TimeBombLevel = -99999;
   public int TimeBombFromID = -99999;
   public int DisOrderValue = -99999;
   public int DisOrderReason = -99999;
   public long DisOrderTill = -99999L;
   public int DisOrderLevel = -99999;
   public int DisOrderFromID = -99999;
   public int ThreadValue = -99999;
   public int ThreadReason = -99999;
   public long ThreadTill = -99999L;
   public int ThreadLevel = -99999;
   public int ThreadFromID = -99999;
   public int TeamValue = -99999;
   public int TeamReason = -99999;
   public long TeamTill = -99999L;
   public int TeamLevel = -99999;
   public int TeamFromID = -99999;
   public int ExplosionValue = -99999;
   public int ExplosionReason = -99999;
   public long ExplosionTill = -99999L;
   public int ExplosionLevel = -99999;
   public int ExplosionFromID = -99999;
   public int BuffLimitValue = -99999;
   public int BuffLimitReason = -99999;
   public long BuffLimitTill = -99999L;
   public int BuffLimitLevel = -99999;
   public int BuffLimitFromID = -99999;
   public int STRValue = -99999;
   public int STRReason = -99999;
   public long STRTill = -99999L;
   public int STRLevel = -99999;
   public int STRFromID = -99999;
   public int INTValue = -99999;
   public int INTReason = -99999;
   public long INTTill = -99999L;
   public int INTLevel = -99999;
   public int INTFromID = -99999;
   public int DEXValue = -99999;
   public int DEXReason = -99999;
   public long DEXTill = -99999L;
   public int DEXLevel = -99999;
   public int DEXFromID = -99999;
   public int LUKValue = -99999;
   public int LUKReason = -99999;
   public long LUKTill = -99999L;
   public int LUKLevel = -99999;
   public int LUKFromID = -99999;
   public int DispelItemOptionByFieldValue = -99999;
   public int DispelItemOptionByFieldReason = -99999;
   public long DispelItemOptionByFieldTill = -99999L;
   public int DispelItemOptionByFieldLevel = -99999;
   public int DispelItemOptionByFieldFromID = -99999;
   public int DarkTornadoValue = -99999;
   public int DarkTornadoReason = -99999;
   public long DarkTornadoTill = -99999L;
   public int DarkTornadoLevel = -99999;
   public int DarkTornadoFromID = -99999;
   public int PVPDamageValue = -99999;
   public int PVPDamageReason = -99999;
   public long PVPDamageTill = -99999L;
   public int PVPDamageLevel = -99999;
   public int PVPDamageFromID = -99999;
   public int PVPScoreBonusValue = -99999;
   public int PVPScoreBonusReason = -99999;
   public long PVPScoreBonusTill = -99999L;
   public int PVPScoreBonusLevel = -99999;
   public int PVPScoreBonusFromID = -99999;
   public int PVPInvincibleValue = -99999;
   public int PVPInvincibleReason = -99999;
   public long PVPInvincibleTill = -99999L;
   public int PVPInvincibleLevel = -99999;
   public int PVPInvincibleFromID = -99999;
   public int PVPRaceEffectValue = -99999;
   public int PVPRaceEffectReason = -99999;
   public long PVPRaceEffectTill = -99999L;
   public int PVPRaceEffectLevel = -99999;
   public int PVPRaceEffectFromID = -99999;
   public int WeaknessMDamageValue = -99999;
   public int WeaknessMDamageReason = -99999;
   public long WeaknessMDamageTill = -99999L;
   public int WeaknessMDamageLevel = -99999;
   public int WeaknessMDamageFromID = -99999;
   public int Frozen2Value = -99999;
   public int Frozen2Reason = -99999;
   public long Frozen2Till = -99999L;
   public int Frozen2Level = -99999;
   public int Frozen2FromID = -99999;
   public int PVPDamageSkillValue = -99999;
   public int PVPDamageSkillReason = -99999;
   public long PVPDamageSkillTill = -99999L;
   public int PVPDamageSkillLevel = -99999;
   public int PVPDamageSkillFromID = -99999;
   public int AmplifyDamageValue = -99999;
   public int AmplifyDamageReason = -99999;
   public long AmplifyDamageTill = -99999L;
   public int AmplifyDamageLevel = -99999;
   public int AmplifyDamageFromID = -99999;
   public int ShockValue = -99999;
   public int ShockReason = -99999;
   public long ShockTill = -99999L;
   public int ShockLevel = -99999;
   public int ShockFromID = -99999;
   public int InfinityForceValue = -99999;
   public int InfinityForceReason = -99999;
   public long InfinityForceTill = -99999L;
   public int InfinityForceLevel = -99999;
   public int InfinityForceFromID = -99999;
   public int IncMaxHPValue = -99999;
   public int IncMaxHPReason = -99999;
   public long IncMaxHPTill = -99999L;
   public int IncMaxHPLevel = -99999;
   public int IncMaxHPFromID = -99999;
   public int IncMaxMPValue = -99999;
   public int IncMaxMPReason = -99999;
   public long IncMaxMPTill = -99999L;
   public int IncMaxMPLevel = -99999;
   public int IncMaxMPFromID = -99999;
   public int HolyMagicShellValue = -99999;
   public int HolyMagicShellReason = -99999;
   public long HolyMagicShellTill = -99999L;
   public int HolyMagicShellLevel = -99999;
   public int HolyMagicShellFromID = -99999;
   public int KeydownTimeIgnoreValue = -99999;
   public int KeydownTimeIgnoreReason = -99999;
   public long KeydownTimeIgnoreTill = -99999L;
   public int KeydownTimeIgnoreLevel = -99999;
   public int KeydownTimeIgnoreFromID = -99999;
   public int ArcaneAimValue = -99999;
   public int ArcaneAimReason = -99999;
   public long ArcaneAimTill = -99999L;
   public int ArcaneAimLevel = -99999;
   public int ArcaneAimFromID = -99999;
   public int MasterMagicOnValue = -99999;
   public int MasterMagicOnReason = -99999;
   public long MasterMagicOnTill = -99999L;
   public int MasterMagicOnLevel = -99999;
   public int MasterMagicOnFromID = -99999;
   public int AsrRValue = -99999;
   public int AsrRReason = -99999;
   public long AsrRTill = -99999L;
   public int AsrRLevel = -99999;
   public int AsrRFromID = -99999;
   public int TerRValue = -99999;
   public int TerRReason = -99999;
   public long TerRTill = -99999L;
   public int TerRLevel = -99999;
   public int TerRFromID = -99999;
   public int DamAbsorbShieldValue = -99999;
   public int DamAbsorbShieldReason = -99999;
   public long DamAbsorbShieldTill = -99999L;
   public int DamAbsorbShieldLevel = -99999;
   public int DamAbsorbShieldFromID = -99999;
   public int DevilishPowerValue = -99999;
   public int DevilishPowerReason = -99999;
   public long DevilishPowerTill = -99999L;
   public int DevilishPowerLevel = -99999;
   public int DevilishPowerFromID = -99999;
   public int RouletteValue = -99999;
   public int RouletteReason = -99999;
   public long RouletteTill = -99999L;
   public int RouletteLevel = -99999;
   public int RouletteFromID = -99999;
   public int CrewCommandershipValue = -99999;
   public int CrewCommandershipReason = -99999;
   public long CrewCommandershipTill = -99999L;
   public int CrewCommandershipLevel = -99999;
   public int CrewCommandershipFromID = -99999;
   public int AsrRByItemValue = -99999;
   public int AsrRByItemReason = -99999;
   public long AsrRByItemTill = -99999L;
   public int AsrRByItemLevel = -99999;
   public int AsrRByItemFromID = -99999;
   public int EventValue = -99999;
   public int EventReason = -99999;
   public long EventTill = -99999L;
   public int EventLevel = -99999;
   public int EventFromID = -99999;
   public int CriticalBuffValue = -99999;
   public int CriticalBuffReason = -99999;
   public long CriticalBuffTill = -99999L;
   public int CriticalBuffLevel = -99999;
   public int CriticalBuffFromID = -99999;
   public int DropRateValue = -99999;
   public int DropRateReason = -99999;
   public long DropRateTill = -99999L;
   public int DropRateLevel = -99999;
   public int DropRateFromID = -99999;
   public int PlusExpRateValue = -99999;
   public int PlusExpRateReason = -99999;
   public long PlusExpRateTill = -99999L;
   public int PlusExpRateLevel = -99999;
   public int PlusExpRateFromID = -99999;
   public int ItemInvincibleValue = -99999;
   public int ItemInvincibleReason = -99999;
   public long ItemInvincibleTill = -99999L;
   public int ItemInvincibleLevel = -99999;
   public int ItemInvincibleFromID = -99999;
   public int AwakeValue = -99999;
   public int AwakeReason = -99999;
   public long AwakeTill = -99999L;
   public int AwakeLevel = -99999;
   public int AwakeFromID = -99999;
   public int ItemCriticalValue = -99999;
   public int ItemCriticalReason = -99999;
   public long ItemCriticalTill = -99999L;
   public int ItemCriticalLevel = -99999;
   public int ItemCriticalFromID = -99999;
   public int ItemEvadeValue = -99999;
   public int ItemEvadeReason = -99999;
   public long ItemEvadeTill = -99999L;
   public int ItemEvadeLevel = -99999;
   public int ItemEvadeFromID = -99999;
   public int Event2Value = -99999;
   public int Event2Reason = -99999;
   public long Event2Till = -99999L;
   public int Event2Level = -99999;
   public int Event2FromID = -99999;
   public int VampiricTouchValue = -99999;
   public int VampiricTouchReason = -99999;
   public long VampiricTouchTill = -99999L;
   public int VampiricTouchLevel = -99999;
   public int VampiricTouchFromID = -99999;
   public int DDRValue = -99999;
   public int DDRReason = -99999;
   public long DDRTill = -99999L;
   public int DDRLevel = -99999;
   public int DDRFromID = -99999;
   public int IncCriticalDamMinValue = -99999;
   public int IncCriticalDamMinReason = -99999;
   public long IncCriticalDamMinTill = -99999L;
   public int IncCriticalDamMinLevel = -99999;
   public int IncCriticalDamMinFromID = -99999;
   public int IncCriticalDamMaxValue = -99999;
   public int IncCriticalDamMaxReason = -99999;
   public long IncCriticalDamMaxTill = -99999L;
   public int IncCriticalDamMaxLevel = -99999;
   public int IncCriticalDamMaxFromID = -99999;
   public int DeathMarkValue = -99999;
   public int DeathMarkReason = -99999;
   public long DeathMarkTill = -99999L;
   public int DeathMarkLevel = -99999;
   public int DeathMarkFromID = -99999;
   public int UsefulAdvancedBlessValue = -99999;
   public int UsefulAdvancedBlessReason = -99999;
   public long UsefulAdvancedBlessTill = -99999L;
   public int UsefulAdvancedBlessLevel = -99999;
   public int UsefulAdvancedBlessFromID = -99999;
   public int LapidificationValue = -99999;
   public int LapidificationReason = -99999;
   public long LapidificationTill = -99999L;
   public int LapidificationLevel = -99999;
   public int LapidificationFromID = -99999;
   public int VenomSnakeValue = -99999;
   public int VenomSnakeReason = -99999;
   public long VenomSnakeTill = -99999L;
   public int VenomSnakeLevel = -99999;
   public int VenomSnakeFromID = -99999;
   public int CarnivalAttackValue = -99999;
   public int CarnivalAttackReason = -99999;
   public long CarnivalAttackTill = -99999L;
   public int CarnivalAttackLevel = -99999;
   public int CarnivalAttackFromID = -99999;
   public int CarnivalDefenceValue = -99999;
   public int CarnivalDefenceReason = -99999;
   public long CarnivalDefenceTill = -99999L;
   public int CarnivalDefenceLevel = -99999;
   public int CarnivalDefenceFromID = -99999;
   public int CarnivalExpValue = -99999;
   public int CarnivalExpReason = -99999;
   public long CarnivalExpTill = -99999L;
   public int CarnivalExpLevel = -99999;
   public int CarnivalExpFromID = -99999;
   public int SlowAttackValue = -99999;
   public int SlowAttackReason = -99999;
   public long SlowAttackTill = -99999L;
   public int SlowAttackLevel = -99999;
   public int SlowAttackFromID = -99999;
   public int PyramidEffectValue = -99999;
   public int PyramidEffectReason = -99999;
   public long PyramidEffectTill = -99999L;
   public int PyramidEffectLevel = -99999;
   public int PyramidEffectFromID = -99999;
   public int KillingPointValue = -99999;
   public int KillingPointReason = -99999;
   public long KillingPointTill = -99999L;
   public int KillingPointLevel = -99999;
   public int KillingPointFromID = -99999;
   public int JokerValue = -99999;
   public int JokerReason = -99999;
   public long JokerTill = -99999L;
   public int JokerLevel = -99999;
   public int JokerFromID = -99999;
   public int KeyDownMovingValue = -99999;
   public int KeyDownMovingReason = -99999;
   public long KeyDownMovingTill = -99999L;
   public int KeyDownMovingLevel = -99999;
   public int KeyDownMovingFromID = -99999;
   public int IgnoreTargetDEFValue = -99999;
   public int IgnoreTargetDEFReason = -99999;
   public long IgnoreTargetDEFTill = -99999L;
   public int IgnoreTargetDEFLevel = -99999;
   public int IgnoreTargetDEFFromID = -99999;
   public int StrikerCorrectionBuffValue = -99999;
   public int StrikerCorrectionBuffReason = -99999;
   public long StrikerCorrectionBuffTill = -99999L;
   public int StrikerCorrectionBuffLevel = -99999;
   public int StrikerCorrectionBuffFromID = -99999;
   public int ReviveOnceValue = -99999;
   public int ReviveOnceReason = -99999;
   public long ReviveOnceTill = -99999L;
   public int ReviveOnceLevel = -99999;
   public int ReviveOnceFromID = -99999;
   public int InvisibleValue = -99999;
   public int InvisibleReason = -99999;
   public long InvisibleTill = -99999L;
   public int InvisibleLevel = -99999;
   public int InvisibleFromID = -99999;
   public int EnrageCrValue = -99999;
   public int EnrageCrReason = -99999;
   public long EnrageCrTill = -99999L;
   public int EnrageCrLevel = -99999;
   public int EnrageCrFromID = -99999;
   public int EnrageCrDamMinValue = -99999;
   public int EnrageCrDamMinReason = -99999;
   public long EnrageCrDamMinTill = -99999L;
   public int EnrageCrDamMinLevel = -99999;
   public int EnrageCrDamMinFromID = -99999;
   public int JudgementValue = -99999;
   public int JudgementReason = -99999;
   public long JudgementTill = -99999L;
   public int JudgementLevel = -99999;
   public int JudgementFromID = -99999;
   public int DojangLuckyBonusValue = -99999;
   public int DojangLuckyBonusReason = -99999;
   public long DojangLuckyBonusTill = -99999L;
   public int DojangLuckyBonusLevel = -99999;
   public int DojangLuckyBonusFromID = -99999;
   public int New_366_02Value = -99999;
   public int New_366_02Reason = -99999;
   public long New_366_02Till = -99999L;
   public int New_366_02Level = -99999;
   public int New_366_02FromID = -99999;
   public int New_366_03Value = -99999;
   public int New_366_03Reason = -99999;
   public long New_366_03Till = -99999L;
   public int New_366_03Level = -99999;
   public int New_366_03FromID = -99999;
   public int New_366_04Value = -99999;
   public int New_366_04Reason = -99999;
   public long New_366_04Till = -99999L;
   public int New_366_04Level = -99999;
   public int New_366_04FromID = -99999;
   public int New_366_05Value = -99999;
   public int New_366_05Reason = -99999;
   public long New_366_05Till = -99999L;
   public int New_366_05Level = -99999;
   public int New_366_05FromID = -99999;
   public int OrbitalExplosionValue = -99999;
   public int OrbitalExplosionReason = -99999;
   public long OrbitalExplosionTill = -99999L;
   public int OrbitalExplosionLevel = -99999;
   public int OrbitalExplosionFromID = -99999;
   public int PhoenixDriveValue = -99999;
   public int PhoenixDriveReason = -99999;
   public long PhoenixDriveTill = -99999L;
   public int PhoenixDriveLevel = -99999;
   public int PhoenixDriveFromID = -99999;
   public int New_366_07Value = -99999;
   public int New_366_07Reason = -99999;
   public long New_366_07Till = -99999L;
   public int New_366_07Level = -99999;
   public int New_366_07FromID = -99999;
   public int New_366_08Value = -99999;
   public int New_366_08Reason = -99999;
   public long New_366_08Till = -99999L;
   public int New_366_08Level = -99999;
   public int New_366_08FromID = -99999;
   public int PriorPreparationStackValue = -99999;
   public int PriorPreparationStackReason = -99999;
   public long PriorPreparationStackTill = -99999L;
   public int PriorPreparationStackLevel = -99999;
   public int PriorPreparationStackFromID = -99999;
   public int PainMarkValue = -99999;
   public int PainMarkReason = -99999;
   public long PainMarkTill = -99999L;
   public int PainMarkLevel = -99999;
   public int PainMarkFromID = -99999;
   public int MagnetValue = -99999;
   public int MagnetReason = -99999;
   public long MagnetTill = -99999L;
   public int MagnetLevel = -99999;
   public int MagnetFromID = -99999;
   public int MagnetAreaValue = -99999;
   public int MagnetAreaReason = -99999;
   public long MagnetAreaTill = -99999L;
   public int MagnetAreaLevel = -99999;
   public int MagnetAreaFromID = -99999;
   public int GuidedArrowValue = -99999;
   public int GuidedArrowReason = -99999;
   public long GuidedArrowTill = -99999L;
   public int GuidedArrowLevel = -99999;
   public int GuidedArrowFromID = -99999;
   public int CraftJavelinValue = -99999;
   public int CraftJavelinReason = -99999;
   public long CraftJavelinTill = -99999L;
   public int CraftJavelinLevel = -99999;
   public int CraftJavelinFromID = -99999;
   public int BlessMarkValue = -99999;
   public int BlessMarkReason = -99999;
   public long BlessMarkTill = -99999L;
   public int BlessMarkLevel = -99999;
   public int BlessMarkFromID = -99999;
   public int BlessMarkIcon = 0;
   public int BlessMarkMax = 0;
   public int ProfessionalAgentValue = -99999;
   public int ProfessionalAgentReason = -99999;
   public long ProfessionalAgentTill = -99999L;
   public int ProfessionalAgentLevel = -99999;
   public int ProfessionalAgentFromID = -99999;
   public int Unk3Value = -99999;
   public int Unk3Reason = -99999;
   public long Unk3Till = -99999L;
   public int Unk3Level = -99999;
   public int Unk3FromID = -99999;
   public int TideOfBattleValue = -99999;
   public int TideOfBattleReason = -99999;
   public long TideOfBattleTill = -99999L;
   public int TideOfBattleLevel = -99999;
   public int TideOfBattleFromID = -99999;
   public int ShadowMomentumValue = -99999;
   public int ShadowMomentumReason = -99999;
   public long ShadowMomentumTill = -99999L;
   public int ShadowMomentumLevel = -99999;
   public int ShadowMomentumFromID = -99999;
   public int GrandCrossSizeValue = -99999;
   public int GrandCrossSizeReason = -99999;
   public long GrandCrossSizeTill = -99999L;
   public int GrandCrossSizeLevel = -99999;
   public int GrandCrossSizeFromID = -99999;
   public int ExpBuffBlockValue = -99999;
   public int ExpBuffBlockReason = -99999;
   public long ExpBuffBlockTill = -99999L;
   public int ExpBuffBlockLevel = -99999;
   public int ExpBuffBlockFromID = -99999;
   public int PinkBeanExpBuffBlockValue = -99999;
   public int PinkBeanExpBuffBlockReason = -99999;
   public long PinkBeanExpBuffBlockTill = -99999L;
   public int PinkBeanExpBuffBlockLevel = -99999;
   public int PinkBeanExpBuffBlockFromID = -99999;
   public int LuckOfUnionValue = -99999;
   public int LuckOfUnionReason = -99999;
   public long LuckOfUnionTill = -99999L;
   public int LuckOfUnionLevel = -99999;
   public int LuckOfUnionFromID = -99999;
   public int VampDeathValue = -99999;
   public int VampDeathReason = -99999;
   public long VampDeathTill = -99999L;
   public int VampDeathLevel = -99999;
   public int VampDeathFromID = -99999;
   public int BlessingArmorIncPADValue = -99999;
   public int BlessingArmorIncPADReason = -99999;
   public long BlessingArmorIncPADTill = -99999L;
   public int BlessingArmorIncPADLevel = -99999;
   public int BlessingArmorIncPADFromID = -99999;
   public int KeyDownAreaMovingValue = -99999;
   public int KeyDownAreaMovingReason = -99999;
   public long KeyDownAreaMovingTill = -99999L;
   public int KeyDownAreaMovingLevel = -99999;
   public int KeyDownAreaMovingFromID = -99999;
   public int LarknessValue = -99999;
   public int LarknessReason = -99999;
   public long LarknessTill = -99999L;
   public int LarknessLevel = -99999;
   public int LarknessFromID = -99999;
   public int StackBuffValue = -99999;
   public int StackBuffReason = -99999;
   public long StackBuffTill = -99999L;
   public int StackBuffLevel = -99999;
   public int StackBuffFromID = -99999;
   public int BlessOfDarknessValue = -99999;
   public int BlessOfDarknessReason = -99999;
   public long BlessOfDarknessTill = -99999L;
   public int BlessOfDarknessLevel = -99999;
   public int BlessOfDarknessFromID = -99999;
   public int BlessOfDarknessCount = 0;
   public int AntiMagicShellValue = -99999;
   public int AntiMagicShellReason = -99999;
   public long AntiMagicShellTill = -99999L;
   public int AntiMagicShellLevel = -99999;
   public int AntiMagicShellFromID = -99999;
   public int LifeTidalValue = -99999;
   public int LifeTidalReason = -99999;
   public long LifeTidalTill = -99999L;
   public int LifeTidalLevel = -99999;
   public int LifeTidalFromID = -99999;
   public int HitCriDamRValue = -99999;
   public int HitCriDamRReason = -99999;
   public long HitCriDamRTill = -99999L;
   public int HitCriDamRLevel = -99999;
   public int HitCriDamRFromID = -99999;
   public int SmashStackValue = -99999;
   public int SmashStackReason = -99999;
   public long SmashStackTill = -99999L;
   public int SmashStackLevel = -99999;
   public int SmashStackFromID = -99999;
   public int PartyBarrierValue = -99999;
   public int PartyBarrierReason = -99999;
   public long PartyBarrierTill = -99999L;
   public int PartyBarrierLevel = -99999;
   public int PartyBarrierFromID = -99999;
   public int ReshuffleSwitchValue = -99999;
   public int ReshuffleSwitchReason = -99999;
   public long ReshuffleSwitchTill = -99999L;
   public int ReshuffleSwitchLevel = -99999;
   public int ReshuffleSwitchFromID = -99999;
   public int SpecialActionValue = -99999;
   public int SpecialActionReason = -99999;
   public long SpecialActionTill = -99999L;
   public int SpecialActionLevel = -99999;
   public int SpecialActionFromID = -99999;
   public int VampDeathSummonValue = -99999;
   public int VampDeathSummonReason = -99999;
   public long VampDeathSummonTill = -99999L;
   public int VampDeathSummonLevel = -99999;
   public int VampDeathSummonFromID = -99999;
   public int StopForceAtomInfoValue = -99999;
   public int StopForceAtomInfoReason = -99999;
   public long StopForceAtomInfoTill = -99999L;
   public int StopForceAtomInfoLevel = -99999;
   public int StopForceAtomInfoFromID = -99999;
   public int SoulGazeCriDamRValue = -99999;
   public int SoulGazeCriDamRReason = -99999;
   public long SoulGazeCriDamRTill = -99999L;
   public int SoulGazeCriDamRLevel = -99999;
   public int SoulGazeCriDamRFromID = -99999;
   public int SoulRageCountValue = -99999;
   public int SoulRageCountReason = -99999;
   public long SoulRageCountTill = -99999L;
   public int SoulRageCountLevel = -99999;
   public int SoulRageCountFromID = -99999;
   public int PowerTransferGaugeValue = -99999;
   public int PowerTransferGaugeReason = -99999;
   public long PowerTransferGaugeTill = -99999L;
   public int PowerTransferGaugeLevel = -99999;
   public int PowerTransferGaugeFromID = -99999;
   public int AffinitySlugValue = -99999;
   public int AffinitySlugReason = -99999;
   public long AffinitySlugTill = -99999L;
   public int AffinitySlugLevel = -99999;
   public int AffinitySlugFromID = -99999;
   public int TrinityValue = -99999;
   public int TrinityReason = -99999;
   public long TrinityTill = -99999L;
   public int TrinityLevel = -99999;
   public int TrinityFromID = -99999;
   public int IncMaxDamageValue = -99999;
   public int IncMaxDamageReason = -99999;
   public long IncMaxDamageTill = -99999L;
   public int IncMaxDamageLevel = -99999;
   public int IncMaxDamageFromID = -99999;
   public int BossShieldValue = -99999;
   public int BossShieldReason = -99999;
   public long BossShieldTill = -99999L;
   public int BossShieldLevel = -99999;
   public int BossShieldFromID = -99999;
   public int MobZoneStateValue = -99999;
   public int MobZoneStateReason = -99999;
   public long MobZoneStateTill = -99999L;
   public int MobZoneStateLevel = -99999;
   public int MobZoneStateFromID = -99999;
   public int GiveMeHealValue = -99999;
   public int GiveMeHealReason = -99999;
   public long GiveMeHealTill = -99999L;
   public int GiveMeHealLevel = -99999;
   public int GiveMeHealFromID = -99999;
   public int TouchMeValue = -99999;
   public int TouchMeReason = -99999;
   public long TouchMeTill = -99999L;
   public int TouchMeLevel = -99999;
   public int TouchMeFromID = -99999;
   public int ContagionValue = -99999;
   public int ContagionReason = -99999;
   public long ContagionTill = -99999L;
   public int ContagionLevel = -99999;
   public int ContagionFromID = -99999;
   public int ComboUnlimitedValue = -99999;
   public int ComboUnlimitedReason = -99999;
   public long ComboUnlimitedTill = -99999L;
   public int ComboUnlimitedLevel = -99999;
   public int ComboUnlimitedFromID = -99999;
   public int SoulExaltValue = -99999;
   public int SoulExaltReason = -99999;
   public long SoulExaltTill = -99999L;
   public int SoulExaltLevel = -99999;
   public int SoulExaltFromID = -99999;
   public int IgnorePCounterValue = -99999;
   public int IgnorePCounterReason = -99999;
   public long IgnorePCounterTill = -99999L;
   public int IgnorePCounterLevel = -99999;
   public int IgnorePCounterFromID = -99999;
   public int IgnoreAllCounterValue = -99999;
   public int IgnoreAllCounterReason = -99999;
   public long IgnoreAllCounterTill = -99999L;
   public int IgnoreAllCounterLevel = -99999;
   public int IgnoreAllCounterFromID = -99999;
   public int IgnorePImmuneValue = -99999;
   public int IgnorePImmuneReason = -99999;
   public long IgnorePImmuneTill = -99999L;
   public int IgnorePImmuneLevel = -99999;
   public int IgnorePImmuneFromID = -99999;
   public int IgnoreAllImmuneValue = -99999;
   public int IgnoreAllImmuneReason = -99999;
   public long IgnoreAllImmuneTill = -99999L;
   public int IgnoreAllImmuneLevel = -99999;
   public int IgnoreAllImmuneFromID = -99999;
   public int FinalJudgementValue = -99999;
   public int FinalJudgementReason = -99999;
   public long FinalJudgementTill = -99999L;
   public int FinalJudgementLevel = -99999;
   public int FinalJudgementFromID = -99999;
   public int FireAuraValue = -99999;
   public int FireAuraReason = -99999;
   public long FireAuraTill = -99999L;
   public int FireAuraLevel = -99999;
   public int FireAuraFromID = -99999;
   public int VengeanceOfAngelValue = -99999;
   public int VengeanceOfAngelReason = -99999;
   public long VengeanceOfAngelTill = -99999L;
   public int VengeanceOfAngelLevel = -99999;
   public int VengeanceOfAngelFromID = -99999;
   public int HeavensDoorValue = -99999;
   public int HeavensDoorReason = -99999;
   public long HeavensDoorTill = -99999L;
   public int HeavensDoorLevel = -99999;
   public int HeavensDoorFromID = -99999;
   public int PreparationValue = -99999;
   public int PreparationReason = -99999;
   public long PreparationTill = -99999L;
   public int PreparationLevel = -99999;
   public int PreparationFromID = -99999;
   public int BullsEyeValue = -99999;
   public int BullsEyeReason = -99999;
   public long BullsEyeTill = -99999L;
   public int BullsEyeLevel = -99999;
   public int BullsEyeFromID = -99999;
   public int IncEffectHPOptionValue = -99999;
   public int IncEffectHPOptionReason = -99999;
   public long IncEffectHPOptionTill = -99999L;
   public int IncEffectHPOptionLevel = -99999;
   public int IncEffectHPOptionFromID = -99999;
   public int IncEffectMPPotionValue = -99999;
   public int IncEffectMPPotionReason = -99999;
   public long IncEffectMPPotionTill = -99999L;
   public int IncEffectMPPotionLevel = -99999;
   public int IncEffectMPPotionFromID = -99999;
   public int BleedingToxinValue = -99999;
   public int BleedingToxinReason = -99999;
   public long BleedingToxinTill = -99999L;
   public int BleedingToxinLevel = -99999;
   public int BleedingToxinFromID = -99999;
   public int IgnoreMobDamRValue = -99999;
   public int IgnoreMobDamRReason = -99999;
   public long IgnoreMobDamRTill = -99999L;
   public int IgnoreMobDamRLevel = -99999;
   public int IgnoreMobDamRFromID = -99999;
   public int AsuraValue = -99999;
   public int AsuraReason = -99999;
   public long AsuraTill = -99999L;
   public int AsuraLevel = -99999;
   public int AsuraFromID = -99999;
   public int MegaSmasherValue = -99999;
   public int MegaSmasherReason = -99999;
   public long MegaSmasherTill = -99999L;
   public int MegaSmasherLevel = -99999;
   public int MegaSmasherFromID = -99999;
   public int FlipTheCoinValue = -99999;
   public int FlipTheCoinReason = -99999;
   public long FlipTheCoinTill = -99999L;
   public int FlipTheCoinLevel = -99999;
   public int FlipTheCoinFromID = -99999;
   public int SerpentSpiritValue = -99999;
   public int SerpentSpiritReason = -99999;
   public long SerpentSpiritTill = -99999L;
   public int SerpentSpiritLevel = -99999;
   public int SerpentSpiritFromID = -99999;
   public int StimulateValue = -99999;
   public int StimulateReason = -99999;
   public long StimulateTill = -99999L;
   public int StimulateLevel = -99999;
   public int StimulateFromID = -99999;
   public int ReturnTeleportValue = -99999;
   public int ReturnTeleportReason = -99999;
   public long ReturnTeleportTill = -99999L;
   public int ReturnTeleportLevel = -99999;
   public int ReturnTeleportFromID = -99999;
   public int EpicDropRIncreaseValue = -99999;
   public int EpicDropRIncreaseReason = -99999;
   public long EpicDropRIncreaseTill = -99999L;
   public int EpicDropRIncreaseLevel = -99999;
   public int EpicDropRIncreaseFromID = -99999;
   public int IgnoreMobPdpRValue = -99999;
   public int IgnoreMobPdpRReason = -99999;
   public long IgnoreMobPdpRTill = -99999L;
   public int IgnoreMobPdpRLevel = -99999;
   public int IgnoreMobPdpRFromID = -99999;
   public int BdRValue = -99999;
   public int BdRReason = -99999;
   public long BdRTill = -99999L;
   public int BdRLevel = -99999;
   public int BdRFromID = -99999;
   public int CapDebuffValue = -99999;
   public int CapDebuffReason = -99999;
   public long CapDebuffTill = -99999L;
   public int CapDebuffLevel = -99999;
   public int CapDebuffFromID = -99999;
   public int ExceedValue = -99999;
   public int ExceedReason = -99999;
   public long ExceedTill = -99999L;
   public int ExceedLevel = -99999;
   public int ExceedFromID = -99999;
   public int DiabolicRecoveryValue = -99999;
   public int DiabolicRecoveryReason = -99999;
   public long DiabolicRecoveryTill = -99999L;
   public int DiabolicRecoveryLevel = -99999;
   public int DiabolicRecoveryFromID = -99999;
   public int FinalAttackPropValue = -99999;
   public int FinalAttackPropReason = -99999;
   public long FinalAttackPropTill = -99999L;
   public int FinalAttackPropLevel = -99999;
   public int FinalAttackPropFromID = -99999;
   public int ExceedOverloadValue = -99999;
   public int ExceedOverloadReason = -99999;
   public long ExceedOverloadTill = -99999L;
   public int ExceedOverloadLevel = -99999;
   public int ExceedOverloadFromID = -99999;
   public int OverloadCountValue = -99999;
   public int OverloadCountReason = -99999;
   public long OverloadCountTill = -99999L;
   public int OverloadCountLevel = -99999;
   public int OverloadCountFromID = -99999;
   public int BuckShotValue = -99999;
   public int BuckShotReason = -99999;
   public long BuckShotTill = -99999L;
   public int BuckShotLevel = -99999;
   public int BuckShotFromID = -99999;
   public int FireBombValue = -99999;
   public int FireBombReason = -99999;
   public long FireBombTill = -99999L;
   public int FireBombLevel = -99999;
   public int FireBombFromID = -99999;
   public int HalfStatByDebuffValue = -99999;
   public int HalfStatByDebuffReason = -99999;
   public long HalfStatByDebuffTill = -99999L;
   public int HalfStatByDebuffLevel = -99999;
   public int HalfStatByDebuffFromID = -99999;
   public int SurplusSupplyValue = -99999;
   public int SurplusSupplyReason = -99999;
   public long SurplusSupplyTill = -99999L;
   public int SurplusSupplyLevel = -99999;
   public int SurplusSupplyFromID = -99999;
   public int SetBaseDamageValue = -99999;
   public int SetBaseDamageReason = -99999;
   public long SetBaseDamageTill = -99999L;
   public int SetBaseDamageLevel = -99999;
   public int SetBaseDamageFromID = -99999;
   public int EVARValue = -99999;
   public int EVARReason = -99999;
   public long EVARTill = -99999L;
   public int EVARLevel = -99999;
   public int EVARFromID = -99999;
   public int NewFlyingValue = -99999;
   public int NewFlyingReason = -99999;
   public long NewFlyingTill = -99999L;
   public int NewFlyingLevel = -99999;
   public int NewFlyingFromID = -99999;
   public int AmaranthGeneratorValue = -99999;
   public int AmaranthGeneratorReason = -99999;
   public long AmaranthGeneratorTill = -99999L;
   public int AmaranthGeneratorLevel = -99999;
   public int AmaranthGeneratorFromID = -99999;
   public int OnCapsuleValue = -99999;
   public int OnCapsuleReason = -99999;
   public long OnCapsuleTill = -99999L;
   public int OnCapsuleLevel = -99999;
   public int OnCapsuleFromID = -99999;
   public int CygnusElementSkillValue = -99999;
   public int CygnusElementSkillReason = -99999;
   public long CygnusElementSkillTill = -99999L;
   public int CygnusElementSkillLevel = -99999;
   public int CygnusElementSkillFromID = -99999;
   public int StrikerHyperElectricValue = -99999;
   public int StrikerHyperElectricReason = -99999;
   public long StrikerHyperElectricTill = -99999L;
   public int StrikerHyperElectricLevel = -99999;
   public int StrikerHyperElectricFromID = -99999;
   public int EventPointAbsorbValue = -99999;
   public int EventPointAbsorbReason = -99999;
   public long EventPointAbsorbTill = -99999L;
   public int EventPointAbsorbLevel = -99999;
   public int EventPointAbsorbFromID = -99999;
   public int EventAssembleValue = -99999;
   public int EventAssembleReason = -99999;
   public long EventAssembleTill = -99999L;
   public int EventAssembleLevel = -99999;
   public int EventAssembleFromID = -99999;
   public int StormBringerValue = -99999;
   public int StormBringerReason = -99999;
   public long StormBringerTill = -99999L;
   public int StormBringerLevel = -99999;
   public int StormBringerFromID = -99999;
   public int ACCRValue = -99999;
   public int ACCRReason = -99999;
   public long ACCRTill = -99999L;
   public int ACCRLevel = -99999;
   public int ACCRFromID = -99999;
   public int DEXRValue = -99999;
   public int DEXRReason = -99999;
   public long DEXRTill = -99999L;
   public int DEXRLevel = -99999;
   public int DEXRFromID = -99999;
   public int AlbatrossValue = -99999;
   public int AlbatrossReason = -99999;
   public long AlbatrossTill = -99999L;
   public int AlbatrossLevel = -99999;
   public int AlbatrossFromID = -99999;
   public int TranslucenceValue = -99999;
   public int TranslucenceReason = -99999;
   public long TranslucenceTill = -99999L;
   public int TranslucenceLevel = -99999;
   public int TranslucenceFromID = -99999;
   public int CosmosValue = -99999;
   public int CosmosReason = -99999;
   public long CosmosTill = -99999L;
   public int CosmosLevel = -99999;
   public int CosmosFromID = -99999;
   public int PoseTypeValue = -99999;
   public int PoseTypeReason = -99999;
   public long PoseTypeTill = -99999L;
   public int PoseTypeLevel = -99999;
   public int PoseTypeFromID = -99999;
   public int LightOfSpiritValue = -99999;
   public int LightOfSpiritReason = -99999;
   public long LightOfSpiritTill = -99999L;
   public int LightOfSpiritLevel = -99999;
   public int LightOfSpiritFromID = -99999;
   public int ElementSoulValue = -99999;
   public int ElementSoulReason = -99999;
   public long ElementSoulTill = -99999L;
   public int ElementSoulLevel = -99999;
   public int ElementSoulFromID = -99999;
   public int CosmikOrbValue = -99999;
   public int CosmikOrbReason = -99999;
   public long CosmikOrbTill = -99999L;
   public int CosmikOrbLevel = -99999;
   public int CosmikOrbFromID = -99999;
   public int CosmikForgeValue = -99999;
   public int CosmikForgeReason = -99999;
   public long CosmikForgeTill = -99999L;
   public int CosmikForgeLevel = -99999;
   public int CosmikForgeFromID = -99999;
   public int GlimmeringTimeValue = -99999;
   public int GlimmeringTimeReason = -99999;
   public long GlimmeringTimeTill = -99999L;
   public int GlimmeringTimeLevel = -99999;
   public int GlimmeringTimeFromID = -99999;
   public int TrueSightValue = -99999;
   public int TrueSightReason = -99999;
   public long TrueSightTill = -99999L;
   public int TrueSightLevel = -99999;
   public int TrueSightFromID = -99999;
   public int SoulExplosionValue = -99999;
   public int SoulExplosionReason = -99999;
   public long SoulExplosionTill = -99999L;
   public int SoulExplosionLevel = -99999;
   public int SoulExplosionFromID = -99999;
   public int SoulMPValue = -99999;
   public int SoulMPReason = -99999;
   public long SoulMPTill = -99999L;
   public int SoulMPLevel = -99999;
   public int SoulMPFromID = -99999;
   public int SoulMPX = 0;
   public int FullSoulMPValue = -99999;
   public int FullSoulMPReason = -99999;
   public long FullSoulMPTill = -99999L;
   public int FullSoulMPLevel = -99999;
   public int FullSoulMPFromID = -99999;
   public int FullSoulMPX = 0;
   public int SoulSkillDamageUpValue = -99999;
   public int SoulSkillDamageUpReason = -99999;
   public long SoulSkillDamageUpTill = -99999L;
   public int SoulSkillDamageUpLevel = -99999;
   public int SoulSkillDamageUpFromID = -99999;
   public int HolyChargeValue = -99999;
   public int HolyChargeReason = -99999;
   public long HolyChargeTill = -99999L;
   public int HolyChargeLevel = -99999;
   public int HolyChargeFromID = -99999;
   public int RestorationValue = -99999;
   public int RestorationReason = -99999;
   public long RestorationTill = -99999L;
   public int RestorationLevel = -99999;
   public int RestorationFromID = -99999;
   public int ReincarnationValue = -99999;
   public int ReincarnationReason = -99999;
   public long ReincarnationTill = -99999L;
   public int ReincarnationLevel = -99999;
   public int ReincarnationFromID = -99999;
   public int ReincarnationActivateValue = -99999;
   public int ReincarnationActivateReason = -99999;
   public long ReincarnationActivateTill = -99999L;
   public int ReincarnationActivateLevel = -99999;
   public int ReincarnationActivateFromID = -99999;
   public int ReincarnationAcceptValue = -99999;
   public int ReincarnationAcceptReason = -99999;
   public long ReincarnationAcceptTill = -99999L;
   public int ReincarnationAcceptLevel = -99999;
   public int ReincarnationAcceptFromID = -99999;
   public int CrossOverChainValue = -99999;
   public int CrossOverChainReason = -99999;
   public long CrossOverChainTill = -99999L;
   public int CrossOverChainLevel = -99999;
   public int CrossOverChainFromID = -99999;
   public int ChargeBuffValue = -99999;
   public int ChargeBuffReason = -99999;
   public long ChargeBuffTill = -99999L;
   public int ChargeBuffLevel = -99999;
   public int ChargeBuffFromID = -99999;
   public int ChillingStepValue = -99999;
   public int ChillingStepReason = -99999;
   public long ChillingStepTill = -99999L;
   public int ChillingStepLevel = -99999;
   public int ChillingStepFromID = -99999;
   public int DotBasedBuffValue = -99999;
   public int DotBasedBuffReason = -99999;
   public long DotBasedBuffTill = -99999L;
   public int DotBasedBuffLevel = -99999;
   public int DotBasedBuffFromID = -99999;
   public int BlessEnsenbleValue = -99999;
   public int BlessEnsenbleReason = -99999;
   public long BlessEnsenbleTill = -99999L;
   public int BlessEnsenbleLevel = -99999;
   public int BlessEnsenbleFromID = -99999;
   public int ComboCostIncValue = -99999;
   public int ComboCostIncReason = -99999;
   public long ComboCostIncTill = -99999L;
   public int ComboCostIncLevel = -99999;
   public int ComboCostIncFromID = -99999;
   public int ExtremeArcheryValue = -99999;
   public int ExtremeArcheryReason = -99999;
   public long ExtremeArcheryTill = -99999L;
   public int ExtremeArcheryLevel = -99999;
   public int ExtremeArcheryFromID = -99999;
   public int NaviFlyingValue = -99999;
   public int NaviFlyingReason = -99999;
   public long NaviFlyingTill = -99999L;
   public int NaviFlyingLevel = -99999;
   public int NaviFlyingFromID = -99999;
   public int QuiverCatridgeValue = -99999;
   public int QuiverCatridgeReason = -99999;
   public long QuiverCatridgeTill = -99999L;
   public int QuiverCatridgeLevel = -99999;
   public int QuiverCatridgeFromID = -99999;
   public int AdvancedQuiverValue = -99999;
   public int AdvancedQuiverReason = -99999;
   public long AdvancedQuiverTill = -99999L;
   public int AdvancedQuiverLevel = -99999;
   public int AdvancedQuiverFromID = -99999;
   public int UserControlMobValue = -99999;
   public int UserControlMobReason = -99999;
   public long UserControlMobTill = -99999L;
   public int UserControlMobLevel = -99999;
   public int UserControlMobFromID = -99999;
   public int ImmuneBarrierValue = -99999;
   public int ImmuneBarrierReason = -99999;
   public long ImmuneBarrierTill = -99999L;
   public int ImmuneBarrierLevel = -99999;
   public int ImmuneBarrierFromID = -99999;
   public int ImmuneBarrierX = 0;
   public int ArmorPiercingValue = -99999;
   public int ArmorPiercingReason = -99999;
   public long ArmorPiercingTill = -99999L;
   public int ArmorPiercingLevel = -99999;
   public int ArmorPiercingFromID = -99999;
   public int CriticalGrowingValue = -99999;
   public int CriticalGrowingReason = -99999;
   public long CriticalGrowingTill = -99999L;
   public int CriticalGrowingLevel = -99999;
   public int CriticalGrowingFromID = -99999;
   public int RelicPatternValue = -99999;
   public int RelicPatternReason = -99999;
   public long RelicPatternTill = -99999L;
   public int RelicPatternLevel = -99999;
   public int RelicPatternFromID = -99999;
   public int QuickDrawValue = -99999;
   public int QuickDrawReason = -99999;
   public long QuickDrawTill = -99999L;
   public int QuickDrawLevel = -99999;
   public int QuickDrawFromID = -99999;
   public int BowMasterConcentrationValue = -99999;
   public int BowMasterConcentrationReason = -99999;
   public long BowMasterConcentrationTill = -99999L;
   public int BowMasterConcentrationLevel = -99999;
   public int BowMasterConcentrationFromID = -99999;
   public int TimeFastABuffValue = -99999;
   public int TimeFastABuffReason = -99999;
   public long TimeFastABuffTill = -99999L;
   public int TimeFastABuffLevel = -99999;
   public int TimeFastABuffFromID = -99999;
   public int TimeFastBBuffValue = -99999;
   public int TimeFastBBuffReason = -99999;
   public long TimeFastBBuffTill = -99999L;
   public int TimeFastBBuffLevel = -99999;
   public int TimeFastBBuffFromID = -99999;
   public int GatherDropRValue = -99999;
   public int GatherDropRReason = -99999;
   public long GatherDropRTill = -99999L;
   public int GatherDropRLevel = -99999;
   public int GatherDropRFromID = -99999;
   public int AimBox2DValue = -99999;
   public int AimBox2DReason = -99999;
   public long AimBox2DTill = -99999L;
   public int AimBox2DLevel = -99999;
   public int AimBox2DFromID = -99999;
   public int TrueSnipingValue = -99999;
   public int TrueSnipingReason = -99999;
   public long TrueSnipingTill = -99999L;
   public int TrueSnipingLevel = -99999;
   public int TrueSnipingFromID = -99999;
   public int DebuffToleranceValue = -99999;
   public int DebuffToleranceReason = -99999;
   public long DebuffToleranceTill = -99999L;
   public int DebuffToleranceLevel = -99999;
   public int DebuffToleranceFromID = -99999;
   public int FairyTearsValue = -99999;
   public int FairyTearsReason = -99999;
   public long FairyTearsTill = -99999L;
   public int FairyTearsLevel = -99999;
   public int FairyTearsFromID = -99999;
   public int DotHealHPPerSecondRValue = -99999;
   public int DotHealHPPerSecondRReason = -99999;
   public long DotHealHPPerSecondRTill = -99999L;
   public int DotHealHPPerSecondRLevel = -99999;
   public int DotHealHPPerSecondRFromID = -99999;
   public int DotHealMPPerSecondRValue = -99999;
   public int DotHealMPPerSecondRReason = -99999;
   public long DotHealMPPerSecondRTill = -99999L;
   public int DotHealMPPerSecondRLevel = -99999;
   public int DotHealMPPerSecondRFromID = -99999;
   public int SpiritGuardValue = -99999;
   public int SpiritGuardReason = -99999;
   public long SpiritGuardTill = -99999L;
   public int SpiritGuardLevel = -99999;
   public int SpiritGuardFromID = -99999;
   public int PreReviveOnceValue = -99999;
   public int PreReviveOnceReason = -99999;
   public long PreReviveOnceTill = -99999L;
   public int PreReviveOnceLevel = -99999;
   public int PreReviveOnceFromID = -99999;
   public int SetBaseDamageByBuffValue = -99999;
   public int SetBaseDamageByBuffReason = -99999;
   public long SetBaseDamageByBuffTill = -99999L;
   public int SetBaseDamageByBuffLevel = -99999;
   public int SetBaseDamageByBuffFromID = -99999;
   public int LimitMPValue = -99999;
   public int LimitMPReason = -99999;
   public long LimitMPTill = -99999L;
   public int LimitMPLevel = -99999;
   public int LimitMPFromID = -99999;
   public int ReflectDamRValue = -99999;
   public int ReflectDamRReason = -99999;
   public long ReflectDamRTill = -99999L;
   public int ReflectDamRLevel = -99999;
   public int ReflectDamRFromID = -99999;
   public int ComboTempestValue = -99999;
   public int ComboTempestReason = -99999;
   public long ComboTempestTill = -99999L;
   public int ComboTempestLevel = -99999;
   public int ComboTempestFromID = -99999;
   public int MHPCutRValue = -99999;
   public int MHPCutRReason = -99999;
   public long MHPCutRTill = -99999L;
   public int MHPCutRLevel = -99999;
   public int MHPCutRFromID = -99999;
   public int MMPCutRValue = -99999;
   public int MMPCutRReason = -99999;
   public long MMPCutRTill = -99999L;
   public int MMPCutRLevel = -99999;
   public int MMPCutRFromID = -99999;
   public int SelfWeaknessValue = -99999;
   public int SelfWeaknessReason = -99999;
   public long SelfWeaknessTill = -99999L;
   public int SelfWeaknessLevel = -99999;
   public int SelfWeaknessFromID = -99999;
   public int ElementDarknessValue = -99999;
   public int ElementDarknessReason = -99999;
   public long ElementDarknessTill = -99999L;
   public int ElementDarknessLevel = -99999;
   public int ElementDarknessFromID = -99999;
   public int FlareTrickValue = -99999;
   public int FlareTrickReason = -99999;
   public long FlareTrickTill = -99999L;
   public int FlareTrickLevel = -99999;
   public int FlareTrickFromID = -99999;
   public int EmberValue = -99999;
   public int EmberReason = -99999;
   public long EmberTill = -99999L;
   public int EmberLevel = -99999;
   public int EmberFromID = -99999;
   public int DominionValue = -99999;
   public int DominionReason = -99999;
   public long DominionTill = -99999L;
   public int DominionLevel = -99999;
   public int DominionFromID = -99999;
   public int SiphonVitalityValue = -99999;
   public int SiphonVitalityReason = -99999;
   public long SiphonVitalityTill = -99999L;
   public int SiphonVitalityLevel = -99999;
   public int SiphonVitalityFromID = -99999;
   public int DarknessAscensionValue = -99999;
   public int DarknessAscensionReason = -99999;
   public long DarknessAscensionTill = -99999L;
   public int DarknessAscensionLevel = -99999;
   public int DarknessAscensionFromID = -99999;
   public int BossWaitingLinesBuffValue = -99999;
   public int BossWaitingLinesBuffReason = -99999;
   public long BossWaitingLinesBuffTill = -99999L;
   public int BossWaitingLinesBuffLevel = -99999;
   public int BossWaitingLinesBuffFromID = -99999;
   public int DamageReduceValue = -99999;
   public int DamageReduceReason = -99999;
   public long DamageReduceTill = -99999L;
   public int DamageReduceLevel = -99999;
   public int DamageReduceFromID = -99999;
   public int ShadowServantValue = -99999;
   public int ShadowServantReason = -99999;
   public long ShadowServantTill = -99999L;
   public int ShadowServantLevel = -99999;
   public int ShadowServantFromID = -99999;
   public int ShadowIllusionValue = -99999;
   public int ShadowIllusionReason = -99999;
   public long ShadowIllusionTill = -99999L;
   public int ShadowIllusionLevel = -99999;
   public int ShadowIllusionFromID = -99999;
   public int KnockBackValue = -99999;
   public int KnockBackReason = -99999;
   public long KnockBackTill = -99999L;
   public int KnockBackLevel = -99999;
   public int KnockBackFromID = -99999;
   public int PMDRValue = -99999;
   public int PMDRReason = -99999;
   public long PMDRTill = -99999L;
   public int PMDRLevel = -99999;
   public int PMDRFromID = -99999;
   public int ComplusionSlantValue = -99999;
   public int ComplusionSlantReason = -99999;
   public long ComplusionSlantTill = -99999L;
   public int ComplusionSlantLevel = -99999;
   public int ComplusionSlantFromID = -99999;
   public int JaguarSummonedValue = -99999;
   public int JaguarSummonedReason = -99999;
   public long JaguarSummonedTill = -99999L;
   public int JaguarSummonedLevel = -99999;
   public int JaguarSummonedFromID = -99999;
   public int JaguarCountValue = -99999;
   public int JaguarCountReason = -99999;
   public long JaguarCountTill = -99999L;
   public int JaguarCountLevel = -99999;
   public int JaguarCountFromID = -99999;
   public int SSFShootingAttackValue = -99999;
   public int SSFShootingAttackReason = -99999;
   public long SSFShootingAttackTill = -99999L;
   public int SSFShootingAttackLevel = -99999;
   public int SSFShootingAttackFromID = -99999;
   public int DevilCryValue = -99999;
   public int DevilCryReason = -99999;
   public long DevilCryTill = -99999L;
   public int DevilCryLevel = -99999;
   public int DevilCryFromID = -99999;
   public int ShieldAttackValue = -99999;
   public int ShieldAttackReason = -99999;
   public long ShieldAttackTill = -99999L;
   public int ShieldAttackLevel = -99999;
   public int ShieldAttackFromID = -99999;
   public int DarkLightingValue = -99999;
   public int DarkLightingReason = -99999;
   public long DarkLightingTill = -99999L;
   public int DarkLightingLevel = -99999;
   public int DarkLightingFromID = -99999;
   public int BMageAuraValue = -99999;
   public int BMageAuraReason = -99999;
   public long BMageAuraTill = -99999L;
   public int BMageAuraLevel = -99999;
   public int BMageAuraFromID = -99999;
   public int AttackCountXValue = -99999;
   public int AttackCountXReason = -99999;
   public long AttackCountXTill = -99999L;
   public int AttackCountXLevel = -99999;
   public int AttackCountXFromID = -99999;
   public int BMageDeathValue = -99999;
   public int BMageDeathReason = -99999;
   public long BMageDeathTill = -99999L;
   public int BMageDeathLevel = -99999;
   public int BMageDeathFromID = -99999;
   public int BombTimeValue = -99999;
   public int BombTimeReason = -99999;
   public long BombTimeTill = -99999L;
   public int BombTimeLevel = -99999;
   public int BombTimeFromID = -99999;
   public int BattlePvP_Mike_ShieldValue = -99999;
   public int BattlePvP_Mike_ShieldReason = -99999;
   public long BattlePvP_Mike_ShieldTill = -99999L;
   public int BattlePvP_Mike_ShieldLevel = -99999;
   public int BattlePvP_Mike_ShieldFromID = -99999;
   public int BattlePvP_Mike_BugleValue = -99999;
   public int BattlePvP_Mike_BugleReason = -99999;
   public long BattlePvP_Mike_BugleTill = -99999L;
   public int BattlePvP_Mike_BugleLevel = -99999;
   public int BattlePvP_Mike_BugleFromID = -99999;
   public int XenonAegisSystemValue = -99999;
   public int XenonAegisSystemReason = -99999;
   public long XenonAegisSystemTill = -99999L;
   public int XenonAegisSystemLevel = -99999;
   public int XenonAegisSystemFromID = -99999;
   public int AngelicBursterSoulSeekerValue = -99999;
   public int AngelicBursterSoulSeekerReason = -99999;
   public long AngelicBursterSoulSeekerTill = -99999L;
   public int AngelicBursterSoulSeekerLevel = -99999;
   public int AngelicBursterSoulSeekerFromID = -99999;
   public int ForceAtomOnOffValue = -99999;
   public int ForceAtomOnOffReason = -99999;
   public long ForceAtomOnOffTill = -99999L;
   public int ForceAtomOnOffLevel = -99999;
   public int ForceAtomOnOffFromID = -99999;
   public int NightWalkerBatValue = -99999;
   public int NightWalkerBatReason = -99999;
   public long NightWalkerBatTill = -99999L;
   public int NightWalkerBatLevel = -99999;
   public int NightWalkerBatFromID = -99999;
   public int HiddenPossesionValue = -99999;
   public int HiddenPossesionReason = -99999;
   public long HiddenPossesionTill = -99999L;
   public int HiddenPossesionLevel = -99999;
   public int HiddenPossesionFromID = -99999;
   public int WizardIgniteValue = -99999;
   public int WizardIgniteReason = -99999;
   public long WizardIgniteTill = -99999L;
   public int WizardIgniteLevel = -99999;
   public int WizardIgniteFromID = -99999;
   public int FireBarrierValue = -99999;
   public int FireBarrierReason = -99999;
   public long FireBarrierTill = -99999L;
   public int FireBarrierLevel = -99999;
   public int FireBarrierFromID = -99999;
   public int ChangeFoxManValue = -99999;
   public int ChangeFoxManReason = -99999;
   public long ChangeFoxManTill = -99999L;
   public int ChangeFoxManLevel = -99999;
   public int ChangeFoxManFromID = -99999;
   public int HolyUnityValue = -99999;
   public int HolyUnityReason = -99999;
   public long HolyUnityTill = -99999L;
   public int HolyUnityLevel = -99999;
   public int HolyUnityFromID = -99999;
   public int DemonFrenzyValue = -99999;
   public int DemonFrenzyReason = -99999;
   public long DemonFrenzyTill = -99999L;
   public int DemonFrenzyLevel = -99999;
   public int DemonFrenzyFromID = -99999;
   public int ShadowSpearValue = -99999;
   public int ShadowSpearReason = -99999;
   public long ShadowSpearTill = -99999L;
   public int ShadowSpearLevel = -99999;
   public int ShadowSpearFromID = -99999;
   public int DemonDamAbsorbShieldValue = -99999;
   public int DemonDamAbsorbShieldReason = -99999;
   public long DemonDamAbsorbShieldTill = -99999L;
   public int DemonDamAbsorbShieldLevel = -99999;
   public int DemonDamAbsorbShieldFromID = -99999;
   public int EllisionValue = -99999;
   public int EllisionReason = -99999;
   public long EllisionTill = -99999L;
   public int EllisionLevel = -99999;
   public int EllisionFromID = -99999;
   public int QuiverFullBurstValue = -99999;
   public int QuiverFullBurstReason = -99999;
   public long QuiverFullBurstTill = -99999L;
   public int QuiverFullBurstLevel = -99999;
   public int QuiverFullBurstFromID = -99999;
   public int QuiverFullBurst = 0;
   public int SwordBaptismValue = -99999;
   public int SwordBaptismReason = -99999;
   public long SwordBaptismTill = -99999L;
   public int SwordBaptismLevel = -99999;
   public int SwordBaptismFromID = -99999;
   public int WildGrenadeValue = -99999;
   public int WildGrenadeReason = -99999;
   public long WildGrenadeTill = -99999L;
   public int WildGrenadeLevel = -99999;
   public int WildGrenadeFromID = -99999;
   public int Unk89Value = -99999;
   public int Unk89Reason = -99999;
   public long Unk89Till = -99999L;
   public int Unk89Level = -99999;
   public int Unk89FromID = -99999;
   public int SwordOfLightValue = -99999;
   public int SwordOfLightReason = -99999;
   public long SwordOfLightTill = -99999L;
   public int SwordOfLightLevel = -99999;
   public int SwordOfLightFromID = -99999;
   public int BattlePvP_Helena_MarkValue = -99999;
   public int BattlePvP_Helena_MarkReason = -99999;
   public long BattlePvP_Helena_MarkTill = -99999L;
   public int BattlePvP_Helena_MarkLevel = -99999;
   public int BattlePvP_Helena_MarkFromID = -99999;
   public int BattlePvP_Helena_windSpiritValue = -99999;
   public int BattlePvP_Helena_windSpiritReason = -99999;
   public long BattlePvP_Helena_windSpiritTill = -99999L;
   public int BattlePvP_Helena_windSpiritLevel = -99999;
   public int BattlePvP_Helena_windSpiritFromID = -99999;
   public int BattlePvP_LangE_ProtectionValue = -99999;
   public int BattlePvP_LangE_ProtectionReason = -99999;
   public long BattlePvP_LangE_ProtectionTill = -99999L;
   public int BattlePvP_LangE_ProtectionLevel = -99999;
   public int BattlePvP_LangE_ProtectionFromID = -99999;
   public int BattlePvP_LeeMalNyun_ScaleUpValue = -99999;
   public int BattlePvP_LeeMalNyun_ScaleUpReason = -99999;
   public long BattlePvP_LeeMalNyun_ScaleUpTill = -99999L;
   public int BattlePvP_LeeMalNyun_ScaleUpLevel = -99999;
   public int BattlePvP_LeeMalNyun_ScaleUpFromID = -99999;
   public int BattlePvp_ReviveValue = -99999;
   public int BattlePvp_ReviveReason = -99999;
   public long BattlePvp_ReviveTill = -99999L;
   public int BattlePvp_ReviveLevel = -99999;
   public int BattlePvp_ReviveFromID = -99999;
   public int PinkbeanAttackBuffValue = -99999;
   public int PinkbeanAttackBuffReason = -99999;
   public long PinkbeanAttackBuffTill = -99999L;
   public int PinkbeanAttackBuffLevel = -99999;
   public int PinkbeanAttackBuffFromID = -99999;
   public int PinkbeanRelaxValue = -99999;
   public int PinkbeanRelaxReason = -99999;
   public long PinkbeanRelaxTill = -99999L;
   public int PinkbeanRelaxLevel = -99999;
   public int PinkbeanRelaxFromID = -99999;
   public int PinkbeanRollingGradeValue = -99999;
   public int PinkbeanRollingGradeReason = -99999;
   public long PinkbeanRollingGradeTill = -99999L;
   public int PinkbeanRollingGradeLevel = -99999;
   public int PinkbeanRollingGradeFromID = -99999;
   public int PinkbeanYoYoStackValue = -99999;
   public int PinkbeanYoYoStackReason = -99999;
   public long PinkbeanYoYoStackTill = -99999L;
   public int PinkbeanYoYoStackLevel = -99999;
   public int PinkbeanYoYoStackFromID = -99999;
   public int RandAreaAttackValue = -99999;
   public int RandAreaAttackReason = -99999;
   public long RandAreaAttackTill = -99999L;
   public int RandAreaAttackLevel = -99999;
   public int RandAreaAttackFromID = -99999;
   public int AranBeyonderDamAbsorbValue = -99999;
   public int AranBeyonderDamAbsorbReason = -99999;
   public long AranBeyonderDamAbsorbTill = -99999L;
   public int AranBeyonderDamAbsorbLevel = -99999;
   public int AranBeyonderDamAbsorbFromID = -99999;
   public int NextAttackEnhanceValue = -99999;
   public int NextAttackEnhanceReason = -99999;
   public long NextAttackEnhanceTill = -99999L;
   public int NextAttackEnhanceLevel = -99999;
   public int NextAttackEnhanceFromID = -99999;
   public int BeyondNextAttackProbValue = -99999;
   public int BeyondNextAttackProbReason = -99999;
   public long BeyondNextAttackProbTill = -99999L;
   public int BeyondNextAttackProbLevel = -99999;
   public int BeyondNextAttackProbFromID = -99999;
   public int AranComboTempestOptionValue = -99999;
   public int AranComboTempestOptionReason = -99999;
   public long AranComboTempestOptionTill = -99999L;
   public int AranComboTempestOptionLevel = -99999;
   public int AranComboTempestOptionFromID = -99999;
   public int ViperTimeLeapValue = -99999;
   public int ViperTimeLeapReason = -99999;
   public long ViperTimeLeapTill = -99999L;
   public int ViperTimeLeapLevel = -99999;
   public int ViperTimeLeapFromID = -99999;
   public int RoyalGuardStateValue = -99999;
   public int RoyalGuardStateReason = -99999;
   public long RoyalGuardStateTill = -99999L;
   public int RoyalGuardStateLevel = -99999;
   public int RoyalGuardStateFromID = -99999;
   public int RoyalGuardPrepareValue = -99999;
   public int RoyalGuardPrepareReason = -99999;
   public long RoyalGuardPrepareTill = -99999L;
   public int RoyalGuardPrepareLevel = -99999;
   public int RoyalGuardPrepareFromID = -99999;
   public int MichaelSoulLinkValue = -99999;
   public int MichaelSoulLinkReason = -99999;
   public long MichaelSoulLinkTill = -99999L;
   public int MichaelSoulLinkLevel = -99999;
   public int MichaelSoulLinkFromID = -99999;
   public int GuardianOfLightValue = -99999;
   public int GuardianOfLightReason = -99999;
   public long GuardianOfLightTill = -99999L;
   public int GuardianOfLightLevel = -99999;
   public int GuardianOfLightFromID = -99999;
   public int TriflingWhimOnOffValue = -99999;
   public int TriflingWhimOnOffReason = -99999;
   public long TriflingWhimOnOffTill = -99999L;
   public int TriflingWhimOnOffLevel = -99999;
   public int TriflingWhimOnOffFromID = -99999;
   public int AddRangeOnOffValue = -99999;
   public int AddRangeOnOffReason = -99999;
   public long AddRangeOnOffTill = -99999L;
   public int AddRangeOnOffLevel = -99999;
   public int AddRangeOnOffFromID = -99999;
   public int KinesisPsychicPointValue = -99999;
   public int KinesisPsychicPointReason = -99999;
   public long KinesisPsychicPointTill = -99999L;
   public int KinesisPsychicPointLevel = -99999;
   public int KinesisPsychicPointFromID = -99999;
   public int KinesisPsychicOverValue = -99999;
   public int KinesisPsychicOverReason = -99999;
   public long KinesisPsychicOverTill = -99999L;
   public int KinesisPsychicOverLevel = -99999;
   public int KinesisPsychicOverFromID = -99999;
   public int KinesisPsychicShieldValue = -99999;
   public int KinesisPsychicShieldReason = -99999;
   public long KinesisPsychicShieldTill = -99999L;
   public int KinesisPsychicShieldLevel = -99999;
   public int KinesisPsychicShieldFromID = -99999;
   public int KinesisIncMasteryValue = -99999;
   public int KinesisIncMasteryReason = -99999;
   public long KinesisIncMasteryTill = -99999L;
   public int KinesisIncMasteryLevel = -99999;
   public int KinesisIncMasteryFromID = -99999;
   public int KinesisPsychicEnrageShieldValue = -99999;
   public int KinesisPsychicEnrageShieldReason = -99999;
   public long KinesisPsychicEnrageShieldTill = -99999L;
   public int KinesisPsychicEnrageShieldLevel = -99999;
   public int KinesisPsychicEnrageShieldFromID = -99999;
   public int BladeStanceValue = -99999;
   public int BladeStanceReason = -99999;
   public long BladeStanceTill = -99999L;
   public int BladeStanceLevel = -99999;
   public int BladeStanceFromID = -99999;
   public int DebuffActiveSkillHPConValue = -99999;
   public int DebuffActiveSkillHPConReason = -99999;
   public long DebuffActiveSkillHPConTill = -99999L;
   public int DebuffActiveSkillHPConLevel = -99999;
   public int DebuffActiveSkillHPConFromID = -99999;
   public int DebuffIncHPValue = -99999;
   public int DebuffIncHPReason = -99999;
   public long DebuffIncHPTill = -99999L;
   public int DebuffIncHPLevel = -99999;
   public int DebuffIncHPFromID = -99999;
   public int BowMasterMortalBlowValue = -99999;
   public int BowMasterMortalBlowReason = -99999;
   public long BowMasterMortalBlowTill = -99999L;
   public int BowMasterMortalBlowLevel = -99999;
   public int BowMasterMortalBlowFromID = -99999;
   public int AngelicBursterSoulResonanceValue = -99999;
   public int AngelicBursterSoulResonanceReason = -99999;
   public long AngelicBursterSoulResonanceTill = -99999L;
   public int AngelicBursterSoulResonanceLevel = -99999;
   public int AngelicBursterSoulResonanceFromID = -99999;
   public int FeverValue = -99999;
   public int FeverReason = -99999;
   public long FeverTill = -99999L;
   public int FeverLevel = -99999;
   public int FeverFromID = -99999;
   public int IgnisRoreValue = -99999;
   public int IgnisRoreReason = -99999;
   public long IgnisRoreTill = -99999L;
   public int IgnisRoreLevel = -99999;
   public int IgnisRoreFromID = -99999;
   public int TeleportMasteryRangeValue = -99999;
   public int TeleportMasteryRangeReason = -99999;
   public long TeleportMasteryRangeTill = -99999L;
   public int TeleportMasteryRangeLevel = -99999;
   public int TeleportMasteryRangeFromID = -99999;
   public int FixCooltimeValue = -99999;
   public int FixCooltimeReason = -99999;
   public long FixCooltimeTill = -99999L;
   public int FixCooltimeLevel = -99999;
   public int FixCooltimeFromID = -99999;
   public int IncMobRateDummyValue = -99999;
   public int IncMobRateDummyReason = -99999;
   public long IncMobRateDummyTill = -99999L;
   public int IncMobRateDummyLevel = -99999;
   public int IncMobRateDummyFromID = -99999;
   public int AdrenalinBoostValue = -99999;
   public int AdrenalinBoostReason = -99999;
   public long AdrenalinBoostTill = -99999L;
   public int AdrenalinBoostLevel = -99999;
   public int AdrenalinBoostFromID = -99999;
   public int AranSmashSwingValue = -99999;
   public int AranSmashSwingReason = -99999;
   public long AranSmashSwingTill = -99999L;
   public int AranSmashSwingLevel = -99999;
   public int AranSmashSwingFromID = -99999;
   public int AranDrainValue = -99999;
   public int AranDrainReason = -99999;
   public long AranDrainTill = -99999L;
   public int AranDrainLevel = -99999;
   public int AranDrainFromID = -99999;
   public int AranHuntersTargetingChargeValue = -99999;
   public int AranHuntersTargetingChargeReason = -99999;
   public long AranHuntersTargetingChargeTill = -99999L;
   public int AranHuntersTargetingChargeLevel = -99999;
   public int AranHuntersTargetingChargeFromID = -99999;
   public int HiddenHyperLinkMaximizationValue = -99999;
   public int HiddenHyperLinkMaximizationReason = -99999;
   public long HiddenHyperLinkMaximizationTill = -99999L;
   public int HiddenHyperLinkMaximizationLevel = -99999;
   public int HiddenHyperLinkMaximizationFromID = -99999;
   public int RWCylinderValue = -99999;
   public int RWCylinderReason = -99999;
   public long RWCylinderTill = -99999L;
   public int RWCylinderLevel = -99999;
   public int RWCylinderFromID = -99999;
   public int RWCombinationValue = -99999;
   public int RWCombinationReason = -99999;
   public long RWCombinationTill = -99999L;
   public int RWCombinationLevel = -99999;
   public int RWCombinationFromID = -99999;
   public int RWUnkValue = -99999;
   public int RWUnkReason = -99999;
   public long RWUnkTill = -99999L;
   public int RWUnkLevel = -99999;
   public int RWUnkFromID = -99999;
   public int RWMagnumBlowValue = -99999;
   public int RWMagnumBlowReason = -99999;
   public long RWMagnumBlowTill = -99999L;
   public int RWMagnumBlowLevel = -99999;
   public int RWMagnumBlowFromID = -99999;
   public int RWBarrierValue = -99999;
   public int RWBarrierReason = -99999;
   public long RWBarrierTill = -99999L;
   public int RWBarrierLevel = -99999;
   public int RWBarrierFromID = -99999;
   public int RWBarrierHealValue = -99999;
   public int RWBarrierHealReason = -99999;
   public long RWBarrierHealTill = -99999L;
   public int RWBarrierHealLevel = -99999;
   public int RWBarrierHealFromID = -99999;
   public int RWMaximizeCannonValue = -99999;
   public int RWMaximizeCannonReason = -99999;
   public long RWMaximizeCannonTill = -99999L;
   public int RWMaximizeCannonLevel = -99999;
   public int RWMaximizeCannonFromID = -99999;
   public int RWOverHeatValue = -99999;
   public int RWOverHeatReason = -99999;
   public long RWOverHeatTill = -99999L;
   public int RWOverHeatLevel = -99999;
   public int RWOverHeatFromID = -99999;
   public int UsingScouterValue = -99999;
   public int UsingScouterReason = -99999;
   public long UsingScouterTill = -99999L;
   public int UsingScouterLevel = -99999;
   public int UsingScouterFromID = -99999;
   public int RWMovingEvarValue = -99999;
   public int RWMovingEvarReason = -99999;
   public long RWMovingEvarTill = -99999L;
   public int RWMovingEvarLevel = -99999;
   public int RWMovingEvarFromID = -99999;
   public int StigmaValue = -99999;
   public int StigmaReason = -99999;
   public long StigmaTill = -99999L;
   public int StigmaLevel = -99999;
   public int StigmaFromID = -99999;
   public int InstallMahaValue = -99999;
   public int InstallMahaReason = -99999;
   public long InstallMahaTill = -99999L;
   public int InstallMahaLevel = -99999;
   public int InstallMahaFromID = -99999;
   public int HeavensDoorBlockedValue = -99999;
   public int HeavensDoorBlockedReason = -99999;
   public long HeavensDoorBlockedTill = -99999L;
   public int HeavensDoorBlockedLevel = -99999;
   public int HeavensDoorBlockedFromID = -99999;
   public int SkillAffectBlockedValue = -99999;
   public int SkillAffectBlockedReason = -99999;
   public long SkillAffectBlockedTill = -99999L;
   public int SkillAffectBlockedLevel = -99999;
   public int SkillAffectBlockedFromID = -99999;
   public int RuneBlockedValue = -99999;
   public int RuneBlockedReason = -99999;
   public long RuneBlockedTill = -99999L;
   public int RuneBlockedLevel = -99999;
   public int RuneBlockedFromID = -99999;
   public int PinPointRocketValue = -99999;
   public int PinPointRocketReason = -99999;
   public long PinPointRocketTill = -99999L;
   public int PinPointRocketLevel = -99999;
   public int PinPointRocketFromID = -99999;
   public int TransformValue = -99999;
   public int TransformReason = -99999;
   public long TransformTill = -99999L;
   public int TransformLevel = -99999;
   public int TransformFromID = -99999;
   public int EnergyBurstValue = -99999;
   public int EnergyBurstReason = -99999;
   public long EnergyBurstTill = -99999L;
   public int EnergyBurstLevel = -99999;
   public int EnergyBurstFromID = -99999;
   public int LightningCascadeValue = -99999;
   public int LightningCascadeReason = -99999;
   public long LightningCascadeTill = -99999L;
   public int LightningCascadeLevel = -99999;
   public int LightningCascadeFromID = -99999;
   public int BulletPartyValue = -99999;
   public int BulletPartyReason = -99999;
   public long BulletPartyTill = -99999L;
   public int BulletPartyLevel = -99999;
   public int BulletPartyFromID = -99999;
   public int LoadedDiceValue = -99999;
   public int LoadedDiceReason = -99999;
   public long LoadedDiceTill = -99999L;
   public int LoadedDiceLevel = -99999;
   public int LoadedDiceFromID = -99999;
   public int PrayValue = -99999;
   public int PrayReason = -99999;
   public long PrayTill = -99999L;
   public int PrayLevel = -99999;
   public int PrayFromID = -99999;
   public int ChainArtsFuryValue = -99999;
   public int ChainArtsFuryReason = -99999;
   public long ChainArtsFuryTill = -99999L;
   public int ChainArtsFuryLevel = -99999;
   public int ChainArtsFuryFromID = -99999;
   public int ReduceFixDamRValue = -99999;
   public int ReduceFixDamRReason = -99999;
   public long ReduceFixDamRTill = -99999L;
   public int ReduceFixDamRLevel = -99999;
   public int ReduceFixDamRFromID = -99999;
   public int PinkBeanYoyoDamageUpValue = -99999;
   public int PinkBeanYoyoDamageUpReason = -99999;
   public long PinkBeanYoyoDamageUpTill = -99999L;
   public int PinkBeanYoyoDamageUpLevel = -99999;
   public int PinkBeanYoyoDamageUpFromID = -99999;
   public int AuraWeaponValue = -99999;
   public int AuraWeaponReason = -99999;
   public long AuraWeaponTill = -99999L;
   public int AuraWeaponLevel = -99999;
   public int AuraWeaponFromID = -99999;
   public int OverloadManaValue = -99999;
   public int OverloadManaReason = -99999;
   public long OverloadManaTill = -99999L;
   public int OverloadManaLevel = -99999;
   public int OverloadManaFromID = -99999;
   public int RhoAiasValue = -99999;
   public int RhoAiasReason = -99999;
   public long RhoAiasTill = -99999L;
   public int RhoAiasLevel = -99999;
   public int RhoAiasFromID = -99999;
   public int PsychicTornadoValue = -99999;
   public int PsychicTornadoReason = -99999;
   public long PsychicTornadoTill = -99999L;
   public int PsychicTornadoLevel = -99999;
   public int PsychicTornadoFromID = -99999;
   public int SpreadThrowValue = -99999;
   public int SpreadThrowReason = -99999;
   public long SpreadThrowTill = -99999L;
   public int SpreadThrowLevel = -99999;
   public int SpreadThrowFromID = -99999;
   public int HowlingGaleValue = -99999;
   public int HowlingGaleReason = -99999;
   public long HowlingGaleTill = -99999L;
   public int HowlingGaleLevel = -99999;
   public int HowlingGaleFromID = -99999;
   public int AutoChargeStackValue = -99999;
   public int AutoChargeStackReason = -99999;
   public long AutoChargeStackTill = -99999L;
   public int AutoChargeStackLevel = -99999;
   public int AutoChargeStackFromID = -99999;
   public int ShadowAssaultValue = -99999;
   public int ShadowAssaultReason = -99999;
   public long ShadowAssaultTill = -99999L;
   public int ShadowAssaultLevel = -99999;
   public int ShadowAssaultFromID = -99999;
   public int MultipleOptionValue = -99999;
   public int MultipleOptionReason = -99999;
   public long MultipleOptionTill = -99999L;
   public int MultipleOptionLevel = -99999;
   public int MultipleOptionFromID = -99999;
   public int Unk37Value = -99999;
   public int Unk37Reason = -99999;
   public long Unk37Till = -99999L;
   public int Unk37Level = -99999;
   public int Unk37FromID = -99999;
   public int BlitzShieldValue = -99999;
   public int BlitzShieldReason = -99999;
   public long BlitzShieldTill = -99999L;
   public int BlitzShieldLevel = -99999;
   public int BlitzShieldFromID = -99999;
   public int SplitArrowValue = -99999;
   public int SplitArrowReason = -99999;
   public long SplitArrowTill = -99999L;
   public int SplitArrowLevel = -99999;
   public int SplitArrowFromID = -99999;
   public int FreudsProtectionValue = -99999;
   public int FreudsProtectionReason = -99999;
   public long FreudsProtectionTill = -99999L;
   public int FreudsProtectionLevel = -99999;
   public int FreudsProtectionFromID = -99999;
   public int OverloadModeValue = -99999;
   public int OverloadModeReason = -99999;
   public long OverloadModeTill = -99999L;
   public int OverloadModeLevel = -99999;
   public int OverloadModeFromID = -99999;
   public int SpotLightValue = -99999;
   public int SpotLightReason = -99999;
   public long SpotLightTill = -99999L;
   public int SpotLightLevel = -99999;
   public int SpotLightFromID = -99999;
   public int Unk39Value = -99999;
   public int Unk39Reason = -99999;
   public long Unk39Till = -99999L;
   public int Unk39Level = -99999;
   public int Unk39FromID = -99999;
   public int WeaponVarietyValue = -99999;
   public int WeaponVarietyReason = -99999;
   public long WeaponVarietyTill = -99999L;
   public int WeaponVarietyLevel = -99999;
   public int WeaponVarietyFromID = -99999;
   public int GloryWingValue = -99999;
   public int GloryWingReason = -99999;
   public long GloryWingTill = -99999L;
   public int GloryWingLevel = -99999;
   public int GloryWingFromID = -99999;
   public int EviscerateDebuffValue = -99999;
   public int EviscerateDebuffReason = -99999;
   public long EviscerateDebuffTill = -99999L;
   public int EviscerateDebuffLevel = -99999;
   public int EviscerateDebuffFromID = -99999;
   public int OverDriveValue = -99999;
   public int OverDriveReason = -99999;
   public long OverDriveTill = -99999L;
   public int OverDriveLevel = -99999;
   public int OverDriveFromID = -99999;
   public int EtherealFormValue = -99999;
   public int EtherealFormReason = -99999;
   public long EtherealFormTill = -99999L;
   public int EtherealFormLevel = -99999;
   public int EtherealFormFromID = -99999;
   public int ReadyToDieValue = -99999;
   public int ReadyToDieReason = -99999;
   public long ReadyToDieTill = -99999L;
   public int ReadyToDieLevel = -99999;
   public int ReadyToDieFromID = -99999;
   public int CriticalReinforceValue = -99999;
   public int CriticalReinforceReason = -99999;
   public long CriticalReinforceTill = -99999L;
   public int CriticalReinforceLevel = -99999;
   public int CriticalReinforceFromID = -99999;
   public int CurseOfCreationValue = -99999;
   public int CurseOfCreationReason = -99999;
   public long CurseOfCreationTill = -99999L;
   public int CurseOfCreationLevel = -99999;
   public int CurseOfCreationFromID = -99999;
   public int CurseOfDestructionValue = -99999;
   public int CurseOfDestructionReason = -99999;
   public long CurseOfDestructionTill = -99999L;
   public int CurseOfDestructionLevel = -99999;
   public int CurseOfDestructionFromID = -99999;
   public int BlackMageAttributesValue = -99999;
   public int BlackMageAttributesReason = -99999;
   public long BlackMageAttributesTill = -99999L;
   public int BlackMageAttributesLevel = -99999;
   public int BlackMageAttributesFromID = -99999;
   public int StackDamRValue = -99999;
   public int StackDamRReason = -99999;
   public long StackDamRTill = -99999L;
   public int StackDamRLevel = -99999;
   public int StackDamRFromID = -99999;
   public int TimeCurseValue = -99999;
   public int TimeCurseReason = -99999;
   public long TimeCurseTill = -99999L;
   public int TimeCurseLevel = -99999;
   public int TimeCurseFromID = -99999;
   public int TimeCurseX = 0;
   public int TimeTorrentValue = -99999;
   public int TimeTorrentReason = -99999;
   public long TimeTorrentTill = -99999L;
   public int TimeTorrentLevel = -99999;
   public int TimeTorrentFromID = -99999;
   public int HarmonyLinkValue = -99999;
   public int HarmonyLinkReason = -99999;
   public long HarmonyLinkTill = -99999L;
   public int HarmonyLinkLevel = -99999;
   public int HarmonyLinkFromID = -99999;
   public int FastCharge2Value = -99999;
   public int FastCharge2Reason = -99999;
   public long FastCharge2Till = -99999L;
   public int FastCharge2Level = -99999;
   public int FastCharge2FromID = -99999;
   public int FullBlessMarkValue = -99999;
   public int FullBlessMarkReason = -99999;
   public long FullBlessMarkTill = -99999L;
   public int FullBlessMarkLevel = -99999;
   public int FullBlessMarkFromID = -99999;
   public int CrystalChargeMaxValue = -99999;
   public int CrystalChargeMaxReason = -99999;
   public long CrystalChargeMaxTill = -99999L;
   public int CrystalChargeMaxLevel = -99999;
   public int CrystalChargeMaxFromID = -99999;
   public int CrystalDeusValue = -99999;
   public int CrystalDeusReason = -99999;
   public long CrystalDeusTill = -99999L;
   public int CrystalDeusLevel = -99999;
   public int CrystalDeusFromID = -99999;
   public int Unk55Value = -99999;
   public int Unk55Reason = -99999;
   public long Unk55Till = -99999L;
   public int Unk55Level = -99999;
   public int Unk55FromID = -99999;
   public int BattlePvP_Ryude_SwordValue = -99999;
   public int BattlePvP_Ryude_SwordReason = -99999;
   public long BattlePvP_Ryude_SwordTill = -99999L;
   public int BattlePvP_Ryude_SwordLevel = -99999;
   public int BattlePvP_Ryude_SwordFromID = -99999;
   public int BattlePvP_Alicia_BlessValue = -99999;
   public int BattlePvP_Alicia_BlessReason = -99999;
   public long BattlePvP_Alicia_BlessTill = -99999L;
   public int BattlePvP_Alicia_BlessLevel = -99999;
   public int BattlePvP_Alicia_BlessFromID = -99999;
   public int Unk58Value = -99999;
   public int Unk58Reason = -99999;
   public long Unk58Till = -99999L;
   public int Unk58Level = -99999;
   public int Unk58FromID = -99999;
   public int Unk59Value = -99999;
   public int Unk59Reason = -99999;
   public long Unk59Till = -99999L;
   public int Unk59Level = -99999;
   public int Unk59FromID = -99999;
   public int SpecterStateValue = -99999;
   public int SpecterStateReason = -99999;
   public long SpecterStateTill = -99999L;
   public int SpecterStateLevel = -99999;
   public int SpecterStateFromID = -99999;
   public int SpectralFormValue = -99999;
   public int SpectralFormReason = -99999;
   public long SpectralFormTill = -99999L;
   public int SpectralFormLevel = -99999;
   public int SpectralFormFromID = -99999;
   public int PlainSpellBulletsValue = -99999;
   public int PlainSpellBulletsReason = -99999;
   public long PlainSpellBulletsTill = -99999L;
   public int PlainSpellBulletsLevel = -99999;
   public int PlainSpellBulletsFromID = -99999;
   public int ScarletSpellBulletsValue = -99999;
   public int ScarletSpellBulletsReason = -99999;
   public long ScarletSpellBulletsTill = -99999L;
   public int ScarletSpellBulletsLevel = -99999;
   public int ScarletSpellBulletsFromID = -99999;
   public int GustSpellBulletsValue = -99999;
   public int GustSpellBulletsReason = -99999;
   public long GustSpellBulletsTill = -99999L;
   public int GustSpellBulletsLevel = -99999;
   public int GustSpellBulletsFromID = -99999;
   public int AbysSpellBulletsValue = -99999;
   public int AbysSpellBulletsReason = -99999;
   public long AbysSpellBulletsTill = -99999L;
   public int AbysSpellBulletsLevel = -99999;
   public int AbysSpellBulletsFromID = -99999;
   public int ImpendingDeathValue = -99999;
   public int ImpendingDeathReason = -99999;
   public long ImpendingDeathTill = -99999L;
   public int ImpendingDeathLevel = -99999;
   public int ImpendingDeathFromID = -99999;
   public int BattleHolicValue = -99999;
   public int BattleHolicReason = -99999;
   public long BattleHolicTill = -99999L;
   public int BattleHolicLevel = -99999;
   public int BattleHolicFromID = -99999;
   public int DivineWrathValue = -99999;
   public int DivineWrathReason = -99999;
   public long DivineWrathTill = -99999L;
   public int DivineWrathLevel = -99999;
   public int DivineWrathFromID = -99999;
   public int InfinitySpellValue = -99999;
   public int InfinitySpellReason = -99999;
   public long InfinitySpellTill = -99999L;
   public int InfinitySpellLevel = -99999;
   public int InfinitySpellFromID = -99999;
   public int MagicCircuitFullDriveValue = -99999;
   public int MagicCircuitFullDriveReason = -99999;
   public long MagicCircuitFullDriveTill = -99999L;
   public int MagicCircuitFullDriveLevel = -99999;
   public int MagicCircuitFullDriveFromID = -99999;
   public int SolusValue = -99999;
   public int SolusReason = -99999;
   public long SolusTill = -99999L;
   public int SolusLevel = -99999;
   public int SolusFromID = -99999;
   public int MemoryOfRootValue = -99999;
   public int MemoryOfRootReason = -99999;
   public long MemoryOfRootTill = -99999L;
   public int MemoryOfRootLevel = -99999;
   public int MemoryOfRootFromID = -99999;
   public int DiscoveryWeatherValue = -99999;
   public int DiscoveryWeatherReason = -99999;
   public long DiscoveryWeatherTill = -99999L;
   public int DiscoveryWeatherLevel = -99999;
   public int DiscoveryWeatherFromID = -99999;
   public int WillPoisonValue = -99999;
   public int WillPoisonReason = -99999;
   public long WillPoisonTill = -99999L;
   public int WillPoisonLevel = -99999;
   public int WillPoisonFromID = -99999;
   public int Unk75Value = -99999;
   public int Unk75Reason = -99999;
   public long Unk75Till = -99999L;
   public int Unk75Level = -99999;
   public int Unk75FromID = -99999;
   public int Unk76Value = -99999;
   public int Unk76Reason = -99999;
   public long Unk76Till = -99999L;
   public int Unk76Level = -99999;
   public int Unk76FromID = -99999;
   public int HolyMagicShellBlockedValue = -99999;
   public int HolyMagicShellBlockedReason = -99999;
   public long HolyMagicShellBlockedTill = -99999L;
   public int HolyMagicShellBlockedLevel = -99999;
   public int HolyMagicShellBlockedFromID = -99999;
   public int LightningSpearValue = -99999;
   public int LightningSpearReason = -99999;
   public long LightningSpearTill = -99999L;
   public int LightningSpearLevel = -99999;
   public int LightningSpearFromID = -99999;
   public int ComboInstinctValue = -99999;
   public int ComboInstinctReason = -99999;
   public long ComboInstinctTill = -99999L;
   public int ComboInstinctLevel = -99999;
   public int ComboInstinctFromID = -99999;
   public int StormGuardValue = -99999;
   public int StormGuardReason = -99999;
   public long StormGuardTill = -99999L;
   public int StormGuardLevel = -99999;
   public int StormGuardFromID = -99999;
   public int InfinityFlameCircleValue = -99999;
   public int InfinityFlameCircleReason = -99999;
   public long InfinityFlameCircleTill = -99999L;
   public int InfinityFlameCircleLevel = -99999;
   public int InfinityFlameCircleFromID = -99999;
   public int SwordOfSoulLightValue = -99999;
   public int SwordOfSoulLightReason = -99999;
   public long SwordOfSoulLightTill = -99999L;
   public int SwordOfSoulLightLevel = -99999;
   public int SwordOfSoulLightFromID = -99999;
   public int MarkOfPhantomValue = -99999;
   public int MarkOfPhantomReason = -99999;
   public long MarkOfPhantomTill = -99999L;
   public int MarkOfPhantomLevel = -99999;
   public int MarkOfPhantomFromID = -99999;
   public int MarkOfPhantomDebuffValue = -99999;
   public int MarkOfPhantomDebuffReason = -99999;
   public long MarkOfPhantomDebuffTill = -99999L;
   public int MarkOfPhantomDebuffLevel = -99999;
   public int MarkOfPhantomDebuffFromID = -99999;
   public int Unk90Value = -99999;
   public int Unk90Reason = -99999;
   public long Unk90Till = -99999L;
   public int Unk90Level = -99999;
   public int Unk90FromID = -99999;
   public int Unk91Value = -99999;
   public int Unk91Reason = -99999;
   public long Unk91Till = -99999L;
   public int Unk91Level = -99999;
   public int Unk91FromID = -99999;
   public int Unk92Value = -99999;
   public int Unk92Reason = -99999;
   public long Unk92Till = -99999L;
   public int Unk92Level = -99999;
   public int Unk92FromID = -99999;
   public int Unk93Value = -99999;
   public int Unk93Reason = -99999;
   public long Unk93Till = -99999L;
   public int Unk93Level = -99999;
   public int Unk93FromID = -99999;
   public int FireBirdSupportActiveValue = -99999;
   public int FireBirdSupportActiveReason = -99999;
   public long FireBirdSupportActiveTill = -99999L;
   public int FireBirdSupportActiveLevel = -99999;
   public int FireBirdSupportActiveFromID = -99999;
   public int MagicBellValue = -99999;
   public int MagicBellReason = -99999;
   public long MagicBellTill = -99999L;
   public int MagicBellLevel = -99999;
   public int MagicBellFromID = -99999;
   public int MagicBellX = 0;
   public int BlackMageCursePmdReduceValue = -99999;
   public int BlackMageCursePmdReduceReason = -99999;
   public long BlackMageCursePmdReduceTill = -99999L;
   public int BlackMageCursePmdReduceLevel = -99999;
   public int BlackMageCursePmdReduceFromID = -99999;
   public int BlackMageCurseForbidPortionValue = -99999;
   public int BlackMageCurseForbidPortionReason = -99999;
   public long BlackMageCurseForbidPortionTill = -99999L;
   public int BlackMageCurseForbidPortionLevel = -99999;
   public int BlackMageCurseForbidPortionFromID = -99999;
   public int BlackMageCurse3Value = -99999;
   public int BlackMageCurse3Reason = -99999;
   public long BlackMageCurse3Till = -99999L;
   public int BlackMageCurse3Level = -99999;
   public int BlackMageCurse3FromID = -99999;
   public int YaldabaothValue = -99999;
   public int YaldabaothReason = -99999;
   public long YaldabaothTill = -99999L;
   public int YaldabaothLevel = -99999;
   public int YaldabaothFromID = -99999;
   public int AionValue = -99999;
   public int AionReason = -99999;
   public long AionTill = -99999L;
   public int AionLevel = -99999;
   public int AionFromID = -99999;
   public int Unk97Value = -99999;
   public int Unk97Reason = -99999;
   public long Unk97Till = -99999L;
   public int Unk97Level = -99999;
   public int Unk97FromID = -99999;
   public int BattlePvP_LangE_ClawValue = -99999;
   public int BattlePvP_LangE_ClawReason = -99999;
   public long BattlePvP_LangE_ClawTill = -99999L;
   public int BattlePvP_LangE_ClawLevel = -99999;
   public int BattlePvP_LangE_ClawFromID = -99999;
   public int BattlePvP_Mike_ProtectionValue = -99999;
   public int BattlePvP_Mike_ProtectionReason = -99999;
   public long BattlePvP_Mike_ProtectionTill = -99999L;
   public int BattlePvP_Mike_ProtectionLevel = -99999;
   public int BattlePvP_Mike_ProtectionFromID = -99999;
   public int SiphonVitalityShieldValue = -99999;
   public int SiphonVitalityShieldReason = -99999;
   public long SiphonVitalityShieldTill = -99999L;
   public int SiphonVitalityShieldLevel = -99999;
   public int SiphonVitalityShieldFromID = -99999;
   public int BloodyFistValue = -99999;
   public int BloodyFistReason = -99999;
   public long BloodyFistTill = -99999L;
   public int BloodyFistLevel = -99999;
   public int BloodyFistFromID = -99999;
   public int AncientGuardiansValue = -99999;
   public int AncientGuardiansReason = -99999;
   public long AncientGuardiansTill = -99999L;
   public int AncientGuardiansLevel = -99999;
   public int AncientGuardiansFromID = -99999;
   public int BattlePvP_Wonky_AttackValue = -99999;
   public int BattlePvP_Wonky_AttackReason = -99999;
   public long BattlePvP_Wonky_AttackTill = -99999L;
   public int BattlePvP_Wonky_AttackLevel = -99999;
   public int BattlePvP_Wonky_AttackFromID = -99999;
   public int BattlePvP_Wonky_HealValue = -99999;
   public int BattlePvP_Wonky_HealReason = -99999;
   public long BattlePvP_Wonky_HealTill = -99999L;
   public int BattlePvP_Wonky_HealLevel = -99999;
   public int BattlePvP_Wonky_HealFromID = -99999;
   public int BattlePvP_Wonky_ProtectionValue = -99999;
   public int BattlePvP_Wonky_ProtectionReason = -99999;
   public long BattlePvP_Wonky_ProtectionTill = -99999L;
   public int BattlePvP_Wonky_ProtectionLevel = -99999;
   public int BattlePvP_Wonky_ProtectionFromID = -99999;
   public int ProtectionOfAncientWarriorValue = -99999;
   public int ProtectionOfAncientWarriorReason = -99999;
   public long ProtectionOfAncientWarriorTill = -99999L;
   public int ProtectionOfAncientWarriorLevel = -99999;
   public int ProtectionOfAncientWarriorFromID = -99999;
   public int NEW_351_02Value = -99999;
   public int NEW_351_02Reason = -99999;
   public long NEW_351_02Till = -99999L;
   public int NEW_351_02Level = -99999;
   public int NNEW_351_02FromID = -99999;
   public int NEW_311_01Value = -99999;
   public int NEW_311_01Reason = -99999;
   public long NEW_311_01Till = -99999L;
   public int NEW_311_01Level = -99999;
   public int NEW_311_01FromID = -99999;
   public int MultiSoccerAddBallValue = -99999;
   public int MultiSoccerAddBallReason = -99999;
   public long MultiSoccerAddBallTill = -99999L;
   public int MultiSoccerAddBallLevel = -99999;
   public int MultiSoccerAddBallFromID = -99999;
   public int NEW_320_01Value = -99999;
   public int NEW_320_01Reason = -99999;
   public long NEW_320_01Till = -99999L;
   public int NEW_320_01Level = -99999;
   public int NEW_320_01FromID = -99999;
   public int EmpressBlessValue = -99999;
   public int EmpressBlessReason = -99999;
   public long EmpressBlessTill = -99999L;
   public int EmpressBlessLevel = -99999;
   public int EmpressBlessFromID = -99999;
   public int EmpressBlessRemainNoCool = 0;
   public int EmpressBlessX = 0;
   public int PinkBeanMatryoshkaValue = -99999;
   public int PinkBeanMatryoshkaReason = -99999;
   public long PinkBeanMatryoshkaTill = -99999L;
   public int PinkBeanMatryoshkaLevel = -99999;
   public int PinkBeanMatryoshkaFromID = -99999;
   public int NEW_315_01Value = -99999;
   public int NEW_315_01Reason = -99999;
   public long NEW_315_01Till = -99999L;
   public int NEW_315_01Level = -99999;
   public int NEW_315_01FromID = -99999;
   public int NEW_315_02Value = -99999;
   public int NEW_315_02Reason = -99999;
   public long NEW_315_02Till = -99999L;
   public int NEW_315_02Level = -99999;
   public int NEW_315_02FromID = -99999;
   public int NEW_320_02Value = -99999;
   public int NEW_320_02Reason = -99999;
   public long NEW_320_02Till = -99999L;
   public int NEW_320_02Level = -99999;
   public int NEW_320_02FromID = -99999;
   public int NEW_320_03Value = -99999;
   public int NEW_320_03Reason = -99999;
   public long NEW_320_03Till = -99999L;
   public int NEW_320_03Level = -99999;
   public int NEW_320_03FromID = -99999;
   public int NEW_320_04Value = -99999;
   public int NEW_320_04Reason = -99999;
   public long NEW_320_04Till = -99999L;
   public int NEW_320_04Level = -99999;
   public int NEW_320_04FromID = -99999;
   public int NEW_320_05Value = -99999;
   public int NEW_320_05Reason = -99999;
   public long NEW_320_05Till = -99999L;
   public int NEW_320_05Level = -99999;
   public int NEW_320_05FromID = -99999;
   public int HoyoungAttributesValue = -99999;
   public int HoyoungAttributesReason = -99999;
   public long HoyoungAttributesTill = -99999L;
   public int HoyoungAttributesLevel = -99999;
   public int HoyoungAttributesFromID = -99999;
   public int CharmPowerValue = -99999;
   public int CharmPowerReason = -99999;
   public long CharmPowerTill = -99999L;
   public int CharmPowerLevel = -99999;
   public int CharmPowerFromID = -99999;
   public int CloneAttackValue = -99999;
   public int CloneAttackReason = -99999;
   public long CloneAttackTill = -99999L;
   public int CloneAttackLevel = -99999;
   public int CloneAttackFromID = -99999;
   public int HyperCloneRampageValue = -99999;
   public int HyperCloneRampageReason = -99999;
   public long HyperCloneRampageTill = -99999L;
   public int HyperCloneRampageLevel = -99999;
   public int HyperCloneRampageFromID = -99999;
   public int ButterflyDreamValue = -99999;
   public int ButterflyDreamReason = -99999;
   public long ButterflyDreamTill = -99999L;
   public int ButterflyDreamLevel = -99999;
   public int ButterflyDreamFromID = -99999;
   public int MastersElixirValue = -99999;
   public int MastersElixirReason = -99999;
   public long MastersElixirTill = -99999L;
   public int MastersElixirLevel = -99999;
   public int MastersElixirFromID = -99999;
   public int WrathOfGodsValue = -99999;
   public int WrathOfGodsReason = -99999;
   public long WrathOfGodsTill = -99999L;
   public int WrathOfGodsLevel = -99999;
   public int WrathOfGodsFromID = -99999;
   public int EmpericalKnowledgeValue = -99999;
   public int EmpericalKnowledgeReason = -99999;
   public long EmpericalKnowledgeTill = -99999L;
   public int EmpericalKnowledgeLevel = -99999;
   public int EmpericalKnowledgeFromID = -99999;
   public int HoyoungUpdateSkillValue = -99999;
   public int HoyoungUpdateSkillReason = -99999;
   public long HoyoungUpdateSkillTill = -99999L;
   public int HoyoungUpdateSkillLevel = -99999;
   public int HoyoungUpdateSkillFromID = -99999;
   public int AncientProtectionMagicValue = -99999;
   public int AncientProtectionMagicReason = -99999;
   public long AncientProtectionMagicTill = -99999L;
   public int AncientProtectionMagicLevel = -99999;
   public int AncientProtectionMagicFromID = -99999;
   public int GraffitiValue = -99999;
   public int GraffitiReason = -99999;
   public long GraffitiTill = -99999L;
   public int GraffitiLevel = -99999;
   public int GraffitiFromID = -99999;
   public int KeyDownStartValue = -99999;
   public int KeyDownStartReason = -99999;
   public long KeyDownStartTill = -99999L;
   public int KeyDownStartLevel = -99999;
   public int KeyDownStartFromID = -99999;
   public int DracoSlasherValue = -99999;
   public int DracoSlasherReason = -99999;
   public long DracoSlasherTill = -99999L;
   public int DracoSlasherLevel = -99999;
   public int DracoSlasherFromID = -99999;
   public int EtherValue = -99999;
   public int EtherReason = -99999;
   public long EtherTill = -99999L;
   public int EtherLevel = -99999;
   public int EtherFromID = -99999;
   public int CreationValue = -99999;
   public int CreationReason = -99999;
   public long CreationTill = -99999L;
   public int CreationLevel = -99999;
   public int CreationFromID = -99999;
   public int AetherGuardValue = -99999;
   public int AetherGuardReason = -99999;
   public long AetherGuardTill = -99999L;
   public int AetherGuardLevel = -99999;
   public int AetherGuardFromID = -99999;
   public int WonderValue = -99999;
   public int WonderReason = -99999;
   public long WonderTill = -99999L;
   public int WonderLevel = -99999;
   public int WonderFromID = -99999;
   public int RestoreValue = -99999;
   public int RestoreReason = -99999;
   public long RestoreTill = -99999L;
   public int RestoreLevel = -99999;
   public int RestoreFromID = -99999;
   public int NobilityValue = -99999;
   public int NobilityReason = -99999;
   public long NobilityTill = -99999L;
   public int NobilityLevel = -99999;
   public int NobilityFromID = -99999;
   public int ResonanceValue = -99999;
   public int ResonanceReason = -99999;
   public long ResonanceTill = -99999L;
   public int ResonanceLevel = -99999;
   public int ResonanceFromID = -99999;
   public int NEW_1096_06Value = -99999;
   public int NEW_1096_06Reason = -99999;
   public long NEW_1096_06Till = -99999L;
   public int NEW_1096_06Level = -99999;
   public int NEW_1096_06FromID = -99999;
   public int RuneofPurificationValue = -99999;
   public int RuneofPurificationReason = -99999;
   public long RuneofPurificationTill = -99999L;
   public int RuneofPurificationLevel = -99999;
   public int RuneofPurificationFromID = -99999;
   public int RuneofPurificationGuage = 0;
   public int RuneOfIgnitionValue = -99999;
   public int RuneOfIgnitionReason = -99999;
   public long RuneOfIgnitionTill = -99999L;
   public int RuneOfIgnitionLevel = -99999;
   public int RuneOfIgnitionFromID = -99999;
   public int DuskDarknessValue = -99999;
   public int DuskDarknessReason = -99999;
   public long DuskDarknessTill = -99999L;
   public int DuskDarknessLevel = -99999;
   public int DuskDarknessFromID = -99999;
   public int YellowAuraValue = -99999;
   public int YellowAuraReason = -99999;
   public long YellowAuraTill = -99999L;
   public int YellowAuraLevel = -99999;
   public int YellowAuraFromID = -99999;
   public int YellowAuraTargetID = -99999;
   public int DrainAuraValue = -99999;
   public int DrainAuraReason = -99999;
   public long DrainAuraTill = -99999L;
   public int DrainAuraLevel = -99999;
   public int DrainAuraFromID = -99999;
   public int DrainAuraTargetID = -99999;
   public int BlueAuraValue = -99999;
   public int BlueAuraReason = -99999;
   public long BlueAuraTill = -99999L;
   public int BlueAuraLevel = -99999;
   public int BlueAuraFromID = -99999;
   public int BlueAuraTargetID = -99999;
   public int BlueAuraDispelCount = -99999;
   public int DarkAuraValue = -99999;
   public int DarkAuraReason = -99999;
   public long DarkAuraTill = -99999L;
   public int DarkAuraLevel = -99999;
   public int DarkAuraFromID = -99999;
   public int DarkAuraTargetID = -99999;
   public int DebuffAuraValue = -99999;
   public int DebuffAuraReason = -99999;
   public long DebuffAuraTill = -99999L;
   public int DebuffAuraLevel = -99999;
   public int DebuffAuraFromID = -99999;
   public int DebuffAuraTargetID = -99999;
   public int UnionAuraBlowValue = -99999;
   public int UnionAuraBlowReason = -99999;
   public long UnionAuraBlowTill = -99999L;
   public int UnionAuraBlowLevel = -99999;
   public int UnionAuraBlowFromID = -99999;
   public int UnionAuraBlowTargetID = -99999;
   public int IceAuraValue = -99999;
   public int IceAuraReason = -99999;
   public long IceAuraTill = -99999L;
   public int IceAuraLevel = -99999;
   public int IceAuraFromID = -99999;
   public int IceAuraTargetID = -99999;
   public int KnightsAuraValue = -99999;
   public int KnightsAuraReason = -99999;
   public long KnightsAuraTill = -99999L;
   public int KnightsAuraLevel = -99999;
   public int KnightsAuraFromID = -99999;
   public int KnightsAuraTargetID = -99999;
   public int ZeroAuraSTRValue = -99999;
   public int ZeroAuraSTRReason = -99999;
   public long ZeroAuraSTRTill = -99999L;
   public int ZeroAuraSTRLevel = -99999;
   public int ZeroAuraSTRFromID = -99999;
   public int ZeroAuraSTRTargetID = -99999;
   public int ZeroAuraSPDValue = -99999;
   public int ZeroAuraSPDReason = -99999;
   public long ZeroAuraSPDTill = -99999L;
   public int ZeroAuraSPDLevel = -99999;
   public int ZeroAuraSPDFromID = -99999;
   public int ZeroAuraSPDTargetID = -99999;
   public int AdventOfGodsValue = -99999;
   public int AdventOfGodsReason = -99999;
   public long AdventOfGodsTill = -99999L;
   public int AdventOfGodsLevel = -99999;
   public int AdventOfGodsFromID = -99999;
   public int RevenantValue = -99999;
   public int RevenantReason = -99999;
   public long RevenantTill = -99999L;
   public int RevenantLevel = -99999;
   public int RevenantFromID = -99999;
   public int RevenantRageValue = -99999;
   public int RevenantRageReason = -99999;
   public long RevenantRageTill = -99999L;
   public int RevenantRageLevel = -99999;
   public int RevenantRageFromID = -99999;
   public int AutoChargeStackOnOffValue = -99999;
   public int AutoChargeStackOnOffReason = -99999;
   public long AutoChargeStackOnOffTill = -99999L;
   public int AutoChargeStackOnOffLevel = -99999;
   public int AutoChargeStackOnOffFromID = -99999;
   public int BlizzardTempestValue = -99999;
   public int BlizzardTempestReason = -99999;
   public long BlizzardTempestTill = -99999L;
   public int BlizzardTempestLevel = -99999;
   public int BlizzardTempestFromID = -99999;
   public int PhotonRayValue = -99999;
   public int PhotonRayReason = -99999;
   public long PhotonRayTill = -99999L;
   public int PhotonRayLevel = -99999;
   public int PhotonRayFromID = -99999;
   public int AbyssalLightningValue = -99999;
   public int AbyssalLightningReason = -99999;
   public long AbyssalLightningTill = -99999L;
   public int AbyssalLightningLevel = -99999;
   public int AbyssalLightningFromID = -99999;
   public int SpearLightningChainValue = -99999;
   public int SpearLightningChainReason = -99999;
   public long SpearLightningChainTill = -99999L;
   public int SpearLightningChainLevel = -99999;
   public int SpearLightningChainFromID = -99999;
   public int RoyalKnightsValue = -99999;
   public int RoyalKnightsReason = -99999;
   public long RoyalKnightsTill = -99999L;
   public int RoyalKnightsLevel = -99999;
   public int RoyalKnightsFromID = -99999;
   public int SalamanderMischiefValue = -99999;
   public int SalamanderMischiefReason = -99999;
   public long SalamanderMischiefTill = -99999L;
   public int SalamanderMischiefLevel = -99999;
   public int SalamanderMischiefFromID = -99999;
   public int LawOfGravityValue = -99999;
   public int LawOfGravityReason = -99999;
   public long LawOfGravityTill = -99999L;
   public int LawOfGravityLevel = -99999;
   public int LawOfGravityFromID = -99999;
   public int RepeatingCrossbowCartridgeValue = -99999;
   public int RepeatingCrossbowCartridgeReason = -99999;
   public long RepeatingCrossbowCartridgeTill = -99999L;
   public int RepeatingCrossbowCartridgeLevel = -99999;
   public int RepeatingCrossbowCartridgeFromID = -99999;
   public int CrystalGateValue = -99999;
   public int CrystalGateReason = -99999;
   public long CrystalGateTill = -99999L;
   public int CrystalGateLevel = -99999;
   public int CrystalGateFromID = -99999;
   public int ThrowBlastingValue = -99999;
   public int ThrowBlastingReason = -99999;
   public long ThrowBlastingTill = -99999L;
   public int ThrowBlastingLevel = -99999;
   public int ThrowBlastingFromID = -99999;
   public int HeavenEarthHumanApparitionValue = -99999;
   public int HeavenEarthHumanApparitionReason = -99999;
   public long HeavenEarthHumanApparitionTill = -99999L;
   public int HeavenEarthHumanApparitionLevel = -99999;
   public int HeavenEarthHumanApparitionFromID = -99999;
   public int DarknessAuraValue = -99999;
   public int DarknessAuraReason = -99999;
   public long DarknessAuraTill = -99999L;
   public int DarknessAuraLevel = -99999;
   public int DarknessAuraFromID = -99999;
   public int WeaponVarietyFinaleValue = -99999;
   public int WeaponVarietyFinaleReason = -99999;
   public long WeaponVarietyFinaleTill = -99999L;
   public int WeaponVarietyFinaleLevel = -99999;
   public int WeaponVarietyFinaleFromID = -99999;
   public int LiberationOrbValue = -99999;
   public int LiberationOrbReason = -99999;
   public long LiberationOrbTill = -99999L;
   public int LiberationOrbLevel = -99999;
   public int LiberationOrbFromID = -99999;
   public int LiberationOrbActiveValue = -99999;
   public int LiberationOrbActiveReason = -99999;
   public long LiberationOrbActiveTill = -99999L;
   public int LiberationOrbActiveLevel = -99999;
   public int LiberationOrbActiveFromID = -99999;
   public int LiberationOrbActiveRemainCount = 0;
   public int EgoWeaponValue = -99999;
   public int LEgoWeaponReason = -99999;
   public long EgoWeaponTill = -99999L;
   public int EgoWeaponLevel = -99999;
   public int EgoWeaponFromID = -99999;
   public int EgoWeaponRemainCount = 0;
   public int UNK_1103_7Value = -99999;
   public int UNK_1103_7Reason = -99999;
   public long UNK_1103_7Till = -99999L;
   public int UNK_1103_7Level = -99999;
   public int UNK_1103_7FromID = -99999;
   public int RelicUnboundDischargeValue = -99999;
   public int RelicUnboundDischargeReason = -99999;
   public long RelicUnboundDischargeTill = -99999L;
   public int RelicUnboundDischargeLevel = -99999;
   public int RelicUnboundDischargeFromID = -99999;
   public int LightOfCourageValue = -99999;
   public int LightOfCourageReason = -99999;
   public long LightOfCourageTill = -99999L;
   public int LightOfCourageLevel = -99999;
   public int LightOfCourageFromID = -99999;
   public int UNK_1104_1Value = -99999;
   public int UNK_1104_1Reason = -99999;
   public long UNK_1104_1Till = -99999L;
   public int UNK_1104_1Level = -99999;
   public int UNK_1104_1FromID = -99999;
   public int AfterImageShockValue = -99999;
   public int AfterImageShockReason = -99999;
   public long AfterImageShockTill = -99999L;
   public int AfterImageShockLevel = -99999;
   public int AfterImageShockFromID = -99999;
   public int PossessionValue = -99999;
   public int PossessionReason = -99999;
   public long PossessionTill = -99999L;
   public int PossessionLevel = -99999;
   public int PossessionFromID = -99999;
   public int PossessionStateValue = -99999;
   public int PossessionStateReason = -99999;
   public long PossessionStateTill = -99999L;
   public int PossessionStateLevel = -99999;
   public int PossessionStateFromID = -99999;
   public int DeathBlessingValue = -99999;
   public int DeathBlessingReason = -99999;
   public long DeathBlessingTill = -99999L;
   public int DeathBlessingLevel = -99999;
   public int DeathBlessingFromID = -99999;
   public int ThanatosDescentValue = -99999;
   public int ThanatosDescentReason = -99999;
   public long ThanatosDescentTill = -99999L;
   public int ThanatosDescentLevel = -99999;
   public int ThanatosDescentFromID = -99999;
   public int RemainIncenseValue = -99999;
   public int RemainIncenseReason = -99999;
   public long RemainIncenseTill = -99999L;
   public int RemainIncenseLevel = -99999;
   public int RemainIncenseFromID = -99999;
   public int GripOfAgonyValue = -99999;
   public int GripOfAgonyReason = -99999;
   public long GripOfAgonyTill = -99999L;
   public int GripOfAgonyLevel = -99999;
   public int GripOfAgonyFromID = -99999;
   public int DragonFangValue = -99999;
   public int DragonFangReason = -99999;
   public long DragonFangTill = -99999L;
   public int DragonFangLevel = -99999;
   public int DragonFangFromID = -99999;
   public int SerenFreezeValue = -99999;
   public int SerenFreezeReason = -99999;
   public long SerenFreezeTill = -99999L;
   public int SerenFreezeLevel = -99999;
   public int SerenFreezeFromID = -99999;
   public int NotIncSerenGaugeValue = -99999;
   public int NotIncSerenGaugeReason = -99999;
   public long NotIncSerenGaugeTill = -99999L;
   public int NotIncSerenGaugeLevel = -99999;
   public int NotIncSerenGaugeFromID = -99999;
   public int SerenDebuffUnkValue = -99999;
   public int SerenDebuffUnkReason = -99999;
   public long SerenDebuffUnkTill = -99999L;
   public int SerenDebuffUnkLevel = -99999;
   public int SerenDebuffUnkFromID = -99999;
   public int PriorPreparationValue = -99999;
   public int PriorPreparationReason = -99999;
   public long PriorPreparationTill = -99999L;
   public int PriorPreparationLevel = -99999;
   public int PriorPreparationFromID = -99999;
   public int CrystalBallOfReneeValue = -99999;
   public int CrystalBallOfReneeReason = -99999;
   public long CrystalBallOfReneeTill = -99999L;
   public int CrystalBallOfReneeLevel = -99999;
   public int CrystalBallOfReneeFromID = -99999;
   public int AdminFixStatCritValue = -99999;
   public int AdminFixStatCritReason = -99999;
   public long AdminFixStatCritTill = -99999L;
   public int AdminFixStatCritLevel = -99999;
   public int AdminFixStatCritFromID = -99999;
   public int AdminFixStatCritDamValue = -99999;
   public int AdminFixStatCritDamReason = -99999;
   public long AdminFixStatCritDamTill = -99999L;
   public int AdminFixStatCritDamLevel = -99999;
   public int AdminFixStatCritDamFromID = -99999;
   public int IncarnationAuraValue = -99999;
   public int IncarnationAuraReason = -99999;
   public long IncarnationAuraTill = -99999L;
   public int IncarnationAuraLevel = -99999;
   public int IncarnationAuraFromID = -99999;
   public int IncarnationAuraTargetID = -99999;
   public int EnergyChargeValue = -99999;
   public int EnergyChargeReason = -99999;
   public long EnergyChargeTill = -99999L;
   public int EnergyChargeLevel = -99999;
   public int EnergyChargeFromID = -99999;
   public int DashJumpValue = -99999;
   public int DashJumpReason = -99999;
   public long DashJumpTill = -99999L;
   public int DashJumpLevel = -99999;
   public int DashJumpFromID = -99999;
   public int DashSpeedValue = -99999;
   public int DashSpeedReason = -99999;
   public long DashSpeedTill = -99999L;
   public int DashSpeedLevel = -99999;
   public int DashSpeedFromID = -99999;
   public int RideVehicleValue = -99999;
   public int RideVehicleReason = -99999;
   public long RideVehicleTill = -99999L;
   public int RideVehicleLevel = -99999;
   public int RideVehicleFromID = -99999;
   public int SpeedInfusionValue = -99999;
   public int SpeedInfusionReason = -99999;
   public long SpeedInfusionTill = -99999L;
   public int SpeedInfusionLevel = -99999;
   public int SpeedInfusionFromID = -99999;
   public int GuidedBulletValue = -99999;
   public int GuidedBulletReason = -99999;
   public long GuidedBulletTill = -99999L;
   public int GuidedBulletLevel = -99999;
   public int GuidedBulletFromID = -99999;
   public int UndeadValue = -99999;
   public int UndeadReason = -99999;
   public long UndeadTill = -99999L;
   public int UndeadLevel = -99999;
   public int UndeadFromID = -99999;
   public int RideVehicleExpireValue = -99999;
   public int RideVehicleExpireReason = -99999;
   public long RideVehicleExpireTill = -99999L;
   public int RideVehicleExpireLevel = -99999;
   public int RideVehicleExpireFromID = -99999;
   public int RelicChargeValue = -99999;
   public int RelicChargeReason = -99999;
   public long RelicChargeTill = -99999L;
   public int RelicChargeLevel = -99999;
   public int RelicChargeFromID = -99999;
   public int DivideValue = -99999;
   public int DivideReason = -99999;
   public long DivideTill = -99999L;
   public int DivideLevel = -99999;
   public int DivideFromID = -99999;
   public int AdrenalinBoostActivateValue = -99999;
   public int AdrenalinBoostActivateReason = -99999;
   public long AdrenalinBoostActivateTill = -99999L;
   public int AdrenalinBoostActivateLevel = -99999;
   public int AdrenalinBoostActivateFromID = -99999;
   public int HoyoungLastCheonJiInSkillSetValue = -99999;
   public int HoyoungLastCheonJiInSkillSetReason = -99999;
   public long HoyoungLastCheonJiInSkillSetTill = -99999L;
   public int HoyoungLastCheonJiInSkillSetLevel = -99999;
   public int HoyoungLastCheonJiInSkillSetFromID = -99999;
   public int HoyoungLastCheonJiInSkill = 0;
   public int Yeti_RageValue = -99999;
   public int Yeti_RageReason = -99999;
   public long Yeti_RageTill = -99999L;
   public int Yeti_RageLevel = -99999;
   public int Yeti_RageFromID = -99999;
   public int Yeti_RageOnValue = -99999;
   public int Yeti_RageOnReason = -99999;
   public long Yeti_RageOnTill = -99999L;
   public int Yeti_RageOnLevel = -99999;
   public int Yeti_RageOnFromID = -99999;
   public int Yeti_SpicyValue = -99999;
   public int Yeti_SpicyReason = -99999;
   public long Yeti_SpicyTill = -99999L;
   public int Yeti_SpicyLevel = -99999;
   public int Yeti_SpicyFromID = -99999;
   public int Yeti_MyFriendPepeValue = -99999;
   public int Yeti_MyFriendPepeReason = -99999;
   public long Yeti_MyFriendPepeTill = -99999L;
   public int Yeti_MyFriendPepeLevel = -99999;
   public int Yeti_MyFriendPepeFromID = -99999;
   public int PinkBean_MagicShowtimeStackValue = -99999;
   public int PinkBean_MagicShowtimeStackReason = -99999;
   public long PinkBean_MagicShowtimeStackTill = -99999L;
   public int PinkBean_MagicShowtimeStackLevel = -99999;
   public int PinkBean_MagicShowtimeStackFromID = -99999;
   public int Unk_1123_1Value = -99999;
   public int Unk_1123_1Reason = -99999;
   public long Unk_1123_1Till = -99999L;
   public int Unk_1123_1Level = -99999;
   public int Unk_1123_1FromID = -99999;
   public int DragonVeinReadingValue = -99999;
   public int DragonVeinReadingReason = -99999;
   public long DragonVeinReadingTill = -99999L;
   public int DragonVeinReadingLevel = -99999;
   public int DragonVeinReadingFromID = -99999;
   public int MountainsSeedsStackValue = -99999;
   public int MountainsSeedsStackReason = -99999;
   public long MountainsSeedsStackTill = -99999L;
   public int MountainsSeedsStackLevel = -99999;
   public int MountainsSeedsStackFromID = -99999;
   public int MountainGuardianValue = -99999;
   public int MountainGuardianReason = -99999;
   public long MountainGuardianTill = -99999L;
   public int MountainGuardianLevel = -99999;
   public int MountainGuardianFromID = -99999;
   public int DesolateWindsValue = -99999;
   public int DesolateWindsReason = -99999;
   public long DesolateWindsTill = -99999L;
   public int DesolateWindsLevel = -99999;
   public int DesolateWindsFromID = -99999;
   public int SunlightSproutValue = -99999;
   public int SunlightSproutReason = -99999;
   public long SunlightSproutTill = -99999L;
   public int SunlightSproutLevel = -99999;
   public int SunlightSproutFromID = -99999;
   public int RiverPuddleDrenchValue = -99999;
   public int RiverPuddleDrenchReason = -99999;
   public long RiverPuddleDrenchTill = -99999L;
   public int RiverPuddleDrenchLevel = -99999;
   public int RiverPuddleDrenchFromID = -99999;
   public int FreeDragonVeinStackValue = -99999;
   public int FreeDragonVeinStackReason = -99999;
   public long FreeDragonVeinStackTill = -99999L;
   public int FreeDragonVeinStackLevel = -99999;
   public int FreeDragonVeinStackFromID = -99999;
   public int TraceOfDragonVeinStackValue = -99999;
   public int TraceOfDragonVeinStackReason = -99999;
   public long TraceOfDragonVeinStackTill = -99999L;
   public int TraceOfDragonVeinStackLevel = -99999;
   public int TraceOfDragonVeinStackFromID = -99999;
   public int LotusFlowerValue = -99999;
   public int LotusFlowerReason = -99999;
   public long LotusFlowerTill = -99999L;
   public int LotusFlowerLevel = -99999;
   public int LotusFlowerFromID = -99999;
   public int LaLa_LinkSkillValue = -99999;
   public int LaLa_LinkSkillReason = -99999;
   public long LaLa_LinkSkillTill = -99999L;
   public int LaLa_LinkSkillLevel = -99999;
   public int LaLa_LinkSkillFromID = -99999;
   public int FlagRace_CramponShoesValue = -99999;
   public int FlagRace_CramponShoesReason = -99999;
   public long FlagRace_CramponShoesTill = -99999L;
   public int FlagRace_CramponShoesLevel = -99999;
   public int FlagRace_CramponShoesFromID = -99999;
   public int NEW_1134_01Value = -99999;
   public int NEW_1134_01Reason = -99999;
   public long NEW_1134_01Till = -99999L;
   public int NEW_1134_01Level = -99999;
   public int NEW_1134_01FromID = -99999;
   public int NEW_1134_02Value = -99999;
   public int NEW_1134_02Reason = -99999;
   public long NEW_1134_02Till = -99999L;
   public int NEW_1134_02Level = -99999;
   public int NEW_1134_02FromID = -99999;
   public int MiniCannonBallStackValue = -99999;
   public int MiniCannonBallStackReason = -99999;
   public long MiniCannonBallStackTill = -99999L;
   public int MiniCannonBallStackLevel = -99999;
   public int MiniCannonBallStackFromID = -99999;
   public int StormWhimValue = -99999;
   public int StormWhimReason = -99999;
   public long StormWhimTill = -99999L;
   public int StormWhimLevel = -99999;
   public int StormWhimFromID = -99999;
   public int SeaSerpentValue = -99999;
   public int SeaSerpentReason = -99999;
   public long SeaSerpentTill = -99999L;
   public int SeaSerpentLevel = -99999;
   public int SeaSerpentFromID = -99999;
   public int SerpentStoneValue = -99999;
   public int SerpentStoneReason = -99999;
   public long SerpentStoneTill = -99999L;
   public int SerpentStoneLevel = -99999;
   public int SerpentStoneFromID = -99999;
   public int SerpentScrewValue = -99999;
   public int SerpentScrewReason = -99999;
   public long SerpentScrewTill = -99999L;
   public int SerpentScrewLevel = -99999;
   public int SerpentScrewFromID = -99999;
   public int SerpentScrewRemainCount = 0;
   public int SerpentScrewBossAttackCount = 0;
   public int EnhanceArrowValue = -99999;
   public int EnhanceArrowReason = -99999;
   public long EnhanceArrowTill = -99999L;
   public int EnhanceArrowLevel = -99999;
   public int EnhanceArrowFromID = -99999;
   public int UntiringNectarValue = -99999;
   public int UntiringNectarReason = -99999;
   public long UntiringNectarTill = -99999L;
   public int UntiringNectarLevel = -99999;
   public int UntiringNectarFromID = -99999;
   public int ScarringSwordValue = -99999;
   public int ScarringSwordReason = -99999;
   public long ScarringSwordTill = -99999L;
   public int ScarringSwordLevel = -99999;
   public int ScarringSwordFromID = -99999;
   public int IceAuraTornadoValue = -99999;
   public int IceAuraTornadoReason = -99999;
   public long IceAuraTornadoTill = -99999L;
   public int IceAuraTornadoLevel = -99999;
   public int IceAuraTornadoFromID = -99999;
   public int HolyWaterValue = -99999;
   public int HolyWaterReason = -99999;
   public long HolyWaterTill = -99999L;
   public int HolyWaterLevel = -99999;
   public int HolyWaterFromID = -99999;
   public int TriumphFeatherValue = -99999;
   public int TriumphFeatherReason = -99999;
   public long TriumphFeatherTill = -99999L;
   public int TriumphFeatherLevel = -99999;
   public int TriumphFeatherFromID = -99999;
   public int FlashMirageValue = -99999;
   public int FlashMirageReason = -99999;
   public long FlashMirageTill = -99999L;
   public int FlashMirageLevel = -99999;
   public int FlashMirageFromID = -99999;
   public int HolyBloodValue = -99999;
   public int HolyBloodReason = -99999;
   public long HolyBloodTill = -99999L;
   public int HolyBloodLevel = -99999;
   public int HolyBloodFromID = -99999;
   public int StealValue = -99999;
   public int StealReason = -99999;
   public long StealTill = -99999L;
   public int StealLevel = -99999;
   public int StealFromID = -99999;
   public int LimitBreakValue = -99999;
   public int LimitBreakReason = -99999;
   public long LimitBreakTill = -99999L;
   public int LimitBreakLevel = -99999;
   public int LimitBreakFromID = -99999;
   public int TranscendentsValue = -99999;
   public int TranscendentsReason = -99999;
   public long TranscendentsTill = -99999L;
   public int TranscendentsLevel = -99999;
   public int TranscendentsFromID = -99999;
   public int Elemental_KnightsValue = -99999;
   public int Elemental_KnightsReason = -99999;
   public long Elemental_KnightsTill = -99999L;
   public int Elemental_KnightsLevel = -99999;
   public int Elemental_KnightsFromID = -99999;
   public int NEW_370_5Value = -99999;
   public int NEW_370_5Reason = -99999;
   public long NEW_370_5Till = -99999L;
   public int NEW_370_5Level = -99999;
   public int NEW_370_5FromID = -99999;
   public int ViperDefenseFormValue = -99999;
   public int ViperDefenseFormReason = -99999;
   public long ViperDefenseFormTill = -99999L;
   public int ViperDefenseFormLevel = -99999;
   public int ViperDefenseFormFromID = -99999;
   public int EmpressBlessStackValue = -99999;
   public int EmpressBlessStackReason = -99999;
   public long EmpressBlessStackTill = -99999L;
   public int EmpressBlessStackLevel = -99999;
   public int EmpressBlessStackFromID = -99999;
   public int OblivionValue = -99999;
   public int OblivionReason = -99999;
   public long OblivionTill = -99999L;
   public int OblivionLevel = -99999;
   public int OblivionFromID = -99999;
   public int ResonateUltimatumValue = -99999;
   public int ResonateUltimatumReason = -99999;
   public long ResonateUltimatumTill = -99999L;
   public int ResonateUltimatumLevel = -99999;
   public int ResonateUltimatumFromID = -99999;
   public int NEW_372_2Value = -99999;
   public int NEW_372_2Reason = -99999;
   public long NEW_372_2Till = -99999L;
   public int NEW_372_2Level = -99999;
   public int NEW_372_2FromID = -99999;
   public int UltimateBPMValue = -99999;
   public int UltimateBPMReason = -99999;
   public long UltimateBPMTill = -99999L;
   public int UltimateBPMLevel = -99999;
   public int UltimateBPMFromID = -99999;
   public int SummonChakriValue = -99999;
   public int SummonChakriReason = -99999;
   public long SummonChakriTill = -99999L;
   public int SummonChakriLevel = -99999;
   public int SummonChakriFromID = -99999;
   public int VoidEnhanceValue = -99999;
   public int VoidEnhanceReason = -99999;
   public long VoidEnhanceTill = -99999L;
   public int VoidEnhanceLevel = -99999;
   public int VoidEnhanceFromID = -99999;
   public int VoidBurstValue = -99999;
   public int VoidBurstReason = -99999;
   public long VoidBurstTill = -99999L;
   public int VoidBurstLevel = -99999;
   public int VoidBurstFromID = -99999;
   public int DeceivingBladeValue = -99999;
   public int DeceivingBladeReason = -99999;
   public long DeceivingBladeTill = -99999L;
   public int DeceivingBladeLevel = -99999;
   public int DeceivingBladeFromID = -99999;
   public int CurseEnchantValue = -99999;
   public int CurseEnchantReason = -99999;
   public long CurseEnchantTill = -99999L;
   public int CurseEnchantLevel = -99999;
   public int CurseEnchantFromID = -99999;
   public int NEW_373_1Value = -99999;
   public int NEW_373_1Reason = -99999;
   public long NEW_373_1Till = -99999L;
   public int NEW_373_1Level = -99999;
   public int NEW_373_1FromID = -99999;
   public int RouletteStackValue = -99999;
   public int RouletteStackReason = -99999;
   public long RouletteStackTill = -99999L;
   public int RouletteStackLevel = -99999;
   public int RouletteStackFromID = -99999;
   public int AuraWeaponStackValue = -99999;
   public int AuraWeaponStackReason = -99999;
   public long AuraWeaponStackTill = -99999L;
   public int AuraWeaponStackLevel = -99999;
   public int AuraWeaponStackFromID = -99999;
   public int MagicCircuitFullDriveStackValue = -99999;
   public int MagicCircuitFullDriveStackReason = -99999;
   public long MagicCircuitFullDriveStackTill = -99999L;
   public int MagicCircuitFullDriveStackLevel = -99999;
   public int MagicCircuitFullDriveStackFromID = -99999;
   public int Karing2PhasePurifyValue = -99999;
   public int Karing2PhasePurifyReason = -99999;
   public long Karing2PhasePurifyTill = -99999L;
   public int Karing2PhasePurifyLevel = -99999;
   public int Karing2PhasePurifyFromID = -99999;
   public int Karing3PhaseLightOfWillValue = -99999;
   public int Karing3PhaseLightOfWillReason = -99999;
   public long Karing3PhaseLightOfWillTill = -99999L;
   public int Karing3PhaseLightOfWillLevel = -99999;
   public int Karing3PhaseLightOfWillFromID = -99999;
   public int karing2phaseConfineValue = -99999;
   public int karing2phaseConfineReason = -99999;
   public long karing2phaseConfineTill = -99999L;
   public int karing2phaseConfineLevel = -99999;
   public int karing2phaseConfineFromID = -99999;
   public int NEW_376_724Value = -99999;
   public int NEW_376_724Reason = -99999;
   public long NEW_376_724Till = -99999L;
   public int NEW_376_724Level = -99999;
   public int NEW_376_724FromID = -99999;
   public int NEW_376_725Value = -99999;
   public int NEW_376_725Reason = -99999;
   public long NEW_376_725Till = -99999L;
   public int NEW_376_725Level = -99999;
   public int NEW_376_725FromID = -99999;
   public int KaringDisruptDeBuffValue = -99999;
   public int KaringDisruptDeBuffReason = -99999;
   public long KaringDisruptDeBuffTill = -99999L;
   public int KaringDisruptDeBuffLevel = -99999;
   public int KaringDisruptDeBuffFromID = -99999;
   public int NEW_379_743Value = -99999;
   public int NEW_379_743Reason = -99999;
   public long NEW_379_743Till = -99999L;
   public int NEW_379_743Level = -99999;
   public int NEW_379_743FromID = -99999;
   public int DoolInducedCurrentValue = -99999;
   public int DoolInducedCurrentReason = -99999;
   public long DoolInducedCurrentTill = -99999L;
   public int DoolInducedCurrentLevel = -99999;
   public int DoolInducedCurrentFromID = -99999;
   public int KarmaBladeValue = -99999;
   public int KarmaBladeReason = -99999;
   public long KarmaBladeTill = -99999L;
   public int KarmaBladeLevel = -99999;
   public int KarmaBladeFromID = -99999;
   public int CraftEnchantJavelinValue = -99999;
   public int CraftEnchantJavelinReason = -99999;
   public long CraftEnchantJavelinTill = -99999L;
   public int CraftEnchantJavelinLevel = -99999;
   public int CraftEnchantJavelinFromID = -99999;
   public int GloryWingEnchantJavelinValue = -99999;
   public int GloryWingEnchantJavelinReason = -99999;
   public long GloryWingEnchantJavelinTill = -99999L;
   public int GloryWingEnchantJavelinLevel = -99999;
   public int GloryWingEnchantJavelinFromID = -99999;
   public int UnlimitedCrystalValue = -99999;
   public int UnlimitedCrystalReason = -99999;
   public long UnlimitedCrystalTill = -99999L;
   public int UnlimitedCrystalLevel = -99999;
   public int UnlimitedCrystalFromID = -99999;
   public int AccRValue = -99999;
   public int AccRReason = -99999;
   public long AccRTill = -99999L;
   public int AccRLevel = -99999;
   public int AccRFromID = -99999;
   public int DexRValue = -99999;
   public int DexRReason = -99999;
   public long DexRTill = -99999L;
   public int DexRLevel = -99999;
   public int DexRFromID = -99999;
   public int New_366_12Value = -99999;
   public int New_366_12Reason = -99999;
   public long New_366_12Till = -99999L;
   public int New_366_12Level = -99999;
   public int New_366_12FromID = -99999;
   public int InfernalVenomValue = -99999;
   public int InfernalVenomReason = -99999;
   public long InfernalVenomTill = -99999L;
   public int InfernalVenomLevel = -99999;
   public int InfernalVenomFromID = -99999;
   public int SixthSkillFrozenValue = -99999;
   public int SixthSkillFrozenReason = -99999;
   public long SixthSkillFrozenTill = -99999L;
   public int SixthSkillFrozenLevel = -99999;
   public int SixthSkillFrozenFromID = -99999;
   public int GrandCrossValue = -99999;
   public int GrandCrossReason = -99999;
   public long GrandCrossTill = -99999L;
   public int GrandCrossLevel = -99999;
   public int GrandCrossFromID = -99999;
   public int ChainArtsStrokeVIValue = -99999;
   public int ChainArtsStrokeVIReason = -99999;
   public long ChainArtsStrokeVITill = -99999L;
   public int ChainArtsStrokeVILevel = -99999;
   public int ChainArtsStrokeVIFromID = -99999;
   public int Unk443Value = -99999;
   public int Unk443Reason = -99999;
   public long Unk443Till = -99999L;
   public int Unk443Level = -99999;
   public int Unk443FromID = -99999;
   public int OldestAbyssValue = -99999;
   public int OldestAbyssReason = -99999;
   public long OldestAbyssTill = -99999L;
   public int OldestAbyssLevel = -99999;
   public int OldestAbyssFromID = -99999;
   public int EternityValue = -99999;
   public int EternityReason = -99999;
   public long EternityTill = -99999L;
   public int EternityLevel = -99999;
   public int EternityFromID = -99999;
   public int HolyAdventValue = -99999;
   public int HolyAdventReason = -99999;
   public long HolyAdventTill = -99999L;
   public int HolyAdventLevel = -99999;
   public int HolyAdventFromID = -99999;
   public int ZodiacBurstValue = -99999;
   public int ZodiacBurstReason = -99999;
   public long ZodiacBurstTill = -99999L;
   public int ZodiacBurstLevel = -99999;
   public int ZodiacBurstFromID = -99999;
   public int FirebirdEnergyValue = -99999;
   public int FirebirdEnergyReason = -99999;
   public long FirebirdEnergyTill = -99999L;
   public int FirebirdEnergyLevel = -99999;
   public int FirebirdEnergyFromID = -99999;
   public int NEW_354_01Value = -99999;
   public int NEW_354_01Reason = -99999;
   public long NEW_354_01Till = -99999L;
   public int NEW_354_01Level = -99999;
   public int NEW_354_01FromID = -99999;
   public int ContinousRingReadyValue = -99999;
   public int ContinousRingReadyReason = -99999;
   public long ContinousRingReadyTill = -99999L;
   public int ContinousRingReadyLevel = -99999;
   public int ContinousRingReadyFromID = -99999;
   public int DefyingFateValue = -99999;
   public int DefyingFateReason = -99999;
   public long DefyingFateTill = -99999L;
   public int DefyingFateLevel = -99999;
   public int DefyingFateFromID = -99999;
   public int AnnihilationValue = -99999;
   public int AnnihilationReason = -99999;
   public long AnnihilationTill = -99999L;
   public int AnnihilationLevel = -99999;
   public int AnnihilationFromID = -99999;
   public int AdminFixStatIgnoreDefenseValue = -99999;
   public int AdminFixStatIgnoreDefenseReason = -99999;
   public long AdminFixStatIgnoreDefenseTill = -99999L;
   public int AdminFixStatIgnoreDefenseLevel = -99999;
   public int AdminFixStatIgnoreDefenseFromID = -99999;
   public int AdminFixStatBossDamageValue = -99999;
   public int AdminFixStatBossDamageReason = -99999;
   public long AdminFixStatBossDamageTill = -99999L;
   public int AdminFixStatBossDamageLevel = -99999;
   public int AdminFixStatBossDamageFromID = -99999;
   public int AdminFixStatBuffTimeRValue = -99999;
   public int AdminFixStatBuffTimeRReason = -99999;
   public long AdminFixStatBuffTimeRTill = -99999L;
   public int AdminFixStatBuffTimeRLevel = -99999;
   public int AdminFixStatBuffTimeRFromID = -99999;
   public int AdminFixStatActionSpeedValue = -99999;
   public int AdminFixStatActionSpeedReason = -99999;
   public long AdminFixStatActionSpeedTill = -99999L;
   public int AdminFixStatActionSpeedLevel = -99999;
   public int AdminFixStatActionSpeedFromID = -99999;
   public int BossFieldFinalDamRValue = -99999;
   public int BossFieldFinalDamRReason = -99999;
   public long BossFieldFinalDamRTill = -99999L;
   public int BossFieldFinalDamRLevel = -99999;
   public int BossFieldFinalDamRFromID = -99999;
   public int FlameSweepValue = -99999;
   public int FlameSweepReason = -99999;
   public long FlameSweepTill = -99999L;
   public int FlameSweepLevel = -99999;
   public int FlameSweepFromID = -99999;
   public int EnhanceSnipingValue = -99999;
   public int EnhanceSnipingReason = -99999;
   public long EnhanceSnipingTill = -99999L;
   public int EnhanceSnipingLevel = -99999;
   public int EnhanceSnipingFromID = -99999;
   public int UltimateSnipingValue = -99999;
   public int UltimateSnipingReason = -99999;
   public long UltimateSnipingTill = -99999L;
   public int UltimateSnipingLevel = -99999;
   public int UltimateSnipingFromID = -99999;
   public int QuadrupleThrowValue = -99999;
   public int QuadrupleThrowReason = -99999;
   public long QuadrupleThrowTill = -99999L;
   public int QuadrupleThrowLevel = -99999;
   public int QuadrupleThrowFromID = -99999;
   public int AngelRayValue = -99999;
   public int AngelRayReason = -99999;
   public long AngelRayTill = -99999L;
   public int AngelRayLevel = -99999;
   public int AngelRayFromID = -99999;
   public int GrandFinaleValue = -99999;
   public int GrandFinaleReason = -99999;
   public long GrandFinaleTill = -99999L;
   public int GrandFinaleLevel = -99999;
   public int GrandFinaleFromID = -99999;
   public int AscendantShadeValue = -99999;
   public int AscendantShadeReason = -99999;
   public long AscendantShadeTill = -99999L;
   public int AscendantShadeLevel = -99999;
   public int AscendantShadeFromID = -99999;
   public int SixthAssassinationValue = -99999;
   public int SixthAssassinationReason = -99999;
   public long SixthAssassinationTill = -99999L;
   public int SixthAssassinationLevel = -99999;
   public int SixthAssassinationFromID = -99999;
   public int SixthAssassinationDarkSightValue = -99999;
   public int SixthAssassinationDarkSightReason = -99999;
   public long SixthAssassinationDarkSightTill = -99999L;
   public int SixthAssassinationDarkSightLevel = -99999;
   public int SixthAssassinationDarkSightFromID = -99999;
   public int ArtificialEvolutionValue = -99999;
   public int ArtificialEvolutionReason = -99999;
   public long ArtificialEvolutionTill = -99999L;
   public int ArtificialEvolutionLevel = -99999;
   public int ArtificialEvolutionFromID = -99999;
   public int FrozenLightingValue = -99999;
   public int FrozenLightingReason = -99999;
   public long FrozenLightingTill = -99999L;
   public int FrozenLightingLevel = -99999;
   public int FrozenLightingFromID = -99999;
   public int NEW_379_744Value = -99999;
   public int NEW_379_744Reason = -99999;
   public long NEW_379_744Till = -99999L;
   public int NEW_379_744Level = -99999;
   public int NEW_379_744FromID = -99999;
   public int SixthStormArrowExValue = -99999;
   public int SixthStormArrowExReason = -99999;
   public long SixthStormArrowExTill = -99999L;
   public int SixthStormArrowExLevel = -99999;
   public int SixthStormArrowExFromID = -99999;
   public int NEW_379_754Value = -99999;
   public int NEW_379_754Reason = -99999;
   public long NEW_379_754Till = -99999L;
   public int NEW_379_754Level = -99999;
   public int NEW_379_754FromID = -99999;
   public int WildVulcanAdvValue = -99999;
   public int WildVulcanAdvReason = -99999;
   public long WildVulcanAdvTill = -99999L;
   public int WildVulcanAdvLevel = -99999;
   public int WildVulcanAdvFromID = -99999;
   public int WildVulcanAdvStackValue = -99999;
   public int WildVulcanAdvStackReason = -99999;
   public long WildVulcanAdvStackTill = -99999L;
   public int WildVulcanAdvStackLevel = -99999;
   public int WildVulcanAdvStackFromID = -99999;
   public int NaturesBeliefValue = -99999;
   public int NaturesBeliefReason = -99999;
   public long NaturesBeliefTill = -99999L;
   public int NaturesBeliefLevel = -99999;
   public int NaturesBeliefFromID = -99999;
   public int AdrenalineSurgeValue = -99999;
   public int AdrenalineSurgeReason = -99999;
   public long AdrenalineSurgeTill = -99999L;
   public int AdrenalineSurgeLevel = -99999;
   public int AdrenalineSurgeFromID = -99999;
   public int LifeAndDeathValue = -99999;
   public int LifeAndDeathReason = -99999;
   public long LifeAndDeathTill = -99999L;
   public int LifeAndDeathLevel = -99999;
   public int LifeAndDeathFromID = -99999;
   public int NEW_379_760Value = -99999;
   public int NEW_379_760Reason = -99999;
   public long NEW_379_760Till = -99999L;
   public int NEW_379_760Level = -99999;
   public int NEW_379_760FromID = -99999;
   public int ForsakenRelicValue = -99999;
   public int ForsakenRelicReason = -99999;
   public long ForsakenRelicTill = -99999L;
   public int ForsakenRelicLevel = -99999;
   public int ForsakenRelicFromID = -99999;
   public int NightmareValue = -99999;
   public int NightmareReason = -99999;
   public long NightmareTill = -99999L;
   public int NightmareLevel = -99999;
   public int NightmareFromID = -99999;
   public int AwakenAbyssValue = -99999;
   public int AwakenAbyssReason = -99999;
   public long AwakenAbyssTill = -99999L;
   public int AwakenAbyssLevel = -99999;
   public int AwakenAbyssFromID = -99999;
   public int RapidFireValue = -99999;
   public int RapidFireReason = -99999;
   public long RapidFireTill = -99999L;
   public int RapidFireLevel = -99999;
   public int RapidFireFromID = -99999;
   public int AllThingsValue = -99999;
   public int AllThingsReason = -99999;
   public long AllThingsTill = -99999L;
   public int AllThingsLevel = -99999;
   public int AllThingsFromID = -99999;
   public int ConsumingFlamesVIValue = -99999;
   public int ConsumingFlamesVIReason = -99999;
   public long ConsumingFlamesVITill = -99999L;
   public int ConsumingFlamesVILevel = -99999;
   public int ConsumingFlamesVIFromID = -99999;
   private static final SecondaryStatFlag[] statFlags = new SecondaryStatFlag[] {
         SecondaryStatFlag.DashSpeed,
         SecondaryStatFlag.DashJump,
         SecondaryStatFlag.RideVehicle,
         SecondaryStatFlag.SpeedInfusion,
         SecondaryStatFlag.GuidedBullet,
         SecondaryStatFlag.Undead,
         SecondaryStatFlag.RideVehicleExpire,
         SecondaryStatFlag.RelicCharge,
         SecondaryStatFlag.Divide
   };
   private static final SecondaryStatFlag[] isMovementAffectingSeat = new SecondaryStatFlag[] {
         SecondaryStatFlag.Stun,
         SecondaryStatFlag.Weakness,
         SecondaryStatFlag.Slow,
         SecondaryStatFlag.Morph,
         SecondaryStatFlag.Ghost,
         SecondaryStatFlag.BasicStatUp,
         SecondaryStatFlag.Attract,
         SecondaryStatFlag.DashJump,
         SecondaryStatFlag.DashSpeed,
         SecondaryStatFlag.Flying,
         SecondaryStatFlag.SixthSkillFrozen,
         SecondaryStatFlag.Frozen,
         SecondaryStatFlag.Frozen2,
         SecondaryStatFlag.Lapidification,
         SecondaryStatFlag.indieSpeed,
         SecondaryStatFlag.indieJump,
         SecondaryStatFlag.KeyDownMoving,
         SecondaryStatFlag.Mechanic,
         SecondaryStatFlag.Magnet,
         SecondaryStatFlag.MagnetArea,
         SecondaryStatFlag.VampDeath,
         SecondaryStatFlag.VampDeathSummon,
         SecondaryStatFlag.Speed,
         SecondaryStatFlag.Jump,
         SecondaryStatFlag.DarkTornado,
         SecondaryStatFlag.NewFlying,
         SecondaryStatFlag.Dance,
         SecondaryStatFlag.SelfWeakness,
         SecondaryStatFlag.RideVehicle,
         SecondaryStatFlag.RideVehicleExpire,
         SecondaryStatFlag.FlagRace_CramponShoes,
         SecondaryStatFlag.GiveMeHeal,
         SecondaryStatFlag.TouchMe
   };

   public SecondaryStat(MapleCharacter player) {
      this.setPlayer(player);
      this.setToRemote();
      this.setEnDecode4Bytes();
   }

   public void setToRemote() {
      this.toRemote.setFlag(SecondaryStatFlag.Speed);
      this.toRemote.setFlag(SecondaryStatFlag.Combo);
      this.toRemote.setFlag(SecondaryStatFlag.BlessedHammer);
      this.toRemote.setFlag(SecondaryStatFlag.SnowCharge);
      this.toRemote.setFlag(SecondaryStatFlag.HolyCharge);
      this.toRemote.setFlag(SecondaryStatFlag.Stun);
      this.toRemote.setFlag(SecondaryStatFlag.Shock);
      this.toRemote.setFlag(SecondaryStatFlag.Darkness);
      this.toRemote.setFlag(SecondaryStatFlag.Seal);
      this.toRemote.setFlag(SecondaryStatFlag.Weakness);
      this.toRemote.setFlag(SecondaryStatFlag.WeaknessMDamage);
      this.toRemote.setFlag(SecondaryStatFlag.Curse);
      this.toRemote.setFlag(SecondaryStatFlag.Slow);
      this.toRemote.setFlag(SecondaryStatFlag.PVPRaceEffect);
      this.toRemote.setFlag(SecondaryStatFlag.TimeBomb);
      this.toRemote.setFlag(SecondaryStatFlag.Team);
      this.toRemote.setFlag(SecondaryStatFlag.DisOrder);
      this.toRemote.setFlag(SecondaryStatFlag.Thread);
      this.toRemote.setFlag(SecondaryStatFlag.Poison);
      this.toRemote.setFlag(SecondaryStatFlag.ShadowPartner);
      this.toRemote.setFlag(SecondaryStatFlag.DarkSight);
      this.toRemote.setFlag(SecondaryStatFlag.SoulArrow);
      this.toRemote.setFlag(SecondaryStatFlag.Morph);
      this.toRemote.setFlag(SecondaryStatFlag.Ghost);
      this.toRemote.setFlag(SecondaryStatFlag.Attract);
      this.toRemote.setFlag(SecondaryStatFlag.Magnet);
      this.toRemote.setFlag(SecondaryStatFlag.MagnetArea);
      this.toRemote.setFlag(SecondaryStatFlag.NoBulletConsume);
      this.toRemote.setFlag(SecondaryStatFlag.BanMap);
      this.toRemote.setFlag(SecondaryStatFlag.Barrier);
      this.toRemote.setFlag(SecondaryStatFlag.DojangShield);
      this.toRemote.setFlag(SecondaryStatFlag.ReverseInput);
      this.toRemote.setFlag(SecondaryStatFlag.RespectPImmune);
      this.toRemote.setFlag(SecondaryStatFlag.RespectMImmune);
      this.toRemote.setFlag(SecondaryStatFlag.DefenseAtt);
      this.toRemote.setFlag(SecondaryStatFlag.DefenseState);
      this.toRemote.setFlag(SecondaryStatFlag.DojangBerserk);
      this.toRemote.setFlag(SecondaryStatFlag.DojangInvincible);
      this.toRemote.setFlag(SecondaryStatFlag.RepeatEffect);
      this.toRemote.setFlag(SecondaryStatFlag.StopPortion);
      this.toRemote.setFlag(SecondaryStatFlag.StopMotion);
      this.toRemote.setFlag(SecondaryStatFlag.Fear);
      this.toRemote.setFlag(SecondaryStatFlag.MagicShield);
      this.toRemote.setFlag(SecondaryStatFlag.IceSkill);
      this.toRemote.setFlag(SecondaryStatFlag.Frozen);
      this.toRemote.setFlag(SecondaryStatFlag.Frozen2);
      this.toRemote.setFlag(SecondaryStatFlag.Web);
      this.toRemote.setFlag(SecondaryStatFlag.DrawBack);
      this.toRemote.setFlag(SecondaryStatFlag.FinalCut);
      this.toRemote.setFlag(SecondaryStatFlag.Sneak);
      this.toRemote.setFlag(SecondaryStatFlag.BeastFormDamageUp);
      this.toRemote.setFlag(SecondaryStatFlag.Mechanic);
      this.toRemote.setFlag(SecondaryStatFlag.BlessingArmorIncPAD);
      this.toRemote.setFlag(SecondaryStatFlag.Inflation);
      this.toRemote.setFlag(SecondaryStatFlag.Explosion);
      this.toRemote.setFlag(SecondaryStatFlag.DarkTornado);
      this.toRemote.setFlag(SecondaryStatFlag.AmplifyDamage);
      this.toRemote.setFlag(SecondaryStatFlag.HideAttack);
      this.toRemote.setFlag(SecondaryStatFlag.HolyMagicShell);
      this.toRemote.setFlag(SecondaryStatFlag.DevilishPower);
      this.toRemote.setFlag(SecondaryStatFlag.CrewCommandership);
      this.toRemote.setFlag(SecondaryStatFlag.Event);
      this.toRemote.setFlag(SecondaryStatFlag.Event2);
      this.toRemote.setFlag(SecondaryStatFlag.DeathMark);
      this.toRemote.setFlag(SecondaryStatFlag.PainMark);
      this.toRemote.setFlag(SecondaryStatFlag.Lapidification);
      this.toRemote.setFlag(SecondaryStatFlag.VampDeath);
      this.toRemote.setFlag(SecondaryStatFlag.VampDeathSummon);
      this.toRemote.setFlag(SecondaryStatFlag.VenomSnake);
      this.toRemote.setFlag(SecondaryStatFlag.PyramidEffect);
      this.toRemote.setFlag(SecondaryStatFlag.PinkbeanRollingGrade);
      this.toRemote.setFlag(SecondaryStatFlag.IgnoreTargetDEF);
      this.toRemote.setFlag(SecondaryStatFlag.StrikerCorrectionBuff);
      this.toRemote.setFlag(SecondaryStatFlag.Invisible);
      this.toRemote.setFlag(SecondaryStatFlag.Judgement);
      this.toRemote.setFlag(SecondaryStatFlag.KeyDownAreaMoving);
      this.toRemote.setFlag(SecondaryStatFlag.StackBuff);
      this.toRemote.setFlag(SecondaryStatFlag.Larkness);
      this.toRemote.setFlag(SecondaryStatFlag.ReshuffleSwitch);
      this.toRemote.setFlag(SecondaryStatFlag.SpecialAction);
      this.toRemote.setFlag(SecondaryStatFlag.StopForceAtomInfo);
      this.toRemote.setFlag(SecondaryStatFlag.SoulGazeCriDamR);
      this.toRemote.setFlag(SecondaryStatFlag.PowerTransferGauge);
      this.toRemote.setFlag(SecondaryStatFlag.BlitzShield);
      this.toRemote.setFlag(SecondaryStatFlag.AffinitySlug);
      this.toRemote.setFlag(SecondaryStatFlag.SoulExalt);
      this.toRemote.setFlag(SecondaryStatFlag.HiddenPieceOn);
      this.toRemote.setFlag(SecondaryStatFlag.SmashStack);
      this.toRemote.setFlag(SecondaryStatFlag.MobZoneState);
      this.toRemote.setFlag(SecondaryStatFlag.GiveMeHeal);
      this.toRemote.setFlag(SecondaryStatFlag.TouchMe);
      this.toRemote.setFlag(SecondaryStatFlag.Contagion);
      this.toRemote.setFlag(SecondaryStatFlag.ComboUnlimited);
      this.toRemote.setFlag(SecondaryStatFlag.IgnorePCounter);
      this.toRemote.setFlag(SecondaryStatFlag.IgnoreAllCounter);
      this.toRemote.setFlag(SecondaryStatFlag.IgnorePImmune);
      this.toRemote.setFlag(SecondaryStatFlag.IgnoreAllImmune);
      this.toRemote.setFlag(SecondaryStatFlag.FinalJudgement);
      this.toRemote.setFlag(SecondaryStatFlag.FireAura);
      this.toRemote.setFlag(SecondaryStatFlag.VengeanceOfAngel);
      this.toRemote.setFlag(SecondaryStatFlag.HeavensDoor);
      this.toRemote.setFlag(SecondaryStatFlag.DamAbsorbShield);
      this.toRemote.setFlag(SecondaryStatFlag.AntiMagicShell);
      this.toRemote.setFlag(SecondaryStatFlag.NotDamaged);
      this.toRemote.setFlag(SecondaryStatFlag.BleedingToxin);
      this.toRemote.setFlag(SecondaryStatFlag.DualBladeFinal);
      this.toRemote.setFlag(SecondaryStatFlag.KarmaBlade);
      this.toRemote.setFlag(SecondaryStatFlag.IgnoreMobDamR);
      this.toRemote.setFlag(SecondaryStatFlag.Asura);
      this.toRemote.setFlag(SecondaryStatFlag.MegaSmasher);
      this.toRemote.setFlag(SecondaryStatFlag.SerpentSpirit);
      this.toRemote.setFlag(SecondaryStatFlag.Stimulate);
      this.toRemote.setFlag(SecondaryStatFlag.ReturnTeleport);
      this.toRemote.setFlag(SecondaryStatFlag.CapDebuff);
      this.toRemote.setFlag(SecondaryStatFlag.OverloadCount);
      this.toRemote.setFlag(SecondaryStatFlag.FireBomb);
      this.toRemote.setFlag(SecondaryStatFlag.SurplusSupply);
      this.toRemote.setFlag(SecondaryStatFlag.NewFlying);
      this.toRemote.setFlag(SecondaryStatFlag.NaviFlying);
      this.toRemote.setFlag(SecondaryStatFlag.AmaranthGenerator);
      this.toRemote.setFlag(SecondaryStatFlag.CygnusElementSkill);
      this.toRemote.setFlag(SecondaryStatFlag.StrikerHyperElectric);
      this.toRemote.setFlag(SecondaryStatFlag.EventPointAbsorb);
      this.toRemote.setFlag(SecondaryStatFlag.EventAssemble);
      this.toRemote.setFlag(SecondaryStatFlag.New_366_12);
      this.toRemote.setFlag(SecondaryStatFlag.PoseType);
      this.toRemote.setFlag(SecondaryStatFlag.CosmikForge);
      this.toRemote.setFlag(SecondaryStatFlag.ElementSoul);
      this.toRemote.setFlag(SecondaryStatFlag.GlimmeringTime);
      this.toRemote.setFlag(SecondaryStatFlag.Reincarnation);
      this.toRemote.setFlag(SecondaryStatFlag.Beholder);
      this.toRemote.setFlag(SecondaryStatFlag.QuiverCatridge);
      this.toRemote.setFlag(SecondaryStatFlag.AdvancedQuiver);
      this.toRemote.setFlag(SecondaryStatFlag.ImmuneBarrier);
      this.toRemote.setFlag(SecondaryStatFlag.FullSoulMP);
      this.toRemote.setFlag(SecondaryStatFlag.Dance);
      this.toRemote.setFlag(SecondaryStatFlag.SpiritGuard);
      this.toRemote.setFlag(SecondaryStatFlag.DemonDamAbsorbShield);
      this.toRemote.setFlag(SecondaryStatFlag.ComboTempest);
      this.toRemote.setFlag(SecondaryStatFlag.HalfStatByDebuff);
      this.toRemote.setFlag(SecondaryStatFlag.ComplusionSlant);
      this.toRemote.setFlag(SecondaryStatFlag.JaguarSummoned);
      this.toRemote.setFlag(SecondaryStatFlag.AttackCountX);
      this.toRemote.setFlag(SecondaryStatFlag.Transform);
      this.toRemote.setFlag(SecondaryStatFlag.EnergyBurst);
      this.toRemote.setFlag(SecondaryStatFlag.LightningCascade);
      this.toRemote.setFlag(SecondaryStatFlag.BulletParty);
      this.toRemote.setFlag(SecondaryStatFlag.LoadedDice);
      this.toRemote.setFlag(SecondaryStatFlag.Pray);
      this.toRemote.setFlag(SecondaryStatFlag.DarkLighting);
      this.toRemote.setFlag(SecondaryStatFlag.BMageAura);
      this.toRemote.setFlag(SecondaryStatFlag.FireBarrier);
      this.toRemote.setFlag(SecondaryStatFlag.KeyDownMoving);
      this.toRemote.setFlag(SecondaryStatFlag.MichaelSoulLink);
      this.toRemote.setFlag(SecondaryStatFlag.KinesisPsychicEnrageShield);
      this.toRemote.setFlag(SecondaryStatFlag.BladeStance);
      this.toRemote.setFlag(SecondaryStatFlag.Fever);
      this.toRemote.setFlag(SecondaryStatFlag.AdrenalinBoost);
      this.toRemote.setFlag(SecondaryStatFlag.RWBarrier);
      this.toRemote.setFlag(SecondaryStatFlag.RWUnk);
      this.toRemote.setFlag(SecondaryStatFlag.RWMagnumBlow);
      this.toRemote.setFlag(SecondaryStatFlag.SerpentScrew);
      this.toRemote.setFlag(SecondaryStatFlag.Karing3PhaseLightOfWill);
      this.toRemote.setFlag(SecondaryStatFlag.Cosmos);
      this.toRemote.setFlag(SecondaryStatFlag.GuidedArrow);
      this.toRemote.setFlag(SecondaryStatFlag.CraftJavelin);
      this.toRemote.setFlag(SecondaryStatFlag.BlessMark);
      this.toRemote.setFlag(SecondaryStatFlag.ProfessionalAgent);
      this.toRemote.setFlag(SecondaryStatFlag.Unk3);
      this.toRemote.setFlag(SecondaryStatFlag.Stigma);
      this.toRemote.setFlag(SecondaryStatFlag.HolyUnity);
      this.toRemote.setFlag(SecondaryStatFlag.RhoAias);
      this.toRemote.setFlag(SecondaryStatFlag.PsychicTornado);
      this.toRemote.setFlag(SecondaryStatFlag.InstallMaha);
      this.toRemote.setFlag(SecondaryStatFlag.OverloadMana);
      this.toRemote.setFlag(SecondaryStatFlag.TrueSniping);
      this.toRemote.setFlag(SecondaryStatFlag.Unk39);
      this.toRemote.setFlag(SecondaryStatFlag.SpotLight);
      this.toRemote.setFlag(SecondaryStatFlag.OverloadMode);
      this.toRemote.setFlag(SecondaryStatFlag.FreudsProtection);
      this.toRemote.setFlag(SecondaryStatFlag.BlessedHammerBig);
      this.toRemote.setFlag(SecondaryStatFlag.OverDrive);
      this.toRemote.setFlag(SecondaryStatFlag.EtherealForm);
      this.toRemote.setFlag(SecondaryStatFlag.ReadyToDie);
      this.toRemote.setFlag(SecondaryStatFlag.Oblivion);
      this.toRemote.setFlag(SecondaryStatFlag.CriticalReinforce);
      this.toRemote.setFlag(SecondaryStatFlag.CurseOfCreation);
      this.toRemote.setFlag(SecondaryStatFlag.CurseOfDestruction);
      this.toRemote.setFlag(SecondaryStatFlag.BlackMageAttributes);
      this.toRemote.setFlag(SecondaryStatFlag.StackDamR);
      this.toRemote.setFlag(SecondaryStatFlag.GloryWing);
      this.toRemote.setFlag(SecondaryStatFlag.TimeCurse);
      this.toRemote.setFlag(SecondaryStatFlag.TimeTorrent);
      this.toRemote.setFlag(SecondaryStatFlag.HarmonyLink);
      this.toRemote.setFlag(SecondaryStatFlag.FastCharge2);
      this.toRemote.setFlag(SecondaryStatFlag.SpectralForm);
      this.toRemote.setFlag(SecondaryStatFlag.ImpendingDeath);
      this.toRemote.setFlag(SecondaryStatFlag.OldestAbyss);
      this.toRemote.setFlag(SecondaryStatFlag.Eternity);
      this.toRemote.setFlag(SecondaryStatFlag.WillPoison);
      this.toRemote.setFlag(SecondaryStatFlag.SwordOfSoulLight);
      this.toRemote.setFlag(SecondaryStatFlag.GrandCrossSize);
      this.toRemote.setFlag(SecondaryStatFlag.SiphonVitalityShield);
      this.toRemote.setFlag(SecondaryStatFlag.BattlePvP_Wonky_Heal);
      this.toRemote.setFlag(SecondaryStatFlag.BattlePvP_Wonky_Protection);
      this.toRemote.setFlag(SecondaryStatFlag.ProtectionOfAncientWarrior);
      this.toRemote.setFlag(SecondaryStatFlag.NEW_351_02);
      this.toRemote.setFlag(SecondaryStatFlag.NEW_354_01);
      this.toRemote.setFlag(SecondaryStatFlag.NEW_311_01);
      this.toRemote.setFlag(SecondaryStatFlag.MultiSoccerAddBall);
      this.toRemote.setFlag(SecondaryStatFlag.PinkBeanMatryoshka);
      this.toRemote.setFlag(SecondaryStatFlag.NEW_320_04);
      this.toRemote.setFlag(SecondaryStatFlag.HyperCloneRampage);
      this.toRemote.setFlag(SecondaryStatFlag.Yaldabaoth);
      this.toRemote.setFlag(SecondaryStatFlag.Aion);
      this.toRemote.setFlag(SecondaryStatFlag.AncientProtectionMagic);
      this.toRemote.setFlag(SecondaryStatFlag.Graffiti);
      this.toRemote.setFlag(SecondaryStatFlag.QuiverFullBurst);
      this.toRemote.setFlag(SecondaryStatFlag.Nobility);
      this.toRemote.setFlag(SecondaryStatFlag.RuneofPurification);
      this.toRemote.setFlag(SecondaryStatFlag.RuneOfIgnition);
      this.toRemote.setFlag(SecondaryStatFlag.DuskDarkness);
      this.toRemote.setFlag(SecondaryStatFlag.YellowAura);
      this.toRemote.setFlag(SecondaryStatFlag.DrainAura);
      this.toRemote.setFlag(SecondaryStatFlag.BlueAura);
      this.toRemote.setFlag(SecondaryStatFlag.DarkAura);
      this.toRemote.setFlag(SecondaryStatFlag.DebuffAura);
      this.toRemote.setFlag(SecondaryStatFlag.UnionAuraBlow);
      this.toRemote.setFlag(SecondaryStatFlag.IceAura);
      this.toRemote.setFlag(SecondaryStatFlag.KnightsAura);
      this.toRemote.setFlag(SecondaryStatFlag.ZeroAuraSTR);
      this.toRemote.setFlag(SecondaryStatFlag.IncarnationAura);
      this.toRemote.setFlag(SecondaryStatFlag.BlizzardTempest);
      this.toRemote.setFlag(SecondaryStatFlag.PhotonRay);
      this.toRemote.setFlag(SecondaryStatFlag.DarknessAura);
      this.toRemote.setFlag(SecondaryStatFlag.AutoChargeStackOnOff);
      this.toRemote.setFlag(SecondaryStatFlag.LiberationOrbActive);
      this.toRemote.setFlag(SecondaryStatFlag.ThanatosDescent);
      this.toRemote.setFlag(SecondaryStatFlag.Annihilation);
      this.toRemote.setFlag(SecondaryStatFlag.HoyoungLastCheonJiInSkillSet);
      this.toRemote.setFlag(SecondaryStatFlag.Yeti_RageOn);
      this.toRemote.setFlag(SecondaryStatFlag.RiverPuddleDrench);
      this.toRemote.setFlag(SecondaryStatFlag.DesolateWinds);
      this.toRemote.setFlag(SecondaryStatFlag.SunlightSprout);
      this.toRemote.setFlag(SecondaryStatFlag.UntiringNectar);
      this.toRemote.setFlag(SecondaryStatFlag.IceAuraTornado);
      this.toRemote.setFlag(SecondaryStatFlag.FlashMirage);
      this.toRemote.setFlag(SecondaryStatFlag.HolyBlood);
      this.toRemote.setFlag(SecondaryStatFlag.Infinity);
      this.toRemote.setFlag(SecondaryStatFlag.TeleportMasteryOn);
      this.toRemote.setFlag(SecondaryStatFlag.ChillingStep);
      this.toRemote.setFlag(SecondaryStatFlag.BlessingArmor);
      this.toRemote.setFlag(SecondaryStatFlag.LimitBreak);
      this.toRemote.setFlag(SecondaryStatFlag.Transcendents);
      this.toRemote.setFlag(SecondaryStatFlag.ArtificialEvolution);
      this.toRemote.setFlag(SecondaryStatFlag.DemonFrenzy);
      this.toRemote.setFlag(SecondaryStatFlag.CrystalGate);
      this.toRemote.setFlag(SecondaryStatFlag.KinesisPsychicPoint);
      this.toRemote.setFlag(SecondaryStatFlag.karing2phaseConfine);
      this.toRemote.setFlag(SecondaryStatFlag.NEW_376_724);
      this.toRemote.setFlag(SecondaryStatFlag.NEW_376_725);
      this.toRemote.setFlag(SecondaryStatFlag.KaringDisruptDeBuff);
      this.toRemote.setFlag(SecondaryStatFlag.Karing2PhasePurify);
      this.toRemote.setFlag(SecondaryStatFlag.NEW_379_743);
      this.toRemote.setFlag(SecondaryStatFlag.NaturesBelief);
      this.toRemote.setFlag(SecondaryStatFlag.AdrenalineSurge);
      this.toRemote.setFlag(SecondaryStatFlag.ConsumingFlamesVI);
      this.toRemote.setFlag(SecondaryStatFlag.BattlePvP_Helena_Mark);
      this.toRemote.setFlag(SecondaryStatFlag.BattlePvP_LangE_Protection);
      this.toRemote.setFlag(SecondaryStatFlag.ShadowSpear);
      this.toRemote.setFlag(SecondaryStatFlag.BattlePvP_Ryude_Sword);
      this.toRemote.setFlag(SecondaryStatFlag.BattlePvP_LangE_Claw);
      this.toRemote.setFlag(SecondaryStatFlag.BattlePvP_Wonky_Attack);
      this.toRemote.setFlag(SecondaryStatFlag.RideVehicle);
   }

   public void setEnDecode4Bytes() {
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.CrewCommandership);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.SoulGazeCriDamR);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.PowerTransferGauge);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.ReturnTeleport);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.ShadowPartner);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.QuiverCatridge);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.ImmuneBarrier);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.NaviFlying);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.Dance);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.Magnet);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.MagnetArea);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.RideVehicle);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.RideVehicleExpire);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.SetBaseDamageByBuff);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.HolyUnity);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.MegaSmasher);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.SiphonVitalityShield);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.RWBarrier);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.BlitzShield);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.DotHealMPPerSecondR);
      this.enDecode4Bytes.setFlag(SecondaryStatFlag.DotHealHPPerSecondR);
   }

   public boolean hasIndieStat(String flagName, int skillID) {
      try {
         Field field = this.getClass().getDeclaredField(flagName);
         if (field != null) {
            Object obj = field.get(this);
            if (obj != null) {
               for (IndieTemporaryStatEntry e : (ArrayList<IndieTemporaryStatEntry>) obj) {
                  if (e.getSkillID() == skillID) {
                     return true;
                  }
               }
            }
         }
      } catch (Exception var8) {
      }

      return false;
   }

   public void putIndieStatValue(String flagName, IndieTemporaryStatEntry entry) {
      try {
         Field field = this.getClass().getDeclaredField(flagName);
         if (field != null) {
            Object obj = field.get(this);
            if (obj != null) {
               List<IndieTemporaryStatEntry> list = (ArrayList) obj;
               boolean check = false;

               for (IndieTemporaryStatEntry e : list) {
                  if (e.getSkillID() == entry.getSkillID()) {
                     e.setStartTime(System.currentTimeMillis());
                     e.setDuration(entry.getDuration());
                     e.setStatValue(entry.getStatValue());
                     check = true;
                  }
               }

               if (!check) {
                  list.add(entry);
               }
            }
         }
      } catch (Exception var9) {
      }
   }

   public void encodeIndieTemporaryStats(PacketEncoder packet, Flag992 toSet, boolean isForeign) {
      List<SecondaryStatFlag> indieFlags = List.of(
            SecondaryStatFlag.indiePartialNotDamaged,
            SecondaryStatFlag.indieGrandCross,
            SecondaryStatFlag.indieQrPointTerm,
            SecondaryStatFlag.indieSummon,
            SecondaryStatFlag.indieBarrier,
            SecondaryStatFlag.indieForceSpeed,
            SecondaryStatFlag.indieFlyAcc);
      if (toSet.hasBuff()) {
         for (int pos = 30; pos >= 1; pos--) {
            for (int j = 31; j >= 0; j--) {
               int vl = 1 << j;
               if ((toSet.getFlags()[pos] & vl) != 0L) {
                  for (SecondaryStatFlag ssf : SecondaryStatFlag.values()) {
                     if (ssf.getBit() == 31 - j + 32 * (30 - pos) && ssf.isIndie()) {
                        try {
                           Field field = this.getClass().getDeclaredField(ssf.name());
                           if (field != null) {
                              field.setAccessible(true);
                              Object obj = field.get(this);
                              if (obj != null) {
                                 List<IndieTemporaryStatEntry> list = (ArrayList) obj;
                                 int size = list.size();
                                 packet.writeInt(size);

                                 for (IndieTemporaryStatEntry entry : list) {
                                    entry.encode(packet);
                                 }
                              }
                           }
                        } catch (Exception var18) {
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void encodeForLocal(PacketEncoder packet, Flag992 toSet, BasicJob job, boolean fromMob) {
      long now = System.currentTimeMillis();
      toSet.encode(packet);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.STR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.INT, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DEX, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LUK, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PAD, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DEF, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MAD, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ACC, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EVA, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EVAR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Craft, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Speed, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Jump, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhancedMaxHP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhancedMaxMP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhancedPAD, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhancedMAD, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhancedDEF, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MagicGuard, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DarkSight, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Booster, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PowerGuard, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Guard, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MaxHP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MaxMP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Invincible, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SoulArrow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Stun, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Shock, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Poison, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Seal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Darkness, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Combo, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlessedHammer, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlessedHammerBig, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SnowCharge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolyCharge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolySymbol, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MesoUp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ShadowPartner, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Steal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PickPocket, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhanceAssassinate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Thaw, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Weakness, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WeaknessMDamage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Curse, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Slow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TimeBomb, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BuffLimit, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Team, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DisOrder, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Thread, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Morph, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Ghost, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Recovery, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BasicStatUp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Stance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SharpEyes, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ManaReflection, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Attract, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Magnet, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MagnetArea, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GuidedArrow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CraftJavelin, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlessMark, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ProfessionalAgent, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk3, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TideOfBattle, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ShadowMomentum, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NoBulletConsume, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StackBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Trinity, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Infinity, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdvancedBless, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IllusionStep, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Blind, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Concentration, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BanMap, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MaxLevelBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Barrier, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DojangShield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReverseInput, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MesoUpByItem, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WealthOfUnion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RuneOfGreed, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ItemUpByItem, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RespectPImmune, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RespectMImmune, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DefenseAtt, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DefenseState, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DojangBerserk, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DojangInvincible, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SoulMasterFinal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DualBladeFinal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KarmaBlade, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ElementReset, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HideAttack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EventRate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComboAbilityBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComboDrain, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComboBarrier, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PartyBarrier, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BodyPressure, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RepeatEffect, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ExpBuffRate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StopPortion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StopMotion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Fear, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MagicShield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MagicResistance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Flying, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IceSkill, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NewFlying, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NaviFlying, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SixthSkillFrozen, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Frozen, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Frozen2, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Web, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Enrage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NotDamaged, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FinalCut, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HowlingAttackDamage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BeastFormDamageUp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Dance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkbeanMinibeenMove, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Sneak, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Mechanic, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DrawBack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BeastFormMaxHP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Dice, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlessingArmor, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlessingArmorIncPAD, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TeleportMasteryOn, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CombatOrders, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Beholder, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DispelItemOption, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DispelItemOptionByField, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Inflation, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.OnixDivineProtection, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Bless, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Explosion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DarkTornado, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncMaxHP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncMaxMP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PVPDamage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PVPDamageSkill, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PVPScoreBonus, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PVPInvincible, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PVPRaceEffect, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolyMagicShell, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.InfinityForce, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AmplifyDamage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KeydownTimeIgnore, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MasterMagicOn, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AsrR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AsrRByItem, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TerR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DamAbsorbShield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Roulette, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RouletteStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Event, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CrewCommandership, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CriticalBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DropRate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PlusExpRate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ItemInvincible, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ItemCritical, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ItemEvade, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Event2, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.VampiricTouch, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DDR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncCriticalDamMin, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncCriticalDamMax, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DeathMark, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PainMark, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.UsefulAdvancedBless, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Lapidification, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.VampDeath, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.VampDeathSummon, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.VenomSnake, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CarnivalAttack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CarnivalDefence, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CarnivalExp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SlowAttack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PyramidEffect, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KillingPoint, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KeyDownMoving, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KeyDownAreaMoving, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CygnusElementSkill, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnoreTargetDEF, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StrikerCorrectionBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Invisible, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReviveOnce, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AntiMagicShell, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnrageCr, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnrageCrDamMin, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LifeTidal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Judgement, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DojangLuckyBonus, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HitCriDamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Larkness, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SmashStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReshuffleSwitch, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SpecialAction, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ArcaneAim, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StopForceAtomInfo, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SoulGazeCriDamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SoulRageCount, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PowerTransferGauge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AffinitySlug, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SoulExalt, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HiddenPieceOn, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BossShield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MobZoneState, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GiveMeHeal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TouchMe, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Contagion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComboUnlimited, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnorePCounter, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnoreAllCounter, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnorePImmune, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnoreAllImmune, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FinalJudgement, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FireAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.VengeanceOfAngel, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HeavensDoor, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Preparation, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BullsEye, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncEffectHPOption, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncEffectMPPotion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SoulMP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FullSoulMP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SoulSkillDamageUp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BleedingToxin, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnoreMobDamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Asura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MegaSmasher, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FlipTheCoin, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SerpentSpirit, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Stimulate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReturnTeleport, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CapDebuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EpicDropRIncrease, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnoreMobPdpR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BdR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Exceed, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DiabolicRecovery, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FinalAttackProp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ExceedOverload, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DevilishPower, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.OverloadCount, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BuckShot, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FireBomb, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HalfStatByDebuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SurplusSupply, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SetBaseDamage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AmaranthGenerator, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StrikerHyperElectric, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EventPointAbsorb, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EventAssemble, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StormBringer, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AccR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DexR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.New_366_12, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PoseType, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CosmikForge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ElementSoul, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CosmikOrb, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GlimmeringTime, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Restoration, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComboCostInc, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TrueSight, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CrossOverChain, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ChillingStep, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Reincarnation, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReincarnationActivate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReincarnationAccept, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DotBasedBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.InfernalVenom, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlessEnsenble, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.QuiverCatridge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdvancedQuiver, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ImmuneBarrier, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CriticalGrowing, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RelicPattern, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.QuickDraw, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BowMasterConcentration, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TimeFastABuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TimeFastBBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GatherDropR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AimBox2D, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TrueSniping, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DebuffTolerance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FairyTears, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DotHealHPPerSecondR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DotHealMPPerSecondR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SpiritGuard, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PreReviveOnce, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SetBaseDamageByBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LimitMP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReflectDamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComboTempest, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MHPCutR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MMPCutR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SelfWeakness, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ElementDarkness, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FlareTrick, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Ember, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Dominion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SiphonVitality, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DarknessAscension, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BossWaitingLinesBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DamageReduce, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ShadowServant, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ShadowIllusion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PMDR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComplusionSlant, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.JaguarSummoned, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.JaguarCount, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SSFShootingAttack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DevilCry, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ShieldAttack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DarkLighting, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BMageAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BMageDeath, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AttackCountX, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BombTime, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.XenonAegisSystem, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AngelicBursterSoulSeeker, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ForceAtomOnOff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NightWalkerBat, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HiddenPossesion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WizardIgnite, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DemonFrenzy, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ShadowSpear, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Helena_Mark, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Helena_windSpirit, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_LangE_Protection, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_LeeMalNyun_ScaleUp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvp_Revive, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkbeanAttackBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RandAreaAttack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk443, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Mike_Shield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Mike_Bugle, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkbeanRelax, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkbeanYoYoStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HowlingGale, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NextAttackEnhance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BeyondNextAttackProb, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AranComboTempestOption, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ViperTimeLeap, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ViperDefenseForm, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RoyalGuardState, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RoyalGuardPrepare, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MichaelSoulLink, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GuardianOfLight, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TriflingWhimOnOff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AddRangeOnOff, now, fromMob);
      if (toSet.check(SecondaryStatFlag.KinesisPsychicPoint)) {
         int value = this.getVarriableInt("KinesisPsychicPointValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(value);
         } else {
            packet.writeShort(value);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("KinesisPsychicPointTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      this.encodeCommon(toSet, packet, SecondaryStatFlag.KinesisPsychicOver, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KinesisIncMastery, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KinesisPsychicEnrageShield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BladeStance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DebuffActiveSkillHPCon, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DebuffIncHP, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BowMasterMortalBlow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AngelicBursterSoulResonance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Fever, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IgnisRore, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TeleportMasteryRange, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FireBarrier, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ChangeFoxMan, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FixCooltime, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncMobRateDummy, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdrenalinBoost, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AranDrain, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AranHuntersTargetingCharge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HiddenHyperLinkMaximization, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWCylinder, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWCombination, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWUnk, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWMagnumBlow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWBarrier, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWBarrierHeal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWMaximizeCannon, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWOverHeat, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RWMovingEvar, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Stigma, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.InstallMaha, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HeavensDoorBlocked, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RuneBlocked, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinPointRocket, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Transform, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnergyBurst, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LightningCascade, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BulletParty, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LoadedDice, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Pray, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ChainArtsFury, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReduceFixDamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkBeanYoyoDamageUp, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolyUnity, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AuraWeapon, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AuraWeaponStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.OverloadMana, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RhoAias, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PsychicTornado, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SpreadThrow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AutoChargeStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MiniCannonBallStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ShadowAssault, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MultipleOption, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk37, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlitzShield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SplitArrow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FreudsProtection, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.OverloadMode, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DemonDamAbsorbShield, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GloryWing, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Ellision, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk39, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SpotLight, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Sylvidia, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WeaponVariety, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EviscerateDebuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.OverDrive, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EtherealForm, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ReadyToDie, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Oblivion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CriticalReinforce, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CurseOfCreation, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CurseOfDestruction, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlackMageAttributes, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StackDamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SkillDamageR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TimeCurse, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TimeTorrent, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HarmonyLink, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FastCharge2, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FullBlessMark, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CrystalChargeMax, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CrystalDeus, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk55, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Ryude_Sword, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Alicia_Bless, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk58, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk59, now, fromMob);
      if (toSet.check(SecondaryStatFlag.SpecterState)) {
         int valuex = this.getVarriableInt("SpecterStateValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuex);
         } else {
            packet.writeShort(valuex);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("SpecterStateTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      if (toSet.check(SecondaryStatFlag.SpectralForm)) {
         int valuexx = this.getVarriableInt("SpectralFormValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexx);
         } else {
            packet.writeShort(valuexx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("SpectralFormTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      if (toSet.check(SecondaryStatFlag.PlainSpellBullets)) {
         int valuexxx = this.getVarriableInt("PlainSpellBulletsValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxx);
         } else {
            packet.writeShort(valuexxx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("PlainSpellBulletsTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      if (toSet.check(SecondaryStatFlag.ScarletSpellBullets)) {
         int valuexxxx = this.getVarriableInt("ScarletSpellBulletsValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxxx);
         } else {
            packet.writeShort(valuexxxx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("ScarletSpellBulletsTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      if (toSet.check(SecondaryStatFlag.GustSpellBullets)) {
         int valuexxxxx = this.getVarriableInt("GustSpellBulletsValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxxxx);
         } else {
            packet.writeShort(valuexxxxx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("GustSpellBulletsTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      if (toSet.check(SecondaryStatFlag.AbysSpellBullets)) {
         int valuexxxxxx = this.getVarriableInt("AbysSpellBulletsValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxxxxx);
         } else {
            packet.writeShort(valuexxxxxx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("AbysSpellBulletsTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      this.encodeCommon(toSet, packet, SecondaryStatFlag.ImpendingDeath, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.OldestAbyss, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Eternity, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolyAdvent, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ZodiacBurst, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattleHolic, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DivineWrath, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.InfinitySpell, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MagicCircuitFullDrive, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MagicCircuitFullDriveStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Solus, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MemoryOfRoot, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DiscoveryWeather, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WillPoison, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk75, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolyMagicShellBlocked, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LightningSpear, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ComboInstinct, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ResonateUltimatum, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StormGuard, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GrandCrossSize, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.QuiverFullBurst, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.InfinityFlameCircle, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SwordBaptism, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SwordOfSoulLight, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MarkOfPhantom, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MarkOfPhantomDebuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WildGrenade, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ChainArtsStrokeVI, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GrandCross, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk90, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk91, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LuckOfUnion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SwordOfLight, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk92, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FirebirdEnergy, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FireBirdSupportActive, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MagicBell, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlackMageCursePmdReduce, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlackMageCurseForbidPortion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlackMageCurse3, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Yaldabaoth, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Aion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_LangE_Claw, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Mike_Protection, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SiphonVitalityShield, now, fromMob);
      if (toSet.check(SecondaryStatFlag.AncientGuardians)) {
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(-1);
         } else {
            packet.writeShort(-1);
         }

         packet.writeInt(this.getPlayer().getJob());
         packet.writeInt(0);
      }

      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Wonky_Attack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Wonky_Heal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BattlePvP_Wonky_Protection, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ProtectionOfAncientWarrior, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_351_02, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_354_01, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_311_01, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MultiSoccerAddBall, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_320_01, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EmpressBless, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EmpressBlessStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ContinousRingReady, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkBeanMatryoshka, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_315_01, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_315_02, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_320_02, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_320_03, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_320_04, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_320_05, now, fromMob);
      if (toSet.check(SecondaryStatFlag.HoyoungAttributes)) {
         int valuexxxxxxx = this.getVarriableInt("HoyoungAttributesValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxxxxxx);
         } else {
            packet.writeShort(valuexxxxxxx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("HoyoungAttributesTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      if (toSet.check(SecondaryStatFlag.CharmPower)) {
         int valuexxxxxxxx = this.getVarriableInt("CharmPowerValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxxxxxxx);
         } else {
            packet.writeShort(valuexxxxxxxx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("CharmPowerTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      this.encodeCommon(toSet, packet, SecondaryStatFlag.CloneAttack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HyperCloneRampage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ButterflyDream, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MastersElixir, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WrathOfGods, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EmpericalKnowledge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HoyoungUpdateSkill, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AncientProtectionMagic, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Graffiti, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KeyDownStart, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_372_2, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DracoSlasher, now, fromMob);
      if (toSet.check(SecondaryStatFlag.Ether)) {
         int valuexxxxxxxxx = this.getVarriableInt("EtherValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxxxxxxxx);
         } else {
            packet.writeShort(valuexxxxxxxxx);
         }

         packet.writeInt(this.getPlayer().getJob());
         int duration = (int) (this.getVarriableLong("EtherTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      this.encodeCommon(toSet, packet, SecondaryStatFlag.Creation, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AetherGuard, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Wonder, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Restore, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Nobility, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Resonance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RuneofPurification, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RuneOfIgnition, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DuskDarkness, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.YellowAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DrainAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlueAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DarkAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DebuffAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.UnionAuraBlow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IceAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KnightsAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ZeroAuraSTR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IncarnationAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BlizzardTempest, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PhotonRay, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdventOfGods, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Revenant, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RevenantRage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AutoChargeStackOnOff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AbyssalLightning, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SpearLightningChain, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RoyalKnights, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DefyingFate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SalamanderMischief, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LawOfGravity, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RepeatingCrossbowCartridge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CrystalGate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ThrowBlasting, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HeavenEarthHumanApparition, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DarknessAura, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WeaponVarietyFinale, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LiberationOrb, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LiberationOrbActive, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EgoWeapon, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RelicUnboundDischarge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LightOfCourage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AfterImageShock, now, fromMob);
      if (toSet.check(SecondaryStatFlag.Possession)) {
         int valuexxxxxxxxxx = this.getVarriableInt("PossessionValue");
         if (this.EnDecode4Byte(toSet)) {
            packet.writeInt(valuexxxxxxxxxx);
         } else {
            packet.writeShort(valuexxxxxxxxxx);
         }

         packet.writeInt(6003);
         int duration = (int) (this.getVarriableLong("PossessionTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      this.encodeCommon(toSet, packet, SecondaryStatFlag.PossessionState, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DeathBlessing, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ThanatosDescent, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Annihilation, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RemainIncense, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GripOfAgony, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DragonFang, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PriorPreparationStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SerenFreeze, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NotIncSerenGauge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SerenDebuffUnk, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PriorPreparation, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CrystalBallOfRenee, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdminFixStatCrit, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdminFixStatCritDam, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdminFixStatIgnoreDefense, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdminFixStatBossDamage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdminFixStatBuffTimeR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdminFixStatActionSpeed, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdrenalinBoostActivate, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HoyoungLastCheonJiInSkillSet, now, fromMob);
      if (toSet.check(SecondaryStatFlag.Yeti_Rage)) {
         packet.writeShort(1);
         packet.writeInt(13500);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Yeti_RageOn)) {
         packet.writeShort(1);
         packet.writeInt(13500);
         int duration = (int) (this.getVarriableLong("Yeti_RageOnTill") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }

      this.encodeCommon(toSet, packet, SecondaryStatFlag.Yeti_Spicy, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Yeti_MyFriendPepe, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkBean_MagicShowtimeStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ExpBuffBlock, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PinkBeanExpBuffBlock, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Unk_1123_1, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DragonVeinReading, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MountainsSeedsStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.MountainGuardian, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RiverPuddleDrench, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DesolateWinds, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SunlightSprout, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FreeDragonVeinStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FlagRace_CramponShoes, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LotusFlower, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LaLa_LinkSkill, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TraceOfDragonVeinStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.BossFieldFinalDamR, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.StormWhim, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SeaSerpent, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SerpentStone, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SerpentScrew, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FlameSweep, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Cosmos, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhanceArrow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.EnhanceSniping, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.UltimateSniping, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.QuadrupleThrow, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.UntiringNectar, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ScarringSword, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.IceAuraTornado, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolyWater, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.TriumphFeather, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AngelRay, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GrandFinale, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AscendantShade, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FlashMirage, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.HolyBlood, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.OrbitalExplosion, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.PhoenixDrive, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LimitBreak, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Transcendents, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SixthAssassination, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SixthAssassinationDarkSight, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ArtificialEvolution, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.FrozenLighting, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.UltimateBPM, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Elemental_Knights, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_370_5, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SummonChakri, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.VoidEnhance, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.VoidBurst, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DeceivingBlade, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CurseEnchant, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.DoolInducedCurrent, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Karing2PhasePurify, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Karing3PhaseLightOfWill, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.karing2phaseConfine, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_376_724, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_376_725, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.KaringDisruptDeBuff, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_379_743, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_379_744, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AllThings, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ConsumingFlamesVI, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.SixthStormArrowEx, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_379_754, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WildVulcanAdv, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.WildVulcanAdvStack, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.CraftEnchantJavelin, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.UnlimitedCrystal, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NaturesBelief, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AdrenalineSurge, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.GloryWingEnchantJavelin, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.LifeAndDeath, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.NEW_379_760, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.ForsakenRelic, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.Nightmare, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.AwakenAbyss, now, fromMob);
      this.encodeCommon(toSet, packet, SecondaryStatFlag.RapidFire, now, fromMob);
      if (toSet.check(SecondaryStatFlag.SoulMP)) {
         packet.writeInt(this.SoulMPX);
         packet.writeInt(this.SoulMPReason);
      }

      if (toSet.check(SecondaryStatFlag.FullSoulMP)) {
         packet.writeInt(this.FullSoulMPX);
      }

      packet.writeShort(this.buffedForSpecMaps.size());
      this.buffedForSpecMaps.forEach(m -> m.encode(packet));
      packet.write(this.defenseAtt);
      packet.write(this.defenseState);
      packet.write(this.pvpDamage);
      packet.writeInt(this.unk1);
      if (toSet.check(SecondaryStatFlag.Dice)) {
         job.encodeForLocal(SecondaryStatFlag.Dice, packet);
      }

      if (toSet.check(SecondaryStatFlag.CurseOfCreation)) {
         job.encodeForLocal(SecondaryStatFlag.CurseOfCreation, packet);
      }

      if (toSet.check(SecondaryStatFlag.CurseOfDestruction)) {
         job.encodeForLocal(SecondaryStatFlag.CurseOfDestruction, packet);
      }

      if (toSet.check(SecondaryStatFlag.Unk76)) {
         job.encodeForLocal(SecondaryStatFlag.Unk76, packet);
      }

      if (toSet.check(SecondaryStatFlag.KeyDownMoving)) {
         packet.writeInt(this.getPlayer().getViperEnergyCharge());
      }

      if (toSet.check(SecondaryStatFlag.PinkbeanRollingGrade)) {
         packet.write(this.PinkbeanRollingGradeValue);
      }

      if (toSet.check(SecondaryStatFlag.Judgement)) {
         job.encodeForLocal(SecondaryStatFlag.Judgement, packet);
      }

      if (toSet.check(SecondaryStatFlag.Infinity)) {
         packet.writeInt(this.InfinityTill);
      }

      if (toSet.check(SecondaryStatFlag.StackBuff)) {
         job.encodeForLocal(SecondaryStatFlag.StackBuff, packet);
      }

      if (toSet.check(SecondaryStatFlag.Trinity)) {
         job.encodeForLocal(SecondaryStatFlag.Trinity, packet);
      }

      if (toSet.check(SecondaryStatFlag.HolyCharge)) {
         job.encodeForLocal(SecondaryStatFlag.HolyCharge, packet);
      }

      if (toSet.check(SecondaryStatFlag.LifeTidal)) {
         job.encodeForLocal(SecondaryStatFlag.LifeTidal, packet);
      }

      if (toSet.check(SecondaryStatFlag.AntiMagicShell)) {
         job.encodeForLocal(SecondaryStatFlag.AntiMagicShell, packet);
      }

      if (toSet.check(SecondaryStatFlag.Larkness)) {
         job.encodeForLocal(SecondaryStatFlag.Larkness, packet);
      }

      if (toSet.check(SecondaryStatFlag.IgnoreTargetDEF)) {
         job.encodeForLocal(SecondaryStatFlag.IgnoreTargetDEF, packet);
      }

      if (toSet.check(SecondaryStatFlag.StrikerCorrectionBuff)) {
         job.encodeForLocal(SecondaryStatFlag.StrikerCorrectionBuff, packet);
      }

      if (toSet.check(SecondaryStatFlag.StopForceAtomInfo)) {
         encodeStopForceAtom(this.getPlayer(), packet, SecondaryStatFlag.StopForceAtomInfo, true);
      }

      if (toSet.check(SecondaryStatFlag.SmashStack)) {
         job.encodeForLocal(SecondaryStatFlag.SmashStack, packet);
      }

      if (toSet.check(SecondaryStatFlag.MobZoneState)) {
         for (int mobZoneState : this.getPlayer().getMobZoneState()) {
            packet.writeInt(mobZoneState);
         }

         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.SkillDamageR)) {
         job.encodeForLocal(SecondaryStatFlag.SkillDamageR, packet);
      }

      if (toSet.check(SecondaryStatFlag.Slow)) {
         packet.write(0);
      }

      if (toSet.check(SecondaryStatFlag.IgnoreMobPdpR)) {
         packet.write(0);
      }

      if (toSet.check(SecondaryStatFlag.BdR)) {
         packet.write(true);
      }

      if (toSet.check(SecondaryStatFlag.EpicDropRIncrease)) {
         packet.writeInt(0);
         packet.write(0);
      }

      if (toSet.check(SecondaryStatFlag.PoseType)) {
         job.encodeForLocal(SecondaryStatFlag.PoseType, packet);
      }

      if (toSet.check(SecondaryStatFlag.Beholder)) {
         if (job instanceof DarkKnight) {
            job.encodeForLocal(SecondaryStatFlag.Beholder, packet);
         } else {
            packet.writeInt(0);
         }
      }

      if (toSet.check(SecondaryStatFlag.CrossOverChain)) {
         job.encodeForLocal(SecondaryStatFlag.CrossOverChain, packet);
      }

      if (toSet.check(SecondaryStatFlag.ImmuneBarrier)) {
         packet.writeInt(this.ImmuneBarrierX);
      }

      if (toSet.check(SecondaryStatFlag.Stance)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.SharpEyes)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.AdvancedBless)) {
         job.encodeForLocal(SecondaryStatFlag.AdvancedBless, packet);
      }

      if (toSet.check(SecondaryStatFlag.UsefulAdvancedBless)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Bless)) {
         job.encodeForLocal(SecondaryStatFlag.Bless, packet);
      }

      if (toSet.check(SecondaryStatFlag.DotHealHPPerSecondR)) {
         job.encodeForLocal(SecondaryStatFlag.DotHealHPPerSecondR, packet);
      }

      if (toSet.check(SecondaryStatFlag.DotHealMPPerSecondR)) {
         job.encodeForLocal(SecondaryStatFlag.DotHealMPPerSecondR, packet);
      }

      if (toSet.check(SecondaryStatFlag.SpiritGuard)) {
         packet.writeInt(this.getPlayer().getSpiritWardCount());
      }

      if (toSet.check(SecondaryStatFlag.DemonDamAbsorbShield)) {
         packet.writeInt(this.getPlayer().getDemonDamAbsorbShieldX());
      }

      if (toSet.check(SecondaryStatFlag.KnockBack)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.ShieldAttack)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.SSFShootingAttack)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.BattlePvP_Helena_Mark)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.PinkbeanAttackBuff)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.RoyalGuardState)) {
         job.encodeForLocal(SecondaryStatFlag.RoyalGuardState, packet);
      }

      if (toSet.check(SecondaryStatFlag.MichaelSoulLink)) {
         job.encodeForLocal(SecondaryStatFlag.MichaelSoulLink, packet);
      }

      if (toSet.check(SecondaryStatFlag.AdrenalinBoost)) {
         packet.write(this.getPlayer().getAdrenalinBoostCount());
      }

      if (toSet.check(SecondaryStatFlag.RWCylinder)) {
         job.encodeForLocal(SecondaryStatFlag.RWCylinder, packet);
      }

      if (toSet.check(SecondaryStatFlag.StackDamR)) {
         packet.writeInt(this.getPlayer().getBodyOfSteal());
      }

      if (toSet.check(SecondaryStatFlag.RWMagnumBlow)) {
         job.encodeForLocal(SecondaryStatFlag.RWMagnumBlow, packet);
      }

      if (toSet.check(SecondaryStatFlag.BladeStance)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.DarkSight)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Stigma)) {
         packet.writeInt(this.getPlayer().getStigmaMax());
      }

      if (toSet.check(SecondaryStatFlag.ProfessionalAgent)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.CriticalGrowing)) {
         job.encodeForLocal(SecondaryStatFlag.CriticalGrowing, packet);
      }

      if (toSet.check(SecondaryStatFlag.Ember)) {
         job.encodeForLocal(SecondaryStatFlag.Ember, packet);
      }

      if (toSet.check(SecondaryStatFlag.PickPocket)) {
         if (job instanceof Shadower) {
            job.encodeForLocal(SecondaryStatFlag.PickPocket, packet);
         } else {
            packet.writeInt(0);
         }
      }

      if (toSet.check(SecondaryStatFlag.HolyUnity)) {
         job.encodeForLocal(SecondaryStatFlag.HolyUnity, packet);
      }

      if (toSet.check(SecondaryStatFlag.DemonFrenzy)) {
         packet.writeShort(0);
      }

      if (toSet.check(SecondaryStatFlag.ShadowSpear)) {
         packet.writeShort(0);
      }

      if (toSet.check(SecondaryStatFlag.RhoAias)) {
         job.encodeForLocal(SecondaryStatFlag.RhoAias, packet);
      }

      if (toSet.check(SecondaryStatFlag.VampDeath)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.HolyMagicShell)) {
         packet.writeInt(this.getPlayer().getHolyMagicShellW());
      }

      for (SecondaryStatFlag flag : SecondaryStatFlag.values()) {
         if (toSet.check(flag) && aTS_StatFlag(flag)) {
            this.encodeTwoStateTemporaryStat(this.getPlayer(), packet, flag);
         }
      }

      this.encodeIndieTemporaryStats(packet, toSet, false);
      if (toSet.check(SecondaryStatFlag.UsingScouter)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Unk39)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.GloryWing)) {
         packet.writeInt(this.getPlayer().isUseMortalWingBit() ? 0 : 1);
         packet.writeInt(1);
      }

      if (toSet.check(SecondaryStatFlag.BlessMark)) {
         packet.writeInt(this.BlessMarkIcon);
         packet.writeInt(this.BlessMarkMax);
      }

      if (toSet.check(SecondaryStatFlag.EviscerateDebuff)) {
         job.encodeForLocal(SecondaryStatFlag.EviscerateDebuff, packet);
      }

      if (toSet.check(SecondaryStatFlag.WeaponVariety)) {
         job.encodeForLocal(SecondaryStatFlag.WeaponVariety, packet);
      }

      if (toSet.check(SecondaryStatFlag.OverloadMode)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.SpecterState)) {
         job.encodeForLocal(SecondaryStatFlag.SpecterState, packet);
      }

      if (toSet.check(SecondaryStatFlag.PlainSpellBullets)) {
         job.encodeForLocal(SecondaryStatFlag.PlainSpellBullets, packet);
      }

      if (toSet.check(SecondaryStatFlag.ScarletSpellBullets)) {
         job.encodeForLocal(SecondaryStatFlag.ScarletSpellBullets, packet);
      }

      if (toSet.check(SecondaryStatFlag.GustSpellBullets)) {
         job.encodeForLocal(SecondaryStatFlag.GustSpellBullets, packet);
      }

      if (toSet.check(SecondaryStatFlag.AbysSpellBullets)) {
         job.encodeForLocal(SecondaryStatFlag.AbysSpellBullets, packet);
      }

      if (toSet.check(SecondaryStatFlag.WillPoison)) {
         packet.writeInt(30);
      }

      if (toSet.check(SecondaryStatFlag.InfinityFlameCircle)) {
         job.encodeForLocal(SecondaryStatFlag.InfinityFlameCircle, packet);
      }

      if (toSet.check(SecondaryStatFlag.MarkOfPhantom)) {
         job.encodeForLocal(SecondaryStatFlag.MarkOfPhantom, packet);
      }

      if (toSet.check(SecondaryStatFlag.MarkOfPhantomDebuff)) {
         job.encodeForLocal(SecondaryStatFlag.MarkOfPhantomDebuff, packet);
      }

      if (toSet.check(SecondaryStatFlag.NightWalkerBat)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.MagicBell)) {
         if (DBConfig.isGanglim) {
            packet.writeInt(10 - this.getPlayer().getKeyValue(100857, "feverCnt"));
         } else {
            packet.writeInt(this.MagicBellX);
         }
      }

      if (toSet.check(SecondaryStatFlag.NEW_315_01)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.NEW_320_05)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.BlackMageCursePmdReduce)) {
         packet.writeInt(this.getPlayer().isDisableEquipChange() ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.BlackMageCurse3)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Combo)) {
         packet.writeInt(this.getPlayer().getComboX());
         packet.writeShort(this.ComboMaxValue);
         packet.writeShort(this.ComboRate);
      }

      if (toSet.check(SecondaryStatFlag.EmpressBless)) {
         packet.writeInt(this.EmpressBlessX);
      }

      if (toSet.check(SecondaryStatFlag.AncientGuardians)) {
         packet.writeInt((int) System.currentTimeMillis());
         packet.writeInt(this.getPlayer().getAncientGuidance());
      }

      if (toSet.check(SecondaryStatFlag.BattlePvP_Wonky_Attack)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.BattlePvP_Wonky_Protection)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.HolySymbol)) {
         job.encodeForLocal(SecondaryStatFlag.HolySymbol, packet);
      }

      if (toSet.check(SecondaryStatFlag.NEW_320_04)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.HoyoungAttributes)) {
         job.encodeForLocal(SecondaryStatFlag.HoyoungAttributes, packet);
      }

      if (toSet.check(SecondaryStatFlag.CharmPower)) {
         job.encodeForLocal(SecondaryStatFlag.CharmPower, packet);
      }

      if (toSet.check(SecondaryStatFlag.WrathOfGods)) {
         packet.writeInt(1);
      }

      if (toSet.check(SecondaryStatFlag.EmpericalKnowledge)) {
         job.encodeForLocal(SecondaryStatFlag.EmpericalKnowledge, packet);
      }

      if (toSet.check(SecondaryStatFlag.Graffiti)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Nobility)) {
         job.encodeForLocal(SecondaryStatFlag.Nobility, packet);
      }

      if (toSet.check(SecondaryStatFlag.Revenant)) {
         packet.writeInt(this.getPlayer().getRevenantRage());
      }

      if (toSet.check(SecondaryStatFlag.RevenantRage)) {
         packet.writeInt(this.getPlayer().getRevenantRage());
         packet.writeInt(this.getPlayer().getRemainRevenantCount());
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.AutoChargeStackOnOff)) {
         job.encodeForLocal(SecondaryStatFlag.AutoChargeStackOnOff, packet);
      }

      if (toSet.check(SecondaryStatFlag.RuneofPurification)) {
         packet.writeInt(this.RuneofPurificationGuage);
      }

      if (toSet.check(SecondaryStatFlag.YellowAura)) {
         packet.writeInt(this.YellowAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.YellowAura) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.DrainAura)) {
         packet.writeInt(this.DrainAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.DrainAura) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.BlueAura)) {
         packet.writeInt(this.BlueAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.BlueAura) ? 1 : 0);
         packet.writeInt(this.BlueAuraDispelCount);
      }

      if (toSet.check(SecondaryStatFlag.DarkAura)) {
         packet.writeInt(this.DarkAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.DarkAura) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.DebuffAura)) {
         packet.writeInt(this.DebuffAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.DebuffAura) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.UnionAuraBlow)) {
         packet.writeInt(this.UnionAuraBlowFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.UnionAuraBlow) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.IceAura)) {
         packet.writeInt(this.IceAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.IceAura) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.KnightsAura)) {
         packet.writeInt(this.KnightsAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.KnightsAura) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.ZeroAuraSTR)) {
         packet.writeInt(this.ZeroAuraSTRFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.ZeroAuraSTR) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.IncarnationAura)) {
         packet.writeInt(this.IncarnationAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.IncarnationAura) ? 1 : 0);
      }

      if (toSet.check(SecondaryStatFlag.BlizzardTempest)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.PhotonRay)) {
         packet.writeInt(this.getPlayer().getPhotonRayCharge());
      }

      if (toSet.check(SecondaryStatFlag.AbyssalLightning)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.LawOfGravity)) {
         job.encodeForLocal(SecondaryStatFlag.LawOfGravity, packet);
      }

      if (toSet.check(SecondaryStatFlag.CrystalGate)) {
         job.encodeForLocal(SecondaryStatFlag.CrystalGate, packet);
      }

      if (toSet.check(SecondaryStatFlag.HolyWater)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.WeaponVarietyFinale)) {
         job.encodeForLocal(SecondaryStatFlag.WeaponVarietyFinale, packet);
      }

      if (toSet.check(SecondaryStatFlag.LiberationOrb)) {
         packet.writeInt(this.getPlayer().getLiberationOrbLightMad());
         packet.writeInt(this.getPlayer().getLiberationOrbDarkMad());
      }

      if (toSet.check(SecondaryStatFlag.DarknessAura)) {
         packet.writeInt(this.getPlayer().getDarknessAuraStack());
      }

      if (toSet.check(SecondaryStatFlag.SerpentScrew)) {
         packet.writeInt(this.SerpentScrewRemainCount);
         packet.writeInt(this.SerpentScrewBossAttackCount);
      }

      if (toSet.check(SecondaryStatFlag.LiberationOrbActive)) {
         packet.writeInt(this.LiberationOrbActiveRemainCount);
      }

      if (toSet.check(SecondaryStatFlag.ThanatosDescent)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.DragonFang)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Magnet)) {
         job.encodeForLocal(SecondaryStatFlag.Magnet, packet);
      }

      if (toSet.check(SecondaryStatFlag.HoyoungLastCheonJiInSkillSet)) {
         packet.writeInt(this.HoyoungLastCheonJiInSkill);
         packet.writeInt(this.HoyoungLastCheonJiInSkill);
      }

      if (toSet.check(SecondaryStatFlag.Yeti_Rage)) {
         packet.writeInt(this.Yeti_RageValue);
      }

      if (toSet.check(SecondaryStatFlag.Yeti_RageOn)) {
         packet.writeInt(this.Yeti_RageOnValue);
      }

      if (toSet.check(SecondaryStatFlag.ExpBuffBlock)) {
         packet.writeInt(this.ExpBuffBlockValue);
         packet.write(0);
      }

      if (toSet.check(SecondaryStatFlag.PinkBeanExpBuffBlock)) {
         packet.writeInt(this.PinkBeanExpBuffBlockValue);
         packet.write(0);
      }

      if (toSet.check(SecondaryStatFlag.NewFlying)) {
         packet.writeInt(this.NewFlyingValue);
      }

      if (toSet.check(SecondaryStatFlag.ReincarnationActivate)) {
         packet.writeInt(this.getPlayer().getReincarnationMaxCount());
      }

      if (toSet.check(SecondaryStatFlag.QuiverFullBurst)) {
         packet.writeInt(this.QuiverFullBurst);
      }

      if (toSet.check(SecondaryStatFlag.ElementSoul)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.NEW_376_725)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.KaringDisruptDeBuff)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.karing2phaseConfine)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.NEW_376_724)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.NEW_379_743)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.ContinousRingReady)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.BMageDeath)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.RapidFire)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.Nightmare)) {
         packet.writeInt(0);
      }

      if (toSet.check(SecondaryStatFlag.CraftEnchantJavelin)) {
         job.encodeForLocal(SecondaryStatFlag.CraftEnchantJavelin, packet);
      }

      if (toSet.check(SecondaryStatFlag.GloryWingEnchantJavelin)) {
         job.encodeForLocal(SecondaryStatFlag.GloryWingEnchantJavelin, packet);
      }

      if (toSet.check(SecondaryStatFlag.LimitBreak)) {
         packet.writeInt(5000);
      }

      packet.writeShort(this.delay);
      packet.write(this.optionForIcon);
      packet.write(this.justBuffCheck);
      packet.write(this.isFirstSet());
      packet.write(true);
      packet.write(true);
      if (isMovementAffectingStat(toSet)) {
         packet.write(0);
      }

      packet.writeInt(this.getAttackCount());
      packet.write(1);
   }

   public void encodeCommon(Flag992 flag992, PacketEncoder packet, SecondaryStatFlag flag, long now, boolean fromMob) {
      if (flag992.check(flag)) {
         int value = this.getVarriableInt(flag.name() + "Value");
         if (this.EnDecode4Byte(flag992)) {
            packet.writeInt(value);
         } else {
            packet.writeShort(value);
         }

         int reason = this.getVarriableInt(flag.name() + "Reason");
         if (fromMob) {
            int skillLevel = this.getVarriableInt(flag.name() + "Level");
            packet.writeShort(reason);
            packet.writeShort(skillLevel);
         } else {
            packet.writeInt(this.getVarriableInt(flag.name() + "Reason"));
         }

         int duration = (int) (this.getVarriableLong(flag.name() + "Till") - now);
         if (duration >= 2000000000) {
            duration = 0;
         }

         packet.writeInt(duration);
      }
   }

   public Integer getValue(SecondaryStatFlag flag) {
      Object obj = this.getVarriable(flag.name() + "Value");
      return obj != null ? (Integer) obj : null;
   }

   public int getReason(SecondaryStatFlag flag) {
      return this.getVarriableInt(flag.name() + "Reason");
   }

   public long getTill(SecondaryStatFlag flag) {
      return this.getVarriableLong(flag.name() + "Till");
   }

   public long getIndieTill(SecondaryStatFlag stat, int skillID) {
      try {
         Field field = this.getClass().getDeclaredField(stat.name());
         if (field != null) {
            field.setAccessible(true);
            SecondaryStatFlag f = SecondaryStatFlag.getByName(field.getName());
            if (this.flag.check(f)) {
               Object obj = field.get(this);
               if (obj != null) {
                  for (IndieTemporaryStatEntry se : (ArrayList<IndieTemporaryStatEntry>) obj) {
                     if (se.getSkillID() == skillID) {
                        return se.getStartTime() + se.getDuration();
                     }
                  }
               }
            }
         }
      } catch (Exception var9) {
      }

      return 0L;
   }

   public int getIndieValue(SecondaryStatFlag stat, int skillID) {
      try {
         Field field = this.getClass().getDeclaredField(stat.name());
         if (field != null) {
            field.setAccessible(true);
            SecondaryStatFlag f = SecondaryStatFlag.getByName(field.getName());
            if (this.flag.check(f)) {
               Object obj = field.get(this);
               if (obj != null) {
                  for (IndieTemporaryStatEntry se : (ArrayList<IndieTemporaryStatEntry>) obj) {
                     if (se.getSkillID() == skillID) {
                        return se.getStatValue();
                     }
                  }
               }
            }
         }
      } catch (Exception var9) {
      }

      return 0;
   }

   public int getLevel(SecondaryStatFlag flag) {
      return this.getVarriableInt(flag.name() + "Level");
   }

   public int getFromID(SecondaryStatFlag flag) {
      return this.getVarriableInt(flag.name() + "FromID");
   }

   public boolean getVarriableBoolean(String name) {
      boolean v = false;
      Object obj = this.getVarriable(name);
      if (obj != null) {
         v = (Boolean) obj;
      }

      return v;
   }

   public int getVarriableInt(String name) {
      int v = 0;
      Object obj = this.getVarriable(name);
      if (obj != null) {
         v = (Integer) obj;
      }

      return v;
   }

   public long getVarriableLong(String name) {
      long v = 0L;
      Object obj = this.getVarriable(name);
      if (obj != null) {
         v = (Long) obj;
      }

      return v;
   }

   public Object getVarriable(String name) {
      try {
         Field field = this.getClass().getDeclaredField(name);
         return field != null ? field.get(this) : null;
      } catch (Exception var3) {
         return null;
      }
   }

   public void setVarriableBoolean(String name, boolean value) {
      try {
         Field field = this.getClass().getDeclaredField(name);
         if (field != null) {
            field.setAccessible(true);
            field.setBoolean(this, value);
         }
      } catch (Exception var4) {
      }
   }

   public void setVarriableInt(String name, int value) {
      try {
         Field field = this.getClass().getDeclaredField(name);
         if (field != null) {
            field.setAccessible(true);
            field.setInt(this, value);
         }
      } catch (Exception var4) {
      }
   }

   public void setVarriableLong(String name, long value) {
      try {
         Field field = this.getClass().getDeclaredField(name);
         if (field != null) {
            field.setAccessible(true);
            field.setLong(this, value);
         }
      } catch (Exception var5) {
      }
   }

   public void putStatValue(String name, int skillID, int skillLevel, int value, long till) {
      this.putStatValue(name, skillID, skillLevel, value, till, 0);
   }

   public void putStatValue(String name, int skillID, int skillLevel, int value, long till, int fromID) {
      this.setVarriableInt(name + "Value", value);
      this.setVarriableInt(name + "Reason", skillID);
      this.setVarriableInt(name + "Level", skillLevel);
      this.setVarriableLong(name + "Till", till);
      this.setVarriableInt(name + "FromID", fromID);
   }

   public void changeStatValue(String name, int value) {
      this.setVarriableInt(name + "Value", value);
   }

   public void changeTill(String name, int value) {
      this.changeTill(name, value, 0);
   }

   public void changeTill(String name, int value, int limit) {
      if (limit != 0) {
         int v = (int) (this.getVarriableLong(name + "Till") - System.currentTimeMillis()) + value;
         if (v > limit) {
            int delta = v - limit;
            value -= delta;
         }
      }

      this.setVarriableLong(name + "Till", this.getVarriableLong(name + "Till") + value);
   }

   public void changeIndieTill(String name, int skillID, int value) {
      try {
         Field field = this.getClass().getDeclaredField(name);
         if (field != null) {
            field.setAccessible(true);
            SecondaryStatFlag flag = SecondaryStatFlag.getByName(field.getName());
            if (flag.name().equals(name)) {
               Object obj = field.get(this);
               if (obj != null) {
                  for (IndieTemporaryStatEntry entry : (ArrayList<IndieTemporaryStatEntry>) obj) {
                     if (entry.getSkillID() == skillID) {
                        entry.setDuration(entry.getDuration() + value);
                     }
                  }
               }
            }
         }
      } catch (Exception var10) {
      }
   }

   public void changeIndieStatValue(String name, int skillID, int value) {
      try {
         Field field = this.getClass().getDeclaredField(name);
         if (field != null) {
            field.setAccessible(true);
            SecondaryStatFlag flag = SecondaryStatFlag.getByName(field.getName());
            if (flag.name().equals(name)) {
               Object obj = field.get(this);
               if (obj != null) {
                  for (IndieTemporaryStatEntry entry : (ArrayList<IndieTemporaryStatEntry>) obj) {
                     if (entry.getSkillID() == skillID) {
                        entry.setStatValue(value);
                     }
                  }
               }
            }
         }
      } catch (Exception var10) {
      }
   }

   public void encodeCommonForRemote(PacketEncoder packet, SecondaryStatFlag flag, Flag992 flag992) {
      if (flag992.check(flag)) {
         int value = this.getVarriableInt(flag.name() + "Value");
         if (value == -99999) {
            value = 0;
         }

         packet.writeShort((short) value);
         int reason = this.getVarriableInt(flag.name() + "Reason");
         if (reason >= 0 && reason <= 255) {
            packet.writeShort(reason);
            packet.writeShort(this.getVarriableInt(flag.name() + "Level"));
         } else {
            packet.writeInt(reason == -99999 ? 0 : reason);
         }
      }
   }

   Flag992 encodeForRemoteFlag() {
      Flag992 clone = new Flag992(this.toRemote.getFlags());
      List<SecondaryStatFlag> removeList = new ArrayList<>();

      for (SecondaryStatFlag flag : SecondaryStatFlag.values()) {
         if (clone.check(flag)) {
            int value = this.getVarriableInt(flag.name() + "Value");
            if (value == -99999) {
               removeList.add(flag);
            }
         }
      }

      for (SecondaryStatFlag flagx : removeList) {
         clone.removeFlag(flagx);
      }

      return clone;
   }

   public void encodeForRemote(PacketEncoder packet, Flag992 flag, BasicJob job, boolean forEnterfield) {
      Flag992 flag992;
      if (forEnterfield) {
         flag992 = this.encodeForRemoteFlag();
      } else {
         flag992 = flag.FilterForRemote(flag);
      }

      flag992.encode(packet);
      if (flag992.check(SecondaryStatFlag.Speed)) {
         packet.write(this.SpeedValue == -99999 ? 0 : this.SpeedValue);
      }

      if (flag992.check(SecondaryStatFlag.Combo)) {
         packet.write(this.ComboValue == -99999 ? 0 : this.ComboValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.BlessedHammer, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SnowCharge, flag992);
      if (flag992.check(SecondaryStatFlag.HolyCharge)) {
         packet.writeShort(this.HolyChargeValue == -99999 ? 0 : this.HolyChargeValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Stun, flag992);
      if (flag992.check(SecondaryStatFlag.Shock)) {
         packet.write(this.ShockValue == -99999 ? 0 : this.ShockValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Darkness, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Seal, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Weakness, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.WeaknessMDamage, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Curse, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Slow, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.PVPRaceEffect, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.TimeBomb, flag992);
      if (flag992.check(SecondaryStatFlag.Team)) {
         packet.write(this.TeamValue == -99999 ? 0 : this.TeamValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.DisOrder, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Thread, flag992);
      if (flag992.check(SecondaryStatFlag.Poison)) {
         packet.writeShort(this.PoisonValue == -99999 ? 0 : this.PoisonValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Poison, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ShadowPartner, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Morph, flag992);
      if (flag992.check(SecondaryStatFlag.Ghost)) {
         packet.writeShort(this.GhostValue == -99999 ? 0 : this.GhostValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Attract, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Magnet, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.MagnetArea, flag992);
      if (flag992.check(SecondaryStatFlag.NoBulletConsume)) {
         packet.writeInt(this.NoBulletConsumeValue == -99999 ? 0 : this.NoBulletConsumeValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.BanMap, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Barrier, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DojangShield, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ReverseInput, flag992);
      if (flag992.check(SecondaryStatFlag.RespectPImmune)) {
         packet.writeInt(this.RespectPImmuneValue == -99999 ? 0 : this.RespectPImmuneValue);
      }

      if (flag992.check(SecondaryStatFlag.RespectMImmune)) {
         packet.writeInt(this.RespectMImmuneValue == -99999 ? 0 : this.RespectMImmuneValue);
      }

      if (flag992.check(SecondaryStatFlag.DefenseAtt)) {
         packet.writeInt(this.DefenseAttValue == -99999 ? 0 : this.DefenseAttValue);
      }

      if (flag992.check(SecondaryStatFlag.DefenseState)) {
         packet.writeInt(this.DefenseStateValue == -99999 ? 0 : this.DefenseStateValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.DojangBerserk, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.RepeatEffect, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.StopPortion, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.StopMotion, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Fear, flag992);
      if (flag992.check(SecondaryStatFlag.MagicShield)) {
         packet.writeInt(this.MagicShieldValue == -99999 ? 0 : this.MagicShieldValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Frozen, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Frozen2, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Web, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DrawBack, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.FinalCut, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Mechanic, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Inflation, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Explosion, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DarkTornado, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AmplifyDamage, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HideAttack, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DevilishPower, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.CrewCommandership, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Event, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Event2, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DeathMark, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.PainMark, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Lapidification, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.VampDeath, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.VampDeathSummon, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.VenomSnake, flag992);
      if (flag992.check(SecondaryStatFlag.PyramidEffect)) {
         packet.writeInt(this.PyramidEffectValue == -99999 ? 0 : this.PyramidEffectValue);
      }

      if (flag992.check(SecondaryStatFlag.PinkbeanRollingGrade)) {
         packet.write(this.PinkbeanRollingGradeValue == -99999 ? 0 : this.PinkbeanRollingGradeValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.IgnoreTargetDEF, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.StrikerCorrectionBuff, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Invisible, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Judgement, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.KeyDownAreaMoving, flag992);
      if (flag992.check(SecondaryStatFlag.StackBuff)) {
         packet.writeShort(this.StackBuffValue == -99999 ? 0 : this.StackBuffValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Larkness, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ReshuffleSwitch, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SpecialAction, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.StopForceAtomInfo, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SoulGazeCriDamR, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.PowerTransferGauge, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BlitzShield, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AffinitySlug, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SoulExalt, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HiddenPieceOn, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SmashStack, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.MobZoneState, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.GiveMeHeal, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.TouchMe, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Contagion, flag992);
      if (flag992.check(SecondaryStatFlag.Contagion)) {
         packet.writeInt(0);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.ComboUnlimited, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IgnorePCounter, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IgnoreAllCounter, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IgnorePImmune, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IgnoreAllImmune, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.FinalJudgement, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.FireAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HeavensDoor, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DamAbsorbShield, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AntiMagicShell, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NotDamaged, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BleedingToxin, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DualBladeFinal, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.KarmaBlade, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IgnoreMobDamR, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Asura, flag992);
      if (flag992.check(SecondaryStatFlag.MegaSmasher)) {
         int value = this.getVarriableInt("MegaSmasherValue");
         if (value == -99999) {
            value = 0;
         }

         packet.writeInt(value);
         int reason = this.getVarriableInt("MegaSmasherReason");
         packet.writeInt(reason == -99999 ? 0 : reason);
      }

      if (flag992.check(SecondaryStatFlag.MegaSmasher)) {
         packet.writeInt(0);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.SerpentSpirit, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Stimulate, flag992);
      if (flag992.check(SecondaryStatFlag.ReturnTeleport)) {
         int value = this.getVarriableInt("ReturnTeleportValue");
         if (value == -99999) {
            value = 0;
         }

         packet.write(value);
         int reason = this.getVarriableInt("ReturnTeleportReason");
         if (reason >= 0 && reason <= 255) {
            packet.writeShort(reason);
            packet.writeShort(this.getVarriableInt("ReturnTeleportLevel"));
         } else {
            packet.writeInt(reason == -99999 ? 0 : reason);
         }
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.CapDebuff, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.OverloadCount, flag992);
      if (flag992.check(SecondaryStatFlag.FireBomb)) {
         int valuex = this.getVarriableInt("FireBombValue");
         if (valuex == -99999) {
            valuex = 0;
         }

         packet.write(valuex);
         int reason = this.getVarriableInt("FireBombReason");
         if (reason >= 0 && reason <= 255) {
            packet.writeShort(reason);
            packet.writeShort(this.getVarriableInt("FireBombLevel"));
         } else {
            packet.writeInt(reason == -99999 ? 0 : reason);
         }
      }

      if (flag992.check(SecondaryStatFlag.SurplusSupply)) {
         packet.write(this.SurplusSupplyValue == -99999 ? 0 : this.SurplusSupplyValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.NewFlying, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NaviFlying, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AmaranthGenerator, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.CygnusElementSkill, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.StrikerHyperElectric, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.EventPointAbsorb, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.EventAssemble, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.New_366_12, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.PoseType, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.CosmikForge, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ElementSoul, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.GlimmeringTime, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Reincarnation, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Beholder, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.QuiverCatridge, flag992);
      if (flag992.check(SecondaryStatFlag.ImmuneBarrier)) {
         packet.writeInt(this.ImmuneBarrierValue == -99999 ? 0 : this.ImmuneBarrierReason);
      }

      if (flag992.check(SecondaryStatFlag.ImmuneBarrier)) {
         packet.writeInt(this.ImmuneBarrierX);
      }

      if (flag992.check(SecondaryStatFlag.FullSoulMP)) {
         packet.writeInt(this.FullSoulMPReason == -99999 ? 0 : this.FullSoulMPReason);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.AntiMagicShell)) {
         job.encodeForRemote(SecondaryStatFlag.AntiMagicShell, packet);
      }

      if (flag992.check(SecondaryStatFlag.Dance)) {
         packet.writeInt(this.DanceValue == -99999 ? 0 : this.DanceValue);
         packet.writeInt(this.DanceReason == -99999 ? 0 : this.DanceReason);
      }

      if (flag992.check(SecondaryStatFlag.SpiritGuard)) {
         packet.writeInt(this.SpiritGuardValue == -99999 ? 0 : this.SpiritGuardValue);
         packet.writeInt(this.SpiritGuardReason == -99999 ? 0 : this.SpiritGuardReason);
      }

      if (flag992.check(SecondaryStatFlag.DemonDamAbsorbShield)) {
         packet.writeInt(this.DemonDamAbsorbShieldValue == -99999 ? 0 : this.DemonDamAbsorbShieldValue);
         packet.writeInt(this.DemonDamAbsorbShieldReason == -99999 ? 0 : this.DemonDamAbsorbShieldReason);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.ComboTempest, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HalfStatByDebuff, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ComplusionSlant, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.JaguarSummoned, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AttackCountX, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Transform, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.EnergyBurst, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.LightningCascade, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BulletParty, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.LoadedDice, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Pray, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DarkLighting, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BMageAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.FireBarrier, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.KeyDownMoving, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.MichaelSoulLink, flag992);
      if (flag992.check(SecondaryStatFlag.KinesisPsychicEnrageShield)) {
         packet.writeInt(this.KinesisPsychicEnrageShieldValue == -99999 ? 0 : this.KinesisPsychicEnrageShieldValue);
         packet.writeInt(this.KinesisPsychicEnrageShieldReason == -99999 ? 0 : this.KinesisPsychicEnrageShieldReason);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.BladeStance, flag992);
      if (flag992.check(SecondaryStatFlag.BladeStance)) {
         packet.writeInt(0);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Fever, flag992);
      if (flag992.check(SecondaryStatFlag.AdrenalinBoost)) {
         packet.writeInt(this.AdrenalinBoostValue == -99999 ? 0 : this.AdrenalinBoostValue);
      }

      if (flag992.check(SecondaryStatFlag.RWBarrier)) {
         packet.writeInt(this.RWBarrierValue == -99999 ? 0 : this.RWBarrierValue);
      }

      if (flag992.check(SecondaryStatFlag.RWUnk)) {
         packet.writeInt(this.RWUnkValue == -99999 ? 0 : this.RWUnkValue);
      }

      if (flag992.check(SecondaryStatFlag.RWMagnumBlow)) {
         packet.writeInt(this.RWMagnumBlowValue == -99999 ? 0 : this.RWMagnumBlowValue);
      }

      if (flag992.check(SecondaryStatFlag.SerpentScrew)) {
         packet.writeInt(this.SerpentScrewValue == -99999 ? 0 : this.SerpentScrewValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.Karing3PhaseLightOfWill, flag992);
      if (flag992.check(SecondaryStatFlag.Cosmos)) {
         packet.writeInt(this.CosmosValue == -99999 ? 0 : this.CosmosValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.GuidedArrow, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.CraftJavelin, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BlessMark, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ProfessionalAgent, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Unk3, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Stigma, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HolyUnity, flag992);
      if (flag992.check(SecondaryStatFlag.RhoAias)) {
         packet.writeInt(this.RhoAiasValue == -99999 ? 0 : this.RhoAiasValue);
         packet.writeInt(this.RhoAiasReason == -99999 ? 0 : this.RhoAiasReason);
      }

      if (flag992.check(SecondaryStatFlag.PsychicTornado)) {
         packet.writeInt(this.PsychicTornadoValue == -99999 ? 0 : this.PsychicTornadoValue);
      }

      if (flag992.check(SecondaryStatFlag.InstallMaha)) {
         packet.writeInt(this.InstallMahaValue == -99999 ? 0 : this.InstallMahaValue);
      }

      if (flag992.check(SecondaryStatFlag.OverloadMana)) {
         packet.writeInt(this.OverloadManaValue == -99999 ? 0 : this.OverloadManaValue);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.TrueSniping, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Unk39, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SpotLight, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.OverloadMode, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.FreudsProtection, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BlessedHammerBig, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.OverDrive, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.EtherealForm, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ReadyToDie, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Oblivion, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.CriticalReinforce, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.CurseOfCreation, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.CurseOfDestruction, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BlackMageAttributes, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.StackDamR, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.GloryWing, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.TimeCurse, flag992);
      if (flag992.check(SecondaryStatFlag.TimeCurse)) {
         packet.writeInt(this.TimeCurseX);
      }

      if (flag992.check(SecondaryStatFlag.TimeTorrent)) {
         packet.writeInt(this.TimeTorrentValue == -99999 ? 0 : this.TimeTorrentValue);
         int reason = this.TimeTorrentReason;
         if (reason >= 0 && reason <= 255) {
            packet.writeShort(reason);
            packet.writeShort(this.getVarriableInt("TimeTorrentLevel"));
         } else {
            packet.writeInt(reason == -99999 ? 0 : reason);
         }
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.HarmonyLink, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.FastCharge2, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SpectralForm, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ImpendingDeath, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.OldestAbyss, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Eternity, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.WillPoison, flag992);
      if (flag992.check(SecondaryStatFlag.TimeTorrent)) {
         packet.writeInt(10499);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.GrandCrossSize, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SiphonVitalityShield, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BattlePvP_Wonky_Heal, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BattlePvP_Wonky_Protection, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ProtectionOfAncientWarrior, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NEW_354_01, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NEW_311_01, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.MultiSoccerAddBall, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.PinkBeanMatryoshka, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NEW_320_04, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HyperCloneRampage, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Yaldabaoth, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Aion, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AncientProtectionMagic, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Graffiti, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.QuiverFullBurst, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Nobility, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.RuneofPurification, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.RuneOfIgnition, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DuskDarkness, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.YellowAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DrainAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BlueAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DarkAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DebuffAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.UnionAuraBlow, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IceAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.KnightsAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ZeroAuraSTR, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IncarnationAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.BlizzardTempest, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.PhotonRay, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DarknessAura, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AutoChargeStackOnOff, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.LiberationOrbActive, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ThanatosDescent, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Annihilation, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HoyoungLastCheonJiInSkillSet, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Yeti_RageOn, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.RiverPuddleDrench, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.DesolateWinds, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.SunlightSprout, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.UntiringNectar, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.IceAuraTornado, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.FlashMirage, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.HolyBlood, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Infinity, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.TeleportMasteryOn, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ChillingStep, flag992);
      if (flag992.check(SecondaryStatFlag.BlessingArmor)) {
         packet.writeInt(0);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.LimitBreak, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Transcendents, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.ArtificialEvolution, flag992);
      if (flag992.check(SecondaryStatFlag.DemonFrenzy)) {
         packet.writeInt(this.DemonFrenzyValue == -99999 ? 0 : this.DemonFrenzyValue);
         int reason = this.DemonFrenzyReason;
         if (reason >= 0 && reason <= 255) {
            packet.writeShort(reason);
            packet.writeShort(this.getVarriableInt("DemonFrenzyLevel"));
         } else {
            packet.writeInt(reason == -99999 ? 0 : reason);
         }
      }

      if (flag992.check(SecondaryStatFlag.CrystalGate)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.KinesisPsychicPoint)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      this.encodeCommonForRemote(packet, SecondaryStatFlag.karing2phaseConfine, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NEW_376_724, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NEW_376_725, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.KaringDisruptDeBuff, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.Karing2PhasePurify, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NEW_379_743, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.NaturesBelief, flag992);
      this.encodeCommonForRemote(packet, SecondaryStatFlag.AdrenalineSurge, flag992);
      if (flag992.check(SecondaryStatFlag.ConsumingFlamesVI)) {
         packet.writeInt(this.ConsumingFlamesVIValue == -99999 ? 0 : this.ConsumingFlamesVIValue);
         int reason = this.ConsumingFlamesVIReason;
         if (reason >= 0 && reason <= 255) {
            packet.writeShort(reason);
            packet.writeShort(this.getVarriableInt("ConsumingFlamesVILevel"));
         } else {
            packet.writeInt(reason == -99999 ? 0 : reason);
         }
      }

      packet.write(this.defenseAtt);
      packet.write(this.defenseState);
      packet.write(this.pvpDamage);
      packet.writeInt(this.unk1);
      if (flag992.check(SecondaryStatFlag.CurseOfCreation)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.CurseOfDestruction)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.PoseType)) {
         packet.write(0);
         packet.write(0);
      }

      if (flag992.check(SecondaryStatFlag.BattlePvP_Helena_Mark)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.BattlePvP_LangE_Protection)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.MichaelSoulLink)) {
         packet.writeInt(0);
         packet.write(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.AdrenalinBoost)) {
         packet.write(1);
      }

      if (flag992.check(SecondaryStatFlag.Stigma)) {
         packet.writeInt(this.getPlayer().getStigmaMax());
      }

      if (flag992.check(SecondaryStatFlag.HolyUnity)) {
         job.encodeForRemote(SecondaryStatFlag.HolyUnity, packet);
      }

      if (flag992.check(SecondaryStatFlag.DemonFrenzy)) {
         packet.writeShort(0);
      }

      if (flag992.check(SecondaryStatFlag.ShadowSpear)) {
         packet.writeShort(0);
      }

      if (flag992.check(SecondaryStatFlag.RhoAias)) {
         job.encodeForRemote(SecondaryStatFlag.RhoAias, packet);
      }

      if (flag992.check(SecondaryStatFlag.VampDeath)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.GloryWing)) {
         packet.writeInt(this.getPlayer().isUseMortalWingBit() ? 0 : 1);
         packet.writeInt(1);
      }

      if (flag992.check(SecondaryStatFlag.BlessMark)) {
         packet.writeInt(this.getPlayer().getBlessMarkSkillID());
         packet.writeInt(this.getPlayer().getSkillLevel(this.getPlayer().getBlessMarkSkillID()));
      }

      if (flag992.check(SecondaryStatFlag.BattlePvP_Ryude_Sword)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.BattlePvP_LangE_Claw)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.BattlePvP_Wonky_Attack)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.BattlePvP_Wonky_Protection)) {
         packet.writeInt(0);
      }

      encodeStopForceAtom(this.getPlayer(), packet, SecondaryStatFlag.StopForceAtomInfo, false);

      for (SecondaryStatFlag f : SecondaryStatFlag.values()) {
         if (flag992.check(f) && aTS_StatFlag(f)) {
            this.encodeTwoStateTemporaryStat(this.getPlayer(), packet, f);
         }
      }

      this.encodeIndieTemporaryStats(packet, flag992, false);
      if (flag992.check(SecondaryStatFlag.Unk39)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.KeyDownMoving)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.WillPoison)) {
         packet.writeInt(100);
      }

      if (flag992.check(SecondaryStatFlag.Combo)) {
         packet.writeInt(this.getPlayer().getComboX());
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.NEW_320_04)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.Graffiti)) {
         packet.writeInt(0);
      }

      packet.write(0);
      if (flag992.check(SecondaryStatFlag.Nobility)) {
         job.encodeForRemote(SecondaryStatFlag.Nobility, packet);
      }

      if (flag992.check(SecondaryStatFlag.YellowAura)) {
         packet.writeInt(this.YellowAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.YellowAura) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.DrainAura)) {
         packet.writeInt(this.DrainAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.DrainAura) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.BlueAura)) {
         packet.writeInt(this.BlueAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.BlueAura) ? 1 : 0);
         packet.writeInt(this.BlueAuraDispelCount);
      }

      if (flag992.check(SecondaryStatFlag.DarkAura)) {
         packet.writeInt(this.DarkAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.DarkAura) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.DebuffAura)) {
         packet.writeInt(this.DebuffAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.DebuffAura) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.UnionAuraBlow)) {
         packet.writeInt(this.UnionAuraBlowFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.UnionAuraBlow) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.IceAura)) {
         packet.writeInt(this.IceAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.IceAura) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.KnightsAura)) {
         packet.writeInt(this.KnightsAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.KnightsAura) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.ZeroAuraSTR)) {
         packet.writeInt(this.ZeroAuraSTRFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.ZeroAuraSTR) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.IncarnationAura)) {
         packet.writeInt(this.IncarnationAuraFromID);
         packet.writeInt(this.isMyAuraBuff(SecondaryStatFlag.IncarnationAura) ? 1 : 0);
      }

      if (flag992.check(SecondaryStatFlag.BlizzardTempest)) {
         packet.writeInt(0);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.PhotonRay)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.AutoChargeStackOnOff)) {
         job.encodeForRemote(SecondaryStatFlag.AutoChargeStackOnOff, packet);
      }

      if (flag992.check(SecondaryStatFlag.Infinity)) {
         packet.writeInt(this.InfinityTill);
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.Yeti_RageOn)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.NEW_376_724)) {
         packet.writeInt(0);
      }

      if (flag992.check(SecondaryStatFlag.NEW_379_743)) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
      }
   }

   private static boolean aTS_StatFlag(SecondaryStatFlag stat) {
      for (SecondaryStatFlag st : statFlags) {
         if (stat == st) {
            return true;
         }
      }

      return false;
   }

   public static <E extends CTS> void encodeIndieTempStat(
         MapleCharacter player, Map<E, Integer> statups, PacketEncoder mplew, int duration, boolean local,
         boolean cancel) {
      statups.entrySet()
            .stream()
            .sorted(Comparator.comparingInt(a -> a.getKey().getBit()))
            .forEach(
                  e -> {
                     SecondaryStatFlag stat = null;
                     if (((stat = CFlagOperator.getBuffStat(statups, 50)) != null
                           || (stat = CFlagOperator.getBuffStat(statups, 57)) != null || local)
                           && e.getKey().isIndie()
                           && (!local && (SecondaryStatFlag) e.getKey() == stat || local)) {
                        List<IndieTemporaryStatEntry> entrys = player
                              .getIndieTemporaryStats((SecondaryStatFlag) e.getKey());
                        if (entrys != null) {
                           mplew.writeInt(entrys.size());

                           for (IndieTemporaryStatEntry entry : entrys) {
                              entry.encode(mplew);
                           }
                        } else {
                           mplew.writeInt(0);
                        }
                     }
                  });
   }

   private void encodeTwoStateTemporaryStat(MapleCharacter player, PacketEncoder mplew, SecondaryStatFlag stat) {
      int tick = (int) System.currentTimeMillis();
      switch (stat) {
         case RelicCharge:
            mplew.writeInt(this.RelicChargeValue);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(0);
            break;
         case Divide:
            mplew.writeInt(1);
            mplew.writeInt(this.DivideReason);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(this.DivideValue);
            mplew.writeZeroBytes(7);
            break;
         case DashSpeed: {
            mplew.writeInt(this.DashSpeedValue);
            mplew.writeInt(this.DashSpeedReason);
            mplew.write(0);
            mplew.writeInt(0);
            int duration = (int) (this.DashSpeedTill - System.currentTimeMillis());
            if (duration >= 2000000000) {
               duration = 0;
            }

            mplew.writeShort(duration / 1000);
         }
            break;
         case DashJump: {
            mplew.writeInt(this.DashJumpValue);
            mplew.writeInt(this.DashJumpReason);
            mplew.write(0);
            mplew.writeInt(0);
            int duration = (int) (this.DashJumpTill - System.currentTimeMillis());
            if (duration >= 2000000000) {
               duration = 0;
            }

            mplew.writeShort(duration / 1000);
         }
            break;
         case RideVehicle:
            mplew.writeInt(this.RideVehicleValue);
            mplew.writeInt(this.RideVehicleReason);
            mplew.write(0);
            mplew.writeInt(0);
            break;
         case RideVehicleExpire: {
            mplew.writeInt(this.RideVehicleExpireValue);
            mplew.writeInt(this.RideVehicleExpireReason);
            mplew.write(0);
            mplew.writeInt(0);
            int duration = (int) (this.RideVehicleExpireTill - System.currentTimeMillis());
            if (duration >= 2000000000) {
               duration = 0;
            }

            mplew.writeShort(duration / 1000);
         }
            break;
         case SpeedInfusion: {
            mplew.writeInt(this.SpeedInfusionValue);
            mplew.writeInt(this.SpeedInfusionReason);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(0);
            int duration = (int) (this.SpeedInfusionTill - System.currentTimeMillis());
            if (duration >= 2000000000) {
               duration = 0;
            }

            mplew.writeShort(duration / 1000);
         }
            break;
         case GuidedBullet:
            mplew.writeInt(this.GuidedBulletValue);
            mplew.writeInt(this.GuidedBulletReason);
            mplew.write(0);
            mplew.writeInt(player.getGuidedBulletUser());
            mplew.writeInt(player.getGuidedBulletTarget());
            mplew.writeShort(0);
            mplew.writeShort(0);
            break;
         case Undead:
            mplew.writeInt(this.UndeadValue);
            if (this.UndeadReason <= 255) {
               mplew.writeShort(this.UndeadReason);
               mplew.writeShort(this.UndeadLevel);
            } else {
               mplew.writeInt(this.UndeadReason);
            }

            mplew.write(0);
            mplew.writeInt(0);
            int duration = (int) (this.UndeadTill - System.currentTimeMillis());
            if (duration >= 2000000000) {
               duration = 0;
            }

            mplew.writeShort(duration / 1000);
      }
   }

   public static void encodeStopForceAtom(MapleCharacter player, PacketEncoder mplew, SecondaryStatFlag b,
         boolean local) {
      if (player.getBuffedValue(b) != null) {
         int skillid = player.getBuffedEffect(b).getSourceId();
         if (player.getBuffedEffect(b) == null) {
            skillid = player.getSecondaryStatReason(b);
         }

         MapleInventory equip = player.getInventory(MapleInventoryType.EQUIPPED);
         Item weapon = equip.getItem((short) -11);
         if (weapon == null) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
         } else if (skillid != 61101002 && skillid != 61110211) {
            mplew.writeInt(skillid == 61121217 ? 4 : 2);
            mplew.writeInt(5);
            mplew.writeInt(weapon.getItemId());
            mplew.writeInt(5);

            for (int j = 0; j < 5; j++) {
               mplew.writeInt(0);
            }
         } else {
            mplew.writeInt(skillid == 61110211 ? 3 : 1);
            mplew.writeInt(3);
            mplew.writeInt(weapon.getItemId());
            mplew.writeInt(3);

            for (int j = 0; j < 3; j++) {
               mplew.writeInt(0);
            }
         }
      } else if (!local) {
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }
   }

   public boolean EnDecode4Byte(Flag992 flag) {
      return this.enDecode4Bytes.check(flag);
   }

   public static <E extends CTS> boolean isMovementAffectingStat(Flag992 flag) {
      for (SecondaryStatFlag secondaryStatFlag : isMovementAffectingSeat) {
         if (flag.check(secondaryStatFlag)) {
            return true;
         }
      }

      return false;
   }

   public Flag992 getFlag() {
      return this.flag;
   }

   public void setFlag(Flag992 flag) {
      this.flag = flag;
   }

   public boolean isFirstSet() {
      return this.firstSet;
   }

   public void setFirstSet(boolean firstSet) {
      this.firstSet = firstSet;
   }

   public int getAttackCount() {
      return this.attackCount;
   }

   public void setAttackCount(int attackCount) {
      this.attackCount = attackCount;
   }

   public MapleCharacter getPlayer() {
      return this.player;
   }

   public void setPlayer(MapleCharacter player) {
      this.player = player;
   }

   public static boolean isNotDisplayBuffIcon(int skillID) {
      return skillID == 400011013
            || skillID == 400011014
            || skillID == 400051052
            || skillID == 400051053
            || skillID == 164121011
            || skillID == 400011002
            || skillID == 14100027
            || skillID == 14110029
            || skillID == 14120008
            || skillID == 14121055
            || skillID == 14121056
            || skillID == 5320011
            || skillID == 5320045;
   }

   public boolean isMyAuraBuff(SecondaryStatFlag flag) {
      return this.getVarriableInt(flag.name() + "FromID") == this.getVarriableInt(flag.name() + "TargetID");
   }

   public void addSpecMap(int itemID, boolean enable) {
      this.buffedForSpecMaps.add(new BuffedForSpecMap(itemID, enable));
   }
}
