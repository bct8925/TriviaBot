package com.bri64.triviabot;

import com.bri64.triviabot.bots.TriviaBot;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

public class Game {
  private TriviaBot main;

  private Map<IUser, Integer> players;
  private List<Question> quiz;
  private int currentQuestion;
  private boolean inProgress = false;

  public Game(final TriviaBot main) {
    this.main = main;
    players = new HashMap<>();

    try {
      getQuestions();
    } catch (IOException ex) {
      quiz = new ArrayList<>();
    }
  }

  // Lobby
  public void joinLobby(IUser user) {
    if (!players.containsKey(user)) {
      players.put(user, 0);
      //clear(user.getOrCreatePMChannel());
      sendAll("[Join] - " + user.getName() + " joined!\n(Players: " + players.keySet().stream().map(IUser::getName).collect(Collectors.joining(", ")) + ")");
    }
  }
  private void send(String message, IUser user) {
    RequestBuffer.request(() -> BotUtils.sendMessage(message, user.getOrCreatePMChannel()));
  }
  private void sendAll(String message) {
    for (IUser user : players.keySet()) {
      send(message, user);
    }
  }
  public void clear(IChannel dm) {
    for (IMessage message : dm.getFullMessageHistory()) {
      if (message.getAuthor().isBot()) {
        RequestBuffer.request(message::delete);
      }
    }
  }
  private void clearAll() {
    for (IUser user : players.keySet()) {
      clear(user.getOrCreatePMChannel());
    }
  }

  // Game
  private void getQuestions() throws IOException {
    quiz = new ArrayList<>();

    String url = "https://opentdb.com/api.php?amount=10&encode=url3986";
    BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
    JSONObject json = new JSONObject(new BufferedReader(new InputStreamReader(in)).readLine());

    if (!json.get("response_code").equals(0)) {
      throw new IOException("Invalid response from TriviaDB!");
    }

    JSONArray results = json.getJSONArray("results");
    for (int i = 0; i < results.length(); i++) {
      JSONObject obj = results.getJSONObject(i);

      String question = URLDecoder.decode(obj.getString("question"), "utf8");
      String answer = URLDecoder.decode(obj.getString("correct_answer"), "utf8");
      List<String> choices = new ArrayList<>();
      JSONArray inc = obj.getJSONArray("incorrect_answers");
      for (int j = 0; j < inc.length(); j++) {
        choices.add(URLDecoder.decode(((String) inc.get(j)), "utf8"));
      }
      choices.add(answer);
      Collections.shuffle(choices);
      if (answer.equalsIgnoreCase("true")) {
        choices.remove(answer);
        choices.add(0, answer);
      } else if (answer.equalsIgnoreCase("false")) {
        choices.remove(answer);
        choices.add(1, answer);
      }
      quiz.add(new Question(question, choices, answer));
    }
  }
  public void startGame(IUser user) {
    if (players.isEmpty()) {
      BotUtils.sendMessage("[Error] - No players in lobby!", user.getOrCreatePMChannel());
    }
    else if (inProgress) {
      BotUtils.sendMessage("[Error] - Game already in progress!", user.getOrCreatePMChannel());
    }
    else {
      sendAll("[Start] - " + user.getName() + " started the game!");

      runGame();
    }
  }

  private void runGame() {
    // Actually start the game
    inProgress = true;
    new Thread(() -> {
      currentQuestion = 0;
      for (Question question : quiz) {
        // Pose question
        poseQuestion(currentQuestion, question);

        // Wait for answers
        while (!question.done(players.size())) {
          BotUtils.waiting();
        }

        // Show results
        for (IUser user : players.keySet()) {
          if (question.correct(user)) {
            players.put(user, players.get(user) + 1);
            send("[Correct] - The answer was: " + question.getAnswer() + "\n" + "Answered Correctly: " + question.whoCorrect(), user);
          } else {
            send("[Incorrect] - The answer was: " + question.getAnswer() + "\n" + "Answered Correctly: " + question.whoCorrect(), user);
          }
        }

        // Wait
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // Next question
        currentQuestion++;
      }

      for (IUser users : players.keySet()) {
        send("[Game Over] - You got " + players.get(users) + " questions correct!", users);
      }
      main.reset();

    }).start();
  }
  private void poseQuestion(int number, Question question) {
    for (IUser user : players.keySet()) {
      new Thread(() -> {
        final IMessage[] message = {null};
        int max = question.getChoices().size();
        boolean[] done = new boolean[max];

        RequestBuilder rb = new RequestBuilder(main.getClient());
        rb.shouldBufferRequests(true);
        rb.setAsync(true);
        rb.doAction(() -> {
          message[0] = user.getOrCreatePMChannel()
              .sendMessage("```[Question " + (number + 1) + "] - " + question.toString() + "```");
          return true;
        });
        for (int i = 0; i < max; i++) {
          int finalI = i;
          rb.andThen(() -> {
            message[0].addReaction(ReactionEmoji.of(BotUtils.EMOJI[finalI]));
            return true;
          });
        }

        rb.execute();
      }).start();
    }
  }
  public void react(IUser user, IReaction reaction) {
    if (inProgress && players.containsKey(user)) {
      Question question = quiz.get(currentQuestion);
      if (!question.attempted(user)) {
        int theirAnswer = Arrays.asList(BotUtils.EMOJI).indexOf(reaction.getEmoji().getName());
        question.guess(user, theirAnswer);
      }
    }
  }
}
