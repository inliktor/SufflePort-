package org.suffleport.zwloader.repository;

import org.suffleport.zwloader.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByLastName(String lastName);
    List<Guest> findByDocumentContainingIgnoreCase(String fragment);
}
