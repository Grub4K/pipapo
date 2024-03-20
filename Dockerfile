FROM openjdk:23-slim as build

EXPOSE 12345

RUN mkdir /pipapo/
WORKDIR /pipapo/

RUN mkdir src/
RUN mkdir out/

COPY src/ src/
COPY MANIFEST.in .
RUN javac -d out/ -classpath src/ src/com/grub4k/pipapo/*.java
RUN jar --create --verbose --manifest MANIFEST.in --file pipapo.jar -C out/ .


FROM openjdk:23-slim as runner

RUN mkdir /pipapo/
WORKDIR /pipapo/
COPY --from=build /pipapo/pipapo.jar /pipapo/

ENTRYPOINT ["java", "-jar", "pipapo.jar"]
