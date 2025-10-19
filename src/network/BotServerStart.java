package network;

import network.discordbot.BotServer;

public class BotServerStart {
   public static void main(String[] args) {
      try {
         new BotServer().run_startup_configurations();
      } catch (Exception var2) {
         System.out.println("BotServer Err");
         var2.printStackTrace();
      }
   }
}
