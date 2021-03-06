// COP4520 Spring 2022
// Evan Combs
// Assignment 2, Problem 1: Minotaur's Birthday Party
import java.util.ArrayList;
import java.lang.String;
import java.util.concurrent.atomic.AtomicBoolean;

class MinotaurMaze
{
  public static void main(String[] args)
  {
    int numGuests = 0;
    try
    {
      numGuests = Integer.parseInt(args[0]);
    }
    catch(Exception e)
    {
      System.out.println(e);
      System.out.println("Error -- Run with \"java MinotaurMaze.java <number of guests>\"");
      return;
    }

    ArrayList<Thread> guests = new ArrayList<>();

    // Create a new labyrinth for the guests to enter
    Labyrinth labyrinth = new Labyrinth(numGuests);
    for (int i = 0; i < numGuests; i++)
    {
      // We can name the guests and elect guest 0 as the leader.
      Thread guest = new Thread(labyrinth, String.valueOf(i));
      guests.add(guest);
    }

    for (Thread guest : guests)
        guest.start();
  }
}


class Labyrinth implements Runnable
{
  // True if there is a cupcake at the end of the maze; false otherwise.
  // Atomic may be overkill with the lock
  AtomicBoolean cupcake;

  // allVisited can be announced by any guest to stop entering the labyrinth,
  // claiming that all guests have visited.
  AtomicBoolean allVisited;

  MazeLock lock = new MazeLock();

  int numGuests;

  public Labyrinth(int numGuests)
  {
    this.numGuests = numGuests;
    cupcake = new AtomicBoolean(true);
    allVisited = new AtomicBoolean(false);
  }
  // If a guest eats a cupcake if and only if it is the first time they have
  // reached the cupcake. A guest does not request a new cupcake, unless they're
  // the leader.
  public void run()
  {
    // Has this guest eaten a cupcake?
    boolean eatenCupcake = false;

    // Only the leader uses this value
    int leaderCount = 0;


    while(!allVisited.get())
    {
      lock.lock();

      // Ensures that no guests enter the maze after the leader announces they are done
      if (allVisited.get())
      {
        lock.unlock();
        break;
      }

      // Enter the maze and see what they find
      try
      {
        if (cupcake.get()) // There is a cupcake present
        {
          // Theres a cupcake here! If this guest hasn't had a cupcake yet, they
          // eat it.
          if (!eatenCupcake)
          {
            eatenCupcake = true;
            cupcake.set(false);
            System.out.println("Guest " + Thread.currentThread().getName() +
                               " finds a cupcake and eats it!");
          }
          else
          {
            // There is a cupcake here, but according to our strategy, we don't eat it
            System.out.println("Guest " + Thread.currentThread().getName() +
                               " finds a cupcake, but doesn't eat it!");
          }
        }
        else // There is NOT a cupcake present
        {
          // The cupcake has been eaten! If the thread is the leader, increment
          // their count and request a new cupcake.
          if(Thread.currentThread().getName().equals("0"))
          {
            leaderCount++;
            System.out.println("* The leader finds an empty plate! They increment their count, and requests a new cupcake.");
            cupcake.set(true);
            if (leaderCount == numGuests)
            {
              System.out.println("*** The leader determines that all guests have visited the labyrinth, and announces it!");
              allVisited.set(true);
            }
          }
          else
          {
            System.out.println("Guest " + Thread.currentThread().getName() +
                               " finds an empty plate! They don't request a new cupcake.");
          }
        }
      }
      finally
      {
        lock.unlock();
      }
    }
  }
}

// A very simple test-and-set lock that ensure that only one guest can be in the maze at a time.
class MazeLock
{
  private AtomicBoolean guestInMaze = new AtomicBoolean(false);

  public void lock()
  {
    // All threads hang here until the thread with the lock calls unlock()
    while(guestInMaze.getAndSet(true))
      ; // wait
  }

  public void unlock()
  {
    guestInMaze.set(false);
  }
}
