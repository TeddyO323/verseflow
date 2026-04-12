$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$gradleWrapper = Join-Path $repoRoot "gradlew.bat"
$releaseDir = Join-Path $repoRoot "releases"
$desktopBinaryRoot = Join-Path $repoRoot "desktopApp\build\compose\binaries\main"

if (-not (Test-Path $gradleWrapper)) {
    throw "Could not find gradlew.bat at $gradleWrapper"
}

New-Item -ItemType Directory -Force -Path $releaseDir | Out-Null

Write-Host "Building VerseFlow Windows desktop installers..."
Push-Location $repoRoot
try {
    & $gradleWrapper :desktopApp:packageDistributionForCurrentOS --console=plain
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle packaging failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

$installerFiles = Get-ChildItem -Path $desktopBinaryRoot -Recurse -File |
    Where-Object { $_.Extension -in @(".exe", ".msi") }

if (-not $installerFiles) {
    throw "No Windows installer files were found under $desktopBinaryRoot"
}

foreach ($file in $installerFiles) {
    $targetPath = Join-Path $releaseDir $file.Name
    Copy-Item -Path $file.FullName -Destination $targetPath -Force
    Write-Host "Copied $($file.Name) to releases/"
}

Write-Host ""
Write-Host "Windows desktop build complete."
Write-Host "Installer files:"
$installerFiles | ForEach-Object {
    Write-Host " - $($_.FullName)"
}
