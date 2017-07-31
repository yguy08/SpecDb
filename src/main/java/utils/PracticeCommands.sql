WITH TrueRange AS (
SELECT t1.*, MAX(Round(t1.high - t1.low,8),Abs(Round(t1.high - t2.low,8)),Abs(Round(t1.low - t2.close,8))) AS TrueRange from markets t1,markets t2 where t1.Date = t2.Date + 86400 AND t1.Symbol = t2.Symbol order by date DESC)
SELECT * FROM TrueRange;

WITH C_Close AS (
SELECT Symbol, Date AS C_Date, Close AS C_Close from markets where date = (SELECT MAX(Date) from markets where Symbol = 'STEEMBTC') AND Symbol = 'STEEMBTC'),
High AS (Select C_Date - Date AS High from markets,C_Close where Close > (SELECT C_Close from C_Close) AND markets.Symbol = 'STEEMBTC' order by date DESC limit 1),
Low AS (Select C_Date - Date AS Low from markets,C_Close where Close < (SELECT C_Close from C_Close) AND markets.Symbol = 'STEEMBTC' order by date DESC limit 1)
SELECT Max(High,Low) from High, Low;
