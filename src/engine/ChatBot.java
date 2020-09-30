package engine;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

// Made according to https://github.com/rubenlagus/TelegramBots/blob/master/TelegramBots.wiki/Getting-Started.md

public class ChatBot extends TelegramLongPollingBot {
	MaksuLista maksut = new MaksuLista();

	public void onUpdateReceived(Update update) {
		// We check if the update has a message and the message has text
		if (update.hasMessage() && update.getMessage().hasText()) {
			String kokoteksti = update.getMessage().getText();
			String sanotaanTakaisin = "";
			if (kokoteksti.contains(" ")) {
				String[] solut = kokoteksti.split(" ");
				String viestinAlku = solut[0];
				if (solut.length == 2) {
					System.out.println("JUKKA " + solut[1]);
				}
				sanotaanTakaisin = sanoTakaisin(kokoteksti, update);
			} else {
				sanotaanTakaisin = sanoTakaisin(kokoteksti, update);
			}
			SendMessage message = new SendMessage() // Create a SendMessage
													// object with mandatory
													// fields
					.setChatId(update.getMessage().getChatId()).setText(sanotaanTakaisin);
			try {
				execute(message); // Call method to send the message
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}

	public String sanoTakaisin(String botilleSanottua, Update update) {
		System.out.println(botilleSanottua);
		String sanotaanTakaisin = "";
		String[] solut = botilleSanottua.split(" ");

		if (botilleSanottua.equals("/onko ohjelmointi kivaa?")) {
			sanotaanTakaisin = "Kyllä. Siitä saa kicksejä!";
		} else if (botilleSanottua.equals("/who will fetch pauline today?")) {
			sanotaanTakaisin = "I can tell you that when that feature is implemented.";
		} else if (botilleSanottua.equals("/when did jukka pay?")) {
			sanotaanTakaisin = "I can tell you that when that feature is implemented.";
		} else if (botilleSanottua.equals("/weather")) {
			sanotaanTakaisin = etsiSaa();
		} else if (botilleSanottua.equals("/foodmenu")) {
			sanotaanTakaisin = etsiRuokalista();
		} else if (botilleSanottua.equals("/jukkapaid")) {
			sanotaanTakaisin = etsiJukanMaksut();
		} else if (solut[0].equals("/tuomaspaid")) {
			if (solut[1].equals("date") || solut[1].equals("today")) {
				String nimi = update.getMessage().getFrom().getFirstName();

				if (solut[1].equals("today")) {
					LocalDate today = LocalDate.now();
					Maksu uusiMaksu = new Maksu(Double.parseDouble(solut[2]), solut[3], nimi, today);
					maksut.add(uusiMaksu);
					sanotaanTakaisin = "Maksu lisätty " + nimi;
				} else if (solut[1].equals("date")) {
					String[] paivat = solut[2].split("\\.");
					LocalDate paivamaara = LocalDate.of(Integer.parseInt(paivat[2]), Integer.parseInt(paivat[1]),
							Integer.parseInt(paivat[0]));
					System.out.println(paivamaara);
					Maksu uusiMaksu = new Maksu(Double.parseDouble(solut[3]), solut[4], nimi, paivamaara);
					maksut.add(uusiMaksu);
					sanotaanTakaisin = "Maksu lisätty " + nimi;
				}
			} else if (solut[1].equals("list") && solut[2].equals("all")) {
				sanotaanTakaisin = "Kaikki maksut:" + maksut.toString();
			} else if (solut[1].equals("delete")) {
				maksut.delete(Integer.parseInt(solut[2]));
				sanotaanTakaisin = "Rivi " + solut[2] + " poistettu.";
			} else if (solut[1].equals("sum") && solut[2].equals("all")) {
				sanotaanTakaisin = "Kaikki maksut yhteensä " + Integer.toString(maksut.sum()) + " euroa.";
			} else if (solut[1].equals("help") && solut[2].equals("me")) {
				sanotaanTakaisin = "Kaikki komennot:\n" + "Aloita komento /tuomaspaid\n"
						+ "Lisaa maksu: today + maksu tai date + pvm + maksu\n"
						+ "Poista maksu: delete + monesko rivi\n" + "Listaa kaikki maksut: list all\n"
						+ "Kaikki maksut yhteensa : sum all\n" + "Summaa kuukauden maksut: count + 2 2020\n"
						+ "Etsi kuukauden tai vuoden maksut : esim find + 2 2020 tai find 2020\n"
						+ "Etsi tietyn henkilon maksut: search + Tuomas tai search + 2020 Tuomas tai search 2 + 2020 + Tuomas \n"
						+ "Aloita alusta: clean all";
			} else if (solut[1].equals("count")) {
				sanotaanTakaisin = "Ajankohdan " + solut[2] + "-" + solut[3] + " kaikki maksut yhteensä: "
						+ maksut.sumMonth(Integer.parseInt(solut[2]), Integer.parseInt(solut[3])) + " euroa.";
			} else if (solut[1].equals("find")) {
				if (solut[2].length() > 2) {
					sanotaanTakaisin = "Haun tulos:\n" + "Ajan " + solut[2] + " maksut:"
							+ maksut.findYearPayments(Integer.parseInt(solut[2]));
				} else {
					sanotaanTakaisin = "Haun tulos:\n" + "Ajan " + solut[2] + "-" + solut[3] + " maksut:"
							+ maksut.findMonthPayments(Integer.parseInt(solut[2]), Integer.parseInt(solut[3]));
				}
			} else if (solut[1].equals("search")) {
				if (solut[2].length() <= 2) {
					sanotaanTakaisin = "Käyttäjän " + solut[4] + " maksut:"
							+ maksut.search(Integer.parseInt(solut[2]), Integer.parseInt(solut[3]), solut[4]) + "\n\n"
							+ "Yhteensä "
							+ maksut.searchSum(Integer.parseInt(solut[2]), Integer.parseInt(solut[3]), solut[4])
							+ " euroa.";
				} else if (solut[2].length() > 2 && isInteger(solut[2]) == true) {
					sanotaanTakaisin = "Käyttäjän " + solut[3] + " maksut:"
							+ maksut.search(0, Integer.parseInt(solut[2]), solut[3]) + "\n\n" + "Yhteensä "
							+ maksut.searchSum(0, Integer.parseInt(solut[2]), solut[3]) + " euroa.";
				} else if (solut[2].length() > 0 && isInteger(solut[2]) == false) {
					sanotaanTakaisin = "Käyttäjän " + solut[2] + " maksut:" + maksut.search(0, 0, solut[2]) + "\n\n"
							+ "Yhteensä " + maksut.searchSum(0, 0, solut[2]) + " euroa.";
				}
			} else if (solut[1].equals("clean") && solut[2].equals("all")) {
				maksut.cleanAll();
				sanotaanTakaisin = "Maksutiedot poistettu.";
			}
		}

		return sanotaanTakaisin;
	}

	public String etsiJukanMaksut() {
		String sanotaanTakaisin = "Jukka has paid a lot";
		return sanotaanTakaisin;
	}

	public String etsiRuokalista() {
		LocalDate tanaan = LocalDate.now();
		int vuosi = tanaan.getYear();
		int kuukausi = tanaan.getMonthValue();
		int paiva = tanaan.getDayOfMonth();
		DayOfWeek viikonpaivaOlio = tanaan.getDayOfWeek();
		String viikonpaiva = viikonpaivaOlio.toString();
		String paivaTanaan = paiva + "." + kuukausi + "." + vuosi;
		String inline = "";
		String ruokainfo = viikonpaiva + " " + paivaTanaan + " ";
		try {
			URL url = new URL("https://hhapp.info/api/amica/pasila/fi");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode();
			if (responsecode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			} else {
				Scanner sc = new Scanner(url.openStream());
				while (sc.hasNext()) {
					inline += sc.nextLine();
				}
				sc.close();
				System.out.println("\nJSON data in string format");
				// System.out.println(inline);
				JSONParser parse = new JSONParser();

				JSONObject result = (JSONObject) parse.parse(inline);
				JSONArray menus = (JSONArray) result.get("LunchMenus");
				Iterator i = menus.iterator();

				System.out.println("TAPANI: ");

				while (i.hasNext()) {
					JSONObject weekinfo = (JSONObject) i.next();
					String dayOfWeek = (String) weekinfo.get("DayOfWeek");
					System.out.println(dayOfWeek);
					String date = (String) weekinfo.get("Date");
					System.out.println(date);
					JSONArray linjastot = (JSONArray) weekinfo.get("SetMenus");
					Iterator j = linjastot.iterator();
					while (j.hasNext()) {
						JSONObject mealInfo = (JSONObject) j.next();
						JSONArray setmenus = (JSONArray) mealInfo.get("SetMenus");

						JSONArray meals = (JSONArray) mealInfo.get("Meals");
						Iterator k = meals.iterator();
						while (k.hasNext()) {
							JSONObject dayInfo = (JSONObject) k.next();
							String name = (String) dayInfo.get("Name");
							System.out.println(name);

							if (date.equals(paivaTanaan)) {
								ruokainfo = ruokainfo + " " + name;
							}
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ruokainfo;
	}

	public String etsiSaa() {
		String inline = "";
		try {
			URL url = new URL(
					"http://api.openweathermap.org/data/2.5/weather?q=Helsinki&APPID=a8720cf3a65bd981b2fecc6381cd729e&units=metric");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode();
			if (responsecode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			} else {
				Scanner sc = new Scanner(url.openStream());
				while (sc.hasNext()) {
					inline += sc.nextLine();
				}
				sc.close();
				System.out.println("\nJSON data in string format");
				System.out.println(inline);
				JSONParser parse = new JSONParser();

				JSONObject result = (JSONObject) parse.parse(inline);
				JSONObject main = (JSONObject) result.get("main");
				double temp = (Double) main.get("temp");
				conn.disconnect();
				url = new URL(
						"http://api.openweathermap.org/data/2.5/weather?q=Nurmijarvi&APPID=a8720cf3a65bd981b2fecc6381cd729e&units=metric");
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.connect();

				sc = new Scanner(url.openStream());
				inline = "";
				while (sc.hasNext()) {
					inline += sc.nextLine();
				}
				sc.close();
				System.out.println("\nJSON data in string format");
				System.out.println(inline);
				parse = new JSONParser();

				result = (JSONObject) parse.parse(inline);
				main = (JSONObject) result.get("main");

				JSONObject wind = (JSONObject) result.get("wind");
				double speed = (Double) wind.get("speed");
				System.out.println("wind speed");
				conn.disconnect();

				LocalDate tanaan = LocalDate.now();
				int vuosi = tanaan.getYear();
				int kuukausi = tanaan.getMonthValue();
				int paiva = tanaan.getDayOfMonth();
				DayOfWeek viikonpaivaOlio = tanaan.getDayOfWeek();
				String viikonpaiva = viikonpaivaOlio.toString();

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				String aika = dtf.format(now);

				System.out.println("JUKKA " + temp);
				inline = "Outside at Helsinki it is now " + temp + "C. Wind speed is: " + speed + "m/s. Today is "
						+ viikonpaiva + " " + paiva + "." + kuukausi + "." + vuosi + " at " + aika + ".";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return inline;
	}

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	public String getBotUsername() {
		return "tuomasvalkamobot";
	}

	@Override
	public String getBotToken() {
		return "";
	}

	public static void main(String[] args) {

		ApiContextInitializer.init();

		TelegramBotsApi botsApi = new TelegramBotsApi();

		try {
			botsApi.registerBot(new ChatBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}