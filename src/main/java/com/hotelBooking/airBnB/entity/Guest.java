package com.hotelBooking.airBnB.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hotelBooking.airBnB.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"userId"})
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User userId;

    @Column(name = "guest_name")
    private String name;

    @Column(name = "guest_gender")
    private Gender gender;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
