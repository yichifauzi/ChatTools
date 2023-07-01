package net.apple70cents.chattools.mixin;

import net.apple70cents.chattools.config.ModClothConfig;
import net.apple70cents.chattools.features.chatbubbles.BubbleRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (ModClothConfig.get().chatBubblesEnabled) {
            BubbleRenderer.render(entity, matrices, vertexConsumers);
        }
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At(value = "HEAD", ordinal = 0), argsOnly = true)
    public Text nickHiderChangeLabel(Text text) {
        ModClothConfig config = ModClothConfig.get();
        if (!config.nickHiderSettings.nickHiderEnabled) {
            return text;
        } else if(MinecraftClient.getInstance().player == null){
            return text;
        } else if (text.getString().contains(MinecraftClient.getInstance().player.getName().getString())){
            text = Text.of(text.getString().replace(MinecraftClient.getInstance().player.getName().getString(), // 替换实体nametag中的玩家名称（如果有）
                    config.nickHiderSettings.nickHiderText.replace('&', '§').replace("\\§", "&")));
        }
        return text;
    }

}
