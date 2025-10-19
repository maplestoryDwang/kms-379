package objects.fields.child.minigame.yutgame;

import network.encode.PacketEncoder;

public abstract class YutGameResultEntry {
   public abstract YutGameResultType getType();

   public abstract void encode(PacketEncoder var1);
}
