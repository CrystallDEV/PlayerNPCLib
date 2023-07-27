package dev.crystall.playernpclib.api.base;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Preconditions;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.skin.PlayerSkin;
import dev.crystall.playernpclib.manager.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings.Visibility;
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
  private List<String> subNames = new ArrayList<>();
  private final UUID uuid = UUID.randomUUID();
  private boolean isSpawned = false;
  private final Map<ItemSlot, ItemStack> itemSlots = new EnumMap<>(ItemSlot.class);
  private final Hologram hologram;

  /**
   * The id the entity will be registered with at the player client. This should not collide with any existing entity at the server
   */
  protected int entityId;
  protected Location location;
  protected PlayerSkin playerSkin;
  protected Location eyeLocation;
  @Setter
  protected boolean lookAtClosestPlayer = true;

  protected boolean visibilityRestricted = false;
  protected Set<UUID> shownTo = new HashSet<>();

  protected BasePlayerNPC(String displayName, Location location, boolean visibilityRestricted) {
    this(displayName, location);
    this.visibilityRestricted = visibilityRestricted;
  }

  protected BasePlayerNPC(String displayName, Location location) {
    this.location = location;
    this.eyeLocation = location;
    this.entityId = EntityManager.ENTITY_ID_COUNTER.getAndDecrement();
    this.hologram = HolographicDisplaysAPI.get(PlayerNPCLib.getPlugin()).createHologram(this.location.clone().add(0, 2.25, 0));
    this.hologram.getVisibilitySettings().setGlobalVisibility(Visibility.HIDDEN);
    this.internalName = uuid.toString().substring(0, 16);
    setDisplayName(displayName);
  }

  protected BasePlayerNPC(String displayName, Location location, List<String> subNames) {
    this(displayName, location);
    setSubNames(subNames);
  }

  public void spawn() {
    spawn(Collections.emptyList());
  }

  public void spawn(List<Player> showTo) {
    this.hologram.getVisibilitySettings().clearIndividualVisibilities();

    for (Player player : showTo) {
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

    for (Player player : getVisibleTo()) {
      PlayerNPCLib.getPacketManager().sendDeathMetaData(player, this);
      hide(player);
    }
  }

  public void update(Player player) {
    hide(player);
    show(player);
  }

  public void show(Player player) {
    if (shownTo.contains(player.getUniqueId())) {
      return;
    }
    init(player);
  }

  public void init(Player player) {
    if (shownTo.contains(player.getUniqueId())) {
      return;
    }
    shownTo.add(player.getUniqueId());
    PlayerNPCLib.getEntityHider().setVisibility(player, getEntityId(), true);
    PlayerNPCLib.getPacketManager().sendNPCCreatePackets(player, this);
    PlayerNPCLib.getPacketManager().sendEquipmentPackets(player, this);
    PlayerNPCLib.getPacketManager().sendScoreBoardTeamPacket(player, this);
    if (hologram != null) {
      hologram.getVisibilitySettings().setIndividualVisibility(player, Visibility.VISIBLE);
      updateDisplayName();
    }
  }

  public void hide(Player player) {
    if (!shownTo.contains(player.getUniqueId())) {
      return;
    }
    shownTo.remove(player.getUniqueId());
    PlayerNPCLib.getPacketManager().sendHidePackets(player, this);
    PlayerNPCLib.getEntityHider().setVisibility(player, getEntityId(), false);
    if (hologram != null) {
      hologram.getVisibilitySettings().setIndividualVisibility(player, Visibility.HIDDEN);
    }
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    updateDisplayName();
  }

  public void setSubNames(List<String> subNames) {
    this.subNames = subNames;
    updateDisplayName();
  }

  private void updateDisplayName() {
    if (hologram != null && !hologram.isDeleted()) {
      hologram.getLines().clear();
      if (displayName != null && !displayName.isEmpty()) {
        hologram.getLines().insertText(0, displayName);
      }
      for (int i = 1; i <= subNames.size(); i++) {
        hologram.getLines().insertText(i, subNames.get(i - 1));
      }
      updateHologram();
    }
  }

  public void updateHologram() {
    if (hologram != null && !hologram.isDeleted()) {
      var variableHeight = subNames.size() * 0.25F;
      hologram.setPosition(this.location.clone().add(0, variableHeight + 2.25F, 0));
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
        .warning(String.format("Unable to play animation for npc: %s-%s! NPC not spawned", this.getDisplayName(), this.getUuid()));
      return;
    }
    for (Player player : getVisibleTo()) {
      PlayerNPCLib.getPacketManager().sendAnimationPacket(player, this, animationId);
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
      for (Player player : getVisibleTo()) {
        PlayerNPCLib.getPacketManager().sendEquipmentPackets(player, this);
      }
    }
  }

  public void setPlayerSkin(PlayerSkin playerSkin) {
    this.playerSkin = playerSkin;
    if (isSpawned) {
      for (Player player : getVisibleTo()) {
        this.update(player);
      }
    }
  }

  public void setLocation(Location location, boolean update) {
    this.location = location;
    updateHologram();
    if (update) {
      for (Player player : getVisibleTo()) {
        PlayerNPCLib.getPacketManager().sendMovePacket(player, this);
      }
    }
  }

  public void setEyeLocation(Location location, boolean update) {
    this.eyeLocation = location;
    if (update) {
      for (Player player : getVisibleTo()) {
        PlayerNPCLib.getPacketManager().sendMovePacket(player, this);
      }
    }
  }

  public void setVisibilityRestricted(boolean visibilityRestricted) {
    this.visibilityRestricted = visibilityRestricted;
    if (!visibilityRestricted) {
      this.shownTo.clear();
    }
    hologram.getVisibilitySettings().clearIndividualVisibilities();
    hologram.getVisibilitySettings().setGlobalVisibility(visibilityRestricted ? Visibility.HIDDEN : Visibility.VISIBLE);
  }

  /**
   * Collects all nearby players that the npc is visible to. If the npc is visibility restricted, then the set is filtered by shownTo Set.
   *
   * @return visibleToSet
   */
  protected Set<Player> getVisibleTo() {
    var stream = location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE).stream();
    if (visibilityRestricted) {
      stream = stream.filter(p -> getShownTo().contains(p.getUniqueId()));
    }
    return stream.collect(Collectors.toSet());
  }

}
