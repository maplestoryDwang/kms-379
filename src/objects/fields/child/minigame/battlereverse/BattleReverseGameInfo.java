package objects.fields.child.minigame.battlereverse;

import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public class BattleReverseGameInfo {
   private MapleCharacter[] players = new MapleCharacter[]{null, null};
   private boolean isinit = false;
   private int[] teamhp = new int[]{10000, 10000};
   private int[] warncount = new int[]{0, 0};
   private int turnteam = 0;
   private long turnteamtime = 0L;
   private byte[][] chips = new byte[8][8];
   private int last_stone_x = -1;
   private int last_stone_y = -1;
   private int last_stone_team = -1;
   private List<Point> team0can = Collections.synchronizedList(new ArrayList<>());
   private List<Point> team1can = Collections.synchronizedList(new ArrayList<>());

   public void setTeamByChr(MapleCharacter chr) {
      if (this.players[0] == null) {
         this.players[0] = chr;
      } else {
         this.players[1] = chr;
      }
   }

   public int getTeamByChr(MapleCharacter chr) {
      if (this.players[0].getId() == chr.getId()) {
         return 0;
      } else {
         return this.players[1].getId() == chr.getId() ? 1 : -1;
      }
   }

   public MapleCharacter getCharacter(int team) {
      return this.players[team];
   }

   public void InitBoard() {
      if (!this.isinit) {
         for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
               this.chips[x][y] = -1;
            }
         }

         this.chips[3][3] = 0;
         this.chips[3][4] = 1;
         this.chips[4][3] = 1;
         this.chips[4][4] = 0;

         for (int count = 0; count < 5; count++) {
            int y;
            int x;
            do {
               x = ThreadLocalRandom.current().nextInt(8);
               y = ThreadLocalRandom.current().nextInt(8);
            } while (
               (
                     this.chips[2][3] != 3 && this.chips[3][2] != 3 && this.chips[4][5] != 3 && this.chips[5][4] != 3
                        ? (this.chips[2][4] == 3 || this.chips[3][5] == 3 || this.chips[4][2] == 3 || this.chips[5][3] == 3)
                           && (x == 2 && y == 4 || x == 3 && y == 5 || x == 4 && y == 2 || x == 5 && y == 3)
                        : x == 2 && y == 3 || x == 3 && y == 2 || x == 4 && y == 5 || x == 5 && y == 4
                  )
                  || this.chips[x][y] != -1
                  || !this.checkBoardBlock(x, y)
            );

            this.chips[x][y] = 3;
         }

         this.findPuttablePos();
         this.isinit = true;
      }
   }

   public boolean isInit() {
      return this.isinit;
   }

   public List<Point> getPuttableList(int team) {
      List<Point> copy = new ArrayList<>();
      if (team == 0) {
         copy.addAll(this.team0can);
      }

      if (team == 1) {
         copy.addAll(this.team1can);
      }

      return copy;
   }

   public void findPuttablePos() {
      this.team0can.clear();
      this.team1can.clear();

      for (int x = 0; x < 8; x++) {
         for (int y = 0; y < 8; y++) {
            if (this.chips[x][y] == -1) {
               Byte right = this.checkRight(x, y);
               if (right != null) {
                  if (right == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (right == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }

               Byte rightdown = this.checkRightDown(x, y);
               if (rightdown != null) {
                  if (rightdown == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (rightdown == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }

               Byte down = this.checkDown(x, y);
               if (down != null) {
                  if (down == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (down == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }

               Byte leftdown = this.checkLeftDown(x, y);
               if (leftdown != null) {
                  if (leftdown == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (leftdown == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }

               Byte left = this.checkLeft(x, y);
               if (left != null) {
                  if (left == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (left == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }

               Byte leftup = this.checkLeftUp(x, y);
               if (leftup != null) {
                  if (leftup == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (leftup == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }

               Byte up = this.checkUp(x, y);
               if (up != null) {
                  if (up == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (up == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }

               Byte rightup = this.checkRightUp(x, y);
               if (rightup != null) {
                  if (rightup == 0) {
                     this.team0can.add(new Point(x, y));
                  }

                  if (rightup == 1) {
                     this.team1can.add(new Point(x, y));
                  }
               }
            }
         }
      }
   }

   Byte checkRight(int x, int y) {
      byte findtype = -1;

      for (int i = x + 1; i < 8; i++) {
         switch (this.chips[i][y]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   Byte checkRightDown(int x, int y) {
      byte findtype = -1;

      for (int delta = 1; delta < 8; delta++) {
         int i = x + delta;
         int j = y + delta;
         if (i < 0 || i >= 8 || j < 0 || j >= 8) {
            break;
         }

         switch (this.chips[i][j]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   Byte checkDown(int x, int y) {
      byte findtype = -1;

      for (int j = y + 1; j < 8; j++) {
         switch (this.chips[x][j]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   Byte checkLeftDown(int x, int y) {
      byte findtype = -1;

      for (int delta = 1; delta < 8; delta++) {
         int i = x - delta;
         int j = y + delta;
         if (i < 0 || i >= 8 || j < 0 || j >= 8) {
            break;
         }

         switch (this.chips[i][j]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   Byte checkLeft(int x, int y) {
      byte findtype = -1;

      for (int i = x - 1; i >= 0; i--) {
         switch (this.chips[i][y]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   Byte checkLeftUp(int x, int y) {
      byte findtype = -1;

      for (int delta = 1; delta < 8; delta++) {
         int i = x - delta;
         int j = y - delta;
         if (i < 0 || i >= 8 || j < 0 || j >= 8) {
            break;
         }

         switch (this.chips[i][j]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   Byte checkUp(int x, int y) {
      byte findtype = -1;

      for (int j = y - 1; j >= 0; j--) {
         switch (this.chips[x][j]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   Byte checkRightUp(int x, int y) {
      byte findtype = -1;

      for (int delta = 1; delta < 8; delta++) {
         int i = x + delta;
         int j = y - delta;
         if (i < 0 || i >= 8 || j < 0 || j >= 8) {
            break;
         }

         switch (this.chips[i][j]) {
            case -1:
               return null;
            case 0:
               if (findtype == -1) {
                  findtype = 1;
               } else if (findtype == 0) {
                  return (byte)1;
               }
               break;
            case 1:
               if (findtype == -1) {
                  findtype = 0;
               } else if (findtype == 1) {
                  return (byte)0;
               }
            case 2:
            default:
               break;
            case 3:
               return null;
         }
      }

      return null;
   }

   private boolean checkBoardBlock(int x, int y) {
      if (x > 0 && this.chips[x - 1][y] == 3) {
         return false;
      } else if (x < 7 && this.chips[x + 1][y] == 3) {
         return false;
      } else {
         return y > 0 && this.chips[x][y - 1] == 3 ? false : y >= 7 || this.chips[x][y + 1] != 3;
      }
   }

   public final byte[][] getBoard() {
      return this.chips;
   }

   public void ProcessChips(int x, int y, byte type) {
      if (this.chips[x][y] == -1) {
         this.chips[x][y] = (byte)(type == 1 ? 0 : 1);
         int[] dx = new int[]{-1, -1, -1, 0, 0, 1, 1, 1};
         int[] dy = new int[]{-1, 0, 1, -1, 1, -1, 0, 1};
         int count = 0;

         for (int i = 0; i < 8; i++) {
            int sideX = x + dx[i];
            int sideY = y + dy[i];
            if (sideX >= 0 && sideY >= 0 && sideX < 8 && sideY < 8 && this.chips[sideX][sideY] != this.chips[x][y]) {
               int[] target = this.findTarget(x, y, sideX, sideY, i);
               if (target != null) {
                  count += this.toggleStone(x, y, target[0], target[1], i);
               }
            }
         }

         if (count > 0) {
            int team = this.chips[x][y] == 0 ? 0 : 1;
            int teamhp = this.getTeamHP(team);
            this.setTeamHP(team, teamhp - count * 100);
            if (count > 5) {
               this.setTeamHP(team, teamhp - 200);
            } else if (count > 3) {
               this.setTeamHP(team, teamhp - 50);
            }
         }

         this.findPuttablePos();
      }
   }

   public int[] findTarget(int x, int y, int sideX, int sideY, int i) {
      switch (i) {
         case 0:
            while (this.chips[sideX--][sideY--] != this.chips[x][y]) {
               if (sideX < 0 || sideY < 0 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX + 1, sideY + 1};
         case 1:
            while (this.chips[sideX--][sideY] != this.chips[x][y]) {
               if (sideX < 0 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX + 1, sideY};
         case 2:
            while (this.chips[sideX--][sideY++] != this.chips[x][y]) {
               if (sideX < 0 || sideY >= 8 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX + 1, sideY - 1};
         case 3:
            while (this.chips[sideX][sideY--] != this.chips[x][y]) {
               if (sideY < 0 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX, sideY + 1};
         case 4:
            while (this.chips[sideX][sideY++] != this.chips[x][y]) {
               if (sideY >= 8 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX, sideY - 1};
         case 5:
            while (this.chips[sideX++][sideY--] != this.chips[x][y]) {
               if (sideX >= 8 || sideY < 0 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX - 1, sideY + 1};
         case 6:
            while (this.chips[sideX++][sideY] != this.chips[x][y]) {
               if (sideX >= 8 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX - 1, sideY};
         case 7:
            while (this.chips[sideX++][sideY++] != this.chips[x][y]) {
               if (sideX >= 8 || sideY >= 8 || this.chips[sideX][sideY] == -1 || this.chips[sideX][sideY] == 3) {
                  return null;
               }
            }

            return new int[]{sideX - 1, sideY - 1};
         default:
            return null;
      }
   }

   public int toggleStone(int x, int y, int tx, int ty, int i) {
      byte turn = this.chips[x][y];
      int count = 0;
      switch (i) {
         case 0:
            while (x-- != tx && y-- != ty && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
            break;
         case 1:
            while (x-- != tx && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
            break;
         case 2:
            while (x-- != tx && y++ != ty && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
            break;
         case 3:
            while (y-- != ty && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
            break;
         case 4:
            while (y++ != ty && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
            break;
         case 5:
            while (x++ != tx && y-- != ty && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
            break;
         case 6:
            while (x++ != tx && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
            break;
         case 7:
            while (x++ != tx && y++ != ty && this.chips[x][y] != -1 && this.chips[x][y] != 3) {
               this.chips[x][y] = turn;
               count++;
            }
      }

      return count;
   }

   public void setTeamHP(int team, int hp) {
      if (hp < 0) {
         hp = 0;
      }

      this.teamhp[team] = hp;
   }

   public int getTeamHP(int team) {
      return this.teamhp[team];
   }

   public int getTurnTeam() {
      return this.turnteam;
   }

   public void nextTurnTeam() {
      this.turnteam = this.turnteam == 1 ? 0 : 1;
      this.turnteamtime = System.currentTimeMillis();
   }

   public boolean canNextTurn() {
      return System.currentTimeMillis() - this.turnteamtime >= 10000L;
   }

   public void setLastStone(int x, int y, int team) {
      this.last_stone_x = x;
      this.last_stone_y = y;
      this.last_stone_team = team;
   }

   public Pair<Point, Integer> getLastStone() {
      return new Pair<>(new Point(this.last_stone_x, this.last_stone_y), this.last_stone_team);
   }

   public void incWarnCount(int team) {
      this.warncount[team]++;
   }

   public int getWarnCount(int team) {
      return this.warncount[team];
   }
}
