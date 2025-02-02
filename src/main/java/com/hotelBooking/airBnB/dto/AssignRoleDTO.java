package com.hotelBooking.airBnB.dto;

import com.hotelBooking.airBnB.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignRoleDTO {

    private String username;

    private Set<Role> roles;
}
