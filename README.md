# Observability Starter

[![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.12-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

재사용 가능한 Spring Boot Starter로, Prometheus와 Grafana를 활용한 프로덕션 레벨의 모니터링 시스템을 제공합니다.

## Why Observability Starter?

- **즉시 적용 가능** - 의존성 추가만으로 모니터링 시스템 구축
- **Zero Configuration** - 합리적인 기본 설정으로 별도 설정 없이 사용 가능
- **커스터마이징 가능** - Properties를 통한 유연한 설정 변경
- **프로덕션 레디** - Prometheus + Grafana 스택 포함

## Quick Start

### Prerequisites

- Java 21+
- Spring Boot 3.4+
- Docker & Docker Compose (모니터링 스택 실행 시)### Installation

#### Option 1: Gradle (Recommended)

**Step 1. GitHub Personal Access Token 생성**

[GitHub Settings → Developer settings → Personal access tokens](https://github.com/settings/tokens)에서 토큰 생성:
- Scope 선택: `read:packages`

**Step 2. gradle.properties 파일 생성**

프로젝트 루트에 `gradle.properties` 파일 생성:
```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.token=ghp_xxxxxxxxxxxxxxxxxxxx
```

**⚠️ 중요: .gitignore에 추가**
```gitignore
gradle.properties
```

**Step 3. build.gradle 설정**

```gradle
repositories {
    mavenCentral()

    // GitHub Packages 저장소 추가
    maven {
        url = uri("https://maven.pkg.github.com/yukmekim/observability-starter")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'dev.observability:observability-starter:0.3.9'
}
```

**Step 4. 애플리케이션 실행**
```bash
./gradlew clean build
./gradlew bootRun
```

**Step 5. 메트릭 확인**
```bash
# Prometheus 메트릭
curl http://localhost:8080/actuator/prometheus

# Health Check
curl http://localhost:8080/actuator/health

# 모든 메트릭 목록
curl http://localhost:8080/actuator/metrics
```

---

#### Option 2: Maven

**Step 1. settings.xml에 GitHub Packages 설정**

`~/.m2/settings.xml`:
```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

**Step 2. pom.xml 설정**

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/yukmekim/observability-starter</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>dev.observability</groupId>
    <artifactId>observability-starter</artifactId>
    <version>0.3.9</version>
  </dependency>
</dependencies>
```

## Features

### Key Features
- **Effective Metric Filtering**: 불필요한 URL(Swagger, Actuator 등) 자동 필터링으로 메트릭 노이즈 제거
- **Latency Distribution**: `percentiles-histogram` 자동 구성을 통해 정확한 응답 시간 분포(P95, P99) 및 히스토그램 제공

### Automatic Configuration

Spring Boot Auto Configuration을 통해 자동으로 설정됩니다:

- **Actuator Endpoints** - health, info, metrics, prometheus
- **Micrometer Metrics** - JVM, HTTP, 커스텀 메트릭 수집
- **Prometheus Integration** - `/actuator/prometheus` 엔드포인트 자동 노출
- **Common Tags** - 애플리케이션 정보 자동 태깅

### Monitoring Stack (Docker Compose)

Prometheus와 Grafana를 Docker Compose로 간편하게 실행:

```bash
cd monitoring-stack
docker-compose up -d
```

**접속 정보:**
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090

## Configuration

### Basic Configuration

```yaml
# application.yml
observability:
  enabled: true
  
  metrics:
    # 1. 공통 태그 설정 (모든 메트릭에 자동 부착)
    common-tags:
      application: reservation-rush
      environment: dev
    # 2. 불필요한 URL 메트릭 수집 제외 (Wildcard 지원)
    exclude-patterns:
      - /actuator/**
      - /swagger-ui/**
      - /v3/api-docs/**
      - /favicon.ico
    # 3. HTTP 응답 시간 분포(Histogram) 및 Percentile 수집 설정
    # 주의: enabled: true로 설정해야 Grafana에서 히스토그램(Bucket) 및 P95, P99 등을 볼 수 있습니다.
    percentiles:
      enabled: true
      percentiles: [0.5, 0.9, 0.95, 0.99]
```

### Actuator Endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    tags:
      application: ${spring.application.name}
```

## Project Structure

```
observability-starter/
├── src/main/java/dev/observability/
│   ├── autoconfigure/       # Auto Configuration 클래스
│   ├── properties/          # Configuration Properties
│   ├── metrics/             # 커스텀 메트릭 유틸리티
│   └── filter/              # 로깅 필터
│
├── monitoring-stack/        # Docker Compose 스택
│   ├── docker-compose.yml
│   ├── prometheus/
│   │   └── prometheus.yml
│   └── grafana/
│       └── dashboards/
│
└── README.md
```

## Tech Stack

**Backend**
- Java 21
- Spring Boot 3.4
- Spring Boot Actuator
- Micrometer

**Monitoring**
- Prometheus (메트릭 수집 및 저장)
- Grafana (시각화 대시보드)

**Build Tool**
- Gradle 8.x
