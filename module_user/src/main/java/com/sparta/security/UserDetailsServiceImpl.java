package com.sparta.security;

import com.sparta.entity.UserEntity;
import com.sparta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String user_id) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUserId(user_id);
        String EncodedPassword = userEntity.getPassword();
        if (userEntity == null)
            throw new UsernameNotFoundException( "Not Found id : " + user_id);

        return new UserDetailsImpl(userEntity);
    }

}
