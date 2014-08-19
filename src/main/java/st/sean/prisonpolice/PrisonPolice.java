package st.sean.prisonpolice;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class PrisonPolice extends JavaPlugin {
	private static Logger log = Logger.getLogger("Minecraft");
	public static Permission perms = null;
	PrisonPoliceDatabase sql;
	PrisonPoliceConfig config;

	@Override
	public void onEnable() {
		setupPermissions();
		sql = new PrisonPoliceDatabase(this, PrisonPolice.log, "PrisonPolice",
				this.getDataFolder().getAbsolutePath(), "PrisonPolice",
				".sqlite");
		sql.createTables();
		config = new PrisonPoliceConfig(this);
		getServer().getPluginManager().registerEvents(
				new PrisonPoliceListener(this), this);
		getCommand("prisonpolice").setExecutor(
				new PrisonPoliceCommandExecutor(this));
		log.info("[PrisonPolice] Enabled.");
	}

	@Override
	public void onDisable() {
		sql.closeConnection();
		log.info("[PrisonPolice] Disabled.");
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer()
				.getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public boolean jailCommands(Player policeman, Player criminal) {
		for (Object command : getConfig().getList("jail.commands.console")) {
			getServer().dispatchCommand(
					getServer().getConsoleSender(),
					command.toString()
							.replace("<criminal>", criminal.getName())
							.replace("<policeman>", policeman.getName()));
		}
		for (Object command : getConfig().getList("jail.commands.policeman")) {
			getServer().dispatchCommand(
					policeman,
					command.toString()
							.replace("<criminal>", criminal.getName())
							.replace("<policeman>", policeman.getName()));
		}
		for (Object command : getConfig().getList("jail.commands.criminal")) {
			getServer().dispatchCommand(
					criminal,
					command.toString()
							.replace("<criminal>", criminal.getName())
							.replace("<policeman>", policeman.getName()));
		}
		return true;
	}

	public String processColors(String str) {
		return str.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}

	public String stripColors(String str) {
		return str.replaceAll("(&([a-f0-9]))", "");
	}
}