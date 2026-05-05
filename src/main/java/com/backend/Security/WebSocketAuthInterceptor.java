package com.backend.Security;

import com.backend.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@NullMarked
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String query = request.getURI().getQuery();

        if (query == null || !query.contains("token=")) {
            return false;
        }

        String token = query.split("token=")[1].split("&")[0];

        return refreshTokenRepository.findByToken(token)
                .map(t -> {
                    boolean valid = t.isValid();
                    if (valid) {
                        attributes.put("userId", t.getUserDevice().getUser().getUserId());
                        attributes.put("deviceId", t.getUserDevice().getUserDeviceId());
                    }
                    return valid;
                })
                .orElse(false);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, @Nullable Exception exception) {
    }
}