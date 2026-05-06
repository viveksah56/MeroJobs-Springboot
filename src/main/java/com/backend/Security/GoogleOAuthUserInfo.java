package com.backend.Security;

import java.util.Map;


public record GoogleOAuthUserInfo(Map<String, Object> attributes) {

    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getFirstName() {
        return (String) attributes.get("given_name");
    }

    public String getLastName() {
        return (String) attributes.get("family_name");
    }

    public String getFullName() {
        return (String) attributes.get("name");
    }

    public String getPictureUrl() {
        return (String) attributes.get("picture");
    }

    public boolean isEmailVerified() {
        Object verified = attributes.get("email_verified");
        return Boolean.TRUE.equals(verified);
    }
}