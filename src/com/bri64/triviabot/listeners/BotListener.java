package com.bri64.triviabot.listeners;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class BotListener {

    private boolean     ready = false;

    public boolean      isReady() {
        return ready;
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        ready = true;
    }

}
