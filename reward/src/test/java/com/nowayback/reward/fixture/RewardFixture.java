package com.nowayback.reward.fixture;

import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.command.CreateRewardOptionCommand;
import com.nowayback.reward.domain.reward.handler.command.RewardCreateCommand;
import com.nowayback.reward.domain.reward.handler.command.RewardOptionCreateCommand;
import com.nowayback.reward.domain.reward.vo.RewardType;

import java.util.List;
import java.util.UUID;

public class RewardFixture {

    public static CreateRewardCommand createCommand() {
        return createCommand("테스트 리워드", 25000, 100);
    }

    public static CreateRewardCommand createCommand(String name, Integer price, Integer stock) {
        return new CreateRewardCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                name,
                "테스트 설명",
                price,
                stock,
                3000,
                50000,
                5,
                RewardType.GENERAL,
                null
        );
    }

    public static CreateRewardCommand createCommandWithOptions() {
        List<CreateRewardOptionCommand> options = List.of(
                createOptionCommand("S", 0, 20, 1),
                createOptionCommand("M", 0, 30, 2),
                createOptionCommand("L", 2000, 25, 3)
        );

        return new CreateRewardCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "옵션 있는 리워드",
                "설명",
                25000,
                100,
                3000,
                50000,
                5,
                RewardType.GENERAL,
                options
        );
    }

    public static CreateRewardOptionCommand createOptionCommand(
            String name, Integer additionalPrice, Integer stock, Integer displayOrder) {
        return new CreateRewardOptionCommand(
                name,
                additionalPrice,
                stock,
                true,
                displayOrder
        );
    }

    public static RewardCreateCommand createRequest() {
        return createRequest("테스트 리워드", 25000, 100);
    }

    public static RewardCreateCommand createRequest(String name, Integer price, Integer stock) {
        return new RewardCreateCommand(
                name,
                "테스트 설명",
                price,
                stock,
                3000,
                50000,
                5,
                RewardType.GENERAL,
                null
        );
    }

    public static RewardCreateCommand createRequestWithOptions() {
        List<RewardOptionCreateCommand> options = List.of(
                createOptionRequest("S", 0, 20, 1),
                createOptionRequest("M", 0, 30, 2),
                createOptionRequest("L", 2000, 25, 3)
        );

        return new RewardCreateCommand(
                "옵션 있는 리워드",
                "설명",
                25000,
                100,
                3000,
                50000,
                5,
                RewardType.GENERAL,
                options
        );
    }

    public static CreateRewardCommand createCommandWithShippingPolicy(
            Integer shippingFee, Integer freeShippingAmount) {
        return new CreateRewardCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "배송 정책 테스트 리워드",
                "테스트 설명",
                25000,
                100,
                shippingFee,
                freeShippingAmount,
                5,
                RewardType.GENERAL,
                null
        );
    }

    public static RewardOptionCreateCommand createOptionRequest(
            String name, Integer additionalPrice, Integer stock, Integer displayOrder) {
        return new RewardOptionCreateCommand(
                name,
                additionalPrice,
                stock,
                true,
                displayOrder
        );
    }

    public static List<CreateRewardOptionCommand> createOptionCommands(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> createOptionCommand("Option" + i, 0, 10, i))
                .toList();
    }

    public static List<RewardCreateCommand> createRequests(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> createRequest("리워드" + i, 25000, 100))
                .toList();
    }
}