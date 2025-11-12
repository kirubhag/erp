XML -> YAML generator
=====================

This repository now stores CI / deployment config artifacts in XML (with XSD schemas) for teams that prefer XML.

Use the provided generator to re-create runtime YAML when needed:

Examples:

Generate docker-compose.yml:

```bash
python3 scripts/xml_to_yaml.py docker-compose
```

Generate GitHub Actions workflow YAML:

```bash
python3 scripts/xml_to_yaml.py github-workflow
```

Generate k8s deployment YAML:

```bash
python3 scripts/xml_to_yaml.py k8s-deployment
```

Generate all:

```bash
python3 scripts/xml_to_yaml.py all
```

Notes:
- The script is intentionally dependency-free (no PyYAML). It creates readable YAML sufficient for local use and CI recreation.
- If you need more feature-complete YAML generation, modify the script or install PyYAML and extend it.
