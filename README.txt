Diese App ist eine Erweiterung f�r die Trello-Android-App.

Sie erm�glich:
- alle 15min soll sie sich aktualisieren bzw. in der App durch dr�cken eines Buttons
- alle f�lligen Aufgaben sollen als permanent Benachrichtigungen angezeigt werden
- die Benachrichtigung kann man aufklappen und erh�lt zwei Buttons: 1. Aufgabe abhaken (archivieren) 2. Aufgabe ansehen (�ffnet Trello App)
- es gibt eine weitere Dauerbenachrichtigung, welche das sofortige hinzuf�gen einer neuen Aufgabe erm�glicht

Funktionsweise:
- MainActivity: Besteht nur aus einem Button, welche den MainService startet (diese Aktivity dient nur zu Dev-Zwecken, um den MainService starten zu k�nnen)
- ServiceStarter: Wird �ber einen Intent aufgerufen, sobald das Handy hochgefahren ist
- MainService: Ruft den RefreshService auf und ruft sich �ber einen Timer alle 15 Minuten erneut auf
- ArchivateService: Archiviert eine gew�nschte Karte, deren ID der Service �ber ein Inent-Extra mitgegeben bekommt
- NewCardService: Erstellt eine neue leere Karte in der Liste mit der ID, welche unter MainService-STANDARD_LIST_ID eingetragen ist und �ffnet die neu erstellte Karte auch in der Trello App
- RefreshService: l�dt sich zun�chst die kompletten Informationen zu jeder Karte runter, anschlie�end l�scht sie alle aktuell offenen Notifications. Danach werden nachfolgende Notifications ge�ffnet:
	1) Add-Card-Notification: klickt man auf diese wird der NewCardService ausgef�hrt - Klickt man auf den Refresh Button der Notification, wird der RefreshService ausgef�hrt
	2) Task-Today-Notification: f�r jede Karte, welche im laufe des aktuellen Tages oder vor dem aktuellen Tag F�llig ist, wird eine Notification erstellt
								 klickt man auf die Notification �ffnet sich die dazugeh�rige Karte in der Trello App
								 klickt man auf den Fertig Button der Notification, so wird der ArchivateService aufgerufen, welcher die Karte archiviert 