package engine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Maksu {
	private double summa;
	private String tuote;
	private String henkilo;
	private LocalDate paivamaara;

	public Maksu(double summa, String tuote, String henkilo, LocalDate paivamaara) {
		this.summa = summa;
		this.tuote = tuote;
		this.henkilo = henkilo;
		this.paivamaara = paivamaara;
	}

	public double getSumma() {
		return summa;
	}

	public void setSumma(double summa) {
		this.summa = summa;
	}

	public String getTuote() {
		return tuote;
	}

	public void setTuote(String tuote) {
		this.tuote = tuote;
	}

	public String getHenkilo() {
		return henkilo;
	}

	public void setHenkilo(String henkilo) {
		this.henkilo = henkilo;
	}

	public LocalDate getPaivamaara() {
		return paivamaara;
	}

	public void setPaivamaara(LocalDate paivamaara) {
		this.paivamaara = paivamaara;
	}

	public int getMonth() {
		return paivamaara.getMonthValue();
	}

	public int getYear() {
		return paivamaara.getYear();
	}

	@Override
	public String toString() {
		DateTimeFormatter formaatti = DateTimeFormatter.ofPattern("d.M.yyyy");
		return formaatti.format(paivamaara) + " " + summa + " " + tuote + " " + henkilo;
	}
}
