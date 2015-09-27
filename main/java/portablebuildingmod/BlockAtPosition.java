package portablebuildingmod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

/**
 * Holds both a {@link IBlockState} and a {@link BlockPos}. This makes is easy
 * to define structures.
 *
 */
public class BlockAtPosition {
	Offset offset;
	IBlockState block;

	/**
	 * @param block
	 *            The {@link IBlockState}.
	 * @param position
	 *            The {@link BlockPos} of the block state.
	 */
	public BlockAtPosition(IBlockState block, Offset offset) {
		this.block = block;
		this.offset = offset;
	}

	/**
	 * @return The {@link Offset}.
	 */
	public Offset getPosition() {
		return offset;
	}

	/**
	 * Set the {@link BlockPos}.
	 * 
	 * @param position
	 *            The block position.
	 */
	public void setPosition(Offset offset) {
		this.offset = offset;
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
