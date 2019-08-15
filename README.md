# helse-reverse-proxy

[![CircleCI](https://circleci.com/gh/navikt/helse-reverse-proxy/tree/master.svg?style=svg)](https://circleci.com/gh/navikt/helse-reverse-proxy/tree/master)

For kommunikasjon fra SBS til FSS.

- TODO: Bytte ut hele tjenesten med nginx reverse proxy..?

## Unngår å..
- ..måtte registrere & Deploye den "tilbydende" tjenesten i FSS i Fasit
- ..måtte sette opp den "tilbydende" tjenesten i API Gateway

## Må istedenfor..
- ..oppdatere naisraitor-fss.yaml for helse-reverse-proxy (i helse-iac repository) med en environment-variabel når en ny tjeneste skal tilgjengeliggjøres

## Må fortsatt..
- ..registrere den "kallende" tjenesten i SBS i Fasit (Ingen deploy)
- ..registrere den "kallende" tjenesten i SBS i API Gateway for å få en API Key (Se nedenfor)

## Henvendelser
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

Interne henvendelser kan sendes via Slack i kanalen #team-düsseldorf.
For å få en API Key som gjør det mulig å kalle denne tjenesten se https://confluence.adeo.no/x/SQ6tEg
