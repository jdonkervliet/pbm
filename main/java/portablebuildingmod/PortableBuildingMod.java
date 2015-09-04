package portablebuildingmod;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = PortableBuildingMod.MODID, version = PortableBuildingMod.VERSION)
public class PortableBuildingMod {
	public static final String MODID = "examplemod";
	public static final String VERSION = "1.0";

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// some example code
		MinecraftForge.EVENT_BUS.register(new PortableBuildingMod());

		System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new Command());
	}

}
