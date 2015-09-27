package portablebuildingmod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * A structure is a collection of {@link BlockAtPosition} objects.
 *
 */
public class Structure implements Iterable<BlockAtPosition> {
	List<BlockAtPosition> blocks;
	List<BlockAtPosition> delayedBlocks;

	public static int BUILD_DELAY = 200;

	public Structure() {
		blocks = new ArrayList<BlockAtPosition>();
		delayedBlocks = new ArrayList<BlockAtPosition>();
	}

	public Structure(World world, BlockPos origin, Offset offset) {
		EnumFacing facing = Minecraft.getMinecraft().getRenderViewEntity()
				.getHorizontalFacing();

		Tuple forwardTuple = Tuple.getIncreasingRange(offset.getForward());
		Tuple upTuple = Tuple.getIncreasingRange(offset.getUp());
		Tuple rightTuple = Tuple.getIncreasingRange(offset.getRight());

		for (int f = forwardTuple.start; f < forwardTuple.end; f++) {
			for (int r = rightTuple.start; r < rightTuple.end; r++) {
				for (int u = upTuple.start; u < upTuple.end; u++) {
					IBlockState blockState = world.getBlockState(origin
							.add(offset.toAbsoluteOffset(facing)));
					this.addBlockAtPosition(new BlockAtPosition(blockState,
							new Offset(f, r, u)));
				}
			}
		}
	}

	/**
	 * Add a block at a position.
	 * 
	 * @param blockAtPos
	 *            The {@link BlockAtPosition} to add.
	 */
	public void addBlockAtPosition(BlockAtPosition blockAtPos) {
		IBlockState blockState = blockAtPos.getBlockState();
		if (shouldBeSaved(blockState)) {
			Block block = blockState.getBlock();
			if (block instanceof BlockTorch || block instanceof BlockDoor) {
				delayedBlocks.add(blockAtPos);
			} else {
				blocks.add(blockAtPos);
			}
		}
	}

	/**
	 * Places the structure in the given world at the given offset.
	 * 
	 * @param world
	 *            The world in which to build the structure.
	 * @param offset
	 *            The offset from the origin where to place the structure.
	 */
	public void build(World world, BlockPos offset) {
		build(world, offset, blocks);
		build(world, offset, delayedBlocks);
	}

	private void build(World world, BlockPos offset,
			List<BlockAtPosition> blocksAtPositions) {
		EnumFacing facing = Minecraft.getMinecraft().getRenderViewEntity()
				.getHorizontalFacing();

		for (BlockAtPosition blockAtPos : blocksAtPositions) {
			BlockPos placementPos = blockAtPos.getPosition()
					.toAbsoluteOffset(facing).add(offset);
			IBlockState blockState = blockAtPos.getBlockState();
			Block block = blockState.getBlock();

			if (block instanceof BlockDoor) {
				ItemDoor.placeDoor(world, placementPos,
						(EnumFacing) blockState.getValue(BlockDoor.FACING),
						block);
			} else {
				world.setBlockState(placementPos, blockState);
			}
		}
	}

	@Override
	public Iterator<BlockAtPosition> iterator() {
		List<BlockAtPosition> allBlocks = new ArrayList<BlockAtPosition>();
		allBlocks.addAll(blocks);
		allBlocks.addAll(delayedBlocks);
		return allBlocks.listIterator();
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
}
