package io.github.filant.filtersmith.lexer;

import java.io.IOException;
import java.io.Reader;

public interface Lexer {
  void tokenize(Reader reader, TokenConsumer consumer) throws IOException;
}

