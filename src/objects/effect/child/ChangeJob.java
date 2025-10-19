package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class ChangeJob implements Effect {
   public static final int header = EffectHeader.ChangeJob.getValue();
   private final int playerID;
   private final int oldJob;
   private final int newJob;

   public ChangeJob(int playerID, int oldJob, int newJob) {
      this.playerID = playerID;
      this.oldJob = oldJob;
      this.newJob = newJob;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeShort(this.oldJob);
      packet.writeShort(this.newJob);
   }

   @Override
   public byte[] encodeForLocal() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
      packet.write(header);
      this.encode(packet);
      return packet.getPacket();
   }

   @Override
   public byte[] encodeForRemote() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
      packet.writeInt(this.playerID);
      packet.write(header);
      this.encode(packet);
      return packet.getPacket();
   }
}
