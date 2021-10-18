package dev.crystall.playernpclib.api.skin;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public record PlayerSkin(String value, String signature) {

  public String getValue() {
    return this.value;
  }

  public String getSignature() {
    return this.signature;
  }

}
