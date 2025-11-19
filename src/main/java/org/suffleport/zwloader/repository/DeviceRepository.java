package org.suffleport.zwloader.repository;

import org.suffleport.zwloader.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, String> {
    // Поиск устройства по имени
    List<Device> findByName(String name);

    // По подстроке в имени
    List<Device> findByNameContainingIgnoreCase(String fragment);
}
