# Event_Tracker

This simulates SNS's event tracking system, which stores an event in chronological order and allows access to these timelines.

FakeRandHeight.java provides a random height for a skip list, but for this project, the random height for each event insertion was set up to ideal situation (50 for 1st floor, 25 for 2nd floor.. and so on).

The event tracking system takes a list of actions text file as a command line argument, which includes adding an event, displaying an events timeline, deleting an event, and printing the entire skip list.

To store events in chronological order, I used a skip list data structure (key: the time of an event, value: event's title).
