package ua.alexcatze.auto_restart.util;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.io.File;
import java.util.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import ua.alexcatze.auto_restart.AutoRestart;

public final class ConfigHandler {
    public static Configuration config;

    public static boolean AUTO_RESTART_ENABLED = true;
    public static boolean AUTO_RESTART_ON_CRASH = true;
    public static List<String> AUTO_RESTART_WARNING_TIMES = new ArrayList<>(Arrays.asList(
            Timing.build(5, TimeUnit.SECONDS).toString(),
            Timing.build(4, TimeUnit.SECONDS).toString(),
            Timing.build(3, TimeUnit.SECONDS).toString(),
            Timing.build(2, TimeUnit.SECONDS).toString(),
            Timing.build(1, TimeUnit.SECONDS).toString()));
    public static List<String> AUTO_RESTART_TIMES = new ArrayList<>(Arrays.asList(
            AutoRestartTask.build(14, 0).toString(),
            AutoRestartTask.build(16, 32).toString()));

    public static String RESTART_COMMAND_INGAME = "restart";
    public static String RESTART_AFTER_COMMAND_INGAME = "restartafter";

    public static final ArrayList<Timing> autoRestartWarningTimes = new ArrayList<>();

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);

        config.load();
        load();

        FMLCommonHandler.instance().bus().register(new ChangeListener());
    }

    public static void load() {
        String desc;

        desc = "Should the Server do automatic restarts?";
        AUTO_RESTART_ENABLED = loadPropBool("auto_restart", desc, AUTO_RESTART_ENABLED);
        desc = "Should the server be automatically restarted when it crashes?";
        AUTO_RESTART_ON_CRASH = loadPropBool("on_crash", desc, AUTO_RESTART_ON_CRASH);

        desc = "Times before an auto restart of the server, a restart warning should be shown.";
        AUTO_RESTART_WARNING_TIMES = loadPropListString("warning_times", desc, AUTO_RESTART_WARNING_TIMES);
        loadAutoRestartWarningTimes();

        desc = "Times in 24-hour format on which the server will automatically restart";
        AUTO_RESTART_TIMES = loadPropListString("times", desc, AUTO_RESTART_TIMES);
        loadAutoRestartTimes();

        desc = "Ingame command to restart server.";
        RESTART_COMMAND_INGAME = loadPropString("ingame_restart_command", desc, RESTART_COMMAND_INGAME);

        desc = "Ingame command to schedule additional server restart.";
        RESTART_AFTER_COMMAND_INGAME =
                loadPropString("ingame_restart_after_command", desc, RESTART_AFTER_COMMAND_INGAME);

        if (config.hasChanged()) config.save();
    }

    private static synchronized void loadAutoRestartTimes() {

        AutoRestart.AUTO_RESTART_TASKS.clear();
        List<String> autoRestartTimeStrings = AUTO_RESTART_TIMES;
        for (int i = 0; i < autoRestartTimeStrings.size(); i++) {
            Optional<AutoRestartTask> autoRestartTime = AutoRestartTask.parse(autoRestartTimeStrings.get(i));
            if (autoRestartTime.isPresent()) {
                AutoRestart.AUTO_RESTART_TASKS.add(autoRestartTime.get());
            } else {
                AutoRestart.logger.warn(
                        "{}: Removed invalid {} from auto restart times.",
                        AutoRestart.MODID,
                        autoRestartTimeStrings.get(i));
                autoRestartTimeStrings.remove(i);
                i--;
            }
        }
    }

    private static synchronized void loadAutoRestartWarningTimes() {

        autoRestartWarningTimes.clear();
        List<String> autoRestartWarningTimeStrings = AUTO_RESTART_WARNING_TIMES;
        for (int i = 0; i < autoRestartWarningTimeStrings.size(); i++) {
            Optional<Timing> autoRestartWarningTime = Timing.parse(autoRestartWarningTimeStrings.get(i));
            if (autoRestartWarningTime.isPresent()) {
                autoRestartWarningTimes.add(autoRestartWarningTime.get());
            } else {
                AutoRestart.logger.warn(String.format(
                        "%s: Removed invalid %s from auto restart warning times.",
                        AutoRestart.MODID, autoRestartWarningTime));
                autoRestartWarningTimeStrings.remove(i);
                i--;
            }
        }
    }

    public static void loadPostInit() {
        if (config.hasChanged()) config.save();
    }

    public static int loadPropInt(String propName, String desc, int default_) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_);
        prop.comment = desc;

        return prop.getInt(default_);
    }

    public static double loadPropDouble(String propName, String desc, double default_) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_);
        prop.comment = desc;

        return prop.getDouble(default_);
    }

    public static String loadPropString(String propName, String desc, String default_) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_);
        prop.comment = desc;

        return prop.getString();
    }

    public static boolean loadPropBool(String propName, String desc, boolean default_) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_);
        prop.comment = desc;

        return prop.getBoolean(default_);
    }

    public static List<String> loadPropListString(String propName, String desc, List<String> default_) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, propName, default_.toArray(new String[0]));
        prop.comment = desc;

        return Arrays.asList(prop.getStringList());
    }

    public static class ChangeListener {

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if (eventArgs.modID.equals(AutoRestart.MODID)) load();
        }
    }
}
