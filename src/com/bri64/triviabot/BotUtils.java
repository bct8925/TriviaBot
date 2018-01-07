package com.bri64.triviabot;

import com.bri64.triviabot.bots.Bot;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import sx.blah.discord.util.MessageBuilder;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BotUtils {
    public static void log(Bot main, String message) {
        System.out.println(new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + ": [" + main.getClass().getSimpleName() + "] - " + message);
    }
    public static void waiting() {
        System.out.print("");
    }

    public static IMessage sendMessage(String message, IChannel channel) {
        return channel.sendMessage(message);
    }
    public static void deleteMessage(IMessage message) {
        if (!message.getChannel().isPrivate()) {
            //message.delete();
        }
    }

    public static String[] EMOJI;
}
