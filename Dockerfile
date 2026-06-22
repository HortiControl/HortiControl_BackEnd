# Estágio 1: Build da aplicação
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
# Copia tudo da raiz para dentro do container
COPY . .
# Faz o build do projeto
RUN ./mvnw clean package -DskipTests

# Estágio 2: Execução (Imagem final mais leve)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia o jar gerado no estágio anterior (Maven coloca na pasta 'target')
COPY --from=build /app/target/*.jar app.jar
# Expõe a porta que o Render vai utilizar
EXPOSE 10000
# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]