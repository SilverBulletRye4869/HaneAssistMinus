package haneassist.haneassistminus;


//import org.bukkit.Bukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerSearch {

    public static Player getNearbyPlayer(Location loc){
        double nearest = 100;
        Player nearestPlayer = null;
        for(Entity entity : loc.getNearbyEntities(3,3,3)) {
            if(entity instanceof Player) {
                double distance = entity.getLocation().distance(loc);
                if(nearest>distance){
                    nearest = distance;
                    nearestPlayer = (Player) entity;
                }
            }
        }
        return nearestPlayer;
    }


    public static String getOfflinePlayerUuid(String name){
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName().equalsIgnoreCase(name)) {
                return offlinePlayer.getUniqueId().toString();
            }
        }
        return null;
    }

    public static String getOnlinePlayerUuid(String name){
        for (Player player:Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player.getUniqueId().toString();
            }
        }
        return null;
    }

    public static Player getPlayerFromName(String name){
        for (Player player:Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public static Player getPlayerFromData(String name){
        Player player = getPlayerFromName(name);
        if(player==null){
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                String playerName = offlinePlayer.getName();
                if(playerName!=null && playerName.equalsIgnoreCase(name)){
                    player = offlinePlayer.getPlayer();
                    break;
                }
            }
        }
        return player;
    }

    public static List<String> getOfflinePlayerList(){
        List<String> offlinePlayers = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            offlinePlayers.add(offlinePlayer.getName());
        }
        return offlinePlayers;
    }

    public static List<String> getAllPlayers(){
        List<String> allPlayers = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            allPlayers.add(offlinePlayer.getName());
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            allPlayers.add(onlinePlayer.getName());
        }
        return allPlayers;
    }
}