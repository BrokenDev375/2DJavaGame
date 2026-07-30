Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Invoke-Checked {
    param(
        [Parameter(Mandatory = $true)]
        [string] $FilePath,

        [Parameter(Mandatory = $true)]
        [string[]] $Arguments
    )

    & $FilePath @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "$FilePath failed with exit code $LASTEXITCODE"
    }
}

$repoRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$gsonJar = Join-Path $repoRoot 'libraries\gson-2.10.1.jar'
$resourceDir = Join-Path $repoRoot 'resource'

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'javac was not found on PATH. Install a JDK and retry.'
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'java was not found on PATH. Install a JDK and retry.'
}

if (-not (Test-Path -LiteralPath $gsonJar)) {
    throw "Missing dependency: $gsonJar"
}

$tempRoot = [System.IO.Path]::GetFullPath([System.IO.Path]::GetTempPath())
$smokeRoot = [System.IO.Path]::GetFullPath((Join-Path $tempRoot 'codex-2djavgame-smoke'))
if (-not $smokeRoot.StartsWith($tempRoot, [StringComparison]::OrdinalIgnoreCase)) {
    throw "Refusing to clean unexpected smoke directory: $smokeRoot"
}

if (Test-Path -LiteralPath $smokeRoot) {
    Remove-Item -LiteralPath $smokeRoot -Recurse -Force
}

$classesDir = Join-Path $smokeRoot 'classes'
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

$sourceRoots = @(
    (Join-Path $repoRoot 'src'),
    (Join-Path $repoRoot 'test')
)
$sources = @(Get-ChildItem -Path $sourceRoots -Recurse -Filter *.java | ForEach-Object { $_.FullName })
if ($sources.Count -eq 0) {
    throw 'No Java source files found under src or test.'
}

Write-Host 'Compiling src and test Java files...'
$compileArgs = @('-encoding', 'UTF-8', '-cp', $gsonJar, '-d', $classesDir) + $sources
Invoke-Checked -FilePath 'javac' -Arguments $compileArgs

$runtimeCp = @($classesDir, $gsonJar, $resourceDir) -join [System.IO.Path]::PathSeparator
$testClasses = @(
    'entity.EntityCoreSmokeTest',
    'main.CollisionGeometrySmokeTest'
)

foreach ($testClass in $testClasses) {
    Write-Host "Running $testClass..."
    Invoke-Checked -FilePath 'java' -Arguments @('-cp', $runtimeCp, $testClass)
}

Write-Host 'Smoke tests passed.'
