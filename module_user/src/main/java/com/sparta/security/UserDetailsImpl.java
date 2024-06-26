package com.sparta.security;

import com.sparta.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final UserEntity userEntity;

    public UserDetailsImpl(UserEntity user) {
        this.userEntity = user;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUserId(); // 1단계
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = userEntity.getRole().split("_")[1];

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }
}
