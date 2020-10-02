package dev.crystall.playernpclib.api.base;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Preconditions;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.skin.PlayerSkin;
import dev.crystall.playernpclib.manager.EntityManager;
import dev.crystall.playernpclib.manager.PacketManager;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public abstract class BasePlayerNPC {

  private String name;
  private String subName;
  private final UUID uuid = UUID.randomUUID();
  private boolean isSpawned = false;
  private final Map<ItemSlot, ItemStack> itemSlots = new EnumMap<>(ItemSlot.class);
  private Hologram hologram;
  private final BukkitTask[] task = new BukkitTask[1];

  /**
   * The id the entity will be registered with at the server
   */
  protected int entityId;
  protected Location location;
  protected PlayerSkin playerSkin;

  protected BasePlayerNPC(String name, Location location) {
    this.location = location;
    this.entityId = EntityManager.tickAndGetCounter();
    this.hologram = HologramsAPI.createHologram(PlayerNPCLib.getPlugin(), this.location.clone().add(0, 2.5, 0));
    setName(name);

    task[0] = Bukkit.getScheduler().runTaskTimer(PlayerNPCLib.getPlugin(), () -> {
      if (this.hologram != null && !this.hologram.isDeleted()) {
        this.hologram.teleport(this.location.clone().add(0, 2.5, 0));
      }
    }, 0L, 1L);
  }

  protected BasePlayerNPC(String name, Location location, String subName) {
    this(name, location);
    setSubName(subName);
  }

  public void spawn() {
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
    if (task[0] != null && !task[0].isCancelled()) {
      task[0].cancel();
    }

    if (this.hologram != null) {
      this.hologram.delete();
    }

    // TODO get only nearby players and play the animation for them
    for (Player player : Bukkit.getOnlinePlayers()) {
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

  public void setName(String name) {
    this.name = name;
    updateDisplayName();
  }

  public void setSubName(String subName) {
    this.subName = subName;
    updateDisplayName();
  }

  public void updateDisplayName() {
    if (hologram != null) {
      hologram.clearLines();
      if (name != null && !name.isEmpty()) {
        hologram.insertTextLine(0, name);
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
      PlayerNPCLib.getPlugin().getLogger().info(String.format("Unable to play animation for npc: %s-%s! NPC not spawned", this.getName(), this.getUuid()));
      return;
    }
    // TODO get only nearby players and play the animation for them
    for (Player player : Bukkit.getOnlinePlayers()) {
      PacketManager.sendAnimationPacket(player, this, animationId);
    }
  }

  public WrappedGameProfile getGameProfile() {
    WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
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
      // TODO get only nearby players
      for (Player player : Bukkit.getServer().getOnlinePlayers()) {
        PacketManager.sendEquipmentPackets(player, this);
      }
    }
  }

  public void setPlayerSkin(PlayerSkin playerSkin) {
    this.playerSkin = playerSkin;
    if (isSpawned) {
      // TODO get only nearby players
      for (Player player : Bukkit.getServer().getOnlinePlayers()) {
        this.update(player);
      }
    }
  }
}
