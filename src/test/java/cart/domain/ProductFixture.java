package cart.domain;

public class ProductFixture {
    public static final Product PRODUCT = new Product(null, "에코", Money.from(30_000_000), "imageUrl");
    public static final Product OTHER_PRODUCT = new Product(null, "로지", Money.from(10_000_000), "imageUrl");
    public static final Product ANOTHER_PRODUCT = new Product(null, "아마란스", Money.from(50_000_000), "imageUrl");
}
