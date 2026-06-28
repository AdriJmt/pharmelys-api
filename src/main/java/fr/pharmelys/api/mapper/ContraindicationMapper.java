package fr.pharmelys.api.mapper;

import org.mapstruct.Mapper;

import fr.pharmelys.api.dto.contraindication.ContraindicationDTO;
import fr.pharmelys.api.entity.DeclaredContraindication;

@Mapper(componentModel = "spring")
public interface ContraindicationMapper {
    ContraindicationDTO toDTO(DeclaredContraindication entity);

    DeclaredContraindication toEntity(ContraindicationDTO dto);
}
