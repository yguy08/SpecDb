WITH TrueRange AS (
SELECT t1.*, MAX(Round(t1.high - t1.low,8),Abs(Round(t1.high - t2.low,8)),Abs(Round(t1.low - t2.close,8))) AS TrueRange from markets t1,markets t2 where t1.Date = t2.Date + 86400 AND t1.Symbol = t2.Symbol order by date DESC)
SELECT * FROM TrueRange