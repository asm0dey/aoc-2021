#!/usr/bin/env bash

DAY="$(printf "%02d" "$1")"

touch src/Day"$DAY".txt
touch src/Day"$DAY"_test.txt

echo "fun main() {
          fun part1(input: List<String>): Int {
              TODO()
          }

          fun part2(input: List<String>): Int {
              TODO()
          }

          // test if implementation meets criteria from the description, like:
          val testInput = readInput(\"Day${DAY}_test\")
          check(part1(testInput) == -1)
      }
" > src/Day"$DAY".kt