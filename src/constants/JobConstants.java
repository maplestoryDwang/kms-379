package constants;

public class JobConstants {
   public static final boolean enableJobs = true;
   public static final int jobOrder = 35;

   public static String getPlayerJobs(int i) {
      String job = "";
      switch (i) {
         case 100:
            job = "검사";
            break;
         case 110:
            job = "파이터";
            break;
         case 111:
            job = "크루세이더";
            break;
         case 112:
            job = "히어로";
            break;
         case 120:
            job = "페이지";
            break;
         case 121:
            job = "나이트";
            break;
         case 122:
            job = "팔라딘";
            break;
         case 130:
            job = "스피어맨";
            break;
         case 131:
            job = "버서커";
            break;
         case 132:
            job = "다크나이트";
            break;
         case 200:
            job = "마법사";
            break;
         case 210:
            job = "위자드(불, 독)";
            break;
         case 211:
            job = "메이지(불, 독)";
            break;
         case 212:
            job = "아크메이지(불,독)";
            break;
         case 220:
            job = "위자드(썬, 콜)";
            break;
         case 221:
            job = "메이지(썬, 콜)";
            break;
         case 222:
            job = "아크메이지(썬,콜)";
            break;
         case 230:
            job = "클레릭";
            break;
         case 231:
            job = "프리스트";
            break;
         case 232:
            job = "비숍";
            break;
         case 300:
            job = "궁수";
            break;
         case 301:
         case 330:
         case 331:
         case 332:
            job = "패스파인더";
            break;
         case 310:
            job = "헌터";
            break;
         case 311:
            job = "레인저";
            break;
         case 312:
            job = "보우마스터";
            break;
         case 320:
            job = "사수";
            break;
         case 321:
            job = "저격수";
            break;
         case 322:
            job = "신궁";
            break;
         case 400:
            job = "도적";
            break;
         case 410:
            job = "어쌔신";
            break;
         case 411:
            job = "허밋";
            break;
         case 412:
            job = "나이트로드";
            break;
         case 420:
            job = "시프";
            break;
         case 421:
            job = "시프마스터";
            break;
         case 422:
            job = "섀도어";
            break;
         case 430:
            job = "세미듀어러";
            break;
         case 431:
            job = "듀어러";
            break;
         case 432:
            job = "듀얼마스터";
            break;
         case 433:
            job = "슬레셔";
            break;
         case 434:
            job = "듀얼블레이더";
            break;
         case 500:
            job = "해적";
            break;
         case 510:
            job = "인파이터";
            break;
         case 511:
            job = "버커니어";
            break;
         case 512:
            job = "바이퍼";
            break;
         case 520:
            job = "건슬링거";
            break;
         case 521:
            job = "발키리";
            break;
         case 522:
            job = "캡틴";
            break;
         case 530:
            job = "캐논슈터";
            break;
         case 531:
            job = "캐논블래스터";
            break;
         case 532:
            job = "캐논마스터";
            break;
         case 1100:
         case 1110:
         case 1111:
         case 1112:
            job = "소울마스터";
            break;
         case 1200:
         case 1210:
         case 1211:
         case 1212:
            job = "플레임위자드";
            break;
         case 1300:
         case 1310:
         case 1311:
         case 1312:
            job = "윈드브레이커";
            break;
         case 1400:
         case 1410:
         case 1411:
         case 1412:
            job = "나이트워커";
            break;
         case 1500:
         case 1510:
         case 1511:
         case 1512:
            job = "스트라이커";
            break;
         case 2100:
         case 2110:
         case 2111:
         case 2112:
            job = "아란";
            break;
         case 2200:
         case 2210:
         case 2211:
         case 2212:
         case 2213:
         case 2214:
         case 2215:
         case 2216:
         case 2217:
         case 2218:
            job = "에반";
            break;
         case 2300:
         case 2310:
         case 2311:
         case 2312:
            job = "메르세데스";
            break;
         case 2400:
         case 2410:
         case 2411:
         case 2412:
            job = "팬텀";
            break;
         case 2500:
         case 2510:
         case 2511:
         case 2512:
            job = "은월";
            break;
         case 2700:
         case 2710:
         case 2711:
         case 2712:
            job = "루미너스";
            break;
         case 3100:
         case 3110:
         case 3111:
         case 3112:
            job = "데몬슬레이어";
            break;
         case 3101:
         case 3120:
         case 3121:
         case 3122:
            job = "데몬어벤져";
            break;
         case 3200:
         case 3210:
         case 3211:
         case 3212:
            job = "배틀메이지";
            break;
         case 3300:
         case 3310:
         case 3311:
         case 3312:
            job = "와일드헌터";
            break;
         case 3500:
         case 3510:
         case 3511:
         case 3512:
            job = "메카닉";
            break;
         case 3600:
         case 3610:
         case 3611:
         case 3612:
            job = "제논";
            break;
         case 3700:
         case 3710:
         case 3711:
         case 3712:
            job = "블래스터";
            break;
         case 5100:
         case 5110:
         case 5111:
         case 5112:
            job = "미하일";
            break;
         case 6100:
         case 6110:
         case 6111:
         case 6112:
            job = "카이저";
            break;
         case 6300:
         case 6310:
         case 6311:
         case 6312:
            job = "카인";
            break;
         case 6400:
         case 6410:
         case 6411:
         case 6412:
            job = "카데나";
            break;
         case 6500:
         case 6510:
         case 6511:
         case 6512:
            job = "엔젤릭버스터";
            break;
         case 10112:
            job = "제로";
            break;
         case 14200:
         case 14210:
         case 14211:
         case 14212:
            job = "키네시스";
            break;
         case 15000:
         case 15200:
         case 15210:
         case 15211:
         case 15212:
            job = "일리움";
            break;
         case 15100:
         case 15110:
         case 15111:
         case 15112:
            job = "아델";
            break;
         case 15400:
         case 15410:
         case 15411:
         case 15412:
            job = "칼리";
            break;
         case 15500:
         case 15510:
         case 15511:
         case 15512:
            job = "아크";
            break;
         case 16200:
         case 16210:
         case 16211:
         case 16212:
            job = "라라";
            break;
         case 16400:
         case 16410:
         case 16411:
         case 16412:
            job = "호영";
            break;
         default:
            job = "초보자";
      }

      return job;
   }

   public static enum LoginJob {
      Resistance(0, JobConstants.LoginJob.JobFlag.ENABLED),
      Adventurer(1, JobConstants.LoginJob.JobFlag.ENABLED),
      Cygnus(2, JobConstants.LoginJob.JobFlag.ENABLED),
      Aran(3, JobConstants.LoginJob.JobFlag.ENABLED),
      Evan(4, JobConstants.LoginJob.JobFlag.ENABLED),
      Mercedes(5, JobConstants.LoginJob.JobFlag.ENABLED),
      Demon(6, JobConstants.LoginJob.JobFlag.ENABLED),
      Phantom(7, JobConstants.LoginJob.JobFlag.ENABLED),
      DualBlade(8, JobConstants.LoginJob.JobFlag.ENABLED),
      Mihile(9, JobConstants.LoginJob.JobFlag.ENABLED),
      Luminous(10, JobConstants.LoginJob.JobFlag.ENABLED),
      Kaiser(11, JobConstants.LoginJob.JobFlag.ENABLED),
      AngelicBuster(12, JobConstants.LoginJob.JobFlag.ENABLED),
      Cannoneer(13, JobConstants.LoginJob.JobFlag.ENABLED),
      Xenon(14, JobConstants.LoginJob.JobFlag.ENABLED),
      Zero(15, JobConstants.LoginJob.JobFlag.ENABLED),
      EunWol(16, JobConstants.LoginJob.JobFlag.ENABLED),
      PinkBean(17, JobConstants.LoginJob.JobFlag.ENABLED),
      Kinesis(18, JobConstants.LoginJob.JobFlag.ENABLED),
      Kadena(19, JobConstants.LoginJob.JobFlag.ENABLED),
      Illium(20, JobConstants.LoginJob.JobFlag.ENABLED),
      Ark(21, JobConstants.LoginJob.JobFlag.ENABLED),
      PathFinder(22, JobConstants.LoginJob.JobFlag.ENABLED),
      Hoyoung(23, JobConstants.LoginJob.JobFlag.ENABLED),
      Adele(24, JobConstants.LoginJob.JobFlag.ENABLED),
      Kain(25, JobConstants.LoginJob.JobFlag.ENABLED),
      Yeti(26, JobConstants.LoginJob.JobFlag.ENABLED),
      Lara(27, JobConstants.LoginJob.JobFlag.ENABLED),
      Khali(28, JobConstants.LoginJob.JobFlag.ENABLED);

      private final int jobType;
      private final int flag;

      private LoginJob(int jobType, JobConstants.LoginJob.JobFlag flag) {
         this.jobType = jobType;
         this.flag = flag.getFlag();
      }

      public int getJobType() {
         return this.jobType;
      }

      public int getFlag() {
         return this.flag;
      }

      public static enum JobFlag {
         DISABLED(0),
         ENABLED(1),
         LEVEL_DISABLED(2);

         private final int flag;

         private JobFlag(int flag) {
            this.flag = flag;
         }

         public int getFlag() {
            return this.flag;
         }
      }
   }
}
