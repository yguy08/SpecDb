WITH TrueRange AS (
SELECT t1.*, MAX(Round(t1.high - t1.low,8),Abs(Round(t1.high - t2.low,8)),Abs(Round(t1.low - t2.close,8))) AS TrueRange 
from markets t1,markets t2 
where t1.Date = t2.Date + 86400 
AND t1.Symbol = t2.Symbol 
order by date DESC)
SELECT * FROM TrueRange;

WITH C_Close AS (
SELECT Symbol, Date AS C_Date, Close AS C_Close from markets where date = (SELECT MAX(Date) from markets where Symbol = 'STEEMBTC') AND Symbol = 'STEEMBTC'),
High AS (Select C_Date - Date AS High from markets,C_Close where Close > (SELECT C_Close from C_Close) AND markets.Symbol = 'STEEMBTC' order by date DESC limit 1),
Low AS (Select C_Date - Date AS Low from markets,C_Close where Close < (SELECT C_Close from C_Close) AND markets.Symbol = 'STEEMBTC' order by date DESC limit 1)
SELECT Max(High,Low) from High, Low;

SELECT m1.Symbol,m1.Date,m1.close,m1.close-m2.close
FROM
	Markets m1, Markets m2,
	(SELECT t1.Symbol,t2.Date AS day1, MAX(t1.Date) AS day2
	FROM Markets t1, Markets t2
	WHERE t1.Date < t2.Date
	AND t1.Symbol = t2.Symbol
	GROUP BY t2.Date) AS prev
WHERE m1.Date=prev.day1
AND m2.Date=prev.day2
AND m1.Symbol = m2.Symbol

WITH cte AS
(
  SELECT 
    t.[Time]
   ,[H-L]    = Round([BID-HIGH]-[BID-LOW],5) 
   ,[H-Cp]   = Abs(Round([BID-HIGH]-[prev_BID-CLOSE],5)) 
   ,[L-Cp]   = Abs(Round([BID-LOW]-[prev_BID-CLOSE],5))
  FROM (SELECT *, 
               [prev_BID-CLOSE]  = LAG([BID-CLOSE]) OVER(ORDER BY [Time])
        FROM [#tbl_GBP-USD_1-Day]) AS t
)
SELECT *
FROM cte
CROSS APPLY (SELECT MAX(v) AS v
             FROM ( VALUES ([H-L]),([H-Cp]),([L-Cp])) AS value(v)
) AS sub([TR]);

WITH cteRange AS (
    SELECT 
        t.[Time],
        Round(t.[BID-HIGH]-t.[BID-LOW],5) AS [H-L],
        Abs(Round(t.[BID-HIGH]-t.[prev_BID-CLOSE],5)) AS [H-Cp],
        Abs(Round(t.[BID-LOW]-t.[prev_BID-CLOSE],5)) AS [L-Cp]
    FROM (
        SELECT *, [prev_BID-CLOSE]  = LAG([BID-CLOSE]) OVER (ORDER BY [Time])
        FROM [#tbl_GBP-USD_1-Day]
    ) AS t
), cteTrueRange AS (
    SELECT *
    FROM cteRange
    CROSS APPLY (
        SELECT MAX(v) AS v
        FROM (VALUES ([H-L]), ([H-Cp]), ([L-Cp])) AS value(v)
    ) AS sub([TR])
), cteTrueRange10 AS (
    SELECT
        *,
        LAG([TR], 1) OVER (ORDER BY [Time]) AS [TR1],
        LAG([TR], 2) OVER (ORDER BY [Time]) AS [TR2],
        LAG([TR], 3) OVER (ORDER BY [Time]) AS [TR3],
        LAG([TR], 4) OVER (ORDER BY [Time]) AS [TR4],
        LAG([TR], 5) OVER (ORDER BY [Time]) AS [TR5],
        LAG([TR], 6) OVER (ORDER BY [Time]) AS [TR6],
        LAG([TR], 7) OVER (ORDER BY [Time]) AS [TR7],
        LAG([TR], 8) OVER (ORDER BY [Time]) AS [TR8],
        LAG([TR], 9) OVER (ORDER BY [Time]) AS [TR9]
    FROM cteTrueRange
)
SELECT [Time], [H-L], [H-Cp], [L-Cp], [TR], [TRA]
    FROM cteTrueRange10
    CROSS APPLY (
        SELECT CASE WHEN [TR9] IS NOT NULL THEN AVG(v) END AS v
        FROM (VALUES ([TR]), ([TR1]), ([TR2]), ([TR3]), ([TR4]), ([TR5]), ([TR6]), ([TR7]), ([TR8]), ([TR9])) AS value(v)
    ) AS sub([TRA]);
