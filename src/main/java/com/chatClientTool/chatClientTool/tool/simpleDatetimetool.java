package com.chatClientTool.chatClientTool.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

public class simpleDatetimetool {

    private static final Logger logger = LoggerFactory.getLogger(simpleDatetimetool.class);

    @Tool(description = "Get the current date and time")
    public String getCurrentDateTime() {
        logger.info("getCurrentDateTime tool called");
        String dateTime = LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
        logger.info("Returning current date and time: {}", dateTime);
        return dateTime;
    }

    @Tool(description = "Set the alarm at a given time")
    public String setAlarm(String time) {
        logger.info("setAlarm tool called with time: {}", time);
        
        try {
            // Parse the input time (support formats: HH:mm, HH:mm:ss)
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime alarmTime = LocalTime.parse(time, timeFormatter);
            logger.info("Parsed alarm time: {}", alarmTime);
            
            // Get current time
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime alarmDateTime = now.with(alarmTime);
            logger.info("Current time: {}", now);
            logger.info("Alarm set for: {}", alarmDateTime);
            
            // If the alarm time has already passed today, set it for tomorrow
            if (alarmDateTime.isBefore(now)) {
                alarmDateTime = alarmDateTime.plusDays(1);
                logger.info("Alarm time passed for today, setting for tomorrow: {}", alarmDateTime);
            }
            
            // Calculate delay in milliseconds
            long delayMillis = ChronoUnit.MILLIS.between(now, alarmDateTime);
            logger.info("Alarm will trigger in {} milliseconds ({} seconds)", delayMillis, delayMillis / 1000);
            
            // Schedule the alarm
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.warn("⏰ ALARM TRIGGERED at {}", LocalDateTime.now());
                    System.out.println("\n🔔 ALARM RINGING! Time: " + LocalDateTime.now() + "\n");
                }
            }, delayMillis);
            
            String response = "Alarm successfully set for " + alarmTime + " (in " + (delayMillis / 1000) + " seconds)";
            logger.info("Alarm set successfully: {}", response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error setting alarm with time: {} - Error: {}", time, e.getMessage(), e);
            return "Error: Invalid time format. Please use HH:mm format (e.g., 14:30)";
        }
    }
}
