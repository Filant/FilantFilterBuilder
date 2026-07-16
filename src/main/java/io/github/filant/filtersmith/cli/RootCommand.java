package io.github.filant.filtersmith.cli;

import io.github.filant.filtersmith.command.VersionCommand;
import picocli.CommandLine.Command;

@Command(
        name = "filtersmith",
        mixinStandardHelpOptions = true,
        version = "0.1.0-SNAPSHOT",
        description = "High performance DNS filter optimizer",
        subcommands = {
                VersionCommand.class
        }
)
public final class RootCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("""
                FilterSmith

                Use one of the available commands.

                Example:
                    filtersmith version
                    filtersmith --help
                """);
    }
}
