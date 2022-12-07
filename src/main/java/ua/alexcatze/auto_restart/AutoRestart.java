package ua.alexcatze.auto_restart;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import java.util.Timer;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.alexcatze.auto_restart.config.ConfigHandler;
import ua.alexcatze.auto_restart.task.AutoRestartTask;
import ua.alexcatze.auto_restart.util.ServerRestarter;

@Mod(modid = AutoRestart.MODID, acceptableRemoteVersions = "*")
public class AutoRestart {

    public static Logger logger = LogManager.getLogger(AutoRestart.MODID);

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public static void handleServerStoppedEvent(FMLServerStoppedEvent event) {
        if (ServerRestarter.shouldDoRestart()) {
            ServerRestarter.restartServer();
        } else {
            if (MinecraftServer.getServer().isServerRunning()) {
                if (ConfigHandler.AUTO_RESTART_ON_CRASH) {
                    ServerRestarter.restartServer();
                }
            } else {
                ServerRestarter.createStopFile();
            }
        }
    }

    @Mod.EventHandler
    public static void handleServerStartedEvent(FMLServerStartedEvent event) {
        new Timer(true).scheduleAtFixedRate(new AutoRestartTask(MinecraftServer.getServer()), 60 * 1000, 1000);

        ((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(new RestartCommand());
    }

    @Mod.EventHandler
    public static void handlerServerStartingEvent(FMLServerStartingEvent event) {
        ServerRestarter.createExceptionFile();
    }

    public static final String MODID = "auto_restart";
}
