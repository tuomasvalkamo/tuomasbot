package engine;

import java.util.ArrayList;
import java.util.List;

public class MaksuLista {
	private List<Maksu> maksut;

	public MaksuLista() {
		this.maksut = new ArrayList<>();
	}

	public void add(Maksu uusiMaksu) {
		this.maksut.add(uusiMaksu);
	}

	public void delete(int rivi) {
		this.maksut.remove(rivi - 1);
	}

	public int sum() {
		int summa = 0;
		for (int i = 0; i < maksut.size(); i++) {
			summa += this.maksut.get(i).getSumma();
		}

		return summa;
	}

	public double sumMonth(int kk, int year) {
		double summa = 0.0;

		for (int i = 0; i < maksut.size(); i++) {
			if (maksut.get(i).getMonth() == kk && maksut.get(i).getYear() == year) {
				summa += maksut.get(i).getSumma();
			}
		}

		return summa;
	}

	public String findMonthPayments(int kk, int year) {
		String merkkijono = "";
		int rivi = 1;

		for (int i = 0; i < maksut.size(); i++) {
			if (maksut.get(i).getMonth() == kk && maksut.get(i).getYear() == year) {
				merkkijono += "\n" + rivi + ". " + maksut.get(i).toString();
				rivi++;
			}
		}

		return merkkijono;
	}

	public String findYearPayments(int y) {
		String merkkijono = "";
		int rivi = 1;

		for (int i = 0; i < maksut.size(); i++) {
			if (maksut.get(i).getYear() == y) {
				merkkijono += "\n" + rivi + ". " + maksut.get(i).toString();
				rivi++;
			}
		}

		return merkkijono;
	}

	public String search(int kk, int year, String henkilo) {
		String merkkijono = "";
		int rivi = 1;
		if (kk == 0 && year != 0) {
			for (int i = 0; i < maksut.size(); i++) {
				if (maksut.get(i).getHenkilo().equals(henkilo) && maksut.get(i).getYear() == year) {
					merkkijono += "\n" + rivi + ". " + maksut.get(i).toString();
					rivi++;
				}
			}

			return merkkijono;
		} else if (kk != 0 && year != 0) {
			for (int i = 0; i < maksut.size(); i++) {
				if (maksut.get(i).getHenkilo().equals(henkilo) && maksut.get(i).getYear() == year
						&& maksut.get(i).getMonth() == kk) {
					merkkijono += "\n" + rivi + ". " + maksut.get(i).toString();
					rivi++;
				}
			}

			return merkkijono;
		} else {
			for (int i = 0; i < maksut.size(); i++) {
				if (maksut.get(i).getHenkilo().equals(henkilo)) {
					merkkijono += "\n" + rivi + ". " + maksut.get(i).toString();
					rivi++;
				}
			}

			return merkkijono;
		}
	}

	public double searchSum(int kk, int year, String henkilo) {
		double summa = 0;
		if (kk == 0 && year != 0) {
			for (int i = 0; i < maksut.size(); i++) {
				if (maksut.get(i).getHenkilo().equals(henkilo) && maksut.get(i).getYear() == year) {
					summa += maksut.get(i).getSumma();
				}
			}

			return summa;
		} else if (kk != 0 && year != 0) {
			for (int i = 0; i < maksut.size(); i++) {
				if (maksut.get(i).getHenkilo().equals(henkilo) && maksut.get(i).getYear() == year
						&& maksut.get(i).getMonth() == kk) {
					summa += maksut.get(i).getSumma();
				}
			}

			return summa;
		} else {
			for (int i = 0; i < maksut.size(); i++) {
				if (maksut.get(i).getHenkilo().equals(henkilo)) {
					summa += maksut.get(i).getSumma();
				}
			}

			return summa;
		}
	}

	public void cleanAll() {
		this.maksut = new ArrayList<>();
	}

	@Override
	public String toString() {
		String merkkijono = "";

		for (int i = 0; i < maksut.size(); i++) {
			merkkijono += "\n" + (i + 1) + ". " + maksut.get(i).toString();
		}

		return merkkijono;
	}
}
