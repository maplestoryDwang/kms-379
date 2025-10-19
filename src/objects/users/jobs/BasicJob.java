package objects.users.jobs;

import java.awt.Point;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.skills.Skill;
import objects.users.skills.TeleportAttackAction;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;

public interface BasicJob {
   void setPlayer(MapleCharacter var1);

   MapleCharacter getPlayer();

   void prepareAttack(AttackInfo var1, SecondaryStatEffect var2, RecvPacketOpcode var3);

   void onAttack(MapleMonster var1, boolean var2, AttackPair var3, Skill var4, long var5, AttackInfo var7, SecondaryStatEffect var8, RecvPacketOpcode var9);

   void afterAttack(boolean var1, AttackInfo var2, long var3, SecondaryStatEffect var5, Skill var6, int var7, long var8, RecvPacketOpcode var10);

   void activeSkillPrepare(PacketDecoder var1);

   void activeSkillCancel();

   void beforeActiveSkill(PacketDecoder var1);

   void onActiveSkill(Skill var1, SecondaryStatEffect var2, PacketDecoder var3);

   void afterActiveSkill();

   void setActiveSkillPrepareID(int var1);

   void setActiveSkillPrepareSLV(int var1);

   Point getKeyDownRectMoveXY();

   void setKeyDownRectMoveXY(Point var1);

   int getActiveSkillID();

   int getActiveSkillLevel();

   int getActiveSkillPrepareID();

   int getActiveSkillPrepareSLV();

   void setActiveSkill(int var1);

   void setActiveSkillLevel(int var1);

   void setActiveSkillFlag(int var1);

   void setExclusive(boolean var1);

   void setTeleportAttackAction(TeleportAttackAction var1);

   TeleportAttackAction getTeleportAttackAction();

   void updatePerSecond();

   short getMoveAction();

   void setMoveAction(short var1);

   byte getSpeed();

   void setSpeed(byte var1);

   void encodeForLocal(SecondaryStatFlag var1, PacketEncoder var2);

   void encodeForRemote(SecondaryStatFlag var1, PacketEncoder var2);
}
