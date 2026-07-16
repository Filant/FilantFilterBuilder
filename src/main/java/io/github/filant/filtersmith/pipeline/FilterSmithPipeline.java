package io.github.filant.filtersmith.pipeline;

import io.github.filant.filtersmith.generate.OutputFormat;
import io.github.filant.filtersmith.generate.RuleGenerator;
import io.github.filant.filtersmith.io.RuleSource;
import io.github.filant.filtersmith.io.SourceReaderFactory;
import io.github.filant.filtersmith.lexer.Lexer;
import io.github.filant.filtersmith.lexer.LineLexer;
import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.optimize.OptimizerChain;
import io.github.filant.filtersmith.parse.DomainRuleParser;
import io.github.filant.filtersmith.parse.RuleParser;
import io.github.filant.filtersmith.report.BuildReport;
import io.github.filant.filtersmith.report.ReportBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FilterSmithPipeline {
  private final SourceReaderFactory readerFactory;
  private final Lexer lexer;
  private final RuleParser parser;
  private final OptimizerChain optimizerChain;
  private final RuleGenerator generator;

  public FilterSmithPipeline() {
    this(
        new SourceReaderFactory(),
        new LineLexer(),
        new DomainRuleParser(),
        OptimizerChain.standard(),
        new RuleGenerator());
  }

  FilterSmithPipeline(
      SourceReaderFactory readerFactory,
      Lexer lexer,
      RuleParser parser,
      OptimizerChain optimizerChain,
      RuleGenerator generator) {
    this.readerFactory = readerFactory;
    this.lexer = lexer;
    this.parser = parser;
    this.optimizerChain = optimizerChain;
    this.generator = generator;
  }

  public BuildReport build(List<RuleSource> sources, OutputFormat format, Path output)
      throws IOException {
    ReportBuilder report = new ReportBuilder();
    List<Rule> rules = readRules(sources, report);
    List<Rule> optimized = optimizerChain.optimize(rules, report);
    writeRules(optimized, format, output);
    report.setGeneratedRules(optimized.size());
    return report.build();
  }

  public List<Rule> parseOnly(List<Path> inputs, ReportBuilder report) throws IOException {
    List<RuleSource> sources =
        inputs.stream()
            .map(path -> new RuleSource(path.getFileName().toString(), path.toString()))
            .toList();
    return readRules(sources, report);
  }

  private List<Rule> readRules(List<RuleSource> sources, ReportBuilder report) throws IOException {
    List<Rule> rules = new ArrayList<>();
    for (RuleSource source : sources) {
      try (Reader reader = readerFactory.open(source.location())) {
        lexer.tokenize(
            reader,
            token -> {
              report.addInputLine();
              parser
                  .parse(token)
                  .ifPresent(
                      rule -> {
                        report.addParsedRule();
                        rules.add(rule);
                      });
            });
      }
    }
    return rules;
  }

  private void writeRules(List<Rule> rules, OutputFormat format, Path output) throws IOException {
    Path parent = output.toAbsolutePath().getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
      writer.write("! Title: FilterSmith generated blocklist");
      writer.newLine();
      writer.write("! Rules: " + rules.size());
      writer.newLine();
      for (Rule rule : rules) {
        writer.write(generator.generate(rule, format));
        writer.newLine();
      }
    }
  }
}

