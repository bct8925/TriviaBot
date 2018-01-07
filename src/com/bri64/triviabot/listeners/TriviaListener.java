package com.bri64.triviabot.listeners;

import com.bri64.triviabot.BotUtils;
import com.bri64.triviabot.bots.TriviaBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("unused")
public class TriviaListener extends MessageListener {

  private TriviaBot main;

  public TriviaListener(TriviaBot main) {
      this.main = main;
      this.help = "```" +
        "TriviaBot:\n" +
        "\t@TriviaBot = Help Command\n" +
        "\t~help = Help Command\n";
  }

  @EventSubscriber
  public void onMention(MentionEvent event) {
      if (!event.getMessage().getContent().contains("@everyone") && !event.getMessage().getContent().contains("@here")) {
        IUser user = event.getMessage().getAuthor();
        IChannel channel = user.getOrCreatePMChannel();
        BotUtils.deleteMessage(event.getMessage());
        BotUtils.sendMessage(help, channel);
      }
  }

  @EventSubscriber
  public void onMessage(MessageReceivedEvent event) {
      if (main.isReady() && !event.getMessage().getAuthor().isBot()) {
        IUser user = event.getMessage().getAuthor();
        String message = event.getMessage().getContent();

        // Help
        if (message.equalsIgnoreCase(main.getSymbol() + "help")) {
          IChannel channel = user.getOrCreatePMChannel();
          BotUtils.deleteMessage(event.getMessage());
          BotUtils.sendMessage(help, channel);
        }

        // Ready
        else if (message.equalsIgnoreCase(main.getSymbol() + "ready")) {
          main.getCurrentGame().joinLobby(user);
        }

        // Start
        else if (message.equalsIgnoreCase(main.getSymbol() + "start")) {
          main.getCurrentGame().startGame(user);
        }

        // Clear
        else if (message.equalsIgnoreCase(main.getSymbol() + "clear")) {
          main.getCurrentGame().clear(user.getOrCreatePMChannel());
        }

        // Bad command
        else if (message.matches("^[" + main.getSymbol() + "][^" + main.getSymbol() + "].*")) {
          BotUtils.deleteMessage(event.getMessage());
          BotUtils.sendMessage(event.getAuthor().mention() + " \"" + message + "\" is not a valid command!", event.getChannel());
        }
      }
  }

  @EventSubscriber
  public void onReaction(ReactionAddEvent event) {
    if (!event.getUser().isBot()) {
      main.getCurrentGame().react(event.getUser(), event.getReaction());
    }
  }

}