package dev.lyfw.lyfwclient.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
   private static final List<Module> MODULES = new ArrayList<>();

   public static void register(Module module) {
      MODULES.add(module);
   }

   public static List<Module> all() {
      return MODULES;
   }

   public static List<Module> byCategory(Category category) {
      List<Module> list = new ArrayList<>();

      for (Module m : MODULES) {
         if (!m.hidden) {
            boolean matches = switch (category) {
               case ALL -> true;
               case ACTIVE -> m.isEnabled();
               case FAVORITE -> m.favorite;
               default -> m.category == category;
            };
            if (matches) {
               list.add(m);
            }
         }
      }

      return list;
   }

   public static Module get(String name) {
      for (Module m : MODULES) {
         if (m.name.equalsIgnoreCase(name)) {
            return m;
         }
      }

      return null;
   }

   public static void initAll() {
      for (Module m : MODULES) {
         m.init();
      }
   }

   public static void tick() {
      for (Module m : MODULES) {
         m.tick();
      }
   }
}
