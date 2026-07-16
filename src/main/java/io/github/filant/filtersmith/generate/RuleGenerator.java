package io.github.filant.filtersmith.generate;

import io.github.filant.filtersmith.model.DomainRule;
import io.github.filant.filtersmith.model.ExceptionRule;
import io.github.filant.filtersmith.model.Rule;

public final class RuleGenerator {
  public String generate(Rule rule, OutputFormat format) {
    return switch (format) {
      case ADGUARD -> generateAdguard(rule);
      case HOSTS ->
          rule instanceof DomainRule ? "0.0.0.0 " + rule.domain() : "# @@ " + rule.domain();
      case PLAIN -> rule instanceof ExceptionRule ? "@@" + rule.domain() : rule.domain();
    };
  }

  private String generateAdguard(Rule rule) {
    String prefix = rule instanceof ExceptionRule ? "@@||" : "||";
    return prefix + rule.domain() + "^";
  }
}

