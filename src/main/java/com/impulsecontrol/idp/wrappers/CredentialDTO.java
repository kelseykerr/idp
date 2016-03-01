package com.impulsecontrol.idp.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by kerrk on 2/26/16.
 */
public class CredentialDTO {

    @JsonProperty
    @NotNull
    public String email;

    @JsonProperty
    @NotNull
    @Pattern(regexp = "(?=.*\\W).{8,255}")
    public String password;
}
