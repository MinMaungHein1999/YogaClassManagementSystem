package com.yogiBooking.common.dto;

public record FileUploadRequestDto(
        byte[] bytes,
        String filename,
        long fileSize,
        String contentType,
        String extension
) {
}
