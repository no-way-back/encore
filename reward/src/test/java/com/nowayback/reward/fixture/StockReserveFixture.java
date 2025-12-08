package com.nowayback.reward.fixture;

import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.command.CreateRewardOptionCommand;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.vo.RewardType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class StockReserveFixture {

    /**
     * 옵션 없는 리워드 생성
     */
    public static Rewards createRewardWithoutOption(Integer stock) {
        CreateRewardCommand command = new CreateRewardCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "기본 티셔츠",
                "기본 리워드 상품입니다",
                30000L,
                stock,
                3000,
                50000,
                5,
                RewardType.GENERAL,
                null
        );
        Rewards reward = Rewards.create(command);
        setRewardId(reward, UUID.randomUUID());
        return reward;
    }

    /**
     * 옵션 있는 리워드 생성 (선택 옵션)
     */
    public static Rewards createRewardWithOptions() {
        List<CreateRewardOptionCommand> options = List.of(
                new CreateRewardOptionCommand("S 사이즈", 0L, 30, false, 1),
                new CreateRewardOptionCommand("M 사이즈", 2000L, 50, false, 2),
                new CreateRewardOptionCommand("L 사이즈", 4000L, 20, false, 3)
        );

        CreateRewardCommand command = new CreateRewardCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "프리미엄 티셔츠",
                "사이즈를 선택할 수 있는 티셔츠입니다",
                50000L,
                100,
                3000,
                50000,
                3,
                RewardType.GENERAL,
                null
        );

        Rewards reward = Rewards.create(command);
        setRewardId(reward, UUID.randomUUID());
        reward.addOptionList(options);

        for (int i = 0; i < reward.getOptionList().size(); i++) {
            setOptionId(reward.getOptionList().get(i), UUID.randomUUID());
        }

        return reward;
    }

    /**
     * 필수 옵션 있는 리워드 생성
     */
    public static Rewards createRewardWithRequiredOption() {
        List<CreateRewardOptionCommand> options = List.of(
                new CreateRewardOptionCommand("블랙", 0L, 15, true, 1),
                new CreateRewardOptionCommand("화이트", 5000L, 25, true, 2)
        );

        CreateRewardCommand command = new CreateRewardCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "커스텀 후드",
                "필수 옵션이 있는 상품입니다",
                80000L,
                100,
                5000,
                null,
                2,
                RewardType.GENERAL,
                null
        );

        Rewards reward = Rewards.create(command);
        setRewardId(reward, UUID.randomUUID());
        reward.addOptionList(options);

        for (int i = 0; i < reward.getOptionList().size(); i++) {
            setOptionId(reward.getOptionList().get(i), UUID.randomUUID());
        }

        return reward;
    }

    /**
     * 옵션 없는 재고 예약 커맨드 생성
     */
    public static StockReserveCommand createCommandWithoutOption(
            UUID fundingId,
            UUID rewardId,
            Integer quantity
    ) {
        UUID userId = UUID.randomUUID();
        List<StockReserveCommand.StockReserveItemCommand> items = List.of(
                new StockReserveCommand.StockReserveItemCommand(rewardId, null, quantity)
        );
        return new StockReserveCommand(userId, fundingId, items);
    }

    /**
     * 옵션 있는 재고 예약 커맨드 생성
     */
    public static StockReserveCommand createCommandWithOption(
            UUID fundingId,
            UUID rewardId,
            UUID optionId,
            Integer quantity
    ) {
        UUID userId = UUID.randomUUID();
        List<StockReserveCommand.StockReserveItemCommand> items = List.of(
                new StockReserveCommand.StockReserveItemCommand(rewardId, optionId, quantity)
        );
        return new StockReserveCommand(userId, fundingId, items);
    }

    /**
     * 여러 아이템 재고 예약 커맨드 생성
     */
    public static StockReserveCommand createCommandWithMultipleItems(
            UUID fundingId,
            UUID rewardId1, UUID optionId1, Integer quantity1,
            UUID rewardId2, UUID optionId2, Integer quantity2
    ) {
        UUID userId = UUID.randomUUID();
        List<StockReserveCommand.StockReserveItemCommand> items = List.of(
                new StockReserveCommand.StockReserveItemCommand(rewardId1, optionId1, quantity1),
                new StockReserveCommand.StockReserveItemCommand(rewardId2, optionId2, quantity2)
        );
        return new StockReserveCommand(userId, fundingId, items);
    }

    private static void setRewardId(Rewards reward, UUID id) {
        try {
            Field idField = Rewards.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(reward, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set reward id", e);
        }
    }

    private static void setOptionId(com.nowayback.reward.domain.reward.entity.RewardOptions option, UUID id) {
        try {
            Field idField = com.nowayback.reward.domain.reward.entity.RewardOptions.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(option, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set option id", e);
        }
    }
}