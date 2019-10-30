package com.flemmli97.wdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandAddWhitelist implements ICommand {

    private final List<String> aliases = new ArrayList<String>();

    public CommandAddWhitelist() {
        this.aliases.add("wlr:whitelist");
    }

    @Override
    public String getName() {
        return "wlr:whitelist";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/wlr:whitelist <add,remove,list> {name}";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(this.getUsage(sender), new Object[0]);
        } else {
            if ("list".equals(args[0])) {
                sender.sendMessage(new TextComponentTranslation("commands.whitelist.list", new Object[] {server.getPlayerList().getWhitelistedPlayerNames().length, server.getPlayerList().getAvailablePlayerDat().length}));
                String[] astring = server.getPlayerList().getWhitelistedPlayerNames();
                sender.sendMessage(new TextComponentString(CommandBase.joinNiceString(astring)));
            } else if ("add".equals(args[0])) {
                if (args.length < 2) {
                    throw new WrongUsageException("commands.whitelist.add.usage", new Object[0]);
                }
                GameProfile gameprofile = server.getPlayerProfileCache().getGameProfileForUsername(args[1]);
                if (gameprofile == null) {
                    throw new CommandException("commands.whitelist.add.failed", new Object[] {args[1]});
                }
                server.getPlayerList().addWhitelistedPlayer(gameprofile);
                CommandBase.notifyCommandListener(sender, this, "commands.whitelist.add.success", new Object[] {args[1]});
            } else if ("remove".equals(args[0])) {
                if (args.length < 2) {
                    throw new WrongUsageException("commands.whitelist.remove.usage", new Object[0]);
                }
                GameProfile gameprofile1 = server.getPlayerList().getWhitelistedPlayers().getByName(args[1]);
                if (gameprofile1 == null) {
                    throw new CommandException("commands.whitelist.remove.failed", new Object[] {args[1]});
                }
                server.getPlayerList().removePlayerFromWhitelist(gameprofile1);
                CommandBase.notifyCommandListener(sender, this, "commands.whitelist.remove.success", new Object[] {args[1]});
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender.getCommandSenderEntity() instanceof EntityPlayer)
            return WhiteListData.get(server.getEntityWorld()).hasRole((EntityPlayer) sender.getCommandSenderEntity());
        return false;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, new String[] {"add", "remove", "list"});
        } else {
            if (args.length == 2) {
                if ("remove".equals(args[0])) {
                    return CommandBase.getListOfStringsMatchingLastWord(args, server.getPlayerList().getWhitelistedPlayerNames());
                }
                if ("add".equals(args[0])) {
                    return CommandBase.getListOfStringsMatchingLastWord(args, server.getPlayerProfileCache().getUsernames());
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
