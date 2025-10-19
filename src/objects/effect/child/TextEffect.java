package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class TextEffect implements Effect {
   public static final int header = EffectHeader.TextEffect.getValue();
   private final int playerID;
   private final int textSpeed;
   private final int showTime;
   private final int uiOrigin;
   private final int rx;
   private final int ry;
   private final int align;
   private final int lineHeight;
   private final int type;
   private final int unk1;
   private final int unk2;
   private final String text;
   private final String sub;

   public TextEffect(int playerID, String text) {
      this.playerID = playerID;
      this.text = text;
      this.textSpeed = 50;
      this.showTime = 1500;
      this.uiOrigin = 4;
      this.rx = 0;
      this.ry = -200;
      this.align = 1;
      this.lineHeight = 4;
      this.type = 2;
      this.unk1 = 0;
      this.unk2 = 0;
      this.sub = "";
   }

   public TextEffect(int playerID, String text, int textSpeed, int showTime, int uiOrigin, int type) {
      this.playerID = playerID;
      this.text = text;
      this.textSpeed = textSpeed;
      this.showTime = showTime;
      this.uiOrigin = uiOrigin;
      this.rx = 0;
      this.ry = -200;
      this.align = 4;
      this.lineHeight = 4;
      this.type = type;
      this.unk1 = 0;
      this.unk2 = 0;
      this.sub = "";
   }

   public TextEffect(int playerID, String text, int textSpeed, int showTime, int uiOrigin, int type, int rx, int ry) {
      this.playerID = playerID;
      this.text = text;
      this.textSpeed = textSpeed;
      this.showTime = showTime;
      this.uiOrigin = uiOrigin;
      this.rx = rx;
      this.ry = ry;
      this.align = 4;
      this.lineHeight = 4;
      this.type = type;
      this.unk1 = 0;
      this.unk2 = 0;
      this.sub = "";
   }

   public TextEffect(String text, int textSpeed, int showTime, int uiOrigin, int x, int y, int align, int lineHeight) {
      this.playerID = -1;
      this.text = text;
      this.textSpeed = textSpeed;
      this.showTime = showTime;
      this.uiOrigin = uiOrigin;
      this.rx = x;
      this.ry = y;
      this.align = align;
      this.lineHeight = lineHeight;
      this.type = 0;
      this.unk1 = 0;
      this.unk2 = 0;
      this.sub = "";
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeMapleAsciiString(this.text);
      packet.writeInt(this.textSpeed);
      packet.writeInt(this.showTime);
      packet.writeInt(this.uiOrigin);
      packet.writeInt(this.rx);
      packet.writeInt(this.ry);
      packet.writeInt(this.align);
      packet.writeInt(this.lineHeight);
      packet.writeInt(this.type);
      packet.writeInt(this.unk1);
      packet.writeInt(this.unk2);
      packet.writeMapleAsciiString(this.sub);
      packet.writeInt(0);
      packet.write(0);
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
