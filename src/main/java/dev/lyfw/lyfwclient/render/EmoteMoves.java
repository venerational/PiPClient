package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class EmoteMoves {
   private static final float RAD = (float) (Math.PI / 180.0);
   private static final float TAU = (float) (Math.PI * 2);
   private static final String[] TABLE = new String[]{
      "WAVE WAVE 1 1 1 0",
      "BIG_WAVE TWO_WAVE 1 1.2 0 0",
      "DOUBLE_WAVE WAVE 1.6 1 -1 0",
      "SHY_WAVE WAVE 0.6 0.5 1 1",
      "SALUTE SALUTE 1 1 1 0",
      "BOW BOW 0.7 1 0 0",
      "DEEP_BOW BOW 0.5 1.6 0 0",
      "CURTSY BOW 0.7 0.6 0 1",
      "HANDSHAKE REACH 1.4 1 1 0",
      "HIGH_FIVE REACH 1 1 1 1",
      "FIST_BUMP REACH 1.6 1 1 2",
      "NOD_HELLO NOD 1 1 0 0",
      "CLAP CLAP 1 1 0 0",
      "SLOW_CLAP CLAP 0.5 1.2 0 0",
      "FAST_CLAP CLAP 1.8 0.8 0 0",
      "APPLAUSE CLAP 1.3 1 0 1",
      "CHEER PUNCH 1 1 0 0",
      "DOUBLE_CHEER PUNCH 1.4 1.2 0 1",
      "FIST_PUMP PUNCH 1.6 1 1 0",
      "VICTORY ARMS_UP 0.8 1 0 1",
      "CHAMPION ARMS_UP 0.6 1.2 0 2",
      "JUMP_FOR_JOY JACKS 1 1 0 1",
      "RAISE_ROOF ARMS_UP 1.4 1 0 0",
      "WOOHOO PUNCH 1.2 1.3 -1 0",
      "ARMS_UP ARMS_UP 0.4 1 0 3",
      "HOORAY ARMS_UP 1 1.1 0 0",
      "DAB DAB 0.8 1 1 0",
      "DAB_LEFT DAB 0.8 1 -1 0",
      "FLOSS FLOSS 1.4 1 0 0",
      "SLOW_FLOSS FLOSS 0.8 1.1 0 0",
      "RUNNING_MAN RUNNING_MAN 1.3 1 0 0",
      "ROBOT ROBOT 0.9 1 0 0",
      "TWIST TWIST 1 1 0 0",
      "FAST_TWIST TWIST 1.7 0.9 0 0",
      "DISCO DISCO 1.1 1 1 0",
      "DISCO_DOWN DISCO 1.1 1 -1 1",
      "HIP_SWAY SWAY 0.8 1 0 0",
      "SLOW_DANCE SWAY 0.5 0.8 0 1",
      "SHUFFLE RUNNING_MAN 1.8 0.7 0 1",
      "MOONWALK MOONWALK 1 1 0 0",
      "CHARLESTON TWIST 1.3 1.2 0 1",
      "MACARENA MACARENA 1 1 0 0",
      "CONGA SWAY 1.2 1 0 2",
      "SPRINKLER SPRINKLER 1 1 1 0",
      "LAWNMOWER LAWNMOWER 1 1 1 0",
      "HEADBANG HEADBANG 1.6 1 0 0",
      "AIR_GUITAR AIR_GUITAR 1.4 1 1 0",
      "AIR_DRUMS AIR_GUITAR 1.8 1 0 1",
      "BREAKDANCE BREAKDANCE 1 1 0 0",
      "SPIN_DANCE SPIN 1 1 0 0",
      "HANDS_UP_DANCE ARMS_UP 1.3 1 0 4",
      "BOUNCE SWAY 1.6 0.8 0 3",
      "GROOVE SWAY 1.1 1.1 0 4",
      "WIGGLE SWAY 2 0.6 0 5",
      "STEP_TOUCH SWAY 0.9 1 0 6",
      "LAUGH LAUGH 1 1 0 0",
      "BIG_LAUGH LAUGH 1.4 1.4 0 1",
      "CRY CRY 1 1 0 0",
      "SOB CRY 1.5 1.3 0 1",
      "FACEPALM FACEPALM 0.5 1 1 0",
      "DOUBLE_FACEPALM FACEPALM 0.5 1 0 1",
      "SHRUG SHRUG 0.7 1 0 0",
      "THINK HAND_TO 0.5 1 1 0",
      "CONFUSED HAND_TO 0.7 1 1 1",
      "ANGRY STOMP 1.4 1 0 1",
      "STOMP STOMP 1 1 0 0",
      "SULK CROSS_ARMS 0.6 1 0 1",
      "YAWN STRETCH 0.5 1 0 1",
      "SLEEPY STRETCH 0.4 0.8 0 2",
      "CHEEKY HAND_TO 1 1 1 2",
      "BLUSH HAND_TO 0.8 1 0 3",
      "POINT POINT 0.8 1 1 0",
      "POINT_UP POINT 0.8 1 1 1",
      "POINT_DOWN POINT 0.8 1 1 2",
      "BECKON REACH 1.2 1 1 3",
      "STOP_HAND REACH 0.4 1 1 4",
      "THUMBS_UP THUMB 0.8 1 1 0",
      "THUMBS_DOWN THUMB 0.8 1 1 1",
      "CROSS_ARMS CROSS_ARMS 0.5 1 0 0",
      "HANDS_ON_HIPS HIPS 0.5 1 0 0",
      "T_POSE TPOSE 0.3 1 0 0",
      "FLEX FLEX 0.8 1 1 0",
      "DOUBLE_FLEX FLEX 0.8 1 0 1",
      "STRETCH STRETCH 0.6 1 0 0",
      "CHECK_WATCH HAND_TO 0.8 1 -1 4",
      "SCRATCH_HEAD HAND_TO 1.2 1 1 5",
      "WIPE_BROW HAND_TO 0.9 1 1 6",
      "PEEK HAND_TO 0.7 1 1 7",
      "SEARCH HAND_TO 0.6 1 1 8",
      "TIP_HAT SALUTE 0.8 1 1 1",
      "THROW THROW 0.9 1 1 0",
      "PUSH_UPS PUSH_UP 0.9 1 0 0",
      "SIT_UPS SIT_UP 0.9 1 0 0",
      "SQUATS SQUAT 1 1 0 0",
      "JUMPING_JACKS JACKS 1.3 1 0 0",
      "RUNNING RUN 1.6 1 0 0",
      "TOE_TOUCH STRETCH 0.7 1 0 3",
      "LUNGES SQUAT 0.8 1 0 1",
      "SHADOW_BOX BOX 1.6 1 0 0",
      "KICK KICK 0.9 1 1 0",
      "KARATE BOX 1.2 1.2 0 1",
      "SIT SIT 0.4 1 0 0",
      "SIT_LEGS_OUT SIT 0.4 1 0 1",
      "KNEEL KNEEL 0.4 1 0 0",
      "LIE_DOWN LIE 0.4 1 0 0",
      "SLEEP LIE 0.3 1 0 1",
      "MEDITATE SIT 0.3 1 0 2",
      "CROUCH_REST KNEEL 0.4 1 0 1",
      "LEAN_BACK HIPS 0.4 1 0 1",
      "FLOAT LIE 0.5 1 0 2",
      "PLAY_DEAD LIE 0.3 1 0 3",
      "CHICKEN CHICKEN 1.4 1 0 0",
      "WORM WORM 1 1 0 0",
      "CARTWHEEL FLIP 0.8 1 0 1",
      "HANDSTAND HANDSTAND 0.5 1 0 0",
      "BACKFLIP FLIP 0.8 1 0 0",
      "ZOMBIE ZOMBIE 0.6 1 0 0",
      "SWIM SWIM 1.2 1 0 0",
      "FISHING ROW 0.5 1 1 1",
      "ROWING ROW 0.8 1 0 0",
      "DRIVING DRIVE 1 1 0 0",
      "TIGHTROPE TPOSE 0.8 1 0 1",
      "DISAGREE SHAKE_HEAD 1 1 0 0"
   };
   private static final Map<CosmeticsModule.Emote, EmoteMoves.Step> STEPS = new EnumMap<>(CosmeticsModule.Emote.class);
   private static final float[] PENDING;

   private EmoteMoves() {
   }

   public static boolean known(CosmeticsModule.Emote emote) {
      return STEPS.containsKey(emote);
   }

   public static void pose(PlayerModel model, CosmeticsModule.Emote emote, float seconds) {
      EmoteMoves.Step step = STEPS.get(emote);
      if (step != null) {
         stand(model);
         PENDING[0] = 0.0F;
         PENDING[1] = 0.0F;
         PENDING[2] = 0.0F;
         PENDING[3] = 0.0F;
         PENDING[4] = 0.0F;
         PENDING[5] = 0.0F;
         PENDING[6] = 0.0F;
         PENDING[6] = 24.0F;
         PENDING[7] = 0.0F;
         PENDING[8] = 0.0F;
         float t = seconds * step.speed();
         float beat = t * (float) (Math.PI * 2);
         float amp = step.amp();
         float side = step.side();
         float extra = step.extra();
         float swing = Mth.sin(beat);
         float swing2 = Mth.sin(beat * 2.0F);
         label405:
         switch (step.move()) {
            case WAVE:
               float arm = extra > 0.0F ? -120.0F : -160.0F;
               one(model, side, arm, 0.0F, out(side, 18.0F + swing * 26.0F * amp));
               head(model, 0.0F, side * -6.0F, swing * 3.0F);
               break;
            case TWO_WAVE:
               both(model, -158.0F, 0.0F, 22.0F + swing * 24.0F * amp);
               head(model, -6.0F, 0.0F, swing * 4.0F);
               break;
            case SALUTE:
               one(model, side, -130.0F, side * -34.0F, out(side, -22.0F));
               head(model, extra > 0.0F ? 6.0F : -4.0F, 0.0F, 0.0F);
               if (extra > 0.0F) {
                  lean(8.0F * Mth.abs(swing), 0.0F, 0.0F);
               }
               break;
            case BOW:
               float deep = (0.5F + 0.5F * -Mth.cos(beat)) * 40.0F * amp;
               lean(deep, 0.0F, 0.0F);
               if (extra > 0.0F) {
                  leg(model, 1.0F, 14.0F, 0.0F, 0.0F);
                  leg(model, -1.0F, -8.0F, 0.0F, -12.0F);
                  both(model, 4.0F, 0.0F, 34.0F);
               } else {
                  one(model, 1.0F, -28.0F, -40.0F, 32.0F);
                  one(model, -1.0F, 10.0F, 0.0F, -16.0F);
               }
               break;
            case REACH:
               float push = (0.5F + 0.5F * swing) * amp;
               float reach = extra == 1.0F ? -150.0F : (extra == 4.0F ? -95.0F : -80.0F - 20.0F * push);
               one(model, side, reach, side * (extra == 3.0F ? -30.0F : -14.0F), out(side, extra == 4.0F ? -4.0F : 10.0F));
               if (extra == 3.0F) {
                  one(model, side, -78.0F, side * -26.0F, out(side, 8.0F + push * 30.0F));
               }

               head(model, extra == 1.0F ? -10.0F : 0.0F, side * -8.0F, 0.0F);
               break;
            case CLAP: {
               float close = (0.5F + 0.5F * Mth.cos(beat)) * amp;
               float open = 26.0F - close * 24.0F;
               arms(model, -72.0F - close * 8.0F, -46.0F, open, extra > 0.0F ? 12.0F : 0.0F);
               head(model, -4.0F + close * 6.0F, 0.0F, 0.0F);
               break;
            }
            case PUNCH:
               float right = Mth.abs(Mth.sin(beat)) * amp;
               float left = extra > 0.0F ? right : Mth.abs(Mth.sin(beat + (float) (Math.PI / 2))) * amp;
               if (side >= 0.0F) {
                  one(model, 1.0F, -80.0F - 95.0F * right, 0.0F, 14.0F);
               }

               if (side <= 0.0F) {
                  one(model, -1.0F, -80.0F - 95.0F * left, 0.0F, -14.0F);
               }

               lift(model, -1.6F * right);
               head(model, -12.0F * right, 0.0F, 0.0F);
               break;
            case ARMS_UP:
               float pump = extra == 0.0F ? Mth.abs(swing) : (extra == 4.0F ? 0.5F + 0.5F * swing : 0.0F);
               both(model, -150.0F - 25.0F * pump * amp, 0.0F, 14.0F + (extra == 1.0F ? 16.0F : 0.0F));
               if (extra == 2.0F) {
                  both(model, -168.0F, 0.0F, 6.0F);
                  head(model, -14.0F, 0.0F, 0.0F);
               }

               if (extra == 3.0F) {
                  both(model, -175.0F, 0.0F, 4.0F + swing * 3.0F);
               }

               if (extra == 4.0F) {
                  sway(model, swing * 10.0F * amp);
               }
               break;
            case DAB:
               one(model, side, -150.0F, side * 30.0F, out(side, -46.0F));
               one(model, -side, -104.0F, -side * 26.0F, out(-side, 34.0F));
               head(model, 26.0F, side * -20.0F, side * 12.0F);
               lean(6.0F, 0.0F, 0.0F);
               break;
            case FLOSS:
               float arms = Mth.sin(beat) * 34.0F * amp;
               float hips = -Mth.sin(beat) * 12.0F * amp;
               one(model, 1.0F, 12.0F + arms, 0.0F, 24.0F + arms * 0.5F);
               one(model, -1.0F, 12.0F + arms, 0.0F, -24.0F + arms * 0.5F);
               sway(model, hips);
               head(model, 0.0F, arms * 0.2F, 0.0F);
               break;
            case RUNNING_MAN:
               float step2 = Mth.sin(beat);
               leg(model, 1.0F, -48.0F * step2 * amp, 0.0F, 0.0F);
               leg(model, -1.0F, 48.0F * step2 * amp, 0.0F, 0.0F);
               one(model, 1.0F, 50.0F * step2, 0.0F, 18.0F);
               one(model, -1.0F, -50.0F * step2, 0.0F, -18.0F);
               lift(model, -Mth.abs(step2) * (extra > 0.0F ? 0.6F : 1.4F));
               break;
            case ROBOT:
               float tick = Math.round(t * 4.0F) / 4.0F;
               float stepped = Mth.sin(tick * (float) (Math.PI * 2));
               one(model, 1.0F, -90.0F, 0.0F, 14.0F + 40.0F * stepped * amp);
               one(model, -1.0F, -90.0F, 0.0F, -14.0F - 40.0F * stepped * amp);
               head(model, 0.0F, stepped * 24.0F, 0.0F);
               sway(model, stepped * 6.0F);
               break;
            case TWIST: {
               float turn = swing * 28.0F * amp;
               tilt(model, 0.0F, turn, 0.0F, 24.0F);
               both(model, -30.0F - (extra > 0.0F ? 30.0F : 0.0F), 0.0F, 42.0F);
               leg(model, 1.0F, 0.0F, 0.0F, 6.0F);
               leg(model, -1.0F, 0.0F, 0.0F, -6.0F);
               lift(model, -Mth.abs(swing2) * 1.2F);
               head(model, 0.0F, -turn * 0.4F, 0.0F);
               break;
            }
            case DISCO:
               float point = 0.5F + 0.5F * swing;
               float high = extra > 0.0F ? 40.0F : -150.0F;
               one(model, side, high + (extra > 0.0F ? -20.0F : 30.0F) * point, side * -20.0F, out(side, 30.0F));
               one(model, -side, 20.0F, 0.0F, out(-side, 18.0F));
               sway(model, swing * 10.0F * amp);
               head(model, extra > 0.0F ? 10.0F : -12.0F, side * 10.0F * swing, 0.0F);
               break;
            case SWAY:
               float side2 = swing * 12.0F * amp;
               sway(model, side2);
               float lift = extra == 3.0F ? Mth.abs(swing) * 2.2F : 0.0F;
               lift(model, -lift);
               switch ((int)extra) {
                  case 1:
                     arms(model, -74.0F, -40.0F, 20.0F, 0.0F);
                     break;
                  case 2:
                     both(model, -150.0F, 0.0F, 16.0F);
                     leg(model, 1.0F, -30.0F * Mth.abs(swing), 0.0F, 0.0F);
                     break;
                  case 3:
                  default:
                     both(model, -16.0F, 0.0F, 24.0F + side2 * 0.5F);
                     break;
                  case 4:
                     one(model, 1.0F, -60.0F + side2 * 2.0F, 0.0F, 30.0F);
                     one(model, -1.0F, -60.0F - side2 * 2.0F, 0.0F, -30.0F);
                     break;
                  case 5:
                     both(model, 8.0F, 0.0F, 22.0F + Mth.abs(side2));
                     break;
                  case 6:
                     leg(model, swing > 0.0F ? 1.0F : -1.0F, -22.0F * Mth.abs(swing), 0.0F, 0.0F);
                     both(model, -20.0F, 0.0F, 26.0F);
               }

               head(model, 0.0F, side2 * 0.6F, side2 * 0.3F);
               break;
            case MOONWALK:
               float slide = Mth.sin(beat);
               leg(model, 1.0F, -34.0F * slide, 0.0F, 0.0F);
               leg(model, -1.0F, 20.0F * slide, 0.0F, 0.0F);
               one(model, 1.0F, 30.0F * slide, 0.0F, 26.0F);
               one(model, -1.0F, -30.0F * slide, 0.0F, -26.0F);
               lean(-6.0F, 0.0F, 0.0F);
               lift(model, -Mth.abs(slide) * 0.8F);
               break;
            case MACARENA:
               int phase = (int)(t % 1.0F * 4.0F);
               switch (phase) {
                  case 0:
                     arms(model, -84.0F, -30.0F, 14.0F, 0.0F);
                     break;
                  case 1:
                     arms(model, -84.0F, 30.0F, 14.0F, 0.0F);
                     break;
                  case 2:
                     arms(model, -150.0F, 0.0F, 30.0F, 0.0F);
                     break;
                  default:
                     arms(model, -40.0F, 0.0F, 52.0F, 0.0F);
               }

               sway(model, swing * 10.0F * amp);
               head(model, 0.0F, swing * 8.0F, 0.0F);
               break;
            case SPRINKLER:
               float sweep = Mth.sin(beat * 0.5F);
               one(model, -side, -150.0F, 0.0F, out(-side, -10.0F));
               one(model, side, -88.0F, side * sweep * 70.0F, out(side, 16.0F));
               tilt(model, 0.0F, sweep * 20.0F, 0.0F, 24.0F);
               head(model, 0.0F, sweep * 30.0F, 0.0F);
               break;
            case LAWNMOWER: {
               float pull = 0.5F + 0.5F * Mth.sin(beat);
               one(model, -side, -70.0F, -side * 20.0F, out(-side, 12.0F));
               one(model, side, -60.0F + 60.0F * pull, side * -30.0F, out(side, 16.0F));
               lean(14.0F - 8.0F * pull, 0.0F, 0.0F);
               lift(model, -pull * 1.2F);
               break;
            }
            case HEADBANG:
               head(model, 34.0F * Mth.abs(swing) * amp - 8.0F, 0.0F, 0.0F);
               both(model, -40.0F, 0.0F, 40.0F);
               lean(10.0F * Mth.abs(swing), 0.0F, 0.0F);
               lift(model, -Mth.abs(swing2) * 0.8F);
               break;
            case AIR_GUITAR:
               if (extra > 0.0F) {
                  float hit = Mth.abs(Mth.sin(beat));
                  float hit2 = Mth.abs(Mth.sin(beat + 1.2F));
                  one(model, 1.0F, -62.0F - 30.0F * hit, -20.0F, 24.0F);
                  one(model, -1.0F, -62.0F - 30.0F * hit2, 20.0F, -24.0F);
                  head(model, 12.0F * hit, 0.0F, 0.0F);
               } else {
                  one(model, side, -50.0F + 40.0F * swing, side * -30.0F, out(side, 26.0F));
                  one(model, -side, -96.0F, -side * 44.0F, out(-side, 18.0F));
                  head(model, 20.0F * Mth.abs(swing) - 6.0F, side * -10.0F, 0.0F);
               }

               lean(8.0F, 0.0F, 0.0F);
               break;
            case BREAKDANCE:
               tilt(model, 0.0F, t * 360.0F, 40.0F, 24.0F);
               both(model, -30.0F, 0.0F, 60.0F);
               leg(model, 1.0F, -60.0F, 0.0F, 24.0F);
               leg(model, -1.0F, 20.0F, 0.0F, -10.0F);
               break;
            case SPIN:
               tilt(model, 0.0F, t * 360.0F, 0.0F, 24.0F);
               both(model, -96.0F, 0.0F, 60.0F);
               head(model, -6.0F, 0.0F, 0.0F);
               lift(model, -Mth.abs(swing2) * 0.8F);
               break;
            case LAUGH:
               float shake = Mth.abs(swing) * amp;
               head(model, -22.0F - 8.0F * shake, 0.0F, 0.0F);
               lean(-8.0F - 6.0F * shake, 0.0F, 0.0F);
               if (extra > 0.0F) {
                  both(model, -30.0F - 20.0F * shake, 0.0F, 44.0F);
               } else {
                  one(model, 1.0F, -46.0F - 14.0F * shake, -40.0F, 18.0F);
                  one(model, -1.0F, 10.0F, 0.0F, -14.0F);
               }
               break;
            case CRY:
               float sobs = Mth.abs(swing) * amp;
               arms(model, -142.0F, -22.0F, -6.0F, 0.0F);
               head(model, 22.0F + 6.0F * sobs, 0.0F, 0.0F);
               lean(12.0F + 6.0F * sobs, 0.0F, 0.0F);
               if (extra > 0.0F) {
                  leg(model, 1.0F, -10.0F, 0.0F, 4.0F);
                  leg(model, -1.0F, -10.0F, 0.0F, -4.0F);
                  lift(model, 2.0F * sobs);
               }
               break;
            case FACEPALM:
               float press = 0.5F + 0.5F * Mth.cos(beat);
               one(model, side == 0.0F ? 1.0F : side, -142.0F - 10.0F * press, -26.0F, 18.0F);
               if (extra > 0.0F) {
                  one(model, -1.0F, -142.0F - 10.0F * press, 26.0F, -18.0F);
               }

               head(model, 22.0F + 8.0F * press, 0.0F, 0.0F);
               lean(8.0F, 0.0F, 0.0F);
               break;
            case SHRUG: {
               float up = 0.5F + 0.5F * Mth.cos(beat);
               arms(model, -28.0F - 16.0F * up, -50.0F, 56.0F + 14.0F * up, 0.0F);
               shoulders(model, -1.4F * up);
               head(model, 6.0F, 0.0F, 10.0F * up);
               break;
            }
            case HAND_TO:
               float idle = Mth.sin(beat) * amp;
               switch ((int)extra) {
                  case 0:
                     one(model, side, -128.0F, side * -30.0F, out(side, 6.0F));
                     head(model, 10.0F, side * 8.0F, side * 6.0F);
                     break label405;
                  case 1:
                     one(model, side, -122.0F, side * -34.0F, out(side, 8.0F));
                     head(model, 4.0F, side * (14.0F + idle * 10.0F), side * 10.0F);
                     break label405;
                  case 2:
                     one(model, side, -150.0F, side * -20.0F, out(side, 10.0F + idle * 8.0F));
                     head(model, -6.0F, side * -12.0F, side * -8.0F);
                     break label405;
                  case 3:
                     arms(model, -136.0F, -30.0F, 4.0F, 0.0F);
                     head(model, 12.0F, 0.0F, 0.0F);
                     break label405;
                  case 4:
                     one(model, side, -96.0F, side * -50.0F, out(side, 14.0F));
                     head(model, 18.0F, side * 10.0F, 0.0F);
                     break label405;
                  case 5:
                     one(model, side, -158.0F, side * -16.0F, out(side, 10.0F + idle * 10.0F));
                     head(model, 8.0F, side * -8.0F, 0.0F);
                     break label405;
                  case 6:
                     one(model, side, -150.0F, side * -10.0F, out(side, 8.0F + idle * 14.0F));
                     head(model, -10.0F, 0.0F, 0.0F);
                     break label405;
                  case 7:
                     one(model, side, -140.0F, side * -40.0F, out(side, 4.0F));
                     head(model, 0.0F, side * 26.0F, 0.0F);
                     tilt(model, 0.0F, side * 14.0F, 0.0F, 24.0F);
                     break label405;
                  default:
                     one(model, side, -110.0F, side * -44.0F, out(side, 12.0F));
                     head(model, -12.0F, idle * 40.0F, 0.0F);
                     break label405;
               }
            case STOMP:
               float stomp = Mth.sin(beat);
               leg(model, stomp > 0.0F ? 1.0F : -1.0F, -44.0F * Mth.abs(stomp) * amp, 0.0F, 0.0F);
               if (extra > 0.0F) {
                  arms(model, -34.0F, -30.0F, 40.0F, 0.0F);
                  head(model, 10.0F, stomp * 10.0F, 0.0F);
               } else {
                  one(model, 1.0F, 20.0F * stomp, 0.0F, 20.0F);
                  one(model, -1.0F, -20.0F * stomp, 0.0F, -20.0F);
               }

               lift(model, -Mth.abs(stomp) * 1.0F);
               break;
            case SIT:
               lift(model, 10.0F);
               leg(model, 1.0F, -90.0F, 0.0F, extra == 2.0F ? 34.0F : 6.0F);
               leg(model, -1.0F, -90.0F, 0.0F, extra == 2.0F ? -34.0F : -6.0F);
               if (extra == 1.0F) {
                  arms(model, -12.0F, 0.0F, 16.0F, 0.0F);
                  tilt(model, -10.0F, 0.0F, 0.0F, 12.0F);
               } else if (extra == 2.0F) {
                  arms(model, -20.0F, -40.0F, 58.0F, 0.0F);
                  head(model, -4.0F, 0.0F, 0.0F);
               } else {
                  arms(model, 6.0F, 0.0F, 18.0F, 0.0F);
               }

               head(model, Mth.sin(beat) * 3.0F, 0.0F, 0.0F);
               break;
            case KNEEL:
               lift(model, 6.0F);
               leg(model, 1.0F, -100.0F, 0.0F, 6.0F);
               leg(model, -1.0F, extra > 0.0F ? -100.0F : -20.0F, 0.0F, -6.0F);
               arms(model, extra > 0.0F ? -20.0F : -40.0F, -20.0F, 20.0F, 0.0F);
               lean(6.0F, 0.0F, 0.0F);
               break;
            case LIE:
               float breathx = Mth.sin(beat) * 2.0F;
               tilt(model, extra != 1.0F && extra != 3.0F ? 86.0F : -86.0F, 0.0F, 0.0F, 24.0F);
               if (extra == 1.0F) {
                  arms(model, -20.0F + breathx, -10.0F, 26.0F, 0.0F);
                  head(model, 0.0F, -24.0F, 0.0F);
               } else if (extra == 2.0F) {
                  arms(model, -40.0F, 0.0F, 40.0F + breathx, 0.0F);
                  leg(model, 1.0F, 8.0F, 0.0F, 8.0F);
                  leg(model, -1.0F, 8.0F, 0.0F, -8.0F);
                  lift(model, -2.0F + breathx * 0.3F);
               } else if (extra == 3.0F) {
                  arms(model, -30.0F, 0.0F, 70.0F, 0.0F);
                  leg(model, 1.0F, 0.0F, 0.0F, 18.0F);
                  leg(model, -1.0F, 0.0F, 0.0F, -18.0F);
               } else {
                  arms(model, breathx, 0.0F, 14.0F, 0.0F);
                  lift(model, -3.6F);
               }
               break;
            case PUSH_UP: {
               float down = 0.5F + 0.5F * Mth.cos(beat);
               tilt(model, 84.0F, 0.0F, 0.0F, 24.0F);
               lift(model, -4.0F + 2.5F * down);
               arms(model, -84.0F + 30.0F * down, 0.0F, 14.0F + 20.0F * down, 0.0F);
               head(model, -20.0F + 14.0F * down, 0.0F, 0.0F);
               break;
            }
            case SIT_UP: {
               float up = 0.5F + 0.5F * Mth.cos(beat);
               tilt(model, 86.0F - 60.0F * up, 0.0F, 0.0F, 22.0F);
               lift(model, -2.0F);
               leg(model, 1.0F, -80.0F, 0.0F, 8.0F);
               leg(model, -1.0F, -80.0F, 0.0F, -8.0F);
               arms(model, -130.0F, -30.0F, 10.0F, 0.0F);
               head(model, 14.0F * up, 0.0F, 0.0F);
               break;
            }
            case SQUAT: {
               float down = 0.5F + 0.5F * Mth.cos(beat);
               if (extra > 0.0F) {
                  leg(model, 1.0F, -54.0F * down, 0.0F, 4.0F);
                  leg(model, -1.0F, 40.0F * down, 0.0F, -4.0F);
                  lift(model, 2.4F * down);
                  arms(model, -20.0F, 0.0F, 22.0F, 0.0F);
               } else {
                  leg(model, 1.0F, -34.0F * down, 0.0F, 12.0F * down);
                  leg(model, -1.0F, -34.0F * down, 0.0F, -12.0F * down);
                  lift(model, 4.0F * down);
                  arms(model, -84.0F * down, -20.0F, 16.0F, 0.0F);
                  lean(14.0F * down, 0.0F, 0.0F);
               }
               break;
            }
            case JACKS: {
               float open = 0.5F + 0.5F * Mth.cos(beat);
               both(model, -20.0F - 150.0F * open, 0.0F, 20.0F + 14.0F * open);
               leg(model, 1.0F, 0.0F, 0.0F, 16.0F * open);
               leg(model, -1.0F, 0.0F, 0.0F, -16.0F * open);
               lift(model, -(extra > 0.0F ? 3.4F : 1.6F) * open * amp);
               break;
            }
            case RUN:
               float stride = Mth.sin(beat);
               leg(model, 1.0F, -60.0F * stride, 0.0F, 0.0F);
               leg(model, -1.0F, 60.0F * stride, 0.0F, 0.0F);
               one(model, 1.0F, 60.0F * stride - 30.0F, 0.0F, 16.0F);
               one(model, -1.0F, -60.0F * stride - 30.0F, 0.0F, -16.0F);
               lean(8.0F, 0.0F, 0.0F);
               lift(model, -Mth.abs(stride) * 1.2F);
               break;
            case BOX:
               float jab = Mth.abs(Mth.sin(beat));
               float cross = Mth.abs(Mth.sin(beat + (float) (Math.PI / 2)));
               one(model, 1.0F, -70.0F - 26.0F * jab, -24.0F + 24.0F * jab, 22.0F - 10.0F * jab);
               one(model, -1.0F, -70.0F - 26.0F * cross, 24.0F - 24.0F * cross, -22.0F + 10.0F * cross);
               if (extra > 0.0F) {
                  leg(model, 1.0F, -60.0F * jab, 0.0F, 14.0F * jab);
                  head(model, 0.0F, 14.0F * (jab - cross), 0.0F);
               }

               sway(model, (jab - cross) * 8.0F);
               lift(model, -Mth.abs(swing2) * 0.7F);
               break;
            case KICK:
               float kick = Mth.abs(Mth.sin(beat)) * amp;
               leg(model, side, -100.0F * kick, 0.0F, 10.0F * kick);
               one(model, -side, -60.0F * kick, 0.0F, out(-side, 30.0F));
               one(model, side, 30.0F * kick, 0.0F, out(side, 20.0F));
               lean(-14.0F * kick, 0.0F, 0.0F);
               break;
            case CHICKEN:
               float flap = Mth.sin(beat);
               arms(model, 0.0F, 0.0F, 64.0F + 24.0F * flap, 0.0F);
               head(model, 0.0F, 0.0F, 0.0F);
               headBob(model, flap * 10.0F, 12.0F);
               leg(model, flap > 0.0F ? 1.0F : -1.0F, -26.0F * Mth.abs(flap), 0.0F, 0.0F);
               sway(model, flap * 6.0F);
               break;
            case WORM:
               float wave = Mth.sin(beat);
               tilt(model, 80.0F, 0.0F, 0.0F, 24.0F);
               lift(model, -3.2F + 2.0F * wave);
               arms(model, -150.0F, 0.0F, 10.0F, 0.0F);
               head(model, -20.0F - 16.0F * wave, 0.0F, 0.0F);
               leg(model, 1.0F, 16.0F * wave, 0.0F, 4.0F);
               leg(model, -1.0F, 16.0F * wave, 0.0F, -4.0F);
               break;
            case FLIP:
               float spin = extra > 0.0F ? 0.0F : -360.0F * t;
               float wheel = extra > 0.0F ? 360.0F * t : 0.0F;
               tilt(model, spin, 0.0F, wheel, 14.0F);
               both(model, -160.0F, 0.0F, 20.0F);
               leg(model, 1.0F, -40.0F, 0.0F, 14.0F);
               leg(model, -1.0F, -10.0F, 0.0F, -14.0F);
               lift(model, -Mth.abs(Mth.sin(t * (float) Math.PI)) * 6.0F);
               break;
            case HANDSTAND:
               float wobble = Mth.sin(beat) * 5.0F * amp;
               tilt(model, 180.0F, 0.0F, wobble, 12.0F);
               both(model, -180.0F, 0.0F, 8.0F);
               leg(model, 1.0F, 0.0F, 0.0F, 12.0F + wobble);
               leg(model, -1.0F, 0.0F, 0.0F, -12.0F + wobble);
               head(model, -20.0F, 0.0F, 0.0F);
               break;
            case ZOMBIE:
               float shamble = Mth.sin(beat);
               both(model, -90.0F, 0.0F, 6.0F);
               leg(model, 1.0F, -20.0F * shamble, 0.0F, 0.0F);
               leg(model, -1.0F, 20.0F * shamble, 0.0F, 0.0F);
               head(model, 6.0F, shamble * 10.0F, 12.0F);
               lean(6.0F, 0.0F, shamble * 4.0F);
               break;
            case SWIM:
               float stroke = Mth.sin(beat);
               tilt(model, 84.0F, 0.0F, 0.0F, 24.0F);
               lift(model, -4.0F);
               one(model, 1.0F, -90.0F - 90.0F * stroke, 0.0F, 10.0F);
               one(model, -1.0F, -90.0F + 90.0F * stroke, 0.0F, -10.0F);
               leg(model, 1.0F, 14.0F * stroke, 0.0F, 4.0F);
               leg(model, -1.0F, -14.0F * stroke, 0.0F, -4.0F);
               head(model, -30.0F, 0.0F, 0.0F);
               break;
            case ROW: {
               float pull = Mth.sin(beat);
               if (extra > 0.0F) {
                  one(model, side, -70.0F + 10.0F * pull, side * -20.0F, out(side, 16.0F));
                  one(model, -side, -50.0F + 10.0F * pull, -side * -10.0F, out(-side, 12.0F));
                  head(model, -6.0F, side * -10.0F, 0.0F);
               } else {
                  arms(model, -80.0F + 34.0F * pull, -16.0F, 12.0F, 0.0F);
                  lean(16.0F - 14.0F * pull, 0.0F, 0.0F);
               }

               lift(model, 6.0F);
               leg(model, 1.0F, -86.0F, 0.0F, 8.0F);
               leg(model, -1.0F, -86.0F, 0.0F, -8.0F);
               break;
            }
            case DRIVE: {
               float turn = Mth.sin(beat) * 20.0F * amp;
               lift(model, 8.0F);
               leg(model, 1.0F, -88.0F, 0.0F, 10.0F);
               leg(model, -1.0F, -88.0F, 0.0F, -10.0F);
               one(model, 1.0F, -86.0F + turn, -14.0F, 16.0F);
               one(model, -1.0F, -86.0F - turn, 14.0F, -16.0F);
               head(model, 0.0F, turn * 0.5F, 0.0F);
               break;
            }
            case THROW:
               float wind = Mth.sin(beat);
               one(model, side, -120.0F + 90.0F * wind, side * -20.0F, out(side, 14.0F));
               one(model, -side, -40.0F, 0.0F, out(-side, 18.0F));
               lean(10.0F * wind, side * -12.0F * wind, 0.0F);
               head(model, -8.0F, 0.0F, 0.0F);
               break;
            case STRETCH:
               float stretchReach = 0.5F + 0.5F * Mth.cos(beat);
               switch ((int)extra) {
                  case 1:
                     one(model, 1.0F, -140.0F * stretchReach, -20.0F, 18.0F);
                     one(model, -1.0F, -120.0F, 30.0F, -30.0F);
                     head(model, -20.0F * stretchReach, 0.0F, 0.0F);
                     break label405;
                  case 2:
                     arms(model, -16.0F, 0.0F, 18.0F, 0.0F);
                     head(model, 16.0F, 10.0F, 16.0F);
                     lean(4.0F, 0.0F, 6.0F);
                     break label405;
                  case 3:
                     lean(70.0F * stretchReach, 0.0F, 0.0F);
                     arms(model, -10.0F, 0.0F, 10.0F, 0.0F);
                     break label405;
                  default:
                     both(model, -170.0F * stretchReach - 10.0F, 0.0F, 14.0F);
                     head(model, -16.0F * stretchReach, 0.0F, 0.0F);
                     lean(-8.0F * stretchReach, 0.0F, 0.0F);
                     break label405;
               }
            case TPOSE:
               arms(model, 0.0F, 0.0F, 90.0F, 0.0F);
               if (extra > 0.0F) {
                  float wobblex = Mth.sin(beat) * 8.0F;
                  tilt(model, 0.0F, 0.0F, wobblex, 24.0F);
                  leg(model, 1.0F, -20.0F - 10.0F * Mth.abs(wobblex) * 0.1F, 0.0F, 0.0F);
               }
               break;
            case CROSS_ARMS:
               float breath = Mth.sin(beat) * 2.0F;
               one(model, 1.0F, -14.0F, -80.0F, 62.0F + breath);
               one(model, -1.0F, -20.0F, 80.0F, -62.0F - breath);
               head(model, extra > 0.0F ? 12.0F : 0.0F, extra > 0.0F ? -14.0F : 0.0F, 0.0F);
               if (extra > 0.0F) {
                  tilt(model, 4.0F, -6.0F, 0.0F, 24.0F);
               }
               break;
            case HIPS:
               arms(model, -6.0F, -70.0F, 74.0F, 0.0F);
               if (extra > 0.0F) {
                  tilt(model, -14.0F, 0.0F, 0.0F, 24.0F);
                  head(model, -8.0F, 0.0F, 0.0F);
               }

               sway(model, Mth.sin(beat) * 4.0F);
               break;
            case FLEX:
               float squeeze = 0.5F + 0.5F * Mth.cos(beat);
               one(model, side == 0.0F ? 1.0F : side, -60.0F - 20.0F * squeeze, -100.0F, 70.0F);
               if (extra > 0.0F) {
                  one(model, -1.0F, -60.0F - 20.0F * squeeze, 100.0F, -70.0F);
               } else {
                  one(model, -1.0F, 10.0F, 0.0F, -16.0F);
               }

               head(model, 0.0F, extra > 0.0F ? 0.0F : side * -16.0F, 0.0F);
               tilt(model, 0.0F, extra > 0.0F ? 0.0F : side * 8.0F, 0.0F, 24.0F);
               break;
            case THUMB:
               one(model, side, extra > 0.0F ? -40.0F : -96.0F, side * -30.0F, out(side, extra > 0.0F ? 4.0F : 12.0F));
               one(model, -side, 10.0F, 0.0F, out(-side, -14.0F));
               head(model, extra > 0.0F ? 12.0F : -6.0F, side * -10.0F, 0.0F);
               break;
            case NOD:
               head(model, 14.0F * Mth.sin(beat) * amp, 0.0F, 0.0F);
               one(model, 1.0F, -14.0F, 0.0F, 16.0F);
               one(model, -1.0F, -14.0F, 0.0F, -16.0F);
               break;
            case SHAKE_HEAD:
               head(model, 0.0F, 24.0F * Mth.sin(beat) * amp, 0.0F);
               arms(model, -20.0F, -30.0F, 40.0F, 0.0F);
               break;
            case POINT:
               float hold = Mth.sin(beat) * 4.0F * amp;
               float pitch = extra == 1.0F ? -168.0F : (extra == 2.0F ? -20.0F : -92.0F);
               one(model, side, pitch + hold, side * -16.0F, out(side, extra == 2.0F ? 26.0F : 10.0F));
               one(model, -side, 8.0F, 0.0F, out(-side, -12.0F));
               head(model, extra == 1.0F ? -24.0F : (extra == 2.0F ? 20.0F : -2.0F), side * -12.0F, 0.0F);
         }

         lyfwclient$applyLean(model);
         lyfwclient$applyTilt(model);
         lyfwclient$applyLift(model);
         settle(model);
      }
   }

   private static void stand(PlayerModel model) {
      set(model.head, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      set(model.body, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      set(model.rightArm, -5.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      set(model.leftArm, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      set(model.rightLeg, -1.9F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      set(model.leftLeg, 1.9F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   private static void set(ModelPart part, float x, float y, float z, float pitch, float yaw, float roll) {
      part.x = x;
      part.y = y;
      part.z = z;
      part.xRot = pitch;
      part.yRot = yaw;
      part.zRot = roll;
   }

   private static void settle(PlayerModel model) {
      model.hat.resetPose();
      model.jacket.resetPose();
      model.rightSleeve.resetPose();
      model.leftSleeve.resetPose();
      model.rightPants.resetPose();
      model.leftPants.resetPose();
   }

   private static float out(float side, float roll) {
      return side >= 0.0F ? roll : -roll;
   }

   private static void one(PlayerModel model, float side, float pitch, float yaw, float roll) {
      ModelPart arm = side >= 0.0F ? model.rightArm : model.leftArm;
      arm.xRot = pitch * (float) (Math.PI / 180.0);
      arm.yRot = yaw * (float) (Math.PI / 180.0);
      arm.zRot = roll * (float) (Math.PI / 180.0);
   }

   private static void both(PlayerModel model, float pitch, float yaw, float roll) {
      arms(model, pitch, yaw, roll, 0.0F);
   }

   private static void arms(PlayerModel model, float pitch, float yaw, float roll, float offset) {
      one(model, 1.0F, pitch + offset, yaw, roll);
      one(model, -1.0F, pitch - offset, -yaw, -roll);
   }

   private static void leg(PlayerModel model, float side, float pitch, float yaw, float roll) {
      ModelPart leg = side >= 0.0F ? model.rightLeg : model.leftLeg;
      leg.xRot = pitch * (float) (Math.PI / 180.0);
      leg.yRot = yaw * (float) (Math.PI / 180.0);
      leg.zRot = roll * (float) (Math.PI / 180.0);
   }

   private static void head(PlayerModel model, float pitch, float yaw, float roll) {
      model.head.xRot = pitch * (float) (Math.PI / 180.0);
      model.head.yRot = yaw * (float) (Math.PI / 180.0);
      model.head.zRot = roll * (float) (Math.PI / 180.0);
   }

   private static void headBob(PlayerModel model, float by, float depth) {
      model.head.z -= by * 0.1F * depth * 0.1F;
      model.head.xRot += by * 0.4F * (float) (Math.PI / 180.0);
   }

   private static void sway(PlayerModel model, float degrees) {
      float shift = degrees * 0.06F;
      model.body.zRot += degrees * (float) (Math.PI / 180.0);
      model.head.zRot += degrees * 0.4F * (float) (Math.PI / 180.0);
      model.head.x -= shift;
      model.rightArm.x -= shift;
      model.leftArm.x -= shift;
   }

   private static void shoulders(PlayerModel model, float by) {
      model.rightArm.y += by;
      model.leftArm.y += by;
   }

   private static void lean(float pitch, float yaw, float roll) {
      PENDING[0] = PENDING[0] + pitch;
      PENDING[1] = PENDING[1] + yaw;
      PENDING[2] = PENDING[2] + roll;
   }

   private static void tilt(PlayerModel model, float pitch, float yaw, float roll, float pivotY) {
      PENDING[3] = PENDING[3] + pitch;
      PENDING[4] = PENDING[4] + yaw;
      PENDING[5] = PENDING[5] + roll;
      PENDING[6] = pivotY;
   }

   private static void lift(PlayerModel model, float by) {
      PENDING[7] = PENDING[7] + by;
   }

   private static void lyfwclient$applyLean(PlayerModel model) {
      if (PENDING[0] != 0.0F || PENDING[1] != 0.0F || PENDING[2] != 0.0F) {
         turn(new ModelPart[]{model.head, model.body, model.rightArm, model.leftArm}, PENDING[0], PENDING[1], PENDING[2], 12.0F);
      }
   }

   private static void lyfwclient$applyTilt(PlayerModel model) {
      if (PENDING[3] != 0.0F || PENDING[4] != 0.0F || PENDING[5] != 0.0F) {
         turn(parts(model), PENDING[3], PENDING[4], PENDING[5], PENDING[6]);
      }
   }

   private static void lyfwclient$applyLift(PlayerModel model) {
      if (PENDING[7] != 0.0F) {
         for (ModelPart part : parts(model)) {
            part.y = part.y + PENDING[7];
         }
      }
   }

   private static void turn(ModelPart[] parts, float pitch, float yaw, float roll, float pivotY) {
      Quaternionf spin = new Quaternionf().rotationZYX(roll * (float) (Math.PI / 180.0), yaw * (float) (Math.PI / 180.0), pitch * (float) (Math.PI / 180.0));

      for (ModelPart part : parts) {
         Vector3f at = new Vector3f(part.x, part.y - pivotY, part.z);
         spin.transform(at);
         part.x = at.x;
         part.y = at.y + pivotY;
         part.z = at.z;
         Vector3f angles = spin.mul(new Quaternionf().rotationZYX(part.zRot, part.yRot, part.xRot), new Quaternionf()).getEulerAnglesZYX(new Vector3f());
         part.xRot = angles.x;
         part.yRot = angles.y;
         part.zRot = angles.z;
      }
   }

   private static ModelPart[] parts(PlayerModel model) {
      return new ModelPart[]{model.head, model.body, model.rightArm, model.leftArm, model.rightLeg, model.leftLeg};
   }

   public static void pose(HumanoidModel<?> model, CosmeticsModule.Emote emote, float seconds) {
      if (model instanceof PlayerModel player) {
         pose(player, emote, seconds);
      }
   }

   static {
      for (String row : TABLE) {
         String[] parts = row.split(" ");

         try {
            STEPS.put(
               CosmeticsModule.Emote.valueOf(parts[0]),
               new EmoteMoves.Step(
                  EmoteMoves.Move.valueOf(parts[1]),
                  Float.parseFloat(parts[2]),
                  Float.parseFloat(parts[3]),
                  Float.parseFloat(parts[4]),
                  Float.parseFloat(parts[5])
               )
            );
         } catch (IllegalArgumentException var6) {
         }
      }

      PENDING = new float[9];
   }

   private static enum Move {
      WAVE,
      TWO_WAVE,
      SALUTE,
      BOW,
      REACH,
      CLAP,
      PUNCH,
      ARMS_UP,
      DAB,
      FLOSS,
      RUNNING_MAN,
      ROBOT,
      TWIST,
      DISCO,
      SWAY,
      MOONWALK,
      MACARENA,
      SPRINKLER,
      LAWNMOWER,
      HEADBANG,
      AIR_GUITAR,
      BREAKDANCE,
      SPIN,
      LAUGH,
      CRY,
      FACEPALM,
      SHRUG,
      HAND_TO,
      STOMP,
      SIT,
      KNEEL,
      LIE,
      PUSH_UP,
      SIT_UP,
      SQUAT,
      JACKS,
      RUN,
      BOX,
      KICK,
      CHICKEN,
      WORM,
      FLIP,
      HANDSTAND,
      ZOMBIE,
      SWIM,
      ROW,
      DRIVE,
      THROW,
      STRETCH,
      TPOSE,
      CROSS_ARMS,
      HIPS,
      FLEX,
      THUMB,
      NOD,
      SHAKE_HEAD,
      POINT;
   }

   private record Step(EmoteMoves.Move move, float speed, float amp, float side, float extra) {
   }
}
