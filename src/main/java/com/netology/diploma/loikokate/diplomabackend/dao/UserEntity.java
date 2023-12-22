package com.netology.diploma.loikokate.diplomabackend.dao;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String password;

    @Column(name = "auth_token")
    private String authToken;
}
