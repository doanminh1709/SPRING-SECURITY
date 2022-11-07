package com.practice.overviewspringsecurity.repository;

import com.practice.overviewspringsecurity.Enum.EnumRole;
import com.practice.overviewspringsecurity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

  Optional<Role> findByName(EnumRole role);

}
