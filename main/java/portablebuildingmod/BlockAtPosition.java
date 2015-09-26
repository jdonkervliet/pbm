package portablebuildingmod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

/**
 * Holds both a {@link IBlockState} and a {@link BlockPos}. This makes is easy
 * to define structures.
 *
 */
public class BlockAtPosition {
	BlockPos position;
	IBlockState block;

	/**
	 * @param block
	 *            The {@link IBlockState}.
	 * @param position
	 *            The {@link BlockPos} of the block state.
	 */
	public BlockAtPosition(IBlockState block, BlockPos position) {
		this.block = block;
		this.position = position;
	}

	/**
	 * @return The {@link BlockPos}.
	 */
	public BlockPos getPosition() {
		return position;
	}

	/**
	 * Set the {@link BlockPos}.
	 * 
	 * @param position
	 *            The block position.
	 */
	public void setPosition(BlockPos position) {
		this.position = position;
	}

	/**
	 * @return The {@link IBlockState}.
	 */
	public IBlockState getBlockState() {
		return block;
	}

	/**
	 * Set the {@link IBlockState}.
	 * 
	 * @param block
	 *            The block state.
	 */
	public void setBlock(IBlockState block) {
		this.block = block;
	}
}
