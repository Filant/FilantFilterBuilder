package io.github.filant.filtersmith.report;

public final class ReportBuilder {
  private long inputLines;
  private long parsedRules;
  private long duplicateRules;
  private long normalizedRules;
  private long removedSubdomains;
  private long generatedRules;

  public void addInputLine() {
    inputLines++;
  }

  public void addParsedRule() {
    parsedRules++;
  }

  public void addDuplicateRule() {
    duplicateRules++;
  }

  public void addNormalizedRule() {
    normalizedRules++;
  }

  public void addRemovedSubdomain() {
    removedSubdomains++;
  }

  public void setGeneratedRules(long generatedRules) {
    this.generatedRules = generatedRules;
  }

  public BuildReport build() {
    return new BuildReport(
        inputLines,
        parsedRules,
        duplicateRules,
        normalizedRules,
        removedSubdomains,
        generatedRules);
  }
}

