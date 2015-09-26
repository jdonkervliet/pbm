package portablebuildingmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
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
		Tuple forwardTuple = getIncreasingRange(forward);
		Tuple upTuple = getIncreasingRange(up);
		Tuple rightTuple = getIncreasingRange(right);
		BlockPos crosshairBlock = blockInCrosshair();

		for (int f = forwardTuple.start; f < forwardTuple.end; f++) {
			for (int r = rightTuple.start; r < rightTuple.end; r++) {
				for (int u = upTuple.start; u < upTuple.end; u++) {
					breakBlock(crosshairBlock.add(relativeToAbsoluteOffset(f,
							r, u)));
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

		Tuple forwardTuple = getIncreasingRange(forward);
		Tuple upTuple = getIncreasingRange(up);
		Tuple rightTuple = getIncreasingRange(right);
		BlockPos crosshairBlock = blockInCrosshair();

		for (int f = forwardTuple.start; f < forwardTuple.end; f++) {
			for (int r = rightTuple.start; r < rightTuple.end; r++) {
				for (int u = upTuple.start; u < upTuple.end; u++) {
					BlockPos pos = crosshairBlock.add(relativeToAbsoluteOffset(
							f, r, u));

					IBlockState blockState = world.getBlockState(pos);
					if (!shouldBeSaved(blockState)) {
						continue;
					}

					PropertyDirection propertyDirection = getPropertyDirectionFromBlockState(blockState);
					EnumFacing facing = null;
					if (propertyDirection != null) {
						facing = (EnumFacing) blockState
								.getValue(propertyDirection);
					}
					RelativeFacing relativeFacing = facing != null ? absoluteToRelativeFacing(facing)
							: RelativeFacing.NONE;

					int blockid = Block.getIdFromBlock(blockState.getBlock());
					writer.println(String.format("%d,%d,%d,%d,%s", f, r, u,
							blockid, relativeFacing.toString()));
				}
			}
		}

		writer.close();
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

	/**
	 * Checks if a {@link IBlockState} should be saved in the structure file or
	 * not.
	 * 
	 * @param blockState
	 *            The block state to check.
	 * @return <code>true</code> iff the block state should be saved to disk.
	 */
	private boolean shouldBeSaved(IBlockState blockState) {
		if ((blockState.getBlock() instanceof BlockDoor)
				&& blockState.getValue(BlockDoor.HALF).equals(
						BlockDoor.EnumDoorHalf.UPPER)) {
			return false;
		}
		return true;
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
		Structure structure = new Structure();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineparts = line.split(",");

			int forwardOffset = Integer.parseInt(lineparts[0]);
			int upOffset = Integer.parseInt(lineparts[1]);
			int rightOffset = Integer.parseInt(lineparts[2]);
			int blockType = Integer.parseInt(lineparts[3]);

			RelativeFacing facing = RelativeFacing.valueOf(lineparts[4]);
			BlockPos offset = relativeToAbsoluteOffset(forwardOffset, upOffset,
					rightOffset);
			IBlockState block = Block.getStateById(blockType);

			if (!facing.equals(RelativeFacing.NONE)) {
				PropertyDirection propertyDirection = getPropertyDirectionFromBlockState(block);
				if (propertyDirection != null) {
					block = block.withProperty(propertyDirection,
							relativeToAbsoluteFacing(facing));
				}
			}

			structure.addBlockAtPosition(new BlockAtPosition(block, offset
					.up(verticalOffset)));
		}
		scanner.close();
		structure.build(world, crosshairBlock);
	}

	/**
	 * Returns the matching {@link PropertyDirection} from a given
	 * {@link IBlockState}. If the given block state does not contain a 'facing'
	 * property the method returns <code>null</code>.
	 * 
	 * @param blockState
	 *            The block state to check for a 'facing' property.
	 * @return The direction property that corresponds to the given block state.
	 */
	private PropertyDirection getPropertyDirectionFromBlockState(
			IBlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof BlockTorch) {
			return BlockTorch.FACING;
		} else if (block instanceof BlockDoor) {
			return BlockDoor.FACING;
		} else {
			return null;
		}
	}

	/**
	 * Get an increasing range of numbers between [0,number] if
	 * <code>number</code> > 0, or [number + 1, 1] if <code>number</code> < 0.
	 * 
	 * @param number
	 *            One end of the range. Can be both positive and negative.
	 * @return An increasing range of numbers.
	 */
	private Tuple getIncreasingRange(int number) {
		if (number > 0) {
			return new Tuple(0, number);
		} else if (number < 0) {
			return new Tuple(number + 1, 1);
		} else {
			return new Tuple(0, 0);
		}
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
	 * Converts an {@link EnumFacing} to a {@link RelativeFacing}.
	 * 
	 * @param facing
	 *            The {@link EnumFacing} to convert.
	 * @return The matching {@link RelativeFacing}
	 */
	private RelativeFacing absoluteToRelativeFacing(EnumFacing facing) {
		if (facing.equals(EnumFacing.UP)) {
			return RelativeFacing.UP;
		} else if (facing.equals(EnumFacing.DOWN)) {
			return RelativeFacing.DOWN;
		}

		EnumFacing playerFacing = Minecraft.getMinecraft()
				.getRenderViewEntity().getHorizontalFacing();

		int distance = (facing.getHorizontalIndex() - playerFacing
				.getHorizontalIndex()) % 4;
		if (distance < 0) {
			distance += 4;
		}

		return RelativeFacing.HORIZONTALS[distance];
	}

	/**
	 * Converts a {@link RelativeFacing} to a {@link EnumFacing}.
	 * 
	 * @param facing
	 *            The {@link RelativeFacing} to convert.
	 * @return The matching {@link EnumFacing}.
	 */
	private EnumFacing relativeToAbsoluteFacing(RelativeFacing facing) {
		if (facing.equals(RelativeFacing.UP)) {
			return EnumFacing.UP;
		} else if (facing.equals(RelativeFacing.DOWN)) {
			return EnumFacing.DOWN;
		}

		EnumFacing playerFacing = Minecraft.getMinecraft()
				.getRenderViewEntity().getHorizontalFacing();

		int index;
		for (index = 0; index < RelativeFacing.HORIZONTALS.length; index++) {
			if (RelativeFacing.HORIZONTALS[index].equals(facing)) {
				break;
			}
		}

		return EnumFacing
				.getHorizontal((playerFacing.getHorizontalIndex() + index) % 4);
	}

	/**
	 * Translates a relative offset (forward, right, up) to a relative offset
	 * (x, y, z).
	 * 
	 * @param forward
	 *            The number of blocks in the forward direction.
	 * @param right
	 *            The number of blocks to the right.
	 * @param up
	 *            The number of blocks up.
	 * @return An absolute offset in terms of x, y, and z.
	 */
	private BlockPos relativeToAbsoluteOffset(int forward, int right, int up) {
		EnumFacing facing = Minecraft.getMinecraft().getRenderViewEntity()
				.getHorizontalFacing();

		BlockPos position = new BlockPos(0, up, 0);

		switch (facing) {
		case EAST:
			position = position.add(forward, 0, right);
			break;
		case WEST:
			position = position.add(-forward, 0, -right);
			break;
		case NORTH:
			position = position.add(right, 0, -forward);
			break;
		case SOUTH:
			position = position.add(-right, 0, forward);
			break;
		default:
			// FIXME ERR
			break;
		}
		return position;
	}

	/**
	 * Tiny class to make ranges easier.
	 *
	 */
	public class Tuple {
		protected int start, end;

		public Tuple(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
}
