package com.musinsa.watcher.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

@RequiredArgsConstructor
@RestController
public class ProfileController {

  private final Environment env;

  @GetMapping("/api/profile")
  public String profile() {
    List<String> profiles = Arrays.asList(env.getActiveProfiles());
    List<String> realProfiles = Arrays.asList("real1", "real2");
    String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);
    return profiles.stream().filter(realProfiles::contains).findAny().orElse(defaultProfile);
  }
}
