# Auto-calendar
### Zespół
Kacper Sołtysiak, Łukasz Orlikowski, Jacek Karwowski

## Wizja
### Opis
Auto-calendar jest kalendarzem, który ma za zadanie ułatwić organizację czasu użytkownikowi. W tym celu umożliwia samodzielne, jak i zautomatyzowane tworzenie planu w oparciu o wprowadzone informacje o obowiązkach. Użytkownik może wybrać różne heurystyki, które optymalizują różne cele.

### Grupy użytkowników/role
Indywidualni użytkownicy

### Funkcjonalność
  - Podstawowe funkcje kalendarze:
    - Dodawanie/usuwanie/modyfikacja eventów o określonym terminie
    - Dodatkowe dane eventów: notatki, miejsce, lista osób
    - Powtarzające się wydarzenia (z opcjonalną datą zakończenia)
  - Przypomnienia:
    - Przypomnienia bezpośrednio w aplikacji
    - Przypomnienia mailowe przed eventem
    - Przypomnienie bez-eventowe
  - Auto:
    - Dodanie zadania o określonym czasie i deadlinie
    - Dodatkowe dane zadań (minimalny/maksymalny czas trwania)
    - Ustawienie powtarzalnego zadania o określonym przedziale czasowym i czasie
    - Sortowanie zależności
    - Ustawianie priorytetów
    - Sprawdzanie kompatybilności przy wstawianiu (i jakieś opcje rozwiązania konfliktu)
    - Auto-ułożenie planu w oparciu o różne heurystyki
    - Ułożenie planu uwzględniając metadane zadań (czas, priorytet)
    - Optymalizacja (braku-)powtarzalności zadań
  - Import/eksport:
    - Import/eksport plików .ical
  - UI:
    - Widok miesięczny/tygodniowy/dzienny
    - Filtracja tasków (przez tag, przez czas)
    - Customizacja kolorów
  - Ekstra:
    - Obsługa stref czasowych
    - Notatki na konkretny dzień
    - Tagowanie zadań

### Technologie
 - Język: Java 1.8
 - UI: Web-based, RESTful-style
 - Build system: Gradle
 - Testing framework: JUnit5

### Narzędzia
 - Repo hosting: github.com
 - Issue tracker: github.com
 - CI: Circle CI
 - Komunikacja: Discord, Telegram

## Harmonogram

### Sprint 0
Deadline: 2021-03-17  
Zakres:
 - Wizja
 - Harmonogram
 
### Sprint 1
Deadline: 2021-04-07  
Zakres:
 - Testowanie i wybór frameworku UI
 - Stworzenie szkieletu UI z jednym widokiem
 - Stworzenie szkieletu aplikacji
 - Docelowo: podstawowe funkcje kalendarza
 - Ustawienie narzędzi (repo, issues, CI)
 - Tagowanie zadań
 - Zaplanowanie zadań na następny sprint
 - Dokumentacja wymagań funkcjonalnych (wydajność, skalowalność, bezpieczeństwo, ergonomia, 1 str)

### Sprint 2
Deadline: 2021-04-28  
Zakres:
 - Dodanie zadania o określonym czasie i deadlinie
 - Dodatkowe dane zadań (minimalny/maksymalny czas trwania)
 - Ustawienie powtarzalnego zadania o określonym przedziale czasowym i czasie
 - Sprawdzanie kompatybilności przy wstawianiu (i jakieś opcje rozwiązania konfliktu)
 - Auto-ułożenie planu w oparciu o jedną heurystykę
 - Zaplanowanie zadań na następny sprint
 - Dokumentacja architektury (1-2 diagramy)

### Sprint 3
Deadline: 2021-05-12  
Zakres:
 - Implementacja pozostałych heurystyk
 - Sortowanie zależności
 - Ustawianie priorytetów
 - Import/eksport plików .ical
 - Zaplanowanie zadań na następny sprint
 
### Sprint 4
Deadline: 2021-05-26  
Zakres:
 - Obsługa stref czasowych
 - Notatki na konkretny dzień
 - Przypomnienia bezpośrednio w aplikacji
 - Przypomnienia mailowe przed eventem
 - Przypomnienie bez-eventowe
 - Zaplanowanie zadań na następny sprint
 
### Sprint 5
Deadline: 2021-06-09  
Zakres:
 - Widok miesięczny/tygodniowy/dzienny
 - Filtracja tasków (przez tag, przez czas)
 - Customizacja kolorów
 - Testy integracyjne
 - Wygenerowanie dokumentacji javadoc-iem
 - Przygotowanie prezentacji
