#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mapper;

import ${package}.api.request.SignUpRequest;
import ${package}.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<SignUpRequest, User> {
    @Mapping(target = "authorities", ignore = true)
    User toEntity(SignUpRequest dto);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    SignUpRequest toDto(User entity);
}
