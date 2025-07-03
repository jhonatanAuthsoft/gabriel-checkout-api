package com.projeto.modelo.repository;


import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.UsuarioStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndCodigoTrocaSenha(String email, Integer codigo);

    Optional<Usuario> findByEmailAndStatus(String email, UsuarioStatus status);

    @Query(value = "SELECT * FROM usuarios WHERE (email = :email OR cpf = :cpf) AND id <> :idUsuario", nativeQuery = true)
    List<Usuario> findByEmailOrCpfAndNotId(@Param("email") String email, @Param("cpf") String cpf, @Param("idUsuario") Long idUsuario);

    @Query(value = "SELECT * FROM usuarios WHERE email = :email AND id <> :idUsuario", nativeQuery = true)
    List<Usuario> findByEmailAndNotId(@Param("email") String email, @Param("idUsuario") Long idUsuario);

    @Query(value = "SELECT * FROM usuarios WHERE permissao = 'CLIENTE'", nativeQuery = true)
    Page<Usuario> listarTodosClientes(Pageable pageable);

    @Query(value = "SELECT * FROM usuarios WHERE permissao <> 'CLIENTE'", nativeQuery = true)
    Page<Usuario> listarUsuarios(Pageable pageable);

}
