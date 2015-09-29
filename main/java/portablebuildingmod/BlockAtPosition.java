package portablebuildingmod;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

/**
 * Holds both a {@link IBlockState} and a {@link BlockPos}. This makes is easy
 * to define structures.
 *
 */
public class BlockAtPosition {
	/**
	 * Position of the block.
	 */
	Offset offset;
	/**
	 * The block itself.
	 */
	IBlockState block;

	/**
	 * @param block
	 *            The {@link IBlockState}.
	 * @param offset
	 *            The {@link Offset} of the block state.
	 */
	public BlockAtPosition(IBlockState block, Offset offset) {
		this.block = block;
		this.offset = offset;
	}

	/**
	 * @return The {@link IBlockState}.
	 */
	public IBlockState getBlockState() {
		return block;
	}

	/**
	 * @return The {@link Offset}.
	 */
	public Offset getPosition() {
		return offset;
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

	/**
	 * Set the {@link BlockPos}.
	 * 
	 * @param offset
	 *            The block offset.
	 */
	public void setPosition(Offset offset) {
		this.offset = offset;
	}

	public String toString() {
		return String.format("(%s) %d", offset,
				Block.getIdFromBlock(block.getBlock()));
	}
}
