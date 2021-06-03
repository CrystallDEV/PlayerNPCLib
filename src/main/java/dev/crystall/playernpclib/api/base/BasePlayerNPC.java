package dev.crystall.playernpclib.api.base;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Preconditions;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.skin.PlayerSkin;
import dev.crystall.playernpclib.manager.EntityManager;
import dev.crystall.playernpclib.manager.PacketManager;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public abstract class BasePlayerNPC {

  private final String internalName;
  private String displayName;
  private String subName;
  private final UUID uuid = UUID.randomUUID();
  private boolean isSpawned = false;
  private final Map<ItemSlot, ItemStack> itemSlots = new EnumMap<>(ItemSlot.class);
  private final Hologram hologram;

  /**
   * The id the entity will be registered with at the server
   */
  protected int entityId;
  protected Location location;
  protected Location eyeLocation;
  protected PlayerSkin playerSkin;

  protected BasePlayerNPC(String displayName, Location location) {
    this.location = location;
    this.entityId = EntityManager.tickAndGetCounter();
    this.hologram = HologramsAPI.createHologram(PlayerNPCLib.getPlugin(), this.location.clone().add(0, 2.5, 0));
    this.hologram.getVisibilityManager().setVisibleByDefault(false);
    this.internalName = uuid.toString().substring(0, 16);
    setDisplayName(displayName);
  }

  protected BasePlayerNPC(String displayName, Location location, String subName) {
    this(displayName, location);
    setSubName(subName);
  }

  public void spawn() {
    this.hologram.getVisibilityManager().resetVisibilityAll();
    for (Player player : PlayerNPCLib.getPlugin().getServer().getOnlinePlayers()) {
      show(player);
    }
    isSpawned = true;
  }

  public void remove() {
    if (!isSpawned) {
      return;
    }

    isSpawned = false;

    if (this.hologram != null) {
      this.hologram.delete();
    }

    for (Player player : location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE)) {
      PacketManager.sendDeathMetaData(player, this);
    }
  }

  public void update(Player player) {
    hide(player);
    show(player);
  }

  public void show(Player player) {
    PlayerNPCLib.getEntityHider().setVisibility(player, getEntityId(), true);
    PacketManager.sendNPCCreatePackets(player, this);
    PacketManager.sendEquipmentPackets(player, this);
    PacketManager.sendScoreBoardTeamPacket(player, this);
    if (hologram != null) {
      updateDisplayName();
      hologram.getVisibilityManager().showTo(player);
    }
  }

  public void hide(Player player) {
    PacketManager.sendHidePackets(player, this);
    PlayerNPCLib.getEntityHider().setVisibility(player, getEntityId(), false);
    if (hologram != null) {
      hologram.getVisibilityManager().hideTo(player);
    }
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    updateDisplayName();
  }

  public void setSubName(String subName) {
    this.subName = subName;
    updateDisplayName();
  }

  public void updateDisplayName() {
    if (hologram != null) {
      hologram.clearLines();
      if (displayName != null && !displayName.isEmpty()) {
        hologram.insertTextLine(0, displayName);
      }
      if (subName != null && !subName.isEmpty()) {
        hologram.insertTextLine(1, subName);
      }
    }
  }

  /**
   * Plays the given animation to all nearby players
   *
   * @param animationId the id of the animation to play
   */
  public void playAnimation(int animationId) {
    if (!isSpawned) {
      PlayerNPCLib.getPlugin().getLogger()
          .info(String.format("Unable to play animation for npc: %s-%s! NPC not spawned", this.getDisplayName(), this.getUuid()));
      return;
    }
    for (Player player : location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE)) {
      PacketManager.sendAnimationPacket(player, this, animationId);
    }
  }

  public WrappedGameProfile getGameProfile() {
    WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, internalName);
    if (playerSkin != null) {
      wrappedGameProfile.getProperties().get("textures").clear();
      wrappedGameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", playerSkin.getValue(), playerSkin.getSignature()));
    }
    return wrappedGameProfile;
  }

  /**
   * Sets an item for the given slot
   *
   * @param slot the slot to update the item for
   * @param itemStack the itemstack to set
   */
  public void setItem(ItemSlot slot, ItemStack itemStack) {
    Preconditions.checkNotNull(itemStack, "itemStack cannot be NULL.");
    Preconditions.checkNotNull(slot, "slot cannot be NULL.");

    itemSlots.put(slot, itemStack);
    if (isSpawned) {
      for (Player player : location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE)) {
        PacketManager.sendEquipmentPackets(player, this);
      }
    }
  }

  public void setPlayerSkin(PlayerSkin playerSkin) {
    this.playerSkin = playerSkin;
    if (isSpawned) {
      for (Player player : location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE)) {
        this.update(player);
      }
    }
  }

  public void setLocation(Location location, boolean update) {
    this.location = location;
    this.hologram.teleport(location.clone().add(0, 2.5, 0));
    if (update) {
      for (Player player : location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE)) {
        update(player);
      }
    }
  }
}
