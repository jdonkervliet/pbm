package portablebuildingmod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

/**
 * Registers the PBM mod commands in Minecraft.
 *
 */
public class Command implements ICommand {

	private List<String> aliases;
	private CommandParser parser;

	/**
	 * Creates a new command.
	 */
	public Command() {
		parser = new CommandParser(MinecraftServer.getServer().getEntityWorld());

		aliases = new ArrayList<String>();
		aliases.add("pbm");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender arg0,
			String[] arg1, BlockPos arg2) {
		return null;
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender arg0) {
		return true;
	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public void execute(ICommandSender arg0, String[] arg1)
			throws CommandException {
		List<String> arguments = new ArrayList<String>();
		for (String argument : arg1) {
			arguments.add(argument);
		}

		parser.parseCommand(arguments);
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/pbm delete <forward> <right> <up>\n"
				+ "/pbm save <name> <forward> <right> <up>\n"
				+ "/pbm load <name> <orientation> [<repeat forward> <repeat right> <repeat up>]";
	}

	@Override
	public String getName() {
		return "portable building mod commands";
	}

	@Override
	public boolean isUsernameIndex(String[] arg0, int arg1) {
		return false;
	}

}
