# dresscode-Hackathon

Program je napisan u svrhu učestvovanja na novogodišnjem takmičenju Hackathon. Predstavlja napredniju verziju iks-oksa, napisan je u Java programskom jeziku. Komunikacija sa korisnikom se vrši interaktivnim grafičkim korisničkim interfejsom koji je implementiran pomoću "JavaFX" biblioteke. Da bi se program pokrenuo, potrebno je prethodno skinuti JavaFX biblioteku i importovati u svoj IDE. Na sledećem linku: https://openjfx.io/openjfx-docs/?fbclid=IwAR3gkcZvha6WR-Qdbnyyhutgnkp-ATLQfbyF4QdMTqeSg-iqoUqqjwxesBY#introduction može se naći uputstvo za importovanje pomenute biblioteke u Eclipse, InteliJ i Netbeans okruženje. Link za skidanje JavaFX: https://gluonhq.com/products/javafx/

## Pravila igre:

* Pobeđuje igrač koji uspe da grupiše 4 ista znaka po dijagonali, vrsti, koloni ili ako ih grupiše u kvadrat dimenzije 2x2.
* Ukoliko niko ne ostvari gore navedeni uslov za pobedu, partija je nerešena

## Kako se igra ?

* Korisnik programa igra protiv računara (inteligencije programa koja je implementirana)
* Prilikom startovanja programa, nasumično se bira ko će prvi igrati i započinje sa "X", dok drugi na redu igra sa "O"
* Korisnik bira polje tako što odabere slobodno polje i odigra potez na izabranom polju levim klikom miša
* Nakon završene partije, dobijamo obaveštenje o tome ko je pobedio ("X" ili "O") i na koji način je ostvarena pobeda
* Odmah posle toga korisnik dobija izbor da li će ponovo da igra ili ne. U slučaju da izabere novu igru, polja se resetuju i ponovo se nasumično bira ko će prvi biti na potezu, u * suprotnom ukoliko odluči da prekine igru, program se gasi.

## Kako funkcioniše inteligencija programa ?

- termini: listForWin - lista koja čuva podatke o poljima koja program mora da odigra kako bi pobedio listForSafe - lista koja čuva podatke o poljima koja program mora da odigra kako bi opstao u igri, odnosno kako korisnik ne bi ostvario pobedu

* Postoje dve liste koje služe za čuvanje polja koja program mora da odigra kako bi pobedio ili kako bi opstao u igri.
* Prvo se izvršava potez koji vodi do pobede (uzima se element iz listForWin) ako je to moguće
* Ako je listForWin prazna lista ili je korisnik u prethodnom potezu pokrio sva polja koja vode do pobede, program proverava da li postoje elementi u listForSafe, ako postoji igra polje iz liste
* U suprotnom, ako nisu ispunjena prethodna dva uslova, program nasumično bira polje na kom će biti postavljen znak.
