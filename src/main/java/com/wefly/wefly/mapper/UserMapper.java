package com.wefly.wefly.mapper;

import com.wefly.wefly.model.User;
import com.wefly.wefly.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Si los nombres de los campos coinciden en User y UserDTO,
    // MapStruct lo hace automáticamente.
    UserDTO toDTO(User user);

    // Al convertir de DTO a Entidad, ignoramos la contraseña
    // porque el DTO no la tiene y no queremos sobrescribirla con null.
    @Mapping(target = "password", ignore = true)
    User toEntity(UserDTO dto);
}
