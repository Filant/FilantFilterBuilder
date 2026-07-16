package io.github.filant.filtersmith.lexer;

@FunctionalInterface
public interface TokenConsumer {
  void accept(Token token);
}

