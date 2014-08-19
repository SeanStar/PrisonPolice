package st.sean.prisonpolice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;

public class PrisonPoliceDatabase {	
	private final PrisonPolice plugin;
	private SQLite _sqLite;
	
    public PrisonPoliceDatabase(PrisonPolice plugin, Logger logger, String pluginName, String path, String dbName, String extension) {
		_sqLite = new SQLite(logger, pluginName, path, dbName, extension);
		try {
			_sqLite.open();
		} catch (Exception e) {
			plugin.getLogger().info(e.getMessage());
			plugin.getPluginLoader().disablePlugin(plugin);
		}
    	this.plugin = plugin;
	}
	
	public void createTables() {
		if (!_sqLite.isTable("block")) {
			try {
				_sqLite.query("CREATE TABLE block("
						    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
						    + "id_type TEXT,"
						    + "uuid TEXT,"
						    + "world TEXT,"
						    + "x INT,"
						    + "y INT,"
						    + "z INT,"
						    + "time DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')));");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!_sqLite.isTable("evidencelist")) {
			try {
				_sqLite.query("CREATE TABLE evidencelist("
						    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
						    + "uuid TEXT, "
						    + "username TEXT,"
						    + "time DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')));");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeBlockFromList(Block block) {
		try {
			_sqLite.query("DELETE FROM block WHERE EXISTS (SELECT world,x,y,z FROM block "
						+ "WHERE world = '" + block.getWorld().getName()+ "' " 
						+ "AND x = '" + block.getX() + "' "
						+ "AND y = '" + block.getY() + "' "
						+ "AND z = '" + block.getZ() + "');");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addBlockToList(Block block, Player player) {
		try {
			_sqLite.query("INSERT INTO block(id_type,uuid,world,x,y,z) VALUES('"
						+ block.getType().toString() + "', '"
						+ player.getUniqueId().toString() + "', '"
						+ block.getWorld().getName() + "', '"
						+ block.getX() + "', '"
						+ block.getY() + "', '"
						+ block.getZ() + "');");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkBlock(Block block, Player player) {
		boolean first = true;
		UUID pID = UUID.fromString("00000000-0000-0000-0000-000000000000");
		String world = "";
		int x = 0;
		int y = 0;
		int z = 0;
		
		try {
			ResultSet rs = _sqLite.query("SELECT * FROM block WHERE uuid = (SELECT uuid FROM block "
						+ "WHERE world = '" + block.getWorld().getName() + "' "
						+ "AND x = '" + block.getX() + "' " 
						+ "AND y = '" + block.getY() + "' "
						+ "AND z = '" + block.getZ() + "');");

			while(rs.next()) {
				try {
					if (first) { 
					pID = UUID.fromString(rs.getString("uuid"));
					addEvidenceList(pID);
					player.sendMessage(plugin.processColors("&c" + plugin.getConfig().getString("blocksname") + " owned by: " + plugin.getServer().getOfflinePlayer(pID).getName() + ". Added to evidence list..."));
					Player p = plugin.getServer().getPlayer(pID);
					if(p != null)
						p.sendMessage(plugin.processColors("&cYou are now wanted by the police. Tread lightly."));
					}
					first = false;
					world = rs.getString("world");
					x = rs.getInt("x");
					y = rs.getInt("y");
					z = rs.getInt("z");
					removeBlock(world,x,y,z);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(first) {
				player.sendMessage(plugin.processColors("&c" + plugin.getConfig().getString("blocksname") + " not owned by anyone."));		
				return;
			}
			_sqLite.query("DELETE FROM block WHERE uuid = '" + pID.toString() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeBlock(String world, int x, int y, int z) {
		plugin.getServer().getWorld(world).getBlockAt(x,y,z).setType(Material.AIR);
	}
	
    public void addEvidenceList(UUID id) {
    	try {
    		_sqLite.query("INSERT INTO evidencelist(uuid,username) SELECT '"
    				+ id + "','" + plugin.getServer().getOfflinePlayer(id).getName() + "' WHERE NOT EXISTS (SELECT * FROM evidencelist WHERE uuid = '" + id.toString() + "') ;");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public void removeEvidenceList(UUID id) {
        try {
        	_sqLite.query("DELETE FROM evidencelist WHERE uuid = '" + id.toString() + "';");
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        
    }
    
	public boolean isOnList(UUID id) {
		try {
			ResultSet checklist = _sqLite.query("SELECT * FROM evidencelist WHERE uuid = '" + id.toString() + "';");
			if(checklist.next()) {
				return true;
			}
		} catch (SQLException e ) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String evidenceList() {
		String listString = "";
		try {
			ResultSet wanted = _sqLite.query("SELECT uuid FROM evidencelist;");
			while(wanted.next()) {
				UUID uuid = UUID.fromString(wanted.getString("uuid"));
				listString += plugin.getServer().getOfflinePlayer(uuid).getName() + ", ";
			}
			if(listString != "") {
				listString = listString.substring(0, listString.length()-2);
			} else {
				listString = "None";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listString;
	}
	
	public void closeConnection() {
		_sqLite.close();
	}
}
