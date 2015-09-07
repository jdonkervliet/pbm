package portablebuildingmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import net.minecraft.block.Block;
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

	public void deleteRelative(int forward, int right, int up) {
		BlockPos offset = relativeToAbsoluteOffset(forward, right, up);
		delete(offset.getX(), offset.getY(), offset.getZ());
	}

	public void saveRelative(String name, int forward, int right, int up) {
		BlockPos offset = relativeToAbsoluteOffset(forward, right, up);
		save(name, offset.getX(), offset.getY(), offset.getZ());
	}

	public void delete(int xdir, int ydir, int zdir) {
		Tuple xTuple = getIncreasingRange(xdir);
		Tuple yTuple = getIncreasingRange(ydir);
		Tuple zTuple = getIncreasingRange(zdir);

		for (int x = xTuple.start; x < xTuple.end; x++) {
			for (int z = zTuple.start; z < zTuple.end; z++) {
				for (int y = yTuple.start; y < yTuple.end; y++) {
					breakBlock(blockInCrosshair().add(x, y, z));
				}
			}
		}
	}

	public void save(String name, int xdir, int ydir, int zdir) {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(BUILDING_DIR + name);
		} catch (FileNotFoundException e) {
			// FIXME care about this.
		}

		Tuple xTuple = getIncreasingRange(xdir);
		Tuple yTuple = getIncreasingRange(ydir);
		Tuple zTuple = getIncreasingRange(zdir);

		for (int x = xTuple.start; x < xTuple.end; x++) {
			for (int z = zTuple.start; z < zTuple.end; z++) {
				for (int y = yTuple.start; y < yTuple.end; y++) {
					// FIXME Write to file
					BlockPos basepos = blockInCrosshair();
					BlockPos pos = blockInCrosshair().add(x, y, z);
					int blockid = Block.getIdFromBlock(world.getBlockState(pos)
							.getBlock());
					writer.println(String.format("%d,%d,%d,%d", pos.getX()
							- basepos.getX(), pos.getY() - basepos.getY(),
							pos.getZ() - basepos.getZ(), blockid));
				}
			}
		}

		writer.close();
	}

	public void build(String name) throws FileNotFoundException {
		BlockPos crosshairPos = blockInCrosshair();
		System.out.println("Building at " + crosshairPos);

		Scanner scanner = new Scanner(new File(BUILDING_DIR + name));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			System.out.println("Reading line " + line);
			String[] lineparts = line.split(",");

			int xoffset = Integer.parseInt(lineparts[0]);
			int yoffset = Integer.parseInt(lineparts[1]);
			int zoffset = Integer.parseInt(lineparts[2]);
			int blockType = Integer.parseInt(lineparts[3]);

			BlockPos offset = absoluteToRelativeOffset(xoffset, yoffset,
					zoffset);
			BlockPos placementPos = crosshairPos.add(offset);
			IBlockState block = Block.getStateById(blockType);
			System.out.println("Putting " + block + " at " + placementPos);
			world.setBlockState(placementPos, block);
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

	private BlockPos absoluteToRelativeOffset(int x, int y, int z) {
		EnumFacing facing = Minecraft.getMinecraft().getRenderViewEntity()
				.getHorizontalFacing();

		BlockPos position = new BlockPos(0, y, 0);

		switch (facing) {
		case EAST:
			position = position.add(z, 0, x);
			break;
		case WEST:
			position = position.add(-z, 0, -x);
			break;
		case NORTH:
			position = position.add(x, 0, -z);
			break;
		case SOUTH:
			position = position.add(-x, 0, z);
			break;
		default:
			// FIXME ERR
			break;
		}
		return position;
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
