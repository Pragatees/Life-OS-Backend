#FROM eclipse-temurin:21-jdk
#
#WORKDIR /app
#
#COPY . .
#
#RUN chmod +x mvnw
#RUN ./mvnw clean package -DskipTests
#
#EXPOSE 8080
#
#CMD ["java", "-jar", "target/life-os-backend-0.0.1-SNAPSHOT.jar"]


#FROM eclipse-temurin:21-jdk
#
#WORKDIR /app
#
#COPY . .
#
#RUN chmod +x mvnw
#RUN ./mvnw clean package -DskipTests
#
#EXPOSE 8080
#
#ENTRYPOINT ["java","-jar","target/life-os-backend-0.0.1-SNAPSHOT.jar"]

# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/life-os-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]