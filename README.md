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
- Docker & Docker Compose (모니터링 스택 실행 시)

### Installation

**1. 의존성 추가**
```gradle
dependencies {
    implementation 'dev.observability:observability-spring-boot-starter:0.1.0'
}
```

**2. 애플리케이션 실행**
```bash
./gradlew bootRun
```

**3. 메트릭 확인**
```
Actuator: http://localhost:8080/actuator
Prometheus Metrics: http://localhost:8080/actuator/prometheus
Health Check: http://localhost:8080/actuator/health
```

## Features

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
  enabled: true  # 모니터링 활성화 (기본값: true)
  metrics:
    common-tags:
      application: my-app
      environment: production
      team: backend
  logging:
    include-headers: true
    include-query-string: true
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

## Usage in Other Projects

### 1. Gradle 의존성 추가
```gradle
dependencies {
    implementation 'dev.observability:observability-spring-boot-starter:0.1.0'
}
```

### 2. 설정 커스터마이징 (선택사항)
```yaml
observability:
  enabled: true
  metrics:
    common-tags:
      application: reservation-rush
      environment: production
```

### 3. 애플리케이션 실행 후 메트릭 확인
```bash
curl http://localhost:8080/actuator/prometheus
```
