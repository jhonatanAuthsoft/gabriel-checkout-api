package com.projeto.modelo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;

@Log4j2
public class TemplateUtils {

    public static String htmlToString(String htmlPath) throws IOException {
        InputStream template = null;
                template = new ClassPathResource(htmlPath).getInputStream();
                return new BufferedReader(new InputStreamReader(template))
                        .lines().parallel().collect(Collectors.joining());
    }
}
