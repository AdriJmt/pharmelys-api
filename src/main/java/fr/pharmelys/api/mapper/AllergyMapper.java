package fr.pharmelys.api.mapper;

import org.mapstruct.Mapper;

import fr.pharmelys.api.dto.allergy.AllergyDTO;
import fr.pharmelys.api.entity.Allergy;

@Mapper(componentModel = "spring")
public interface AllergyMapper {
    AllergyDTO toDTO(Allergy entity);

    Allergy toEntity(AllergyDTO dto);
}
