package ua.alexcatze.auto_restart.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TimerTask;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.alexcatze.auto_restart.config.AutoRestartTime;
import ua.alexcatze.auto_restart.config.ConfigHandler;
import ua.alexcatze.auto_restart.config.Timing;
import ua.alexcatze.auto_restart.util.ServerRestarter;

public class AutoRestartTask extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger(AutoRestartTask.class);

    private final MinecraftServer server;

    private boolean isRestartRunning = false;

    // private static LocalDateTime empty_time;

    // private static long tpsProblemDuration = 0;

    public AutoRestartTask(MinecraftServer _server) {

        server = _server;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {

        if (isRestartRunning) {
            return;
        }
        LocalDateTime current_time = LocalDateTime.now();
        /*if( ServerConfig.getOnEmptyRestartEnabled() && empty_time != null ) {
        	if( Duration.between( empty_time, current_time ).getSeconds() >=
        		ServerConfig.getOnEmptyRestartDelay().getSeconds() ) {
        		LOGGER.info( "Auto restarting Server on empty server" );
        		restart();
        		return;
        	}
        }*/
        if (ConfigHandler.AUTO_RESTART_ENABLED) {
            for (AutoRestartTime autoRestartTime : ConfigHandler.autoRestartTimes) {
                Duration difference = autoRestartTime.getDifferenceTo(current_time);
                for (Timing warning_time : ConfigHandler.autoRestartWarningTimes) {
                    if (difference.getSeconds() == warning_time.getSeconds()) {
                        server.addChatMessage(new ChatComponentText(
                                String.format("Restarting in %s...", warning_time.getDisplayString())));
                        /*server.getPlayerList().broadcastMessage(
                        	new StringTextComponent( String.format(
                        		"Restarting in %s...",
                        		warning_time.getDisplayString()
                        	) ).setStyle( Style.EMPTY.applyFormat( TextFormatting.YELLOW ) ),
                        	ChatType.SYSTEM,
                        	Util.NIL_UUID
                        );*/
                    }
                }
                if (autoRestartTime.getHour() == current_time.getHour()
                        && autoRestartTime.getMinute() == current_time.getMinute()) {
                    LOGGER.info("Auto restarting Server on auto restarting time");
                    restart();
                    return;
                }
            }
        }
    }

    private void restart() {

        ServerRestarter.restart(server);
        isRestartRunning = true;
    }
    /*
    public static void resetEmptyTime() {

    	empty_time = null;
    	LOGGER.info( "Empty server timer stopped" );
    }

    public static void setEmptyTime() {

    	empty_time = LocalDateTime.now();
    	LOGGER.info( "Empty server timer started" );
    }*/
}
