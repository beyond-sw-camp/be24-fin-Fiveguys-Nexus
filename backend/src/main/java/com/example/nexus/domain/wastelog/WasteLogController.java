package com.example.nexus.domain.wastelog;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.user.model.AuthUserDetails;
import com.example.nexus.domain.wastelog.model.WasteLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/wastelog")
@RestController
@RequiredArgsConstructor
public class WasteLogController {
    private final WasteLogService wasteLogService;

    // [가맹점주] POS 재고 lot 기준 폐기
    @PostMapping("/waste/pos")
    public ResponseEntity<BaseResponse<WasteLogDto.WasteRes>> createWasteFromPos(@AuthenticationPrincipal AuthUserDetails userDetails, @RequestBody WasteLogDto.PosWasteReq req) {
        WasteLogDto.WasteRes result = wasteLogService.createWaste(userDetails.getIdx(), req);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
