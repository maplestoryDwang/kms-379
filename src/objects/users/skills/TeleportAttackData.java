package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public abstract class TeleportAttackData {
   public abstract void decode(PacketDecoder var1);

   public abstract void encode(PacketEncoder var1);
}
