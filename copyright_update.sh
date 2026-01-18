#!/usr/bin/env bash
set -euo pipefail

# Auto-update or add copyright headers for Java files
# Created 2026

author_name="$(git config --get user.name || true)"
author_email="$(git config --get user.email || true)"
currentyear="$(date +%Y)"

git ls-files '*.java' | while IFS= read -r file; do
    if grep -qE 'Copyright[[:space:]]+[0-9]{4}' "$file"; then
        # Extract first and last years mentioned
        startyear="$(grep -oE 'Copyright[[:space:]]+[0-9]{4}' "$file" | head -1 | awk '{print $2}')"
        endyear="$(grep -oE 'Copyright[[:space:]]+[0-9]{4}' "$file" | tail -1 | awk '{print $2}')"

        # Safety check
        if [[ -z "$startyear" || -z "$endyear" ]]; then
            echo "Could not parse years in $file, skipping"
            continue
        fi

        if [[ "$endyear" == "$currentyear" ]]; then
            echo "No update needed for $file (already $currentyear)"
        else
            # Replace single-year or range safely
            sed -i -E \
                "s/Copyright[[:space:]]+$startyear(-[0-9]{4})?/Copyright $startyear-$currentyear/" \
                "$file"
            echo "Updated copyright year in $file ($startyear-$currentyear)"
        fi
    else
        filename="$(basename "$file")"

        # Get year file was first added to git
        startyear="$(git log --diff-filter=A --follow --format='%ad' --date=format:%Y -- "$file" | tail -1)"
        [[ -z "$startyear" ]] && startyear="$currentyear"

        if [[ "$startyear" == "$currentyear" ]]; then
            copyright="Copyright $currentyear $author_name <$author_email>"
        else
            copyright="Copyright $startyear-$currentyear $author_name <$author_email>"
        fi

        header="/*
 * $filename
 *
 * $copyright
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */"

        tmpfile="$(mktemp)"
        {
            echo "$header"
            echo
            cat "$file"
        } > "$tmpfile"

        mv "$tmpfile" "$file"
        echo "Header added to $file"
    fi
done

