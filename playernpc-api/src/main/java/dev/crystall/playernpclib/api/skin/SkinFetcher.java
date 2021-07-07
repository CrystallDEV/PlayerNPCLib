package dev.crystall.playernpclib.api.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.crystall.playernpclib.PlayerNPCLib;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class SkinFetcher {

  private static final String MINESKIN_API = "https://api.mineskin.org/get/id/";
  private static final Map<Integer, PlayerSkin> cachedSkins = new ConcurrentHashMap<>();

  // TODO maybe replace with the mineskin API java client -> https://github.com/InventivetalentDev/MineskinClient
  public static void asyncFetchSkin(int id, Callback<PlayerSkin> callback) {
    Bukkit.getScheduler().runTaskAsynchronously(PlayerNPCLib.getPlugin(), () -> {
      PlayerSkin skin = fetchSkin(id);
      if (skin == null) {
        callback.failed();
        return;
      }
      callback.call(skin);
    });
  }

  public static PlayerSkin fetchSkin(int id) {
    try {
      // Check for cached value and return it
      if (cachedSkins.containsKey(id)) {
        return cachedSkins.get(id);
      }

      StringBuilder builder = new StringBuilder();
      HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(MINESKIN_API + id).openConnection();
      httpURLConnection.setRequestMethod("GET");
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setDoInput(true);
      httpURLConnection.connect();

      Scanner scanner = new Scanner(httpURLConnection.getInputStream());
      while (scanner.hasNextLine()) {
        builder.append(scanner.nextLine());
      }

      scanner.close();
      httpURLConnection.disconnect();

      JsonObject jsonObject = (JsonObject) new JsonParser().parse(builder.toString());
      JsonObject textures = jsonObject.get("data").getAsJsonObject().get("texture").getAsJsonObject();
      String value = textures.get("value").getAsString();
      String signature = textures.get("signature").getAsString();

      PlayerSkin skin = new PlayerSkin(value, signature);
      cachedSkins.put(id, skin);
      return skin;
    } catch (IOException exception) {
      Bukkit.getLogger().severe("Could not fetch skin! (Id: " + id + "). Message: " + exception.getMessage());
      exception.printStackTrace();
      return null;
    }
  }

}
