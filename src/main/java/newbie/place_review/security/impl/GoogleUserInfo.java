package newbie.place_review.security.impl;

import lombok.RequiredArgsConstructor;
import newbie.place_review.security.OAuth2UserInfo;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }
}
