package fr.pharmelys.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.pharmelys.api.dto.alert.AlertDTO;
import fr.pharmelys.api.entity.AlertSubscription;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    @Mapping(source = "medication.cisCode", target = "cisCode")
    AlertDTO toDto(AlertSubscription entity);

    @Mapping(target = "medication", ignore = true)
    @Mapping(target = "lastNotifiedAt", ignore = true)
    AlertSubscription toEntity(AlertDTO dto);
}
