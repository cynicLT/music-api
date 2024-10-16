FROM eclipse-temurin:23-jre@sha256:sha256:d26950918386388a3c33e8272bb26e116bbe9b325c1239228e176617ab861e9e AS builder

WORKDIR /tmp

COPY target/spring-boot/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:23-jre@sha256:sha256:d26950918386388a3c33e8272bb26e116bbe9b325c1239228e176617ab861e9e

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=80.0 -XX:+UseZGC --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED"
ENV TMP_DIR="/usr/src/app/data/temp"

WORKDIR /usr/src/app
RUN mkdir -p ${TMP_DIR}

COPY --from=builder /tmp/dependencies/ ./
COPY --from=builder /tmp/spring-boot-loader/ ./
COPY --from=builder /tmp/application/ ./
ENTRYPOINT java ${JAVA_OPTS} -Djava.io.tmpdir=${TMP_DIR} org.springframework.boot.loader.launch.JarLauncher
