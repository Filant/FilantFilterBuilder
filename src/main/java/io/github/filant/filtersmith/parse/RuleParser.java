package io.github.filant.filtersmith.parse;

import io.github.filant.filtersmith.lexer.Token;
import io.github.filant.filtersmith.model.Rule;
import java.util.Optional;

public interface RuleParser {
  Optional<Rule> parse(Token token);
}

