package jakubw.pracainz.goalsexecutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    TextView helpText;
    TextView helpText2;
    TextView gromadzenieHelp, analizaHelp, porzadkowanieHelp, przegladHelp, porzadkowanieZakladkiHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        helpText = findViewById(R.id.helpText);
        helpText2 = findViewById(R.id.helpText2);
        gromadzenieHelp = findViewById(R.id.gromadzenieHelp);
        analizaHelp = findViewById(R.id.analizaHelp);
        porzadkowanieHelp = findViewById(R.id.porzadkowanieHelp);
        przegladHelp = findViewById(R.id.przegladHelp);
        porzadkowanieZakladkiHelp = findViewById(R.id.porzadkowanieZakladkiHelp);



        helpText.setText("WYKORZYSTANIE METODOLOGII GETTING THINGS DONE W APLIKACJI GoalsExecutor");
        helpText2.setText("Celem metodologii Getting Things Done jest zwiększenie produktywności jej " +
                "użytkownika poprzez odpowiednie zarządzanie i realizowanie jego zadań. Dokładnie polega ono na przechodzeniu" +
                " przez pięć etapów metodologii: gromadzenia, analizy, porządkowania, przeglądu i działania (zostaną one opisane " +
                "poniżej). W rezultacie oprócz wymienionej zwiększonej produktywności użytkownik będzie" +
                " odczuwać także poczucie wewnętrznego spokoju, kontrolowania sytuacji oraz dobrego samopoczucia.");
        gromadzenieHelp.setText("W realizacji fazy gromadzenia wspomaga użytkownika zakładka „BOX”. " +
                "Jest odpowiednikiem skrzyni bądź teczki na spływające sprawy. Do tej zakładki " +
                "użytkownik dodaje pojawiające się nowe sprawy określając JEDYNIE ich nazwę.");
        analizaHelp.setText("Na podstawie drzewa decyzyjnego lub według własnych preferencji" +
                " użytkownik podejmie decyzję o tym w jakim miejscu umieścić zgromadzone w zakładce „BOX” zadania.");
        porzadkowanieHelp.setText("Po przeanalizowaniu zadań w „BOXie” klient aplikacji przystępuje do przenoszenia" +
                " ich do odpowiednich zakładek. Aby to zrobić wystarczy kliknąć w zadanie znajdujące się w „BOXie”," +
                " następnie pojawi się okno z zapytaniem, w jakiej zakładce chcemy to zadanie umieścić.");
        porzadkowanieZakladkiHelp.setText("Typy zakładek do przechowywania zadań użytkownika: \n" +
                "- Next Action - znajdują się tutaj zadania, których nie da się podzielić na podzadania." +
                " Zadania te nie mają określonego momentu wykonania – należy je wykonać w najbliższej wolnej chwili. " +
                "Aby ułatwić zarządzanie zadaniami, tym bardziej jeśli jest ich bardzo dużo, zalecane" +
                " jest, aby podzielić je ze względu na kontekst: w domu, pracy, mieście itd. za pomocą etykiet" +
                "tworzonych w zakładce „Labels.” \n" +
                "- Calendar - tu przechowywane są zadania, które należy wykonać o określonej porze, na przykład „wizyta u dentysty 23.07.2020r. godz. 15:00”. \n" +
                "- Projects - traktowany jest jako więcej niż jedno działanie, które jest potrzebne do osiągnięcia pożądanego rezultatu, na przykład „napisz książkę" +
                " na temat wspinaczki górskiej”.\n" + "Na liście „Projects” będzie umieszczany spis docelowych rezultatów, jakie osoba chce osiągnąć." +
                " Nie przechowuje się tu detali związanych z projektem. Użytkownik " +
                "ma tylko mieć świadomość, nad jakimi projektami pracuje. Konkretne zadania z nim związane znajdą się na liście „Next Action”." +
                " Nie wykonywany jest projekt – wykonywane jest działanie, które ma prowadzić do jego zrealizowania.\n" +
                "- Someday - znajdują się tutaj zadania na nieokreśloną przyszłość.");
        przegladHelp.setText("Użytkownik powinien wykonywać dwa rodzaje przeglądów swoich zadań – dzienny i tygodniowy." + "\n" +
                " Dzienny - ten rodzaj przeglądu obejmować będzie tylko zakładkę „Next Action” (najbliższe działanie) oraz" +
                " „Calendar” (kalendarz), w celu ogólnego rozeznania w kwestii tego co ma zostać zrealizowane w najbliższym czasie.\n" +
                " Tygodniowy - składa się on z trzech opisanych wyżej kroków GTD (gromadzenie, analiza, porządkowanie). Klient aplikacji" +
                " powinien dodatkowo zadbać o aktualność swojego systemu - usunąć wszystkie przestarzałe i nieaktualne sprawy, które zaburzają jego klarowność.");
    }


}
