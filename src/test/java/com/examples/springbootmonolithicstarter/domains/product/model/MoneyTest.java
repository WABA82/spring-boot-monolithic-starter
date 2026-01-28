package com.examples.springbootmonolithicstarter.domains.product.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Money 값 객체")
class MoneyTest {

    @Nested
    @DisplayName("생성")
    class Creation {

        @Test
        @DisplayName("양수 금액으로 생성할 수 있다")
        void createWithPositiveAmount() {
            Money money = Money.of(BigDecimal.valueOf(1000));

            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("0원으로 생성할 수 있다")
        void createWithZero() {
            Money money = Money.of(BigDecimal.ZERO);

            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("음수 금액으로 생성하면 예외가 발생한다")
        void createWithNegativeAmountThrowsException() {
            assertThatThrownBy(() -> Money.of(BigDecimal.valueOf(-1000)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("null로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> Money.of(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("연산")
    class Operations {

        @Test
        @DisplayName("두 금액을 더할 수 있다")
        void add() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(500);

            Money result = money1.add(money2);

            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        }

        @Test
        @DisplayName("금액을 뺄 수 있다")
        void subtract() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(300);

            Money result = money1.subtract(money2);

            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(700));
        }

        @Test
        @DisplayName("뺀 결과가 음수면 예외가 발생한다")
        void subtractResultingNegativeThrowsException() {
            Money money1 = Money.of(100);
            Money money2 = Money.of(500);

            assertThatThrownBy(() -> money1.subtract(money2))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("수량을 곱할 수 있다")
        void multiply() {
            Money money = Money.of(1000);

            Money result = money.multiply(3);

            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        }
    }

    @Nested
    @DisplayName("비교")
    class Comparison {

        @Test
        @DisplayName("같은 금액은 동등하다")
        void equalMoney() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(1000);

            assertThat(money1).isEqualTo(money2);
            assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
        }

        @Test
        @DisplayName("큰 금액을 비교할 수 있다")
        void isGreaterThan() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(500);

            assertThat(money1.isGreaterThan(money2)).isTrue();
            assertThat(money2.isGreaterThan(money1)).isFalse();
        }
    }
}
