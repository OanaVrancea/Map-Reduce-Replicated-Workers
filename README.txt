Vrancea Oana Roxana, 336 CA

Pentru implementarea temei am folosit concepte din laboratorul 7, utilizand
ExecutorService si Future.
In functia main, dupa citirea datelor de intare, am inceput sa construiesc
taskuri de tip Map care primesc ca parametrii numele fisierului, offsetul,
dimensiunea fragmentului, dimensiunea totala a fisierului si ExecutorService.
Fiecare task, dupa ce este creat cu ajutorul unui constructor, este adaugat 
intr-o lista de taskuri. Dupa ce au fost create toate taskurile, se introduc
toate in pool utilizand metoda submit().
Clasa MapTask implementeaza interfata Callable. Prima parte din metoda Call
este citirea din fisier. In primul rand, verific daca caracterul de la pozitia
offset este un delimitator sau nu. Daca nu este delimitator, verific si 
caracterul care se afla inaintea sa, daca exista. In cazul in care si acesta
nu este un delimitator, inseamna ca am inceput citirea in mijlocul unui cuvant,
iar de la offset citesc si ignor caractere pana ajung la urmatorul delimitator.
In continuare citesc cati bytes mai sunt disponibili din fragment, iar la final
verific daca fragmentul se termina in mijlocul unui cuvant sau nu. Verific 
caracterele de la offset + size - 1 si de la offset + size. Daca ambele nu sunt 
delimitatori, atunci citesc bytes pana cand gasesc un nou delimitator. Dupa 
finalizarea procesului de citire, se construieste dictionarul pentru fragmentul
respectiv si lista de cuvinte, se dau ca parametrii unui obiect de tip MapTaskResult
care este intors de metoda callable.
In main, rezultatele metodei callable sunt rezultate intr-un vector de tipul
Future<MapTaskResult>, dupa care oprim ExecutorService din a primi task-uri noi
utilizand metoda shutdown(). Tot in main se prelucreaza rezultatele etapei de map
si se creeaza task-uri noi de tip ReduceTask pentru thread-urile worker care se
ocupa de etapa Reduce (se porneste iar un ExecutorService). Noile task-uri sunt 
introduse in pool prin metoda submit.
Clasa ReduceTask implementeaza interfata Callable. Aici se creaza un singur 
dictionar si o singura lista de cuvinte din multitudinea de dictionarii si 
liste specifice unui singur fisier(etapa de combinare), dupa care se calculeaza
rangul unui fisier dupa formula(etapa de procesare). Metoda call returneaza un
obiect de tip ReduceTaskResult.
Rezultatele etapei de reduce sunt salvate intr-un vector de tipul 
Future<ReduceTaskResult>, sunt sortate descrescator dupa rank si sunt scrise
in fisierul de iesire.