package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.gui.HudEditorScreen;
import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardModule extends HudModule {
   private static final int MAX_ROWS = 15;
   private static final int ROW_H = 9;
   private static final int TITLE_H = 9;
   private static final int TITLE_BLOCK = 10;
   private static final Comparator<PlayerScoreEntry> ORDER = Comparator.comparing(PlayerScoreEntry::value)
      .reversed()
      .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
   private final BooleanSetting show = this.register(new BooleanSetting("Show Scoreboard", true));
   private final EnumSetting<ScoreboardModule.Position> position = this.register(new EnumSetting<>("Position", ScoreboardModule.Position.VANILLA));
   private final BooleanSetting showTitle = this.register(new BooleanSetting("Show Title", true));
   private final ColorSetting titleColor = this.register(new ColorSetting("Title Color", -1));
   private final ColorSetting textColor = this.register(new ColorSetting("Text Color", -1));
   private final BooleanSetting showNumbers = this.register(new BooleanSetting("Show Numbers", true));
   private final ColorSetting numberColor = this.register(new ColorSetting("Number Color", -43691));
   private final BooleanSetting shadow = this.register(new BooleanSetting("Text Shadow", false));
   private final BooleanSetting background = this.register(new BooleanSetting("Background", true));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", 1275068416));
   private static final int TITLE_STRIP_STEP = 26;

   public ScoreboardModule() {
      super("Scoreboard", "Takes over the server's sidebar so you can resize it, recolor it or hide it.", false, 4.0, 60.0);
      this.show.group = "Scoreboard";
      this.position.group = "Scoreboard";
      this.showTitle.group = "Text";
      this.titleColor.group = "Text";
      this.textColor.group = "Text";
      this.showNumbers.group = "Text";
      this.numberColor.group = "Text";
      this.shadow.group = "Text";
      this.background.group = "Background";
      this.backgroundColor.group = "Background";
   }

   public static ScoreboardModule get() {
      return ModuleManager.get("Scoreboard") instanceof ScoreboardModule scoreboard ? scoreboard : null;
   }

   public boolean takesOver() {
      return this.isEnabled();
   }

   @Override
   protected void pushTransform(GuiGraphics context) {
      if (this.position.get() != ScoreboardModule.Position.VANILLA) {
         super.pushTransform(context);
      } else {
         float scale = (float)this.scale.get().doubleValue();
         List<ScoreboardModule.Row> rows = this.rows();
         context.pose().pushMatrix();
         context.pose().translate(this.vanillaX(context, rows, scale), this.vanillaY(context, rows));
         context.pose().scale(scale);
      }
   }

   private float vanillaX(GuiGraphics context, List<ScoreboardModule.Row> rows, float scale) {
      return context.guiWidth() - 1 - this.panelWidth(rows) * scale;
   }

   private float vanillaY(GuiGraphics context, List<ScoreboardModule.Row> rows) {
      int count = rows.size();
      return context.guiHeight() / 2 + count * 9 / 3 - count * 9 - (this.showTitle.get() ? 10 : 0);
   }

   @Override
   public void render(GuiGraphics context) {
      if (this.show.get()) {
         Minecraft mc = Minecraft.getInstance();
         List<ScoreboardModule.Row> rows = this.rows();
         if (rows.isEmpty()) {
            if (mc.screen instanceof HudEditorScreen) {
               context.drawString(mc.font, "Scoreboard", 2, 1, this.titleColor.get(), this.shadow.get());
            }
         } else {
            int width = this.panelWidth(rows);
            int top = this.showTitle.get() ? 10 : 0;
            int height = top + rows.size() * 9;
            if (this.background.get()) {
               int body = this.backgroundColor.get();
               if (this.showTitle.get()) {
                  context.fill(0, 0, width, 9, titleStrip(body));
               }

               context.fill(0, this.showTitle.get() ? 9 : 0, width, height, body);
            }

            if (this.showTitle.get()) {
               Component title = this.objective().getDisplayName();
               int titleWidth = mc.font.width(title);
               context.drawString(mc.font, title, 2 + (width - 4) / 2 - titleWidth / 2, 1, this.titleColor.get(), this.shadow.get());
            }

            for (int i = 0; i < rows.size(); i++) {
               ScoreboardModule.Row row = rows.get(i);
               int y = top + i * 9;
               context.drawString(mc.font, row.name(), 2, y, this.textColor.get(), this.shadow.get());
               if (this.showNumbers.get()) {
                  if (row.plainScore() == null) {
                     context.drawString(mc.font, row.score(), width - row.scoreWidth(), y, this.textColor.get(), this.shadow.get());
                  } else {
                     context.drawString(mc.font, row.plainScore(), width - row.scoreWidth(), y, this.numberColor.get(), this.shadow.get());
                  }
               }
            }
         }
      }
   }

   private static int titleStrip(int body) {
      return Math.min(255, (body >>> 24) + 26) << 24 | body & 16777215;
   }

   private int panelWidth(List<ScoreboardModule.Row> rows) {
      Minecraft mc = Minecraft.getInstance();
      Objective objective = this.objective();
      int widest = objective != null && this.showTitle.get() ? mc.font.width(objective.getDisplayName()) : 0;
      int gap = mc.font.width(": ");

      for (ScoreboardModule.Row row : rows) {
         int score = this.showNumbers.get() && row.scoreWidth() > 0 ? gap + row.scoreWidth() : 0;
         widest = Math.max(widest, mc.font.width(row.name()) + score);
      }

      return widest + 4;
   }

   private Objective objective() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null) {
         Scoreboard scoreboard = mc.level.getScoreboard();
         PlayerTeam team = scoreboard.getPlayersTeam(mc.player.getScoreboardName());
         if (team != null) {
            DisplaySlot slot = DisplaySlot.teamColorToSlot(team.getColor());
            if (slot != null) {
               Objective forTeam = scoreboard.getDisplayObjective(slot);
               if (forTeam != null) {
                  return forTeam;
               }
            }
         }

         return scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
      } else {
         return null;
      }
   }

   private List<ScoreboardModule.Row> rows() {
      Minecraft mc = Minecraft.getInstance();
      Objective objective = this.objective();
      List<ScoreboardModule.Row> out = new ArrayList<>();
      if (objective == null) {
         return out;
      } else {
         Scoreboard scoreboard = objective.getScoreboard();
         NumberFormat format = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
         boolean serverFormatted = objective.numberFormat() != null;
         scoreboard.listPlayerScores(objective).stream().filter(entry -> !entry.isHidden()).sorted(ORDER).limit(15L).forEach(entry -> {
            Component name = PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(entry.owner()), entry.ownerName());
            Component score = entry.formatValue(format);
            boolean styled = serverFormatted || entry.numberFormatOverride() != null;
            String plain = styled ? null : String.valueOf(entry.value());
            int width = mc.font.width(plain == null ? score.getString() : plain);
            out.add(new ScoreboardModule.Row(name, score, plain, width));
         });
         return out;
      }
   }

   @Override
   protected int contentWidth() {
      return this.panelWidth(this.rows());
   }

   @Override
   protected int contentHeight() {
      return (this.showTitle.get() ? 10 : 0) + Math.max(1, this.rows().size()) * 9;
   }

   public static enum Position {
      VANILLA("Vanilla"),
      CUSTOM("Custom");

      public final String title;

      private Position(String title) {
         this.title = title;
      }
   }

   private record Row(Component name, Component score, String plainScore, int scoreWidth) {
   }
}
