package com.yogiBooking.common.service;

import com.yogiBooking.common.controller.UserController;
import com.yogiBooking.common.entity.PackageCreditHistory;
import com.yogiBooking.common.entity.YogaClass;
import com.yogiBooking.common.entity.YogiPackage;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.repository.PackageCreditHistoryRepository;
import com.yogiBooking.common.repository.YogaClassRepository;
import com.yogiBooking.common.repository.YogiPackageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PackageCreditService {

    private final YogiPackageRepository yogiPackageRepository;
    private final PackageCreditHistoryRepository packageCreditHistoryRepository;
    private final YogaClassRepository yogaClassRepository;
    private static final Logger logger = LoggerFactory.getLogger(PackageCreditService.class);

    @Transactional
    public void refundCredit(Long yogiId, Long classId) {
        YogiPackage yogiPackage = yogiPackageRepository.findActiveByYogiIdAndYogaClassId(yogiId, classId)
                .orElseThrow(() -> new IllegalStateException("No active package found for yogiId: " + yogiId + " and classId: " + classId));

        YogaClass yogaClass = yogaClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Class with ID %d not found".formatted(classId)));
        double creditValue = yogaClass.getFeeOfCredit();

        if (creditValue <= 0) {
            logger.warn("Credit value for class {} is not positive (value: {}).  No refund.", classId, creditValue);
            return; // Or throw an exception, depending on your business rules
        }

        // 3. Increase the credits in the YogiPackage.
        yogiPackage.setCredit(yogiPackage.getCredit() + creditValue);
        yogiPackageRepository.save(yogiPackage);  //redundant?

        // 4. Record the credit refund in the history log.
        PackageCreditHistory creditHistory = new PackageCreditHistory();
        creditHistory.setYogiPackage(yogiPackage);
        creditHistory.setCreditChange(creditValue);
        creditHistory.setChangeReason("Class cancellation refund (Class ID: " + classId + ")");
        creditHistory.setChangeTime(LocalDateTime.now());
        packageCreditHistoryRepository.save(creditHistory);

        logger.info("Refunded {} credits to YogiPackage ID {} for Yogi ID {} and Class ID {}",
                creditValue, yogiPackage.getId(), yogiId, classId);
    }
}