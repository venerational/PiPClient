package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.accounts.PipAccounts;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import ru.vidtu.ias.IAS;
import ru.vidtu.ias.account.MicrosoftAccount;
import ru.vidtu.ias.auth.handlers.CreateHandler;
import ru.vidtu.ias.auth.microsoft.MSAuthClient;
import ru.vidtu.ias.auth.microsoft.MSAuthServer;
import ru.vidtu.ias.config.IASConfig;
import ru.vidtu.ias.crypt.HardwareCrypt;
import ru.vidtu.ias.platform.IStonecutter;

public class AccountSignInScreen extends Screen implements CreateHandler {
   private static final int PANEL_W = 300;
   private static final int PANEL_H = 150;
   private static final int PAD = 12;
   private final Screen parent;
   private volatile String message = "Starting...";
   private volatile String link = "";
   private volatile String code = "";
   private volatile boolean finished;
   private volatile boolean closed;
   private MSAuthClient authClient;
   private MSAuthServer authServer;

   public AccountSignInScreen(Screen parent) {
      super(Component.literal("Add account"));
      this.parent = parent;
   }

   protected void init() {
      if (this.authClient == null && this.authServer == null && !this.finished) {
         this.start();
      }
   }

   private void start() {
      Minecraft mc = Minecraft.getInstance();

      try {
         if (IASConfig.useServerAuth()) {
            this.authServer = new MSAuthServer("Signed in - you can close this tab and go back to Minecraft.", HardwareCrypt.INSTANCE_V2, this);
            CompletableFuture.runAsync(this.authServer::run, IAS.executor()).thenRunAsync(() -> {
               this.link = this.authServer.authUrl();
               IStonecutter.openUrl(this.link);
               this.message = "Sign in to Microsoft in the browser tab that just opened, then come back here.";
            }, mc).exceptionally(t -> {
               this.error(t);
               return null;
            });
         } else {
            this.authClient = new MSAuthClient(HardwareCrypt.INSTANCE_V2, this);
            this.authClient.start().thenAcceptAsync(auth -> {
               this.link = auth.uri().toString();
               this.code = auth.user();
               IStonecutter.openUrl(this.link);
               mc.keyboardHandler.setClipboard(this.code);
               this.message = "Enter this code on the Microsoft page that just opened. It is already copied.";
            }, mc).exceptionally(t -> {
               this.error(t);
               return null;
            });
         }
      } catch (Throwable var3) {
         this.error(var3);
      }
   }

   public boolean cancelled() {
      return this.closed;
   }

   public void stage(String key, Object... args) {
      if (!this.link.isEmpty() && !key.startsWith("ias.login.link")) {
         this.message = "Signing in...";
      }
   }

   public void success(MicrosoftAccount account) {
      Minecraft mc = Minecraft.getInstance();
      mc.execute(() -> {
         this.finished = true;
         PipAccounts.add(account);
         this.onClose();
      });
   }

   public void error(Throwable t) {
      Minecraft.getInstance().execute(() -> {
         this.finished = true;
         this.message = PipAccounts.describe(t);
      });
   }

   public void removed() {
      this.closed = true;

      try {
         if (this.authClient != null) {
            this.authClient.close();
         }

         if (this.authServer != null) {
            this.authServer.close();
         }
      } catch (Throwable var2) {
      }
   }

   private int panelX() {
      return (this.width - 300) / 2;
   }

   private int panelY() {
      return (this.height - 150) / 2;
   }

   private int[] openBounds() {
      return new int[]{this.panelX() + 12, this.panelY() + 150 - 12 - 20, 84, 20};
   }

   private int[] copyBounds() {
      return new int[]{this.panelX() + 12 + 90, this.panelY() + 150 - 12 - 20, 84, 20};
   }

   private int[] cancelBounds() {
      return new int[]{this.panelX() + 300 - 12 - 84, this.panelY() + 150 - 12 - 20, 84, 20};
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      int x = this.panelX();
      int y = this.panelY();
      context.fill(0, 0, this.width, this.height, Theme.scrim());
      ThemeRenderer.fillRounded(context, x + 3, y + 4, 300, 150, Theme.shadow(), 10);
      ThemeRenderer.panel(context, x, y, 300, 150, Theme.border(), Theme.panelBg(), 10);
      context.drawString(this.font, "ADD ACCOUNT", x + 12, y + 12, Theme.textPrimary());
      List<FormattedCharSequence> lines = this.font.split(Component.literal(this.message), 276);
      int lineY = y + 12 + 18;

      for (FormattedCharSequence line : lines) {
         context.drawString(this.font, line, x + 12, lineY, Theme.textSecondary());
         lineY += 11;
      }

      if (!this.code.isEmpty()) {
         context.pose().pushMatrix();
         context.pose().translate(x + 150.0F, lineY + 8);
         context.pose().scale(2.0F);
         context.drawCenteredString(this.font, this.code, 0, 0, Theme.accent());
         context.pose().popMatrix();
      }

      if (!this.link.isEmpty()) {
         this.button(context, this.openBounds(), "Open page", mouseX, mouseY);
      }

      if (!this.code.isEmpty()) {
         this.button(context, this.copyBounds(), "Copy code", mouseX, mouseY);
      }

      this.button(context, this.cancelBounds(), this.finished ? "Close" : "Cancel", mouseX, mouseY);
   }

   private void button(GuiGraphics context, int[] b, String label, int mouseX, int mouseY) {
      boolean hovered = mouseX >= b[0] && mouseX < b[0] + b[2] && mouseY >= b[1] && mouseY < b[1] + b[3];
      ThemeRenderer.row(context, b[0], b[1], b[2], b[3], hovered, Theme.trackBg(), Theme.rowBgHover(), 6);
      context.drawCenteredString(this.font, label, b[0] + b[2] / 2, b[1] + 6, ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary()));
   }

   private static boolean inside(int mx, int my, int[] b) {
      return mx >= b[0] && mx < b[0] + b[2] && my >= b[1] && my < b[1] + b[3];
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      int mx = (int)click.x();
      int my = (int)click.y();
      if (inside(mx, my, this.cancelBounds())) {
         this.onClose();
         return true;
      } else if (!this.link.isEmpty() && inside(mx, my, this.openBounds())) {
         IStonecutter.openUrl(this.link);
         return true;
      } else if (!this.code.isEmpty() && inside(mx, my, this.copyBounds())) {
         Minecraft.getInstance().keyboardHandler.setClipboard(this.code);
         return true;
      } else {
         return super.mouseClicked(click, doubled);
      }
   }

   public boolean keyPressed(KeyEvent input) {
      if (input.key() == 256) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(input);
      }
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }
}
