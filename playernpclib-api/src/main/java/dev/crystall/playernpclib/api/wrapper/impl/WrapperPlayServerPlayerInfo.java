package dev.crystall.playernpclib.api.wrapper.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerPlayerInfo;
import dev.crystall.playernpclib.api.wrapper.MinecraftVersions;
import java.util.List;
import java.util.Set;

/**
 * Shared wrapper for PLAYER_INFO packet across all Minecraft versions.
 *
 * VERSION-AWARE: Uses runtime detection for field index.
 * - Legacy (1.16-1.19): Uses index 0 for player data list
 * - Modern (1.20+): Uses index 1 for player data list
 *
 * This is the ONLY wrapper with version-specific logic.
 */
public class WrapperPlayServerPlayerInfo extends AbstractPacket implements BaseWrapperPlayServerPlayerInfo {

  public WrapperPlayServerPlayerInfo() {
    super(new PacketContainer(PacketType.Play.Server.PLAYER_INFO), PacketType.Play.Server.PLAYER_INFO);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerPlayerInfo(PacketContainer packet) {
    super(packet, PacketType.Play.Server.PLAYER_INFO);
  }

  @Override
  public Set<PlayerInfoAction> getActions() {
    return handle.getPlayerInfoActions().read(0);
  }

  @Override
  public void setActions(Set<PlayerInfoAction> value) {
    handle.getPlayerInfoActions().write(0, value);
  }

  @Override
  public List<PlayerInfoData> getData() {
    int index = getPlayerInfoDataIndex();
    return handle.getPlayerInfoDataLists().read(index);
  }

  @Override
  public void setData(List<PlayerInfoData> value) {
    int index = getPlayerInfoDataIndex();
    handle.getPlayerInfoDataLists().write(index, value);
  }

  /**
   * Get the version-specific field index for player info data list.
   *
   * @return 0 for legacy versions (1.16-1.19), 1 for modern versions (1.20+)
   * @throws IllegalStateException if NMS version not detected
   */
  private int getPlayerInfoDataIndex() {
    MinecraftVersions version = PlayerNPCLib.getDetectedNMSVersion();
    if (version == null) {
      throw new IllegalStateException("NMS version not detected - cannot determine player info data index");
    }
    return version.getPlayerInfoDataIndex();
  }
}
