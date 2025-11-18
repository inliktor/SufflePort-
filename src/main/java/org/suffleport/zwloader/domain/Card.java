package org.suffleport.zwloader.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {

    // uid — первичный ключ (реальный UID карты с ридера)
    @Id
    @Column(name = "uid")
    private String uid;

    // пока просто UUID сотрудника; позже можем заменить на @ManyToOne Personnel
    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    // БД сама ставит now(), поэтому insertable=false, updatable=false
    @Column(name = "created_at", updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    public Card() {
    }

    public Card(String uid, UUID personId) {
        this.uid = uid;
        this.personId = personId;
        this.active = true; // по умолчанию карта активна
    }
}