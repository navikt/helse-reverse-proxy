# helse-reverse-proxy

For kommunikasjon fra SBS til FSS.
Alle requester må ha header "X-Correlation-Id" satt.

## Unngår å..
- ..måtte registrere & Deploye den "tilbydende" tjenesten i FSS i Fasit
- ..måtte sette opp den "tilbydende" tjenesten i API Gateway

## Må istedenfor..
- ..oppdatere naisraitor-fss.yaml for helse-reverse-proxy (i helse-iac repository) med en environment-variabel når en ny tjeneste skal tilgjengeliggjøres

## Må fortsatt..
- ..registrere den "kallende" tjenesten i SBS i Fasit (Ingen deploy)
- ..registrere den "kallende" tjenesten i SBS i API Gateway for å få en API Key (Se nedenfor)

## For NAV-interne
For å få en API Key som gjør det mulig å kalle denne tjenesten se https://confluence.adeo.no/x/SQ6tEg

