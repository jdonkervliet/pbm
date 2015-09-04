package portablebuildingmod;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import net.minecraft.block.Block;
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
		Position offset = relativeToAbsoluteOffset(forward, right, up);
		delete(offset.x, offset.y, offset.z);
	}

	public void saveRelative(String name, int forward, int right, int up) {
		Position offset = relativeToAbsoluteOffset(forward, right, up);
		save(name, offset.x, offset.y, offset.z);
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

	private Position relativeToAbsoluteOffset(int forward, int right, int up) {
		EnumFacing facing = Minecraft.getMinecraft().getRenderViewEntity()
				.getHorizontalFacing();

		Position position = new Position();
		position.y = up;

		switch (facing) {
		case EAST:
			position.x = forward;
			position.z = right;
			break;
		case WEST:
			position.x = -forward;
			position.z = -right;
			break;
		case NORTH:
			position.x = right;
			position.z = -forward;
			break;
		case SOUTH:
			position.x = -right;
			position.z = forward;
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

	public class Position {
		protected int x, y, z;
	}

}
