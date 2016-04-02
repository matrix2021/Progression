package joshie.progression.gui.tree.buttons;

import joshie.progression.api.criteria.IProgressionCriteria;
import joshie.progression.api.criteria.IProgressionTab;
import joshie.progression.gui.core.FeatureTooltip;
import joshie.progression.gui.core.GuiCore;
import joshie.progression.gui.editors.*;
import joshie.progression.gui.editors.FeatureItemSelector.Position;
import joshie.progression.gui.filters.FilterSelectorItem;
import joshie.progression.handlers.APIHandler;
import joshie.progression.helpers.MCClientHelper;
import joshie.progression.json.Options;
import joshie.progression.lib.ProgressionInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ButtonTab extends ButtonBase implements ITextEditable, IItemSelectable {
    private IProgressionTab tab;

    public ButtonTab(IProgressionTab tab, int x, int y) {
        super(0, x, y, 25, 25, "");
        this.tab = tab;
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        boolean hovering = hovered = x >= xPosition && y >= yPosition && x < xPosition + width && y < yPosition + height;
        int k = getHoverState(hovering);
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.getTextureManager().bindTexture(ProgressionInfo.textures);
        int yTexture = GuiTreeEditor.INSTANCE.currentTab == tab ? 25 : 0;
        RenderHelper.disableStandardItemLighting();
        int xTexture = 206;
        if (xPosition == 0) xTexture = 231;
        GuiCore.INSTANCE.drawTexture(ProgressionInfo.textures, xPosition, yPosition, xTexture, yTexture, 25, 25);
        if (xPosition == 0) {
            GuiCore.INSTANCE.drawStack(tab.getStack(), xPosition + 2, yPosition + 5, 1F);
        } else GuiCore.INSTANCE.drawStack(tab.getStack(), xPosition + 7, yPosition + 5, 1F);

        boolean displayTooltip = false;
        if (MCClientHelper.isInEditMode()) {
            //displayTooltip = TextEditor.INSTANCE.getEditable() == this;
        }

        if (k == 2 || displayTooltip) {
            ArrayList<String> name = new ArrayList();
            String hidden = tab.isVisible() ? "" : "(Hidden)";
            name.add(TextEditor.INSTANCE.getText(this) + hidden);
            if (MCClientHelper.isInEditMode()) {
                name.add(EnumChatFormatting.GRAY + "(Sort Index) " + tab.getSortIndex());
                name.add(EnumChatFormatting.GRAY + "Shift + Click to rename");
                name.add(EnumChatFormatting.GRAY + "Ctrl + Click to select item icon");
                name.add(EnumChatFormatting.GRAY + "Alt + Click to make this tab the default");
                name.add(EnumChatFormatting.GRAY + "I + Click to hide/unhide");
                name.add(EnumChatFormatting.GRAY + "Arrow keys to move up/down");
                name.add(EnumChatFormatting.GRAY + "Delete + Click to delete");
                name.add(EnumChatFormatting.RED + "  Deleting a tab, deletes all criteria in it");
            }

            FeatureTooltip.INSTANCE.clear();
            FeatureTooltip.INSTANCE.addTooltip(name);
        }
    }

    @Override
    public void onClicked() {
        GuiCore.INSTANCE.clickedButton = true;
        //MCClientHelper.getPlayer().closeScreen(); //Close everything first
        //If the tab is already selected, then we should edit it instead        

        boolean donestuff = false;
        if (MCClientHelper.isInEditMode()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
                IProgressionTab newTab = GuiTreeEditor.INSTANCE.currentTab;
                if (tab == GuiTreeEditor.INSTANCE.currentTab) {
                    newTab = GuiTreeEditor.INSTANCE.previousTab;
                }

                if (newTab != null) {
                    if (!APIHandler.getTabs().containsKey(newTab.getUniqueID())) {
                        for (IProgressionTab tab : APIHandler.getTabs().values()) {
                            newTab = tab;
                            break;
                        }
                    }
                }

                GuiTreeEditor.INSTANCE.selected = null;
                GuiTreeEditor.INSTANCE.previous = null;
                GuiTreeEditor.INSTANCE.lastClicked = null;
                GuiTreeEditor.INSTANCE.currentTab = newTab;
                for (IProgressionCriteria c : tab.getCriteria()) {
                    APIHandler.removeCriteria(c.getUniqueID(), true);
                }

                APIHandler.getTabs().remove(tab.getUniqueID()); //Reopen after removing
                GuiCore.INSTANCE.setEditor(GuiTreeEditor.INSTANCE);
                return;
            }

            if (GuiScreen.isShiftKeyDown()) {
                TextEditor.INSTANCE.setEditable(this);
                donestuff = true;
            } else if (GuiScreen.isCtrlKeyDown() || FeatureItemSelector.INSTANCE.isVisible()) {
                FeatureItemSelector.INSTANCE.select(FilterSelectorItem.INSTANCE, this, Position.TOP);
            } else if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
                boolean current = tab.isVisible();
                tab.setVisibility(!current);
                donestuff = true;
            } else if (GuiScreen.isAltKeyDown()) {
                Options.settings.defaultTabID = tab.getUniqueID();
            }
        }

        if (!donestuff) {
            GuiTreeEditor.INSTANCE.previousTab = GuiTreeEditor.INSTANCE.currentTab;
            GuiTreeEditor.INSTANCE.currentTab = tab;
            GuiTreeEditor.INSTANCE.currentTabID = tab.getUniqueID(); //Reopen the gui
        }

        //Rebuild
        GuiTreeEditor.INSTANCE.rebuildCriteria();
    }

    @Override
    public void onNotClicked() {

    }

    @Override
    public String getTextField() {
        return tab.getDisplayName();
    }

    @Override
    public void setTextField(String str) {
        tab.setDisplayName(str);
    }

    @Override
    public void setObject(Object stack) {
        if (stack instanceof ItemStack) {
            tab.setStack(((ItemStack) stack).copy());
        }
    }
}