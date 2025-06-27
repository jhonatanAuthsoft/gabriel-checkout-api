
Adicionar Como rodar
Swagger: http://localhost:8080/swagger-ui.html ou http://localhost:8080/swagger-ui/index.html



Integração com banco inter
Seguir a documentação = https://developers.inter.co/docs/introducao/como-criar-uma-aplicacao

Baixar os certificado e enviar. o certificado tem duração de 1 anos e após deve ser feito nova integração.
Copiar as clientId e a clientSecret
--

GERAR CERTIFICADOS PARA MTLS INTER
Necessario opennSSL
acesse C:\Program Files\OpenSSL-Win64 -> rode o start.bat -> utilize o comando

Para gerar esse certificado precisa ir no site da INTER
PROD:https://cdpj.partners.bancointer.com.br 
DEV:https://cdpj-sandbox.partners.uatinter.co 
baixar o certificado


Gerar Keystore PKCS12:

openssl pkcs12 -export -in (nome do certificado).crt -inkey (nome da chave).key -out keystore.p12 -name cliente -CAfile (nome do certificado).crt -caname root
Password: (senha que deseja adicionar)

EX:
openssl pkcs12 -export -in certificado.crt -inkey certificado.key -out keystore.p12 -name cliente -CAfile certificado.crt -caname root
Password: 123456

Adicione o certificado dentro da pasta de certificados

Gerar o  truststore:
keytool -importcert -file (certificado site inter).crt -keystore truststore.jks -alias server_inter_prod -storepass 123456

EX:
keytool -importcert -file certificado_servidor.crt -keystore truststore.jks -alias server_inter_prod -storepass 123456

---
Se necessario pode ser feito a parte de baixo

comando
openssl s_client -showcerts -connect cdpj.partners.bancointer.com.br:443

keytool -import -alias server_prod -keystore keystore_prod.p12 -file server_inter_prod.crt

adicionar o segundo certitificado

-----BEGIN CERTIFICATE-----
....
-----END CERTIFICATE-----

keytool -importcert -alias bancointer-intermediate -file bancointer-intermediate.crt -keystore truststore_prod.jks -storepass 123456

