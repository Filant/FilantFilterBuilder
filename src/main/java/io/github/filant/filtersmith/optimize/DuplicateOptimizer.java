package io.github.filant.filtersmith.optimize;

import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.report.ReportBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class DuplicateOptimizer implements RuleOptimizer {
  @Override
  public List<Rule> optimize(Collection<Rule> rules, ReportBuilder report) {
    Set<Rule> unique = new LinkedHashSet<>();
    for (Rule rule : rules) {
      if (!unique.add(rule)) {
        report.addDuplicateRule();
      }
    }
    return new ArrayList<>(unique);
  }
}

