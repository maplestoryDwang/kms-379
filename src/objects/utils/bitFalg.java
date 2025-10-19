package objects.utils;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;
import objects.users.stats.SecondaryStatFlag;

public class bitFalg extends JDialog {
   private JPanel contentPane;
   private JButton buttonOK;
   private JButton buttonCancel;
   private JTextField GiveBuffPacketText;
   private JTable table1;

   public bitFalg() {
      this.setContentPane(this.contentPane);
      this.contentPane.setPreferredSize(new Dimension(500, 500));
      this.setModal(true);
      this.getRootPane().setDefaultButton(this.buttonOK);
      this.buttonOK.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            bitFalg.this.onOK();
         }
      });
      this.buttonCancel.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            bitFalg.this.onCancel();
         }
      });
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            bitFalg.this.onCancel();
         }
      });
      this.contentPane.registerKeyboardAction(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            bitFalg.this.onCancel();
         }
      }, KeyStroke.getKeyStroke(27, 0), 1);
   }

   private void onOK() {
      DefaultTableModel dtm = (DefaultTableModel)this.table1.getModel();
      dtm.setRowCount(0);
      dtm.setColumnCount(1);
      DefaultTableModel tableModel = (DefaultTableModel)this.table1.getModel();
      String giveBuffPacket = this.GiveBuffPacketText.getText();
      PacketDecoder giveBuffStream = new PacketDecoder(new ByteArrayByteStream(HexTool.getByteArrayFromHexString(giveBuffPacket)));
      StringBuilder extractedInformation = new StringBuilder();
      tableModel.addColumn("TestCal");
      new JTable(tableModel);
      extractedInformation.append("총 크기:").append(giveBuffStream.available()).append("\n");
      extractedInformation.append("더미 int * 2 제거크기:").append(giveBuffStream.available()).append("\n\n");

      for (int i = 31; i >= 1; i--) {
         int buffMask = giveBuffStream.readInt();
         if (buffMask > 0) {
            extractedInformation.append("포지션: ").append(i).append(" ");
            tableModel.insertRow(0, new Object[]{"포지션 :", i});
            extractedInformation.append("버프마스크: ").append(buffMask);
            tableModel.insertRow(0, new Object[]{"버프마스크: ", buffMask});

            for (int j = 0; j <= 31; j++) {
               int bitMask = 1 << j;
               if ((buffMask & bitMask) == bitMask) {
                  int bitFlag = getBitFlag(i, j);
                  tableModel.insertRow(0, new Object[]{"이름 : ", SecondaryStatFlag.getByBit(bitFlag)});
                  tableModel.insertRow(0, new Object[]{"플래그 : ", 31 - j});
                  tableModel.insertRow(0, new Object[]{"비트 플래그 : ", bitFlag});
               }
            }

            extractedInformation.append("\n").append("\n");
         }
      }

      extractedInformation.append("비트마스킹 이후 크기: ").append(giveBuffStream.available());
   }

   private void onCancel() {
   }

   public static int getBitFlag(int position, int buffMaskFlag) {
      return (31 - position) * 32 + (31 - buffMaskFlag);
   }

   public static void main(String[] args) {
      bitFalg dialog = new bitFalg();
      dialog.pack();
      dialog.setVisible(true);
      System.exit(0);
   }
}
