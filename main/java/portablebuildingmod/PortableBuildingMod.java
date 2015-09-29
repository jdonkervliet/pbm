package portablebuildingmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Hooking the mod into Minecraft using Minecraft Forge.
 */
@Mod(modid = PortableBuildingMod.MODID, version = PortableBuildingMod.VERSION)
public class PortableBuildingMod {
	/**
	 * The mod id.
	 */
	public static final String MODID = "pbm";
	/**
	 * Mod version number.
	 */
	public static final String VERSION = "0.1";

	/**
	 * Inititialize the mod through minecraft forge.
	 * 
	 * @param event
	 *            ignored.
	 */
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PortableBuildingMod());
	}

	/**
	 * Register the pbm commands on the minecraft server.
	 * 
	 * @param event
	 *            Start event used to register command with.
	 */
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new Command());
	}

}
