package com.projeto.modelo.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/teste-swagger")
public class ConfiguracaoController {


    @Operation(
            summary = "Teste swagger",
            description = "Endpoint de teste do swagger"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requisição bem-sucedida"),
            @ApiResponse(responseCode = "404", description = "objeto não encontrado")
    })
    @GetMapping()
    public ResponseEntity testeSwagger(@Parameter(description = "ID do objeto", required = true)
                                       @RequestParam Long id) {
        return new ResponseEntity( HttpStatus.OK);
    }

}
