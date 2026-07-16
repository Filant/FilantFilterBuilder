package io.github.filant.filtersmith.optimize;

import io.github.filant.filtersmith.model.DomainRule;
import io.github.filant.filtersmith.model.ExceptionRule;
import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.report.ReportBuilder;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class NormalizerOptimizer implements RuleOptimizer {
  @Override
  public List<Rule> optimize(Collection<Rule> rules, ReportBuilder report) {
    List<Rule> optimized = new ArrayList<>(rules.size());
    for (Rule rule : rules) {
      String normalized = normalize(rule.domain());
      if (!normalized.equals(rule.domain())) {
        report.addNormalizedRule();
      }
      optimized.add(
          rule instanceof ExceptionRule
              ? new ExceptionRule(normalized)
              : new DomainRule(normalized));
    }
    return optimized;
  }

  private String normalize(String domain) {
    String normalized = domain.trim().toLowerCase();
    while (normalized.endsWith(".")) {
      normalized = normalized.substring(0, normalized.length() - 1);
    }
    return IDN.toASCII(normalized, IDN.USE_STD3_ASCII_RULES);
  }
}

