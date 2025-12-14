package com.nowayback.funding.application.client.reward.dto.response;

import java.util.List;
import java.util.UUID;

public record StockReserveResponse(
	UUID fundingId,
	List<ReservedItem> reservedItems,
	Long totalAmount
) {
	/**
	 * 예약된 항목 정보
	 *
	 * @param reservationId 예약 ID (환불/취소 시 사용)
	 * @param rewardId 리워드 ID
	 * @param optionId 옵션 ID
	 * @param quantity 수량
	 * @param itemAmount 해당 항목의 금액
	 */
	public record ReservedItem(
		UUID reservationId,
		UUID rewardId,
		UUID optionId,
		Integer quantity,
		Long itemAmount
	) {}
}