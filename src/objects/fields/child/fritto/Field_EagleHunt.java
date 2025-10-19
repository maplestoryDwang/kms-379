package objects.fields.child.fritto;

import constants.QuestExConstants;
import java.awt.Point;
import java.awt.Rectangle;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_EagleHunt extends Field {
   private MapleCharacter player = null;
   private boolean endGame = false;

   public Field_EagleHunt(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player != null) {
         if (this.endGame) {
            this.player.send(CField.showEffect("killing/clear"));
            this.player.setRegisterTransferField(993000601);
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            this.endGame = false;
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.player = null;
      this.endGame = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      this.resetFully(false);
      super.onEnter(player);
      this.player = player;
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
      packet.writeInt(1);
      player.send(packet.getPacket());
      player.send(CField.UIPacket.hideChar(true));
      player.send(CField.environmentChange("PoloFritto/msg1", 20, 263));
      player.send(CField.getClock(30));
      this.sendCreateGun();
      this.sendSetGun();
      this.sendSetAmmo(20);
      player.send(CField.environmentChange("killing/first/start", 16));
      this.spawnEagle();
      player.updateOneInfo(26022, "gameType", "0");
      player.updateOneInfo(15141, "reward", "1");
      player.updateOneInfo(15141, "point", "0");
      player.updateOneInfo(15141, "ammo", "20");
   }

   public void spawnEagle() {
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833000), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833000), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833000), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833000), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833000), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833000), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833001), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833001), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833001), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833002), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833002), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833003), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833003), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833003), new Point(0, 0));
      this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9833003), new Point(0, 0));
   }

   public void addScore(int mobID, int objectID) {
      int score = this.player.getOneInfoQuestInteger(15141, "point");
      switch (mobID) {
         case 9833000:
            score += 50;
            break;
         case 9833001:
            score += 100;
            break;
         case 9833002:
            score += 200;
            break;
         case 9833003:
            score -= 50;
      }

      this.player.updateOneInfo(15141, "point", String.valueOf(score));
      this.sendDeadFPSMode(objectID, score);
      if (this.mobSize() <= 0 || score >= 1000) {
         this.endGame = true;
      }
   }

   public int mobSize() {
      int ret = 0;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (mob.getId() != 9833003) {
            ret++;
         }
      }

      return ret;
   }

   public void shootResult() {
      int bullet = this.player.getOneInfoQuestInteger(15141, "ammo");
      if (bullet > 0) {
         this.sendShootAttackInFPSMode();
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      player.send(CField.UIPacket.hideChar(false));
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
      packet.write(0);
      packet.write(1);
      player.send(packet.getPacket());
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   private void sendCreateGun() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_CREATE_GUN.getValue());
      this.player.send(packet.getPacket());
   }

   private void sendClearGun() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_CLEAR_GUN.getValue());
      this.player.send(packet.getPacket());
   }

   private void sendSetGun() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_SET_GUN.getValue());
      packet.writeMapleAsciiString("shotgun");
      packet.writeMapleAsciiString("shotgun");
      packet.writeInt(1);
      packet.writeInt(200);
      packet.encodeRect(new Rectangle(-8, -8, 16, 16));
      this.player.send(packet.getPacket());
   }

   private void sendSetAmmo(int bullet) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_SET_AMMO.getValue());
      packet.writeInt(bullet);
      this.player.send(packet.getPacket());
   }

   private void sendShootAttackInFPSMode() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_SHOOT_ATTACK_IN_FPS_MODE.getValue());
      packet.write(1);
      this.player.send(packet.getPacket());
   }

   private void sendDeadFPSMode(int objectID, int point) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_DEAD_FPS_MODE.getValue());
      packet.writeInt(objectID);
      packet.writeInt(point);
      this.player.send(packet.getPacket());
   }
}
