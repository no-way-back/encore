package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.projectdraft.command.SaveSettlementDraftCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "정산 드래프트 저장 요청")
public record SaveProjectSettlementDraft(

    @Schema(description = "사업자등록번호", example = "123-45-67890")
    String businessNumber,

    @Schema(description = "은행명", example = "KB국민은행")
    String accountBank,

    @Schema(description = "계좌번호", example = "12345678901234")
    String accountNumber,

    @Schema(description = "예금주명", example = "홍길동")
    String accountHolder
) {

    public SaveSettlementDraftCommand toCommand(UUID draftId, UUID userId) {
        return SaveSettlementDraftCommand.of(
            draftId,
            businessNumber,
            accountBank,
            accountNumber,
            accountHolder
        );
    }
}
