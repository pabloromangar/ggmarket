# --- ETAPA 1: Construcción (Build Stage) ---
# Usamos una imagen oficial de Maven que ya tiene Java y Maven instalados.
# Especificamos Java 17 (puedes cambiarlo a 21 si es lo que usas).
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar la caché de Docker
COPY pom.xml .

# Descargamos las dependencias
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Ejecutamos el comando de construcción para compilar y empaquetar la aplicación
RUN mvn package -DskipTests


# --- ETAPA 2: Ejecución (Runtime Stage) ---
# Usamos una imagen de Java más ligera, solo para ejecutar la aplicación.
FROM eclipse-temurin:17-jre-alpine

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo .jar construido en la etapa anterior a esta nueva etapa
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080 (el puerto por defecto de Spring Boot)
EXPOSE 8080

# El comando que se ejecutará cuando el contenedor arranque
ENTRYPOINT ["java", "-jar", "app.jar"]