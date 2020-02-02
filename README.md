# Simple Vote Listener
Simple Vote Listener is a vote listener for NuVotifier. It allows you to specify commands 
in the config file that will be executed when a vote is received from NuVotifier.
These commands are often used to give rewards to players for voting on server/voting lists. 

That's all there is to it! As simple and lightweight as possible.

# Config file
``` 
Params available:  %player%
   commands = [
     "adminpay %player% 250",
     "title %player% title {\"text\":\"Thanks for voting!\",\"bold\":true,\"color\":\"red\"}"
   ]
```
The parameter %player% is available to target the player that voted.