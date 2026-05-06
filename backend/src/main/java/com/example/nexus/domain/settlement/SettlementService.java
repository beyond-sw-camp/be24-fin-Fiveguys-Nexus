package com.example.nexus.domain.settlement;

import com.example.nexus.domain.head.HeadIncomeRepository;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final PaymentClient pg;
    private final HeadIncomeRepository headIncomeRepository;

    @Transactional
    public void verify(AuthUserDetails authUserDetails, SettlementDto.VerifyReq dto) {
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
}
