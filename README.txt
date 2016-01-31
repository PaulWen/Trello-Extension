Diese App ist eine Erweiterung für die Trello-Android-App.

Sie ermöglich:
- alle 15min soll sie sich aktualisieren bzw. in der App durch drücken eines Buttons
- alle fälligen Aufgaben sollen als permanent Benachrichtigungen angezeigt werden
- die Benachrichtigung kann man aufklappen und erhält zwei Buttons: 1. Aufgabe abhaken (archivieren) 2. Aufgabe ansehen (öffnet Trello App)
- es gibt eine weitere Dauerbenachrichtigung, welche das sofortige hinzufügen einer neuen Aufgabe ermöglicht

Funktionsweise:
- MainActivity: Besteht nur aus einem Button, welche den MainService startet (diese Aktivity dient nur zu Dev-Zwecken, um den MainService starten zu können)
- ServiceStarter: Wird über einen Intent aufgerufen, sobald das Handy hochgefahren ist
- MainService: Ruft den RefreshService auf und ruft sich über einen Timer alle 15 Minuten erneut auf
- ArchivateService: Archiviert eine gewünschte Karte, deren ID der Service über ein Inent-Extra mitgegeben bekommt
- NewCardService: Erstellt eine neue leere Karte in der Liste mit der ID, welche unter MainService-STANDARD_LIST_ID eingetragen ist und öffnet die neu erstellte Karte auch in der Trello App
- RefreshService: lädt sich zunächst die kompletten Informationen zu jeder Karte runter, anschließend löscht sie alle aktuell offenen Notifications. Danach werden nachfolgende Notifications geöffnet:
	1) Add-Card-Notification: klickt man auf diese wird der NewCardService ausgeführt - Klickt man auf den Refresh Button der Notification, wird der RefreshService ausgeführt
	2) Task-Today-Notification: für jede Karte, welche im laufe des aktuellen Tages oder vor dem aktuellen Tag Fällig ist, wird eine Notification erstellt
								 klickt man auf die Notification öffnet sich die dazugehörige Karte in der Trello App
								 klickt man auf den Fertig Button der Notification, so wird der ArchivateService aufgerufen, welcher die Karte archiviert 