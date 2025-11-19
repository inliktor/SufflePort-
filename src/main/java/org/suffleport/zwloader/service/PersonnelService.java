package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.Personnel;
import org.suffleport.zwloader.domain.Position;
import org.suffleport.zwloader.repository.PersonnelRepository;
import org.suffleport.zwloader.repository.PositionRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final PositionRepository positionRepository;

    @Transactional
    public Personnel create(String lastName,
                            String firstName,
                            String middleName,
                            LocalDate dateOfBirth,
                            UUID positionId,
                            String phone) {
        Personnel p = new Personnel(lastName, firstName, middleName, dateOfBirth, null, phone);
        if (positionId != null) {
            Position pos = positionRepository.findById(positionId)
                    .orElseThrow(() -> new NoSuchElementException("Должность не найдена: " + positionId));
            p.setPosition(pos);
        }
        return personnelRepository.save(p);
    }

    @Transactional(readOnly = true)
    public Personnel getOrThrow(UUID id) {
        return personnelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Сотрудник не найден: " + id));
    }

    @Transactional
    public Personnel update(UUID id,
                            String lastName,
                            String firstName,
                            String middleName,
                            LocalDate dateOfBirth,
                            UUID positionId,
                            String phone) {
        Personnel p = getOrThrow(id);
        if (lastName != null) p.setLastName(lastName);
        if (firstName != null) p.setFirstName(firstName);
        if (middleName != null) p.setMiddleName(middleName);
        if (dateOfBirth != null) p.setDateOfBirth(dateOfBirth);
        if (phone != null) p.setPhone(phone);
        if (positionId != null) {
            Position pos = positionRepository.findById(positionId)
                    .orElseThrow(() -> new NoSuchElementException("Должность не найдена: " + positionId));
            p.setPosition(pos);
        }
        return personnelRepository.save(p);
    }

    @Transactional
    public Personnel reassignPosition(UUID personId, UUID positionId) {
        Personnel p = getOrThrow(personId);
        Position pos = positionRepository.findById(positionId)
                .orElseThrow(() -> new NoSuchElementException("Должность не найдена: " + positionId));
        p.setPosition(pos);
        return personnelRepository.save(p);
    }

    @Transactional
    public void delete(UUID id) { personnelRepository.delete(getOrThrow(id)); }

    // Поисковые методы
    @Transactional(readOnly = true)
    public List<Personnel> findByLastName(String lastName) { return personnelRepository.findByLastName(lastName); }

    @Transactional(readOnly = true)
    public List<Personnel> findByFirstName(String firstName) { return personnelRepository.findByFirstName(firstName); }

    @Transactional(readOnly = true)
    public List<Personnel> findByFirstAndLast(String firstName, String lastName) {
        return personnelRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Transactional(readOnly = true)
    public List<Personnel> findByFullName(String fullName) { return personnelRepository.findByFullName(fullName); }

    @Transactional(readOnly = true)
    public List<Personnel> findByPositionName(String positionName) {
        return personnelRepository.findByPosition_Name(positionName);
    }

    @Transactional(readOnly = true)
    public List<Personnel> findByLastNameIgnoreCase(String lastName) {
        return personnelRepository.findByLastNameIgnoreCase(lastName);
    }

    @Transactional(readOnly = true)
    public List<Personnel> findWithPhone() { return personnelRepository.findByPhoneNotNull(); }

    @Transactional(readOnly = true)
    public List<Personnel> findByPositionOrdered(String positionName) {
        return personnelRepository.findByPosition_NameOrderByLastNameAsc(positionName);
    }

    @Transactional(readOnly = true)
    public List<Personnel> createdAfter(OffsetDateTime date) { return personnelRepository.findByCreatedAtAfter(date); }

    @Transactional(readOnly = true)
    public List<Personnel> createdBetween(OffsetDateTime start, OffsetDateTime end) {
        return personnelRepository.findByCreatedAtBetween(start, end);
    }

    @Transactional(readOnly = true)
    public Page<Personnel> pageByLastName(String lastName, Pageable pageable) {
        return personnelRepository.findByLastName(lastName, pageable);
    }

    @Transactional(readOnly = true)
    public List<Personnel> listAll() { return personnelRepository.findAll(); }
}
