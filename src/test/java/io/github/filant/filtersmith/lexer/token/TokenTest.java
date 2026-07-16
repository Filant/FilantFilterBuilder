package io.github.filant.filtersmith.lexer.token;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @Test
    void shouldCreateToken() {

        Token token = new Token(
                TokenType.DOMAIN,
                "google.com",
                1,
                3
        );

        assertThat(token.type()).isEqualTo(TokenType.DOMAIN);
        assertThat(token.value()).isEqualTo("google.com");
        assertThat(token.line()).isEqualTo(1);
        assertThat(token.column()).isEqualTo(3);

    }

}