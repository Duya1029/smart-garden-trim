# 放行防火墙端口，允许手机 / 其他电脑通过局域网访问前端 5173 与后端 9999
$log = 'C:\Users\28467\AppData\Local\Temp\open-firewall.log'
try {
    $ports = 5173, 9999
    foreach ($p in $ports) {
        $name = "SmartGarden-Port-$p"
        Remove-NetFirewallRule -DisplayName $name -ErrorAction SilentlyContinue
        New-NetFirewallRule -DisplayName $name -Direction Inbound -Protocol TCP -LocalPort $p -Action Allow -Profile Any | Out-Null
    }
    "OK 已放行端口 $($ports -join ',') $(Get-Date -Format 'HH:mm:ss')" | Out-File $log -Encoding utf8
} catch {
    "FAIL $($_.Exception.Message)" | Out-File $log -Encoding utf8
}
