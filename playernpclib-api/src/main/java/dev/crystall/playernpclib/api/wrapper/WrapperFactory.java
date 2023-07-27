package dev.crystall.playernpclib.api.wrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by CrystallDEV on 18/08/2021
 */
@Slf4j
@SuppressWarnings("unchecked")
public class WrapperFactory {

  private static final String BASE_PACKAGE = "dev.crystall.playernpclib";

  public static Class<? extends BaseWrapperPlayClientUseEntity> BASE_WRAPPER_PLAY_CLIENT_USE_ENTITY;
  public static Class<? extends BaseWrapperPlayServerAnimation> BASE_WRAPPER_PLAY_SERVER_ANIMATION;
  public static Class<? extends BaseWrapperPlayServerEntityDestroy> BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY;
  public static Class<? extends BaseWrapperPlayServerEntityEquipment> BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT;
  public static Class<? extends BaseWrapperPlayServerEntityHeadRotation> BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION;
  public static Class<? extends BaseWrapperPlayServerEntityMetadata> BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA;
  public static Class<? extends BaseWrapperPlayServerEntityTeleport> BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT;
  public static Class<? extends BaseWrapperPlayServerNamedEntitySpawn> BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN;
  public static Class<? extends BaseWrapperPlayServerPlayerInfo> BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO;
  public static Class<? extends BaseWrapperPlayServerScoreboardTeam> BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM;

  private static boolean HAS_ERROR = false;

  private WrapperFactory() {
  }

  public static boolean init() {
    if (PlayerNPCLib.getDetectedNMSVersion() == null) {
      return false;
    }
    resolvePlayClientUseEntity();
    resolvePlayServerAnimation();
    resolvePlayServerEntityDestroy();
    resolvePlayServerEntityEquipment();
    resolvePlayServerEntityHeadRotation();
    resolvePlayServerEntityMetadata();
    resolvePlayServerEntityTeleport();
    resolvePlayServerNamedEntitySpawn();
    resolvePlayServerPlayerInfo();
    resolvePlayServerPlayerInfoRemove();
    resolvePlayServerScoreboardTeam();
    return !HAS_ERROR;
  }

  public static void resolvePlayClientUseEntity() {
    BASE_WRAPPER_PLAY_CLIENT_USE_ENTITY = (Class<? extends BaseWrapperPlayClientUseEntity>) resolveWrapperForVersion(
      "WrapperPlayClientUseEntity");
  }

  public static void resolvePlayServerAnimation() {
    BASE_WRAPPER_PLAY_SERVER_ANIMATION = (Class<? extends BaseWrapperPlayServerAnimation>) resolveWrapperForVersion(
      "WrapperPlayServerAnimation");
  }

  public static void resolvePlayServerEntityDestroy() {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY = (Class<? extends BaseWrapperPlayServerEntityDestroy>) resolveWrapperForVersion(
      "WrapperPlayServerEntityDestroy");
  }

  public static void resolvePlayServerEntityEquipment() {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT = (Class<? extends BaseWrapperPlayServerEntityEquipment>) resolveWrapperForVersion(
      "WrapperPlayServerEntityEquipment");
  }

  public static void resolvePlayServerEntityHeadRotation() {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION = (Class<? extends BaseWrapperPlayServerEntityHeadRotation>) resolveWrapperForVersion(
      "WrapperPlayServerEntityHeadRotation");
  }

  public static void resolvePlayServerEntityMetadata() {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA = (Class<? extends BaseWrapperPlayServerEntityMetadata>) resolveWrapperForVersion(
      "WrapperPlayServerEntityMetadata");
  }

  public static void resolvePlayServerEntityTeleport() {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT = (Class<? extends BaseWrapperPlayServerEntityTeleport>) resolveWrapperForVersion(
      "WrapperPlayServerEntityTeleport");
  }

  public static void resolvePlayServerNamedEntitySpawn() {
    BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN = (Class<? extends BaseWrapperPlayServerNamedEntitySpawn>) resolveWrapperForVersion(
      "WrapperPlayServerNamedEntitySpawn");
  }

  public static void resolvePlayServerPlayerInfo() {
    BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO = (Class<? extends BaseWrapperPlayServerPlayerInfo>) resolveWrapperForVersion(
      "WrapperPlayServerPlayerInfo");
  }

  public static void resolvePlayServerScoreboardTeam() {
    BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM = (Class<? extends BaseWrapperPlayServerScoreboardTeam>) resolveWrapperForVersion(
      "WrapperPlayServerScoreboardTeam");
  }

  private static Class<?> parseWrapperClass(MinecraftVersions minecraftVersions, String wrapperClassName) {
    Class<?> loadedClass = null;
    String fullClassName = BASE_PACKAGE + ".nms_" + PlayerNPCLib.getDetectedNMSVersion().name() + "." + typePackageName + "." + className;
    try {
      loadedClass = WrapperFactory.class.getClassLoader().loadClass(fullClassName);
      log.info("Loaded {} class {}", typePackageName, fullClassName);
    } catch (ClassNotFoundException exception) {
      HAS_ERROR = true;
      log.error("Unable to load {} class {}", typePackageName, fullClassName);
    }
    return loadedClass;
  }

}
