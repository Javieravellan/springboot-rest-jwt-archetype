#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.migrations;

import ${package}.domain.Role;
import ${package}.domain.User;
import ${package}.domain.enums.ERole;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;

@ChangeLog(order = "01")
public class DatabaseMigration {

    @ChangeSet(order = "01", author = "anonymousUser", id = "addRoles")
    public void addRoles(MongockTemplate mongockTemplate) {
        Role roleAdmin = new Role();
        roleAdmin.setName(ERole.ROLE_ADMIN);
        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_USER);

        Role roleAldeamo = new Role();
        roleAldeamo.setName(ERole.ROLE_ALDEAMO);
        Role roleSender = new Role();
        roleSender.setName(ERole.ROLE_SENDER);

        mongockTemplate.save(roleAdmin);
        mongockTemplate.save(roleUser);
        mongockTemplate.save(roleSender);
        mongockTemplate.save(roleAldeamo);
    }

    @ChangeSet(order = "02", author = "anonymousUser", id = "addUsers")
    public void addUsers(MongockTemplate mongockTemplate) {
        var roleAdmin = mongockTemplate.findOne(Query.query(Criteria.where("name").is("ROLE_ADMIN")), Role.class, "roles");

        User userAdmin = new User();
        userAdmin.setUsername("admin");
        userAdmin.setPassword("${symbol_dollar}2a${symbol_dollar}12${symbol_dollar}OcaWuQyP.K0JWwiYJtDGEu6ULQt38awMs8YoSc1MdEKV0IOFSqSVu");
        userAdmin.getAuthorities().add(roleAdmin);
        userAdmin.setCreatedByUser("anonymousUser");
        userAdmin.setCreatedDate(LocalDateTime.now());
        mongockTemplate.save(userAdmin);
    }
}
