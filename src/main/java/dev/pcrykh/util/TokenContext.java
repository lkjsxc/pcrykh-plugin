package dev.pcrykh.util;

public record TokenContext(
    String subject,
    String subjectId,
    String tier,
    int tierIndex,
    int count,
    int ap
) {}
