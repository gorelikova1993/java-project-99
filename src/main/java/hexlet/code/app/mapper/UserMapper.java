package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.dto.UserUpdateDto;
import hexlet.code.app.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public abstract class UserMapper {
    @Autowired
    private PasswordEncoder encoder;
    public abstract User toEntity(UserCreateDto createDto);
    public abstract User map(UserUpdateDto model);
    public abstract User map(UserDto model);
    public abstract void updateEntityFromDto(UserUpdateDto updateDto, @MappingTarget User entity);
    public abstract UserDto toDto(User user);
    @BeforeMapping
    public void encryptPassword(UserCreateDto data) {
        var password = data.getPassword();
        data.setPassword(encoder.encode(password));
    }
}
