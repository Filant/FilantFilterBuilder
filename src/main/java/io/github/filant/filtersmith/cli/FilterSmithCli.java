package io.github.filant.filtersmith.cli;

import io.github.filant.filtersmith.config.ConfigLoader;
import io.github.filant.filtersmith.config.FilterSmithConfig;
import io.github.filant.filtersmith.config.ProfileConfig;
import io.github.filant.filtersmith.config.SourceConfig;
import io.github.filant.filtersmith.generate.OutputFormat;
import io.github.filant.filtersmith.io.RuleSource;
import io.github.filant.filtersmith.model.Rule;
import io.github.filant.filtersmith.pipeline.FilterSmithPipeline;
import io.github.filant.filtersmith.report.BuildReport;
import io.github.filant.filtersmith.report.ReportBuilder;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "filtersmith",
    mixinStandardHelpOptions = true,
    version = "FilterSmith 0.1.0-SNAPSHOT",
    description = "Build, merge, optimize and validate DNS blocklists.",
    subcommands = {
      FilterSmithCli.BuildCommand.class,
      FilterSmithCli.MergeCommand.class,
      FilterSmithCli.OptimizeCommand.class,
      FilterSmithCli.ValidateCommand.class,
      FilterSmithCli.BenchmarkCommand.class,
      FilterSmithCli.StatsCommand.class,
      FilterSmithCli.VersionCommand.class
    })
public final class FilterSmithCli implements Runnable {
  public static void main(String[] args) {
    int exitCode = new CommandLine(new FilterSmithCli()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }

  @Command(name = "build", description = "Build an optimized blocklist from YAML configuration.")
  static final class BuildCommand implements Callable<Integer> {
    @Option(
        names = {"-c", "--config"},
        required = true,
        description = "YAML configuration file.")
    private Path configPath;

    @Option(
        names = {"-p", "--profile"},
        defaultValue = "balanced",
        description = "Profile name.")
    private String profileName;

    @Override
    public Integer call() throws Exception {
      FilterSmithConfig config = new ConfigLoader().load(configPath);
      ProfileConfig profile = config.profiles().get(profileName);
      if (profile == null) {
        throw new CommandLine.ParameterException(
            new CommandLine(this), "Profile not found: " + profileName);
      }
      List<RuleSource> sources = selectSources(config.sources(), profile.sources());
      OutputFormat format = parseFormat(profile.format());
      Path output = Path.of(profile.output() == null ? "dist/blocklist.txt" : profile.output());
      BuildReport report = new FilterSmithPipeline().build(sources, format, output);
      printReport(report);
      return 0;
    }
  }

  @Command(name = "merge", description = "Merge input files into one optimized AdGuard list.")
  static final class MergeCommand extends OptimizeCommand {}

  @Command(name = "optimize", description = "Optimize local input files.")
  static class OptimizeCommand implements Callable<Integer> {
    @Parameters(arity = "1..*", description = "Input blocklist files.")
    private List<Path> inputs;

    @Option(
        names = {"-o", "--output"},
        required = true,
        description = "Output file.")
    private Path output;

    @Option(
        names = {"-f", "--format"},
        defaultValue = "adguard",
        description = "adguard, hosts, plain")
    private String format;

    @Override
    public Integer call() throws Exception {
      List<RuleSource> sources =
          inputs.stream()
              .map(path -> new RuleSource(path.getFileName().toString(), path.toString()))
              .toList();
      BuildReport report = new FilterSmithPipeline().build(sources, parseFormat(format), output);
      printReport(report);
      return 0;
    }
  }

  @Command(
      name = "validate",
      description = "Validate that input files contain parseable DNS rules.")
  static final class ValidateCommand implements Callable<Integer> {
    @Parameters(arity = "1..*", description = "Input blocklist files.")
    private List<Path> inputs;

    @Override
    public Integer call() throws Exception {
      ReportBuilder report = new ReportBuilder();
      List<Rule> rules = new FilterSmithPipeline().parseOnly(inputs, report);
      System.out.printf("valid=true%nparsedRules=%d%n", rules.size());
      return rules.isEmpty() ? 2 : 0;
    }
  }

  @Command(name = "stats", description = "Print parsed rule statistics.")
  static final class StatsCommand implements Callable<Integer> {
    @Parameters(arity = "1..*", description = "Input blocklist files.")
    private List<Path> inputs;

    @Override
    public Integer call() throws Exception {
      ReportBuilder report = new ReportBuilder();
      List<Rule> rules = new FilterSmithPipeline().parseOnly(inputs, report);
      BuildReport buildReport = report.build();
      System.out.printf("inputLines=%d%nparsedRules=%d%n", buildReport.inputLines(), rules.size());
      return 0;
    }
  }

  @Command(name = "benchmark", description = "Run a simple parser benchmark.")
  static final class BenchmarkCommand implements Callable<Integer> {
    @Parameters(arity = "1..*", description = "Input blocklist files.")
    private List<Path> inputs;

    @Override
    public Integer call() throws Exception {
      long started = System.nanoTime();
      ReportBuilder report = new ReportBuilder();
      List<Rule> rules = new FilterSmithPipeline().parseOnly(inputs, report);
      long elapsedMillis = (System.nanoTime() - started) / 1_000_000;
      System.out.printf("parsedRules=%d%nelapsedMillis=%d%n", rules.size(), elapsedMillis);
      return 0;
    }
  }

  @Command(name = "version", description = "Print FilterSmith version.")
  static final class VersionCommand implements Callable<Integer> {
    @Override
    public Integer call() {
      System.out.println("FilterSmith 0.1.0-SNAPSHOT");
      return 0;
    }
  }

  private static List<RuleSource> selectSources(List<SourceConfig> sources, List<String> names) {
    if (names == null || names.isEmpty()) {
      return sources.stream()
          .map(source -> new RuleSource(source.name(), source.location()))
          .toList();
    }
    return sources.stream()
        .filter(source -> names.contains(source.name()))
        .map(source -> new RuleSource(source.name(), source.location()))
        .toList();
  }

  private static OutputFormat parseFormat(String value) {
    return OutputFormat.valueOf(value.toUpperCase(Locale.ROOT));
  }

  private static void printReport(BuildReport report) {
    System.out.printf(
        "inputLines=%d%nparsedRules=%d%nduplicateRules=%d%nnormalizedRules=%d%nremovedSubdomains=%d%ngeneratedRules=%d%n",
        report.inputLines(),
        report.parsedRules(),
        report.duplicateRules(),
        report.normalizedRules(),
        report.removedSubdomains(),
        report.generatedRules());
  }
}

