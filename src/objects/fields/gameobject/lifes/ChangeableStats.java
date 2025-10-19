package objects.fields.gameobject.lifes;

public class ChangeableStats extends OverrideMonsterStats {
   public int watk;
   public int matk;
   public int acc;
   public int eva;
   public int PDRate;
   public int MDRate;
   public int pushed;
   public int level;
   public int speed;

   public ChangeableStats(MapleMonsterStats stats, OverrideMonsterStats ostats) {
      this.hp = ostats.getHp();
      this.exp = ostats.getExp();
      this.mp = ostats.getMp();
      this.watk = stats.getPhysicalAttack();
      this.matk = stats.getMagicAttack();
      this.acc = stats.getAcc();
      this.eva = stats.getEva();
      this.PDRate = stats.getPDRate();
      this.MDRate = stats.getMDRate();
      this.pushed = stats.getPushed();
      this.level = stats.getLevel();
      this.speed = stats.getSpeed();
   }

   public ChangeableStats(MapleMonsterStats stats, OverrideMonsterStats ostats, int newLevel) {
      this.hp = ostats.getHp();
      this.exp = ostats.getExp();
      this.mp = ostats.getMp();
      this.watk = stats.getPhysicalAttack();
      this.matk = stats.getMagicAttack();
      this.acc = stats.getAcc();
      this.eva = stats.getEva();
      this.PDRate = stats.getPDRate();
      this.MDRate = stats.getMDRate();
      this.pushed = stats.getPushed();
      this.level = newLevel;
      this.speed = stats.getSpeed();
   }

   public ChangeableStats(MapleMonsterStats stats) {
      this.hp = stats.getHp();
      this.exp = stats.getExp();
      this.mp = stats.getMp();
      this.watk = stats.getPhysicalAttack();
      this.matk = stats.getMagicAttack();
      this.acc = stats.getAcc();
      this.eva = stats.getEva();
      this.PDRate = stats.getPDRate();
      this.MDRate = stats.getMDRate();
      this.pushed = stats.getPushed();
      this.level = stats.getLevel();
   }

   public ChangeableStats(MapleMonsterStats stats, int newLevel, boolean pqMob) {
      double mod = (double)newLevel / stats.getLevel();
      double hpRatio = (double)stats.getHp() / stats.getExp();
      double pqMod = pqMob ? 2.5 : 1.0;
      this.hp = (long)(stats.getHp() * mod * pqMod) / 10L;
      this.exp = (long)(stats.getExp() * mod * mod * pqMod) * (newLevel * 30);
      this.mp = (int)Math.round(stats.getMp() * mod * pqMod);
      this.watk = (int)Math.round(stats.getPhysicalAttack() * mod) / 10;
      this.matk = (int)Math.round(stats.getMagicAttack() * mod) / 10;
      this.acc = Math.round((float)(stats.getAcc() + Math.max(0, newLevel - stats.getLevel()) * 2));
      this.eva = Math.round((float)(stats.getEva() + Math.max(0, newLevel - stats.getLevel())));
      this.PDRate = Math.min(stats.isBoss() ? 30 : 20, (int)Math.round(stats.getPDRate() * mod));
      this.MDRate = Math.min(stats.isBoss() ? 30 : 20, (int)Math.round(stats.getMDRate() * mod));
      this.pushed = (int)Math.round(stats.getPushed() * mod);
      this.level = newLevel;
   }
}
