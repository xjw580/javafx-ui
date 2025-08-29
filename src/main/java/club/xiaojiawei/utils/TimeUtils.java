package club.xiaojiawei.utils;

/**
 * @author 肖嘉威
 * @date 2025/8/29 10:10
 */

import java.time.Duration;

public record TimeUtils(int maxHour, int maxMin, int maxSec, int minHour, int minMin, int minSec) {

    public TimeUtils() {
        this(23, 59, 59, 0, 0, 0);
    }

    public TimeUtils(int maxHour, int maxMin, int maxSec, int minHour, int minMin, int minSec) {
        this.maxHour = maxHour;
        this.maxMin = maxMin;
        this.maxSec = maxSec;
        this.minHour = minHour;
        this.minMin = minMin;
        this.minSec = minSec;
        if (!isValidTime(maxHour, maxMin, maxSec)) throw new IllegalArgumentException();
    }


    /**
     * 格式化 Duration 为小时字符串 (HH)
     * @param duration Duration 对象
     * @return 格式化的小时字符串
     */
    public String formatHour(Duration duration) {
        return String.format("%02d", getHoursFromDuration(duration));
    }

    /**
     * 格式化 Duration 为分钟字符串 (mm)
     * @param duration Duration 对象
     * @return 格式化的分钟字符串
     */
    public String formatMinute(Duration duration) {
        return String.format("%02d", getMinutesFromDuration(duration));
    }

    /**
     * 格式化 Duration 为秒字符串 (ss)
     * @param duration Duration 对象
     * @return 格式化的秒字符串
     */
    public String formatSecond(Duration duration) {
        return String.format("%02d", getSecondsFromDuration(duration));
    }

    /**
     * 将 HH:mm:ss 格式字符串转换为 Duration
     * @param duration HH:mm:ss 格式的时间字符串
     * @return Duration 对象，如果格式错误或超出范围返回 null
     */
    public Duration secStringToDuration(String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = duration.trim().split(":");
            if (parts.length != 3) {
                return null;
            }

            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            // 验证范围
            if (!isValidTime(hours, minutes, seconds)) {
                return null;
            }

            return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 将 HH:mm 格式字符串转换为 Duration
     * @param duration HH:mm 格式的时间字符串
     * @return Duration 对象，如果格式错误或超出范围返回 null
     */
    public Duration stringToDuration(String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = duration.trim().split(":");
            if (parts.length != 2) {
                return null;
            }

            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            // 验证范围（秒数默认为0）
            if (!isValidTime(hours, minutes, 0)) {
                return null;
            }

            return Duration.ofHours(hours).plusMinutes(minutes);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 格式化 Duration 为 HH:mm
     * @param duration Duration 对象
     * @return HH:mm 格式的字符串
     */
    public String durationToString(Duration duration) {
        if (duration == null || duration.isNegative()) {
            return "00:00";
        }

        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        // 确保不超过最大值
        if (hours > maxHour) {
            hours = maxHour;
            minutes = maxMin;
        } else if (hours == maxHour && minutes > maxMin) {
            minutes = maxMin;
        }

        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * 格式化 Duration 为 HH:mm:ss
     * @param duration Duration 对象
     * @return HH:mm:ss 格式的字符串
     */
    public String durationToSecString(Duration duration) {
        if (duration == null || duration.isNegative()) {
            return "00:00:00";
        }

        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        // 确保不超过最大值
        if (hours > maxHour) {
            hours = maxHour;
            minutes = maxMin;
            seconds = maxSec;
        } else if (hours == maxHour) {
            if (minutes > maxMin) {
                minutes = maxMin;
                seconds = maxSec;
            } else if (minutes == maxMin && seconds > maxSec) {
                seconds = maxSec;
            }
        }

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * 从 Duration 中获取小时数
     * @param duration Duration 对象
     * @return 小时数，限制在 minHour 到 maxHour 范围内
     */
    public int getHoursFromDuration(Duration duration) {
        if (duration == null || duration.isNegative()) {
            return minHour;
        }

        long hours = duration.toHours();

        // 限制在范围内
        if (hours < minHour) {
            return minHour;
        } else if (hours > maxHour) {
            return maxHour;
        }

        return (int) hours;
    }

    /**
     * 从 Duration 中获取分钟数（不包含小时部分）
     * @param duration Duration 对象
     * @return 分钟数，限制在 minMin 到 maxMin 范围内
     */
    public int getMinutesFromDuration(Duration duration) {
        if (duration == null || duration.isNegative()) {
            return minMin;
        }

        long totalMinutes = duration.toMinutes();
        long minutes = totalMinutes % 60;

        // 如果总时长超过最大值，返回最大分钟数
        if (duration.toHours() > maxHour) {
            return maxMin;
        }

        // 限制在范围内
        if (minutes < minMin) {
            return minMin;
        } else if (minutes > maxMin) {
            return maxMin;
        }

        return (int) minutes;
    }

    /**
     * 从 Duration 中获取秒数（不包含小时和分钟部分）
     * @param duration Duration 对象
     * @return 秒数，限制在 minSec 到 maxSec 范围内
     */
    public int getSecondsFromDuration(Duration duration) {
        if (duration == null || duration.isNegative()) {
            return minSec;
        }

        long totalSeconds = duration.getSeconds();
        long seconds = totalSeconds % 60;

        // 如果总时长超过最大值，返回最大秒数
        if (duration.toHours() > maxHour ||
            (duration.toHours() == maxHour && (duration.toMinutes() % 60) > maxMin)) {
            return maxSec;
        }

        // 限制在范围内
        if (seconds < minSec) {
            return minSec;
        } else if (seconds > maxSec) {
            return maxSec;
        }

        return (int) seconds;
    }

    /**
     * 验证时间是否在有效范围内
     * @param hours 小时
     * @param minutes 分钟
     * @param seconds 秒
     * @return 是否有效
     */
    private boolean isValidTime(int hours, int minutes, int seconds) {
        // 检查基本范围
        if (hours < minHour || hours > maxHour) {
            return false;
        }
        if (minutes < minMin || minutes > maxMin) {
            return false;
        }
        if (seconds < minSec || seconds > maxSec) {
            return false;
        }

        // 检查数字位数（最大两位数）
        if (hours > 99 || minutes > 99 || seconds > 99) {
            return false;
        }

        return true;
    }

    // 测试方法
    public static void main(String[] args) {
        TimeUtils utils = new TimeUtils(99, 59, 59, 0, 0, 0);

        // 测试 secStringToDuration
        System.out.println("测试 secStringToDuration:");
        Duration d1 = utils.secStringToDuration("66:01:45");
        System.out.println("66:30:45 -> " + d1);

        Duration d2 = utils.secStringToDuration("25:00:00"); // 超出范围
        System.out.println("25:00:00 -> " + d2);

        // 测试 stringToDuration
        System.out.println("\n测试 stringToDuration:");
        Duration d3 = utils.stringToDuration("12:30");
        System.out.println("12:30 -> " + d3);

        // 测试 durationToString
        System.out.println("\n测试 durationToString:");
        System.out.println(d1 + " -> " + utils.durationToString(d1));

        // 测试 durationToSecString
        System.out.println("\n测试 durationToSecString:");
        System.out.println(d1 + " -> " + utils.durationToSecString(d1));

        // 测试新增的获取时分秒方法
        System.out.println("\n测试获取时分秒方法:");
        if (d1 != null) {
            System.out.println("Duration: " + d1);
            System.out.println("小时: " + utils.getHoursFromDuration(d1));
            System.out.println("分钟: " + utils.getMinutesFromDuration(d1));
            System.out.println("秒数: " + utils.getSecondsFromDuration(d1));
        }

        // 测试新增的格式化器方法
        System.out.println("\n测试格式化器方法:");
        if (d1 != null) {
            System.out.println("Duration: " + d1);
            System.out.println("格式化小时: " + utils.formatHour(d1));
            System.out.println("格式化分钟: " + utils.formatMinute(d1));
            System.out.println("格式化秒数: " + utils.formatSecond(d1));
        }


        // 测试超出范围的情况
        System.out.println("\n测试超出范围:");
        Duration longDuration = Duration.ofHours(25).plusMinutes(70).plusSeconds(80);
        System.out.println("超长Duration: " + longDuration);
        System.out.println("限制后小时: " + utils.getHoursFromDuration(longDuration));
        System.out.println("限制后分钟: " + utils.getMinutesFromDuration(longDuration));
        System.out.println("限制后秒数: " + utils.getSecondsFromDuration(longDuration));
    }
}