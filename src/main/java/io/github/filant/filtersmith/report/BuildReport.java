package io.github.filant.filtersmith.report;

public record BuildReport(
    long inputLines,
    long parsedRules,
    long duplicateRules,
    long normalizedRules,
    long removedSubdomains,
    long generatedRules) {}

