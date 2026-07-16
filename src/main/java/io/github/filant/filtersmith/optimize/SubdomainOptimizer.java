package io.github.filant.filtersmith.optimize;

import io.github.filant.filtersmith.model.DomainRule;
import io.github.filant.filtersmith.model.ExceptionRule;
import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.report.ReportBuilder;
import io.github.filant.filtersmith.trie.ReverseDomainTrie;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SubdomainOptimizer implements RuleOptimizer {
  @Override
  public List<Rule> optimize(Collection<Rule> rules, ReportBuilder report) {
    ReverseDomainTrie trie = new ReverseDomainTrie();
    List<Rule> optimized = new ArrayList<>(rules.size());
    for (Rule rule : rules) {
      if (rule instanceof ExceptionRule) {
        optimized.add(rule);
        continue;
      }
      if (trie.hasAncestor(rule.domain())) {
        report.addRemovedSubdomain();
        continue;
      }
      trie.insert(rule.domain());
      optimized.add(new DomainRule(rule.domain()));
    }
    return optimized;
  }
}

