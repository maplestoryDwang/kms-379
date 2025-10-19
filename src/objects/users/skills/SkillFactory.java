package objects.users.skills;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.fieldskill.FieldSkill;
import objects.fields.fieldskill.FieldSkillEntry;
import objects.fields.fieldskill.PresetInfo;
import objects.summoned.SummonedSkillEntry;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.StringUtil;
import objects.utils.Triple;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataDirectoryEntry;
import objects.wz.provider.MapleDataFileEntry;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class SkillFactory {
   private static final Map<Integer, Skill> skills = new HashMap<>();
   private static final Map<String, Integer> delays = new HashMap<>();
   private static final Map<Integer, SkillFactory.CraftingEntry> crafts = new HashMap<>();
   private static final Map<Integer, List<Integer>> skillsByJob = new HashMap<>();
   private static final Map<Integer, SummonedSkillEntry> SummonSkillInformation = new HashMap<>();
   private static final Map<Integer, FieldSkill> fieldSkills = new HashMap<>();
   private static final Map<Integer, Integer> ridingSkillInfo = new HashMap<>();
   private static final MapleData delayData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz"))
      .getData("00002000.img");
   private static final MapleData stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"))
      .getData("Skill.img");
   private static final MapleDataProvider datasource = MapleDataProviderFactory.getDataProvider(
      new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz")
   );

   public static void load() {
      int del = 0;

      for (MapleData delay : delayData) {
         if (!delay.getName().equals("info")) {
            delays.put(delay.getName(), del);
            del++;
         }
      }

      for (MapleDataDirectoryEntry root : datasource.getRoot()) {
         if (!root.getName().startsWith("MobSkill")) {
            for (MapleDataFileEntry topDir : root.getFiles()) {
               cachingSkills(topDir, root, datasource);
            }
         }
      }
   }

   public static void cachingSkills(MapleDataFileEntry topDir, MapleDataDirectoryEntry root, MapleDataProvider source) {
      int skillid = 0;
      if (topDir.getName().length() <= 10) {
         for (MapleData data : source.getData(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz/" + root.getName(), topDir.getName())) {
            if (data.getName().equals("skill")) {
               for (MapleData data2 : data) {
                  try {
                     if (data2 != null) {
                        skillid = Integer.parseInt(data2.getName());
                        Skill skil = Skill.loadFromData(skillid, data2, delayData);
                        List<Integer> job = skillsByJob.computeIfAbsent(skillid / 10000, k -> new ArrayList<>());
                        job.add(skillid);
                        skil.setName(getName(skillid, stringData));
                        skills.put(skillid, skil);
                        MapleData summon_data = data2.getChildByPath("summon/attack1/info");
                        if (summon_data != null) {
                           SummonedSkillEntry sse = new SummonedSkillEntry();
                           sse.type = (byte)MapleDataTool.getInt("type", summon_data, 0);
                           sse.mobCount = (byte)(skillid == 33101008 ? 3 : MapleDataTool.getInt("mobCount", summon_data, 1));
                           sse.attackCount = (byte)MapleDataTool.getInt("attackCount", summon_data, 1);
                           if (summon_data.getChildByPath("range/lt") != null) {
                              MapleData ltd = summon_data.getChildByPath("range/lt");
                              sse.lt = (Point)ltd.getData();
                              sse.rb = (Point)summon_data.getChildByPath("range/rb").getData();
                           } else {
                              sse.lt = new Point(-100, -100);
                              sse.rb = new Point(100, 100);
                           }

                           sse.delay = MapleDataTool.getInt("effectAfter", summon_data, 0) + MapleDataTool.getInt("attackAfter", summon_data, 0);

                           for (MapleData effect : summon_data) {
                              if (effect.getChildren().size() > 0) {
                                 for (MapleData effectEntry : effect) {
                                    sse.delay = sse.delay + MapleDataTool.getIntConvert("delay", effectEntry, 0);
                                 }
                              }
                           }

                           for (MapleData effectx : data2.getChildByPath("summon/attack1")) {
                              sse.delay = sse.delay + MapleDataTool.getIntConvert("delay", effectx, 0);
                           }

                           SummonSkillInformation.put(skillid, sse);
                        }
                     }
                  } catch (Exception var16) {
                     System.out.println("SkillFactory Err");
                     var16.printStackTrace();
                  }
               }
            }
         }
      } else if (topDir.getName().startsWith("Recipe")) {
         for (MapleData datax : source.getData(topDir.getName())) {
            skillid = Integer.parseInt(datax.getName());
            SkillFactory.CraftingEntry skil = new SkillFactory.CraftingEntry(
               skillid,
               (byte)MapleDataTool.getInt("incFatigability", datax, 0),
               (byte)MapleDataTool.getInt("reqSkillLevel", datax, 0),
               (byte)MapleDataTool.getInt("incSkillProficiency", datax, 0),
               MapleDataTool.getInt("needOpenItem", datax, 0) > 0,
               MapleDataTool.getInt("period", datax, 0)
            );

            for (MapleData d : datax.getChildByPath("target")) {
               skil.targetItems
                  .add(new Triple<>(MapleDataTool.getInt("item", d, 0), MapleDataTool.getInt("count", d, 0), MapleDataTool.getInt("probWeight", d, 0)));
            }

            for (MapleData d : datax.getChildByPath("recipe")) {
               skil.reqItems.put(MapleDataTool.getInt("item", d, 0), MapleDataTool.getInt("count", d, 0));
            }

            crafts.put(skillid, skil);
         }
      } else if (topDir.getName().startsWith("RidingSkillInfo")) {
         for (MapleData datax : source.getData(topDir.getName())) {
            skillid = Integer.parseInt(datax.getName());
            ridingSkillInfo.put(skillid, MapleDataTool.getInt("vehicleID", datax, 0));
         }
      } else if (topDir.getName().startsWith("FieldSkill")) {
         for (MapleData datax : source.getData(topDir.getName())) {
            int skillID = Integer.parseInt(datax.getName());
            FieldSkill fieldSkill = new FieldSkill(datax.getChildByPath("level"));
            fieldSkills.put(skillID, fieldSkill);
         }
      }

      for (Skill skill : skills.values()) {
         skill.afterLoad();
      }
   }

   public static FieldSkill getFieldSkill(int skillID) {
      return fieldSkills.get(skillID);
   }

   public static List<Integer> getSkillsByJob(int jobId) {
      return skillsByJob.get(jobId);
   }

   public static String getSkillName(int id) {
      Skill skil = getSkill(id);
      return skil != null ? skil.getName() : null;
   }

   public static Integer getDelay(String id) {
      return SkillFactory.Delay.fromString(id) != null ? SkillFactory.Delay.fromString(id).i : delays.get(id);
   }

   private static String getName(int id, MapleData stringData) {
      String strId = Integer.toString(id);
      strId = StringUtil.getLeftPaddedStr(strId, '0', 7);
      MapleData skillroot = stringData.getChildByPath(strId);
      return skillroot != null ? MapleDataTool.getString(skillroot.getChildByPath("name"), "") : "";
   }

   public static SummonedSkillEntry getSummonData(int skillid) {
      return SummonSkillInformation.get(skillid);
   }

   public static Collection<Skill> getAllSkills() {
      return skills.values();
   }

   public static Skill getSkill(int id) {
      if (!skills.isEmpty()) {
         return id >= 91000000 && id < 100000000 && crafts.containsKey(id) ? crafts.get(id) : skills.get(id);
      } else {
         return null;
      }
   }

   public static int getVehicleID(Integer skillID) {
      if (!ridingSkillInfo.isEmpty()) {
         return ridingSkillInfo.get(skillID) != null ? ridingSkillInfo.get(skillID) : 0;
      } else {
         return 0;
      }
   }

   public static long getDefaultSExpiry(Skill skill) {
      if (skill == null) {
         return -1L;
      } else {
         return skill.isTimeLimited() ? System.currentTimeMillis() + 2592000000L : -1L;
      }
   }

   public static SkillFactory.CraftingEntry getCraft(int id) {
      return !crafts.isEmpty() ? crafts.get(id) : null;
   }

   public static void printSkillInfoDetail(int skillid) {
      Skill skill = getSkill(skillid);
      if (skill != null) {
         int maxLevel = skill.getMaxLevel();
         int masterLevel = skill.getMasterLevel();

         for (int i = 0; i < maxLevel; i++) {
            int level = i + 1;
            SecondaryStatEffect effect = skill.getEffect(level);
            if (effect != null) {
               String content = "Skill ID: "
                  + skill.getId()
                  + "\nSkill Name: "
                  + skill.getName()
                  + "\nSkill MaxLevel: "
                  + maxLevel
                  + "\nSkill MasterLevel: "
                  + masterLevel
                  + "\nSkill Effect Level: "
                  + level
                  + "\nSkill ScondaryEffect: "
                  + effect.toString()
                  + "\n----------------------------------------------------------------------------------------------------------\n";
               File file = new File(String.format("D:\\MapleStory\\%d_%s_스킬정보.txt", skillid, skill.getName()));
               FileOutputStream writer = null;

               try {
                  writer = new FileOutputStream(file, true);
                  writer.write(content.getBytes(StandardCharsets.UTF_8));
                  writer.flush();
                  writer.close();
               } catch (Exception var19) {
                  System.out.println("SkillInfoDetail Err");
                  var19.printStackTrace();
               } finally {
                  try {
                     if (writer != null) {
                        writer.close();
                     }
                  } catch (IOException var18) {
                     var18.printStackTrace();
                  }
               }
            }
         }
      }
   }

   public static void printAllSkillInfos() {
      int count = 0;
      System.out.println("entrySet size : " + skills.entrySet().size());

      for (Entry<Integer, Skill> skillSet : skills.entrySet()) {
         count++;

         int masterLevel;
         try {
            masterLevel = skillSet.getValue().getMasterLevel();
            if (masterLevel == 0) {
               masterLevel = 1;
            }
         } catch (NullPointerException var19) {
            var19.printStackTrace();
            masterLevel = 1;
         }

         String content = "Skill ID["
            + count
            + "]: "
            + skillSet.getKey()
            + "\nSkill Name: "
            + skillSet.getValue().getName()
            + "\nSkill SkillType: "
            + skillSet.getValue().getSkillType()
            + "\nSkill Type: "
            + skillSet.getValue().getType()
            + "\nSkill isHyper: "
            + skillSet.getValue().isHyper()
            + "\nSkill isMagic: "
            + skillSet.getValue().isMagic()
            + "\nSkill isSpecialSkill: "
            + skillSet.getValue().isSpecialSkill()
            + "\nSkill isSummon: "
            + skillSet.getValue().isSummon()
            + "\nSkill isChargeSkill: "
            + skillSet.getValue().isChargeSkill()
            + "\nSkill isFourthJob: "
            + skillSet.getValue().isFourthJob()
            + "\nSkill isMovement: "
            + skillSet.getValue().isMovement()
            + "\nSkill Effect Level(Masterlevel): "
            + masterLevel
            + "\nSkill ScondaryEffect: "
            + skillSet.getValue().getEffect(masterLevel).toString()
            + "\n----------------------------------------------------------------------------------------------------------\n";
         File file = new File("D:\\MapleStory\\Royal_372_스킬정보.txt");
         FileOutputStream writer = null;

         try {
            writer = new FileOutputStream(file, true);
            writer.write(content.getBytes(StandardCharsets.UTF_8));
            writer.flush();
            writer.close();
         } catch (Exception var17) {
            System.out.println("AllSkillPrint Err");
            var17.printStackTrace();
         } finally {
            try {
               if (writer != null) {
                  writer.close();
               }
            } catch (IOException var16) {
               var16.printStackTrace();
            }
         }
      }
   }

   public static byte[] EncodeFieldSkill(int skillID, int skillLevel, int refMobID, int mobSkill, int minBulletCount, int maxBulletCount) {
      if (getFieldSkill(skillID) != null && getFieldSkill(skillID).getFieldSkillEntry(skillLevel) != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
         packet.writeInt(skillID);
         packet.writeInt(skillLevel);
         FieldSkillEntry entry = getFieldSkill(skillID).getFieldSkillEntry(skillLevel);
         packet.writeInt(entry.getLt().x);
         packet.writeInt(entry.getLt().y);
         packet.writeInt(entry.getRb().x);
         packet.writeInt(entry.getRb().y);
         packet.writeInt(entry.getDelay() == null ? 0 : entry.getDelay());
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(1);
         packet.writeInt(refMobID);
         packet.writeInt(mobSkill);
         packet.write(1);
         packet.write(0);
         int bullet = ThreadLocalRandom.current().nextInt(minBulletCount, maxBulletCount + 1);
         ArrayList<PresetInfo> randList = new ArrayList<>(entry.getPresetInfo(entry.getRandPreset()));
         Collections.shuffle(randList);
         packet.writeInt(bullet);

         for (int i = 0; i < bullet; i++) {
            Point pos = randList.get(i).getPos();
            packet.writeShort(pos.x);
            packet.writeShort(pos.y);
         }

         return packet.getPacket();
      } else {
         return null;
      }
   }

   public static class CraftingEntry extends Skill {
      public boolean needOpenItem;
      public int period;
      public byte incFatigability;
      public byte reqSkillLevel;
      public byte incSkillProficiency;
      public List<Triple<Integer, Integer, Integer>> targetItems = new ArrayList<>();
      public Map<Integer, Integer> reqItems = new HashMap<>();

      public CraftingEntry(int id, byte incFatigability, byte reqSkillLevel, byte incSkillProficiency, boolean needOpenItem, int period) {
         super(id);
         this.incFatigability = incFatigability;
         this.reqSkillLevel = reqSkillLevel;
         this.incSkillProficiency = incSkillProficiency;
         this.needOpenItem = needOpenItem;
         this.period = period;
      }
   }

   public static enum Delay {
      walk1(0),
      walk2(1),
      stand1(2),
      stand2(3),
      alert(4),
      swingO1(5),
      swingO2(6),
      swingO3(7),
      swingOF(8),
      swingT1(9),
      swingT2(10),
      swingT3(11),
      swingTF(12),
      swingP1(13),
      swingP2(14),
      swingPF(15),
      stabO1(16),
      stabO2(17),
      stabOF(18),
      stabT1(19),
      stabT2(20),
      stabTF(21),
      swingD1(22),
      swingD2(23),
      stabD1(24),
      swingDb1(25),
      swingDb2(26),
      swingC1(27),
      swingC2(28),
      rushBoom(28),
      tripleBlow(25),
      quadBlow(26),
      deathBlow(27),
      finishBlow(28),
      finishAttack(29),
      finishAttack_link(30),
      finishAttack_link2(30),
      shoot1(31),
      shoot2(32),
      shootF(33),
      shootDb2(40),
      shotC1(41),
      dash(37),
      dash2(38),
      proneStab(41),
      prone(42),
      heal(43),
      fly(44),
      jump(45),
      sit(46),
      rope(47),
      dead(48),
      ladder(49),
      rain(50),
      alert2(52),
      alert3(53),
      alert4(54),
      alert5(55),
      alert6(56),
      alert7(57),
      ladder2(58),
      rope2(59),
      shoot6(60),
      magic1(61),
      magic2(62),
      magic3(63),
      magic5(64),
      magic6(65),
      explosion(65),
      burster1(66),
      burster2(67),
      savage(68),
      avenger(69),
      assaulter(70),
      prone2(71),
      assassination(72),
      assassinationS(73),
      tornadoDash(76),
      tornadoDashStop(76),
      tornadoRush(76),
      rush(77),
      rush2(78),
      brandish1(79),
      brandish2(80),
      braveSlash(81),
      braveslash1(81),
      braveslash2(81),
      braveslash3(81),
      braveslash4(81),
      darkImpale(97),
      sanctuary(82),
      meteor(83),
      paralyze(84),
      blizzard(85),
      genesis(86),
      blast(88),
      smokeshell(89),
      showdown(90),
      ninjastorm(91),
      chainlightning(92),
      holyshield(93),
      resurrection(94),
      somersault(95),
      straight(96),
      eburster(97),
      backspin(98),
      eorb(99),
      screw(100),
      doubleupper(101),
      dragonstrike(102),
      doublefire(103),
      triplefire(104),
      fake(105),
      airstrike(106),
      edrain(107),
      octopus(108),
      backstep(109),
      shot(110),
      rapidfire(110),
      fireburner(112),
      coolingeffect(113),
      fist(114),
      timeleap(115),
      homing(117),
      ghostwalk(118),
      ghoststand(119),
      ghostjump(120),
      ghostproneStab(121),
      ghostladder(122),
      ghostrope(123),
      ghostfly(124),
      ghostsit(125),
      cannon(126),
      torpedo(127),
      darksight(128),
      bamboo(129),
      pyramid(130),
      wave(131),
      blade(132),
      souldriver(133),
      firestrike(134),
      flamegear(135),
      stormbreak(136),
      vampire(137),
      swingT2PoleArm(139),
      swingP1PoleArm(140),
      swingP2PoleArm(141),
      doubleSwing(142),
      tripleSwing(143),
      fullSwingDouble(144),
      fullSwingTriple(145),
      overSwingDouble(146),
      overSwingTriple(147),
      rollingSpin(148),
      comboSmash(149),
      comboFenrir(150),
      comboTempest(151),
      finalCharge(152),
      finalBlow(154),
      finalToss(155),
      magicmissile(156),
      lightningBolt(157),
      dragonBreathe(158),
      breathe_prepare(159),
      dragonIceBreathe(160),
      icebreathe_prepare(161),
      blaze(162),
      fireCircle(163),
      illusion(164),
      magicFlare(165),
      elementalReset(166),
      magicRegistance(167),
      magicBooster(168),
      magicShield(169),
      recoveryAura(170),
      flameWheel(171),
      killingWing(172),
      OnixBlessing(173),
      Earthquake(174),
      soulStone(175),
      dragonThrust(176),
      ghostLettering(177),
      darkFog(178),
      slow(179),
      mapleHero(180),
      Awakening(181),
      flyingAssaulter(182),
      tripleStab(183),
      fatalBlow(184),
      slashStorm1(185),
      slashStorm2(186),
      bloodyStorm(187),
      flashBang(188),
      upperStab(189),
      bladeFury(190),
      chainPull(192),
      chainAttack(192),
      owlDead(193),
      monsterBombPrepare(195),
      monsterBombThrow(195),
      finalCut(196),
      finalCutPrepare(196),
      suddenRaid(198),
      fly2(199),
      fly2Move(200),
      fly2Skill(201),
      knockback(202),
      rbooster_pre(206),
      rbooster(206),
      rbooster_after(206),
      crossRoad(209),
      nemesis(210),
      tank(217),
      tank_laser(221),
      siege_pre(223),
      tank_siegepre(223),
      sonicBoom(226),
      darkLightning(228),
      darkChain(229),
      cyclone_pre(0),
      cyclone(0),
      glacialchain(247),
      flamethrower(233),
      flamethrower_pre(233),
      flamethrower2(234),
      flamethrower_pre2(234),
      gatlingshot(239),
      gatlingshot2(240),
      drillrush(241),
      earthslug(242),
      rpunch(243),
      clawCut(244),
      swallow(247),
      swallow_attack(247),
      swallow_loop(247),
      flashRain(249),
      OnixProtection(264),
      OnixWill(265),
      phantomBlow(266),
      comboJudgement(267),
      arrowRain(268),
      arrowEruption(269),
      iceStrike(270),
      swingT2Giant(273),
      cannonJump(295),
      swiftShot(296),
      giganticBackstep(298),
      mistEruption(299),
      cannonSmash(300),
      cannonSlam(301),
      flamesplash(302),
      noiseWave(306),
      superCannon(310),
      jShot(312),
      demonSlasher(313),
      bombExplosion(314),
      cannonSpike(315),
      speedDualShot(316),
      strikeDual(317),
      bluntSmash(319),
      crossPiercing(320),
      piercing(321),
      elfTornado(323),
      immolation(324),
      multiSniping(327),
      windEffect(328),
      elfrush(329),
      elfrush2(329),
      dealingRush(334),
      maxForce0(336),
      maxForce1(337),
      maxForce2(338),
      maxForce3(339),
      iceAttack1(274),
      iceAttack2(275),
      iceSmash(276),
      iceTempest(277),
      iceChop(278),
      icePanic(279),
      iceDoubleJump(280),
      shockwave(292),
      demolition(293),
      snatch(294),
      windspear(295),
      windshot(296);

      public int i;

      private Delay(int i) {
         this.i = i;
      }

      public static SkillFactory.Delay fromString(String s) {
         for (SkillFactory.Delay b : values()) {
            if (b.name().equalsIgnoreCase(s)) {
               return b;
            }
         }

         return null;
      }
   }
}
