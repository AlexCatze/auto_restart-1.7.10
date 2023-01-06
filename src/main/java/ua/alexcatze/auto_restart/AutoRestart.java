package ua.alexcatze.auto_restart;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import java.util.ArrayList;
import java.util.Timer;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.alexcatze.auto_restart.commands.RestartAfterCommand;
import ua.alexcatze.auto_restart.commands.RestartCommand;
import ua.alexcatze.auto_restart.util.AutoRestartTask;
import ua.alexcatze.auto_restart.util.ConfigHandler;
import ua.alexcatze.auto_restart.util.ServerRestarter;

@Mod(modid = AutoRestart.MODID, version = AutoRestart.VERSION,name = AutoRestart.MODNAME, acceptableRemoteVersions = "*")
public class AutoRestart {

    public static final ArrayList<AutoRestartTask> AUTO_RESTART_TASKS = new ArrayList<>();
    public static Logger logger = LogManager.getLogger(AutoRestart.MODID);

    public static String defaultRestartReason = StatCollector.translateToLocal("autorestart.title.restart");

    public static final String MODID = "GRADLETOKEN_MODID";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String MODNAME = "GRADLETOKEN_MODNAME";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public static void handleServerStoppedEvent(FMLServerStoppedEvent event) {
        if (!ServerRestarter.shouldDoRestart()) {
            ServerRestarter.createStopFile();
        }
    }

    @Mod.EventHandler
    public static void handleServerStartedEvent(FMLServerStartedEvent event) {
        new Timer(true).scheduleAtFixedRate(new AutoRestartService(MinecraftServer.getServer()), 60 * 1000, 1000);

        ((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(new RestartCommand());
        ((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(new RestartAfterCommand());
    }

    @Mod.EventHandler
    public static void handlerServerStartingEvent(FMLServerStartingEvent event) {
        ServerRestarter.createExceptionFile();
    }
}
