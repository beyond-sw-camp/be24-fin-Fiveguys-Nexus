package com.example.nexus.domain.settlement;

import com.example.nexus.domain.head.HeadIncomeRepository;
import com.example.nexus.domain.head.HeadIncomeService;
import com.example.nexus.domain.head.model.HeadIncome;
import com.example.nexus.domain.settlement.model.Settlement;
import com.example.nexus.domain.settlement.model.SettlementDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.ToNumberPolicy;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final PaymentClient pg;
    private final HeadIncomeRepository headIncomeRepository;
    private final HeadIncomeService headIncomeService;

    @Transactional
    public void verify(AuthUserDetails authUserDetails, SettlementDto.VerifyReq dto, YearMonth lastMonth) {
        CompletableFuture<Payment> completableFuture = pg.getPayment(dto.getPaymentIdx());
        Payment payment = completableFuture.join();

        if (payment instanceof PaidPayment paidPayment) {
            Map<String, Object> customData = new GsonBuilder()
                    .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                    .create().fromJson(paidPayment.getCustomData(), Map.class);

            Long ordersIdx = Long.parseLong(customData.get("headIncomeIdx").toString());
            Settlement settlement = settlementRepository.findById(ordersIdx).orElseThrow();

            int totalPrice = 0;
            List<HeadIncome> headIncomeList = headIncomeRepository.findBySettlementIdx(settlement.getIdx());

            for(HeadIncome headIncome : headIncomeList) {
                totalPrice += headIncome.getPrice();
            }


            if (paidPayment.getAmount().getTotal() == totalPrice) {
                settlement.setPaid(true);
                settlement.setPgPaymentId(dto.getPaymentIdx());
                settlementRepository.save(settlement);
            }


        }
    }


    public Long pay(Long storeIdx, SettlementDto.PayReq dto) {
        YearMonth currentMonth = YearMonth.now();

        YearMonth lastMonth = currentMonth.minusMonths(1);

        String settlementYm = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Settlement settlement = Settlement.builder()
                .paid(false)
                .price(dto.getPaymentPrice())
                .storeIdx(storeIdx)
                .settlementYm(settlementYm)
                .build();


        settlementRepository.save(settlement);

        for (Long idx : dto.getHeadIncomeidxList()) {
            HeadIncome headIncome = headIncomeRepository.findById(idx).orElse(null);
            headIncome.setSettlementIdx(settlement.getIdx());
        }

        return settlement.getIdx();
    }

}
