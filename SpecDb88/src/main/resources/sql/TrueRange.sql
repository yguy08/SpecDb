WITH cte AS
(
  SELECT
    m1.Symbol
   ,m1.Date AS Date
   ,m2.Date AS Dp
   ,m1.High AS High
   ,m1.Low AS Low
   ,m1.Close AS Close
   ,m2.Close AS Cp
   ,MAX(Round(m1.High - m1.Low,8),Abs(Round(m1.High- m2.Close,8)),Abs(Round(m1.Low-m2.Close,8)))
   AS TR
  FROM 
	Markets m1, Markets m2,
	(SELECT t1.Symbol,t2.Date AS day1,MAX(t1.Date) AS day2
	FROM markets t1, markets t2
	WHERE t1.Date < t2.Date
	AND t1.Symbol = t2.Symbol
	GROUP BY t2.Date) AS prev
WHERE m1.Date = prev.day1
AND m2.Date = prev.day2
AND m1.Symbol = m2.Symbol
)
SELECT * FROM cte
ORDER BY Date DESC