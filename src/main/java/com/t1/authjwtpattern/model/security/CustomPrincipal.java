package com.t1.authjwtpattern.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomPrincipal {
    private Set<Role> roles;
    private UUID userId;
}
