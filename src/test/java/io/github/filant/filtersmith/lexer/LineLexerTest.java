package io.github.filant.filtersmith.lexer.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.filant.filtersmith.lexer.LineLexer;
import io.github.filant.filtersmith.lexer.Token;
import io.github.filant.filtersmith.lexer.TokenType;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class LineLexerTest {
  @Test
  void tokenizesLinesWithoutLoadingWholeInput() throws Exception {
    LineLexer lexer = new LineLexer();
    List<Token> tokens = new ArrayList<>();

    lexer.tokenize(new StringReader("! comment\n\n||example.com^\n"), tokens::add);

    assertThat(tokens)
        .extracting(Token::type)
        .containsExactly(TokenType.COMMENT, TokenType.BLANK, TokenType.RULE);
    assertThat(tokens.get(2).lineNumber()).isEqualTo(3);
  }
}

