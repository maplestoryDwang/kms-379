package objects.users.skills;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import objects.utils.Randomizer;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;
import objects.wz.provider.MapleDataType;

public class VCoreData {
   private static Map<Integer, VCoreData.VCoreInfo> coreData = new HashMap<>();

   public static int getCoreIdByJob(int job) {
      List<Integer> coreList = new LinkedList<>();
      int randomValue = Randomizer.nextInt(100);
      int randomType = randomValue < 70 ? 2 : (randomValue < 90 ? 1 : 3);
      coreData.forEach((id, core) -> {
         if ((core.isEnabledJob(job) || core.getCoreID() / 10000000 == 3) && core.getCoreID() / 10000000 == randomType) {
            coreList.add(core.getCoreID());
         }
      });
      return coreList.get(Randomizer.nextInt(coreList.size()));
   }

   public static int getCoreIdBySkill(int skillID) {
      AtomicInteger coreID = new AtomicInteger();
      coreData.forEach((id, core) -> {
         if (core.getConnectSkill(0) == skillID || core.getConnectSkill(1) == skillID || core.getConnectSkill(2) == skillID) {
            coreID.set(id);
         }
      });
      return coreID.get();
   }

   public static VCoreData.VCoreInfo getCoreInfo(int coreID) {
      return coreData != null && !coreData.isEmpty() ? coreData.get(coreID) : null;
   }

   public static int[] getMainCoreSkill(int coreId) {
      int[] ret = new int[3];
      VCoreData.VCoreInfo core = getCoreInfo(coreId);
      if (core != null) {
         ret[0] = core.getConnectSkill(0);
         ret[1] = core.getConnectSkill(1);
         ret[2] = core.getConnectSkill(2);
      }

      return ret;
   }

   public static VSpecialCoreOption getSpecialCoreOption(int coreID) {
      VCoreData.VCoreInfo core = getCoreInfo(coreID);
      return core != null ? core.getSpecialCoreOption() : null;
   }

   public static int getCountDecomPieceOfCore(int type) {
      switch (type) {
         case 0:
            return 40;
         case 1:
            return 10;
         case 2:
            return 50;
         default:
            return 0;
      }
   }

   public static int getNeedMakePieceOfCore(int type) {
      switch (type) {
         case 0:
            return 140;
         case 1:
            return 70;
         case 2:
            return 250;
         default:
            return 0;
      }
   }

   public static List<Integer> generateConnectSkillByJob(int job) {
      List<Integer> connectSkills = new LinkedList<>();
      coreData.forEach((id, core) -> {
         if (core.isEnabledJob(job) && core.getCoreID() >= 20000000 && core.getCoreID() < 30000000) {
            connectSkills.add(core.getConnectSkill(0));
         }
      });
      return connectSkills;
   }

   public static void LoadVCore() {
      String WZpath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz"));
      int coreID = 0;
      int jobid = 0;
      MapleData nameData = prov.getData("VCore.img");

      try {
         for (MapleData dat : nameData) {
            if (dat.getName().equals("CoreData")) {
               for (MapleData d : dat) {
                  coreID = Integer.parseInt(d.getName());
                  List<Integer> connectSkills = new LinkedList<>();

                  for (int i = 0; i < 3; i++) {
                     connectSkills.add(MapleDataTool.getInt("connectSkill/" + i, d, 0));
                  }

                  int type = MapleDataTool.getInt("type", d, 0);
                  int maxLevel = type == 0 ? 25 : 50;
                  VCoreData.VCoreInfo info = new VCoreData.VCoreInfo(MapleDataTool.getInt("type", d, 0), maxLevel, coreID, connectSkills);
                  List<Integer> jobs = new LinkedList<>();

                  for (MapleData dd : d.getChildren()) {
                     if (dd.getName().equals("job")) {
                        for (MapleData ddd : dd.getChildren()) {
                           if (ddd.getType() == MapleDataType.STRING) {
                              String job = (String)ddd.getData();
                              if (job.equals("warrior")) {
                                 jobs.add(112);
                                 jobs.add(122);
                                 jobs.add(132);
                                 jobs.add(1112);
                                 jobs.add(2112);
                                 jobs.add(3112);
                                 jobs.add(3122);
                                 jobs.add(3712);
                                 jobs.add(5112);
                                 jobs.add(6112);
                                 jobs.add(15112);
                              } else if (job.equals("magician")) {
                                 jobs.add(212);
                                 jobs.add(222);
                                 jobs.add(232);
                                 jobs.add(1212);
                                 jobs.add(14212);
                                 jobs.add(2218);
                                 jobs.add(2712);
                                 jobs.add(3212);
                              } else if (job.equals("archer")) {
                                 jobs.add(312);
                                 jobs.add(322);
                                 jobs.add(332);
                                 jobs.add(1312);
                                 jobs.add(2312);
                                 jobs.add(3312);
                                 jobs.add(6312);
                              } else if (job.equals("rogue")) {
                                 jobs.add(412);
                                 jobs.add(422);
                                 jobs.add(434);
                                 jobs.add(1412);
                                 jobs.add(2412);
                                 jobs.add(3612);
                                 jobs.add(6412);
                                 jobs.add(16412);
                              } else if (job.equals("pirate")) {
                                 jobs.add(512);
                                 jobs.add(522);
                                 jobs.add(532);
                                 jobs.add(1512);
                                 jobs.add(2512);
                                 jobs.add(3512);
                                 jobs.add(3612);
                                 jobs.add(6512);
                                 jobs.add(15512);
                              } else if (!job.equals("all") && !job.equals("none")) {
                                 int jobId = Integer.parseInt(job);
                                 jobs.add(jobId);
                              }
                           } else {
                              jobs.add((Integer)ddd.getData());
                           }
                        }
                     }
                  }

                  VSpecialCoreOption spOption = null;
                  if (d.getChildByPath("spCoreOption") != null) {
                     spOption = new VSpecialCoreOption();
                     spOption.setCondType(MapleDataTool.getString("spCoreOption/cond/type", d, null));
                     spOption.setCoolTime(MapleDataTool.getInt("spCoreOption/cond/cooltime", d, 0));
                     spOption.setCount(MapleDataTool.getInt("spCoreOption/cond/count", d, 0));
                     spOption.setValidTime(MapleDataTool.getInt("spCoreOption/cond/validTime", d, 0));
                     spOption.setProb(MapleDataTool.getDouble("spCoreOption/cond/prob", d, 0.0));
                     spOption.setEffectType(MapleDataTool.getString("spCoreOption/effect/type", d, null));
                     spOption.setSkillId(MapleDataTool.getInt("spCoreOption/effect/skill_id", d, 0));
                     spOption.setSkillLevel(MapleDataTool.getInt("spCoreOption/effect/skill_level", d, 0));
                     spOption.setHealPercent(MapleDataTool.getInt("spCoreOption/effect/heal_percent", d, 0));
                     spOption.setReducePercent(MapleDataTool.getInt("spCoreOption/effect/reducePercent", d, 0));
                  }

                  info.setSpecialCoreOption(spOption);
                  info.setJobs(jobs);
                  coreData.put(coreID, info);
               }
            }
         }
      } catch (Exception var20) {
         System.out.println("VCoreData Err");
         var20.printStackTrace();
      }
   }

   public static class VCoreInfo {
      private int type;
      private int maxLevel;
      private int coreId;
      private List<Integer> connectSkills;
      private List<Integer> jobs;
      private VSpecialCoreOption specialCoreOption;

      public VCoreInfo(int type, int maxLevel, int coreId, List<Integer> connectSkills) {
         this.type = type;
         this.maxLevel = maxLevel;
         this.coreId = coreId;
         this.connectSkills = connectSkills;
      }

      public void setJobs(List<Integer> jobs) {
         this.jobs = jobs;
      }

      public boolean isEnabledJob(int jobID) {
         for (int job : this.jobs) {
            if (job == jobID || job == 0) {
               return true;
            }
         }

         return false;
      }

      public List<Integer> getJobs() {
         return this.jobs;
      }

      public int getType() {
         return this.type;
      }

      public int getMaxLevel() {
         return this.maxLevel;
      }

      public int getCoreID() {
         return this.coreId;
      }

      public int getConnectSkill(int index) {
         return this.connectSkills == null ? -1 : this.connectSkills.get(index);
      }

      public VSpecialCoreOption getSpecialCoreOption() {
         return this.specialCoreOption;
      }

      public void setSpecialCoreOption(VSpecialCoreOption specialCoreOption) {
         this.specialCoreOption = specialCoreOption;
      }
   }
}
