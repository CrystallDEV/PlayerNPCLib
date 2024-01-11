package dev.crystall.playernpclib.api.wrapper;

import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import java.util.List;
import java.util.Set;

/**
 * Created by CrystallDEV on 18/08/2021
 */
public interface BaseWrapperPlayServerPlayerInfo extends IBaseWrapper{

  Set<PlayerInfoAction> getActions();

  void setActions(Set<PlayerInfoAction> value);

  List<PlayerInfoData> getData();

  void setData(List<PlayerInfoData> value);

}
