package dev.pcrykh.util;

public record TokenContext(
        String subject,
        String subjectId,
        int tierIndex,
        String tier,
        int count,
        int ap
) {
}
