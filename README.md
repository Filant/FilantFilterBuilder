# FilterSmith

FilterSmith is a Java 21 command-line tool for building compact DNS blocklists from large upstream filter sources.

The project follows this pipeline:

```text
Reader -> Lexer -> Parser -> Rule Model -> Optimizer -> Generator -> Report
```

## Current Scope

- Streaming line lexer based on `Reader`.
- Parser for common DNS blocklist formats:
  - AdGuard rules like `||example.com^`
  - AdGuard exceptions like `@@||example.com^`
  - hosts rules like `0.0.0.0 example.com`
  - dnsmasq rules like `address=/example.com/0.0.0.0`
  - plain domains and URL host extraction
- Java records for rule model.
- Optimizers:
  - normalization
  - duplicate removal
  - subdomain removal via reverse domain trie
- Generators:
  - AdGuard Home
  - hosts
  - plain domain list
- CLI commands:
  - `build`
  - `merge`
  - `optimize`
  - `validate`
  - `benchmark`
  - `stats`
  - `version`

## Build

```bash
mvn verify
mvn package
```

The executable jar is produced at:

```text
target/filtersmith-0.1.0-SNAPSHOT.jar
```

## Usage

Optimize local lists:

```bash
java -jar target/filtersmith-0.1.0-SNAPSHOT.jar optimize input.txt --output dist/adguard.txt --format adguard
```

Build from YAML config:

```bash
java -jar target/filtersmith-0.1.0-SNAPSHOT.jar build --config filtersmith.yml --profile balanced
```

Validate a list:

```bash
java -jar target/filtersmith-0.1.0-SNAPSHOT.jar validate input.txt
```

## Example Config

See [filtersmith.example.yml](filtersmith.example.yml).

## Design Notes

Parsing intentionally avoids regular expressions. The lexer reads line by line and does not load whole source files into memory. Global optimization currently stores parsed rules because duplicate detection and trie-based subdomain optimization require comparing the full rule set.
