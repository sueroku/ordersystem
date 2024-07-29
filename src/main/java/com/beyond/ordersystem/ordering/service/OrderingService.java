package com.beyond.ordersystem.ordering.service;

import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.repository.MemberRepository;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.domain.OrderStatus;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.beyond.ordersystem.ordering.dto.OrderingSaveReqDto;
import com.beyond.ordersystem.ordering.repository.OrderDetailRepository;
import com.beyond.ordersystem.ordering.repository.OrderingRepository;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    private final OrderDetailRepository orderDetailRepository; // 없어도 되는 겁니다. 없다고 생각하세용

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Transactional
    public Ordering orderCreate(List<OrderingSaveReqDto> dtos){
////        방법1.쉬운방식
////        Ordering생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("없음"));
//        Ordering ordering = orderingRepository.save(dto.toEntity(member));
//
////        OrderDetail생성 : order_id, product_id, quantity
//        for(OrderingSaveReqDto.OrderDetailDto orderDto : dto.getOrderDetailList()){
//            Product product = productRepository.findById(orderDto.getProductId()).orElse(null);
//            int quantity = orderDto.getProductCount();
//            OrderDetail orderDetail =  OrderDetail.builder()
//                    .product(product)
//                    .quantity(quantity)
//                    .ordering(ordering)
//                    .build();
//            orderDetailRepository.save(orderDetail);
//        }
//
//        return ordering;

//        // 방법2. JPA에 최적화한 방식
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("회원이 없습니다.")); // 이제는 필요없어용
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        String memberRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities(); // 꺼낼일 없다. 지워라.
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(()->new EntityNotFoundException("회원이 없습니다."));
        Ordering ordering = new Ordering().builder()
                .member(member).build();
        for(OrderingSaveReqDto odd : dtos){
            Product product = productRepository.findById(odd.getProductId()).orElseThrow(()->new EntityNotFoundException("상품이 없습니다"));
            if(product.getStockQuantity() < odd.getProductCount()){
                throw new IllegalArgumentException(product.getId()+" "+product.getName()+" "+"재고부족");
            }
            product.updateStockQuantity(odd.getProductCount()); // 변경감지(dirty checking)로 인해 별도의 save 불필요(jpa가 알아서 해준다.)
            OrderDetail orderDetail = OrderDetail.builder()
                .ordering(ordering)
                .product(product).quantity(odd.getProductCount()).build();
            ordering.getOrderDetails().add(orderDetail); // 불러다가 넣어
        }

        Ordering savedOrdering = orderingRepository.save(ordering);
        return savedOrdering;

    }

    public Page<OrderListResDto> orderList(Pageable pageable){
        Page<Ordering> orderings = orderingRepository.findAll(pageable);
        return orderings.map(a->a.listFromEntity()); // List로 만든다면 for문으로 만들어죵.. 어 근데 걔도 맵가능하지 않나..
    }

    public Page<OrderListResDto> myorderList(Pageable pageable){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("회원이 없습니다."));
        Page<Ordering> orderings = orderingRepository.findByMemberId(member.getId(),pageable); // Page<Ordering> orderings = orderingRepository.findByMember(member,pageable);
        return orderings.map(a->a.listFromEntity()); // List로 만든다면 for문으로 만들어죵.. 어 근데 걔도 맵가능하지 않나..
    }

    @Transactional
    public OrderListResDto orderCancel(Long id){
        Ordering ordering = orderingRepository.findById(id).orElseThrow(()->new EntityNotFoundException("주문이 없습니다."));
        ordering.updateOrderStatus(OrderStatus.CANCELD);
//        orderingRepository.save(ordering);
        return ordering.listFromEntity();
    }
}










// 크리에이트
////        // 방법2. JPA에 최적화한 방식
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("회원이 없습니다."));
//Ordering ordering = new Ordering().builder()
//        .member(member).build();
//
////        List<OrderDetail> orderDetailList = new ArrayList<>(); // 안만들고
//        for(OrderingSaveReqDto.OrderDetailDto odd : dto.getOrderDetailList()){
//Product product = productRepository.findById(odd.getProductId()).orElseThrow(()->new EntityNotFoundException("상품이 없습니다"));
//            if(product.getStockQuantity() < odd.getProductCount()){
//        throw new IllegalArgumentException(product.getId()+" "+product.getName()+" "+"재고부족");
//        }
//        product.updateStockQuantity(odd.getProductCount()); // 변경감지(dirty checking)로 인해 별도의 save 불필요(jpa가 알아서 해준다.)
//OrderDetail orderDetail = OrderDetail.builder()
//        .ordering(ordering)
//        .product(product).quantity(odd.getProductCount()).build();
//            ordering.getOrderDetails().add(orderDetail); // 불러다가 넣어
//        }
//
//Ordering savedOrdering = orderingRepository.save(ordering);
//        return savedOrdering;