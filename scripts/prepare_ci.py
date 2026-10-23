"""Create fake Firebase build configuration for CI; never real credentials."""
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
config = {
    'project_info': {'project_number': '123456789012', 'project_id': 'layoff-ci-only',
                     'storage_bucket': 'layoff-ci-only.invalid'},
    'client': [{'client_info': {'mobilesdk_app_id': '1:123456789012:android:0000000000000000',
                               'android_client_info': {'package_name': 'com.example.tracklayoff'}},
                # Matches Firebase's local format checks, but is not a real key.
                'oauth_client': [], 'api_key': [{'current_key': 'A' + '0' * 38}]}],
    'configuration_version': '1',
}
target = root / 'app/src/debug/google-services.json'
if target.exists():
    raise SystemExit('Refusing to replace existing Firebase configuration')
target.parent.mkdir(parents=True, exist_ok=True)
target.write_text(json.dumps(config))
