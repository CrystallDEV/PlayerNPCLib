package dev.crystall.playernpclib.api.base;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.google.common.base.Preconditions;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.skin.PlayerSkin;
import dev.crystall.playernpclib.manager.EntityManager;
import dev.crystall.playernpclib.manager.PacketManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CrystallDEV on 01/09/2020
 */
@Getter
public abstract class BasePlayerNPC {

  private String name;
  private final UUID uuid = UUID.randomUUID();
  private boolean isSpawned = false;
  private final List<Pair<ItemSlot, ItemStack>> itemSlots = Arrays.asList(
    new Pair<>(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.AIR)),
    new Pair<>(EnumWrappers.ItemSlot.OFFHAND, new ItemStack(Material.AIR)),
    new Pair<>(EnumWrappers.ItemSlot.FEET, new ItemStack(Material.AIR)),
    new Pair<>(EnumWrappers.ItemSlot.LEGS, new ItemStack(Material.AIR)),
    new Pair<>(EnumWrappers.ItemSlot.CHEST, new ItemStack(Material.AIR)),
    new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.AIR))
  );

  /**
   * The id the entity will be registered with at the server
   */
  protected int entityId = Integer.MAX_VALUE - EntityManager.getPlayerNPCList().size();
  protected Location location;
  protected WrappedGameProfile gameProfile;


  protected BasePlayerNPC(String name, Location location) {
    this.name = name;
    this.location = location;
    this.gameProfile = new WrappedGameProfile(uuid, name);
  }

  public void onSpawn() {
    for (Player player : PlayerNPCLib.getInstance().getPlugin().getServer().getOnlinePlayers()) {
      show(player);
      PacketManager.sendEquipmentPackets(player, this);
    }
    isSpawned = true;
  }

  public void onDespawn() {
    for (Player player : PlayerNPCLib.getInstance().getPlugin().getServer().getOnlinePlayers()) {
      hide(player);
    }
    isSpawned = false;
  }

  public void update() {
    onDespawn();
    onSpawn();
  }

  public void show(Player player) {
    PacketManager.sendNPCCreatePackets(player, this);
  }

  public void hide(Player player) {
    PacketManager.sendHidePackets(player, this);
  }

  public BasePlayerNPC setSkin(PlayerSkin skin) {
    gameProfile.getProperties().get("textures").clear();
    if (skin != null) {
      gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", skin.getValue(), skin.getSignature()));
    }
    return this;
  }

  public void setName(String name) {
    // TODO send packet to update the name
    this.name = name;
    this.gameProfile = new WrappedGameProfile(uuid, name);

  }

  public Hologram generateHologram() {
    return null;
  }

  /**
   * Sets an item for the given slot
   *
   * @param slot
   * @param itemStack
   */
  public void setItem(ItemSlot slot, ItemStack itemStack) {
    Preconditions.checkNotNull(itemStack, "itemStack cannot be NULL.");
    Preconditions.checkNotNull(slot, "slot cannot be NULL.");

    Optional<Pair<ItemSlot, ItemStack>> gearSlot = itemSlots.stream().filter(slot1 -> slot1.getFirst().equals(slot)).findFirst();
    if (gearSlot.isEmpty()) {
      throw new RuntimeException("Slot is not set on npc: " + getName() + "-" + getUuid());
    }
    gearSlot.get().setSecond(itemStack);

    if (isSpawned) {
      for (Player player : Bukkit.getServer().getOnlinePlayers()) {
        PacketManager.sendEquipmentPackets(player, this);
      }
    }
  }

}
