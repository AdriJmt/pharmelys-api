package fr.pharmelys.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.pharmelys.api.dto.contraindication.ContraindicationDTO;
import fr.pharmelys.api.entity.DeclaredContraindication;

@Mapper(componentModel = "spring")
public interface ContraindicationMapper {
    ContraindicationDTO toDto(DeclaredContraindication entity);

    @Mapping(target = "patient", ignore = true)
    DeclaredContraindication toEntity(ContraindicationDTO dto);
}
