package portablebuildingmod;

import java.io.FileNotFoundException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Parses PBM commands from Minecraft.
 *
 */
public class CommandParser {

	/**
	 * Keyword to trigger the delete command.
	 */
	public static final String MOD_DELETE_COMMAND = "delete";
	/**
	 * Keyword to trigger the save command.
	 */
	public static final String MOD_SAVE_COMMAND = "save";
	/**
	 * Keyword to trigger the build command.
	 */
	public static final String MOD_BUILD_COMMAND = "build";

	private Builder builder;

	/**
	 * Create a new command parser that executes the given commands on a given
	 * world.
	 * 
	 * @param world
	 *            The world.
	 */
	public CommandParser(World world) {
		this.builder = new Builder();
	}

	/**
	 * Parse a command where each argument is an element in the given list.
	 * 
	 * @param command
	 *            The command and its arguments.
	 */
	public void parseCommand(List<String> command) {
		try {
			if (command.get(0).equals(MOD_DELETE_COMMAND)) {
				parseDeleteCommand(command.subList(1, command.size()));
			} else if (command.get(0).equals(MOD_SAVE_COMMAND)) {
				parseSaveCommand(command.subList(1, command.size()));
			} else if (command.get(0).equals(MOD_BUILD_COMMAND)) {
				parseBuildCommand(command.subList(1, command.size()));
			} else {
				throw new InvalidPBMCommandException();
			}
		} catch (InvalidPBMCommandException e) {
			sendChatMessage("Invalid command");
		}
	}

	private void parseBuildCommand(List<String> command)
			throws InvalidPBMCommandException {
		if (command.size() == 1 || command.size() == 2) {
			try {
				if (command.size() == 1) {
					builder.build(command.get(0));
				} else if (command.size() == 2) {
					builder.build(command.get(0),
							Integer.parseInt(command.get(1)));
				}
			} catch (FileNotFoundException e) {
				System.out.println("Cannot find file " + command.get(0));
				throw new InvalidPBMCommandException();
			}
		} else {
			throw new InvalidPBMCommandException();
		}
	}

	private void parseDeleteCommand(List<String> command)
			throws InvalidPBMCommandException {
		if (command.size() == 3) {
			int forward, right, up;
			try {
				forward = Integer.parseInt(command.get(0));
				right = Integer.parseInt(command.get(1));
				up = Integer.parseInt(command.get(2));
			} catch (NumberFormatException e) {
				throw new InvalidPBMCommandException();
			}
			builder.delete(forward, right, up);
		} else {
			throw new InvalidPBMCommandException();
		}
	}

	private void parseSaveCommand(List<String> command)
			throws InvalidPBMCommandException {
		if (command.size() == 4 && command.get(0).matches("\\w+")) {
			String name = command.get(0);
			int forward, right, up;
			try {
				forward = Integer.parseInt(command.get(1));
				right = Integer.parseInt(command.get(2));
				up = Integer.parseInt(command.get(3));
			} catch (NumberFormatException e) {
				throw new InvalidPBMCommandException();
			}

			builder.save(name, forward, right, up);
		} else {
			throw new InvalidPBMCommandException();
		}
	}

	private void sendChatMessage(String message) {
		Minecraft.getMinecraft().thePlayer
				.addChatMessage(new ChatComponentText(message));
	}

}
