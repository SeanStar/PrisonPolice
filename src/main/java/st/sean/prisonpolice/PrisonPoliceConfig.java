package st.sean.prisonpolice;

import java.util.Arrays;

public class PrisonPoliceConfig {
    private final PrisonPolice plugin;
    
    public PrisonPoliceConfig(PrisonPolice plugin) {
    	String[] batons = {"STICK"};
    	String[] illegalblocks = {"CACTUS","SUGAR_CANE_BLOCK","RED_MUSHROOM"};
    	String illegalblocksname = "Drugs";
    	String[] consolecommands = {"econ add <policeman> 500","pex user <criminal> group set default"};
    	String[] policemancommands = {"prisonpolice"};
    	String[] criminalcommands = {"spawn","sethome"};
    	plugin.getConfig().addDefault("batons", Arrays.asList(batons));   	
    	plugin.getConfig().addDefault("illegalblocks", Arrays.asList(illegalblocks));
    	plugin.getConfig().addDefault("blocksname", illegalblocksname);
    	plugin.getConfig().addDefault("jail.commands.console", Arrays.asList(consolecommands));
    	plugin.getConfig().addDefault("jail.commands.policeman", Arrays.asList(policemancommands));
    	plugin.getConfig().addDefault("jail.commands.criminal", Arrays.asList(criminalcommands));
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        this.plugin = plugin;
    }
    
}