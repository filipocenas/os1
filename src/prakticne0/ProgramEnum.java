package prakticne0;

/* Implementirati klasu 'Karta' sa osobinama 'boja' i 'rang' koje predstavljaju
 * standardne osobine karata klasicnog spila od 52+2 karte.
 * 
 * Potrebno je predstaviti sledece boje: pik, karo, herc i tref, dok su dozvo-
 * ljene vrednosti za rang poredjane po velicini: brojevi od 2 do 10, zandar,
 * kraljica, kralj i kec. Takodje je potrebno predstaviti i dva dzokera, jedan
 * u boji, jedan ne.
 * 
 * Implementirati klasu 'Spil' ciji konstruktor kreira nov spil koji sadrzi sve
 * 54 razlicite karte. Takodje, implementirati sledece operacije:
 * 
 *   int velicina()            - vraca broj karata trenutno u spilu
 *   Karta uzmiOdGore()        - ukljanja gornju kartu i vraca je kao rezultat
 *   Karta uzmiOdDole()        - ukljanja donju kartu i vraca je kao rezultat
 *   Karta uzmiIzSredine()     - ukljanja nasumicno izabranu kartu i vraca je
 *   void staviGore(Karta)     - dodaje kartu na vrh spila
 *   void staviDole(Karta)     - dodaje kartu na dno spila
 *   void staviUSredinu(Karta) - dodaje kartu na nasumicno izabrao mesto u spilu
 *   void promesaj()           - nasumicno rasporedjuje karte trenutno u spilu
 * 
 * Napisati program koji implementira sledecu igru za 12 igraca. Igraci redom
 * vuku po jednu kartu sa vrha spila i okrecu je. Program ispisuje koji igrac
 * je izvukao koju kartu. Pobednik je onaj igrac (ili igraci) cija je karta
 * najjaca, pri cemu se ne gleda boja karte a dzokeri su jaci od svih ostalih
 * karata. Ako je bilo vise pobednika igra se ponavlja samo sa pobednicima dok
 * ne ostane samo jedan. Program ispisuje ime konacnog pobednika.
 * 
 * Unapred smisliti imena za igrace, kreirati jedan spil i promesati ga pre
 * igre. Pretpostaviti da u toku igre nece nestati karata u spilu.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum BojaEnum {

	PIK(0xDCA0), KARO(0xDCC0), HERC(0xDCB0), TREF(0xDCD0);

	private final int vrednost;

	private BojaEnum(int vrednost) {
		this.vrednost = vrednost;
	}

	public int getVrednost() {
		return vrednost;
	}
}

enum RangEnum {

	DVA(2), TRI(3), CETIRI(4), PET(5),
	SEST(6), SEDAM(7), OSAM(8),
	DEVET(9), DESET(10), ZANDAR(11),
	KRALJICA(13), KRALJ(14), KEC(1);

	private final int vrednost;

	private RangEnum(int vrednost) {
		this.vrednost = vrednost;
	}

	public int getVrednost() {
		return vrednost;
	}
}

class KartaEnum {

	private final BojaEnum boja;
	private final RangEnum rang;

	public KartaEnum(BojaEnum boja, RangEnum rang) {
		if (boja == null) {
			throw new IllegalArgumentException("boja");
		}
		if (rang == null) {
			throw new IllegalArgumentException("rang");
		}
		this.boja = boja;
		this.rang = rang;
	}

	
	public KartaEnum(boolean uBoji) {
		if (uBoji) {
			this.boja = BojaEnum.HERC;
		} else {
			this.boja = null;
		}
		this.rang = null; // Karta bez ranga je dzoker
	}

	public BojaEnum getBoja() {
		return boja;
	}

	public RangEnum getRang() {
		return rang;
	}

	@Override
	public int hashCode() {
		if (rang == null) {
			return 0;
		}
		int rezultat = boja.ordinal() + 1;
		rezultat = rezultat * 17 + rang.ordinal() + 1;
		return rezultat;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		KartaEnum that = (KartaEnum) object;
		if (!Objects.equals(this.rang, that.rang)) {
			return false;
		}
		if (rang == null) {
			return (this.boja == null) == (that.boja == null);
		}
		if (!Objects.equals(this.boja, that.boja)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (rang == null && boja != null) {
			return "\uD83C\uDCDF"; // Dzoker u boji
		}
		if (rang == null && boja == null) {
			return "\uD83C\uDCCF"; // Dzoker bez boje
		}
		return "\uD83C" + (char) (boja.getVrednost() + rang.getVrednost());
	}
}

class SpilEnum {

	private final List<KartaEnum> karte = new ArrayList<>(54);
	private final Random random;

	public SpilEnum() {
		this(3347);
	}

	public SpilEnum(long seed) {
		random = new Random(seed);
		for (BojaEnum boja : BojaEnum.values()) {
			for (RangEnum rang : RangEnum.values()) {
				karte.add(new KartaEnum(boja, rang));
			}
		}
		karte.add(new KartaEnum(true)); // Dzoker u boji
		karte.add(new KartaEnum(false)); // Dzoker bez boje
	}

	public int velicina() {
		return karte.size();
	}

	public KartaEnum uzmiOdGore() {
		return karte.remove(karte.size() - 1);
	}

	public KartaEnum uzmiOdDole() {
		return karte.remove(0);
	}

	public KartaEnum uzmiIzSredine() {
		return karte.remove(random.nextInt(karte.size()));
	}

	public void staviGore(KartaEnum karta) {
		karte.add(karta);
	}

	public void staviDole(KartaEnum karta) {
		karte.add(0, karta);
	}

	public void staviUSredinu(KartaEnum karta) {
		karte.add(random.nextInt(karte.size() + 1), karta);
	}

	public void promesaj() {
		Collections.shuffle(karte, random);
	}
}

class IgracEnum implements Comparable<IgracEnum> {

	private final String ime;
	private final List<KartaEnum> karte;
	private boolean aktivan;

	public IgracEnum(String ime) {
		if (ime == null) {
			throw new IllegalArgumentException("ime");
		}
		this.ime = ime;
		this.karte = new ArrayList<>();
		aktivan = true;
	}

	public String getIme() {
		return ime;
	}

	public void dodajKartu(KartaEnum karta) {
		karte.add(karta);
	}

	public boolean getAktivan() {
		return aktivan;
	}

	public void setAktivan(boolean aktivan) {
		this.aktivan = aktivan;
	}

	@Override
	public int hashCode() {
		return ime.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		IgracEnum that = (IgracEnum) object;
		return Objects.equals(this.ime, that.ime);
	}

	@Override
	public String toString() {
		String opis = karte.stream()
				.map(KartaEnum::toString)
				.collect(Collectors.joining());
		return String.format("%s %s %s", ime, aktivan ? ":)" : "  ", opis);
	}

	private static Comparator<RangEnum> KOMPARATOR_RANGOVA =
			Comparator.nullsLast(Comparator.naturalOrder());
	private static Comparator<KartaEnum> KOMPARATOR_KARATA =
			Comparator.comparing(KartaEnum::getRang, KOMPARATOR_RANGOVA);

	@Override
	public int compareTo(IgracEnum that) {
		// Igraci se porede po kolicini i jacini izvucenih karata
		int result = this.karte.size() - that.karte.size();
		for (int i = 0; i < karte.size() && result == 0; i++) {
			result = Objects.compare(
					this.karte.get(i),
					that.karte.get(i),
					KOMPARATOR_KARATA);
		}
		return result;
	}
}

public class ProgramEnum {

	public static void main(String[] args) {

		List<IgracEnum> igraci = Stream.of("Paja", "Kaja", "Bata", "Maja",
				                           "Taja", "Pata", "Gaja", "Raja",
				                           "Fata", "Baja", "Caja", "Kata")
				.map(IgracEnum::new)
				.collect(Collectors.toList());

		SpilEnum spil = new SpilEnum();
		spil.promesaj();

		boolean pobeda;
		do {
			igraci.stream() // Svi aktivni igraci vuku karte
					.filter(IgracEnum::getAktivan)
					.forEach(igrac -> igrac.dodajKartu(spil.uzmiOdGore()));
			IgracEnum pobednik = igraci.stream() // Trazi se pobednik
					.filter(IgracEnum::getAktivan)
					.max(Comparator.naturalOrder())
					.get();
			igraci.stream() // Svi koji nisu pobedili vise nisu aktivni
					.filter(IgracEnum::getAktivan)
					.filter(igrac -> pobednik.compareTo(igrac) != 0)
					.forEach(igrac -> igrac.setAktivan(false));
			pobeda = igraci.stream() // Da li je bio samo jedan pobednik
					.filter(IgracEnum::getAktivan)
					.count() == 1;
		} while (!pobeda);

		igraci.stream()
				.forEach(System.out::println);

	}
}

