package teorijske1;

/*
 * Napisati program koji implementira dve niti: jednu definisanu pomoću klase Thread koja ispisuje prvih 
 * 10000 pozitivnih celih brojeva, i drugu definisanu pomoću interfejsa Runnable koja ispisuje prvih 
 * 10000 negativnih celih brojeva. 
 * Nitima postaviti i imena
 */

public class Niti1 {
	public static void main(String[] args) {
		NitPozitivni p = new NitPozitivni();
		p.setName("Pozitivni");
		NitNegativni n = new NitNegativni();
		Thread neg = new Thread(n);
		neg.setName("Negativni");

		p.start();
		neg.start();

	}
}

class NitPozitivni extends Thread {
	@Override
	public void run() {
		for (int i = 1; i <= 100; i++) {
			System.out.println(i);
		}
	}
}

class NitNegativni implements Runnable {

	@Override
	public void run() {
		for (int i = -1; i >= -100; i--) {
			System.out.println(i);
		}
	}

}
