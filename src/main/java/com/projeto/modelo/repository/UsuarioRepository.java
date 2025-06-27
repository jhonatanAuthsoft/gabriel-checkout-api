package com.projeto.modelo.repository;



import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.UsuarioStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndCodigoTrocaSenha(String email, Integer codigo);

    Optional<Usuario> findByEmailAndStatus(String email, UsuarioStatus status);

}
