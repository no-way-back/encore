package com.nowayback.funding.presentation.Funding.dto.request;

import java.util.List;
import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.presentation.Funding.dto.request.validation.ValidFundingRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ValidFundingRequest
public record CreateFundingRequest(
	@NotNull(message = "프로젝트 ID는 필수입니다.")
	UUID projectId,

	@Valid
	List<RewardItemRequest> rewardItems,

	@Min(value = 1000, message = "후원 금액은 최소 1,000원 이상이어야 합니다.")
	Long amount,

	@NotNull(message = "PG 결제 키는 필수입니다.")
	String pgPaymentKey,

	@NotNull(message = "PG 주문 ID는 필수입니다.")
	String pgOrderId,

	@NotNull(message = "결제 수단은 필수입니다.")
	String pgMethod
) {
	public CreateFundingCommand toCommand(UUID userId, String idempotencyKey) {
		List<CreateFundingCommand.RewardItem> commandRewardItems = null;

		if (rewardItems != null && !rewardItems.isEmpty()) {
			commandRewardItems = rewardItems.stream()
				.map(item -> new CreateFundingCommand.RewardItem(
					item.rewardId(),
					item.optionId(),
					item.quantity()
				))
				.toList();
		}

		return new CreateFundingCommand(
			projectId,
			userId,
			commandRewardItems,
			amount,
			pgPaymentKey,
			pgOrderId,
			pgMethod,
			idempotencyKey
		);
	}

	public record RewardItemRequest(
		@NotNull(message = "리워드 ID는 필수입니다.")
		Long rewardId,

		@NotNull(message = "옵션 ID는 필수입니다.")
		Long optionId,

		@NotNull(message = "수량은 필수입니다.")
		@Positive(message = "수량은 0보다 커야 합니다.")
		Integer quantity
	) {}
}
