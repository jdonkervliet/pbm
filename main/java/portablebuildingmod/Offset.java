package portablebuildingmod;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * A relative offset in the 3D world.
 *
 */
public class Offset {
	/**
	 * Offset in the forward direction.
	 */
	int forward;
	/**
	 * Offset in the right direction.
	 */
	int right;
	/**
	 * Offset in the up direction.
	 */
	int up;

	/**
	 * Creates a new offset.
	 * 
	 * @param forwardOffset
	 *            Number of units forward.
	 * @param rightOffset
	 *            Number of units to the right.
	 * @param upOffset
	 *            Number of units up.
	 */
	public Offset(int forwardOffset, int rightOffset, int upOffset) {
		this.forward = forwardOffset;
		this.right = rightOffset;
		this.up = upOffset;
	}

	/**
	 * @return The amount of units in the forward direction.
	 */
	public int getForward() {
		return forward;
	}

	/**
	 * 
	 * @return The amount of units to the right.
	 */
	public int getRight() {
		return right;
	}

	/**
	 * 
	 * @return The amount of units in the up direction.
	 */
	public int getUp() {
		return up;
	}

	/**
	 * Set the number of units in the forward direction.
	 * 
	 * @param forward
	 *            The number of units.
	 */
	public void setForward(int forward) {
		this.forward = forward;
	}

	/**
	 * Set the number of units to the right.
	 * 
	 * @param right
	 *            The number of units.
	 */
	public void setRight(int right) {
		this.right = right;
	}

	/**
	 * Set the number of units in the up direction.
	 * 
	 * @param up
	 *            The number of units.
	 */
	public void setUp(int up) {
		this.up = up;
	}

	/**
	 * Translates this relative offset to an absolute offset (x, y, z),
	 * depending on where the player is looking.
	 * 
	 * @param facing
	 *            The direction the player is facing in.
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

	public String toString() {
		return String.format("%d %d %d", forward, right, up);
	}
}
