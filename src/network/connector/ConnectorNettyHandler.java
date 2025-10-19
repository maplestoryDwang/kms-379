package network.connector;

import constants.ServerConstants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import javax.swing.table.DefaultTableModel;
import network.connector.panel.ConnectorPanel;
import network.decode.PacketDecoder;
import objects.utils.Randomizer;

public class ConnectorNettyHandler extends SimpleChannelInboundHandler<PacketDecoder> {
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      String IP = ctx.channel().remoteAddress().toString().split(":")[0];
      long time = System.currentTimeMillis();
      byte[] serverRecv = new byte[]{
         (byte)Randomizer.nextInt(255), (byte)Randomizer.nextInt(255), (byte)Randomizer.nextInt(255), (byte)Randomizer.nextInt(255)
      };
      byte[] serverSend = new byte[]{
         (byte)Randomizer.nextInt(255), (byte)Randomizer.nextInt(255), (byte)Randomizer.nextInt(255), (byte)Randomizer.nextInt(255)
      };
      byte[] realIvRecv = new byte[4];
      byte[] realIvSend = new byte[4];
      System.arraycopy(serverRecv, 0, realIvRecv, 0, 4);
      System.arraycopy(serverSend, 0, realIvSend, 0, 4);
      TundraAes send = new TundraAes(realIvSend);
      TundraAes recv = new TundraAes(realIvRecv);
      ConnectorClient cli = new ConnectorClient(ctx.channel(), send, recv);
      if (ConnectorServer.getInstance().getClientStorage().getMainClient(cli.getAddressIP()) != null) {
         ConnectorServer.getInstance().getClientStorage().removeMainClient(cli.getAddressIP());
      }

      ConnectorServer.getInstance().getClientStorage().registerMainClient(cli, cli.getAddressIP());
      ctx.write(PacketCreator.sendHandShake(serverSend, serverRecv));
      ctx.flush();
      ctx.channel().attr(ConnectorClient.CLIENTKEY).set(cli);
      cli.setPingTime(System.currentTimeMillis());
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      ConnectorClient client = (ConnectorClient)ctx.channel().attr(ConnectorClient.CLIENTKEY).get();
      if (client != null) {
         if (client.getId() != null && ServerConstants.authlist2.get(client.getId()) != null) {
            ServerConstants.authlist2.remove(client.getId());
         }

         if (client.getAuth() != null && ServerConstants.authlist.get(client.getAuth()) != null) {
            ServerConstants.authlist.remove(client.getAuth());
         }

         ConnectorServer.getInstance().getClientStorage().deregisterClient(client);
         client.DisableAccount(client.getId());
         DefaultTableModel model = (DefaultTableModel)ConnectorPanel.jTable1.getModel();

         try {
            if (model.getRowCount() >= 1) {
               for (int i = model.getRowCount() - 1; i >= 0; i--) {
                  if (model.getValueAt(i, 0).toString().equals(client.getId())) {
                     model.removeRow(i);
                     break;
                  }
               }
            }
         } catch (Exception var5) {
            System.out.println("jTable1에서 클라삭제하다 오류 발생\r\n");
            var5.printStackTrace();
         }
      }

      ctx.channel().attr(ConnectorClient.CLIENTKEY).set(null);
      ctx.channel().close();
      System.out.println("팅겼다고인마");
      ctx.close();
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable ex) {
   }

   public byte[] intToByteArray(int value) {
      return new byte[]{(byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value};
   }

   public void userEventTriggered(ChannelHandlerContext ctx, Object event) {
      if (event instanceof IdleStateEvent) {
         IdleStateEvent e = (IdleStateEvent)event;
         ConnectorClient client = (ConnectorClient)ctx.channel().attr(ConnectorClient.CLIENTKEY).get();
         if (e.state() == IdleState.READER_IDLE) {
            ctx.close();
            if (client != null) {
               ConnectorServerHandler.ConnectorLog("핑타임 초과 ID : " + client.getId() + " IP : " + client.getAddressIP() + " ", client);
            }
         } else if (e.state() == IdleState.WRITER_IDLE && client != null) {
            client.setSendSkill((int)(client.getSendSkill() + 1L));
            if (client.getSendSkill() >= 6L) {
               client.send(PacketCreator.sendProcessKillList(client));
               client.setSendSkill(0);
            }
         }
      }
   }

   protected void channelRead0(ChannelHandlerContext ctx, PacketDecoder slea) throws Exception {
      ConnectorClient client = (ConnectorClient)ctx.channel().attr(ConnectorClient.CLIENTKEY).get();
      ConnectorServerHandler.ConnectorLog(client.getSession().toString() + " \r\n" + slea.toString(), client);
      byte header = slea.readByte();

      for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
         if (recv.getValue() == header) {
            ConnectorServerHandler.HandlePacket(recv, slea, client);
         }
      }
   }
}
