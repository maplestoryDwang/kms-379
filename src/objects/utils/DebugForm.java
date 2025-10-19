package objects.utils;

import constants.ServerConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import network.auction.AuctionServer;
import network.game.GameServer;
import network.models.CField;
import objects.users.MapleCharacter;

public class DebugForm extends JFrame {
   private JButton jButton1;
   private JButton jButton2;
   private JButton jButton3;
   private JButton jButton4;
   private JButton jButton5;
   private JScrollPane jScrollPane1;
   private JTextArea jTextArea1;

   public DebugForm() {
      this.initComponents();
   }

   private void initComponents() {
      this.jButton1 = new JButton();
      this.jButton2 = new JButton();
      this.jButton3 = new JButton();
      this.jButton4 = new JButton();
      this.jScrollPane1 = new JScrollPane();
      this.jTextArea1 = new JTextArea();
      this.jButton5 = new JButton();
      this.setDefaultCloseOperation(3);
      this.jButton1.setText("Broadcast EndDirectionMode");
      this.jButton1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            DebugForm.this.jButton1ActionPerformed(evt);
         }
      });
      this.jButton2.setText("Broadcast EndIngameDirectionMode");
      this.jButton2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            DebugForm.this.jButton2ActionPerformed(evt);
         }
      });
      this.jButton3.setText("Broadcast Packet");
      this.jButton3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            DebugForm.this.jButton3ActionPerformed(evt);
         }
      });
      this.jButton4.setText("DC GameServer");
      this.jButton4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            DebugForm.this.jButton4ActionPerformed(evt);
         }
      });
      this.jTextArea1.setColumns(20);
      this.jTextArea1.setRows(5);
      this.jScrollPane1.setViewportView(this.jTextArea1);
      this.jButton5.setText("Debug Packet");
      this.jButton5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            DebugForm.this.jButton5ActionPerformed(evt);
         }
      });
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.jButton2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(this.jButton4)
                                    )
                                    .addComponent(this.jButton3)
                              )
                              .addGap(0, 367, 32767)
                        )
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(this.jScrollPane1)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.jButton1)
                                          .addPreferredGap(ComponentPlacement.UNRELATED)
                                          .addComponent(this.jButton5, -2, 116, -2)
                                          .addGap(0, 0, 32767)
                                    )
                              )
                              .addContainerGap()
                        )
                  )
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton1).addComponent(this.jButton5))
                  .addPreferredGap(ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton2).addComponent(this.jButton4))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPane1, -1, 248, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jButton3)
                  .addContainerGap()
            )
      );
      this.pack();
   }

   private void jButton1ActionPerformed(ActionEvent evt) {
      for (GameServer cserv : GameServer.getAllInstances()) {
         cserv.broadcastPacket(CField.UIPacket.IntroLock(false));
      }
   }

   private void jButton2ActionPerformed(ActionEvent evt) {
      for (GameServer cserv : GameServer.getAllInstances()) {
         cserv.broadcastPacket(CField.DirectionPacket.IntroEnableUI(1));
      }
   }

   private void jButton3ActionPerformed(ActionEvent evt) {
      for (GameServer cserv : GameServer.getAllInstances()) {
         cserv.broadcastPacket(HexTool.getByteArrayFromHexString(this.jTextArea1.getText()));
      }

      AuctionServer.getPlayerStorage().broadcastPacket(HexTool.getByteArrayFromHexString(this.jTextArea1.getText()));
   }

   private void jButton4ActionPerformed(ActionEvent evt) {
      for (GameServer cserv : GameServer.getAllInstances()) {
         for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
            chr.getClient().getSession().close();
            System.out.println("팅겼다고인마");
         }
      }

      for (MapleCharacter chr : AuctionServer.getPlayerStorage().getAllCharacters()) {
         chr.getClient().getSession().close();
         System.out.println("팅겼다고인마");
      }
   }

   private void jButton5ActionPerformed(ActionEvent evt) {
      if (ServerConstants.DEBUG_SEND) {
         ServerConstants.DEBUG_SEND = false;
         ServerConstants.DEBUG_RECEIVE = false;
      } else {
         ServerConstants.DEBUG_SEND = true;
         ServerConstants.DEBUG_RECEIVE = true;
      }
   }
}
