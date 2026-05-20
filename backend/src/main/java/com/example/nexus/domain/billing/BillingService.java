package com.example.nexus.domain.billing;

import com.example.nexus.domain.billing.model.Billing;
import com.example.nexus.domain.billing.model.BillingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BillingService {

    private final BillingRepository billingRepository;

    public void saveUserBillingInfo(BillingDto.UserReqDto dto) {
        Billing billing = dto.toEntity();
        billingRepository.save(billing);
    }
}
