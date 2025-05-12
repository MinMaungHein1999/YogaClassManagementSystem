package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.yogiPackage.MasterYogiPackageDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageCreateDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageUpdateDTO;
import com.yogiBooking.common.entity.Yogi;
import com.yogiBooking.common.entity.YogiPackage;
import com.yogiBooking.common.entity.constants.PackageStatus;
import com.yogiBooking.common.exception.PaymentFailedException;
import com.yogiBooking.common.exception.VerificationEmailFailedException;
import com.yogiBooking.common.mapper.YogiPackageMapper;
import com.yogiBooking.common.repository.YogiPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final YogiPackageRepository yogiPackageRepository;

    private boolean addPaymentCard(PaymentCardRequest paymentCardRequest) {
        if (paymentCardRequest.cardNumber().equals("1234-5678-9012-3456")) {
            return false;
        }
        return true;
    }

    private boolean paymentCharge(PaymentChargeRequest paymentChargeRequest) {
        if (paymentChargeRequest.amount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (paymentChargeRequest.cardNumber().equals("9999-9999-9999-9999")) {
            return false;
        }
        return true;
    }

    private boolean sendVerifyEmail(String email) {
        if (email.contains("invalid")) {
            return false;
        }
        return true;
    }

    public PaymentResult handlePaymentAndVerification(MasterYogiPackageDTO yogiPackageDTO, Yogi yogi, YogiPackageMapper yogiPackageMapper) {

        PaymentCardRequest paymentCardRequest = new PaymentCardRequest(yogiPackageDTO.getBankCardNumber());
        if (!addPaymentCard(paymentCardRequest)) {
            throw new PaymentFailedException("Failed to add payment card.");
        }

        PaymentChargeRequest paymentChargeRequest = new PaymentChargeRequest(yogiPackageDTO.getAmountOfCredit(), yogiPackageDTO.getBankCardNumber());
        if (!paymentCharge(paymentChargeRequest)) {
            throw new PaymentFailedException("Payment processing failed for the package purchase.");
        }

        YogiPackage yogiPackage;

        if (yogiPackageDTO instanceof YogiPackageCreateDTO createDTO) {
            yogiPackage = yogiPackageMapper.toEntity(createDTO);
        } else if (yogiPackageDTO instanceof YogiPackageUpdateDTO updateDTO) {
            yogiPackage = yogiPackageMapper.toEntity(updateDTO);
        } else {
            throw new IllegalArgumentException("Unsupported DTO type: " + yogiPackageDTO.getClass());
        }

        yogiPackage.setYogi(yogi);
        yogiPackage.setVerificationEmailSentAt(LocalDateTime.now());
        YogiPackage savedYogiPackage = yogiPackageRepository.save(yogiPackage);
        String yogiEmail = yogi.getLoginUser().getEmail();
        if ( yogiEmail != null) {
            if (!sendVerifyEmail(yogiEmail)) {
                System.err.println("Warning: Failed to send verification email for  package.");
                savedYogiPackage.setVerificationEmailSentAt(null);
                savedYogiPackage.setPackageStatus(PackageStatus.PAYMENT_FAIL);
                yogiPackageRepository.save(savedYogiPackage);
                throw new VerificationEmailFailedException("Failed to send verification email."); // Or not, depending on your business logic
            } else {
                savedYogiPackage.setVerificationEmailSentAt(LocalDateTime.now());
                savedYogiPackage.setPackageStatus(PackageStatus.ACTIVE);
                yogiPackageRepository.save(savedYogiPackage);
            }
        }
        return new PaymentResult(savedYogiPackage);
    }
}

record PaymentResult(YogiPackage savedYogiPackage) {
}

record PaymentCardRequest(String cardNumber) {
}

record PaymentChargeRequest(double amount, String cardNumber) {
}
