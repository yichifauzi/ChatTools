package net.apple70cents.chattools.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.apple70cents.chattools.ChatTools;
import net.minecraft.text.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

//#if MC>=12005
import net.minecraft.registry.BuiltinRegistries;
//#endif

/**
 * @author 70CentsApple
 */
public class TextUtils {
    public static final Style WEBSITE_URL_STYLE = Style.EMPTY.withUnderline(true)
                                                             .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://70centsapple.top/blogs/#/chat-tools-faq"))
                                                             .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ConfigScreenUtils.getTooltip("faq", "FAQ", null)));
    public static final String PREFIX = "key.chattools.";

    public static class MessageUnit {
        public Text message;
        public long unixTimestamp;

        public MessageUnit(Text message, long unixTimestamp) {
            this.message = message;
            this.unixTimestamp = unixTimestamp;
        }
    }

    // For a newly received message, the key is its hashcode and the value is its MessageUnit
    public static Map<String, MessageUnit> messageMap = new LinkedHashMap<>();

    /**
     * Generates a random string conducted by 0-9,a-z
     *
     * @param length the length
     * @return the random string
     */
    public static String generateRandomString(int length) {
        final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz";
        final Random RANDOM = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }

    public static String putMessageMap(Text text, long unixTimestamp) {
        int maxSize = ((Number) ChatTools.CONFIG.get("general.MaxHistoryLength")).intValue();
        while (messageMap.size() > maxSize) {
            // pop the first element
            messageMap.remove(messageMap.keySet().iterator().next());
        }

        // The `hashcode` is NOT REALLY a hashcode, but actually just a random string
        String hashcode = generateRandomString(6);
        int retries = 0;
        while (messageMap.containsKey(hashcode) && retries < 10) {
            hashcode = generateRandomString(6);
            retries++;
        }
        messageMap.put(hashcode, new MessageUnit(text, unixTimestamp));
        return hashcode;
    }

    public static MessageUnit getMessageMap(String hash) {
        try {
            return messageMap.get(hash);
        } catch (Exception e) {
            return null;
        }
    }


    public static Text literal(String str) {
        //#if MC>=11900
        return Text.literal(str);
        //#else
        //$$return new LiteralText(str);
        //#endif
    }

    public static Text transWithPrefix(String str, String prefix) {
        //#if MC>=11900
        return Text.translatable(prefix + str);
        //#else
        //$$return new TranslatableText(prefix + str);
        //#endif
    }

    public static Text transWithPrefix(String str, String prefix, Object... args) {
        //#if MC>=11900
        return Text.translatable(prefix + str, args);
        //#else
        //$$return new TranslatableText(prefix + str, args);
        //#endif
    }

    public static Text trans(String str, Object... args) {
        return transWithPrefix(str, PREFIX, args);
    }

    public static Text trans(String str) {
        return transWithPrefix(str, PREFIX);
    }

    public static Text of(String str) {
        return Text.of(str);
    }

    public static Text empty() {
        //#if MC>=11900
        return Text.empty();
        //#else
        //$$return of("");
        //#endif
    }

    /**
     * removes color codes in the string
     *
     * @param str the string
     * @return string with no color codes
     */
    public static String wash(String str) {
        return Pattern.compile("§.").matcher(str).replaceAll("");
    }

    public static String escapeColorCodes(String str) {
        return str.replace('&', '§').replace("\\§", "&");
    }

    public static String backEscapeColorCodes(String str) {
        return str.replace('§', '&');
    }

    public static Text textArray2text(List<Text> texts) {
        MutableText result = (MutableText) of("");
        for (Text text : texts) {
            result.append(text);
            if (text != texts.get(texts.size()-1)) {
                result.append(of("\n"));
            }
        }
        return result;
    }

    /**
     * replace a {@link MutableText}
     *
     * @param text      the text
     * @param oldString old string
     * @param newString new string
     * @return text after replacement
     */
    public static MutableText replaceText(MutableText text, String oldString, String newString) {
        //#if MC>=12005
        JsonElement jsonElement = new Text.Serializer(BuiltinRegistries.createWrapperLookup()).serialize(text, null, null);
        //#else
        //$$ JsonElement jsonElement = Text.Serialization.toJsonTree(text);
        //#endif
        replaceFieldValue(jsonElement, oldString, newString);
        //#if MC>=12005
        return new Text.Serializer(BuiltinRegistries.createWrapperLookup()).deserialize(jsonElement, null, null);
        //#else
        //$$ return Text.Serialization.fromJsonTree(jsonElement);
        //#endif
    }

    private static void replaceFieldValue(JsonElement jsonElement, String oldValue, String newValue) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> ele : jsonObject.entrySet()) {
                String key = ele.getKey();
                JsonElement value = ele.getValue();
                if (value.isJsonPrimitive() && value.getAsString().contains(oldValue)) {
                    jsonObject.addProperty(key, value.getAsString().replace(oldValue, newValue));
                } else {
                    replaceFieldValue(value, oldValue, newValue);
                }
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                replaceFieldValue(element, oldValue, newValue);
            }
        }
    }
}
