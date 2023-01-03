package ua.alexcatze.auto_restart.commands;

import java.time.LocalTime;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.ArrayUtils;
import ua.alexcatze.auto_restart.AutoRestart;
import ua.alexcatze.auto_restart.util.AutoRestartTask;
import ua.alexcatze.auto_restart.util.ConfigHandler;
import ua.alexcatze.auto_restart.util.Timing;

public class RestartAfterCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return ConfigHandler.RESTART_AFTER_COMMAND_INGAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getCommandUsage();
    }

    public String getCommandUsage() {
        return "/" + getCommandName() + " <time> [reason]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1 || !Timing.parse(args[0]).isPresent()) throw new WrongUsageException(getCommandUsage());
        LocalTime time = LocalTime.now().plusSeconds(Timing.parse(args[0]).get().getSeconds());
        AutoRestartTask newTask = AutoRestartTask.build(time.getHour(), time.getMinute());
        if (args.length > 1) newTask.setReason(String.join(" ", ArrayUtils.removeElement(args, 0)));
        sender.addChatMessage(new ChatComponentText(
                StatCollector.translateToLocalFormatted("autorestart.title.scheduledrestart", newTask)));
        AutoRestart.AUTO_RESTART_TASKS.add(newTask);
    }
}
