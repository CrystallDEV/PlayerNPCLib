package dev.crystall.playernpclib.api.skin;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class PlayerSkin {

  private final String value;
  private final String signature;

  public PlayerSkin(String value, String signature) {
    this.value = value;
    this.signature = signature;
  }

  public String getValue() {
    return this.value;
  }

  public String getSignature() {
    return this.signature;
  }
}
