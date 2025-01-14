package com.example.springboot.repositories;

import com.example.springboot.models.OPTModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OPTRepository extends JpaRepository<OPTModel, Long> {
}
