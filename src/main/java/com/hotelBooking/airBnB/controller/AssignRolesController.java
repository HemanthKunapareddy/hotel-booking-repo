package com.hotelBooking.airBnB.controller;

import com.hotelBooking.airBnB.dto.AssignRoleDTO;
import com.hotelBooking.airBnB.entity.User;
import com.hotelBooking.airBnB.enums.Role;
import com.hotelBooking.airBnB.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/admin/assignRoles")
@Slf4j
@RequiredArgsConstructor
public class AssignRolesController {

    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    public ResponseEntity<?> assignRolesToTheUser(@RequestBody AssignRoleDTO assignRoleDTO){
        User user = userRepository.findByEmail(assignRoleDTO.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("User with username: "+assignRoleDTO.getUsername()+" not found!"));
        Set<Role> roles = user.getRoles();
        roles.addAll(assignRoleDTO.getRoles());
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
