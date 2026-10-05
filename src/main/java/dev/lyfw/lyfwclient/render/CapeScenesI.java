package dev.lyfw.lyfwclient.render;

final class CapeScenesI {
   private CapeScenesI() {
   }

   static final class AlienArcade implements AnimatedCapes.Scene {
      private static final String[][] ALIENS = new String[][]{
         {"..X..X..", "...XX...", "..XXXX..", ".XX..XX.", "XXXXXXXX", "X.X..X.X"},
         {"..X..X..", "...XX...", "..XXXX..", ".XX..XX.", "XXXXXXXX", ".X....X."},
         {"...XX...", "..XXXX..", ".XX.X.X.", ".XXXXXX.", "..X..X..", ".X.XX.X."},
         {"...XX...", "..XXXX..", ".X.XX.X.", ".XXXXXX.", "...XX...", "..X..X.."},
         {".XXXXXX.", "XXX..XXX", "XXXXXXXX", "..XXXX..", ".X.XX.X.", "X......X"},
         {".XXXXXX.", "XXX..XXX", "XXXXXXXX", "..XXXX..", ".XX..XX.", "..X..X.."}
      };

      private static void sprite(AnimatedCapes.Canvas c, String[] rows, double x, double y, int color) {
         for (int r = 0; r < rows.length; r++) {
            for (int col = 0; col < rows[r].length(); col++) {
               if (rows[r].charAt(col) == 'X') {
                  c.set((int)x + col, (int)y + r, color);
               }
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         c.fill(-16645624);

         for (int i = 0; i < 50; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 1) * 128.0 + t * (4.0 + AnimatedCapes.hash(i, 2) * 10.0), 128.0);
            c.set((int)(AnimatedCapes.hash(i, 3) * 80.0), (int)y, AnimatedCapes.lerp(-12961200, -5197616, AnimatedCapes.hash(i, 4)));
         }

         double cycle = AnimatedCapes.wrap(t, 24.0);
         int round = (int)Math.floor(t / 24.0);
         int step = (int)Math.floor(cycle * 2.0);
         double march = (step % 16 < 8 ? step % 16 : 16 - step % 16) * 1.5;
         double drop = Math.min(24.0, step / 16 * 6.0);
         int frame = step % 2;
         int[] rowColors = new int[]{-40752, -10428161, -8323232};
         int alive = 0;

         for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 6; col++) {
               double killedAt = 2.0 + AnimatedCapes.hash(col * 7 + row, round) * 22.0;
               double ax = 4 + col * 12 + march;
               double ay = 16 + row * 10 + drop;
               int type = Math.min(2, row);
               if (cycle < killedAt) {
                  sprite(c, ALIENS[type * 2 + frame], ax, ay, rowColors[type]);
                  alive++;
               } else if (cycle < killedAt + 0.4) {
                  double p = (cycle - killedAt) / 0.4;

                  for (int k = 0; k < 8; k++) {
                     double a = k * Math.PI / 4.0;
                     c.set((int)(ax + 4.0 + Math.cos(a) * p * 6.0), (int)(ay + 3.0 + Math.sin(a) * p * 6.0), -1);
                  }
               }
            }
         }

         double shipX = 40.0 + Math.sin(t * 1.3) * 26.0 + Math.sin(t * 3.1) * 6.0;
         c.rect(shipX - 5.0, 116.0, 11.0, 3.0, -10420352);
         c.rect(shipX - 2.0, 113.0, 5.0, 3.0, -10420352);
         c.rect(shipX - 0.5, 111.0, 2.0, 2.0, -10420352);

         for (int s = 0; s < 3; s++) {
            double life = AnimatedCapes.wrap(t * 1.4 + s / 3.0, 1.0);
            double sx = 40.0 + Math.sin((t - life / 1.4) * 1.3) * 26.0 + Math.sin((t - life / 1.4) * 3.1) * 6.0;
            c.rect(sx, 110.0 - life * 100.0, 1.0, 3.0, -1);
         }

         double bomb = AnimatedCapes.wrap(t * 0.7, 1.0);
         c.rect(20.0 + AnimatedCapes.hash((int)(t * 0.7), 9) * 40.0, 50.0 + bomb * 60.0, 1.0, 3.0, -40896);

         for (int b = 0; b < 3; b++) {
            double bx = 10 + b * 24;

            for (int py = 0; py < 6; py++) {
               for (int px = 0; px < 12; px++) {
                  boolean arch = py > 3 && px > 3 && px < 8;
                  boolean eaten = AnimatedCapes.hash(px + b * 20, py + (int)(t * 0.3) % 5) > 0.85;
                  if (!arch && !eaten && (py != 0 || px != 0 && px != 11)) {
                     c.set((int)bx + px, 100 + py, -12525472);
                  }
               }
            }
         }

         int score = alive == 0 ? 2400 : (24 - alive) * 100;
         int[] digits = new int[]{31599, 11415, 29671, 29647, 23497, 31183, 31215, 29257, 31727, 31695};

         for (int d = 0; d < 4; d++) {
            int digit = score / (int)Math.pow(10.0, 3 - d) % 10;

            for (int bit = 0; bit < 15; bit++) {
               if ((digits[digit] >> 14 - bit & 1) != 0) {
                  c.set(4 + d * 4 + bit % 3, 3 + bit / 3, -1);
               }
            }
         }

         for (int l = 0; l < 3; l++) {
            c.rect(62 + l * 6, 4.0, 4.0, 2.0, -10420352);
            c.rect(63 + l * 6, 3.0, 2.0, 1.0, -10420352);
         }

         c.rect(0.0, 121.0, 80.0, 0.6, -10420352);

         for (int y = 0; y < 128; y += 2) {
            for (int x = 0; x < 80; x++) {
               c.px[y * 80 + x] = AnimatedCapes.shade(c.px[y * 80 + x], 0.78);
            }
         }
      }
   }

   static final class Beacon implements AnimatedCapes.Scene {
      private static final int B = 8;

      private static void block(AnimatedCapes.Canvas c, int x, int y, int top, int side) {
         for (int py = 0; py < 8; py++) {
            for (int px = 0; px < 8; px++) {
               int col = AnimatedCapes.lerp(top, side, (px + py) / 14.0);
               if (px == 0 || py == 0) {
                  col = AnimatedCapes.lerp(col, -1, 0.25);
               } else if (px != 7 && py != 7) {
                  if (AnimatedCapes.hash(x + px, y + py) > 0.85) {
                     col = AnimatedCapes.shade(col, 0.9);
                  }
               } else {
                  col = AnimatedCapes.shade(col, 0.65);
               }

               c.set(x + px, y + py, col);
            }
         }
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.8}, new int[]{-16513510, -15065536});

         for (int i = 0; i < 130; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 90.0,
               AnimatedCapes.hash(i, 3) > 0.96 ? 1.0 : 0.0,
               -1512193,
               0.1 + AnimatedCapes.hash(i, 4) * 0.45
            );
         }

         for (int x = 0; x < 80; x += 8) {
            for (int y = 112; y < 128; y += 8) {
               block(c, x, y, y == 112 ? -10837952 : -9811414, y == 112 ? -12944854 : -11914208);
            }
         }

         int[][] tiers = new int[][]{{-1513236, -6645088}, {-997312, -5207520}, {-9770784, -13983584}};

         for (int tier = 0; tier < 3; tier++) {
            int y = 104 - tier * 8;

            for (int x = 8 + tier * 8; x < 72 - tier * 8; x += 8) {
               block(c, x, y, tiers[tier][0], tiers[tier][1]);
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         int[] dyes = new int[]{-46518, -20416, -4000, -10420352, -12529409, -7708417, -40752};
         double phase = AnimatedCapes.wrap(t * 0.25, dyes.length);
         int beam = AnimatedCapes.lerp(dyes[(int)phase], dyes[((int)phase + 1) % dyes.length], AnimatedCapes.smoothstep(0.7, 1.0, phase - Math.floor(phase)));
         c.glow(40.0, 76.0, 40.0, beam, 0.35);

         for (int y = 0; y < 76; y++) {
            double shimmer = 0.85 + 0.15 * Math.sin(y * 0.45 - t * 10.0);

            for (int x = 28; x < 52; x++) {
               double d = Math.abs(x + 0.5 - 40.0);
               if (d < 3.0) {
                  c.add(x, y, AnimatedCapes.lerp(beam, -1, 0.75), (1.0 - d / 3.0) * shimmer);
               }

               if (d < 11.0) {
                  c.add(x, y, beam, 0.55 * Math.pow(1.0 - d / 11.0, 2.0) * shimmer);
               }
            }
         }

         for (int py = 0; py < 8; py++) {
            for (int px = 0; px < 8; px++) {
               boolean frame = px == 0 || py == 0 || px == 7 || py == 7;
               c.set(36 + px, 76 + py, frame ? -9770768 : -15062470);
            }
         }

         c.glow(40.0, 80.0, 5.0, -1, 0.7 + 0.2 * Math.sin(t * 4.0));
         c.polygon(new double[][]{{40.0, 77.5}, {42.0, 80.0}, {40.0, 82.5}, {38.0, 80.0}}, -984833);

         for (int i = 0; i < 24; i++) {
            double life = AnimatedCapes.wrap(t * 0.4 + AnimatedCapes.hash(i, 10), 1.0);
            double x = 40.0 + (AnimatedCapes.hash(i, 11) - 0.5) * 10.0 + Math.sin(life * 8.0 + i) * 2.0;
            double y = 74.0 - life * 74.0;
            c.rect(x, y, 1.0, 1.0, AnimatedCapes.alpha(AnimatedCapes.lerp(beam, -1, 0.5), Math.sin(life * Math.PI)));
         }

         for (int i = 0; i < 8; i++) {
            int tick = (int)Math.floor(t + AnimatedCapes.hash(i, 20) * 4.0);
            double x = 10.0 + AnimatedCapes.hash(i, tick + 21) * 60.0;
            double y = 82.0 + AnimatedCapes.hash(i, tick + 22) * 28.0;
            c.star(x, y, 1.0, -1, Math.sin(AnimatedCapes.wrap(t + AnimatedCapes.hash(i, 20) * 4.0, 1.0) * Math.PI) * 0.8);
         }
      }
   }

   static final class Equalizer implements AnimatedCapes.Scene {
      private static final int BARS = 12;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-16251878, -15070672});

         for (int x = 0; x < 80; x += 8) {
            c.line(x, 0.0, x, 128.0, 0.4, AnimatedCapes.alpha(-11916662, 0.25));
         }

         for (int y = 0; y < 128; y += 8) {
            c.line(0.0, y, 80.0, y, 0.4, AnimatedCapes.alpha(-11916662, 0.25));
         }
      }

      private static double level(int bar, double t) {
         double beat = Math.pow(Math.max(0.0, Math.sin(t * Math.PI * 2.0 * 2.0)), 8.0);
         double bass = bar < 3 ? beat * 0.6 : 0.0;
         double snare = bar > 4 && bar < 9 ? Math.pow(Math.max(0.0, Math.sin(t * Math.PI * 2.0 * 2.0 + Math.PI)), 10.0) * 0.45 : 0.0;
         double hat = bar >= 9 ? AnimatedCapes.noise(t * 12.0, bar) * 0.35 : 0.0;
         return AnimatedCapes.clamp(0.15 + AnimatedCapes.noise(t * 3.0 + bar * 1.7, bar * 3.1) * 0.35 + bass + snare + hat, 0.0, 1.0);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double floor = 92.0;
         double maxH = 70.0;
         double barW = 5.0;
         double gap = (80.0 - 12.0 * barW) / 13.0;
         double beat = Math.pow(Math.max(0.0, Math.sin(t * Math.PI * 4.0)), 8.0);
         c.ring(40.0, 50.0, 20.0 + beat * 14.0, 1.2, AnimatedCapes.alpha(-40752, 0.3 * beat));
         c.glow(40.0, floor, 50.0, -7716609, 0.15 + beat * 0.15);

         for (int b = 0; b < 12; b++) {
            double x = gap + b * (barW + gap);
            double level = level(b, t);
            double h = level * maxH;
            double peak = 0.0;

            for (double back = 0.0; back < 1.2; back += 0.1) {
               peak = Math.max(peak, level(b, t - back) - back * 0.6);
            }

            for (int y = (int)(floor - h); y < floor; y++) {
               double f = (floor - y) / maxH;
               int col = AnimatedCapes.ramp(f, new double[]{0.0, 0.5, 1.0}, new int[]{-12525313, -5218049, -48992});
               if ((int)(floor - y) % 3 != 0) {
                  c.rect(x, y, barW, 1.0, col);
               }
            }

            c.glow(x + barW / 2.0, floor - h, 5.0, -32560, 0.25);
            c.rect(x, floor - peak * maxH - 3.0, barW, 1.2, -1);

            for (int yx = (int)floor + 2; yx < floor + 2.0 + h * 0.4; yx++) {
               double f = (yx - floor - 2.0) / (h * 0.4 + 1.0);
               c.rect(x, yx, barW, 1.0, AnimatedCapes.alpha(-9813824, 0.35 * (1.0 - f)));
            }
         }

         c.rect(0.0, floor, 80.0, 1.0, -5209857);

         for (int x = 0; x < 79; x++) {
            double y0 = 14.0 + Math.sin(x * 0.35 + t * 8.0) * 4.0 * (0.5 + beat) + Math.sin(x * 0.9 - t * 5.0) * 1.5;
            double y1 = 14.0 + Math.sin((x + 1) * 0.35 + t * 8.0) * 4.0 * (0.5 + beat) + Math.sin((x + 1) * 0.9 - t * 5.0) * 1.5;
            c.beam(x, y0, x + 1, y1, 0.5, -10424065, 0.8);
         }
      }
   }

   static final class FallingBlocks implements AnimatedCapes.Scene {
      private static final int COLS = 10;
      private static final int ROWS = 17;
      private static final int CELL = 7;
      private static final int OX = 5;
      private static final int OY = 6;
      private static final int[][][] SHAPES = new int[][][]{
         {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
         {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
         {{0, 0}, {1, 0}, {2, 0}, {1, 1}},
         {{0, 0}, {1, 0}, {1, 1}, {2, 1}},
         {{1, 0}, {2, 0}, {0, 1}, {1, 1}},
         {{0, 0}, {0, 1}, {1, 1}, {2, 1}},
         {{2, 0}, {0, 1}, {1, 1}, {2, 1}}
      };
      private static final int[] COLORS = new int[]{-12525328, -995264, -5222160, -1029558, -11870112, -11900176, -1009104};
      private static final double DROP = 1.1;

      private static int[][] rotate(int[][] shape, int turns) {
         int[][] cells = new int[shape.length][];

         for (int i = 0; i < shape.length; i++) {
            int x = shape[i][0];
            int y = shape[i][1];

            for (int r = 0; r < turns; r++) {
               int nx = -y;
               y = x;
               x = nx;
            }

            cells[i] = new int[]{x, y};
         }

         int minX = Integer.MAX_VALUE;
         int minY = Integer.MAX_VALUE;

         for (int[] cell : cells) {
            minX = Math.min(minX, cell[0]);
            minY = Math.min(minY, cell[1]);
         }

         for (int[] cell : cells) {
            cell[0] -= minX;
            cell[1] -= minY;
         }

         return cells;
      }

      private static boolean fits(int[][] board, int[][] cells, int col, int row) {
         for (int[] cell : cells) {
            int x = col + cell[0];
            int y = row + cell[1];
            if (x < 0 || x >= 10 || y >= 17 || y >= 0 && board[y][x] != 0) {
               return false;
            }
         }

         return true;
      }

      private static void cell(AnimatedCapes.Canvas c, int col, double row, int color) {
         double x = 5 + col * 7;
         double y = 6.0 + row * 7.0;
         c.rect(x, y, 7.0, 7.0, color);
         c.rect(x, y, 7.0, 1.0, AnimatedCapes.lerp(color, -1, 0.45));
         c.rect(x, y, 1.0, 7.0, AnimatedCapes.lerp(color, -1, 0.3));
         c.rect(x, y + 7.0 - 1.0, 7.0, 1.0, AnimatedCapes.shade(color, 0.6));
         c.rect(x + 7.0 - 1.0, y, 1.0, 7.0, AnimatedCapes.shade(color, 0.7));
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-16119264, -15070678});
         c.rect(3.0, 4.0, 74.0, 123.0, -9803024);
         c.rect(4.0, 5.0, 72.0, 121.0, -16382438);

         for (int x = 0; x <= 10; x++) {
            c.rect(5 + x * 7, 6.0, 0.5, 119.0, AnimatedCapes.alpha(-14013862, 0.5));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         int game = (int)Math.floor(t / 70.0);
         double inGame = AnimatedCapes.wrap(t, 70.0);
         int placed = (int)Math.floor(inGame / 1.1);
         int[][] board = new int[17][10];
         int flashRow = -1;
         double flash = 0.0;

         for (int p = 0; p <= placed; p++) {
            int shapeIndex = (int)(AnimatedCapes.hash(p, game + 1) * SHAPES.length);
            int bestTurn = 0;
            int bestCol = 0;
            int bestRow = -1;
            double bestScore = -1.0E9;

            for (int turn = 0; turn < 4; turn++) {
               int[][] cells = rotate(SHAPES[shapeIndex], turn);

               for (int col = 0; col < 10; col++) {
                  if (fits(board, cells, col, 0)) {
                     int row = 0;

                     while (fits(board, cells, col, row + 1)) {
                        row++;
                     }

                     double score = row * 2;

                     for (int[] cc : cells) {
                        int below = row + cc[1] + 1;
                        if (below < 17 && board[below][col + cc[0]] == 0) {
                           boolean covered = true;

                           for (int[] other : cells) {
                              if (other[0] == cc[0] && other[1] == cc[1] + 1) {
                                 covered = false;
                              }
                           }

                           if (covered) {
                              score -= 3.0;
                           }
                        }
                     }

                     if (score > bestScore) {
                        bestScore = score;
                        bestTurn = turn;
                        bestCol = col;
                        bestRow = row;
                     }
                  }
               }
            }

            int[][] cells = rotate(SHAPES[shapeIndex], bestTurn);
            if (bestRow < 0) {
               board = new int[17][10];
            } else {
               if (p == placed) {
                  double fall = (inGame - placed * 1.1) / 1.1;
                  double row = Math.min((double)bestRow, -2.0 + fall * (bestRow + 2) * 1.4);

                  for (int[] ccx : cells) {
                     cell(c, bestCol + ccx[0], row + ccx[1], COLORS[shapeIndex]);
                  }

                  for (int[] ccx : cells) {
                     c.rect(5 + (bestCol + ccx[0]) * 7 + 1, 6 + (bestRow + ccx[1]) * 7 + 1, 5.0, 5.0, AnimatedCapes.alpha(COLORS[shapeIndex], 0.2));
                  }
                  break;
               }

               for (int[] ccx : cells) {
                  board[bestRow + ccx[1]][bestCol + ccx[0]] = shapeIndex + 1;
               }

               for (int row = 16; row >= 0; row--) {
                  boolean full = true;

                  for (int colx = 0; colx < 10; colx++) {
                     full &= board[row][colx] != 0;
                  }

                  if (full) {
                     if (p == placed - 1) {
                        flashRow = row;
                        flash = 1.0 - (inGame - placed * 1.1) / 1.1;
                     }

                     for (int r = row; r > 0; r--) {
                        board[r] = (int[])board[r - 1].clone();
                     }

                     board[0] = new int[10];
                     row++;
                  }
               }
            }
         }

         for (int row = 0; row < 17; row++) {
            for (int colx = 0; colx < 10; colx++) {
               if (board[row][colx] != 0) {
                  cell(c, colx, row, COLORS[board[row][colx] - 1]);
               }
            }
         }

         if (flashRow >= 0 && flash > 0.0) {
            c.rect(5.0, 6 + flashRow * 7, 70.0, 7.0, AnimatedCapes.alpha(-1, flash * 0.8));
            c.glow(40.0, 6 + flashRow * 7 + 3.5, 30.0, -1, flash * 0.6);
         }
      }
   }

   static final class LanternFestival implements AnimatedCapes.Scene {
      private static final int RIVER = 96;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.7}, new int[]{-16447458, -14017984});

         for (int i = 0; i < 90; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 70.0, 0.0, -1512193, 0.1 + AnimatedCapes.hash(i, 3) * 0.35);
         }

         AnimatedCapes.mountains(c, 71, 84.0, 22.0, 0.05, -15462876, 0);
         c.fillBelow(xx -> 92 - (AnimatedCapes.hash((int)(xx / 5.0), 3) > 0.5 ? 6 : 3) - (xx > 30.0 && xx < 38.0 ? 8 : 0), -16119790);

         for (int i = 0; i < 26; i++) {
            c.rect(
               Math.floor(AnimatedCapes.hash(i, 5) * 80.0),
               92.0 - AnimatedCapes.hash(i, 6) * 8.0,
               1.0,
               1.0,
               AnimatedCapes.alpha(-14224, 0.6 + AnimatedCapes.hash(i, 7) * 0.4)
            );
         }

         for (int y = 96; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-15068104, -16382962, (y - 96) / 32.0), x, y));
            }
         }
      }

      private static void lantern(AnimatedCapes.Canvas c, double x, double y, double s, double sway, double flicker) {
         c.glow(x, y, 7.0 * s, -30160, 0.5);
         c.polygon(
            new double[][]{{x - 2.4 * s + sway, y - 3.0 * s}, {x + 2.4 * s + sway, y - 3.0 * s}, {x + 1.8 * s, y + 3.0 * s}, {x - 1.8 * s, y + 3.0 * s}},
            AnimatedCapes.lerp(-34262, -12176, flicker)
         );
         c.rect(x - 1.8 * s, y + 2.4 * s, 3.6 * s, Math.max(0.6, 0.6 * s), -5223910);
         c.disc(x + sway * 0.3, y + 1.0 * s, 0.8 * s, -1840);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int i = 0; i < 24; i++) {
            double depth = 0.35 + AnimatedCapes.hash(i, 10) * 0.65;
            double life = AnimatedCapes.wrap(t * (0.02 + depth * 0.03) + AnimatedCapes.hash(i, 11), 1.0);
            double x = AnimatedCapes.hash(i, 12) * 80.0 + Math.sin(t * 0.5 + i) * 3.0 * depth + life * 10.0 * (AnimatedCapes.hash(i, 13) - 0.5);
            double y = 90.0 - life * 110.0 * (0.6 + depth * 0.4);
            double sway = Math.sin(t * 1.5 + i) * 0.5 * depth;
            double flicker = AnimatedCapes.noise(t * 4.0 + i, i);
            double s = 0.4 + depth * 0.9;
            lantern(c, x, y, s, sway, flicker);
            double ry = 96.0 + (96.0 - y) * 0.25;
            if (ry < 128.0) {
               c.ellipse(x + Math.sin(t * 2.0 + i) * 0.8, ry, 1.6 * s, 0.6 * s, 0.0, AnimatedCapes.alpha(-20400, 0.3 * (1.0 - life)));
            }
         }

         for (int k = 0; k < 5; k++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(k, 20) * 80.0 + t * 2.0, 90.0) - 5.0;
            double y = 104 + k * 5;
            c.glow(x, y, 4.0, -24512, 0.4);
            c.polygon(new double[][]{{x - 2.0, y}, {x + 2.0, y}, {x + 1.2, y - 2.5}, {x - 1.2, y - 2.5}}, -16288);
            c.rect(x - 2.5, y, 5.0, 1.0, -9815526);
         }

         for (int y = 97; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (AnimatedCapes.noise(x * 0.4 + t * 0.3, y * 0.9 - t * 0.4) > 0.72) {
                  c.add(x, y, -20384, 0.08);
               }
            }
         }
      }
   }

   static final class RetroTv implements AnimatedCapes.Scene {
      private static final double SX = 16.0;
      private static final double SY = 46.0;
      private static final double SW = 48.0;
      private static final double SH = 38.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               boolean stripe = x / 6 % 2 == 0;
               int paper = stripe ? -9794966 : -10847654;
               if ((x + 3) % 12 == 0 && (y + 4) % 16 < 3) {
                  paper = -4677536;
               }

               c.set(x, y, AnimatedCapes.shade(paper, 0.55));
            }
         }

         c.vignette(0.5);
         c.rect(0.0, 112.0, 80.0, 16.0, -12965348);
         c.rect(0.0, 112.0, 80.0, 1.0, -10862544);
         c.polygon(
            new double[][]{{8.0, 36.0}, {72.0, 36.0}, {72.0, 104.0}, {8.0, 104.0}},
            (xx, y) -> AnimatedCapes.lerp(-7710160, -10864098, (xx - 8) / 64.0 + AnimatedCapes.fbm(xx * 0.1, y * 0.5, 2) * 0.2)
         );
         c.rect(8.0, 36.0, 64.0, 1.5, -5211576);
         c.rect(12.0, 104.0, 4.0, 8.0, -12966892);
         c.rect(64.0, 104.0, 4.0, 8.0, -12966892);
         c.polygon(new double[][]{{13.0, 43.0}, {67.0, 43.0}, {67.0, 87.0}, {13.0, 87.0}}, -14013906);
         c.rect(18.0, 90.0, 44.0, 10.0, -11915240);

         for (int k = 0; k < 2; k++) {
            c.disc(56 - k * 10, 95.0, 3.0, -4671312);
            c.disc(56 - k * 10, 95.0, 1.6, -9803162);
         }

         for (int k = 0; k < 8; k++) {
            c.rect(20 + k * 3, 92.0, 2.0, 6.0, -14018034);
         }

         c.ellipse(40.0, 36.0, 6.0, 2.0, 0.0, -14013906);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double tilt = Math.sin(t * 0.8) * 0.1;
         c.line(40.0, 35.0, 40.0 - 20.0 * Math.cos(0.9 + tilt), 35.0 - 30.0 * Math.sin(0.9 + tilt), 0.6, -3618616);
         c.line(40.0, 35.0, 40.0 + 22.0 * Math.cos(1.0 - tilt), 35.0 - 30.0 * Math.sin(1.0 - tilt), 0.6, -3618616);
         int channel = (int)Math.floor(t / 3.5) % 3;
         double sinceSwitch = AnimatedCapes.wrap(t, 3.5);
         double knob = channel * 1.2 + (sinceSwitch < 0.2 ? sinceSwitch * 5.0 : 0.0);
         c.line(56.0, 95.0, 56.0 + Math.cos(knob) * 2.5, 95.0 + Math.sin(knob) * 2.5, 0.6, -14013910);
         int[] bars = new int[]{-1513240, -1515456, -12525344, -12525504, -2080544, -2080704, -12566304};

         for (int y = 46; y < 84.0; y++) {
            for (int x = 16; x < 64.0; x++) {
               double u = (x - 16.0) / 48.0;
               double v = (y - 46.0) / 38.0;
               int col;
               if (sinceSwitch < 0.25) {
                  col = AnimatedCapes.hash(x, y + (int)(t * 60.0)) > 0.5 ? -2565928 : -14671840;
               } else if (channel == 0) {
                  col = v < 0.7 ? bars[(int)(u * 7.0)] : (v < 0.8 ? bars[6 - (int)(u * 7.0)] : ((int)(u * 5.0) % 2 == 0 ? -15724480 : -1513240));
               } else if (channel == 1) {
                  int grey = (int)(AnimatedCapes.hash(x * 3, y * 7 + (int)(t * 30.0)) * 220.0);
                  col = 0xFF000000 | grey << 16 | grey << 8 | grey;
               } else {
                  double ground = 0.72 + Math.sin(u * 6.0) * 0.03;
                  col = v > ground ? -12935104 : AnimatedCapes.lerp(-9778960, -2559745, v);
                  double bounce = Math.abs(Math.sin(t * 3.0));
                  double bx = AnimatedCapes.wrap(t * 0.25, 1.0);
                  double by = ground - 0.08 - bounce * 0.45;
                  if (Math.hypot((u - bx) * 48.0, (v - by) * 38.0) < 4.0) {
                     col = -1557958;
                  }

                  if (Math.hypot((u - 0.8) * 48.0, (v - 0.15) * 38.0) < 4.0) {
                     col = -8096;
                  }
               }

               double edge = Math.max(Math.abs(u - 0.5), Math.abs(v - 0.5)) * 2.0;
               col = AnimatedCapes.shade(col, (y % 2 == 0 ? 0.85 : 1.0) * (1.0 - Math.pow(edge, 6.0) * 0.6));
               c.set(x, y, col);
            }
         }

         double roll = AnimatedCapes.wrap(t * 20.0, 48.0) - 5.0;
         c.rect(16.0, 46.0 + roll, 48.0, 3.0, AnimatedCapes.alpha(-1, 0.08));
         c.ellipse(28.0, 54.0, 10.0, 4.0, -0.3, AnimatedCapes.alpha(-1, 0.12));
         c.glow(40.0, 65.0, 40.0, channel == 0 ? -5197569 : (channel == 1 ? -4144960 : -8335105), 0.12);
      }
   }

   static final class Sculk implements AnimatedCapes.Scene {
      private static final int B = 8;
      private boolean[] veins;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int bx = x / 8;
               int by = y / 8;
               boolean sculk = AnimatedCapes.fbm(bx * 0.4, by * 0.3, 3) > 0.45 || by >= 12;
               int col;
               if (sculk) {
                  col = AnimatedCapes.lerp(-16378852, -15981520, AnimatedCapes.hash(x / 2 + bx * 7, y / 2 + by * 5));
                  if (AnimatedCapes.hash(x, y) > 0.97) {
                     col = -15054248;
                  }
               } else {
                  col = AnimatedCapes.lerp(-14013904, -12961214, AnimatedCapes.hash(x / 2 * 3 + bx, y / 2 * 5 + by));
                  if ((x + y * 2) % 8 == 0) {
                     col = AnimatedCapes.shade(col, 0.8);
                  }
               }

               if (x % 8 == 0 || y % 8 == 0) {
                  col = AnimatedCapes.shade(col, 0.8);
               }

               c.set(x, y, col);
            }
         }

         for (int x = 0; x < 80; x++) {
            for (int y = 0; y < 14; y++) {
               c.set(x, y, AnimatedCapes.lerp(-15066590, -14013900, AnimatedCapes.hash(x / 4, y / 3)));
               if (y % 7 == 0 || (x + y / 7 * 5) % 10 == 0) {
                  c.set(x, y, -15724518);
               }
            }
         }

         this.veins = new boolean[10240];

         for (int yx = 14; yx < 128; yx++) {
            for (int x = 0; x < 80; x++) {
               double v = AnimatedCapes.fbm(x * 0.06, yx * 0.06 + 4.0, 3);
               double v2 = AnimatedCapes.fbm(x * 0.09 + 20.0, yx * 0.05, 3);
               if (Math.abs(v - 0.5) < 0.022 || Math.abs(v2 - 0.5) < 0.016) {
                  this.veins[yx * 80 + x] = true;
                  c.set(x, yx, -15977408);
               }
            }
         }

         c.vignette(0.5);
         c.rect(12.0, 88.0, 16.0, 8.0, -15062480);
         c.rect(12.0, 88.0, 16.0, 2.0, -2566976);
         c.rect(14.0, 80.0, 12.0, 8.0, -15984096);

         for (int k = 0; k < 4; k++) {
            c.rect(14 + k * 3, 78.0, 1.5, 3.0, -2566976);
         }

         c.rect(54.0, 98.0, 12.0, 6.0, -15981520);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double sensorX = 60.0;
         double sensorY = 98.0;
         double wave = AnimatedCapes.wrap(t * 12.0, 90.0);

         for (int y = 14; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double d = Math.hypot(x - sensorX, y - sensorY);
               double pulse = Math.exp(-Math.pow((d - wave) / 6.0, 2.0));
               if (this.veins[y * 80 + x]) {
                  c.add(x, y, -13965088, 0.2 + pulse * 0.9);
               } else if (AnimatedCapes.hash(x / 2, y / 2) > 0.93) {
                  c.add(x, y, -13965088, 0.08 + pulse * 0.35);
               }
            }
         }

         for (int k = 0; k < 5; k++) {
            double a = (-Math.PI / 2) + (k - 2) * 0.35;
            double wiggle = Math.sin(t * 3.0 + k) * 0.2;
            double len = 8 + k % 2 * 3;
            double ex = sensorX + Math.cos(a + wiggle) * len;
            double ey = sensorY + Math.sin(a + wiggle) * len;
            c.line(sensorX, sensorY, ex, ey, 0.8, -15975864);
            c.glow(ex, ey, 2.5, -13965088, 0.5 + 0.5 * Math.max(0.0, Math.sin(t * 4.0 - k)));
         }

         double shriek = AnimatedCapes.wrap(t, 5.0);
         if (shriek < 2.5) {
            for (int r = 0; r < 3; r++) {
               double p = shriek / 2.5 - r * 0.15;
               if (p > 0.0 && p < 1.0) {
                  c.ring(20.0, 78.0, p * 40.0, 1.4, AnimatedCapes.alpha(-12914448, 0.7 * (1.0 - p)));
               }
            }

            c.glow(20.0, 82.0, 8.0, -12914448, 0.6 * (1.0 - shriek / 2.5));
         }

         c.glow(70.0, 20.0, 10.0, -12920577, 0.5 + 0.1 * AnimatedCapes.noise(t * 3.0, 1.0));
         c.rect(67.0, 16.0, 6.0, 8.0, -14013904);
         c.rect(68.0, 18.0, 4.0, 4.0, -7669505);

         for (int i = 0; i < 18; i++) {
            double life = AnimatedCapes.wrap(t * 0.12 + AnimatedCapes.hash(i, 30), 1.0);
            double xx = AnimatedCapes.hash(i, 31) * 80.0 + Math.sin(life * 6.0 + i) * 4.0;
            double y = 120.0 - life * 110.0;
            c.glow(xx, y, 1.5, -9768705, 0.6 * Math.sin(life * Math.PI));
         }
      }
   }

   static final class Snake implements AnimatedCapes.Scene {
      private static final int COLS = 16;
      private static final int ROWS = 24;
      private static final int CELL = 5;
      private static final int OY = 8;
      private static final int[][] CYCLE = buildCycle();
      private static final int SPACING = 29;

      private static int[][] buildCycle() {
         int[][] path = new int[384][];
         int i = 0;

         for (int x = 0; x < 16; x++) {
            path[i++] = new int[]{x, 0};
         }

         for (int y = 1; y < 24; y++) {
            if (y % 2 == 1) {
               for (int x = 15; x >= 1; x--) {
                  path[i++] = new int[]{x, y};
               }
            } else {
               for (int x = 1; x < 16; x++) {
                  path[i++] = new int[]{x, y};
               }
            }
         }

         for (int yx = 23; yx >= 1; yx--) {
            path[i++] = new int[]{0, yx};
         }

         return path;
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.fill(-15853040);

         for (int y = 0; y < 24; y++) {
            for (int x = 0; x < 16; x++) {
               c.rect(x * 5, 8 + y * 5, 5.0, 5.0, (x + y) % 2 == 0 ? -7681968 : -8734138);
            }
         }

         c.rect(0.0, 0.0, 80.0, 8.0, -14005730);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double steps = t * 9.0 + 420.0;
         double gameLength = 986.0;
         double inGame = AnimatedCapes.wrap(steps, gameLength);
         long head = (long)Math.floor(inGame);
         double part = inGame - head;
         int eaten = (int)(head / 29L);
         int length = 4 + eaten * 3;
         int n = CYCLE.length;
         long apple = (eaten + 1L) * 29L;
         int[] ap = CYCLE[(int)(apple % n)];
         double ax = ap[0] * 5 + 2.5;
         double ay = 8 + ap[1] * 5 + 2.5;
         c.disc(ax, ay + 0.3, 2.1, -2084822);
         c.disc(ax - 0.7, ay - 0.4, 0.6, -25968);
         c.line(ax, ay - 2.0, ax + 1.0, ay - 3.2, 0.5, -12944870);

         for (int s = length - 1; s >= 0; s--) {
            long index = head - s;
            if (index >= 0L) {
               int[] cell = CYCLE[(int)(index % n)];
               int[] next = CYCLE[(int)((index + 1L) % n)];
               double f = s == 0 ? part : 0.0;
               double x = cell[0] * 5 + (next[0] - cell[0]) * 5 * f;
               double y = 8 + cell[1] * 5 + (next[1] - cell[1]) * 5 * f;
               int col = AnimatedCapes.lerp(-14001440, -15062390, (double)s / Math.max(1, length));
               c.rect(x + 0.5, y + 0.5, 4.0, 4.0, col);
               if (s > 0) {
                  int[] ahead = CYCLE[(int)((index + 1L) % n)];
                  c.rect(
                     Math.min(cell[0], ahead[0]) * 5 + 0.5 + (cell[0] == ahead[0] ? 0 : 2),
                     8 + Math.min(cell[1], ahead[1]) * 5 + 0.5 + (cell[1] == ahead[1] ? 0 : 2),
                     4.0,
                     4.0,
                     col
                  );
               }

               if (s == 0) {
                  int dx = next[0] - cell[0];
                  int dy = next[1] - cell[1];
                  double cx = x + 2.5;
                  double cy = y + 2.5;

                  for (int side = -1; side <= 1; side += 2) {
                     double ex = cx + dx * 0.8 - dy * side * 1.2;
                     double ey = cy + dy * 0.8 + dx * side * 1.2;
                     c.rect(ex - 0.5, ey - 0.5, 1.2, 1.2, -1);
                  }

                  if (AnimatedCapes.wrap(t * 2.0, 1.0) < 0.3) {
                     c.line(cx + dx * 2.5, cy + dy * 2.5, cx + dx * 4, cy + dy * 4, 0.4, -2084822);
                  }
               }
            }
         }

         int score = eaten * 10;
         int[] digits = new int[]{31599, 11415, 29671, 29647, 23497, 31183, 31215, 29257, 31727, 31695};

         for (int d = 0; d < 3; d++) {
            int digit = score / (int)Math.pow(10.0, 2 - d) % 10;

            for (int bit = 0; bit < 15; bit++) {
               if ((digits[digit] >> 14 - bit & 1) != 0) {
                  c.set(4 + d * 4 + bit % 3, 2 + bit / 3, -1);
               }
            }
         }

         c.disc(70.0, 4.0, 2.0, -2084822);
      }
   }

   static final class StreetLamp implements AnimatedCapes.Scene {
      private static final double LX = 30.0;
      private static final double LY = 30.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.8}, new int[]{-16381938, -15591906});

         for (int y = 0; y < 104; y++) {
            int row = y / 5;

            for (int x = 52; x < 80; x++) {
               boolean mortar = y % 5 == 0 || (x + row % 2 * 4) % 8 == 0;
               c.set(x, y, mortar ? -15462386 : AnimatedCapes.lerp(-12967392, -11916762, AnimatedCapes.hash((x + row % 2 * 4) / 8, row)));
            }
         }

         c.rect(58.0, 60.0, 14.0, 22.0, -16118768);
         c.rect(59.0, 61.0, 12.0, 20.0, -15064528);
         c.line(65.0, 61.0, 65.0, 81.0, 0.6, -16118768);

         for (int y = 104; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int stone = AnimatedCapes.lerp(-14803420, -14013904, AnimatedCapes.hash(x / 6 + y / 4 % 2 * 3, y / 4));
               if (y % 4 == 0 || (x + y / 4 % 2 * 3) % 6 == 0) {
                  stone = -15724524;
               }

               c.set(x, y, stone);
            }
         }

         c.ellipse(26.0, 116.0, 14.0, 3.0, 0.0, -15722458);
         c.ellipse(62.0, 122.0, 10.0, 2.2, 0.0, -15722458);
         c.rect(28.5, 38.0, 3.0, 76.0, -15460838);
         c.rect(27.0, 100.0, 6.0, 4.0, -15460838);
         c.line(30.0, 38.0, 30.0, 34.0, 1.2, -15460838);
         c.polygon(new double[][]{{24.0, 29.0}, {36.0, 29.0}, {30.0, 24.0}}, -15460838);
         c.polygon(new double[][]{{25.0, 29.0}, {35.0, 29.0}, {33.5, 36.0}, {26.5, 36.0}}, -15066592);
         c.rect(4.0, 94.0, 18.0, 2.0, -12965346);
         c.rect(4.0, 90.0, 18.0, 1.5, -12965346);
         c.rect(5.0, 96.0, 1.2, 8.0, -15460838);
         c.rect(20.0, 96.0, 1.2, 8.0, -15460838);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         boolean flickerOff = AnimatedCapes.hash((int)Math.floor(t * 10.0), 3) > 0.96;
         double on = flickerOff ? 0.25 : 1.0;
         c.polygon(
            new double[][]{{26.5, 35.0}, {33.5, 35.0}, {60.0, 116.0}, {0.0, 116.0}},
            (x, y) -> AnimatedCapes.alpha(-10096, 0.1 * on * (1.0 - (y - 30.0) / 110.0))
         );
         c.glow(30.0, 32.0, 30.0, -20400, 0.45 * on);
         c.polygon(new double[][]{{26.0, 30.0}, {34.0, 30.0}, {33.0, 35.0}, {27.0, 35.0}}, AnimatedCapes.lerp(-9807302, -5984, on));
         c.glow(26.0, 116.0, 10.0, -16288, 0.35 * on);
         c.ellipse(26.0 + Math.sin(t * 2.0) * 0.8, 116.0, 2.0, 0.6, 0.0, AnimatedCapes.alpha(-8032, 0.8 * on));

         for (int i = 0; i < 140; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 10) * 128.0 + t * (70.0 + AnimatedCapes.hash(i, 11) * 20.0), 136.0) - 4.0;
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 12) * 80.0 - t * 10.0, 80.0);
            double dx = Math.abs(x - 30.0) - (y - 30.0) * 0.27;
            boolean lit = y > 30.0 && dx < 4.0;
            c.line(x, y, x - 0.8, y + 4.0, 0.4, AnimatedCapes.alpha(lit ? -5968 : -9799024, (lit ? 0.7 : 0.25) * (lit ? on : 1.0)));
         }

         for (int k = 0; k < 4; k++) {
            double life = AnimatedCapes.wrap(t * 1.3 + k * 0.25, 1.0);
            int n = (int)Math.floor(t * 1.3 + k * 0.25);
            double px = 14.0 + AnimatedCapes.hash(k, n) * 24.0;
            double py = 114.0 + AnimatedCapes.hash(k, n + 1) * 5.0;
            c.ellipse(px, py, life * 4.0, life * 1.2, 0.0, AnimatedCapes.alpha(-7693648, 0.5 * (1.0 - life)));
         }

         for (int m = 0; m < 3; m++) {
            double a = t * (4 + m) + m * 2;
            double mx = 30.0 + Math.cos(a) * (5 + m * 2);
            double my = 32.0 + Math.sin(a * 1.3) * (3 + m);
            c.rect(mx, my, 1.0, 1.0, AnimatedCapes.alpha(-1515312, 0.8 * on));
         }

         double walk = AnimatedCapes.wrap(t * 0.07, 1.0);
         double wx = 90.0 - walk * 110.0;
         double step = Math.sin(t * 5.0);
         int ink = -16382198;
         c.line(wx - 1.0, 104.0, wx - 1.0 + step * 1.5, 112.0, 1.1, ink);
         c.line(wx + 1.0, 104.0, wx + 1.0 - step * 1.5, 112.0, 1.1, ink);
         c.polygon(new double[][]{{wx - 3.0, 104.0}, {wx + 3.0, 104.0}, {wx + 2.0, 92.0}, {wx - 2.0, 92.0}}, ink);
         c.disc(wx, 89.5, 2.2, ink);
         c.line(wx + 1.0, 94.0, wx + 1.0, 80.0, 0.4, ink);
         c.polygon(new double[][]{{wx - 9.0, 82.0}, {wx + 11.0, 82.0}, {wx + 1.0, 76.0}}, -7726550);
         c.line(wx - 9.0, 82.0, wx + 11.0, 82.0, 0.4, -10876396);
      }
   }

   static final class VinylRecord implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 58.0;
      private static final double R = 34.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double grain = Math.sin(y * 0.9 + AnimatedCapes.fbm(x * 0.03, y * 0.3, 3) * 8.0);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-10864094, -8760780, 0.5 + 0.25 * grain), x, y));
            }
         }

         c.rect(4.0, 100.0, 72.0, 24.0, -14013906);
         c.rect(4.0, 100.0, 72.0, 1.0, -11908528);
         c.disc(42.0, 61.0, 37.0, AnimatedCapes.alpha(-16777216, 0.4));
         c.disc(40.0, 58.0, 36.0, -12961216);
         c.ring(40.0, 58.0, 35.5, 0.8, -7697774);
         c.polygon(new double[][]{{50.0, 110.0}, {76.0, 110.0}, {76.0, 128.0}, {46.0, 128.0}}, -2056128);
         c.disc(62.0, 120.0, 5.0, -14017990);
         c.ring(62.0, 120.0, 7.0, 0.6, -8032);
         c.disc(70.0, 18.0, 5.0, -9803152);
         c.disc(70.0, 18.0, 3.0, -5723984);

         for (int k = 0; k < 3; k++) {
            c.disc(12 + k * 8, 110.0, 2.5, -15066594);
            c.disc(12 + k * 8, 110.0, 1.0, -7697774);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double spin = t * 3.5;

         for (int y = 23; y <= 93.0; y++) {
            for (int x = 5; x <= 75.0; x++) {
               double dx = x + 0.5 - 40.0;
               double dy = y + 0.5 - 58.0;
               double d = Math.hypot(dx, dy);
               if (!(d > 34.0)) {
                  double a = Math.atan2(dy, dx);
                  int col;
                  if (d < 11.0) {
                     double la = a - spin;
                     col = d < 1.2 ? -15066598 : AnimatedCapes.lerp(-1557926, -5232054, 0.5 + 0.5 * Math.sin(la * 2.0));
                     if (d > 4.0 && d < 9.0 && Math.abs(Math.sin(la * 3.0)) < 0.15) {
                        col = -464688;
                     }

                     if (Math.abs(d - 10.2) < 0.5) {
                        col = -464688;
                     }
                  } else {
                     double groove = 0.5 + 0.5 * Math.sin(d * 3.2);
                     boolean gap = Math.abs(d - 20.0) < 0.8 || Math.abs(d - 27.0) < 0.8;
                     col = AnimatedCapes.lerp(-16119284, -14935006, groove * (gap ? 0.2 : 1.0));
                     double sheen = Math.pow(Math.max(0.0, Math.cos(2.0 * (a + 0.8))), 12.0);
                     col = AnimatedCapes.lerp(col, -7697766, sheen * 0.5 * (d / 34.0));
                     double wobble = Math.pow(Math.max(0.0, Math.cos(a - spin)), 40.0) * 0.08;
                     col = AnimatedCapes.lerp(col, -10855830, wobble);
                  }

                  c.set(x, y, col);
               }
            }
         }

         double armA = 2.15 + Math.sin(t * 0.05) * 0.05;
         double tipX = 40.0 + Math.cos(armA - Math.PI) * 24.0 + 6.0;
         double tipY = 58.0 + Math.sin(armA - Math.PI) * 24.0 + 18.0 + Math.sin(t * 20.0) * 0.2;
         c.line(70.0, 18.0, 72.0, 60.0, 1.6, -3618608);
         c.line(72.0, 60.0, tipX + 4.0, tipY + 2.0, 1.4, -3618608);
         c.rect(tipX - 1.0, tipY - 1.5, 6.0, 3.5, -14013906);
         c.rect(67.0, 12.0, 6.0, 3.0, -12961216);
         double beat = Math.pow(Math.max(0.0, Math.sin(t * 5.2)), 6.0);
         c.glow(66.0, 110.0, 3.0, -12910742, 0.5 + beat * 0.5);

         for (int k = 0; k < 16; k++) {
            double a = spin * 0.4 + k * Math.PI / 8.0;
            c.rect(40.0 + Math.cos(a) * 35.2 - 0.4, 58.0 + Math.sin(a) * 35.2 - 0.4, 0.8, 0.8, -1513232);
         }

         for (int nte = 0; nte < 5; nte++) {
            double life = AnimatedCapes.wrap(t * 0.25 + nte / 5.0, 1.0);
            double xx = 18 + nte * 10 + Math.sin(life * 6.0 + nte) * 5.0;
            double y = 96.0 - life * 90.0;
            int col = AnimatedCapes.alpha(AnimatedCapes.hsv(nte * 60 + 300, 0.5, 1.0), Math.sin(life * Math.PI));
            c.ellipse(xx, y, 1.6, 1.2, -0.4, col);
            c.line(xx + 1.4, y, xx + 1.4, y - 6.0, 0.5, col);
            if (nte % 2 == 0) {
               c.line(xx + 1.4, y - 6.0, xx + 4.0, y - 5.0, 0.8, col);
            }
         }
      }
   }
}
