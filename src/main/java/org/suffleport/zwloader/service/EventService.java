package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.*;
import org.suffleport.zwloader.repository.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CardRepository cardRepository;
    private final PersonnelRepository personnelRepository;
    private final DeviceRepository deviceRepository;

    @Transactional
    public Event create(String cardUid,
                        java.util.UUID personId,
                        String deviceId,
                        String faceName,
                        Direction direction,
                        Source source,
                        EventMeta meta) {
        if (direction == null) throw new IllegalArgumentException("direction is required");
        if (source == null) throw new IllegalArgumentException("source is required");

        Card card = null;
        if (cardUid != null && !cardUid.isBlank()) {
            card = cardRepository.findById(cardUid)
                    .orElseThrow(() -> new NoSuchElementException("Карта не найдена: " + cardUid));
        }
        Personnel person = null;
        if (personId != null) {
            person = personnelRepository.findById(personId)
                    .orElseThrow(() -> new NoSuchElementException("Сотрудник не найден: " + personId));
        }
        Device device = null;
        if (deviceId != null && !deviceId.isBlank()) {
            device = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> new NoSuchElementException("Устройство не найдено: " + deviceId));
        }

        Event e = new Event(card, person, device, faceName, direction, source, meta);
        return eventRepository.save(e);
    }

    @Transactional(readOnly = true)
    public Event getOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено: " + id));
    }

    @Transactional(readOnly = true)
    public List<Event> listAll() { return eventRepository.findAll(); }

    @Transactional(readOnly = true)
    public List<Event> listByPerson(java.util.UUID personId) { return eventRepository.findByPerson_Id(personId); }

    @Transactional(readOnly = true)
    public List<Event> listByCard(String cardUid) { return eventRepository.findByCard_Uid(cardUid); }

    @Transactional(readOnly = true)
    public List<Event> listByDeviceAndPeriod(String deviceId, OffsetDateTime start, OffsetDateTime end) {
        return eventRepository.findByDevice_IdAndCreatedAtBetween(deviceId, start, end);
    }

    @Transactional(readOnly = true)
    public List<Event> findBySource(Source source) { return eventRepository.findBySource(source); }

    @Transactional(readOnly = true)
    public List<Event> listCreatedAfter(OffsetDateTime date) { return eventRepository.findByCreatedAtAfter(date); }

    @Transactional(readOnly = true)
    public List<Event> listBetween(OffsetDateTime start, OffsetDateTime end) { return eventRepository.findByCreatedAtBetween(start, end); }

    @Transactional
    public void delete(Long id) { eventRepository.delete(getOrThrow(id)); }

    @Transactional(readOnly = true)
    public Event lastByCard(String cardUid) { return eventRepository.findTop1ByCard_UidOrderByCreatedAtDesc(cardUid); }

    @Transactional(readOnly = true)
    public Event lastByFace(String faceName) { return eventRepository.findTop1ByFaceNameOrderByCreatedAtDesc(faceName); }

    @Transactional(readOnly = true)
    public Event lastByDevice(String deviceId) { return eventRepository.findTop1ByDevice_IdOrderByCreatedAtDesc(deviceId); }

    private Direction next(Direction prev) { return prev == Direction.IN ? Direction.OUT : Direction.IN; }

    @Transactional
    public Event createNfcToggleEvent(String cardUid, String deviceId) {
        if (cardUid == null || cardUid.isBlank()) throw new IllegalArgumentException("cardUid required");
        Card card = cardRepository.findById(cardUid).orElse(null); // может быть null (неизвестная карта)
        Device device = null;
        if (deviceId != null && !deviceId.isBlank()) {
            device = deviceRepository.findById(deviceId).orElse(null);
        }
        Direction dir = Direction.IN; // по умолчанию первый проход = IN
        Event last = lastByCard(cardUid);
        if (last != null) {
            dir = next(last.getDirection());
        }
        Personnel person = card != null ? card.getPerson() : null;
        Event e = new Event(card, person, device, null, dir, Source.NFC, null);
        return eventRepository.save(e);
    }

    @Transactional
    public Event createFaceToggleEvent(String faceName, String deviceId) {
        if (faceName == null || faceName.isBlank()) throw new IllegalArgumentException("faceName required");
        Device device = null;
        if (deviceId != null && !deviceId.isBlank()) {
            device = deviceRepository.findById(deviceId).orElse(null);
        }
        Direction dir = Direction.IN;
        Event last = lastByFace(faceName);
        if (last != null) dir = next(last.getDirection());
        // попытка найти сотрудника по полному имени
        Personnel person = null;
        List<Personnel> matches = personnelRepository.findByFullName(faceName);
        if (matches.size() == 1) person = matches.get(0);
        Event e = new Event(null, person, device, faceName, dir, Source.FACE, null);
        return eventRepository.save(e);
    }

    @Transactional
    public Event createSecurityAlert(String uidOrFace, String deviceId, Source source, String reason) {
        Device device = null;
        if (deviceId != null && !deviceId.isBlank()) device = deviceRepository.findById(deviceId).orElse(null);
        EventMeta meta = new EventMeta();
        meta.setDecision(reason != null ? reason : "DENY");
        Event e = new Event(null, null, device, source == Source.FACE ? uidOrFace : null, Direction.OUT, source, meta);
        return eventRepository.save(e);
    }
}
