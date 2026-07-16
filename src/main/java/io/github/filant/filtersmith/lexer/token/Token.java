package io.github.filant.filtersmith.lexer.token;

public record Token(

        TokenType type,

        String value,

        int line,

        int column

) {
}