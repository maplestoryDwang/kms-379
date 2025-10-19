package objects.context.expedition;

public enum ExpeditionType {
   Normal_Balrog(15, 2001, 50, 200),
   Horntail(30, 2003, 80, 200),
   Zakum(30, 2002, 50, 200),
   Chaos_Zakum(30, 2005, 100, 200),
   ChaosHT(30, 2006, 110, 200),
   Pink_Bean(30, 2004, 140, 200),
   CWKPQ(30, 2007, 90, 200),
   Von_Leon(30, 2008, 120, 200),
   Cygnus(18, 2009, 170, 200),
   AKAYRUM(18, 2009, 120, 200),
   VANVAN(18, 2011, 120, 200),
   Velum(6, 2012, 120, 200),
   Swoo(18, 2014, 120, 200),
   H_Swoo(18, 2015, 120, 200),
   Demian(18, 2016, 120, 200),
   H_Demian(18, 2017, 120, 200),
   Magnus(18, 2018, 120, 200),
   CahosPapul(18, 2019, 120, 200),
   Lucid(18, 2020, 120, 200),
   Will(18, 2021, 120, 200),
   Cross(18, 2022, 120, 200),
   JinHillah(18, 2023, 120, 200),
   Hillah(6, 2010, 120, 200);

   public int maxMembers;
   public int maxParty;
   public int exped;
   public int minLevel;
   public int maxLevel;

   private ExpeditionType(int maxMembers, int exped, int minLevel, int maxLevel) {
      this.maxMembers = maxMembers;
      this.exped = exped;
      this.maxParty = maxMembers / 2 + (maxMembers % 2 > 0 ? 1 : 0);
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
   }

   public static ExpeditionType getById(int id) {
      for (ExpeditionType pst : values()) {
         if (pst.exped == id) {
            return pst;
         }
      }

      return null;
   }
}
