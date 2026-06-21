package com.poly.hospital_support.repository;

<<<<<<< HEAD
public class RoleRepository {
=======
import com.poly.hospital_support.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
>>>>>>> 8e0f29c6c218516f95ecee0d5a2d816de8a4ec9f
}
