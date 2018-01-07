package com.bri64.triviabot.bots;

import com.bri64.triviabot.BotUtils;
import com.bri64.triviabot.Game;
import com.bri64.triviabot.listeners.TriviaListener;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.ClientBuilder;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class TriviaBot extends Bot {

    private static String BotToken = "Mzk5NDIwNzM2MDg4ODM0MDY4.DTM1EA.qvJUjxGfTE76f86cVvpINLHm2nc";
    private TriviaListener triviaListener;

    private Game currentGame;
    public Game getCurrentGame() {
      return currentGame;
    }

    public TriviaBot(String symbol) {
        // Setup bot
        this.symbol = symbol;
        client = new ClientBuilder().withToken(BotToken).build();

        // Initialize base bot
        init();

        // Register listeners
        registerListeners();

        // Add emojis
        BotUtils.EMOJI = new String[] {
          EmojiManager.getForAlias("regional_indicator_symbol_a").getUnicode(),
          EmojiManager.getForAlias("regional_indicator_symbol_b").getUnicode(),
          EmojiManager.getForAlias("regional_indicator_symbol_c").getUnicode(),
          EmojiManager.getForAlias("regional_indicator_symbol_d").getUnicode()
        };

        // Reset game and lobby
        reset();
    }

    private void reset() {
      currentGame = new Game();
    }

    @Override
    void registerListeners() {
        client.getDispatcher().registerListener(triviaListener = new TriviaListener(this));
    }
}