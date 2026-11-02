
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY src/ ./src/
COPY public/ ./public/
COPY dictionary.txt ./dictionary.txt

RUN mkdir -p out && javac --add-modules jdk.httpserver -d out src/*.java

CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "out", "SmartSuggestServer"]
