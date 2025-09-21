# 런타임만 담긴 가벼운 베이스 이미지
FROM eclipse-temurin:21-jre

# 보안: 비루트 유저로 실행
RUN useradd -r -u 1001 appuser
USER 1001

WORKDIR /app

# 애플리케이션 JAR 복사 (로컬 빌드 산출물 경로에 맞춰 수정)
ARG JAR=build/libs/*-SNAPSHOT.jar
COPY ${JAR} app.jar

# 기본 환경
ENV TZ=Asia/Seoul \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50" \
    SPRING_PROFILES_ACTIVE=prod,secret

EXPOSE 8080

# 헬스체크 (actuator 사용 시)
# HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
#   CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
