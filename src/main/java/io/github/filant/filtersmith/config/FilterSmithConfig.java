package io.github.filant.filtersmith.config;

import java.util.List;
import java.util.Map;

public record FilterSmithConfig(List<SourceConfig> sources, Map<String, ProfileConfig> profiles) {}

