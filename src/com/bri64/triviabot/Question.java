package com.bri64.triviabot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("WeakerAccess")
public class Question {

  private String question;
  private List<String> choices;
  private String answer;
  private Map<IUser, Boolean> guesses;

  public String getQuestion() {
    return question;
  }
  public List<String> getChoices() {
    return choices;
  }
  public String getAnswer() {
    return answer;
  }
  public int getCorrect() {
    return choices.indexOf(answer);
  }

  public Question(final String question, final List<String> choices, final String answer) {
    this.question = question;
    this.choices = choices;
    this.answer = answer;
    this.guesses = new HashMap<>();
  }



  public void guess(IUser user, int guess) {
    guesses.put(user, guess == getCorrect());
  }
  public boolean attempted(IUser user) {
    return guesses.containsKey(user);
  }
  public boolean correct(IUser user) {
    return guesses.get(user);
  }
  public boolean done(int players) {
    return guesses.size() == players;
  }

  public String whoCorrect() {
    return guesses.keySet().stream().filter(user -> guesses.get(user)).map(IUser::getName).collect(Collectors.joining(", "));
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(question + "\n");
    for (String choice : choices) {
      result.append(choice).append("\n");
    }
    return result.toString();
  }
}


