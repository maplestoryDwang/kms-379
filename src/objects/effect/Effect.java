package objects.effect;

import java.util.HashMap;
import java.util.Map;
import network.encode.PacketEncoder;

public interface Effect {
   int header = -1;
   int playerID = 0;
   Map<Integer, Integer> decodeList = new HashMap<>();

   void encode(PacketEncoder var1);

   byte[] encodeForLocal();

   byte[] encodeForRemote();
}
