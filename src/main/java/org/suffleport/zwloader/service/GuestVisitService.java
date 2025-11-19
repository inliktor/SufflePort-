package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.Guest;
import org.suffleport.zwloader.domain.GuestVisit;
import org.suffleport.zwloader.domain.Personnel;
import org.suffleport.zwloader.repository.GuestRepository;
import org.suffleport.zwloader.repository.GuestVisitRepository;
import org.suffleport.zwloader.repository.PersonnelRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestVisitService {

    private final GuestVisitRepository guestVisitRepository;
    private final GuestRepository guestRepository;
    private final PersonnelRepository personnelRepository;

    @Transactional
    public GuestVisit create(UUID guestId,
                             UUID hostPersonId,
                             OffsetDateTime plannedFrom,
                             OffsetDateTime plannedTo,
                             String reason) {
        if (guestId == null) throw new IllegalArgumentException("guestId is required");
        if (hostPersonId == null) throw new IllegalArgumentException("hostPersonId is required");
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new NoSuchElementException("Гость не найден: " + guestId));
        Personnel host = personnelRepository.findById(hostPersonId)
                .orElseThrow(() -> new NoSuchElementException("Сотрудник-хост не найден: " + hostPersonId));
        GuestVisit gv = new GuestVisit(guest, host, plannedFrom, plannedTo, reason);
        return guestVisitRepository.save(gv);
    }

    @Transactional(readOnly = true)
    public GuestVisit getOrThrow(Long id) {
        return guestVisitRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Визит не найден: " + id));
    }

    @Transactional
    public GuestVisit updatePlan(Long id,
                                 OffsetDateTime plannedFrom,
                                 OffsetDateTime plannedTo,
                                 String reason) {
        GuestVisit gv = getOrThrow(id);
        if (plannedFrom != null) gv.setPlannedFrom(plannedFrom);
        if (plannedTo != null) gv.setPlannedTo(plannedTo);
        if (reason != null) gv.setReason(reason);
        return guestVisitRepository.save(gv);
    }

    @Transactional
    public GuestVisit setStatus(Long id, String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("status is required");
        GuestVisit gv = getOrThrow(id);
        gv.setStatus(status);
        return guestVisitRepository.save(gv);
    }

    @Transactional(readOnly = true)
    public List<GuestVisit> listByGuest(UUID guestId) { return guestVisitRepository.findByGuest_Id(guestId); }

    @Transactional(readOnly = true)
    public List<GuestVisit> listByHost(UUID hostId) { return guestVisitRepository.findByHost_Id(hostId); }

    @Transactional(readOnly = true)
    public List<GuestVisit> listPlannedBetween(OffsetDateTime start, OffsetDateTime end) {
        return guestVisitRepository.findByPlannedFromBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<GuestVisit> listAll() { return guestVisitRepository.findAll(); }

    @Transactional
    public void delete(Long id) { guestVisitRepository.delete(getOrThrow(id)); }
}

