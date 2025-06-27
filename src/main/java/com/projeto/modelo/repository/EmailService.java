package com.projeto.modelo.repository;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {


     @Async
     void cadastraUsuario(String toEmail, String senha);

     void enviarEmailEsqueceuSenha(String toEmail);
}
