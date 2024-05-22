package apt.project.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import apt.project.backend.models.User;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> {return new UsernameNotFoundException(username + " email not found");});
    }

    public Boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void register(User newUser){
        userRepository.save(newUser);
        Long Id = userRepository.getIdFromEmail(newUser.getEmail()).get();
        userRepository.insertUserRole(Id, "USER");
    }
}
