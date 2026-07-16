package io.github.filant.filtersmith.config;

import java.util.List;

public record ProfileConfig(List<String> sources, String format, String output) {}

