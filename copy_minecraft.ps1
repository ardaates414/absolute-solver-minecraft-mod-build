# Script to copy Minecraft 1.21.5 files to Gradle cache
# Usage: .\copy_minecraft.ps1 -MinecraftPath "C:\Path\To\Your\Minecraft\versions\1.21.5"

param(
    [string]$MinecraftPath = ""
)

$mcVersion = "1.21.5"

# If no path provided, try to find it
if ([string]::IsNullOrEmpty($MinecraftPath)) {
    Write-Host "Searching for Minecraft 1.21.5..."
    
    $searchPaths = @(
        "$env:APPDATA\.minecraft\versions\$mcVersion",
        "$env:USERPROFILE\.minecraft\versions\$mcVersion",
        "C:\Users\$env:USERNAME\AppData\Roaming\.minecraft\versions\$mcVersion"
    )
    
    foreach ($path in $searchPaths) {
        if (Test-Path $path) {
            if (Test-Path "$path\$mcVersion.jar") {
                $MinecraftPath = $path
                Write-Host "Found at: $MinecraftPath"
                break
            }
        }
    }
}

# Check if path exists
if ([string]::IsNullOrEmpty($MinecraftPath) -or -not (Test-Path $MinecraftPath)) {
    Write-Host "ERROR: Minecraft $mcVersion path not found!"
    Write-Host ""
    Write-Host "Please provide the path manually:"
    Write-Host '  .\copy_minecraft.ps1 -MinecraftPath "C:\Path\To\versions\1.21.5"'
    Write-Host ""
    Write-Host "Or navigate to your Minecraft versions folder and copy the path to the 1.21.5 folder."
    exit 1
}

# Check if required files exist
if (-not (Test-Path "$MinecraftPath\$mcVersion.jar")) {
    Write-Host "ERROR: Minecraft client jar not found at: $MinecraftPath\$mcVersion.jar"
    Write-Host "Files found in folder:"
    Get-ChildItem $MinecraftPath | Select-Object Name
    exit 1
}

Write-Host "Found Minecraft files at: $MinecraftPath"

# Copy to Gradle Loom cache
$loomCacheDir = "$env:USERPROFILE\.gradle\caches\fabric-loom\minecraft\$mcVersion"
New-Item -ItemType Directory -Force -Path $loomCacheDir | Out-Null

Write-Host "`nCopying files to Gradle cache..."

# Copy client jar
if (Test-Path "$MinecraftPath\$mcVersion.jar") {
    Copy-Item "$MinecraftPath\$mcVersion.jar" "$loomCacheDir\client.jar" -Force
    Write-Host "  ✓ Copied client.jar"
} else {
    Write-Host "  ✗ Client jar not found"
}

# Copy version JSON if exists
if (Test-Path "$MinecraftPath\$mcVersion.json") {
    Copy-Item "$MinecraftPath\$mcVersion.json" "$loomCacheDir\version.json" -Force
    Write-Host "  ✓ Copied version.json"
} else {
    Write-Host "  ! Version JSON not found (this is okay, Loom will try to download it)"
}

# Also try to copy to general Minecraft cache
$gradleMcCache = "$env:USERPROFILE\.gradle\caches\minecraft\decompiled"
New-Item -ItemType Directory -Force -Path $gradleMcCache | Out-Null
if (Test-Path "$MinecraftPath\$mcVersion.jar") {
    Copy-Item "$MinecraftPath\$mcVersion.jar" "$gradleMcCache\minecraft-$mcVersion-client.jar" -Force
    Write-Host "  ✓ Copied to general Minecraft cache"
}

Write-Host "`n✓ Files copied successfully!"
Write-Host "`nNow try running: gradlew.bat build"

