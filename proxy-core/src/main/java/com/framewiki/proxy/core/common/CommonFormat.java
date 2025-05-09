package com.framewiki.proxy.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.common
 * @ClassName: CommonConstants
 * @Description: 公用的格式化类
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonFormat {

    /**
     * 获取socket匹配对key
     *
     * @param listenPort
     * @return
     */
    public static String generateSocketPartKey(Integer listenPort) {
        DecimalFormat fiveLenFormat = new DecimalFormat("00000");
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String randomNum = RandomStringUtils.randomNumeric(4);
        return String.join("-", "SK", fiveLenFormat.format(listenPort), dateTime, randomNum);
    }

    /**
     * 根据socketPartKey获取端口号
     *
     * @param socketPartKey 隧道标识
     * @return
     */
    public static Integer getSocketPortByPartKey(String socketPartKey) {
        String[] split = socketPartKey.split("-");
        return Integer.valueOf(split[1]);
    }

    /**
     * 获取交互流水号
     *
     * @return
     */
    public static String generateInteractiveSeq() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String randomNum = RandomStringUtils.randomNumeric(4);
        return String.join("-", "IS", dateTime, randomNum);
    }

}
