$ErrorActionPreference='Continue'
$base='http://localhost:8081'

function Show-Err($e) {
    $s = $e.Exception.Response
    if ($null -eq $s) { return $e.Exception.Message }
    $code = [int]$s.StatusCode
    $sr = New-Object System.IO.StreamReader($s.GetResponseStream())
    $body = $sr.ReadToEnd()
    return "HTTP=$code BODY=$body"
}

Write-Host "== 1. GET balance wallet 101 =="
(Invoke-RestMethod -Uri "$base/api/v1/wallets/101/balance" -Method GET) | ConvertTo-Json -Compress

Write-Host "== 2. POST deposit 250 to wallet 101 =="
try { (Invoke-RestMethod -Uri "$base/api/v1/wallets/101/deposit" -Method POST -ContentType 'application/json' -Body '250') | ConvertTo-Json -Compress } catch { Show-Err $_ }

Write-Host "== 3. POST withdraw 99999 (exceeds balance) =="
try { Invoke-RestMethod -Uri "$base/api/v1/wallets/101/withdraw" -Method POST -ContentType 'application/json' -Body '99999' } catch { Show-Err $_ }

Write-Host "== 4. POST withdraw -100 (negative) =="
try { Invoke-RestMethod -Uri "$base/api/v1/wallets/101/withdraw" -Method POST -ContentType 'application/json' -Body '-100' } catch { Show-Err $_ }

Write-Host "== 5. POST register new user =="
$em = "e2e_" + (Get-Random) + "@example.com"
$body = "{`"name`":`"E2E User`",`"email`":`"$em`",`"password`":`"secret123`",`"phone`":`"9999999999`"}"
$uid = $null
try { $u = Invoke-RestMethod -Uri "$base/api/v1/users" -Method POST -ContentType 'application/json' -Body $body; $u | ConvertTo-Json -Compress; $uid = $u.id } catch { Show-Err $_ }
Write-Host "uid=$uid"

Write-Host "== 6. Create wallet for new user =="
if ($uid) { try { (Invoke-RestMethod -Uri "$base/api/v1/wallets/users/$uid" -Method POST) | ConvertTo-Json -Compress } catch { Show-Err $_ } }

Write-Host "== 7. Duplicate wallet for user 1 (expect 409) =="
try { Invoke-RestMethod -Uri "$base/api/v1/wallets/users/1" -Method POST } catch { Show-Err $_ }

Write-Host "== 8. Transfer 50 from 101 to 102 =="
try { (Invoke-RestMethod -Uri "$base/api/v1/transactions/transfer/101" -Method POST -ContentType 'application/json' -Body '{"receiverWalletId":102,"amount":50}') | ConvertTo-Json -Compress } catch { Show-Err $_ }

Write-Host "== 9. Self-transfer 101 to 101 (expect 400) =="
try { Invoke-RestMethod -Uri "$base/api/v1/transactions/transfer/101" -Method POST -ContentType 'application/json' -Body '{"receiverWalletId":101,"amount":10}' } catch { Show-Err $_ }

Write-Host "== 10. Transfer to wallet 9999 (expect 404, sender not debited) =="
try { Invoke-RestMethod -Uri "$base/api/v1/transactions/transfer/101" -Method POST -ContentType 'application/json' -Body '{"receiverWalletId":9999,"amount":100}' } catch { Show-Err $_ }

Write-Host "== 11. Balance wallet 101 after =="
(Invoke-RestMethod -Uri "$base/api/v1/wallets/101/balance" -Method GET) | ConvertTo-Json -Compress

Write-Host "== 12. History wallet 101 (top 4 rows) =="
$h = Invoke-RestMethod -Uri "$base/api/v1/transactions/history/101" -Method POST
$h | Select-Object -Last 4 | ForEach-Object { "$($_.transactionType) $($_.walletMoneyFlowType) amt=$($_.amount) cpty=$($_.counterPartyName) t=$($_.transactionTime)" }
