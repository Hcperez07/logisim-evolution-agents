# Headless CLI kill strategy for agent wrappers

For deterministic process supervision, wrap Logisim-evolution CLI calls with an OS timeout and also pass `--timeout-ms`.

## Recommended wrapper pattern

```bash
timeout --signal=TERM 35s java -jar logisim-evolution.jar --timeout-ms 30000 --validate-circ project.circ
```

## Expected exit codes

- `0`: operation completed successfully.
- `1`: domain failure (e.g., validation failed / test-vector mismatch).
- `2`: internal operation error (e.g., export setup failure).
- `124`: watchdog timeout (`--timeout-ms` exceeded).
- `130`: interrupted by `SIGINT` (Ctrl+C / agent cancel mapped to INT).
- `143`: interrupted by `SIGTERM` (external supervisor timeout kill).

## Minimal deterministic stderr markers

The CLI emits machine-detectable lines:

- Timeout: `[logisim-cli] timeout operation=<name> timeoutMs=<n>`
- Interrupt: `[logisim-cli] interrupted signal=SIGINT|SIGTERM`

Use these markers to distinguish timeout/interrupt conditions from parse/validation errors.
