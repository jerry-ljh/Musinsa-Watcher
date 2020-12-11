package com.musinsa.watcher.web;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

public class ProfileControllerTest {

  @Test
  public void real_profile이_조회된다() {
    String expectedProfile = "real";
    MockEnvironment env = new MockEnvironment();
    env.addActiveProfile(expectedProfile);
    env.addActiveProfile("real-db");
    env.addActiveProfile("oauth");

    ProfileController controller = new ProfileController(env);

    String profile = controller.profile();

    assertEquals(profile, expectedProfile);
  }

  @Test
  public void real_profile이_없으면_첫번째가_조회된다(){
    String expectedProfile = "oauth";
    MockEnvironment env = new MockEnvironment();
    env.addActiveProfile(expectedProfile);
    env.addActiveProfile("read-db");
    ProfileController controller = new ProfileController(env);

    String profile = controller.profile();

    assertEquals(profile, expectedProfile);
  }

  @Test
  public void active_profile이_없으면_default가_조회된다(){
    String expectedProfile = "default";
    MockEnvironment env = new MockEnvironment();
    ProfileController controller = new ProfileController(env);

    String profile = controller.profile();

    assertEquals(profile, expectedProfile);
  }
}