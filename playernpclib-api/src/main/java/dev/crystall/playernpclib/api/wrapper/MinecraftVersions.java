package dev.crystall.playernpclib.api.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Minecraft version enumeration with version-aware helper methods.
 * The duplicate version numbers (v1_16_R1, v1_16_R2) are legacy related.
 *
 * Created by CrystallDEV on 01/09/2020
 * Enhanced for wrapper consolidation on 2025-10-07
 */
@AllArgsConstructor
@Getter
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
  v1_20_R1("1.20.1"),  // Protocol change: PLAYER_INFO packet structure changed
  v1_20_R2("1.20.2"),
  v1_20_R4("1.20.4"),
  v1_21_R7("1.21.7"),
  ;

  private final String serverVersion;

  /**
   * Parse server version string to enum.
   *
   * @param version Version string from Bukkit.getServer().getMinecraftVersion()
   * @return Matching enum value or null if not supported
   */
  public static MinecraftVersions parse(String version) {
    for (MinecraftVersions minecraftVersion : values()) {
      if (minecraftVersion.serverVersion.equals(version)) {
        return minecraftVersion;
      }
    }
    return null;
  }

  /**
   * Check if this version is modern (1.20+).
   * Modern versions have different protocol structure:
   * - PLAYER_INFO packet uses field index 1 instead of 0
   * - PLAYER_INFO_REMOVE packet exists
   * - DataWatcher API changed (sendDeathMetaData disabled)
   *
   * @return true if v1_20_R1 or later
   */
  public boolean isModern() {
    return this.ordinal() >= v1_20_R1.ordinal();
  }

  /**
   * Get the correct field index for PLAYER_INFO data list accessor.
   * This is the ONLY version-specific field index difference across all wrappers.
   *
   * @return 0 for legacy versions (1.16-1.19), 1 for modern versions (1.20+)
   */
  public int getPlayerInfoDataIndex() {
    return isModern() ? 1 : 0;
  }

  /**
   * Check if PLAYER_INFO_REMOVE packet is available.
   * This packet was introduced in Minecraft 1.20.
   *
   * @return true if v1_20_R1 or later
   */
  public boolean hasPlayerInfoRemove() {
    return isModern();
  }

  /**
   * Check if SCOREBOARD_TEAM packet works with current wrapper implementation.
   * MC 1.21.7 changed packet structure - Strings array only contains team name.
   * Nametag visibility and collision rule moved to Parameters object which
   * ProtocolLib 5.4.0 cannot properly access (Issues #3053, #3147, #2973).
   *
   * Verified error: "Field index 1 is out of bounds for length 1"
   *
   * @return true if SCOREBOARD_TEAM packet works with current implementation
   */
  public boolean hasCompatibleScoreboardTeam() {
    return this.ordinal() < v1_21_R7.ordinal();
  }

  /**
   * Check if ENTITY_TELEPORT packet uses modern structure (MC 1.21.3+).
   * MC 1.21.3 changed packet to use InternalStructure with PositionMoveRotation.
   *
   * Old structure: Separate Double fields for X/Y/Z, Byte fields for Yaw/Pitch
   * New structure: InternalStructure with Vector position, Float rotation
   *
   * Source: ProtocolLib Issue #3341
   *
   * @return true if version uses modern ENTITY_TELEPORT structure
   */
  public boolean hasModernEntityTeleport() {
    // MC 1.21.7 uses modern structure (changed in 1.21.3)
    // We only have v1_21_R7 enum, which is >= 1.21.3
    return this == v1_21_R7;
  }

  /**
   * Check if NAMED_ENTITY_SPAWN packet is available.
   * This packet was completely removed in MC 1.20.2 and replaced with SPAWN_ENTITY.
   *
   * Source: ProtocolLib Issues #2585, #2577
   * Error: "Could not find packet for type NAMED_ENTITY_SPAWN"
   *
   * @return true if version has NAMED_ENTITY_SPAWN packet
   */
  public boolean hasNamedEntitySpawn() {
    // NAMED_ENTITY_SPAWN removed in MC 1.20.2
    return this.ordinal() < v1_20_R2.ordinal();
  }
}
