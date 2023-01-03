package ua.alexcatze.auto_restart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimerTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import ua.alexcatze.auto_restart.util.AutoRestartTask;
import ua.alexcatze.auto_restart.util.ConfigHandler;
import ua.alexcatze.auto_restart.util.ServerRestarter;
import ua.alexcatze.auto_restart.util.Timing;

public class AutoRestartService extends TimerTask {

    private final MinecraftServer server;

    public AutoRestartService(MinecraftServer _server) {

        server = _server;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {

        if (ServerRestarter.shouldDoRestart()) {
            return;
        }
        LocalDateTime current_time = LocalDateTime.now();
        if (ConfigHandler.AUTO_RESTART_ENABLED) {
            for (AutoRestartTask autoRestartTask : AutoRestart.AUTO_RESTART_TASKS) {
                Duration difference = autoRestartTask.getDifferenceTo(current_time);
                for (Timing warning_time : ConfigHandler.autoRestartWarningTimes) {
                    if (difference.getSeconds() == warning_time.getSeconds()) {
                        for (EntityPlayerMP player : (ArrayList<EntityPlayerMP>)
                                new ArrayList(server.getConfigurationManager().playerEntityList))
                            player.addChatMessage(new ChatComponentText(StatCollector.translateToLocalFormatted(
                                    "autorestart.title.restartafter", warning_time.getDisplayString())));
                    }
                }
                if (autoRestartTask.getHour() == current_time.getHour()
                        && autoRestartTask.getMinute() == current_time.getMinute()) {
                    AutoRestart.logger.info("Auto restarting Server on auto restarting time");
                    ServerRestarter.restart(server, autoRestartTask.getReason());
                    return;
                }
            }
        }
    }
}
