package com.deos.travel_anchors.gui;

import com.deos.travel_anchors.TravelAnchors;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.moddingx.libx.render.RenderHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelAnchorScreen extends AbstractContainerScreen<TravelAnchorMenu> {

    private static final ResourceLocation GUI_TEXTURE = TravelAnchors.location("textures/gui/travel_anchor.png");
    private static final int COLOR_RED = ChatFormatting.RED.getColor() != null ? ChatFormatting.RED.getColor() : 0xB31616;
    private static final int COLOR_GRAY = ChatFormatting.DARK_GRAY.getColor() != null ? ChatFormatting.DARK_GRAY.getColor() : 0x404040;

    private EditBox textFieldWidget;
    private Button saveButton;

    public TravelAnchorScreen(TravelAnchorMenu screenMenu, Inventory inv, Component titleIn) {
        super(screenMenu, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        this.textFieldWidget = new EditBox(this.font, this.width / 2 - 72, this.height / 2 - 63, 100, 16, Component.empty());
        this.textFieldWidget.setMaxLength(128);
        this.textFieldWidget.setFocused(true);
        if(this.menu.getBlockEntity() != null){
            this.textFieldWidget.setValue(this.menu.getBlockEntity().getName());
        }

        this.saveButton = Button.builder(LangHolder.GUI_BUTTON_SAVE_LABEL.getComponent(), btn -> {
            TravelAnchors.getNetwork().sendLock(this.menu.getLevel(), this.menu.getPos());
            this.onClose();
        }).bounds(this.width / 2 + 33, this.height / 2 - 65, 44, 20).build();
        this.saveButton.active = false;

        this.textFieldWidget.setResponder(str -> {
            this.saveButton.active = !str.trim().isEmpty();
        });

        this.addRenderableWidget(this.saveButton);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderHelper.resetColor();
        graphics.blit(
                GUI_TEXTURE,
                (this.width - this.imageWidth) / 2,
                (this.height - this.imageHeight) / 2,
                0,
                0,
                this.imageWidth,
                this.imageHeight
        );
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        String title = this.title.getString();
        graphics.drawString(
                this.font,
                title,
                this.imageWidth / 2 - this.font.width(title) / 2,
                6,
                COLOR_GRAY,
                false
        );
        boolean empty = this.textFieldWidget.getValue().trim().isEmpty();
        graphics.drawString(this.font,
                this.playerInventoryTitle.getString(),
                8,
                this.imageHeight - 126,
                empty ? COLOR_RED : COLOR_GRAY,
                false
        );
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        this.textFieldWidget.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
        return this.textFieldWidget;
    }

    @Override
    public void removed() {
        super.removed();
        if (Minecraft.getInstance().level != null) {
            TravelAnchors.getNetwork().sendNameChange(this.menu.getLevel(), this.menu.getPos(), this.textFieldWidget.getValue().trim());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mapping = InputConstants.getKey(keyCode, scanCode);
        //noinspection ConstantConditions
        if (keyCode != 256 && (this.minecraft.options.keyInventory.isActiveAndMatches(mapping)
                || this.minecraft.options.keyDrop.isActiveAndMatches(mapping))) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
