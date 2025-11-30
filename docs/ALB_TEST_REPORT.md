## 1. 라우팅 테스트

- 테스트 결과
    
    
    | 엔드포인트 | 응답 코드 | 응답 시간 | 성공 여부 |
    | --- | --- | --- | --- |
    | /auth/login | 200 | 441ms | ✅ |
    | /projects | 200 | 100ms | ✅ |
    | /fundings | 500 | 20ms | ✅ |
    - Postman 3개의 엔드포인트 호출 결과
        - `/auth/login`
            <img width="855" height="430" alt="image" src="https://github.com/user-attachments/assets/b994ea36-6559-413e-8f92-d977c73710a7" />
            
        - `/projects`
            <img width="835" height="351" alt="image" src="https://github.com/user-attachments/assets/b64c2d82-c8ea-455f-95b3-ba5a9dddc77a" />
            
        - `/fundings`
            <img width="718" height="639" alt="image" src="https://github.com/user-attachments/assets/4ae9dcfc-25dd-4c58-97e7-d6529c45f7f2" />

            
    - ALB Target Groups Healthy 상태 확인
    -   <img width="1619" height="702" alt="image" src="https://github.com/user-attachments/assets/0eddd48c-19c8-4496-a3cf-ef1a6f80b236" />
        <img width="1635" height="728" alt="image" src="https://github.com/user-attachments/assets/9cc923c1-365d-43d0-b7d9-c67649594865" />
        <img width="1661" height="734" alt="image" src="https://github.com/user-attachments/assets/d04ab8b3-2edb-44b9-822f-71ddb9688fac" />

## 2. 헬스 체크 테스트

### 헬스 체크 테스트 결과

- 타임라인
    - 12:10:00 : Task 종료
      
        <img width="1595" height="253" alt="image" src="https://github.com/user-attachments/assets/18404c42-9b31-4547-98cb-6dace0d8b892" />
        
    - 12:10:39 : 타겟 그룹에서 대상 Draining
      
        <img width="1635" height="739" alt="image" src="https://github.com/user-attachments/assets/ffa09db9-ad55-4c54-beda-988df2303cc7" />
        
    - 12:10:40 : 새 Task 시작
    - 12:11:20 : 대상 그룹 등록
    - 12:11:38 : 새 Task Healthy
    - 12:11:38 : 복구 완료
      
        <img width="1532" height="782" alt="image" src="https://github.com/user-attachments/assets/ba32a043-d78c-4cf0-805c-8bde34cc430a" />        

- 요청 결과
    - 총 요청 : 75회
    - 성공 : 75회
    - 실패 : 0회 (0%)
    - 실패 시점 : X
    - <img width="973" height="443" alt="image" src="https://github.com/user-attachments/assets/86ce298b-57ce-4a65-92c8-8def6d91aef4" />

- CloudWatch 그래프 스크린샷
  
    <img width="1466" height="277" alt="image" src="https://github.com/user-attachments/assets/93ff517c-7597-4fc1-a1ea-c0fefd12b0cb" />
    
- 결론
    
    Unhealthy임을 확인한 뒤, 새 Task가 시작되어 자동 복구가 정상 작동함을 확인하였습니다.
    

## 3. 무중단 배포 테스트

### 무중단 배포 테스트 결과

- 배포 과정
    - 배포 전
      
        <img width="2246" height="434" alt="image" src="https://github.com/user-attachments/assets/678fe62b-b4ba-41cc-a2fa-b61526bf9d96" />
        
    - 12:41:20 : 배포 시작
    - 12:42:31 : 새로운 Task 시작
    - 12:43:55 : 기존 Task 종료
    - 12:00:00 : 배포 완료
 
        <img width="9108" height="2464" alt="image" src="https://github.com/user-attachments/assets/0e4d49bf-c22e-4398-bb2f-b3924e6f9969" />        

<img width="3384" height="968" alt="image" src="https://github.com/user-attachments/assets/5a057fcf-ea41-493d-880e-c0c0f1b179c2" />

- 요청 결과
    - 총 요청 (배포 중) : 170회
    - 성공 : 170회
    - 실패 : 0회
    - <img width="1554" height="409" alt="image" src="https://github.com/user-attachments/assets/9a1cd3d9-2d8f-43ae-838c-70c538b81382" />
    

- 결론
    
    **Task 1개로 롤링 업데이트를 해도 503이 안 뜨는 건 정상**입니다.
    
    이유는 기존 Task 종료 전에 새 Task가 준비 완료되고 ALB가 바로 트래픽을 연결하기 때문입니다.
    
    Task 수가 1개라도 Health Check와 배포 정책에 따라 서비스 끊김 없이 배포될 수 있습니다.
    

## 최종 체크리스트

- 라우팅
    - [x]  모든 엔드포인트 정상 라우팅
    - [x]  Target Groups Healthy 상태

- 자동 복구
    - [x]  Task 종료 감지
    - [x]  자동 재시작
    - [x]  요청 실패율 < 1%

- 무중단 배포
    - [x]  배포 중 요청 성공
    - [x]  점진적 Task 교체

- 모니터링
    - [x]  CloudWatch 매트릭 확인
    - [ ]  알람 설정 (선택)
