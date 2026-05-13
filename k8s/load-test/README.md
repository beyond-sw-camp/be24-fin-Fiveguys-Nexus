# 부하 테스트 환경

운영 서버에 영향 없이 부하 테스트를 수행하기 위한 별도 환경.

## 구조

```
운영 환경 (건드리지 않음)                테스트 환경 (부하 테스트용)
┌──────────────────────┐              ┌──────────────────────┐
│ nexus-backend-green  │              │ test-backend         │
│   → db-service       │              │   → test-db-service  │
│   (Istio sidecar O)  │              │   (Istio sidecar X)  │
│   (리소스 제한 없음)   │              │   (CPU 1core / 1Gi)  │
└──────────────────────┘              │   (JVM -Xms512m)     │
                                      └──────────────────────┘
```

## 배포 순서

```bash
# 1. 테스트용 DB 생성
kubectl apply -f test-db-statefulset.yaml

# 2. 테스트용 ConfigMap 생성 (테스트 DB를 바라봄)
kubectl apply -f test-backend-configmap.yaml

# 3. 테스트용 Backend 배포
kubectl apply -f test-backend-deployment.yaml

# 4. pod 상태 확인
kubectl get pods -l app=test-backend
kubectl get pods -l type=test-db

# 5. 테스트 DB에 데이터 세팅 필요
#    운영 DB에서 스키마 + 테스트 데이터를 dump해서 테스트 DB에 넣기
#    (아래 '데이터 준비' 섹션 참고)
```

## 데이터 준비

```bash
# 운영 DB에서 스키마 + 기본 데이터 dump
kubectl exec db-statefulset-0 -- mysqldump -uroot -pqwer1234 nexus > dump.sql

# 테스트 DB에 import
kubectl cp dump.sql test-db-statefulset-0:/tmp/dump.sql
kubectl exec test-db-statefulset-0 -- mysql -uroot -pqwer1234 nexus < /tmp/dump.sql
```

## k6 부하 테스트

```bash
# 테스트 Backend 접속 주소
# <아무 노드 IP>:30088 로 접근 가능

# 예시
k6 run --vus 10 --duration 1m test.js
```

## 정리 (테스트 끝나면)

```bash
kubectl delete -f test-backend-deployment.yaml
kubectl delete -f test-backend-configmap.yaml
kubectl delete -f test-db-statefulset.yaml

# 테스트 DB PVC도 삭제
kubectl delete pvc volume-test-db-statefulset-0
```
