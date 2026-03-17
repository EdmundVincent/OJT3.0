package com.collaboportal.api.model;

import java.io.Serializable;

import lombok.NoArgsConstructor;
import lombok.Getter;

@NoArgsConstructor
@Getter
public class BaseResponseBody implements Serializable {

    private static final String DEFAULT_OK = "200";
    private static final String DEFAULT_SUCCESS_LEVEL = "S";

    private String nb_err_cod = DEFAULT_OK;
    private String err_msg = null;
    private String err_level = DEFAULT_SUCCESS_LEVEL;

    public BaseResponseBody(String nb_err_cod, String err_msg, String err_level) {
        this.nb_err_cod = nb_err_cod;
        this.err_msg = err_msg;
        this.err_level = err_level;
    }
}
