package org.suffleport.zwloader.repository;

import org.suffleport.zwloader.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    // Поиск позиции по названию
    Position findByName(String name);

    // Найти все позиции, где название начинается с фрагмента
    List<Position> findByNameStartingWithIgnoreCase(String prefix);
}
