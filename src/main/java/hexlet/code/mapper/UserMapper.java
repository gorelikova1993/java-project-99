package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserUpdateDto;
import hexlet.code.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface  UserMapper {
    User toEntity(UserCreateDto dto, @Context PasswordEncoder encoder); // для шифрования
    UserDto toDto(User user);
    User map(UserDto model);
    User map(UserUpdateDto model);
    void updateEntityFromDto(UserUpdateDto dto, @MappingTarget User entity);
    @AfterMapping
    default void encryptPassword(@MappingTarget User user,
                                 UserCreateDto dto,
                                 @Context PasswordEncoder encoder) {
        String rawPassword = dto.getPassword();
        user.setPassword(encoder.encode(rawPassword));
    }
}
