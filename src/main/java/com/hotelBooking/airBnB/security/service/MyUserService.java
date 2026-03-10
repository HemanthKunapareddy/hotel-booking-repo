package com.hotelBooking.airBnB.security.service;

import com.hotelBooking.airBnB.entity.User;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import com.hotelBooking.airBnB.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class
MyUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)
                .orElseThrow(()-> new ResourceNotFoundException("User with username: "+username+ " not found"));
    }

    public User getUserById(Long id){
        return userRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User with Id: "+id+" nto found"));
    }

    public User getUserByEmail(String email){
        return userRepository
                .findByEmail(email)
                .orElse(null);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }
}
