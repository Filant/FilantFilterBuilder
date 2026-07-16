package io.github.filant.filtersmith.parse.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.filant.filtersmith.lexer.Token;
import io.github.filant.filtersmith.lexer.TokenType;
import io.github.filant.filtersmith.model.DomainRule;
import io.github.filant.filtersmith.model.ExceptionRule;
import io.github.filant.filtersmith.parse.DomainRuleParser;
import org.junit.jupiter.api.Test;

class DomainRuleParserTest {
  private final DomainRuleParser parser = new DomainRuleParser();

  @Test
  void parsesAdguardDomainRule() {
    assertThat(parser.parse(rule("||Ads.Example.COM^")).get())
        .isEqualTo(new DomainRule("ads.example.com"));
  }

  @Test
  void parsesAdguardExceptionRule() {
    assertThat(parser.parse(rule("@@||safe.example.com^")).get())
        .isEqualTo(new ExceptionRule("safe.example.com"));
  }

  @Test
  void parsesHostsRule() {
    assertThat(parser.parse(rule("0.0.0.0 tracker.example.com")).get())
        .isEqualTo(new DomainRule("tracker.example.com"));
  }

  @Test
  void parsesDnsmasqRule() {
    assertThat(parser.parse(rule("address=/analytics.example.com/0.0.0.0")).get())
        .isEqualTo(new DomainRule("analytics.example.com"));
  }

  @Test
  void extractsUrlHost() {
    assertThat(parser.parse(rule("https://bad.example.com/path?q=1")).get())
        .isEqualTo(new DomainRule("bad.example.com"));
  }

  private Token rule(String text) {
    return new Token(TokenType.RULE, text, 1);
  }
}

