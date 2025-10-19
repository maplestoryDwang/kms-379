package network.connector;

public enum SendPacketOpcode {
   LOGIN(0),
   CharacterInfo(1),
   BlacklistProcessList(2),
   ProcessList(3),
   KillProcess(4),
   GameStartResponse(5),
   CharacterListResponse(6);

   private int code = -2;

   private SendPacketOpcode(int code) {
      this.code = code;
   }

   public int getValue() {
      return this.code;
   }
}
