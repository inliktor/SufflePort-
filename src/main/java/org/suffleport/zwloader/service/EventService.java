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
        if (direction == null) throw new IllegalArgumentException("Пум пум ин аут капут);
        if (source == null) throw new IllegalArgumentException("фейс или карта капут");

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
}

