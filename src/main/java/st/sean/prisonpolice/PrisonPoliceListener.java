package st.sean.prisonpolice;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
 
public class PrisonPoliceListener implements Listener {
	private final PrisonPolice plugin;
	
    public PrisonPoliceListener(PrisonPolice plugin) {
        this.plugin = plugin;
    }
	
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
    	Player player = (Player) event.getPlayer();
    	if(player.hasPermission("prisonpolice.fugitive") || plugin.sql.isOnList(player.getUniqueId())) {
    		player.sendMessage(plugin.processColors("&cYou are wanted by the police. Tread lightly."));
    	}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.isCancelled()) return;
    	if(event.getPlayer().hasPermission("prisonpolice.freeplayer")) {
    	    if(plugin.getConfig().getList("illegalblocks").contains(event.getBlock().getType().toString())) {
    		    plugin.sql.removeBlockFromList(event.getBlock());
    	    }
    	}
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	if(event.isCancelled()) return;
    	if(event.getPlayer().hasPermission("prisonpolice.freeplayer")) {
    	    if(plugin.getConfig().getList("illegalblocks").contains(event.getBlock().getType().toString())) {
    		    plugin.sql.addBlockToList(event.getBlock(), event.getPlayer());
    	    }
    	}
    }
    @EventHandler
    public void jailStickHandling(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Player)) return;

        Player policeman = (Player) event.getPlayer();
        Player criminal = (Player) event.getRightClicked();

        if(plugin.getConfig().getList("batons").contains(policeman.getItemInHand().getType().toString())) {
            if(policeman.hasPermission("prisonpolice.stick")) {
            	if(criminal.hasPermission("prisonpolice.fugitive") || plugin.sql.isOnList(criminal.getUniqueId())) {
            		policeman.sendMessage("You've managed to arrest " + criminal.getName() + ".");
                	criminal.sendMessage("You've been arrested. Have fun back in jail, criminal scum.");
            		plugin.jailCommands(policeman,criminal);
            		plugin.sql.removeEvidenceList(criminal.getUniqueId());
                }
            } else {
            }
        }
    }
    @EventHandler
    public void stickInteract(PlayerInteractEvent event) {
    	if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
    	Player player = (Player) event.getPlayer();
    	if(plugin.getConfig().getList("batons").contains(player.getItemInHand().getType().toString())) {
    		if(player.hasPermission("prisonpolice.stick")) {
    			if(plugin.getConfig().getList("illegalblocks").contains(event.getClickedBlock().getType().toString())) {
    				plugin.sql.checkBlock(event.getClickedBlock(), player);
    			}
    		}
    	}
    }
}