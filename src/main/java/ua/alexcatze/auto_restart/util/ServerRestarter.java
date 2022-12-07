package ua.alexcatze.auto_restart.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.alexcatze.auto_restart.AutoRestart;
import ua.alexcatze.auto_restart.config.ConfigHandler;

public class ServerRestarter {

    private static final Logger LOGGER = LogManager.getLogger(ServerRestarter.class);

    private static volatile boolean shouldDoRestart = false;

    public static void restart(MinecraftServer server) {

        shouldDoRestart = true;
        createRestartFile();
        server.initiateShutdown();
        // server.halt( false );
    }

    public static void restartServer() {

        if (!ConfigHandler.USES_EXTERNAL_RESTART_SCRIPT) {
            LOGGER.info("Restart Server");
            ProcessBuilder builder = new ProcessBuilder(ConfigHandler.RESTART_COMMAND);
            try {
                builder.start();
            } catch (IOException exception) {
                LOGGER.error("Auto Restart could not be done.", exception);
            }
        }
    }

    public static void createExceptionFile() {

        saveToFile(StopType.EXCEPTION);
    }

    public static void createStopFile() {

        saveToFile(StopType.STOP);
    }

    private static void createRestartFile() {

        saveToFile(StopType.RESTART);
    }

    private static void saveToFile(StopType type) {

        FileWriter fileWriter = null;
        try {
            LOGGER.info("Saving restart status \"{}\" to file", type);
            File file = new File("." + File.separator + AutoRestart.MODID + File.separator + "restart");
            if (file.exists() || file.getParentFile().mkdirs() && file.createNewFile()) {
                fileWriter = new FileWriter(file);
                fileWriter.write(String.valueOf(type.ordinal() - 1));
                fileWriter.flush();
            } else {
                LOGGER.error("Restart File could not be created");
            }
        } catch (IOException exception) {
            LOGGER.error("FileWriter failed", exception);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException exception) {
                    LOGGER.error("FileWriter failed to close", exception);
                }
            }
        }
    }

    public static boolean shouldDoRestart() {

        return shouldDoRestart;
    }
}