package com.example.messenger.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;


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

    //    public Set<User> getUsers() {
    //        return users;
    //    }
    //
    //    public void setUsers(Set<User> users) {
    //        this.users = users;
    //    }

    @Override
    public String getAuthority() {
        return getName();
    }
}