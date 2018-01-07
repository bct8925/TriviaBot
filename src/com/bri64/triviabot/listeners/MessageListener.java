package com.bri64.triviabot.listeners;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

abstract class MessageListener {
    String help;

    @EventSubscriber
    public abstract void onMention(MentionEvent event);

    @EventSubscriber
    public abstract void onMessage(MessageReceivedEvent event);
}
