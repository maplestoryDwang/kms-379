package network.connector.panel;

import constants.ServerConstants;
import database.DBConnection;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import network.center.Center;
import network.connector.ConnectorClient;
import network.connector.PacketCreator;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CWvsContext;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import org.jdesktop.layout.GroupLayout;

public class ConnectorPanel extends JFrame implements ActionListener {
   static String txtMsg;
   static String txtOpt;
   static int intOpt;
   public static MapleClient cc = null;
   private int minutesLeft = 0;
   private JButton jButton1;
   private JButton jButton11;
   private JButton jButton12;
   private JButton jButton13;
   private JButton jButton14;
   private JButton jButton15;
   private JButton jButton16;
   private JButton jButton17;
   private JButton jButton18;
   private JButton jButton19;
   private JButton jButton2;
   private JButton jButton20;
   private JButton jButton21;
   private JButton jButton22;
   private JButton jButton23;
   private JButton jButton24;
   private JButton jButton25;
   private JButton jButton26;
   private JButton jButton27;
   private JButton jButton28;
   private JButton jButton29;
   private JButton jButton3;
   private JButton jButton30;
   private JButton jButton31;
   private JButton jButton32;
   private JButton jButton4;
   private JButton jButton5;
   private JButton jButton6;
   private JButton jButton7;
   private JButton jButton8;
   private JButton jButton9;
   private JCheckBox jCheckBox1;
   private JCheckBox jCheckBox2;
   private JCheckBox jCheckBox4;
   private JCheckBox jCheckBox5;
   private JComboBox<String> jComboBox1;
   private JComboBox<String> jComboBox2;
   public static JFrame jFrame2;
   private JLabel jLabel1;
   private JLabel jLabel10;
   private JLabel jLabel11;
   private JLabel jLabel12;
   private JLabel jLabel13;
   private JLabel jLabel14;
   private JLabel jLabel15;
   private JLabel jLabel16;
   private JLabel jLabel17;
   private JLabel jLabel18;
   private JLabel jLabel19;
   private JLabel jLabel2;
   private JLabel jLabel20;
   private JLabel jLabel21;
   private JLabel jLabel22;
   private JLabel jLabel23;
   private JLabel jLabel24;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JLabel jLabel9;
   private JList<String> jList1;
   private JList<String> jList2;
   private JList<String> jList3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JScrollPane jScrollPane3;
   private JScrollPane jScrollPane4;
   private JScrollPane jScrollPane6;
   private JScrollPane jScrollPane7;
   private JScrollPane jScrollPane8;
   private JTabbedPane jTabbedPane1;
   public static JTable jTable1;
   public static JTable jTable2;
   public static final JTable jTable3 = new JTable();
   public static JTextArea jTextArea2;
   private JTextField jTextField1;
   private JTextField jTextField10;
   private JTextField jTextField11;
   private JTextField jTextField12;
   private JTextField jTextField13;
   private JTextField jTextField14;
   private JTextField jTextField15;
   private JTextField jTextField16;
   private JTextField jTextField17;
   private JTextField jTextField18;
   private JTextField jTextField19;
   private JTextField jTextField2;
   private JTextField jTextField20;
   private JTextField jTextField21;
   private JTextField jTextField22;
   private JTextField jTextField23;
   private JTextField jTextField24;
   private JTextField jTextField25;
   private JTextField jTextField26;
   private JTextField jTextField27;
   private JTextField jTextField28;
   private JTextField jTextField29;
   private JTextField jTextField3;
   private JTextField jTextField30;
   private JTextField jTextField4;
   private JTextField jTextField5;
   private JTextField jTextField6;
   private JTextField jTextField7;
   private JTextField jTextField8;
   private JTextField jTextField9;

   public ConnectorPanel() {
      changeAllSwingComponentDefaultFont();
      this.initComponents();
      jTable2.getTableHeader().setReorderingAllowed(false);
      DefaultTableModel model = (DefaultTableModel)jTable2.getModel();
      this.jCheckBox1.setSelected(ServerConstants.ConnectorLog);
   }

   private void initComponents() {
      jFrame2 = new JFrame();
      this.jButton5 = new JButton();
      this.jScrollPane3 = new JScrollPane();
      this.jTabbedPane1 = new JTabbedPane();
      this.jPanel1 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      jTable1 = new JTable();
      this.jButton1 = new JButton();
      this.jButton2 = new JButton();
      this.jButton3 = new JButton();
      this.jTextField1 = new JTextField();
      this.jButton6 = new JButton();
      this.jButton7 = new JButton();
      this.jPanel2 = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      jTable2 = new JTable();
      this.jTextField4 = new JTextField();
      this.jButton14 = new JButton();
      this.jButton13 = new JButton();
      this.jPanel3 = new JPanel();
      this.jScrollPane4 = new JScrollPane();
      jTextArea2 = new JTextArea();
      this.jButton4 = new JButton();
      this.jCheckBox1 = new JCheckBox();
      this.jButton8 = new JButton();
      this.jLabel1 = new JLabel();
      this.jTextField2 = new JTextField();
      this.jTextField3 = new JTextField();
      this.jTextField5 = new JTextField();
      this.jLabel2 = new JLabel();
      this.jTextField6 = new JTextField();
      this.jTextField7 = new JTextField();
      this.jButton9 = new JButton();
      this.jLabel3 = new JLabel();
      this.jTextField8 = new JTextField();
      this.jTextField9 = new JTextField();
      this.jButton11 = new JButton();
      this.jLabel4 = new JLabel();
      this.jButton12 = new JButton();
      this.jComboBox1 = new JComboBox<>();
      this.jLabel5 = new JLabel();
      this.jTextField10 = new JTextField();
      this.jButton15 = new JButton();
      this.jTextField11 = new JTextField();
      this.jScrollPane6 = new JScrollPane();
      this.jList1 = new JList<>();
      this.jButton16 = new JButton();
      this.jButton17 = new JButton();
      this.jButton18 = new JButton();
      this.jLabel6 = new JLabel();
      this.jScrollPane7 = new JScrollPane();
      this.jList2 = new JList<>();
      this.jLabel7 = new JLabel();
      this.jTextField12 = new JTextField();
      this.jTextField13 = new JTextField();
      this.jLabel8 = new JLabel();
      this.jLabel9 = new JLabel();
      this.jButton20 = new JButton();
      this.jButton21 = new JButton();
      this.jCheckBox4 = new JCheckBox();
      this.jCheckBox5 = new JCheckBox();
      this.jTextField25 = new JTextField();
      this.jLabel18 = new JLabel();
      this.jLabel19 = new JLabel();
      this.jTextField26 = new JTextField();
      this.jTextField27 = new JTextField();
      this.jButton28 = new JButton();
      this.jLabel10 = new JLabel();
      this.jButton19 = new JButton();
      this.jButton22 = new JButton();
      this.jTextField14 = new JTextField();
      this.jLabel11 = new JLabel();
      this.jLabel12 = new JLabel();
      this.jTextField15 = new JTextField();
      this.jLabel13 = new JLabel();
      this.jTextField16 = new JTextField();
      this.jButton23 = new JButton();
      this.jComboBox2 = new JComboBox<>();
      this.jTextField17 = new JTextField();
      this.jLabel14 = new JLabel();
      this.jTextField18 = new JTextField();
      this.jLabel15 = new JLabel();
      this.jTextField19 = new JTextField();
      this.jButton24 = new JButton();
      this.jButton25 = new JButton();
      this.jLabel16 = new JLabel();
      this.jButton26 = new JButton();
      this.jScrollPane8 = new JScrollPane();
      this.jList3 = new JList<>();
      this.jButton27 = new JButton();
      this.jTextField20 = new JTextField();
      this.jTextField21 = new JTextField();
      this.jLabel17 = new JLabel();
      this.jTextField22 = new JTextField();
      this.jButton29 = new JButton();
      this.jLabel20 = new JLabel();
      this.jTextField23 = new JTextField();
      this.jButton30 = new JButton();
      this.jLabel21 = new JLabel();
      this.jTextField24 = new JTextField();
      this.jButton31 = new JButton();
      this.jTextField28 = new JTextField();
      this.jTextField29 = new JTextField();
      this.jCheckBox2 = new JCheckBox();
      this.jTextField30 = new JTextField();
      this.jButton32 = new JButton();
      this.jLabel22 = new JLabel();
      this.jLabel23 = new JLabel();
      this.jLabel24 = new JLabel();
      jFrame2.setName("jFrame2");
      jFrame2.setResizable(false);
      this.jButton5.setText("강제종료");
      this.jButton5.setName("jButton5");
      this.jButton5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton5ActionPerformed(evt);
         }
      });
      this.jScrollPane3.setName("jScrollPane3");
      jTable3.setModel(new DefaultTableModel(new Object[0][], new String[]{"Process Name", "Window Title"}) {
         Class[] types = new Class[]{String.class, String.class};

         @Override
         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }
      });
      jTable3.setName("jTable3");
      jTable3.getTableHeader().setReorderingAllowed(false);
      jTable3.getTableHeader().setResizingAllowed(false);
      this.jScrollPane3.setViewportView(jTable3);
      GroupLayout jFrame2Layout = new GroupLayout(jFrame2.getContentPane());
      jFrame2.getContentPane().setLayout(jFrame2Layout);
      jFrame2Layout.setHorizontalGroup(
         jFrame2Layout.createParallelGroup(1)
            .add(jFrame2Layout.createSequentialGroup().add(this.jScrollPane3, -2, 475, -2).add(0, 0, 32767))
            .add(jFrame2Layout.createSequentialGroup().addContainerGap().add(this.jButton5).addContainerGap(-1, 32767))
      );
      jFrame2Layout.setVerticalGroup(
         jFrame2Layout.createParallelGroup(1)
            .add(jFrame2Layout.createSequentialGroup().add(this.jScrollPane3, -1, 449, 32767).add(18, 18, 18).add(this.jButton5).addContainerGap())
      );
      this.setDefaultCloseOperation(3);
      this.setName("Form");
      this.jTabbedPane1.setName("서버관련");
      this.jPanel1.setAlignmentX(0.0F);
      this.jPanel1.setAlignmentY(0.0F);
      this.jPanel1.setName("jPanel1");
      this.jScrollPane1.setBorder(null);
      this.jScrollPane1.setName("jScrollPane1");
      jTable1.setModel(new DefaultTableModel(new Object[0][], new String[]{"아이디", "비밀번호", "아이피", "접속중인캐릭터", "클라이언트", "대표캐릭터"}) {
         Class[] types = new Class[]{String.class, String.class, String.class, String.class, Object.class, Object.class};

         @Override
         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }
      });
      jTable1.setName("jTable1");
      jTable1.getTableHeader().setReorderingAllowed(false);
      jTable1.getTableHeader().setResizingAllowed(false);
      jTable1.setShowHorizontalLines(false);
      jTable1.setShowVerticalLines(false);
      this.jScrollPane1.setViewportView(jTable1);
      if (jTable1.getColumnModel().getColumnCount() > 0) {
         jTable1.getColumnModel().getColumn(1).setMinWidth(0);
         jTable1.getColumnModel().getColumn(1).setMaxWidth(0);
         jTable1.getColumnModel().getColumn(4).setMinWidth(0);
         jTable1.getColumnModel().getColumn(4).setMaxWidth(0);
      }

      this.jButton1.setText("영구퇴장");
      this.jButton1.setToolTipText("");
      this.jButton1.setName("jButton1");
      this.jButton1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton1ActionPerformed(evt);
         }
      });
      this.jButton2.setText("강제퇴장");
      this.jButton2.setName("jButton2");
      this.jButton2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton2ActionPerformed(evt);
         }
      });
      this.jButton3.setText("리스트");
      this.jButton3.setName("jButton3");
      this.jButton3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton3ActionPerformed(evt);
         }
      });
      this.jTextField1.setText("jTextField1");
      this.jTextField1.setName("jTextField1");
      this.jButton6.setText("전체 공지");
      this.jButton6.setName("jButton6");
      this.jButton6.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton6ActionPerformed(evt);
         }
      });
      this.jButton7.setText("선택 공지");
      this.jButton7.setName("jButton7");
      this.jButton7.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton7ActionPerformed(evt);
         }
      });
      GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(1)
            .add(jPanel1Layout.createSequentialGroup().add(this.jTextField1).addPreferredGap(0).add(this.jButton6))
            .add(
               jPanel1Layout.createSequentialGroup()
                  .add(this.jButton2, -2, 100, -2)
                  .addPreferredGap(0)
                  .add(this.jButton1, -2, 100, -2)
                  .addPreferredGap(0)
                  .add(this.jButton3, -2, 100, -2)
                  .addPreferredGap(0, -1, 32767)
                  .add(this.jButton7)
            )
            .add(2, this.jScrollPane1, -1, 538, 32767)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(1)
            .add(
               jPanel1Layout.createSequentialGroup()
                  .add(this.jScrollPane1, -2, 388, -2)
                  .addPreferredGap(0)
                  .add(jPanel1Layout.createParallelGroup(3).add(this.jButton3).add(this.jButton1).add(this.jButton2).add(this.jButton7))
                  .addPreferredGap(1)
                  .add(jPanel1Layout.createParallelGroup(3).add(this.jTextField1, -2, -1, -2).add(this.jButton6))
                  .add(0, 131, 32767)
            )
      );
      this.jTabbedPane1.addTab("접속자", this.jPanel1);
      this.jPanel2.setName("jPanel2");
      this.jScrollPane2.setName("jScrollPane2");
      jTable2.setModel(new DefaultTableModel(new Object[0][], new String[]{"IP"}) {
         Class[] types = new Class[]{String.class};

         @Override
         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }
      });
      jTable2.setName("jTable2");
      this.jScrollPane2.setViewportView(jTable2);
      this.jTextField4.setText("127.0.0.1");
      this.jTextField4.setName("jTextField4");
      this.jButton14.setText("추가");
      this.jButton14.setName("jButton14");
      this.jButton14.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton14ActionPerformed(evt);
         }
      });
      this.jButton13.setText("삭제");
      this.jButton13.setToolTipText("");
      this.jButton13.setName("jButton13");
      this.jButton13.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton13ActionPerformed(evt);
         }
      });
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(1)
            .add(this.jScrollPane2, -1, 538, 32767)
            .add(
               jPanel2Layout.createSequentialGroup()
                  .add(
                     jPanel2Layout.createParallelGroup(2)
                        .add(jPanel2Layout.createSequentialGroup().add(this.jButton14, -2, 111, -2).add(26, 26, 26).add(this.jButton13, -2, 107, -2))
                        .add(this.jTextField4, -2, 244, -2)
                  )
                  .add(0, 0, 32767)
            )
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(1)
            .add(
               jPanel2Layout.createSequentialGroup()
                  .add(this.jScrollPane2, -2, 392, -2)
                  .addPreferredGap(0)
                  .add(this.jTextField4, -2, -1, -2)
                  .addPreferredGap(0)
                  .add(jPanel2Layout.createParallelGroup(3).add(this.jButton14).add(this.jButton13))
                  .add(0, 133, 32767)
            )
      );
      this.jTabbedPane1.addTab("아이피 차단", this.jPanel2);
      this.jPanel3.setName("jPanel3");
      this.jScrollPane4.setName("jScrollPane4");
      jTextArea2.setColumns(20);
      jTextArea2.setRows(5);
      jTextArea2.setName("jTextArea2");
      this.jScrollPane4.setViewportView(jTextArea2);
      this.jButton4.setText("청소");
      this.jButton4.setName("jButton4");
      this.jButton4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jButton4ActionPerformed(evt);
         }
      });
      this.jCheckBox1.setSelected(false);
      this.jCheckBox1.setText("로그기록");
      this.jCheckBox1.setName("jCheckBox1");
      this.jCheckBox1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ConnectorPanel.this.jCheckBox1ActionPerformed(evt);
         }
      });
      GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
      this.jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(1)
            .add(this.jScrollPane4, -1, 538, 32767)
            .add(
               jPanel3Layout.createSequentialGroup()
                  .addContainerGap()
                  .add(this.jButton4, -2, 100, -2)
                  .add(18, 18, 18)
                  .add(this.jCheckBox1)
                  .addContainerGap(-1, 32767)
            )
      );
      jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(1)
            .add(
               jPanel3Layout.createSequentialGroup()
                  .add(this.jScrollPane4, -2, 403, -2)
                  .addPreferredGap(0)
                  .add(jPanel3Layout.createParallelGroup(1).add(this.jCheckBox1).add(this.jButton4, -2, 129, -2))
                  .addContainerGap(-1, 32767)
            )
      );
      this.jTabbedPane1.addTab("로그", this.jPanel3);
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(layout.createSequentialGroup().add(this.jTabbedPane1, -2, 543, -2).add(0, 0, 32767)));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(this.jTabbedPane1));
      this.pack();
   }

   public void logChat(String a) {
      jTextArea2.append(a);
      jTextArea2.append("\r\n");
   }

   private void jButton5ActionPerformed(ActionEvent evt) {
      String procn = jTable3.getValueAt(jTable3.getSelectedRow(), 0).toString();
      ConnectorClient cc = (ConnectorClient)jTable1.getValueAt(jTable1.getSelectedRow(), 4);
      cc.send(PacketCreator.sendProcEnd(procn));
   }

   private void jCheckBox1ActionPerformed(ActionEvent evt) {
      ServerConstants.ConnectorLog = this.jCheckBox1.isSelected();
   }

   private void jButton4ActionPerformed(ActionEvent evt) {
      jTextArea2.setText("");
   }

   private void jButton13ActionPerformed(ActionEvent evt) {
      if (jTable2.getSelectedRow() <= -1) {
         txtMsg = "선택하지 않았습니다.";
         txtOpt = "오류";
         intOpt = 0;
         ConnectorPanel.Alert al = new ConnectorPanel.Alert();
         al.start();
      } else {
         int sel = jTable2.getSelectedRow();
         DefaultTableModel model = (DefaultTableModel)jTable2.getModel();
         model.removeRow(sel);

         try {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < model.getRowCount(); i++) {
               sb.append(model.getValueAt(i, 0)).append("\r\n");
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("KickList.txt")));
            bw.write(sb.toString());
            bw.flush();
            bw.close();
         } catch (FileNotFoundException var7) {
            Logger.getLogger(ConnectorPanel.class.getName()).log(Level.SEVERE, null, var7);
         } catch (IOException var8) {
            Logger.getLogger(ConnectorPanel.class.getName()).log(Level.SEVERE, null, var8);
         }
      }
   }

   private void jButton14ActionPerformed(ActionEvent evt) {
      DefaultTableModel model = (DefaultTableModel)jTable2.getModel();
      model.addRow(new Object[]{this.jTextField4.getText()});
      FileOutputStream out = null;
      String txt = null;

      try {
         out = new FileOutputStream("KickList.txt", true);
         txt = model.getValueAt(model.getRowCount() - 1, 0) + "\r\n";
         out.write(txt.getBytes(StandardCharsets.UTF_8));
      } catch (IOException var14) {
      } finally {
         try {
            if (out != null) {
               out.close();
            }
         } catch (IOException var13) {
         }
      }
   }

   private void jButton7ActionPerformed(ActionEvent evt) {
      if (jTable1.getSelectedRow() < 0) {
         txtMsg = "선택하지 않았습니다.";
         txtOpt = "오류";
         intOpt = 0;
         ConnectorPanel.Alert al = new ConnectorPanel.Alert();
         al.start();
      } else {
         ConnectorClient cc = (ConnectorClient)jTable1.getValueAt(jTable1.getSelectedRow(), 4);
         PacketEncoder mplew = new PacketEncoder();
         mplew.write(5);
         mplew.write(7);
         mplew.writeMapleAsciiString2(this.jTextField1.getText());
         cc.send(mplew.getPacket());

         for (String sp : jTable1.getValueAt(jTable1.getSelectedRow(), 3).toString().split(",")) {
            if (Center.Find.findChannel(sp) >= 0) {
               MapleCharacter chr = GameServer.getInstance(Center.Find.findChannel(sp)).getPlayerStorage().getCharacterByName(sp);
               if (chr != null) {
                  chr.getMap().broadcastMessage(CWvsContext.yellowChat("GM개인메세지 : " + this.jTextField1.getText()));
               }
            }
         }
      }
   }

   private void jButton6ActionPerformed(ActionEvent evt) {
      try {
         Center.Broadcast.broadcastMessage(CWvsContext.yellowChat("GM전체메세지 : " + this.jTextField1.getText()));
      } catch (Exception var3) {
         System.out.println("전체 메시지 전송 오류 발생");
         var3.printStackTrace();
      }
   }

   private void jButton3ActionPerformed(ActionEvent evt) {
      jFrame2.setVisible(true);
      jFrame2.setSize(480, 440);
      if (jTable1.getSelectedRow() < 0) {
         txtMsg = "선택하지 않았습니다.";
         txtOpt = "오류";
         intOpt = 0;
         ConnectorPanel.Alert al = new ConnectorPanel.Alert();
         al.start();
      } else {
         ConnectorClient cc = (ConnectorClient)jTable1.getValueAt(jTable1.getSelectedRow(), 4);
         PacketEncoder mplew = new PacketEncoder();
         mplew.write(3);
         cc.send(mplew.getPacket());
      }
   }

   private void jButton2ActionPerformed(ActionEvent evt) {
      ConnectorClient cc = (ConnectorClient)jTable1.getValueAt(jTable1.getSelectedRow(), 4);
   }

   private void jButton1ActionPerformed(ActionEvent evt) {
      if (jTable1.getSelectedRow() <= -1) {
         txtMsg = "선택하지 않았습니다.";
         txtOpt = "오류";
         intOpt = 0;
         ConnectorPanel.Alert al = new ConnectorPanel.Alert();
         al.start();
      } else {
         DefaultTableModel model = (DefaultTableModel)jTable2.getModel();
         Connection con = null;
         PreparedStatement ps = null;
         PreparedStatement ps1 = null;
         ResultSet rs = null;
         int accountid = 0;
         boolean secondAcc = false;
         String text = "SELECT * FROM accounts WHERE name = ?";

         try {
            con = DBConnection.getConnection();
            if (jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString().contains(",")) {
               for (int i = 1; i < jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString().split(",").length; i++) {
                  text = text + " or name = ?";
               }

               secondAcc = true;
            }

            ConnectorClient cc = (ConnectorClient)jTable1.getValueAt(jTable1.getSelectedRow(), 4);
            FileOutputStream out = null;
            String txt = null;

            try {
               out = new FileOutputStream("KickList.txt", true);
               txt = jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString().split("/")[1].split(":")[0] + "\r\n";
               out.write(txt.getBytes(StandardCharsets.UTF_8));
            } catch (IOException var48) {
            } finally {
               try {
                  if (out != null) {
                     out.close();
                  }
               } catch (IOException var47) {
               }

               model.addRow(new Object[]{jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString().split("/")[1].split(":")[0]});
            }

            ps = con.prepareStatement(text);
            if (secondAcc) {
               for (int i = 1; i <= jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString().split(",").length; i++) {
                  ps.setString(i, jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString().split(",")[i - 1]);
               }

               rs = ps.executeQuery();

               while (rs.next()) {
                  String conkey = rs.getString("connecterKey");
                  ps1 = con.prepareStatement("INSERT INTO connectorban (`connecterkey`, `ip`) VALUES (?, ?)");
                  ps1.setString(1, conkey);
                  ps1.setString(2, rs.getString("SessionIP"));
                  ps1.execute();
                  ps1.close();
                  ps1 = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ?, banby = '운영자' WHERE name = ?");
                  ps1.setString(1, "영구 퇴장 당하셨습니다.");
                  ps1.setString(2, rs.getString("name"));
                  ps1.executeUpdate();
                  ps1.close();
               }

               ps.close();
               rs.close();
            } else {
               ps.close();
               ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
               ps.setString(1, jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
               rs = ps.executeQuery();
               if (rs.next()) {
                  ps1 = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ?, banby = '운영자' WHERE name = ?");
                  ps1.setString(1, "영구 퇴장 당하셨습니다.");
                  ps1.setString(2, rs.getString("name"));
                  ps1.executeUpdate();
                  ps1.close();
               }

               ps.close();
               rs.close();
            }
         } catch (SQLException var50) {
            System.out.println("Ban Error : ");
            var50.printStackTrace();
         } finally {
            if (ps != null) {
               try {
                  ps.close();
               } catch (SQLException var46) {
               }
            }

            if (rs != null) {
               try {
                  rs.close();
               } catch (SQLException var45) {
               }
            }

            if (con != null) {
               try {
                  con.close();
               } catch (SQLException var44) {
                  var44.printStackTrace();
               }
            }
         }
      }
   }

   private void jTextField27ActionPerformed(ActionEvent evt) {
   }

   private void jCheckBox5ActionPerformed(ActionEvent evt) {
   }

   private void jButton21ActionPerformed(ActionEvent evt) {
   }

   private void jButton21MouseClicked(MouseEvent evt) {
   }

   private void jButton20ActionPerformed(ActionEvent evt) {
   }

   private void jButton20MouseClicked(MouseEvent evt) {
   }

   private void jTextField13ActionPerformed(ActionEvent evt) {
   }

   private void jTextField13MouseClicked(MouseEvent evt) {
   }

   private void jTextField12ActionPerformed(ActionEvent evt) {
   }

   private void jTextField12MouseClicked(MouseEvent evt) {
   }

   private void jButton18ActionPerformed(ActionEvent evt) {
   }

   private void jButton18MouseClicked(MouseEvent evt) {
      String data = Integer.parseInt(this.jTextField10.getText()) + "," + Integer.parseInt(this.jTextField11.getText());
      DefaultListModel model = new DefaultListModel();

      for (int i = 0; i < this.jList1.getModel().getSize(); i++) {
         if (i != this.jList1.getSelectedIndex()) {
            model.addElement(this.jList1.getModel().getElementAt(i));
         }
      }

      this.jList1.setModel(model);
   }

   private void jButton17ActionPerformed(ActionEvent evt) {
   }

   private void jButton17MouseClicked(MouseEvent evt) {
      DefaultListModel model = new DefaultListModel();
      this.jList1.setModel(model);
   }

   private void jButton16ActionPerformed(ActionEvent evt) {
   }

   private void jTextField11ActionPerformed(ActionEvent evt) {
   }

   private void jTextField11MouseClicked(MouseEvent evt) {
      this.jTextField11.setText("");
   }

   private void jButton15ActionPerformed(ActionEvent evt) {
   }

   private void jButton15MouseClicked(MouseEvent evt) {
      String data = Integer.parseInt(this.jTextField10.getText()) + "," + Integer.parseInt(this.jTextField11.getText());
      DefaultListModel model = new DefaultListModel();

      for (int i = 0; i < this.jList1.getModel().getSize(); i++) {
         model.addElement(this.jList1.getModel().getElementAt(i));
      }

      model.addElement(data);
      this.jList1.setModel(model);
   }

   private void jTextField10ActionPerformed(ActionEvent evt) {
   }

   private void jTextField10MouseClicked(MouseEvent evt) {
      this.jTextField10.setText("");
   }

   private void jComboBox1ActionPerformed(ActionEvent evt) {
   }

   private void jButton12ActionPerformed(ActionEvent evt) {
   }

   private void jButton11ActionPerformed(ActionEvent evt) {
   }

   private void jTextField9ActionPerformed(ActionEvent evt) {
   }

   private void jTextField9MouseClicked(MouseEvent evt) {
      this.jTextField9.setText("");
   }

   private void jTextField8ActionPerformed(ActionEvent evt) {
   }

   private void jTextField8MouseClicked(MouseEvent evt) {
      this.jTextField8.setText("");
   }

   private void jButton9ActionPerformed(ActionEvent evt) {
   }

   private void jTextField7ActionPerformed(ActionEvent evt) {
   }

   private void jTextField7MouseClicked(MouseEvent evt) {
      this.jTextField7.setText("");
   }

   private void jTextField6ActionPerformed(ActionEvent evt) {
   }

   private void jTextField6MouseClicked(MouseEvent evt) {
      this.jTextField6.setText("");
   }

   private void jTextField5ActionPerformed(ActionEvent evt) {
   }

   private void jTextField5MouseEntered(MouseEvent evt) {
   }

   private void jTextField5MouseClicked(MouseEvent evt) {
      this.jTextField5.setText("");
   }

   private void jTextField3ActionPerformed(ActionEvent evt) {
   }

   private void jTextField3MouseClicked(MouseEvent evt) {
      this.jTextField3.setText("");
   }

   private void jTextField2ActionPerformed(ActionEvent evt) {
   }

   private void jTextField2MouseClicked(MouseEvent evt) {
      this.jTextField2.setText("");
   }

   private void jButton8ActionPerformed(ActionEvent evt) {
   }

   private void jTextField7InputMethodTextChanged(InputMethodEvent evt) {
   }

   private void jTextField7KeyPressed(KeyEvent evt) {
      this.jTextField27.setText("0");
      this.jTextField26.setText("0");
   }

   private void jTextField25KeyPressed(KeyEvent evt) {
      this.jTextField27.setText("0");
      this.jTextField26.setText("0");
   }

   private void jTextField25KeyTyped(KeyEvent evt) {
   }

   private void jTextField7KeyTyped(KeyEvent evt) {
   }

   private void jButton28MouseClicked(MouseEvent evt) {
      int a = Integer.parseInt(this.jTextField7.getText().replaceAll("[^0-9]", ""));
      int b = 1;
      if (this.jCheckBox5.isSelected()) {
         b = Integer.parseInt(this.jTextField25.getText().replaceAll("[^0-9]", ""));
      }

      this.jTextField27.setText(a * b + "");
      if (this.jCheckBox4.isSelected()) {
         this.jTextField26.setText("0");
      } else {
         this.jTextField26.setText(a + "");
      }
   }

   private void jButton28ActionPerformed(ActionEvent evt) {
   }

   private void jCheckBox4StateChanged(ChangeEvent evt) {
   }

   private void jCheckBox5StateChanged(ChangeEvent evt) {
   }

   private void jCheckBox4ItemStateChanged(ItemEvent evt) {
      this.jTextField26.setText("0");
      this.jTextField27.setText("0");
   }

   private void jCheckBox5ItemStateChanged(ItemEvent evt) {
      this.jTextField26.setText("0");
      this.jTextField27.setText("0");
   }

   private void jButton32ActionPerformed(ActionEvent evt) {
   }

   private void jTextField30ActionPerformed(ActionEvent evt) {
   }

   private void jTextField29ActionPerformed(ActionEvent evt) {
   }

   private void jTextField29MouseClicked(MouseEvent evt) {
   }

   private void jTextField28ActionPerformed(ActionEvent evt) {
   }

   private void jTextField28MouseClicked(MouseEvent evt) {
   }

   private void jButton31ActionPerformed(ActionEvent evt) {
   }

   private void jTextField24ActionPerformed(ActionEvent evt) {
   }

   private void jTextField24MouseClicked(MouseEvent evt) {
   }

   private void jButton30ActionPerformed(ActionEvent evt) {
   }

   private void jTextField23MouseClicked(MouseEvent evt) {
   }

   private void jButton29ActionPerformed(ActionEvent evt) {
   }

   private void jButton29MouseClicked(MouseEvent evt) {
   }

   private void jTextField22MouseClicked(MouseEvent evt) {
   }

   private void jButton25ActionPerformed(ActionEvent evt) {
   }

   private void jTextField19ActionPerformed(ActionEvent evt) {
   }

   private void jTextField18ActionPerformed(ActionEvent evt) {
   }

   private void jButton23ActionPerformed(ActionEvent evt) {
   }

   private void jTextField14ActionPerformed(ActionEvent evt) {
   }

   private void jButton19ActionPerformed(ActionEvent evt) {
   }

   @Override
   public void actionPerformed(ActionEvent evt) {
      JMenuItem menu = (JMenuItem)evt.getSource();
      jFrame2.setVisible(true);
      jFrame2.setSize(480, 440);
   }

   private void Alert(String msg, String opt, int opt2) {
      JOptionPane.showMessageDialog(null, msg, opt, opt2);
   }

   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            ConnectorPanel cp = new ConnectorPanel();
            cp.setVisible(true);
         }
      });
   }

   public static int getModelId(String ip) {
      return 1;
   }

   public static void changeAllSwingComponentDefaultFont() {
      try {
         UIDefaults swingComponentDefaultTable = UIManager.getDefaults();
         Enumeration allDefaultKey = swingComponentDefaultTable.keys();

         while (allDefaultKey.hasMoreElements()) {
            String defaultKey = allDefaultKey.nextElement().toString();
            if (defaultKey.indexOf("font") != -1) {
               Font newDefaultFont = new Font("맑은 고딕", 1, 12);
               UIManager.put(defaultKey, newDefaultFont);
            }
         }
      } catch (Exception var4) {
         System.out.println("폰트 저장 오류 발생");
         var4.printStackTrace();
      }
   }

   class Alert extends Thread {
      @Override
      public void run() {
         try {
            ConnectorPanel.this.Alert(ConnectorPanel.txtMsg, ConnectorPanel.txtOpt, ConnectorPanel.intOpt);
         } catch (Exception var2) {
         }
      }
   }
}
