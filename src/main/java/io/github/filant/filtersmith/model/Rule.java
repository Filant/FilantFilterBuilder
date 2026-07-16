package io.github.filant.filtersmith.model;

public sealed interface Rule permits DomainRule, ExceptionRule {
  String domain();
}

