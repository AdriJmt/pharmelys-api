package fr.pharmelys.api.mapper;

import org.mapstruct.Mapper;

import fr.pharmelys.api.dto.patient.PatientDTO;
import fr.pharmelys.api.entity.PatientProfile;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDTO toDTO(PatientProfile entity);

    PatientProfile toEntity(PatientDTO dto);
}