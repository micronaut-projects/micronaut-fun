To run the app locally, run opensearch via docker

```bash
docker run -d --name micronautfunopensearch -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "plugins.security.disabled=true" -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=DummyPassword#1233" opensearchproject/opensearch:2.19.3
```