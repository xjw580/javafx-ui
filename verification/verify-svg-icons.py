#!/usr/bin/env python3
"""Run SVG icon checks against the packaged library, on classpath and JPMS.
Run Maven clean verify first. This script resolves dependencies using the supplied
Maven executable/settings; Monocle is test-only and never a production dependency.
"""
import argparse
import os
import pathlib
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument('--maven', default='mvn')
parser.add_argument('--settings', type=pathlib.Path)
parser.add_argument('--repository', type=pathlib.Path)
parser.add_argument('--desktop', action='store_true', help='Use desktop JavaFX rather than test-only Monocle')
args = parser.parse_args()
project = pathlib.Path(__file__).resolve().parents[1]
out = project / 'target' / 'svg-verification'
out.mkdir(parents=True, exist_ok=True)
os.environ.setdefault('XDG_CACHE_HOME', str(out / 'cache'))
pathlib.Path(os.environ['XDG_CACHE_HOME']).mkdir(parents=True, exist_ok=True)
maven = [args.maven, '-B']
if args.settings: maven += ['-s', str(args.settings.resolve())]
if args.repository: maven += ['-Dmaven.repo.local=' + str(args.repository.resolve())]

def run(label, command):
    result = subprocess.run(command, cwd=project, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, timeout=240)
    (out / (label + '.log')).write_text(result.stdout)
    print(result.stdout)
    if result.returncode: raise SystemExit(f'{label} failed with exit code {result.returncode}')

cp_file = out / 'classpath.txt'
run('resolve-dependencies', maven + ['dependency:build-classpath', '-Dmdep.outputFile=' + str(cp_file)])
deps = [pathlib.Path(p) for p in cp_file.read_text().strip().split(os.pathsep)]
# The classifier-free JavaFX artifacts are empty forwarding JARs, not modules.
modules = [p for p in deps if not (p.name.startswith('javafx-') and p.stat().st_size < 4096)]
jar = project / 'target' / 'javafx-ui-3.0.0.jar'
if not jar.is_file(): raise SystemExit('Run Maven clean verify first: missing ' + str(jar))
cp = os.pathsep.join(str(p) for p in [jar] + modules)
classes = out / 'classes'; classes.mkdir(exist_ok=True)
run('compile', ['java', '-m', 'jdk.compiler/com.sun.tools.javac.Main', '-source', '21', '-target', '21', '-proc:none', '-cp', cp, '-d', str(classes), str(project / 'verification/SvgIconVerification.java')])
fx_flags = ['-Djavafx.cachedir=' + str(out / 'javafx-cache')]
monocle = None
if not args.desktop:
    run('resolve-monocle', maven + ['dependency:get', '-Dartifact=org.testfx:openjfx-monocle:21.0.2', '-Dtransitive=false'])
    repo = args.repository.resolve() if args.repository else pathlib.Path.home() / '.m2/repository'
    monocle = repo / 'org/testfx/openjfx-monocle/21.0.2/openjfx-monocle-21.0.2.jar'
    if not monocle.is_file(): raise SystemExit('Missing resolved Monocle ' + str(monocle))
    fx_flags += ['-Dglass.platform=Monocle', '-Dmonocle.platform=Headless', '-Dprism.order=sw']
icon_sources = str(project / 'src/main/java/club/xiaojiawei/controls/ico')
classpath = os.pathsep.join([str(classes), cp] + ([str(monocle)] if monocle else []))
run('classpath', ['java'] + fx_flags + ['-cp', classpath, 'SvgIconVerification', icon_sources, str(out / 'contact-sheet.png')])
module_flags = ['--module-path', cp, '--add-modules', 'club.xiaojiawei.javafx_ui']
if monocle: module_flags += ['--patch-module', 'javafx.graphics=' + str(monocle)]
run('jpms', ['java'] + fx_flags + module_flags + ['-cp', str(classes), 'SvgIconVerification', icon_sources, str(out / 'contact-sheet.png')])
print('PASS packaged classpath and JPMS SVG consumers')
