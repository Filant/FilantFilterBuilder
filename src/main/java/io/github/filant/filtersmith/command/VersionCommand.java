package io.github.filant.filtersmith.command;

import picocli.CommandLine.Command;

@Command(
        name = "version",
        description = "Print application version"
)
public final class VersionCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("FilterSmith 0.1.0-SNAPSHOT");
    }
}