package fr.pharmelys.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.pharmelys.api.dto.allergy.AllergyDTO;
import fr.pharmelys.api.entity.Allergy;

@Mapper(componentModel = "spring")
public interface AllergyMapper {

    @Mapping(target = "substanceName", ignore = true)
    AllergyDTO toDto(Allergy entity);

    @Mapping(target = "patientProfile", ignore = true)
    Allergy toEntity(AllergyDTO dto);
}