package dev.crystall.playernpclib.nms_v1_19_R2.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import dev.crystall.playernpclib.api.wrapper.AbstractPacket;
import dev.crystall.playernpclib.api.wrapper.BaseWrapperPlayServerEntityMetadata;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerEntityMetadata extends AbstractPacket implements BaseWrapperPlayServerEntityMetadata {

  public WrapperPlayServerEntityMetadata() {
    super(new PacketContainer(PacketType.Play.Server.ENTITY_METADATA), PacketType.Play.Server.ENTITY_METADATA);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerEntityMetadata(PacketContainer packet) {
    super(packet, PacketType.Play.Server.ENTITY_METADATA);
  }

  @Override
  public int getEntityID() {
    return handle.getIntegers().read(0);
  }

  @Override
  public void setEntityID(int value) {
    handle.getIntegers().write(0, value);
  }

  @Override
  public Entity getEntity(World world) {
    return handle.getEntityModifier(world).read(0);
  }

  @Override
  public Entity getEntity(PacketEvent event) {
    return getEntity(event.getPlayer().getWorld());
  }

  @Override
  public List<WrappedWatchableObject> getMetadata() {
    return handle.getWatchableCollectionModifier().read(0);
  }

  @Override
  public void setMetadata(List<WrappedWatchableObject> value) {
    handle.getWatchableCollectionModifier().write(0, value);
  }

}
