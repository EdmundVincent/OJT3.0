package com.collaboportal.api.oauth2.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OauthTokenResult implements Serializable {
    private String accessToken;
    private boolean isSuccess;
    private String name;
    private String sub;
    private String email;
    private String given_name;
    private String family_name;
}
