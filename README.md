[![Quality gate](https://sonarqube.crystall.dev/api/project_badges/quality_gate?project=dev.crystall%3Aplayernpclib)](https://sonarqube.crystall.dev/dashboard?id=dev.crystall%3Aplayernpclib)

[![Build Status](https://jenkins.crystall.dev/buildStatus/icon?job=PlayerNPCLib%2Fdevelop)](https://jenkins.crystall.dev/job/PlayerNPCLib/job/develop/)

[![Bugs](https://sonarqube.crystall.dev/api/project_badges/measure?project=dev.crystall%3Aplayernpclib&metric=bugs)](https://sonarqube.crystall.dev/dashboard?id=dev.crystall%3Aplayernpclib)
[![Lines of Code](https://sonarqube.crystall.dev/api/project_badges/measure?project=dev.crystall%3Aplayernpclib&metric=ncloc)](https://sonarqube.crystall.dev/dashboard?id=dev.crystall%3Aplayernpclib)
[![Maintainability Rating](https://sonarqube.crystall.dev/api/project_badges/measure?project=dev.crystall%3Aplayernpclib&metric=sqale_rating)](https://sonarqube.crystall.dev/dashboard?id=dev.crystall%3Aplayernpclib)
[![Reliability Rating](https://sonarqube.crystall.dev/api/project_badges/measure?project=dev.crystall%3Aplayernpclib&metric=reliability_rating)](https://sonarqube.crystall.dev/dashboard?id=dev.crystall%3Aplayernpclib)
[![Security Rating](https://sonarqube.crystall.dev/api/project_badges/measure?project=dev.crystall%3Aplayernpclib&metric=security_rating)](https://sonarqube.crystall.dev/dashboard?id=dev.crystall%3Aplayernpclib)

# PlayerNPCLib

NPC Library which is 99% based on packets to spawn in custom player looking npcs

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
    <version>1.2-SNAPSHOT</version>
  </dependency>
</dependencies>
```

Add the correct nms version as a dependency

```xml
<dependencies>
  <dependency>
    <groupId>dev.crystall</groupId>
    <artifactId>playernpclib-nms-v1_20_R1</artifactId>
    <version>1.2-SNAPSHOT</version>
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

Setup the library. This should be called on startup

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
