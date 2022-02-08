FROM nexus.kontur.io:8084/redhat/ubi8
RUN dnf -y install java-17-openjdk \
  && dnf clean all

RUN useradd spring
USER spring:spring

EXPOSE 8630

ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","io.kontur.layers.LayersApiApplication"]