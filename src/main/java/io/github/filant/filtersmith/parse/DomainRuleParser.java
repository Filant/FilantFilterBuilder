package io.github.filant.filtersmith.parse;

import io.github.filant.filtersmith.lexer.Token;
import io.github.filant.filtersmith.lexer.TokenType;
import io.github.filant.filtersmith.model.DomainRule;
import io.github.filant.filtersmith.model.ExceptionRule;
import io.github.filant.filtersmith.model.Rule;
import java.net.IDN;
import java.util.Optional;

public final class DomainRuleParser implements RuleParser {
  @Override
  public Optional<Rule> parse(Token token) {
    if (token.type() != TokenType.RULE) {
      return Optional.empty();
    }
    String value = stripInlineComment(token.text().trim());
    if (value.isEmpty()) {
      return Optional.empty();
    }
    boolean exception = value.startsWith("@@");
    if (exception) {
      value = value.substring(2);
    }
    Optional<String> domain = extractDomain(value);
    if (domain.isEmpty() || !looksLikeDomain(domain.get())) {
      return Optional.empty();
    }
      String normalized;
      try {
          normalized = normalizeDomain(domain.get());
      } catch (IllegalArgumentException ex) {
          return Optional.empty();
      }
      if (normalized.isEmpty()) {
          return Optional.empty();
      }
    return Optional.of(exception ? new ExceptionRule(normalized) : new DomainRule(normalized));
  }

  private String stripInlineComment(String value) {
    int hash = value.indexOf('#');
    if (hash >= 0) {
      value = value.substring(0, hash).trim();
    }
    return value;
  }

  private Optional<String> extractDomain(String value) {
    if (value.startsWith("||")) {
      int end = findAdblockDomainEnd(value, 2);
      return Optional.of(value.substring(2, end));
    }
    if (value.startsWith("|http://")) {
      return hostFromUrl(value.substring(1));
    }
    if (value.startsWith("|https://")) {
      return hostFromUrl(value.substring(1));
    }
    if (value.startsWith("http://") || value.startsWith("https://")) {
      return hostFromUrl(value);
    }
    if (value.startsWith("address=/")) {
      return extractDnsmasqAddress(value);
    }
    Optional<String> hostsDomain = extractHostsDomain(value);
    return hostsDomain.isPresent() ? hostsDomain : Optional.of(value);
  }

  private int findAdblockDomainEnd(String value, int start) {
    int index = start;
    while (index < value.length()) {
      char current = value.charAt(index);
      if (current == '^' || current == '/' || current == '$') {
        break;
      }
      index++;
    }
    return index;
  }

  private Optional<String> hostFromUrl(String value) {
    int scheme = value.indexOf("://");
    if (scheme < 0) {
      return Optional.empty();
    }
    int start = scheme + 3;
    int end = value.length();
    for (int i = start; i < value.length(); i++) {
      char current = value.charAt(i);
      if (current == '/' || current == ':' || current == '?' || current == '#') {
        end = i;
        break;
      }
    }
    return end > start ? Optional.of(value.substring(start, end)) : Optional.empty();
  }

  private Optional<String> extractDnsmasqAddress(String value) {
    int start = "address=/".length();
    int end = value.indexOf('/', start);
    if (end <= start) {
      return Optional.empty();
    }
    return Optional.of(value.substring(start, end));
  }

  private Optional<String> extractHostsDomain(String value) {
    int firstSpace = firstWhitespace(value);
    if (firstSpace < 0) {
      return Optional.empty();
    }
    String address = value.substring(0, firstSpace);
    if (!address.equals("0.0.0.0") && !address.equals("127.0.0.1") && !address.equals("::")) {
      return Optional.empty();
    }
    String rest = value.substring(firstSpace).trim();
    int nextSpace = firstWhitespace(rest);
    return Optional.of(nextSpace >= 0 ? rest.substring(0, nextSpace) : rest);
  }

  private int firstWhitespace(String value) {
    for (int i = 0; i < value.length(); i++) {
      if (Character.isWhitespace(value.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  private boolean looksLikeDomain(String value) {
    return value.indexOf('.') > 0 && value.indexOf('*') < 0;
  }

  private String normalizeDomain(String value) {
    String domain = value.trim().toLowerCase();
    while (domain.endsWith(".")) {
      domain = domain.substring(0, domain.length() - 1);
    }
    if (domain.isEmpty()) {
      return domain;
    }
    return IDN.toASCII(domain, IDN.USE_STD3_ASCII_RULES);
  }
}

