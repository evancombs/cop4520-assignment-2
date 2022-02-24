# cop4520-assignment-2
## Problem 1: Minotaur's Birthday Party

Compile with: 
  ```"javac MinotaurMaze.java"```
  
Run with: ```"java MinotaurMaze <number of guests>"```

Outputs to console, with a log of the actions of the guests in the maze.

My solution to this problem makes use of a strategy decided beforehand.
The guests elect a leader, thread 0 in the program. There is only one leader.
When a guest visits the maze and finds a cupcake, they eat it if and only if
they haven't eaten a cupcake before. Non-leader guests do NOT request new cupcakes.

The leader tracks every time they visit the maze and find an empty plate. This means that
at least one guest has been here before. When the leader has found an empty plate
N times, that means that every guest has visited the maze at least once, since
an empty plate only arises from a unique guest eating a cupcake.

My solution makes use of two atomic variables, but they are not used to communicate between guests except for the status
of the cupcake, and the status of the end of the "game".


## Problem 2: Minotaur’s Crystal Vase
