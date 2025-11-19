package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.Card;
import org.suffleport.zwloader.domain.Personnel;
import org.suffleport.zwloader.repository.CardRepository;
import org.suffleport.zwloader.repository.PersonnelRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final PersonnelRepository personnelRepository;

    // Создать новую карту и привязать к сотруднику
    @Transactional
    public Card createCard(String uid, UUID personId) {
        validateUid(uid);
        Objects.requireNonNull(personId, "personId is required");

        if (cardRepository.findByUid(uid) != null) {
            throw new IllegalStateException("Карта с таким UID уже существует: " + uid);
        }

        Personnel person = personnelRepository.findById(personId)
                .orElseThrow(() -> new NoSuchElementException("Сотрудник не найден: " + personId));

        Card card = new Card();
        card.setUid(uid);
        card.setPerson(person);
        card.setActive(true);
        return cardRepository.save(card);
    }

    // Получить карту по UID (или бросить, если нет)
    @Transactional(readOnly = true)
    public Card getByUidOrThrow(String uid) {
        validateUid(uid);
        Card card = cardRepository.findByUid(uid);
        if (card == null) {
            throw new NoSuchElementException("Карта не найдена: " + uid);
        }
        return card;
    }

    // Мягкая активация карты
    @Transactional
    public Card activate(String uid) {
        Card card = getByUidOrThrow(uid);
        if (!card.isActive()) {
            card.setActive(true);
            card = cardRepository.save(card);
        }
        return card;
    }

    // Мягкая деактивация карты
    @Transactional
    public Card deactivate(String uid) {
        Card card = getByUidOrThrow(uid);
        if (card.isActive()) {
            card.setActive(false);
            card = cardRepository.save(card);
        }
        return card;
    }

    // Перепривязать карту к другому сотруднику
    @Transactional
    public Card reassignOwner(String uid, UUID newPersonId) {
        validateUid(uid);
        Objects.requireNonNull(newPersonId, "newPersonId is required");

        Card card = getByUidOrThrow(uid);
        Personnel newPerson = personnelRepository.findById(newPersonId)
                .orElseThrow(() -> new NoSuchElementException("Сотрудник не найден: " + newPersonId));
        card.setPerson(newPerson);
        return cardRepository.save(card);
    }

    // Удалить карту по UID
    @Transactional
    public void deleteByUid(String uid) {
        Card card = getByUidOrThrow(uid);
        cardRepository.delete(card);
    }

    // Массовое удаление по сотруднику
    @Transactional
    public void deleteAllByPerson(UUID personId) {
        Objects.requireNonNull(personId, "personId is required");
        cardRepository.deleteByPerson_Id(personId);
    }

    // Найти карты сотрудника
    @Transactional(readOnly = true)
    public List<Card> findByPerson(UUID personId) {
        Objects.requireNonNull(personId, "personId is required");
        return cardRepository.findByPerson_Id(personId);
    }

    // Найти активные карты сотрудника
    @Transactional(readOnly = true)
    public List<Card> findActiveByPerson(UUID personId) {
        Objects.requireNonNull(personId, "personId is required");
        return cardRepository.findByPerson_IdAndActiveTrue(personId);
    }

    // Найти неактивные карты сотрудника
    @Transactional(readOnly = true)
    public List<Card> findInactiveByPerson(UUID personId) {
        Objects.requireNonNull(personId, "personId is required");
        return cardRepository.findByPerson_IdAndActiveFalse(personId);
    }

    // Поиск по части UID
    @Transactional(readOnly = true)
    public List<Card> searchByUidFragment(String fragment) {
        if (fragment == null || fragment.isBlank()) return List.of();
        return cardRepository.findByUidContainingIgnoreCase(fragment);
    }

    // Карты, созданные после даты
    @Transactional(readOnly = true)
    public List<Card> listCreatedAfter(OffsetDateTime date) {
        Objects.requireNonNull(date, "date is required");
        return cardRepository.findByCreatedAtAfter(date);
    }

    // Сколько активных карт у сотрудника
    @Transactional(readOnly = true)
    public long countActiveByPerson(UUID personId) {
        Objects.requireNonNull(personId, "personId is required");
        return cardRepository.countByPerson_IdAndActiveTrue(personId);
    }

    @Transactional(readOnly = true)
    public Card findByUid(String uid) { return cardRepository.findByUid(uid); }

    public record RegisterResult(String status, String personName) {}

    // Регистрация карты по ФИО (авто-привязка если ровно одно совпадение)
    @Transactional
    public RegisterResult registerCardByName(String uid, String fullName) {
        validateUid(uid);
        if (fullName == null || fullName.isBlank()) {
            return new RegisterResult("needs_assignment", null);
        }
        List<Personnel> matches = personnelRepository.findByFullName(fullName);
        if (matches.isEmpty()) {
            return new RegisterResult("needs_assignment", null);
        }
        if (matches.size() > 1) {
            return new RegisterResult("multiple_matches", null);
        }
        Personnel person = matches.get(0);
        Card existing = cardRepository.findByUid(uid);
        if (existing != null) {
            if (existing.getPerson().getId().equals(person.getId())) {
                return new RegisterResult("already_assigned", existing.getPerson().getFullName());
            }
            existing.setPerson(person);
            cardRepository.save(existing);
            return new RegisterResult("auto_assigned", person.getFullName());
        }
        Card c = new Card();
        c.setUid(uid);
        c.setPerson(person); // person NOT NULL
        c.setActive(true);
        cardRepository.save(c);
        return new RegisterResult("auto_assigned", person.getFullName());
    }

    // Анализ сканирования в режиме регистрации (без имени)
    @Transactional
    public RegisterResult analyzeScanStatus(String uid) {
        validateUid(uid);
        Card existing = cardRepository.findByUid(uid);
        if (existing == null) {
            return new RegisterResult("needs_assignment", null);
        }
        return new RegisterResult("already_assigned", existing.getPerson().getFullName());
    }

    private void validateUid(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new IllegalArgumentException("uid is required");
        }
    }
}
