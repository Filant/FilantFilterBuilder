package io.github.filant.filtersmith.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class LineLexer implements Lexer {
  @Override
  public void tokenize(Reader reader, TokenConsumer consumer) throws IOException {
    BufferedReader bufferedReader =
        reader instanceof BufferedReader current ? current : new BufferedReader(reader);
    long lineNumber = 0;
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      lineNumber++;
      consumer.accept(new Token(classify(line), line, lineNumber));
    }
  }

  private TokenType classify(String line) {
    String trimmed = line.trim();
    if (trimmed.isEmpty()) {
      return TokenType.BLANK;
    }
    if (trimmed.startsWith("#") || trimmed.startsWith("!")) {
      return TokenType.COMMENT;
    }
    return TokenType.RULE;
  }
}

