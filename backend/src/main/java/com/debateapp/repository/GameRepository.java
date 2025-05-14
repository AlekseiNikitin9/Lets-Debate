package com.debateapp.repository;

import com.debateapp.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    Optional<Game> findByCode(String code);
    boolean existsByCode(String code);
}
