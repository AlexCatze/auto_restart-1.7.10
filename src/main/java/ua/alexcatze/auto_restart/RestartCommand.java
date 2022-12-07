package ua.alexcatze.auto_restart;

import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import ua.alexcatze.auto_restart.config.ConfigHandler;
import ua.alexcatze.auto_restart.util.ServerRestarter;

public class RestartCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return ConfigHandler.RESTART_COMMAND_INGAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AutoRestart.logger.info("{}: Restarting server", AutoRestart.MODID);
        ServerRestarter.restart(FMLServerHandler.instance().getServer());
    }
}
