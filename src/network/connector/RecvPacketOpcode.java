package network.connector;

public enum RecvPacketOpcode {
   LoginRequest(0),
   AccountInfoRequest(1),
   Heartbeat(2),
   ProcessDetected(3),
   ProcessListResponse(4),
   GameStartRequest(5),
   CharacterListRequest(6),
   SetMainCharacterRequest(7);

   private int code = -2;

   public final int getValue() {
      return this.code;
   }

   private RecvPacketOpcode(int code) {
      this.code = code;
   }
}
