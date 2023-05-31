package cart.repository;

import static cart.domain.OrderFixture.member;
import static cart.domain.ProductFixture.ANOTHER_PRODUCT;
import static cart.domain.ProductFixture.OTHER_PRODUCT;
import static cart.domain.ProductFixture.PRODUCT;

import cart.domain.CartItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartItemFakeRepository implements CartItemRepository {
    List<CartItem> cartItems = new ArrayList<>(
            List.of(
                    new CartItem(1L, 2, PRODUCT, member),
                    new CartItem(2L, 1, OTHER_PRODUCT, member),
                    new CartItem(3L, 3, ANOTHER_PRODUCT, member)
            )
    );

    @Override
    public Optional<CartItem> findById(Long id) {
        return cartItems.stream()
                .filter(cartItem -> cartItem.getId().equals(id))
                .findFirst();
    }


}