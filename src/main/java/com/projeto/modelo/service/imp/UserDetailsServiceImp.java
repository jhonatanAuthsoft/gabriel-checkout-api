package com.projeto.modelo.service.imp;


import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.repository.UsuarioRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = this.usuarioRepository.findByEmail(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String permissao = user.getPermissao().toString();

        String role = permissao.startsWith("ROLE_") ? permissao : "ROLE_" + permissao;

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        return new User(user.getUsername(), user.getPassword(), Collections.singletonList(authority));
    }
}
