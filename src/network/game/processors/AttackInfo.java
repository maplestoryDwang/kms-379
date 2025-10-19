package network.game.processors;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import objects.users.MapleCharacter;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackAction;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.AttackPair;

public class AttackInfo {
   public int skillID;
   public int skillLevel;
   public int keydown;
   public int lastAttackTickCount;
   public int finalAttackActiveSkillID;
   public List<AttackPair> allDamage;
   public Point forcedPos;
   public int display;
   public int unk;
   public int consumeItemID;
   public int tbyte;
   public boolean bAddAttackProc;
   public byte hits;
   public byte targets;
   public byte speed;
   public byte bulletCashItemPos;
   public byte AOE;
   public byte slot;
   public byte finalAttackActiveSkillLevel;
   public boolean isUnstableMemorize = false;
   public boolean real = true;
   public boolean tag = false;
   public boolean isJumpAttack = false;
   public boolean isAttackWithDarkSight = false;
   public byte unk2;
   public Point attackPosition;
   public Point attackPosition2;
   public Point attackPosition3;
   public boolean dragonShowSkillEffect;
   public int dragonAttackAction;
   public boolean dragonShowAttackAction;
   public boolean dragonAttack;
   public Point dragonPos = new Point(0, 0);
   public Point shadowSpearPos;
   public Point position2;
   public Point keydownSkillRectMoveXY;
   public List<Point> affectedSpawnPos;
   public int bySummonedID;
   public byte flag;
   public int targetID;
   public Point targetPosition;
   public byte hitAction;
   public byte lifting;
   public byte alone;
   public short delay;
   public int UNK_Additional_1;
   public int UNK_Additional_2;
   public int bombID;
   public Point bombHitPos;
   public byte bombD;
   public int specialFlag;
   public int attacktype;
   public int isBuckShot;
   public int isShadowPartner;
   public int facingleft;
   public TeleportAttackAction teleportAttackAction;
   public byte canRWCharge = 1;
   public int dllAttackCount = 0;
   public List<Boolean> dllCritical = new ArrayList<>();

   public final SecondaryStatEffect getAttackEffect(MapleCharacter chr, int skillLevel, Skill skill_) {
      if (GameConstants.isLinkedAranSkill(this.skillID)) {
         Skill skillLink = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(this.skillID));
         return skillLink.getEffect(skillLevel);
      } else {
         return skill_.getEffect(skillLevel);
      }
   }
}
