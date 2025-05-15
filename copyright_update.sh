#! /bin/bash

# Created using ChatGPT-4o, 2025

# Get Git user name and email
author_name=$(git config user.name)
author_email=$(git config user.email)

git ls-files '*.java' | while read file; do
    if ! grep -q 'Copyright' "$file"; then
        filename=$(basename "$file")
        startyear=$(git log --diff-filter=A --follow --format='%ad' --date=format:'%Y' -- "$file" | tail -1)

        [ -z "$startyear" ] && startyear=2025

        # Format copyright line
        if [ "$startyear" -eq 2025 ]; then
            copyright="Copyright 2025 $author_name <$author_email>"
        else
            copyright="Copyright $startyear-2025 $author_name <$author_email>"
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
 * 
 * 
 */"

        # Insert header at top
        tmpfile=$(mktemp)
        echo "$header" > "$tmpfile"
        echo "" >> "$tmpfile"
        cat "$file" >> "$tmpfile"
        mv "$tmpfile" "$file"

        echo "Header added to $file"
    fi
done

