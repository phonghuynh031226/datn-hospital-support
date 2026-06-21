package com.poly.hospital_support.entity;

<<<<<<< HEAD
public class Role {
=======
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;
>>>>>>> 8e0f29c6c218516f95ecee0d5a2d816de8a4ec9f
}
