package com.tmp.tmp1cservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@NoArgsConstructor
@Data
@Table("clients")
public class Client {

    @Id
    private UUID id;

    @Column("login")
    private String login;

    @Column("password")
    private String password;

    @Column("url1c")
    private String url1c;

    public Client(String login, String password, String url1c) {
        this.login = login;
        this.password = password;
        this.url1c = url1c;
    }
}
