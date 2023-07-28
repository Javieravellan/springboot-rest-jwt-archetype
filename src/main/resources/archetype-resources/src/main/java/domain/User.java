#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User extends AuditingEntity {
    @Id
    private String id;
    @Size(max = 20)
    private String username;
    @Size(max = 120)
    private String password;
    @DBRef
    private Set<Role> authorities = new HashSet<>();
    @Field("aldeamo_user_id")
    private String aldeamoUserId;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
