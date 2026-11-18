#!/usr/bin/env bash
# Runs every unit test in the project and prints a per-module summary.
# Exits with a non-zero status when any test fails or a module fails to compile.
# Extra arguments are passed to Gradle, e.g. ./run-tests.sh --info
set -u

cd "$(dirname "$0")"

modules=(domain data presentation)

tasks=()
for module in "${modules[@]}"; do
  # --rerun runs the tests even when Gradle considers them up to date or cached.
  tasks+=(":$module:testAndroidHostTest" --rerun)
  # Keeps a module that fails to compile from showing results of an earlier run.
  rm -rf "$module/build/test-results/testAndroidHostTest"
done

./gradlew "${tasks[@]}" --continue "$@"
gradle_status=$?

echo
echo "Test summary"
total_tests=0
total_problems=0
for module in "${modules[@]}"; do
  results=("$module"/build/test-results/testAndroidHostTest/*.xml)
  if [ ! -e "${results[0]}" ]; then
    echo "  $module: no results (did it compile?)"
    total_problems=$((total_problems + 1))
    continue
  fi
  read -r tests failures errors skipped < <(
    sed -n 's/.*<testsuite [^>]*tests="\([0-9]*\)" skipped="\([0-9]*\)" failures="\([0-9]*\)" errors="\([0-9]*\)".*/\1 \3 \4 \2/p' "${results[@]}" |
      awk '{ t += $1; f += $2; e += $3; s += $4 } END { print t + 0, f + 0, e + 0, s + 0 }'
  )
  echo "  $module: $tests tests, $failures failed, $errors errors, $skipped skipped"
  total_tests=$((total_tests + tests))
  total_problems=$((total_problems + failures + errors))
done

echo
if [ "$gradle_status" -eq 0 ] && [ "$total_problems" -eq 0 ]; then
  echo "All $total_tests tests passed."
  exit 0
fi

echo "Tests did not all pass. Reports: <module>/build/reports/tests/testAndroidHostTest/index.html"
exit 1
