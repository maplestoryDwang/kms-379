package objects.utils;

import constants.GameConstants;
import constants.JosaType;
import constants.Locales;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import logging.LoggingManager;
import network.auction.AuctionServer;
import network.center.Center;
import network.center.WeeklyItemManager;
import network.center.praise.PraiseDonationMeso;
import network.center.praise.PraiseDonationMesoRank;
import network.center.praise.PraisePointRank;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.shop.CashShopServer;
import objects.androids.Android;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.child.dojang.DojangRanking;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCabinet;
import objects.users.MapleCabinetItem;
import objects.users.MapleCharacter;

public class AdminClient extends JFrame {
   private int loadedThread = 0;
   private int count = 0;
   private int notSaveCount = 0;
   private static final List<String> names = new ArrayList<>();
   private static final List<String> chatLogs = new ArrayList<>();
   public static boolean freezeChat = false;
   private static long nextDisableChatTime = 0L;
   private static boolean disabledChat = false;
   private static long startTime = 0L;
   private static ReentrantReadWriteLock chatLogLock;
   private JButton FreezeChat;
   private JTextField Receiver;
   public static JList<String> chatList;
   public static JList connectList;
   private static JLabel dongsi;
   private static JTextField dropRate;
   private static JTextField expRate;
   private JTextField hongboPoint;
   private JTextField hottimeEnd;
   private JTextField hottimeItemID;
   private JTextField hottimeRate;
   private JTextField hottimeRecvCount;
   private JTextField hottimeStart;
   private JTextField itemCode;
   private JButton jButton1;
   private JButton jButton10;
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
   private JButton jButton3;
   private JButton jButton4;
   private JButton jButton5;
   private JButton jButton6;
   private JButton jButton7;
   private JButton jButton8;
   private JButton jButton9;
   private JCheckBox jCheckBox1;
   private JCheckBox jCheckBox2;
   private JComboBox<String> jComboBox1;
   private JLabel jLabel1;
   private JLabel jLabel10;
   private JLabel jLabel11;
   private JLabel jLabel12;
   private JLabel jLabel13;
   private JLabel jLabel14;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JLabel jLabel9;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JSlider jSlider1;
   private JTextField killPoint;
   private JTextField meso;
   private static JTextField mesoRate;
   private JTextField noticeMsg;
   private JTextField realCash;
   private static JLabel runtime;

   public AdminClient() {
      this.initComponents();
   }

   private void initComponents() {
      this.jSlider1 = new JSlider();
      this.jScrollPane1 = new JScrollPane();
      connectList = new JList();
      dongsi = new JLabel();
      this.jScrollPane2 = new JScrollPane();
      chatList = new JList<>();
      this.jLabel2 = new JLabel();
      this.jButton2 = new JButton();
      this.jButton3 = new JButton();
      this.jButton1 = new JButton();
      this.realCash = new JTextField();
      this.jButton4 = new JButton();
      this.hongboPoint = new JTextField();
      this.jButton5 = new JButton();
      this.itemCode = new JTextField();
      this.jButton6 = new JButton();
      this.meso = new JTextField();
      this.jButton7 = new JButton();
      this.Receiver = new JTextField();
      this.jButton8 = new JButton();
      this.jLabel5 = new JLabel();
      this.jLabel6 = new JLabel();
      this.hottimeItemID = new JTextField();
      this.hottimeRecvCount = new JTextField();
      this.jButton9 = new JButton();
      this.jLabel7 = new JLabel();
      this.jLabel8 = new JLabel();
      this.jLabel9 = new JLabel();
      this.jLabel10 = new JLabel();
      this.jLabel11 = new JLabel();
      this.FreezeChat = new JButton();
      this.noticeMsg = new JTextField();
      this.jButton10 = new JButton();
      this.jButton11 = new JButton();
      this.jButton12 = new JButton();
      this.jButton13 = new JButton();
      this.jButton14 = new JButton();
      this.killPoint = new JTextField();
      this.jButton15 = new JButton();
      expRate = new JTextField();
      mesoRate = new JTextField();
      dropRate = new JTextField();
      this.jLabel1 = new JLabel();
      this.jLabel3 = new JLabel();
      this.jLabel4 = new JLabel();
      this.jButton16 = new JButton();
      runtime = new JLabel();
      this.jLabel12 = new JLabel();
      this.hottimeStart = new JTextField();
      this.jLabel13 = new JLabel();
      this.hottimeEnd = new JTextField();
      this.jButton17 = new JButton();
      this.jCheckBox1 = new JCheckBox();
      this.hottimeRate = new JTextField();
      this.jLabel14 = new JLabel();
      this.jButton19 = new JButton();
      this.jComboBox1 = new JComboBox<>();
      this.jCheckBox2 = new JCheckBox();
      this.jButton18 = new JButton();
      this.jButton20 = new JButton();
      this.jButton21 = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Jihyeon AdminClient");
      connectList.setFixedCellWidth(200);
      connectList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent evt) {
            AdminClient.this.connectListMouseClicked(evt);
         }
      });
      this.jScrollPane1.setViewportView(connectList);
      dongsi.setText("同时在线人数 : 0名");
      dongsi.setToolTipText("");
      this.jScrollPane2.setViewportView(chatList);
      this.jLabel2.setText("实时聊天日志");
      this.jButton2.setText("服务器保存");
      this.jButton2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton2ActionPerformed(evt);
         }
      });
      this.jButton3.setText("拍卖行自动保存停止");
      this.jButton3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton3ActionPerformed(evt);
         }
      });
      this.jButton1.setText("全部召唤到村庄");
      this.jButton1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton1ActionPerformed(evt);
         }
      });
      this.realCash.setFont(new Font("굴림", 0, 10));
      this.realCash.setText("要发放的点数");
      this.realCash.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.realCashMouseReleased(evt);
         }
      });
      this.realCash.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.realCashActionPerformed(evt);
         }
      });
      this.jButton4.setText("赞助点数发放");
      this.jButton4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton4ActionPerformed(evt);
         }
      });
      this.hongboPoint.setFont(new Font("굴림", 0, 10));
      this.hongboPoint.setText("要发放的点数");
      this.hongboPoint.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.hongboPointMouseReleased(evt);
         }
      });
      this.hongboPoint.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.hongboPointActionPerformed(evt);
         }
      });
      this.jButton5.setText("推广点数发放");
      this.jButton5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton5ActionPerformed(evt);
         }
      });
      this.itemCode.setFont(new Font("굴림", 0, 10));
      this.itemCode.setText("物品代码,数量");
      this.itemCode.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            AdminClient.this.itemCodeMousePressed(evt);
         }
      });
      this.itemCode.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.itemCodeActionPerformed(evt);
         }
      });
      this.jButton6.setText("物品发放");
      this.jButton6.setMaximumSize(new Dimension(125, 23));
      this.jButton6.setMinimumSize(new Dimension(125, 23));
      this.jButton6.setPreferredSize(new Dimension(125, 23));
      this.jButton6.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton6ActionPerformed(evt);
         }
      });
      this.meso.setFont(new Font("굴림", 0, 10));
      this.meso.setText("要发放的金币");
      this.meso.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.mesoMouseReleased(evt);
         }
      });
      this.meso.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.mesoActionPerformed(evt);
         }
      });
      this.jButton7.setText("金币发放");
      this.jButton7.setMaximumSize(new Dimension(125, 23));
      this.jButton7.setMinimumSize(new Dimension(125, 23));
      this.jButton7.setPreferredSize(new Dimension(125, 23));
      this.jButton7.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton7ActionPerformed(evt);
         }
      });
      this.Receiver.setFont(new Font("맑은 고딕", 1, 24));
      this.Receiver.setText("要发放的角色名称");
      this.Receiver.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.ReceiverMouseReleased(evt);
         }
      });
      this.Receiver.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.ReceiverActionPerformed(evt);
         }
      });
      this.jButton8.setText("GM权限授予/解除");
      this.jButton8.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton8ActionPerformed(evt);
         }
      });
      this.jLabel6.setFont(new Font("맑은 고딕", 1, 24));
      this.jLabel6.setText("热门时间发放 -");
      this.hottimeItemID.setText("物品代码,数量");
      this.hottimeItemID.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.hottimeItemIDMouseReleased(evt);
         }
      });
      this.hottimeItemID.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.hottimeItemIDActionPerformed(evt);
         }
      });
      this.hottimeRecvCount.setText("接收发放的会员数");
      this.hottimeRecvCount.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.hottimeRecvCountMouseReleased(evt);
         }
      });
      this.hottimeRecvCount.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.hottimeRecvCountActionPerformed(evt);
         }
      });
      this.jButton9.setText("热门时间发放!");
      this.jButton9.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton9ActionPerformed(evt);
         }
      });
      this.jLabel7.setFont(new Font("맑은 고딕", 1, 24));
      this.jLabel7.setText("热门时间帮助");
      this.jLabel8.setText("如果要向所有会员发放，请在接收发放的会员数");
      this.jLabel9.setText("栏中填写 ALL 或 全部。");
      this.jLabel10.setFont(new Font("맑은 고딕", 0, 14));
      this.jLabel10.setText("Made by. Jihyeon");
      this.jLabel11.setFont(new Font("맑은 고딕", 1, 36));
      this.jLabel11.setText("Ganglim");
      this.FreezeChat.setText("冻结聊天窗口");
      this.FreezeChat.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.FreezeChatActionPerformed(evt);
         }
      });
      this.noticeMsg.setText("输入公告内容");
      this.noticeMsg.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.noticeMsgMouseReleased(evt);
         }
      });
      this.jButton10.setText("弹窗公告");
      this.jButton10.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton10ActionPerformed(evt);
         }
      });
      this.jButton11.setText("滚动公告");
      this.jButton11.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton11ActionPerformed(evt);
         }
      });
      this.jButton12.setText("聊天窗口公告");
      this.jButton12.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton12ActionPerformed(evt);
         }
      });
      this.jButton13.setText("屏幕中央公告");
      this.jButton13.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton13ActionPerformed(evt);
         }
      });
      this.jButton14.setText("全部发送");
      this.jButton14.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton14ActionPerformed(evt);
         }
      });
      this.killPoint.setFont(new Font("굴림", 0, 10));
      this.killPoint.setText("要发放的点数");
      this.killPoint.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AdminClient.this.killPointMouseReleased(evt);
         }
      });
      this.jButton15.setText("狩猎点数发放");
      this.jButton15.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton15ActionPerformed(evt);
         }
      });
      expRate.setText("经验值");
      mesoRate.setText("金币");
      dropRate.setText("掉落");
      this.jLabel1.setText("经验值");
      this.jLabel3.setText("金币");
      this.jLabel4.setText("掉落");
      this.jButton16.setText("倍率更改");
      this.jButton16.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton16ActionPerformed(evt);
         }
      });
      runtime.setText("服务器运行时间 : ");
      this.jLabel12.setText("自动热门时间经验值活动 -");
      this.hottimeStart.setText("20:30");
      this.hottimeStart.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.hottimeStartActionPerformed(evt);
         }
      });
      this.jLabel13.setText("~");
      this.hottimeEnd.setText("21:30");
      this.jButton17.setText("设置更改");
      this.jButton17.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton17ActionPerformed(evt);
         }
      });
      this.jCheckBox1.setText("禁用");
      this.jCheckBox1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jCheckBox1ActionPerformed(evt);
         }
      });
      this.hottimeRate.setText("2.0");
      this.jLabel14.setText("*");
      this.jButton19.setText("发放");
      this.jButton19.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton19ActionPerformed(evt);
         }
      });
      this.jComboBox1
         .setModel(
            new DefaultComboBoxModel<>(
               new String[]{
                  "新手礼包",
                  "娱乐强化礼包",
                  "Tier.1 (10万)",
                  "Tier.2 (30万)",
                  "Tier.3 (50万)",
                  "Tier.4 (75万)",
                  "Tier.5 (100万)",
                  "Tier.6 (150万)",
                  "Tier.7 (200万)",
                  "250万奖励 (250万)",
                  "Tier.8 (300万)",
                  "350万奖励 (350万)",
                  "Tier.9 (400万)",
                  "450万奖励 (450万)",
                  "Tier.X (500万)",
                  "550万奖励 (550万)",
                  "Tier.XI (600万)",
                  "650万奖励 (650万)",
                  "Tier.XII (700万)",
                  "750万奖励 (750万)",
                  "Tier.XIII (800万)",
                  "850万奖励 (850万)",
                  "Tier.XIV (900万)",
                  "950万奖励 (950万)",
                  "Tier.XV (1000万)",
                  "1050万奖励 (1050万)",
                  "1100万奖励 (1100万)",
                  "1150万奖励 (1150万)",
                  "LUXURY (1200万)",
                  "1250万奖励 (1250万)",
                  "1300万奖励 (1300万)",
                  "1350万奖励 (1350万)",
                  "PLATINUM (1400万)",
                  "1450万奖励 (1450万)",
                  "1500万奖励 (1500万)",
                  "1550万奖励 (1550万)",
                  "ROYAL (1600万)",
                  "1650万奖励 (1650万)",
                  "1700万奖励 (1700万)",
                  "1750万奖励 (1750万)",
                  "PRESTIGE (1800万)",
                  "1850万奖励 (1850万)",
                  "1900万奖励 (1900万)",
                  "1950万奖励 (1950万)",
                  "ILLIONAIRE (2000万)"
               }
            )
         );
      this.jCheckBox2.setText("聊天日志禁用");
      this.jCheckBox2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jCheckBox2ActionPerformed(evt);
         }
      });
      this.jButton18.setText("序列号封禁");
      this.jButton18.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton18ActionPerformed(evt);
         }
      });
      this.jButton20.setText("序列号封禁解除");
      this.jButton20.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton20ActionPerformed(evt);
         }
      });
      this.jButton21.setText("冻结拍卖行");
      this.jButton21.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AdminClient.this.jButton21ActionPerformed(evt);
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
                     layout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel10).addComponent(this.jLabel11))
                              .addGap(76, 76, 76)
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel9).addComponent(this.jLabel7).addComponent(this.jLabel8)
                              )
                              .addGap(18, 18, 18)
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup().addComponent(this.jLabel12).addGap(0, 0, 32767))
                                                .addGroup(
                                                   layout.createSequentialGroup()
                                                      .addComponent(this.hottimeStart, -2, 78, -2)
                                                      .addPreferredGap(ComponentPlacement.RELATED)
                                                      .addComponent(this.jLabel13)
                                                      .addPreferredGap(ComponentPlacement.RELATED)
                                                      .addComponent(this.hottimeEnd, -2, 78, -2)
                                                      .addGap(11, 11, 11)
                                                      .addComponent(this.jLabel14, -2, 14, -2)
                                                      .addPreferredGap(ComponentPlacement.RELATED)
                                                      .addComponent(this.hottimeRate, -1, 66, 32767)
                                                      .addGap(15, 15, 15)
                                                      .addComponent(this.jButton17)
                                                      .addPreferredGap(ComponentPlacement.UNRELATED)
                                                      .addComponent(this.jCheckBox1)
                                                )
                                          )
                                          .addGap(11, 11, 11)
                                    )
                                    .addComponent(this.FreezeChat, -1, -1, 32767)
                              )
                        )
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup().addComponent(this.noticeMsg).addGap(19, 19, 19))
                                    .addGroup(
                                       Alignment.TRAILING,
                                       layout.createSequentialGroup()
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup().addGap(0, 0, 32767).addComponent(this.jScrollPane1, -2, 253, -2))
                                                .addGroup(
                                                   layout.createSequentialGroup()
                                                      .addGroup(
                                                         layout.createParallelGroup(Alignment.LEADING, false)
                                                            .addComponent(this.jButton16, -1, -1, 32767)
                                                            .addGroup(
                                                               layout.createSequentialGroup()
                                                                  .addGroup(
                                                                     layout.createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(expRate, -2, 76, -2)
                                                                        .addComponent(this.jLabel1)
                                                                  )
                                                                  .addGap(13, 13, 13)
                                                                  .addGroup(
                                                                     layout.createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(mesoRate, -2, 76, -2)
                                                                        .addComponent(this.jLabel3)
                                                                  )
                                                                  .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                  .addGroup(
                                                                     layout.createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(this.jLabel4)
                                                                        .addComponent(dropRate, -2, 76, -2)
                                                                  )
                                                            )
                                                            .addComponent(this.jComboBox1, 0, -1, 32767)
                                                      )
                                                      .addGap(0, 0, 32767)
                                                )
                                          )
                                          .addPreferredGap(ComponentPlacement.UNRELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(runtime)
                                                .addGroup(
                                                   layout.createParallelGroup(Alignment.TRAILING, false)
                                                      .addComponent(this.jButton8, Alignment.LEADING, -1, -1, 32767)
                                                      .addComponent(this.Receiver, Alignment.LEADING)
                                                      .addComponent(this.jButton19, Alignment.LEADING, -2, 241, -2)
                                                      .addGroup(
                                                         layout.createSequentialGroup()
                                                            .addGroup(
                                                               layout.createParallelGroup(Alignment.LEADING)
                                                                  .addComponent(this.realCash)
                                                                  .addComponent(this.hongboPoint, Alignment.TRAILING)
                                                                  .addComponent(this.itemCode, Alignment.TRAILING)
                                                                  .addComponent(this.meso)
                                                                  .addComponent(this.killPoint)
                                                                  .addComponent(this.jButton18, -2, 109, -2)
                                                            )
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addGroup(
                                                               layout.createParallelGroup(Alignment.LEADING, false)
                                                                  .addComponent(this.jButton20, -1, -1, 32767)
                                                                  .addComponent(this.jButton4, -1, -1, 32767)
                                                                  .addComponent(this.jButton5, -1, -1, 32767)
                                                                  .addComponent(this.jButton6, -1, -1, 32767)
                                                                  .addComponent(this.jButton7, -1, -1, 32767)
                                                                  .addComponent(this.jButton15, -1, -1, 32767)
                                                            )
                                                      )
                                                )
                                          )
                                          .addGap(18, 18, 18)
                                    )
                                    .addGroup(layout.createSequentialGroup().addComponent(dongsi, -1, -1, 32767).addGap(331, 331, 331))
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.TRAILING, false)
                                                .addComponent(this.jButton21, Alignment.LEADING, -1, -1, 32767)
                                                .addGroup(
                                                   layout.createSequentialGroup()
                                                      .addGroup(
                                                         layout.createParallelGroup(Alignment.LEADING, false)
                                                            .addComponent(this.jButton3, -1, -1, 32767)
                                                            .addComponent(this.jButton2, -1, -1, 32767)
                                                            .addComponent(this.jButton1, Alignment.TRAILING, -2, 258, -2)
                                                      )
                                                      .addGroup(
                                                         layout.createParallelGroup(Alignment.LEADING)
                                                            .addGroup(
                                                               layout.createSequentialGroup().addGap(121, 121, 121).addComponent(this.jButton14, -1, -1, 32767)
                                                            )
                                                            .addGroup(
                                                               layout.createSequentialGroup()
                                                                  .addGap(9, 9, 9)
                                                                  .addGroup(
                                                                     layout.createParallelGroup(Alignment.TRAILING)
                                                                        .addComponent(this.jButton9, -1, -1, 32767)
                                                                        .addComponent(this.jLabel6, Alignment.LEADING)
                                                                        .addGroup(
                                                                           layout.createSequentialGroup()
                                                                              .addComponent(this.hottimeItemID, -2, 107, -2)
                                                                              .addGap(22, 22, 22)
                                                                              .addComponent(this.hottimeRecvCount, -2, 108, -2)
                                                                        )
                                                                  )
                                                            )
                                                      )
                                                )
                                                .addGroup(
                                                   Alignment.LEADING,
                                                   layout.createSequentialGroup()
                                                      .addComponent(this.jButton10, -2, 114, -2)
                                                      .addPreferredGap(ComponentPlacement.UNRELATED)
                                                      .addComponent(this.jButton11, -2, 121, -2)
                                                      .addPreferredGap(ComponentPlacement.UNRELATED)
                                                      .addComponent(this.jButton12, -2, 108, -2)
                                                      .addPreferredGap(ComponentPlacement.UNRELATED)
                                                      .addComponent(this.jButton13)
                                                )
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                    )
                              )
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup().addComponent(this.jLabel5).addGap(0, 461, 32767))
                                    .addComponent(this.jScrollPane2, Alignment.TRAILING, -1, 461, 32767)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.jLabel2)
                                          .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                          .addComponent(this.jCheckBox2)
                                    )
                              )
                        )
                  )
                  .addGap(22, 22, 22)
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(dongsi)
                        .addComponent(runtime)
                        .addComponent(this.jCheckBox2)
                        .addComponent(this.jLabel2)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING, false)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.jScrollPane1, -2, 210, -2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.jLabel4)
                                                .addComponent(this.jLabel3)
                                                .addComponent(this.jLabel1)
                                          )
                                          .addGap(9, 9, 9)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(mesoRate, -2, -1, -2)
                                                .addComponent(dropRate, -2, -1, -2)
                                                .addComponent(expRate, -2, -1, -2)
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(this.jButton16)
                                    )
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.Receiver, -2, 62, -2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(this.jButton8, -2, 31, -2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.realCash, -2, 23, -2)
                                                .addComponent(this.jButton4, -2, 23, -2)
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.hongboPoint, -2, 23, -2)
                                                .addComponent(this.jButton5)
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.itemCode, -2, 23, -2)
                                                .addComponent(this.jButton6, -2, -1, -2)
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.meso, -2, 23, -2)
                                                .addComponent(this.jButton7, -2, -1, -2)
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.jButton15)
                                                .addComponent(this.killPoint, -2, 23, -2)
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(this.jButton18, -1, -1, 32767)
                                                .addComponent(this.jButton20, -2, 35, -2)
                                          )
                                    )
                              )
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(this.jButton19, -1, -1, 32767)
                                    .addComponent(this.jComboBox1, -2, 51, -2)
                              )
                              .addPreferredGap(ComponentPlacement.UNRELATED)
                              .addComponent(this.noticeMsg, -2, 31, -2)
                              .addPreferredGap(ComponentPlacement.UNRELATED)
                              .addGroup(
                                 layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(this.jButton10)
                                    .addComponent(this.jButton11)
                                    .addComponent(this.jButton12)
                                    .addComponent(this.jButton13)
                              )
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.jButton14)
                                          .addGap(7, 7, 7)
                                          .addComponent(this.jLabel6)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.hottimeItemID, -2, 35, -2)
                                                .addComponent(this.hottimeRecvCount, -2, 35, -2)
                                          )
                                    )
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.jButton2, -2, 51, -2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(this.jButton3, -2, 51, -2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(this.jButton1, -2, 51, -2)
                                                .addComponent(this.jButton9, -2, 51, -2)
                                          )
                                    )
                              )
                              .addPreferredGap(ComponentPlacement.UNRELATED)
                              .addComponent(this.jButton21, -2, 51, -2)
                        )
                        .addComponent(this.jScrollPane2)
                  )
                  .addPreferredGap(ComponentPlacement.UNRELATED, -1, 32767)
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGap(17, 17, 17)
                              .addComponent(this.jLabel7)
                              .addGap(3, 3, 3)
                              .addComponent(this.jLabel8)
                              .addGap(11, 11, 11)
                              .addComponent(this.jLabel9)
                        )
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGap(37, 37, 37)
                              .addComponent(this.jLabel10)
                              .addGap(1, 1, 1)
                              .addComponent(this.jLabel11, -2, 40, -2)
                        )
                        .addGroup(
                           layout.createSequentialGroup()
                              .addComponent(this.jLabel12)
                              .addGroup(
                                 layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(this.hottimeStart, -2, -1, -2)
                                    .addComponent(this.jLabel13)
                                    .addComponent(this.hottimeEnd, -2, -1, -2)
                                    .addComponent(this.jCheckBox1)
                                    .addComponent(this.jButton17, -2, 21, -2)
                                    .addComponent(this.hottimeRate, -2, -1, -2)
                                    .addComponent(this.jLabel14)
                              )
                              .addPreferredGap(ComponentPlacement.UNRELATED)
                              .addComponent(this.FreezeChat, -2, 61, -2)
                        )
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jLabel5)
            )
      );
      this.pack();
   }

   private void setLoadedThread(int set) {
      this.loadedThread = set;
   }

   private void addLoadedThread() {
      this.loadedThread++;
   }

   public void setCount(int count) {
      this.count = count;
   }

   private void addCount() {
      this.count++;
   }

   public void setNotSaveCount(int notSaveCount) {
      this.notSaveCount = notSaveCount;
   }

   private void addNotSaveCount() {
      this.notSaveCount++;
   }

   private void jButton2ActionPerformed(ActionEvent evt) {
      if (ServerConstants.workingSave) {
         JOptionPane.showMessageDialog(this, "自动保存已在运行中。", "Jihyeon 1.2.331", 1);
      } else {
         ServerConstants.workingSave = true;
         Timer.EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               long startTime = System.currentTimeMillis();
               ServerConstants.totalSaveCount.addAndGet(1);
               Center.Auction.save();
               AdminClient.this.addLoadedThread();
               ServerConstants.currentSaveCount.addAndGet(1);
               long endTime = System.currentTimeMillis();
               System.out.println("拍卖保存完成 (完成时间 : " + (endTime - startTime) + " m/s)");
               int total = ServerConstants.totalSaveCount.get();
               int current = ServerConstants.currentSaveCount.get();
               if (current == total) {
                  System.out.println("所有保存已完成。");
                  ServerConstants.totalSaveCount.set(0);
                  ServerConstants.currentSaveCount.set(0);
                  ServerConstants.workingSave = false;
               }
            }
         }, 0L);
         Timer.EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               ServerConstants.totalSaveCount.addAndGet(1);

               for (GameServer cs : GameServer.getAllInstances()) {
                  for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                     if (chr != null) {
                        if (chr.getClient().getSession().isOpen()) {
                           chr.saveToDB(false, false);
                           AdminClient.this.addCount();
                        }

                        if (System.currentTimeMillis() - chr.getLastHeartBeatTime() > 120000L) {
                           AdminClient.this.addNotSaveCount();
                        }
                     }
                  }
               }

               for (MapleCharacter chrx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                  if (chrx != null) {
                     chrx.getClient().disconnect(true);
                  }
               }

               ServerConstants.currentSaveCount.addAndGet(1);
               System.out.println("角色保存完成");
               int total = ServerConstants.totalSaveCount.get();
               int current = ServerConstants.currentSaveCount.get();
               if (current == total) {
                  System.out.println("所有保存已完成。");
                  ServerConstants.totalSaveCount.set(0);
                  ServerConstants.currentSaveCount.set(0);
                  ServerConstants.workingSave = false;
               }
            }
         }, 0L);
         if (!DBConfig.isGanglim) {
            Timer.EtcTimer.getInstance().schedule(new Runnable() {
               @Override
               public void run() {
                  ServerConstants.totalSaveCount.addAndGet(1);
                  WeeklyItemManager.saveWeeklyItems();
                  System.out.println("周间物品保存完成");
                  ServerConstants.currentSaveCount.addAndGet(1);
                  int total = ServerConstants.totalSaveCount.get();
                  int current = ServerConstants.currentSaveCount.get();
                  if (current == total) {
                     System.out.println("모든 저장이 완료되었습니다.");
                     ServerConstants.totalSaveCount.set(0);
                     ServerConstants.currentSaveCount.set(0);
                     ServerConstants.workingSave = false;
                  }
               }
            }, 0L);
         }

         Timer.EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               ServerConstants.totalSaveCount.addAndGet(1);
               Center.Guild.save();
               System.out.println("公会保存完成");
               ServerConstants.currentSaveCount.addAndGet(1);
               int total = ServerConstants.totalSaveCount.get();
               int current = ServerConstants.currentSaveCount.get();
               if (current == total) {
                  System.out.println("모든 저장이 완료되었습니다.");
                  ServerConstants.totalSaveCount.set(0);
                  ServerConstants.currentSaveCount.set(0);
                  ServerConstants.workingSave = false;
               }
            }
         }, 0L);
         Timer.EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               ServerConstants.totalSaveCount.addAndGet(1);
               Center.Alliance.save();
               System.out.println("联盟保存完成");
               ServerConstants.currentSaveCount.addAndGet(1);
               int total = ServerConstants.totalSaveCount.get();
               int current = ServerConstants.currentSaveCount.get();
               if (current == total) {
                  System.out.println("모든 저장이 완료되었습니다.");
                  ServerConstants.totalSaveCount.set(0);
                  ServerConstants.currentSaveCount.set(0);
                  ServerConstants.workingSave = false;
               }
            }
         }, 0L);
         Timer.EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               ServerConstants.totalSaveCount.addAndGet(1);
               DojangRanking.saveRanks();
               System.out.println("武陵道场排名保存完成");
               ServerConstants.currentSaveCount.addAndGet(1);
               int total = ServerConstants.totalSaveCount.get();
               int current = ServerConstants.currentSaveCount.get();
               if (current == total) {
                  System.out.println("모든 저장이 완료되었습니다.");
                  ServerConstants.totalSaveCount.set(0);
                  ServerConstants.currentSaveCount.set(0);
                  ServerConstants.workingSave = false;
               }
            }
         }, 0L);
         if (!DBConfig.isGanglim) {
            Timer.EtcTimer.getInstance().schedule(new Runnable() {
               @Override
               public void run() {
                  ServerConstants.totalSaveCount.addAndGet(1);
                  PraisePointRank.loadRanks();
                  System.out.println("赞扬点数保存完成");
                  ServerConstants.currentSaveCount.addAndGet(1);
                  ServerConstants.currentSaveCount.addAndGet(1);
                  int total = ServerConstants.totalSaveCount.get();
                  int current = ServerConstants.currentSaveCount.get();
                  if (current == total) {
                     System.out.println("모든 저장이 완료되었습니다.");
                     ServerConstants.totalSaveCount.set(0);
                     ServerConstants.currentSaveCount.set(0);
                     ServerConstants.workingSave = false;
                  }
               }
            }, 0L);
         }

         this.setLoadedThread(0);
         if (ServerConstants.timeScheduleEntry.isChange()) {
            ServerConstants.timeScheduleEntry.save();
            ServerConstants.timeScheduleEntry.setChange(false);
         }

         if (!DBConfig.isGanglim) {
            PraiseDonationMesoRank.saveRank();
            PraiseDonationMeso.saveLogs();
         }

         LoggingManager.insert();
         JOptionPane.showMessageDialog(this, "服务器保存线程将运行。", "Jihyeon 1.2.331", 1);
         this.setCount(0);
         this.setNotSaveCount(0);
      }
   }

   private void jButton1ActionPerformed(ActionEvent evt) {
      for (MapleCharacter chr : GameServer.getInstance(1).getPlayerStorage().getAllCharacters()) {
         if (chr != null && chr.getMapId() != ServerConstants.TownMap) {
            Field map = chr.getClient().getChannelServer().getMapFactory().getMap(ServerConstants.TownMap);
            if (map != null) {
               chr.changeMap(map, map.getPortal("sp"));
               chr.dropMessage(6, "被管理员移动到广场。");
            }
         }
      }

      JOptionPane.showMessageDialog(this, "已将1频道中的所有玩家移动到广场。", "Jihyeon 1.2.331", 1);
   }

   private void realCashActionPerformed(ActionEvent evt) {
   }

   private void jButton4ActionPerformed(ActionEvent evt) {
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("要发放的角色名称")) {
         boolean found = false;

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               if (chr.getName().equals(this.Receiver.getText())) {
                  try {
                     int rc = Integer.parseInt(this.realCash.getText());
                     chr.gainRealCash(rc);
                     String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
                     chr.dropMessage(
                        5, Integer.parseInt(this.realCash.getText()) + " " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 지급 받았습니다."
                     );
                  } catch (NumberFormatException var17) {
                     JOptionPane.showMessageDialog(this, "请输入数字形式的点数！", "Jihyeon 1.2.331", 0);
                     return;
                  }

                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            for (MapleCharacter chrx : CashShopServer.getPlayerStorage().getAllCharacters()) {
               if (chrx.getName().equals(this.Receiver.getText())) {
                  try {
                     int rc = Integer.parseInt(this.realCash.getText());
                     chrx.gainRealCash(rc);
                     String cashName = DBConfig.isGanglim ? "赞助现金" : "强林点数";
                     chrx.dropMessage(
                        5, Integer.parseInt(this.realCash.getText()) + " " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 已发放。"
                     );
                  } catch (NumberFormatException var16) {
                     JOptionPane.showMessageDialog(this, "请输入数字格式的点数！", "Jihyeon 1.2.331", 0);
                     return;
                  }

                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            for (MapleCharacter chrxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
               if (chrxx.getName().equals(this.Receiver.getText())) {
                  try {
                     int rc = Integer.parseInt(this.realCash.getText());
                     chrxx.gainRealCash(rc);
                     String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
                     chrxx.dropMessage(
                        5, Integer.parseInt(this.realCash.getText()) + " " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 已发放。"
                     );
                  } catch (NumberFormatException var15) {
                     JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                     return;
                  }

                  found = true;
                  break;
               }
            }
         }

         boolean f = false;
         if (!found) {
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
               ps.setString(1, this.Receiver.getText());
               ResultSet rs = ps.executeQuery();

               while (rs.next()) {
                  boolean ff = false;

                  for (GameServer cs : GameServer.getAllInstances()) {
                     for (MapleCharacter chrxxx : cs.getPlayerStorage().getAllCharacters()) {
                        if (chrxxx.getAccountID() == rs.getInt("accountid")) {
                           try {
                              int rc = Integer.parseInt(this.realCash.getText());
                              chrxxx.gainRealCash(rc);
                              String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
                              chrxxx.dropMessage(
                                 5, Integer.parseInt(this.realCash.getText()) + " " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 지급 받았습니다."
                              );
                           } catch (NumberFormatException var20) {
                              JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                              return;
                           }

                           ff = true;
                           f = true;
                           break;
                        }
                     }
                  }

                  if (!ff) {
                     for (MapleCharacter chrxxxx : CashShopServer.getPlayerStorage().getAllCharacters()) {
                        if (chrxxxx.getAccountID() == rs.getInt("accountid")) {
                           try {
                              int rc = Integer.parseInt(this.realCash.getText());
                              chrxxxx.gainRealCash(rc);
                              String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
                              chrxxxx.dropMessage(
                                 5, Integer.parseInt(this.realCash.getText()) + " " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 지급 받았습니다."
                              );
                           } catch (NumberFormatException var19) {
                              JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                              return;
                           }

                           ff = true;
                           f = true;
                           break;
                        }
                     }
                  }

                  if (!ff) {
                     for (MapleCharacter chrxxxxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                        if (chrxxxxx.getAccountID() == rs.getInt("accountid")) {
                           try {
                              int rc = Integer.parseInt(this.realCash.getText());
                              chrxxxxx.gainRealCash(rc);
                              String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
                              chrxxxxx.dropMessage(
                                 5, Integer.parseInt(this.realCash.getText()) + " " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 지급 받았습니다."
                              );
                           } catch (NumberFormatException var18) {
                              JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                              return;
                           }

                           ff = true;
                           f = true;
                           break;
                        }
                     }
                  }

                  if (!ff) {
                     PreparedStatement ps2 = con.prepareStatement("SELECT `realCash` FROM accounts WHERE `id` = ?");
                     ps2.setInt(1, rs.getInt("accountid"));

                     ResultSet rs2;
                     for (rs2 = ps2.executeQuery(); rs2.next(); f = true) {
                        int rc = rs2.getInt("realCash");
                        int point = Integer.parseInt(this.realCash.getText());
                        PreparedStatement ps3 = con.prepareStatement("UPDATE accounts SET realCash = ? WHERE `id` = ?");
                        ps3.setInt(1, rc + point);
                        ps3.setInt(2, rs.getInt("accountid"));
                        ps3.executeUpdate();
                        ps3.close();
                     }

                     rs2.close();
                     ps2.close();
                  }
               }

               rs.close();
               ps.close();
            } catch (SQLException var22) {
            }
         }

         StringBuilder sb = new StringBuilder();
         sb.append("赞助点数发放 : ");
         sb.append(" (");
         sb.append(this.Receiver.getText());
         sb.append(") ");
         sb.append(this.realCash.getText() + " 点数");
         sb.append("\r\n");
         FileoutputUtil.log("./TextLog/DonatorPointLog.txt", sb.toString());
         if (!found) {
            if (f) {
               JOptionPane.showMessageDialog(this, "离线发放已完成。", "Jihyeon 1.2.331", 1);
            } else {
               JOptionPane.showMessageDialog(this, "离线发放过程中发生错误。", "Jihyeon 1.2.331", 1);
            }
         } else {
            JOptionPane.showMessageDialog(this, "发放已完成。", "Jihyeon 1.2.331", 1);
         }
      } else {
         String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
         JOptionPane.showMessageDialog(this, cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 请输入要发放的角色！", "Jihyeon 1.2.351", 2);
      }
   }

   private void itemCodeActionPerformed(ActionEvent evt) {
   }

   private void mesoActionPerformed(ActionEvent evt) {
   }

   private void disconnectByAdmin(List<String> charnameList) {
      boolean found = false;
      String target = "";

      for (String charname : charnameList) {
         if (charname != null) {
            for (GameServer cs : GameServer.getAllInstances()) {
               for (Field map : cs.getMapFactory().getAllMaps()) {
                  for (MapleCharacter chr : new ArrayList<>(map.getCharacters())) {
                     if (chr != null && chr.getName().equals(charname)) {
                        chr.getClient().disconnect(false);
                        chr.getClient().getSession().close();
                        System.out.println("已断开连接");
                        found = true;
                        target = charname;
                     }
                  }
               }
            }

            for (MapleCharacter p : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
               if (p.getName().equals(charname)) {
                  p.getClient().setPlayer(p);
                  p.getClient().disconnect(false);
                  p.getClient().getSession().close();
                  System.out.println("已断开连接");
                  found = true;
                  target = charname;
               }
            }

            for (MapleCharacter px : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
               if (px.getName().equals(charname)) {
                  px.getClient().setPlayer(px);
                  px.getClient().disconnect(false);
                  px.getClient().getSession().close();
                  System.out.println("팅겼다고인마");
                  found = true;
                  target = charname;
               }
            }
         }
      }

      if (found) {
         JOptionPane.showMessageDialog(this, target + " 已断开连接", "Jihyeon 1.2.331", 1);
      } else {
         JOptionPane.showMessageDialog(this, "找不到该用户！", "Jihyeon 1.2.331", 0);
      }
   }

   private void jButton8ActionPerformed(ActionEvent evt) {
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         String username = this.Receiver.getText();
         List<String> charnameList = new ArrayList<>();
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id from accounts where name = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            int accid = 0;
            if (rs.next()) {
               accid = rs.getInt(1);
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT name from characters where accountid = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();

            while (rs.next()) {
               charnameList.add(rs.getString(1));
            }

            rs.close();
            ps.close();
         } catch (SQLException var11) {
            var11.printStackTrace();
         }

         this.disconnectByAdmin(charnameList);
      } else {
         JOptionPane.showMessageDialog(this, "请输入要断开连接的角色！", "Jihyeon 1.2.331", 2);
      }
   }

   private void hottimeRecvCountActionPerformed(ActionEvent evt) {
   }

   private void jButton3ActionPerformed(ActionEvent evt) {
      int flag = JOptionPane.showConfirmDialog(this, "是否进行服务器保存和重启？", "Jihyeon 1.2.351", 0);
      if (flag == 0) {
         if (ServerConstants.workingSave) {
            JOptionPane.showMessageDialog(this, "服务器保存正在进行中。请在保存完成后重试。", "Jihyeon 1.2.351", 1);
            return;
         }

         Center.unregisterAutoSave();
         JOptionPane.showMessageDialog(this, "服务器保存和重启将开始进行。", "Jihyeon 1.2.351", 1);
         ServerConstants.workingSave = true;
         ServerConstants.blockedEnterAuction = true;
         ServerConstants.workingReboot = true;

         for (MapleCharacter p : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
            if (p != null) {
               p.getClient().getSession().close();
               System.out.println("팅겼다고인마");
            }
         }

         for (MapleCharacter px : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
            if (px != null) {
               px.getClient().getSession().close();
               System.out.println("팅겼다고인마");
            }
         }

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               chr.getClient().getSession().close();
               System.out.println("팅겼다고인마");
            }
         }

         long startSaveTime = System.currentTimeMillis();

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               if (chr != null && chr.getClient().getSession().isOpen()) {
                  chr.saveToDB(false, false);
               }
            }
         }

         for (MapleCharacter chrx : AuctionServer.getPlayerStorage().getAllCharacters()) {
            if (chrx != null) {
               chrx.getClient().disconnect(true);
            }
         }

         try (Connection con = DBConnection.getConnection()) {
            Map<Integer, Long> cloneMap = new HashMap<>();
            boolean clone = false;

            while (!clone) {
               try {
                  cloneMap.putAll(Center.ServerSave.characterMesoMap);
                  clone = true;
               } catch (Exception var19) {
                  try {
                     Thread.sleep(100L);
                  } catch (Exception var18) {
                  }

                  clone = false;
               }
            }

            for (Entry<Integer, Long> entry : cloneMap.entrySet()) {
               int charid = entry.getKey();
               long meso = entry.getValue();

               try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET `meso` = ? WHERE `id` = ?")) {
                  ps.setLong(1, meso);
                  ps.setInt(2, charid);
                  ps.executeUpdate();
               } catch (Exception var21) {
                  System.out.println("角色金币失败 " + charid);
                  var21.printStackTrace();
               }
            }
         } catch (Exception var23) {
            System.out.println("角色金币保存失败");
            var23.printStackTrace();
         }

         System.out.println("金币保存完成");
         System.out.println("角色保存完成");
         WeeklyItemManager.saveWeeklyItems();
         System.out.println("周间物品保存完成");
         Center.Guild.save();
         System.out.println("길드 저장 완료");
         Center.Alliance.save();
         System.out.println("연합 저장 완료");
         DojangRanking.saveRanks();
         System.out.println("무릉도장랭킹 저장완료");
         if (!DBConfig.isGanglim) {
            PraisePointRank.loadRanks();
            System.out.println("칭찬포인트저장완료");
         }

         System.out.println("옥션 저장이 시작됩니다.");
         long startTime = System.currentTimeMillis();
         Center.Auction.save();
         long endTime = System.currentTimeMillis();
         System.out.println("옥션 저장 완료 (완료 시간 : " + (endTime - startTime) + " m/s)");
         if (ServerConstants.timeScheduleEntry.isChange()) {
            ServerConstants.timeScheduleEntry.save();
            ServerConstants.timeScheduleEntry.setChange(false);
         }

         long endSaveTime = System.currentTimeMillis();
         System.out.println("모든 저장이 완료되었습니다. (완료 시간 : " + (endSaveTime - startSaveTime) + " m/s)");
         System.out.println("이제 서버를 종료해도 좋습니다.");
      }
   }

   private void connectListMouseClicked(MouseEvent evt) {
      try {
         if (connectList.getModel() != null) {
            String name = connectList.getModel().getElementAt(connectList.getSelectedIndex()).toString();
            this.Receiver.setText(name);
         }
      } catch (Exception var3) {
      }
   }

   private void FreezeChatActionPerformed(ActionEvent evt) {
      if (freezeChat) {
         freezeChat = false;
         this.FreezeChat.setText("채팅창 얼리기");
         JOptionPane.showMessageDialog(this, "채팅창을 녹였습니다.", "Jihyeon 1.2.331", 1);
      } else {
         freezeChat = true;
         this.FreezeChat.setText("채팅창 녹이기");
         JOptionPane.showMessageDialog(this, "채팅창을 얼렸습니다.", "Jihyeon 1.2.331", 1);
      }
   }

   private void ReceiverActionPerformed(ActionEvent evt) {
   }

   private void realCashMouseReleased(MouseEvent evt) {
      if (this.realCash.getText().equals("要发放的点数")) {
         this.realCash.setText("");
      }
   }

   private void hongboPointMouseReleased(MouseEvent evt) {
      if (this.hongboPoint.getText().equals("要发放的点数")) {
         this.hongboPoint.setText("");
      }
   }

   private void itemCodeMousePressed(MouseEvent evt) {
      if (this.itemCode.getText().equals("아이템 코드,갯수")) {
         this.itemCode.setText("");
      }
   }

   private void mesoMouseReleased(MouseEvent evt) {
      if (this.meso.getText().equals("要发放的金币")) {
         this.meso.setText("");
      }
   }

   private void hottimeItemIDMouseReleased(MouseEvent evt) {
      if (this.hottimeItemID.getText().equals("아이템 코드,갯수")) {
         this.hottimeItemID.setText("");
      }
   }

   private void hottimeRecvCountMouseReleased(MouseEvent evt) {
      if (this.hottimeRecvCount.getText().equals("지급 받을 회원수")) {
         this.hottimeRecvCount.setText("");
      }
   }

   private void ReceiverMouseReleased(MouseEvent evt) {
      if (this.Receiver.getText().equals("要发放的角色名称")) {
         this.Receiver.setText("");
      }
   }

   private void jButton7ActionPerformed(ActionEvent evt) {
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         boolean found = false;

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               if (chr.getName().equals(this.Receiver.getText())) {
                  try {
                     chr.gainMeso(Integer.parseInt(this.meso.getText()), true);
                     chr.dropMessage(5, Integer.parseInt(this.meso.getText()) + "메소를 지급 받았습니다.");
                  } catch (NumberFormatException var8) {
                     JOptionPane.showMessageDialog(this, "메소를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                     return;
                  }

                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            JOptionPane.showMessageDialog(this, this.Receiver.getText() + "(을)를 찾을 수 없습니다!", "Jihyeon 1.2.331", 0);
         } else {
            JOptionPane.showMessageDialog(this, "지급이 완료되었습니다.", "Jihyeon 1.2.331", 1);
         }
      } else {
         JOptionPane.showMessageDialog(this, "请输入要发放金币的角色！", "Jihyeon 1.2.331", 2);
      }
   }

   private void jButton5ActionPerformed(ActionEvent evt) {
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         boolean found = false;

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               if (chr.getName().equals(this.Receiver.getText())) {
                  try {
                     int rc = Integer.parseInt(this.hongboPoint.getText());
                     chr.gainHongboPoint(rc);
                     chr.dropMessage(5, Integer.parseInt(this.hongboPoint.getText()) + " 홍보 포인트를 지급 받았습니다.");
                  } catch (NumberFormatException var17) {
                     JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                     return;
                  }

                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            for (MapleCharacter chrx : CashShopServer.getPlayerStorage().getAllCharacters()) {
               if (chrx.getName().equals(this.Receiver.getText())) {
                  try {
                     int rc = Integer.parseInt(this.hongboPoint.getText());
                     chrx.gainHongboPoint(rc);
                     chrx.dropMessage(5, Integer.parseInt(this.hongboPoint.getText()) + " 홍보 포인트를 지급 받았습니다.");
                  } catch (NumberFormatException var16) {
                     JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                     return;
                  }

                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            for (MapleCharacter chrxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
               if (chrxx.getName().equals(this.Receiver.getText())) {
                  try {
                     int rc = Integer.parseInt(this.hongboPoint.getText());
                     chrxx.gainHongboPoint(rc);
                     chrxx.dropMessage(5, Integer.parseInt(this.hongboPoint.getText()) + " 홍보 포인트를 지급 받았습니다.");
                  } catch (NumberFormatException var15) {
                     JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                     return;
                  }

                  found = true;
                  break;
               }
            }
         }

         boolean f = false;
         if (!found) {
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
               ps.setString(1, this.Receiver.getText());
               ResultSet rs = ps.executeQuery();

               while (rs.next()) {
                  boolean ff = false;

                  for (GameServer cs : GameServer.getAllInstances()) {
                     for (MapleCharacter chrxxx : cs.getPlayerStorage().getAllCharacters()) {
                        if (chrxxx.getAccountID() == rs.getInt("accountid")) {
                           try {
                              int rc = Integer.parseInt(this.hongboPoint.getText());
                              chrxxx.gainHongboPoint(rc);
                              String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
                              chrxxx.dropMessage(5, Integer.parseInt(this.hongboPoint.getText()) + " 홍보 포인트를 지급 받았습니다.");
                           } catch (NumberFormatException var20) {
                              JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                              return;
                           }

                           ff = true;
                           f = true;
                           break;
                        }
                     }
                  }

                  if (!ff) {
                     for (MapleCharacter chrxxxx : CashShopServer.getPlayerStorage().getAllCharacters()) {
                        if (chrxxxx.getAccountID() == rs.getInt("accountid")) {
                           try {
                              int rc = Integer.parseInt(this.hongboPoint.getText());
                              chrxxxx.gainHongboPoint(rc);
                              chrxxxx.dropMessage(5, Integer.parseInt(this.hongboPoint.getText()) + " 홍보 포인트를 지급 받았습니다.");
                           } catch (NumberFormatException var19) {
                              JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                              return;
                           }

                           ff = true;
                           f = true;
                           break;
                        }
                     }
                  }

                  if (!ff) {
                     for (MapleCharacter chrxxxxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                        if (chrxxxxx.getAccountID() == rs.getInt("accountid")) {
                           try {
                              int rc = Integer.parseInt(this.hongboPoint.getText());
                              chrxxxxx.gainHongboPoint(rc);
                              chrxxxxx.dropMessage(5, Integer.parseInt(this.hongboPoint.getText()) + " 홍보 포인트를 지급 받았습니다.");
                           } catch (NumberFormatException var18) {
                              JOptionPane.showMessageDialog(this, "포인트를 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
                              return;
                           }

                           ff = true;
                           f = true;
                           break;
                        }
                     }
                  }

                  if (!ff) {
                     PreparedStatement ps2 = con.prepareStatement("SELECT `hongbo_point` FROM accounts WHERE `id` = ?");
                     ps2.setInt(1, rs.getInt("accountid"));
                     ResultSet rs2 = ps2.executeQuery();

                     while (rs2.next()) {
                        int rc = rs2.getInt("hongbo_point");
                        int point = Integer.parseInt(this.hongboPoint.getText());
                        PreparedStatement ps3 = con.prepareStatement("UPDATE accounts SET hongbo_point = ? WHERE `id` = ?");
                        ps3.setInt(1, rc + point);
                        ps3.setInt(2, rs.getInt("accountid"));
                        ps3.executeUpdate();
                        ps3.close();
                        f = true;
                     }

                     rs2.close();
                     ps2.close();
                  }
               }

               rs.close();
               ps.close();
            } catch (SQLException var22) {
            }
         }

         StringBuilder sb = new StringBuilder();
         sb.append("홍보 포인트 지급 : ");
         sb.append(" (");
         sb.append(this.Receiver.getText());
         sb.append(") ");
         sb.append(this.hongboPoint.getText() + " 포인트");
         sb.append("\r\n");
         FileoutputUtil.log("./TextLog/PromotionPointLog.txt", sb.toString());
         JOptionPane.showMessageDialog(this, "지급이 완료되었습니다.", "Jihyeon 1.2.331", 1);
      } else {
         JOptionPane.showMessageDialog(this, "请输入要发放推广点数的角色！", "Jihyeon 1.2.331", 2);
      }
   }

   private void hongboPointActionPerformed(ActionEvent evt) {
   }

   private void jButton6ActionPerformed(ActionEvent evt) {
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         String[] parse = this.itemCode.getText().trim().split(",");
         int itemID = Integer.parseInt(parse[0]);
         int quantity = Integer.parseInt(parse[1]);
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         Item item;
         if (GameConstants.getInventoryType(itemID) == MapleInventoryType.EQUIP) {
            item = ii.randomizeStats((Equip)ii.getEquipById(itemID));
         } else {
            item = new Item(itemID, (short)0, (short)quantity, 0);
         }

         item.setGMLog(CurrentTime.getAllCurrentTime() + "에 서버 관리기 아이템 지급을 통해 만들어진 아이템");
         boolean find = false;
         if (!ii.itemExists(itemID)) {
            JOptionPane.showMessageDialog(this, "존재하지 않는 아이템입니다.", "Jihyeon 1.2.354", 0);
         } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
            Calendar CAL = new GregorianCalendar(Locale.KOREA);
            String fDate = sdf.format(CAL.getTime());
            MapleCharacter p = null;

            for (GameServer gs : GameServer.getAllInstances()) {
               for (Field map : gs.getMapFactory().getAllMaps()) {
                  for (MapleCharacter ch : new ArrayList<>(map.getCharacters())) {
                     if (ch != null && ch.getName().equals(this.Receiver.getText())) {
                        p = ch;
                        break;
                     }
                  }
               }
            }

            if (p == null) {
               PreparedStatement ps = null;
               PreparedStatement pse = null;
               ResultSet rs = null;

               try (Connection con = DBConnection.getConnection()) {
                  int accountID = 0;
                  int lastIndex = 0;
                  ps = con.prepareStatement("SELECT `accountid` FROM `characters` WHERE `name` = ?");
                  ps.setString(1, this.Receiver.getText());
                  rs = ps.executeQuery();

                  while (rs.next()) {
                     accountID = rs.getInt("accountid");
                  }

                  rs.close();
                  ps.close();
                  if (accountID <= 0) {
                     JOptionPane.showMessageDialog(this, "존재하지 않는 유저입니다.", "Jihyeon 1.2.354", 1);
                  } else {
                     for (GameServer gs : GameServer.getAllInstances()) {
                        for (Field map : gs.getMapFactory().getAllMaps()) {
                           for (MapleCharacter chx : new ArrayList<>(map.getCharacters())) {
                              if (chx != null && chx.getAccountID() == accountID) {
                                 p = chx;
                                 break;
                              }
                           }
                        }
                     }

                     if (p != null) {
                        MapleCabinet cabinet = p.getCabinet();
                        if (cabinet == null) {
                           JOptionPane.showMessageDialog(this, "해당 유저의 보관함을 열람하는 과정에서 오류가 발생하였습니다.", "Jihyeon 1.2.354", 1);
                           return;
                        }

                        cabinet.addCabinetItem(
                           new MapleCabinetItem(cabinet.getNextIndex(), System.currentTimeMillis() + 604800000L, "[GM 선물]", fDate + "에 운영자가 보낸 아이템입니다.", item)
                        );
                        p.send(CField.maplecabinetResult(8));
                        p.setSaveFlag(p.getSaveFlag() | CharacterSaveFlag.CABINET.getFlag());
                        p.dropMessage(5, "[알림] 운영자로부터 아이템 도착하였습니다. 메이플 보관함을 통해 수령해주시기 바랍니다.");
                        find = true;
                        JOptionPane.showMessageDialog(this, "해당 플레이어와 동일한 계정 내에 캐릭터에게 전송되었습니다.", "Jihyeon 1.2.354", 1);
                     } else {
                        ps = con.prepareStatement("SELECT `cabinet_index` FROM `cabinet_items` WHERE `accountid` = ? ORDER BY `cabinet_index` DESC");
                        ps.setInt(1, accountID);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                           lastIndex = rs.getInt("cabinet_index");
                        }

                        ps = con.prepareStatement(
                           "INSERT INTO `cabinet_items` (accountid, itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender, once_trade, cabinet_index, cabinet_expired_time, cabinet_title, cabinet_desc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                           1
                        );
                        pse = con.prepareStatement(
                           "INSERT INTO `cabinet_equipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        );
                        MapleInventoryType mit = MapleInventoryType.getByType((byte)(item.getItemId() / 1000000));
                        if (item.getPosition() == -55) {
                           return;
                        }

                        if (mit == MapleInventoryType.EQUIP && ii.isCash(item.getItemId())) {
                           mit = MapleInventoryType.CASH_EQUIP;
                        }

                        AtomicInteger idx = new AtomicInteger(1);
                        ps.setInt(idx.getAndIncrement(), accountID);
                        ps.setInt(idx.getAndIncrement(), item.getItemId());
                        ps.setInt(idx.getAndIncrement(), mit.getType());
                        ps.setInt(idx.getAndIncrement(), item.getPosition());
                        ps.setInt(idx.getAndIncrement(), item.getQuantity());
                        ps.setString(idx.getAndIncrement(), item.getOwner());
                        ps.setString(idx.getAndIncrement(), item.getGMLog());
                        if (item.getPet() != null) {
                           ps.setLong(idx.getAndIncrement(), Math.max(item.getUniqueId(), item.getPet().getUniqueId()));
                        } else {
                           ps.setLong(idx.getAndIncrement(), item.getUniqueId());
                        }

                        ps.setLong(idx.getAndIncrement(), item.getExpiration());
                        ps.setInt(idx.getAndIncrement(), item.getFlag());
                        ps.setByte(idx.getAndIncrement(), (byte)8);
                        ps.setString(idx.getAndIncrement(), item.getGiftFrom());
                        ps.setInt(idx.getAndIncrement(), item.getOnceTrade());
                        ps.setInt(idx.getAndIncrement(), lastIndex + 1);
                        ps.setLong(idx.getAndIncrement(), System.currentTimeMillis() + 604800000L);
                        ps.setString(idx.getAndIncrement(), "[GM 선물]");
                        ps.setString(idx.getAndIncrement(), fDate + "에 운영자가 보낸 아이템입니다.");
                        ps.executeUpdate();
                        rs = ps.getGeneratedKeys();
                        if (!rs.next()) {
                           rs.close();
                           return;
                        }

                        long iid = rs.getLong(1);
                        rs.close();
                        item.setInventoryId(iid);
                        if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED) || mit.equals(MapleInventoryType.CASH_EQUIP)) {
                           Equip equip = (Equip)item;
                           if (equip.getUniqueId() > 0L && equip.getItemId() / 10000 == 166) {
                              Android android = equip.getAndroid();
                              if (android != null) {
                                 android.saveToDb();
                              }
                           }

                           int index = 1;
                           pse.setLong(index++, iid);
                           pse.setInt(index++, 0);
                           pse.setInt(index++, accountID);
                           pse.setInt(index++, equip.getItemId());
                           pse.setInt(index++, Math.max(0, equip.getUpgradeSlots()));
                           pse.setInt(index++, equip.getLevel());
                           pse.setInt(index++, equip.getStr());
                           pse.setInt(index++, equip.getDex());
                           pse.setInt(index++, equip.getInt());
                           pse.setInt(index++, equip.getLuk());
                           pse.setInt(index++, equip.getArc());
                           pse.setInt(index++, equip.getArcEXP());
                           pse.setInt(index++, equip.getArcLevel());
                           pse.setInt(index++, equip.getHp());
                           pse.setInt(index++, equip.getMp());
                           pse.setInt(index++, equip.getHpR());
                           pse.setInt(index++, equip.getMpR());
                           pse.setInt(index++, equip.getWatk());
                           pse.setInt(index++, equip.getMatk());
                           pse.setInt(index++, equip.getWdef());
                           pse.setInt(index++, equip.getMdef());
                           pse.setInt(index++, equip.getAcc());
                           pse.setInt(index++, equip.getAvoid());
                           pse.setInt(index++, equip.getHands());
                           pse.setInt(index++, equip.getSpeed());
                           pse.setInt(index++, equip.getJump());
                           pse.setInt(index++, equip.getViciousHammer());
                           pse.setInt(index++, equip.getItemEXP());
                           pse.setInt(index++, equip.getDurability());
                           pse.setByte(index++, equip.getEnhance());
                           pse.setByte(index++, equip.getState());
                           pse.setByte(index++, equip.getLines());
                           pse.setInt(index++, equip.getPotential1());
                           pse.setInt(index++, equip.getPotential2());
                           pse.setInt(index++, equip.getPotential3());
                           pse.setInt(index++, equip.getPotential4());
                           pse.setInt(index++, equip.getPotential5());
                           pse.setInt(index++, equip.getPotential6());
                           pse.setInt(index++, equip.getFusionAnvil());
                           pse.setInt(index++, equip.getIncSkill());
                           pse.setShort(index++, equip.getCharmEXP());
                           pse.setShort(index++, equip.getPVPDamage());
                           pse.setShort(index++, equip.getSpecialAttribute());
                           pse.setByte(index++, equip.getReqLevel());
                           pse.setByte(index++, equip.getGrowthEnchant());
                           pse.setByte(index++, (byte)(equip.getFinalStrike() ? 1 : 0));
                           pse.setShort(index++, equip.getBossDamage());
                           pse.setShort(index++, equip.getIgnorePDR());
                           pse.setByte(index++, equip.getTotalDamage());
                           pse.setByte(index++, equip.getAllStat());
                           pse.setByte(index++, equip.getKarmaCount());
                           pse.setShort(index++, equip.getSoulName());
                           pse.setShort(index++, equip.getSoulEnchanter());
                           pse.setShort(index++, equip.getSoulPotential());
                           pse.setInt(index++, equip.getSoulSkill());
                           pse.setLong(index++, equip.getFire());
                           pse.setByte(index++, equip.getStarForce());
                           pse.setInt(index++, 0);
                           pse.setInt(index++, equip.getDownLevel());
                           pse.setInt(index++, equip.getSpecialPotential());
                           pse.setInt(index++, equip.getSPGrade());
                           pse.setInt(index++, equip.getSPAttack());
                           pse.setInt(index++, equip.getSPAllStat());
                           pse.setInt(index++, equip.getItemState());
                           pse.setInt(index++, equip.getCsGrade());
                           pse.setInt(index++, equip.getCsOption1());
                           pse.setInt(index++, equip.getCsOption2());
                           pse.setInt(index++, equip.getCsOption3());
                           pse.setLong(index++, equip.getCsOptionExpireDate());
                           pse.setLong(index++, equip.getExGradeOption());
                           pse.setInt(index++, equip.getCHUC());
                           pse.setInt(index++, equip.getClearCheck());
                           pse.setInt(index++, equip.isSpecialRoyal() ? 1 : 0);
                           pse.setLong(index++, equip.getSerialNumberEquip());
                           pse.setInt(index++, equip.getCashEnchantCount());
                           pse.executeUpdate();
                        }
                     }

                     find = true;
                     JOptionPane.showMessageDialog(this, "오프라인 지급되었습니다.", "Jihyeon 1.2.354", 1);
                  }
               } catch (SQLException var39) {
                  new RuntimeException(var39);
               } finally {
                  try {
                     if (ps != null) {
                        ps.close();
                        PreparedStatement var43 = null;
                     }

                     if (pse != null) {
                        pse.close();
                        PreparedStatement var45 = null;
                     }

                     if (rs != null) {
                        rs.close();
                        ResultSet var47 = null;
                     }
                  } catch (SQLException var36) {
                  }
               }
            } else {
               MapleCabinet cabinet = p.getCabinet();
               if (cabinet == null) {
                  JOptionPane.showMessageDialog(this, "해당 유저의 보관함을 열람하는 과정에서 오류가 발생하였습니다.", "Jihyeon 1.2.354", 0);
                  return;
               }

               cabinet.addCabinetItem(
                  new MapleCabinetItem(cabinet.getNextIndex(), System.currentTimeMillis() + 604800000L, "[GM 선물]", fDate + "에 운영자가 보낸 아이템입니다.", item)
               );
               p.send(CField.maplecabinetResult(8));
               p.setSaveFlag(p.getSaveFlag() | CharacterSaveFlag.CABINET.getFlag());
               p.dropMessage(5, "[알림] 운영자로부터 아이템이 도착하였습니다. 메이플 보관함을 통해 수령해주시기 바랍니다.");
               find = true;
               JOptionPane.showMessageDialog(this, "아이템을 지급하였습니다.", "Jihyeon 1.2.354", 1);
            }

            if (!find) {
               JOptionPane.showMessageDialog(this, this.Receiver.getText() + "(을)를 찾을 수 없습니다!", "Jihyeon 1.2.354", 0);
            }
         }
      } else {
         JOptionPane.showMessageDialog(this, "请输入要发放物品的角色！", "Jihyeon 1.2.331", 2);
      }
   }

   private void jButton9ActionPerformed(ActionEvent evt) {
      if (!this.hottimeItemID.getText().isEmpty()
         && !this.hottimeItemID.getText().equals("아이템 코드, 갯수")
         && !this.hottimeRecvCount.getText().isEmpty()
         && !this.hottimeRecvCount.getText().equals("지급 받을 회원수")) {
         List<MapleCharacter> player = new LinkedList<>();

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               player.add(chr);
            }
         }

         for (MapleCharacter p : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
            if (p != null) {
               player.add(p);
            }
         }

         for (MapleCharacter px : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
            if (px != null) {
               player.add(px);
            }
         }

         Collections.shuffle(player);
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int itemID = 0;
         int quantity = 1;
         int userCount = 1;
         boolean all = false;

         try {
            String[] args = this.hottimeItemID.getText().trim().split(",");
            itemID = Integer.parseInt(args[0]);
            quantity = Integer.parseInt(args[1]);
            if (!this.hottimeRecvCount.getText().equals("ALL")
               && !this.hottimeRecvCount.getText().equals("all")
               && !this.hottimeRecvCount.getText().equals("전체")
               && !this.hottimeRecvCount.getText().equals("모두")) {
               userCount = Math.min(Integer.parseInt(this.hottimeRecvCount.getText()), player.size());
            } else {
               userCount = player.size();
               all = true;
            }
         } catch (NumberFormatException var18) {
            JOptionPane.showMessageDialog(this, "지급될 아이템 ID나 갯수를 숫자로 정확하게 입력해주세요.", "Jihyeon 1.2.331", 0);
            return;
         }

         if (!ii.itemExists(itemID)) {
            JOptionPane.showMessageDialog(this, itemID + "번 아이템은 존재하지 않는 아이템입니다.", "Jihyeon 1.2.331", 0);
         } else {
            List<MapleCharacter> recipient = new LinkedList<>();
            int requestCount = 0;

            while (true) {
               int side = Randomizer.rand(0, player.size() - 1);
               MapleCharacter g = player.get(side);
               if (requestCount++ >= userCount * 10 || recipient.size() >= userCount) {
                  JOptionPane.showMessageDialog(this, "지급이 완료되었습니다.", "Jihyeon 1.2.331", 1);
                  return;
               }

               if (g != null) {
                  boolean alreadyRecv = false;

                  for (MapleCharacter ch : recipient) {
                     if (ch.getName().equals(g.getName())) {
                        alreadyRecv = true;
                     }
                  }

                  if (!alreadyRecv) {
                     MapleCabinet cabinet = g.getCabinet();
                     if (cabinet != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
                        Calendar CAL = new GregorianCalendar(Locale.KOREA);
                        String fDate = sdf.format(CAL.getTime());
                        cabinet.addCabinetItem(
                           new MapleCabinetItem(
                              cabinet.getNextIndex(),
                              System.currentTimeMillis() + 604800000L,
                              "[핫타임 보상]",
                              fDate + "에 지급된 핫타임 보상입니다.",
                              new Item(itemID, (short)0, (short)quantity, 0, MapleInventoryIdentifier.getInstance())
                           )
                        );
                        g.send(CField.maplecabinetResult(8));
                        g.setSaveFlag(g.getSaveFlag() | CharacterSaveFlag.CABINET.getFlag());
                        String message = "";
                        if (!all) {
                           message = message + "축하드립니다! ";
                        }

                        if (!all && userCount < 5) {
                           Center.Broadcast.broadcastMessage(
                              CField.chatMsg(21, "[" + g.getName() + "] 님이 핫타임 이벤트 보상으로 " + ii.getName(itemID) + " " + quantity + "개를 지급 받았습니다. 모두 축하해주세요!")
                           );
                        }

                        message = message + "[" + ii.getName(itemID) + "] " + quantity + "개를 핫타임 보상으로 지급 받았습니다.\r\n[메이플 보관함]을 확인해주세요.";
                        g.dropMessage(1, message);
                        g.dropMessage(5, message);
                        recipient.add(g);
                     }
                  }
               }
            }
         }
      } else {
         JOptionPane.showMessageDialog(this, "请准确输入要发放的热门时间物品代码或会员数！", "Jihyeon 1.2.331", 2);
      }
   }

   private void hottimeItemIDActionPerformed(ActionEvent evt) {
   }

   private void noticeMsgMouseReleased(MouseEvent evt) {
      if (this.noticeMsg.getText().equals("공지사항 입력")) {
         this.noticeMsg.setText("");
      }
   }

   private void jButton10ActionPerformed(ActionEvent evt) {
      if (!this.noticeMsg.getText().isEmpty() && !this.noticeMsg.getText().equals("공지사항 입력")) {
         Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(1, this.noticeMsg.getText()));
         JOptionPane.showMessageDialog(this, "공지사항을 송출하였습니다.", "Jihyeon 1.2.331", 1);
      } else {
         JOptionPane.showMessageDialog(this, "공지사항 메세지를 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
      }
   }

   private void jButton11ActionPerformed(ActionEvent evt) {
      if (!this.noticeMsg.getText().isEmpty() && !this.noticeMsg.getText().equals("공지사항 입력")) {
         for (GameServer cserv : GameServer.getAllInstances()) {
            cserv.setServerMessage(this.noticeMsg.getText());
         }

         JOptionPane.showMessageDialog(this, "공지사항을 송출하였습니다.", "Jihyeon 1.2.331", 1);
      } else {
         JOptionPane.showMessageDialog(this, "공지사항 메세지를 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
      }
   }

   private void jButton12ActionPerformed(ActionEvent evt) {
      if (!this.noticeMsg.getText().isEmpty() && !this.noticeMsg.getText().equals("공지사항 입력")) {
         Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, this.noticeMsg.getText()));
         JOptionPane.showMessageDialog(this, "공지사항을 송출하였습니다.", "Jihyeon 1.2.331", 1);
      } else {
         JOptionPane.showMessageDialog(this, "공지사항 메세지를 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
      }
   }

   private void jButton13ActionPerformed(ActionEvent evt) {
      if (!this.noticeMsg.getText().isEmpty() && !this.noticeMsg.getText().equals("공지사항 입력")) {
         Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage(this.noticeMsg.getText(), false, 1));
         JOptionPane.showMessageDialog(this, "공지사항을 송출하였습니다.", "Jihyeon 1.2.331", 1);
      } else {
         JOptionPane.showMessageDialog(this, "공지사항 메세지를 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
      }
   }

   private void jButton14ActionPerformed(ActionEvent evt) {
      if (!this.noticeMsg.getText().isEmpty() && !this.noticeMsg.getText().equals("공지사항 입력")) {
         Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, this.noticeMsg.getText()));
         Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(1, this.noticeMsg.getText()));
         Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(4, this.noticeMsg.getText()));
         Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage(this.noticeMsg.getText(), false, 1));
         Center.Broadcast.broadcastMessage(CWvsContext.getStaticScreenMessage(this.noticeMsg.getText(), false, 1));
         JOptionPane.showMessageDialog(this, "공지사항을 송출하였습니다.", "Jihyeon 1.2.331", 1);
      } else {
         JOptionPane.showMessageDialog(this, "공지사항 메세지를 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
      }
   }

   private void killPointMouseReleased(MouseEvent evt) {
      if (this.killPoint.getText().equals("要发放的点数")) {
         this.killPoint.setText("");
      }
   }

   private void jButton15ActionPerformed(ActionEvent evt) {
      if (!ServerConstants.useAdminClientUpgrade) {
         JOptionPane.showMessageDialog(this, "추가 의뢰 기능입니다. 문의 주시기 바랍니다.", "Jihyeon 1.2.331", 1);
      } else if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         boolean found = false;

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               if (chr.getName().equals(this.Receiver.getText())) {
                  String[] parse = this.killPoint.getText().split(",");
                  int itemID = Integer.parseInt(parse[0]);
                  int quantity = Integer.parseInt(parse[1]);
                  int TI = itemID / 1000000;
                  if (MapleItemInformationProvider.getInstance().isCash(itemID)) {
                     TI = 6;
                  }

                  for (int i = 0; i < quantity; i++) {
                     Item item = null;
                     if ((item = chr.getInventory(MapleInventoryType.getByType((byte)TI)).findById(itemID)) != null) {
                        MapleInventoryManipulator.removeFromSlot(
                           chr.getClient(), MapleInventoryType.getByType((byte)TI), item.getPosition(), (short)1, false, false
                        );
                        found = true;
                     }
                  }
                  break;
               }
            }
         }

         if (!found) {
            JOptionPane.showMessageDialog(this, this.Receiver.getText() + "(을)를 찾을 수 없습니다!", "Jihyeon 1.2.331", 0);
         } else {
            JOptionPane.showMessageDialog(this, "해당 갯수 만큼 회수가 완료되었습니다.", "Jihyeon 1.2.331", 1);
         }
      } else {
         JOptionPane.showMessageDialog(this, "请输入要发放狩猎点数的角色！", "Jihyeon 1.2.331", 2);
      }
   }

   private void jButton16ActionPerformed(ActionEvent evt) {
      if (!ServerConstants.useAdminClientUpgrade) {
         JOptionPane.showMessageDialog(this, "추가 의뢰 기능입니다. 문의 주시기 바랍니다.", "Jihyeon 1.2.331", 1);
      } else {
         double expRate_ = 1.0;
         double mesoRate_ = 1.0;
         double dropRate_ = 1.0;

         try {
            expRate_ = Double.parseDouble(expRate.getText());
            mesoRate_ = Double.parseDouble(mesoRate.getText());
            dropRate_ = Double.parseDouble(dropRate.getText());
         } catch (NumberFormatException var10) {
            JOptionPane.showMessageDialog(this, "배율을 숫자로 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 0);
            return;
         }

         for (GameServer cs : GameServer.getAllInstances()) {
            cs.setExpRate(expRate_);
            cs.setMesoRate(mesoRate_);
            cs.setDropRate(dropRate_);
         }

         JOptionPane.showMessageDialog(this, "배율 설정이 완료되었습니다.", "Jihyeon 1.2.331", 1);
      }
   }

   private void hottimeStartActionPerformed(ActionEvent evt) {
   }

   private void jCheckBox1ActionPerformed(ActionEvent evt) {
      if (!this.jCheckBox1.isSelected()) {
         ServerConstants.expHottimeStartTime = this.hottimeStart.getText();
         ServerConstants.expHottimeEndTime = this.hottimeEnd.getText();
         ServerConstants.expHottimeRate = Double.parseDouble(this.hottimeRate.getText());
         Center.registerAutoExpBuff();
      } else {
         Center.cancelExpHottimeTask(false);
         System.out.println("[핫타임] 경험치 핫타임 기능을 종료합니다.");
      }
   }

   private void jButton17ActionPerformed(ActionEvent evt) {
      try {
         ServerConstants.expHottimeStartTime = this.hottimeStart.getText();
         ServerConstants.expHottimeEndTime = this.hottimeEnd.getText();
         ServerConstants.expHottimeRate = Double.parseDouble(this.hottimeRate.getText());
         Center.registerAutoExpBuff();
      } catch (NumberFormatException var3) {
         JOptionPane.showMessageDialog(this, "핫타임 경험치 배율을 정확히 입력해주시기 바랍니다.", "Jihyeon 1.2.331", 0);
      }
   }

   private void jButton19ActionPerformed(ActionEvent evt) {
      String d = (String)this.jComboBox1.getSelectedItem();
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         if (d.equals("新手礼包")) {
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
               ps.setString(1, this.Receiver.getText());
               ResultSet rs = ps.executeQuery();

               while (rs.next()) {
                  PreparedStatement ps2 = con.prepareStatement("INSERT INTO `beginner_package` (`accountid`, `name`) VALUES (?, ?)");
                  ps2.setInt(1, rs.getInt("accountid"));
                  ps2.setString(2, null);
                  ps2.executeUpdate();
                  ps2.close();
               }

               rs.close();
               ps.close();
               StringBuilder sb = new StringBuilder();
               sb.append("초심자 패키지 지급 : ");
               sb.append(" (");
               sb.append(this.Receiver.getText());
               sb.append(") ");
               sb.append("\r\n");
               FileoutputUtil.log("./TextLog/DonatorLog.txt", sb.toString());
               JOptionPane.showMessageDialog(this, "초심자 패키지 지급이 완료되었습니다.", "Jihyeon 1.2.331", 1);
            } catch (SQLException var17) {
            }
         } else if (d.equals("주간패키지")) {
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps2 = con.prepareStatement("INSERT INTO `weekly_package` (`USER_ID`) VALUES (?)");
               ps2.setString(1, this.Receiver.getText());
               ps2.executeUpdate();
               ps2.close();
               StringBuilder sb = new StringBuilder();
               sb.append("주간 패키지 지급 : ");
               sb.append(" (");
               sb.append(this.Receiver.getText());
               sb.append(") ");
               sb.append("\r\n");
               FileoutputUtil.log("./TextLog/DonatorLog.txt", sb.toString());
               JOptionPane.showMessageDialog(this, "주간 패키지 지급이 완료되었습니다.", "Jihyeon 1.2.331", 1);
            } catch (SQLException var15) {
            }
         } else {
            String tier = d.split("\\(")[1].split("만")[0];

            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps_ = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
               ps_.setString(1, this.Receiver.getText());
               ResultSet rs_ = ps_.executeQuery();

               while (rs_.next()) {
                  int accountID = rs_.getInt("accountid");
                  PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM extreme_point_log WHERE `accountid` = ? && type='" + tier + "'");
                  ps.setInt(1, accountID);
                  ResultSet rs = ps.executeQuery();
                  if (rs.next()) {
                     JOptionPane.showMessageDialog(this, "이미 훈장을 지급 받은 유저입니다.", "Jihyeon 1.2.331", 1);
                     rs.close();
                     ps.close();
                     return;
                  }

                  rs.close();
                  ps.close();
                  ps = con.prepareStatement("INSERT INTO extreme_point_log (accountid, type) VALUES(?, ?)");
                  ps.setInt(1, accountID);
                  ps.setString(2, tier);
                  ps.executeUpdate();
                  ps.close();
                  StringBuilder sb = new StringBuilder();
                  sb.append("Tier." + tier + " 지급 : ");
                  sb.append(" (");
                  sb.append(this.Receiver.getText());
                  sb.append(") ");
                  sb.append("\r\n");
                  FileoutputUtil.log("./TextLog/DonatorLog.txt", sb.toString());
                  JOptionPane.showMessageDialog(this, tier + "만 누적 보상 지급이 완료되었습니다.", "Jihyeon 1.2.331", 1);
               }
            } catch (SQLException var19) {
            }
         }
      } else {
         JOptionPane.showMessageDialog(this, "请输入要发放新手礼包的角色！", "Jihyeon 1.2.331", 2);
      }
   }

   private void jCheckBox2ActionPerformed(ActionEvent evt) {
      setDisabledChat(!isDisabledChat());
   }

   private boolean additionalBanOption(String charname, String banReason, String days) {
      boolean showCommandHelp = false;
      if (banReason.equals("") && days.equals("")) {
         return showCommandHelp;
      } else {
         Timestamp TS = null;
         if (!days.equals("")) {
            Calendar duration = Calendar.getInstance();

            try {
               duration.add(5, Integer.parseInt(days));
            } catch (NumberFormatException var12) {
               return true;
            }

            TS = new Timestamp(duration.getTimeInMillis());
         }

         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            int accid = 0;
            if (rs.next()) {
               accid = rs.getInt(1);
            }

            rs.close();
            ps.close();
            if (TS != null) {
               ps = con.prepareStatement("UPDATE accounts SET banned = 0, tempban = ?, banreason = ? WHERE id = ?");
               ps.setTimestamp(1, TS);
               ps.setString(2, "밴 사유: " + banReason);
               ps.setInt(3, accid);
               ps.execute();
               ps.close();
            } else {
               ps = con.prepareStatement("UPDATE accounts SET banreason = ? WHERE id = ?");
               ps.setString(1, "밴 사유: " + banReason);
               ps.setInt(2, accid);
               ps.execute();
               ps.close();
            }
         } catch (SQLException var14) {
            var14.printStackTrace();
         }

         return showCommandHelp;
      }
   }

   private void jButton18ActionPerformed(ActionEvent evt) {
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         boolean found = false;
         boolean byAdminClient = true;
         boolean isTempban = false;
         boolean showCommandHelp = false;
         String charname = "";
         String banReason = "";
         String days = "";
         String[] enteredValue = this.Receiver.getText().split(", ");
         charname = enteredValue[0];
         if (enteredValue.length == 2) {
            banReason = enteredValue[1];
         } else if (enteredValue.length == 3) {
            banReason = enteredValue[1];
            days = enteredValue[2];
            isTempban = true;
         }

         banReason.trim();
         days.trim();

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               if (chr.getName().equals(charname)) {
                  if (!isTempban) {
                     chr.serialBan(byAdminClient);
                  }

                  showCommandHelp = this.additionalBanOption(charname, banReason, days);
                  if (!showCommandHelp) {
                     chr.getClient().disconnect(false);
                     chr.getClient().getSession().close();
                     System.out.println("팅겼다고인마");
                  }

                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
               ps.setString(1, charname);
               ResultSet rs = ps.executeQuery();

               while (rs.next()) {
                  PreparedStatement ps2 = con.prepareStatement("SELECT `name` FROM accounts WHERE `id` = ?");
                  ps2.setInt(1, rs.getInt("accountid"));

                  for (ResultSet rs2 = ps2.executeQuery(); rs2.next(); found = true) {
                     if (!isTempban) {
                        MapleCharacter.serialBan(rs2.getString("name"), byAdminClient);
                     }

                     showCommandHelp = this.additionalBanOption(charname, banReason, days);
                  }
               }
            } catch (SQLException var17) {
            }
         }

         if (!found) {
            JOptionPane.showMessageDialog(this, this.Receiver.getText() + "(을)를 찾을 수 없습니다!", "Jihyeon 1.2.331", 0);
         } else {
            if (isTempban && !showCommandHelp) {
               JOptionPane.showMessageDialog(this, "기간 밴 처리 되었습니다.", "Jihyeon 1.2.331", 1);
            } else if (!isTempban) {
               JOptionPane.showMessageDialog(this, "시리얼 밴 처리 되었습니다.", "Jihyeon 1.2.331", 1);
            } else if (showCommandHelp) {
               JOptionPane.showMessageDialog(this, "입력값을 확인해 주세요. \n*** 올바른 커맨드 사용법 ***\n-시리얼 밴: 캐릭이름, 사유\n-기간 밴: 캐릭이름, 사유, 일수(숫자)", "Jihyeon 1.2.331", 1);
            }
         }
      } else {
         JOptionPane.showMessageDialog(this, "시리얼 밴 할 캐릭터 이름을 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 2);
      }
   }

   private void jButton20ActionPerformed(ActionEvent evt) {
      if (!this.Receiver.getText().isEmpty() && !this.Receiver.getText().equals("지급할 캐릭터 이름")) {
         boolean found = false;

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
            ps.setString(1, this.Receiver.getText());
            String sessionIP = "";
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               PreparedStatement ps2 = con.prepareStatement("SELECT `name`, `SessionIP` FROM accounts WHERE `id` = ?");
               ps2.setInt(1, rs.getInt("accountid"));

               ResultSet rs2;
               for (rs2 = ps2.executeQuery(); rs2.next(); found = true) {
                  MapleCharacter.unSerialBan(rs2.getString("name"), true);
                  sessionIP = rs2.getString("SessionIP");
               }

               rs2.close();
               ps2.close();
               ps2 = con.prepareStatement("DELETE FROM ipbans WHERE ip = ?");
               ps2.setString(1, sessionIP);
               ps2.executeQuery();
               ps2.close();
            }

            rs.close();
            ps.close();
         } catch (SQLException var11) {
            var11.printStackTrace();
         }

         if (!found) {
            JOptionPane.showMessageDialog(this, this.Receiver.getText() + "(을)를 찾을 수 없습니다!", "Jihyeon 1.2.331", 0);
         } else {
            JOptionPane.showMessageDialog(this, "모든 밴이 해제 처리 되었습니다.", "Jihyeon 1.2.331", 1);
         }
      } else {
         JOptionPane.showMessageDialog(this, "시리얼 밴 해제 할 캐릭터 이름을 입력해주시기 바랍니다!", "Jihyeon 1.2.331", 2);
      }
   }

   private void jButton21ActionPerformed(ActionEvent evt) {
      ServerConstants.blockedEnterAuction = !ServerConstants.blockedEnterAuction;
      if (ServerConstants.blockedEnterAuction) {
         for (MapleCharacter player : AuctionServer.getPlayerStorage().getAllCharacters()) {
            player.getClient().getSession().close();
            System.out.println("팅겼다고인마");
            player.getClient().disconnect(false);
         }

         this.jButton21.setText("경매장 녹이기");
         JOptionPane.showMessageDialog(this, "경매장 입장이 제한되었습니다.", "Jihyeon 1.2.331", 1);
      } else {
         this.jButton21.setText("경매장 얼리기");
         JOptionPane.showMessageDialog(this, "경매장 입장 제한이 해제되었습니다.", "Jihyeon 1.2.331", 1);
      }
   }

   public static void addChatLog(int type, String message, String sender) {
      addChatLog(type, 1, message, sender, "", null);
   }

   public static void addChatLog(int type, int channel, String message, String sender, String reciver) {
      addChatLog(type, channel, message, sender, reciver, null);
   }

   public static void addChatLog(int type, int channel, String message, String sender, String reciver, MapleCharacter chr) {
      if (!isDisabledChat() || DBConfig.isGanglim) {
         String ch = "";
         if (channel == 1) {
            ch = "1";
         } else if (channel == 2) {
            ch = "20세 이상";
         } else if (channel > 2) {
            ch = Integer.toString(channel - 1);
         }

         StringBuilder sb = new StringBuilder();
         switch (type) {
            case 0:
               sb.append("[일반 Ch.");
               sb.append(ch);
               sb.append("] ");
               break;
            case 1:
               sb.append("[친구에게 Ch.");
               sb.append(ch);
               sb.append("] ");
               break;
            case 2:
               if (chr != null) {
                  String list = "";
                  List<String> names = new ArrayList<>();
                  if (chr.getParty() == null) {
                     names.add(chr.getName());
                  } else {
                     for (PartyMemberEntry mpc : new ArrayList<>(chr.getParty().getPartyMemberList())) {
                        names.add(mpc.getName());
                     }
                  }

                  list = String.join(",", names);
                  sb.append("[파티에게 Ch.");
                  sb.append(ch);
                  sb.append("] (");
                  sb.append(list);
                  sb.append(") ");
               }
               break;
            case 3:
               sb.append("[길드에게 Ch.");
               sb.append(ch);
               sb.append("] ");
               break;
            case 4:
               sb.append("[연합에게 Ch.");
               sb.append(ch);
               sb.append("] ");
               break;
            case 5:
               sb.append("[원정대에게 Ch.");
               sb.append(ch);
               sb.append("] ");
               break;
            case 6:
               sb.append("[귓속말 : ");
               sb.append(sender);
               sb.append(" -> ");
               sb.append(reciver);
               sb.append(" Ch.");
               sb.append(ch);
               sb.append("] ");
               break;
            case 7:
               sb.append("[전체 채팅 Ch.");
               sb.append(ch);
               sb.append("] ");
         }

         sb.append(sender);
         sb.append(" : ");
         sb.append(message);
         if (DBConfig.isGanglim) {
            if (type == 0) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String dayString = sdf.format(System.currentTimeMillis());
               FileoutputUtil.log("./TextLog/Chat/General/" + dayString + ".txt", sb.toString());
            }

            if (type == 1) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String dayString = sdf.format(System.currentTimeMillis());
               FileoutputUtil.log("./TextLog/Chat/Friend/" + dayString + ".txt", sb.toString());
            }

            if (type == 2) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String dayString = sdf.format(System.currentTimeMillis());
               FileoutputUtil.log("./TextLog/Chat/Party/" + dayString + ".txt", sb.toString());
            }

            if (type == 3) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String dayString = sdf.format(System.currentTimeMillis());
               FileoutputUtil.log("./TextLog/Chat/Guild/" + dayString + ".txt", sb.toString());
            }

            if (type == 4) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String dayString = sdf.format(System.currentTimeMillis());
               FileoutputUtil.log("./TextLog/Chat/Alliance/" + dayString + ".txt", sb.toString());
            }

            if (type == 6) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String dayString = sdf.format(System.currentTimeMillis());
               FileoutputUtil.log("./TextLog/Chat/Whisper/" + dayString + ".txt", sb.toString());
            }

            if (type == 7) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String dayString = sdf.format(System.currentTimeMillis());
               FileoutputUtil.log("./TextLog/GlobalChat/" + dayString + ".txt", sb.toString());
            }
         }

         if (!DBConfig.isGanglim && type == 7) {
            FileoutputUtil.log("./TextLog/GlocalChat.txt", sb.toString());
         }

         if (!isDisabledChat()) {
            if (nextDisableChatTime != 0L) {
               FileoutputUtil.log("./TextLog/ChattingLog.txt", sb.toString());
            }

            try {
               chatLogLock.writeLock().lock();
               if (chatLogs.size() > 1200) {
                  chatLogs.remove(0);
               }

               chatLogs.add(sb.toString());
            } finally {
               chatLogLock.writeLock().unlock();
            }

            chatList.setListData(chatLogs.toArray(new String[0]));
         }
      }
   }

   public static void updatePlayerList() {
      try {
         names.clear();
         int count = 0;

         for (GameServer cs : GameServer.getAllInstances()) {
            for (Field map : new ArrayList<>(cs.getMapFactory().getAllMaps())) {
               for (MapleCharacter chr : new ArrayList<>(map.getCharacters())) {
                  if (chr != null && chr.getName() != null) {
                     if (names.contains(chr.getName())) {
                        names.add(chr.getName() + "_" + Randomizer.rand(0, 99999));
                     } else if (chr.getClient().getSession().isOpen()) {
                        count++;
                        names.add(chr.getName());
                     }
                  }
               }
            }
         }

         for (MapleCharacter player : AuctionServer.getPlayerStorage().getAllCharacters()) {
            if (player != null) {
               if (names.contains(player.getName())) {
                  names.add(player.getName() + "_" + Randomizer.rand(0, 99999));
               } else if (player.getClient().getSession().isOpen()) {
                  count++;
                  names.add(player.getName());
               }
            }
         }

         for (MapleCharacter playerx : CashShopServer.getPlayerStorage().getAllCharacters()) {
            if (playerx != null) {
               if (names.contains(playerx.getName())) {
                  names.add(playerx.getName() + "_" + Randomizer.rand(0, 99999));
               } else if (playerx.getClient().getSession().isOpen()) {
                  count++;
                  names.add(playerx.getName());
               }
            }
         }

         if (connectList != null) {
            connectList.setListData(names.toArray(new String[0]));
            dongsi.setText("同时在线人数 : " + count + "名");
         }
      } catch (Exception var7) {
         System.out.println("updatePlayerList 함수 실행중 오류발생" + var7.toString());
         var7.printStackTrace();
      }
   }

   public static void init() {
      expRate.setText(GameServer.getInstance(1).getExpRate() + "");
      mesoRate.setText(GameServer.getInstance(1).getMesoRate() + "");
      dropRate.setText(GameServer.getInstance(1).getDropRate() + "");
      Timer.EtcTimer.getInstance().register(new Runnable() {
         @Override
         public void run() {
            long runtime_l = (System.currentTimeMillis() - AdminClient.startTime) / 1000L;
            long second = runtime_l % 60L;
            runtime_l /= 60L;
            long minute = runtime_l % 60L;
            runtime_l /= 60L;
            long hour = runtime_l % 24L;
            runtime_l /= 24L;
            long day = runtime_l % 365L;
            AdminClient.runtime.setText("서버 런타임 : " + day + "일 " + hour + "시간 " + minute + "분 " + second + "초");
         }
      }, 1000L);
   }

   public String StringFilter(BufferedReader check, BufferedReader reader) throws IOException {
      StringBuilder builder = new StringBuilder();

      while (check.readLine() != null) {
         String line = reader.readLine();
         if (line.indexOf("//") != -1) {
            line = line.replace(line.substring(line.indexOf("//"), line.length()), "");
         }

         builder.append(line);
      }

      return builder.toString();
   }

   public static void main() {
      try {
         for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException var4) {
         Logger.getLogger(AdminClient.class.getName()).log(Level.SEVERE, null, var4);
      } catch (InstantiationException var5) {
         Logger.getLogger(AdminClient.class.getName()).log(Level.SEVERE, null, var5);
      } catch (IllegalAccessException var6) {
         Logger.getLogger(AdminClient.class.getName()).log(Level.SEVERE, null, var6);
      } catch (UnsupportedLookAndFeelException var7) {
         Logger.getLogger(AdminClient.class.getName()).log(Level.SEVERE, null, var7);
      }

      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            AdminClient.startTime = System.currentTimeMillis();
            AdminClient.chatLogLock = new ReentrantReadWriteLock();
            new AdminClient().setVisible(true);
            AdminClient.updatePlayerList();
            AdminClient.init();
         }
      });
   }

   public static long getNextDisableChatTime() {
      return nextDisableChatTime;
   }

   public static void setNextDisableChatTime(long aNextDisableChatTime) {
      nextDisableChatTime = aNextDisableChatTime;
   }

   public static boolean isDisabledChat() {
      return disabledChat;
   }

   public static void setDisabledChat(boolean aDisabledChat) {
      disabledChat = aDisabledChat;
   }
}
