package App;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Program extends Application {
	
	private static final int DIM_BUTTON = 200;
	private static final String INIT_BUTTON_STYLE = "-fx-background-color: LightGrey; -fx-text-fill: Blue";
	private static final String INIT_BUTTON_CLICK_STYLE ="-fx-background-color: #36454F; -fx-text-fill: Red";
	private static final String INIT_BUTTON_WIN_STYLE = "-fx-background-color: Green";
	private static final int SPACE = 5;
	
	private Button[][] table;
	private int moveCounter;
	private char winner; // x ili o
	private String message; // poruka koja nam govori o nacinu na koji je ostvarena pobeda
	
	// Koristimo liste za cuvanje koordinata polja koje program mora da odigra kako bi pobedio ili kako bi opstao u igri.
	// Moze se implementirati i sa Stringom, ali odabrao sam liste zato sto je moguce da nekad ima vise od jedne
	// mogucnosti da pobedi ili izgubi. Takodje, ukoliko ima vise resenja za pobedu, ne bi bilo okej da cuva samo jedno
	// zato sto mozda nece moci da odigra ako korisnik pokrije bas to polje, iz tog razloga da bi inteligencija programa bila 
	// na vecem nivou, uzima se sledece polje iz listForWin i ostvaruje se pobeda.
	private ArrayList<String> listForWin = new ArrayList<>(); 
	private ArrayList<String> listForSafe = new ArrayList<>();
	private int next; // odredjuje ko prvi igra
	private String programCharacter;
	private String userCharacter;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Iks-Oks 4x4");
		
		Scene scene = new Scene(initGui(), DIM_BUTTON * 4 , DIM_BUTTON * 4);
		primaryStage.setScene(scene);
			
		primaryStage.setResizable(false);
		primaryStage.show();
		
	}

	private GridPane initGui() {
		GridPane mainPanel = new GridPane();
		
		table = new Button[4][4];
		moveCounter = 0;
		mainPanel.setHgap(SPACE);
		mainPanel.setVgap(SPACE);
		mainPanel.setPadding(new Insets(SPACE));
		mainPanel.setStyle("-fx-background-color: transparent;");
		
		// Inicijalizacija dugmica, odnosno polja
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Button b = new Button(" ");
				b.setPrefSize(DIM_BUTTON, DIM_BUTTON);
				b.setOnAction(this :: action);
				b.setStyle(INIT_BUTTON_STYLE);
				b.setFont(Font.font("", FontWeight.BOLD, 75));
				table[i][j] = b;
				mainPanel.add(b, i, j);
			}
		}
		
		userCharacter = "X";
		programCharacter = "O";
		
		// Promenljivi next se dodeljuje ranodm vrednost (0 ili 1), ako se generise 0, prvi potez igra korisnik,
		// u suprotnom prvi potez "igra" program. Sanse su ravnopravne (50%).
		Random rnd = new Random();
		next = rnd.nextInt(2);
		
		if (next % 2 != 0) {
			userCharacter = "O";
			programCharacter = "X";
			botPlay();
		}
		
		return mainPanel;
		
	}

	// Metod koji se izvvrsava pritiskom na Button, tj. posle svakog poteza
	private void action(ActionEvent event) {
		if (next % 2 == 0) {
			next++;
			Button btn = (Button) event.getSource();
			btn.setText(userCharacter);
			btn.setDisable(true);
			btn.setStyle(INIT_BUTTON_CLICK_STYLE);
			
			++moveCounter;
			checkGameStatus();
		}
		
		if (next % 2 != 0) {
			botPlay();
		}
		
	}

	// Metod koji izvrsava potez koji generise program na osnovu zadatih uslova i analiza
	private void botPlay() {
		
		Random rnd = new Random();
		
		if (!listForWin.isEmpty()) {
			boolean exit = false;
			// Moramo da stavimo do-while, zato sto je moguce da u prethodnom potezu dobije info o tome
			// koje polje treba da odigra kako bi pobedio, ali ako korisnik popuni navedeno polje, 
			// onda ne moze da ga igra, pa bira neko sledece iz liste, sve dok llista ne bude prazna
			// ako ne nista ne moze da odigra, onda se izvrsava blok naredbi iz "else if (allIsDisableButton)"
			while (!listForWin.isEmpty() && !exit) {
				int rndIndex = rnd.nextInt(listForWin.size());
				String[] tokens = listForWin.get(rndIndex).split(",");
	
				int x = Integer.parseInt(tokens[0]);
				int y = Integer.parseInt(tokens[1]);
				
				if (!table[x][y].isDisable()) {
					table[x][y].setDisable(true);
					table[x][y].setText(programCharacter);
					exit = true;
				}
				
				listForWin.remove(rndIndex);
				//System.out.println("win -> " + listForWin);
			}
	
			if (!exit) // Ako nije odigrao onda igra random polje
				randomPlay(); // #1
			
		} else if (!listForSafe.isEmpty()) {
			
			int rndIndex = rnd.nextInt(listForSafe.size());
			String[] tokens = listForSafe.get(rndIndex).split(",");
			
			int x = Integer.parseInt(tokens[0]);
			int y = Integer.parseInt(tokens[1]);
			table[x][y].setDisable(true);
			table[x][y].setText(programCharacter);
			
			listForSafe.remove(rndIndex);
			
			//System.out.println("safe -> " + listForSafe);
			
		} else { // Ako ne moze da odigra potez za pobedu ili opstanak, bira se random polje
			randomPlay(); // #2
		}
		
		moveCounter++;
		next++;
		checkGameStatus();
		
	}
	
	// Posebna metoda kako bi kod za random mogao da se koristi na mestu obelezenom sa #1 i #2
	private void randomPlay() {
		int rndIndexX;
		int rndIndexY;
		Random rnd = new Random();
		
		boolean change = false;
		do {
			rndIndexX = rnd.nextInt(4);
			rndIndexY = rnd.nextInt(4);
			if (!table[rndIndexX][rndIndexY].isDisable()) {
				change = true;
				table[rndIndexX][rndIndexY].setDisable(true);
				table[rndIndexX][rndIndexY].setText(programCharacter);
			}
		} while (!change);
	}

	// Proverava status igre, tj. da li imamo pobednika ili su sva polja popunjena (nereseno)
	private void checkGameStatus() {
		boolean gameOver = false;
		
		if (checkWinner()) {
			new Alert(Alert.AlertType.INFORMATION, "Pobednik je " + winner + (next % 2 == 0? " (Program)":" (Korisnik)") + ". \n" +
						"Pobeda je ostvarena " + message).showAndWait();
			gameOver = true;
		} else if (moveCounter == 16) {
			new Alert(Alert.AlertType.INFORMATION, "Nerešeno je ").showAndWait();
			gameOver = true;
		}
		
		// Ako je igra zavrsena, pitaj da li korisnik zeli novu igru
		if (gameOver) {
			try {
				Alert newGameAlert = new Alert(Alert.AlertType.CONFIRMATION, "New game ? (Press \"OK\" for continue)");
				Optional<ButtonType> response = newGameAlert.showAndWait();
				if (response.isPresent() && response.get() == ButtonType.OK)
					resetGame();
				else
					Platform.exit();
			} catch (IllegalArgumentException iae) {
				// Blok je namerno ostavljen prazan, ako dodje do greske program ce zbog "else" svakako biti zatvoren
			}
		}
	}

	// Metod koji proverava da li postoji pobednik
	private boolean checkWinner() {
		
		if (checkColumns())
			return true;
		
		if (checkRows())
			return true;
		
		if (checkMainDiagonal())
			return true;
		
		if (checkSideDiagonal())
			return true;
		
		if (checkSquare())
			return true;
		
		return false;
	}
	
	
	// Metod koji proverava da li postoji pobednik koji je uspeo da grupise 4 znaka u kvadrat
	private boolean checkSquare() {
		String check = "";
		for (int j = 0; j < 3; j++) {

			for (int i = 0; i < 3; i++) {
				
				// Uzima vrednost sa polja koja cine kvadrat
				String c1 = table[i][j].getText();
				String c2 = table[i][j + 1].getText();
				String c3 = table[i+1][j].getText();
				String c4 = table[i+1][j+1].getText();	
				
				check += c1+c2+c3+c4;
				
				if (check.equals("XXXX") || check.equals("OOOO")) {
					winner = check.charAt(0); // uzimamo pobednika
					message = "kvadratom na poljima: (" + i + ", " + j + "), (" 
												+ i + ", " + (j + 1) + ") , (" + 
												+ (i + 1) + ", " + j + ") , ("
												+ (i + 1) + ", " + (j + 1) + ")";
					String colorText;
					if (next % 2 != 0)
						colorText = "; -fx-text-fill: Red";
					else
						colorText = "; -fx-text-fill: Blue";
					table[i][j].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
					table[i][j + 1].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
					table[i + 1][j].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
					table[i + 1][j + 1].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
					
					return true;
				} else { 
					
					int safePos = safe(check);
					int winPos = win(check);
					
					// Ako postoji polje koje je moguce odigrati kako ne bi izgubio, izvrsi sledeci uslov
					// i polje dodaj u listu
					if (safePos != -1) { 
						String safeXY = "";
						switch(safePos) {
							case 0: safeXY = i + "," + j; break;
							case 1: safeXY = i + "," + (j + 1); break;
							case 2: safeXY = (i + 1) + "," + j; break;
							case 3: safeXY = (i + 1) + "," + (j + 1); break;
						}
						//System.out.println("za opstanak" + safeXY); // ispis u konzoli kako bi se proverila inteligencija programa
						addToListForSafe(safeXY);
					}
					
					// Ako postoji polje koje je moguce odigrati kako bi pobedio, izvrsi sledeci uslov
					// i polje dodaj u listu
					if (winPos != -1) {
						String winXY = "";
						switch(winPos) {
							case 0: winXY = i + "," + j; break;
							case 1: winXY = i + "," + (j + 1); break;
							case 2: winXY = (i + 1) + "," + j; break;
							case 3: winXY = (i + 1) + "," + (j + 1); break;
						}
						//System.out.println("za pobedu" + winXY); // ispis u konzoli kako bi se proverila inteligencija programa
						addToListForWin(winXY);
					}
					
				}
				
				check = "";
			}
			
		}
		
		return false;
	}

	// Proverava da li postoji pobednik koji je spojio znakove po sporednoj dijagonali
	private boolean checkSideDiagonal() {
		String check = "";
		
		for (int i = 0; i < 4; i++) {
					
			check += table[3 - i][i].getText();
			
			if (check.equals("XXXX") || check.equals("OOOO")) {
				winner = check.charAt(0); // Uzimamo pobednika
				message = "preko sporedne dijagonale.";
				
				String colorText;
				if (next % 2 != 0)
					colorText = "; -fx-text-fill: Red";
				else
					colorText = "; -fx-text-fill: Blue";
				table[0][3].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[1][2].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[2][1].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[3][0].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				
				return true;
			} else {
				
				String safeXY;
				String winXY;
				int safePos = safe(check);
				int winPos = win(check);
				
				if (safePos != -1) {
					safeXY = 3 - safePos + "," + safePos;
					//System.out.println("za opstanak" + safeXY); // ispis u konzoli kako bi se proverila inteligencija programa
					addToListForSafe(safeXY);
				}
				
				if (winPos != -1) {
					winXY = 3 - winPos + "," + winPos;
					//System.out.println("za pobedu" + winXY); // ispis u konzoli kako bi se proverila inteligencija programa
					addToListForWin(winXY);
				}
				
			}
					
		}
				
		return false;
	}

	// Proverava da li postoji pobednik koji je spojio znakove po glavnoj dijagonali
	private boolean checkMainDiagonal() {
		String check = "";
		
		for (int i = 0; i < 4; i++) {
					
			check += table[i][i].getText();
					
			if (check.equals("XXXX") || check.equals("OOOO")) {
				winner = check.charAt(0); // uzimamo pobednika
				message = "preko glavne dijagonale.";
				
				String colorText;
				if (next % 2 != 0)
					colorText = "; -fx-text-fill: Red";
				else
					colorText = "; -fx-text-fill: Blue";
				table[0][0].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[1][1].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[2][2].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[3][3].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				
				return true;
			} else {
				
				String safeXY;
				String winXY;
				int safePos = safe(check);
				int winPos = win(check);
				
				if (safePos != -1) {
					safeXY = safePos + "," + safePos;
					//System.out.println("za opstanak" + safeXY); // ispis u konzoli kako bi se proverila inteligencija programa
					addToListForSafe(safeXY);
				}
				
				if (winPos != -1) {
					winXY = winPos + "," + winPos;
					//System.out.println("za pobedu" + winXY); // ispis u konzoli kako bi se proverila inteligencija programa
					addToListForWin(winXY);
				}
				
			}
					
		}
				
		return false;
	}

	// Proverava da li postoji pobednik koji je spojio znakove u nekoj koloni
	private boolean checkColumns() {
		//proveravamo po svim kolonama
		for (int j = 0; j < 4; j++) {
			//proveravamo da li u i-toj koloni imamoo pobednika
			String check = "";
			for (int i = 0; i < 4; i++) {
				check += table[i][j].getText();
			}
					
			if (check.equals("XXXX") || check.equals("OOOO")) {
				winner = check.charAt(0); // uzimamo pobednika
				message = "u " + (j + 1) + ". koloni";
				
				String colorText;
				if (next % 2 != 0)
					colorText = "; -fx-text-fill: Red";
				else
					colorText = "; -fx-text-fill: Blue";
				table[0][j].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[1][j].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[2][j].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[3][j].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				
				return true;
			} else {
				
				int safePos = safe(check);
				int winPos = win(check);
				
				if (safePos != -1) {
					String safeXY = "";
					switch(safePos) {
						case 0: safeXY = 0 + "," + j; break;
						case 1: safeXY = 1 + "," + j; break;
						case 2: safeXY = 2 + "," + j; break;
						case 3: safeXY = 3 + "," + j; break;
					}
					//System.out.println("za opstanak" + safeXY); ispis u konzoli kako bi se proverila inteligencija programa
					addToListForSafe(safeXY);
				}
				
				if (winPos != -1) {
					String winXY = "";
					switch(winPos) {
						case 0: winXY = 0 + "," + j; break;
						case 1: winXY = 1 + "," + j; break;
						case 2: winXY = 2 + "," + j; break;
						case 3: winXY = 3 + "," + j; break;
					}
					//System.out.println("za pobedu" + winXY);  ispis u konzoli kako bi se proverila inteligencija programa
					addToListForWin(winXY);
				}
				
			}
					
		}
				
		return false;
	}

	// Proverava da li postoji pobednik koji je spojio znakove u nekom redu
	private boolean checkRows() {
		//proveravamo po svim redovima
		for (int i = 0; i < 4; i++) {
			//proveravamo da li u i-tom redu imamoo pobednika
			String check = "";
			for (int j = 0; j < 4; j++) {
				check += table[i][j].getText();
			}
					
			if (check.equals("XXXX") || check.equals("OOOO")) {
				winner = check.charAt(0); // uzimamo pobednika
				message = "u " + (i + 1) + ". redu";
				
				String colorText;
				if (next % 2 != 0)
					colorText = "; -fx-text-fill: Red";
				else
					colorText = "; -fx-text-fill: Blue";
				table[i][0].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[i][1].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[i][2].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				table[i][3].setStyle(INIT_BUTTON_WIN_STYLE + colorText);
				
				return true;
			} else {
				
				int safePos = safe(check);
				int winPos = win(check);
				
				if (safePos != -1) {
					String safeXY = "";
					switch(safePos) {
						case 0: safeXY = i + "," + 0; break;
						case 1: safeXY = i + "," + 1; break;
						case 2: safeXY = i + "," + 2; break;
						case 3: safeXY = i + "," + 3; break;
					}
					//System.out.println("za opstanak" + safeXY); // ispis u konzoli kako bi se proverila inteligencija programa
					addToListForSafe(safeXY);
				}
				
				if (winPos != -1) {
					String winXY = "";
					switch(winPos) {
						case 0: winXY = i + "," + 0; break;
						case 1: winXY = i + "," + 1; break;
						case 2: winXY = i + "," + 2; break;
						case 3: winXY = i + "," + 3; break;
					}
					//System.out.println("za pobedu" + winXY); // ispis u konzoli kako bi se proverila inteligencija programa
					addToListForWin(winXY);
				}
				
			}
					
		}
		return false;
	}
	
	// Postoji mogucnost da se preko istog polja moze pobediti/optstati na 2 nacina, pa su sledeca dva metoda napisana kako
	// ne bi doslo do dupliranja istih polja (koordinata)
	
	private void addToListForWin(String xy) {
		boolean add = true;
		for (String s : listForWin) {
			if (s.equals(xy))
				add = false;
		}
		if (add)
			listForWin.add(xy);
	}
	
	private void addToListForSafe(String xy) {
		boolean add = true;
		for (String s : listForSafe) {
			if (s.equals(xy))
				add = false;
		}
		if (add)
			listForSafe.add(xy);
	}
	
	// Nadji poziciju koju treba odigrati kako ne bi izgubio
	private int safe(String check) {
		
		if (userCharacter.equals("X")) {
			if (check.equals("XXX ") || check.equals("XX X") || check.equals("X XX") || check.equals(" XXX"))
				return check.indexOf(" ");
		} else {
			if (check.equals("OOO ") || check.equals("OO O") || check.equals("O OO") || check.equals(" OOO"))
				return check.indexOf(" ");
		}
		return -1;
	}

	// Nadji poziciju koju treba odigrati kako bi pobedio
	private int win(String check) {
		
		if (userCharacter.equals("X")) {
			if (check.equals("OOO ") || check.equals("OO O") || check.equals("O OO") || check.equals(" OOO"))
				return check.indexOf(" ");
		} else {
			if (check.equals("XXX ") || check.equals("XX X") || check.equals("X XX") || check.equals(" XXX"))
				return check.indexOf(" ");
		}
			
		return -1;
	}
	
	// Metod za resetovanje igre (Ako korisnik pozeli novu partiju)
	private void resetGame() {
		moveCounter = 0;
		listForSafe.clear();
		listForWin.clear();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				table[i][j].setStyle(INIT_BUTTON_STYLE);
				table[i][j].setText(" ");
				table[i][j].setDisable(false);
			}
		}
		
		userCharacter = "X";
		programCharacter = "O";
		
		Random rnd = new Random();
		next = rnd.nextInt(2);

		if (next % 2 != 0) {
			userCharacter = "O";
			programCharacter = "X";
			botPlay();
		}
		
	}
	

}
