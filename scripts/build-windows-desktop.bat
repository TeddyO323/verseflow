@echo off
setlocal
powershell -ExecutionPolicy Bypass -File "%~dp0build-windows-desktop.ps1"
endlocal
