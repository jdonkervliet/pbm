package portablebuildingmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BuildingSaver {

	/**
	 * Directory to save buildings/structures.
	 */
	public static String BUILDING_DIR = System.getProperty("user.home")
			+ "/.minecraft/pbm/";

	World world;

	/**
	 * Create a new BuildingSaver using the {@link MinecraftServer} world
	 * object.
	 */
	public BuildingSaver() {
		this.world = MinecraftServer.getServer().getEntityWorld();
	}

	/**
	 * Builds the structure that is specified by the given name. The building is
	 * placed at the block the player is looking at, with an additional vertical
	 * offset of 1. As an example, if the player looks at the ground while this
	 * command is called, the building is build on top of the ground, in stead
	 * of replacing it.
	 * 
	 * @param name
	 *            The name of the structure to build.
	 * @throws FileNotFoundException
	 *             If the structure file can not be found.
	 */
	public void build(String name) throws FileNotFoundException {
		build(name, 1);
	}

	/**
	 * Builds the structure that is specified by the given name. The building is
	 * placed at the block the player is looking at.
	 * 
	 * @param name
	 *            The name of the structure to build.
	 * @param verticalOffset
	 *            The vertical offset to use when placing the structure. A
	 *            positive offset raised the building up.
	 * @throws FileNotFoundException
	 *             If the structure file can not be found.
	 */
	public void build(String name, int verticalOffset)
			throws FileNotFoundException {
		BlockPos crosshairBlock = blockInCrosshair();
		Scanner scanner = new Scanner(new File(BUILDING_DIR + name));
		(new StructureIO().read(scanner)).build(world,
				crosshairBlock.up(verticalOffset));
		scanner.close();
	}

	/**
	 * Delete one or multiple blocks in the world. A 3-dimensional box is
	 * defined through the given parameters. All blocks in this box are deleted.
	 * One of the end points of the box is the block the player is looking at.
	 * The diagonal end point of the box is defined through the given
	 * parameters.
	 * 
	 * @param forward
	 *            The size of the box in the 'forward' dimension.
	 * @param right
	 *            The size of the box in the 'right' dimension.
	 * @param up
	 *            The size of the box in the 'up' direction.
	 */
	public void delete(int forward, int right, int up) {
		EnumFacing facing = Minecraft.getMinecraft().getRenderViewEntity()
				.getHorizontalFacing();
		BlockPos crosshairBlock = blockInCrosshair();

		Tuple forwardRange = Tuple.getIncreasingRange(forward);
		Tuple rightRange = Tuple.getIncreasingRange(right);
		Tuple upRange = Tuple.getIncreasingRange(up);

		for (int f = forwardRange.start; f < forwardRange.end; f++) {
			for (int r = rightRange.start; r < rightRange.end; r++) {
				for (int u = upRange.start; u < upRange.end; u++) {
					breakBlock(crosshairBlock.add((new Offset(f, r, u)
							.toAbsoluteOffset(facing))));
				}
			}
		}
	}

	/**
	 * Save a box of blocks on disk.
	 * 
	 * @param name
	 *            The name the player assigns to the structure to save. A
	 *            3-dimensional box is defined through the given parameters. All
	 *            blocks in this box are saved as a structure. One of the end
	 *            points of the box is the block the player is looking at. The
	 *            diagonal end point of the box is defined through the given
	 *            parameters.
	 * @param forward
	 *            The size of the box in the 'forward' dimension.
	 * @param right
	 *            The size of the box in the 'right' dimension.
	 * @param up
	 *            The size of the box in the 'up' direction.
	 */
	public void save(String name, int forward, int right, int up) {
		PrintWriter writer = null;
		try {
			writer = getBuildingWriter(name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		writer.write((new StructureIO()).write(new Structure(world,
				blockInCrosshair(), new Offset(forward, right, up))));
		writer.close();
	}

	/**
	 * @return Return the {@link BlockPos} the player is looking at.
	 */
	private BlockPos blockInCrosshair() {
		return Minecraft.getMinecraft().getRenderViewEntity()
				.rayTrace(200, 1.0F).getBlockPos();
	}

	/**
	 * Replace the current block at the given {@link BlockPos} with air.
	 * 
	 * @param pos
	 *            The block position to replace.
	 */
	private void breakBlock(BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		block.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
		world.setBlockToAir(pos);
	}

	/**
	 * Creates a {@link PrintWriter} that writes to the given filename. If the
	 * file or one of the parent directories does not exist, it is created.
	 * 
	 * @param filename
	 *            The filename of the file to write to.
	 * @return A {@link PrintWriter} that prints to the specified file.
	 * @throws IOException
	 *             If something goes wrong with IO.
	 */
	private PrintWriter getBuildingWriter(String filename) throws IOException {
		PrintWriter writer = null;
		File savedir = new File(BUILDING_DIR);
		if (!savedir.exists()) {
			savedir.mkdirs();
		}
		writer = new PrintWriter(
				new File(BUILDING_DIR + filename).getCanonicalPath());
		return writer;
	}
}
