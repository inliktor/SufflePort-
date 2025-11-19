package org.suffleport.zwloader.repository;

import org.suffleport.zwloader.domain.GuestVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface GuestVisitRepository extends JpaRepository<GuestVisit, Long> {
    // Все визиты конкретного гостя
    List<GuestVisit> findByGuest_Id(Long guestId);

    // Все визиты сотрудника как принимающей стороны
    List<GuestVisit> findByHost_Id(java.util.UUID hostId);

    // Визиты за период
    List<GuestVisit> findByVisitDateBetween(OffsetDateTime start, OffsetDateTime end);
}
