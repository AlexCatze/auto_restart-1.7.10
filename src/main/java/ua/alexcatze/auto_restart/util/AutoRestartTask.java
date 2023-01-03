package ua.alexcatze.auto_restart.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import ua.alexcatze.auto_restart.AutoRestart;

public class AutoRestartTask {

    private final int hour;

    private final int minute;

    private String reason = AutoRestart.defaultRestartReason;

    private AutoRestartTask(int _hour, int _minute) {
        hour = _hour;
        minute = _minute;
    }

    // package-private
    public static AutoRestartTask build(int hour, int minute) {
        return new AutoRestartTask(hour, minute);
    }

    // package-private
    public static Optional<AutoRestartTask> parse(String time) {
        String[] timeElements = time.split(":");
        if (timeElements.length != 2) {
            return Optional.empty();
        }
        try {
            int hour = Integer.parseInt(timeElements[0]);
            int min = Integer.parseInt(timeElements[1]);

            if (hour < 0 || hour > 23 || min < 0 || min > 59) {
                return Optional.empty();
            }
            return Optional.of(new AutoRestartTask(hour, min));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public Duration getDifferenceTo(LocalDateTime time) {
        LocalDate autoRestartDate = time.toLocalDate();
        LocalTime autoRestartTime = LocalTime.of(getHour(), getMinute());
        if (time.toLocalTime().isAfter(autoRestartTime)) {
            autoRestartDate = autoRestartDate.plusDays(1);
        }
        return Duration.between(time, LocalDateTime.of(autoRestartDate, autoRestartTime));
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String _reason) {
        reason = _reason;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }
}
