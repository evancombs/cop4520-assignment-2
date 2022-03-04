// COP4520 Spring 2022
// Evan Combs
// Assignment 2, Problem 2: Minotaur's Crystal Vase
import java.util.ArrayList;
import java.lang.Runnable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

class MinotaurVase
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
      System.out.println("Error -- Run with \"java MinotaurVase.java <number of guests>\"");
      return;
    }

    ArrayList<Thread> guests = new ArrayList<>();

    Showroom showroom = new Showroom(numGuests);
    for (int i = 0; i < numGuests; i++)
    {
      Thread guest = new Thread(showroom, String.valueOf(i));
      guests.add(guest);
    }

    for (Thread guest : guests)
        guest.start();
  }

}

// Showroom simulates the guests trying to view the vase
class Showroom implements Runnable
{
  ShowroomQueue queueLock;

  public Showroom(int numGuests)
  {
     queueLock = new ShowroomQueue(numGuests);
  }

  public void run()
  {
    // All threads will reach here, and hang while the guests (thread) that has
    // acquired the lock looks at the vase;
    enjoyParty();

    boolean interestedInVase = true;
    while(interestedInVase)
    {
      System.out.println("Guest " + Thread.currentThread().getName() +
                         " is interested in the vase, and gets in the queue.");
      queueLock.lock();
      try
      {
        lookAtVase();
      }
      finally
      {
        queueLock.unlock();
      }
      // The vase is so enthralling that a guest may decide to get back in line again.

      if (Math.random() <= 0.5)
        continue;
      else
      {
        interestedInVase = false;
      }
    }
  }


  public void lookAtVase()
  {
    System.out.println("   Guest " + Thread.currentThread().getName() +
                       " looks at the vase, and is very impressed.");
  }

  // Adds a small bit of random time
  public void enjoyParty()
  {
    try
    {
      System.out.println("Guest " + Thread.currentThread().getName() +
                         " decides to enjoy the rest of the party.");
      Thread.sleep((long) Math.random());
    }
    catch(Exception e)
    {

    }
  }
}

// ShowroomQueue is a circular array-based lock
// Modeled after ALock from Art of Multiprocessor Programming, pg 150
class ShowroomQueue
{
  int size;
  AtomicInteger tail;
  volatile boolean[] flag;
  ThreadLocal<Integer> threadIndex;

  public ShowroomQueue(int capacity)
  {
    // Setup intial lock properties
    size = capacity;
    tail = new AtomicInteger(0);
    flag = new boolean[capacity];
    flag[0] = true;

    threadIndex = new ThreadLocal<Integer>()
    {
      protected Integer initialValue()
      {
        return 0;
      }
    };

  }

  public void lock()
  {
    // Tail always increments, since the queue is circular
    int slot = tail.getAndIncrement() % size;

    // Store the slot this thread is waiting at
    threadIndex.set(slot);

    // Hang here until this threads slot becomes unlocked
    while (!flag[slot])
      ; // wait
  }

  public void unlock()
  {
    int slot = threadIndex.get();

    // Flag that this slot is now free
    flag[slot] = false;

    // "Notify" the next thread
    flag[(slot + 1) % size] = true;
  }
}
