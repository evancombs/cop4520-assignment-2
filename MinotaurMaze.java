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
  AtomicBoolean cupcake;

  // allVisited can be announced by any guest to stop entering the labyrinth,
  // claiming that all guests have visited.
  AtomicBoolean allVisited;

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
      if (cupcake.get())
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
          System.out.println("Guest " + Thread.currentThread().getName() +
                             " finds a cupcake, but doesn't eat it!");
        }
      }
      else
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
            System.out.println("* The leader determines that all guests have visited the labyrinth, and announces it!");
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
  }

}
