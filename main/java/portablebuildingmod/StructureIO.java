package portablebuildingmod;

import java.util.Scanner;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

public class StructureIO {

	public Structure read(Scanner scanner) {

		Structure structure = new Structure();

		while (scanner.hasNextLine()) {
			String[] lineparts = scanner.nextLine().split(",");

			int forwardOffset = Integer.parseInt(lineparts[0]);
			int rightOffset = Integer.parseInt(lineparts[1]);
			int upOffset = Integer.parseInt(lineparts[2]);
			int blockType = Integer.parseInt(lineparts[3]);

			RelativeFacing facing = RelativeFacing.valueOf(lineparts[4]);
			Offset offset = new Offset(forwardOffset, rightOffset, upOffset);
			IBlockState block = Block.getStateById(blockType);

			if (!facing.equals(RelativeFacing.NONE)) {
				PropertyDirection propertyDirection = getPropertyDirectionFromBlockState(block);
				if (propertyDirection != null) {
					block = block.withProperty(propertyDirection,
							relativeToAbsoluteFacing(facing));
				}
			}

			structure.addBlockAtPosition(new BlockAtPosition(block, offset));
		}

		return structure;
	}

	public String write(Structure structure) {
		StringBuilder stringBuilder = new StringBuilder();

		String newline = System.getProperty("line.separator");
		for (BlockAtPosition blockAtPos : structure) {
			Offset offset = blockAtPos.getPosition();
			IBlockState blockState = blockAtPos.getBlockState();

			PropertyDirection propertyDirection = getPropertyDirectionFromBlockState(blockState);
			EnumFacing facing = null;
			if (propertyDirection != null) {
				facing = (EnumFacing) blockState.getValue(propertyDirection);
			}
			RelativeFacing relativeFacing = facing != null ? absoluteToRelativeFacing(facing)
					: RelativeFacing.NONE;

			int blockid = Block.getIdFromBlock(blockState.getBlock());
			stringBuilder.append(String.format("%d,%d,%d,%d,%s",
					offset.getForward(), offset.getRight(), offset.getUp(),
					blockid, relativeFacing.toString()));
			stringBuilder.append(newline);
		}

		return stringBuilder.toString();
	}

	/**
	 * Converts an {@link EnumFacing} to a {@link RelativeFacing}.
	 * 
	 * @param facing
	 *            The {@link EnumFacing} to convert.
	 * @return The matching {@link RelativeFacing}
	 */
	private RelativeFacing absoluteToRelativeFacing(EnumFacing facing) {
		if (facing.equals(EnumFacing.UP)) {
			return RelativeFacing.UP;
		} else if (facing.equals(EnumFacing.DOWN)) {
			return RelativeFacing.DOWN;
		}

		EnumFacing playerFacing = Minecraft.getMinecraft()
				.getRenderViewEntity().getHorizontalFacing();

		int distance = (facing.getHorizontalIndex() - playerFacing
				.getHorizontalIndex()) % 4;
		if (distance < 0) {
			distance += 4;
		}

		return RelativeFacing.HORIZONTALS[distance];
	}

	/**
	 * Returns the matching {@link PropertyDirection} from a given
	 * {@link IBlockState}. If the given block state does not contain a 'facing'
	 * property the method returns <code>null</code>.
	 * 
	 * @param blockState
	 *            The block state to check for a 'facing' property.
	 * @return The direction property that corresponds to the given block state.
	 */
	private PropertyDirection getPropertyDirectionFromBlockState(
			IBlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof BlockTorch) {
			return BlockTorch.FACING;
		} else if (block instanceof BlockDoor) {
			return BlockDoor.FACING;
		} else {
			return null;
		}
	}

	/**
	 * Converts a {@link RelativeFacing} to a {@link EnumFacing}.
	 * 
	 * @param facing
	 *            The {@link RelativeFacing} to convert.
	 * @return The matching {@link EnumFacing}.
	 */
	private EnumFacing relativeToAbsoluteFacing(RelativeFacing facing) {
		if (facing.equals(RelativeFacing.UP)) {
			return EnumFacing.UP;
		} else if (facing.equals(RelativeFacing.DOWN)) {
			return EnumFacing.DOWN;
		}

		EnumFacing playerFacing = Minecraft.getMinecraft()
				.getRenderViewEntity().getHorizontalFacing();

		int index;
		for (index = 0; index < RelativeFacing.HORIZONTALS.length; index++) {
			if (RelativeFacing.HORIZONTALS[index].equals(facing)) {
				break;
			}
		}

		return EnumFacing
				.getHorizontal((playerFacing.getHorizontalIndex() + index) % 4);
	}
}
