SELECT m.*
FROM markets m INNER JOIN
	(
		SELECT Symbol, Min(Close) Close
		FROM markets		
		WHERE date > 1501977600
		GROUP BY Symbol
	) t ON m.Symbol = t.Symbol AND m.Close >= t.Close
	WHERE date = (SELECT max(Date) from markets)