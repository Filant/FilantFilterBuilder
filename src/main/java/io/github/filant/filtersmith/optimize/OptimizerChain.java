package io.github.filant.filtersmith.optimize;

import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.report.ReportBuilder;
import java.util.Collection;
import java.util.List;

public final class OptimizerChain {
  private final List<RuleOptimizer> optimizers;

  public OptimizerChain(List<RuleOptimizer> optimizers) {
    this.optimizers = List.copyOf(optimizers);
  }

  public List<Rule> optimize(Collection<Rule> rules, ReportBuilder report) {
    Collection<Rule> current = rules;
    for (RuleOptimizer optimizer : optimizers) {
      current = optimizer.optimize(current, report);
    }
    return List.copyOf(current);
  }

  public static OptimizerChain standard() {
    return new OptimizerChain(
        List.of(new NormalizerOptimizer(), new DuplicateOptimizer(), new SubdomainOptimizer()));
  }
}

