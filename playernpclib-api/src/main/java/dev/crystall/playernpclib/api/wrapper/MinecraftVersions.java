package dev.crystall.playernpclib.api.wrapper;

import lombok.AllArgsConstructor;

/**
 * The duplicate version numbers are legacy related.
 * Created by CrystallDEV on 01/09/2020
 */

@AllArgsConstructor
public enum MinecraftVersions {
  v1_16_R1("1.16.1"),
  v1_16_R2("1.16.2"),
  v1_16_R3("1.16.3"),
  v1_17_R1("1.17.1"),
  v1_18_R1("1.18.1"),
  v1_18_R2("1.18.2"),
  v1_19_R1("1.19.1"),
  v1_19_R2("1.19.2"),
  v1_19_R3("1.19.3"),
  v1_20_R1("1.20.1");

  private final String serverVersion;

  public static MinecraftVersions parse(String version) {
    for (MinecraftVersions minecraftVersion : values()) {
      if (minecraftVersion.serverVersion.equals(version)) {
        return minecraftVersion;
      }
    }
    return null;
  }

}
