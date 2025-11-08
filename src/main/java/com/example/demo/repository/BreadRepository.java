package com.example.demo.repository;

import com.example.demo.entity.Bread;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface BreadRepository extends JpaRepository<Bread, Long> {
}
