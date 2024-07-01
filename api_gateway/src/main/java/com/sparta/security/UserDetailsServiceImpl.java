package com.sparta.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String AUTHENTICATION_SERVICE_URL = "http://localhost:8080/api/user/{userId}";
    private RestTemplate restTemplate;

    public UserDetailsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            // User 서비스의 사용자 정보 조회 API 호출
            UserDetails userDetails = restTemplate.getForObject(AUTHENTICATION_SERVICE_URL, UserDetails.class, userId);
            return userDetails;
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("User not found : " + userId);
        }
    }

}
