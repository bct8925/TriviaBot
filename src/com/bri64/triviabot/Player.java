package com.bri64.triviabot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class Player {
  private String name;
  private IChannel dm;

  public String getName() {
    return name;
  }
  public IChannel getDm() {
    return dm;
  }

  public Player(final IUser user) {
    this.name = user.getName();
    this.dm = user.getOrCreatePMChannel();
  }
}
