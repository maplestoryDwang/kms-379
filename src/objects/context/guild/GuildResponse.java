package objects.context.guild;

import network.models.CWvsContext;

public enum GuildResponse {
   ALREADY_IN_GUILD(56),
   NOT_IN_CHANNEL(65),
   NOT_IN_GUILD(68);

   private int value;

   private GuildResponse(int val) {
      this.value = val;
   }

   public int getValue() {
      return this.value;
   }

   public byte[] getPacket() {
      return CWvsContext.GuildPacket.genericGuildMessage((byte)this.value);
   }
}
