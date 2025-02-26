package net.apple70cents.chattools;

import net.apple70cents.chattools.config.ConfigStorage;
import net.apple70cents.chattools.features.chatkeybindings.Macro;
import net.apple70cents.chattools.features.chatkeybindings.Repeat;
import net.apple70cents.chattools.utils.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;

/**
 * @author 70CentsApple
 */
public class ChatTools implements ModInitializer {

    public final static ConfigStorage DEFAULT_CONFIG = new ConfigStorage(true);
    public static ConfigStorage CONFIG;

    @Override
    public void onInitialize() {
        LoggerUtils.init();

        if (ConfigStorage.configFileExists()) {
            // Check and migrate if the config file is out of date.
            // This should be done before the config is loaded.
            ConfigMigrationUtils.checkAndMigrate();
        } else {
            // if the config file doesn't exist, create a new one with the default settings.
            DEFAULT_CONFIG.save();
        }

        CONFIG = new ConfigStorage(false).withDefault(DEFAULT_CONFIG.getHashmap());

        // show welcome message if needed
        ClientTickEvents.START_WORLD_TICK.register(client -> {
            if ((boolean) CONFIG.get("general.ShowWelcomeMessageEnabled")) {
                if (MinecraftClient.getInstance().player != null) {
                    MessageUtils.sendToNonPublicChat(((MutableText) TextUtils.trans("texts.welcomeMessage")).setStyle(TextUtils.WEBSITE_URL_STYLE));
                    LoggerUtils.info("[ChatTools] Shown welcome message.");
                    CONFIG.set("general.ShowWelcomeMessageEnabled", false);
                    CONFIG.save();
                }
            }
        });

        // register features
        ClientTickEvents.START_WORLD_TICK.register(client -> {
            if (!(boolean) CONFIG.get("general.ChatTools.Enabled")) {
                return;
            }
            Repeat.tick();
            if ((boolean) CONFIG.get("chatkeybindings.Macro.Enabled")) {
                Macro.tick();
            }
        });

        // register commands
        CommandRegistryUtils.register();

        Runnable runnable = () -> {
            if (DownloadUtils.shouldCheckIfFullyReady()) {
                if (!DownloadUtils.checkIfFullyReady()) {
                    DownloadUtils.startDownload();
                    LoggerUtils.info("[ChatTools] Not yet fully ready, downloading...");
                }
                LoggerUtils.info("[ChatTools] Initial download thread terminated.");
            } else {
                LoggerUtils.info("[ChatTools] No need to check addons readiness.");
            }
        };
        // Start the file download in a new thread
        Thread downloadThread = new Thread(runnable, "ChatTools-Download-Thread");
        downloadThread.start();
    }
}
