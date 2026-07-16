package io.github.filant.filtersmith.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;

public final class ConfigLoader {
  private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public FilterSmithConfig load(Path path) throws IOException {
    return mapper.readValue(path.toFile(), FilterSmithConfig.class);
  }
}

