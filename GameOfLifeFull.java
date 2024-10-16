import java.lang.Math;
import java.util.Scanner;

class GameOfLifeFull {

    public Verden verden;

    public void lagVerden(int rader, int kolonner) {
        verden = new Verden(rader, kolonner);
    }

    class Celle {

        boolean levende;
        Celle[] naboer = new Celle[8]; // en celle kan ha opptil 8 naboer
        int antNaboer;
        int antLevendeNaboer;

        public Celle() { // initierer et objekt av Celle
            this.antNaboer = 0;
            this.antLevendeNaboer = 0;
        }

        // setter Celle-objektet som 'død'
        public void settDoed() { this.levende = false; }

        // setter Celle-objektet som 'levende'
        public void settLevende() { this.levende = true; }

        // sjekker om Celle-objektet 'lever'
        public boolean erLevende() { return this.levende; }

        public char hentStatusTegn() { // henter tegn basert på 'død' eller 'levende'
            if (this.erLevende()) {
                return 'O';
            }
            return '.';
        }

        public void leggTilNabo(Celle nabo) { // legger inn en nabo-Celle
            boolean fortsett = true; // variabel som forteller om nabo-Cellen er lagt inn
            for (int i = 0; i < this.naboer.length; i++) { // går gjennom om finner ledig plass i naboer Array
                if (this.naboer[i] == null && fortsett) { 
                    this.naboer[i] = nabo;
                    this.antNaboer++;
                    fortsett = false;
                }
            }
        }

        public void tellLevendeNaboer() { // oppdaterer antLevendeNaboer for Celle-objektet
            int antLevende = 0;
            for (int i = 0; i < this.naboer.length; i++) {
                if (this.naboer[i] != null){ 
                    if (this.naboer[i].erLevende()) {
                        antLevende++;
                    }
                }
            }
            this.antLevendeNaboer = antLevende;
        }

        public void oppdaterStatus() { // oppdaterer 'død'/'levende' basert på nabo-Celler
            if (this.erLevende()) {
                if (this.antLevendeNaboer < 2 || this.antLevendeNaboer > 3) {
                    this.settDoed();
                }
            } else {
                if (this.antLevendeNaboer == 3) {
                    this.settLevende();
                }
            }
        }
    }

    class Rutenett {
        int antRader;
        int antKolonner;
        Celle[][] rutene;

        public Rutenett(int antRad, int antKol) { // initsierer et objekt av Rutenett med gitte dimensjoner
            this.antRader = antRad;
            this.antKolonner = antKol;
            this.rutene = new Celle[this.antRader][this.antKolonner];
        }

        public void lagCelle(int rad, int kol) {
            Celle nyCelle = new Celle(); // oppretter et nytt Celle-objekt
            this.rutene[rad][kol] = nyCelle; // legger Celle-objektet i en gitt plass i rutenettet

            if (Math.random() <= 0.3333) { // Celle-objektet har en ~1/3 sjanse for å 'leve'
                nyCelle.settLevende();
            }
        }

        public void fyllMedTilfeldigeCeller() { // fyller Rutenett-objektet med Celle-objekter
            for (int i = 0; i < this.antRader; i++) {
                for (int j = 0; j < antKolonner; j++) {
                    this.lagCelle(i, j);
                }
            }
        }

        public Celle hentCelle(int rad, int kol) { // henter ut referanse til et Celle-objekt på gitte koordinater
            if (rad >= 0 && rad < this.antRader && kol >= 0 && kol < this.antKolonner) {
                return this.rutene[rad][kol];
            }
            return null; // returnerer null dersom Celle-objektet ikke eksisterer
        }

        public void tegnRutenett() { // skriver ut en grafisk versjon av Rutenett-objektet til terminalen
            String linjeDel = "+---";
            System.out.println("\n\n\n\n");
            for (int i = 0; i < antRader; i++) {
                System.out.println("\n" + linjeDel.repeat(antKolonner) + "+");
                System.out.print("|");
                for (int j = 0; j < antKolonner; j++) {
                    System.out.print(" " + this.hentCelle(i, j).hentStatusTegn() + " |");
                }
            }
            System.out.println("\n" + linjeDel.repeat(antKolonner) + "+");
        }

        public void settNaboer(int rad, int kol) { // finner og legger inn nabo-Celler til en Celle på gitte koordinater
            Celle hovedCelle = this.hentCelle(rad, kol);

            for (int r = rad-1; r < rad+2; r++) {
                for (int k = kol-1; k < kol+2; k++) {
                    Celle naboCelle = this.hentCelle(r, k);

                    if (naboCelle != null && naboCelle != hovedCelle) { // sjekker om nabo-koordinatene inneholder en gyldig nabo-Celle
                        hovedCelle.leggTilNabo(naboCelle);
                    }
                }
            }
        }

        public void kobleAlleCeller() { // går gjennom Rutenett-objektet og legger inn naboer for alle Celle-objekter
            for (int i = 0; i < antRader; i++) {
                for (int j = 0; j < antKolonner; j++) {
                    this.settNaboer(i, j);
                }
            }
        }

        public int antallLevende() { // går gjennom og teller antall 'levende' Celle-objekter
            int antLevende = 0;
            for (int i = 0; i < antRader; i++) {
                for (int j = 0; j < antKolonner; j++) {
                    Celle celle = this.hentCelle(i, j);
                    if (celle.erLevende()) {
                        antLevende++;
                    }
                }
            }
            return antLevende;
        }
    }

    class Verden {
        int antRader;
        int antKolonner;
        int genNr;
        Rutenett rutenett;

        public Verden(int antRad, int antKol) { // initsierer et objekt av Verden med gitte dimensjoner
            this.antRader = antRad;
            this.antKolonner = antKol;
            this.genNr = 0;

            rutenett = new Rutenett(this.antRader, this.antKolonner); // oppretter et Rutenett-objekt med dimensjonene
            rutenett.fyllMedTilfeldigeCeller(); // fyller Rutenett-objektet med Celle-objekter
            rutenett.kobleAlleCeller(); // setter Celle-objektene som naboer til hveradre
        }

        public void tegn() { // skriver ut en grafisk versjon av Rutenett-objektet og info om generajonen av celler
            this.rutenett.tegnRutenett();
            int antLevendeCeller = this.rutenett.antallLevende();
            System.out.println("\nI generasjon nr. " + this.genNr + " er det " + antLevendeCeller + " levende celler.\n\n\n");
        }

        public void oppdatering() { // går gjennom og oppdaterer 'død'/'levende' statusen til alle Celle-objektene i Verden/Rutenett
            for (int i = 0; i < this.antRader; i++) {
                for (int j = 0; j < antKolonner; j++) {
                    Celle celle = this.rutenett.hentCelle(i, j);
                    celle.tellLevendeNaboer(); // oppdaterer antall 'levende' Celle-objekter i Rutenett-objektet
                }
            }

            for (int i = 0; i < this.antRader; i++) {
                for (int j = 0; j < antKolonner; j++) {
                    Celle celle = this.rutenett.hentCelle(i, j);
                    celle.oppdaterStatus(); // oppdaterer 'død'/'levende' statusen til et Celle-objektene
                }
            }

            this.genNr++; // markerer at det er en ny generasjon
        }
    }

    public static void main(String[] args) {

        GameOfLifeFull gol = new GameOfLifeFull();

        Scanner s = new Scanner(System.in);

        // skriver ut velkomst og får inn dimensjonene som skal brukes for Verden-objektet
        System.out.println("\nVelkommen til Game Of Life!");
        System.out.println("Skriv inn antall rader:");
        int rader = s.nextInt();
        System.out.println("Skriv inn antall kolonner:");
        int kolonner = s.nextInt();

        // oppretter Verden-objektet med de gitte korrdinatene
        gol.lagVerden(rader, kolonner);
        
        gol.verden.tegn(); //tegner 0. gen

        // initierer svar-variabelen 
        String svar = "";
        do {
            // spør om bruker vil fortsette eller avslutte programmet/spillet
            System.out.println("\nTrykk 'enter' for å fortsette til nest generasjon.");
            System.out.println("Eller, trykk 'q' etterfulgt av 'enter 'for å avslutte:");
            svar = s.nextLine();

            while (! svar.equals("") && ! svar.equals("q")) { // spør om nytt input ved ugyldig svar
                
                System.out.println("\nFeil input. Prøv igjen:");
                svar = s.nextLine();
            }
            
            if (svar == "") { 
                gol.verden.oppdatering(); // oppdaterer til neste generasjon
                gol.verden.tegn(); // tegner neste generasjon
            }
        } while (! svar.equals("q")); // stopper programmet dersom bruker vil avslutte
    }
}