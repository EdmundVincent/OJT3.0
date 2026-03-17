package com.collaboportal.api.oauth2.model;

import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthTokenResponseBody implements Serializable {
    private String access_token;
    private String refresh_token;
    private String id_token;
    private String token_type;
}
