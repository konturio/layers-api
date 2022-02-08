FROM nexus.kontur.io:8084/redhat/ubi8
RUN dnf -y install java-17-openjdk \
  && dnf clean all

RUN useradd spring
USER spring:spring

EXPOSE 8630

COPY build/dependency/BOOT-INF/lib /app/lib
COPY build/dependency/META-INF /app/META-INF
COPY build/dependency/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","io.kontur.layers.LayersApiApplication"]