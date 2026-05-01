package com.backend.Util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class DeviceInfoExtractor {

    public static final String DEVICE_ID_HEADER = "X-Device-ID";

    private final UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(1000)
            .build();

    public String extractDeviceId(HttpServletRequest request) {
        String fromHeader = request.getHeader(DEVICE_ID_HEADER);
        if (isValid(fromHeader)) {
            log.debug("Device ID resolved from header: {}", fromHeader);
            return fromHeader.trim();
        }

        String fromParam = request.getParameter("deviceId");
        if (isValid(fromParam)) {
            log.debug("Device ID resolved from query param: {}", fromParam);
            return fromParam.trim();
        }

        String fromFingerprint = request.getHeader("X-Fingerprint");
        if (isValid(fromFingerprint)) {
            log.debug("Device ID resolved from X-Fingerprint: {}", fromFingerprint);
            return fromFingerprint.trim();
        }

        String ip = extractIp(request);
        String userAgent = extractUserAgentString(request);
        String generated = UUID.nameUUIDFromBytes(
                (ip + userAgent).getBytes(StandardCharsets.UTF_8)
        ).toString();

        log.debug("Device ID generated from IP + User-Agent fallback: {}", generated);
        return generated;
    }

    public String extractIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_CLIENT_IP"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (isValid(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    public String extractUserAgentString(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "unknown";
    }

    public String extractDeviceType(HttpServletRequest request) {
        UserAgent agent = parse(request);
        String deviceClass = agent.getValue(UserAgent.DEVICE_CLASS);

        return switch (deviceClass.toLowerCase()) {
            case "phone" -> "Mobile";
            case "tablet" -> "Tablet";
            case "desktop" -> "Desktop";
            case "tv" -> "TV";
            case "game console" -> "Game Console";
            case "set-top box" -> "Set-Top Box";
            case "robot" -> "Robot";
            default -> "Unknown";
        };
    }

    public String extractOs(HttpServletRequest request) {
        UserAgent agent = parse(request);
        String osName = agent.getValue(UserAgent.OPERATING_SYSTEM_NAME);
        String osVersion = agent.getValue(UserAgent.OPERATING_SYSTEM_VERSION);

        if (!isValid(osName)) return "Unknown OS";

        return isValid(osVersion) ? osName + " " + osVersion : osName;
    }

    public String extractBrowser(HttpServletRequest request) {
        UserAgent agent = parse(request);
        String agentName = agent.getValue(UserAgent.AGENT_NAME);
        String agentVersion = agent.getValue(UserAgent.AGENT_VERSION_MAJOR);

        if (!isValid(agentName)) return "Unknown Browser";

        return isValid(agentVersion) ? agentName + " " + agentVersion : agentName;
    }

    private UserAgent parse(HttpServletRequest request) {
        return userAgentAnalyzer.parse(extractUserAgentString(request));
    }

    private boolean isValid(String value) {
        return value != null
                && !value.isBlank()
                && !"unknown".equalsIgnoreCase(value)
                && !"??".equals(value);
    }
}