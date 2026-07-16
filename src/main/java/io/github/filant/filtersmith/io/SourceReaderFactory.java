package io.github.filant.filtersmith.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SourceReaderFactory {
  public Reader open(String location) throws IOException {
    if (location.startsWith("http://") || location.startsWith("https://")) {
      URLConnection connection = URI.create(location).toURL().openConnection();
      connection.setConnectTimeout(15_000);
      connection.setReadTimeout(60_000);
      return new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
    }
    return Files.newBufferedReader(Path.of(location), StandardCharsets.UTF_8);
  }
}

