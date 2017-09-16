#Design
---
##Use Cases
---
###View Latest Trading Account Balance

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

###View Open Positions

1. Trader launches app
2. System retrieves the latest open positions from the db
3. System performs the necessary currency conversions
4. System calculates performance metrics
5. System displays the latest open positions to the trader  


