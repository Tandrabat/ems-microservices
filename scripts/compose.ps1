[CmdletBinding(PositionalBinding = $false)]
param(
    [string]$Context,
    [string]$ComposeFile = "docker-compose.yml",
    [Parameter(Position = 0, ValueFromRemainingArguments = $true)]
    [string[]]$ComposeArgs
)

$ErrorActionPreference = "Stop"

$dockerCliDir = Join-Path $env:LOCALAPPDATA "Microsoft\WinGet\Packages\Docker.DockerCLI_Microsoft.Winget.Source_8wekyb3d8bbwe\docker"
$composeDir = Join-Path $env:LOCALAPPDATA "Microsoft\WinGet\Packages\Docker.DockerCompose_Microsoft.Winget.Source_8wekyb3d8bbwe"
$dockerExe = Join-Path $dockerCliDir "docker.exe"
$composeExe = Join-Path $composeDir "docker-compose.exe"

if (-not (Test-Path $dockerExe)) {
    Write-Error "Docker CLI not found at '$dockerExe'. Install with: winget install --id Docker.DockerCLI -e --source winget --accept-package-agreements --accept-source-agreements"
}

if (-not (Test-Path $composeExe)) {
    Write-Error "Docker Compose not found at '$composeExe'. Install with: winget install --id Docker.DockerCompose -e --source winget --accept-package-agreements --accept-source-agreements"
}

$env:Path = "$dockerCliDir;$composeDir;$env:Path"

if ($Context) {
    & $dockerExe context use $Context
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}

if (-not $ComposeArgs -or $ComposeArgs.Count -eq 0) {
    $ComposeArgs = @("build")
}

& $composeExe -f $ComposeFile @ComposeArgs
exit $LASTEXITCODE
