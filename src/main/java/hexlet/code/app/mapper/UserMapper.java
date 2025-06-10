package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.dto.UserUpdateDto;
import hexlet.code.app.model.User;
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
        System.out.println("Шифруем пароль: " + dto.getPassword());
        String rawPassword = dto.getPassword();
        user.setPassword(encoder.encode(rawPassword));
    }
}
