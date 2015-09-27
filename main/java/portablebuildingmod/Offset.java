package portablebuildingmod;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Offset {
	int forward, right, up;

	public Offset(int forwardOffset, int rightOffset, int upOffset) {
		this.forward = forwardOffset;
		this.right = rightOffset;
		this.up = upOffset;
	}

	public int getForward() {
		return forward;
	}

	public void setForward(int forward) {
		this.forward = forward;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getUp() {
		return up;
	}

	public void setUp(int up) {
		this.up = up;
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
	public BlockPos toAbsoluteOffset(EnumFacing facing) {
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

}
