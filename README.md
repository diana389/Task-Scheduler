Tema 2 - APD
Stefan Diana Maria - 332CC

MyDispatcher:
- clasa care implementeaza interfata Dispatcher
- metoda `addTask` (sincronizata)
    -> Adauga un task in coada de task-uri a unui host, in functie de algoritmul de alocare ales.
    -> ROUND ROBIN: Task-urile sunt alocate nodurilor dupa formula (i + 1) % n, unde i este ID-ul nodului ales anterior. Initial, nodul este -1 pentru a trimite primul task catre nodul cu ID-ul 0.
    -> SHORTEST QUEUE: Aloca task-ul nodului cu cele mai putine task-uri in coada.
    -> SIZE INTERVAL TASK ASSIGNMENT: Task-urile sunt alocate in functie de tipul lor (short, medium, long).
    -> LEAST WORK LEFT: Task-ul este alocat nodului cu durata de executie ramasa minima.

MyHost:
- clasa care implementeaza interfata Host
- metoda `addTask`
    -> Adauga un task in coada de task-uri a unui host. Coada este sortata in functie de prioritatea task-ului, iar apoi de timpul adaugarii.
    -> Daca un task este in executie in momentul apelarii si este preemptibil, daca noul task are prioritate mai mica, se notifica task-ul curent pentru a fi preemptat si inlocuit cu noul task.
- metoda `getQueueSize`
    -> Returneaza numarul de task-uri din coada de task-uri a unui host.
- metoda `getWorkLeft`
    -> Returneaza durata de executie ramasa a tuturor task-urilor din coada.
- metoda `shutdown`
    -> Schimba valoarea flag-ului `isRunning` pentru a opri host-ul. `isRunning` este volatile pentru a asigura vizibilitatea modificarii de catre thread-ul de executie din `run`.
- metoda `run`
    -> Se ia primul task din coada de task-uri si se executa.
    -> Se verifica daca task-ul ar trebui preemptat, caz in care executia se opreste si task-ul este inlocuit cu unul nou.
    -> Executia se continua, cate o secunda, pana cand task-ul este complet executat sau pana cand este preemptat.
    -> Consumarea timpului pe procesor se face prin `wait`, avand ca argument minimul dintre timpul de executie ramas al task-ului si 1 secunda.
    -> Daca timpul petrecut in `wait` este mai mic decat timpul dat ca argument, inseamna ca task-ul a fost preemptat.
    -> Se scade din timpul de executie ramas timpul petrecut in `wait`.
    -> Daca task-ul este complet executat, se sterge din coada de task-uri si se actualizeaza campul`finishTime`.
