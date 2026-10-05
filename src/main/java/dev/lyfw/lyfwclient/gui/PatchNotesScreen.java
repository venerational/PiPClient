package dev.lyfw.lyfwclient.gui;

import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class PatchNotesScreen extends Screen {
   private static final int PANEL_W = 460;
   private static final int PAD = 14;
   private static final int HEADER_H = 42;
   private static final int LINE_H = 11;
   private static final int RELEASE_GAP = 8;
   private final Screen parent;
   private int panelX;
   private int panelY;
   private final Anim open = new Anim(0.2F);
   private static final int DROP = 20;
   private int panelH;
   private final Scroll scroll = new Scroll();
   private static final List<PatchNotesScreen.Release> RELEASES = List.of(
      new PatchNotesScreen.Release(
         "2.6.1",
         "19 Sept 2026",
         List.of(
            "Emotes: every emote in the wardrobe has a + Wheel button - click it to put that emote on your emote wheel, click again to take it off. Up to 12. Until you have picked any, the wheel still fills itself from the emotes you have starred."
         )
      ),
      new PatchNotesScreen.Release(
         "2.6.0",
         "19 Sept 2026",
         List.of(
            "New module: Health Ring, in HUD. Your health as a ring round the crosshair - the arc shrinks as you take damage and runs from green through yellow to red. Radius, thickness, a faint track for what is missing, and your own colour if you would rather.",
            "New module: Crystal Customizer. End crystals in any colour and any opacity, and a glow - lit up wherever they are, with a soft halo of their colour round them.",
            "New module: Anchor Customizer. Respawn anchors in any colour and any opacity, with the same glow. Works with Sodium.",
            "Death Animation: choose how long a dead player stays on screen, from 0 to 5 seconds, and switch the fall-over on or off. 0 makes them vanish the moment they die; past a second the body is kept on your screen for the full time. Players Only leaves mobs dying the normal way. Changes apply straight away.",
            "Skybox: new Image tab - put a PNG or an animated GIF round the sky, from a file in your config folder or a link, the same way Mouse Tracker takes its background. Panoramas fit best. Opacity slider included.",
            "Armor Tint is now Item Glint, with Armor and Items tabs. Glint strength for armour and for items (0-300%), your own glint colour on items, and Glint Everything to put the shimmer on every item, block and piece of armour. Your Armor Tint colour carries over.",
            "Block Colors: new Saturation tab - search any block, add it, and set how much colour it keeps, 0% fully grey to 100% untouched, like a saturation slider in a photo editor. Works with Sodium."
         )
      ),
      new PatchNotesScreen.Release(
         "2.5.1", "19 Sept 2026", List.of("Removed the Gold trail. If you were wearing it, you now have no trail on - pick another in the Trails tab.")
      ),
      new PatchNotesScreen.Release(
         "2.5.0",
         "18 Sept 2026",
         List.of(
            "100 trails. Alongside the fourteen from 2.4.0 there are 86 new ones, and no two look alike: eighteen new shapes - waves, zigzags, braids, twin rails, neon tubes, smoke, drips, lacing, serpents, spines, glass shards, frost, meteors, afterimages, orbits and more - and fresh takes on the originals.",
            "Trails now come out of both feet: two strands, one from each foot, just off the ground.",
            "Colour can run along a trail now - gradients, rainbows, pulses racing back from your feet, flickers and slow shimmers - and some trails have a soft glow under them.",
            "Every trail is one continuous line. The ones made of sparks, rings or petals have a faint thread joining them up, so they read as a trail rather than loose bits."
         )
      ),
      new PatchNotesScreen.Release(
         "2.4.0",
         "18 Sept 2026",
         List.of(
            "Trails are now a cosmetic: pick one in the wardrobe's new Trails tab and it follows you, like your wings and cape. The separate Trails module is gone.",
            "All-new look, matching Aurora's trails - fourteen of them, from Void's twisting strands to Gold's ribbon of beads, each with its own colour, width and length, and most in five colourways. Click the trail you are wearing to pick its colourway.",
            "Trails fade out by age, so one shrinks away behind you when you stop. The wardrobe figures show each trail streaming off their back.",
            "Other Pip Client players see the trail you are wearing, and you see theirs. Your own trail is hidden in first person."
         )
      ),
      new PatchNotesScreen.Release(
         "2.3.5",
         "18 Sept 2026",
         List.of(
            "Player Glow's Fill now colours the player themselves - their skin and armour are tinted with it - instead of a see-through box laid over them, so every mark on the skin still shows through the colour.",
            "Fill starts at 50%; the glow line round the edge is unchanged."
         )
      ),
      new PatchNotesScreen.Release(
         "2.3.4",
         "18 Sept 2026",
         List.of(
            "Actually fixed the elytra vanishing under an animated cape. The game falls back to the cape's picture for an elytra with no texture of its own, so the fix in 2.3.3 still left it invisible - under an animated, colour or drawn cape the elytra now always gets the normal elytra texture."
         )
      ),
      new PatchNotesScreen.Release(
         "2.3.3",
         "18 Sept 2026",
         List.of(
            "Fixed animated capes wiping out the elytra's texture. An animated, colour or drawn cape now leaves your elytra looking like an elytra; a Minecraft cape still gives it that cape's matching elytra, the way the game does.",
            "Fixed emotes tearing your skin apart: the outer layer of the skin - hat, jacket, sleeves and trouser legs - was being moved twice and floated off the arms, legs and head. It now stays on them through every emote.",
            "Crouching now stops an emote, like moving, jumping or swinging does."
         )
      ),
      new PatchNotesScreen.Release(
         "2.3.2",
         "18 Sept 2026",
         List.of(
            "Player Glow wraps round armour: the colour covers helmets, chestplates, leggings and boots, and the line runs round the outside of them instead of being hidden behind them."
         )
      ),
      new PatchNotesScreen.Release(
         "2.3.1",
         "18 Sept 2026",
         List.of(
            "Player Glow redone to look like cpvp.gg's: a see-through colour over the player, so their skin still shows, with a bright line round their edge.",
            "Fixed Player Glow showing through walls and trees - it is drawn with the world now, so anything in front of a player hides their glow along with them.",
            "Fixed Player Glow coming out as a solid block of colour with the player hidden inside it.",
            "New Outline and Outline Width settings for the line round the edge, which stays the same width near or far; Fill sets how strong the colour over the body is."
         )
      ),
      new PatchNotesScreen.Release(
         "2.3.0",
         "18 Sept 2026",
         List.of(
            "New emote wheel: hold V (rebindable in Minecraft's own controls), point at an emote and let go. It fills itself with the emotes you have starred in the Wardrobe, and the one you are pointing at is shown in the middle being done.",
            "Fixed emotes that threw their arms and legs out of place - anything that turns the whole body over, like a bow, a handstand or a backflip, now turns the limbs with it instead of leaving them behind.",
            "Lying down, press-ups, sit-ups and the worm sit on top of the floor instead of half through it, bows and leans bend at the waist with the legs staying put, and a handstand now stands on its hands.",
            "Player Glow no longer shows through blocks: it is drawn as part of the player, so a wall in front of someone hides their glow as well. It fills the body in with the colour and glows round the edge, rather than only tracing an outline.",
            "New Fill, Glow and Glow Size sliders for how solid that colour and its glow are."
         )
      ),
      new PatchNotesScreen.Release(
         "2.2.0",
         "18 Sept 2026",
         List.of(
            "New Player Glow module (Render) - a glowing outline round players, like the one on cpvp.gg.",
            "The colour runs through the spectrum on its own, at whatever speed you like, or switch that off and pick a colour of your own.",
            "Different Per Player gives everyone their own colour, so you can tell people apart at a glance.",
            "Show On picks whether it is everyone else, everyone including you, or only you, and Range can keep it to players nearby."
         )
      ),
      new PatchNotesScreen.Release(
         "2.1.0",
         "18 Sept 2026",
         List.of(
            "New Emotes tab in the Wardrobe with 123 emotes - waves, bows, dances, laughing, sitting, press-ups, backflips and plenty of daft ones.",
            "Pick one and press B to do it; press it again, or move, and you stand back up. The key can be changed in Minecraft's own controls.",
            "Every tile in the tab does its own emote, so you can see what each one looks like before you pick it.",
            "Pets walk themselves now instead of sliding about with you: each one keeps its own place in the world, trails you, turns to face the way it is going, runs when it falls behind and looks back at you while it waits.",
            "A pet's legs run off how far it has actually walked, so they move when it moves and stop when it stops."
         )
      ),
      new PatchNotesScreen.Release(
         "2.0.0",
         "18 Sept 2026",
         List.of(
            "New Effect Timers module (HUD) - a timer for your potion effects in the corner, counting down as they run out.",
            "Three looks: Lunar's list with the icon, name and countdown; a bar per effect that empties as it runs out; or the vanilla icons with the time written under them.",
            "The last few seconds flash, and effects are listed soonest-to-run-out first so the one about to drop is on top.",
            "The vanilla icons in the top right are hidden while it is on, so you are not reading two of the same thing - there is a switch to keep them.",
            "Settings for the colour (each potion's own colour or one you pick), the name, the level, the background, the flashing, and hiding beacon effects."
         )
      ),
      new PatchNotesScreen.Release(
         "1.99.0",
         "17 Sept 2026",
         List.of(
            "Fixed pets sinking into the ground and flickering while they roll over - they now roll over around the middle of their body and stay on top of the ground.",
            "The piglet rolls in a mud puddle that spreads under it and splashes up as it flops over.",
            "New Pet tricks switch in the Wardrobe's Options tab - turn it off and pets just walk with you.",
            "Roars look real now - a breath in, then head thrust forward, jaw wide with fangs, a slow sweep and a tremble. Only the lions, tigers, bears, T-rex and dragon roar.",
            "Wolves and huskies howl at the sky, the hippo gives a big yawn and the crocodile snaps its jaws.",
            "The penguin flops onto its belly and slides around on the ground.",
            "All pets are cute babies now, with bigger heads and eyes and shorter legs.",
            "Removed the foal, donkey, unicorn, fawn, reindeer, moose, giraffe, zebra, camel, llama, alpaca, flamingo and ostrich.",
            "New pets: quokka, chinchilla, hamster, chipmunk, badger, tanuki, platypus, kiwi, budgie, narwhal, seahorse, starfish and caterpillar."
         )
      ),
      new PatchNotesScreen.Release(
         "1.98.0",
         "17 Sept 2026",
         List.of(
            "The hippo looks like a hippo: a big wide muzzle with nostrils on top, eyes peeking over it, tiny ears and a round barrel body.",
            "The rhino, boar, capybara and beaver were redrawn so they no longer look like dogs or bears - heads carried low, armour folds on the rhino, a bristly back on the boar, a blunt block face on the capybara, and tiny ears and whiskers on the beaver.",
            "Pet tricks move like real animals: rolls settle down, tip onto the back with legs kicking and wriggle before getting up; jumps crouch and pounce; roars rear back then lunge; dogs play-bow before barking; tail-chasers scrabble round twice; bunnies do a twisting binky, whales breach and skunks do a handstand."
         )
      ),
      new PatchNotesScreen.Release(
         "1.97.0",
         "17 Sept 2026",
         List.of(
            "Every pet has its own trick every few seconds: dogs bark, dinosaurs and big cats roar, otters and pandas roll, the hamster spins, bunnies flip, horses rear up, birds flap and peck, fish swim a lap, the pufferfish puffs up and the crab scuttles sideways.",
            "The Misc tab is now All, with every module, and the old All tab is now Active Mods - just the modules you have switched on.",
            "Fixed Profile Presets: loading a profile now switches modules on and off to match it straight away, instead of leaving modules on from the last profile."
         )
      ),
      new PatchNotesScreen.Release(
         "1.96.0",
         "17 Sept 2026",
         List.of(
            "100 pets! Foxes, pandas, lions, elephants, unicorns, penguins, owls, parrots, dinosaurs, a baby dragon, turtles, bees, jellyfish, sharks and many more - all Pip's own designs.",
            "Recolour any pet: switch off Natural colors and pick a colour. Markings and bellies follow along, so a black cat still looks like a cat.",
            "Choose where each pet goes: walking on the ground beside you, sitting on your shoulder, or riding on your head. Pets on your head stack up."
         )
      ),
      new PatchNotesScreen.Release(
         "1.95.0",
         "17 Sept 2026",
         List.of(
            "Pets are all Pip's own designs now: a golden puppy with a red collar, an orange tabby kitten, a baby goat, a fluffy lamb and a caramel bunny, alongside the hamster ball.",
            "Each pet moves its own way - the puppy wags its tail, ears flop as they walk, the kitten's tail sways and the bunny hops.",
            "New module: Superman Flying. Fly with an elytra and your arms stretch out in front of you, like Superman."
         )
      ),
      new PatchNotesScreen.Release(
         "1.94.0",
         "17 Sept 2026",
         List.of(
            "Pets! A new Pets tab on the Cosmetics page with a baby goat, a cat, a dog, a baby sheep, a hamster in a rolling ball and a bunny.",
            "Pets walk beside you and keep up when you move. Put on as many as you like and they line up in a little pack either side of you.",
            "Each pet has its own Size, X angle and Y angle, and other Pip Client players see your pets too."
         )
      ),
      new PatchNotesScreen.Release(
         "1.93.0",
         "17 Sept 2026",
         List.of(
            "Fixed the game crashing when Pip and another mod that changes how armour is drawn, like Visual-Tweaks, were installed together. Transparent Players now shares that code with the other mod instead of fighting over it.",
            "Totem Tweaks shares the totem pop's spin the same way, so a mod that changes it too can no longer stop the game starting."
         )
      ),
      new PatchNotesScreen.Release(
         "1.92.0",
         "17 Sept 2026",
         List.of(
            "Other Pip Client players now see your cosmetics, and you see theirs - headwear, wings and cape, with their colours, sizes and angles.",
            "Wear as many headwear and wings as you like at once. Click one to put it on, click a worn one to pick it, and Take off removes just that one.",
            "Size is per cosmetic now: resizing your crown leaves your cat ears alone.",
            "Every headwear and wing has its own X angle and Y angle sliders to tilt and turn it.",
            "The Testing tab is now All, holding every module. The New and Updated tabs are gone - modules show a New or Updated badge beside their name instead.",
            "This screen shows the version you are really running again, and has every release since 1.66.0."
         )
      ),
      new PatchNotesScreen.Release(
         "1.91.0", "17 Sept 2026", List.of("A search bar on the Cosmetics page finds headwear, wings and capes by name, or by typing headwear, wings or cape.")
      ),
      new PatchNotesScreen.Release("1.90.0", "17 Sept 2026", List.of("100 headwear, 100 wings and 100 animated capes.")),
      new PatchNotesScreen.Release("1.89.0", "16 Sept 2026", List.of("40 animated capes, all redrawn in detail.")),
      new PatchNotesScreen.Release(
         "1.88.0",
         "16 Sept 2026",
         List.of(
            "Animated capes rebuilt as detailed moving scenes.",
            "A spin button on the Cosmetics preview turns the spinning player on or off.",
            "The Cape tab opens on Animated capes."
         )
      ),
      new PatchNotesScreen.Release("1.87.0", "15 Sept 2026", List.of("Animated capes. The Cape tab is split into Minecraft, Animated and All.")),
      new PatchNotesScreen.Release(
         "1.86.0",
         "15 Sept 2026",
         List.of(
            "Name Protect's Change Skin says it couldn't load the skin and to try again when the player exists but Mojang is slow, instead of saying there is no player by that name."
         )
      ),
      new PatchNotesScreen.Release(
         "1.85.0", "15 Sept 2026", List.of("Every cosmetic has a favourite star, and starred ones collect under Favorites.", "40 more headwear and wings.")
      ),
      new PatchNotesScreen.Release(
         "1.84.0",
         "15 Sept 2026",
         List.of("10 more headwear and 10 more wings.", "Fixes for the Minecraft GUI theme: module cards, toggles and the favourite star.")
      ),
      new PatchNotesScreen.Release(
         "1.83.0",
         "15 Sept 2026",
         List.of(
            "When the game crashes, a window explains why - including which mod caused it.",
            "Headwear can use natural colours. 22 new headwear and 4 new wings."
         )
      ),
      new PatchNotesScreen.Release("1.82.0", "15 Sept 2026", List.of("More wing types, and wings can use their natural colours until you change them.")),
      new PatchNotesScreen.Release(
         "1.81.0", "15 Sept 2026", List.of("The ClickGUI is rebuilt, with a full Cosmetics page showing animated 3D previews and each cosmetic's settings.")
      ),
      new PatchNotesScreen.Release(
         "1.80.0", "14 Sept 2026", List.of("Fixed a crash from Block Colors asking for grass, leaf and water colours before a world was loaded.")
      ),
      new PatchNotesScreen.Release("1.79.0", "14 Sept 2026", List.of("Cosmetics has a Show On setting: everyone, or just you.")),
      new PatchNotesScreen.Release("1.78.0", "14 Sept 2026", List.of("Wings join together on your back while still spreading out.")),
      new PatchNotesScreen.Release("1.77.0", "14 Sept 2026", List.of("In-Game Account Switcher comes with Pip, so everyone can switch accounts from the GUI.")),
      new PatchNotesScreen.Release("1.76.0", "14 Sept 2026", List.of("Your name and skin sit in the GUI. Click them to open the account menu.")),
      new PatchNotesScreen.Release(
         "1.75.0", "14 Sept 2026", List.of("Cape Style opens a picker showing every Minecraft cape with a picture.", "Wings moved higher up the back.")
      ),
      new PatchNotesScreen.Release(
         "1.74.0", "14 Sept 2026", List.of("Cosmetics lock onto your model. Wings recreated, plus Minecraft capes and a draw-your-own cape.")
      ),
      new PatchNotesScreen.Release(
         "1.73.0", "14 Sept 2026", List.of("Type a username into Name Protect to wear their skin.", "Hide Inventory Player is now a Name Protect setting.")
      ),
      new PatchNotesScreen.Release("1.72.0", "14 Sept 2026", List.of("Your protected name changes to the player whose skin Random picked.")),
      new PatchNotesScreen.Release(
         "1.71.0", "14 Sept 2026", List.of("Random says \"No players to pick from\" instead of hanging when there is nobody to pick.")
      ),
      new PatchNotesScreen.Release("1.70.0", "14 Sept 2026", List.of("The inventory player preview can be fully blank.", "Name Protect's nametag is centred.")),
      new PatchNotesScreen.Release(
         "1.69.0", "14 Sept 2026", List.of("Name Protect has Change Skin, with a Random button that gives you a random player's skin.")
      ),
      new PatchNotesScreen.Release(
         "1.68.0", "13 Sept 2026", List.of("Optimizers turns Marlow's Crystal Optimizer, Client Side Crystals and Hero's Anchor Optimizer on and off.")
      ),
      new PatchNotesScreen.Release(
         "1.67.0",
         "13 Sept 2026",
         List.of("A Public Profiles panel on the Leaderboard screen.", "The On Pip Client title moves with the panel when you drag it.")
      ),
      new PatchNotesScreen.Release(
         "1.66.0",
         "13 Sept 2026",
         List.of(
            "Profiles can be shared. Hit Public on a saved profile in Profile Presets and anyone on Pip can find it under Public Profiles and copy it.",
            "A share carries Pip's settings, your options and your keybinds - nothing else. Your name, background images, music folder, server addresses, playtime and every other mod's config stay on your machine.",
            "Copying files it under Profile Presets and stops there. Loading a shared profile merges it into what you have instead of replacing it, so your own name, background, resource packs, sound device and other mods' settings are left alone.",
            "Shared profiles are checked again when they arrive, so one carrying a web address or a Name Protect name cannot plant either on your client.",
            "Unshare takes yours back down. Sharing needs the database opened up for it first - until then Public Profiles says so plainly rather than failing quietly."
         )
      ),
      new PatchNotesScreen.Release(
         "1.65.0",
         "13 Sept 2026",
         List.of(
            "The Leaderboard and the On Pip Client panels can be dragged by their headers, and each keeps its own corner - so you can pull them apart and put them where you want.",
            "Where you leave them is remembered across restarts. Drag one somewhere silly and it still cannot take its own back button off the edge with it.",
            "Buttons win over the drag, so Options, Refresh and back all still work from the header."
         )
      ),
      new PatchNotesScreen.Release(
         "1.64.0",
         "13 Sept 2026",
         List.of(
            "The ClickGUI reopens on the tab you had open last instead of dropping you back on Render every time.",
            "It survives closing the game too, and it is remembered by name - so adding a tab later will not shuffle you onto a different one."
         )
      ),
      new PatchNotesScreen.Release(
         "1.63.0",
         "13 Sept 2026",
         List.of(
            "No Hand Sway now removes the swing and the equip dip as well, both on by default. The hand holds completely still - checked frame against frame, a swing and a resting shot come out pixel-identical.",
            "The two switches that were already there did work. Sway is only the tilt while the arm catches up to the camera, so it was never going to stop the arm crossing your screen when you click - that is the swing, which is a separate animation and much the bigger of the two.",
            "Only your own first-person copy. Everyone else still sees you swing, and so does your own third-person view.",
            "Worth knowing: with the swing off there is no longer a visual tell that a click registered. It is a switch, so turn it back on if you miss it."
         )
      ),
      new PatchNotesScreen.Release(
         "1.62.0",
         "13 Sept 2026",
         List.of(
            "Block Colors can recolor snow. One switch covers the snow layer and the snow block, since one becomes the other as it deepens and nobody wants them different colors.",
            "That is eight models, not one - the layer has a separate model per depth, and a miss in any of them would show as white patches in a snowy field. All eight checked at once.",
            "A block with snow on top keeps its vanilla snow-capped side. That face belongs to the grass or podzol underneath, not to the snow, and vanilla gives it no tint of its own."
         )
      ),
      new PatchNotesScreen.Release(
         "1.61.0",
         "13 Sept 2026",
         List.of(
            "Pink Fog is now called Custom Fog. Same module, same settings - it does any colour and has for a long time, so the name was the last thing still calling it pink.",
            "Your existing Pink Fog settings carry across on their own. Nothing to redo.",
            "Skybox has lost its own Custom Fog switch. It was a flat colour replacement, and Custom Fog already did the job properly - keeping the brightness, pulling the fog in close, and handling water and lava.",
            "Skybox keeps sky, clouds and stars."
         )
      ),
      new PatchNotesScreen.Release(
         "1.60.1",
         "12 Sept 2026",
         List.of(
            "Fixed Totem Transparency rendering as a giant magenta and black block. The four opacity packs pointed their model at item/smaller_util, which is not a vanilla model - it came from whatever pack they were originally written against.",
            "A model with a parent that does not exist falls back to the missing-model cube, which is what you were holding. They point at item/generated now and the totem fades the way it should."
         )
      ),
      new PatchNotesScreen.Release(
         "1.60.0",
         "12 Sept 2026",
         List.of(
            "New tab: Testing, sitting between Misc and New. Everything below lives in it, and all of it is off by default.",
            "Totem Transparency - fades the totem in your hand so it stops covering a corner of the screen. The packs for this had shipped since forever and nothing ever switched them on.",
            "No Hand Sway - stops the held item tilting when you turn. Hand bobbing is a separate switch, so you can drop it and keep the camera's, which the vanilla option cannot do.",
            "Brightness - gamma as a dial instead of Toggle Brightness's all-or-nothing, and well past the ceiling the vanilla slider stops at. What you had is put back when you switch it off.",
            "Skybox - your own sky, fog, cloud and star settings. Star Brightness at full shows stars at noon, which the game will never do on its own.",
            "Snow - snow in any biome, in any weather, and under any roof, each its own switch. Purely what you see: nothing is sent to the server and the blocks do not change.",
            "Block Colors - recolors dirt, coarse dirt, sand, gravel, stone, cobblestone, obsidian, bedrock, netherrack, end stone and oak planks, plus grass, leaves and water. It is a multiply over the real texture, so tinted dirt is still dirt.",
            "Food Overlay - saturation on the hunger bar, which vanilla never shows at all, and what the food in your hand would restore before you waste it.",
            "Tabs now size to their labels instead of splitting the row evenly, so a seventh tab no longer turns Favorite into Favorit."
         )
      ),
      new PatchNotesScreen.Release(
         "1.59.0",
         "11 Sept 2026",
         List.of(
            "New module: Scoreboard (HUD). Switch it on and Pip takes the server's sidebar over, so you can resize it, recolor it or hide it outright.",
            "Show Scoreboard turns it off completely. Scale resizes it. Title, text, number and background colors are all yours, and the title, the scores and the backing can each be switched off on their own.",
            "At default settings it is pixel-for-pixel what the game drew - checked frame against frame, not by eye - so turning it on is never itself a change.",
            "Position is Vanilla by default, meaning it stays exactly where the sidebar has always been. Set it to Custom and it uses X and Y, which is what the HUD editor drags.",
            "Colors are handed over as the fallback, so a server that colors its own lines keeps them. The scores are the exception - their red is the game's own default, so it is only replaced when the server has not set a format of its own."
         )
      ),
      new PatchNotesScreen.Release(
         "1.58.0",
         "11 Sept 2026",
         List.of(
            "Every remaining panel now drops into place too: the theme picker, patch notes, search, profile presets, the leaderboard and the song player.",
            "Scrolling glides instead of jumping. The wheel still moves in notches, but the list eases to the new spot over about a sixth of a second - so you can see where you ended up rather than losing your place.",
            "It eases a fraction of the remaining gap per second, so a long throw starts fast and a single notch is barely a nudge, and the speed does not change with your frame rate.",
            "Drawing and clicking both read the eased position, so a row you click mid-glide is the row you were pointing at. Switching tab still snaps - that is a different list, and easing between two unrelated places looks like a glitch.",
            "Profile presets still steps a whole entry at a time; that list is drawn by index rather than by pixel."
         )
      ),
      new PatchNotesScreen.Release(
         "1.57.0",
         "11 Sept 2026",
         List.of(
            "The GUI is a lot cheaper to draw. Every rounded corner used to be a stack of one-pixel rows - the ClickGUI was handing the game 1086 shapes a frame and spending 2.8ms building the list before anything reached the screen. It is 203 shapes and 0.3ms now.",
            "That is not just five times fewer: the game checks each new shape against every one already queued, so the saving compounds. Corners are properly antialiased into the bargain.",
            "The colour wheels were a grid of three-pixel squares redrawn every frame - over a thousand of them on the theme picker. Both are one image now, and the brightness sliders are a single gradient instead of a fill per row.",
            "Keystroke caps got the same treatment, so the HUD is cheaper during a fight too.",
            "The ClickGUI, module settings, the colour popup and the font picker now drop into place instead of appearing. Timed off the clock rather than off frames, so it looks the same however the game is running."
         )
      ),
      new PatchNotesScreen.Release(
         "1.56.0",
         "11 Sept 2026",
         List.of(
            "Old Potions is gone. The switch, the textures and the pack are all removed - Nostalgia is back to Crystal Motion, Old Lighting and Old Glint.",
            "Nothing else on the module changed, and any Old Potions setting left in your config is ignored and dropped on the next save."
         )
      ),
      new PatchNotesScreen.Release(
         "1.55.0",
         "11 Sept 2026",
         List.of(
            "New module: Fire Color (Render). Pick a colour and all fire takes it - blocks on the ground, the flames on anyone burning, and the overlay across your own screen.",
            "Any colour, not just warm ones. Tinting the orange sprite could only ever muddy it, so the module carries a pack that strips the hue out of fire and puts yours back. Blue is really blue.",
            "Shape, shading and the animation are untouched, and turning the module off puts vanilla fire back exactly as it was."
         )
      ),
      new PatchNotesScreen.Release(
         "1.54.0",
         "10 Sept 2026",
         List.of(
            "Old Potions uses the real 1.8 textures now, taken from the 1.8.9 copy in your own launcher - not a drawing of them.",
            "That means the actual glass flask, the orange cork, and the proper splash bottle with its tail.",
            "My three attempts at drawing these were not close enough and are gone."
         )
      ),
      new PatchNotesScreen.Release(
         "1.53.0",
         "10 Sept 2026",
         List.of(
            "Custom Font: the font is picked from a searchable list of what is actually on your machine, not typed. A wrong letter used to mean nothing happened at all.",
            "Any .ttf or .otf you drop in the config folder is listed first, above the system fonts.",
            "Type to filter, enter takes the top match. Old Potions redrawn - the first attempt was a thick outline with a hollow middle and looked nothing like glass."
         )
      ),
      new PatchNotesScreen.Release(
         "1.52.0",
         "10 Sept 2026",
         List.of(
            "Nostalgia has Old Potions: the pre-1.9 flask, rounder and flat-topped, with the pink cork and the liquid tinted per effect.",
            "Splash keeps a brown neck and lingering a purple one, so a thrown pot is not mistaken for a drinkable one mid-fight.",
            "Drawn rather than copied - a recreation of that look, not Mojang's old files."
         )
      ),
      new PatchNotesScreen.Release(
         "1.51.0",
         "10 Sept 2026",
         List.of(
            "TierTagger is gone. The Tiers mod does this properly and is already installed, so the client no longer competes with it for the same nametag.",
            "Nothing else changes - the star, the ping and the song on a nametag are still Pip's."
         )
      ),
      new PatchNotesScreen.Release(
         "1.50.0",
         "10 Sept 2026",
         List.of(
            "The gamemode picture is inside the tier bracket now, not on your screen - [icon LT3] rather than a badge in the corner.",
            "Each mode has its own 8x8 icon, drawn at the size a nametag actually gives you. The service's own kit art is inventory screenshots, which are unreadable that small.",
            "End crystals sat a block and a half above their hitbox on No Bob. getYOffset carries the whole vertical placement, not just the wobble, so returning zero deleted the drop."
         )
      ),
      new PatchNotesScreen.Release(
         "1.49.0",
         "10 Sept 2026",
         List.of(
            "End crystals animate again. Old Crystals froze them outright, which threw the animation away rather than changing it.",
            "It is Crystal Motion now: Normal, No Bob, or Static. No Bob keeps the spin and drops only the up-and-down.",
            "TierTagger draws the selected gamemode's artwork on the HUD, from the ranking service's own kit images, with a movable position and size."
         )
      ),
      new PatchNotesScreen.Release(
         "1.48.0",
         "10 Sept 2026",
         List.of(
            "Song Player can turn other people's songs off. Show Others' Songs and its color were stranded on the hidden presence module, where nothing could reach them.",
            "The star on Pip users is still on that hidden module - say if you want that switch surfaced too."
         )
      ),
      new PatchNotesScreen.Release(
         "1.47.0",
         "10 Sept 2026",
         List.of(
            "TierTagger shows the tier for a gamemode you pick. Overall is gone - it was a top-hundred placing rather than a tier, so almost nobody had one and the module looked broken.",
            "Gamemodes are the eight the service ranks: Netherite OP, Vanilla, Pot, UHC, SMP, Sword, Axe and Mace.",
            "Nostalgia, Pop Chams and TierTagger show up under New in the module grid."
         )
      ),
      new PatchNotesScreen.Release(
         "1.46.0",
         "10 Sept 2026",
         List.of(
            "New module: TierTagger, in Misc. Shows each player's combat tier on their nametag and in the tab list.",
            "Pick the kit - Overall, Vanilla, UHC, Pot, NethOP, SMP, Sword, Axe or Mace - and tiers are colored by rank.",
            "Show Own hides your own tier from both places at once.",
            "It asks the ranking service once per player per session and remembers \"not ranked\" too, so most players are never asked about twice.",
            "Requests go out one at a time with a gap, so joining a full server is a trickle rather than a hundred connections.",
            "Emptying the Host setting switches the lookups off entirely. The module is off by default."
         )
      ),
      new PatchNotesScreen.Release(
         "1.45.0",
         "10 Sept 2026",
         List.of(
            "Typing with the module grid open goes straight into the search. You no longer have to click the field first.",
            "Nameplates is now Nametags, and Greyscale is now Grayscale. Everything they had saved comes across.",
            "Spelling is American English throughout the labels and descriptions - color, armor, gray."
         )
      ),
      new PatchNotesScreen.Release(
         "1.44.0",
         "9 Sept 2026",
         List.of(
            "New module: Pop Chams, in Render. Popping a totem leaves a fading ghost of the player, in their own skin and their own pose.",
            "The ghost is placed by the game's own transform code, so it sits at the right size and tilts correctly when gliding, swimming, crawling or spinning.",
            "It is taken from the frame the player is actually drawn on, so it lands where they were rather than a tick behind.",
            "Slim skins get the slim model.",
            "Lifetime and Fade Out are separate options - a lifetime is not part of fading.",
            "Nostalgia has an icon in the module grid."
         )
      ),
      new PatchNotesScreen.Release(
         "1.43.0",
         "9 Sept 2026",
         List.of(
            "New module: Nostalgia, in Render, with three switches - Old Crystals, Old Lighting and Old Glint.",
            "Old Glint: the classic streaked glint at full strength. Later versions dimmed it; this drops that.",
            "Old Lighting: flatter, brighter faces - less contrast between a lit side and a shaded one.",
            "Old Crystals: end crystals stop spinning and bobbing and sit where they are.",
            "Old Lighting and Old Crystals settings from before carry into Nostalgia by themselves.",
            "Every Pip resource pack now shows the Pip icon in the pack list.",
            "The controls screen shows real names for Toggle Brightness, Reset Pop Counter and Preview Kill Effect instead of raw keys."
         )
      ),
      new PatchNotesScreen.Release(
         "1.42.0",
         "8 Sept 2026",
         List.of(
            "When the system source cannot run, the panel says why instead of quietly showing local files.",
            "On a Mac that meant \"Folder not found\" - the local player complaining about a music folder that was never the problem - while the real reason sat in a field nothing displayed.",
            "The reasons it can now give: Spotify is not open, macOS has not granted Automation permission, or the media session could not be read."
         )
      ),
      new PatchNotesScreen.Release(
         "1.41.0",
         "8 Sept 2026",
         List.of(
            "The progress bar flows. Spotify only reports a new position every four or five seconds, and the client was throwing its own clock away twice a second and restarting from that stale number.",
            "Windows now hands over how old each reading is, so a stale one still tells the truth about where the song is.",
            "The bar keeps the fraction of a pixel it is partway through, so the fill and the handle glide instead of hopping - a whole pixel is a second or two of a song at that size.",
            "Seeks, pauses and track changes still snap immediately."
         )
      ),
      new PatchNotesScreen.Release(
         "1.40.0",
         "8 Sept 2026",
         List.of(
            "Keystrokes Pad lays itself out from a Keys setting - rows split on \"/\", keys on \",\". \"LMB,RMB\" is a two-button pad; the old block is still the default.",
            "An empty cell leaves a hole instead of closing the gap, so \"M1,,M2\" drops W and leaves the mouse buttons where they were.",
            "Columns sets how wide the block is in keys, so a short layout is not stuck at three wide.",
            "Custom Font has Pip Client HUD as its own switch. On with the other two off puts the font on the client's overlay and leaves chat, the hotbar and every menu alone.",
            "Transparent Players swaps its Self tick for Applies To: Everyone Else, Everyone, or Only Me."
         )
      ),
      new PatchNotesScreen.Release(
         "1.39.0",
         "8 Sept 2026",
         List.of(
            "Tenor, Giphy and Imgur share links work as backgrounds now. Those links are web pages, not pictures, so the client follows the page to the picture it names.",
            "Applies everywhere a background link does: Mouse Tracker, Custom Hotbar, Main Menu and the Keystrokes Pad.",
            "A link that will not work says why - a web page, an expired link, or a format that is not a GIF or PNG - and names the link rather than always blaming the Keystrokes Pad."
         )
      ),
      new PatchNotesScreen.Release(
         "1.38.0",
         "8 Sept 2026",
         List.of(
            "Global Color leaves the Mouse Tracker's pad and trail alone - it is a backdrop for a picture, not part of the palette.",
            "Return Speed is back on the Mouse Tracker, and defaults to zero, which is the pure wrap."
         )
      ),
      new PatchNotesScreen.Release(
         "1.37.0",
         "7 Sept 2026",
         List.of(
            "Song Player on macOS: asks Spotify directly, once a second, for the track, artist, album and play position.",
            "It no longer needs Accessibility permission, and no longer opens Spotify just to check whether Spotify was open.",
            "The panel says \"Spotify is not open\" or names the permission to grant, rather than sitting blank.",
            "Duration is read in whichever unit Spotify gives it, so the progress bar cannot come out a thousand times too long."
         )
      ),
      new PatchNotesScreen.Release(
         "1.36.0",
         "7 Sept 2026",
         List.of(
            "The mouse pad wraps. Run off one edge and the cursor comes back in at the opposite one, instead of parking against the rail.",
            "Return Speed is gone - there is nothing left for it to do.",
            "Both keystrokes layouts are rounded caps now, lighting up on press and easing back out so a quick tap still reads as one.",
            "Colors survive Global Color. Saving while it was on used to write the one shade over every color you had picked, permanently.",
            "Settings that would not parse cost that one setting instead of everything after it, and say so in the log.",
            "Theme shadows, the scrim and your playtime load again on a config with no saved accent color.",
            "The media helper drops the -ExecutionPolicy Bypass flag and dies with the game rather than being left running.",
            "Store apps no longer show as \"SpotifyAB.SpotifyMusic_zpdnekdrzrea0!Spotify\" on the panel.",
            "Appear To Other Pip Users, in Song Player: a real off switch for presence, not just for the song."
         )
      ),
      new PatchNotesScreen.Release(
         "1.35.0",
         "6 Sept 2026",
         List.of(
            "Album art searches songs before albums and checks the artist matches, so a wrong cover is never shown in place of none.",
            "The mouse cursor could get stuck against an edge and never come back."
         )
      ),
      new PatchNotesScreen.Release(
         "1.34.0",
         "6 Sept 2026",
         List.of(
            "Runs alongside VulkanMod. Motion Blur is the one thing that cannot come with it, since it needs a post-processing pass VulkanMod does not have."
         )
      ),
      new PatchNotesScreen.Release(
         "1.31.0",
         "6 Sept 2026",
         List.of(
            "Transparent Players can fade armor on its own slider, separately from the body.",
            "Armor needed its render layer swapped as well - a cutout layer keeps or drops a pixel and never blends, so alpha alone did nothing."
         )
      ),
      new PatchNotesScreen.Release(
         "1.30.0",
         "6 Sept 2026",
         List.of(
            "Headwear: halo, crown, and cat, fox, wolf, bunny, bear and mouse ears.",
            "Wings reshaped - they sweep out and lift at the tips instead of hanging.",
            "The GUI is Carrot's GUI only now. The other ten layouts are gone; the color is still yours to pick.",
            "This screen. Click the version in the header to get back to it.",
            "Pip Presence is always on and no longer a module you can switch off.",
            "Song sharing moved to the Song Player, as Show My Song To Others."
         )
      ),
      new PatchNotesScreen.Release(
         "1.29.0",
         "6 Sept 2026",
         List.of(
            "Keystrokes Pad draws again - hiding the keys used to leave it blank after the mouse pad moved out.",
            "Sound Control actually works. The old hook was never called, so nothing it did reached the game.",
            "Twenty sounds to set: crystals, anchors, totems, shield breaks, bows, footsteps and more.",
            "Pip Client users get a star in the tab list as well as on their nametag."
         )
      ),
      new PatchNotesScreen.Release(
         "1.27.0",
         "6 Sept 2026",
         List.of(
            "Custom Font sits at the right size and on the right line - it was scaling off the wrong metric.",
            "Text is no longer stretched to fill the space vanilla reserved for it."
         )
      ),
      new PatchNotesScreen.Release(
         "1.26.0", "6 Sept 2026", List.of("An online panel beside the leaderboard: who else is on Pip Client, and what they are playing.")
      ),
      new PatchNotesScreen.Release(
         "1.23.0",
         "6 Sept 2026",
         List.of(
            "Pip Presence: a star on other Pip Client players, and their track beside it.",
            "Cosmetics: cat ears, wings and a cape, all colorable.",
            "Sound Control, Global Color and Nametag Ping."
         )
      ),
      new PatchNotesScreen.Release(
         "1.19.0",
         "6 Sept 2026",
         List.of(
            "Custom Font: point it at a .ttf or an installed font and the HUD, chat and menus follow.", "Chat keeps its colors; nothing in the layout moves."
         )
      ),
      new PatchNotesScreen.Release("1.18.0", "6 Sept 2026", List.of("Custom Hotbar: its own background, and every slot movable and colorable on its own.")),
      new PatchNotesScreen.Release(
         "1.17.0", "6 Sept 2026", List.of("Main Menu: your own picture behind the title screen, and PIP across the top in a moving rainbow.")
      ),
      new PatchNotesScreen.Release("1.16.0", "6 Sept 2026", List.of("Mouse Tracker is its own module, and also ships as a standalone mod.")),
      new PatchNotesScreen.Release("1.13.0", "5 Sept 2026", List.of("Trails rebuilt: camera-facing ribbons with feathered edges, along a smooth curve."))
   );

   public PatchNotesScreen(Screen parent) {
      super(Component.literal("What's new"));
      this.parent = parent;
   }

   private int bodyTop() {
      return this.panelY + 42;
   }

   private int bodyBottom() {
      return this.panelY + this.panelH - 14;
   }

   private int contentHeight() {
      int total = 0;

      for (PatchNotesScreen.Release release : RELEASES) {
         total += 11 + release.lines().size() * 11 + 8;
      }

      return total;
   }

   private int maxScroll() {
      return Math.max(0, this.contentHeight() - (this.bodyBottom() - this.bodyTop()));
   }

   private void computeLayout() {
      this.panelH = Math.max(200, Math.min(this.height - 40, 420));
      this.panelX = (this.width - 460) / 2;
      this.panelY = Math.max(12, (this.height - this.panelH) / 2) - this.open.drop(20);
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      this.computeLayout();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      ThemeRenderer.fillRounded(context, this.panelX + 3, this.panelY + 4, 460, this.panelH, Theme.shadow(), 12);
      ThemeRenderer.panel(context, this.panelX, this.panelY, 460, this.panelH, Theme.border(), Theme.panelBg(), 12);
      int backX = this.panelX + 14 - 4;
      boolean backHovered = isInside(mouseX, mouseY, backX, this.panelY + 10, 20, 20);
      ThemeRenderer.row(context, backX, this.panelY + 10, 20, 20, backHovered, Theme.trackBg(), Theme.rowBgHover(), 6);
      context.drawString(this.font, "<", backX + 7, this.panelY + 16, ThemeRenderer.rowTextColor(backHovered, Theme.textSecondary(), Theme.textPrimary()));
      context.drawString(this.font, "What's new", backX + 30, this.panelY + 16, Theme.textPrimary());
      String current = "You are on "
         + FabricLoader.getInstance()
            .getModContainer("lyfw-client")
            .map(c -> c.getMetadata().getVersion().getFriendlyString())
            .orElse(RELEASES.get(0).version());
      context.drawString(this.font, current, this.panelX + 460 - 14 - this.font.width(current), this.panelY + 16, Theme.textMuted());
      int left = this.panelX + 14;
      int right = this.panelX + 460 - 14;
      context.enableScissor(left, this.bodyTop(), right, this.bodyBottom());
      int y = this.bodyTop() - this.scroll.shown();

      for (PatchNotesScreen.Release release : RELEASES) {
         if (y + 11 >= this.bodyTop() && y <= this.bodyBottom()) {
            context.drawString(this.font, release.version(), left, y, Theme.accent());
            String date = release.date();
            context.drawString(this.font, date, right - this.font.width(date), y, Theme.textMuted());
         }

         y += 11;

         for (String line : release.lines()) {
            if (y + 11 >= this.bodyTop() && y <= this.bodyBottom()) {
               context.drawString(this.font, this.font.plainSubstrByWidth(line, right - left - 8), left + 8, y, Theme.textSecondary());
            }

            y += 11;
         }

         y += 8;
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackX = right + 4;
         int trackH = this.bodyBottom() - this.bodyTop();
         context.fill(trackX, this.bodyTop(), trackX + 2, this.bodyTop() + trackH, Theme.trackBg());
         int handleH = Math.max(18, trackH * trackH / (trackH + maxScroll));
         int handleY = this.bodyTop() + (int)((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
         context.fill(trackX, handleY, trackX + 2, handleY + handleH, Theme.accent());
      }
   }

   private static boolean isInside(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      if (isInside((int)Math.round(click.x()), (int)Math.round(click.y()), this.panelX + 14 - 4, this.panelY + 10, 20, 20)) {
         this.onClose();
         return true;
      } else {
         return super.mouseClicked(click, doubled);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 11.0 * 2.0), 0, this.maxScroll()));
      return true;
   }

   public boolean keyPressed(KeyEvent input) {
      if (input.key() != 256) {
         return super.keyPressed(input);
      } else {
         this.onClose();
         return true;
      }
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }

   public record Release(String version, String date, List<String> lines) {
   }
}
