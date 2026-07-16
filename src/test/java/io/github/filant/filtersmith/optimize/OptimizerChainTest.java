package io.github.filant.filtersmith.optimize.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.filant.filtersmith.model.DomainRule;
import io.github.filant.filtersmith.model.ExceptionRule;
import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.optimize.OptimizerChain;
import io.github.filant.filtersmith.report.BuildReport;
import io.github.filant.filtersmith.report.ReportBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;

class OptimizerChainTest {
  @Test
  void normalizesDeduplicatesAndRemovesCoveredSubdomains() {
    ReportBuilder report = new ReportBuilder();
    List<Rule> optimized =
        OptimizerChain.standard()
            .optimize(
                List.of(
                    new DomainRule("Example.COM."),
                    new DomainRule("example.com"),
                    new DomainRule("ads.example.com"),
                    new ExceptionRule("safe.example.com")),
                report);

    assertThat(optimized)
        .containsExactly(new DomainRule("example.com"), new ExceptionRule("safe.example.com"));

    BuildReport buildReport = report.build();
    assertThat(buildReport.normalizedRules()).isEqualTo(1);
    assertThat(buildReport.duplicateRules()).isEqualTo(1);
    assertThat(buildReport.removedSubdomains()).isEqualTo(1);
  }
}

