package dev.crystall.playernpclib.nms_v1_20_R2.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerPlayerInfoRemove;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import java.util.List;
import java.util.UUID;

public class WrapperPlayServerPlayerInfoRemove extends AbstractPacket implements BaseWrapperPlayServerPlayerInfoRemove {

  public WrapperPlayServerPlayerInfoRemove() {
    super(new PacketContainer(Server.PLAYER_INFO_REMOVE), Server.PLAYER_INFO_REMOVE);
    handle.getModifier().writeDefaults();
  }


  public WrapperPlayServerPlayerInfoRemove(PacketContainer packet) {
    super(packet, PacketType.Play.Server.PLAYER_INFO_REMOVE);
    handle.getModifier().writeDefaults();
  }

  @Override
  public List<UUID> getPlayerIds() {
    return handle.getUUIDLists().read(0);
  }

  @Override
  public void setPlayerIds(List<UUID> value) {
    handle.getUUIDLists().write(0, value);
  }

  @Override
  public void addPlayerId(UUID playerId) {
    handle.getUUIDLists().read(0).add(playerId);
  }

  @Override
  public void removePlayerId(UUID playerId) {
    handle.getUUIDLists().read(0).remove(playerId);
  }

}
