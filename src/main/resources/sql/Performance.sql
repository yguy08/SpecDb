SELECT ACCOUNT_BAL.COUNTER, MARKETS.COUNTER,ACCOUNT_BAL.AMOUNT, MARKETS.CLOSE
FROM ACCOUNT_BAL
INNER JOIN MARKETS ON ACCOUNT_BAL.COUNTER=MARKETS.BASE
WHERE MARKETS.COUNTER = 'BTC' AND MARKETS.DATE = (SELECT MAX(DATE) AS DATE FROM ACCOUNT_BAL)

Need to smooth volume...needs to be done by exchange b/c you can't be sure every exchange will have similar volume stats?

convert to BTC vol:
- if counter is BTC then it is Vol * BTC_price (USD)
- if counter is ETH then it is Vol * ETH_price * BTC_Price