#!/bin/bash
set -e

wait_for_solr() {
  SOLR_URL="$1/"

  RETRY_COUNT=0
  while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' $SOLR_URL)" != "200" ]]; do
    if [ $RETRY_COUNT -gt 60 ]; then
       echo "Oops! Something is wrong. See details with 'docker-compose logs -f solr3'"
       exit 1
    fi

    sleep 0.5
    echo "waiting $SOLR_URL $RETRY_COUNT"
    ((RETRY_COUNT+=1))
  done;

  echo "Done! $SOLR_URL"
}

wait_for_document() {
  SOLR_URL="$1/products/select?indent=true&q.op=OR&q=*%3A*"

  RETRY_COUNT=0
  while [[ "$(curl $SOLR_URL | grep -c 'First')" -le 0 ]]; do
    if [ $RETRY_COUNT -gt 30 ]; then
       echo "Oops! Documents not indexed. See details with 'docker-compose logs -f solr3'"
       exit 1
    fi

    sleep 0.5
    echo "waiting documents $SOLR_URL $RETRY_COUNT"
    ((RETRY_COUNT+=1))
  done;

  echo "Documents indexed $SOLR_URL with success"
}

solr_host="http://solr3:8983"
wait_for_solr "http://solr1:8983/solr"
wait_for_solr "http://solr2:8983/solr"
wait_for_solr "${solr_host}/solr"

curl -X POST \
     -o /dev/null \
     "${solr_host}/solr/admin/collections?action=CREATE&name=products&numShards=2&replicationFactor=1&wt=json"

curl -X POST \
     -o /dev/null \
     -H 'Content-type:application/json' \
     -d @/solr/config/request-handler.json \
     "${solr_host}/api/collections/products/config"

curl -X POST \
     -o /dev/null \
     -H 'Content-type:application/json' \
     -d @/solr/sample/products.json \
     "${solr_host}/solr/products/update?commit=true"

wait_for_document "${solr_host}/solr"
