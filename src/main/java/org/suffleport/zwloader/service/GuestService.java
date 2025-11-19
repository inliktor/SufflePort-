package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.Guest;
import org.suffleport.zwloader.repository.GuestRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    @Transactional
    public Guest create(String lastName,
                        String firstName,
                        String middleName,
                        String phone,
                        String company) {
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("lastName is required");
        Guest g = new Guest(lastName, firstName, middleName, phone, company);
        return guestRepository.save(g);
    }

    @Transactional(readOnly = true)
    public Guest getOrThrow(UUID id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Гость не найден: " + id));
    }

    @Transactional(readOnly = true)
    public List<Guest> listAll() { return guestRepository.findAll(); }

    @Transactional
    public Guest update(UUID id,
                        String lastName,
                        String firstName,
                        String middleName,
                        String phone,
                        String company,
                        String document,
                        String notes) {
        Guest g = getOrThrow(id);
        if (lastName != null) g.setLastName(lastName);
        if (firstName != null) g.setFirstName(firstName);
        if (middleName != null) g.setMiddleName(middleName);
        if (phone != null) g.setPhone(phone);
        if (company != null) g.setCompany(company);
        if (document != null) g.setDocument(document);
        if (notes != null) g.setNotes(notes);
        // пересоберём ФИО
        g.setFullName(buildFullName(g.getLastName(), g.getFirstName(), g.getMiddleName()));
        return guestRepository.save(g);
    }

    @Transactional
    public void delete(UUID id) { guestRepository.delete(getOrThrow(id)); }

    @Transactional(readOnly = true)
    public List<Guest> findByLastName(String lastName) { return guestRepository.findByLastName(lastName); }

    @Transactional(readOnly = true)
    public List<Guest> searchByDocument(String fragment) {
        if (fragment == null || fragment.isBlank()) return List.of();
        return guestRepository.findByDocumentContainingIgnoreCase(fragment);
    }

    private String buildFullName(String lastName, String firstName, String middleName) {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName).append(" ");
        if (firstName != null) sb.append(firstName).append(" ");
        if (middleName != null) sb.append(middleName);
        return sb.toString().trim();
    }
}

