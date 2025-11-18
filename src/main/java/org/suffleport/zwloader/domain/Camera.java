package org.suffleport.zwloader.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "cameras")
public class Camera {

    @Id
    @Column(name = "camera_id")
    private String id;              // ID камеры, например "cam-1"

    @Column(name = "name", nullable = false)
    private String name;            // человекочитаемое имя

    @Column(name = "rtsp_url", nullable = false)
    private String rtspUrl;         // RTSP ссылка

    @Column(name = "location")
    private String location;        // описание места

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;          // устройство, к которому привязана (может быть null)

    @Column(name = "created_at", updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    public Camera() {
    }

    public Camera(String id, String name, String rtspUrl, String location, Device device) {
        this.id = id;
        this.name = name;
        this.rtspUrl = rtspUrl;
        this.location = location;
        this.device = device;
    }
}
