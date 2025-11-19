package org.suffleport.zwloader.repository;

import org.suffleport.zwloader.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {
    // Все события одного сотрудника
    List<Event> findByPerson_Id(UUID personId);

    // События по устройству за дату/период
    List<Event> findByDevice_DeviceIdAndCreatedAtBetween(String deviceId, OffsetDateTime start, OffsetDateTime end);

    // Все события по карте
    List<Event> findByCard_Uid(String cardUid);

    // Поиск событий по source/direction
    List<Event> findBySource(String source); // если Source — enum, то соответствующий тип

    // События за последние n дней
    List<Event> findByCreatedAtAfter(OffsetDateTime date);

    // События между датами
    List<Event> findByCreatedAtBetween(OffsetDateTime start, OffsetDateTime end);

    // от и до
    List<Event> findByCreatedAtBetween(OffsetDateTime start, OffsetDateTime end);



}
