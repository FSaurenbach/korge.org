@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION
cd .11ty & deno install & deno run -A npm:@11ty/eleventy --serve & set SAVEDRC=!ERRORLEVEL! & call;
EXIT /B %SAVEDRC%
