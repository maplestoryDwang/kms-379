package objects.fields.child.minigame.space;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class Field_Mission2Space extends Field {
   private long startGameTime = 0L;
   private boolean startGame = false;
   private long endGameTime = 0L;
   private boolean endGame = false;
   private MapleCharacter player = null;
   private byte[] blocks = new byte[1000];
   private int currentBlockIndex = 0;
   private boolean fever = false;
   private long endFeverTime = 0L;
   private int combo = 0;
   private int success = 0;
   private int score = 0;
   private int maxCombo = 0;
   private int failCount = 0;
   private int greenSuccess = 0;
   private int redSuccess = 0;
   private int blueSuccess = 0;
   private static Mission2SpaceUnk[] unks = new Mission2SpaceUnk[]{
      new Mission2SpaceUnk(0, 35),
      new Mission2SpaceUnk(150, 100),
      new Mission2SpaceUnk(230, 110),
      new Mission2SpaceUnk(310, 120),
      new Mission2SpaceUnk(390, 130),
      new Mission2SpaceUnk(470, 140),
      new Mission2SpaceUnk(550, 150),
      new Mission2SpaceUnk(630, 160)
   };
   private static Mission2SpaceUnk2[] unks2 = new Mission2SpaceUnk2[]{
      new Mission2SpaceUnk2(0, 0, false),
      new Mission2SpaceUnk2(1, 1, false),
      new Mission2SpaceUnk2(2, 2, false),
      new Mission2SpaceUnk2(3, 0, true),
      new Mission2SpaceUnk2(4, 1, true),
      new Mission2SpaceUnk2(5, 2, true)
   };

   public Field_Mission2Space(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (!this.endGame) {
         if (!this.endGame && this.endGameTime != 0L && this.endGameTime <= System.currentTimeMillis()) {
            this.endGame();
         }

         if (this.fever && this.endFeverTime != 0L && this.endFeverTime <= System.currentTimeMillis()) {
            this.fever = false;
            this.endFeverTime = 0L;
            this.sendMission2SpaceState(3);
         }

         if (!this.startGame) {
            if (this.startGameTime == 0L) {
               this.sendMission2SpaceState(1);
               this.sendMission2SpaceUnk3();
               this.sendMission2SpaceUnk();
               this.sendMission2SpaceUnk2();
               this.generateAndSendBlock();
               this.sendMission2SpaceState(2);
               this.startGameTime = System.currentTimeMillis() + 3000L;
               return;
            }

            if (this.startGameTime != 0L && this.startGameTime <= System.currentTimeMillis()) {
               this.sendMission2SpaceState(3);
               this.startGame = true;
               this.endGameTime = System.currentTimeMillis() + 90000L;
               this.player.send(CField.getClock(90));
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.player = null;
      this.blocks = new byte[1000];
      this.currentBlockIndex = 0;
      this.startGameTime = 0L;
      this.startGame = false;
      this.endGameTime = 0L;
      this.endGame = false;
      this.combo = 0;
      this.success = 0;
      this.score = 0;
      this.fever = false;
      this.endFeverTime = 0L;
      this.maxCombo = 0;
      this.failCount = 0;
      this.greenSuccess = 0;
      this.redSuccess = 0;
      this.blueSuccess = 0;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.resetFully(false);
      this.player = player;
      player.send(CField.UIPacket.setIngameDirectionMode(false, false, false));
      player.send(CField.UIPacket.hideChar(true));
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.send(CField.UIPacket.endInGameDirectionMode(0));
   }

   public void sendMission2SpaceState(int state) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_STATE.getValue());
      packet.writeInt(state);
      this.player.send(packet.getPacket());
   }

   public void endGame() {
      int coin = 0;
      coin = this.score / 10000;
      if (this.score % 10000 >= 5000) {
         coin++;
      }

      this.player.updateOneInfo(1234569, "miniGame3_coin", String.valueOf(coin));
      this.player.updateOneInfo(1234569, "miniGame3_success", String.valueOf(this.currentBlockIndex));
      this.sendMission2SpaceState(5);
      this.sendMission2SpaceGameResult();
      this.player.setRegisterTransferField(993191100);
      this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
      this.endGame = true;
   }

   public void sendMission2SpaceUnk() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_UNK.getValue());
      packet.writeInt(unks.length);

      for (Mission2SpaceUnk unk : unks) {
         unk.encode(packet);
      }

      this.player.send(packet.getPacket());
   }

   public void sendMission2SpaceUnk2() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_UNK_2.getValue());
      packet.writeInt(unks2.length);

      for (Mission2SpaceUnk2 unk : unks2) {
         unk.encode(packet);
      }

      this.player.send(packet.getPacket());
   }

   public void sendMission2SpaceUnk3() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_UNK_3.getValue());
      packet.writeInt(800);
      packet.writeInt(50);
      this.player.send(packet.getPacket());
   }

   public void sendMission2SpaceSuccess(boolean bonus) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_SUCCESS.getValue());
      packet.write(bonus);
      this.player.send(packet.getPacket());
   }

   public void sendMission2SpaceScore() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_SCORE.getValue());
      packet.writeInt(this.combo);
      packet.writeInt(this.success);
      packet.writeInt(this.score);
      this.player.send(packet.getPacket());
   }

   public void sendMission2SpaceGameResult() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_GAME_RESULT.getValue());
      packet.writeInt(this.score);
      packet.writeInt(this.maxCombo);
      packet.writeInt(this.failCount);
      packet.writeInt(this.currentBlockIndex);
      packet.writeInt(this.greenSuccess);
      packet.writeInt(this.redSuccess);
      packet.writeInt(this.blueSuccess);
      this.player.send(packet.getPacket());
   }

   public void generateAndSendBlock() {
      this.blocks = new byte[1000];

      for (int i = 0; i < 1000; i++) {
         if (i != 0 && i % 20 == 0) {
            this.blocks[i] = (byte)Randomizer.rand(3, 5);
         } else {
            this.blocks[i] = (byte)Randomizer.rand(0, 2);
         }
      }

      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MISSION_2_SPACE_SET_BLOCK.getValue());
      packet.writeInt(this.blocks.length);

      for (int ix = 0; ix < this.blocks.length; ix++) {
         packet.write(this.blocks[ix]);
      }

      this.player.send(packet.getPacket());
   }

   public void checkInput(int type) {
      int currentBlock = this.blocks[this.currentBlockIndex];
      if (currentBlock > 2) {
         currentBlock -= 3;
      }

      boolean bonus = false;
      if (currentBlock == type || this.fever && type == 3) {
         this.combo++;
         if (this.maxCombo < this.combo) {
            this.maxCombo = this.combo;
         }

         if (!this.fever) {
            this.success++;
         }

         int block = this.blocks[this.currentBlockIndex];
         if (block == 0) {
            this.greenSuccess++;
         } else if (block == 1) {
            this.redSuccess++;
         } else if (block == 2) {
            this.blueSuccess++;
         }

         this.currentBlockIndex++;
         this.score = this.score + Randomizer.rand(95, 105);
         if (Randomizer.isSuccess(5)) {
            this.score = this.score + Randomizer.rand(1500, 2000);
            bonus = true;
         }

         if (this.success >= 51) {
            this.success = 0;
            this.activeFever();
         }
      } else {
         this.failCount++;
         this.combo = 0;
      }

      this.sendMission2SpaceSuccess(bonus);
      this.sendMission2SpaceScore();
   }

   public void activeFever() {
      this.fever = true;
      this.endFeverTime = System.currentTimeMillis() + 5000L;
      this.sendMission2SpaceState(4);
   }
}
