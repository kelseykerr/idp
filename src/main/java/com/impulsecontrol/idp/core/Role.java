package com.impulsecontrol.idp.core;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kerrk on 2/19/16.
 */
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "role", cascade = { CascadeType.ALL })
    private List<UserToRole> userRoles = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
