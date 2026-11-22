# 自动启用 SQL Server (SQLEXPRESS) 的 TCP/IP 并固定端口 1433
$log = 'C:\Users\28467\AppData\Local\Temp\sql-tcp-setup.log'
try {
    $tcp = 'HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQLServer\SuperSocketNetLib\Tcp'
    # 启用 TCP/IP 协议
    Set-ItemProperty -Path $tcp -Name Enabled -Value 1 -Type DWord
    # 固定监听端口 1433（IPAll 对所有 IP 生效）
    Set-ItemProperty -Path "$tcp\IPAll" -Name TcpPort -Value '1433' -Type String
    Set-ItemProperty -Path "$tcp\IPAll" -Name TcpDynamicPorts -Value '' -Type String
    # 重启 SQL Server 服务使配置生效
    Restart-Service 'MSSQL$SQLEXPRESS' -Force
    "OK $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" | Out-File $log -Encoding utf8
} catch {
    "FAIL $($_.Exception.Message)" | Out-File $log -Encoding utf8
}
