package cart.application;

import static cart.domain.OrderFixture.order;
import static org.assertj.core.api.Assertions.assertThat;

import cart.domain.Money;
import cart.domain.Payment;
import cart.repository.DeliveryPolicyFakeRepository;
import cart.repository.DiscountPolicyFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(new DeliveryPolicyFakeRepository(), new DiscountPolicyFakeRepository());
    }

    @Test
    @DisplayName("Order 객체를 받아서 Payment 생성할 수 있다.")
    void createDraftPayment() {
        //given
        Payment expected = new Payment(Money.from(55000), Money.from(5500), Money.from(3500));
        //when
        Payment actual = paymentService.createDraftPayment(order);
        //then

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }
}