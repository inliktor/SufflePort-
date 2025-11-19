package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.Position;
import org.suffleport.zwloader.repository.PositionRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    @Transactional
    public Position create(String name, Integer accessLevel) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        Position p = new Position(name, accessLevel);
        return positionRepository.save(p);
    }

    @Transactional(readOnly = true)
    public Position getOrThrow(UUID id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Должность не найдена: " + id));
    }

    @Transactional
    public Position update(UUID id, String name, Integer accessLevel) {
        Position p = getOrThrow(id);
        if (name != null) p.setName(name);
        if (accessLevel != null) p.setAccessLevel(accessLevel);
        return positionRepository.save(p);
    }

    @Transactional
    public void delete(UUID id) { positionRepository.delete(getOrThrow(id)); }

    @Transactional(readOnly = true)
    public Position findByName(String name) { return positionRepository.findByName(name); }

    @Transactional(readOnly = true)
    public List<Position> findByNamePrefix(String prefix) { return positionRepository.findByNameStartingWithIgnoreCase(prefix); }

    @Transactional(readOnly = true)
    public List<Position> listAll() { return positionRepository.findAll(); }
}

