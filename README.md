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

The "randomness" of the guest scheduling is simulated by the random nature of thread
assignment. Each guest (thread) continually enters the maze; it is impossible to know
in what order they do so.

My solution makes use of two atomic variables, but they are not used to communicate between guests except for the status of the cupcake, and the status of the end of the "game" (so the guests know when to
stop entering the maze).


## Problem 2: Minotaur’s Crystal Vase

Compile with:
  ```"javac MinotaurVase.java"```

Run with: ```"java MinotaurVase <number of guests>"```

Outputs to console, with a log of the guests who viewed the vase.

For this problem, we are given three approaches to discuss and choose from.

Approach 1) This approach is similar to a spin lock, where each guests repeatedly
waits at the door to see if it will open, and the first guest who happens to see it
open enters.
There is a major problem in this approach if any guest decides they want to view
the vase again, increasing the crowding around the door. This could cause some guests
to get to see the vase multiple times, while others never get to see it. In this way,
this approach is NOT starvation-free.


Approach 2) This approach is most similar to a blocking approach, where guests
who see that the showroom is "Busy" are free to enjoy the party instead. This has
a similar problem to approach 1, however, in that a guest has no guarantee that
they will get to see the vase, for example if it is always "Busy" when they go
to check.


Approach 3) This approach is the best approach to ensure that all guests who want to
will eventually get to see the vase, i.e. starvation-free. The downside to this,
however, is that like with Approach 1, guests waiting for the vase are unable to
enjoy the party while waiting.

Since the Minotaur wants to show off his vase to everyone who wants to see it,
and wants to ensure that everyone who wants to is able to, this approach is the
best to ensure this.

### Implementation
For my implementation I have implemented a circular array-based lock similar
to ALock from pg 150 of the textbook. Threads (guests) maintain a slot in a queue and
use a volatile boolean array to track which threads acquire the lock.

The situation is simulated in the Showroom class, where guests can either look at the
vase (and be impressed) or spend a random amount of time at the party. All guests
will want to look at the vase at least once, and thus will get in line; the queue
lock ensures that all guests who queue will eventually get to see the vase (starvation-freedom).
