package dev.crystall.playernpclib.wrapper;

import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import java.util.List;

/**
 * Created by CrystallDEV on 18/08/2021
 */
public interface BaseWrapperPlayServerPlayerInfo extends IBaseWrapper{

  PlayerInfoAction getAction();

  void setAction(PlayerInfoAction value);

  List<PlayerInfoData> getData();

  void setData(List<PlayerInfoData> value);

}
