package fr.pharmelys.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.pharmelys.api.dto.patient.PatientDTO;
import fr.pharmelys.api.entity.PatientProfile;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDTO toDTO(PatientProfile entity);

    @Mapping(target = "allergies", ignore = true)
    @Mapping(target = "contraindications", ignore = true)
    PatientProfile toEntity(PatientDTO dto);
}