#!/usr/bin/env python3
"""
Simple XML -> YAML generator for this repository's CI/dev XML artifacts.

Usage:
  python3 scripts/xml_to_yaml.py [target]

Targets:
  docker-compose    -> writes ./docker-compose.yml from .ci/docker-compose.xml
  github-workflow   -> writes .github/workflows/integration-mysql.yml from .ci/workflows/integration-mysql.xml
  k8s-deployment    -> writes docs/deployment/erp-deployment-k8s.yaml from docs/deployment/erp-deployment-k8s.xml
  all               -> generate all of the above

This script has no external dependencies and generates readable YAML sufficient for local use.
"""
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def gen_docker_compose(xml_path, out_path):
    tree = ET.parse(xml_path)
    root = tree.getroot()
    version = root.findtext('version') or '3.8'
    lines = [f"version: '{version}'", 'services:']
    for svc in root.findall('./services/service'):
        name = svc.get('name')
        lines.append(f"  {name}:")
        image = svc.findtext('image')
        if image:
            lines.append(f"    image: {image}")
        env = svc.find('environment')
        if env is not None:
            lines.append('    environment:')
            for var in env.findall('var'):
                safe = (var.text or '').replace("'", "\\'")
                lines.append(f"      {var.get('name')}: '{safe}'")
        ports = svc.find('ports')
        if ports is not None:
            lines.append('    ports:')
            for p in ports.findall('port'):
                lines.append(f"      - '{p.text}'")
        hc = svc.find('healthcheck')
        if hc is not None:
            lines.append('    healthcheck:')
            test = hc.findtext('test')
            if test:
                # keep test as list form preferred by docker-compose
                lines.append(f"      test: ['CMD-SHELL', \"{test}\"]")
            for tag in ('interval', 'timeout', 'retries'):
                val = hc.findtext(tag)
                if val:
                    lines.append(f"      {tag}: {val}")
    # volumes at top-level not implemented in xml form -> preserve existing if needed
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f'Wrote {out_path}')


def gen_github_workflow(xml_path, out_path):
    tree = ET.parse(xml_path)
    root = tree.getroot()
    # Basic mapping: name, triggers, job steps
    name = root.findtext('name') or 'CI'
    lines = [f"name: {name}", '', 'on:']
    triggers = root.find('triggers')
    if triggers is not None:
        for trig in triggers.findall('trigger'):
            ttype = trig.get('type')
            branches = [b.text for b in trig.findall('./branches/branch')]
            if branches:
                lines.append(f"  {ttype}:")
                lines.append('    branches:')
                for b in branches:
                    lines.append(f"      - {b}")
    lines.append('', 'jobs:')
    for job in root.findall('./jobs/job'):
        jid = job.get('id')
        runs_on = job.findtext('runsOn') or 'ubuntu-latest'
        lines.append(f"  {jid}:")
        lines.append(f"    runs-on: {runs_on}")
        services = job.find('services')
        if services is not None:
            lines.append('    services:')
            for svc in services.findall('service'):
                sname = svc.get('name')
                lines.append(f"      {sname}:")
                lines.append(f"        image: {svc.findtext('image')}")
        lines.append('    steps:')
        for step in job.findall('./steps/step'):
            stype = step.get('type')
            if stype == 'checkout':
                lines.append('      - name: Checkout')
                lines.append('        uses: actions/checkout@v4')
            elif stype == 'setup-java':
                lines.append('      - name: Set up JDK 21')
                lines.append('        uses: actions/setup-java@v4')
                lines.append('        with:')
                dist = step.findtext('distribution') or 'temurin'
                jv = step.findtext('javaVersion') or '21'
                lines.append(f"          distribution: {dist}")
                lines.append(f"          java-version: '{jv}'")
            else:
                run = step.findtext('run')
                if run:
                    lines.append('      - name: Run step')
                    lines.append('        run: |')
                    for ln in run.splitlines():
                        lines.append('          ' + ln)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f'Wrote {out_path}')


def gen_k8s_deployment(xml_path, out_path):
    tree = ET.parse(xml_path)
    root = tree.getroot()
    lines = []
    lines.append('apiVersion: ' + (root.findtext('apiVersion') or 'apps/v1'))
    lines.append('kind: ' + (root.findtext('kind') or 'Deployment'))
    lines.append('metadata:')
    lines.append('  name: ' + root.find('./metadata/name').text)
    spec = root.find('spec')
    lines.append('spec:')
    lines.append('  replicas: ' + spec.findtext('replicas'))
    lines.append('  selector:')
    lines.append('    matchLabels:')
    lines.append('      app: ' + spec.findtext('selector/matchLabels/app'))
    lines.append('  template:')
    lines.append('    metadata:')
    lines.append('      labels:')
    lines.append('        app: ' + spec.findtext('template/metadata/labels/app'))
    lines.append('    spec:')
    lines.append('      containers:')
    for c in spec.findall('template/spec/containers/container'):
        lines.append('        - name: ' + c.findtext('name'))
        lines.append('          image: ' + c.findtext('image'))
        env = c.find('env')
        if env is not None:
            lines.append('          env:')
            for v in env.findall('var'):
                lines.append(f"            - name: {v.get('name')}")
                lines.append(f"              value: '{(v.text or '')}'")
        vm = c.find('volumeMounts')
        if vm is not None:
            lines.append('          volumeMounts:')
            for mount in vm.findall('volumeMount'):
                lines.append('            - name: ' + mount.findtext('name'))
                lines.append('              mountPath: ' + mount.findtext('mountPath'))
    # Volumes
    vols = spec.find('template/spec/volumes')
    if vols is not None:
        lines.append('      volumes:')
        for v in vols.findall('volume'):
            lines.append('        - name: ' + v.findtext('name'))
            hp = v.find('hostPath')
            if hp is not None:
                lines.append('          hostPath:')
                lines.append('            path: ' + hp.findtext('path'))
                lines.append('            type: ' + hp.findtext('type'))
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f'Wrote {out_path}')


def main():
    tgt = (sys.argv[1] if len(sys.argv) > 1 else 'all')
    if tgt in ('docker-compose', 'all'):
        xml = ROOT / '.ci' / 'docker-compose.xml'
        out = ROOT / 'docker-compose.yml'
        if xml.exists():
            gen_docker_compose(xml, out)
        else:
            print('Missing', xml)
    if tgt in ('github-workflow', 'all'):
        xml = ROOT / '.ci' / 'workflows' / 'integration-mysql.xml'
        out = ROOT / '.github' / 'workflows' / 'integration-mysql.yml'
        if xml.exists():
            gen_github_workflow(xml, out)
        else:
            print('Missing', xml)
    if tgt in ('k8s-deployment', 'all'):
        xml = ROOT / 'docs' / 'deployment' / 'erp-deployment-k8s.xml'
        out = ROOT / 'docs' / 'deployment' / 'erp-deployment-k8s.yaml'
        if xml.exists():
            gen_k8s_deployment(xml, out)
        else:
            print('Missing', xml)


if __name__ == '__main__':
    main()
