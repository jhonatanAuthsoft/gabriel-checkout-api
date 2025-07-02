package com.projeto.modelo.service;


import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.model.entity.Imagem;
import com.projeto.modelo.repository.ImagemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ImagemRepository imagemRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public Imagem uploadFile(MultipartFile file, String path, Imagem imagem) throws IOException {
        path = normalizePath(path);

        String sanitizedFileName = sanitizeFileName(file.getOriginalFilename());

        String fullKey = path + "/" + new Date().getTime() + "_" + sanitizedFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        imagem.setCaminhoImagem(fullKey);

        return imagemRepository.save(imagem);
    }

    public String generateSignedDownloadUrl(Long imagemId) {
        Instant expirationTime = Instant.now().plus(1, ChronoUnit.HOURS);

        String fullKey = imagemRepository.findById(imagemId).orElseThrow(() -> new ExcecoesCustomizada("Imagem não encontrada!", HttpStatus.NOT_FOUND)).getCaminhoImagem();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        // Gera URL assinada
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.between(Instant.now(), expirationTime))
                .build();

        // Gera a URL assinada
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public void deleteFile(Long imagemId) {
        String fullKey = imagemRepository.findById(imagemId).orElseThrow(() -> new ExcecoesCustomizada("Imagem não encontrada!", HttpStatus.NOT_FOUND)).getCaminhoImagem();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String normalizePath(String path) {
        if (path == null) return "";

        // Remove barras no início e no fim
        path = path.trim();
        path = path.replaceAll("^/+", "").replaceAll("/+$", "");

        return path;
    }

    private String sanitizeFileName(String originalFileName) {
        if (originalFileName == null) {
            return UUID.randomUUID().toString();
        }

        // Remove caracteres especiais e substitui espaços
        String sanitized = originalFileName
                .replaceAll("[^a-zA-Z0-9.-]", "_")
                .replaceAll("\\s+", "_");

        // Se após sanitização ficar vazio, usa UUID
        return sanitized.isEmpty()
                ? UUID.randomUUID().toString()
                : sanitized;
    }
}
