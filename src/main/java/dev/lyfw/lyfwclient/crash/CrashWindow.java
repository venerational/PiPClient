package dev.lyfw.lyfwclient.crash;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Desktop.Action;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public final class CrashWindow {
   private static final Color BG = new Color(1184536);
   private static final Color BORDER = new Color(2895416);
   private static final Color ROW = new Color(1842468);
   private static final Color TEXT = new Color(15922167);
   private static final Color MUTED = new Color(10396083);
   private static final Color FAINT = new Color(7040640);
   private static final Color BUTTON = new Color(1710882);
   private static final Color BUTTON_HOVER = new Color(2500659);
   private static final Color BUTTON_BORDER = new Color(3158588);
   private static final Color PRIMARY_BORDER = new Color(15132912);
   private static final Color ACCENT_A = new Color(8150271);
   private static final Color ACCENT_B = new Color(12610815);

   private CrashWindow() {
   }

   public static void main(String[] args) throws Exception {
      if (args.length >= 3 && args[0].equals("--render")) {
         render(CrashSummary.read(Path.of(args[1])), Path.of(args[2]));
      } else if (args.length >= 1) {
         Path file = Path.of(args[0]);
         CrashSummary summary = CrashSummary.read(file);
         SwingUtilities.invokeLater(() -> show(summary, file));
      }
   }

   private static void render(CrashSummary summary, Path out) throws Exception {
      CrashWindow.Card card = new CrashWindow.Card(summary, null);
      Dimension size = card.getPreferredSize();
      int scale = 2;
      BufferedImage image = new BufferedImage(size.width * scale, size.height * scale, 2);
      Graphics2D g = image.createGraphics();
      g.scale(scale, scale);
      card.paintCard(g, true);
      g.dispose();
      ImageIO.write(image, "png", out.toFile());
   }

   private static void show(CrashSummary summary, Path summaryFile) {
      JFrame frame = new JFrame("Pip Client - Minecraft crashed");
      frame.setUndecorated(true);
      CrashWindow.Card card = new CrashWindow.Card(summary, frame);
      GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      boolean translucent = screen.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
      if (translucent) {
         frame.setBackground(new Color(0, 0, 0, 0));
      }

      card.setOpaque(!translucent);
      frame.setContentPane(card);
      frame.pack();
      if (!translucent) {
         frame.setShape(new Double(0.0, 0.0, frame.getWidth(), frame.getHeight(), 18.0, 18.0));
      }

      frame.setIconImage(logo(64));
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(3);
      frame.getRootPane().registerKeyboardAction(e -> System.exit(0), KeyStroke.getKeyStroke(27, 0), 2);
      frame.setAlwaysOnTop(true);
      frame.setVisible(true);
      frame.toFront();
      Timer settle = new Timer(1500, e -> frame.setAlwaysOnTop(false));
      settle.setRepeats(false);
      settle.start();

      try {
         Files.deleteIfExists(summaryFile.resolveSibling("pip-crash-pending"));
      } catch (Exception var8) {
      }
   }

   private static BufferedImage logo(int size) {
      BufferedImage image = new BufferedImage(size, size, 2);
      Graphics2D g = image.createGraphics();
      paintLogo(g, 0, 0, size);
      g.dispose();
      return image;
   }

   private static void paintLogo(Graphics2D g, int x, int y, int size) {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setPaint(new GradientPaint(x, y, ACCENT_A, x + size, y + size, ACCENT_B));
      g.fill(new Double(x, y, size, size, size * 0.52, size * 0.52));
      g.setColor(Color.WHITE);
      g.setFont(new Font("Segoe UI", 1, Math.round(size * 0.58F)));
      FontMetrics fm = g.getFontMetrics();
      g.drawString("P", x + (size - fm.stringWidth("P")) / 2, y + (size - fm.getHeight()) / 2 + fm.getAscent());
   }

   static final class Card extends JComponent {
      private static final int WIDTH = 460;
      private static final int PAD = 22;
      private static final int LOGO = 36;
      private static final String[] LABELS = new String[]{"Open mods folder", "Open crash report", "Close"};
      private final CrashSummary summary;
      private final JFrame frame;
      private final Font titleFont = new Font("Segoe UI", 1, 17);
      private final Font bodyFont = new Font("Segoe UI", 0, 13);
      private final Font rowFont = new Font("Segoe UI", 0, 14);
      private final Font smallFont = new Font("Segoe UI", 0, 12);
      private final Font monoFont = new Font("Consolas", 0, 11);
      private final Font buttonFont = new Font("Segoe UI", 0, 13);
      private final Rectangle[] buttons = new Rectangle[LABELS.length];
      private int hovered = -1;
      private Point dragFrom;

      Card(CrashSummary summary, JFrame frame) {
         this.summary = summary;
         this.frame = frame;
         MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
               int over = Card.this.buttonAt(e.getPoint());
               if (over != Card.this.hovered) {
                  Card.this.hovered = over;
                  Card.this.setCursor(Cursor.getPredefinedCursor(over >= 0 ? 12 : 0));
                  Card.this.repaint();
               }
            }

            @Override
            public void mouseExited(MouseEvent e) {
               this.mouseMoved(new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), 0, -1, -1, 0, false));
            }

            @Override
            public void mousePressed(MouseEvent e) {
               Card.this.dragFrom = Card.this.buttonAt(e.getPoint()) < 0 ? e.getPoint() : null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
               if (Card.this.dragFrom != null && Card.this.frame != null) {
                  Point on = e.getLocationOnScreen();
                  Card.this.frame.setLocation(on.x - Card.this.dragFrom.x, on.y - Card.this.dragFrom.y);
               }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
               int button = Card.this.buttonAt(e.getPoint());
               if (button >= 0 && Card.this.dragFrom == null) {
                  Card.this.press(button);
               }

               Card.this.dragFrom = null;
            }
         };
         this.addMouseListener(mouse);
         this.addMouseMotionListener(mouse);
      }

      private int buttonAt(Point point) {
         for (int i = 0; i < this.buttons.length; i++) {
            if (this.buttons[i] != null && this.buttons[i].contains(point)) {
               return i;
            }
         }

         return -1;
      }

      private void press(int button) {
         switch (button) {
            case 0:
               open(this.summary.modsDir);
               break;
            case 1:
               open(this.summary.reportPath);
               break;
            default:
               System.exit(0);
         }
      }

      private static void open(String path) {
         if (path != null && !path.isEmpty()) {
            File file = new File(path);

            try {
               if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.OPEN)) {
                  Desktop.getDesktop().open(file);
                  return;
               }
            } catch (Exception var4) {
            }

            try {
               new ProcessBuilder("explorer", file.getAbsolutePath()).start();
            } catch (Exception var3) {
            }
         }
      }

      @Override
      public Dimension getPreferredSize() {
         Graphics2D probe = new BufferedImage(1, 1, 2).createGraphics();
         int height = this.paintCard(probe, false);
         probe.dispose();
         return new Dimension(460, height);
      }

      @Override
      protected void paintComponent(Graphics graphics) {
         Graphics2D g = (Graphics2D)graphics.create();
         this.paintCard(g, true);
         g.dispose();
      }

      int paintCard(Graphics2D g, boolean draw) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         int height = draw ? this.paintCard(g, false) : 0;
         if (draw) {
            g.setColor(CrashWindow.BORDER);
            g.fill(new Double(0.0, 0.0, 460.0, height, 18.0, 18.0));
            g.setColor(CrashWindow.BG);
            g.fill(new Double(1.0, 1.0, 458.0, height - 2, 16.0, 16.0));
            CrashWindow.paintLogo(g, 22, 22, 36);
         }

         int textX = 72;
         int textW = 438 - textX;
         int y = 21;
         y = this.text(g, draw, this.titleFont, CrashWindow.TEXT, this.summary.title, textX, y, textW, 22);
         y = this.text(g, draw, this.bodyFont, CrashWindow.MUTED, this.summary.subtitle, textX, y + 1, textW, 18);
         y = Math.max(y, 58) + 16;

         for (CrashSummary.Suspect suspect : this.summary.suspects) {
            if (draw) {
               g.setColor(CrashWindow.ROW);
               g.fill(new Double(22.0, y, 416.0, 36.0, 10.0, 10.0));
               g.setFont(this.smallFont);
               FontMetrics idMetrics = g.getFontMetrics();
               int idW = idMetrics.stringWidth(suspect.id());
               g.setColor(CrashWindow.FAINT);
               g.drawString(suspect.id(), 424 - idW, y + 18 + idMetrics.getAscent() / 2 - 1);
               g.setFont(this.rowFont);
               FontMetrics nameMetrics = g.getFontMetrics();
               g.setColor(CrashWindow.TEXT);
               g.drawString(fit(suspect.name(), nameMetrics, 388 - idW - 12), 36, y + 18 + nameMetrics.getAscent() / 2 - 1);
            }

            y += 42;
         }

         if (!this.summary.suspects.isEmpty()) {
            y += 2;
         }

         y = this.text(g, draw, this.bodyFont, CrashWindow.MUTED, this.summary.advice, 22, y, 416, 18);
         if (!this.summary.detail.isEmpty()) {
            y = this.text(g, draw, this.monoFont, CrashWindow.FAINT, this.summary.detail, 22, y + 4, 416, 15);
         }

         y += 14;
         int gap = 8;
         int buttonW = (416 - gap * 2) / 3;

         for (int i = 0; i < LABELS.length; i++) {
            Rectangle bounds = new Rectangle(22 + i * (buttonW + gap), y, buttonW, 38);
            this.buttons[i] = bounds;
            if (draw) {
               boolean primary = i == LABELS.length - 1;
               boolean over = i == this.hovered;
               g.setColor(over ? CrashWindow.BUTTON_HOVER : CrashWindow.BUTTON);
               g.fill(new Double(bounds.x, bounds.y, bounds.width, bounds.height, 12.0, 12.0));
               g.setColor(primary ? CrashWindow.PRIMARY_BORDER : CrashWindow.BUTTON_BORDER);
               g.setStroke(new BasicStroke(primary ? 1.6F : 1.0F));
               g.draw(new Double(bounds.x + 0.5, bounds.y + 0.5, bounds.width - 1, bounds.height - 1, 12.0, 12.0));
               g.setFont(this.buttonFont);
               FontMetrics fm = g.getFontMetrics();
               String label = fit(LABELS[i], fm, bounds.width - 12);
               g.setColor(CrashWindow.TEXT);
               g.drawString(label, bounds.x + (bounds.width - fm.stringWidth(label)) / 2, bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent());
            }
         }

         return y + 38 + 22;
      }

      private int text(Graphics2D g, boolean draw, Font font, Color color, String text, int x, int y, int width, int lineHeight) {
         if (text != null && !text.isEmpty()) {
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            g.setColor(color);

            for (String line : wrap(text, fm, width)) {
               if (draw) {
                  g.drawString(line, x, y + fm.getAscent());
               }

               y += lineHeight;
            }

            return y;
         } else {
            return y;
         }
      }

      private static List<String> wrap(String text, FontMetrics fm, int width) {
         List<String> lines = new ArrayList<>();

         for (String paragraph : text.split("\n")) {
            StringBuilder line = new StringBuilder();

            for (String word : paragraph.split(" ")) {
               String attempt = line.isEmpty() ? word : line + " " + word;
               if (fm.stringWidth(attempt) > width && !line.isEmpty()) {
                  lines.add(line.toString());
                  line.setLength(0);
                  line.append(fit(word, fm, width));
               } else {
                  line.setLength(0);
                  line.append(fit(attempt, fm, width));
               }
            }

            lines.add(line.toString());
         }

         return lines;
      }

      private static String fit(String text, FontMetrics fm, int width) {
         if (fm.stringWidth(text) <= width) {
            return text;
         } else {
            String trimmed = text;

            while (trimmed.length() > 1 && fm.stringWidth(trimmed + "…") > width) {
               trimmed = trimmed.substring(0, trimmed.length() - 1);
            }

            return trimmed + "…";
         }
      }
   }
}
