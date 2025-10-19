package objects.utils;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;

public class ConnectorController extends JFrame {
   public static int connectUser = 0;
   public static int connectThread = 0;
   public static DefaultListModel nowConnectionList = new DefaultListModel();
   private JButton banBtn;
   private JButton banOffBtn;
   public static JList connectionList;
   private JScrollPane jScrollPane4;
   private JLabel online;
   private JLabel online1;
   public static JLabel onlineCheck;
   public static JLabel threadCheck;

   public ConnectorController() {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation((screenSize.width - 1505) / 2, (screenSize.height - 576) / 2);
      this.initComponents();
      this.setTitle("[EXTREME] ConnectorController :: Ver 1.2.379");
   }

   public static void addController(String connection) {
      nowConnectionList.addElement(connection);
      connectionList.setModel(nowConnectionList);
      onlineCheck.setText(nowConnectionList.size() + "명");
   }

   public static void removeController(String connection) {
      nowConnectionList.removeElement(connection);
      connectionList.setModel(nowConnectionList);
      onlineCheck.setText(nowConnectionList.size() + "명");
   }

   public static void addThread() {
      connectThread++;
      threadCheck.setText(connectThread + "개");
   }

   public static void removeThread() {
      connectThread--;
      threadCheck.setText(connectThread + "개");
   }

   private void initComponents() {
      this.banBtn = new JButton();
      this.banOffBtn = new JButton();
      this.online = new JLabel();
      onlineCheck = new JLabel();
      this.jScrollPane4 = new JScrollPane();
      connectionList = new JList();
      this.online1 = new JLabel();
      threadCheck = new JLabel();
      this.setDefaultCloseOperation(3);
      this.banBtn.setText("시리얼 밴");
      this.banBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorController.this.banBtnActionPerformed(evt);
         }
      });
      this.banOffBtn.setText("시리얼 밴 해제");
      this.online.setFont(new Font("굴림", 1, 12));
      this.online.setText("연결자 수 :");
      onlineCheck.setFont(new Font("굴림", 1, 12));
      onlineCheck.setText("0명");
      connectionList.setSelectionMode(0);
      this.jScrollPane4.setViewportView(connectionList);
      this.online1.setFont(new Font("굴림", 1, 12));
      this.online1.setText("쓰레드 수 :");
      threadCheck.setFont(new Font("굴림", 1, 12));
      threadCheck.setText("0개");
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.jScrollPane4, -2, 0, 32767)
                        .addGroup(
                           Alignment.TRAILING,
                           layout.createSequentialGroup()
                              .addComponent(this.banBtn)
                              .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                              .addComponent(this.banOffBtn)
                        )
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.online)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(onlineCheck)
                                    )
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.online1)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(threadCheck)
                                    )
                              )
                              .addGap(0, 0, 32767)
                        )
                  )
                  .addContainerGap()
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.online).addComponent(onlineCheck))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.online1).addComponent(threadCheck))
                  .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                  .addComponent(this.jScrollPane4, -2, 194, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.banOffBtn).addComponent(this.banBtn))
                  .addContainerGap()
            )
      );
      this.pack();
   }

   private void banBtnActionPerformed(ActionEvent evt) {
   }

   public static void main(String[] args) {
      try {
         for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException var5) {
         Logger.getLogger(ConnectorController.class.getName()).log(Level.SEVERE, null, var5);
      } catch (InstantiationException var6) {
         Logger.getLogger(ConnectorController.class.getName()).log(Level.SEVERE, null, var6);
      } catch (IllegalAccessException var7) {
         Logger.getLogger(ConnectorController.class.getName()).log(Level.SEVERE, null, var7);
      } catch (UnsupportedLookAndFeelException var8) {
         Logger.getLogger(ConnectorController.class.getName()).log(Level.SEVERE, null, var8);
      }

      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            new ConnectorController().setVisible(true);
         }
      });
   }
}
