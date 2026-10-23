"""Run every module that contains instrumentation tests, without empty test APKs."""
import re
import subprocess
from pathlib import Path


def instrumentation_tasks(root):
    settings = (root / 'settings.gradle.kts').read_text()
    modules = re.findall(r'include\("(:[^"]+)"\)', settings)
    tasks = []
    for module in modules:
        source = root.joinpath(*module.strip(':').split(':'), 'src', 'androidTest')
        if any(source.rglob('*.kt')) or any(source.rglob('*.java')):
            tasks.append(module + ':connectedDebugAndroidTest')
    if not tasks:
        raise ValueError('No instrumentation test sources found')
    return tasks


if __name__ == '__main__':
    root = Path(__file__).resolve().parents[1]
    tasks = instrumentation_tasks(root)
    print('Instrumentation test modules: ' + ', '.join(tasks), flush=True)
    try:
        subprocess.run(
            ['./gradlew', '-PciBuild=true', '--no-daemon', '--max-workers=2', *tasks],
            cwd=root, check=True,
        )
    finally:
        report = root / 'build/reports/emulator/logcat.txt'
        report.parent.mkdir(parents=True, exist_ok=True)
        with report.open('w') as output:
            subprocess.run(['adb', 'logcat', '-d'], stdout=output, stderr=subprocess.STDOUT)
