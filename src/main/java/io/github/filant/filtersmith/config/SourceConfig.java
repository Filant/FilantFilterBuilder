package io.github.filant.filtersmith.config;

public record SourceConfig(String name, String url, String path) {
  public String location() {
    return url != null && !url.isBlank() ? url : path;
  }
}

