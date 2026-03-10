package com.hotelBooking.airBnB.security.service;

import com.hotelBooking.airBnB.dto.LoginDTO;
import com.hotelBooking.airBnB.dto.LoginResponseDTO;
import com.hotelBooking.airBnB.dto.SignUpDTO;
import com.hotelBooking.airBnB.entity.User;
import com.hotelBooking.airBnB.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MyUserService userService;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public SignUpDTO signUp(SignUpDTO userDTO) {
        User user = userService.getUserByEmail(userDTO.getEmail());
        if (user != null) {
            throw new RuntimeException("User already exists!!");
        }
        user = modelMapper.map(userDTO, User.class);
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(Role.GUEST));
        return modelMapper.map(userService.saveUser(user), SignUpDTO.class);
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);
        User user = userService.getUserById(id);
        return jwtService.createAccessToken(user);
    }
}
