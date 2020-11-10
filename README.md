# BarnehageGuiden

Eksamensoppgave for Applikasjonsutvikling USN Vår 2019 - raskt utkast gjennomført som forberedelse til eksamen H2019

Oppgavetekst:

Du skal lage en app som f.eks. kan brukes av nytilflyttede barnefamilier i en kommune, eller familier
som planlegger å flytte, for å finne informasjon om barnehager i et område.
REST-tjenester / APIer med data om barnehagene finnes på denne siden
https://www.barnehagefakta.no/swagger/ui/index. Se beskrivelse av datagrunnlag nedenfor.
Krav til funksjonalitet i appen er beskrevet under overskriften funksjonalitet nedenfor. Du står svært
fritt til å utforme detaljert funksjonalitet og GUI innenfor disse rammene. Kreativitet med hensyn til
funksjonalitet og brukergrensesnitt teller positivt.

Datagrunnlag
Informasjonen om barnehagene finnes tilgjengelig i et åpent API hos barnehagefakta.no.
Se websiden https://www.barnehagefakta.no/swagger/ui/index.
APIet består av flere ulike tjenester med hver sin URL. Disse er godt dokumentert på denne siden, og
du kan teste ut URLene på samme side. Alle tjenestene leverer data i JSON-format. Nedenfor finner
du noen eksempler på URLer og beskrivelse av hvilket resultat de gir. Kjør eksempel-URL'ene for å se
resultatet
URL/eksempel: https://www.barnehagefakta.no/api/Fylker
Resultat: Alle landets fylker med deres tilhørende kommuner.
Legg merke til at dataene inneholder to nivåer av JSON-tabeller (fylker og kommuner).
URL: http://www.barnehagefakta.no/api/Kommune/{kommunenr}
Resultat: Data om en kommune med gitt kommunenummer.
Eksempel: https://www.barnehagefakta.no/api/Kommune/0807
URL: http://www.barnehagefakta.no/api/Location/kommune/{kommunenr}
Resultat: Alle barnehager for et gitt kommunenummer. Her finnes bl.a. den unike id-en til hver av
barnehagene.
Eksempel: https://www.barnehagefakta.no/api/Location/kommune/0807
URL: http://www.barnehagefakta.no/api/Barnehage/orgnr/{orgnr}
Resultat: Nøkkeltall om en barnehage gitt barnehagens unike id i barnehagefakta.
Eksempel: https://www.barnehagefakta.no/api/Barnehage/1016022

Funksjonalitet
App’en skal la brukeren utføre funksjonene nedenfor. For oversikten skyld er funksjonaliteten
spesifisert i nummererte punkter, men du behøver ikke følge denne rekkefølgen/strukturen når du
løser oppgaven. Du står svært fritt til å designe GUI’et selv.
Hvis du ikke klarer / rekker å implementere all funksjonaliteten, bør du prioritere å få det du
programmerer til å fungere korrekt.3

a. Finne barnehage via fylke og kommune
Bruker skal enkelt kunne finne alle barnehager i en kommune og vise disse i en liste. Bruker skal først
få en liste over alle fylker i Norge. Ved å velge et fylke skal brukeren se alle kommunene i dette fylket
og kunne velge én kommune. Hvordan disse listene presenteres for bruker og funksjonaliteten i
valgene bestemmer du selv.
Etter at bruker har valgt en kommune skal følgende informasjon om kommunen vises. (Du kan gjerne
vise mer informasjon hvis du synes det er relevant):
• Antall barn i barnehager i kommunen
• Antall barn pr. ansatt (snitt for kommunen)
• Andel ansatte med barnehagelærerutdanning
• Leke og oppholdsareal pr. barn (snitt for kommunen)
• Andel barnehager i kommunen som oppfyller pedagognormen
Fra dette bildet skal det være mulig å vise en liste med alle barnehager i kommunen. Velg
presentasjonsform og sortering selv.

b. Vise informasjon om barnehage
Når bruker velger en barnehage fra listen (eller finner den på annen måte), skal relevant informasjon
om barnehagen vises i et eget bilde. Gjør et utvalg av relevant informasjon selv. Finn selv gode /
brukervennlige måter å presentere informasjonen på.
Dette bildet skal også ha to valg for å kontakte barnehagen basert på barnehagens kontaktdata:
1. Sende e-epost til barnehagens e-post adresse
2. Ringe barnehagens telefonnummer
Bildet skal også ha et valg for å vise barnehagens webside (url finnes blant barnehagens data).

c. Finne barnehager basert på brukers posisjon (geografisk søk)
Appen skal også ha en funksjon for å vise en liste med alle barnehager i det nærheten av der
brukeren befinner seg. Denne funksjonen skal bruke mobilenhetens GPS for å finne brukerens
posisjon. Du må bruke en (eller flere) av REST-metodene i APIet på en hensiktsmessig måte for å
finne barnehager i nærheten.
Når bruker velger en barnehage fra listen, skal den vises i bildet fra pkt b.
Tilleggsfunksjon (ekstra utfordring): Appen bør gi bruker mulighet til å kunne utvide eller begrense
søkeområdet (f.eks. radius) i forhold til sin posisjon.

d. Innstillinger / Settings / brukervalg
App’en skal ha et valg for Innstillinger (Settings) der bruker kan legge inn noen faste opplysninger,
f.eks.:
• Valgt kommune (favoritt)
• Ønsket alder for barnehage. Hvis dette er valgt bør listene som viser barnehagene filtrere bort
barnehager som ikke passer for alderen.
Disse opplysningene skal lagres lokalt på enheten.

e. Ekstra utfordring: Kunne få vist barnehagene i et kart (f.eks. Google Maps)
Denne funksjonen skal vise barnehagene i nærheten i et kart. Du kan selv bestemme detaljene i
funksjonen. Eksempelvis kan kartet vise alle barnehager i valgt kommune, eller alle barnehager i
resultatet fra et geografisk søk (i pkt c).
