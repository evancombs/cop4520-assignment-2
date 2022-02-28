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

    Showroom showroom = new Showroom();
    for (int i = 0; i < numGuests; i++)
    {
      Thread guest = new Thread(showroom, String.valueOf(i));
      guests.add(guest);
    }

    for (Thread guest : guests)
        guest.start();
  }

}

class Showroom extends Runnable
{

  public void run()
  {

  }


  public void lookAtVase()
  {
    System.out.println("Guest " + Thread.currentThread().getName() +
                       " looks at the vase, and is very impressed.");
  }
}
