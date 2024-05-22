package apt.project.backend.models;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GenerationType;

@Entity(name = "users")
@Table(
    name = "users"
)
@Setter
@Getter
@NoArgsConstructor
public class User implements UserDetails {
    @SequenceGenerator(
        name = "user_id_seq",
        sequenceName = "user_id_seq",
        allocationSize = 1
    )
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_id_seq"
    )
    @Column(
        nullable = false,
        name = "id"
    )
    private long id;
    @Column(
        nullable = false,
        name = "first_name"
    )
    private String firstName;
    @Column(
        nullable = false,
        name = "last_name"
    )
    private String lastName;
    @Column(
        nullable = false,
        name = "password"
    )
    private String password;
    @Column(
        nullable = false,
        name = "email"
    )
    private String email;

    public User(String fname, String lname, String email, String password){
        this.firstName = fname;
        this.lastName = lname;
        this.email = email;
        this.password = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority("USER");
        return Collections.singletonList(auth);
    }


    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true; // this will be handled by verification of jwt
    }


    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return email;
    }
}
