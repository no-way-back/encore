package com.nowayback.payment.infrastructure.settlement.external.project;

import com.nowayback.payment.application.settlement.service.project.ProjectClient;
import com.nowayback.payment.application.settlement.service.project.dto.ProjectAccountResult;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.infrastructure.settlement.external.project.dto.ProjectAccountResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectClientImpl implements ProjectClient {

    private final ProjectFeignClient projectFeignClient;

    @Override
    public ProjectAccountResult getProjectAccountInfo(UUID projectId) {
        log.info("[External Project Service] 프로젝트 계좌 정보 조회 - projectId: {}", projectId);

        try {
            /*
            ProjectAccountResponse response = projectFeignClient.getProjectAccountInfo(projectId);

            log.info("[External Project Service] 프로젝트 계좌 정보 조회 성공 - projectId: {}", projectId);

            return new ProjectAccountResult(
                    response.projectId(),
                    response.accountBank(),
                    response.accountNumber(),
                    response.accountHolderName()
            );
            */

            log.info("[External Project Service] 프로젝트 계좌 정보 조회 성공 - projectId: {}", projectId);

            return new ProjectAccountResult(
                    projectId,
                    "MockBank",
                    "000-000-0000",
                    "홍길동"
            );
        } catch (FeignException e){
            log.error("[External Project Service] 프로젝트 계좌 정보 조회 실패 - projectId: {}, error: {}", projectId, e.getMessage());

            throw new PaymentException(PaymentErrorCode.PROJECT_CLIENT_REQUEST_FAILED);
        }
    }
}
