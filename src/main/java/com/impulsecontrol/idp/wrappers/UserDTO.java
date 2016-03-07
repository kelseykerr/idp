package com.impulsecontrol.idp.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.impulsecontrol.idp.core.User;

/**
 * Created by kerrk on 3/6/16.
 */
public class UserDTO {
    @JsonProperty
    public String userName;

    @JsonProperty
    public String firstName;

    @JsonProperty
    public String lastName;

    public UserDTO(User user) {
        this.userName = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public static UserDTO transform(User user) {
        return new UserDTO(user);
    }
}
