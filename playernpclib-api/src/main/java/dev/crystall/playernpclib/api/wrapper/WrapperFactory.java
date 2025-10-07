package dev.crystall.playernpclib.api.wrapper;

import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.wrapper.impl.*;
import dev.crystall.playernpclib.manager.PacketManager;
import dev.crystall.playernpclib.manager.impl.SharedPacketManagerImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory for wrapper class references and PacketManager instantiation.
 *
 * SIMPLIFIED: Direct class references instead of dynamic loading.
 * All wrappers are now shared implementations in the impl package.
 *
 * Created by CrystallDEV on 18/08/2021
 * Simplified for wrapper consolidation on 2025-10-07
 */
@Slf4j
public class WrapperFactory {

  // Wrapper class references (loaded directly, no reflection)
  public static Class<? extends BaseWrapperPlayClientUseEntity> BASE_WRAPPER_PLAY_CLIENT_USE_ENTITY;
  public static Class<? extends BaseWrapperPlayServerAnimation> BASE_WRAPPER_PLAY_SERVER_ANIMATION;
  public static Class<? extends BaseWrapperPlayServerEntityDestroy> BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY;
  public static Class<? extends BaseWrapperPlayServerEntityEquipment> BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT;
  public static Class<? extends BaseWrapperPlayServerEntityHeadRotation> BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION;
  public static Class<? extends BaseWrapperPlayServerEntityMetadata> BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA;
  public static Class<? extends BaseWrapperPlayServerEntityTeleport> BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT;
  public static Class<? extends BaseWrapperPlayServerNamedEntitySpawn> BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN;
  public static Class<? extends BaseWrapperPlayServerPlayerInfo> BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO;
  public static Class<? extends BaseWrapperPlayServerPlayerInfoRemove> BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO_REMOVE;
  public static Class<? extends BaseWrapperPlayServerScoreboardTeam> BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM;

  private WrapperFactory() {
    // Utility class, no instantiation
  }

  /**
   * Initialize all wrapper class references.
   *
   * @return true if initialization successful, false if version not detected
   */
  public static boolean init() {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null) {
      log.error("Cannot initialize WrapperFactory: NMS version not detected");
      return false;
    }

    log.info("Initializing shared wrappers for version {}", version.getServerVersion());

    // Direct class references - no dynamic loading needed
    BASE_WRAPPER_PLAY_CLIENT_USE_ENTITY = WrapperPlayClientUseEntity.class;
    BASE_WRAPPER_PLAY_SERVER_ANIMATION = WrapperPlayServerAnimation.class;
    BASE_WRAPPER_PLAY_SERVER_ENTITY_DESTROY = WrapperPlayServerEntityDestroy.class;
    BASE_WRAPPER_PLAY_SERVER_ENTITY_EQUIPMENT = WrapperPlayServerEntityEquipment.class;
    BASE_WRAPPER_PLAY_SERVER_ENTITY_HEAD_ROTATION = WrapperPlayServerEntityHeadRotation.class;
    BASE_WRAPPER_PLAY_SERVER_ENTITY_METADATA = WrapperPlayServerEntityMetadata.class;
    BASE_WRAPPER_PLAY_SERVER_ENTITY_TELEPORT = WrapperPlayServerEntityTeleport.class;
    BASE_WRAPPER_PLAY_SERVER_NAMED_ENTITY_SPAWN = WrapperPlayServerNamedEntitySpawn.class;
    BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO = WrapperPlayServerPlayerInfo.class;
    BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM = WrapperPlayServerScoreboardTeam.class;

    // Conditional loading for modern versions (1.20+)
    if (version.hasPlayerInfoRemove()) {
      BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO_REMOVE = WrapperPlayServerPlayerInfoRemove.class;
      log.info("Loaded PLAYER_INFO_REMOVE wrapper (modern version)");
    } else {
      BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO_REMOVE = null;
      log.info("Skipped PLAYER_INFO_REMOVE wrapper (legacy version)");
    }

    log.info("Successfully initialized {} shared wrappers",
        BASE_WRAPPER_PLAY_SERVER_PLAYER_INFO_REMOVE == null ? 10 : 11);

    return true;
  }

  /**
   * Create PacketManager instance.
   *
   * @return SharedPacketManagerImpl instance
   */
  public static PacketManager createPacketManager() {
    log.info("Creating shared PacketManager implementation");
    return new SharedPacketManagerImpl();
  }
}
