package dev.crystall.playernpclib.nms_v1_17_R1.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerPlayerInfo;
import java.util.List;
import java.util.Set;

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
    return handle.getPlayerInfoDataLists().read(0);
  }

  @Override
  public void setData(List<PlayerInfoData> value) {
    handle.getPlayerInfoDataLists().write(0, value);
  }

}
