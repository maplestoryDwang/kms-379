package objects.fields.fieldset.instance;

import java.awt.Point;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.PacketHelper;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class PuzzleMaster extends FieldSetInstance {
   public List<Integer> randomPuzzleList = new ArrayList<>();
   public int randomPuzzle = 0;
   public int stage = 1;
   public HashMap<Integer, String> PickedPuzzle = new HashMap<>();
   public static List<Rectangle> lotation = new ArrayList<>();
   public int completePuzzleCount = 3;
   public List<Integer> setPuzzles = new ArrayList<>();
   public List<Integer> generatePuzzles = new ArrayList<>();

   public PuzzleMaster(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      if (lotation.size() == 0) {
         for (int i = 0; i < 5; i++) {
            int start = 764 + 160 * i;
            int rightStart = start + 139;
            int leftTop = 746;
            int rightBottom = leftTop + 129;
            Rectangle rect = new Rectangle(start, leftTop, rightStart - start, rightBottom - leftTop);
            lotation.add(rect);
         }

         for (int i = 0; i < 5; i++) {
            int start = 764 + 160 * i;
            int rightStart = start + 139;
            int leftTop = 896;
            int rightBottom = leftTop + 129;
            Rectangle rect = new Rectangle(start, leftTop, rightStart - start, rightBottom - leftTop);
            lotation.add(rect);
         }

         for (int i = 0; i < 5; i++) {
            int start = 764 + 160 * i;
            int rightStart = start + 139;
            int leftTop = 1046;
            int rightBottom = leftTop + 129;
            Rectangle rect = new Rectangle(start, leftTop, rightStart - start, rightBottom - leftTop);
            lotation.add(rect);
         }

         for (int i = 0; i < 5; i++) {
            int start = 764 + 160 * i;
            int rightStart = start + 139;
            int leftTop = 1196;
            int rightBottom = leftTop + 129;
            Rectangle rect = new Rectangle(start, leftTop, rightStart - start, rightBottom - leftTop);
            lotation.add(rect);
         }
      }

      this.generatePuzzle();
      this.randomPuzzle = Randomizer.rand(0, 38);
      this.randomPuzzleList.add(this.randomPuzzle);
      this.channel = channel;
      this.fieldSeteventTime = 1800000;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);

      for (Integer map : this.fsim.instances) {
         this.field(map).resetFully(false);
         this.field(map).setLastRespawnTime(System.currentTimeMillis());
         this.field(map).setFieldSetInstance(this);
      }

      this.fs.fInstance.putIfAbsent(this, new ArrayList<>());

      for (PartyMemberEntry mpc : this.leader.getParty().getPartyMemberList()) {
         this.fs.fInstance.get(this).add(mpc.getId());
      }

      this.userList = this.fs.fInstance.get(this);
      this.waitGameStart();
      this.spawnPuzzleReactor();
      this.timeOut(this.fieldSeteventTime + 20000);
   }

   @Override
   public void userEnter(MapleCharacter user) {
      if (user.getMap().getId() == 993194800) {
         user.send(CField.getClock(19));
      }

      if (user.getMap().getId() == 993194900) {
         SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
         Date lastTime = null;
         Date now = null;

         try {
            lastTime = sdf.parse(user.getOneInfo(501600, "date"));
            now = sdf.parse(sdf.format(new Date()));
         } catch (Exception var6) {
            lastTime = null;
         }

         if (lastTime != null && !lastTime.equals(now) || lastTime == null) {
            user.updateOneInfo(501600, "start", "1");
            user.updateOneInfo(501600, "date", sdf.format(new Date()));
            user.updateOneInfo(501600, "today", "0");
         }

         user.updateOneInfo(501600, "complete", "0");
         user.send(PuzzleMasterStartTimer(1, System.currentTimeMillis() + 8000L));
         user.send(this.PuzzleMasterSetInfo(this.randomPuzzle, this.stage));
         user.send(CField.tangyoonClock(1800));
         user.send(CField.addPopupSay(9062572, 5000, "#b#e크리에이터들의\r\n퍼즐 맞추기 도전!#n#k\r\n#r지금 바로 시작합니다!#k", ""));
      }
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
   }

   @Override
   public void userDead(MapleCharacter user) {
   }

   @Override
   public void userLeftParty(MapleCharacter user) {
      super.userLeftParty(user);
   }

   @Override
   public void userDisbandParty() {
      super.userDisbandParty();
   }

   @Override
   public void userDisconnected(MapleCharacter user) {
      super.userDisconnected(user);
   }

   @Override
   public void mobDead(MapleMonster mMob) {
   }

   public void waitGameStart() {
      if (this.eventSchedules.get("waitGameStart") != null) {
         this.eventSchedules.get("waitGameStart").cancel(false);
         this.eventSchedules.remove("waitGameStart");
      }

      this.eventSchedules.put("waitGameStart", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!PuzzleMaster.this.dispose) {
               Field map = PuzzleMaster.this.field(993194800);
               if (map.getCharactersSize() > 0) {
                  for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                     chr.changeMap(993194900);
                  }
               }
            }
         }
      }, 20000L));
   }

   public void spawnPuzzleReactor() {
      final Field map = this.field(993194900);
      map.reloadReactors();
      this.eventSchedules.put("hitReactor", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!PuzzleMaster.this.dispose) {
               map.broadcastMessage(PuzzleMaster.PuzzleMasterStartTimer(2, System.currentTimeMillis()));
               map.broadcastMessage(PuzzleMaster.this.PuzzleMasterSetPuzzle());
               map.setReactorState((byte)1);
               map.setReactorState((byte)2);
            }
         }
      }, 29000L));
   }

   public void generatePuzzle() {
      this.setPuzzles.clear();
      this.generatePuzzles.clear();

      while (this.setPuzzles.size() < 3) {
         int random = Randomizer.rand(0, 19);
         if (!this.setPuzzles.contains(random)) {
            this.setPuzzles.add(random);
         }
      }

      while (this.generatePuzzles.size() < 17) {
         int random = Randomizer.rand(0, 19);
         if (!this.setPuzzles.contains(random) && !this.generatePuzzles.contains(random)) {
            this.generatePuzzles.add(random);
         }
      }

      this.generatePuzzles.add(Randomizer.rand(0, 17), -1);
      this.generatePuzzles.add(Randomizer.rand(0, 17), -1);
      this.generatePuzzles.add(Randomizer.rand(0, 17), -1);
   }

   public void pickUpPuzzle(final MapleCharacter user, final Reactor reactor) {
      this.PickedPuzzle.put(user.getId(), reactor.getName());
      reactor.forceHitReactor((byte)3);
      user.getMap().broadcastMessage(PuzzleMasterPickUp(user.getId(), reactor.getName(), 0));
      this.eventSchedules.put(user.getName(), Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!PuzzleMaster.this.dispose) {
               user.temporaryStatSet(196731, 1000, SecondaryStatFlag.Stun, 1);
               reactor.forceHitReactor((byte)1);
               user.getMap().broadcastMessage(PuzzleMaster.PuzzleMasterPickUp(user.getId(), "", 2));
               user.getMap().broadcastMessage(PuzzleMaster.PuzzleMasterSet());
               reactor.forceHitReactor((byte)2);
            }
         }
      }, 20000L));
   }

   public void putDownPuzzle(MapleCharacter user, Point pos) {
      if (this.eventSchedules.get(user.getName()) != null) {
         this.eventSchedules.get(user.getName()).cancel(false);
         this.eventSchedules.remove(user.getName());
      }

      if (this.PickedPuzzle.get(user.getId()) != null) {
         final Reactor reactor = user.getMap().getReactorByName(this.PickedPuzzle.get(user.getId()));
         if (reactor != null) {
            int index = 0;

            for (Rectangle rect : lotation) {
               if (rect.contains(pos)) {
                  break;
               }

               index++;
            }

            int temp = Integer.parseInt(reactor.getName().replace("outPuzzle", ""));
            int reactorNumber = Integer.parseInt(reactor.getName().replace("outPuzzle", ""));
            reactorNumber = this.generatePuzzles.get(reactorNumber);
            if (reactorNumber == index) {
               this.completePuzzleCount++;
               final Field map = this.field(993194900);
               reactor.forceHitReactor((byte)0);
               map.broadcastMessage(this.PuzzleMasterPutDown(temp, index));
               map.broadcastMessage(PuzzleMasterPickUp(user.getId(), "", 1));
               map.broadcastMessage(PuzzleMasterSet());
               if (this.completePuzzleCount >= 20) {
                  this.completePuzzleCount = 3;
                  this.randomPuzzle = Randomizer.rand(0, 38);

                  while (this.randomPuzzleList.contains(this.randomPuzzle)) {
                     this.randomPuzzle = Randomizer.rand(0, 38);
                  }

                  this.randomPuzzleList.add(this.randomPuzzle);
                  if (this.stage < 3) {
                     this.stage++;
                     this.generatePuzzle();
                     map.broadcastMessage(PuzzleMasterStartTimer(3, System.currentTimeMillis() + 6000L));
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           if (!PuzzleMaster.this.dispose) {
                              map.setReactorState((byte)0);
                              map.broadcastMessage(PuzzleMaster.PuzzleMasterStartTimer(1, System.currentTimeMillis() + 10000L));
                              map.broadcastMessage(PuzzleMaster.this.PuzzleMasterSetInfo(PuzzleMaster.this.randomPuzzle, PuzzleMaster.this.stage));
                           }
                        }
                     }, 6000L);
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           if (!PuzzleMaster.this.dispose) {
                              map.broadcastMessage(PuzzleMaster.PuzzleMasterStartTimer(2, System.currentTimeMillis()));
                              map.broadcastMessage(PuzzleMaster.this.PuzzleMasterSetPuzzle());
                              map.setReactorState((byte)1);
                              map.setReactorState((byte)2);
                           }
                        }
                     }, 17000L);
                  } else {
                     if (this.eventSchedules.get("timeOut") != null) {
                        this.eventSchedules.get("timeOut").cancel(false);
                        this.eventSchedules.remove("timeOut");
                     }

                     map.broadcastMessage(PuzzleMasterStartTimer(4, System.currentTimeMillis()));
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           if (!PuzzleMaster.this.dispose) {
                              if (map.getCharactersSize() > 0) {
                                 for (MapleCharacter player : new ArrayList<>(map.getCharacters())) {
                                    player.updateOneInfo(501600, "start", "0");
                                    player.updateOneInfo(501600, "complete", "1");
                                    player.changeMap(993194801);
                                 }
                              }
                           }
                        }
                     }, 5000L);
                  }
               }
            } else {
               user.temporaryStatSet(196731, 1000, SecondaryStatFlag.Stun, 1);
               reactor.forceHitReactor((byte)1);
               user.getMap().broadcastMessage(PuzzleMasterPickUp(user.getId(), "", 2));
               user.getMap().broadcastMessage(PuzzleMasterSet());
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     if (!PuzzleMaster.this.dispose) {
                        reactor.forceHitReactor((byte)2);
                     }
                  }
               }, 1000L);
            }
         }
      }
   }

   public byte[] PuzzleMasterSetInfo(int puzzleNumber, int stage) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.PUZZLE_MASTER_LOAD_IMAGE.getValue());
      p.writeInt(puzzleNumber);
      p.writeInt(stage);
      p.writeInt(5);
      p.writeInt(4);
      p.writeInt(20000);
      return p.getPacket();
   }

   public static byte[] PuzzleMasterStartTimer(int command, long time) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.PUZZLE_MASTER_START_TIMER.getValue());
      p.writeInt(command);
      p.writeLong(PacketHelper.getTime(time));
      return p.getPacket();
   }

   public byte[] PuzzleMasterSetPuzzle() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.PUZZLE_MASTER_PACKET.getValue());
      p.writeInt(this.setPuzzles.size());

      for (Integer set : this.setPuzzles) {
         p.writeInt(set);
      }

      p.writeInt(this.generatePuzzles.size());

      for (Integer gene : this.generatePuzzles) {
         p.writeInt(gene);
      }

      return p.getPacket();
   }

   public byte[] PuzzleMasterPutDown(int geneIndex, int puzzleIndex) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.PUZZLE_MASTER_PUT_DOWN.getValue());
      p.writeInt(geneIndex);
      p.writeInt(puzzleIndex);
      return p.getPacket();
   }

   public static byte[] PuzzleMasterPickUp(int userId, String reactorName, int command) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.PUZZLE_MASTER_PICK_UP.getValue());
      p.writeInt(userId);
      p.writeMapleAsciiString(reactorName);
      p.writeInt(command);
      return p.getPacket();
   }

   public static byte[] PuzzleMasterSet() {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.PUZZLE_MASTER_SET.getValue());
      return p.getPacket();
   }
}
