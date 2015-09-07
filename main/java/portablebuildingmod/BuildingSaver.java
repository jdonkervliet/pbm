package portablebuildingmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BuildingSaver {

	public static String BUILDING_DIR = "/tmp/";

	World world;

	public BuildingSaver() {
		this.world = MinecraftServer.getServer().getEntityWorld();
	}

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

	public void save(String name, int forward, int right, int up) {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(BUILDING_DIR + name);
		} catch (FileNotFoundException e) {
			// FIXME care about this.
		}

		Tuple forwardTuple = getIncreasingRange(forward);
		Tuple upTuple = getIncreasingRange(up);
		Tuple rightTuple = getIncreasingRange(right);
		BlockPos crosshairBlock = blockInCrosshair();

		for (int f = forwardTuple.start; f < forwardTuple.end; f++) {
			for (int r = rightTuple.start; r < rightTuple.end; r++) {
				for (int u = upTuple.start; u < upTuple.end; u++) {
					// FIXME Write to file
					BlockPos pos = crosshairBlock.add(relativeToAbsoluteOffset(
							f, r, u));

					IBlockState blockState = world.getBlockState(pos);
					EnumFacing facing = (EnumFacing) blockState.getProperties()
							.get(PropertyDirection.create("facing"));

					int blockid = Block.getIdFromBlock(blockState.getBlock());
					writer.println(String.format("%d,%d,%d,%d,%s", f, r, u,
							blockid, facing));
				}
			}
		}

		writer.close();
	}

	public void build(String name) throws FileNotFoundException {
		build(name, 1);
	}

	public void build(String name, int verticalOffset)
			throws FileNotFoundException {
		BlockPos crosshairBlock = blockInCrosshair();

		Scanner scanner = new Scanner(new File(BUILDING_DIR + name));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineparts = line.split(",");

			int forwardOffset = Integer.parseInt(lineparts[0]);
			int upOffset = Integer.parseInt(lineparts[1]);
			int rightOffset = Integer.parseInt(lineparts[2]);
			int blockType = Integer.parseInt(lineparts[3]);

			EnumFacing facing = null;
			if (lineparts[4] != "null") {
				facing = EnumFacing.byName(lineparts[4]);
			}

			BlockPos offset = relativeToAbsoluteOffset(forwardOffset, upOffset,
					rightOffset);
			BlockPos placementPos = crosshairBlock.add(offset);
			IBlockState block = Block.getStateById(blockType);
			if (block.getBlock() instanceof BlockTorch) {
				world.setBlockState(placementPos.up(verticalOffset),
						block.withProperty(BlockTorch.FACING, facing));
			} else {
				world.setBlockState(placementPos.up(verticalOffset), block);
			}
		}
		scanner.close();
	}

	private Tuple getIncreasingRange(int number) {
		if (number > 0) {
			return new Tuple(0, number);
		} else if (number < 0) {
			return new Tuple(number + 1, 1);
		} else {
			return new Tuple(0, 0);
		}
	}

	private BlockPos blockInCrosshair() {
		return Minecraft.getMinecraft().getRenderViewEntity()
				.rayTrace(200, 1.0F).getBlockPos();
	}

	private void breakBlock(BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		block.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
		world.setBlockToAir(pos);
	}

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

	public class Tuple {
		protected int start, end;

		public Tuple(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
}
