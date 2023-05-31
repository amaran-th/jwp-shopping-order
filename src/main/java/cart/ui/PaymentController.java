package cart.ui;

import cart.application.OrderService;
import cart.application.PaymentService;
import cart.domain.Member;
import cart.dto.DiscountResponse;
import cart.dto.PaymentResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

//@RestController
public class PaymentController {

    private final OrderService orderService;

    private final PaymentService paymentService;


    public PaymentController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    //    @GetMapping("/total-cart-price")
    public ResponseEntity<PaymentResponse> getCartPrice(Member member, @RequestParam List<Long> cartItemIds) {
        return ResponseEntity.ok(new PaymentResponse(55_000,
                List.of(new DiscountResponse("5만원 이상 구매 시 10% 할인", 5_500)), 49_500, 3_500, 53_000));
    }

}
