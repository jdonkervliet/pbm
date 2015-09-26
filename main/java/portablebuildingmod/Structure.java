package portablebuildingmod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * A structure is a collection of {@link BlockAtPosition} objects.
 *
 */
public class Structure {
	List<BlockAtPosition> blocks;
	List<BlockAtPosition> delayedBlocks;

	public static int BUILD_DELAY = 200;

	public Structure() {
		blocks = new ArrayList<BlockAtPosition>();
		delayedBlocks = new ArrayList<BlockAtPosition>();
	}

	/**
	 * Add a block at a position.
	 * 
	 * @param blockAtPos
	 *            The {@link BlockAtPosition} to add.
	 */
	public void addBlockAtPosition(BlockAtPosition blockAtPos) {
		Block block = blockAtPos.getBlockState().getBlock();
		if (block instanceof BlockTorch || block instanceof BlockDoor) {
			delayedBlocks.add(blockAtPos);
		} else {
			blocks.add(blockAtPos);
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
		for (BlockAtPosition blockAtPos : blocksAtPositions) {
			BlockPos placementPos = blockAtPos.getPosition().add(offset);
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
}
