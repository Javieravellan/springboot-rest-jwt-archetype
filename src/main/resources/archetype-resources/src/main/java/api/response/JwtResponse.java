#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private String id;
    private String jwt;
    private List<String> authorities;
    private String username;

    private JwtResponse() {}

    public JwtResponse(String id, String jwt, List<String> authorities, String username) {
        this.id = id;
        this.jwt = jwt;
        this.authorities = authorities;
        this.username = username;
    }
}