package com.impulsecontrol.idp.wrappers;

import javax.validation.constraints.NotNull;

/**
 * Created by kerrk on 3/31/16.
 */
public class SamlResponseDTO {

    @NotNull
    public String acsUrl;

    @NotNull
    public String samlResponse;
}
