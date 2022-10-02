package com.parpar8090.mystorage.sqlite;

import com.google.gson.Gson;
import com.parpar8090.mystorage.PersonalInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InventoryDB {
    private final SQLiteConnection db;

    public final HashMap<UUID, PersonalInventory> cachedInventories = new HashMap<>();

    public InventoryDB(){
        db = new SQLiteConnection("inventories.db");
        String sql = "CREATE TABLE IF NOT EXISTS `inventories` (`uuid` varchar(36) NOT NULL, `slot` TINYINT NOT NULL, `item` TEXT NOT NULL, PRIMARY KEY (`uuid`, `slot`));";
        db.executeSql(sql);
    }

    public void saveInventory(HumanEntity player, PersonalInventory inventory){
        db.deleteWithCondition("inventories", "`uuid` = '"+player.getUniqueId()+"'");
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < inventory.getInv().getSize(); i++) {
            ItemStack item = inventory.getInv().getContents()[i];
            if(item == null) continue;
            Gson gson = new Gson();

            items.add("('"+player.getUniqueId()+"', "+i+", '"+gson.toJson(item.serialize(), Map.class)+"')");
        }
        if(items.isEmpty()) return;
        db.insertIfNotExists("inventories", List.of("uuid", "slot", "item").toArray(new String[0]), items.toArray(new String[0]));
    }

    public void loadInventory(Player player){
        String query = "SELECT slot, item FROM inventories WHERE uuid = '"+player.getUniqueId()+"';";
        ResultSet rs = db.executeQuery(query);
        //ResultSet rs = db.selectAllWithCondition("inventories", "uuid = '" + player.getUniqueId() + "'");
        PersonalInventory inv = cachedInventories.getOrDefault(player.getUniqueId(), new PersonalInventory(player, 6));
        try {
            System.out.println("hisss");
            while (rs.next()) {
                int slot = rs.getInt("slot");
                String stringItem = rs.getString("item");

                System.out.println(slot + ": " +stringItem);
                //stringItem = stringItem.substring(1, stringItem.length()-1); //remove the ''

                Gson gson = new Gson();

                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                Map<?, ?> map1 = gson.fromJson(stringItem, Map.class);
                for(Map.Entry<?, ?> entry : map1.entrySet()){
                    map.put(entry.getKey().toString(), entry.getValue());
                }

                inv.getInv().setItem(slot, ItemStack.deserialize(map));
            }
            rs.close();
            cachedInventories.put(player.getUniqueId(), inv);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
