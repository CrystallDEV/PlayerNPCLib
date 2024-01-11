package dev.crystall.playernpclib.api.wrapper;


import java.util.List;
import java.util.UUID;

/**
 * Created by CrystallDEV on 18/08/2021
 */
public interface BaseWrapperPlayServerPlayerInfoRemove extends IBaseWrapper {

  List<UUID> getPlayerIds();

  void setPlayerIds(List<UUID> value);

  void addPlayerId(UUID playerId);

  void removePlayerId(UUID playerId);

}
