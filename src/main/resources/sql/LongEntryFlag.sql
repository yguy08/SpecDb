SELECT m.* FROM markets m INNER JOIN
							(SELECT Base,Counter, Exchange, Max(Close) Close FROM markets WHERE date > 1500508800
							GROUP BY Base,Counter,Exchange) t ON m.Base = t.Base
							AND m.Counter = t.Counter AND m.exchange = t.Exchange AND m.Close >= t.Close
							WHERE date = (SELECT Max(Date) from markets)
							
SELECT * FROM markets where date > 1471478400