package com.nowayback.funding.application.client.reward.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockReserveRequest(
	@NotNull(message = "fundingId는 필수입니다")
	UUID fundingId,

	@NotEmpty(message = "재고 차감 항목은 최소 1개 이상이어야 합니다")
	@Valid
	List<StockReserveItem> items
) {
	/**
	 * 재고 차감 항목
	 *
	 * @param rewardId 리워드 ID
	 * @param optionId 옵션 ID (선택사항)
	 * @param quantity 수량
	 */
	public record StockReserveItem(
		@NotNull(message = "rewardId는 필수입니다")
		UUID rewardId,

		UUID optionId,

		@NotNull(message = "수량은 필수입니다")
		@Positive(message = "수량은 1 이상이어야 합니다")
		Integer quantity
	) {}
}
