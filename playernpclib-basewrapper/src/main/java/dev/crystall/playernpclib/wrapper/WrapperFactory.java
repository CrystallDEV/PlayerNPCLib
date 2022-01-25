package dev.crystall.playernpclib.wrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by CrystallDEV on 18/08/2021
 */
@Slf4j
@SuppressWarnings("unchecked")
public class WrapperFactory {

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

  public static boolean init(MinecraftVersions minecraftVersions) {
    if (minecraftVersions == null) {
      return false;
    }
    resolvePlayClientUseEntity(minecraftVersions);
    resolvePlayServerAnimation(minecraftVersions);
    resolvePlayServerEntityDestroy(minecraftVersions);
    resolvePlayServerEntityEquipment(minecraftVersions);
    resolvePlayServerEntityHeadRotation(minecraftVersions);
    resolvePlayServerEntityMetadata(minecraftVersions);
    resolvePlayServerEntityTeleport(minecraftVersions);
    resolvePlayServerNamedEntitySpawn(minecraftVersions);
    resolvePlayServerPlayerInfo(minecraftVersions);
    resolvePlayServerScoreboardTeam(minecraftVersions);
    return !HAS_ERROR;
  }

  public static void resolvePlayClientUseEntity(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_CLIENT_USE_ENTITY = (Class<? extends BaseWrapperPlayClientUseEntity>) parseWrapperClass(minecraftVersions, "WrapperPlayClientUseEntity");
  }

  public static void resolvePlayServerAnimation(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_ANIMATION = (Class<? extends BaseWrapperPlayServerAnimation>) parseWrapperClass(minecraftVersions, "WrapperPlayServerAnimation");
  }

  public static void resolvePlayServerEntityDestroy(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY = (Class<? extends BaseWrapperPlayServerEntityDestroy>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerEntityDestroy");
  }

  public static void resolvePlayServerEntityEquipment(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT = (Class<? extends BaseWrapperPlayServerEntityEquipment>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerEntityEquipment");
  }

  public static void resolvePlayServerEntityHeadRotation(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION = (Class<? extends BaseWrapperPlayServerEntityHeadRotation>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerEntityHeadRotation");
  }

  public static void resolvePlayServerEntityMetadata(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA = (Class<? extends BaseWrapperPlayServerEntityMetadata>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerEntityMetadata");
  }

  public static void resolvePlayServerEntityTeleport(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT = (Class<? extends BaseWrapperPlayServerEntityTeleport>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerEntityTeleport");
  }

  public static void resolvePlayServerNamedEntitySpawn(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN = (Class<? extends BaseWrapperPlayServerNamedEntitySpawn>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerNamedEntitySpawn");
  }

  public static void resolvePlayServerPlayerInfo(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO = (Class<? extends BaseWrapperPlayServerPlayerInfo>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerPlayerInfo");
  }

  public static void resolvePlayServerScoreboardTeam(MinecraftVersions minecraftVersions) {
    BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM = (Class<? extends BaseWrapperPlayServerScoreboardTeam>) parseWrapperClass(minecraftVersions,
      "WrapperPlayServerScoreboardTeam");
  }

  private static Class<?> parseWrapperClass(MinecraftVersions minecraftVersions, String wrapperClassName) {
    Class<?> loadedClass = null;
    String className = "dev.crystall.playernpclib.nms_" + minecraftVersions.name() + ".wrappers." + wrapperClassName;
    try {
      loadedClass = WrapperFactory.class.getClassLoader().loadClass(className);
      log.info("Loaded wrapper class {}", className);
    } catch (ClassNotFoundException exception) {
      HAS_ERROR = true;
      log.error("Unable to load wrapper class {}", className);
    }
    return loadedClass;
  }

}
