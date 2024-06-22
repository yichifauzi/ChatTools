package net.apple70cents.chattools.mixins;

import net.apple70cents.chattools.ChatTools;
import net.apple70cents.chattools.features.general.ClickEventsPreviewer;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#if MC>=12000
import net.minecraft.client.gui.DrawContext;
//#else
//$$ import net.minecraft.client.item.TooltipContext;
//$$ import net.minecraft.client.gui.screen.Screen;
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
        return ClickEventsPreviewer.work(style);
    }
}
