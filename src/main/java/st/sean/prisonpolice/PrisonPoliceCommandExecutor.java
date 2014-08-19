package st.sean.prisonpolice;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrisonPoliceCommandExecutor  implements CommandExecutor {
    private final PrisonPolice plugin;
    
    public PrisonPoliceCommandExecutor(PrisonPolice plugin) {
        this.plugin = plugin;
    }
	 
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(args.length > 0) {
	    	if (args[0].equalsIgnoreCase("reload")) {
	    		if(sender.hasPermission("prisonpolice.reload")) {
		    		plugin.reloadConfig();
		    		sender.sendMessage("[PrisonPolice] Configuration reloaded.");
		    		return true;
	    		}
	    	}
    	}
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player) sender;
			sender.sendMessage("Wanted Players: " + plugin.sql.evidenceList());
		}
		return true;
    }
}