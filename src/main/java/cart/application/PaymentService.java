package cart.application;

import cart.domain.DeliveryPolicy;
import cart.domain.DiscountPolicy;
import cart.domain.Order;
import cart.domain.Payment;
import cart.repository.DeliveryPolicyRepository;
import cart.repository.DiscountPolicyRepository;
import java.util.List;

public class PaymentService {

    private final DeliveryPolicyRepository deliveryPolicyRepository;
    private final DiscountPolicyRepository discountPolicyRepository;

    public PaymentService(DeliveryPolicyRepository deliveryPolicyRepository,
                          DiscountPolicyRepository discountPolicyRepository) {
        this.deliveryPolicyRepository = deliveryPolicyRepository;
        this.discountPolicyRepository = discountPolicyRepository;
    }

    // TODO: 2023/05/31 Custom Exception 만들기
    public Payment createDraftPayment(Order order) {
        DiscountPolicy discountPolicy = discountPolicyRepository.findDefault().orElseThrow();
        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findDefault().orElseThrow();
        return Payment.of(order, List.of(discountPolicy), deliveryPolicy);
    }
}
