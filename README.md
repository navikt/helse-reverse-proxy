# helse-reverse-proxy

[![CircleCI](https://circleci.com/gh/navikt/helse-reverse-proxy/tree/master.svg?style=svg)](https://circleci.com/gh/navikt/helse-reverse-proxy/tree/master)

For kommunikasjon fra SBS til FSS.

## Unngår å..
- ..måtte sette opp den "tilbydende" tjenesten i API Gateway

## Må istedenfor..
- ..Oppdatere nais-filene (i dette repositoriet)  med en environment-variabel når en ny tjeneste skal tilgjengeliggjøres

## Må fortsatt..
- ..registrere konsumenten i SBS i API Gateway for å få en API Key (Se README.md under api-gateway folder.)

## Henvendelser
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

Interne henvendelser kan sendes via Slack i kanalen #sykdom-i-familien
