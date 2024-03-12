package teorijske1;
/*
 * Napisati program koji implementira dve niti: jednu definisanu pomoću klase Thread koja ispisuje prvih 10,000,000 pozitivnih celih brojeva, 
 * i drugu definisanu pomoću interfejsa Runnable koja ispisuje prvih 10,000,000 negativnih celih brojeva. Glavna nit pokrece ove dve niti, 
 * ceka dve sekunde, posle čega ih prekida i čeka da obe u potpunosti završe svoj rad.
 */
public class PrekidanjeNiti {
	
	public static void main(String[] args) throws InterruptedException {
		
		PozitivnaNit p = new PozitivnaNit();
		p.setName("Pozitivni");
		
		NegativnaNit n = new NegativnaNit();
		Thread neg = new Thread(n);
		neg.setName("Negativni");
		
		p.start();
		neg.start();
		
		Thread.sleep(2000);
		
		p.interrupt();
		neg.interrupt();
		
		p.join();
		neg.join();
		
		System.out.println("Kraj");
		
	}
	
	
	
	
}

class PozitivnaNit extends Thread {
	@Override
	public void run() {
		for(int i = 1; i <= 10_000_000 && !interrupted(); i++) {
			System.out.println(i);
		}
	}
}

class NegativnaNit implements Runnable {

	@Override
	public void run() {
		for(int i = -1; i >= -10_000_000 && !Thread.interrupted(); i--) {
			System.out.println(i);
		}
	}
	
}