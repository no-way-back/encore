package com.nowayback.payment.infrastructure.settlement.external.openbanking;

import com.nowayback.payment.application.settlement.service.openbanking.OpenBankingClient;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenBankingClientImpl implements OpenBankingClient {

    private final OpenBankingFeignClient openBankingFeignClient;

    @Override
    public String transfer(String accountBank, String accountNumber, String accountHolderName, Long amount) {
        log.info("[External Open Banking] 정산금 이체 요청 - bank: {}, accountNumber: {}, accountHolderName: {}, amount: {}",
                accountBank, accountNumber, accountHolderName, amount);

        try {
            /*
            OpenBankingTransferRequest request = new OpenBankingTransferRequest(
                    accountBank,
                    accountNumber,
                    accountHolderName,
                    amount
            );

            OpenBankingTransferResponse response = openBankingFeignClient.transfer(request);

            log.info("[External Open Banking] 정산금 이체 성공 - transactionId: {}", response.transactionId());

            return response.transactionId();
            */

            String mockTransactionId = "OB-" + System.currentTimeMillis();

            log.info("[External Open Banking] 정산금 이체 성공 - transactionId: {}", mockTransactionId);

            return mockTransactionId;
        } catch (Exception e) {
            log.error("[External Open Banking] 정산금 이체 실패 - bank: {}, accountNumber: {}, error: {}",
                    accountBank, accountNumber, e.getMessage());

            throw new PaymentException(PaymentErrorCode.OPEN_BANKING_TRANSFER_FAILED);
        }
    }
}
