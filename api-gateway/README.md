# Api Gateway

## Registrere ny konsument
1. En med rettigheter på AD-gruppen i requesten må logge inn på `https://api-management.nais.adeo.no/api.html`
2. Oppdatere `oneshot.json` i dette repositoriet med ny konsument (ellers vil konsumenten din fjernes neste gang noen legger til en ny og din ikke ligger her).
3. Gjøre en `PUT` request på `rest/v2/katalog/applikasjoner/helse-reverse-proxy` med header `kilde` satt til `noFasit` og body tilsvarende ìnnholdet i filen `oneshot.json` hvor konsument ble lagt til i punktet over.
4. Gjør en `PUT` request på `rest/v2/register/deploy/helse-reverse-proxy` med `gatewayEnv` og `tilbyderEnv` til `q1` eller `p` avhengig av hvilket miljø du ønsker å oppdatere.
5. Api Gateway Key for ny konsument legges i Vault. Se Api Gateway sin dokumentasjon for dette.
