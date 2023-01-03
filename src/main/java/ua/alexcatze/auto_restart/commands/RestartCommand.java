package ua.alexcatze.auto_restart.commands;

import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import ua.alexcatze.auto_restart.AutoRestart;
import ua.alexcatze.auto_restart.util.ConfigHandler;
import ua.alexcatze.auto_restart.util.ServerRestarter;

public class RestartCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return ConfigHandler.RESTART_COMMAND_INGAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " [reason]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AutoRestart.logger.info("{}: Restarting server", AutoRestart.MODID);
        String reason = AutoRestart.defaultRestartReason;
        if (args.length > 0) {
            reason = String.join(" ", args);
        }
        ServerRestarter.restart(FMLServerHandler.instance().getServer(), reason);
    }
}
