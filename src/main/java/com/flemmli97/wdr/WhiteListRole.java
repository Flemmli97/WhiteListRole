package com.flemmli97.wdr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = WhiteListRole.MODID, name = WhiteListRole.MODNAME, version = WhiteListRole.VERSION, acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class WhiteListRole {

	public static final String MODID = "wrl";
	public static final String MODNAME = "Whitelist Role";
	public static final String VERSION = "1.0.0";
	public static final Logger logger = LogManager.getLogger(WhiteListRole.MODID);

	@Instance
	public static WhiteListRole instance = new WhiteListRole();

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		if (event.getServer().isDedicatedServer()) {
			event.registerServerCommand(new CommandAddWhitelistRole());
			event.registerServerCommand(new CommandAddWhitelist());
		}
	}
}
