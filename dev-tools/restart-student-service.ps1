# 重启学生系统的 Web 服务，恢复数据库连接
$log = 'C:\Users\28467\AppData\Local\Temp\restart-student-service.log'
try {
    Restart-Service -Name 'NIApplicationWebServer' -Force -ErrorAction Stop
    Start-Sleep -Seconds 3
    "OK 学生系统服务已重启 $(Get-Date -Format 'HH:mm:ss')" | Out-File $log -Encoding utf8
} catch {
    "FAIL $($_.Exception.Message)" | Out-File $log -Encoding utf8
}
