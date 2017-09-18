# Design
## Use Cases
### View Latest Trading Account Balance

1. Trader launches app
2. System collects exchange balances using exchange api and updates balances in the database
3. System retrieves the latest balances from the db
4. System performs the neccessary currency conversions
5. System calculates performance metrics
6. System displays the latest trading account balance  

**Alternative:** *App Running*  
1.a: Trader clicks refresh button on main screen  
Return to primary scenario at step 2  

**Alternative:** *Update fails*  
At step 2, system fails to update balances in the database  
System continues with steps 3, 4 & 5.  
At step 6, System displays the latest trading account balance with an informative message that the account balance may be out of date by a few minutes  

**Alternative:** *Retrieval fails*  
At step 3, retrieval from the database fails  
Display message to close app and try again or contact administrator  

**Alternative:** *Up on 1 day*  
At step 5, system calculates that trader is up on the 1 day  
Display up on 1 day special status message  

### View Open Positions

1. Trader launches app
2. System retrieves the latest open positions from the db
3. System performs the necessary currency conversions
4. System calculates performance metrics
5. System displays the latest open positions to the trader  
  
**Alternative:** *Retrieval fails*  
At step 2, system fails to retrieve open positions from the db  
Display message to close app and try again or contact administrator  

**Alternative:** *Position needs to be close*  
At step 4, system calculates that a position needs to be closed  
Continue to step 5 and display close button along side the position that needs to be closed

**Alternative:** *Auto trade*  
At step 4, system calculates that a position needs to be closed  
Send user email and let them know they have until the end of the day to check and close or the trade will be closed  

### View New Entries  

1. Trader launches app
2. System retrieves latest prices from exchange api
3. System performs the neccessary entry filtering
4. System calculates entry position sizing
5. System adds entries to the database
6. System retrieves the latest entries from the database
7. System displays the latest entries to the trader

