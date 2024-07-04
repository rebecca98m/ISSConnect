# ISSConnect
Homework II per il corso Programmazione di Sistema

## Descrizione del progetto
Per verificare l'esecuzione di processi multitasking in ambiente Android, sono state sviluppate due applicazioni installate sullo stesso dispositivo. 

La prima applicazione (ISSConnectService) implementa un servizio che ottiene i dati sulla posizione della stazione spaziale internazionale da un web service esterno ed opera su un thread secondario che non termina. 

La seconda applicazione (ISSConnect), a seguito dell'apposita richiesta dell'utente, si connette al servizio precedente, ottiene i dati e li espone in due modalità: tramite una stringa di testo e tramite una visualizzazione sulla mappa satellitare. 

Al fine di simulare delle operazioni più dispendiose di tempo, sono stati introdotti degli intervalli di tempo all'interno del codice.

## Tecnologie utilizzate
Per lo sviluppo delle applicazioni è stato utilizzato Android Studio, principalmente adottando il linguaggio Java.

La documentazione delle API esterne utilizzate per la ricezione dei dati sulla posizione della Stazione Spaziale Internazionale sono visualizzabili al sito https://wheretheiss.at.

La mappa satellitare è stata implementata tramite l'utilizzo della libreria JavaScript Leaflet. 

L'icona utilizzata è distribuita gratuitamente da FlatIcon.com


## Esecuzione dell'applicazione

https://github.com/rebecca98m/ISSConnect/assets/56120271/827a5956-d6c3-4bd6-a212-f1732e07ad18

