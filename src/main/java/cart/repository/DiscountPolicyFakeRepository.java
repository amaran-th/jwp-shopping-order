package cart.repository;

import cart.domain.DefaultDiscountPolicy;
import cart.domain.DiscountPolicy;
import java.util.List;
import java.util.Optional;

public class DiscountPolicyFakeRepository implements DiscountPolicyRepository {

    List<DiscountPolicy> discountPolicies = List.of(new DefaultDiscountPolicy(0.1));

    @Override
    public Optional<DiscountPolicy> findDefault() {
        return Optional.ofNullable(discountPolicies.get(0));
    }
}
