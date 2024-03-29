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
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@SuppressWarnings("UnstableApiUsage")
@Slf4j
@Getter
public abstract class BasePlayerNPC {

  private final String internalName;
  private String displayName;
  private List<String> subNames = new ArrayList<>();
  private final UUID uuid = UUID.randomUUID();
  private boolean isSpawned = false;
  private final Map<ItemSlot, ItemStack> itemSlots = new EnumMap<>(ItemSlot.class);
  private final TextDisplay display;

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
  /**
   * The set of players that this npc is currently shown to.
   */
  protected Set<UUID> shownTo = new HashSet<>();
  /**
   * The set of players that are currently able to see the npc. This set is only used if the npc is visibility restricted.
   */
  protected Set<UUID> visibleTo = new HashSet<>();

  protected BasePlayerNPC(String displayName, Location location, boolean visibilityRestricted) {
    this(displayName, location);
    updateDefaultVisibility(visibilityRestricted);
  }

  protected BasePlayerNPC(String displayName, Location location) {
    this.location = location;
    this.eyeLocation = location;
    this.entityId = EntityManager.ENTITY_ID_COUNTER.getAndDecrement();
    this.display = spawnTextDisplay();
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
    this.display.setVisibleByDefault(true);

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

    if (this.display != null) {
      this.display.remove();
    }

    for (Player player : getNearbyVisibleTo()) {
      PlayerNPCLib.getPacketManager().sendDeathMetaData(player, this);
      hide(player);
    }
  }

  public void update(Player player) {
    hide(player);
    show(player);
  }

  public void show(Player player) {
    visibleTo.add(player.getUniqueId());
    if (shownTo.contains(player.getUniqueId())) {
      return;
    }
    PlayerNPCLib.getPacketManager().sendNPCCreatePackets(player, this);
    PlayerNPCLib.getPacketManager().sendEquipmentPackets(player, this);
    PlayerNPCLib.getPacketManager().sendScoreBoardTeamPacket(player, this);
    if (display != null) {
      player.showEntity(PlayerNPCLib.getPlugin(), display);
    }
    shownTo.add(player.getUniqueId());
  }

  public void hide(Player player) {
    visibleTo.remove(player.getUniqueId());
    if (!shownTo.contains(player.getUniqueId())) {
      return;
    }
    PlayerNPCLib.getPacketManager().sendHidePackets(player, this);
    if (display != null) {
      player.hideEntity(PlayerNPCLib.getPlugin(), display);
    }
    shownTo.remove(player.getUniqueId());
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
    if (display == null || display.isDead()) {
      return;
    }
    var textComponent = Component.text();
    if (displayName != null && !displayName.isEmpty()) {
      textComponent.content(displayName);
    }
    for (int i = 1; i <= subNames.size(); i++) {
      var subNameComponent = Component.text(subNames.get(i - 1));
      textComponent.appendNewline();
      textComponent.append(subNameComponent);
    }
    display.text(textComponent.build());
    updateTextDisplay();
  }

  private void updateDefaultVisibility(boolean visibilityRestricted) {
    this.visibilityRestricted = visibilityRestricted;
    if (!visibilityRestricted) {
      this.visibleTo.clear();
    }
  }

  private TextDisplay spawnTextDisplay() {
    TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
    textDisplay.setBillboard(Billboard.VERTICAL);
    textDisplay.setShadowed(false);
    return textDisplay;
  }

  public void updateTextDisplay() {
    if (display != null && !display.isDead()) {
      var x = location.getX();
      var y = location.getY() + 2.25F;
      var z = location.getZ();
      display.teleport(new Location(location.getWorld(), x, y, z));
    }
  }

  /**
   * Plays the given animation to all nearby players
   *
   * @param animationId the id of the animation to play
   */
  public void playAnimation(int animationId) {
    if (!isSpawned) {
      log.warn("Unable to play animation for npc: {}-{}! NPC not spawned", this.getDisplayName(), this.getUuid());
      return;
    }
    for (Player player : getNearbyVisibleTo()) {
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
      for (Player player : getNearbyVisibleTo()) {
        PlayerNPCLib.getPacketManager().sendEquipmentPackets(player, this);
      }
    }
  }

  public void setPlayerSkin(PlayerSkin playerSkin) {
    this.playerSkin = playerSkin;
    if (isSpawned) {
      for (Player player : getNearbyVisibleTo()) {
        this.update(player);
      }
    }
  }

  public void setLocation(Location location, boolean update) {
    this.location = location;
    updateTextDisplay();
    if (update) {
      for (Player player : getNearbyVisibleTo()) {
        PlayerNPCLib.getPacketManager().sendMovePacket(player, this);
      }
    }
  }

  public void setEyeLocation(Location location, boolean update) {
    this.eyeLocation = location;
    if (update) {
      for (Player player : getNearbyVisibleTo()) {
        PlayerNPCLib.getPacketManager().sendMovePacket(player, this);
      }
    }
  }

  public void setVisibilityRestricted(boolean visibilityRestricted) {
    this.visibilityRestricted = visibilityRestricted;
    this.display.setVisibleByDefault(!visibilityRestricted);

    if (!visibilityRestricted) {
      this.visibleTo.clear();
    }

    for (Player player : getNearbyVisibleTo()) {
      player.showEntity(PlayerNPCLib.getPlugin(), display);
    }
  }

  /**
   * Collects all nearby players that the npc is currently visible to. If the npc is visibility restricted, then the set is filtered by shownTo Set.
   *
   * @return visibleToSet
   */
  protected Set<Player> getNearbyVisibleTo() {
    var stream = location.getNearbyPlayers(Constants.NPC_VISIBILITY_RANGE).stream();
    if (visibilityRestricted) {
      stream = stream.filter(p -> getVisibleTo().contains(p.getUniqueId()));
    }
    return stream.collect(Collectors.toSet());
  }

}
