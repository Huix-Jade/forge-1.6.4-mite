package net.minecraft.client.gui;

import java.io.FileWriter;
import java.util.Date;
import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.mite.MITEConstant;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

public class GuiCreateWorld extends GuiScreen {
   private GuiScreen parentGuiScreen;
   private GuiTextField textboxWorldName;
   private GuiTextField textboxSeed;
   private String folderName;
   private String gameMode = "survival";
   private boolean generateStructures = true;
   private boolean commandsAllowed;
   private boolean commandsToggled;
   private boolean bonusItems;
   private boolean isHardcore;
   private boolean createClicked;
   private boolean moreOptions;
   private GuiButton buttonGameMode;
   private GuiButton moreWorldOptions;
   private GuiButton buttonGenerateStructures;
   private GuiButton buttonBonusItems;
   private GuiButton buttonWorldType;
   private GuiButton buttonAllowCommands;
   private GuiButton buttonCustomize;
   private String gameModeDescriptionLine1;
   private String gameModeDescriptionLine2;
   private String seed;
   private String localizedNewWorldText;
   private int worldTypeId = 2;
   public String generatorOptionsToUse = "";
   private static final String[] ILLEGAL_WORLD_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
   private GuiButton button_cancel;
   private GuiButton button_skills;
   private boolean are_skills_enabled;

   public GuiCreateWorld(GuiScreen par1GuiScreen) {
      this.parentGuiScreen = par1GuiScreen;
      this.seed = "";
      this.localizedNewWorldText = I18n.getString("selectWorld.newWorld");
   }

   public void updateScreen() {
      this.textboxWorldName.updateCursorCounter();
      this.textboxSeed.updateCursorCounter();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 154, this.height - 28, 152, 20, I18n.getString("selectWorld.create")));
      this.buttonList.add(this.button_cancel = new GuiButton(1, this.width / 2 + 2, this.height - 28, 152, 20, I18n.getString("gui.cancel")));
      this.buttonList.add(this.buttonGameMode = new GuiButton(2, this.width / 2 - 74, 114, 152, 20, I18n.getString("selectWorld.gameMode")));
      this.buttonList.add(this.moreWorldOptions = new GuiButton(3, this.width / 2 - 74, this.height - 52, 152, 20, I18n.getString("selectWorld.moreWorldOptions")));
      this.buttonList.add(this.buttonGenerateStructures = new GuiButton(4, this.width / 2 - 154, 114, 152, 20, I18n.getString("selectWorld.mapFeatures")));
      this.buttonGenerateStructures.drawButton = false;
      this.buttonList.add(this.buttonBonusItems = new GuiButton(7, this.width / 2 + 2, 150, 152, 20, I18n.getString("selectWorld.bonusItems")));
      this.buttonBonusItems.drawButton = false;
      this.buttonList.add(this.buttonWorldType = new GuiButton(5, this.width / 2 + 2, 114, 152, 20, I18n.getString("selectWorld.mapType")));
      this.buttonWorldType.drawButton = false;
      this.buttonList.add(this.buttonAllowCommands = new GuiButton(6, this.width / 2 - 154, 150, 152, 20, I18n.getString("selectWorld.allowCommands")));
      this.buttonAllowCommands.drawButton = false;
      this.buttonList.add(this.button_skills = new GuiButton(9, this.width / 2 + 2, 114, 152, 20, I18n.getString("selectWorld.professions")));
      this.button_skills.drawButton = false;
      this.buttonWorldType.enabled = false;
      this.buttonAllowCommands.enabled = false;
      this.buttonBonusItems.enabled = false;
      this.buttonList.add(this.buttonCustomize = new GuiButton(8, this.width / 2 + 5, 120, 150, 20, I18n.getString("selectWorld.customizeType")));
      this.buttonCustomize.drawButton = false;
      this.textboxWorldName = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
      this.textboxWorldName.setFocused(true);
      this.textboxWorldName.setText(this.localizedNewWorldText);
      this.textboxSeed = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
      this.textboxSeed.setText(this.seed);
      this.func_82288_a(this.moreOptions);
      this.makeUseableName();
      this.updateButtonText();
   }

   private void makeUseableName() {
      this.folderName = this.textboxWorldName.getText().trim();
      char[] var1 = ChatAllowedCharacters.allowedCharactersArray;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1[var3];
         this.folderName = this.folderName.replace(var4, '_');
      }

      if (MathHelper.stringNullOrLengthZero(this.folderName)) {
         this.folderName = "World";
      }

      this.folderName = func_73913_a(this.mc.getSaveLoader(), this.folderName);
   }

   private void updateButtonText() {
      this.buttonGameMode.displayString = I18n.getString("selectWorld.gameMode") + " " + I18n.getString("selectWorld.gameMode." + this.gameMode);
      this.gameModeDescriptionLine1 = I18n.getString("selectWorld.gameMode." + this.gameMode + ".line1");
      this.gameModeDescriptionLine2 = I18n.getString("selectWorld.gameMode." + this.gameMode + ".line2");
      this.buttonGenerateStructures.displayString = I18n.getString("selectWorld.mapFeatures") + " ";
      if (this.generateStructures) {
         this.buttonGenerateStructures.displayString = this.buttonGenerateStructures.displayString + I18n.getString("options.on");
      } else {
         this.buttonGenerateStructures.displayString = this.buttonGenerateStructures.displayString + I18n.getString("options.off");
      }

      this.buttonBonusItems.displayString = I18n.getString("selectWorld.bonusItems") + " ";
      this.buttonBonusItems.displayString = this.buttonBonusItems.displayString + I18n.getString("options.off");
      this.buttonWorldType.displayString = I18n.getString("selectWorld.mapType") + " " + I18n.getString(WorldType.worldTypes[this.worldTypeId].getTranslateName());
      this.buttonAllowCommands.displayString = I18n.getString("selectWorld.allowCommands") + " ";
      this.buttonAllowCommands.displayString = this.buttonAllowCommands.displayString + I18n.getString("options.off");
      this.button_skills.displayString = I18n.getString("selectWorld.professions") + " " + I18n.getString(this.are_skills_enabled ? "options.enabled" : "options.disabled");
   }

   public static String func_73913_a(ISaveFormat par0ISaveFormat, String par1Str) {
      par1Str = par1Str.replaceAll("[\\./\"]", "_");
      String[] var2 = ILLEGAL_WORLD_NAMES;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (par1Str.equalsIgnoreCase(var5)) {
            par1Str = "_" + par1Str + "_";
         }
      }

      while(par0ISaveFormat.getWorldInfo(par1Str) != null) {
         par1Str = par1Str + "-";
      }

      return par1Str;
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 1) {
            this.mc.displayGuiScreen(this.parentGuiScreen);
         } else if (par1GuiButton.id == 0) {
            this.mc.displayGuiScreen((GuiScreen)null);
            if (this.createClicked) {
               return;
            }

            this.createClicked = true;
            long var2 = (new Random()).nextLong();
            String var4 = this.textboxSeed.getText();
            if (!MathHelper.stringNullOrLengthZero(var4)) {
               try {
                  long var5 = Long.parseLong(var4);
                  if (var5 != 0L) {
                     var2 = var5;
                  }
               } catch (NumberFormatException var10) {
                  var2 = (long)var4.hashCode();
               }
            }

            EnumGameType var8 = EnumGameType.getByName(this.gameMode);
            WorldSettings var6 = new WorldSettings(var2, var8, this.generateStructures, this.isHardcore, WorldType.worldTypes[this.worldTypeId], this.are_skills_enabled);
            var6.func_82750_a(this.generatorOptionsToUse);
            if (this.bonusItems && !this.isHardcore) {
               var6.enableBonusChest();
            }

            if (this.commandsAllowed && !this.isHardcore) {
               var6.enableCommands();
            }

            this.mc.launchIntegratedServer(this.folderName, this.textboxWorldName.getText().trim(), var6);
            this.mc.statFileWriter.readStat(StatList.createWorldStat, 1);

            try {
               FileWriter fw = new FileWriter("MITE/world_seeds.txt", true);
               StringBuffer sb = new StringBuffer();
               sb.append(this.textboxWorldName.getText().trim());
               sb.append(": ");
               sb.append(var6.getSeed());
               sb.append(" (");
               sb.append(new Date());
               sb.append(")" + MITEConstant.newline);
               fw.append(sb.toString());
               fw.close();
            } catch (Exception var9) {
            }
         } else if (par1GuiButton.id == 3) {
            this.func_82287_i();
         } else if (par1GuiButton.id == 2) {
            if (this.gameMode.equals("survival")) {
               if (!this.commandsToggled) {
                  this.commandsAllowed = false;
               }

               this.isHardcore = false;
               this.gameMode = "hardcore";
               this.isHardcore = true;
               this.buttonAllowCommands.enabled = false;
               this.buttonBonusItems.enabled = false;
               this.updateButtonText();
            } else {
               if (!this.commandsToggled) {
                  this.commandsAllowed = false;
               }

               this.gameMode = "survival";
               this.updateButtonText();
               this.isHardcore = false;
            }

            this.updateButtonText();
         } else if (par1GuiButton.id == 4) {
            this.generateStructures = !this.generateStructures;
            this.updateButtonText();
         } else if (par1GuiButton.id == 7) {
            this.bonusItems = false;
            this.updateButtonText();
         } else if (par1GuiButton.id != 5) {
            if (par1GuiButton.id == 6) {
               this.commandsToggled = true;
               this.commandsAllowed = false;
               this.updateButtonText();
            } else if (par1GuiButton.id == 8) {
               WorldType.worldTypes[this.worldTypeId].onCustomizeButton(this.mc, this);
            } else if (par1GuiButton == this.button_skills) {
               this.are_skills_enabled = !this.are_skills_enabled;
               this.updateButtonText();
            }
         }
      }

   }

   private void func_82287_i() {
      this.func_82288_a(!this.moreOptions);
   }

   private void func_82288_a(boolean par1) {
      this.moreOptions = par1;
      this.buttonGameMode.drawButton = !this.moreOptions;
      this.buttonGenerateStructures.drawButton = this.moreOptions;
      this.buttonBonusItems.drawButton = this.moreOptions;
      this.buttonAllowCommands.drawButton = this.moreOptions;
      this.button_skills.drawButton = this.moreOptions;
      this.buttonWorldType.drawButton = this.moreOptions;
      this.button_skills.drawButton = false;
      this.buttonCustomize.drawButton = this.moreOptions && (WorldType.worldTypes[this.worldTypeId].isCustomizable());
      if (this.moreOptions) {
         this.moreWorldOptions.displayString = "Back";
         this.textboxSeed.setFocused(true);
      } else {
         this.moreWorldOptions.displayString = I18n.getString("selectWorld.moreWorldOptions");
         this.textboxWorldName.setFocused(true);
      }

   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == 1) {
         if (this.moreOptions) {
            this.actionPerformed(this.moreWorldOptions);
         } else {
            this.actionPerformed(this.button_cancel);
         }
      } else if (par2 == 15) {
         this.actionPerformed(this.moreWorldOptions);
         return;
      }

      if (this.textboxWorldName.isFocused() && !this.moreOptions) {
         this.textboxWorldName.textboxKeyTyped(par1, par2);
         this.localizedNewWorldText = this.textboxWorldName.getText();
      } else if (this.textboxSeed.isFocused() && this.moreOptions) {
         this.textboxSeed.textboxKeyTyped(par1, par2);
         this.seed = this.textboxSeed.getText();
      }

      if (par2 == 28 || par2 == 156) {
         this.actionPerformed((GuiButton)this.buttonList.get(0));
      }

      ((GuiButton)this.buttonList.get(0)).enabled = this.textboxWorldName.getText().length() > 0;
      this.makeUseableName();
   }

   protected void mouseClicked(int par1, int par2, int par3) {
      super.mouseClicked(par1, par2, par3);
      if (this.moreOptions) {
         this.textboxSeed.mouseClicked(par1, par2, par3);
      } else {
         this.textboxWorldName.mouseClicked(par1, par2, par3);
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("selectWorld.create"), this.width / 2, 20, 16777215);
      if (this.moreOptions) {
         this.drawString(this.fontRenderer, I18n.getString("selectWorld.enterSeed"), this.width / 2 - 100, 47, 10526880);
         this.drawString(this.fontRenderer, I18n.getString("selectWorld.seedInfo"), this.width / 2 - 100, 85, 10526880);
         this.drawString(this.fontRenderer, I18n.getString("selectWorld.mapFeatures.info"), this.width / 2 - 150, 136, 10526880);
         this.drawString(this.fontRenderer, I18n.getString("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, 10526880);
         this.textboxSeed.drawTextBox();
      } else {
         this.drawString(this.fontRenderer, I18n.getString("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
         this.drawString(this.fontRenderer, I18n.getString("selectWorld.resultFolder") + " " + this.folderName, this.width / 2 - 100, 85, 10526880);
         this.textboxWorldName.drawTextBox();
         this.drawString(this.fontRenderer, this.gameModeDescriptionLine1, this.width / 2 - 100, 149, 10526880);
         this.drawString(this.fontRenderer, this.gameModeDescriptionLine2, this.width / 2 - 100, 161, 10526880);
      }

      super.drawScreen(par1, par2, par3);
   }

   public void func_82286_a(WorldInfo par1WorldInfo) {
      this.localizedNewWorldText = I18n.getStringParams("selectWorld.newWorld.copyOf", par1WorldInfo.getWorldName());
      this.seed = par1WorldInfo.getSeed() + "";
      this.worldTypeId = par1WorldInfo.getTerrainType().getWorldTypeID();
      this.generatorOptionsToUse = par1WorldInfo.getGeneratorOptions();
      this.generateStructures = par1WorldInfo.isMapFeaturesEnabled();
      this.commandsAllowed = par1WorldInfo.areCommandsAllowed();
      if (par1WorldInfo.isHardcoreModeEnabled()) {
         this.gameMode = "hardcore";
      } else if (par1WorldInfo.getGameType().isSurvivalOrAdventure()) {
         this.gameMode = "survival";
      } else if (par1WorldInfo.getGameType().isCreative()) {
         this.gameMode = "creative";
      }

   }
}
