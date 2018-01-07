package com.bri64.triviabot.bots;

import com.bri64.triviabot.BotUtils;
import com.bri64.triviabot.listeners.BotListener;
import org.apache.commons.lang3.StringEscapeUtils;
import sx.blah.discord.api.IDiscordClient;

public abstract class Bot {

    IDiscordClient              client;
    String                      symbol;
    private BotListener         botListener;

    public IDiscordClient       getClient() {
        return client;
    }
    public boolean              isReady() {
        return botListener.isReady();
    }
    public String               getSymbol() {
        return StringEscapeUtils.escapeJava(symbol);
    }

    void registerBotListener() {
        client.getDispatcher().registerListener(botListener = new BotListener());
    }
    abstract void registerListeners();

    void login() {
        client.login();
        while (!botListener.isReady()) {
            BotUtils.waiting();
        }
    }

    void init() {
      registerBotListener();
      login();
      BotUtils.log(this, "Initialization complete.");
    }
}
