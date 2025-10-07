# PlayerNPCLib

NPC Library which is 99% based on packets to spawn in custom player looking npcs.

The library is based on paper and should break on other non paper server distributions.

## Maven

Add my nexus as a repository

``` xml
<repositories>
  <repository>
    <id>crystall.dev</id>
    <url>https://nexus.crystall.dev</url>
  </repository>
</repositories>
```

Add PlayerNPCLib as a dependency

```xml
<dependencies>
  <dependency>
    <groupId>dev.crystall</groupId>
    <artifactId>playernpclib-api</artifactId>
    <version>1.3.1-SNAPSHOT</version>
  </dependency>
</dependencies>
```

Add the correct nms version as a dependency

```xml
<dependencies>
  <dependency>
    <groupId>dev.crystall</groupId>
    <artifactId>playernpclib-nms-v1_20_R1</artifactId>
    <version>1.3.1-SNAPSHOT</version>
  </dependency>
</dependencies>
```

## Gradle

Add my nexus as a repository

``` groovy
repositories {
    maven { url 'https://nexus.crystall.dev' }
}
```

Add PlayerNPCLib as a dependency

``` groovy
dependencies {
    implementation 'dev.crystall:PlayerNPCLib:1.0-SNAPSHOT'
}
```

# Code examples

Set up the library. This should be called on startup

``` Java
playerNPCLib = new PlayerNPCLib(this);
```

## Entity Management

To manage a npc, you should always use the entityManager.

### To spawn an entity

``` Java
PlayerNPCLib.getEntityManager().spawnEntity(characterSelectionNPC, true);
```

### Remove an entity

``` Java
PlayerNPCLib.getEntityManager().removeEntity(characterSelectionNPC, true);
```

### Create your own entity

To Create your own entity, you simply have to extend the BasePlayerNPC class. A good example is the already existing MovablePlayerNPC which uses packets to
display a moving PlayerNPC.

``` Java 
public class MovablePlayerNPC extends BasePlayerNPC {
  protected Creature bukkitLivingEntity;
  private final EntityType entityType;
  private boolean isAggressive = false;

  public MovablePlayerNPC(String name, Location location, EntityType entityType) {
    super(name, location);
    this.entityType = entityType;
  }

  public void spawn() {
    this.bukkitLivingEntity = (Creature)this.getLocation().getWorld().spawnEntity(this.getLocation(), this.entityType);
    if (this.bukkitLivingEntity instanceof Ageable) {
      ((Ageable)this.bukkitLivingEntity).setAdult();
    }

    if (this.bukkitLivingEntity instanceof Zombie) {
      ((Zombie)this.bukkitLivingEntity).setShouldBurnInDay(false);
    }

    this.bukkitLivingEntity.setSilent(true);
    this.bukkitLivingEntity.setCanPickupItems(false);
    super.spawn();
  }
}
```


## Visibility Management

The visibility of an entity is handled its settings. If an entity is visible by default, each nearby player receives on certain conditions the packets to display the entity.

If an entity is not visible by default, each nearby player receives on certain conditions the packets to hide the entity.

A npc can be shown to a player by adding it to the visibleTo list of the npc.
