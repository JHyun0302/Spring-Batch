# Spring OAuth2 Client JWT 실습

## 실습 목표
- 스프링 배치 5 프레임워크를 활용하여 스프링 생태계에서 대량의 데이터를 안전하게 처리할 수 있는 기본적인 환경을 구축한다.

---
## 배치란?
- 사전적 의미 : “일정 시간 동안 대량의 데이터를 한 번에 처리하는 방식”

### 프레임워크를 사용하는 이유는? 
- 아주 많은 데이터를 처리 하는 중간에 프로그램이 멈출 수 있는 상황을 대비해 안전 장치를 마련해야 하기 때문
- 10만개의 데이터를 복잡한 JOIN을 걸어 DB간 이동 시키는 도중 프로그램이 멈춰버리면 처음부터 다시 시작할 수 없기 때문에 작업 지점을 기록해야하며,
- 급여나 은행 이자 시스템의 경우 특정 일 (7월, 오늘, 2020년, 등등)에 했던 처리를 또 하는 중복 불상사도 막아야하는 이유가 있습니다.

![](https://yummi-image-1.s3.amazonaws.com/image-f05f97f2-2435-49e1-b23c-ebe4968a0678.jpg)

### 구현
- 메타 테이블 DB / 운영 테이블 DB 자체를 분리

- FirstBatch : 테이블 → 테이블 배치
- SecondBatch : 특정 조건이 일치하는 경우, 다른 컬럼 값 변경하는 Process
- FourthBatch : 엑셀 → 테이블 배치
- FifthBatch : 테이블 → 엑셀 배치
- SixthBatch : JPA 성능 문제와 JDBC

## 스프링 배치 모식도

![](https://yummi-image-1.s3.amazonaws.com/image-5a2a9bcf-1bd8-471d-87d6-9eb2b92f6a96.jpg)
