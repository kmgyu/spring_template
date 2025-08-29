## MariaDB 초기 세팅

이 템플릿에서는 MariaDB를 사용합니다.
도커 컨테이너 이미지를 활용했습니다.

개발 환경에서 필요한 DB 및 계정을 생성하세요:

```sql
CREATE DATABASE IF NOT EXISTS `template-board`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

CREATE USER IF NOT EXISTS 'template_user'@'%' IDENTIFIED BY 'template_pass';
GRANT ALL PRIVILEGES ON `template-board`.* TO 'template_user'@'%';
FLUSH PRIVILEGES;
```


이후 애플리케이션 설정(application.yml 또는 application.properties)에서 아래와 같이 세팅할 수 있습니다.

> 주의 사항<br>
> 보안에 관련된 환경변수는 application-secrets.properties와 같이 노출되지 않는 파일에 저장하세요.


```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.url=jdbc:mariadb://localhost:3307/template-board
spring.datasource.username=template_user
spring.datasource.password=template_pass
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.show-sql:true
```


형상 관리를 위해 flyway를 사용합니다.