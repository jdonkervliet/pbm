package portablebuildingmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandParser {

	public static final String MOD_DELETE_COMMAND = "delete";
	public static final String MOD_SAVE_COMMAND = "save";
	public static final String MOD_BUILD_COMMAND = "build";

	private BuildingSaver builder;

	public CommandParser(World world) {
		this.builder = new BuildingSaver();
	}

	public void parseCommand(List<String> command) {
		try {
			if (command.get(0).equals(MOD_DELETE_COMMAND)) {
				parseDeleteCommand(command.subList(1, command.size()));
			} else if (command.get(0).equals(MOD_SAVE_COMMAND)) {
				parseSaveCommand(command.subList(1, command.size()));
			} else if (command.get(0).equals(MOD_BUILD_COMMAND)) {
				parseBuildCommand(command.subList(1, command.size()));
			} else {
				System.out.println("[[[");
				throw new InvalidPBMCommandException();
			}
		} catch (InvalidPBMCommandException e) {
			sendChatMessage("Invalid command");
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
				System.out.println("###");
				throw new InvalidPBMCommandException();
			}

			builder.saveRelative(name, forward, right, up);
		} else {
			System.out.println("@@@");
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
			builder.deleteRelative(forward, right, up);
		} else {
			throw new InvalidPBMCommandException();
		}
	}

	private void parseBuildCommand(List<String> command)
			throws InvalidPBMCommandException {
		System.out.println("Build command.");
		if (command.size() == 1) {
			if (new File(BuildingSaver.BUILDING_DIR + command.get(0)).exists()) {
				System.out.println("Calling build.");
				try {
					builder.build(command.get(0));
				} catch (FileNotFoundException e) {
					// FIXME This should not happen, we just checked.
					e.printStackTrace();
				}
			} else {
				System.out.println("Cannot find file " + command.get(0));
			}
		} else {
			throw new InvalidPBMCommandException();
		}
	}

	private void sendChatMessage(String message) {
		Minecraft.getMinecraft().thePlayer
				.addChatMessage(new ChatComponentText(message));
	}

}
