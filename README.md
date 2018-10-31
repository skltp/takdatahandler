# Tak data handler

Syftet med takdatahandler är att underlätta för komponenter som behöver göra en behörighetskontroll eller hämta vägval från TAK datat inklusive trädklättring i HSA trädet och standardvägval/-behörighet.

Projektet består av tre delar.
1. Vagvalhandler - exponerar en metod för att hämta vägval från TAK datat med trädklättring och standardvägval.
2. Behorighethandler - exponerar en metod för att kontrollera behörighet i TAK datat med trädklättring och standardbehörighet.
3. Common - Gemensamma metoder som används av VagvalHandler och BehorighetHandler
