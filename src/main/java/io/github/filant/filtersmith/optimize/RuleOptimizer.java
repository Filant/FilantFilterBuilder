package io.github.filant.filtersmith.optimize;

import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.report.ReportBuilder;
import java.util.Collection;
import java.util.List;

public interface RuleOptimizer {
  List<Rule> optimize(Collection<Rule> rules, ReportBuilder report);
}

