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

Write-Host "== Empty DB: history for wallet 1 should 404 =="
try { Invoke-RestMethod -Uri "$base/api/v1/transactions/history/1" -Method POST } catch { Show-Err $_ }

Write-Host "== Register Alice =="
$em = "alice_" + (Get-Random) + "@example.com"
$body = "{`"name`":`"Alice`",`"email`":`"$em`",`"password`":`"pw`",`"phone`":`"9000000001`"}"
try { $u = Invoke-RestMethod -Uri "$base/api/v1/users" -Method POST -ContentType 'application/json' -Body $body; ($u | ConvertTo-Json -Compress); $uid = $u.id } catch { Show-Err $_; exit 1 }

Write-Host "== Register Bob =="
$em2 = "bob_" + (Get-Random) + "@example.com"
$body2 = "{`"name`":`"Bob`",`"email`":`"$em2`",`"password`":`"pw`",`"phone`":`"9000000002`"}"
try { $u2 = Invoke-RestMethod -Uri "$base/api/v1/users" -Method POST -ContentType 'application/json' -Body $body2; ($u2 | ConvertTo-Json -Compress); $uid2 = $u2.id } catch { Show-Err $_; exit 1 }

Write-Host "== Create wallet for Alice (id=$uid) and Bob (id=$uid2) =="
$wA = Invoke-RestMethod -Uri "$base/api/v1/wallets/users/$uid" -Method POST
$wB = Invoke-RestMethod -Uri "$base/api/v1/wallets/users/$uid2" -Method POST
($wA | ConvertTo-Json -Compress)
($wB | ConvertTo-Json -Compress)

Write-Host "== Deposit 500 into Alice wallet $($wA.walletId) =="
(Invoke-RestMethod -Uri "$base/api/v1/wallets/$($wA.walletId)/deposit" -Method POST -ContentType 'application/json' -Body '500') | ConvertTo-Json -Compress

Write-Host "== Transfer 200 Alice -> Bob =="
(Invoke-RestMethod -Uri "$base/api/v1/transactions/transfer/$($wA.walletId)" -Method POST -ContentType 'application/json' -Body "{`"receiverWalletId`":$($wB.walletId),`"amount`":200}") | ConvertTo-Json -Compress

Write-Host "== Balance check =="
"ALICE: " + ((Invoke-RestMethod -Uri "$base/api/v1/wallets/$($wA.walletId)/balance" -Method GET) | ConvertTo-Json -Compress)
"BOB:   " + ((Invoke-RestMethod -Uri "$base/api/v1/wallets/$($wB.walletId)/balance" -Method GET) | ConvertTo-Json -Compress)

Write-Host "== History for Alice (should have 1 DEPOSIT INCOMING + 1 TRANSFER OUTGOING) =="
$h = Invoke-RestMethod -Uri "$base/api/v1/transactions/history/$($wA.walletId)" -Method POST
$h | ForEach-Object { "$($_.transactionType) $($_.walletMoneyFlowType) amt=$($_.amount) cpty=$($_.counterPartyName)" }
