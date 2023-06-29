![image](https://user-images.githubusercontent.com/81465068/219571225-27face3f-67dd-4264-85a6-de09b521e7f5.png)

# 우테코 5기 2레벨 7~8주차 - 장바구니(협업) 미션

![Generic badge](https://img.shields.io/badge/level2-week7~8-green.svg)
![Generic badge](https://img.shields.io/badge/version-1.0.1-brightgreen.svg)

> 우아한테크코스 5기 2레벨 7~8주차 미션, 장바구니(협업) 미션을 구현한 저장소입니다.


# 목차
- [도메인 요소의 구성 관계](#도메인-요소의-구성-관계)
- [DB(DAO)](#dbdao)
- [기능 목록](#기능-목록)
  - [체크된 장바구니 상품 가격 조회](#체크된-장바구니-상품-가격-조회)
  - [주문하기 (주문서 생성)](주문하기-주문서-생성)
  - [주문 목록 조회](#주문-목록-조회)
  - [주문 내역 상세 조회](#주문-내역-상세-조회)

## 도메인 요소의 구성 관계
- 클래스 다이어그램
```mermaid
classDiagram

    class CartItem {
        Long id
        int quantity
        Product product
        Member member
    }
    class Product {
        Long id
        String name
        Money price
        String imageUrl
    }

    class OrderItem {
        Long id
        String name
        String imageUrl
        int quantity
        Money totalPrice
    }

    class Order {
        Long id
        Member member
        OrderItem[] orderItems
        LocalDateTime orderTime
    }
    class Payment
    class PaymentRecord {
        Order order
        Money originalTotalPrice
        Map[DiscountPolicy, Money] policyToDiscountAmounts
        Map[DeliveryPolicy, Money] policyToDeliveryFees
    }

    class DiscountPolicy {
        <<interface>>
        + Money calculateDiscountAmount(Order order)
    }
    class DefaultDiscountPolicy {
        long id
        String name
        Money threshold
        double discountRate
        + Money calculateDiscountAmount(Order order)
    }

    class DeliveryPolicy {
        <<interface>>
        + Money calculateDeliveryFee(Order order)
    }
    class DefaultDeliveryPolicy {
        long id
        String name
        + Money calculateDeliveryFee(Order order)
    }

    Product --o CartItem
    OrderItem ..> CartItem
    OrderItem --o Order
    DiscountPolicy --o PaymentRecord
    DeliveryPolicy --o PaymentRecord
    DiscountPolicy ..> Order
    DeliveryPolicy ..> Order
    Order --o PaymentRecord
    DefaultDeliveryPolicy ..|> DeliveryPolicy
    DefaultDiscountPolicy ..|> DiscountPolicy
```

## DB(DAO)

- In-Memory DB(MySQL)를 사용한다.
- DB 테이블 설계
    - Database 명 : `amaranthdb`
        ![wtc_jwp-shopping-order](https://github.com/amaran-th/jwp-shopping-order/assets/81465068/d421aa6f-b036-46ac-b686-392902aea4b7)



## 기능 목록

### 체크된 장바구니 상품 가격 조회
```mermaid
    sequenceDiagram
        autonumber
        actor A as client
        participant B as PaymentController
        participant C as PaymentService
    		participant D as Repository(도메인 별로 있지만 하나로 표현)
        A ->> B: 가격 정보 조회 API 요청
        B ->> C: 예비 주문서(DraftPaymentRecord) 생성 서비스 호출
    		C ->> D: CartItem 조회
    		C ->> C: CartItem을 OrderItem으로 변환한 후 Order 객체 생성
    		C ->> D: 전체 할인/배송비정책 조회
    		C ->> C: Order와 정책들의 정보를 종합하여 PaymentRecord 객체 생성
        C ->> B: PaymentRecord 객체 반환
    		B ->> A: PaymentRecord를 PaymentResponse로 매핑 후 반환
```
- Request

  ```
  GET /total-cart-price?cartItemIds=1&cartItemIds=2&cartItemIds=3 HTTP/1.1
  Authorization: Basic 12oirfhgaq023984u0r0475
  ```

- Response

  ```
  {
  	"originalPrice": number, // 상품들의 주문 가격
  
    // 할인 정책, 할인율, 적용시 가격을 담은 객체의 배열
  	"discounts": [{
  		"discountPolicy": string,
  			"discountAmount":number
  	 }],
  
    // 정책이 모두 적용된 총 가격ca
  	"discountedPrice":number,
  	"deliveryFee":number,
  
    // 배송비 + 물건 총 가격
  	"finalPrice":number
  }
  ```

### 주문하기 (주문서 생성)
```mermaid
    sequenceDiagram
        autonumber
        actor A as client
        participant B as OrderController
        participant C as OrderService
        participant D as PaymentService
    		participant E as Repository(도메인 별로 있지만 하나로 표현)
    		participant F as database
        A ->> B: 주문하기 API 요청
        B ->> C: Order 생성 서비스 호출
    		C ->> E: CartItem 조회 요청
    		C ->> C: CartItem을 OrderItem으로 변환한 후 Order 객체 생성
        C ->> E: Order 객체 저장 요청
        E ->> F: OrderItem, Order 엔티티 저장
    		C ->> E: CartItem 객체 삭제 요청
    		E ->> F: CartItem 엔티티 삭제
        C ->> C: 저장된 Order 엔티티의 Id를 포함한 Order 객체 생성
        B ->> D: PaymentRecord 생성 서비스 호출
    		D ->> E: 전체 할인/배송비정책 조회
    		D ->> D: Order와 정책들의 정보를 종합하여 PaymentRecord 객체 생성
        D ->> E: PaymentRecord 객체 저장
    		E ->> F: PaymentRecord, AppliedDisountPolicy, AppliedDeliveryPolicy 엔티티 저장
        B ->> A: 생성된 Order의 Id 반환
```
- Request

  ```
  POST /orders HTTP/1.1
  Authorization: Basic ${credentials}
  
  {
  	"cartItemIds": [1, 2, 3]
  }
  ```

- Response

  ```
  HTTP/1.1 201 CREATED
  Content-Type: application/json
  Location: /orders/{orderId}
  ```

### 주문 목록 조회
```mermaid
    sequenceDiagram
        autonumber
        actor A as client
        participant B as OrderController
        participant C as OrderService
    		participant D as Repository(도메인 별로 있지만 하나로 표현)
        A ->> B: 주문 목록 조회 API 요청
        B ->> C: Order 목록 조회 서비스 호출
        C ->> D: Member가 가진 Order 리스트 조회 요청
        D ->> B: 저장된 Order 리스트 반환
        
        B ->> A: Order 리스트를 OrderResponse 리스트로 매핑 후 반환
```
- Request

  ```
  GET /orders HTTP/1.1
  Authorization: Basic ${credentials}
  ```

- Response

  ```
  HTTP/1.1 200 OK
  Content-Type: application/json
  [
      {
          "id":1,
          "orderTime": 2023-05-25T18:25:43.511Z, //ISO8601 형식
          "productList":[
             {
  							"name": "뽀로로 튼튼한 성장기 어린이 음료 235mL",
  							"totalPrice": 2550,
  							"quantity": 3,
  							"imageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSrv1JEzSxNjrQgR2VcpDw5wUJV4_RiJEwRb-gn2-Q&s"
  					},
  					{
  						"name": "데자와 민트초코 밀크티 ",
  						"totalPrice": 50000,
  						"quantity": 4,
  		        "imageUrl": "https://image-cdn.hypb.st/https%3A%2F%2Fkr.hypebeast.com%2Ffiles%2F2020%2F11%2Ftejava-mint-chocolate-milk-tea-2020-release-info-1.jpg?q=90&w=1400&cbr=1&fit=max"
  					},
          ]
      },
      {
          "id":2,
          "orderTime": 2023-05-26T18:25:43.511Z,
          "productList":[
              {
                  "name":"차",
                  "totalPrice":190000,
                  "quantity":3,
  								"imageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSrv1JEzSxNjrQgR2VcpDw5wUJV4_RiJEwRb-gn2-Q&s"
              },
              {
                  "name":"자동차",
                  "totalPrice":210000,
                  "quantity":4,
  								"imageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSrv1JEzSxNjrQgR2VcpDw5wUJV4_RiJEwRb-gn2-Q&s"
              },
          ]
      }
  ]
  ```

### 주문 내역 상세 조회
```mermaid
    sequenceDiagram
        autonumber
        actor A as client
        participant B as OrderController
        participant C as OrderService
        participant D as PaymentService
    		participant E as Repository
    		participant F as database
        A ->> B: 주문 상세 내역 조회 API 요청
        B ->> C: Order 조회 서비스 호출
    		C ->> E: Order 객체 불러오기
        E ->> F: OrderItem, Order 엔티티 불러오기
        E ->> B: 저장된 Order 객체 반환
        B ->> D: PaymentRecord 조회 서비스 호출
        D ->> E: PaymentRecord 객체 불러오기
    		E ->> F: order id로 PaymentRecord, AppliedDisountPolicy, AppliedDeliveryPolicy 엔티티 불러오기
        E ->> B: 저장된 PaymentRecord 객체를 반환
        B ->> A: PaymentRecord 객체를 OrderDetailResponse로 매핑 후 반환
```
- Request

  ```
  GET /orders/{orderId} HTTP/1.1
  Authorization: Basic ${credentials}
  ```

- Response

  ```
  HTTP/1.1 200 OK
  Content-Type: application/json
  
  {
  	"id":1,
  	"productList":[
  		{
  			"name":"차",
  			"totalPrice":190000,
  			"quantity":3,
        "imageUrl": "https://image-cdn.hypb.st/https%3A%2F%2Fkr.hypebeast.com%2Ffiles%2F2020%2F11%2Ftejava-mint-chocolate-milk-tea-2020-release-info-1.jpg?q=90&w=1400&cbr=1&fit=max"
  		},
  	],
  	"paymentAmount":{
  		"originalPrice": number, // 상품들의 주문 가격
  	
  	  // 할인 정책, 할인율, 적용시 가격을 담은 객체의 배열
  		"discounts": [
  			{
  				"discountPolicy": string,
  				"discountAmount":number
  			 },
  		],
  	
  	  // 정책이 모두 적용된 총 가격
  		"discountedPrice":number,
  		"deliveryFee":number,
  	
  	  // 배송비 + 물건 총 가격
  			"finalPrice":number
  	}
  }
  
  ```


# 2단계 기능 목록

- 장바구니에서 선택한 상품들의 예상 결제 금액을 확인할 수 있다.
- 장바구니에 담은 상품을 주문할 수 있다.
    - 최소 1개 이상의 장바구니 상품을 주문할 수 있다.
    - 주문을 완료한 장바구니 상품은 장바구니에서 삭제된다.
- 할인 정책을 적용할 수 있다.
    - 할인 정책은 기준 금액과 할인율을 가지고 있다.
        - 할인율은 100% 이하여야한다.
    - 기준 금액 이상 주문 시 전체 금액에서 할인율만큼 할인된다.

      ex) 5만원 이상 주문 시 전체 금액에서 10% 할인이 된다.

    - 적용될 수 있는 할인 정책이 여러 개인 경우 할인율이 가장 높은 정책을 적용한다.
- 사용자 별로 주문 목록을 확인할 수 있다.
    - 최신순으로 주문 목록을 확인할 수 있다.
    - 주문별로 주문번호, 상품별 정보(이름, 수량, 수량이 반영된 가격, 이미지) 를 확인할 수 있다.
- 특정 주문의 상세 정보를 확인할 수 있다.
    - 주문 번호, 상품별 정보와 결제 금액 정보를 확인할 수 있다.
        - 결제 금액 정보는 주문 금액, 할인 금액, 배송비 정보를 포함한다.
