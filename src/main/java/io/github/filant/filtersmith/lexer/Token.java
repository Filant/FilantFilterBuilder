package io.github.filant.filtersmith.lexer;

public record Token(TokenType type, String text, long lineNumber) {}

