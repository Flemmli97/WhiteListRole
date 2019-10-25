package com.flemmli97.wdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandAddWhitelistRole implements ICommand {

	private final List<String> aliases = new ArrayList<String>();

	public CommandAddWhitelistRole() {
		this.aliases.add("wlr:addWhitelistRole");
	}

	@Override
	public String getName() {
		return "wlr:addWhitelistRole";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/wlr:addWhitelistRole <add,remove,list> {name}";
	}

	@Override
	public List<String> getAliases() {
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!args[0].equals("list") && args.length < 2) {
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		} else {
			if (args[0].equals("list")) {
				Set<String> astring = WhiteListData.get(server.getEntityWorld()).players(server);
				sender.sendMessage(
					new TextComponentString("People with whitelist permission: (" + astring.size() + "):"));
				sender.sendMessage(new TextComponentString(joinNiceString(astring)));
			} else if (args[0].equals("remove")) {
				if (WhiteListData.get(server.getEntityWorld()).removePlayer(server, args[1]))
					CommandBase.notifyCommandListener(sender, this, "Removed whitelist permission for %s",
						new Object[] { args[1] });
				else {
					throw new CommandException("Couldnt remove permission for %s", new Object[] { args[1] });
				}
			} else if (args[0].equals("add")) {
				if (WhiteListData.get(server.getEntityWorld()).addPlayer(server, args[1]))
					CommandBase.notifyCommandListener(sender, this, "Added whitelist permission for %s",
						new Object[] { args[1] });
				else {
					throw new CommandException("Couldnt add permission for $s", new Object[] { args[1] });
				}
			}
		}
	}

	private static String joinNiceString(Set<String> elements) {
		StringBuilder stringbuilder = new StringBuilder();
		int i = 0;
		for (String s : elements) {
			if (i > 0) {
				if (i == elements.size() - 1) {
					stringbuilder.append(" and ");
				} else {
					stringbuilder.append(", ");
				}
			}
			stringbuilder.append(s);
			i++;
		}
		return stringbuilder.toString();
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(3, this.getName());
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
		BlockPos targetPos) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, new String[] { "add", "remove", "list" });
		} else {
			if (args.length == 2) {
				if ("remove".equals(args[0])) {
					return CommandBase.getListOfStringsMatchingLastWord(args,
						server.getPlayerList().getWhitelistedPlayerNames());
				}

				if ("add".equals(args[0])) {
					return CommandBase.getListOfStringsMatchingLastWord(args,
						server.getPlayerProfileCache().getUsernames());
				}
			}
			return Collections.<String>emptyList();
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}
}
