package com.example.messenger.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;


/**
 * The Role entity represents a user role in the application.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long role_id;

    @Column(name = "name")
    private String name;

//    @Transient
//    @ManyToMany(mappedBy = "roles")
//    private Set<User> users;

    public Role(){

    }

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}