SELECT m.*     
FROM markets m INNER JOIN
    (
        SELECT Symbol, MIN(Date) MinDate
        FROM markets
        GROUP BY Symbol
    ) t ON m.Symbol = t.Symbol AND m.Date = t.MinDate