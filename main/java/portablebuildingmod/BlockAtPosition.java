package portablebuildingmod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class BlockAtPosition {
	BlockPos position;
	IBlockState block;

	public BlockAtPosition(IBlockState block, BlockPos position) {
		this.block = block;
		this.position = position;
	}

	public BlockPos getPosition() {
		return position;
	}

	public void setPosition(BlockPos position) {
		this.position = position;
	}

	public IBlockState getBlock() {
		return block;
	}

	public void setBlock(IBlockState block) {
		this.block = block;
	}
}
