package net.apple70cents.chattools.mixins;

import net.apple70cents.chattools.ChatTools;
import net.apple70cents.chattools.utils.LoggerUtils;
import net.apple70cents.chattools.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

//#if MC>=12000
import net.minecraft.client.gui.DrawContext;
//#else
//$$ import net.minecraft.client.item.TooltipContext;
//#endif

//#if MC>=12000
@Mixin(DrawContext.class)
//#else
//$$ @Mixin(Screen.class)
//#endif
public abstract class DrawContextMixin {
    @ModifyVariable(method =
            //#if MC>=12000
            "drawHoverEvent"
            //#else
            //$$ "renderTextHoverEffect"
            //#endif
            , at = @At(value = "HEAD"), argsOnly = true)
    public Style modifyHoverEvent(Style style) {
        if (!(boolean) ChatTools.CONFIG.get("general.ChatTools.Enabled")) {
            return style;
        }
        if (!(boolean) ChatTools.CONFIG.get("general.PreviewClickEvents.Enabled")) {
            return style;
        }
        HoverEvent hoverEvent = style.getHoverEvent();
        Text textToAppend = getTextToAppend(style);

        // Return if already injected or with no text to append
        if (isModified(style) || textToAppend == null) {
            return style;
        }
        // Add two empty lines before it
        textToAppend = ((MutableText) TextUtils.of("\n\n")).append(textToAppend);
        if (hoverEvent == null) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textToAppend));
        } else {
            Text oldHoverText = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
            // Has Actions.SHOW_TEXT
            if (oldHoverText != null) {
                Text newHoverText = ((MutableText) oldHoverText.copy()).append(textToAppend);
                style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newHoverText));
            } else {
                HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
                HoverEvent.ItemStackContent itemContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
                if (entityContent != null) {
                    // Has Actions.SHOW_ENTITY
                    oldHoverText = TextUtils.textArray2text(entityContent.asTooltip());
                } else if (itemContent != null) {
                    // Has Actions.SHOW_ITEM
                    oldHoverText = TextUtils.textArray2text(
                            //#if MC>=12000
                            Screen.getTooltipFromItem(MinecraftClient.getInstance(), itemContent.asStack())
                            //#elseif MC>=11900
                            //$$ itemContent.asStack().getTooltip(MinecraftClient.getInstance().player, TooltipContext.ADVANCED)
                            //#else
                            //$$ itemContent.asStack().getTooltip(MinecraftClient.getInstance().player, TooltipContext.Default.ADVANCED)
                            //#endif
                    );
                }
                if (oldHoverText != null) {
                    Text newHoverText = ((MutableText) oldHoverText).append(textToAppend);
                    style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newHoverText));
                } else {
                    style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textToAppend));
                }
            }
        }
        return style;
    }

    @Unique
    private static Text getTextToAppend(Style style) {
        boolean hasInsertion = style.getInsertion() != null && !style.getInsertion().isBlank();
        boolean hasClickEvent = style.getClickEvent() != null;
        if (!hasInsertion && !hasClickEvent) {
            return null;
        }
        List<Text> texts = new ArrayList<>();
        texts.add(TextUtils.trans("texts.PreviewClickEvents.overall"));
        if (hasInsertion) {
            texts.add(TextUtils.trans("texts.PreviewClickEvents.insertion", style.getInsertion()));
        }
        if (hasClickEvent) {
            texts.add(TextUtils.trans("texts.PreviewClickEvents.clickEvent"));
            ClickEvent clickEvent = style.getClickEvent();
            Text value = TextUtils.of(clickEvent.getValue()).copy().formatted(Formatting.GREEN);
            //#if MC>=12000
            String action = clickEvent.getAction().asString();
            //#else
            //$$ String action = clickEvent.getAction().getName();
            //#endif
            switch (action) {
                case "open_url":
                    texts.add(TextUtils.trans("texts.PreviewClickEvents.clickEvent.openUrl", value));
                    break;
                case "open_file":
                    texts.add(TextUtils.trans("texts.PreviewClickEvents.clickEvent.openFile", value));
                    break;
                case "run_command":
                    if (value.getString().contains("/chattools get_message")) {
                        // skip it
                        texts.remove(texts.size() - 1);
                        // only the overall text is left
                        if (texts.size() <= 1) {
                            return null;
                        }
                    } else {
                        texts.add(TextUtils.trans("texts.PreviewClickEvents.clickEvent.runCommand", value));
                    }
                    break;
                case "suggest_command":
                    texts.add(TextUtils.trans("texts.PreviewClickEvents.clickEvent.suggestCommand", value));
                    break;
                case "change_page":
                    texts.add(TextUtils.trans("texts.PreviewClickEvents.clickEvent.changePage", value));
                    break;
                case "copy_to_clipboard":
                    texts.add(TextUtils.trans("texts.PreviewClickEvents.clickEvent.copyToClipboard", value));
                    break;
                default:
                    LoggerUtils.warn("[ChatTools] Unknown clickEvent action type: " + action);
            }
        }
        return TextUtils.textArray2text(texts);
    }

    @Unique
    private static Boolean isModified(Style style) {
        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent == null) {
            return false;
        }
        Text tooltip = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
        if (tooltip == null) {
            return false;
        }
        return tooltip.getString().contains(TextUtils.trans("texts.PreviewClickEvents.overall").getString());
    }
}
