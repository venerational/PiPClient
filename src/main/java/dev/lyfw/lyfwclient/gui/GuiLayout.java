package dev.lyfw.lyfwclient.gui;

import net.minecraft.client.gui.screens.Screen;

public enum GuiLayout {
   CARROT("Carrot's GUI", "A full-screen panel with wide rows, your name and version in the header, and room for every description.") {
      @Override
      public Screen create() {
         return new CarrotClickGuiScreen();
      }
   };

   public final String title;
   public final String description;

   private GuiLayout(String title, String description) {
      this.title = title;
      this.description = description;
   }

   public abstract Screen create();
}
